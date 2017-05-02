package bfst17.KDTrees;

import bfst17.Enums.WayType;
import bfst17.RoadNode;
import bfst17.ShapeStructure.PolygonApprox;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class RoadKDTree implements Serializable {
    TreeNode root;
    WayType type;
    int size;
    boolean isVertical = true;

    public RoadKDTree(WayType type) {
        size = 0;
        root = null;
        this.type = type;
    }

    public WayType getType() {
        return type;
    }

    public TreeNode getRoot() {
        return root;
    }

    public class TreeNode implements Comparable<TreeNode>, Serializable {
        private double x, y;
        private RoadNode roadNode;
        private TreeNode low;
        private TreeNode high;
        private double highSplit;
        private double lowSplit;

        private TreeNode( double x, double y,RoadNode node) {
            this.x = x;
            this.y = y;
            this.roadNode = node;
        }
        private RoadNode getRoadNode(){return roadNode;}
        public PolygonApprox getShape (){return getRoadNode().getShape();}
        public String getRoadName(){return getRoadNode().getRoadName();}

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

    public Shape getNearestNeighbour(Point2D point) {
        TreeNode champion = neighbourRecursion(point, root, true, root, 100);
        if (champion != null) {
            return champion.getShape();
        } else {
            return null;
        }
    }

    public double distance(Point2D p1, TreeNode node) {
        Rectangle2D bounds = node.getShape().getBounds2D();
        Point2D p2 = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
    }

    public TreeNode neighbourRecursion(Point2D point, TreeNode node, boolean vertical, TreeNode champion, double bestDistance) {
        if (node == null) {
            return champion;
        }

        double distance = distance(point, node);
        if (distance < bestDistance) {
            bestDistance = distance;
            champion = node;
        }
        boolean nodeIsSmaller = vertical ? node.getX() < point.getX() : node.getY() < point.getY();
        TreeNode nextNode = nodeIsSmaller ? node.high : node.low;
        TreeNode otherNode = !nodeIsSmaller ? node.high : node.low;

        return neighbourRecursion(point, nextNode, !vertical, champion, bestDistance);
    }


    public void fillTreeWithPoints(List<Point2D> points) {

    }

    public void fillTreeWithShapes(ArrayList<RoadNode> shapes) {
        if (shapes.size() == 0) {
            return;
        }

        ArrayList<TreeNode> allShapesList = new ArrayList<>();
        for (int i = 0; i < shapes.size(); i++) {
            PolygonApprox shape = (PolygonApprox) shapes.get(i).getShape();
            Rectangle2D bounds = shape.getBounds2D();

            allShapesList.add(new TreeNode(bounds.getCenterX(), bounds.getCenterY(),shapes.get(i)));
            allShapesList.add(new TreeNode(bounds.getMinX(), bounds.getMinY(),shapes.get(i)));
            allShapesList.add(new TreeNode(bounds.getMinX(), bounds.getMaxY(),shapes.get(i)));
            allShapesList.add(new TreeNode(bounds.getMaxX(), bounds.getMinY(),shapes.get(i)));
            allShapesList.add(new TreeNode(bounds.getMaxX(), bounds.getMaxY(),shapes.get(i)));
        }
        TreeNode[] allShapes = allShapesList.toArray(new TreeNode[allShapesList.size()]);
        insertArray(allShapes, 0, allShapes.length - 1, true);
    }

    public void insertArray(TreeNode[] allShapes, int lo, int hi, boolean vertical) {
        if (hi - lo == 0) {
            tmpDepth = 0;
            insert(allShapes[lo], root, true);
            return;
        }
        isVertical = vertical;
        Arrays.sort(allShapes, lo, hi + 1);
        Integer medianIndex = (lo + hi) / 2;
        tmpDepth = 0;
        insert(allShapes[medianIndex], root, true);
        //Indsæt medianerne fra de to subarrays (Uden at inkludere medianIndex)
        if (hi > medianIndex) {
            insertArray(allShapes, medianIndex + 1, hi, !vertical);
        }
        if (lo < medianIndex) {
            insertArray(allShapes, lo, medianIndex - 1, !vertical);
        }
    }

    private HashSet<RoadNode> roadSet;

    public HashSet<RoadNode> getInRange(Rectangle2D rect) {
        roadSet = new HashSet<>();
        getShapesBelowNodeInsideBounds(root, rect, true);
        return roadSet;
    }

    private void add(TreeNode node) {
        roadSet.add(node.getRoadNode());
    }

    public void getShapesBelowNodeInsideBounds(TreeNode startNode, Rectangle2D rect, boolean vertical) {
        if (startNode == null) {
            return;
        }
        Rectangle2D bounds = startNode.getShape().getBounds2D();
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
    Integer tmpDepth = 0;

    public TreeNode insert(TreeNode insertNode, TreeNode compareNode, boolean vertical) {
        Rectangle2D bounds;
        if (compareNode == null) {
            bounds = insertNode.getShape().getBounds2D();
            insertNode.highSplit = bounds.getMaxX();
            insertNode.lowSplit = bounds.getMinX();
            root = insertNode;
            count++;
            return insertNode;
        }
        bounds = insertNode.getShape().getBounds2D();
        tmpDepth++;
        boolean isSmaller = vertical ? insertNode.getX() < compareNode.getX() : insertNode.getY() < compareNode.getY();
        TreeNode nextNode = isSmaller ? compareNode.low : compareNode.high;
        if (nextNode != null) {
            insert(insertNode, nextNode, !vertical);
        } else {
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