package bfst17;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by Michelle on 4/19/2017.
 */
public class Region {
    Shape shape;
    OSMNode center;
    public Region(Shape shape, OSMNode center){
        this.shape = shape;
        this.center = center;
    }
    public Shape getShape(){return shape;}
    public OSMNode getCenter(){return center;}

}
