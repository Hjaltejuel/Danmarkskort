package bfst17.AddressHandling;


import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Beskrivelse: DuplicateAddressNode klassen som bruges som en liste af AddressNode. Bruges som en liste der implementer TSTInterface
 */
public class DuplicateAddressNode extends ArrayList<TSTInterface> implements TSTInterface,Serializable {
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
