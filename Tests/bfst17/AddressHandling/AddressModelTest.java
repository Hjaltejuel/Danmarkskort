package bfst17.AddressHandling;

import bfst17.OSMData.OSMNode;
import bfst17.OSMData.OSMWay;
import bfst17.ShapeStructure.PolygonApprox;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Michelle on 5/8/2017.
 */
public class AddressModelTest {
    AddressModel model;
    @Before
    public void setUp() throws Exception {
        model = new AddressModel();

    }

    @Test
    public void getTSTTree() throws Exception {
        assertNotNull(model.getTSTTree());
    }

    @Test
    public void putAddress() throws Exception {
        model.putAddress(Address.parse("Vester 23, 2300 København"), new OSMNode(2,2));
        assertNotNull(model.getAddress("Vester 23, 2300 København"));
    }

    @Test
    public void putRegion() throws Exception {
        ArrayList<Point2D> testList = new ArrayList<>();
        testList.add(new Point2D.Float(2,3));
        model.putRegion(("København"),new Region((Shape) new PolygonApprox(testList),new Point2D.Float(2,2)));
        assertNotNull(model.getAddress("København"));
    }

    @Test
    public void putCity() throws Exception {
        model.putCity("Holbæk", new OSMNode(2,2));
        assertNotNull(model.getAddress("Holbæk"));
    }

    @Test
    public void getAddress() throws Exception {
        model.putAddress(Address.parse("Test"),new OSMNode(2,2));
        model.getAddress("Test");

    }

}