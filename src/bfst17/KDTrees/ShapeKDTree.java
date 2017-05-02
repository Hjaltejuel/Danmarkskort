package bfst17.KDTrees;

import bfst17.Enums.WayType;
import bfst17.ShapeStructure.PolygonApprox;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class ShapeKDTree extends KDTree implements Serializable {
    WayType type;

    public ShapeKDTree(WayType type) {
        this.type = type;
    }

    public WayType getType() {
        return type;
    }

    public class ShapeTreeNode extends TreeNode {
        private double highSplit;
        private double lowSplit;

        boolean isVertical() {
            return isVertical;
        }

        public ShapeTreeNode(PolygonApprox s, double x , double y) {
            this.X = x;
            this.Y = y;
            this.shape = s;
        }
    }

    /*
    public void getShapesBelowNodeInsideBounds(TreeNode _startNode, Rectangle2D rect, boolean vertical) {
        if (_startNode == null) {
            return;
        }
        ShapeTreeNode startNode = (ShapeTreeNode)_startNode;
        Rectangle2D bounds = startNode.shape.getBounds2D();
        //Kun tegn det der er inde for skærmen
        if (rect.intersects(bounds)) {
            add(startNode);
        }

        boolean goLow = false, goHigh = false;
        if (vertical) {
            if (startNode.lowSplit <= rect.getMaxX()) {
                goHigh = true;
            }
            if (startNode.highSplit >= rect.getMinX()) {
                goLow = true;
            }
        } else {
            if (startNode.lowSplit <= rect.getMaxY()) {
                goHigh = true;
            }
            if (startNode.highSplit >= rect.getMinY()) {
                goLow = true;
            }
        }
        if (goLow || goHigh) {
            if (goHigh) {
                getShapesBelowNodeInsideBounds((ShapeTreeNode)startNode.high, rect, !vertical);
            }
            if (goLow) {
                getShapesBelowNodeInsideBounds((ShapeTreeNode)startNode.low, rect, !vertical);
            }
            return;
        }
        getShapesBelowNodeInsideBounds((ShapeTreeNode)startNode.high, rect, !vertical);
        getShapesBelowNodeInsideBounds((ShapeTreeNode)startNode.low, rect, !vertical);
    }
    /**/
    public void fillTreeWithShapes(List<Shape> shapes) {
        if (shapes.size() == 0) {
            return;
        }
        ArrayList<ShapeTreeNode> allShapesList = new ArrayList<>();
        for(int i=0;i<shapes.size();i++) {
            PolygonApprox shape = (PolygonApprox)shapes.get(i);
            Rectangle2D bounds = shape.getBounds2D();
            //System.out.println(bounds.getWidth()+bounds.getHeight());

            allShapesList.add(new ShapeTreeNode(shape, bounds.getCenterX(), bounds.getCenterY()));
            allShapesList.add(new ShapeTreeNode(shape, bounds.getMinX(), bounds.getMinY()));
            allShapesList.add(new ShapeTreeNode(shape, bounds.getMinX(), bounds.getMaxY()));
            allShapesList.add(new ShapeTreeNode(shape, bounds.getMaxX(), bounds.getMinY()));
            allShapesList.add(new ShapeTreeNode(shape, bounds.getMaxX(), bounds.getMaxY()));
        }
        ShapeTreeNode[] allShapes = allShapesList.toArray(new ShapeTreeNode[allShapesList.size()]);
        insertArray(allShapes, 0, allShapes.length - 1, true);
        allShapesList.clear();
    }
    /**/


    public ShapeTreeNode insert(TreeNode insertNode) {
        if(root==null) {
            root=insertNode;
            Size++;
        } else {
            tmpDepth=0;
            ShapeTreeNode newNode = (ShapeTreeNode) insertNode(insertNode, root, true);
        }
        ShapeTreeNode _insertNode = (ShapeTreeNode)insertNode;
        Rectangle2D bounds = _insertNode.shape.getBounds2D();

        //_insertNode.highSplit = !vertical ? bounds.getMaxX() : bounds.getMaxY();
        //_insertNode.lowSplit = !vertical ? bounds.getMinX() : bounds.getMinY();
        //System.out.println(root.highSplit + " " + root.lowSplit);
        return _insertNode;
    }
}