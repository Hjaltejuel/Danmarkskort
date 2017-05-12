package bfst17.Directions;

import bfst17.Enums.WeighType;

/**
 * Created by Jakob Roos on 24/04/2017.
 */
public class Edge {
    private GraphNode source;
    private GraphNode destination;
    private double weight;
    private double distance;


    public Edge(GraphNode source, GraphNode destination) {
        this.source = source;
        this.destination = destination;
        distance = Math.sqrt(Math.pow(destination.getPoint2D().getX() - source.getPoint2D().getX(), 2) +
                Math.pow(destination.getPoint2D().getY() - source.getPoint2D().getY(), 2));
    }

    private void calcWeightForFastest() {
        Integer maxSpeed = Math.max(source.getMaxSpeed(),destination.getMaxSpeed());
        weight = distance / maxSpeed;
    }

    private void calcWeightForShortest() {
        weight = distance;
    }

    public GraphNode getSource() {
        return source;
    }

    public GraphNode getDestination() {
        return destination;
    }

    public double getWeight(WeighType weighType) {
        if(weighType == WeighType.FASTEST) {
            calcWeightForFastest();
        } else if(weighType == WeighType.SHORTEST) {
            calcWeightForShortest();
        }
        return weight;

    }
}
