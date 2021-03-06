package bfst17.Directions;

import bfst17.Enums.VehicleType;
import bfst17.Enums.WeighType;
import bfst17.OSMData.OSMWay;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;


public class Graph implements Serializable {
    HashMap<Long,GraphNode> idToGraphNode;
    private PriorityQueue<GraphNode> unRelaxedNodes;
    private ArrayList<Point2D> pointList;
    private ArrayList<GraphNode> pathList;
    private Directions directions;

    /**
     * Opretter en Graf
     */
    public Graph(HashMap<Long,GraphNode> idToGraphNode) {
        this.idToGraphNode = idToGraphNode;
    }

    /**
     * Resetter graphen så den er klar til en ny Shortest Path
     * Sætter alle distTo til +uendlig.
     */
    public void cleanUpGraph() {
        for (GraphNode graphNode : idToGraphNode.values()) {
            graphNode.setMarked(false);
            graphNode.setDistTo(Double.POSITIVE_INFINITY);
            graphNode.setNodeFrom(null);
        }
    }

    /**
     * Finder den korteste vej mellem to punkter
     * @param point2Source          Start punkt
     * @param point2Destination     Slut punkt
     * @param vehicleType             Vægttype ( FASTEST | SHORTEST )
     */
    public void findShortestPath(GraphNode point2Source, GraphNode point2Destination, VehicleType vehicleType) {
        GraphNode source = point2Source;
        GraphNode target = point2Destination;
        target = point2Destination;
        if (source == null || target == null) {
            return; //Mangler source eller target
        }
        cleanUpGraph(); //Clean graph før vi går i gang

        unRelaxedNodes = new PriorityQueue<>();

        source.setDistTo(0.0);

        unRelaxedNodes.add(source);
        while (!unRelaxedNodes.isEmpty()) {
            GraphNode node = unRelaxedNodes.peek();
            //node.setSettled(true);
            node.setMarked(true);
            unRelaxedNodes.remove(node);
            relaxEdges(node, vehicleType);
        }
        source.setNodeFrom(null);

        pathList = new ArrayList();
        pointList = new ArrayList<>();
        for (GraphNode n = target; n.getNodeFrom() != null; n = n.getNodeFrom()) {
            pathList.add(n);
            pointList.add(n.getPoint2D());
        }
        pointList.add(source.getPoint2D());
        Collections.reverse(pathList);

        directions = new Directions(pathList, vehicleType);
    }

    /**
     * Tjekker 'node's edges (Naboer) og ser om de er relaxed (Markeret), hvis ikke
     * bliver de lagt i Queuen (unRelaxedNodes) og så bliver deres naboer undersøgt.
     * ved samtidig at lægge hver edges weight til nodernes distance, finder vi den korteste path
     * @param node          Den node, hvis naboer skal undersøges
     * @param vehicleType     vægtTypen ( CAR | BICYCLE | FOOT )
     */
    private void relaxEdges(GraphNode node, VehicleType vehicleType) {
        ArrayList<Edge> edgelist = node.getEdgeList();
        for (Edge edge : edgelist) {
            GraphNode destinationNode =idToGraphNode.get(edge.getDestinationId());
            if (!destinationNode.isMarked()) {
                if (edge.supportVehicle(vehicleType)) {
                    double tempDistTo = node.getDistTo() + edge.getWeight(vehicleType);
                    if (tempDistTo < destinationNode.getDistTo()) {
                        destinationNode.setDistTo(tempDistTo);
                        destinationNode.setNodeFrom(node);
                    }
                        destinationNode.setMarked(true);
                        unRelaxedNodes.add(destinationNode);

                } else {
                }
            }
        }
    }


    public Directions getDirections() {
        return directions;
    }

    public ArrayList<GraphNode> getPathList() {
        return pathList;
    }

    public ArrayList<Point2D> getPointList() {
        return pointList;
    }

}