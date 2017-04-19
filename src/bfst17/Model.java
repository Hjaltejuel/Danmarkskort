package bfst17;

import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * Created by trold on 2/1/17.
 */
public class Model extends Observable implements Serializable {
    private HashMap<String,Point2D> addressToCordinate = new HashMap<>();
    private String[] addressBuilder = new String[4];
    private HashSet<String> PostCode = new HashSet<>();
    private boolean isAddressNode = false;
    private AddressModel addressModel = new AddressModel();
    private HashMap<String,WayType> namesToWayTypes = new HashMap<>();{
        for(WayType type: WayType.values()){
            namesToWayTypes.put(type.name(),type);
        }
    }
    private KDTree tree = new KDTree();
    private float minlat, minlon, maxlat, maxlon;
    private long nodeID;
    private ArrayList<Shape> coastlines = new ArrayList<>();

    public ArrayList<Shape> getCoastlines() {
		return coastlines;
    }

    public Model(String filename) {
        load(filename);
    }

    public KDTree getTree() {
        return tree;
    }

    public AddressModel getAddressModel() { return addressModel; }

    public Iterable<Shape> get(WayType type) {
        return shapes.get(type);
    }

    private EnumMap<WayType, List<Shape>> shapes = new EnumMap<>(WayType.class); {
        for (WayType type : WayType.values()) {
            shapes.put(type, new ArrayList<>());
        }
    }

    public Model() {
        load(this.getClass().getResource("/bornholm.osm").getPath());
		/*
        try {
            //load("C:\\Users\\Jens\\Downloads\\denmark-latest.osm");
            load(this.getClass().getResource("/bornholm.osm").getPath());
        } catch (Exception e) {
            System.out.println("Der opstod en fejl ved indlæsning af bornholm.bin");
        }
        */
    }

    public void add(WayType type, Shape shape) {
        shapes.get(type).add(shape);
        dirty();
    }

    private void dirty() {
        setChanged();
        notifyObservers();
    }

    public void save(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            //Ryk rundt på dem her og få med Jens' knytnæve at bestille
            out.writeObject(coastlines);
			/*
			out.writeObject(shapes);
			out.writeObject(addressModel);
			out.writeFloat(minlon);
			out.writeFloat(minlat);
			out.writeFloat(maxlon);
			out.writeFloat(maxlat);
			*/
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void loadAllCoastlines(){
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("/Users/Mads/Desktop/heleDKCL.bin"))) {
			//Ryk rundt på dem her og få med Jens' knytnæve at bestille
			coastlines = (ArrayList<Shape>) in.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
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
                //Ryk rundt på dem her og få med Jens' knytnæve at bestille
                coastlines = (ArrayList<Shape>) in.readObject();	
				shapes = (EnumMap<WayType, List<Shape>>) in.readObject();
				addressModel = (AddressModel) in.readObject();
				minlon = in.readFloat();
				minlat = in.readFloat();
				maxlon = in.readFloat();
				maxlat = in.readFloat();
                tree.fillTree(shapes);

                dirty();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadOSM(InputSource source) {
        try {
            coastlines = new ArrayList<>();
			loadAllCoastlines();
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

    int numnodes=0;
    public float getMinLat() {return minlat;}

    public void addToBounds(float newMaxLat,float newMinLat,float newMaxLon,float newMinLon)
    {
        maxlat += newMaxLat;
        minlat += newMinLat;
        maxlon += newMaxLon;
        minlon += newMinLon;
    }

    private class OSMHandler implements ContentHandler {
        //LongToPointMap idToNode = new LongToPointMap(18000000);
        Map<Long,OSMWay> idToWay = new HashMap<>();
        HashMap<Long, OSMNode> idToNode = new HashMap<>();
        Map<OSMNode,OSMWay> tmpcoastlines = new HashMap<>();
        OSMWay way;
        OSMRelation relation;
        WayType type;
        private float lonfactor;

        @Override
        public void setDocumentLocator(Locator locator) {

        }
		/*
		 public LongToPointMap getIdToNode()
		{
			return idToNode;
		}
		*/

        @Override
        public void startDocument() throws SAXException {

        }

        @Override
        public void endDocument() throws SAXException {
            for(String s: PostCode){
                addressModel.put(s,null);
            }
            //tree.fillTree(shapes);
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
                    numnodes++;
                    if ((numnodes & 0xFFFF) == 0) {
                        System.out.println("Numnodes: " + numnodes);
                    }
                    nodeID = Long.parseLong(atts.getValue("id"));
                    float lat = Float.parseFloat(atts.getValue("lat"));
                    float lon = Float.parseFloat(atts.getValue("lon"));
                    idToNode.put(nodeID, new OSMNode(lonfactor * lon, -lat));
                    break;
                case "way":
                    way = new OSMWay();
                    Long id = Long.parseLong(atts.getValue("id"));
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
                    // Løber waytypes igennem for at se om den matcher med attributes
                    WayType typeTest = namesToWayTypes.get(k.toUpperCase() + "_"+v.toUpperCase());
                    if(typeTest!=null){
                        type = typeTest;
                    }
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
						case "building":
							type = WayType.BUILDING;
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

                    if (isAddressNode == true) {
                        for (int i = 0; i < addressBuilder.length; i++) {
                            if (addressBuilder[i] == null) {
                                addressBuilder[i] = "";
                            }
                        }
                        String address = addressBuilder[0] + " " + addressBuilder[1] + ", " + addressBuilder[2] + " " + addressBuilder[3];
                        PostCode.add(addressBuilder[2] + " " + addressBuilder[3]);
                        PostCode.add(addressBuilder[3]);


                        //LongToPointMap.Node m = (LongToPointMap.Node) idToNode.get(nodeID);
                        //LongToPointMap.Node k = new LongToPointMap.Node(m.key, (float) m.getX(), (float) m.getY(), null);
                        addressModel.put(Address.parse(address).toString(), idToNode.get(nodeID).getPoint2D());
                        isAddressNode = false;
                    }

                    break;
                case "way":

                    if (type == WayType.NATURAL_COASTLINE) {
                        OSMWay before = tmpcoastlines.remove(way.getFromNode());
                        OSMWay after = tmpcoastlines.remove(way.getToNode());
                        OSMWay merged = new OSMWay();
                        if (before != null) {
                            merged.addAll(before.subList(0, before.size() - 1));
                        }
                        merged.addAll(way);
                        if (after != null) {
                            merged.addAll(after.subList(1, after.size()));
                        }
                        tmpcoastlines.put(merged.getFromNode(), merged);
                        tmpcoastlines.put(merged.getToNode(), merged);
                    } /* else {
                        System.out.println("jeeee");
                        add(type, way.toPath2D());
                    }*/

                    break;
                case "relation":
                    add(type, relation.toPath2D());
                    break;
                case "osm":
                    tmpcoastlines.forEach((key, way) -> {
                        if (key == way.getFromNode()) {
                            coastlines.add(way.toPath2D());
                            add(WayType.NATURAL_COASTLINE, way.toPath2D());
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