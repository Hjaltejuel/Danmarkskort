package bfst17.KDTrees;

import bfst17.Enums.PointsOfInterest;
import bfst17.OSMData.PointOfInterestObject;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;

public class POIKDTree extends KDTree {

    boolean isVertical = true;

    public POIKDTree() {
    }

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