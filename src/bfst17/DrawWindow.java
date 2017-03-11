package bfst17;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by trold on 2/1/17.
 */
public class DrawWindow implements Observer {
	Model model;
	JFrame window;
	DrawCanvas canvas;
	AddressModel addressModel = new AddressModel();
	private ArrayList listItems;
	private StringSearchable searchable;
	private AutocompleteJComboBox combo;
	private JTextArea userOutput;

	//e

	public DrawWindow(Model model) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		this.model = model;
		this.addressModel = model.getAddressModel();
		model.addObserver(this);
		addressModel.addObserver(this);
		window = new JFrame("Awesome OSM Visualizer Thingy!!!! 2.0");
		window.setLayout(new BorderLayout());
		canvas = new DrawCanvas(model);
		canvas.setPreferredSize(new Dimension(500, 500));
		new CanvasMouseController(canvas, model);
		window.add(canvas, BorderLayout.CENTER);
		setUpMenu();
		paintAutocomplete();
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);

		canvas.pan(-model.getMinLon(),-model.getMaxLat());
		canvas.zoom(canvas.getWidth()/(model.getMaxLon()- model.getMinLon()));



		new WindowKeyController(this, model);
	}

	/**
	 * Makes the autocomplete button with all the addresses
	 */
	public void paintAutocomplete()
	{
		this.listItems = new ArrayList();
		Iterator var2 = addressModel.iterator();

		while(var2.hasNext()) {
			Address address = (Address)var2.next();
			this.listItems.add(address.toString().toLowerCase());
		}

		this.searchable = new StringSearchable(this.listItems);
		this.combo = new AutocompleteJComboBox(this.searchable);
		this.combo.putClientProperty("JComboBox.isTableCellEditor",Boolean.TRUE);
		this.combo.setPreferredSize(new Dimension(500, 30));
		this.combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent event) {
				if(event.getKeyChar() == 10) {
					String s = (String) combo.getSelectedItem();

					float lat = -model.getOSMNodeToAddress(s.trim()).getLat();
					float lon = -model.getOSMNodeToAddress(s.trim()).getLon();

					float yCenter = ((-model.getMaxLat())+(-model.getMinLat()))/2;
					float xCenter = ((-model.getMaxLon())+(-model.getMinLon()))/2;

					float xAxisLenght = model.getMaxLon() - model.getMinLon();
					float yAxisLenght = model.getMaxLat()- model.getMinLat();

					float distanceToCenterY = lat-yCenter;
					float distanceToCenterX = lon-xCenter;

					double scalingFactorX = canvas.getXZoomFactor();
					double screenHeight = scalingFactorX*(-model.getMaxLat()+model.getMinLat());
					double scalingFactorY = -(screenHeight/canvas.getHeight());

					double dx = distanceToCenterX*scalingFactorX;
					double dy = distanceToCenterY*scalingFactorY;
					canvas.pan(dx,dy);
					combo.setSelectedItem((null));
				}

			}
		});
		this.window.add(this.combo, "North");
		this.userOutput = new JTextArea();
		this.userOutput.setEditable(false);
		this.userOutput.setBackground(Color.LIGHT_GRAY);
		//this.update((Observable)null, (Object)null);
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
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		JMenuItem load = new JMenuItem("Load", KeyEvent.VK_L);
		load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK));
		JMenuItem exit = new JMenuItem( "Exit", KeyEvent.VK_Q);


		file.add(save);
		file.add(load);
		file.add(exit);
		menu.add(file);

		JMenu tools = new JMenu("Tools");
        JMenuItem zoomIn = new JMenuItem("Zoom In", KeyEvent.VK_PLUS);
        zoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Event.CTRL_MASK));
        JMenuItem zoomOut = new JMenuItem("Zoom Out", KeyEvent.VK_MINUS);
        zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Event.CTRL_MASK));

        tools.add(zoomIn);
        tools.add(zoomOut);
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


        zoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dy = window.getContentPane().getWidth();
                int dx = window.getContentPane().getHeight();
                canvas.pan(-window.getContentPane().getWidth()/2, -window.getContentPane().getHeight()/2);
                canvas.zoom(1.25);
                canvas.pan(window.getContentPane().getWidth()/2, window.getContentPane().getHeight()/2);
            }
        });
        zoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.pan(-window.getContentPane().getWidth()/2, -window.getContentPane().getHeight()/2);
                canvas.zoom(0.75);
                canvas.pan(window.getContentPane().getWidth()/2, window.getContentPane().getHeight()/2);
            }
        });


	}
}

