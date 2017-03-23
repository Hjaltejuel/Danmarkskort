package bfst17;

import java.awt.geom.Point2D;
public class LongToPointMap {
    int MASK;
    public Node[] tab;
    int count=0;


    public LongToPointMap(int capacity) {
        tab = new Node[1 << capacity];
        MASK = tab.length - 1;
    }

    public void put(long key, float x, float y) {
        int h = Long.hashCode(key) & MASK;
        tab[h] = new Node(key, x, y);
        count++;
    }

    public Integer size(){
        return count;
    }

    public Point2D get(long key) {
        /*
        for (Node n = tab[Long.hashCode(key) & MASK]; n != null; n = n.next) {
            if (n.key == key) return n;
        }
        */
        return null;
    }
}


