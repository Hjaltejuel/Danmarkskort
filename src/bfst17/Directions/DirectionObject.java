package bfst17.Directions;

import bfst17.Enums.RoadDirektion;
import bfst17.Enums.VehicleType;
import bfst17.Model;

import java.awt.geom.Point2D;

/**
 * Created by Jens on 10-05-2017.
 */
public class DirectionObject {
    private String currentRoad;
    private double roadLength;
    private RoadDirektion roadDirection;
    private Point2D location;

    /**
     * Opret et direktionobjekt
     * @param from Punktet fra
     * @param to punktet til
     * @param model modellen
     */
    public DirectionObject(Point2D to, Model model, VehicleType vehicleType) {
        location=to;
        //calculationRoadLength(from, to);
        //setTurnType(from, to);
        setRoadName(to, model, vehicleType);
    }

    /**
     * Sætter den nuværende vejs navn vha nearest neighbour
     * @param to
     * @param model
     */
    public void setRoadName(Point2D to, Model model, VehicleType vehicleType) {
        currentRoad = model.getClosestRoad(to, vehicleType).getRoadName();
        roadLength=0;
        roadDirection=RoadDirektion.EAST;
    }

    /**
     * Udregner vejens længde vha pythagoras (a^2+b^2=c^2)
     * @param from
     * @param to
     */
    public void calculationRoadLength(Point2D from, Point2D to) {
        this.roadLength=Math.sqrt(Math.pow(from.getX()-to.getX(),2)+Math.pow(from.getY()-to.getY(),2));
    }

    /**
     * Udregner vinklen på de to punkter og finder vejens retning
     * @param from
     * @param to
     */
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

    public RoadDirektion getRoadDirection() {
        return roadDirection;
    }

    public Point2D getLocation() {
        return location;
    }

    public String getCurrentRoad() {
        return currentRoad;
    }
}
