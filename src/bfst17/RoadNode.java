package bfst17;

import bfst17.Directions.Graph;
import bfst17.Directions.GraphNode;
import bfst17.Enums.WayType;
import bfst17.ShapeStructure.PolygonApprox;

import java.io.Serializable;
import java.util.ArrayList;


public class RoadNode implements Serializable {
    PolygonApprox shape;
    String roadName;
    ArrayList<Long> nodes = new ArrayList<>();


    public RoadNode(PolygonApprox shape, String roadName, ArrayList<Long> nodes){
        this.shape = shape;
        this.nodes = nodes;
        this.roadName = roadName;
    }
    public ArrayList<Long> getNodes(){return nodes;}
    public PolygonApprox getShape() {
        return shape;
    }

    public String getRoadName() {
        return roadName;
    }
}