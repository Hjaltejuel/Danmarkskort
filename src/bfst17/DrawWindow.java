package bfst17;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by trold on 2/1/17.
 */
public class DrawWindow implements Observer {
	Model model;
	JFrame window;
	DrawCanvas canvas;
	//e

	public DrawWindow(Model model) {
		this.model = model;
		model.addObserver(this);
		window = new JFrame("Awesome OSM Visualizer Thingy!!!! 2.0");
		window.setLayout(new BorderLayout());
		canvas = new DrawCanvas(model);
		canvas.setPreferredSize(new Dimension(500, 500));
		new CanvasMouseController(canvas, model);
		window.add(canvas, BorderLayout.CENTER);
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		canvas.pan(-model.getMinLon(), -model.getMaxLat());
		canvas.zoom(window.getWidth() / (model.getMaxLon() - model.getMinLon()));
		new WindowKeyController(this, model);
	}

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

	public void addKeyListener(KeyListener keyListener) {
		window.addKeyListener(keyListener);
	}

	public void toggleAA() {
		canvas.toggleAA();
	}
}
