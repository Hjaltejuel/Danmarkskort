package bfst17.Directions;

import bfst17.Enums.RoadDirektion;

import java.awt.geom.Point2D;

public class DirectionsObject
{
    private String roadName;
    private Integer roadLength = 0;
    private RoadDirektion roadDirection;
    private Point2D location;
    boolean visible;
    public String nextRoad;

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