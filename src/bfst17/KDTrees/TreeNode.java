package bfst17.KDTrees;

import java.awt.*;
import java.io.Serializable;

/**
 * Created by Jens on 02-05-2017.
 */
public abstract class TreeNode implements Comparable<TreeNode>, Serializable {
    protected double X, Y;
    protected TreeNode low;
    protected TreeNode high;
    protected boolean vertical;
    protected Shape shape;
    public double getX() { return X; };
    public double getY() { return Y; };
    public Shape getShape() { return shape; }

    abstract boolean isVertical();
    protected double getSplit() {
        return vertical ? X : Y;
    }

    public int compareTo(TreeNode other) {
        double cmp = isVertical() ? getX() - other.getX() : getY() - other.getY();
        if (cmp > 0) {
            return 1;
        } else if (cmp < 0) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "(" + X + "," + Y + ")";
    }
}
