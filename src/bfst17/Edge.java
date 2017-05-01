package bfst17;

/**
 * Created by Jakob Roos on 24/04/2017.
 */
public class Edge {
        private OSMNode source;
        private OSMNode destination;

        private double weightCar;
        private double weightFoot;
        private double weightShortest;

        double weightedX;
        double weightedY;


        public Edge(OSMNode source, OSMNode destination){
            this.source = source;
            this.destination = destination;
            weightedX = destination.getPoint2D().getX() - source.getPoint2D().getX();
            weightedY = destination.getPoint2D().getY() - source.getPoint2D().getY();
            Weigh();
            source.insertNeighbor(this);
       //     System.out.println(weightCar + " " + weightFoot + " " + weightBicycle);
        }
        private void Weigh(){

            if(source.isShortest() && destination.isShortest()){
                weightShortest = calcWeightForShortest();
            }
            if(source.getMaxspeed() > 0 && destination.getMaxspeed() > 0){
                weightCar = calcWeightForFastest();
            }
        }
        private double calcWeightForFastest(){
            int speed;
            if(source.getMaxspeed() >= destination.getMaxspeed()) {
                speed = source.getMaxspeed();
            }
            else{
                speed = destination.getMaxspeed();
            }
            return Math.sqrt(Math.pow(weightedX,2) + Math.pow(weightedY,2))/speed;
        }
        private double calcWeightForShortest(){

            return Math.sqrt(Math.pow(weightedX,2) + Math.pow(weightedY,2));
        }
        public OSMNode getSource(){
            return source;
        }
        public OSMNode getDestination(){
            return destination;
        }
        public double getWeightCar(){
            return weightCar;
        }
        public double getWeightShortest(){
            return weightFoot;
        }


}
