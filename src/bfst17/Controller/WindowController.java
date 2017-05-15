package bfst17.Controller;

import bfst17.AddressHandling.AddressModel;
import bfst17.AddressHandling.TSTInterface;
import bfst17.Directions.GraphNode;
import bfst17.Enums.GUIMode;
import bfst17.Enums.POIclasification;
import bfst17.Enums.VehicleType;
import bfst17.GUI.DrawCanvas;
import bfst17.GUI.DrawWindow;
import bfst17.KDTrees.RoadKDTree;
import bfst17.Model;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

/**
 * Beskrivelse: Klassen WindowController, virker som controlleren til drawWindow, virker som listener til vinduet
 */
public class WindowController implements KeyListener, ActionListener, MouseListener, ComponentListener {
    private DrawWindow window;
    private Model model;
    private DrawCanvas canvas;
    private AddressModel addressModel;
    private boolean setUpDirectionsMenu = false;
    private boolean startDirections = false;
    private boolean destinationDirections = false;
    private boolean isPopUpOpen = false;
    VehicleType vType = VehicleType.CAR;
    GraphNode fromPoint, toPoint;
    public WindowController(Model model) {
        window = new DrawWindow();
        this.model = model;
        this.addressModel = model.getAddressModel();
        this.canvas = new DrawCanvas(model);
        canvas.setBounds(0, 0, window.getWindow().getWidth(), window.getWindow().getHeight());
        canvas.resetCamera();
        new CanvasMouseController(canvas, model);
        initiate();
    }

    /**
     * Beskrivelse: Initialiserer winduet
     */
    public void initiate() {
        window.createAutocomplete(addressModel.getTSTTree());
        window.setComponentzZOrder(canvas);
        window.setKeyListener(this);
        window.setMouseListener(this);
        window.setComponentListener(this);
        window.addActionListener(this);
    }

