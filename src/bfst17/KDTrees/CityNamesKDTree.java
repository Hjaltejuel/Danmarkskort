package bfst17.KDTrees;

import bfst17.AddressHandling.StreetAndPointNode;
import sun.reflect.generics.tree.Tree;
import sun.security.pkcs11.wrapper.CK_C_INITIALIZE_ARGS;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;


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

    HashSet<TreeNode> nodes = new HashSet<>();
    public HashSet<TreeNode> getInRange(Rectangle2D rect){
        nodes = new HashSet<>();
        getShapesBelowNodeInsideBounds(root, rect);
        return nodes;
    }

    public void getShapesBelowNodeInsideBounds(TreeNode startNode, Rectangle2D rect) {
        if (startNode == null) {
            return;
        }

        //Kun tegn det der er inde for skærmen
        if(startNode.isInside(rect)) {
            nodes.add(startNode);
        }

        boolean goLow = startNode.vertical ? startNode.getSplit() > rect.getMinX() : startNode.getSplit() > rect.getMinY();
        boolean goHigh = startNode.vertical ? startNode.getSplit() < rect.getMaxX() : startNode.getSplit() < rect.getMaxY();

        if(goLow) {
            getShapesBelowNodeInsideBounds(startNode.low, rect);
        }
        if(goHigh) {
            getShapesBelowNodeInsideBounds(startNode.high, rect);
        }
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
