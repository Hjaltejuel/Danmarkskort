package bfst17;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Michelle on 3/6/2017.
 */
public class AddressModel extends Observable implements Serializable {

    public AddressModel() {
        this.addressToCordinate = new HashMap<>();
    }


    public HashMap<String,Point2D> getAddressToCordinate(){return addressToCordinate;}


    public void put(String address, Point2D point) {
            addressToCordinate.put(address, point);
            setChanged();
            notifyObservers();
    }

    public HashMap<String, Point2D> addressToCordinate;

    public Point2D getPoint2DToAddress(String address) {
        return addressToCordinate.get(Address.parse(address).toString());
    }
}
