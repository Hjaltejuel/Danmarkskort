package bfst17.KDTrees;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

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
    public abstract <E> void fillTree(List<E> objs);

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
    private HashSet<TreeNode> nodes;
    public abstract <E> HashSet<E> getInRange(Rectangle2D rect);

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


    public TreeNode getNearestNeighbour(Point2D point) {
        TreeNode champion = neighbourRecursion(point, root, true, root, 100);
        if(champion!=null) {
            return champion;
        } else {
            return null;
        }
    }

    public TreeNode neighbourRecursion(Point2D point, TreeNode node, boolean vertical, TreeNode champion, double bestDistance) {
        if (node == null) {
            return champion;
        }

        double distance = node.distance(point);
        if (distance < bestDistance) {
            bestDistance = distance;
            champion = node;
        }

        boolean nodeIsSmaller = vertical ? node.getX() < point.getX() : node.getY() < point.getY();
        TreeNode nextNode = nodeIsSmaller ? node.high : node.low;
        TreeNode compareNode = null;
        if (vertical) {
            double yDiff = Math.abs(point.getY() - node.getY());
            if (yDiff < distance) {
                compareNode = nodeIsSmaller ? node.low : node.high;
            }
        } else {
            double xDiff = Math.abs(point.getX() - node.getX());
            if (xDiff < distance) {
                compareNode = nodeIsSmaller ? node.low : node.high;
            }
        }
        if (compareNode == null) {
            return neighbourRecursion(point, nextNode, !vertical, champion, bestDistance);
        } else {
            TreeNode champ1 = neighbourRecursion(point, nextNode, !vertical, champion, bestDistance);
            TreeNode champ2 = neighbourRecursion(point, compareNode, !vertical, champion, bestDistance);
            if (champ1.distance(point) < champ2.distance(point)) {
                return champ1;
            } else {
                return champ2;
            }
        }
    }
}
