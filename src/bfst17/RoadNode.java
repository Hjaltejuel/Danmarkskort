package bfst17;

import bfst17.ShapeStructure.PolygonApprox;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by Michelle on 5/2/2017.
 */
public class RoadNode {
    PolygonApprox s;
    String roadName;


    public RoadNode(PolygonApprox s, String roadName){
        this.s = s;
        this.roadName = roadName;
    }
    public PolygonApprox getShape() {
        return s;
    }

    public String getRoadName() {
        return roadName;
    }
}