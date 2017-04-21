package bfst17;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class KDTree implements Serializable {
    TreeNode root;
    int size;
    Point2D point;
    boolean isVertical = true;

    public KDTree() {
        size = 0;
        root = null;
    }

    public TreeNode getRoot() {
        return root;
    }

    public class TreeNode implements Comparable<TreeNode>, Serializable {
        //private Point2D point;
        private double x, y;
        private Shape shape;
        private WayType type;
        private TreeNode left;
        private TreeNode right;

        public TreeNode(double x, double y, Shape s, WayType type) {
            this.x = x;
            this.y = y;
            this.shape = s;
            this.type = type;
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

    public void fillTree(EnumMap<WayType, List<Shape>> shapes) {
        ArrayList<TreeNode> listOfShapes = new ArrayList<>();
        /**/
        for (WayType type : WayType.values()) {
            //WayType type = WayType.NATURAL_WOOD;
            List<Shape> list = shapes.get(type);
            for (Shape s : list) {
                //Add en node for hvert hjørne i bounds
                Rectangle2D bounds = s.getBounds2D();
                listOfShapes.add(new TreeNode(bounds.getCenterX(), bounds.getCenterY(), s, type));
                listOfShapes.add(new TreeNode(bounds.getX(), bounds.getY(), s, type));
                listOfShapes.add(new TreeNode(bounds.getX(), bounds.getY() + bounds.getHeight(), s, type));
                listOfShapes.add(new TreeNode(bounds.getX() + bounds.getWidth(), bounds.getY(), s, type));
                listOfShapes.add(new TreeNode(bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight(), s, type));
            }
        }
        TreeNode[] allShapes = listOfShapes.toArray(new TreeNode[listOfShapes.size()]);
        if(allShapes.length==0) {
            return;
        }
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


    private EnumMap<WayType, List<Shape>> shapes;
    public EnumMap<WayType, List<Shape>> getInRange(Rectangle2D rect) {
        shapes = new EnumMap<>(WayType.class);
        for (WayType type : WayType.values()) {
            shapes.put(type, new ArrayList<>());
        }
        getShapesBelowNodeInsideBounds(root, rect, true);
        return shapes;
    }

    private void add(TreeNode node){
        shapes.get(node.type).add(node.shape);
    }

    boolean isLargerThan(TreeNode node, double x, double y, boolean vertical) {
        boolean isBigger = vertical ? node.getX() > x : node.getY() > y;
        return isBigger;
    }

    public void getShapesBelowNodeInsideBounds(TreeNode startNode, Rectangle2D rect, boolean vertical) {
        if (startNode == null) {
            return;
        }
        if(rect.contains(startNode.getX(),startNode.getY())){
            add(startNode);
        }
        if (isLargerThan(startNode, rect.getMinX(), rect.getMinY(), vertical)) {
            getShapesBelowNodeInsideBounds(startNode.left, rect, !vertical);
        }
        if (!isLargerThan(startNode, rect.getMaxX(), rect.getMaxY(), vertical)) {
            getShapesBelowNodeInsideBounds(startNode.right, rect, !vertical);
        }
    }

    Integer count = 0;
    Integer maxDepth = 0;
    Integer tmpDepth=0;
    public TreeNode insert(TreeNode insertNode, TreeNode compareNode, boolean vertical) {
        if (compareNode == null) {
            root = insertNode;
            count++;
            return insertNode;
        }
        tmpDepth++;
        boolean isSmaller = vertical ? insertNode.getX() < compareNode.getX() : insertNode.getY() < compareNode.getY();
        TreeNode nextNode = isSmaller ? compareNode.left : compareNode.right;
        if (nextNode != null) {
            insert(insertNode, nextNode, !vertical);
        }
        else {
            if (isSmaller) { //Ryk til venstre hvis comparisonNode er mindre end
                compareNode.left = insertNode;
            } else {
                compareNode.right = insertNode;
            }
            count++;
            maxDepth = Math.max(tmpDepth, maxDepth);
            return insertNode;
        }
        return null;
    }
}