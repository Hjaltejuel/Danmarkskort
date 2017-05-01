package bfst17.KDTrees;

import bfst17.ShapeStructure.PolygonApprox;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

/**
 * Created by Mads on 01/05/2017.
 */
public class RoadKDTree implements Serializable {
    TreeNode root;
    int size;
    boolean isVertical = true;

    public RoadKDTree() {
        size = 0;
        root = null;
    }

    public TreeNode getRoot() {
        return root;
    }

    public class TreeNode implements Comparable<TreeNode>, Serializable {
        private double x, y;
        private PolygonApprox shape;
        private String streetName;
        private TreeNode low;
        private TreeNode high;
        private double highSplit;
        private double lowSplit;

        public String getStreetName() {
            return streetName;
        }



        public TreeNode(PolygonApprox s, double x , double y, String streetName) {
            this.x = x;
            this.y = y;
            this.shape = s;
            this.streetName = streetName;
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

    public void fillTreeWithShapes(HashMap<String, Shape> shapes) {
        if (shapes.size() == 0) {
            return;
        }

        ArrayList<TreeNode> allShapesList = new ArrayList<>();


        for(String k : shapes.keySet()) {
            PolygonApprox shape = (PolygonApprox) shapes.get(k);
            Rectangle2D bounds = shape.getBounds2D();
            //System.out.println(bounds.getWidth()+bounds.getHeight());

            allShapesList.add(new TreeNode(shape, shape.getCenterX(), shape.getCenterY(), k));
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

    private HashSet<TreeNode> roadSet;

    public HashSet<TreeNode> getInRange(Rectangle2D rect) {
        roadSet = new HashSet<TreeNode>();
        getShapesBelowNodeInsideBounds(root, rect, true);
        return roadSet;
    }

    private void add(TreeNode node){
        roadSet.add(node);
    }

    public void getShapesBelowNodeInsideBounds(TreeNode startNode, Rectangle2D rect, boolean vertical) {
        if (startNode == null) {
            return;
        }
        Rectangle2D bounds = startNode.shape.getBounds2D();
        //Kun tegn det der er inde for skærmen
        if (rect.intersects(bounds)) {
            add(startNode);
        }

        boolean goLow = false, goHigh = false;
        if (vertical) {
            if (startNode.lowSplit <= rect.getMaxX()) {
                goHigh = true;
            }
            if (startNode.highSplit >= rect.getMinX()) {
                goLow = true;
            }
        } else {
            if (startNode.lowSplit <= rect.getMaxY()) {
                goHigh = true;
            }
            if (startNode.highSplit >= rect.getMinY()) {
                goLow = true;
            }
        }
        if (goLow || goHigh) {
            if (goHigh) {
                getShapesBelowNodeInsideBounds(startNode.high, rect, !vertical);
            }
            if (goLow) {
                getShapesBelowNodeInsideBounds(startNode.low, rect, !vertical);
            }
            return;
        }
        getShapesBelowNodeInsideBounds(startNode.high, rect, !vertical);
        getShapesBelowNodeInsideBounds(startNode.low, rect, !vertical);
    }

    Integer count = 0;
    Integer maxDepth = 0;
    Integer tmpDepth=0;
    public TreeNode insert(TreeNode insertNode, TreeNode compareNode, boolean vertical) {
        Rectangle2D bounds;
        if (compareNode == null) {
            bounds = insertNode.shape.getBounds2D();
            insertNode.highSplit = bounds.getMaxX();
            insertNode.lowSplit = bounds.getMinX();
            root = insertNode;
            count++;
            return insertNode;
        }
        bounds = insertNode.shape.getBounds2D();
        tmpDepth++;
        boolean isSmaller = vertical ? insertNode.getX() < compareNode.getX() : insertNode.getY() < compareNode.getY();
        TreeNode nextNode = isSmaller ? compareNode.low : compareNode.high;
        if (nextNode != null) {
            insert(insertNode, nextNode, !vertical);
        }
        else {
            insertNode.highSplit = !vertical ? bounds.getMaxX() : bounds.getMaxY();
            insertNode.lowSplit = !vertical ? bounds.getMinX() : bounds.getMinY();

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
