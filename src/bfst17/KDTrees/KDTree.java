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
    protected Integer Size=0;
    protected Integer tmpDepth=0;
    protected Integer maxDepth=0;
    protected boolean isVertical;
    protected TreeNode root = null;

    public Integer getSize() {
        return Size;
    }

    public Integer getMaxDepth() {
        return maxDepth;
    }

    public abstract TreeNode insert(TreeNode t1);
    public abstract void fillTreeWithShapes(java.util.List<Shape> shapes);

    public void insertArray(TreeNode[] allShapes, int lo, int hi, boolean vertical) {
        if (hi - lo == 0) {
            insert(allShapes[lo]);
            return;
        }
        isVertical = vertical;
        Integer medianIndex = (lo + hi) / 2;
        TreeNode insertNode = QuickSelect.select(allShapes, medianIndex, lo, hi);
        insert(insertNode);
        //Indsæt medianerne fra de to subarrays (Uden at inkludere medianIndex)
        if (hi > medianIndex) {
            insertArray(allShapes, medianIndex + 1, hi, !vertical);
        }
        if (lo < medianIndex) {
            insertArray(allShapes, lo, medianIndex - 1, !vertical);
        }
    }

    public TreeNode insertNode(TreeNode nodeToInsert, TreeNode nodeToCompare) {
        tmpDepth++;
        boolean vertical = nodeToCompare.vertical;

        boolean isSmaller = vertical ? nodeToInsert.getX() < nodeToCompare.getX() : nodeToInsert.getY() < nodeToCompare.getY();
        TreeNode nextNode = isSmaller ? nodeToCompare.low : nodeToCompare.high;
        if (nextNode != null) {
            return insertNode(nodeToInsert, nextNode);
        } else {
            Size++;
            maxDepth = Math.max(tmpDepth, maxDepth);
            nodeToInsert.vertical = !vertical;
            if (isSmaller) {
                nodeToCompare.low = nodeToInsert;
            } else {
                nodeToCompare.high = nodeToInsert;
            }
            return nodeToInsert;
        }
    }
    private HashSet<Shape> shapes;
    public HashSet<Shape> getInRange(Rectangle2D rect) {
        if(shapes==null) {
            shapes = new HashSet<>();
        } else {
            Iterator<Shape> iter = shapes.iterator();
            while (iter.hasNext()) {
                Rectangle2D bounds = iter.next().getBounds2D();
                if(!bounds.intersects(rect) || !rect.contains(bounds)) {
                    iter.remove();
                }
            }
            //System.out.println(shapes.size());
        }
        shapes = new HashSet<>();
        getShapesBelowNodeInsideBounds(root, rect);
        return shapes;
    }

    public void drawTree(Graphics2D g) {
        c=0;
        g.setColor(Color.blue);
        drawTreeNode(g, root, 0 ,0);
        System.out.println(c);
    }
    int c;
    public void drawTreeNode(Graphics2D g, TreeNode node, Integer X, Integer Y) {
        if(node==null){return;}
        c++;
        Integer reverseDepth = maxDepth-Y;
        g.fill(new Rectangle2D.Double(X*reverseDepth+300,Y*6+100,5,5));
        drawTreeNode(g, node.low, X-1, Y+1);
        drawTreeNode(g, node.high, X+1, Y+1);
    }

    public void getShapesBelowNodeInsideBounds(TreeNode startNode, Rectangle2D rect) {
        if (startNode == null) {
            return;
        }

        Rectangle2D bounds = startNode.shape.getBounds2D();
        //Kun tegn det der er inde for skærmen
        shapes.add(startNode.getShape());

        boolean goLow = startNode.vertical ? startNode.getSplit() > rect.getMaxX() : startNode.getSplit() > rect.getMaxY();
        boolean goHigh = startNode.vertical ? startNode.getSplit() < rect.getMinX() : startNode.getSplit() < rect.getMinY();


        if(goLow||goHigh) {
            if (goLow) {
                getShapesBelowNodeInsideBounds(startNode.low, rect);
            }
            if (goHigh) {
                getShapesBelowNodeInsideBounds(startNode.high, rect);
            }
            return;
        }
        getShapesBelowNodeInsideBounds(startNode.low, rect);
        getShapesBelowNodeInsideBounds(startNode.high, rect);
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
