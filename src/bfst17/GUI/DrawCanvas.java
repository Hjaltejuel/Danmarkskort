package bfst17.GUI;

import bfst17.AddressHandling.TSTInterface;
import bfst17.Directions.DirectionObject;
import bfst17.Directions.Graph;
import bfst17.Enums.*;
import bfst17.KDTrees.*;
import bfst17.Model;
import bfst17.RoadNode;
import bfst17.ShapeStructure.PolygonApprox;
import sun.reflect.generics.tree.Tree;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Timer;

/**
 * Created by trold on 2/8/17.
 */
public class DrawCanvas extends JComponent {
    private Model model;
    private AffineTransform transform = new AffineTransform();

	private boolean antiAliasFromMenu; //Bestemmer over antiAliasFromPanning
    private boolean antiAliasFromPanning;
    private boolean needToDrawNearestNeighbour;
    private boolean fancyPanEnabled = false;
    private boolean drawCityNames = true;
    private boolean[] POIToShow = new boolean[POIclasification.values().length];

    private Shape regionShape = null;
	private GUIMode GUITheme = GUIMode.NORMAL;
    private Point2D pin;
    private Integer FrameCounter=0;
    private double timeTracker;
    private Integer FPS=0;
    private Rectangle2D screenRectangle;
    private Timer timer;
    private final float lonToKM = 111.320f;
    private RoadKDTree.RoadTreeNode addressNode;
    private long MapDrawTime;
    private ArrayList<Long> times = new ArrayList<>();
    private HashMap<String, BufferedImage> PinAndPOIImageMap;

    public DrawCanvas(Model model) {
		this.model = model;
		loadImages();
	}

