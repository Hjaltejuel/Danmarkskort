package bfst17;

import java.awt.geom.Point2D;

public class KDTree{
    public Node root;
    int size;
    Point2D point;

    public KDTree(){
        size = 0;
        root = null;
    }
    public static class Node {
        private Point2D point;
        private Node left;
        private Node right;
        private boolean isVertical;

        public Node(Point2D point,Node left,Node right, boolean isVertical) {
            this.point = point;
            this.left = left;
            this.right = right;
            this.isVertical = isVertical;
        }
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public void insert(Point2D p) {
        root = insert(root, p, true);
    }

    public Node insert(Node node, Point2D point, boolean isVertical){
        if (node == null) {
            size++;
            return new Node(point, null, null, isVertical);
        }

        // do not insert the point if it is already in the 2d-tree;
        // return the existing node instead.
        if (point.equals(node.point)) {
            return node;
        }
        if(isSmallerThanPointInNode(point,node)){
            node.left = insert(node.left,point,!node.isVertical);
        } else {
            node.right = insert(node.left,point,!node.isVertical);
        }
        return node;
    }
    private boolean isSmallerThanPointInNode(Point2D p, Node node) {
        double cmp = node.isVertical ? p.getX()-node.point.getX() : (p.getY()-node.point.getY());
        return (cmp < 0);
    }
}