package bfst17.KDTrees;

import bfst17.AddressHandling.StreetAndPointNode;
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
        if (_cityNames.size() == 0) {
            return;
        }
        ArrayList<TreeNode> allCitiesList = new ArrayList<>();
        for(E _E : _cityNames) {
            StreetAndPointNode node = (StreetAndPointNode)_E;
            allCitiesList.add(new CityNameTreeNode(node.getName(), node.getPoint()));
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
            insertNode(insertNode, root);
        }

        return insertNode;
    }

    public class CityNameTreeNode extends TreeNode {
        private String cityName;

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
