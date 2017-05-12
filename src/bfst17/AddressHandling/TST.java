package bfst17.AddressHandling;
import bfst17.OSMData.OSMNode;


import sun.awt.image.ImageWatched;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TST<TSTInterface> implements Serializable {
    private int n;
    private Node<TSTInterface> root;
    private String suffix = "";
    private String prefix = "";
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
    /**
     * Description: returns the value of a specific key by call on get, used for searching, saves the suffix (city and postcode val)
       If it finds a duplicateAddressNode, return the val that matches the suffix
     * @param unfilteredKey the key to search for
     * @return TSTInterface (AddressNode,OSMNode)
     */
    public TSTInterface get(String unfilteredKey) {
        String suffix = "";
        //no commas, makes it easyer to match
        String key = unfilteredKey.replace(",", "");
        //match the pattern of citty and postcode
        Matcher matcher = pattern.matcher(key);
        if(matcher.find()){
            //save the suffix
            suffix = key.substring(matcher.start(),matcher.end());
            //save the new filtered key
            key = key.substring(0,matcher.start()-1);
        }
        if (key == null) {
            throw new IllegalArgumentException("calls get() with null argument");
        }
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        //return the given keys node;
        Node<TSTInterface> x = get(root, key, 0);
        if (x == null) return null;
        //Check if the there are duplicates of the given address in other city's
        if(x.val instanceof DuplicateAddressNode){
            //run trough the duplicates and find the one that matches the given keys suffix and then return it
            for(bfst17.AddressHandling.TSTInterface node: (DuplicateAddressNode)(x.val)){
                if(node instanceof AddressNode){
                if(node.getAddress().equals(suffix)) {
                    return (TSTInterface) node;
                 }
                }
                else {
                    int randomNum = ThreadLocalRandom.current().nextInt(0, ((DuplicateAddressNode) x.val).size());
                    return (TSTInterface) ((DuplicateAddressNode) x.val).get(randomNum);
                }
                }
            }
        //if it isnt a duplicate return the node, this makes it so you can also search on addresses without suffix
        // if there arent duplicates
        return x.val;
    }

    /**
     * Description: returns the end node of a given line of nodes, whose char value corresponds to the key
     * Does this by recursively calling itself
     * @param x: the node to compare the key to
     * @param key: the key we are searching for
     * @param d: the index of the string we are on
     * @return Node
     */
    private Node<TSTInterface> get(Node<TSTInterface> x, String key, int d) {
        if (x == null) return null;
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        //saves the char we are currently at
        char c = key.charAt(d);
        //performs recursive calls unitl we find the given node to the key
        //if the char is smaller go left
        if (c < x.c) return get(x.left, key, d);
        //if the char is bigger go right
         else if (c > x.c) return get(x.right, key, d);
        //if the char matches but we arent at the end of the key go down the middle
         else if (d < key.length() - 1) return get(x.mid, key, d + 1);
        else return x;
    }

    /**
     * Description: the put method which starts the recursive chain of calls, calls put with the root node
     * @param key: the key to be placed in the tree
     * @param val: the val to be placed in the tree
     */
    public void put(String key, TSTInterface val) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with null key");
        }
        n++;
        //starts the recursive chain
        root = put(root, key, val, 0);
    }

    /**
     * Description: the recursive put method, which places a key and a val into the tree
     * @param x: The node to compare the key to
     * @param key: the key to be placed
     * @param val: the val to be placed
     * @param d: the index of the key we are working on
     * @return: Node
     */
    private Node<TSTInterface> put(Node<TSTInterface> x, String key, TSTInterface val, int d) {
        //The char at the current index
        char c = key.charAt(d);
        //If the compare node = null make a new node and give it the current index char val
        if (x == null) {
            x = new Node<TSTInterface>();
            x.c = c;
        }
        //if the key char is smaller than the node char go left
        if (c < x.c) x.left = put(x.left, key, val, d);
        //if the key char is bigger than the node char go right
        else if (c > x.c) x.right = put(x.right, key, val, d);
        //if the key char equals the node char but we are not at the end of the key go in the middle
        else if (d < key.length() - 1) x.mid = put(x.mid, key, val, d + 1);
        // if there is already a node with the keys prefix, then addShape the val to be entered to the node val
        else  if(x.val != null){
            //if there is multiple instances of the node, then addShape the val to the list
            if(x.val instanceof DuplicateAddressNode){
                ((DuplicateAddressNode) x.val).add((bfst17.AddressHandling.TSTInterface)val);
            } else {
                //if there is only one addressNode, make a new list and addShape them both
                DuplicateAddressNode addresArray = new DuplicateAddressNode();
                addresArray.add((bfst17.AddressHandling.TSTInterface) x.val);
                addresArray.add((bfst17.AddressHandling.TSTInterface) val);
                x.val = (TSTInterface) addresArray;
            }
            //else make the value of the new node the value of the key
        } else x.val = val;
        return x;
    }

    /**
     * Description: Method for returning a list of the top 5 mathces to the param: prefix
     * @param prefixUser: the string to be searched for
     * @return ArrayList<String>
     */
    public ArrayList<String> keysWithPrefix(String prefixUser) {
        //cant match with nothing
        if (prefixUser != "") {
                //set the prefix
                this.prefix = prefixUser;
                //delete the commas
                String[] split = prefix.split(",");
                //reset the values
                this.suffix = "";
                prefix = "";
                //run trough the split array and make the suffix
                prefix = split[0];
                //run trough the split array and make the suffix
                if (split.length > 1) {
                    suffix = split[1];
                }

                if (prefix == null) {
                    throw new IllegalArgumentException("calls keysWithPrefix() with null argument");
                }
                //make a new priority queue and get the node belonging to the prefix if there are any
                PriorityQueue<PriorityStrings> queue = new PriorityQueue<>();
                Node<TSTInterface> x = get(root, prefix, 0);

                if (x == null) return makeArray(queue);

                //If the prefix had a val, (only happens with cities and regions)
                if (x.val != null) {
                    //if there are duplicates, addShape them all
                    if (x.val instanceof DuplicateAddressNode) {
                        for (bfst17.AddressHandling.TSTInterface node : ((DuplicateAddressNode) x.val)) {
                            if (node instanceof AddressNode) {
                                addAddressNodeToQueue(queue, (AddressNode) node, prefix);
                            } else {
                                addOtherNodeToQueue(queue, prefix);
                            }
                        }
                    } else
                        //if there only is one match and its an address
                        if (x.val instanceof AddressNode) {
                            queue.add(new PriorityStrings(1, prefix + ", " + x.val.toString()));
                        } else {
                            //if there only is one match and its a city or region
                            queue.add(new PriorityStrings(1, prefix));
                        }
                }
                //starts the recursive addToQueue call
                addToQueue(x.mid, new StringBuilder(prefix), queue);

                return makeArray(queue);
            } else return null;
        }

    /**
     * Description: method for making the list of the top 5 results
     * @param q: priority queue with results
     * @return: Arraylist<containing top 5 results></>
     */
    public ArrayList<String> makeArray(PriorityQueue<PriorityStrings> q){
        ArrayList<String> returnList = new ArrayList<>();
        for(int i = 0; i<5 && !q.isEmpty();i++){

            returnList.add(q.poll().getAddress());
        }
        return returnList;
    }
    
    
    private void addToQueue(Node<TSTInterface> x, StringBuilder prefix, PriorityQueue<PriorityStrings> queue) {
        if (x == null) return;
        addToQueue(x.left, prefix, queue);

        if (x.val != null ) {
            String found = prefix.toString() + x.c;
            if(x.val instanceof  DuplicateAddressNode){
                for(bfst17.AddressHandling.TSTInterface node: ((DuplicateAddressNode)x.val)){
                    if(node instanceof AddressNode) {
                        addAddressNodeToQueue(queue, (AddressNode) node, found);
                    } else if(node instanceof OSMNode){
                        addOtherNodeToQueue(queue,found);
                    }
                }
            }
             else if(x.val instanceof AddressNode)
            {
                addAddressNodeToQueue(queue,(AddressNode)x.val,found);
            }
                else{addOtherNodeToQueue(queue,found);}

        }
        addToQueue(x.mid, prefix.append(x.c), queue);
        prefix.deleteCharAt(prefix.length() - 1);
        addToQueue(x.right, prefix, queue);
    }

    public void addAddressNodeToQueue(PriorityQueue<PriorityStrings> queue, AddressNode x, String found){
        String compare = x.toString();
        double similarity = similarity(compare,suffix);
        double n = (((double) this.prefix.length()  / ((double) found.length()+ x.toString().length()))+similarity);
        //System.out.println(prefix + " " + n);
        queue.add(new PriorityStrings(n,found + ", " +x.toString()));
    }

    public void addOtherNodeToQueue(PriorityQueue<PriorityStrings> queue, String found){
        double n = ((double) this.prefix.length()  / (double) found.length());
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


