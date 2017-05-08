package bfst17;

import bfst17.Enums.PointsOfInterest;
import bfst17.Enums.WayType;
import bfst17.KDTrees.CityNamesKDTree;
import bfst17.KDTrees.KDTree;
import bfst17.KDTrees.POIKDTree;
import bfst17.AddressHandling.Address;
import bfst17.AddressHandling.AddressModel;
import bfst17.AddressHandling.Region;
import bfst17.OSMData.OSMNode;
import bfst17.OSMData.OSMRelation;
import bfst17.OSMData.OSMWay;
import bfst17.ShapeStructure.MultiPolygonApprox;
import bfst17.ShapeStructure.PolygonApprox;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * Created by trold on 2/1/17.
 */
public class Model extends Observable implements Serializable {
    private Address.Builder addressBuilder = new Address.Builder();

    String name= "";
    OSMNode regionCenter = null;
    boolean adminRelation = false;

    private boolean isAddressNode = false;
    private AddressModel addressModel = new AddressModel();

    private HashMap<String, WayType> namesToWayTypes = new HashMap<>(); {
        for(WayType type : WayType.values()) {
            namesToWayTypes.put(type.name(),type);
        }
    }

    private HashMap<String, HashSet<Point2D>> pointsOfInterest = new HashMap<>(); {
        for(PointsOfInterest type: PointsOfInterest.values()) {
            pointsOfInterest.put(type.name(),new HashSet<>());
        }
    }

    private HashMap<String, Point2D> cityNames = new HashMap<>();
    private HashMap<String, Point2D> townNames = new HashMap<>();

    private float minlat, minlon, maxlat, maxlon;
    private float clminlat, clminlon, clmaxlat, clmaxlon;
    private long nodeID;
    private ArrayList<Shape> coastlines = new ArrayList<>();
    private float lonfactor;


    public Model(String filename) throws IOException {
        load(filename);
    }

    private ArrayList<KDTree> treeList = new ArrayList<>();
    public ArrayList<KDTree> getTrees() {
        return treeList;
    }
    private POIKDTree POITree = new POIKDTree();
    public POIKDTree getPOITree() {
        return POITree;
    }

    private CityNamesKDTree cityTree = new CityNamesKDTree();
    public CityNamesKDTree getCityTree() { return cityTree; }

    private CityNamesKDTree townTree = new CityNamesKDTree();
    public CityNamesKDTree getTownTreeTree() { return townTree;}

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
        //Til osm
        try {
            //load("C:\\Users\\Jens\\Downloads\\denmark-latest.osm");
            //load("C:\\Users\\Jens\\Downloads\\map (2).osm");
            load(this.getClass().getResource("/bornholm.osm").getPath());
        } catch (Exception e) {

        }
        //til bin
        //String path = System.getProperty("user.dir") + "/resources/kastrup.bin";
        loadAllCoastlines();
        //loadFile(path);
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
            out.writeObject(treeList);
            out.writeObject(POITree);
            out.writeObject(cityTree);
            out.writeObject(townTree);
            out.writeObject(addressModel);
            out.writeFloat(minlon);
            out.writeFloat(minlat);
            out.writeFloat(maxlon);
            out.writeFloat(maxlat);
            out.flush();
            System.out.println("Saved successfully!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double currentTimeInSeconds() {
        return System.nanoTime() / 1_000_000_000d;
    }