    /**
     * Beskrivelse: ActionPerformed metoden, som bliver kaldt når en actionListener bliver aktiveret
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        //setter komponenten
        Object source = e.getSource();
        //finder actionCommanden som bliver brugt til at identificerer komponenter
        String command = e.getActionCommand();
        //Cheker for at se om det er en POIMenu der er blevet kaldt
        if (source instanceof JCheckBoxMenuItem  && !command.equals("ShowCityNames")) {
            JCheckBoxMenuItem[] menu = window.getPOICheckBoxArray();
            for (int i = 0; i < menu.length; i++) {
                //Finder den specielle menu og kalder canvas så POI bliver vist
                if (e.getSource() == menu[i]) {
                    canvas.setPointsOfInterest(POIclasification.values()[i]);
                }
            }
        } else {
            //switcher ved command
            switch (command) {
                case "Save":
                    //hvis det er save knappen så save
                    save();
                    break;
                case "Load":
                    //hvis det er load knappen så load
                    try {
                        loadFile();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                case "Exit":
                    //hvis det er exit knappen så exit
                    System.exit(0);
                    break;
                case "Directions":
                    //hvis det er directions så toogle directionsbaren og sæt boolean til at starte direction
                    window.toggleDirectionsBar();
                    if(window.getDirectionsBoolean()){
                        startDirections = true;
                    }
                    else startDirections = false;
                    break;
                case "Nightmode":
                    //Hvis det er nightmode knappen så sæt nightmode
                    setColorTheme(GUIMode.NIGHT);
                    break;
                case "Greyscale":
                    //Hvis det er greyScale så sæt greyScale
                    setColorTheme(GUIMode.GREYSCALE);
                    break;
                case "Aa":
                    //Hvis det er aa så sæt aa
                    canvas.toggleAA();
                    break;
                case "Fancypan":
                    //Hvis det er FancyPan så sæt fancypan
                    canvas.toggleFancyPan();
                    break;
                case "ZoomIn":
                    //Hvis det er zoomIn så zoom ind
                    zoomIn();
                    break;
                case "ZoomOut":
                    //Hvis det er zoomOut så zoom ud
                    zoomOut();
                    break;
                case "ShowCityNames":
                    //Hvis det er showCityNames så toogle city names
                    canvas.toggleCityNames();
                    break;
                case "Car":
                    vType=VehicleType.CAR;
                    calculateGraph();
                    break;
                case "Bike":
                    vType=VehicleType.BICYCLE;
                    calculateGraph();
                    break;
                case "Walk":
                    vType=VehicleType.FOOT;
                    calculateGraph();
                    break;
            }
        }
    }


    /**
     * Beskrivelse: Ændrer GUI-temaet og tilpasser menuen.
     * @param newTheme
     */
    public void setColorTheme(GUIMode newTheme) {
        if (canvas.getGUITheme() == newTheme) {
            newTheme = GUIMode.NORMAL;
        }
        canvas.setGUITheme(newTheme);
        if (newTheme == GUIMode.NORMAL) {
            window.getNightModeMenuItem().setText("NightMode (CTRL-N)");
            window.getGreyScaleMenuItem().setText("GreyScale (CTRL-G)");
        } else if (newTheme == GUIMode.GREYSCALE) {
            window.getNightModeMenuItem().setText("NightMode (CTRL-N)");
            window.getGreyScaleMenuItem().setText("Color (CTRL-G)");
        } else if (newTheme == GUIMode.NIGHT) {
            window.getNightModeMenuItem().setText("Color (CTRL-N)");
            window.getGreyScaleMenuItem().setText("GreyScale (CTRL-G)");
        }
        window.toggleNightModeComboBox(newTheme);
        canvas.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    /**
     * Beskrivelse: KeyReleased metoden, starter directions søgningen
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        //Hvis enter er blevet trykket
        if (e.getKeyChar() == 10) {
            //Hvis startDirections er true
            if (startDirections) {
                //finder de 2 strings i de 2 comboboxe
                String firstComboBoxString = (String) window.getCombo().getSelectedItem();
                String secondComboBoxString = (String) window.getSecondCombo().getSelectedItem();
                //tjekker og ser om noget er null eller empty
                if (firstComboBoxString == null || firstComboBoxString.length() == 0 ||
                        secondComboBoxString == null || secondComboBoxString.length() == 0) {
                    return; //Ikke noget at søge efter!
                }
                //finder addresserne
                TSTInterface addressDest = addressModel.getAddress(secondComboBoxString.trim());
                TSTInterface address = addressModel.getAddress(firstComboBoxString.trim());
                //tjekker om de er null
                if (addressDest == null || address == null) return;

                //finder de tætteste veje på addresserne
                RoadKDTree.RoadTreeNode closestNode = model.getClosestRoad(new Point2D.Double(address.getX(), address.getY()), vType);
                fromPoint = closestNode.getGraphNode();

                closestNode = model.getClosestRoad(new Point2D.Double(addressDest.getX(), addressDest.getY()), vType);
                toPoint = closestNode.getGraphNode();

                calculateGraph();

                if (!isPopUpOpen) {
                    //søg ind på startpunktet
                    search();
                } else {
                    isPopUpOpen = false;
                }
            } else {
                if (!isPopUpOpen) {
                    //søg
                    search();
                } else {
                    isPopUpOpen = false;
                }
            }
        }
    }

    public void calculateGraph() {
        //Finder den korteste vej
        model.getGraph().findShortestPath(fromPoint, toPoint, vType);

        model.resetDirections();
        window.fillDirections(model.getDirections(vType));
        canvas.repaint();
    }
    /**
     * Beskrivelse: Sætter startingDirectory og kalder loadFile med dette startDirectory.
     * @throws IOException
     */
    public void loadFile() throws IOException {
        File startingDirectory = new File(System.getProperty("user.dir"));
        loadFile(startingDirectory);
        canvas.resetCamera();
    }

    /**
     * Beskrivelse: Laver en JFileChooser der begrænser de accepterede filer er begrænset til, osm, bin, zil og app(mapper på mac).
     * Beskrivelse: Hvis filen findes kaldes model.load.
     * @param startingDirectory
     * @throws IOException
     */
    public void loadFile(File startingDirectory) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(startingDirectory);

        fileChooser.setAcceptAllFileFilterUsed(false);
        //Her sørger vi får at man kun vælge bestemte filer
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".osm") || file.getName().endsWith(".bin") ||
                        file.getName().endsWith(".zip") || (file.isDirectory() && !file.getName().endsWith(".app"));
            }

            @Override
            public String getDescription() {
                return ".osm files, .bin files or .zip files";
            }
        });
        fileChooser.setDialogTitle("Choose file to loadFile");

        int userSelection = fileChooser.showOpenDialog(window.getWindow());
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            if (fileChooser.accept(fileToLoad) && fileToLoad.exists()) {
                //start et nyt program
                model.load(fileToLoad.getAbsolutePath());
                addressModel = model.getAddressModel();
                window.setTreeInAutocompleter(addressModel.getTSTTree());
            } else { //Filen blev ikke fundet - giv fejlmeddelelse
                if (!fileChooser.accept(fileToLoad)) {
                    JOptionPane.showMessageDialog(window.getWindow(), "You must choose a correct filetype to loadFile");
                } else if (!fileToLoad.exists()) {
                    JOptionPane.showMessageDialog(window.getWindow(), "File does not exist");
                }
                loadFile(fileChooser.getCurrentDirectory()); //Prøv igen..
            }
        }
    }

    /**
     * Beskrivelse: Laver en JFileChooser der giver mulighed for at gemme en fil.
     */
    public void save() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose save location");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int userSelection = fileChooser.showSaveDialog(window.getWindow());
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            model.save(fileToSave.getAbsolutePath() + ".bin");
        }
    }

    /**
     * Beskrivelse: Search metoden, søger ind på den valgte addresses punkt
     */
    public void search() {
        //cancel timer så der ikke kommer to timers på én gang
        if(canvas.getTimer() != null){
            canvas.getTimer().cancel();
        }
        //gemmer den givne tekst
        String s = (String) window.getCombo().getSelectedItem();
        if (s == null || s.length() == 0) {
            return; //Ikke noget at søge efter!
        }
        //finder addressen
        TSTInterface address = addressModel.getAddress(s.trim());
        if(address == null) {
            isPopUpOpen = true;
            JOptionPane.showMessageDialog(canvas, "Din søgning på '" +  s + "' gav ingen resultater");
            return; //Ingen adresse fundet...
        }
        //gemmer lat lon
        double lat = -address.getY();
        double lon = -address.getX();

        //kalder setPin på addressen
        canvas.setPin(address);
        //tjekker om det er en region
        boolean isRegion = address.getShape() != null;
        if (!isRegion) {
            if (canvas.isFancyPanEnabled()) {
                canvas.fancyPan(lon, lat);
            } else {
                canvas.panToPoint(lon, lat);
                canvas.centerZoomToZoomLevel(150000);
            }
        } else {
            //hvis det er en region så pan til centerkoordinatet og zoom ind til regionens størrelse
            canvas.panToPoint(lon, lat);
            double regionZoomLevel = canvas.getWidth() / address.getShape().getBounds2D().getWidth();
            canvas.centerZoomToZoomLevel(regionZoomLevel);
        }
        canvas.repaint();
    }

    public void zoomIn() {
        canvas.centerZoom(1.25);
    }

    public void zoomOut() {
        canvas.centerZoom(0.75);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
            Component clickedButton = e.getComponent();
            if (clickedButton == window.getSearchButton()) {
                search();
            } else if (clickedButton == window.getZoomInButton()) {
                zoomIn();
            } else if (clickedButton == window.getZoomOutButton()) {
                zoomOut();
            } else if (clickedButton == window.getPointsOfInterestButton()) {
                window.showMenuOne();
            } else if (clickedButton == window.getMenuButton()) {
                window.showMenuTwo();
            }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        window.setBounds(canvas);
    }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void componentMoved(ComponentEvent e) { }

    @Override
    public void componentShown(ComponentEvent e) { }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

}