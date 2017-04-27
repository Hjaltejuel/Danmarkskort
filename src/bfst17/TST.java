package bfst17;

import sun.awt.image.ImageWatched;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class TST<TSTInterface> {
    private int n;
    private Node<TSTInterface> root;
    private String oldPrefix;

    private static class Node<TSTInterface> {
        private char c;
        private Node<TSTInterface> left, mid, right;
        private TSTInterface val;
    }

    public TST() {
    }

    public int size() {
        return n;
    }

    public boolean contains(String key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to contains() is null");
        }
        return get(key) != null;
    }


    public TSTInterface get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("calls get() with null argument");
        }
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        Node<TSTInterface> x = get(root, key, 0);
        if (x == null) return null;
        return x.val;
    }

    // return subtrie corresponding to given key
    private Node<TSTInterface> get(Node<TSTInterface> x, String key, int d) {
        if (x == null) return null;
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        char c = key.charAt(d);
        if (c < x.c) return get(x.left, key, d);
         else if (c > x.c) return get(x.right, key, d);
         else if (d < key.length() - 1) return get(x.mid, key, d + 1);
        else return x;
    }


    public void put(String key, TSTInterface val) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with null key");
        }
        if (!contains(key)) n++;
        root = put(root, key, val, 0);
    }

    private Node<TSTInterface> put(Node<TSTInterface> x, String key, TSTInterface val, int d) {
        char c = key.charAt(d);
        if (x == null) {
            x = new Node<TSTInterface>();
            x.c = c;
        }
        if (c < x.c) x.left = put(x.left, key, val, d);
        else if (c > x.c) x.right = put(x.right, key, val, d);
        else if (d < key.length() - 1) x.mid = put(x.mid, key, val, d + 1);
        else x.val = val;
        return x;
    }

    public ArrayList<String> keysWithPrefix(String prefix) {
        if(prefix!="") {
            oldPrefix = prefix;
            if (prefix == null) {
                throw new IllegalArgumentException("calls keysWithPrefix() with null argument");
            }
            PriorityQueue<PriorityStrings> queue = new PriorityQueue<>();
            Node<TSTInterface> x = get(root, prefix, 0);

            if (x == null) return makeArray(queue);
            if (x.val != null ) queue.add(new PriorityStrings(1, prefix + ", " + x.val.toString()));
            collect(x.mid, new StringBuilder(prefix), queue);
            return makeArray(queue);
        } else return null;
    }
    public ArrayList<String> makeArray(PriorityQueue<PriorityStrings> q){
        ArrayList<String> returnList = new ArrayList<>();
        for(int i = 0; i<5 && !q.isEmpty();i++){

            returnList.add(q.poll().getAddress());
        }
        return returnList;
    }

    private void collect(Node<TSTInterface> x, StringBuilder prefix, PriorityQueue<PriorityStrings> queue) {
        if (x == null) return;
        collect(x.left, prefix, queue);

        if (x.val != null ) {
            String found = prefix.toString() + x.c;
            if(x.val instanceof AddressNode)
            {
                double n = ((double) oldPrefix.length()  / ((double) found.length()+ x.val.toString().length()+2));
                queue.add(new PriorityStrings(n,prefix.toString() + x.c + ", " +x.val.toString()));
            }
                else{double n = ((double) oldPrefix.length()  / (double) found.length());
                queue.add(new PriorityStrings(n,prefix.toString() + x.c));}

        }
        collect(x.mid, prefix.append(x.c), queue);
        prefix.deleteCharAt(prefix.length() - 1);
        collect(x.right, prefix, queue);
    }
}


