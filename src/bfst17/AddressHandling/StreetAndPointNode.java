package bfst17.AddressHandling;

import java.awt.geom.Point2D;

/**
 * Created by Jens on 07-05-2017.
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
