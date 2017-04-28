package bfst17;

import java.util.*;

/**
 * Created by Jakob Roos on 21/04/2017.
 */
public class Graph {

    private OSMNode source;
    private OSMNode target;

    private OSMNode sourceTest;
    private OSMNode targetTest;

    private ArrayList<OSMNode> nodes;
    private ArrayList<Edge> edges;
    private ShortestPath sp;


    public Graph(HashMap<Long, OSMWay> idToWay) {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        Iterator it = idToWay.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            OSMWay way = (OSMWay) pair.getValue();
            for (int i = 0; i < way.size(); i++) {
                    if (way.get(i).isRelevantForRouting() == true) {
                        //DET HER ER BLOT TESTING;
                        //Fjern de to nedenstående if-statements,
                        //integrér shortestpath klassen med ekstern "node-finding".
                        if (i == 1){
                            source = way.get(i);
                            sourceTest = source;
                        }
                        if (i == 25){
                            target = way.get(i);
                            targetTest = target;
                        }
                        if (nodes.contains(way.get(i))) {
                            if (i - 1 != -1) {
                                addEdge(way.get(i), way.get(i - 1));
                            }
                            if (i < way.size() - 1) {
                                addEdge(way.get(i), way.get(i + 1));
                            }
                        } else {
                            addNode(way.get(i));
                            if (i - 1 != -1) {
                                addEdge(way.get(i), way.get(i - 1));
                            }
                            if (i < way.size() - 1) {
                                addEdge(way.get(i), way.get(i + 1));
                            }
                            if (i == way.size() - 1) {

                            }
                        }
                    }
                }

                it.remove();
            }
            sp = new ShortestPath(this);
            sp.execute(source, target);

        }

    public void addEdge(OSMNode node, OSMNode neighbour)
    {
        Edge e = new Edge(node, neighbour);

        if(!edges.contains(e)){
        edges.add(e);
    }
    }
    public void addNode(OSMNode node){
        nodes.add(node);
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }
    public ArrayList<OSMNode> getNodes() {
        return nodes;
    }

    public ArrayList<OSMNode> getPath(OSMNode source,OSMNode destination){
        ArrayList<OSMNode> pathList = new ArrayList<>();

        for(OSMNode n = destination; n.getNodeFrom() != null; n = n.getNodeFrom()) {
                pathList.add(n);
        }
        Collections.reverse(pathList);
       return pathList;
    }

    public ShortestPath getSP(){
        return sp;
    }

    public OSMNode getSourceTest() {
        return sourceTest;
    }

    public OSMNode getTargetTest() {
        return targetTest;
    }
}

