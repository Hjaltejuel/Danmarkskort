package bfst17.Directions;

import bfst17.Model;
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

    private HashMap<Point2D, GraphNode> graphNodeBuilder;

    private ArrayList<GraphNode> pathList;
    private ArrayList<Point2D> pointList;


    private ShortestPath sp;



    public Graph(HashMap<Point2D, GraphNode> graphNodeBuilder, ArrayList<OSMWay> graphWays) {
        this.graphWays = graphWays;
        this.graphNodeBuilder = graphNodeBuilder;
        buildEdges();
    }

    public void addEdge(GraphNode node, GraphNode neighbour) {
        new Edge(node, neighbour);
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
                currentGraphNode = graphNodeBuilder.get(currentWay.get(i));
                //TEST
                if (j == 88) {
                   // source = currentGraphNode;
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

    public ArrayList<Point2D> getPointList() {
        return pointList;
    }

    public ArrayList<GraphNode> getPath(GraphNode source, GraphNode destination) {
        pathList = new ArrayList<>();
        pointList = new ArrayList<>();


        for (GraphNode n = destination; n.getNodeFrom() != null; n = n.getNodeFrom()) {
            pathList.add(n);
            pointList.add(n.getPoint2D());
        }
        Collections.reverse(pathList);
        source.setNodeFrom(null);
        return pathList;
    }

    public ArrayList<GraphNode> pathfinding(){
        return pathList;
    }

    public ShortestPath getSP() {
        return sp;
    }
    public HashMap<Point2D, GraphNode> getGraphFilteredMap(){
        return graphNodeBuilder;
    }
    public void setNodes(Point2D point2Source, Point2D point2Destination){
        this.source = graphNodeBuilder.get(point2Source);
        this.target = graphNodeBuilder.get(point2Destination);

        if(source != null || target != null) {
            sp = new ShortestPath(this);
            sp.execute(source, target);
        }
    }

    public void cleanUpGraph() {
        Iterator it = graphNodeBuilder.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            GraphNode g = (GraphNode)pair.getValue();
            g.setDistTo(Double.POSITIVE_INFINITY);
            g.setNodeFrom(null);
        }
    }

    public ArrayList<GraphNode> getPathList() {
        return pathList;
    }
}

