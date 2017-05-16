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
    HashSet<TreeNode> nodes = new HashSet<>();
    boolean isVertical = true;

    public CityNamesKDTree() {
    }

    /**
     * Fylder træet med en type <E> som bliver castet til en StreetAndPointNode
     * @param _cityNames    Listen af bynavne
     * @param <E>           Generisk type der castes til StreetAndPointNode
     */
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


    /**
     * Indsætter en treeNode
     * @param insertNode    Noden der skal indsættes
     * @return              Noden der blev indsat
     */
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

    /**
     * Fylder et hashSet med TreeNodes indenfor den givne range
     * @param rect  Den givne range (Skærmbilledet)
     * @return      HashSettet med TreeNodes
     */
    public HashSet<TreeNode> getInRange(Rectangle2D rect){
        nodes = new HashSet<>();
        getShapesBelowNodeInsideBounds(root, rect);
        return nodes;
    }


    /**
     * Del 2 af getInRange
     * Fungerer rekursivt ved at lede ned igennem træet
     * Hvis hvis maxX / maxY (På skærmbilledets rect) er mere end startNodes splitpunkt så skal vi søge OP i træet
     * Hvis hvis minX / minY (På skærmbilledets rect) er mindre end startNodes splitpunkt så skal vi søge NED i træet
     * @param startNode     Den node der bliver sammenlignet med
     * @param rect          De bounds vi kigger indenfor
     */
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

        /**
         * Bruges til quickSelect for at se om arrayet af nodes skal sorteres efter X eller efter Y
         * @return er vertikal | er ikke vertical
         */
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
