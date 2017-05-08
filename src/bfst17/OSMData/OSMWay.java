package bfst17.OSMData;

import java.util.ArrayList;

/**
 * Created by trold on 2/15/17.
 */
public class OSMWay extends ArrayList<OSMNode> {

    public OSMNode getFromNode() {
        return get(0);
    }

    public OSMNode getToNode() {
        return get(size()-1);
    }

}
