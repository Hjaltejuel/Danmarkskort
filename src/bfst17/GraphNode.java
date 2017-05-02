package bfst17;

import bfst17.OSMData.OSMNode;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by trold on 2/15/17.
 */
public class GraphNode implements Comparable {
    private OSMNode originOSMNode;
    private GraphNode nodeFrom;
    private boolean isSettled;
    private boolean shortest, oneway;
    private int maxspeed;
    private boolean relevantForRouting, isStart, isEnd = false;
    private ArrayList<Edge> edgeList;
    private double dist = Double.POSITIVE_INFINITY;


    public GraphNode(OSMNode originOSMNode) {
        this.originOSMNode = originOSMNode;
        edgeList = new ArrayList<>();
    }


    public void setNodeTags(boolean bicycle, boolean foot, int maxspeed, boolean oneway) {
        if(bicycle || foot) {
            this.shortest = true;
        }
        this.oneway = oneway;
        this.maxspeed = maxspeed;
    }

    public void setRelevantForRouting(boolean b) {
        relevantForRouting = b;
    }

    public Point2D getPoint2D() {
        return new Point2D.Double(originOSMNode.getX(), originOSMNode.getY());
    }

    public boolean isRelevantForRouting() {
        return relevantForRouting;
    }

    public boolean isShortest() {
        return shortest;
    }

    public boolean isOneway() {
        return oneway;
    }

    public int getMaxspeed() {
        return maxspeed;
    }

    public ArrayList<Edge> getEdgeList(){

        return edgeList;
    }

    public void insertNeighbor(Edge e) {
        edgeList.add(e);
        //System.out.println(edgeList.size());
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

    public boolean isSettled() {
        return isSettled;
    }

    public void setSettled(boolean settled) {
        isSettled = settled;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }
}

