package bfst17;

import java.awt.*;

/**
 * Created by trold on 2/15/17.
 */

public enum WayType {
	NATURAL_COASTLINE
			(new Color(234, 234, 234), //Fuck it!
					null,
					FillType.SOLID),
	NATURAL_GRASSLAND
			(new Color(198,228,180),
					null,
					FillType.SOLID),
	LANDUSE_GRASS
			(new Color(197,236,148),
					null,
					FillType.SOLID),
	LANDUSE_RESIDENTIAL
			(new Color(218,218,218),
					null,
					FillType.SOLID),
	LANDUSE_COMMERCIAL
			(new Color(238,207,207),
					null,
					FillType.SOLID),
	LANDUSE_RETAIL
			(new Color(254,202,197),
					null,
					FillType.SOLID),
	LANDUSE_INDUSTRIAL
			(new Color(230,209,227),
					null,
					FillType.SOLID),
	LANDUSE_RAILWAY
			(new Color(230,209,227),
					null,
					FillType.SOLID),
	LANDUSE_CEMETERY
			(new Color(170,202,174),
					null,
					FillType.SOLID),
	LEISURE_GOLF_COURSE
			(new Color(181,226,181),
					null,
					FillType.SOLID),
	NATURAL_WATER
			(new Color(181,208,208),
					null,
					FillType.SOLID),
	LEISURE_SWIMMING_POOL
			(new Color(181,208,208),
					null,
					FillType.SOLID),
	NATURAL_WOOD
			(new Color(157,202,138),
					null,
					FillType.SOLID),
	NATURAL_SCRUB
			(new Color(181,227,181),
					null,
					FillType.SOLID),
	NATURAL_HEATH
			(new Color(214,217,159),
					null,
					FillType.SOLID),
	NATURAL_SAND
			(new Color(241,229,184),
					null,
					FillType.SOLID),
	NATURAL_BEACH
			(new Color(255,240,169),
					null,
					FillType.SOLID),
	NATURAL_BARE_ROCK
			(new Color(233,225,217),
					null,
					FillType.SOLID),
	NATURAL_SCREE
			(new Color(237,228,220),
					null,
					FillType.SOLID),
	NATURAL_SHINGLE
			(new Color(237,228,220),
					null,
					FillType.SOLID),
	NATURAL_WETLAND
			(new Color(255, 255, 255),
					null,
					FillType.SOLID),
	WETLAND_WET_MEADOW
			(new Color(229,229,229),
					null,
					FillType.SOLID),
	WETLAND_FEN
			(new Color(229,229,229),
					null,
					FillType.SOLID),
	WETLAND_SALTMARSH
			(new Color(229,229,229),
					null,
					FillType.SOLID),
	WETLAND_REEDBED
			(new Color(108,180,247),
					null,
					FillType.SOLID),
	WETLAND_SWAMP
			(new Color(174,209,160),
					null,
					FillType.SOLID),
	WETLAND_MARSH
			(new Color(108,180,247),
					null,
					FillType.SOLID),
	WETLAND_BOG
			(new Color(213,216,159),
					null,
					FillType.SOLID),
	WETLAND_STRING_BOG
			(new Color(213,216,159),
					null,
					FillType.SOLID),
	NATURAL_GLACIER
			(new Color(221,236,236),
					null,
					FillType.SOLID),
	AMENITY_GRAVE_YARD
			(new Color(170,202,174),
					null,
					FillType.SOLID),
	RELIGION_CHRISTIAN
			(new Color(170,202,174),
					null,
					FillType.SOLID),
	LANDUSE_BROWNFIELD
			(new Color(167,168,126),
					null,
					FillType.SOLID),
	LANDUSE_CONSTRUCTION
			(new Color(167,168,126),
					null,
					FillType.SOLID),
	LANDUSE_LANDFILL
			(new Color(167,168,126),
					null,
					FillType.SOLID),
	AEROWAY_TERMINAL
			(new Color(204,152,255),
					null,
					FillType.SOLID),
	AMENITY_PARKING
			(new Color(246,238,182),
					null,
					FillType.SOLID),
	AEROWAY_APRON
			(new Color(233,209,255),
					null,
					FillType.SOLID),
	LANDUSE_FARM
			(new Color(224,208,180),
					null,
					FillType.SOLID),
	LANDUSE_FARMYARD
			(new Color(218,187,143),
					null,
					FillType.SOLID),
	LANDUSE_ALLOTMENTS
			(new Color(238,207,179),
					null,
					FillType.SOLID),
	LANDUSE_ORCHARD
			(new Color(158,220,144),
					null,
					FillType.SOLID),
	LANDUSE_FOREST
			(new Color(157,202,138),
					null,
					FillType.SOLID),
	LANDUSE_VINEYARD
			(new Color(158,220,144),
					null,
					FillType.SOLID),
	LANDUSE_QUARRY
			(new Color(183,181,181),
					null,
					FillType.SOLID),
	POWER_SUBSTATION
			(new Color(186,186,186),
					null,
					FillType.SOLID),
	POWER_PLANT
			(new Color(186,186,186),
					null,
					FillType.SOLID),
	POWER_GENERATOR
			(new Color(186,186,186),
					null,
					FillType.SOLID),
	LEISURE_PARK
			(new Color(205,247,201),
					null,
					FillType.SOLID),
	NATURAL_MEADOW
			(new Color(197,236,148),
					null,
					FillType.SOLID),
	LANDUSE_RECREATION_GROUND
			(new Color(197,236,148),
					null,
					FillType.SOLID),
	LANDUSE_VILLAGE_GREEN
			(new Color(197,236,148),
					null,
					FillType.SOLID),
	LEISURE_GARDEN
			(new Color(197,236,148),
					null,
					FillType.SOLID),
	LEISURE_PLAYGROUND
			(new Color(204,255,241),
					null,
					FillType.SOLID),
	LEISURE_DOG_PARK
			(new Color(223,252,226),
					null,
					FillType.SOLID),
	LEISURE_SPORTS_CENTRE
			(new Color(38,198,134),
					null,
					FillType.SOLID),
	LEISURE_STADIUM
			(new Color(38,198,134),
					null,
					FillType.SOLID),
	LEISURE_PITCH
			(new Color(138,211,175),
					null,
					FillType.SOLID),
	LEISURE_TRACK
			(new Color(189,227,203),
					null,
					FillType.SOLID),
	LEISURE_MINIATURE_GOLF
			(new Color(181,226,181),
					null,
					FillType.SOLID),
	AMENITY_SCHOOL
			(new Color(240,241,215),
					null,
					FillType.SOLID),
	AMENITY_KINDERGARTEN
			(new Color(240,241,215),
					null,
					FillType.SOLID),
	AMENITY_COLLEGE
			(new Color(240,241,215),
					null,
					FillType.SOLID),
	AMENITY_UNIVERSITY
			(new Color(240,241,215),
					null,
					FillType.SOLID),
	AMENITY_HOSPITAL
			(new Color(240,241,215),
					null,
					FillType.SOLID),
	AMENITY_PRISON
			(new Color(228,226,221),
					null,
					FillType.SOLID),
	AMENITY_PLACE_OF_WORSHIP
			(new Color(174,156,140),
					null,
					FillType.SOLID),
	LANDUSE_MILITARY
			(new Color(240,209,203),
					null,
					FillType.SOLID),
	MILITARY_DANGER_AREA
			(new Color(252,198,201),
					null,
					FillType.SOLID),
	BUILDING
			(new Color(217, 208, 201),
					null,
					FillType.SOLID),
	WATERWAY_DRAIN
			(new Color(181, 208, 208),
					null,
					FillType.LINE),
	HIGHWAY_MOTORWAY
			(new Color(213,18,88),
					null,
					FillType.LINE),
	HIGHWAY_TRUNK
			(new Color(191,62,35),
					null,
					FillType.LINE),
	HIGHWAY_PRIMARY
			(new Color(154,105,8),
					null,
					FillType.LINE),
	HIGHWAY_SECONDARY
			(new Color(105,121,0),
					null,
					FillType.LINE),
	HIGHWAY_TERTIARY
			(new Color(157,157,157),
					null,
					FillType.LINE),
	HIGHWAY_UNCLASSIFIED
			(new Color(246, 239, 239),
					null,
					FillType.LINE),
	HIGHWAY_RESIDENTIAL
			(new Color(246, 239, 239),
					new BasicStroke(0.00004f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL),
					FillType.LINE),
	HIGHWAY_SERVICE
			(new Color(246, 239, 239),
					null,
					FillType.LINE),
	HIGHWAY_LIVING_STREET
			(new Color(237, 237, 237),
					null,
					FillType.LINE),
	HIGHWAY_PEDESTRIAN
			(new Color(150,148,149),
					null,
					FillType.LINE),
	HIGHWAY_BRIDLEWAY
			(new Color(221,232,217),
					null,
					FillType.LINE),
	HIGHWAY_CYCLEWAY
			(new Color(172,171,245),
					null,
					FillType.LINE),
	HIGHWAY_FOOTWAY
			(new Color(246,222,216),
					new BasicStroke(0.000008f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10f, new float[]{0.00003f}, 0),
					FillType.LINE),
	HIGHWAY_PATH
			(new Color(246,222,216),
					null,
					FillType.LINE),
	HIGHWAY_STEPS
			(new Color(249,104,92),
					null,
					FillType.LINE),
	HIGHWAY_TRACK
			(new Color(216,188,134),
					null,
					FillType.LINE),
	TRACKTYPE_GRADE1
			(new Color(216,188,134),
					null,
					FillType.LINE),
	TRACKTYPE_GRADE2
			(new Color(180,145,73),
					null,
					FillType.LINE),
	TRACKTYPE_GRADE3
			(new Color(237,230,216),
					null,
					FillType.LINE),
	TRACKTYPE_GRADE4
			(new Color(236,230,216),
					null,
					FillType.LINE),
	TRACKTYPE_GRADE5
			(new Color(209,188,146),
					null,
					FillType.LINE),
	HIGHWAY_BUS_GUIDEWAY
			(new Color(157,156,246),
					null,
					FillType.LINE),
	HIGHWAY_RACEWAY
			(new Color(255,192,202),
					null,
					FillType.LINE),
	HIGHWAY_ROAD
			(new Color(181,181,180),
					null,
					FillType.LINE),
	RAILWAY_RAIL
			(new Color(153,153,153),
					null,
					FillType.LINE),
	RAILWAY_TRAM
			(new Color(245,245,245),
					null,
					FillType.LINE),
	RAILWAY_MINIATURE
			(new Color(194,247,187),
					null,
					FillType.LINE),
	RAILWAY_DISUSED
			(new Color(225,222,215),
					null,
					FillType.LINE),
	AERIALWAY_CHAIR_LIFT
			(new Color(128,128,128),
					null,
					FillType.LINE),
	AERIALWAY_DRAG_LIFT
			(new Color(128,128,128),
					null,
					FillType.LINE),
	AERIALWAY_T
			(new Color(128,128,128),
					null,
					FillType.LINE),
	AERIALWAY_J
			(new Color(128,128,128),
					null,
					FillType.LINE),
	AERIALWAY_PLATTER
			(new Color(128,128,128),
					null,
					FillType.LINE),
	AERIALWAY_ROPE_TOW
			(new Color(128,128,128),
					null,
					FillType.LINE),
	AERIALWAY_GONDOLA
			(new Color(128,128,128),
					null,
					FillType.LINE),
	AERIALWAY_CABLE_CAR
			(new Color(128,128,128),
					null,
					FillType.LINE),
	AERIALWAY_GOODS
			(new Color(128,128,128),
					null,
					FillType.LINE),
	ROUTE_FERRY
			(new Color(181,208,208),
					null,
					FillType.LINE),
	POWER_LINE
			(new Color(221,218,213),
					null,
					FillType.LINE),
	POWER_MINOR_LINE
			(new Color(189,187,184),
					null,
					FillType.LINE),
	BARRIER_CITY_WALL
			(new Color(110,110,110),
					null,
					FillType.LINE),
	HISTORIC_CITYWALLS
			(new Color(110,110,110),
					null,
					FillType.LINE),
	BARRIER_RETAINING_WALL
			(new Color(172,171,167),
					null,
					FillType.LINE),
	BARRIER_WALL
			(new Color(172,171,167),
					null,
					FillType.LINE),
	BARRIER_FENCE
			(new Color(172,171,167),
					null,
					FillType.LINE),
	BARRIER_CHAIN
			(new Color(172,171,167),
					null,
					FillType.LINE),
	BARRIER_GUARD_RAIL
			(new Color(172,171,167),
					null,
					FillType.LINE),
	BARRIER_HANDRAIL
			(new Color(172,171,167),
					null,
					FillType.LINE),
	BARRIER_DITCH
			(new Color(172,171,167),
					null,
					FillType.LINE),
	BARRIER_KERB
			(new Color(172,171,167),
					null,
					FillType.LINE),
	BARRIER_HEDGE
			(new Color(174,209,160),
					null,
					FillType.LINE),
	NATURAL_TREE_ROW
			(new Color(169,206,161),
					null,
					FillType.LINE),
	BOUNDARY_ADMINISTRATIVE
			(new Color(207,155,203),
					null,
					FillType.LINE),
	ADMIN_LEVEL_2
			(new Color(207,155,203),
					null,
					FillType.LINE),
	ADMIN_LEVEL_3
			(new Color(207,155,203),
					null,
					FillType.LINE),
	ADMIN_LEVEL_4
			(new Color(207,155,203),
					null,
					FillType.LINE),
	ADMIN_LEVEL_5
			(new Color(208,154,204),
					null,
					FillType.LINE),
	ADMIN_LEVEL_6
			(new Color(208,154,204),
					null,
					FillType.LINE),
	ADMIN_LEVEL_7
			(new Color(208,154,204),
					null,
					FillType.LINE),
	ADMIN_LEVEL_8
			(new Color(208,154,204),
					null,
					FillType.LINE),
	ADMIN_LEVEL_9
			(new Color(208,154,204),
					null,
					FillType.LINE),
	ADMIN_LEVEL_10
			(new Color(208,154,204),
					null,
					FillType.LINE),
	BOUNDARY_NATIONAL_PARK
			(new Color(230,233,222),
					null,
					FillType.LINE),
	MAN_MADE_PIER
			(new Color(250, 157, 154),
					null,
					FillType.LINE),
	UNKNOWN
			(new Color(250, 255, 0,0),
					null,
					FillType.LINE)
	;


	private final Color drawColor;
	private final Stroke drawStroke;

	public FillType getFillType() {
		return fillType;
	}

	private final FillType fillType;

	WayType(Color drawColor, Stroke drawStroke, FillType fillType)
	{
		if(drawStroke==null){
			drawStroke=new BasicStroke(0.00002f);
		}
		this.fillType=fillType;
		this.drawColor = drawColor;
		this.drawStroke = drawStroke;
	}

	public Color getDrawColor()
	{
		return drawColor;
	}
	public Stroke getDrawStroke()
	{
		return drawStroke;
	}


}
