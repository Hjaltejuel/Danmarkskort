package bfst17;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.*;
import java.util.Observable;
import java.util.Observer;



/**
 * Created by trold on 2/8/17.
 */
public class DrawCanvas extends JComponent implements Observer,ComponentListener {
	Model model;
	AffineTransform transform = new AffineTransform();
	float centerCordinateX;
	float centerCordinateY;
	double scalingFactorX;
	double modelHeight;
	double scalingFactorY;
	boolean antiAlias;
	int oldHeight;
	int oldWidth;
	boolean firstTime = true;
	boolean greyScale = false;
	boolean nightmode = false;

	public DrawCanvas(Model model) {
		this.model = model;
		model.addObserver(this);
		centerCordinateY = 0;
		centerCordinateX = ((-model.getMaxLon())+(-model.getMinLon()))/2;
		this.addComponentListener(this);

	}

	public void initialise()
	{
		//sets the scalling factor
		scalingFactorX = getXZoomFactor();

		//sets the screenHeight
		modelHeight = scalingFactorX*(-model.getMaxLat()+model.getMinLat());

		//calculates the real center y, based on the diff between model height and screen height
		centerCordinateY += -model.getMaxLat() -(getHeight()/modelHeight*((-model.getMaxLat()-(-model.getMinLat()))/2));
		scalingFactorY = getYZoomFactor();
		oldHeight = getHeight();
		oldWidth = getWidth();
	}

	public void setCenter(double dx, double dy)
	{
		centerCordinateX += dx;
		centerCordinateY += dy;
	}
	public float getCenterCordinateX(){return centerCordinateX;}
	public float getCenterCordinateY(){return centerCordinateY;}

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
	@Override
	protected void paintComponent(Graphics _g) {
		Graphics2D g = (Graphics2D) _g;
		if(nightmode){
			g.setColor(new Color(36,47,62));
		} else {
			g.setColor(WayType.NATURAL_WATER.getDrawColor());
		}
		g.fillRect(0,0, getWidth(),getHeight());
		g.setTransform(transform);
		g.setStroke(new BasicStroke(Float.MIN_VALUE));

		if (antiAlias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//Draw all shapes

		for(WayType type: WayType.values())
		{
			if(!greyScale && !nightmode) {
				g.setColor(type.getDrawColor());
			} else if(greyScale)
				{
					Color c = type.getDrawColor();
					int red = (int)(c.getRed() * 0.299);
					int green = (int)(c.getGreen() * 0.587);
					int blue = (int)(c.getBlue() *0.114);
					Color newColor = new Color(red+green+blue,

							red+green+blue,red+green+blue);
					g.setColor(newColor);
				} else
			{
				g.setColor(type.getNightMode());
			}
			g.setStroke(type.getDrawStroke());
            if(type.getZoomFactor() < getXZoomFactor() ){
			//How should the data be drawn?
			if(type.getFillType()==FillType.LINE) {
				for (Shape shape : model.get(type)) {
					g.draw(shape);
				}
			}
			else if(type.getFillType()==FillType.SOLID) {
				for (Shape shape : model.get(type)) {
					g.fill(shape);
				}
			}
            }
		}
	}

	public void pan(double dx, double dy) {
		transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
		repaint();
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
	}
	//finds the number of pixel moved and updates the scalingfactors
	@Override
	public void componentResized(ComponentEvent e) {
		if(!firstTime) {
			scalingFactorX = transform.getScaleX();
			modelHeight = scalingFactorX*(-model.getMaxLat()+model.getMinLat());
			scalingFactorY = transform.getScaleY();
			double centerMovedX = -((getWidth() - oldWidth) / 2) /transform.getScaleX();
			double centerMovedY = -((getHeight() - oldHeight) / 2) / scalingFactorY;
			oldHeight = getHeight();
			oldWidth = getWidth();
			setCenter(centerMovedX, centerMovedY);
		}
		firstTime = false;
	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void componentHidden(ComponentEvent e) {

	}
}
