package bfst17.AddressHandling;

import bfst17.OSMData.OSMNode;
import bfst17.OSMData.PointOfInterestObject;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

/**
 * Beskrivelse: AddresseModel klassen som fungerer som et led mellem tst træet og selve modellen
 */
public class AddressModel extends Observable implements Serializable {

    TST<TSTInterface> tree;

    public AddressModel() {
        tree = new TST<>();
    }

    public TST<TSTInterface> getTSTTree(){return tree;}

    /**
     * Beskrivelse: Metoden som bliver brugt til at indsætte en normal addresse, deler addressen i et prefix og et sufix, hvorefter den indsætter prefix
     * med sufix og et punkt som val;
     * @param address
     * @param Location
     */
    public void putAddress(Address address, Point2D Location) {
        if(address==null) {
            return;
        }
        //Gemer prefix
        String streetAndHouseNum = address.getStreetAndHouseNum();
        //Laver en addressnode som val, med suffix og punktet
        AddressNode point = new AddressNode((float)Location.getX(), (float)Location.getY(), address.getPostcodeAndCity());
        tree.put(streetAndHouseNum, point);
    }

    /**
     * Beskrivelse: Metoden som bliver brugt til at indsætte en region. Indsætter regionens navn som key og en Region som shape
     * @param region
     * @param shape
     */
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
