package bfst17.OSMData;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class LongToPointMap implements Serializable {
        int MASK;
        public Node[] tab;

    /**
     * Opretter et LongToPointMap objekt.
     * @param capacity
     */

    public LongToPointMap(int capacity) {
            tab = new Node[1 << capacity];
            MASK = tab.length - 1;
        }

        public void put(long key, float x, float y) {
            int h = Long.hashCode(key) & MASK;
            tab[h] = new Node(key, x, y, tab[h]);
        }


        public Point2D get(long key) {
            for (Node n = tab[Long.hashCode(key) & MASK] ; n != null ; n = n.next) {
                if (n.key == key) return n;
            }
            return null;
        }
        static class Node extends Point2D.Float implements Serializable {
            public static final long serialVersionUID = 20160216;
            Node next;
            long key;

            /**
             * Opretter et Node objekt.
             * @param _key
             * @param x
             * @param y
             * @param _next
             */

            public Node(long _key, float x, float y, Node _next) {
                super(x, y);
                key = _key;
                next = _next;
            }
        }

    }


