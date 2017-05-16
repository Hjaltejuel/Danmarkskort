package bfst17.Directions;

import bfst17.Enums.RoadTypes;
import bfst17.Enums.VehicleType;
import bfst17.Model;
import bfst17.Enums.WeighType;
import bfst17.OSMData.OSMNode;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;


public class GraphNode implements Comparable, Serializable {
    private long id;
    private Point2D point;
    private GraphNode nodeFrom;

    private boolean marked;
    private ArrayList<Edge> edgeList;

    private double distance = Double.POSITIVE_INFINITY;

    public long getId(){return id;}
    public double getX(){return point.getX();}
    public double getY(){return point.getY();}

    /**
     * Opretter en GraphNode
     * @param point     Den OSMNode grafnoden er placeret på
     */
    public GraphNode(Point2D point, long id) {;
        this.point = point;
        this.id = id;
        marked = false;
        edgeList = new ArrayList<>();
    }

    public Point2D getPoint2D() {
        return new Point2D.Double(point.getX(), point.getY());
    }

    public ArrayList<Edge> getEdgeList() {
        return edgeList;
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

    public void addEdge(GraphNode destination, String roadName, double maxSpeed, RoadTypes roadTypes) {
        Edge edge = new Edge(this, destination, destination.getId(), roadName, maxSpeed, roadTypes);
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