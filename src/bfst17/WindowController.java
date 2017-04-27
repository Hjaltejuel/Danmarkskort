package bfst17;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.stream.Collectors;

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
    boolean first = true;
    boolean setUpDirectionsMenu = false;
    File currentPath;

    public WindowController(Model model){
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

    public void initiate(){
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
        if(source instanceof JCheckBoxMenuItem && source!=window.getDirections()){
            JCheckBoxMenuItem[] menu = window.getPointsOfInterestMenues();
            for(int i = 0; i<menu.length;i++){
                if(e.getSource() ==menu[i] ) {
                    canvas.setPointsOfInterest(POIclasification.values()[i].toString());
                }
            }
        } else if(command.equals("Save")){
            save();
        } else if(command.equals("Load")){
            try {
                load();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if(command.equals("Exit")){
            System.exit(0);
        } else if (command.equals("Directions")){
            setUpDirectionsMenu = !setUpDirectionsMenu;
            if(setUpDirectionsMenu) {
                window.SetSecondSearch(addressModel.getTSTTree());
            } else window.tearSecondSearch();
        } else if(command.equals("Nightmode")){
                setNightMode();
        } else if(command.equals("Greyscale")){
                setGreyScale();
        } else if(command.equals("Aa")){
            canvas.toggleAA();
        } else if(command.equals("Fancypan")){
            canvas.toggleFancyPan();
        } else if(command.equals("ZoomIn")){
            zoomIn();
        } else if (command.equals("ZoomOut")){
            zoomOut();
        }
    }

    public void setGreyScale(){
        if (window.getGreyScale().getText().equals("GreyScale (CTRL-G)")) {
            canvas.setGreyScale();
            canvas.setNightModeFalse();
        if (window.getNightMode().getText().equals("Color (CTRL-N)")) {
            window.getNightMode().setText("NightMode (CTRL-N)");
            window.tearDownNightMode();
        }
            canvas.repaint();
            window.getGreyScale().setText("Color (CTRL-G)");
    } else {
            canvas.setGreyScaleFalse();
            canvas.repaint();
            window.getGreyScale().setText("GreyScale (CTRL-G)");
    }}
    public void setNightMode(){
        if(window.getNightMode().getText().equals("NightMode (CTRL-N)")) {
            canvas.setNightMode();
            canvas.setGreyScaleFalse();
            window.getGreyScale().setText("GreyScale (CTRL-G)");
            canvas.repaint();
            window.getNightMode().setText("Color (CTRL-N)");
            window.setUpNightMode();


        } else {
            canvas.setNightModeFalse();
            window.tearDownNightMode();
            canvas.repaint();
            window.getNightMode().setText("NightMode (CTRL-N)");
        }
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
        if(first) {
            currentPath = null;
        }

        JFileChooser fileChooser = new JFileChooser();
        if(currentPath != null) {
            fileChooser.setCurrentDirectory(currentPath);
        }

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
            if(fileChooser.accept(fileToLoad) && fileToLoad.exists()){
                model.load(fileToLoad.getAbsolutePath());
                window.getWindow().dispose();
                WindowController b = this;
                WindowController a = new WindowController(model);
                b = null;
                first = true;
            }
            else if(!fileChooser.accept(fileToLoad)){
                JOptionPane.showMessageDialog(window.getWindow(), "You must choose a correct filetype to load");
                currentPath = fileChooser.getCurrentDirectory();
                first = false;
                load();

            }
            else if(!fileToLoad.exists()){
                JOptionPane.showMessageDialog(window.getWindow(), "File does not exist");
                currentPath = fileChooser.getCurrentDirectory();
                first = false;
                load();
            }

        }


    }

    public void save(){JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle("Choose save location");

        int userSelection = fileChooser.showSaveDialog(window.getWindow());

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            model.save(fileToSave.getAbsolutePath() + ".bin");
        }}

    public void search() {
        String s = (String) combo.getSelectedItem();
        if (!s.equals("")) {
            //points lat, lon
            if(addressModel.getPoint2DToAddress(s.trim())!=null) {
                double lat = -addressModel.getPoint2DToAddress(s.trim()).getY();
                double lon = -addressModel.getPoint2DToAddress(s.trim()).getX();

                //distance from center of screen in lat lon
                double distanceToCenterY = lat - canvas.getCenterCordinateY();
                double distanceToCenterX = lon - canvas.getCenterCordinateX();


                if (canvas.fancyPan) {
                    double distance = Math.sqrt(Math.abs(Math.pow(distanceToCenterX * canvas.getXZoomFactor(), 2) + Math.pow(distanceToCenterY * canvas.getYZoomFactor(), 2)));
                    double amountOfZoom = 150000 / canvas.getXZoomFactor();

                    if (amountOfZoom >= 2) {
                        canvas.panSlowAndThenZoomIn(distanceToCenterX, distanceToCenterY, true);
                    } else {
                        if (distance < 400) {
                            canvas.panSlowAndThenZoomIn(distanceToCenterX, distanceToCenterY, false);
                        } else {
                            canvas.zoomOutSlowAndThenPan(distanceToCenterX, distanceToCenterY);
                        }
                    }
                } else if (!canvas.fancyPan) {
                    double dx = distanceToCenterX * canvas.getXZoomFactor();
                    double dy = distanceToCenterY * canvas.getYZoomFactor();
                    canvas.pan(dx, dy);
                    canvas.zoomAndCenter();
                }
                canvas.setSearchMode((float) lon, (float) lat);
            } else if(addressModel.getRegion(s.trim())!=null){
                Shape shape = addressModel.getRegion(s.trim()).getShape();
                Point2D center = addressModel.getRegion(s.trim()).getCenter();
                canvas.regionSearch(shape);
                double lat = -center.getY();
                double lon = -center.getX();


                double distanceToCenterY = lat - canvas.getCenterCordinateY();
                double distanceToCenterX = lon - canvas.getCenterCordinateX();
                double dx = distanceToCenterX * canvas.getXZoomFactor();
                double dy = distanceToCenterY * canvas.getYZoomFactor();
                canvas.pan(dx, dy);
                canvas.pan(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
                canvas.zoom((canvas.getWidth()/(shape.getBounds2D().getMaxX()-shape.getBounds2D().getMinX()))/canvas.getXZoomFactor());
                canvas.pan(canvas.getWidth() / 2, canvas.getHeight() / 2);
                canvas.repaint();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Component component = e.getComponent();
        if(component==window.getSearch()){
            search();
        } else if(component == window.getZoomIn()){
            zoomIn();
        } else if(component == window.getZoomOut()){
            zoomOut();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Component component = e.getComponent();
        if(component == window.getPointsOfInterest()) {
            window.showMenuOne();
        } else if(component == window.getMenu()){
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
    public void zoomIn(){
        canvas.pan(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
        canvas.zoom(1.25);
        canvas.pan(canvas.getWidth()/ 2, canvas.getHeight() / 2);
    }
    public void zoomOut(){
        canvas.pan(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
        canvas.zoom(0.75);
        canvas.pan(canvas.getWidth()/ 2, canvas.getHeight() / 2);
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
