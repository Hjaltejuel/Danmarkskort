package bfst17;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

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
