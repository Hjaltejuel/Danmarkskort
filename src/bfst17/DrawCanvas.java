package bfst17;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * Created by trold on 2/8/17.
 */
public class DrawCanvas extends JComponent implements Observer {
	Model model;
	AffineTransform transform = new AffineTransform();
    Shape regionShape = null;
    boolean regionSearch = false;
	boolean antiAlias;
	boolean greyScale = false;
	boolean nightmode = false;
	boolean fancyPan = true;
	HashMap<String,Boolean> nameToBoolean = new HashMap<>();
	Point2D pin;
	boolean searchMode = false;

	public DrawCanvas(Model model) {
		this.model = model;
		model.addObserver(this);
		fillNameToBoolean();
	}

	public void fillNameToBoolean(){
		for(POIclasification name: POIclasification.values()){
			nameToBoolean.put(name.toString(),false);
		}
	}
	public void regionSearch(Shape shape){
        regionSearch = true;
        regionShape = shape;
    }

	public double getCenterCordinateX() {
        return (transform.getTranslateX()-getWidth()/2)/transform.getScaleX();
	}
	public double getCenterCordinateY() {
		return (transform.getTranslateY()-getHeight()/2) / transform.getScaleY();
    }

    public void setSearchMode(float lon,float lat) {
        searchMode = true;
        regionSearch = false;
        pin = new Point2D.Float(lon,lat);
    }
    public void setPointsOfInterest(String name){
		boolean nameToBooleanCopy = nameToBoolean.get(name);
		nameToBoolean.put(name,!nameToBooleanCopy);
		repaint();
	}
	public void setGreyScale()
	{
		greyScale = true;
	}
	public void setGreyScaleFalse()
	{
		greyScale = false;
	}
	public void setNightMode(){
		nightmode = true;
	}
	public void setNightModeFalse(){
		nightmode = false;
	}

