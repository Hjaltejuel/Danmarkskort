package bfst17;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Created by Michelle on 4/19/2017.
 */
public class Region implements Serializable,TSTInterface {
    Shape shape;
    Point2D center;
    public Region(Shape shape, Point2D center){
        this.shape = shape;
        this.center = center;
    }
    public Shape getShape(){return shape;}
    public double getX(){return center.getX();}
    public double getY(){return center.getY();}

}
