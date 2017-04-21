package bfst17;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.stream.Collectors;

/**
 * Created by Hjalte on 21-04-2017.
 */
public class Controller implements KeyListener, ActionListener, MouseListener {
    DrawWindow window;
    Model model;
    DrawCanvas canvas;
    AddressModel addressModel;
    AutocompleteJComboBox combo;
    int counter = 0;
    boolean isClicked1 = false;
    boolean isClicked2 = false;
    boolean first = true;
    File currentPath ;

    public Controller(DrawWindow window,Model model){
        this.window = window;
        this.model = model;
        this.canvas = window.getCanvas();
        this.addressModel = model.getAddressModel();
        ArrayList listItems = new ArrayList();
        listItems.addAll(addressModel.getAddressToCordinate().keySet().stream().map(a -> a.toString().toLowerCase()).collect(Collectors.toList()));
        listItems.addAll(addressModel.getRegionToShape().keySet().stream().map(a->a.toString().toLowerCase()).collect(Collectors.toList()));
        window.createAutocomplete(listItems);
        this.combo = window.getCombo();
        window.setComponentzZOrder();
        window.setKeyListener(this);
        window.setMouseListener(this);
        window.addActionListener(this);

    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        String command = e.getActionCommand();
        if(source instanceof JCheckBoxMenuItem){
            JCheckBoxMenuItem[] menu = window.getPointsOfInterestMenues();
            for(int i = 0; i<menu.length;i++){
                if(e.getSource() ==menu[i] ) {
                    canvas.setPointsOfInterest(POIclasification.values()[i].toString());
                }
            }
        } else if(command.equals("Save")){
            save();
        } else if(command.equals("Load")){
            load();
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
    public void load(){
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
                DrawWindow a = new DrawWindow(model);
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
                Point2D center = addressModel.getRegion(s.trim()).getCenter().getPoint2D();
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
            canvas.pan(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
            canvas.zoom(1.25);
            canvas.pan(canvas.getWidth()/ 2, canvas.getHeight() / 2);
        } else if(component == window.getZoomOut()){
            canvas.pan(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
            canvas.zoom(0.75);
            canvas.pan(canvas.getWidth()/ 2, canvas.getHeight() / 2);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Component component = e.getComponent();
        if(component == window.getPointsOfInterest()) {
            if (!isClicked1) {
                window.getPoiPopUpMenu().show(e.getComponent(), 0, 40);
                isClicked1 = true;
            } else if (isClicked1) {
                window.getPoiPopUpMenu().setVisible(false);
                isClicked1 = false;
            }
            canvas.repaint();
        } else if(component == window.getMenu()){
            if(!isClicked2){
                window.getPopUpMenu().show(e.getComponent(),-100,40);
                isClicked2=true;
            }else if(isClicked2){
                window.getPopUpMenu().setVisible(false);
                isClicked2=false;
            }
            canvas.repaint();
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
}
