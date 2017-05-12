package bfst17.Directions;

import bfst17.OSMData.OSMNode;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by trold on 2/15/17.
 */
public class GraphNode implements Comparable {
    private OSMNode originOSMNode;
    private GraphNode nodeFrom;
    //private boolean isSettled;
    boolean end, shortest, oneway;
    int maxSpeed = 0;
    RoadTypes type;
    private ArrayList<Edge> edgeList;
    private double dist = Double.POSITIVE_INFINITY;

    public GraphNode(OSMNode originOSMNode, RoadTypes type, boolean oneway, int maxSpeed) {
        this.oneway = oneway;
        this.maxSpeed = maxSpeed;
        this.type = type;
        VehicleType[] types = type.getVehicletypes();
        for(int i = 0; i<types.length;i++){
            if(types[i] == VehicleType.BICYCLE || types[i] == VehicleType.ONFOOT){
                this.shortest = true;
            }
        }
        this.originOSMNode = originOSMNode;
        edgeList = new ArrayList<>();
    }

    public Point2D getPoint2D() {
        return new Point2D.Double(originOSMNode.getX(), originOSMNode.getY());
    }

    public int getMaxSpeed(){return maxSpeed;}

    public boolean isShortest() {
        return shortest;
    }

    public boolean isOneway() {
        return oneway;
    }

    public VehicleType[] getTypes() {
        return type.getVehicletypes();
    }

    public ArrayList<Edge> getEdgeList(){

        return edgeList;
    }

    public void insertNeighbor(Edge e) {
        edgeList.add(e);
    }
    public double getDistTo()
    {
        return dist;
    }
    public void setDistTo(double dist){
        this.dist = dist;
    }

    @Override
    public int compareTo(Object o) {
        GraphNode n = (GraphNode) o;
        if(dist == n.dist){
            return 0;
        }
        else if(dist < n.dist){
            return -1;
        }
        else{
            return 1;
        }
    }
    public GraphNode getNodeFrom() {
        return nodeFrom;
    }

    public void setNodeFrom(GraphNode nodeFrom) {
        this.nodeFrom = nodeFrom;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }
    public void addEdge(GraphNode destination){
        Edge e = new Edge(this, destination);
    }
}

