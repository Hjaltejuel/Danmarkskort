package bfst17;

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

    @Override
    public double getX() {
        return node.getX();
    }

    @Override
    public double getY() {
        return node.getY();
    }


    @Override
    public Shape getShape() {
        return null;
    }

    @Override
    public String getAddress() {
        return cityAndPostcode;
    }

    public String toString(){return cityAndPostcode;}
}
