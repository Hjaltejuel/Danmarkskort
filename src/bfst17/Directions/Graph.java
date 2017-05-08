package bfst17.Directions;

import bfst17.OSMData.OSMNode;
import bfst17.OSMData.OSMWay;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Created by Jakob Roos on 21/04/2017.
 */
public class Graph {

    private final ArrayList<OSMWay> graphWays;
    private GraphNode source;
    private GraphNode target;

    private HashMap<Point2D, GraphNode> graphFilteredMap;
    private HashMap<Point2D, NodeTags> graphNodeBuilder;
    //private Map<Long, OSMWay> idToWay;

    private ShortestPath sp;

    ArrayList<GraphNode> graphNodeList;


    public Graph(Map<Long, OSMWay> idToWay, HashMap<Point2D, NodeTags> graphNodeBuilder, ArrayList<OSMWay> graphWays) {

        this.graphWays = graphWays;
     //   this.idToWay = idToWay;
        this.graphNodeBuilder = graphNodeBuilder;
        this.graphFilteredMap = new HashMap<>();

    //    graphNodeList = new ArrayList<>();
    }

    public void addEdge(GraphNode node, GraphNode neighbour) {
        Edge e = new Edge(node, neighbour);
    }

    public void buildEdges() {
        System.out.println("Building Edges!");
        GraphNode previousGraphNode = null;
        GraphNode currentGraphNode = null;
        int j = 0;

        for (OSMWay currentWay : graphWays) {
            j++;
            for (int i = 0; i < currentWay.size(); i++) {
                if (i > 0) {
                    previousGraphNode = currentGraphNode;
                }
                currentGraphNode = graphFilteredMap.get(currentWay.get(i));
                //TEST
                if (j == 88) {
                    source = currentGraphNode;
                    j++;
                }
                //TEST
                if (j == 400) {
                    target = currentGraphNode;
                    j++;
                }
                if (i > 0) {
                    addEdge(previousGraphNode, currentGraphNode);
                    addEdge(currentGraphNode, previousGraphNode);
                }
            }
        }


        System.out.println("Graph complete!");
    }

    public void buildGraphNodes(){
        System.out.println("Building GraphNodes!");
        Iterator it = graphNodeBuilder.entrySet().iterator();
        int j = 0;
        GraphNode currentGraphNode;
        while(it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            OSMNode currentOSMNode = (OSMNode)pair.getKey();
            NodeTags currentTags =(NodeTags)pair.getValue();
            currentGraphNode = new GraphNode(currentOSMNode);
            currentGraphNode.setNodeTags(currentTags.bicycle,currentTags.foot,currentTags.maxspeed,currentTags.oneway);
            graphFilteredMap.put(currentOSMNode, currentGraphNode);
            it.remove();
        }
        graphNodeBuilder.clear();
    }

    public void addGraphNode(GraphNode node) {
        graphNodeList.add(node);
    }

    public ArrayList<GraphNode> getRandomPath() {
        Random r = new Random();
//        int i = r.nextInt(graphNodeList.size());
//        int i2 = r.nextInt(graphNodeList.size());
//        source = graphNodeList.get(297);
//        target = graphNodeList.get(7148);
        sp = new ShortestPath(this);
        sp.execute(source, target);
        return getPath(source, target);
    }

    public ArrayList<GraphNode> getPath(GraphNode source, GraphNode destination) {
        ArrayList<GraphNode> pathList = new ArrayList<>();

        for (GraphNode n = destination; n.getNodeFrom() != null; n = n.getNodeFrom()) {
            pathList.add(n);
        }
        Collections.reverse(pathList);
        return pathList;
    }
    public void reweighEdges(String weightType){
                Iterator it = graphFilteredMap.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry pair = (Map.Entry)it.next();
                    GraphNode graphNode = (GraphNode)pair.getValue();
                    graphNode.reweighEdges(weightType);
                }
    }

    public ShortestPath getSP() {
        return sp;
    }
}

