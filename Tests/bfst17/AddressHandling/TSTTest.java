package bfst17.AddressHandling;

import bfst17.OSMData.OSMNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Michelle on 5/8/2017.
 */
public class TSTTest {
    TST tst;
    @Before
    public void setUp() throws Exception {
        tst = new TST();
    }

    @Test
    public void size() throws Exception {

    }

    @Test
    public void get() throws Exception {

    }

    @Test
    public void put() throws Exception {
        AddressNode node = new AddressNode(new OSMNode(2,2),"12 2300");
        tst.put("Vejlands ale",node);
        assertEquals(tst.get("Vejlands ale"),node);
    }

    @Test
    public void keysWithPrefix() throws Exception {
        AddressNode node1 = new AddressNode(new OSMNode(2,2),"");
        AddressNode node2 = new AddressNode(new OSMNode(2,2),"");
        AddressNode node3 = new AddressNode(new OSMNode(2,2),"");
        AddressNode node4 = new AddressNode(new OSMNode(2,2),"");
        AddressNode node5 = new AddressNode(new OSMNode(2,2),"");
        AddressNode node6 = new AddressNode(new OSMNode(2,2),"");

        tst.put("vejlan",node3);
        tst.put("ve",node5);
        tst.put("vej",node2);
        tst.put("vejland",node4);
        tst.put("vejlands",node6);
        tst.put("vejl",node4);
        tst.put("vejla",node1);


        ArrayList<String> test = tst.keysWithPrefix("v");

        assertEquals(test.get(0).replace(",","").trim(),"ve");
        assertEquals(test.get(1).replace(",","").trim(),"vej");
        assertEquals(test.get(2).replace(",","").trim(),"vejl");
        assertEquals(test.get(3).replace(",","").trim(),"vejla");
        assertEquals(test.get(4).replace(",","").trim(),"vejlan");

    }

    @Test
    public void makeArray() throws Exception {

    }

    @Test
    public void addAddressNodeToQueue() throws Exception {

    }

    @Test
    public void addOtherNodeToQueue() throws Exception {

    }

    @Test
    public void similarity() throws Exception {

    }

    @Test
    public void editDistance() throws Exception {

    }

}