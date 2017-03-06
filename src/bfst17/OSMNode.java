package bfst17;

/**
 * Created by trold on 2/15/17.
 */
public class OSMNode {
	float lon, lat;

	public float getLon() {
		return lon;
	}

	public float getLat() {
		return lat;
	}

	public OSMNode(float lon, float lat) {
		this.lon = lon;
		this.lat = lat;
	}
}
