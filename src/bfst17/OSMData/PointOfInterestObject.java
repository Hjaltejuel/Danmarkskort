package bfst17.OSMData;

import bfst17.Enums.PointsOfInterest;

/**
 * Created by Jens on 03-05-2017.
 */
public class PointOfInterestObject {
    public PointsOfInterest getType() {
        return type;
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    PointsOfInterest type;
    double X, Y;

    /**
     * Opretter et PointOfInterestObject Objekt
     * @param type
     * @param X
     * @param Y
     */
    public PointOfInterestObject(PointsOfInterest type, double X, double Y) {
        this.type=type;
        this.X=X;
        this.Y=Y;
    }
}
