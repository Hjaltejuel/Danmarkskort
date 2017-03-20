package bfst17;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;

/**
 * Created by Michelle on 3/6/2017.
 */
public class AddressModel extends Observable{
    private ArrayList<Address> addresses = new ArrayList<>();

    public AddressModel() {
        this.addresses = new ArrayList();
    }

    public void add(Address address) {
        this.addresses.add(address);
        this.setChanged();
        this.notifyObservers();
    }

    public Iterator<Address> iterator() {
        return this.addresses.iterator();
    }

}
