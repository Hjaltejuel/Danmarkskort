package bfst17.Directions;

import bfst17.Enums.RoadTypes;
import bfst17.Enums.VehicleType;
import bfst17.Model;
import bfst17.Enums.WeighType;
import bfst17.OSMData.OSMNode;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by trold on 2/15/17.
 */
public class GraphNode implements Comparable {
    private Point2D originOSMNode;
    private GraphNode nodeFrom;
    private boolean end, oneway;
    private boolean marked;
    private int maxSpeed = 0;
    private RoadTypes type;
    private ArrayList<Edge> edgeList;
    private double distance = Double.POSITIVE_INFINITY;

    public RoadTypes getType() {
        return type;
    }

    /**
     * Opretter en GraphNode
     * @param originOSMNode     Den OSMNode grafnoden er placeret på
     * @param type              Hvilken vejtype noden er på
     * @param oneway            Hvorvidt vejen er ensrettet
     * @param maxSpeed          Hvor hurtigt man må køre på vejen
     */
    public GraphNode(Point2D originOSMNode, RoadTypes type, boolean oneway, int maxSpeed) {
        this.oneway = oneway;
        if(maxSpeed==0) {
            this.maxSpeed = type.getMaxSpeed();
        } else {
            this.maxSpeed = maxSpeed;
        }
        this.type = type;
        this.originOSMNode = originOSMNode;
        marked = false;
        edgeList = new ArrayList<>();
    }

    public Point2D getPoint2D() {
        return new Point2D.Double(originOSMNode.getX(), originOSMNode.getY());
    }

    public boolean supportsVehicle(VehicleType vehicle) {
        for (VehicleType vehicleType : type.getVehicletypes()) {
            if (vehicleType == vehicle) {
                return true;
            }
        }
        return false;
    }

    public boolean isOneway() {
        return oneway;
    }

    public String getRoadName(Model model){
        return model.getClosestRoad(getPoint2D(), VehicleType.CAR).getRoadName();
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

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
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