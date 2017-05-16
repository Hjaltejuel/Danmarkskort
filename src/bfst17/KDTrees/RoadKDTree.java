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
    HashSet<RoadNode> nodes = new HashSet<>();
    WayType type;

    public RoadKDTree(WayType type) {
        this.type = type;
    }

    public WayType getType() {
        return type;
    }

    /**
     * Fylder træet med en type <E> som bliver castet til en roadNode
     * @param roadNodes    Listen af veje
     * @param <E>           Generisk type der castes til roadNode
     */
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
                allShapesList.add(new RoadTreeNode(xyVal[0],xyVal[1],rdNode,rdNode.getNodes().get(k)));
                it.next();
                k++;
            }

        }
        TreeNode[] allShapes = allShapesList.toArray(new TreeNode[allShapesList.size()]);
        insertArray(allShapes, 0, allShapes.length - 1, true);
    }

    /**
     * Indsætter en treeNode
     * @param insertNode    Noden der skal indsættes
     * @return              Noden der blev indsat
     */
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
    /**
     * Fylder et hashSet med RoadNodes indenfor den givne range
     * @param rect  Den givne range (Skærmbilledet)
     * @return      HashSettet med RoadNodes
     */
    public HashSet<RoadNode> getInRange(Rectangle2D rect){
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
        private long graphNodeId;

        private RoadTreeNode(float x, float y, RoadNode node,Long graphNodeId) {
            this.X = x;
            this.Y =y;
            this.graphNodeId = graphNodeId;
            this.roadNode = node;
        }

        protected boolean sortVertically() {
            return isVertical;
        }

        protected boolean isInside(Rectangle2D rect) {
            return rect.contains(X, Y);
        }

        public Long getGraphNode(){return graphNodeId;}

        public RoadNode getRoadNode() {
            return roadNode;
        }

        public PolygonApprox getShape() {
            return getRoadNode().getShape();
        }

        public String getRoadName() {
            return getRoadNode().getRoadName();
        }
    }
}