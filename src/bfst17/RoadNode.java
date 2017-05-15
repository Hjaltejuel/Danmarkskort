package bfst17;

import bfst17.Enums.WayType;
import bfst17.ShapeStructure.PolygonApprox;

import java.io.Serializable;


public class RoadNode implements Serializable {
    PolygonApprox shape;
    String roadName;
    WayType type;

    public RoadNode(PolygonApprox shape, String roadName, WayType type){
        this.shape = shape;
        this.roadName = roadName;
    }
    public PolygonApprox getShape() {
        return shape;
    }

    public String getRoadName() {
        return roadName;
    }
}