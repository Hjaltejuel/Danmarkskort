package bfst17;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by trold on 2/8/17.
 */
public class DrawCanvas extends JComponent implements Observer {
	Model model;
	AffineTransform transform = new AffineTransform();
	boolean antiAlias;
	boolean greyScale = false;
	boolean nightmode = false;
	boolean fancyPan = true;

	Point2D pin;
	boolean searchMode = false;

	public DrawCanvas(Model model) {
		this.model = model;
		model.addObserver(this);

	}
	public double getCenterCordinateX() {
        return (transform.getTranslateX()/transform.getScaleX())-((getWidth()/transform.getScaleX())/2);
	}
	public double getCenterCordinateY() {
		return (transform.getTranslateY() / transform.getScaleY()) -((getHeight() / transform.getScaleY())/2);
    }

    public void setSearchMode(float lon,float lat){
        searchMode = true;
        pin = new Point2D.Float(lon,lat);
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

	private Color getDrawColor(WayType type){
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

		for(Shape s: model.get(WayType.NATURAL_COASTLINE)) {
			g.setStroke(WayType.NATURAL_COASTLINE.getDrawStroke());
			g.setColor(getDrawColor(WayType.NATURAL_COASTLINE));
			g.fill(s);
		}

		Point2D p1 = screenCoordsToLonLat(0,0);
		Point2D p2 = screenCoordsToLonLat(getWidth(),getHeight());
		Shape screenRectangle = new Rectangle2D.Double(p1.getX(),p1.getY(),p2.getX()-p1.getX(),p2.getY()-p1.getY());

		EnumMap<WayType, List<Shape>> shapes = model.getTree().getInRange((Rectangle2D) screenRectangle);
		for (WayType type : WayType.values()) {
			g.setColor(getDrawColor(type));

			g.setStroke(type.getDrawStroke());
			if (type.getZoomFactor() < getXZoomFactor()) {
				//How should the data be drawn?
				if (type.getFillType() == FillType.LINE) {
					for (Shape shape : shapes.get(type)) {
						g.draw(shape);
					}
				} else if (type.getFillType() == FillType.SOLID) {
					for (Shape shape : shapes.get(type)) {
						g.fill(shape);
					}
				}
			}
			/*
			g.setStroke(new BasicStroke(0.000008f));
			Shape rectangle1 = shape.getBounds2D();
			g.draw(rectangle1);
			*/

		}
		setPin(g);
	}

	public void pan(double dx, double dy) {
		transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
		repaint();
        revalidate();
	}

	public void setPin(Graphics g)  {
        if(pin!=null) {
            BufferedImage image = null;
            try {
                image = ImageIO.read(getClass().getResource("/temppin.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            AffineTransform imageTransform = new AffineTransform();
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            imageTransform.setToIdentity();

            //System.out.println(pin.getX() + " " + pin.getY());
            double offsetHeight = (image.getHeight()/transform.getScaleY())/7;
            double offsetWidth = ((image.getWidth()/4)/transform.getScaleX())/7;

            imageTransform.translate(-pin.getX(),-pin.getY());
            imageTransform.scale(((1/transform.getScaleX())/7),((1/transform.getScaleY())/7));
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
	private Point2D screenCoordsToLonLat(double x, double y) {
		if(x<0||x>getWidth()||y<0||y>getHeight()){
			System.out.println("Tror du bruger den forkerte funktion.. (screenCoordsToLonLat)");
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
                else if (zoomOutCounter < 100){

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

