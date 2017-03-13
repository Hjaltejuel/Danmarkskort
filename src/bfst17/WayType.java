package bfst17;

import java.awt.*;

/**
 * Created by trold on 2/15/17.
 */

public enum WayType {
	COASTLINE
			(new Color(234, 234, 234), //Fuck it!
					null,
					FillType.SOLID,
					10),

	GRASS
			(new Color(205, 235, 176),
					null,
					FillType.SOLID,
					10),
	FOREST
			(new Color(173, 209, 158),
					null,
					FillType.SOLID,
					10),
	PARK
			(new Color(200, 250, 204),
					null,
					FillType.SOLID,
					10),
	LANDUSE
			(new Color(200, 250, 204),
					null,
					FillType.SOLID,
					10),
	INDUSTRIAL
			(new Color(235, 219, 232),
					null,
					FillType.SOLID,
					10),
	CONSTRUCTION
			(new Color(199, 199, 180),
					null,
					FillType.SOLID,
					10),
	BROWNFIELD
			(new Color(199, 199, 180),
					null,
					FillType.SOLID,
					10),
	PARKING
			(new Color(247, 239, 183),
					null,
					FillType.SOLID,
					10),
	UNIVERSITY
			(new Color(240, 240, 216),
					null,
					FillType.SOLID,
					10),
	ROUTE
			(new Color(134, 138, 222),
					new BasicStroke(0.000008f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 10f, new float[]{0.00008f}, 0),
					FillType.LINE,
					10),
	FERRYROUTE
			(new Color(134, 138, 222),
					new BasicStroke(0.000008f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 10f, new float[]{0.00008f}, 0),
					FillType.LINE,
					10),
	BUILDING
			(new Color(217, 208, 201),
					null,
					FillType.SOLID,
					135000),
	ROAD
			(new Color(255, 255, 255),
					new BasicStroke(0.00008f),
					FillType.LINE,
					10),
	HIGHWAY
			(new Color(255, 235, 175),
					new BasicStroke(0.00008f),
					FillType.LINE,
					10),
	SERVICE
			(new Color(255, 255, 255),
					new BasicStroke(0.00004f),
					FillType.LINE,
					10),
	FOOTWAY
			(new Color(197, 132, 129),
					new BasicStroke(0.000008f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10f, new float[]{0.00003f}, 0),
					FillType.LINE,
					10),
	UNKNOWN
			(new Color(255, 0, 19, 0), //Fuck it!
					null,
					FillType.LINE,
					10),
	WATER
			(new Color(181, 208, 208),
					null,
					FillType.SOLID,
					10)
	;


	private final Color drawColor;
	private final Stroke drawStroke;
	private final double zoomFactor;

	public FillType getFillType() {
		return fillType;
	}

	private final FillType fillType;

	WayType(Color drawColor, Stroke drawStroke, FillType fillType, double zoomFactor)
	{
		if(drawStroke==null){
			drawStroke=new BasicStroke(0.00002f);
		}
		this.fillType=fillType;
		this.drawColor = drawColor;
		this.drawStroke = drawStroke;
		this.zoomFactor = zoomFactor;
	}

	public Color getDrawColor()
	{
		return drawColor;
	}
	public Stroke getDrawStroke()
	{
		return drawStroke;
	}


	public double getZoomFactor() {
		return zoomFactor;
	}
}
