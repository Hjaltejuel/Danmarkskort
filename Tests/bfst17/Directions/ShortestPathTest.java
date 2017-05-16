package bfst17.Directions;

import bfst17.Enums.RoadTypes;
import bfst17.Enums.VehicleType;
import bfst17.Enums.WeighType;
import org.junit.Before;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
    Hvis der findes en vej mellem to punkter er start, slut og punkerne på vejen med.
    Hvis der ikke findes en vej er startpunktet kun med.
 */
public class ShortestPathTest {
    Graph shortestPathGraph;
    HashMap<Long, GraphNode> idToGraphNode;
    GraphNode testNodeOne;
    GraphNode testNodeTwo;
    GraphNode testNodeThree;
    GraphNode testNodeFour;
    GraphNode testNodeFive;
    GraphNode testNodeSix;
    GraphNode testNodeSeven;
    ArrayList<GraphNode> pathList;

    @Before
    public void setUp() throws Exception {
        idToGraphNode = new HashMap<>();

        testNodeOne = new GraphNode(new Point2D.Double(1,1), 1l);
        testNodeTwo = new GraphNode(new Point2D.Double(2,5), 2l);
        testNodeThree = new GraphNode(new Point2D.Double(1,4), 3l);
        testNodeFour = new GraphNode(new Point2D.Double(6,8), 4l);
        testNodeFive = new GraphNode(new Point2D.Double(9,2), 5l);
        testNodeSix = new GraphNode(new Point2D.Double(4,3), 6l);
        testNodeSeven = new GraphNode(new Point2D.Double(6, 4), 7l);

        idToGraphNode.put(1l, testNodeOne);
        idToGraphNode.put(2l, testNodeTwo);
        idToGraphNode.put(3l, testNodeThree);
        idToGraphNode.put(4l, testNodeFour);
        idToGraphNode.put(5l, testNodeFive);
        idToGraphNode.put(6l, testNodeSix);
        idToGraphNode.put(7l, testNodeSeven);

        shortestPathGraph = new Graph(idToGraphNode);

        testNodeOne.addEdge(testNodeThree, "1->3", 20, RoadTypes.HIGHWAY_ROAD);
        testNodeOne.addEdge(testNodeSix, "1->6", 20, RoadTypes.HIGHWAY_ROAD);
        testNodeTwo.addEdge(testNodeFour, "2->4", 5, RoadTypes.HIGHWAY_STEPS);
        testNodeThree.addEdge(testNodeOne, "3->1", 20, RoadTypes.HIGHWAY_ROAD);
        testNodeThree.addEdge(testNodeTwo, "3->2", 20, RoadTypes.HIGHWAY_ROAD);
        testNodeFour.addEdge(testNodeFive, "4->5", 20, RoadTypes.HIGHWAY_ROAD);
        testNodeSix.addEdge(testNodeOne, "6->1", 20, RoadTypes.HIGHWAY_ROAD);
        testNodeSix.addEdge(testNodeTwo, "6->2", 20, RoadTypes.HIGHWAY_ROAD);

        shortestPathGraph.cleanUpGraph();

    }

    @Test
    public void findShortestPathFromOneToTwoInCar() throws Exception {
        //vej 1 til 2
        shortestPathGraph.findShortestPath(testNodeOne, testNodeTwo, VehicleType.CAR);
        pathList = shortestPathGraph.getPathList();
        //Source skal med i listen for testingens skyld
        pathList.add(0, testNodeOne);

        assertTrue(pathList.size() == 3);
        assertEquals(pathList.get(0), testNodeOne);
        assertEquals(pathList.get(1), testNodeThree);
        assertEquals(pathList.get(2), testNodeTwo);
    }

    @Test
    public void findShortestPathFromTwoToSixInCar() throws Exception {
        //vej 2 til 6
        shortestPathGraph.findShortestPath(testNodeTwo, testNodeSix, VehicleType.CAR);
        pathList = shortestPathGraph.getPathList();
        pathList.add(0, testNodeTwo);

        assertTrue(pathList.size() == 1);
        assertEquals(pathList.get(0), testNodeTwo);
    }

    @Test
    public void findShortestPathFromThreeToFourInCar() throws Exception {
        //vej 3 til 4
        shortestPathGraph.findShortestPath(testNodeThree, testNodeFour, VehicleType.CAR);
        pathList = shortestPathGraph.getPathList();
        pathList.add(0, testNodeThree);

        assertTrue(pathList.size() == 1);
        assertEquals(pathList.get(0), testNodeThree);
    }

    @Test
    public void findShortestPathFromOneToFourOnFoot() throws Exception {
        //vej 3 til 4
        shortestPathGraph.findShortestPath(testNodeOne, testNodeFour, VehicleType.FOOT);
        pathList = shortestPathGraph.getPathList();
        pathList.add(0, testNodeOne);

        assertTrue(pathList.size() == 4);
        assertEquals(pathList.get(0), testNodeOne);
        assertEquals(pathList.get(1), testNodeThree);
        assertEquals(pathList.get(2), testNodeTwo);
        assertEquals(pathList.get(3), testNodeFour);
    }

    @Test
    public void findShortestPathFromOneToFourOnBicyle() throws Exception {
        //vej 3 til 4
        shortestPathGraph.findShortestPath(testNodeOne, testNodeFour, VehicleType.BICYCLE);
        pathList = shortestPathGraph.getPathList();
        pathList.add(0, testNodeOne);

        assertTrue(pathList.size() == 1);
        assertEquals(pathList.get(0), testNodeOne);
    }

    @Test
    public void findShortestPathFromSevenToThreeInCar() throws Exception {
        //vej 7til 3
        shortestPathGraph.findShortestPath(testNodeSeven, testNodeThree, VehicleType.CAR);
        pathList = shortestPathGraph.getPathList();
        pathList.add(0, testNodeSeven);

        assertTrue(pathList.size() == 1);
        assertEquals(pathList.get(0), testNodeSeven);
    }
}