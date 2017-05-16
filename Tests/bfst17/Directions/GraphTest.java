package bfst17.Directions;

import org.junit.Before;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by Mads on 16/05/2017.
 */
public class GraphTest {
    Graph graph;
    @Before
    public void setUp() throws Exception {
        HashMap<Long, GraphNode> idToGraphNode = new HashMap<>();
        idToGraphNode.put(1l, new GraphNode(new Point2D.Double(1,1), 1l));
        idToGraphNode.put(2l, new GraphNode(new Point2D.Double(2,5), 2l));
        idToGraphNode.put(3l, new GraphNode(new Point2D.Double(4,8), 3l));
        graph = new Graph(idToGraphNode);

        graph.idToGraphNode.get(1l).setDistTo(4.0);
        graph.idToGraphNode.get(2l).setDistTo(20.0);
        graph.idToGraphNode.get(3l).setDistTo(1.0);
    }

    @Test
    public void cleanUpGraph() throws Exception {
        assertEquals(4.0, graph.idToGraphNode.get(1l).getDistTo(),0.5);
        assertEquals(20.0, graph.idToGraphNode.get(2l).getDistTo(),0.5);
        assertEquals(1.0, graph.idToGraphNode.get(3l).getDistTo(),0.5);

        graph.cleanUpGraph();

        assertEquals(Double.POSITIVE_INFINITY, graph.idToGraphNode.get(1l).getDistTo(), 0.5);
        assertEquals(Double.POSITIVE_INFINITY, graph.idToGraphNode.get(2l).getDistTo(), 0.5);
        assertEquals(Double.POSITIVE_INFINITY, graph.idToGraphNode.get(3l).getDistTo(), 0.5);



    }

}