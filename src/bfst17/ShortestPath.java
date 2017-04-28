package bfst17;

import java.util.*;

/**
 * Created by Jakob Roos on 22/04/2017.
 */
public class ShortestPath {
    //FIXME lav enum til vægte.
    private final List<OSMNode> nodes;
    private final List<Edge> edges;
    private Set<OSMNode> settledNodes;
    private PriorityQueue<OSMNode> unSettledNodes;
    private Map<OSMNode, OSMNode> predecessors;
    private Graph graph;

    public ShortestPath(Graph graph) {
        this.nodes = new ArrayList<>(graph.getNodes());
        this.edges = new ArrayList<>(graph.getEdges());
        this.graph = graph;
    }

    public void execute(OSMNode source, OSMNode target) {
        settledNodes = new HashSet<>();
        unSettledNodes = new PriorityQueue<>();
        predecessors = new HashMap<>();
        source.setDistTo(0.0);
        unSettledNodes.add(source);
        while (unSettledNodes.size() > 0) {
            OSMNode node = unSettledNodes.poll();
            node.setSettled(true);
            unSettledNodes.remove(node);
            System.out.println(unSettledNodes.size());
            relaxEdges(node);
        }
        if(unSettledNodes.size() == 0){
            source.setNodeFrom(null);
            graph.getPath(source, target);
        }
    }

    private void relaxEdges(OSMNode node) {
        ArrayList<Edge> edgelist = node.getEdgeList();
        for (Edge e: edgelist) {
            if(!e.getDestination().isSettled()) {
                double tempDistTo = node.getDistTo() + e.getWeightCar();
                if (tempDistTo < e.getDestination().getDistTo()) {
                    e.getDestination().setDistTo(tempDistTo);
                    e.getDestination().setNodeFrom(node);
                }
                unSettledNodes.add(e.getDestination());
            }
        }
    }
}


