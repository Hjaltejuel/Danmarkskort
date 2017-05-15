package bfst17.OSMData;

import bfst17.AddressHandling.TSTInterface;

import java.awt.*;
import java.awt.geom.Point2D;


public class OSMNode extends Point2D.Float implements TSTInterface {
	/**
	 * Opretter et OSMNode objekt ud fra et x og et y koordinat.
	 * @param x
	 * @param y
	 */
	public OSMNode(float x, float y) {
		super(x, y);
	}

	/**
	 * Opretter et OSMNode objekt ud fra et Point2D objekt.
	 * @param p
	 */
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

