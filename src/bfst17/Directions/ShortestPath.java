package bfst17.Directions;

import java.util.*;

/**
 * Created by Jakob Roos on 22/04/2017.
 */
public class ShortestPath {
    //FIXME lav enum til vægte.

    private Set<GraphNode> settledNodes;
    private PriorityQueue<GraphNode> unSettledNodes;
    private Map<GraphNode, GraphNode> predecessors;
    private Graph graph;

    public ShortestPath(Graph graph) {

        this.graph = graph;
    }

    public void execute(GraphNode source, GraphNode target) {
        settledNodes = new HashSet<>();
        unSettledNodes = new PriorityQueue<>();
        predecessors = new HashMap<>();
        source.setDistTo(0.0);
        unSettledNodes.add(source);
        while (unSettledNodes.size() > 0) {
            GraphNode node = unSettledNodes.poll();
            node.setSettled(true);
            unSettledNodes.remove(node);
            //System.out.println(unSettledNodes.size());
            relaxEdges(node);
        }
        if(unSettledNodes.size() == 0){
            source.setNodeFrom(null);
            graph.getPath(source, target);
        }
    }

    private void relaxEdges(GraphNode node) {
        ArrayList<Edge> edgelist = node.getEdgeList();
        for (Edge e: edgelist) {
            if(!e.getDestination().isSettled()) {
                double tempDistTo = node.getDistTo() + e.getWeight();
                if (tempDistTo < e.getDestination().getDistTo()) {
                    e.getDestination().setDistTo(tempDistTo);
                    e.getDestination().setNodeFrom(node);
                }
                unSettledNodes.add(e.getDestination());
            }
        }
    }
}


