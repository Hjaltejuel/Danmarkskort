package bfst17.AddressHandling;

import bfst17.OSMData.OSMNode;

import java.awt.*;
import java.io.Serializable;

/**
 * Created by Michelle on 4/27/2017.
 */
public class AddressNode implements TSTInterface, Serializable{
    float X, Y;
    String cityAndPostcode;
    public AddressNode(float lon, float lat, String cityAndPostcode){
        this.cityAndPostcode = cityAndPostcode;
        this.X=lon;
        this.Y=lat;
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
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
