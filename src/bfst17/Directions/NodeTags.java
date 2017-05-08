package bfst17.Directions;

/**
 * Created by Jakob Roos on 07/05/2017.
 */
public class NodeTags {
    public boolean foot, bicycle, oneway;
    public int maxspeed;
    public NodeTags(boolean foot, boolean bicycle, boolean oneway, int maxspeed){
        this.bicycle = bicycle;
        this.foot = foot;
        this.oneway = oneway;
        this.maxspeed = maxspeed;
    }

}
