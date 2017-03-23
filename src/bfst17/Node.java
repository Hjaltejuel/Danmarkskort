package bfst17;

import java.awt.geom.Point2D;

public class Node extends Point2D.Float implements Comparable<Node> {
    public static final long serialVersionUID = 20160216;
    Node next;
    long key;

    public Node(long _key, float x, float y, Node _next) {
        super(x, y);
        key = _key;
        next = _next;
    }
    public void setNextNodeToNull()
    {
        this.next = null;
    }

    public int compareTo(Node n){
        return 0;
    }

    public double getX() {
        return super.getX();
    }

    public double getY() {
        return super.getY();
    }
}