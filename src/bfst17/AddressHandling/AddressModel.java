package bfst17.AddressHandling;

import bfst17.OSMData.OSMNode;
import bfst17.OSMData.PointOfInterestObject;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Michelle on 3/6/2017.
 */
public class AddressModel extends Observable implements Serializable {

    TST<TSTInterface> tree;

    public AddressModel() {
        tree = new TST<>();
    }

    public TST<TSTInterface> getTSTTree(){return tree;}

    public void putAddress(Address address, Point2D Location) {
        if(address==null) {
            return;
        }
        String streetAndHouseNum = address.getStreetAndHouseNum();
        AddressNode point = new AddressNode((float)Location.getX(), (float)Location.getY(), address.getPostcodeAndCity());
        tree.put(streetAndHouseNum, point);
    }
    public void putRegion(String region, Region shape){
        tree.put(region,shape);
    }

    public void putCity(String city, Point2D point){
        tree.put(city,new OSMNode(point));
    }

    public TSTInterface getAddress(String address) {
        return tree.get(address);
    }
}
