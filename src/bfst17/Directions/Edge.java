package bfst17.Directions;

import bfst17.Enums.RoadTypes;
import bfst17.Enums.VehicleType;
import bfst17.Enums.WeighType;

import java.util.DoubleSummaryStatistics;


public class Edge {
    private GraphNode destination;
    private double weight;
    private double distance;
    private String roadName;
    private double maxSpeed;
    private RoadTypes roadTypes;
    public String getRoadName() {
        return roadName;
    }

    public double getEstimatedTime(VehicleType vehicleType) {
        if(vehicleType==VehicleType.CAR){
            if(maxSpeed==0){maxSpeed=1;}
            return distance / maxSpeed;
        } else if(vehicleType==vehicleType.BICYCLE) {
            return distance/25;
        } else if(vehicleType==vehicleType.BICYCLE) {
            return distance / 5;
        }
        return 0;
    }

    /**
     * Opretter en edge mellem to GraphNodes
     * @param source
     * @param destination
     */
    public Edge(GraphNode source, GraphNode destination, String roadName, double maxSpeed, RoadTypes roadTypes) {
        this.roadName=roadName;
        this.destination = destination;
        if(maxSpeed==0) {
            this.maxSpeed = roadTypes.getMaxSpeed();
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
        if(maxSpeed==0) {
            //System.out.println("Car route fucked!");
            maxSpeed=roadTypes.getMaxSpeed();
           // maxSpeed = 1;
        }
        weight = distance / maxSpeed;
    }

    /**
     * Udregner edges vægt på baggrund af faktisk afstand mellem de to GraphNodes
     */
    private void calcWeightForShortest() {
        weight = distance;
    }

    public GraphNode getDestination() {
        return destination;
    }

    public boolean supportVehicle(VehicleType vehicleType){
        for(VehicleType vehicle: roadTypes.getVehicletypes()){
            if(vehicle == vehicleType){
                return true;
            }
        }  return false;
    }


    /**
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