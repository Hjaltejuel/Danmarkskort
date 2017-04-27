package bfst17;

import bfst17.Enums.GUIMode;
import bfst17.Enums.POIclasification;

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
    AutocompleteJComboBox combo;
    int counter = 0;
    boolean setUpDirectionsMenu = false;
    File currentPath;

    public WindowController(Model model) {
        window = new DrawWindow();
        this.model = model;
        this.addressModel = model.getAddressModel();
        this.canvas = new DrawCanvas(model);
        canvas.setBounds(0, 0, window.getWindow().getWidth(), window.getWindow().getHeight());
        canvas.pan(-model.getMinLon(), model.getMaxLat());
        canvas.zoom(canvas.getWidth() / (model.getMaxLon() - model.getMinLon()));
        new CanvasMouseController(canvas, model);
        initiate();

    }

    public void initiate() {
        window.createAutocomplete(addressModel.getTSTTree());
        this.combo = window.getCombo();
        window.setComponentzZOrder(canvas);
        window.setKeyListener(this);
        window.setMouseListener(this);
        window.setComponentListener(this);
        window.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        String command = e.getActionCommand();
        if (source instanceof JCheckBoxMenuItem && source != window.getDirections()) {
            JCheckBoxMenuItem[] menu = window.getPointsOfInterestMenues();
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
                        load();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                case "Exit":
                    System.exit(0);
                    break;
                case "Directions":
                    setUpDirectionsMenu = !setUpDirectionsMenu;
                    if (setUpDirectionsMenu) {
                        window.SetSecondSearch(addressModel.getTSTTree());
                    } else window.tearSecondSearch();
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
            }
        }
    }

    public void setColorTheme(GUIMode newTheme) {
        if (canvas.GUITheme == newTheme) {
            newTheme = GUIMode.NORMAL;
        }

        //Hvad gør setup & tear down?? Der er umiddelbart ingen forskel om de er med eller fra

        canvas.setGUITheme(newTheme);
        if (newTheme == GUIMode.NORMAL) {
            window.getNightModeMenuItem().setText("NightMode (CTRL-N)");
            window.getGreyScaleMenuItem().setText("GreyScale (CTRL-G)");
            //window.tearDownNightMode();
        } else if (newTheme == GUIMode.GREYSCALE) {
            window.getNightModeMenuItem().setText("NightMode (CTRL-N)");
            window.getGreyScaleMenuItem().setText("Color (CTRL-G)");
            //window.tearDownNightMode();
        } else if (newTheme == GUIMode.NIGHT) {
            window.getNightModeMenuItem().setText("Color (CTRL-N)");
            window.getGreyScaleMenuItem().setText("GreyScale (CTRL-G)");
            //window.setUpNightMode();
        }
        canvas.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 10) {
            search();
        }
    }

    public void load() throws IOException {
        JFileChooser fileChooser = new JFileChooser();

        if (currentPath==null) {
            currentPath = new File(System.getProperty("user.dir"));;
        }
        fileChooser.setCurrentDirectory(currentPath);

        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".osm") || file.getName().endsWith(".bin") || file.getName().endsWith(".zip") || (file.isDirectory() && !file.getName().endsWith(".app"));
            }

            @Override
            public String getDescription() {
                return ".osm files, .bin files or .zip files";
            }
        });
        fileChooser.setDialogTitle("Choose file to load");

        int userSelection = fileChooser.showOpenDialog(window.getWindow());
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            if (fileChooser.accept(fileToLoad) && fileToLoad.exists()) {
                model.load(fileToLoad.getAbsolutePath());
                window.getWindow().dispose();
                WindowController b = this;
                WindowController a = new WindowController(model);
                b = null;
                currentPath = null;
            } else if (!fileChooser.accept(fileToLoad)) {
                JOptionPane.showMessageDialog(window.getWindow(), "You must choose a correct filetype to load");
                currentPath = fileChooser.getCurrentDirectory();
                load();

            } else if (!fileToLoad.exists()) {
                JOptionPane.showMessageDialog(window.getWindow(), "File does not exist");
                currentPath = fileChooser.getCurrentDirectory();
                load();
            }
        }
    }

    public void save() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle("Choose save location");

        int userSelection = fileChooser.showSaveDialog(window.getWindow());

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            model.save(fileToSave.getAbsolutePath() + ".bin");
        }
    }

    public void search() {
        String s = (String) combo.getSelectedItem();
        if (s == null || s.length()==0) {
            return; //Ikke noget at søge efter!
        }

        //points lat, lon
        TSTInterface address = addressModel.getAddress(s.trim());
        if(address == null) {
            return; //Ingen adresse fundet...
        }
        if (!(address instanceof Region)) {
            double lat = -address.getY();
            double lon = -address.getX();

            if (canvas.shouldFancyPan) {
                canvas.fancyPan(lon, lat);
            } else {
                canvas.panToPoint(lon,lat);
                canvas.zoomAndCenter();
            }
            canvas.setSearchMode((float) lon, (float) lat);
        } else if (address instanceof Region) {
            Shape shape = address.getShape();
            Point2D center = new Point2D.Float((float) -address.getX(), (float) -address.getY());
            canvas.regionSearch(shape, center);
            double lat = center.getY();
            double lon = center.getX();

            double distanceToCenterY = lat - canvas.getCenterCordinateY();
            double distanceToCenterX = lon - canvas.getCenterCordinateX();
            double dx = distanceToCenterX * canvas.getXZoomFactor();
            double dy = distanceToCenterY * canvas.getYZoomFactor();
            canvas.pan(dx, dy);
            canvas.pan(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
            canvas.zoom((canvas.getWidth() / (shape.getBounds2D().getMaxX() - shape.getBounds2D().getMinX())) / canvas.getXZoomFactor());
            canvas.pan(canvas.getWidth() / 2, canvas.getHeight() / 2);
            canvas.repaint();
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        Component component = e.getComponent();
        if (component == window.getSearch()) {
            search();
        } else if (component == window.getZoomIn()) {
            zoomIn();
        } else if (component == window.getZoomOut()) {
            zoomOut();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Component component = e.getComponent();
        if (component == window.getPointsOfInterest()) {
            window.showMenuOne();
        } else if (component == window.getMenu()) {
            window.showMenuTwo();
        }
    }


    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void zoomIn() {
        canvas.pan(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
        canvas.zoom(1.25);
        canvas.pan(canvas.getWidth() / 2, canvas.getHeight() / 2);
    }

    public void zoomOut() {
        canvas.pan(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
        canvas.zoom(0.75);
        canvas.pan(canvas.getWidth() / 2, canvas.getHeight() / 2);
    }


    @Override
    public void componentResized(ComponentEvent e) {
        window.setBounds(canvas);
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}