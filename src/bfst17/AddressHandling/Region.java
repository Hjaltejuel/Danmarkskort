package bfst17.AddressHandling;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Beskrivelse Region klassen som fungerer som en val for en region i tst træet. Indeholder et center punkt på regionen og en shape på regionen
 */
public class Region implements Serializable,TSTInterface {
    Shape shape;
    Point2D center;
    public Region(Shape shape, Point2D center){
        this.shape = shape;
        this.center = center;
    }
    public Shape getShape(){return shape;}

    @Override
    public String getAddress() {
        return null;
    }

    public double getX(){return center.getX();}
    public double getY(){return center.getY();}

}
