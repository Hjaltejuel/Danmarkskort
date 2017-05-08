package bfst17.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Jens on 08-05-2017.
 */
public class LoadCanvas extends JComponent implements Observer {
    public LoadCanvas(){
        System.out.println("A");
    }

    @Override
    public void update(Observable o, Object arg) {

    }
    @Override
    protected void paintComponent(Graphics _g) {
        Graphics2D g = (Graphics2D)_g;
        System.out.println("A");

        g.draw(new Rectangle2D.Double(0,0,1000,1000));
    }
}
