package bfst17;

import com.sun.org.apache.bcel.internal.generic.StackConsumer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

public class KDTree {
    TreeNode root;
    int size;
    Point2D point;
    boolean isVertical = true;

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
        Integer depth=0;

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
            arrLength += list.size() * 4;
        }
        TreeNode[] allShapes = new TreeNode[arrLength];
        Integer index = 0;
        for (WayType type : WayType.values()) {
            List<Shape> list = shapes.get(type);
            for (Shape s: list) {
                //Add en node for hvert hjørne i bounds
                Rectangle2D bounds = s.getBounds2D();
                allShapes[index++] = (new TreeNode(bounds.getX(), bounds.getY(), s, type));
                allShapes[index++] = (new TreeNode(bounds.getX(), bounds.getY() + bounds.getHeight(), s, type));
                allShapes[index++] = (new TreeNode(bounds.getX() + bounds.getWidth(), bounds.getY(), s, type));
                allShapes[index++] = (new TreeNode(bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight(), s, type));
            }
        }
        insertArray(allShapes, 0, arrLength, true);
        System.out.println("Max tree depth: " + maxDepth);
        System.out.println("Der burde være: " + arrLength + " Der er: " +  + count);
    }

    public void insertArray(TreeNode[] allShapes, int lo, int hi, boolean vertical) {
        if (hi - lo <=1) {
            stackCount=0;
            insert(allShapes[lo], root, true);
            return;
        }
        isVertical = vertical;
        Arrays.sort(allShapes, lo, hi);
        Integer medianIndex = (lo + hi) / 2;
        stackCount=0;
        insert(allShapes[medianIndex], root, true);
        //Indsæt medianerne fra de to subarrays (Uden at inkludere medianIndex)
        if (lo < medianIndex) {
            insertArray(allShapes, lo, medianIndex, !vertical);
        }
        if (hi > medianIndex) {
            insertArray(allShapes, medianIndex + 1, hi, !vertical);
        }
    }

    HashSet<TreeNode> nodes;
    public HashSet<TreeNode> getInRange(Rectangle2D rect) {
        nodes = new HashSet<>();
        getShapesBelowNodeInsideBounds(root, rect);
        return nodes;
    }

    boolean isLargerThan(TreeNode node, double x, double y) {
        //System.out.println(y+" "+node.getY());
        //System.out.println(x+" "+node.getX());
        boolean vertical=node.depth%2==0;
        boolean isBigger = vertical ? node.getX() > x : node.getY() > y;
        return isBigger;
    }

    public void getShapesBelowNodeInsideBounds(TreeNode startNode, Rectangle2D rect) {
        if (startNode == null) {
            return;
        }
        nodes.add(startNode);
        if (isLargerThan(startNode, rect.getMinX(), rect.getMinY())) {
            getShapesBelowNodeInsideBounds(startNode.left, rect);
        }
        if (!isLargerThan(startNode, rect.getMaxX(), rect.getMaxY())) {
            getShapesBelowNodeInsideBounds(startNode.right, rect);
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    Integer count = 0;
    Integer maxDepth = 0;
    Integer stackCount=0;
    public TreeNode insert(TreeNode insertNode, TreeNode compareNode, boolean vertical) {
        if(insertNode==compareNode){
            System.out.println("Wtf?");
            return insertNode;

        }
        stackCount++;
        //System.out.println(stackCount);
        if (compareNode == null) {
            root = insertNode;
            count++;
            return insertNode;
        }
        insertNode.depth=compareNode.depth+1;
        boolean isSmaller = vertical ? insertNode.getX() < compareNode.getX() : insertNode.getY() < compareNode.getY();
        TreeNode nextNode = isSmaller ? compareNode.left : compareNode.right;
        if (nextNode != null) {
                insert(insertNode, nextNode, !vertical);

        }
        else {
            if (isSmaller) { //Ryk til venstre hvis comparisonNode er mindre end
                compareNode.left = insertNode;
            } else {
                compareNode.right = insertNode;
            }
            count++;
            maxDepth = Math.max(insertNode.depth, maxDepth);
            return insertNode;
        }
        return null;
    }
}