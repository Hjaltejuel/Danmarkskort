package bfst17.KDTrees;

import bfst17.Enums.PointsOfInterest;
import bfst17.OSMData.PointOfInterestObject;
import sun.reflect.generics.tree.Tree;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;

public class POIKDTree extends KDTree {
    HashSet<TreeNode> nodes = new HashSet<>();
    boolean isVertical = true;

    public POIKDTree() {
    }

    /**
     * Fylder træet med en type <E> som bliver castet til en PointsOfInterestObject
     * @param POIS    Listen af points of i nterests
     * @param <E>     Generisk type der castes til PointsOfInterestObject
     */
    public <E> void fillTree(List<E> POIS) {
        if (POIS.size() == 0) {
            return;
        }
        ArrayList<POITreeNode> allPOISList = new ArrayList<>();
        for(E EPOIObj :  POIS) {
            PointOfInterestObject POIObj = (PointOfInterestObject)EPOIObj;
            allPOISList.add(new POITreeNode(POIObj));
        }
        POITreeNode[] allShapes = allPOISList.toArray(new POITreeNode[allPOISList.size()]);
        insertArray(allShapes, 0, allShapes.length - 1, true);
    }

    /**
     * Indsætter en treeNode
     * @param insertNode    Noden der skal indsættes
     * @return              Noden der blev indsat
     */
    public POITreeNode insert(TreeNode insertNode) {
        if(root==null) {
            root=insertNode;
            root.vertical=true;
            Size++;
        } else {
            tmpDepth=1;
            POITreeNode newNode = (POITreeNode) insertNode(insertNode, root);
        }
        POITreeNode _insertNode = (POITreeNode) insertNode;
        return _insertNode;
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

    public class POITreeNode extends TreeNode {
        private PointsOfInterest POIType;

        public PointsOfInterest getPOIType() {
            return POIType;
        }

        protected boolean sortVertically() {
            return isVertical;
        }

        protected boolean isInside(Rectangle2D rect) {
            return rect.contains(X,Y);
        }

        public POITreeNode(PointOfInterestObject POIObj) {
            POIType = POIObj.getType();
            X = POIObj.getX();
            Y = POIObj.getY();
        }
    }
}