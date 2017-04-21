package bfst17;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
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

    }

    public void actionPerformed(ActionEvent e) {

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
        if((JButton)e.getComponent()==window.getSearch()){
            search();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

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
