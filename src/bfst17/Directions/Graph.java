package bfst17.Directions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Jakob Roos on 21/04/2017.
 */
public class Graph {

    private GraphNode source;
    private GraphNode target;

    private GraphNode sourceTest;
    private GraphNode targetTest;

    private ShortestPath sp;

    ArrayList<GraphNode> graphNodeList;


    public Graph() {
        graphNodeList = new ArrayList<>();
    }

    public void addEdge(GraphNode node, GraphNode neighbour)
    {
        Edge e = new Edge(node, neighbour);
    }

    public void addGraphNode(GraphNode node) {
        graphNodeList.add(node);
    }

    public ArrayList<GraphNode> getRandomPath() {
        Random r = new Random();
        int i = r.nextInt(graphNodeList.size());
        int i2 = r.nextInt(graphNodeList.size());
        source = graphNodeList.get(i);
        target = graphNodeList.get(i2);
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

    public ShortestPath getSP(){
        return sp;
    }

    public GraphNode getSourceTest() {
        return sourceTest;
    }

    public GraphNode getTargetTest() {
        return targetTest;
    }
}

