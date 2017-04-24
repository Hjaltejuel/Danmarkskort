package bfst17;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class tmpKDTree implements Serializable {
    TreeNode root;
    int size;
    Point2D point;

    private enum compareType {minX, minY, maxX, maxY}

    compareType compareVar = compareType.minX;

    public tmpKDTree() {
        size = 0;
        root = null;
    }

    public TreeNode getRoot() {
        return root;
    }

    public class TreeNode implements Comparable<TreeNode>, Serializable {
        //private Point2D point;
        private Shape shape;
        private WayType type;
        private TreeNode left;
        private TreeNode right;
        private TreeNode down;
        private TreeNode up;
        private Rectangle2D bounds;

        public TreeNode(Shape s, WayType type) {
            this.shape = s;
            this.type = type;
            this.bounds = s.getBounds2D();
        }

        double getComparePoint(compareType cmpType) {
            boolean compareMin = cmpType == compareType.minX || cmpType == compareType.minY;
            boolean compareX = cmpType == compareType.maxX || cmpType == compareType.minX;
            double comparePoint;
            if (compareMin) {
                comparePoint = compareX ? bounds.getMinX() : bounds.getMinY();
            } else {
                comparePoint = compareX ? bounds.getMaxX() : bounds.getMaxY();
            }
            return comparePoint;
        }

        public int compareTo(TreeNode other) {
            return compare(this, other, compareVar);
        }
    }

    public int compare(TreeNode node1, TreeNode node2, compareType cmpType) {
        double cmp = node1.getComparePoint(cmpType) - node2.getComparePoint(cmpType);
        //System.out.println(node1.getComparePoint(cmpType) + " " + node2.getComparePoint(cmpType)+ " "+ cmp + " " +cmpType);
        if (cmp > 0) {
            return 1;
        } else if (cmp < 0) {
            return -1;
        }
        return 0;
    }

    public void fillTree(EnumMap<WayType, List<Shape>> shapes) {
        ArrayList<TreeNode> listOfShapes = new ArrayList<>();
        /**/
        for (WayType type : WayType.values()) {
            //if(type==WayType.UNKNOWN) { continue; }
            //WayType type = WayType.NATURAL_WOOD;
            List<Shape> list = shapes.get(type);
            for (Shape s : list) {
                listOfShapes.add(new TreeNode(s, type));
            }
        }
        TreeNode[] allShapes = listOfShapes.toArray(new TreeNode[listOfShapes.size()]);
        if (allShapes.length == 0) {
            return;
        }
        insertArray(allShapes, 0, allShapes.length - 1, compareType.maxX);
        //System.out.println("Max tree depth: " + maxDepth);
        //System.out.println("Der burde være: " + allShapes.length + " Der er: " + +count);
    }

    compareType getNextCompareType(compareType currentType) {
        return compareType.values()[(currentType.ordinal() + 1) % 4];
    }

    public void insertArray(TreeNode[] allShapes, int lo, int hi, compareType cmpType) {
        if (hi - lo == 0) {
            insert(allShapes[lo], root, compareType.minX);
            return;
        }
        compareVar = cmpType;
        Arrays.sort(allShapes, lo, hi + 1);
        Integer medianIndex = (lo + hi) / 2;
        insert(allShapes[medianIndex], root, compareType.minX);
        //Indsæt medianerne fra de to subarrays (Uden at inkludere medianIndex)
        if (lo < medianIndex) {
            insertArray(allShapes, lo, medianIndex - 1, getNextCompareType(cmpType));
        }
        if (hi > medianIndex) {
            insertArray(allShapes, medianIndex + 1, hi, getNextCompareType(cmpType));
        }
    }


    private EnumMap<WayType, List<Shape>> shapes;

    public EnumMap<WayType, List<Shape>> getInRange(Rectangle2D rect) {
        shapes = new EnumMap<>(WayType.class);
        for (WayType type : WayType.values()) {
            shapes.put(type, new ArrayList<>());
        }
        getShapesBelowNodeInsideBounds(root, rect);
        return shapes;
    }

    private void add(TreeNode node) {
        shapes.get(node.type).add(node.shape);
    }

    public void getShapesBelowNodeInsideBounds(TreeNode startNode, Rectangle2D rect) {
        if (startNode == null) {
            return;
        }
        add(startNode);
        //compareType nextCompareType = getNextCompareType(cmpType);
        //TreeNode screeenRectangleNode = new TreeNode(rect, WayType.NATURAL_COASTLINE);
        if (startNode.bounds.getMinX() < rect.getMaxX()) {
            getShapesBelowNodeInsideBounds(startNode.right, rect);
        }
        if (startNode.bounds.getMaxX() > rect.getMinX()) {
            getShapesBelowNodeInsideBounds(startNode.left, rect);
        }
        if (startNode.bounds.getMinY() < rect.getMaxY()) {
            getShapesBelowNodeInsideBounds(startNode.up, rect);
        }
        if (startNode.bounds.getMaxY() > rect.getMinY()) {
            getShapesBelowNodeInsideBounds(startNode.down, rect);
        }
    }

    Integer count = 0;
    Integer maxDepth = 0;
    Integer tmpDepth = 0;

    public TreeNode insert(TreeNode insertNode, TreeNode compareNode, compareType cmpType) {
        if (compareNode == null) {
            root = insertNode;
            count++;
            return insertNode;
        }
        if (compareNode == root) {
            tmpDepth = 0;
        }
        tmpDepth++;

        boolean isSmaller = compare(insertNode, compareNode, cmpType) < 0;

        TreeNode nextNode;
        boolean vert = (cmpType == compareType.maxY || cmpType == compareType.minY);
        if (vert) {
            nextNode = isSmaller ? compareNode.down : compareNode.up;
        } else {
            nextNode = isSmaller ? compareNode.left : compareNode.right;
        }
        if (nextNode != null) {
            insert(insertNode, nextNode, getNextCompareType(cmpType));
        } else {
            if (vert) {
                if (isSmaller) { //Ryk til venstre hvis comparisonNode er mindre end
                    compareNode.down = insertNode;
                } else {
                    compareNode.up = insertNode;
                }
            } else {
                if (isSmaller) { //Ryk til venstre hvis comparisonNode er mindre end
                    compareNode.left = insertNode;
                } else {
                    compareNode.right = insertNode;
                }
            }

            count++;
            maxDepth = Math.max(tmpDepth, maxDepth);
            return insertNode;
        }
        return null;
    }
}