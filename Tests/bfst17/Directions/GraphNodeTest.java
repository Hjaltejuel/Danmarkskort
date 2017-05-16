package bfst17.Directions;

import org.junit.Before;
import org.junit.Test;

import java.awt.geom.Point2D;

import static org.junit.Assert.*;

/**
 * Created by Mads on 16/05/2017.
 */
public class GraphNodeTest {
    GraphNode testNodeOne;
    GraphNode testNodeTwo;
    GraphNode testNodeThree;
    @Before
    public void setUp() throws Exception {
        testNodeOne = new GraphNode(new Point2D.Double(1,1), 1l);
        testNodeTwo = new GraphNode(new Point2D.Double(2,5), 3l);
        testNodeThree = new GraphNode(new Point2D.Double(1,1), 3l);

    }

    @Test
    public void getPoint2D() throws Exception {
        assertEquals(true, (testNodeOne.getPoint2D().equals(testNodeThree.getPoint2D())));
        assertEquals(false, (testNodeOne.getPoint2D() == testNodeThree.getPoint2D()));

        assertEquals(true, (testNodeOne.getPoint2D().equals(testNodeOne.getPoint2D())));

    }

    @Test
    public void getEdgeList() throws Exception {

    }


    @Test
    public void addEdge() throws Exception {

    }



}