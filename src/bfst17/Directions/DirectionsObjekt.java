package bfst17.Directions;

import bfst17.Enums.TurnType;
import bfst17.Model;
import bfst17.OSMData.PointOfInterestObject;

import java.awt.geom.Point2D;

/**
 * Created by Jens on 10-05-2017.
 */
public class DirectionsObjekt {
    public String getCurrentRoad() {
        return currentRoad;
    }

    public double getRoadLength() {
        return roadLength;
    }

    public TurnType getTurnDirection() {
        return turnDirection;
    }

    String currentRoad;
    double roadLength;
    TurnType turnDirection;
    public DirectionsObjekt(Point2D from, Point2D to, Model model) {
        calculationRoadLength(from,to);
        setTurnType(from,to);
        setRoadName(from,model);
    }

    public void setRoadName(Point2D from, Model model) {
        currentRoad = model.getClosestRoad(from).getRoadName();
    }

    public void calculationRoadLength(Point2D from, Point2D to) {
        this.roadLength=Math.sqrt(Math.pow(from.getX()-to.getX(),2)+Math.pow(from.getY()-to.getY(),2));
    }

    public void setTurnType(Point2D from, Point2D to) {
        double angle = Math.atan2(to.getY() - from.getY(), to.getX() - from.getX());
        if (angle < 1 && angle > -1) {
            this.turnDirection = TurnType.RIGHT;
        } else if (angle < 2 && angle > -2) {
            this.turnDirection = TurnType.LEFT;
        } else {
            this.turnDirection = TurnType.STRAIGHT;
        }
    }

    @Override
    public String toString(){
        return currentRoad+" "+roadLength+" "+turnDirection.name();
    }
}
