package bfst17.KDTrees;

import sun.security.pkcs11.wrapper.CK_C_INITIALIZE_ARGS;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Mads on 28/04/2017.
 */
public class CityNamesKDTree extends KDTree {
    boolean isVertical = true;

    public CityNamesKDTree() {
    }


    public <E> void fillTree(List<E> _cityNames) {
        HashMap<String, Point2D> cityNames = (HashMap<String, Point2D>) _cityNames;
        if (cityNames.size() == 0) {
            return;
        }
        ArrayList<TreeNode> allCitiesList = new ArrayList<>();
        for(String k : cityNames.keySet()) {
            String city = k;
            allCitiesList.add(new CityNameTreeNode(k, cityNames.get(k)));
        }
        TreeNode[] allCities = allCitiesList.toArray(new TreeNode[allCitiesList.size()]);
        insertArray(allCities, 0, allCities.length - 1, true);
    }

    public TreeNode insert(TreeNode insertNode) {
        if (root == null) {
            root = insertNode;
            root.vertical=true;
            Size++;
        } else {
            tmpDepth=1;
            CityNameTreeNode newNode = (CityNameTreeNode) insertNode(insertNode, root);
        }
        CityNameTreeNode _insertNode = (CityNameTreeNode) insertNode;
        Rectangle2D bounds = _insertNode.shape.getBounds2D();

        return _insertNode;
        /*
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

        return insertNode;
        */
    }

    public class CityNameTreeNode extends TreeNode {
        private String cityName;
        private String typeOfCity;

        public String getCityName() {
            return cityName;
        }

        boolean sortVertically() {
            return isVertical;
        }

        protected boolean isInside(Rectangle2D rect) {
            return rect.contains(X,Y);
        }

        public CityNameTreeNode(String cityName, Point2D point) {
            this.cityName = cityName;
            this.X = point.getX();
            this.Y = point.getY();
        }
    }
}