	private void drawCoastlines(Graphics2D g) {
		for(Shape s: model.getCoastlines()) {
			g.setStroke(WayType.NATURAL_COASTLINE.getDrawStroke());
			g.setColor(getDrawColor(WayType.NATURAL_COASTLINE));
			g.fill(s);
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

	private Color getDrawColor(WayType type) {
		Color drawColor = type.getDrawColor();

		if(nightmode) {
			drawColor = type.getNightModeColor();
		} else if (greyScale) {
			int red = (int) (drawColor.getRed() * 0.299);
			int green = (int) (drawColor.getGreen() * 0.587);
			int blue = (int) (drawColor.getBlue() * 0.114);
			int sum = red + green + blue;
			drawColor = new Color(sum, sum, sum);
		}
		return drawColor;
	}

	@Override
	protected void paintComponent(Graphics _g) {
        Graphics2D g = (Graphics2D) _g;
		if (nightmode) {
			g.setColor(WayType.NATURAL_WATER.getNightModeColor());
		} else {
			g.setColor(WayType.NATURAL_WATER.getDrawColor());
		}

		g.fillRect(0, 0, getWidth(), getHeight());
		g.setTransform(transform);
		g.setStroke(new BasicStroke(Float.MIN_VALUE));

		if (antiAlias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		drawCoastlines(g);



		Point2D topLeft = screenCordsToLonLat(0, 0);
		Point2D topRight = screenCordsToLonLat(getWidth(), getHeight());
		Shape screenRectangle = new Rectangle2D.Double(topLeft.getX(), topLeft.getY(),
                topRight.getX() - topLeft.getX(), topRight.getY() - topLeft.getY());

        for (KDTree tree : model.getTree()) {
            WayType type = tree.type;
            if (type.getZoomFactor() > getXZoomFactor()) {
                continue;
            }
            HashSet<Shape> shapes = tree.getInRange((Rectangle2D) screenRectangle);
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
                    g.draw(shape.getBounds2D());
                }
            }

        }
        if(getXZoomFactor() > 40000) {
			POIKDTree POITree = model.getPOITree();
			for (POIKDTree.TreeNode PoiNodes : POITree.getInRange((Rectangle2D)screenRectangle)) {
				drawPointOfInterest(g, PoiNodes.getPOIType(), PoiNodes.getX(), PoiNodes.getY());
			}
		}
        /*
        else if(transform.getScaleY()>40000) {
            if(nameToBoolean.get(n.getPointsOfInterest().getClassification().toString())) {
                POI.add(n);
            }

        }

    }
		if(transform.getScaleY()>40000) {
        for (KDTree.TreeNode n : POI) {
            drawPointsOfInterest(g, n.getPointsOfInterest(), n.getX(), n.getY());
        }
    }
    /
		/**/
		setPin(g);

        if(regionSearch){
            Color color = g.getColor();
            g.setColor(new Color(255,0,0,127));
            g.draw(regionShape);
            g.setColor(color);

        }

	}

	public void pan(double dx, double dy) {
		transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
		repaint();
        revalidate();
	}
	public void drawPointOfInterest(Graphics2D g, PointsOfInterest type, double x, double y) {
			BufferedImage image = null;
				try {
					image = ImageIO.read(getClass().getResource("/POI/" + type.name() + ".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				AffineTransform imageTransform = new AffineTransform();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
						imageTransform.setToIdentity();
						imageTransform.translate(x, y);
						imageTransform.scale(((1 / transform.getScaleX())), ((1 / transform.getScaleY())));
						g.drawImage(image, imageTransform, null);
			}


	public void setPin(Graphics2D g) {
        if(pin!=null) {
            BufferedImage image = null;
            try {
                image = ImageIO.read(getClass().getResource("/temppin.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            AffineTransform imageTransform = new AffineTransform();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            imageTransform.setToIdentity();

            imageTransform.translate(-pin.getX()-(((image.getWidth()/10)/2))/transform.getScaleX(),-pin.getY()-((image.getHeight()/10)/transform.getScaleX()));
            imageTransform.scale(((1/transform.getScaleX())/10),((1/transform.getScaleY())/10));
            ((Graphics2D) g).drawImage(image, imageTransform, null);
            searchMode = false;
        }
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
					pan(-getWidth() / 2, -getHeight() / 2);
					zoom(150000 / getXZoomFactor() * zoomInCounter * factor);
					pan(getWidth() / 2, getHeight() / 2);
					zoomInCounter++;
				}
			}
		}, 0, 20);
	}

	private Point2D lonLatToPixel(double x, double y) {
		return new Point2D.Double(x*getXZoomFactor(),y*getYZoomFactor());
	}

	private Point2D pixelToLonLat(double x, double y) {
		return new Point2D.Double(x/getXZoomFactor(),y/getYZoomFactor());
	}
	private Point2D screenCordsToLonLat(double x, double y) {
		if(x<0||x>getWidth()||y<0||y>getHeight()) {
			System.out.println("Tror du bruger den forkerte funktion.. (screenCordsToLonLat)");
		}
		return new Point2D.Double(-(transform.getTranslateX()-x)/getXZoomFactor(),-(transform.getTranslateY()-y)/getYZoomFactor());
	}

	public void panSlowAndThenZoomIn(double distanceToCenterX, double distanceToCenterY, boolean needToZoom) {
		java.util.Timer timer = new java.util.Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			double dx = distanceToCenterX * getXZoomFactor();
			double dy = distanceToCenterY * getYZoomFactor();

			double partDX = dx / 100;
			double partDY = dy / 100;

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
					pan(-getWidth() / 2, -getHeight() / 2);
					zoom(150000 / getXZoomFactor() * 10 / zoomOutCounter);
					pan(getWidth() / 2, getHeight() / 2);

					zoomOutCounter++;
                }
            }
        }, 0 , 20);
    }

    public void zoomAndCenter(){
		pan(-getWidth() / 2, -getHeight() / 2);
		zoom(150000 / getXZoomFactor());
		pan(getWidth() / 2, getHeight() / 2);
	}

	public double getXZoomFactor(){return transform.getScaleX();}
	public double getYZoomFactor(){return transform.getScaleY();}
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

	public void zoom(double factor) {
        //Zoom begrænsning
	    if(getXZoomFactor()*factor>800000) {
            return;
        }
		transform.preConcatenate(AffineTransform.getScaleInstance(factor, factor));
        repaint();
        revalidate();
	}

	public Point2D toModelCoords(Point2D lastMousePosition) {
		try {
			return transform.inverseTransform(lastMousePosition, null);
		} catch (NoninvertibleTransformException e) {
			throw new RuntimeException(e);
		}
	}

	public void toggleAA() {
		antiAlias = !antiAlias;
		repaint();
        revalidate();
	}

	public void toggleFancyPan() {
		fancyPan = !fancyPan;
	}
}

