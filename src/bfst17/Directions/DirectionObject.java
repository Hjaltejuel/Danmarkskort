package bfst17.Directions;

import bfst17.Enums.RoadDirektion;
import bfst17.Model;

import java.awt.geom.Point2D;

/**
 * Created by Jens on 10-05-2017.
 */
public class DirectionObject {
    public String getCurrentRoad() {
        return currentRoad;
    }

    public RoadDirektion getRoadDirection() {
        return roadDirection;
    }

    String currentRoad;
    double roadLength;
    RoadDirektion roadDirection;

    public Point2D getLocation() {
        return location;
    }

    Point2D location;
    public DirectionObject(Point2D from, Point2D to, Model model) {
        location=from;
        calculationRoadLength(from, to);
        setTurnType(from, to);
        setRoadName(to, model);
    }

    public void setRoadName(Point2D to, Model model) {
        currentRoad = model.getClosestRoad(to).getRoadName();
    }

    public void calculationRoadLength(Point2D from, Point2D to) {
        this.roadLength=Math.sqrt(Math.pow(from.getX()-to.getX(),2)+Math.pow(from.getY()-to.getY(),2));
    }

    public void setTurnType(Point2D from, Point2D to) {
        double angle = Math.atan2(to.getY() - from.getY(), to.getX() - from.getX());
        double angleDegree = ((angle*180/Math.PI)+360)%360; //Altid mere en 0 & under 360
        if (angleDegree < 0 || angleDegree > 360) throw new AssertionError();

        if (angleDegree > 315 || angleDegree <= 45) {
            this.roadDirection = RoadDirektion.EAST;
        } else if (angleDegree > 45  && angleDegree <= 135) {
            this.roadDirection = RoadDirektion.SOUTH;
        } else if (angleDegree > 135  && angleDegree <= 225) {
            this.roadDirection = RoadDirektion.WEST;
        } else if (angleDegree > 225  && angleDegree <= 315) {
            this.roadDirection = RoadDirektion.NORTH;
        }
    }

    @Override
    public String toString(){
        return currentRoad+" "+roadLength+" "+ roadDirection.name();
    }
}
