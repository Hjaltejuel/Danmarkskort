package bfst17;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Created by trold on 2/15/17.
 */
public class OSMNode extends Point2D.Float implements TSTInterface {
	String address;
	public OSMNode(float x, float y) {
		super(x, y);
		this.address = null;
	}
	public void setAddressName(String address){
		this.address = address;
	}
	public void getAddress(){}

	@Override
	public Shape getShape() {
		return null;
	}
}

