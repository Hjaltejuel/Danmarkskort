package bfst17.Directions;

import bfst17.Enums.RoadDirektion;
import bfst17.Enums.VehicleType;
import bfst17.Model;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public class DirectionObject {
    public Integer getRoadLength() {
        return roadLength;
    }
    public ArrayList<String> allRoadNames;
    private String roadName;

    public void setRoadLength(Integer roadLength) {
        this.roadLength = roadLength;
    }

    private Integer roadLength = 0;
    private RoadDirektion roadDirection;
    private Point2D location;

    public void setNextRoad(String nextRoad) {
        this.nextRoad = nextRoad;
    }

    public String getNextRoad() {
        return nextRoad;
    }

    public String nextRoad;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    boolean visible;

    /**
     * Opret et direktionobjekt
     *
     * @param to    punktet til
     * @param model modellen
     */
    public DirectionObject(Point2D to, Model model, VehicleType vehicleType, double angle, String roadName) {
        visible=false;
        nextRoad="";
        location = to;
        setTurnType(angle);
        this.roadName = roadName;
        //setRoadName(to, model, vehicleType);
    }

    /**
     * Sætter den nuværende vejs navn vha nearest neighbour
     *
     * @param to
     * @param model
     */
    public void setRoadName(Point2D to, Model model, VehicleType vehicleType) {
        roadName = model.getClosestRoad(to, vehicleType).getRoadName();
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public void calculationRoadLength(DirectionObject neighbourNode) {
        this.roadLength = (int) Math.round(Math.sqrt(Math.pow(getX() - neighbourNode.getX(), 2) + Math.pow(getY() - neighbourNode.getY(), 2)) * 100) * 10;
    }

    //Returner x i meter
    public double getX() {
        return location.getX() * 111.320 * Math.cos(location.getY() / 180 * Math.PI);
    }

    //Returner y i meter
    public double getY() {
        return location.getY() * 110.574;
    }

    /**
     * Udregner vinklen på de to punkter og finder vejens retning
     *
     * @param from
     * @param to
     */
    public void setTurnType(double angle) {
        //double angle = Math.atan2(to.getY() - from.getY(), to.getX() - from.getX());
        double angleDegree = ((angle * 180 / Math.PI) + 360) % 360; //Altid mere en 0 & under 360
        roadLength=(int)(angleDegree);
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