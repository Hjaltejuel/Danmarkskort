package bfst17;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michelle on 4/28/2017.
 */
public class DuplicateAddressNode extends ArrayList<TSTInterface> implements TSTInterface{
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
