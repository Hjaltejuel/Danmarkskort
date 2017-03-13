package bfst17;

import java.awt.*;

/**
 * Created by trold on 2/15/17.
 */

public enum WayType {
	GRASS
			(new Color(205, 235, 176),
					null,
					FillType.SOLID),
	FOREST
			(new Color(173, 209, 158),
					null,
					FillType.SOLID),
	PARK
			(new Color(200, 250, 204),
					null,
					FillType.SOLID),
	LANDUSE
			(new Color(200, 250, 204),
					null,
					FillType.SOLID),
	INDUSTRIAL
			(new Color(235, 219, 232),
					null,
					FillType.SOLID),
	CONSTRUCTION
			(new Color(199, 199, 180),
					null,
					FillType.SOLID),
	BROWNFIELD
			(new Color(199, 199, 180),
					null,
					FillType.SOLID),
	PARKING
			(new Color(247, 239, 183),
					null,
					FillType.SOLID),
	UNIVERSITY
			(new Color(240, 240, 216),
					null,
					FillType.SOLID),
	ROUTE
			(new Color(134, 138, 222),
					new BasicStroke(0.000008f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 10f, new float[]{0.00008f}, 0),
					FillType.LINE),
	FERRYROUTE
			(new Color(134, 138, 222),
					new BasicStroke(0.000008f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 10f, new float[]{0.00008f}, 0),
					FillType.LINE),
	BUILDING
			(new Color(217, 208, 201),
					null,
					FillType.SOLID),
	ROAD
			(new Color(255, 255, 255),
					new BasicStroke(0.00008f),
					FillType.LINE),
	HIGHWAY
			(new Color(255, 235, 175),
					new BasicStroke(0.00008f),
					FillType.LINE),
	SERVICE
			(new Color(255, 255, 255),
					new BasicStroke(0.00004f),
					FillType.LINE),
	FOOTWAY
			(new Color(197, 132, 129),
					new BasicStroke(0.000008f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10f, new float[]{0.00003f}, 0),
					FillType.LINE),
	UNKNOWN
			(new Color(255, 0, 19, 0), //Fuck it!
					null,
					FillType.LINE),
	WATER
			(new Color(181, 208, 208),
					null,
					FillType.SOLID),
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
			(new Color(189,184,184),
					null,
					FillType.LINE),
	HIGHWAY_RESIDENTIAL
			(new Color(189,184,184),
					null,
					FillType.LINE),
	HIGHWAY_SERVICE
			(new Color(189,184,184),
					null,
					FillType.LINE),
	HIGHWAY_LIVING_STREET
			(new Color(184,184,184),
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
					null,
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
	NATURAL_COASTLINE
			(new Color(234, 234, 234, 0), //Fuck it!
					null,
					FillType.SOLID),
	NATURAL_WATER
			(new Color(181,208,208),
					null,
					FillType.LINE),
	LEISURE_SWIMMING_POOL
			(new Color(181,208,208),
					null,
					FillType.LINE),
	NATURAL_WOOD
			(new Color(157,202,138),
					null,
					FillType.LINE),
	NATURAL_GRASSLAND
			(new Color(198,228,180),
					null,
					FillType.LINE),
	NATURAL_SCRUB
			(new Color(181,227,181),
					null,
					FillType.LINE),
	NATURAL_HEATH
			(new Color(214,217,159),
					null,
					FillType.LINE),
	NATURAL_SAND
			(new Color(241,229,184),
					null,
					FillType.LINE),
	NATURAL_BEACH
			(new Color(255,240,169),
					null,
					FillType.LINE),
	NATURAL_BARE_ROCK
			(new Color(233,225,217),
					null,
					FillType.LINE),
	NATURAL_SCREE
			(new Color(237,228,220),
					null,
					FillType.LINE),
	NATURAL_SHINGLE
			(new Color(237,228,220),
					null,
					FillType.LINE),
	NATURAL_WETLAND
			(new Color(0,0,0),
					null,
					FillType.LINE),
	WETLAND_WET_MEADOW
			(new Color(229,229,229),
					null,
					FillType.LINE),
	WETLAND_FEN
			(new Color(229,229,229),
					null,
					FillType.LINE),
	WETLAND_SALTMARSH
			(new Color(229,229,229),
					null,
					FillType.LINE),
	WETLAND_REEDBED
			(new Color(108,180,247),
					null,
					FillType.LINE),
	WETLAND_SWAMP
			(new Color(174,209,160),
					null,
					FillType.LINE),
	WETLAND_MARSH
			(new Color(108,180,247),
					null,
					FillType.LINE),
	WETLAND_BOG
			(new Color(213,216,159),
					null,
					FillType.LINE),
	WETLAND_STRING_BOG
			(new Color(213,216,159),
					null,
					FillType.LINE),
	NATURAL_GLACIER
			(new Color(221,236,236),
					null,
					FillType.LINE),
	LANDUSE_RESIDENTIAL
			(new Color(218,218,218),
					null,
					FillType.LINE),
	LANDUSE_COMMERCIAL
			(new Color(238,207,207),
					null,
					FillType.LINE),
	LANDUSE_RETAIL
			(new Color(254,202,197),
					null,
					FillType.LINE),
	LANDUSE_INDUSTRIAL
			(new Color(230,209,227),
					null,
					FillType.LINE),
	LANDUSE_RAILWAY
			(new Color(230,209,227),
					null,
					FillType.LINE),
	LANDUSE_CEMETERY
			(new Color(170,202,174),
					null,
					FillType.LINE),
	AMENITY_GRAVE_YARD
			(new Color(170,202,174),
					null,
					FillType.LINE),
	RELIGION_CHRISTIAN
			(new Color(170,202,174),
					null,
					FillType.LINE),
	RELIGION_JEWISH
			(new Color(170,202,174),
					null,
					FillType.LINE),
	LANDUSE_BROWNFIELD
			(new Color(167,168,126),
					null,
					FillType.LINE),
	LANDUSE_CONSTRUCTION
			(new Color(167,168,126),
					null,
					FillType.LINE),
	LANDUSE_LANDFILL
			(new Color(167,168,126),
					null,
					FillType.LINE),
	AEROWAY_TERMINAL
			(new Color(204,152,255),
					null,
					FillType.LINE),
	AMENITY_PARKING
			(new Color(246,238,182),
					null,
					FillType.LINE),
	AEROWAY_APRON
			(new Color(233,209,255),
					null,
					FillType.LINE),
	LANDUSE_FARM
			(new Color(224,208,180),
					null,
					FillType.LINE),
	LANDUSE_FARMYARD
			(new Color(218,187,143),
					null,
					FillType.LINE),
	LANDUSE_ALLOTMENTS
			(new Color(238,207,179),
					null,
					FillType.LINE),
	LANDUSE_ORCHARD
			(new Color(158,220,144),
					null,
					FillType.LINE),
	LANDUSE_FOREST
			(new Color(157,202,138),
					null,
					FillType.LINE),
	LANDUSE_VINEYARD
			(new Color(158,220,144),
					null,
					FillType.LINE),
	LANDUSE_QUARRY
			(new Color(183,181,181),
					null,
					FillType.LINE),
	POWER_SUBSTATION
			(new Color(186,186,186),
					null,
					FillType.LINE),
	POWER_PLANT
			(new Color(186,186,186),
					null,
					FillType.LINE),
	POWER_GENERATOR
			(new Color(186,186,186),
					null,
					FillType.LINE),
	LEISURE_PARK
			(new Color(205,247,201),
					null,
					FillType.LINE),
	LANDUSE_GRASS
			(new Color(197,236,148),
					null,
					FillType.LINE),
	NATURAL_MEADOW
			(new Color(197,236,148),
					null,
					FillType.LINE),
	LANDUSE_RECREATION_GROUND
			(new Color(197,236,148),
					null,
					FillType.LINE),
	LANDUSE_VILLAGE_GREEN
			(new Color(197,236,148),
					null,
					FillType.LINE),
	LEISURE_GARDEN
			(new Color(197,236,148),
					null,
					FillType.LINE),
	LEISURE_PLAYGROUND
			(new Color(204,255,241),
					null,
					FillType.LINE),
	LEISURE_DOG_PARK
			(new Color(223,252,226),
					null,
					FillType.LINE),
	LEISURE_SPORTS_CENTRE
			(new Color(38,198,134),
					null,
					FillType.LINE),
	LEISURE_STADIUM
			(new Color(38,198,134),
					null,
					FillType.LINE),
	LEISURE_PITCH
			(new Color(138,211,175),
					null,
					FillType.LINE),
	LEISURE_TRACK
			(new Color(189,227,203),
					null,
					FillType.LINE),
	LEISURE_GOLF_COURSE
			(new Color(181,226,181),
					null,
					FillType.LINE),
	LEISURE_MINIATURE_GOLF
			(new Color(181,226,181),
					null,
					FillType.LINE),
	AMENITY_SCHOOL
			(new Color(240,241,215),
					null,
					FillType.LINE),
	AMENITY_KINDERGARTEN
			(new Color(240,241,215),
					null,
					FillType.LINE),
	AMENITY_COLLEGE
			(new Color(240,241,215),
					null,
					FillType.LINE),
	AMENITY_UNIVERSITY
			(new Color(240,241,215),
					null,
					FillType.LINE),
	AMENITY_HOSPITAL
			(new Color(240,241,215),
					null,
					FillType.LINE),
	AMENITY_PRISON
			(new Color(228,226,221),
					null,
					FillType.LINE),
	AMENITY_PLACE_OF_WORSHIP
			(new Color(174,156,140),
					null,
					FillType.LINE),
	LANDUSE_MILITARY
			(new Color(240,209,203),
					null,
					FillType.LINE),
	MILITARY_DANGER_AREA
			(new Color(252,198,201),
					null,
					FillType.LINE),

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
