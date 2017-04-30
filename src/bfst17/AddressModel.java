package bfst17;

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

    public void putAddress(Address address, OSMNode node) {
        if(address==null) {
            return;
        }
        String streetAndHouseNum = address.getStreetAndHouseNum();
        AddressNode point = new AddressNode(node, address.getPostcodeAndCity());
        tree.put(streetAndHouseNum, point);
    }
    public void putRegion(String region, Region shape){
        tree.put(region,shape);
    }

    public void putCity(String city, OSMNode point){
        tree.put(city,point);
    }

    public TSTInterface getAddress(String address) {
        return tree.get(address);
    }
}
