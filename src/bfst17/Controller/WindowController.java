package bfst17.Controller;

import bfst17.AddressHandling.AddressModel;
import bfst17.AddressHandling.DuplicateAddressNode;
import bfst17.AddressHandling.TSTInterface;
import bfst17.Directions.DirectionObject;
import bfst17.Enums.GUIMode;
import bfst17.Enums.POIclasification;
import bfst17.Enums.WeighType;
import bfst17.GUI.DrawCanvas;
import bfst17.GUI.DrawWindow;
import bfst17.KDTrees.TreeNode;
import bfst17.Model;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

/**
 * Created by Hjalte on 21-04-2017.
 */
public class WindowController implements KeyListener, ActionListener, MouseListener, ComponentListener {
    DrawWindow window;
    Model model;
    DrawCanvas canvas;
    AddressModel addressModel;
    boolean setUpDirectionsMenu = false;
    boolean startDirections = false;
    boolean destinationDirections = false;

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

    public void initiate() {
        window.createAutocomplete(addressModel.getTSTTree());
        window.setComponentzZOrder(canvas);
        window.setKeyListener(this);
        window.setMouseListener(this);
        window.setComponentListener(this);
        window.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        String command = e.getActionCommand();
        if (source instanceof JCheckBoxMenuItem && source != window.getDirections() && !command.equals("ShowCityNames")) {
            JCheckBoxMenuItem[] menu = window.getPOICheckBoxArray();
            for (int i = 0; i < menu.length; i++) {
                if (e.getSource() == menu[i]) {
                    canvas.setPointsOfInterest(POIclasification.values()[i]);
                }
            }
        } else {
            switch (command) {
                case "Save":
                    save();
                    break;
                case "Load":
                    try {
                        loadFile();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                case "Exit":
                    System.exit(0);
                    break;
                case "Directions":
                    window.toggleDirectionsBar();
                    if(window.getDirectionsBoolean()){
                        startDirections = true;
                    }
                    else startDirections = false;

                    break;
                case "Nightmode":
                    setColorTheme(GUIMode.NIGHT);
                    break;
                case "Greyscale":
                    setColorTheme(GUIMode.GREYSCALE);
                    break;
                case "Aa":
                    canvas.toggleAA();
                    break;
                case "Fancypan":
                    canvas.toggleFancyPan();
                    break;
                case "ZoomIn":
                    zoomIn();
                    break;
                case "ZoomOut":
                    zoomOut();
                    break;
                case "ShowCityNames":
                    canvas.toggleCityNames();
                    break;
            }
        }
    }

    public void setColorTheme(GUIMode newTheme) {
        if (canvas.getGUITheme() == newTheme) {
            newTheme = GUIMode.NORMAL;
        }

        //Hvad gør setup & tear down?? Der er umiddelbart ingen forskel om de er med eller fra
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

    boolean isPopUpOpen = false;

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 10) {
            if (startDirections) {
                String firstComboBoxString = (String) window.getCombo().getSelectedItem();
                String secondComboBoxString = (String) window.getSecondCombo().getSelectedItem();
                if (firstComboBoxString == null || firstComboBoxString.length() == 0 ||
                        secondComboBoxString == null || secondComboBoxString.length() == 0) {
                    return; //Ikke noget at søge efter!
                }
                TSTInterface addressDest = addressModel.getAddress(secondComboBoxString.trim());
                TSTInterface address = addressModel.getAddress(firstComboBoxString.trim());

                TreeNode closestNode = model.getClosestRoad(new Point2D.Double(address.getX(), address.getY()));
                Point2D fromPoint = new Point2D.Double(closestNode.getX(), closestNode.getY());

                closestNode = model.getClosestRoad(new Point2D.Double(addressDest.getX(), addressDest.getY()));
                Point2D toPoint = new Point2D.Double(closestNode.getX(), closestNode.getY());

                model.getGraph().findShortestPath(fromPoint, toPoint, WeighType.FASTESTCAR);
                System.out.println("Begynd...");
                for (DirectionObject DirObj : model.getDirectionsList()) {
                    System.out.println(DirObj);
                }
            }

            if (!isPopUpOpen) {
                search();
            } else {
                isPopUpOpen = false;
            }
        }
    }

    public void loadFile() throws IOException {
        File startingDirectory = new File(System.getProperty("user.dir"));
        loadFile(startingDirectory);
        canvas.resetCamera();
    }

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
            if (fileChooser.accept(fileToLoad) && fileToLoad.exists()) { //Filen er fundet! Indlæs:
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

    public void search() {
        //cancel timer så der ikke kommer to timers på én gang
        if(canvas.getTimer() != null){
            canvas.getTimer().cancel();
        }
        String s = (String) window.getCombo().getSelectedItem();
        if (s == null || s.length()==0) {
            return; //Ikke noget at søge efter!
        }

        TSTInterface address = addressModel.getAddress(s.trim());
        if(address == null) {
            isPopUpOpen = true;
            JOptionPane.showMessageDialog(canvas, "Din søgning på '" +  s + "' gav ingen resultater");
            return; //Ingen adresse fundet...
        }
        if(address instanceof DuplicateAddressNode){

        }


        double lat = -address.getY();
        double lon = -address.getX();

        canvas.setPin(address);
        boolean isRegion = address.getShape() != null;
        if (!isRegion) {
            if (canvas.isFancyPanEnabled()) {
                canvas.fancyPan(lon, lat);
            } else {
                canvas.panToPoint(lon, lat);
                canvas.centerZoomToZoomLevel(150000);
            }
        } else {
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



    //<editor-fold desc="Ting vi skal override, men ikke bruger">
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
    //</editor-fold>
}