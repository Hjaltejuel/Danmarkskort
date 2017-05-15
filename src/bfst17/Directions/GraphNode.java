package bfst17.Directions;

import bfst17.Enums.RoadTypes;
import bfst17.Enums.VehicleType;
import bfst17.Model;
import bfst17.Enums.WeighType;
import bfst17.OSMData.OSMNode;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public class GraphNode implements Comparable {
    private Point2D point;
    private GraphNode nodeFrom;
    private boolean isCAR, isBIKE, isFOOT;

    private boolean marked;

    private int maxSpeed = 0;

    private RoadTypes type;

    private ArrayList<Edge> edgeList;

    private double distance = Double.POSITIVE_INFINITY;

    public void setType(RoadTypes type) {
        for (VehicleType vehicleType : type.getVehicletypes()) {
            if (vehicleType == VehicleType.CAR) {
                isCAR = true;
            }
            if (vehicleType == vehicleType.BICYCLE) {
                isBIKE = true;
            }
            if (vehicleType == vehicleType.FOOT) {
                isFOOT = true;
            }
        }
    }

    public double getX(){return point.getX();}
    public double getY(){return point.getY();}

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     * Opretter en GraphNode
     * @param point     Den OSMNode grafnoden er placeret på
     * @param type              Hvilken vejtype noden er på
     * @param maxSpeed          Hvor hurtigt man må køre på vejen
     */
    public GraphNode(Point2D point, RoadTypes type, int maxSpeed) {
        if(maxSpeed==0) {
            this.maxSpeed = type.getMaxSpeed();
        } else {
            this.maxSpeed = maxSpeed;
        }
        this.type = type;
        setType(type);
        this.point = point;
        marked = false;
        edgeList = new ArrayList<>();
    }

    public Point2D getPoint2D() {
        return new Point2D.Double(point.getX(), point.getY());
    }
    public RoadTypes getType(){return this.type;}
    public boolean supportsVehicle(VehicleType vehicleType) {
        if (vehicleType == VehicleType.CAR && isCAR) {
            return true;
        }
        if (vehicleType == vehicleType.BICYCLE && isBIKE) {
            return true;
        }
        if (vehicleType == vehicleType.FOOT && isFOOT) {
            return true;
        }
        return false;
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

    public void addEdge(GraphNode destination, String roadName) {
        Edge edge = new Edge(this, destination, roadName);
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