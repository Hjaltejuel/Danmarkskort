package bfst17.Directions;

import bfst17.Enums.VehicleType;
import bfst17.Enums.WeighType;
import bfst17.OSMData.OSMWay;

import java.awt.geom.Point2D;
import java.util.*;


public class Graph {
    HashMap<Long,GraphNode> idToGraphNode;
    private PriorityQueue<GraphNode> unRelaxedNodes;
    private ArrayList<Point2D> pointList;
    private ArrayList<GraphNode> pathList;
    int k = 0;


    /**
     * Opretter en Graf
     */
    public Graph(HashMap<Long,GraphNode> idToGraphNode) {
        this.idToGraphNode = idToGraphNode;
    }

    /**
     * Resetter graphen så den er klar til en ny Shortest Path
     */
    public void cleanUpGraph() {
        for (GraphNode graphNode : idToGraphNode.values()) {
            graphNode.setMarked(false);
            graphNode.setDistTo(Double.POSITIVE_INFINITY);
            graphNode.setNodeFrom(null);
        }
    }

    HashSet<GraphNode> unrelaxed;
    GraphNode target;
    /**
     * Finder den korteste vej mellem to punkter
     * @param point2Source          Start punkt
     * @param point2Destination     Slut punkt
     * @param weighType             Vægttype ( FASTEST | SHORTEST )
     */
    public void findShortestPath(GraphNode point2Source, GraphNode point2Destination, VehicleType weighType) {
        GraphNode source = point2Source;
        target = point2Destination;
        if (source == null || target == null) {
            return; //Mangler source eller target
        }
        cleanUpGraph(); //Clean graph før vi går i gang

        unRelaxedNodes = new PriorityQueue<>();

        unrelaxed = new HashSet<>();

        source.setDistTo(0.0);

        unRelaxedNodes.add(source);
    int i = 0;
        while (!unRelaxedNodes.isEmpty()) {
            i++;
            GraphNode node = unRelaxedNodes.peek();
            //node.setSettled(true);
            node.setMarked(true);
            unRelaxedNodes.remove(node);
            relaxEdges(node, weighType);
        }
        System.out.println(i);
        source.setNodeFrom(null);

        pathList = new ArrayList();
        pointList = new ArrayList<>();
        for (GraphNode n = target; n.getNodeFrom() != null; n = n.getNodeFrom()) {
            pathList.add(n);
            pointList.add(n.getPoint2D());
        }
        pointList.add(source.getPoint2D());
        Collections.reverse(pathList);
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
            GraphNode destinationNode = edge.getDestination();
            if (!destinationNode.isMarked()) {
                if (destinationNode.supportsVehicle(vehicleType)) {
                    double tempDistTo = node.getDistTo() + edge.getWeight(vehicleType);
                    if (tempDistTo < destinationNode.getDistTo()) {
                        if(destinationNode ==target){
                            System.out.println("we made it");
                        }
                        destinationNode.setDistTo(tempDistTo);
                        destinationNode.setNodeFrom(node);
                    }
                    if(unrelaxed.contains(destinationNode)){} else {
                        unRelaxedNodes.add(destinationNode);
                        unrelaxed.add(destinationNode);
                    }

                } else {
                }
            }
        }
    }

    public ArrayList<GraphNode> getPathList() {
        return pathList;
    }

    public ArrayList<GraphNode> pathfinding() {
        return pathList;
    }

    public ArrayList<Point2D> getPointList() {
        return pointList;
    }

}