package bfst17;

import bfst17.AddressHandling.*;
import bfst17.Directions.DirectionObject;
import bfst17.Directions.Graph;
import bfst17.Directions.GraphNode;
import bfst17.Enums.*;
import bfst17.KDTrees.*;
import bfst17.OSMData.*;
import bfst17.ShapeStructure.MultiPolygonApprox;
import bfst17.ShapeStructure.PolygonApprox;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;
import sun.reflect.generics.tree.Tree;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;


//FIXME LAV BIN SAVE TIL GRAPH
public class Model extends Observable implements Serializable {
    private Address.Builder addressBuilder = new Address.Builder();

    private EnumMap<WayType, List<Shape>> shapes = new EnumMap<>(WayType.class);
    private HashMap<String, WayType> namesToWayTypes = new HashMap<>();
    private HashMap<String, HashSet<Point2D>> pointsOfInterest = new HashMap<>();

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


    public ArrayList<Line2D> startStopPunkter = new ArrayList<>();
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

        public RoadKDTree.RoadTreeNode getRoadName(Point2D point1, Point2D point2, VehicleType vehicle) {
        ArrayList<TreeNode> roadsOnPoint1 = getAllClosestRoads(point1, vehicle);
        ArrayList<TreeNode> roadsOnPoint2 = getAllClosestRoads(point2, vehicle);
        for(TreeNode t1 : roadsOnPoint1) {
            for(TreeNode t2 : roadsOnPoint2) {
                if(t1==t2){
                    //System.out.println(((RoadKDTree.RoadTreeNode)t1).getRoadName()+ " " +((RoadKDTree.RoadTreeNode)t1).getRoadName());
                }
            }
        }
        return null;
    }

