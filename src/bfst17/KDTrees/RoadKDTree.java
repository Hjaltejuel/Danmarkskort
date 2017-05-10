package bfst17.KDTrees;

import bfst17.Enums.WayType;
import bfst17.RoadNode;
import bfst17.ShapeStructure.PolygonApprox;

import java.awt.geom.PathIterator;
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
            float[] xyVal = new float[2];
            PathIterator it = rdNode.getShape().getPathIterator(null,0);
            while(!it.isDone()){
                it.currentSegment(xyVal);
                allShapesList.add(new RoadTreeNode(xyVal[0],xyVal[1],rdNode.getType(),rdNode.getRoadName()));
                it.next();
            }
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
        String name;
        WayType type;
        private RoadTreeNode(double x, double y, WayType type, String name) {
            this.X = x;
            this.Y = y;
            this.name = name;
            this.type = type;

        }

        public String getName(){return name;}

        protected boolean sortVertically() {
            return isVertical;
        }

        protected boolean isInside(Rectangle2D rect) {
            return rect.contains(X, Y);
        }

        public WayType getType() {
            return type;
        }
    }
}