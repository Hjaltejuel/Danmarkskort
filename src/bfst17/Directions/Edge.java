package bfst17.Directions;

/**
 * Created by Jakob Roos on 24/04/2017.
 */
public class Edge {
        private GraphNode source;
        private GraphNode destination;

        private double weight;

        double weighted;


        public Edge(GraphNode source, GraphNode destination){
            this.source = source;
            this.destination = destination;
            weighted = Math.pow(destination.getPoint2D().getX() - source.getPoint2D().getX(),2) +
                    Math.pow(destination.getPoint2D().getY() - source.getPoint2D().getY(),2);
            Weigh("FASTEST");
            source.insertNeighbor(this);
       //     System.out.println(weightCar + " " + weightFoot + " " + weightBicycle);
        }
        public void Weigh(String weighingMethod){
            switch(weighingMethod){
                case "SHORTEST": calcWeightForShortest();
                    break;
                case "FASTEST": calcWeightForFastest();
                    break;
            }
        }
        private void calcWeightForFastest(){

            if(source.getMaxSpeed() >= destination.getMaxSpeed()) {
                weight = Math.sqrt(weighted)/source.getMaxSpeed();
            }
            else{
                weight = Math.sqrt(weighted)/(destination.getMaxSpeed());
            }

        }
        private void calcWeightForShortest(){

            weight = weighted;
        }
        public GraphNode getSource(){
            return source;
        }
        public GraphNode getDestination() {
            return destination;
        }
        public double getWeight(){
            return weight;
    }


}
