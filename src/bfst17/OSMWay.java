package bfst17;

import java.awt.geom.Path2D;
import java.util.ArrayList;

/**
 * Created by trold on 2/15/17.
 */
public class OSMWay extends ArrayList<OSMNode> {
    public Path2D toPath2D() {
        Path2D path = new Path2D.Float();
        OSMNode node = get(0);
        path.moveTo(node.getLon(), node.getLat());
        for (int i = 1 ; i < size() ; i++) {
            node = get(i);
            path.lineTo(node.getLon(), node.getLat());
        }
        return path;
    }


    public OSMNode getFromNode() {
        return get(0);
    }

    public OSMNode getToNode() {
        return get(size()-1);
    }
}
