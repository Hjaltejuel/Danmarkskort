package bfst17;

import sun.reflect.generics.tree.Tree;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class KDTree implements Serializable {
    TreeNode root;
    WayType type;
    int size;
    boolean isVertical = true;

    public KDTree(WayType type) {
        size = 0;
        root = null;
        this.type = type;
    }

    public TreeNode getRoot() {
        return root;
    }

    public class TreeNode implements Comparable<TreeNode>, Serializable {
        //private Point2D point;
        private double x, y;
        private Shape shape;
        private TreeNode low;
        private TreeNode high;
        private double highSplit;
        private double lowSplit;
        private Rectangle2D bounds;

        public TreeNode(Shape s, double x , double y) {
            this.x = x;
            this.y = y;
            this.shape = s;
            bounds = s.getBounds2D();
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
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

    public void fillTreeWithShapes(List<Shape> shapes) {
        if (shapes.size() == 0) {
            return;
        }

        ArrayList<TreeNode> allShapesList = new ArrayList<>();
        for(int i=0;i<shapes.size();i++) {
            Shape shape = shapes.get(i);
            Rectangle2D bounds = shape.getBounds2D();
            allShapesList.add(new TreeNode(shape, bounds.getCenterX(), bounds.getCenterY()));
            allShapesList.add(new TreeNode(shape, bounds.getMinX(), bounds.getMinY()));
            allShapesList.add(new TreeNode(shape, bounds.getMinX(), bounds.getMaxY()));
            allShapesList.add(new TreeNode(shape, bounds.getMaxX(), bounds.getMinY()));
            allShapesList.add(new TreeNode(shape, bounds.getMaxX(), bounds.getMaxY()));
        }
        TreeNode[] allShapes = allShapesList.toArray(new TreeNode[allShapesList.size()]);
        insertArray(allShapes, 0, allShapes.length - 1, true);
    }

    public void insertArray(TreeNode[] allShapes, int lo, int hi, boolean vertical) {
        if (hi - lo == 0) {
            tmpDepth=0;
            insert(allShapes[lo], root, true);
            return;
        }
        isVertical = vertical;
        Arrays.sort(allShapes, lo, hi + 1);
        Integer medianIndex = (lo + hi) / 2;
        tmpDepth=0;
        insert(allShapes[medianIndex], root, true);
        //Indsæt medianerne fra de to subarrays (Uden at inkludere medianIndex)
        if (hi > medianIndex) {
            insertArray(allShapes, medianIndex + 1, hi, !vertical);
        }
        if (lo < medianIndex) {
            insertArray(allShapes, lo, medianIndex - 1, !vertical);
        }
    }


    private HashSet<Shape> shapes;
    public HashSet<Shape> getInRange(Rectangle2D rect) {
        shapes = new HashSet<>();
        getShapesBelowNodeInsideBounds(root, rect, true);
        return shapes;
    }

    private void add(TreeNode node){
        shapes.add(node.shape);
    }

    public void getShapesBelowNodeInsideBounds(TreeNode startNode, Rectangle2D rect, boolean vertical) {
        if (startNode == null) {
            return;
        }

        //Kun tegn det der er inde for skærmen
        if(rect.intersects(startNode.bounds)) {
            add(startNode);
        }

        if(vertical){
            if(startNode.lowSplit < rect.getMaxX()) {
                getShapesBelowNodeInsideBounds(startNode.high, rect, !vertical);
            }
            if(startNode.highSplit > rect.getMinX()) {
                getShapesBelowNodeInsideBounds(startNode.low, rect, !vertical);
            }
        } else {
            if (startNode.highSplit > rect.getMinY()) {
                getShapesBelowNodeInsideBounds(startNode.low, rect, !vertical);
            }
            if (startNode.lowSplit < rect.getMaxY()) {
                getShapesBelowNodeInsideBounds(startNode.high, rect, !vertical);
            }
        }
    }

    Integer count = 0;
    Integer maxDepth = 0;
    Integer tmpDepth=0;
    public TreeNode insert(TreeNode insertNode, TreeNode compareNode, boolean vertical) {
        if (compareNode == null) {
            insertNode.highSplit = insertNode.bounds.getMaxX();
            insertNode.lowSplit = insertNode.bounds.getMinX();
            root = insertNode;
            count++;
            return insertNode;
        }
        tmpDepth++;
        boolean isSmaller = vertical ? insertNode.getX() < compareNode.getX() : insertNode.getY() < compareNode.getY();
        TreeNode nextNode = isSmaller ? compareNode.low : compareNode.high;
        if (nextNode != null) {
            insert(insertNode, nextNode, !vertical);
        }
        else {
            insertNode.highSplit = !vertical ? insertNode.bounds.getMaxX() : insertNode.bounds.getMaxY();
            insertNode.lowSplit = !vertical ? insertNode.bounds.getMinX() : insertNode.bounds.getMinY();
            if (isSmaller) {
                compareNode.low = insertNode;
            } else {
                compareNode.high = insertNode;
            }
            count++;
            maxDepth = Math.max(tmpDepth, maxDepth);
            return insertNode;
        }
        return null;
    }
}