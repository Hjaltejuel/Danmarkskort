package bfst17.GUI;

import bfst17.Enums.*;
import bfst17.KDTrees.CityNamesKDTree;
import bfst17.KDTrees.KDTree;
import bfst17.KDTrees.POIKDTree;
import bfst17.Model;
import bfst17.AddressHandling.TSTInterface;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;

/**
 * Created by trold on 2/8/17.
 */
public class DrawCanvas extends JComponent implements Observer {
	Model model;
	AffineTransform transform = new AffineTransform();
    Shape regionShape = null;
	boolean antiAlias;
    GUIMode GUITheme = GUIMode.NORMAL;
	boolean fancyPanEnabled = true;
	HashMap<POIclasification, Boolean> nameToBoolean = new HashMap<>();
	Point2D pin;
	Integer FrameCounter=0;
	double timeTracker;
	Integer FPS=0;
	Rectangle2D screenRectangle;
	HashMap<String, BufferedImage> PinAndPOIImageMap;
	boolean drawCityNames = true;

    public DrawCanvas(Model model) {
		this.model = model;
		model.addObserver(this);
		fillNameToBoolean();
		loadImages();
	}

	private void loadImages() {
        PinAndPOIImageMap = new HashMap<>();
        try{
            BufferedImage img = ImageIO.read(getClass().getResource("/PinImage.png"));
            PinAndPOIImageMap.put("pin", img);
        } catch (Exception e){
            e.printStackTrace();
        }
        try{
            for (PointsOfInterest POI : PointsOfInterest.values()){
                BufferedImage img = ImageIO.read(getClass().getResource("/POI/" + POI.name() + ".png"));
                PinAndPOIImageMap.put(POI.name(), img);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

	public GUIMode getGUITheme() {
        return GUITheme;
    }

	public void fillNameToBoolean() {
		for(POIclasification name: POIclasification.values()) {
			nameToBoolean.put(name, false);
		}
	}

    public void toggleAA() {
        antiAlias = !antiAlias;
        repaint();
        revalidate();
    }

    public boolean isFancyPanEnabled() {
        return fancyPanEnabled;
    }

    public void toggleFancyPan() {
        fancyPanEnabled = !fancyPanEnabled;
    }

    public void setPointsOfInterest(POIclasification name) {
        boolean nameToBooleanCopy = nameToBoolean.get(name);
        nameToBoolean.put(name, !nameToBooleanCopy);
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

    public void setGUITheme(GUIMode newTheme) {
	    GUITheme = newTheme;
    }

    public void checkFPS() {
        FrameCounter++;
        if(System.nanoTime()-timeTracker >= 1_000_000_000) {
            FPS = FrameCounter;
            timeTracker = System.nanoTime();
            FrameCounter = 0;
        }
    }

	/**
	 * Calls the UI delegate's paint method, if the UI delegate
	 * is non-<code>null</code>.  We pass the delegate a copy of the
	 * <code>Graphics</code> object to protect the rest of the
	 * paint code from irrevocable changes
	 * (for example, <code>Graphics.translate</code>).
	 * <p>
	 * If you override this in a subclass you should not make permanent
	 * changes to the passed in <code>Graphics</code>. For example, you
	 * should not alter the clip <code>Rectangle</code> or modify the
	 * transform. If you need to do these operations you may find it
	 * easier to create a new <code>Graphics</code> from the passed in
	 * <code>Graphics</code> and manipulate it. Further, if you do not
	 * invoker super's implementation you must honor the opaque property,
	 * that is
	 * if this component is opaque, you must completely fill in the background
	 * in a non-opaque color. If you do not honor the opaque property you
	 * will likely see visual artifacts.
	 * <p>
	 * The passed in <code>Graphics</code> object might
	 * have a transform other than the identify transform
	 * installed on it.  In this case, you might get
	 * unexpected results if you cumulatively apply
	 * another transform.
	 *
	 *
	 * @see #paint
	 */
	@Override
	protected void paintComponent(Graphics _g) {
        Graphics2D g = (Graphics2D) _g;

        //Definér skærmbilledet
        Point2D topLeft = screenCordsToLonLat(0, 0);
        Point2D topRight = screenCordsToLonLat(getWidth(), getHeight());
        screenRectangle = new Rectangle2D.Double(topLeft.getX(), topLeft.getY(),
                topRight.getX() - topLeft.getX(), topRight.getY() - topLeft.getY());

        //Tegn kortet
        drawMap(g);

        //Tegn overlay (Pin, POI, Målebånd, FPS)
        drawOverlay(g);

        //Opdater FPS
        checkFPS();
	}

    //<editor-fold desc="Funktioner der tegner">
    private void drawOverlay(Graphics2D g) {
        //Reset transform til billeder / overlay
        g.setTransform(new AffineTransform());

        drawPin(g);

        drawPointsOfInteres(g);

        drawMeasureBand(g);

        drawFPSCounter(g);

        if(drawCityNames) {
            drawCityAndTownNames(g);
        }
    }

    public void drawPin(Graphics2D g) {
        if (pin == null) {
            return; //Lad være at tegne, hvis der ikke er en pin
        }
        drawImageAtLocation(g,"pin",pin.getX(),pin.getY());
    }

    public void drawPointsOfInteres(Graphics2D g) {
        if (getXZoomFactor() > 40000) {
            POIKDTree POITree = model.getPOITree();
            for (POIKDTree.TreeNode PoiNodes : POITree.getInRange(screenRectangle)) {
                //POIclasification POIClass = PoiNodes.getPOIClass();
                //if (nameToBoolean.get(POIClass)) {
                    //PointsOfInterest POIType = PoiNodes.getPOIType();
                    //String imagePath = POIType.name();
                    drawImageAtLocation(g, PointsOfInterest.AMENITY_BANK.name(), -PoiNodes.getX(), -PoiNodes.getY());
                //}
            }
        }
    }

    public void drawCityAndTownNames(Graphics2D g) {
        CityNamesKDTree townTree = model.getTownTreeTree();

        if (getXZoomFactor() > 900 && getXZoomFactor() < 9000) {
            for(CityNamesKDTree.TreeNode cityNodes : townTree.getInRange(screenRectangle)) {
                String cityName = cityNodes.getCityName();
                Point2D drawLocation = lonLatToScreenCords(-cityNodes.getX(), -cityNodes.getY());
                g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                int stringWidth = g.getFontMetrics().stringWidth(cityName);
                g.drawString(cityName, (int) drawLocation.getX() - stringWidth / 2, (int) drawLocation.getY());
            }

        }

        CityNamesKDTree cityTree = model.getCityTree();
        if (getXZoomFactor() < 400 && getXZoomFactor() > 80){
            for(CityNamesKDTree.TreeNode cityNodes : cityTree.getInRange(screenRectangle)) {
                String cityName = cityNodes.getCityName();
                Point2D drawLocation = lonLatToScreenCords(-cityNodes.getX(), -cityNodes.getY());
                g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
                int stringWidth = g.getFontMetrics().stringWidth(cityName);
                g.drawString(cityName, (int) drawLocation.getX() - stringWidth / 2, (int) drawLocation.getY());
            }
        }
    }


    public void drawImageAtLocation(Graphics2D g, String imagePath, double x, double y) {
        BufferedImage image = PinAndPOIImageMap.get(imagePath);
        Rectangle2D imageRect = new Rectangle2D.Double(-x,-y,image.getWidth()/getXZoomFactor(),image.getHeight()/getYZoomFactor());

        if(!screenRectangle.intersects(imageRect)) {
            return; //Billedet er ikke inden for skærmen
        }
        Point2D drawLocation = lonLatToScreenCords(x, y);
        g.drawImage(image, (int) drawLocation.getX() - image.getWidth() / 2, (int) drawLocation.getY() - image.getHeight(), this);
    }

    final float lonToKM = 111.320f, KMToMiles = 0.621371192f;

    private void drawMeasureBand(Graphics2D g) {
	    g.setColor(Color.black);
	    g.setStroke(new BasicStroke(1f));
	    Integer Y = getHeight() - 55;
	    Integer X1 = getWidth()-100, X2=getWidth()-35;

	    Point2D p1 = screenCordsToLonLat(X1,Y);
        Point2D p2 = screenCordsToLonLat(X2,Y);

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

        g.setColor(new Color(255,255,255,100));
        g.fill(rect);
        g.setColor(Color.black);
        g.drawString(showString,X1+(32-showString.length()*4),Y-2);
        g.draw(line);
        g.draw(rightVertLine);
        g.draw(leftVertLine);
    }

    private Integer getRoundedDistance() {
        //y= 0.4607*e^(0.7682*x)

        return 0;
    }


    private void drawFPSCounter(Graphics2D g) {
        try {
            InputStream is = this.getClass().getResourceAsStream("/HelveticaNeueLT.otf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(15f);
            g.setFont(font);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        g.drawString("FPS: "+FPS ,5,getHeight()-55);
    }

    private void drawMap(Graphics2D g) {
        //Tegn vand
        g.setColor(getDrawColor(WayType.NATURAL_WATER));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setTransform(transform);
        g.setStroke(new BasicStroke(Float.MIN_VALUE));

        if (antiAlias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Tegn coastlines
        drawCoastlines(g);

        //Hent og tegn shapes fra diverse KDTræer
        drawShapes(g);

        //Tegn regionen, hvis der er søgt efter den
        if(regionShape != null){
            Color color = g.getColor();
            g.setColor(new Color(255,0,0,127));
            g.draw(regionShape);
            g.setColor(color);
        }
    }

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
     * Tegner alle de coastlines der er indenfor skærmbillede
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

	public void drawShapes(Graphics2D g) {
        for (KDTree tree : model.getTrees()) {
            WayType type = tree.getType();
            if (type.getZoomFactor() > getXZoomFactor()) {
                continue;
            }
            HashSet<Shape> shapes = tree.getInRange(screenRectangle);
            g.setColor(getDrawColor(type));

            g.setStroke(type.getDrawStroke());

            //Her bestemmes om shapes skal fyldes eller ej
            if (type.getFillType() == FillType.LINE) {
                for (Shape shape : shapes) {
                    g.draw(shape);
                }
            } else if (type.getFillType() == FillType.SOLID) {
                for (Shape shape : shapes) {
                    g.fill(shape);
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Zoom funktioner, Punktudregnings funktioner, Panerings funktioner & Panko rasp!">

    //Punktudregnings ting
    public double getCenterCordinateX() {
        return (transform.getTranslateX()-getWidth()/2)/getXZoomFactor();
    }
    public double getCenterCordinateY() {
        return (transform.getTranslateY()-getHeight()/2) / getYZoomFactor();
    }

    public Point2D getCenterCordinate() {
	    return screenCordsToLonLat(getWidth()/2,getHeight()/2);
    }

    public Point2D getDistanceInPixelToPoint(double lon, double lat) {
        //distance from center of screen in lat lon
        double Xdist = (lon - getCenterCordinateX()) * getXZoomFactor();
        double Ydist = (lat - getCenterCordinateY()) * getYZoomFactor();
        return new Point2D.Double(Xdist, Ydist);
    }

    private Point2D screenCordsToLonLat(double x, double y) {
        double correctedX = -(transform.getTranslateX() - x) / getXZoomFactor();
        double correctedY = -(transform.getTranslateY() - y) / getYZoomFactor();
        return new Point2D.Double(correctedX, correctedY);
    }

    private Point2D lonLatToScreenCords(double x, double y) {
        double correctedX = -(x * getXZoomFactor() - transform.getTranslateX());
        double correctedY = -(y * getYZoomFactor() - transform.getTranslateY());
        return new Point2D.Double(correctedX, correctedY);
    }

    public Point2D toModelCoords(Point2D lastMousePosition) {
        try {
            return transform.inverseTransform(lastMousePosition, null);
        } catch (NoninvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }

    //Pan ting
    public void pan(double dx, double dy) {
        transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
        repaint();
        revalidate();
    }

    public void panToPoint(double lon, double lat) {
        Point2D distanceVector = getDistanceInPixelToPoint(lon, lat);
        double distanceToCenterX = distanceVector.getX();
        double distanceToCenterY = distanceVector.getY();

        pan(distanceToCenterX, distanceToCenterY);
    }

    public void fancyPan(double lon, double lat) {
        double distanceToCenterX = lon - getCenterCordinateX();
        double distanceToCenterY = lat - getCenterCordinateY();

        double distance = Math.sqrt(Math.abs(Math.pow(distanceToCenterX, 2) + Math.pow(distanceToCenterY, 2)));
        double amountOfZoom = 150000 / getXZoomFactor();

        if (amountOfZoom >= 2) {
            panSlowAndThenZoomIn(distanceToCenterX, distanceToCenterY, true);
        } else {
            if (distance < 400/getXZoomFactor()) {
                panSlowAndThenZoomIn(distanceToCenterX, distanceToCenterY, false);
            } else {
                zoomOutSlowAndThenPan(distanceToCenterX, distanceToCenterY);
            }
        }
    }

    public void panSlowAndThenZoomIn(double distanceToCenterX, double distanceToCenterY, boolean needToZoom) {
        java.util.Timer timer = new java.util.Timer();

        double partDX = distanceToCenterX * getXZoomFactor() / 100;
        double partDY = distanceToCenterY * getYZoomFactor() / 100;

        timer.scheduleAtFixedRate(new TimerTask() {

            int panCounter = 1;

            @Override
            public void run() {
                if (panCounter >= 100) {
                    if (needToZoom) {
                        zoomWithFactor(3.0 / 100.0);
                    }
                    cancel();
                }
                pan(partDX, partDY);
                panCounter++;
            }

        }, 0, 10);
    }

    public void zoomOutSlowAndThenPan(double distanceToCenterX, double distanceToCenterY) {
        java.util.Timer timer = new java.util.Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            int zoomOutCounter = 1;

            @Override
            public void run() {
                if(zoomOutCounter >= 100) {
                    panSlowAndThenZoomIn(distanceToCenterX, distanceToCenterY, true);
                    cancel();
                }
                else if (zoomOutCounter < 100) {
                    centerZoomToZoomLevel(150000  * 10 / zoomOutCounter);

                    zoomOutCounter++;
                }
            }
        }, 0 , 20);
    }

    public void resetCamera() {
        panToPoint(-(model.getMinLon() + model.getMaxLon()) / 2, (model.getMinLat() + model.getMaxLat()) / 2);
        centerZoomToZoomLevel(getWidth() / (model.getMaxLon() - model.getMinLon()));
    }

    //Zoom ting
    public void zoom(double factor) {
        //Zoom begrænsning
        if(getXZoomFactor()*factor>800000) {
            return;
        }
        transform.preConcatenate(AffineTransform.getScaleInstance(factor, factor));
        repaint();
        revalidate();
    }

    public void centerZoomToZoomLevel(double zoomLevel){
        centerZoom(zoomLevel / getXZoomFactor());
    }

    public void centerZoom(double factor) {
        pan(-getWidth() / 2, -getHeight() / 2);
        zoom(factor);
        pan(getWidth() / 2, getHeight() / 2);
    }

    public void zoomWithFactor(double factor) {
        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int zoomInCounter = 1;

            @Override
            public void run() {
                if (zoomInCounter > 100) {
                    cancel();
                } else {
                    centerZoomToZoomLevel(150000 * zoomInCounter * factor);
                    zoomInCounter++;
                }
            }
        }, 0, 20);
    }

    public double getXZoomFactor(){return transform.getScaleY();}
    public double getYZoomFactor(){return transform.getScaleY();}
    //</editor-fold>

	/**
	 * This method is called whenever the observed object is changed. An
	 * application calls an <tt>Observable</tt> object's
	 * <code>notifyObservers</code> method to have all the object's
	 * observers notified of the change.
	 *
	 * @param o   the observable object.
	 * @param arg an argument passed to the <code>notifyObservers</code>
	 */
	@Override
	public void update(Observable o, Object arg) {
	}

    public void toggleCityNames() {
        drawCityNames = !drawCityNames;
        repaint();
        revalidate();
    }
}