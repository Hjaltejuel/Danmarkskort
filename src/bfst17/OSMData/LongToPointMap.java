package bfst17.OSMData;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class LongToPointMap implements Serializable {
        int MASK;
        public Node[] tab;

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

            public Node(long _key, float x, float y, Node _next) {
                super(x, y);
                key = _key;
                next = _next;
            }
            public void setNextNodeToNull()
            {
                this.next = null;
            }
        }

    }


