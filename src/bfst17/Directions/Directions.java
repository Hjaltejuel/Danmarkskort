package bfst17.Directions;

import bfst17.Enums.RoadDirektion;
import bfst17.Enums.VehicleType;

import java.util.ArrayList;


public class Directions extends ArrayList<DirectionsObject> {
    double totalRoadLength=0;
    double estimatedTime=0;
    VehicleType vehicleType;
    /**
     * Description: Lav vejvisning ud fra shortestPath hvis den er lavet.
     * Description: HVIS shortest path eksisterer: ArrayListe af DirectionObjekter, der indeholder vejvisningsinformation
     * Description: HVIS ikke shortest path exist: En tom ArrayListe
     * @return
     *
     */
    public Directions(ArrayList<GraphNode> graphNodeList, VehicleType vehicleType){
        double currentDirection = 0;
        if (graphNodeList == null) {
            return;
        }
        this.vehicleType=vehicleType;

        for (int i = 1; i < graphNodeList.size(); i++) {
            GraphNode prevGraphNode = graphNodeList.get(i - 1);
            GraphNode currentGraphNode = graphNodeList.get(i);
            double angle = Math.atan2(currentGraphNode.getPoint2D().getY() - prevGraphNode.getPoint2D().getY(),
                    currentGraphNode.getPoint2D().getX() - prevGraphNode.getPoint2D().getX());
            for (Edge edge : prevGraphNode.getEdgeList()) {
                if (edge.getDestination() == currentGraphNode) {
                    DirectionsObject DirObj = new DirectionsObject(prevGraphNode.getPoint2D(), currentDirection - angle, edge);
                    this.add(DirObj);
                    if (DirObj.getRoadDirection() != RoadDirektion.lige_ud) {
                        DirObj.setVisible(true);
                    }
                    currentDirection = angle;
                    break;
                }
            }
        }
        double distanceSum = 0;
        for (int i = 1; i < this.size(); i++) {
            DirectionsObject prevDirObj = this.get(i - 1);
            DirectionsObject currDirObj = this.get(i);
            if(prevDirObj.getRoadDirection()==RoadDirektion.lige_ud){
                if(!prevDirObj.getRoadName().equals(currDirObj.getRoadName())) {
                    currDirObj.setVisible(true);
                }
            }
            distanceSum += prevDirObj.getRoadLength(currDirObj);
            if (currDirObj.isVisible()) {
                currDirObj.setRoadLength((int) distanceSum);
                totalRoadLength += distanceSum;
                estimatedTime += currDirObj.distanceToTime(distanceSum, vehicleType);
                distanceSum = 0;
            }
            prevDirObj.nextRoad = currDirObj.getRoadName();
        }
        System.out.println("Estimated time: " +estimatedTime);
    }

    public String getTotalRoadLengthText() {
        if (totalRoadLength > 1000) {
            return (int) (totalRoadLength / 1000) + " km";
        } else {
            return (int) totalRoadLength + " m";
        }
    }


    public String getDirectionString(Integer index) {
        DirectionsObject dirObj = this.get(index);
        String prefix;
        RoadDirektion vejRetning = dirObj.getRoadDirection();
        if (vejRetning == RoadDirektion.lige_ud) {
            prefix = "Fortsæt lige ud";
            return null;
        } else {
            prefix = "Drej til " + vejRetning.name();
        }
        String directionText = "";
        if ((index + 1) == this.size()) {
            directionText = "Ankommer til " + dirObj.getRoadName();
        } else {
            String roadLengthString = "m";
            Integer roadLength = dirObj.getRoadLength();
            if (roadLength > 1000) {
                roadLength = roadLength / 1000;
                roadLengthString = "km";
            }
            directionText = "Om " + roadLength + roadLengthString + " " + prefix + " ad " + dirObj.getRoadName();//nextDirection.getRoadName();
        }
        return directionText;
    }
}