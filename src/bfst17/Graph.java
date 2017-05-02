package bfst17;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jakob Roos on 21/04/2017.
 */
public class Graph {

    private GraphNode source;
    private GraphNode target;

    private GraphNode sourceTest;
    private GraphNode targetTest;

    private ArrayList<Edge> edges;
    private ShortestPath sp;


    public Graph(ArrayList<GraphNode> graphNodeList) {
        
        int j = 0;

        for (int i = 0; i < graphNodeList.size(); i++) {


            //DET HER ER BLOT TESTING;
            //Fjern de to nedenstående if-statements,
            //integrér shortestpath klassen med ekstern "node-finding".
            if (i == 1804) {
                source = graphNodeList.get(i);
                sourceTest = source;
            }
            if (i == 12) {
                target = graphNodeList.get(i);
                targetTest = target;
            }
            if (graphNodeList.get(i).isStart()) j = 0;

            if (j - 1 != -1) {
                addEdge(graphNodeList.get(i),
                        graphNodeList.get(i - 1));
            }
            if (!graphNodeList.get(i).isEnd()) {
                if (i != graphNodeList.size() - 1) {
                    addEdge(graphNodeList.get(i),
                            graphNodeList.get(i + 1));
                }
            }

            j++;
        }


        sp = new ShortestPath(this);
        sp.execute(source, target);

    }


    public void addEdge(GraphNode node, GraphNode neighbour)
    {
        Edge e = new Edge(node, neighbour);

//        if(!edges.contains(e)){

//    }
    }



    public ArrayList<GraphNode> getPath(GraphNode source, GraphNode destination){
        ArrayList<GraphNode> pathList = new ArrayList<>();

        for(GraphNode n = destination; n.getNodeFrom() != null; n = n.getNodeFrom()) {
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

