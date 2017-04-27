package bfst17;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by Michelle on 4/27/2017.
 */
public class AddressNode implements TSTInterface{
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
}
