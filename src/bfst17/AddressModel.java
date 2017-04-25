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

    public TST<Point2D,Region> getTSTTree(){return tree;}

    public void put(String address, Point2D point) {
        if(!address.equals("")) {
            tree.put(address, point, null);
        }
    }

    public void putRegion(String region, Region shape){
        tree.put(region,null,shape);
    }

    TST<Point2D,Region> tree;

    public Region getRegion(String region){
        return tree.getval2(region);
    }

    public Point2D getPoint2DToAddress(String address) {
        return tree.get(Address.parse(address).toString());
    }
}
