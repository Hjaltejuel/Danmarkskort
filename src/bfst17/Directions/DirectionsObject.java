package bfst17.Directions;

import bfst17.Enums.RoadDirektion;
import bfst17.Enums.VehicleType;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class DirectionsObject implements Serializable
{
    private String roadName;
    private Integer roadLength = 0;
    private RoadDirektion roadDirection;
    private Point2D location;
    boolean visible;
    public String nextRoad;

    public double getMaxSpeed() {
        return maxSpeed;
    }

    double maxSpeed;

    /**
     * Opret et direktionobjekt
     *
     * @param to    punktet til
     * @param model modellen
     */
    public DirectionsObject(Point2D to, double angle, Edge edge) {
        visible = false;
        nextRoad = "";
        location = to;
        setTurnType(angle);
        this.roadName = edge.getRoadName();
        this.maxSpeed = edge.getMaxSpeed();
    }

    double distanceToTime(double distance, VehicleType vehicleType) {
        double time =0;
        distance/=1000;
        if (vehicleType == VehicleType.CAR) {
            if (maxSpeed == 0) {
                maxSpeed = 1;
            }
            time = distance / maxSpeed;
        } else if (vehicleType == vehicleType.BICYCLE) {
            time = distance / 15;
        } else if (vehicleType == vehicleType.FOOT) {
            time = distance / 5;
        }
        return time*60;
    }

    public void setRoadLength(Integer roadLength) {
        this.roadLength = roadLength;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Konverterer en vinkel til højre | venstre | lige ud
     * @param angle     vinklen i forhold til den forrige vej *I radianer*
     */
    public void setTurnType(double angle) {
        //double angle = Math.atan2(to.getY() - from.getY(), to.getX() - from.getX());
        double angleDegree = ((angle * 180 / Math.PI) + 360) % 360; //Altid mere en 0 & under 360
        if (angleDegree >= 30 && angleDegree <= 150) {
            this.roadDirection = RoadDirektion.venstre;
        } else if (angleDegree > 150 && angleDegree <= 210) {
            this.roadDirection = RoadDirektion.lige_ud;
        } else if (angleDegree < 30 || angleDegree >= 330) {
            this.roadDirection = RoadDirektion.lige_ud;
        } else {
            this.roadDirection = RoadDirektion.højre;
        }
    }

    public double getRoadLength(DirectionsObject neighbourNode) {
        return Math.sqrt(Math.pow(getX() - neighbourNode.getX(), 2) + Math.pow(getY() - neighbourNode.getY(), 2)) * 1000;
    }

    public Integer getRoadLength() {
        return roadLength;
    }

    public boolean isVisible() {
        return visible;
    }

    //Returner x i meter
    public double getX() {
        return location.getX() * 111.320 * Math.cos(location.getY() / 180 * Math.PI);
    }

    //Returner y i meter
    public double getY() {
        return location.getY() * 110.574;
    }

    @Override
    public String toString() {
        return roadName + " " + roadLength + " " + roadDirection.name();
    }

    public RoadDirektion getRoadDirection() {
        return roadDirection;
    }

    public Point2D getLocation() {
        return location;
    }

    public String getRoadName() {
        return roadName;
    }
}