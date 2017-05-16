package bfst17.KDTrees;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;


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

    /**
     * Indsætter et array af treenodes. Funktionen er rekursiv og deler sig hele tiden op i to
     * de elementer i arrayet der er mellem 'lo' og 'hi' bliver sorteret efter enten X | Y
     * @param allShapes     Alle treenodes
     * @param lo            den lave ende af sorteringsfeltet
     * @param hi            den høje ende af sorteringsfeltet
     * @param vertical      om der skal sorteret efter X | Y
     */
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

    /**
     * Indsætter en node rekursivt i træet.
     * Hvis nodeToCompare har nodes under sig, så dykker vi længere ned i træet, ved at sætte de nodes der ligger under til nodeToCompare
     * @param nodeToInsert      Den node der skal indsættes
     * @param nodeToCompare     Den der sammenlignes med
     * @return
     */
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
    public abstract <E> HashSet<E> getInRange(Rectangle2D rect);

    /**
     * Igangsætter en nearest neighbour søgning
     * @param point     Hvilket punkt der skal søges fra
     * @return          Den tætteste TreeNode
     */
    public TreeNode getNearestNeighbour(Point2D point) {
        TreeNode champion = neighbourRecursion(point, root, true, root, 100);
        if(champion!=null) {
            return champion;
        } else {
            return null;
        }
    }

    /**
     * Den rekursive søgning der kører gennem træet
     * @param point     Punktet der søges fra
     * @param node      Den node der tages distance til
     * @param vertical  Hvorvidt noden skiller på X eller Y
     * @param champion  den tættest node indtilvidere
     * @param bestDistance  den bedste afstand indtilvidere
     * @return
     */
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
