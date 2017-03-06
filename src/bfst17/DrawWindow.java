package bfst17;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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

	public DrawWindow(Model model) {
		this.model = model;
		model.addObserver(this);
		window = new JFrame("Awesome OSM Visualizer Thingy!!!! 2.0");
		window.setLayout(new BorderLayout());
		canvas = new DrawCanvas(model);
		canvas.setPreferredSize(new Dimension(500, 500));
		new CanvasMouseController(canvas, model);
		window.add(canvas, BorderLayout.CENTER);
		setUpMenu();
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


	public void setUpMenu(){
		JMenuBar menu = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem save = new JMenuItem("Save", KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));
		JMenuItem load = new JMenuItem("Load", KeyEvent.VK_L);
		load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0));
		JMenuItem exit = new JMenuItem( "Exit", KeyEvent.VK_Q);


		file.add(save);
		file.add(load);
		file.add(exit);
		menu.add(file);

		JMenu tools = new JMenu("Tools");
		JMenuItem pan = new JMenuItem("Pan", KeyEvent.VK_P);
		pan.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));

		tools.add(pan);
		menu.add(tools);

		window.setJMenuBar(menu);


		//metode til at save
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String savename = JOptionPane.showInputDialog("Enter a filename to save!");
				savename += ".bin";
				model.save(savename);
			}
		});

		//metode til at loade
		load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String loadname = JOptionPane.showInputDialog("Enter the name of the file you want to load.");
				loadname += ".bin";
				model.load(loadname);
			}
		});

		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});



		pan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CanvasMouseController.isPanning();
			}
		});


	}
}

