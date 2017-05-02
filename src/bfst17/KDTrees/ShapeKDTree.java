package bfst17.KDTrees;

import bfst17.Enums.WayType;
import bfst17.ShapeStructure.PolygonApprox;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class ShapeKDTree extends KDTree implements Serializable {
    boolean isVertical = true;
    WayType type;
    TreeNode root;

    public ShapeKDTree(WayType type) {
        Size = 0;
        this.root = null;
        this.type = type;
    }

    public WayType getType() {
        return type;
    }

    public class TreeNode implements Comparable<TreeNode>, Serializable {
        private double x, y;
        private PolygonApprox shape;
        private TreeNode low;
        private TreeNode high;
        private double highSplit;
        private double lowSplit;

        public TreeNode(PolygonApprox s, double x , double y) {
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

    public Shape getNearestNeighbour(Point2D point) {
        TreeNode champion = neighbourRecursion(point, root, true, root, 100);
        if(champion!=null) {
            return champion.shape;
        } else {
            return null;
        }
    }

    public double distance(Point2D p1, TreeNode node) {
        Rectangle2D bounds = node.shape.getBounds2D();
        Point2D p2 = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
        return Math.sqrt(Math.pow(p1.getX()-p2.getX(),2)+Math.pow(p1.getY()-p2.getY(),2));
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

    public void fillTreeWithShapes(List<Shape> shapes) {
        if (shapes.size() == 0) {
            return;
        }

        ArrayList<TreeNode> allShapesList = new ArrayList<>();
        for(int i=0;i<shapes.size();i++) {
            PolygonApprox shape = (PolygonApprox)shapes.get(i);
            Rectangle2D bounds = shape.getBounds2D();
            //System.out.println(bounds.getWidth()+bounds.getHeight());

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
    public HashSet<Line2D> lines;
    public HashSet<Shape> getInRange(Rectangle2D rect) {
        if(shapes==null) {
            shapes = new HashSet<>();
        } else {
            Iterator<Shape> iter = shapes.iterator();
            while (iter.hasNext()) {
                Shape s = iter.next();
                if(!s.getBounds2D().intersects(rect)) {
                    iter.remove();
                }
            }
        }
        lines = new HashSet<>();
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

    Integer maxDepth = 0;
    Integer tmpDepth=0;
    public TreeNode insert(TreeNode insertNode, TreeNode compareNode, boolean vertical) {
        Rectangle2D bounds;
        if (compareNode == null) {
            bounds = insertNode.shape.getBounds2D();
            insertNode.highSplit = bounds.getMaxX();
            insertNode.lowSplit = bounds.getMinX();
            root = insertNode;
            Size++;
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
            Size++;
            maxDepth = Math.max(tmpDepth, maxDepth);
            return insertNode;
        }
        return null;
    }
}