package bfst17.OSMData;

import java.util.ArrayList;


public class OSMWay extends ArrayList<OSMNode> {

    public OSMNode getFromNode() {
        return get(0);
    }

    public OSMNode getToNode() {
        return get(size()-1);
    }

}
