package bfst17;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.lang.instrument.Instrumentation;
import java.util.*;

/**
 * Created by Michelle on 3/6/2017.
 */
public class AddressModel extends Observable implements Serializable {

    public AddressModel() {
        tree = new TST<>();
    }

    public TST<TSTInterface> getTSTTree(){return tree;}

    public void put(String address, AddressNode point) {
        if(!address.equals("")) {
            tree.put(address, point);
        }
    }

    public void putRegion(String region, Region shape){
        tree.put(region,shape);
    }

    TST<TSTInterface> tree;


    public TSTInterface getAddress(String address) {
        return tree.get(Address.parse(address).toString());
    }
}
