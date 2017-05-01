package bfst17;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by trold on 2/15/17.
 */
public class OSMNode implements Comparable{
	private OSMNode nodeFrom;
	float lon, lat;
	private boolean isSettled;
	private boolean shortest, oneway;
	private int maxspeed;
	private boolean relevantForRouting = false;
	private ArrayList<Edge> edgeList;
	private double dist = Double.POSITIVE_INFINITY;

	public float getLon() {
		return lon;
	}

	public float getLat() {
		return lat;
	}

	public OSMNode(float lon, float lat) {
		this.lon = lon;
		this.lat = lat;
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
		return new Point2D.Double(lon, lat);
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
	public void insertNeighbor(Edge e){
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
		OSMNode n = (OSMNode) o;
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
	public OSMNode getNodeFrom() {
		return nodeFrom;
	}

	public void setNodeFrom(OSMNode nodeFrom) {
		this.nodeFrom = nodeFrom;
	}

	public boolean isSettled() {
		return isSettled;
	}

	public void setSettled(boolean settled) {
		isSettled = settled;
	}
}

