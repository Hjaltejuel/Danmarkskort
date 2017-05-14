package bfst17.AddressHandling;

import java.awt.*;
import java.io.Serializable;

/**
 * Beskrivelse: AddressNode klassen som Symboliserer en addresses val i tst træet. Indeholder et punkt og et suffix
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
