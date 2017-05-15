package bfst17.KDTrees;

import bfst17.Directions.Graph;
import bfst17.Directions.GraphNode;
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
        if (roadNodes==null || roadNodes.size() == 0) {
            return;
        }

        ArrayList<TreeNode> allShapesList = new ArrayList<>();

        for (int i = 0; i < roadNodes.size(); i++) {
            RoadNode rdNode = (RoadNode) roadNodes.get(i);
            float[] xyVal = new float[2];
            PathIterator it = rdNode.getShape().getPathIterator(null,0);
            int k = 0;
            while(!it.isDone()) {

                it.currentSegment(xyVal);
                allShapesList.add(new RoadTreeNode(rdNode,rdNode.getNodes().get(k)));
                it.next();
                k++;
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


    HashSet<RoadNode> nodes = new HashSet<>();
    public HashSet<RoadNode> getInRange(Rectangle2D rect){
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
            nodes.add(((RoadTreeNode)startNode).getRoadNode());
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

    public class RoadTreeNode extends TreeNode {
        private RoadNode roadNode;
        private GraphNode graphNode;

        private RoadTreeNode(RoadNode node,GraphNode graphNode) {
            this.X = graphNode.getX();
            this.Y = graphNode.getY();
            this.graphNode = graphNode;
            this.roadNode = node;
        }

        protected boolean sortVertically() {
            return isVertical;
        }

        protected boolean isInside(Rectangle2D rect) {
            return rect.contains(X, Y);
        }

        public GraphNode getGraphNode(){return graphNode;}

        public RoadNode getRoadNode() {
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