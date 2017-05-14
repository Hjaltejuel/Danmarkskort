package bfst17;

import bfst17.AddressHandling.*;
import bfst17.Directions.*;
import bfst17.AddressHandling.Address;
import bfst17.AddressHandling.AddressModel;
import bfst17.AddressHandling.Region;
import bfst17.AddressHandling.StreetAndPointNode;
import bfst17.Directions.DirectionObject;
import bfst17.Directions.Edge;
import bfst17.Directions.Graph;
import bfst17.Directions.GraphNode;
import bfst17.Enums.*;
import bfst17.KDTrees.*;
import bfst17.OSMData.*;
import bfst17.ShapeStructure.MultiPolygonApprox;
import bfst17.ShapeStructure.PolygonApprox;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

/**
 * Created by trold on 2/1/17.
 */
//FIXME LAV BIN SAVE TIL GRAPH
public class Model extends Observable implements Serializable {
    private Address.Builder addressBuilder = new Address.Builder();

    private EnumMap<WayType, List<Shape>> shapes = new EnumMap<>(WayType.class);
    private HashMap<String, WayType> namesToWayTypes = new HashMap<>();
    private HashMap<String, HashSet<Point2D>> pointsOfInterest = new HashMap<>();
    private HashMap<WayType, ArrayList<RoadNode>> roads = new HashMap<>();

    private ArrayList<Shape> coastlines = new ArrayList<>();

    private CityNamesKDTree cityTree = new CityNamesKDTree();
    private POIKDTree POITree = new POIKDTree();
    private ArrayList<ShapeKDTree> treeList = new ArrayList<>();
    private ArrayList<RoadKDTree> roadKDTreeList = new ArrayList<>();
    private CityNamesKDTree townTree = new CityNamesKDTree();

    String name = "";
    Point2D regionCenter = null;
    boolean adminRelation = false;
    private boolean isAddressNode = false;
    private AddressModel addressModel = new AddressModel();

    private float minlat, minlon, maxlat, maxlon;
    private long nodeID;
    private float lonfactor;
    private Graph graph;

    public Model(String filename) throws IOException {
        load(filename);
    }