    /**
     * Får de veje der ligger tættest på punktet i samtlige RoadKDTræer
     * @param point     Punktet
     * @return          Arraylist af veje
     */
    public ArrayList<TreeNode> getAllClosestRoads(Point2D point, VehicleType vehicle) {
        ArrayList<TreeNode> roadNodes = new ArrayList<>();
        for (int i = getRoadKDTreeList().size()-1; i>=0; i--) {
            RoadKDTree tree = getRoadKDTreeList().get(i);
            TreeNode trNode=null;
            if (vehicle == VehicleType.ANY) {
                trNode = tree.getNearestNeighbour(point);
            } else {
                if (vehicleSupportsType(tree.getType(), vehicle)) {
                    trNode = tree.getNearestNeighbour(point);
                }
            }
            if (trNode!=null && trNode.distance(point) <= 0.0005) {
                roadNodes.add(trNode);
            }
        }
        if(roadNodes.size()==0){
            //System.out.println("Ingen roadNodes");
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
        catch (Exception e) {
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

    public ArrayList<DirectionObject> getDirectionsList() {
        if(directions==null) {
            directions=new ArrayList<>();
        }
        return directions;
    }

    public boolean nextNodeHasSameRoadName(DirectionObject currentNode, DirectionObject nextNode, VehicleType vehicleType) {
        //System.out.println("BEGYND!");
        for(TreeNode curNode : getAllClosestRoads(currentNode.getLocation(), vehicleType)) {
            for (TreeNode nexNode : getAllClosestRoads(nextNode.getLocation(), vehicleType)) {
                String currentRoadName = ((RoadKDTree.RoadTreeNode)curNode).getRoadName();
                String nextRoadName = ((RoadKDTree.RoadTreeNode)nexNode).getRoadName();
                //System.out.println(currentRoadName + " "+ nextRoadName);
                if(currentRoadName.length()==0 || nextRoadName.length() == 0){ continue; }
                if (currentRoadName.equals(nextRoadName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Description: Lav vejvisning ud fra shortestPath hvis den er lavet.
     * Description: HVIS shortest path eksisterer: ArrayListe af DirectionObjekter, der indeholder vejvisningsinformation
     * Description: HVIS ikke shortest path exist: En tom ArrayListe
     * @return
     *
     */
    ArrayList<DirectionObject> directions;
    public void calculateDirectionsList() {
        double currentDirection = 0;
        ArrayList<GraphNode> graphNodeList = graph.getPathList();
        if (graphNodeList == null) {
            return;
        }

        for (int i = graphNodeList.size() - 1; i >= 1; i--) {
            GraphNode currentGraphNode = graphNodeList.get(i);
            GraphNode nextGraphNode = graphNodeList.get(i - 1);
            double angle = Math.atan2(nextGraphNode.getPoint2D().getY() - currentGraphNode.getPoint2D().getY(),
                    nextGraphNode.getPoint2D().getX() - currentGraphNode.getPoint2D().getX());
            DirectionObject DirObj = new DirectionObject(currentGraphNode.getPoint2D(), this, VehicleType.CAR, currentDirection - angle);
            directions.add(DirObj);
            currentDirection = angle;
        }
        Collections.reverse(directions);

        for (int i = 0; i < directions.size() - 1; i++) {
            DirectionObject DirObj = directions.get(i);
            if (DirObj.getRoadDirection() != RoadDirektion.lige_ud) {
                DirObj.setVisible(true);

                if (nextNodeHasSameRoadName(DirObj, directions.get(i + 1), VehicleType.CAR)) {
                    //Ad <Vej>
                    DirObj.nextRoad = DirObj.getCurrentRoad();
                } else {
                    //Mod <Vej>
                    DirObj.nextRoad = directions.get(i + 1).getCurrentRoad();
                }
                //DirObj.setRoadName(nextGraphNode.getPoint2D(),this, VehicleType.CAR);
            } else {
                DirObj.nextRoad=DirObj.getCurrentRoad();
            }

            /*
            GraphNode currentGraphNode = graphNodeList.get(i);
            GraphNode nextGraphNode = graphNodeList.get(i - 1);
            double angle = Math.atan2(nextGraphNode.getPoint2D().getY() - currentGraphNode.getPoint2D().getY(),
                    nextGraphNode.getPoint2D().getX() - currentGraphNode.getPoint2D().getX());

            DirectionObject DirObj = new DirectionObject(currentGraphNode.getPoint2D(), this, VehicleType.CAR, currentDirection - angle);

            getRoadName(currentGraphNode.getPoint2D(), nextGraphNode.getPoint2D(),VehicleType.CAR);

            if(DirObj.getRoadDirection()!=RoadDirektion.lige_ud) {
                //DirObj.setRoadName(nextGraphNode.getPoint2D(),this, VehicleType.CAR);
                directions.add(DirObj);
            }

            currentDirection = angle;
        }
        for (int i = 1; i < directions.size(); i++) {
            DirectionObject prevDirobj = directions.get(i - 1);
            DirectionObject currentDirobj = directions.get(i);

            if (prevDirobj.getRoadDirection() == RoadDirektion.lige_ud && prevDirobj.getCurrentRoad().equals(currentDirobj.getCurrentRoad())) {
                directions.remove(i - 1);
                i = 1;
            }

            if (directions.size() > i + 1) {
                DirectionObject nextDirobj = directions.get(i + 1);
                if (nextDirobj.getCurrentRoad().equals(prevDirobj.getCurrentRoad())) {
                    directions.remove(i);
                    i = 1;
                }
            }
        }

        for (int i = 1; i < directions.size(); i++) {
            directions.get(i - 1).calculationRoadLength(directions.get(i));
        }
        */
            //directions.get(0).calculationRoadLength(directions.get(0));
        }
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
        private HashMap<WayType, ArrayList<RoadNode>> roads = new HashMap<>();

        private LongToPointMap idToNode = new LongToPointMap(22);
        private Map<Long, OSMWay> idToWay = new HashMap<>();
        private float lat;
        private float lon;
        private OSMWay way;
        private OSMRelation relation;
        private WayType type;
        private Pattern pattern = Pattern.compile(("^-?\\d+$"));
        ArrayList<Long> tmpNodeIDs = new ArrayList<>();


        private String roadName;
        private boolean isHighway = false;
        private boolean roundAbout = false;
        private boolean oneway = false;
        private PointsOfInterest POIType;
        private Integer totalDepth = 0, totalShapes = 0;
        private int maxSpeed = 0;
        private HashMap<Long, GraphNode> idToGraphNode = new HashMap<>();

        private HashMap<String, Enum<?>> stringToEnum = new HashMap<>();
        {
            for (WayType type : WayType.values()) {
                stringToEnum.put(type.name(), type);
            }
            for (PointsOfInterest type : PointsOfInterest.values()) {
                stringToEnum.put(type.name(), type);
            }
        }
        public HashMap<Long,GraphNode> getIdToGraphNode(){
            return idToGraphNode;
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
         */
        public void addRoad(PolygonApprox shape, String roadName, ArrayList<GraphNode> nodes){
            if (roads.get(type) == null) {
                roads.put(type, new ArrayList<>());
            }
            roads.get(type).add(new RoadNode(shape, roadName, nodes));
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

            graph = new Graph(idToGraphNode);
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {

        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {

        }

        OSMElement currentElementType;
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
                    currentElementType = OSMElement.NODE;
                    nodeID = Long.parseLong(atts.getValue("id"));
                    lat = Float.parseFloat(atts.getValue("lat"));
                    lon = Float.parseFloat(atts.getValue("lon"));
                    idToNode.put(nodeID, lonfactor * lon, -lat);
                    POIType = PointsOfInterest.UNKNOWN;
                    type = WayType.UNKNOWN;
                    break;
                case "way":
                    currentElementType = OSMElement.WAY;
                    way = new OSMWay();
                    tmpNodeIDs.clear();
                    Long id = Long.parseLong(atts.getValue("id"));
                    type = WayType.UNKNOWN;
                    idToWay.put(id, way);
                    break;
                case "relation":
                    currentElementType = OSMElement.RELATION;
                    relation = new OSMRelation();
                    type = WayType.UNKNOWN;
                    break;
                case "nd":
                    long ref = Long.parseLong(atts.getValue("ref"));
                    way.add(new OSMNode(idToNode.get(ref)));
                    tmpNodeIDs.add(ref);

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
                            Matcher matcher = pattern.matcher(v);
                            if (matcher.matches()) {
                                maxSpeed = Integer.parseInt(v);
                            }
                            break;
                        case "oneway":
                            if (isHighway) {
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
                        case "junction":
                            if(v=="roundabout"){
                                roundAbout=true;
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
                        if (idToNode.get(ref) == null) {
                            regionCenter = new OSMNode((maxlon + minlon) / 2, -(maxlat + minlat) / 2);
                        } else {
                            regionCenter = idToNode.get(ref);
                        }
                        adminRelation = true;
                    }
                    OSMWay way = idToWay.get(ref);
                    if (way != null) {
                        if(relation.size()!=0) {
                            if (relation.get(relation.size() - 1).getToNode().getX() != way.getFromNode().getX()) {
                                Collections.reverse(way);
                            }
                        }
                        relation.add(way);
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
                    currentElementType=OSMElement.NONE;
                    break;
                case "way":
                    if (type != WayType.NATURAL_COASTLINE && type != WayType.UNKNOWN) {
                        PolygonApprox shape = new PolygonApprox(way);
                        if (isHighway) { //Hvis vejen er en highway
                            try {
                                RoadTypes roadType = RoadTypes.valueOf(type.toString());
                                ArrayList<GraphNode> nodes = new ArrayList<>();
                                for (int i = 0; i < way.size(); i++) {
                                  GraphNode node = idToGraphNode.get(tmpNodeIDs.get(i));
                                    if (node == null) {
                                        GraphNode gNode = new GraphNode(way.get(i), roadType, maxSpeed);
                                        idToGraphNode.put(tmpNodeIDs.get(i), gNode);
                                        nodes.add(gNode);
                                        if(oneway) {
                                            if (i == 0) {
                                            } else {
                                                idToGraphNode.get(tmpNodeIDs.get(i - 1)).addEdge(gNode);
                                            }
                                        } else {
                                            if(i == 0){
                                            } else {
                                                idToGraphNode.get(tmpNodeIDs.get(i-1)).addEdge(gNode);
                                                gNode.addEdge(idToGraphNode.get(tmpNodeIDs.get(i-1)));
                                            }
                                        }
                                        } else {
                                        nodes.add(node);
                                        if(oneway) {
                                            if (i == 0) {
                                            } else {
                                                idToGraphNode.get(tmpNodeIDs.get(i - 1)).addEdge(node);
                                            }
                                        } else {
                                            if(i == 0){} else {
                                                idToGraphNode.get(tmpNodeIDs.get(i-1)).addEdge(node);
                                                node.addEdge(idToGraphNode.get(tmpNodeIDs.get(i)));
                                            }
                                        }
                                        }
                                    }
                                addRoad(shape, name, nodes); //Tilføj vej
                            } catch (Exception e) {

                            }
                        }
                        addShape(type, shape); //Tilføj shape
                    }
                    name="";
                    maxSpeed = 0;
                    oneway = false;
                    isHighway = false;
                    roundAbout = false;
                    currentElementType=OSMElement.NONE;
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
                    currentElementType=OSMElement.NONE;
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