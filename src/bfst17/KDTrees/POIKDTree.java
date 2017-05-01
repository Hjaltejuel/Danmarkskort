package bfst17.KDTrees;

import bfst17.Enums.POIclasification;
import bfst17.Enums.PointsOfInterest;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class POIKDTree implements Serializable {
    TreeNode root;
    int size;
    boolean isVertical = true;

    public POIKDTree() {
        size = 0;
        root = null;
    }

    public TreeNode getRoot() {
        return root;
    }

    public class TreeNode implements Comparable<TreeNode>, Serializable {
        private double x;
        private double y;
        private TreeNode low;
        private TreeNode high;
        private double split;
        private PointsOfInterest POIType;

        public TreeNode(PointsOfInterest POIType, Point2D point) {
            this.POIType = POIType;
            this.x = point.getX();
            this.y = point.getY();
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

    public void fillTree(HashMap<String,HashSet<Point2D>> POIS) {
        if (POIS.size() == 0) {
            return;
        }
        ArrayList<TreeNode> allPOISList = new ArrayList<>();
        for(String value : POIS.keySet()) {
            PointsOfInterest POIType = PointsOfInterest.valueOf(value);
            HashSet<Point2D> POILocations = POIS.get(value);
            for(Point2D point : POILocations) {
                allPOISList.add(new TreeNode(POIType, point));
            }
        }
        TreeNode[] allShapes = allPOISList.toArray(new TreeNode[allPOISList.size()]);
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

    private HashSet<TreeNode> POISet;
    public HashSet<TreeNode> getInRange(Rectangle2D rect) {
        POISet = new HashSet<TreeNode>();
        getShapesBelowNodeInsideBounds(root, rect, true);
        return POISet;
    }

    private void add(TreeNode node){
        POISet.add(node);
    }

    public void getShapesBelowNodeInsideBounds(TreeNode startNode, Rectangle2D rect, boolean vertical) {
        if (startNode == null) {
            return;
        }

        //Kun tegn det der er inde for skærmen
        Point2D point = new Point2D.Double(startNode.getX(),startNode.getY());
        if (rect.contains(point)) {
            add(startNode);
        }

        boolean goHigh=false, goLow=false;
        if (vertical) {
            if (startNode.split <= rect.getMaxX()) {
                goHigh = true;
            }
            if (startNode.split >= rect.getMinX()) {
                goLow = true;
            }
        } else {
            if (startNode.split <= rect.getMaxY()) {
                goHigh = true;
            }
            if (startNode.split >= rect.getMinY()) {
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
        if (compareNode == null) {
            insertNode.split = insertNode.getX();
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
            insertNode.split = !vertical ? insertNode.getX() : insertNode.getY();

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