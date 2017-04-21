package bfst17;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Created by Michelle on 4/19/2017.
 */
public class Region implements Serializable {
public class Region implements Serializable{
    Shape shape;
    OSMNode center;
    public Region(Shape shape, OSMNode center){
        this.shape = shape;
        this.center = center;
    }
    public Shape getShape(){return shape;}
    public OSMNode getCenter(){return center;}

}
