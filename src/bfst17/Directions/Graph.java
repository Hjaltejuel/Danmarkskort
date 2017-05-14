package bfst17.Directions;

import bfst17.Enums.VehicleType;
import bfst17.Enums.WeighType;
import bfst17.OSMData.OSMWay;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Created by Jakob Roos on 21/04/2017.
 */
public class Graph {
    private HashMap<Point2D, GraphNode> graphNodeBuilder;
    private ArrayList<GraphNode> pathList;
    private ArrayList<Point2D> pointList;
    private HashSet<GraphNode> relaxedNodes;
    private PriorityQueue<GraphNode> unRelaxedNodes;
    private double finalDistance;

    /**
     * Opretter en Graf
     * @param graphNodeBuilder  Et HashMap der kan finde GrafNoder udfra Point2D
     * @param graphWays         En ArrayListe af OSMWays, der kan bruges til at trække grafnoder ud af graphNodeBuilder
     */
    public Graph(HashMap<Point2D, GraphNode> graphNodeBuilder, ArrayList<OSMWay> graphWays) {
        this.graphNodeBuilder = graphNodeBuilder;
        buildEdges(graphWays);
    }

    /**
     * Opretter en edge mellem to GraphNodes
     * @param node          noden der skal oprettes edge fra
     * @param neighbour     noden der skal oprettes edge til
     */
    public void addEdge(GraphNode node, GraphNode neighbour) {
        node.addEdge(neighbour);
    }

    /**
     * Kører alle OSMWays igennem og finder de graphNodes der skal lave edges mellem hinanden
     * Vi finder graphNodesne vha. de punkter der er i OSMWays'ne
     * @param graphWays     OSMWays
     */
    public void buildEdges(ArrayList<OSMWay> graphWays) {
        System.out.println("Building Edges!");
        for (OSMWay currentWay : graphWays) {
            for (int i = 1; i < currentWay.size(); i++) {
                GraphNode previousGraphNode = graphNodeBuilder.get(currentWay.get(i - 1));
                GraphNode currentGraphNode = graphNodeBuilder.get(currentWay.get(i));
                addEdge(previousGraphNode, currentGraphNode);
                if(!currentGraphNode.isOneway()) {
                    addEdge(currentGraphNode, previousGraphNode);
                }
            }
        }
        System.out.println("Graph complete!");
    }

    /**
     * Resetter graphen så den er klar til en ny Shortest Path
     */
    public void cleanUpGraph() {
        int j=0;
        for (GraphNode graphNode : graphNodeBuilder.values()) {
            graphNode.setDistTo(Double.POSITIVE_INFINITY);
            graphNode.setNodeFrom(null);
        }
        System.out.println(j);
    }

    /**
     * Finder den korteste vej mellem to punkter
     * @param point2Source          Start punkt
     * @param point2Destination     Slut punkt
     * @param weighType             Vægttype ( FASTEST | SHORTEST )
     */
    public void findShortestPath(Point2D point2Source, Point2D point2Destination, VehicleType weighType) {
        GraphNode source = graphNodeBuilder.get(point2Source);
        GraphNode target = graphNodeBuilder.get(point2Destination);
        if (source == null || target == null) {
            return; //Mangler source eller target
        }

        cleanUpGraph(); //Clean graph før vi går i gang

        relaxedNodes = new HashSet<>();
        unRelaxedNodes = new PriorityQueue<>();

        source.setDistTo(0.0);

        unRelaxedNodes.add(source);

        while (!unRelaxedNodes.isEmpty()) {
            GraphNode node = unRelaxedNodes.peek();
            //node.setSettled(true);
            relaxedNodes.add(node);
            unRelaxedNodes.remove(node);
            relaxEdges(node, weighType);
        }
        source.setNodeFrom(null);

        pathList = new ArrayList<>();
        pointList = new ArrayList<>();

        for (GraphNode n = target; n.getNodeFrom() != null; n = n.getNodeFrom()) {
            pathList.add(n);
            pointList.add(n.getPoint2D());
        }
        Collections.reverse(pathList);
    }

    /**
     * Tjekker 'node's edges (Naboer) og ser om de er relaxed (Markeret), hvis ikke
     * bliver de lagt i Queuen (unRelaxedNodes) og så bliver deres naboer undersøgt.
     * ved samtidig at lægge hver edges weight til nodernes distance, finder vi den korteste path
     * @param node          Den node, hvis naboer skal undersøges
     * @param vehicleType1     vægtTypen ( CAR | BICYCLE | FOOT )
     */
    private void relaxEdges(GraphNode node, VehicleType vehicleType1) {
        ArrayList<Edge> edgelist = node.getEdgeList();
        for (Edge edge : edgelist) {
            GraphNode destinationNode = edge.getDestination();
            if (!relaxedNodes.contains(destinationNode)) {
                double tempDistTo;
                if(destinationNode.supportsVehicle(vehicleType1)){
                    tempDistTo = node.getDistTo() + edge.getWeight(vehicleType1);
                    if (tempDistTo < destinationNode.getDistTo()) {
                        destinationNode.setDistTo(tempDistTo);
                        destinationNode.setNodeFrom(node);
                    }
                } else {
                    tempDistTo = Double.POSITIVE_INFINITY;
                }
                unRelaxedNodes.add(destinationNode);
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

    public HashMap<Point2D, GraphNode> getGraphFilteredMap() {
        return graphNodeBuilder;
    }
    public double getFinalDistance(){
        return finalDistance;
    }
}