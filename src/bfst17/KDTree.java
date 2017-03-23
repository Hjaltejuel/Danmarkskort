package bfst17;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.UncheckedIOException;

public class KDTree{
    Node root;
    int size;
    Point2D point;

    public KDTree() {
        size = 0;
        root = null;
    }

    public Node getRoot() {
        return root;
    }
/*
    public static class TreeNode {
        private Point2D point;
        private TreeNode left;
        private TreeNode right;
        private boolean isVertical;

        public TreeNode(Point2D point, TreeNode left, TreeNode right, boolean isVertical) {
            this.point = point;
            this.left = left;
            this.right = right;
            this.isVertical = isVertical;
        }
    }
*/
    public boolean isEmpty(){
        return size == 0;
    }

    public void insert(Point2D p) {
        //root = insert(root, p, true);
    }

    public Node insert(Node node){
        if(root==null){
            root=node;
            return node;
        }
        Node comparisonNode = root;
        Integer depth = 0;
        while(true) { //Snyder lidt her :P
            boolean isVertical = depth%2==0; //Skifter ved hver depth
            double compare = isVertical ? node.getX() - comparisonNode.getX() : node.getY() - comparisonNode.getY();
            depth++;
            if (compare < 0) { //Ryk til venstre hvis comparisonNode er mindre end
                if (comparisonNode.left != null) {
                    comparisonNode=comparisonNode.left;
                    continue;
                }
                comparisonNode.left=node;
                //System.out.println("Indsat ("+node.getX()+","+node.getY()+") til venstre "+depth);
                break;
            } else { //Ellers til højre
                if (comparisonNode.right != null) {
                    comparisonNode=comparisonNode.right;
                    continue;
                }
                comparisonNode.right=node;
                //System.out.println("Indsat ("+node.getX()+","+node.getY()+") til højre "+depth);
                break;
            }
        }

        /*if (node == null) {
            size++;
            return new TreeNode(point, null, null, isVertical);
        }*/



        // do not insert the point if it is already in the 2d-tree;
        // return the existing node instead.
        /*
        if (point.equals(node.point)) {
            return node;
        }
        if(isSmallerThanPointInNode(point,node)){
            node.left = insert(node.left,point,!node.isVertical);
        } else {
            node.right = insert(node.left,point,!node.isVertical);
        }
        */
        return node;
    }

    /*
    private boolean isSmallerThanPointInNode(Point2D p, Node node) {
        double cmp = isVertical ? p.getX()-node.getX() : (p.getY()-node.getY());
        return (cmp < 0);
    }
    */
}