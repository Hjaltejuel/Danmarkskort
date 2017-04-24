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
    public void fillTree(){
        Object[] data =  addressToCordinate.keySet().toArray();
        Arrays.sort(data);
        System.out.println("hej");
        putElement(data,0,data.length-1);
        System.out.println("hej");

    }
    public String putElement(Object[] data, int lo, int hi){
        if (hi - lo == 0) {
            String s = (String) data[lo];
            tree.put(s,addressToCordinate.get(data[lo]));
            return "";
        }
        int median = (hi+lo)/2;
        Arrays.sort(data);
        tree.put((String)data[median],addressToCordinate.get(data[median]));
        if(hi>median) {
            putElement(data, median+1, hi);
        }
        if(lo<median){
            putElement(data, lo, median-1);
        }
        return "";
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
    TST<Point2D> tree = new TST<>();

    public Region getRegion(String region){
        return regionToShape.get(region);
    }

    public Point2D getPoint2DToAddress(String address) {
        return addressToCordinate.get(Address.parse(address).toString());
    }
}
