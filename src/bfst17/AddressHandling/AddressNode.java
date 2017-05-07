package bfst17.AddressHandling;

import bfst17.OSMData.OSMNode;

import java.awt.*;
import java.io.Serializable;

/**
 * Created by Michelle on 4/27/2017.
 */
public class AddressNode implements TSTInterface, Serializable{
    OSMNode node;
    String cityAndPostcode;
    public AddressNode(OSMNode node, String cityAndPostcode){
        this.cityAndPostcode = cityAndPostcode;
        this.node = node;
    }

    public double getX() {
        return node.getX();
    }

    public double getY() {
        return node.getY();
    }

    public Shape getShape() {
        return null;
    }

    public String getAddress() {
        return cityAndPostcode;
    }

    @Override
    public String toString(){return cityAndPostcode;}
}
