package bfst17;

import java.awt.geom.Path2D;
import java.util.ArrayList;

/**
 * Created by trold on 2/15/17.
 */
public class OSMRelation extends ArrayList<OSMWay> {
    public Path2D toPath2D() {
        Path2D path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
        for (OSMWay way : this) {
            if (way != null) {
                path.append(way.toPath2D(), false);
            }
        }
        return path;
    }
}
