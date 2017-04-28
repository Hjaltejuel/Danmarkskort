package bfst17;

import sun.awt.image.ImageWatched;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TST<TSTInterface> implements Serializable {
    private int n;
    private Node<TSTInterface> root;
    private String oldPrefix;
    private String leftover;
    Pattern pattern = Pattern.compile("\\d{4}+.*");

    private static class Node<TSTInterface> implements Serializable{
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


    public TSTInterface get(String unfilteredKey) {
        String suffix = "";
        String key = unfilteredKey.replace(",", "");
        Matcher matcher = pattern.matcher(key);
        if(matcher.find()){
            suffix = key.substring(matcher.start(),matcher.end());
            key = key.substring(0,matcher.start()-1);
        }
        if (key == null) {
            throw new IllegalArgumentException("calls get() with null argument");
        }
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        Node<TSTInterface> x = get(root, key, 0);
        if (x == null) return null;
        if(x.val instanceof DuplicateAddressNode){
            for(bfst17.TSTInterface node: (DuplicateAddressNode)(x.val)){
                if(node.getAddress().equals(suffix)){
                    return (TSTInterface) node;
                }
            }
        }
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
        n++;
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
        else  if(x.val != null){
            if(x.val instanceof DuplicateAddressNode){
                ((DuplicateAddressNode) x.val).add((bfst17.TSTInterface)val);
            } else {
                DuplicateAddressNode addresArray = new DuplicateAddressNode();
                addresArray.add((bfst17.TSTInterface) x.val);
                addresArray.add((bfst17.TSTInterface) val);
                x.val = (TSTInterface) addresArray;
            }
        } else x.val = val;
        return x;
    }

    public ArrayList<String> keysWithPrefix(String prefixUser) {
        if(prefixUser!="") {
            String prefix = prefixUser;
            prefix = prefix.replace(",","");
            String[] split = prefix.split(" ");
            this.leftover = "";
            String treeSavedAddress = "";
            for(int i = 2; i<split.length; i++){
                if(i!= 0){
                    leftover += " " + split[i];
                } else
                leftover += split[i];
            }
            for(int i = 0; i<2 ; i++){
                if(i<split.length){
                    if(i!= 0) {
                        treeSavedAddress += " " +split[i];
                    } else treeSavedAddress += split[i];
                }

            }
            oldPrefix = prefix;
            if (prefix == null) {
                throw new IllegalArgumentException("calls keysWithPrefix() with null argument");
            }
            PriorityQueue<PriorityStrings> queue = new PriorityQueue<>();
            Node<TSTInterface> x = get(root, treeSavedAddress, 0);

            if (x == null) return makeArray(queue);
            if (x.val != null ){
                if(x.val instanceof DuplicateAddressNode){
                    for(bfst17.TSTInterface node: ((DuplicateAddressNode)x.val)){
                        addAddressNodeToQueue(queue,(AddressNode)node,treeSavedAddress);
                    }
                } else
                if(x.val instanceof AddressNode) {
                    queue.add(new PriorityStrings(1, treeSavedAddress + ", " + x.val.toString()));
                } else {
                    queue.add(new PriorityStrings(1, treeSavedAddress));
                }
            }
            collect(x.mid, new StringBuilder(treeSavedAddress), queue);
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
            if(x.val instanceof  DuplicateAddressNode){
                for(bfst17.TSTInterface node: ((DuplicateAddressNode)x.val)){
                    addAddressNodeToQueue(queue,(AddressNode) node,found);
                }
            }
             else if(x.val instanceof AddressNode)
            {
                addAddressNodeToQueue(queue,(AddressNode)x.val,found);
            }
                else{addOtherNodeToQueue(queue,found);}

        }
        collect(x.mid, prefix.append(x.c), queue);
        prefix.deleteCharAt(prefix.length() - 1);
        collect(x.right, prefix, queue);
    }

    public void addAddressNodeToQueue(PriorityQueue<PriorityStrings> queue, AddressNode x, String found){
        String compare = x.toString();
        double similarity = similarity(compare,leftover);

        double n = (((double) oldPrefix.length()  / ((double) found.length()+ x.toString().length()+2))+similarity);
        queue.add(new PriorityStrings(n,found + ", " +x.toString()));
    }

    public void addOtherNodeToQueue(PriorityQueue<PriorityStrings> queue, String found){
        double n = ((double) oldPrefix.length()  / (double) found.length());
        queue.add(new PriorityStrings(n,found));
    }

    public double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0;  }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }
}


