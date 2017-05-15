package bfst17.Controller;

import bfst17.GUI.DrawCanvas;
import bfst17.Model;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.Timer;

/**
 * Beskrivelse: CanvasMouseController klassen
 * Controller til mussefunktioner i drawCanvas
 */
public class CanvasMouseController extends MouseAdapter implements MouseMotionListener {
	Model model;
	DrawCanvas canvas;
	Point2D lastMousePosition;
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
	 * Beskrivelse: MousePressed metoden sætter det punkt du har trykket på, og cancler timeren til fancypan hvis du trykker på skærmen
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
	 * Beskrivelse: MouseReleased metoden, sætter AA til, når du ikke længere panner og cancler timeren til fancypan hvis du releaser musen.
	 * Beskrivelse: Hvis der klikkes på højreklik har brugeren mulighed for at tilføje et punkt til kortet. Punktet er en by da det bare er et punkt med et navn
	 * @param e
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		//tilføj punkt til kortet
		if(SwingUtilities.isRightMouseButton(e)){
			String customPointText = JOptionPane.showInputDialog("Tilføj punkt", "Navnet på punktet");
			if (customPointText == null){
				return;
			}
			customPointText = customPointText.substring(0,1).toUpperCase() + customPointText.substring(1, customPointText.length());
			model.getAddressModel().putCity(customPointText , canvas.screenCordsToLonLat(e.getX(), e.getY()));
		}

		if(canvas.getTimer() != null) {
			canvas.getTimer().cancel();
		}
		canvas.AAOn();

	}

	/**
	 * Beskrivelse: MouseDragged metoden som sætter AA fra og finder forskellen mellem den nuværende museposition og
	 * den gamle museposition, så vi kan panne forskellen.
	 * @param e
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
	 * Beskrivelse: MouseWheelMoved metoden, som zommer ind og ud når mouseWheel bliver roteret med musen som center
	 * @param e
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//Gemmer factoren
		double factor = Math.pow(0.9, e.getWheelRotation());
		Point2D currentMousePosition = e.getPoint();
		double dx = currentMousePosition.getX();
		double dy = currentMousePosition.getY();
		//paner først ud og derefter zommer og paner igen, hvilket skaber zoom ind på mus effekten
		canvas.pan(-dx,-dy);
		canvas.zoom(factor);
		canvas.pan(dx,dy);
	}

	/**
	 * Beskrivelse: MouseMoved metoden, finder nearest neighbor og repainter
	 * @param e
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		canvas.getNearestNeighbourFromMousePos(e.getPoint());
		canvas.repaint();
	}
}
