package bfst17.Directions;

import bfst17.Enums.RoadTypes;
import bfst17.Enums.VehicleType;
import bfst17.Enums.WeighType;

import java.io.Serializable;
import java.util.DoubleSummaryStatistics;


public class Edge implements Serializable {
    private long graphNodeid;
    private double weight;
    private double distance;
    private String roadName;

    private double maxSpeed;

    private RoadTypes roadTypes;
    public String getRoadName() {
        return roadName;
    }
    public double getMaxSpeed() {
        return maxSpeed;
    }


    /**
     * Beskrivelse: opretter en edge mellem to graphnodes baseret på deres id
     * @param source
     * @param destination
     * @param graphNodeid
     * @param roadName
     * @param maxSpeed
     * @param roadTypes
     */
    public Edge(GraphNode source,GraphNode destination, long graphNodeid, String roadName, double maxSpeed, RoadTypes roadTypes) {
        this.roadName = roadName;
        this.graphNodeid = graphNodeid;
        if (maxSpeed == 0) {
            //Ganger med en konstant under 1 for at tage højde for sving mm.
            this.maxSpeed = roadTypes.getMaxSpeed()*0.8f;
        } else {
            this.maxSpeed = maxSpeed;
        }
        this.roadTypes = roadTypes;
        distance = Math.sqrt(Math.pow(destination.getPoint2D().getX() - source.getPoint2D().getX(), 2) +
                Math.pow(destination.getPoint2D().getY() - source.getPoint2D().getY(), 2));
    }

    /**
     * Udregner edges vægt mhp. at det skal være den hurtigeste rute
     * der tages altså også højde for hvor hurtigt man må køre på vejen
     */
    private void calcWeightForFastest() {
        weight = distance / maxSpeed;
    }

    /**
     * Udregner edges vægt på baggrund af faktisk afstand mellem de to GraphNodes
     */
    private void calcWeightForShortest() {
        weight = distance;
    }

    public long getDestinationId() {
        return graphNodeid;
    }

    /**
     * Beskrivelse: Tjekker om en edge kan blive traverset af den bestemte type
     * @param vehicleType
     * @return
     */
    public boolean supportVehicle(VehicleType vehicleType){
        for(VehicleType vehicle: roadTypes.getVehicletypes()){
            if(vehicle == vehicleType){
                return true;
            }
        }  return false;
    }


    /**
     * Beskrivelse: Udregner vægten
     * @param vehicleType hvorvidt det skal være Fastest / Shortest
     * @return returnerer den tilpassede vægt
     */
    public double getWeight(VehicleType vehicleType) {
        if(vehicleType == VehicleType.CAR) {
            calcWeightForFastest();
        } else if(vehicleType == VehicleType.BICYCLE || vehicleType == VehicleType.FOOT) {
            calcWeightForShortest();
        }
        return weight;
    }
}