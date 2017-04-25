package bfst17;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by trold on 2/15/17.
 */
public class OSMWay extends ArrayList<OSMNode> {
    public List<Point2D> toList(){
        ArrayList<Point2D> returnList = new ArrayList<>();
        for(int i = 0; i<size();i++){
            returnList.add(get(i).getPoint2D());
        }
        return returnList;

    }


    public OSMNode getFromNode() {
        return get(0);
    }

    public OSMNode getToNode() {
        return get(size()-1);
    }
}