    public Model() {
        //Til osm
        try {
            load(System.getProperty("user.dir") + "/resources/bornholm.osm");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //til bin
        //String path = System.getProperty("user.dir") + "/resources/kastrup.bin";
        loadAllCoastlines();
        //loadFile(path);
    }

    /**
     * Får de veje der ligger tættest på punktet i samtlige RoadKDTræer
     * @param point     Punktet
     * @return          Arraylist af veje
     */
    public ArrayList<TreeNode> getAllClosestRoads(Point2D point, VehicleType vehicle) {
        ArrayList<TreeNode> roadNodes = new ArrayList<>();
        for (RoadKDTree tree : getRoadKDTreeList()) {
            if (vehicle == VehicleType.ANY) {
                roadNodes.add(tree.getNearestNeighbour(point));
            } else {
                if (vehicleSupportsType(tree.getType(), vehicle)) {
                    roadNodes.add(tree.getNearestNeighbour(point));
                }
            }
        }
        return roadNodes;
    }

    /**
     * Check if the WayType supports the given vehicle
     * @param type          Type of road
     * @param vehicle       Type of vehicle
     * @return              Whether the vehicle can drive on the given type of road (Boolean)
     */
    public boolean vehicleSupportsType(WayType type, VehicleType vehicle){
        try {
            for (VehicleType vType : RoadTypes.valueOf(type.name()).getVehicletypes()) {
                if (vType == vehicle) {
                    return true;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Description: Løber igennem alle roadKDtræerne for at finde et nearestNeightbour for alle træerne, hvor den korteste vælges.
     * @param point
     * @return RoadTreeNode
     */
    public RoadKDTree.RoadTreeNode getClosestRoad(Point2D point, VehicleType vehicleType) {
        TreeNode closestNode = null;
        for (TreeNode node : getAllClosestRoads(point, vehicleType)) {
            if (closestNode == null) {
                closestNode = node;
            } else {
                if (node.distance(point) < closestNode.distance(point)) {
                    closestNode = node;
                }
            }
        }
        return (RoadKDTree.RoadTreeNode) closestNode;
    }

    /**
     * Description: Lav vejvisning ud fra shortestPath hvis den er lavet.
     * Description: HVIS shortest path eksisterer: ArrayListe af DirectionObjekter, der indeholder vejvisningsinformation
     * Description: HVIS ikke shortest path exist: En tom ArrayListe
     * @return
     *
     */
    ArrayList<DirectionObject> directions;
    public ArrayList<DirectionObject> getDirectionsList() {
        if (directions != null) {
            return directions;
        }
        directions = new ArrayList<>();
        String prevRoad = "";
        double currentDirection = 0;
        ArrayList<GraphNode> graphNodeList = graph.getPathList();
        if (graphNodeList == null) {
            return directions;
        }
        for (int i = graphNodeList.size() - 1; i >= 1; i--) {
            GraphNode currentGraphNode = graphNodeList.get(i);
            if (currentGraphNode.getEdgeList().size() <= 2) {
                if (!prevRoad.equals(currentGraphNode.getRoadName(this))) {
                    DirectionObject DirObj = new DirectionObject(currentGraphNode.getPoint2D(), this, VehicleType.CAR);
                    prevRoad = DirObj.getCurrentRoad();
                    directions.add(DirObj);
                }
                continue;
            }
            if (currentGraphNode.getEdgeList().size() > 2) {
                String currentRoadName = currentGraphNode.getRoadName(this);
                if ((i - 1) > 0) {
                    GraphNode nextGraphNode = graphNodeList.get(i - 1);
                    boolean erSideVej = false;
                    for (Edge edge : currentGraphNode.getEdgeList()) {
                        if (edge.getDestination() != nextGraphNode) {
                            if (edge.getDestination().getRoadName(this).equals(currentRoadName)) {
                                erSideVej = true;
                                break;
                            }
                        }
                    }
                    if (erSideVej) {
                        continue;
                    }
                }
            }
            if (currentGraphNode.getRoadName(this).equals(prevRoad)) {
                continue;
            }
            DirectionObject DirObj = new DirectionObject(currentGraphNode.getPoint2D(), this, VehicleType.CAR);
            prevRoad = DirObj.getCurrentRoad();
            directions.add(DirObj);

            /*
            GraphNode prevNode = graphNodeList.get(i-1);
            double angle = Math.atan2(currentGraphNode.getPoint2D().getY() - prevNode.getPoint2D().getY(),
                    currentGraphNode.getPoint2D().getX() - prevNode.getPoint2D().getX());
            currentDirection = ((angle*180/Math.PI)+360)%360; //Altid mere en 0 & under 360
            */
        }
        return directions;
    }

    /**
     * Description: Returnere en ArrayList med RoadKD-træer
     * @return ArrayList<RoadKDTree>
     */
    public ArrayList<RoadKDTree> getRoadKDTreeList() {
        return roadKDTreeList;
    }

    /**
     * Description: Returnere en ArrayList med ShapeKD-træer
     * @return ArrayList<ShapeKDTree>
     */
    public ArrayList<ShapeKDTree> getTrees() {
        return treeList;
    }

    /**
     * Description: Returnere et POIKD-træ
     * @return POIKDTree
     */
    public POIKDTree getPOITree() {
        return POITree;
    }

    /**
     * Description: Returnere et CityNamesKDTree-træ
     * @return CityNamesKDTree
     */
    public CityNamesKDTree getCityTree() {
        return cityTree;
    }
    /**
     * Description: Returnerer et CityNamesKD-træ - indeholdende towns (mindre by end city)
     * @return POIKDTree
     */
    public CityNamesKDTree getTownTreeTree() {
        return townTree;
    }

    /**
     * Description: Returnere et AddressModel objekt
     * @return AddressModel
     */
    public AddressModel getAddressModel() {
        return addressModel;
    }

    /**
     * Description: Tilføjer entres for hver type POI i pointsOfInterest og WayType i namesToWayTypes og i shapes.
     * Description:
     */
    public void initializeHashmaps() {
        for (PointsOfInterest type : PointsOfInterest.values()) {
            pointsOfInterest.put(type.name(), new HashSet<>());
        }
        for (WayType type : WayType.values()) {
            namesToWayTypes.put(type.name(), type);
        }
        for (WayType type : WayType.values()) {
            shapes.put(type, new ArrayList<>());
        }
    }

    /**
     * Description: Tilføjer en entry til HashMappet shapes.
     * @param type
     * @param shape
     */
    public void add(WayType type, Shape shape) {
        shapes.get(type).add(shape);
        dirty();
    }

    private void dirty() {
        setChanged();
        notifyObservers();
    }

    /**
     * Description: Opretter en bin-fil med de forskellige KD-træ objekter, addressModel og min/max koordinaterne kortet har.
     * @param filename
     */
    public void save(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            //Ryk rundt på dem her og få med Jens' knytnæve at bestille
            System.out.println("Saving trees");
            out.writeObject(treeList);
            out.writeObject(roadKDTreeList);
            out.writeObject(POITree);
            out.writeObject(cityTree);
            out.writeObject(townTree);
            System.out.println("Saving adressModel");
            out.writeObject(addressModel);
            System.out.println("Saving coordinates");
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

    /**
     * Description: Returerne den nuværende tid i sekunder.
     * @return double
     */
    public double currentTimeInSeconds() {
        return System.nanoTime() / 1_000_000_000d;
    }

    /**
     * Description: Håndtere loading af en fil.
     * Description: Udskriver parsing informationer.
     * @param filename
     * @throws IOException
     */
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
            initializeHashmaps();
            this.addressModel = new AddressModel();
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
            loadBin(input);
        }
        dirty();

        progressPrinter.cancel();
        int loadTime = (int) Math.round(currentTimeInSeconds() - startTime);
        System.out.printf("\nLoad time: %d:%02d\n", loadTime / 60, loadTime % 60);
    }

    /**
     * Description: Loader objekterne fra bin filen
     * @param input
     */
    private void loadBin(BufferedInputStream input){
        try (ObjectInputStream in = new ObjectInputStream(input)) {
            //Ryk rundt på dem her og få med Jens' knytnæve at bestille
            System.out.println("Loading Trees");
            treeList = (ArrayList<ShapeKDTree>) in.readObject();
            roadKDTreeList = (ArrayList<RoadKDTree>) in.readObject();
            POITree = (POIKDTree) in.readObject();
            cityTree = (CityNamesKDTree) in.readObject();
            townTree = (CityNamesKDTree) in.readObject();
            System.out.println("Loading AdressModel");
            addressModel = (AddressModel) in.readObject();
            System.out.println("Loading Coordinates");
            minlon = in.readFloat();
            minlat = in.readFloat();
            maxlon = in.readFloat();
            maxlat = in.readFloat();

            System.out.printf("WE HAVE ACHIEVED: [Object deserialization]\n");
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

    /**
     * Desription: Loader OSM-filer. Laver en ny OSMHandler og parser filen.
     * @param source
     */
    private void loadOSM(InputSource source) {
        try {
            loadAllCoastlines();
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(new OSMHandler());
            reader.parse(source);
            reader.setContentHandler(null);
            reader = null;
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Description: Loader coastlines for hele Danmark ind fra en bin fil.
     */
    public void loadAllCoastlines() {
        String path = System.getProperty("user.dir") + "/resources/dkcoast.bin";
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            coastlines = (ArrayList<Shape>) in.readObject();
            lonfactor = in.readFloat();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Description: Returnerer minimun longitude.
     * @return float
     */
    public float getMinLon() {
        return minlon;
    }

    /**
     * Description: Returnerer minimun lattitude.
     * @return float
     */
    public float getMinLat() {
        return minlat;
    }

    /**
     * Description: Returnerer maximum lattitude.
     * @return float
     */
    public float getMaxLat() {
        return maxlat;
    }

    /**
     * Description: Returnerer maximum longitude.
     * @return float
     */
    public float getMaxLon() {
        return maxlon;
    }

    /**
     * Description: Returnerer ArrayListen der indeholder alle coastlines.
     * @return ArrayList<Shape>
     */
    public ArrayList<Shape> getCoastlines() {
        return coastlines;
    }

    /**
     * Desctription: Returnerer den graf der virker som vejnettet på kortet.
     * @return Graph
     */
    public Graph getGraph() {
        return graph;
    }

    private class OSMHandler implements ContentHandler {
        private LongToPointMap idToNode = new LongToPointMap(22);
        private Map<Long, OSMWay> idToWay = new HashMap<>();
        private HashMap<Point2D, GraphNode> graphNodeBuilder = new HashMap<>();
        private ArrayList<OSMWay> graphWays = new ArrayList<>();
        private float lat;
        private float lon;
        private OSMWay way;
        private OSMRelation relation;
        private WayType type;
        private Pattern pattern = Pattern.compile(("^-?\\d+$"));

        private String roadName;
        private boolean isHighway = false;
        private boolean oneway = false;
        private PointsOfInterest POIType;
        private Integer totalDepth = 0, totalShapes = 0;
        private int maxSpeed = 0;

        private HashMap<String, Enum<?>> stringToEnum = new HashMap<>();
        {
            for (WayType type : WayType.values()) {
                stringToEnum.put(type.name(), type);
            }
            for (PointsOfInterest type : PointsOfInterest.values()) {
                stringToEnum.put(type.name(), type);
            }
        }

        private ArrayList<PointOfInterestObject> pointsOfInterest = new ArrayList<>();

        private ArrayList<StreetAndPointNode> cityNames = new ArrayList<>();

        private ArrayList<StreetAndPointNode> townNames = new ArrayList<>();

        private EnumMap<WayType, List<Shape>> shapes = new EnumMap<>(WayType.class);
        {
            for (WayType type : WayType.values()) {
                shapes.put(type, new ArrayList<>());
            }
        }

        /**
         * Description: Tilføjer et nyt entry til roads hashmappet med WayTypen og og en ny ArrayList, hvis den ikke findes i mappet allerede.
         * @param shape
         * @param roadName
         * @param type
         */
        public void addRoad(PolygonApprox shape, String roadName, WayType type){
            if (roads.get(type) == null) {
                roads.put(type, new ArrayList<>());
            }
            roads.get(type).add(new RoadNode(shape, roadName, type));
        }

        /**
         * Description: Tilføjer et entry til den List som WayTypen har i shapes enummappet.
         * @param type
         * @param shape
         */
        public void addShape(WayType type, Shape shape) {
            shapes.get(type).add(shape);
            dirty();
        }

        @Override
        public void setDocumentLocator(Locator locator) {

        }

        @Override
        public void startDocument() throws SAXException {
        }

        /**
         * Description: Fylder de forskellige træer ud fra de ArrayLister der er blevet fyldt under parsingen.
         * Description: ArrayListerne cleares efter følgende.
         */
        private void fillTrees() {
            treeList = new ArrayList<>();
            POITree = new POIKDTree();
            for (WayType type : WayType.values()) {
                List<Shape> shapeList = shapes.get(type);
                if (shapeList.size() == 0 || type == WayType.UNKNOWN || type == WayType.NATURAL_COASTLINE) {
                    continue;
                }
                if (type.name().split("_")[0].equals("HIGHWAY")) {
                    RoadKDTree tmpRoadKDTree = new RoadKDTree(type);
                    tmpRoadKDTree.fillTree(roads.get(type));
                    roadKDTreeList.add(tmpRoadKDTree);
                    continue;
                }
                //if(type!=WayType.BARRIER_RETAINING_WALL){continue;}
                ShapeKDTree treeWithType = new ShapeKDTree(type);
                treeWithType.fillTree(shapeList);
                treeList.add(treeWithType);
                totalDepth += treeWithType.getMaxDepth();
                totalShapes += treeWithType.getSize();
            }
            POITree.fillTree(pointsOfInterest);
            cityTree.fillTree(cityNames);
            townTree.fillTree(townNames);

            cityNames.clear();
            pointsOfInterest.clear();
            townNames.clear();
            shapes.clear();
            roads.clear();

            System.out.println("totalDepth: " + totalDepth + " total TreeNodes:  " + totalShapes);
        }

        @Override
        public void endDocument() throws SAXException {
            long StartTime = System.nanoTime();
            fillTrees();
            System.out.println("fillTrees() ran in: " + (System.nanoTime() - StartTime) / 1_000_000 + " ms");

            graph = new Graph(graphNodeBuilder, graphWays);


            TSTInterface addressDest = addressModel.getAddress("Aasen 4, 3730 Nexø");
            TSTInterface address = addressModel.getAddress("Engen 1, 3730 Nexø");

            TreeNode closestNode = getClosestRoad(new Point2D.Double(address.getX(), address.getY()), VehicleType.CAR);
            Point2D fromPoint = new Point2D.Double(closestNode.getX(), closestNode.getY());

            closestNode = getClosestRoad(new Point2D.Double(addressDest.getX(), addressDest.getY()), VehicleType.CAR);
            Point2D toPoint = new Point2D.Double(closestNode.getX(), closestNode.getY());

            getGraph().findShortestPath(fromPoint, toPoint, VehicleType.CAR);

            //graphNodeBuilder.clear();
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
                    idToNode.put(nodeID, lonfactor * lon, -lat);
                    POIType = PointsOfInterest.UNKNOWN;
                    type = WayType.UNKNOWN;
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
                    way.add(new OSMNode(idToNode.get(ref)));
                    break;
                case "tag":
                    String k = atts.getValue("k");
                    String v = atts.getValue("v");

                    Enum<?> enumTag = stringToEnum.get(k.toUpperCase() + "_" + v.toUpperCase());
                    if (enumTag != null) {
                        if (enumTag.getClass() == PointsOfInterest.class) {
                            POIType = (PointsOfInterest) enumTag;
                        } else {
                            type = (WayType) enumTag;
                        }
                    }

                    switch (k) {
                        case "highway":
                            isHighway = true;
                            break;
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
                        case "maxspeed":
                            if (isHighway) {
                                Matcher matcher = pattern.matcher(v);
                                if (matcher.matches()) {
                                    maxSpeed = Integer.parseInt(v);
                                }
                            }
                            break;
                        case "oneway":
                            if(isHighway){
                                if (v.equals("yes")) {
                                    oneway = true;
                                }
                            }
                            break;
                        case "name":
                            name = v;
                            if (isHighway) {
                                roadName = v;
                            }
                            break;
                        case "place":
                            if (v.equals("village") || v.equals("town") || v.equals("city")) {
                                addressModel.putCity(name, idToNode.get(nodeID));
                            }
                            if (v.equals("town")) {
                                townNames.add(new StreetAndPointNode(name, new Point2D.Double(lon * lonfactor, -lat)));
                            }
                            if (v.equals("city")) {
                                cityNames.add(new StreetAndPointNode(name, new Point2D.Double(lon * lonfactor, -lat)));
                            }
                            break;
                    }
                    break;
                case "member":
                    String role = atts.getValue("role");
                    ref = Long.parseLong(atts.getValue("ref"));
                    if (role.equals("admin_centre")) {
                        if (idToNode.get(ref)==null) {
                            regionCenter = new OSMNode((maxlon + minlon) / 2, -(maxlat + minlat) / 2);
                        } else {regionCenter =  idToNode.get(ref);}
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
                    if (POIType != PointsOfInterest.UNKNOWN) {
                        PointOfInterestObject POIObj = new PointOfInterestObject(POIType, lon * lonfactor, -lat);
                        pointsOfInterest.add(POIObj);
                    }
                    break;
                case "way":
                    if (type != WayType.NATURAL_COASTLINE && type != WayType.UNKNOWN) {
                        PolygonApprox shape = new PolygonApprox(way);
                        if (type.toString().split("_")[0].equals("HIGHWAY")) { //Hvis vejen er en highway
                            addRoad(shape, name, type); //Tilføj vej

                            graphWays.add(way);
                            for (int i = 0; i < way.size(); i++) {
                                if (!graphNodeBuilder.containsKey(way.get(i))) {
                                    RoadTypes roadType = RoadTypes.valueOf(type.toString());
                                    graphNodeBuilder.put(way.get(i), new GraphNode(way.get(i), roadType, oneway, maxSpeed));
                                }
                            }
                        }
                        addShape(type, shape); //Tilføj shape
                    }
                    maxSpeed = 0;
                    oneway = false;
                    isHighway = false;
                    break;
                case "relation":
                    if (relation.size() != 0) {
                        MultiPolygonApprox path = new MultiPolygonApprox(relation);
                        if (adminRelation == true) {
                            addressModel.putRegion(name, new Region(path, regionCenter));
                            adminRelation = false;
                        } else {
                            addShape(type, path);
                        }
                    }
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