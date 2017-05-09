package bfst17.KDTrees;

import bfst17.Enums.WayType;
import bfst17.RoadNode;
import bfst17.ShapeStructure.PolygonApprox;

import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

public class RoadKDTree extends KDTree {
    WayType type;

    public RoadKDTree(WayType type) {
        this.type = type;
    }

    public WayType getType() {
        return type;
    }

    public <E> void fillTree(List<E> roadNodes) {
        if (roadNodes.size() == 0) {
            return;
        }

        ArrayList<TreeNode> allShapesList = new ArrayList<>();
        for (int i = 0; i < roadNodes.size(); i++) {
            RoadNode rdNode = (RoadNode) roadNodes.get(i);
            Rectangle2D bounds = rdNode.getShape().getBounds2D();

            allShapesList.add(new RoadTreeNode(bounds.getCenterX(), bounds.getCenterY(), rdNode));
            allShapesList.add(new RoadTreeNode(bounds.getMinX(), bounds.getMinY(), rdNode));
            allShapesList.add(new RoadTreeNode(bounds.getMinX(), bounds.getMaxY(), rdNode));
            allShapesList.add(new RoadTreeNode(bounds.getMaxX(), bounds.getMinY(), rdNode));
            allShapesList.add(new RoadTreeNode(bounds.getMaxX(), bounds.getMaxY(), rdNode));
        }
        TreeNode[] allShapes = allShapesList.toArray(new TreeNode[allShapesList.size()]);
        insertArray(allShapes, 0, allShapes.length - 1, true);
    }

    public TreeNode insert(TreeNode insertNode) {
        if (root == null) {
            root = insertNode;
            root.vertical = true;
            Size++;
        } else {
            tmpDepth = 1;
            insertNode(insertNode, root);
        }
        return insertNode;
    }

    public class RoadTreeNode extends TreeNode {
        private RoadNode roadNode;

        private RoadTreeNode(double x, double y, RoadNode node) {
            this.X = x;
            this.Y = y;
            this.roadNode = node;
        }

        protected boolean sortVertically() {
            return isVertical;
        }

        protected boolean isInside(Rectangle2D rect) {
            return rect.contains(X, Y);
        }

        private RoadNode getRoadNode() {
            return roadNode;
        }

        public PolygonApprox getShape() {
            return getRoadNode().getShape();
        }

        public String getRoadName() {
            return getRoadNode().getRoadName();
        }

        public WayType getType() {
            return type;
        }
    }
}