package bfst17.OSMData;

import bfst17.AddressHandling.TSTInterface;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by trold on 2/15/17.
 */
public class OSMNode extends Point2D.Float implements TSTInterface {
	public OSMNode(float x, float y) {
		super(x, y);
	}

	public OSMNode(Point2D p) {
		super((float)p.getX(), (float)p.getY());
	}

	@Override
	public Shape getShape() {
		return null;
	}

	@Override
	public String getAddress() {
		return null;
	}
}

