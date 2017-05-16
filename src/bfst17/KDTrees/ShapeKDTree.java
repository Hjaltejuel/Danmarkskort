package bfst17.KDTrees;

import bfst17.Enums.WayType;
import bfst17.ShapeStructure.PolygonApprox;

import java.awt.*;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

public class ShapeKDTree extends KDTree {
    HashSet<Shape> nodes = new HashSet<>();
    WayType type;

    public ShapeKDTree(WayType type) {
        this.type = type;
    }

    public WayType getType() {
        return type;
    }

    /**
     * Fylder træet med en type <E> som bliver castet til en E
     * @param shapes    Listen af shapes
     * @param <E>       Generisk type der castes til E
     */
    public <E> void fillTree(List<E> shapes) {
        if (shapes.size() == 0) {
            return;
        }
        ArrayList<ShapeTreeNode> allShapesList = new ArrayList<>();
        for(E _shape : shapes) {
            int Counter=0;

            PolygonApprox shape = (PolygonApprox)_shape;
            allShapesList.add(new ShapeTreeNode(shape, shape.getCenterX(), shape.getCenterY()));
            float[] coordinates = new float[2];
            PathIterator PI = shape.getPathIterator(null);
            while(!PI.isDone()) {
                Counter++;
                if(Counter%60==0) {
                    PI.currentSegment(coordinates);
                    allShapesList.add(new ShapeTreeNode(shape, coordinates[0], coordinates[1]));
                }
                PI.next();
            }
        }
        ShapeTreeNode[] allShapes = allShapesList.toArray(new ShapeTreeNode[allShapesList.size()]);
        insertArray(allShapes, 0, allShapes.length - 1, true);
    }

    /**
     * Indsætter en treeNode
     * @param insertNode    Noden der skal indsættes
     * @return              Noden der blev indsat
     */
    public ShapeTreeNode insert(TreeNode insertNode) {
        if(root==null) {
            root=insertNode;
            root.vertical=true;
            Size++;
        } else {
            tmpDepth=1;
            ShapeTreeNode newNode = (ShapeTreeNode) insertNode(insertNode, root);
        }
        ShapeTreeNode _insertNode = (ShapeTreeNode) insertNode;
        Rectangle2D bounds = _insertNode.shape.getBounds2D();

        //_insertNode.highSplit = !vertical ? bounds.getMaxX() : bounds.getMaxY();
        //_insertNode.lowSplit = !vertical ? bounds.getMinX() : bounds.getMinY();
        //System.out.println(root.highSplit + " " + root.lowSplit);
        return _insertNode;
    }

    /**
     * Fylder et hashSet med Shapes indenfor den givne range
     * @param rect  Den givne range (Skærmbilledet)
     * @return      HashSettet med shapes
     */
    public HashSet<Shape> getInRange(Rectangle2D rect){
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
            nodes.add(startNode.getShape());
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

    public class ShapeTreeNode extends TreeNode {
        boolean sortVertically() {
            return isVertical;
        }

        protected boolean isInside(Rectangle2D rect) {
            return rect.intersects(shape.getBounds2D());
        }
        public ShapeTreeNode(PolygonApprox s, double x , double y) {
            this.X = x;
            this.Y = y;
            this.shape = s;
        }
    }
}