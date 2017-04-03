package bfst17;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

public class KDTree {
    TreeNode root;
    int size;
    Point2D point;
    boolean isVertical = false;

    public KDTree() {
        size = 0;
        root = null;
    }

    public TreeNode getRoot() {
        return root;
    }

    public class TreeNode implements Comparable<TreeNode> {
        //private Point2D point;
        private double x, y;
        private Shape shape;
        private WayType type;
        private TreeNode left;
        private TreeNode right;

        public TreeNode(double x, double y, Shape s, WayType type) {
            this.x = x;
            this.y = y;
            this.shape = s;
            this.type = type;
        }

        public WayType getType() {
            return type;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }

        public Shape getShape() {
            return shape;
        }

        public int compareTo(TreeNode other) {
            double cmp = isVertical ? x - other.getX() : y - other.getY();
            if (cmp > 0) {
                return 1;
            } else if (cmp < 0) {
                return -1;
            }
            return 0;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }

    public void fillTree(EnumMap<WayType, List<Shape>> shapes) {
        Integer arrLength = 0;
        for (List<Shape> list : shapes.values()) {
            arrLength += list.size();// * 4;
        }
        TreeNode[] allShapes = new TreeNode[arrLength];
        Integer index = 0;
        for (WayType type : WayType.values()) {
            List<Shape> list = shapes.get(type);
            for (Shape s: list) {
                allShapes[index++] = (new TreeNode(s.getBounds2D().getCenterX(), s.getBounds2D().getCenterX(), s, type));
            }
            /*
            List<Shape> list = shapes.get(type);
            for (Shape s: list) {
                //Add en node for hvert hjørne i bounds
                Rectangle2D bounds = s.getBounds2D();
                allShapes[index++] = (new TreeNode(bounds.getX(), bounds.getY(), s, type));
                allShapes[index++] = (new TreeNode(bounds.getX(), bounds.getY() + bounds.getHeight(), s, type));
                allShapes[index++] = (new TreeNode(bounds.getX() + bounds.getWidth(), bounds.getY(), s, type));
                allShapes[index++] = (new TreeNode(bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight(), s, type));
            }
            */
        }
        Arrays.sort(allShapes);
        insertArray(allShapes, 0, arrLength - 1, true);
        System.out.println("Max tree depth: " + maxDepth);
        System.out.println("Der burde være: " + arrLength + " Der er: " +  + count);
    }

    public void insertArray(TreeNode[] allShapes, int lo, int hi, boolean vertical) {
        if (hi - lo == 0) {
            insert(allShapes[lo], root, vertical);
            return;
        }
        isVertical = vertical;
        Arrays.sort(allShapes, lo, hi);
        Integer medianIndex = (lo + hi) / 2;
        insert(allShapes[medianIndex], root, vertical);
        //Indsæt medianerne fra de to subarrays (Uden at inkludere medianIndex)
        if (lo < medianIndex) {
            insertArray(allShapes, lo, medianIndex - 1, !vertical);
        }
        if (hi > medianIndex) {
            insertArray(allShapes, medianIndex + 1, hi, !vertical);
        }
    }

    public HashSet<TreeNode> getInRange(Rectangle2D rect) {
        nodes = new HashSet<>();
        TreeNode startNode = root;
        getShapesBelowNodeInsideBounds(startNode, true, rect);
        return nodes;
    }

    HashSet<TreeNode> nodes;

    boolean isLargerThan(TreeNode node, double x, double y, boolean vertical) {
        double compare = vertical ? x - node.getX() : y - node.getY();
        return compare <= 0;
    }

    public void getShapesBelowNodeInsideBounds(TreeNode startNode, boolean vertical, Rectangle2D rect) {
        if (startNode == null) {
            return;
        }
        nodes.add(startNode);
        if (isLargerThan(startNode, rect.getX(), rect.getY(), vertical)) {
            getShapesBelowNodeInsideBounds(startNode.left, !vertical, rect);
        }
        if (!isLargerThan(startNode, rect.getMaxX(), rect.getMaxY(), vertical)) {
            getShapesBelowNodeInsideBounds(startNode.right, !vertical, rect);
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void insert(Point2D p) {
        //root = insert(root, p, true);
    }

    Integer count = 0;
    Integer maxDepth = 0;
    Integer tmpDepth = 0;
    public TreeNode insert(TreeNode insertNode, TreeNode compareNode, boolean vertical) {
        if (compareNode == null) {
            root = insertNode;
            count++;
            return insertNode;
        }
        if(compareNode==root){
            tmpDepth=0;
        }
        double compare = vertical ? insertNode.getX() - compareNode.getX() : insertNode.getY() - compareNode.getY();
        TreeNode nextNode = compare < 0 ? compareNode.left : compareNode.right;
        if (nextNode != null) {
            tmpDepth++;
            insert(insertNode, nextNode, !vertical);
        }
        else {
            if (compare < 0) { //Ryk til venstre hvis comparisonNode er mindre end
                compareNode.left = insertNode;
                count++;
                maxDepth = Math.max(tmpDepth, maxDepth);
                return insertNode;
                //System.out.println("Indsat ("+node.getX()+","+node.getY()+") til venstre "+depth);
            } else { //Ellers til højre
                compareNode.right = insertNode;
                count++;
                maxDepth = Math.max(tmpDepth, maxDepth);
                return insertNode;
                //System.out.println("Indsat ("+node.getX()+","+node.getY()+") til højre "+depth);
            }
        }
        return null;
    }

    public TreeNode insert(TreeNode node) {
        count++;
        if (root == null) {
            root = node;
            return node;
        }
        TreeNode comparisonNode = root;
        Integer depth = 0;
        while (true) { //Snyder lidt her :P
            boolean _isVertical = depth % 2 == 0; //Skifter ved hver depth
            double compare = _isVertical ? node.getX() - comparisonNode.getX() : node.getY() - comparisonNode.getY();
            depth++;
            if (compare < 0) { //Ryk til venstre hvis comparisonNode er mindre end
                if (comparisonNode.left != null) {
                    comparisonNode = comparisonNode.left;
                    continue;
                }
                comparisonNode.left = node;
                maxDepth = Math.max(depth, maxDepth);
                //System.out.println("Indsat ("+node.getX()+","+node.getY()+") til venstre "+depth);
                break;
            } else { //Ellers til højre
                if (comparisonNode.right != null) {
                    comparisonNode = comparisonNode.right;
                    continue;
                }
                comparisonNode.right = node;
                maxDepth = Math.max(depth, maxDepth);
                //System.out.println("Indsat ("+node.getX()+","+node.getY()+") til højre "+depth);
                break;
            }
        }
        return node;
    }
}