    /**
     * Indlæs billeder så det kun behøver gøres én gang.
     */
	private void loadImages() {
        PinAndPOIImageMap = new HashMap<>();
        try{
            BufferedImage img = ImageIO.read(getClass().getResource("/PinImage.png"));
            PinAndPOIImageMap.put("pin", img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            for (PointsOfInterest POI : PointsOfInterest.values()){
                if(POI==PointsOfInterest.UNKNOWN){continue;}
                BufferedImage img = ImageIO.read(getClass().getResource("/POI/" + POI.name() + ".png"));
                PinAndPOIImageMap.put(POI.name(), img);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Beskrivelse: Returnerer et GUI thema (nightmode, greyscale)
     * @return GUIMode
     */
	public GUIMode getGUITheme() {
        return GUITheme;
    }

    /**
     * Beskrivelse: Slår antiAliasing til og fra
     * Beskrivelse: Metoden kaldes fra AntiAliasing fra menuen.
     */
    public void toggleAA() {
        antiAliasFromMenu = !antiAliasFromMenu;
        repaint();
        revalidate();
    }

    //De næste metoder er til at slå aa fra hvis der pannes på og fra når der stoppes.
    //Den før kan ikke bruges da det giver den modsatte værdi. Hvis AA nu er false bliver den sat til true når der pannes og det skal den ikke.
    /**
     * Beskrivelse: Slår antiAliasing fra.
     */
    public void AAOff(){
        antiAliasFromPanning = false;
        repaint();
        revalidate();
    }

    /**
     * Beskrivelse: Slår antiAliasing til hvis det er slået til fra menu.
     * Beskrivelse: Det bruges til slå AA til når der pannes, men kun hvis du er slået til fra menuen.
     */
    public void AAOn() {
        if(antiAliasFromMenu) {
            antiAliasFromPanning = true;
            repaint();
            revalidate();
        }
    }

    /**
     * Beskrivelse: Viser om FancyPan er slået til
     * @return boolean
     */
    public boolean isFancyPanEnabled() {
        return fancyPanEnabled;
    }

    /**
     * Beskrivelse: Slår FancyPan til eller fra.
     */
    public void toggleFancyPan() {
        fancyPanEnabled = !fancyPanEnabled;
    }

    /**
     * Vælger hvilke points of interests der skal vises, vha. et boolean array
     * @param name - hvilken type af POI det er
     */
    public void setPointsOfInterest(POIclasification name) {
        Integer EnumIndex = name.ordinal();
        POIToShow[EnumIndex]=!POIToShow[EnumIndex];
        repaint();
    }

    /**
     * Placér den blå pin på den givne adresse / region
     * @param address  Den adresse pinnen skal placeres på
     */
    public void setPin(TSTInterface address) {
        regionShape = address.getShape();
        pin = new Point2D.Double(-address.getX(), -address.getY());
    }

    /**
     * Beskrivelse: Ændrer farvetemaet af GUI'en
     * @param newTheme the new theme enum
     */
    public void setGUITheme(GUIMode newTheme) {
	    GUITheme = newTheme;
    }

    /**
     * Beskrivelse: Opdaterer FPScounter ved at sætte et timestamp og tælle hvor mange gange en counter når at stige på et sekund.
     */
    public void checkFPS() {
        FrameCounter++;
        if(System.nanoTime()-timeTracker >= 1_000_000_000) {
            FPS = FrameCounter;
            timeTracker = System.nanoTime();
            FrameCounter = 0;
        }
    }

    /**
     * Beskrivelse PaintComponent metoden, står for tegningen af elementer under repaint
     * @param _g
     */
	@Override
	protected void paintComponent(Graphics _g) {
        Graphics2D g = (Graphics2D) _g;

        //Definér skærmbilledet
        Point2D topLeft = screenCordsToLonLat(0, 0);
        Point2D topRight = screenCordsToLonLat(getWidth(), getHeight());
        screenRectangle = new Rectangle2D.Double(topLeft.getX(), topLeft.getY(), topRight.getX() - topLeft.getX(), topRight.getY() - topLeft.getY());
        MapDrawTime = System.nanoTime();
        //Tegn kortet
        drawMap(g);
        MapDrawTime = System.nanoTime()-MapDrawTime;
        times.add(MapDrawTime);

        //Tegn overlay (Pin, POI, Målebånd, FPS)
        drawOverlay(g);
	}

    //<editor-fold desc="Funktioner der tegner">

    /**
     * Beskrivelse: Kalder alle de funktioner der tegner overlayet (information oven på kortet).
     * @param g
     */
    private void drawOverlay(Graphics2D g) {
        //Reset transform til billeder / overlay
        g.setTransform(new AffineTransform());

        drawPin(g);

        drawPointsOfInterest(g);

        drawMeasureBand(g);

        drawFPSCounter(g);

        if (needToDrawNearestNeighbour) {
            drawClosestRoadName(g);
        }

        if(drawCityNames) {
            drawCityAndTownNames(g);
        }

        for(DirectionObject DirObj: model.getDirectionsList()) {
            Point2D drawLocation = lonLatToScreenCords(-DirObj.getLocation().getX(), -DirObj.getLocation().getY());
            g.drawString(DirObj.toString(),(float)drawLocation.getX(),(float)drawLocation.getY());
        }
    }

    /**
     * Beskrivelse: Tegner et billede af en pin.
     * Beskrivelse: Bruges når der søges på by/vejnavn/region.
     * @param g
     */
    public void drawPin(Graphics2D g) {
        if (pin == null) {
            return; //Lad være at tegne, hvis der ikke er en pin
        }
        drawImageAtLocation(g,"pin",pin.getX(),pin.getY());
    }

    /**
     * Beskrivelse: Finder den nærmeste addressNode udfra musen lonlat koordinater. AddressNode bliver opdateret.
     * Beskrivelse: Hvis distancen til addressNode er for stor skal der ikke vises nogen og needToDrawNearestNeighbour sættes til false.
     * @param mousePos
     */
    public void getNearestNeighbourFromMousePos(Point2D mousePos) {
        Point2D lonLatCords = screenCordsToLonLat(mousePos.getX(), mousePos.getY());
        addressNode = model.getClosestRoad(lonLatCords, VehicleType.ANY);
        if(addressNode == null){
            return;
        }
       //Vi vil ikke vise nearestNeighbour hvis musen er for langt væk fra en vertex. Hvis distancen er over 0.01 i latlon koordinater vises ingen nearestNeighbour
        if (addressNode.distance(lonLatCords) > 0.01) {
            needToDrawNearestNeighbour = false;
        } else {
            needToDrawNearestNeighbour = true;
        }
        repaint();
    }



    /**
     * Beskrivelse: Tegner det vejnavn der er tættest på musemakøren, nede i højre hjørne
     * @param g
     */
    public void drawClosestRoadName(Graphics2D g) {
        if(addressNode!=null) {
            String nearestNeighbourText = addressNode.getRoadName();

            int textWidth = g.getFontMetrics().stringWidth(nearestNeighbourText);

            //Boksen omkring teksten.
            Integer Y = getHeight() - 50;
            Integer X1 = getWidth()-textWidth-39, X2=getWidth()-35;
            Rectangle2D rect = new Rectangle2D.Double(X1,Y-13,X2-X1,13);
            Line2D line = new Line2D.Double(X1,Y,X2,Y);
            Line2D rightVertLine = new Line2D.Double(X2,Y-13,X2,Y);
            Line2D leftVertLine = new Line2D.Double(X1,Y-13,X1,Y);

            g.setColor(new Color(255,255,255,100));
            g.fill(rect);

            g.setColor(Color.black);
            g.draw(line);
            g.draw(rightVertLine);
            g.draw(leftVertLine);

            g.drawString(nearestNeighbourText, getWidth()-35-textWidth, getHeight()-53);
        }
    }

    /**
     * Beskrivelse: Tegn Points Of Interest billederne, hvis zoomFactor tillader det
     * @param g
     */
    public void drawPointsOfInterest(Graphics2D g) {
        if (getZoomFactor() > 40000) {
            POIKDTree POITree = model.getPOITree();
            for (TreeNode node : POITree.getInRange(screenRectangle)) {
                POIKDTree.POITreeNode POINode = (POIKDTree.POITreeNode)node;
                PointsOfInterest POIType = POINode.getPOIType();
                if (POIToShow[POIType.getClassification().ordinal()]) {
                    String imagePath = POIType.name();
                    drawImageAtLocation(g, imagePath, -POINode.getX(), -POINode.getY());
                }
            }
        }
    }

    /**
     * Beskrivelse: Tegn bynavne, hvis zoomFactor tillader det
     * @param g
     */
    public void drawCityAndTownNames(Graphics2D g) {
        try {
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(
                    "/Gravity-Regular.otf")));
        } catch (IOException |FontFormatException e) {
            //Handle exception
        }
        g.setFont(new Font("Gravity-Regular", Font.PLAIN, 20));

        //Draw townnames
        if (getZoomFactor() > 3000 && getZoomFactor() < 9000) {
            CityNamesKDTree townTree = model.getTownTreeTree();
            for (TreeNode townNode : townTree.getInRange(screenRectangle)) {
                CityNamesKDTree.CityNameTreeNode node = (CityNamesKDTree.CityNameTreeNode) townNode;
                drawCityAndRoadNodeNames(g,node,node.getCityName());
            }
        }

        //Draw citynames
        if (getZoomFactor() < 400 && getZoomFactor() > 180) {
            CityNamesKDTree cityTree = model.getCityTree();
            for (TreeNode cityNode : cityTree.getInRange(screenRectangle)) {
                CityNamesKDTree.CityNameTreeNode node = (CityNamesKDTree.CityNameTreeNode) cityNode;
                drawCityAndRoadNodeNames(g,node,node.getCityName());
            }
        }
    }
    public void drawCityAndRoadNodeNames(Graphics2D g, TreeNode node, String name){
        TreeNode cityNode =  node;
        String cityName = name;
        Point2D drawLocation = lonLatToScreenCords(-cityNode.getX(), -cityNode.getY());
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        int stringWidth = g.getFontMetrics().stringWidth(cityName);
        g.drawString(cityName, (int) drawLocation.getX() - stringWidth / 2, (int) drawLocation.getY());
    }

    /**
     * Beskrivelse: Tegn shortest path
     * @param g
     */
    public void drawGraph(Graphics2D g) {
        g.setColor(Color.BLACK);

        g.setStroke(new BasicStroke(0.000004f));
        Graph graph = model.getGraph();
        if (graph == null) {
            return;
        } else {
            ArrayList<Point2D> graphPointList = graph.getPointList();
            if (graphPointList != null) {
                if(graphPointList.size() != 0) {
                    PolygonApprox polygon = new PolygonApprox(graphPointList);
                    g.draw(polygon);
                }
            }
        }
    }

    /**
     * Beskrivelse: Tegner et billede på kortet ud fra lonlat koordinater og en path til billedet som passer til et billede i et HashMap
     * @param g Det givne graphicsobjekt givet fra paintComponent overriden
     * @param imagePath
     * @param x
     * @param y
     */
    public void drawImageAtLocation(Graphics2D g, String imagePath, double x, double y) {
        BufferedImage image = PinAndPOIImageMap.get(imagePath);
        Rectangle2D imageRect = new Rectangle2D.Double(-x, -y, image.getWidth() / getZoomFactor(), image.getHeight() / getZoomFactor());

        if (!screenRectangle.intersects(imageRect)) {
            return; //Billedet er ikke inden for skærmen
        }
        Point2D drawLocation = lonLatToScreenCords(x, y);
        g.drawImage(image, (int) drawLocation.getX() - image.getWidth() / 2, (int) drawLocation.getY() - image.getHeight(), this);
    }

    /**
     * Beskrivelse: Omregner to pixelkoordinater til lonlatkoordinater og herefter udregner afstanden i mellem dem til km.
     * Beskrivelse: Tegner GUI til at vise afstandsmåleren.
      * @param g Det givne graphicsobjekt givet fra paintComponent overriden
     */
    private void drawMeasureBand(Graphics2D g) {
	    g.setColor(Color.black);
	    g.setStroke(new BasicStroke(1f));
	    Integer Y = getHeight() - 75;
	    Integer X1 = getWidth()-100, X2=getWidth()-35;

	    Point2D p1 = screenCordsToLonLat(X1, Y);
        Point2D p2 = screenCordsToLonLat(X2, Y);

        double distance = (p2.getX()-p1.getX()) * lonToKM;
        String mål = " KM";
        if(distance<1) { //Konvertér til meter
            distance *= 1000;
            mål = " M";
        }

        Integer roundedDistance = (int)Math.round(distance);

        Rectangle2D rect = new Rectangle2D.Double(X1,Y-13,X2-X1,13);
        Line2D line = new Line2D.Double(X1,Y,X2,Y);
        Line2D rightVertLine = new Line2D.Double(X2,Y-13,X2,Y);
        Line2D leftVertLine = new Line2D.Double(X1,Y-13,X1,Y);

        String showString = roundedDistance+mål;
        Integer stringWidth = g.getFontMetrics().stringWidth(showString);

        g.setColor(new Color(255,255,255,100));
        g.fill(rect);
        g.setColor(Color.black);
        g.drawString(showString,X1+(32-stringWidth/2),Y-2);
        g.draw(line);
        g.draw(rightVertLine);
        g.draw(leftVertLine);
    }

    /**
     * Beskrivelse: Opdaterer FPS og derefter tegner FPS counteren
     * @param g Det givne graphicsobjekt givet fra paintComponent overriden
     */
    private void drawFPSCounter(Graphics2D g) {
        checkFPS();
        g.drawString("FPS: " + FPS, 5, getHeight() - 55);
        if (FPS == 0) {
            return;
        }

        long avg = 0;
        for (Long l : times) {
            avg += l / times.size();
        }

        g.drawString("Curr time: " + MapDrawTime/1_000_000, 5, getHeight() - 100);
        g.drawString("Avg  time: " + avg/1_000_000, 5, getHeight() - 115);
    }

    /**
     * Beskrivelse: Tegner hele kortet ved først at farve vand, tegne coastlines, alle shapes, veje, og tegne det korteste vej hvis det er sat.
     * Beskrivelse: Hvis der er søgt efter en region tegnes omridset af denne også.
     * Beskrivelse: Denne funktion bliver kaldt fra paintComponent, hvilket vil sige hver gang der repaintes.
     * @param g Det givne graphicsobjekt givet fra paintComponent overriden
     */
    private void drawMap(Graphics2D g) {
        //Tegn vand
        g.setColor(getDrawColor(WayType.NATURAL_WATER));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setTransform(transform);
        g.setStroke(new BasicStroke(Float.MIN_VALUE));

        if (antiAliasFromMenu && antiAliasFromPanning)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Tegn coastlines
        drawCoastlines(g);

        //Hent og tegn shapes fra diverse KDTræer
        drawShapes(g);

        //Tegn vejene og evt vejnavne
        drawRoads(g);

        drawGraph(g);

/*        g.setColor(Color.black);
        for(Line2D l : model.startStopPunkter){
            g.draw(l);
        }
*/
        //Tegn regionen, hvis der er søgt efter den
        if (regionShape != null) {
            Color color = g.getColor();
            g.setStroke(new BasicStroke((float) 0.001f));
            g.setColor(new Color(255, 0, 0));
            g.draw(regionShape);
            g.setColor(color);
        }
    }

    /**
     * Beskrivelse: Henter en WayTypes drawColor og returner den. Der laves beregninger på farven, hvis NightMode og GreyScale er aktiveret.
     * @param type
     * @return
     */
    private Color getDrawColor(WayType type) {
        Color drawColor = type.getDrawColor();

        if(GUITheme == GUIMode.NIGHT) {
            drawColor = type.getNightModeColor();
        } else if (GUITheme == GUIMode.GREYSCALE) {
            int red = (int) (drawColor.getRed() * 0.299);
            int green = (int) (drawColor.getGreen() * 0.587);
            int blue = (int) (drawColor.getBlue() * 0.114);
            int sum = red + green + blue;
            drawColor = new Color(sum, sum, sum);
        }
        return drawColor;
    }

    /**
     * Beskrivelse: Tegner alle de coastlines der er indenfor skærmbillede
     * @param g Det givne graphicsobjekt givet fra paintComponent overriden
     */
    private void drawCoastlines(Graphics2D g) {
        for(Shape s: model.getCoastlines()) {
            if(!screenRectangle.intersects(s.getBounds2D())) { continue; } //Tegn kun dem indenfor skærmen
            g.setStroke(WayType.NATURAL_COASTLINE.getDrawStroke());
            g.setColor(getDrawColor(WayType.NATURAL_COASTLINE));
            g.fill(s);
        }
    }

    /**
     * Beskrivelse: Undersøger om den specifikke WayType der bliver taget som parameter skal tegnes udfra den nuværende zoomfactor
     * @param roadType
     * @return boolean
     */
    public boolean shouldDrawRoadName(WayType roadType) {
        switch (roadType) {
            default:
                if (getZoomFactor() > 250000) {
                    return true;
                }
        }
        return false;
    }

    /**
     * Beskrivelse: Tegner vejnavne på veje ved hjælp af pathiterator, tegner et vejnavn i center af hvert shape
     * @param g
     * @param roadNode
     */
    public void drawRoadNameInCenter(Graphics2D g, RoadNode roadNode) {
        //laver et float array til xy cord
        float[] coords = new float[2];
        //finder shape og navn på vejen
        String roadName = roadNode.getRoadName();
        PolygonApprox shape = roadNode.getShape();
        //laver en pathIterator
        PathIterator iterator = shape.getPathIterator(new AffineTransform(), 0);
        g.setTransform(new AffineTransform());
        Point2D from = null;
        int i = 0;
        // Løber igennem punkterne
        while (!iterator.isDone()) {
            //sætter cordinaterne
            iterator.currentSegment(coords);
            Point2D drawLocation = lonLatToScreenCords(-coords[0], -coords[1]);
            //hvis vi er i midten så sæt den gamle drawlocation
            if (i == (shape.getLengthOfCoords() / 4) - 1) {
                from = drawLocation;
                //sæt de andre kordinater
            } else if (i == shape.getLengthOfCoords() / 4) {
                //find en vinkel mellem de 2 punkter
                double angle = getAngle(from, drawLocation);
                //Rotér hvis skriften er vendt på hovedet
                if (angle > 1.57079633 || angle < -1.57079633) {
                    angle += Math.PI;
                }
                //find bredden af strengen så vi tegner i midten
                int width = g.getFontMetrics().stringWidth(roadName);
                //finder midtpunktet i xy cord
                int midpointX = (int) ((from.getX() + drawLocation.getX()) / 2);
                int midpointY = (int) ((from.getY() + drawLocation.getY()) / 2);
                //roterer texten rundt om midtpunktet
                g.rotate(angle, midpointX, midpointY);
                //tegner strengen og roterer tilbage
                g.drawString(roadName, (midpointX - width / 2), midpointY);
                g.rotate(-angle, midpointX, midpointY);
                from = drawLocation;
            }
            iterator.next();
            i++;

        }
        g.setTransform(transform);
    }

    /**
     * Beskrivelse: Udregner vinklen mellem to punkter i radianer.
     * @param from
     * @param to
     * @return double
     */
    public double getAngle(Point2D from, Point2D to) {
        double theta = Math.atan2(to.getY()-from.getY(),to.getX()-from.getX());
        return theta;
    }

    /**
     * Beskrivelse: Gennemgår alle road Kd-træer, og undersøger for hver WayType ved hvilket zoomFactor de skal tegnes og tegner dem.
     * De samme Kd-træer bliver gennemgået for at undersøge og tegne vejnavne hvis de skal tegnes.
     * @param g Det givne graphicsobjekt givet fra paintComponent overriden
     */
    public void drawRoads(Graphics2D g) {
        for(RoadKDTree tree : model.getRoadKDTreeList()) {
            WayType type = tree.getType();
            if (type.getZoomFactor() > getZoomFactor()) {
                continue;
            }
            HashSet<RoadNode> roadNodes = tree.getInRange(screenRectangle);
            for (RoadNode roadNode : roadNodes) {
                g.setStroke(type.getDrawStroke());
                g.setColor(type.getDrawColor());
                g.draw(roadNode.getShape());
            }
        }
        for(RoadKDTree tree: model.getRoadKDTreeList()){
            WayType type = tree.getType();
            if (type.getZoomFactor() > getZoomFactor()) {
                continue;
            }
            boolean shouldDrawRoadName = shouldDrawRoadName(type);
            HashSet<RoadNode> roadNodes = tree.getInRange(screenRectangle);
            for(RoadNode roadNode: roadNodes) {
                if (shouldDrawRoadName) {
                    g.setColor(Color.black);
                    drawRoadNameInCenter(g, roadNode);
                }
            }
        }
    }

    /**
     * Beskrivelse: Gennemgår alle shapes Kd-træer, og undersøger ved hvilket zoomFactor det skal vises.
     * De tegnes eller fylder alle de shapes der har punkter inden for screenRectangle
     * @param g Det givne graphicsobjekt givet fra paintComponent overriden
     */
	public void drawShapes(Graphics2D g) {
        for (ShapeKDTree tree : model.getTrees()) {
            WayType type = tree.getType();
            if (type.getZoomFactor() > getZoomFactor()) {
                continue;
            }
            g.setColor(getDrawColor(type));
            g.setStroke(type.getDrawStroke());

            HashSet<Shape> nodes = tree.getInRange(screenRectangle);

            //Her bestemmes om shapes skal fyldes eller ej
            if (type.getFillType() == FillType.LINE) {
                for (Shape shape : nodes) {
                    g.draw(shape);
                }
            } else if (type.getFillType() == FillType.SOLID) {
                for (Shape shape : nodes) {
                    g.fill(shape);
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Zoom funktioner, Punktudregnings funktioner, Panerings funktioner & Panko rasp!">

    //Punktudregnings ting

    /**
     * Returnere center x koordinat i lon vha et AffineTransform objekt.
     * @return
     */
    public double getCenterCordinateX() {
        return (transform.getTranslateX()-getWidth()/2)/ getZoomFactor();
    }

    /**
     * Returnere center y koordinat i lat vha et AffineTransform objekt.
     * @return double
     */
    public double getCenterCordinateY() {
        return (transform.getTranslateY()-getHeight()/2) / getZoomFactor();
    }


    /**
     * Beskrivelse: Omregner pixel koordinater til lon lat koordinater vha et AffineTransform objekt
     * @param x
     * @param y
     * @return Point2D
     */
    private Point2D screenCordsToLonLat(double x, double y) {
        double correctedX = -(transform.getTranslateX() - x) / getZoomFactor();
        double correctedY = -(transform.getTranslateY() - y) / getZoomFactor();
        return new Point2D.Double(correctedX, correctedY);
    }

    /**
     * Beskrivelse: Omregner lon lat koordinater til pixel koordinater vha et AffineTransform objekt
     * @param x
     * @param y
     * @return Point2D
     */
    private Point2D lonLatToScreenCords(double x, double y) {
        double correctedX = -(x * getZoomFactor() - transform.getTranslateX());
        double correctedY = -(y * getZoomFactor() - transform.getTranslateY());
        return new Point2D.Double(correctedX, correctedY);
    }

    /**
     * Beskrivelse: Panner til et x,y koordinat, i forhold til venstre øvre hjørne (som altid er 0,0)
     * @param dx
     * @param dy
     */
    public void pan(double dx, double dy) {
        transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
        repaint();
        revalidate();
    }

    /**
     * Beskrivelse: Panner til et specifikt punkt i lon, lat koordinater.
     * @param lon
     * @param lat
     */
    public void panToPoint(double lon, double lat) {
        double Xdist = (lon - getCenterCordinateX()) * getZoomFactor();
        double Ydist = (lat - getCenterCordinateY()) * getZoomFactor();

        Point2D distanceVector = new Point2D.Double(Xdist, Ydist);
        double distanceToCenterX = distanceVector.getX();
        double distanceToCenterY = distanceVector.getY();

        pan(distanceToCenterX, distanceToCenterY);
    }

    /**
     * Beskrivelse: Styrer hvordan der skal zoomes og pannes. Hvis der allerede er zoomet langt ud behøves der ikke at zoome ud, men blot at panne og zoome ind.
     * Hvis dette ikke er tilfældet og hvis afstanden til punktet der skal zoomes til er kort pannes der bare, ellers zoomes der ud, pannes og herefter zoomes ind.
     * @param lon
     * @param lat
     */
    public void fancyPan(double lon, double lat) {
        double distanceToCenterX = lon - getCenterCordinateX();
        double distanceToCenterY = lat - getCenterCordinateY();

        //Udregner en afstance udfra lat/lon-koordinater
        double distance = Math.sqrt(Math.abs(Math.pow(distanceToCenterX, 2) + Math.pow(distanceToCenterY, 2)));
        double amountOfZoom = 150000 / getZoomFactor();

        if (amountOfZoom >= 2) {
            panSlowAndThenZoomIn(distanceToCenterX, distanceToCenterY, true);
        } else {
            if (distance < 400/ getZoomFactor()) {
                panSlowAndThenZoomIn(distanceToCenterX, distanceToCenterY, false);
            } else {
                zoomOutSlowAndThenPan(distanceToCenterX, distanceToCenterY);
            }
        }
    }

    /**
     * Beskrivelse: Panner til et punkt over 100 steps ved brug af en timer. Hvis der skal zoomes kaldes zoomWithFactor
     * @param distanceToCenterX
     * @param distanceToCenterY
     * @param needToZoom: Bestemmer om der skal zoomes efter der er blevet pannet.
     */
    public void panSlowAndThenZoomIn(double distanceToCenterX, double distanceToCenterY, boolean needToZoom) {
        timer = new Timer();

        //Vi panner en 1/100 hver gang.
        double partDX = distanceToCenterX * getZoomFactor() / 100;
        double partDY = distanceToCenterY * getZoomFactor() / 100;

        timer.scheduleAtFixedRate(new TimerTask() {
            int panCounter = 1;
            @Override
            public void run() {
                if (panCounter >= 100) {
                    if (needToZoom) {
                        zoomWithFactor(1.5 / 100.0);
                    }
                    cancel();
                }
                pan(partDX, partDY);
                panCounter++;
            }

        }, 0, 10);
    }

    /**
     * Beskrivelse: Zoomer ud 120 gange hver 20ms ved brug af en timer. Efter der er zoomet ud, kaldes panSlowAndThenZoomIn der panner og zoomer ind.
     * @param distanceToCenterX
     * @param distanceToCenterY
     */
    public void zoomOutSlowAndThenPan(double distanceToCenterX, double distanceToCenterY) {
        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            int zoomOutCounter = 1;
            @Override
            public void run() {
                if(zoomOutCounter >= 120) {
                    panSlowAndThenZoomIn(distanceToCenterX, distanceToCenterY, true);
                    cancel();
                }
                else if (zoomOutCounter < 120) {
                    centerZoomToZoomLevel(15000  * 10 / zoomOutCounter);
                    zoomOutCounter++;
                }
            }
        }, 0 , 20);
    }

    /**
     * Beskrivelse: Panner til kortets center ud fra min/max lat/lon. Bruges når der loadet en fil i programmet.
     */
    public void resetCamera() {
        panToPoint(-(model.getMinLon() + model.getMaxLon()) / 2, (model.getMinLat() + model.getMaxLat()) / 2);
        centerZoomToZoomLevel(getWidth() / (model.getMaxLon() - model.getMinLon()));
    }

    /**
     * Beskrivelse: Zoomer enten ind eller ud. Bruges til zoom med mousewheel og zoomknapper.
     * @param factor: Over 1 zoomes der ind. Under 1 zoomes der ud.
     */
    public void zoom(double factor) {
        //Zoom begrænsning
        if(getZoomFactor() * factor > 800000) {
            return;
            //max zoom out
        } else if(getZoomFactor() * factor < 120){
            return;
        }

        else {
            transform.preConcatenate(AffineTransform.getScaleInstance(factor, factor));

            repaint();
            revalidate();
        }
    }

    /**
     * Beskrivelse: Zoomer til et specifikt zoomlevel.
     * @param zoomLevel
     */
    public void centerZoomToZoomLevel(double zoomLevel){
        centerZoom(zoomLevel / getZoomFactor());
    }

    /**
     * Beskrivelse: Panner først til center zoomer og panner tilbage til det pågældende punkt.
     * @param factor
     */
    public void centerZoom(double factor) {
        pan(-getWidth() / 2, -getHeight() / 2);
        zoom(factor);
        pan(getWidth() / 2, getHeight() / 2);
    }

    /**
     * Beskrivelse: Del af FancyPan. Bruges når der zoomes ind. Desto mindre faktoren er desto mindre zoomer den ind.
     * @param factor
     */
    public void zoomWithFactor(double factor) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int zoomInCounter = 1;
            @Override
            public void run() {
                if (zoomInCounter > 100) {
                    cancel();
                } else {
                    //den zoomer mest ud når zoomInCounter er lille.
                    centerZoomToZoomLevel(150000 * zoomInCounter * factor);
                    zoomInCounter++;
                }
            }
        }, 0, 20);
    }

    /**
     * Beskrivelse: returnere zoomfactoren fra et AffineTransform Objekt
     * @return double: scaleX
     */
    public double getZoomFactor() {
        return transform.getScaleX();
    }

    //</editor-fold>

    /**
     * Beskrivelse: returnere en timer, som bruges til FancyPan
     * @return Timer: timer
     */
    public Timer getTimer(){
        return timer;
    }

    /**
     * Desciption: Slår boolean'en der styrer om der bliver tegnet bynavne.
     */
    public void toggleCityNames() {
        drawCityNames = !drawCityNames;
        repaint();
        revalidate();
    }
}