package bfst17;

import java.util.Arrays;
import java.util.List;

public class BST{
    Node rootNode = null;

    public void fillTree(String[] data){
        Arrays.sort(data);
        insertArray(data,0,data.length-1);
    }
    public void insertArray(String[] data,int lo, int hi){
        if (hi - lo == 0) {
            insert(rootNode,new Node(data[lo],null,null));
            return;
        }
        int median = (hi+lo)/2;
        Node insertNode = new Node(data[median],null,null);
        insert(rootNode,insertNode);
        if(hi>median) {
            insertArray(data, median+1, hi);
        }
        if(lo<median){
            insertArray(data, lo, median-1);
        }

    }
    public Node insert(Node compareNode, Node node){
        if(rootNode == null ){
            rootNode = node;
            return node;
        }

        double compare = compareNode.name.compareTo(node.name);
        if(compare>0){
            if(compareNode.leftNode!=null){
                insert(compareNode.leftNode,node);
            } else {
                compareNode.leftNode = node;
            }
        } else {
            if(compareNode.rightNode!=null){
                insert(compareNode.rightNode,node);
            } else {
                compareNode.rightNode = node;
            }
        }
        return node;


    }

    private class Node{
        String name;
        Node leftNode;
        Node rightNode;

        public Node(String name, Node leftNode, Node rightNode){
            this.name = name;
            this.leftNode = leftNode;
            this.rightNode = rightNode;
        }
    }
}