    public void load(String filename) throws IOException {
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(filename));
        int total = input.available();
        double startTime = currentTimeInSeconds();
        Timer progressPrinter = new Timer();
        progressPrinter.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            double fractionLeft = input.available() / (double) total;
                            double fractionDone = 1 - fractionLeft;
                            if (fractionLeft < 1) {
                                double secondsUsed = currentTimeInSeconds() - startTime;
                                long secondsLeft = Math.round(secondsUsed / fractionDone * fractionLeft);
                                System.out.printf("\rParsing %.1f%% done, time left: %d:%02d\n", 100 * fractionDone, secondsLeft / 60, secondsLeft % 60);
                            }
                        } catch (IOException e) {
                            System.out.print("Parsing 100% done, time left: 0:00\n");
                            System.out.println("Now drawing shapes, please wait.");
                            progressPrinter.cancel();
                        }
                    }
                }, 0, 1000);

        if (filename.endsWith(".osm")) {
            loadOSM(new InputSource(input));
        } else if (filename.endsWith(".zip")) {
            try {
                ZipInputStream zip = new ZipInputStream(input);
                zip.getNextEntry();
                loadOSM(new InputSource(zip));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (ObjectInputStream in = new ObjectInputStream(input)) {
                //Ryk rundt på dem her og få med Jens' knytnæve at bestille
                treeList = (ArrayList<KDTree>) in.readObject();
                POITree = (POIKDTree) in.readObject();
                cityTree = (CityNamesKDTree) in.readObject();
                townTree = (CityNamesKDTree) in.readObject();
                addressModel = (AddressModel) in.readObject();
                minlon = in.readFloat();
                minlat = in.readFloat();
                maxlon = in.readFloat();
                maxlat = in.readFloat();
                double elapsedTime = currentTimeInSeconds() - startTime;
                System.out.printf("WE HAVE ACHIEVED: [Object deserialization: %f s]\n", elapsedTime);
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
        progressPrinter.cancel();
        int loadTime = (int)Math.round(currentTimeInSeconds() - startTime);
        System.out.printf("\nLoad time: %d:%02d\n", loadTime / 60, loadTime % 60);
    }

    private void fillTrees() {
        treeList = new ArrayList<>();
        POITree = new POIKDTree();

        for (WayType type : WayType.values()) {
            List<Shape> list = shapes.get(type);
            if (list.size() == 0 || type == WayType.UNKNOWN || type == WayType.NATURAL_COASTLINE) {
                continue;
            }
            KDTree treeWithType = new KDTree(type);
            treeWithType.fillTreeWithShapes(list);
            treeList.add(treeWithType);
            //System.out.println("MaxDepth: " + treeWithType.maxDepth + "\t\t\tElement Count:" + treeWithType.count + "\t\t\tType: " + type);
        }
        //System.out.println("Number of trees: "+treeList.size());

        if (pointsOfInterest != null) {
            POITree.fillTree(pointsOfInterest);
        }
        if (cityTree != null) {
            cityTree.fillTree(cityNames);
        }

        if (townTree != null) {
            townTree.fillTree(townNames);
        }

        //Ryd op!
        shapes.clear();
        shapes=null;
    }

    private void loadOSM(InputSource source) {
        try {
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

    public void loadAllCoastlines() {
        String path = System.getProperty("user.dir") + "/resources/dkcoast.bin";
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            //Ryk rundt på dem her og få med Jens' knytnæve at bestille
            coastlines = (ArrayList<Shape>) in.readObject();
            lonfactor = in.readFloat();
            clminlon = in.readFloat();
            clminlat = in.readFloat();
            clmaxlon = in.readFloat();
            clmaxlat = in.readFloat();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public float getMinLon() {
        return minlon;
    }

    public float getMinLat() {
        return minlat;
    }

    public float getMaxLat() {
        return maxlat;
    }

    public float getMaxLon() {
        return maxlon;
    }

    public ArrayList<Shape> getCoastlines() {
        return coastlines;
    }

    private class OSMHandler implements ContentHandler {
        //LongToPointMap idToNode = new LongToPointMap(18000000);
        Map<Long,OSMWay> idToWay = new HashMap<>();
        HashMap<Long, OSMNode> idToNode = new HashMap<>();
        Map<OSMNode,OSMWay> coastlines = new HashMap<>();
        float lat;
        float lon;
        OSMWay way;
        OSMRelation relation;
        WayType type;

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
            fillTrees();
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {

        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {

        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            switch (qName) {
                case "bounds":
                    minlat = Float.parseFloat(atts.getValue("minlat"));
                    minlon = Float.parseFloat(atts.getValue("minlon"));
                    maxlat = Float.parseFloat(atts.getValue("maxlat"));
                    maxlon = Float.parseFloat(atts.getValue("maxlon"));
                    minlon *= lonfactor;
                    maxlon *= lonfactor;
                    break;
                case "node":
                    nodeID = Long.parseLong(atts.getValue("id"));
                    lat = Float.parseFloat(atts.getValue("lat"));
                    lon = Float.parseFloat(atts.getValue("lon"));
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

                    WayType typeTest = namesToWayTypes.get(k.toUpperCase() + "_" + v.toUpperCase());
                    if (typeTest != null) {
                        type = typeTest;
                    } else {
                        HashSet<Point2D> typePointsOfInterest = pointsOfInterest.get(k.toUpperCase() + "_" + v.toUpperCase());
                        if (typePointsOfInterest != null) {
                            pointsOfInterest.get(k.toUpperCase() + "_" + v.toUpperCase()).add(new Point2D.Double(lon * lonfactor, -lat));
                        }
                    }

                    switch (k) {
                        case "addr:street":
                            addressBuilder.street(v);
                            isAddressNode = true;
                            break;
                        case "addr:housenumber":
                            addressBuilder.house(v);
                            isAddressNode = true;
                            break;
                        case "addr:postcode":
                            addressBuilder.postcode(v);
                            isAddressNode = true;
                            break;
                        case "addr:city":
                            addressBuilder.city(v);
                            isAddressNode = true;
                            break;
                        case "building":
                            type = WayType.BUILDING;
                            break;
                        case "name":
                            name = v;
                            break;
                        case "place":
                            if (v.equals("village") || v.equals("town") || v.equals("city")) {
                                addressModel.putCity(name, idToNode.get(nodeID));
                            }
                            if (v.equals("town")) {
                                townNames.put(name, new Point2D.Double(lon * lonfactor, -lat));
                            }
                            if (v.equals("city")) {
                                cityNames.put(name, new Point2D.Double(lon * lonfactor, -lat));
                            }

                    }
                    break;
                case "member":
                    String role = atts.getValue("role");
                    ref = Long.parseLong(atts.getValue("ref"));
                    if (role.equals("admin_centre")) {
                        regionCenter = idToNode.get(ref);
                        if (regionCenter == null) {
                            regionCenter = new OSMNode((maxlon + minlon) / 2, -(maxlat + minlat) / 2);
                        }
                        adminRelation = true;
                    }
                    OSMWay way = idToWay.get(ref);
                    if (way != null) {
                        relation.add(idToWay.get(ref));
                    }
                    break;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (qName) {
                case "node":
                    if (isAddressNode == true) {
                        Address address = addressBuilder.build();
                        addressModel.putAddress(address, idToNode.get(nodeID));
                        isAddressNode = false;
                    }
                    break;
                case "way":
                    if (type == WayType.NATURAL_COASTLINE) {
                        //DO NOTHING
                    } else {
                        add(type, new PolygonApprox(way));
                    }
                    break;
                case "relation":
                    if (relation.size() != 0) {
                        MultiPolygonApprox path = new MultiPolygonApprox(relation);
                        if (adminRelation == true) {
                            addressModel.putRegion(name, new Region(path, regionCenter));
                            adminRelation = false;
                        } else {
                            add(type, path);
                        }
                    }
                    break;
                case "osm":
                    coastlines.forEach((key, way) -> {
                        if (key == way.getFromNode()) {
                            add(WayType.NATURAL_COASTLINE, new PolygonApprox(way));
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