package bfst17;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

/**
 * Created by Michelle on 3/6/2017.
 */
public class AddressModel extends Observable{
    private ArrayList<Address> addresses = new ArrayList<Address>();
    private List<Address> addressesUdskrift;

    public AddressModel() {
        this.addresses = new ArrayList();
        this.addressesUdskrift = new ArrayList();
    }

    public void add(Address address) {
        this.addressesUdskrift.add(address);
        this.addresses.add(address);
        this.setChanged();
        this.notifyObservers();
    }

    public Iterator<Address> iterator() {
        return this.addresses.iterator();
    }

    public List<Address> getAddresses() {
        return this.addresses;
    }

    public List<Address> getAddressesUdskrift() {
        return this.addressesUdskrift;
    }
}
