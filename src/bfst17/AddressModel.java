package bfst17;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Michelle on 3/6/2017.
 */
public class AddressModel extends Observable implements Serializable {
    private ArrayList<Address> addresses = new ArrayList<>();
    private ArrayList<Point2D> points = new ArrayList<>();

    public AddressModel() {
        this.addresses = new ArrayList();
    }

    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public void add(Address address) {
        this.addresses.add(address);
        this.setChanged();
        this.notifyObservers();
    }

    public void put(String address, Point2D point) {
        addressToCordinate.put(address, point);
    }

    public HashMap<String, Point2D> addressToCordinate = new HashMap<>();

    public Point2D getPoint2DToAddress(String address) {
        return addressToCordinate.get(address);
    }
}
