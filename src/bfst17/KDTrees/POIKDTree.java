package bfst17.KDTrees;

import bfst17.Enums.PointsOfInterest;
import bfst17.OSMData.PointOfInterestObject;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;

public class POIKDTree extends KDTree implements Serializable {

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

        public boolean sortVertically() {
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



/*

private HashSet<POITreeNode> POISet;
public HashSet<POITreeNode> getInRange(Rectangle2D rect) {
    POISet = new HashSet<POITreeNode>();
    getShapesBelowNodeInsideBounds(root, rect, true);
    return POISet;
}

private void add(POITreeNode node){
    POISet.add(node);
}

public void getShapesBelowNodeInsideBounds(POITreeNode startNode, Rectangle2D rect, boolean vertical) {
    if (startNode == null) {
        return;
    }

    //Kun tegn det der er inde for skærmen
    Point2D point = new Point2D.Double(startNode.getX(),startNode.getY());
    if (rect.contains(point)) {
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
public POITreeNode insert(POITreeNode insertNode, POITreeNode compareNode, boolean vertical) {
    if (compareNode == null) {
        insertNode.split = insertNode.getX();
        root = insertNode;
        count++;
        return insertNode;
    }
    tmpDepth++;
    boolean isSmaller = vertical ? insertNode.getX() < compareNode.getX() : insertNode.getY() < compareNode.getY();
    POITreeNode nextNode = isSmaller ? compareNode.low : compareNode.high;
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
*/