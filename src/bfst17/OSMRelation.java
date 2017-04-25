package bfst17;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by trold on 2/15/17.
 */
public class OSMRelation extends ArrayList<OSMWay> {
    public ArrayList<ArrayList<Point2D>> toList() {
        ArrayList<ArrayList<Point2D>> returnList = new ArrayList<>();
        for (OSMWay way : this) {
            if (way != null) {
                returnList.add((ArrayList<Point2D>) way.toList());
            }
        }
        if(returnList.size()!=0) {
            return returnList;
        } else return null;
    }
}
