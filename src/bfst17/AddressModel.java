package bfst17;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Michelle on 3/6/2017.
 */
public class AddressModel extends Observable implements Serializable {

    public AddressModel() {
        this.addressToCordinate = new HashMap<>();
        this.regionToShape = new HashMap<>();
    }


    public HashMap<String,Point2D> getAddressToCordinate(){return addressToCordinate;}
    public HashMap<String,Region> getRegionToShape(){return regionToShape;}


    public void put(String address, Point2D point) {
            addressToCordinate.put(address, point);
            setChanged();
            notifyObservers();
    }

    public void putRegion(String region, Region shape){
        regionToShape.put(region,shape);
    }

    public HashMap<String, Point2D> addressToCordinate;
    private HashMap<String, Region> regionToShape;

    public Region getRegion(String region){
        return regionToShape.get(region);
    }

    public Point2D getPoint2DToAddress(String address) {
        return addressToCordinate.get(Address.parse(address).toString());
    }
}
