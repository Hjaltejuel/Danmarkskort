package bfst17.Directions;

import bfst17.Enums.VehicleType;
import bfst17.Enums.WeighType;

import java.util.DoubleSummaryStatistics;


public class Edge {
    private GraphNode destination;
    private double weight;
    private double distance;

    /**
     * Opretter en edge mellem to GraphNodes
     * @param source
     * @param destination
     */
    public Edge(GraphNode source, GraphNode destination) {
        this.destination = destination;
        distance = Math.sqrt(Math.pow(destination.getPoint2D().getX() - source.getPoint2D().getX(), 2) +
                Math.pow(destination.getPoint2D().getY() - source.getPoint2D().getY(), 2));
    }

    /**
     * Udregner edges vægt mhp. at det skal være den hurtigeste rute
     * der tages altså også højde for hvor hurtigt man må køre på vejen
     */
    private void calcWeightForFastest() {
        double maxSpeed = destination.getMaxSpeed();
        if(maxSpeed==0) {
            //System.out.println("Car route fucked!");
            maxSpeed=destination.getType().getMaxSpeed();
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