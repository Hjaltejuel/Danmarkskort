package bfst17.Directions;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Jakob Roos on 24/04/2017.
 */
public class EdgeList<GraphNode> extends LinkedList<GraphNode>{
    ArrayList<Edge> adjacentEdges;
    public EdgeList(){
        adjacentEdges = new ArrayList<>();
    }
    public ArrayList getadjacentEdges(){
        return adjacentEdges;
    }
    public void insertEdge(Edge e){
        adjacentEdges.add(e);

    }
}
