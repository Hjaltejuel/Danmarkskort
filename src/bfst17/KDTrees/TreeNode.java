package bfst17.KDTrees;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;


public abstract class TreeNode implements Comparable<TreeNode>, Serializable {
    protected double X, Y;
    protected TreeNode low;
    protected TreeNode high;
    protected boolean vertical;
    protected Shape shape;
    public double getX() { return X; }
    public double getY() { return Y; }
    public Shape getShape() { return shape; }

    abstract boolean sortVertically();

    //Er noden inde i den givne rectangel
    protected abstract boolean isInside(Rectangle2D rect);

    public double getSplit() {
        return vertical ? X : Y;
    }

    double getComparePoint() {
        return sortVertically() ? X : Y;
    }

    public int compareTo(TreeNode other) {
        double cmp = getComparePoint() - other.getComparePoint();
        if (cmp > 0) {
            return 1;
        } else if (cmp < 0) {
            return -1;
        }
        return 0;
    }


    public double distance(Point2D p1) {
        return Math.sqrt(Math.pow(p1.getX()-getX(),2)+Math.pow(p1.getY()-getY(),2));
    }

    @Override
    public String toString() {
        return "(" + X + "," + Y + ")";
    }
}
