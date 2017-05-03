package bfst17.Controller;

import bfst17.GUI.DrawCanvas;
import bfst17.Model;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

/**
 * Created by trold on 2/8/17.
 */
public class CanvasMouseController extends MouseAdapter {
	Model model;
	DrawCanvas canvas;
	Point2D lastMousePosition;
	private static boolean draggingLine;


	public CanvasMouseController(DrawCanvas canvas, Model model) {
		this.model = model;
		this.canvas = canvas;
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
		lastMousePosition = e.getPoint();
		canvas.AAOff();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param e
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
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
		Point2D currentMousePosition = e.getPoint();
			double dx = currentMousePosition.getX() - lastMousePosition.getX();
			double dy = currentMousePosition.getY() - lastMousePosition.getY();
			canvas.pan(dx,dy);

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
}
