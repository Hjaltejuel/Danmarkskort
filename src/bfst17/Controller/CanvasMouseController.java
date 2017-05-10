package bfst17.Controller;

import bfst17.GUI.DrawCanvas;
import bfst17.Model;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.Timer;

/**
 * Created by trold on 2/8/17.
 *
 *
 */
//hejss
public class CanvasMouseController extends MouseAdapter implements MouseMotionListener {
	Model model;
	DrawCanvas canvas;
	Point2D lastMousePosition;
	private static boolean draggingLine;
	//NN Timer
	long timeSinceMove;
	Timer timer;


	public CanvasMouseController(DrawCanvas canvas, Model model) {
		this.model = model;
		this.canvas = canvas;
		timer = new Timer();
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addMouseWheelListener(this);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param e
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if(canvas.getTimer() != null) {
			canvas.getTimer().cancel();
		}
		lastMousePosition = e.getPoint();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param e
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if(canvas.getTimer() != null) {
			canvas.getTimer().cancel();
		}
		canvas.AAOn();

	}

	/**
	 * {@inheritDoc}
	 *
	 * @param e
	 *
	 * @since 1.6
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		canvas.AAOff();
		Point2D currentMousePosition = e.getPoint();
		double dx = currentMousePosition.getX() - lastMousePosition.getX();
		double dy = currentMousePosition.getY() - lastMousePosition.getY();
		canvas.pan(dx, dy);

		lastMousePosition = currentMousePosition;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param e
	 *
	 * @since 1.6
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		double factor = Math.pow(0.9, e.getWheelRotation());
		Point2D currentMousePosition = e.getPoint();
		double dx = currentMousePosition.getX();
		double dy = currentMousePosition.getY();
		canvas.pan(-dx,-dy);
		canvas.zoom(factor);
		canvas.pan(dx,dy);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		canvas.setMousePos(e.getPoint());
		canvas.repaint();
	}
}
