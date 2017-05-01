package bfst17.KDTrees;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Mads on 28/04/2017.
 */
public class CityNamesKDTree implements Serializable {
    TreeNode root;
    int size;
    boolean isVertical = true;

    public CityNamesKDTree() {
        size = 0;
        root = null;
    }

    public TreeNode getRoot() {
        return root;
    }

    public class TreeNode implements Comparable<TreeNode>, Serializable {
        private String cityName;
        private String typeOfCity;
        private Point2D point;
        private TreeNode low;
        private TreeNode high;
        private double split;

        public String getCityName() {
            return cityName;
        }



        public TreeNode(String cityName, Point2D point) {
            this.cityName = cityName;
            this.point = point;
        }

        public int compareTo(TreeNode other) {
            double cmp = isVertical ? point.getX() - other.getX() : point.getY() - other.getY();
            if (cmp > 0) {
                return 1;
            } else if (cmp < 0) {
                return -1;
            }
            return 0;
        }

        public double getX() {
            return point.getX();
        }

        public double getY() {
            return point.getY();
        }
    }

    public void fillTree(HashMap<String, Point2D> cityNames) {
        if (cityNames.size() == 0) {
            return;
        }
        ArrayList<TreeNode> allCitiesList = new ArrayList<>();
        for(String k : cityNames.keySet()) {
            String city = k;
            allCitiesList.add(new TreeNode(k, cityNames.get(k)));

        }
        TreeNode[] allCities = allCitiesList.toArray(new TreeNode[allCitiesList.size()]);
        insertArray(allCities, 0, allCities.length - 1, true);
    }


    public void insertArray(TreeNode[] allCities, int lo, int hi, boolean vertical) {
        if (hi - lo == 0) {
            tmpDepth=0;
            insert(allCities[lo], root, true);
            return;
        }
        isVertical = vertical;
        Arrays.sort(allCities, lo, hi + 1);
        Integer medianIndex = (lo + hi) / 2;
        tmpDepth=0;
        insert(allCities[medianIndex], root, true);
        //Indsæt medianerne fra de to subarrays (Uden at inkludere medianIndex)
        if (hi > medianIndex) {
            insertArray(allCities, medianIndex + 1, hi, !vertical);
        }
        if (lo < medianIndex) {
            insertArray(allCities, lo, medianIndex - 1, !vertical);
        }
    }

    private HashSet<TreeNode> citySet;

    public HashSet<TreeNode> getInRange(Rectangle2D rect) {
        citySet = new HashSet<TreeNode>();
        getShapesBelowNodeInsideBounds(root, rect, true);
        return citySet;
    }

    private void add(TreeNode node){
        citySet.add(node);
    }

    public void getShapesBelowNodeInsideBounds(TreeNode startNode, Rectangle2D rect, boolean vertical) {
        if (startNode == null) {
            return;
        }

        //Kun tegn det der er inde for skærmen
        if (rect.contains(startNode.point)) {
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
