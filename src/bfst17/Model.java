package bfst17;

import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * Created by trold on 2/1/17.
 */
public class Model extends Observable implements Serializable {
	private EnumMap<WayType, List<Shape>> shapes = new EnumMap<>(WayType.class); {
		for (WayType type : WayType.values()) {
			shapes.put(type, new ArrayList<>());
		}
	}
	private float minlat, minlon, maxlat, maxlon;

	private String[] addressBuilder = new String[4];

	private boolean isAddressNode = false;

	private AddressModel addressModel = new AddressModel();

	private HashMap<String,String> cityMap = new HashMap<String,String>();

	public AddressModel getAddressModel() {return addressModel;}

	public Model(String filename) {
		load(filename);
	}

	public Model() {
		load(this.getClass().getResource("/denmark-latest-free.zip").toString());
	}

	public void add(WayType type, Shape shape) {
		shapes.get(type).add(shape);
		dirty();
	}

	private void dirty() {
		setChanged();
		notifyObservers();
	}

	public Iterable<Shape> get(WayType type) {
		return shapes.get(type);
	}

	public void save(String filename) {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
			out.writeObject(shapes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void load(String filename) {
		if (filename.endsWith(".osm")) {
			loadOSM(new InputSource(filename));
		} else if (filename.endsWith(".zip")) {
			try {
				ZipInputStream zip = new ZipInputStream(new FileInputStream(filename));
				zip.getNextEntry();
				loadOSM(new InputSource(zip));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
				shapes = (EnumMap<WayType,List<Shape>>) in.readObject();
				dirty();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch(ClassCastException e) {
				e.printStackTrace();
			}
			
		}
	}

	private void loadOSM(InputSource source) {
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(new OSMHandler());
			reader.parse(source);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public float getMinLon() {
		return minlon;
	}

	public float getMaxLat() {
		return maxlat;
	}

	public float getMaxLon() {
		return maxlon;
	}

	private class OSMHandler implements ContentHandler {
		Map<Long,OSMNode> idToNode = new HashMap<>();
		Map<Long,OSMWay> idToWay = new HashMap<>();
		Map<OSMNode,OSMWay> coastlines = new HashMap<>();
		OSMWay way;
		OSMRelation relation;
		WayType type;
		private float lonfactor;
		private int minLon;

		@Override
		public void setDocumentLocator(Locator locator) {

		}

		@Override
		public void startDocument() throws SAXException {

		}

		@Override
		public void endDocument() throws SAXException {
			for(String s: cityMap.keySet()){
				addressModel.add(Address.parse(cityMap.get(s)));
				addressModel.add(Address.parse(s + " " + cityMap.get(s)));
			}
		}

		@Override
		public void startPrefixMapping(String prefix, String uri) throws SAXException {

		}

		@Override
		public void endPrefixMapping(String prefix) throws SAXException {

		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

			switch(qName) {
				case "bounds":
					minlat = Float.parseFloat(atts.getValue("minlat"));
					minlon = Float.parseFloat(atts.getValue("minlon"));
					maxlat = Float.parseFloat(atts.getValue("maxlat"));
					maxlon = Float.parseFloat(atts.getValue("maxlon"));
					float avglat = minlat + (maxlat - minlat) / 2;
					lonfactor = (float) Math.cos(avglat / 180 * Math.PI);
					minlon *= lonfactor;
					maxlon *= lonfactor;
					minlat = -minlat;
					maxlat = -maxlat;
					break;
				case "node":
					long id = Long.parseLong(atts.getValue("id"));
					float lat = Float.parseFloat(atts.getValue("lat"));
					float lon = Float.parseFloat(atts.getValue("lon"));
					idToNode.put(id, new OSMNode(lonfactor * lon, -lat));
					break;
				case "way":
					way = new OSMWay();
					id = Long.parseLong(atts.getValue("id"));
					type = WayType.UNKNOWN;
					idToWay.put(id, way);
					break;
				case "relation":
					relation = new OSMRelation();
					type = WayType.UNKNOWN;
					break;
				case "nd":
					long ref = Long.parseLong(atts.getValue("ref"));
					way.add(idToNode.get(ref));
					break;
				case "tag":
					String k = atts.getValue("k");
					String v = atts.getValue("v");
					switch (k) {


						case "addr:street":
							addressBuilder[0] = v;
							isAddressNode = true;
							break;
						case "addr:housenumber":
							addressBuilder[1] = v;
							isAddressNode = true;
							break;
						case "addr:postcode":
							addressBuilder[2] = v;
							isAddressNode = true;
							break;
						case "addr:city":
							addressBuilder[3] = v;
							isAddressNode = true;
							break;
						case "highway":
							type = WayType.ROAD;
							switch(v) {
								case "primary":
									type=WayType.HIGHWAY;
									break;
								case "secondary":
									type=WayType.HIGHWAY;
									break;
								case "footway":
									type=WayType.FOOTWAY;
									break;
								case "cycleway":
									type=WayType.FOOTWAY;
									break;
								case "service":
									type=WayType.SERVICE;
									break;
							}
							break;
						case "building":
							type = WayType.BUILDING;
							break;
						case "amenity":
							switch (v){
								case "parking":
									type = WayType.PARKING;
									break;
								case "university":
									type = WayType.UNIVERSITY;
									break;
							}

							break;
						case "natural":
							switch (v){
								case "water":
									type = WayType.WATER;
									break;
								case "coastline":
									type = WayType.COASTLINE;
									break;
							}
							break;
						case "landuse":
							type = WayType.LANDUSE;
							switch (v) {
								case "grass":
									type = WayType.GRASS;
								break;
								case "forest":
									type = WayType.GRASS;
								break;
								case "industrial":
									type = WayType.INDUSTRIAL;
								break;
								case "construction":
									type = WayType.CONSTRUCTION;
								break;
								case "brownfield":
									type = WayType.CONSTRUCTION;
								break;
							}
							break;
						case "route":
							type = WayType.ROUTE;
							if (v.equals("ferry")) {
								type = WayType.FERRYROUTE;
							}
							break;
						case "leisure":
							if (v.equals("park")) {
								type = WayType.PARK;
							}
							break;
					}
					break;
				case "member":
					ref = Long.parseLong(atts.getValue("ref"));
					relation.add(idToWay.get(ref));
					break;
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			switch (qName) {
				case "node":
					if(isAddressNode == true) {
                        for(int i = 0; i < addressBuilder.length; i++){
                            if(addressBuilder[i] == null){addressBuilder[i] = "";}
                        }
						String address = addressBuilder[0] + " " +  addressBuilder[1] + ", " + addressBuilder[2] + " " + addressBuilder[3];
						cityMap.put(addressBuilder[2],addressBuilder[3]);
						addressModel.add(Address.parse(address));
						isAddressNode = false;
					}
				break;
				case "way":
					if (type == WayType.COASTLINE) {
						OSMWay before = coastlines.remove(way.getFromNode());
						OSMWay after = coastlines.remove(way.getToNode());
						OSMWay merged = new OSMWay();
						if (before != null) {
							merged.addAll(before.subList(0, before.size()-1));
						}
						merged.addAll(way);
						if (after != null) {
							merged.addAll(after.subList(1, after.size()));
						}
						coastlines.put(merged.getFromNode(), merged);
						coastlines.put(merged.getToNode(), merged);
					} else {
						add(type, way.toPath2D());
					}
					break;
				case "relation":
					add(type, relation.toPath2D());
					break;
				case "osm":
					coastlines.forEach((key, way) -> {
						if (key == way.getFromNode()) {
							add(WayType.COASTLINE, way.toPath2D());
						}
					});
					break;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {

		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

		}

		@Override
		public void processingInstruction(String target, String data) throws SAXException {

		}

		@Override
		public void skippedEntity(String name) throws SAXException {

		}
	}
}
