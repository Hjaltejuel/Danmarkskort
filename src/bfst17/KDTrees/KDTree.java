package bfst17.KDTrees;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Jens on 02-05-2017.
 */
    public abstract class KDTree implements Serializable {
    public Integer getSize() {
        return Size;
    }

    protected Integer Size=0;
    protected Integer tmpDepth=0;

    public Integer getMaxDepth() {
        return maxDepth;
    }

    protected Integer maxDepth=0;
    boolean isVertical;
    protected TreeNode root = null;

    public abstract TreeNode insert(TreeNode t1);
    public abstract void fillTreeWithShapes(java.util.List<Shape> shapes);

    public TreeNode getMedian(TreeNode[] allShapes, int lo, int hi, boolean vertical) {
        if (hi - lo == 0) {
            return allShapes[lo];
        }
        isVertical = vertical;
        Arrays.sort(allShapes, lo, hi + 1);
        Integer medianIndex = (lo + hi) / 2;
        return allShapes[lo];
    }

    public void insertArray(TreeNode[] allShapes, int lo, int hi, boolean vertical) {
        if (hi - lo == 0) {
            tmpDepth=0;
            insert(allShapes[lo]);
            return;
        }
        isVertical = vertical;
        Arrays.sort(allShapes, lo, hi + 1);
        Integer medianIndex = (lo + hi) / 2;
        tmpDepth=0;
        insert(allShapes[medianIndex]);
        //Indsæt medianerne fra de to subarrays (Uden at inkludere medianIndex)
        if (hi > medianIndex) {
            insertArray(allShapes, medianIndex + 1, hi, !vertical);
        }
        if (lo < medianIndex) {
            insertArray(allShapes, lo, medianIndex - 1, !vertical);
        }
    }

    public TreeNode insertNode(TreeNode nodeToInsert, TreeNode nodeToCompare, boolean vertical) {
        tmpDepth++;
        boolean isSmaller = vertical ? nodeToInsert.getX() < nodeToCompare.getX() : nodeToInsert.getY() < nodeToCompare.getY();
        TreeNode nextNode = isSmaller ? nodeToCompare.low : nodeToCompare.high;
        if (nextNode != null) {
            return insertNode(nodeToInsert, nextNode, !vertical);
        } else {
            Size++;
            maxDepth = Math.max(tmpDepth, maxDepth);
            if (isSmaller) {
                nodeToCompare.low = nodeToInsert;
            } else {
                nodeToCompare.high = nodeToInsert;
            }
            return nodeToInsert;
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

    public void getShapesBelowNodeInsideBounds(TreeNode startNode, Rectangle2D rect, boolean vertical) {
        if (startNode == null) {
            return;
        }

        Rectangle2D bounds = startNode.shape.getBounds2D();
        //Kun tegn det der er inde for skærmen
        shapes.add(startNode.getShape());

        if(startNode.getSplit() <= rect.getMaxX()) {
            getShapesBelowNodeInsideBounds(startNode.high, rect, !vertical);
        }
        if (rect.intersects(bounds)) {
            //shapes.add(startNode.getShape());
        }
/*
        boolean goLow = false, goHigh = false;
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
        }*/
        //getShapesBelowNodeInsideBounds(startNode.high, rect, !vertical);
        //getShapesBelowNodeInsideBounds(startNode.low, rect, !vertical);
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
        //ShapeTreeNode nextNode = nodeIsSmaller ? node.high : node.low;
        //ShapeTreeNode otherNode = !nodeIsSmaller ? node.high : node.low;

        //return neighbourRecursion(point, nextNode, !vertical, champion, bestDistance);
        return null;
    }
}
