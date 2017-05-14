package bfst17.Directions;

import bfst17.Enums.RoadTypes;
import bfst17.Enums.VehicleType;
import bfst17.Model;
import bfst17.OSMData.OSMNode;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by trold on 2/15/17.
 */
public class GraphNode implements Comparable {
    private OSMNode originOSMNode;
    private GraphNode nodeFrom;
    private boolean end, shortest, oneway;
    private int maxSpeed = 0;
    private RoadTypes type;
    private ArrayList<Edge> edgeList;
    private double distance = Double.POSITIVE_INFINITY;

    /**
     * Opretter en GraphNode
     * @param originOSMNode     Den OSMNode grafnoden er placeret på
     * @param type              Hvilken vejtype noden er på
     * @param oneway            Hvorvidt vejen er ensrettet
     * @param maxSpeed          Hvor hurtigt man må køre på vejen
     */
    public GraphNode(OSMNode originOSMNode, RoadTypes type, boolean oneway, int maxSpeed) {
        this.oneway = oneway;
        this.maxSpeed = maxSpeed;
        this.type = type;
        for(VehicleType vehicleType : type.getVehicletypes()) {
            if (vehicleType == VehicleType.BICYCLE || vehicleType == VehicleType.ONFOOT) {
                this.shortest = true;
                break;
            }
        }
        this.originOSMNode = originOSMNode;
        edgeList = new ArrayList<>();
    }

    public Point2D getPoint2D() {
        return new Point2D.Double(originOSMNode.getX(), originOSMNode.getY());
    }

    public boolean isShortest() {
        return shortest;
    }

    public boolean isOneway() {
        return oneway;
    }

    public String getRoadName(Model model){
        return model.getClosestRoad(getPoint2D()).getRoadName();
    }
    public VehicleType[] getTypes() {
        return type.getVehicletypes();
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public ArrayList<Edge> getEdgeList() {
        return edgeList;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public double getDistTo() {
        return distance;
    }

    public void setDistTo(double distance) {
        this.distance = distance;
    }

    public GraphNode getNodeFrom() {
        return nodeFrom;
    }

    public void setNodeFrom(GraphNode nodeFrom) {
        this.nodeFrom = nodeFrom;
    }

    public void addEdge(GraphNode destination) {
        Edge edge = new Edge(this, destination);
        edgeList.add(edge);
    }

    @Override
    public int compareTo(Object o) {
        GraphNode n = (GraphNode) o;
        if (distance == n.distance) {
            return 0;
        } else if (distance < n.distance) {
            return -1;
        } else {
            return 1;
        }
    }
}