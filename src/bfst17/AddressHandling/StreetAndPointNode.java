package bfst17.AddressHandling;

import java.awt.geom.Point2D;


/**
 * Beskrivelse: StreetAndPointNode klassen som symboliserer en gade med et centerpunkt i TST træet
 */
public class StreetAndPointNode {
    public String getName() {
        return name;
    }

    public Point2D getPoint() {
        return point;
    }

    String name;
    Point2D point;
    public StreetAndPointNode(String name, Point2D point) {
        this.name=name;
        this.point=point;
    }
}
