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
    Point2D point;
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

        public TreeNode(Shape s, double x , double y) {
            this.x = x;
            this.y = y;
            this.shape = s;
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
        //System.out.println("Max tree depth: " + maxDepth);
        //System.out.println("Der burde være: " + allShapes.length + " Der er: " + +count);
    }

    public void insertArray(TreeNode[] allShapes, int lo, int hi, boolean vertical) {
        if (hi - lo == 0) {
            tmpDepth=0;
            insert(allShapes[lo], root, true);
            return;
        }
        isVertical = vertical;
        Arrays.sort(allShapes, lo, hi+1);
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
    public HashSet<Line2D> lines;
    public HashSet<Shape> getInRange(Rectangle2D rect) {
        shapes = new HashSet<>();
        lines= new HashSet<>();
        getShapesBelowNodeInsideBounds(root, rect, true);
        //System.out.println(shapes.size());
        return shapes;
    }

    private void add(TreeNode node){
        shapes.add(node.shape);
    }

    boolean isLargerThan(TreeNode node, double x, double y, boolean vertical) {
        boolean isBigger = vertical ? node.getX() > x : node.getY() > y;
        return isBigger;
    }

    public void getShapesBelowNodeInsideBounds(TreeNode startNode, Rectangle2D rect, boolean vertical) {
        if (startNode == null) {
            return;
        }

        Line2D line;
        Line2D line1;
        if(vertical) {
            line = new Line2D.Double( startNode.lowSplit, 0, startNode.lowSplit,-60);
            line1 = new Line2D.Double( startNode.highSplit, 0, startNode.highSplit,-60);
        } else{
            line = new Line2D.Double(50,startNode.lowSplit, 0, startNode.lowSplit);
            line1 = new Line2D.Double(50,startNode.highSplit, 0, startNode.highSplit);
        }
        lines.add(line);
        lines.add(line1);


        /*if(rect.contains(startNode.shape.getBounds2D())) {
            add(startNode);
        }*/
        add(startNode);

        if(vertical){
            //if(startNode.highSplit<rect.getMinY()){return;}
            if(startNode.lowSplit < rect.getMaxX()) {
                getShapesBelowNodeInsideBounds(startNode.high, rect, !vertical);
            }
            if(startNode.highSplit > rect.getMinX()) {
                getShapesBelowNodeInsideBounds(startNode.low, rect, !vertical);
            }
        } else {
            //System.out.println(startNode.highSplit-rect.getMinY());
            if (startNode.highSplit > rect.getMinY()) {
                getShapesBelowNodeInsideBounds(startNode.low, rect, !vertical);
            }
            if (startNode.lowSplit < rect.getMaxY()) {
                getShapesBelowNodeInsideBounds(startNode.high, rect, !vertical);
            }
        }
        /*
        if(vertical){
            //if(startNode.highSplit<rect.getMinY()){return;}
            if(startNode.lowSplit > rect.getMaxX()) {
                getShapesBelowNodeInsideBounds(startNode.low, rect, !vertical);
                return;
            }
            if(startNode.highSplit <rect.getMinX()) {
                getShapesBelowNodeInsideBounds(startNode.high, rect, !vertical);
                return;
            }
        } else {
            if (startNode.highSplit < rect.getMinY()) {
                getShapesBelowNodeInsideBounds(startNode.high, rect, !vertical);
                return;
            }
            if (startNode.lowSplit > rect.getMaxY()) {
                getShapesBelowNodeInsideBounds(startNode.low, rect, !vertical);
                return;
            }
        }
        getShapesBelowNodeInsideBounds(startNode.high, rect, !vertical);
        getShapesBelowNodeInsideBounds(startNode.low, rect, !vertical);
        */
        /*
        if(rect.contains(startNode.shape.getBounds2D())) {
            //System.out.println("A");
            //add(startNode);
            getShapesBelowNodeInsideBounds(startNode.high, rect, !vertical);
            getShapesBelowNodeInsideBounds(startNode.low, rect, !vertical);
            return;
        }
        */
    }

    Integer count = 0;
    Integer maxDepth = 0;
    Integer tmpDepth=0;
    public TreeNode insert(TreeNode insertNode, TreeNode compareNode, boolean vertical) {
        if (compareNode == null) {
            insertNode.highSplit = insertNode.shape.getBounds2D().getMaxX();
            insertNode.lowSplit = insertNode.shape.getBounds2D().getMinX();
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
            insertNode.highSplit = !vertical ? insertNode.shape.getBounds2D().getMaxX() : insertNode.shape.getBounds2D().getMaxY();
            insertNode.lowSplit = !vertical ? insertNode.shape.getBounds2D().getMinX() : insertNode.shape.getBounds2D().getMinY();
            if (isSmaller) { //Ryk til venstre hvis comparisonNode er mindre end
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