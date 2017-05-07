package bfst17.OSMData;

import java.util.ArrayList;

/**
 * Created by trold on 2/15/17.
 */
public class OSMWay extends ArrayList<OSMNode> {
    private boolean relevantForRouting = false;

    public OSMNode getFromNode() {
        return get(0);
    }

    public OSMNode getToNode() {
        return get(size()-1);
    }

    public boolean isRelevantForRouting() {
        return relevantForRouting;
    }

    public void setRelevantForRouting(boolean relevantForRouting) {
        this.relevantForRouting = relevantForRouting;
    }
}
