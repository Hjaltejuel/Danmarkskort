package bfst17;

import bfst17.Enums.WayType;
import bfst17.ShapeStructure.PolygonApprox;

import java.io.Serializable;

/**
 * Created by Michelle on 5/2/2017.
 */
public class RoadNode implements Serializable {
    PolygonApprox shape;
    String roadName;
    WayType type;

    public RoadNode(PolygonApprox shape, String roadName, WayType type){
        this.shape = shape;
        this.roadName = roadName;
        this.type = type;
    }
    public PolygonApprox getShape() {
        return shape;
    }

    public WayType getType() {
        return type;
    }

    public String getRoadName() {
        return roadName;
    }
}