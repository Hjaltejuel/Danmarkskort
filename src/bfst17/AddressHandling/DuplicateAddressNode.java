package bfst17.AddressHandling;

import bfst17.AddressHandling.TSTInterface;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Michelle on 4/28/2017.
 */
public class DuplicateAddressNode extends ArrayList<TSTInterface> implements TSTInterface,Serializable{
    public DuplicateAddressNode() {
    }

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }

    @Override
    public Shape getShape() {
        return null;
    }

    @Override
    public String getAddress() {
        return null;
    }
}
