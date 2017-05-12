package bfst17.Directions;

import bfst17.Enums.WeighType;
import bfst17.OSMData.OSMNode;
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

    public Graph(HashMap<Point2D, GraphNode> graphNodeBuilder, ArrayList<OSMWay> graphWays) {
        this.graphNodeBuilder = graphNodeBuilder;
        buildEdges(graphWays);
        buildGraphNodes();
    }

    public void addEdge(GraphNode node, GraphNode neighbour) {
        node.addEdge(neighbour);
    }

    public void buildEdges(ArrayList<OSMWay> graphWays) {
        System.out.println("Building Edges!");
        for (OSMWay currentWay : graphWays) {
            for (int i = 1; i < currentWay.size(); i++) {
                GraphNode previousGraphNode = graphNodeBuilder.get(currentWay.get(i - 1));
                GraphNode currentGraphNode = graphNodeBuilder.get(currentWay.get(i));
                addEdge(previousGraphNode, currentGraphNode);
                addEdge(currentGraphNode, previousGraphNode);
            }
        }
        System.out.println("Graph complete!");
    }


    public void buildGraphNodes() {
        System.out.println("Building GraphNodes!");
        GraphNode currentGraphNode;
        for (GraphNode graphNode : graphNodeBuilder.values()) {
            /*
            OSMNode currentOSMNode = (OSMNode)pair.getKey();
            NodeTags currentTags =(NodeTags)pair.getValue();
            currentGraphNode = new GraphNode(currentOSMNode);
            currentGraphNode.setNodeTags(currentTags.bicycle,currentTags.foot,currentTags.maxspeed,currentTags.oneway);
            graphFilteredMap.put(currentOSMNode, currentGraphNode);
            */
        }
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

    public void findShortestPath(Point2D point2Source, Point2D point2Destination, WeighType weighType) {
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
            GraphNode node = unRelaxedNodes.poll();
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

    private void relaxEdges(GraphNode node, WeighType weighType) {
        ArrayList<Edge> edgelist = node.getEdgeList();
        for (Edge edge : edgelist) {
            GraphNode destinationNode = edge.getDestination();
            if (!relaxedNodes.contains(destinationNode)) {
                double tempDistTo = node.getDistTo() + edge.getWeight(weighType);
                if (tempDistTo < destinationNode.getDistTo()) {
                    destinationNode.setDistTo(tempDistTo);
                    destinationNode.setNodeFrom(node);
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
}