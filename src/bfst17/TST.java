package bfst17;

import sun.awt.image.ImageWatched;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class TST<Value,Value2> {
    private int n;              // size
    private Node<Value,Value2> root;
    private String oldPrefix;// root of TST

    private static class Node<Value,Value2> {
        private char c;                        // character
        private Node<Value,Value2> left, mid, right;  // left, middle, and right subtries
        private Value val;
        private Value2 val2;// value associated with string
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


    public Value get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("calls get() with null argument");
        }
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        Node<Value,Value2> x = get(root, key, 0);
        if (x == null) return null;
        return x.val;
    }
    public Value2 getval2(String key) {
        if (key == null) {
            throw new IllegalArgumentException("calls get() with null argument");
        }
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        Node<Value,Value2> x = get(root, key, 0);
        if (x == null) return null;
        return x.val2;
    }

    // return subtrie corresponding to given key
    private Node<Value,Value2> get(Node<Value,Value2> x, String key, int d) {
        if (x == null) return null;
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        char c = key.charAt(d);
        if (c < x.c) return get(x.left, key, d);
        else if (c > x.c) return get(x.right, key, d);
        else if (d < key.length() - 1) return get(x.mid, key, d + 1);
        else return x;
    }

    /**
     * Inserts the key-value pair into the symbol table, overwriting the old value
     * with the new value if the key is already in the symbol table.
     * If the value is {@code null}, this effectively deletes the key from the symbol table.
     *
     * @param key the key
     * @param val the value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void put(String key, Value val,Value2 val2) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with null key");
        }
        if (!contains(key)) n++;
        root = put(root, key, val,val2, 0);
    }

    private Node<Value,Value2> put(Node<Value,Value2> x, String key, Value val, Value2 val2, int d) {
        char c = key.charAt(d);
        if (x == null) {
            x = new Node<Value,Value2>();
            x.c = c;
        }
        if (c < x.c) x.left = put(x.left, key, val,val2, d);
        else if (c > x.c) x.right = put(x.right, key, val,val2, d);
        else if (d < key.length() - 1) x.mid = put(x.mid, key, val,val2, d + 1);
        else if(val!=null){
            x.val = val;}
        else x.val2 = val2;
        return x;
    }

    public ArrayList<String> keysWithPrefix(String prefix) {
        if(prefix!="") {
            oldPrefix = prefix;
            if (prefix == null) {
                throw new IllegalArgumentException("calls keysWithPrefix() with null argument");
            }
            PriorityQueue<PriorityStrings> queue = new PriorityQueue<>();
            Node<Value, Value2> x = get(root, prefix, 0);
            if (x == null) return makeArray(queue);
            if (x.val != null || x.val2!=null) queue.add(new PriorityStrings(1, prefix));
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

    // all keys in subtrie rooted at x with given prefix
    private void collect(Node<Value,Value2> x, StringBuilder prefix, PriorityQueue<PriorityStrings> queue) {
        if (x == null) return;
        collect(x.left, prefix, queue);

        if (x.val != null ||x.val2!=null) {
            String found = prefix.toString() + x.c;
            double n = ((double) oldPrefix.length() / (double) found.length());
            queue.add(new PriorityStrings(n,prefix.toString() + x.c));
        }
        collect(x.mid, prefix.append(x.c), queue);
        prefix.deleteCharAt(prefix.length() - 1);
        collect(x.right, prefix, queue);
    }
}


