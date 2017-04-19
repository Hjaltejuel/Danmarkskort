package bfst17;

		import javax.imageio.ImageIO;
		import javax.swing.*;
		import javax.swing.border.LineBorder;
		import javax.swing.event.DocumentEvent;
		import javax.swing.filechooser.FileFilter;
		import javax.swing.plaf.basic.BasicComboPopup;
		import javax.swing.text.JTextComponent;
		import java.awt.*;
		import java.awt.event.*;
		import java.awt.geom.Point2D;
		import java.awt.image.BufferedImage;
		import java.io.File;
		import java.io.IOException;
		import java.util.*;
		import java.util.stream.Collectors;
		import java.util.Timer;
		import java.util.concurrent.TimeUnit;

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
	private AutocompleteJComboBox secondCombo;
	private JLayeredPane WindowPane;
	private JPopupMenu popUpMenu;
	private JPopupMenu poiMenu;
	private JLabel barImage;
	private JButton zoomIn;
	private JButton zoomOut;
	private JButton pointsOfInterest;
	private JPanel sidebarMenu  = new JPanel(new GridLayout(0,1));
	boolean isClicked1 = false;
	boolean isClicked2 = false;

	boolean setUpDirectionsMenu = false;


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
		window = new JFrame("Danmarkskort gruppe A");
		WindowPane = new JLayeredPane();
		window.setPreferredSize(new Dimension(750, 750));
		canvas = new DrawCanvas(model);
		new CanvasMouseController(canvas, model);
		WindowPane.add(canvas,100);

		paintAutocomplete();
		setUpSideButtons();

		WindowPane.add(sidebarMenu,50);
		WindowPane.add(combo, 50);
		combo.setBounds(10,10,300,40);
		WindowPane.setComponentZOrder(combo,0);
		WindowPane.setComponentZOrder(canvas,1);
		WindowPane.setComponentZOrder(sidebarMenu,0);
		window.add(WindowPane, BorderLayout.CENTER);

		window.pack();
		setUpButtons();
		canvas.setBounds(0,0,window.getWidth(),window.getHeight());

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);

		canvas.pan(-model.getMinLon(), -model.getMaxLat());
		canvas.zoom(canvas.getWidth()/(model.getMaxLon()-model.getMinLon()));
	}

	/**
	 * Makes the autocomplete bar with all the addresses
	 */
	public void paintAutocomplete() {
		this.listItems = new ArrayList();
		this.listItems.addAll(addressModel.getAddressToCordinate().keySet().stream().map(a -> a.toString().toLowerCase()).collect(Collectors.toList()));
		this.listItems.addAll(addressModel.getRegionToShape().keySet().stream().map(a->a.toString().toLowerCase()).collect(Collectors.toList()));
		this.searchable = new StringSearchable(this.listItems);
		this.combo = new AutocompleteJComboBox(this.searchable);
		this.combo.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
		this.combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent event) {
				if (event.getKeyChar() == 10) {
					search();
				}
			}
		});
	}

	public void search() {
		String s = (String) combo.getSelectedItem();
		if (!s.equals("")) {
			//points lat, lon
			if(addressModel.getPoint2DToAddress(s.trim())!=null) {
				double lat = -addressModel.getPoint2DToAddress(s.trim()).getY();
				double lon = -addressModel.getPoint2DToAddress(s.trim()).getX();

				//distance from center of screen in lat lon
				double distanceToCenterY = lat - canvas.getCenterCordinateY();
				double distanceToCenterX = lon - canvas.getCenterCordinateX();


				if (canvas.fancyPan) {
					double distance = Math.sqrt(Math.abs(Math.pow(distanceToCenterX * canvas.getXZoomFactor(), 2) + Math.pow(distanceToCenterY * canvas.getYZoomFactor(), 2)));
					double amountOfZoom = 150000 / canvas.getXZoomFactor();

					if (amountOfZoom >= 2) {
						canvas.panSlowAndThenZoomIn(distanceToCenterX, distanceToCenterY, true);
					} else {
						if (distance < 400) {
							canvas.panSlowAndThenZoomIn(distanceToCenterX, distanceToCenterY, false);
						} else {
							canvas.zoomOutSlowAndThenPan(distanceToCenterX, distanceToCenterY);
						}
					}
				} else if (!canvas.fancyPan) {
					double dx = distanceToCenterX * canvas.getXZoomFactor();
					double dy = distanceToCenterY * canvas.getYZoomFactor();
					canvas.pan(dx, dy);
					canvas.zoomAndCenter();
				}
				canvas.setSearchMode((float) lon, (float) lat);
			} else if(addressModel.getRegion(s.trim())!=null){
				Shape shape = addressModel.getRegion(s.trim()).getShape();
				Point2D center = addressModel.getRegion(s.trim()).getCenter().getPoint2D();
				canvas.regionSearch(shape);
				double lat = -center.getY();
				double lon = -center.getX();


				double distanceToCenterY = lat - canvas.getCenterCordinateY();
				double distanceToCenterX = lon - canvas.getCenterCordinateX();
				double dx = distanceToCenterX * canvas.getXZoomFactor();
				double dy = distanceToCenterY * canvas.getYZoomFactor();
				canvas.pan(dx, dy);
				canvas.pan(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
				canvas.zoom((canvas.getWidth()/(shape.getBounds2D().getMaxX()-shape.getBounds2D().getMinX()))/canvas.getXZoomFactor());
				canvas.pan(canvas.getWidth() / 2, canvas.getHeight() / 2);
				canvas.repaint();
			}
		}
	}

	public void addPOIActionListener(JCheckBoxMenuItem item, POIclasification s){
		item.addActionListener(e-> canvas.setPointsOfInterest(s.toString()));
	}
	public void setUpSideButtons(){
		sidebarMenu.setOpaque(false);
		poiMenu = new JPopupMenu("Points of interest");
		JCheckBoxMenuItem foodAndDrinks = new JCheckBoxMenuItem("Food and drinks");
        addPOIActionListener(foodAndDrinks,POIclasification.FOOD_AND_DRINKS);
		foodAndDrinks.setUI(new StayOpenCheckBoxMenuItemUI());
		JCheckBoxMenuItem attractions = new JCheckBoxMenuItem("Attractions");
        addPOIActionListener(attractions,POIclasification.ATTRACTION);
		attractions.setUI(new StayOpenCheckBoxMenuItemUI());
		JCheckBoxMenuItem nature = new JCheckBoxMenuItem("Nature");
        addPOIActionListener(nature,POIclasification.NATURE);
		nature.setUI(new StayOpenCheckBoxMenuItemUI());
		JCheckBoxMenuItem healthCare = new JCheckBoxMenuItem("Healthcare");
        addPOIActionListener(healthCare,POIclasification.HEALTH_CARE);
		healthCare.setUI(new StayOpenCheckBoxMenuItemUI());
		JCheckBoxMenuItem utilities = new JCheckBoxMenuItem("Utilities");
        addPOIActionListener(utilities,POIclasification.UTILITIES);
		utilities.setUI(new StayOpenCheckBoxMenuItemUI());
		JCheckBoxMenuItem emergency = new JCheckBoxMenuItem("Emergency");
        addPOIActionListener(emergency,POIclasification.EMERGENCY);
		emergency.setUI(new StayOpenCheckBoxMenuItemUI());
		JCheckBoxMenuItem shops = new JCheckBoxMenuItem("Shops");
        addPOIActionListener(shops,POIclasification.SHOPS);
		shops.setUI(new StayOpenCheckBoxMenuItemUI());
		poiMenu.add(foodAndDrinks);
		poiMenu.add(attractions);
		poiMenu.add(nature);
		poiMenu.add(healthCare);
		poiMenu.add(utilities);
		poiMenu.add(emergency);
		poiMenu.add(shops);



		zoomIn = new JButton();
		zoomIn.setBounds(window.getHeight()-45,window.getWidth()-67,40,40);

		try {
			Image img3 = ImageIO.read(getClass().getResource("/zoomin.png"));
			zoomIn.setIcon(new ImageIcon(img3.getScaledInstance(40,40,Image.SCALE_SMOOTH)));

		}catch (Exception ex){
			System.out.println(ex);
		}
		zoomIn.setBorderPainted(false);
		zoomIn.setFocusPainted(false);
		zoomIn.setContentAreaFilled(false);
		sidebarMenu.add(zoomIn);

		zoomIn.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				canvas.pan(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
				canvas.zoom(1.25);
				canvas.pan(canvas.getWidth()/ 2, canvas.getHeight() / 2);			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});

		zoomOut = new JButton();
		zoomOut.setBounds(window.getHeight()-85,window.getWidth()-67,40,40);

		try {
			Image img4 = ImageIO.read(getClass().getResource("/zoomout.png"));
			zoomOut.setIcon(new ImageIcon(img4.getScaledInstance(40,40,Image.SCALE_SMOOTH)));

		}catch (Exception ex){
			System.out.println(ex);
		}
		zoomOut.setBorderPainted(false);
		zoomOut.setFocusPainted(false);
		zoomOut.setContentAreaFilled(false);
		sidebarMenu.add(zoomOut);


		zoomOut.setPreferredSize(new Dimension(30,30));
		zoomOut.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				canvas.pan(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
				canvas.zoom(0.75);
				canvas.pan(canvas.getWidth()/ 2, canvas.getHeight() / 2);			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

		});

		pointsOfInterest = new JButton();
		pointsOfInterest.setBounds(window.getHeight()-85,window.getWidth()-67,40,40);

		try {
			Image img4 = ImageIO.read(getClass().getResource("/pointsOfInterest.png"));
			pointsOfInterest.setIcon(new ImageIcon(img4.getScaledInstance(40,40,Image.SCALE_SMOOTH)));

		}catch (Exception ex){
			System.out.println(ex);
		}
		pointsOfInterest.setBorderPainted(false);
		pointsOfInterest.setFocusPainted(false);
		pointsOfInterest.setContentAreaFilled(false);
		sidebarMenu.add(pointsOfInterest);


		pointsOfInterest.setPreferredSize(new Dimension(30,30));
		pointsOfInterest.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {
				if(!isClicked1){
					poiMenu.show(e.getComponent(),0,40);
					isClicked1=true;
				}else if(isClicked1){
					poiMenu.setVisible(false);
					isClicked1=false;
				}
				canvas.repaint();
			}


			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

		});



	}

	public void setUpButtons(){
		JButton search = new JButton();
		try {
			Image img = ImageIO.read(getClass().getResource("/search.png"));
			search.setIcon(new ImageIcon(img.getScaledInstance(40,40, Image.SCALE_SMOOTH)));

		} catch (Exception ex) {
			System.out.println(ex);
		}
		search.setBorderPainted(false);
		search.setFocusPainted(false);
		search.setContentAreaFilled(false);
		search.setBorder(BorderFactory.createEmptyBorder());
		search.setPreferredSize(new Dimension(30,30));
		search.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				search();
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		WindowPane.add(search);
		WindowPane.setComponentZOrder(search,1);
		search.setBounds(313,10,40,40);

		JButton menu = new JButton();
		menu.setBounds(357,10,40,40);

		try {
			Image img2 = ImageIO.read(getClass().getResource("/Untitled.png"));
			menu.setIcon(new ImageIcon(img2.getScaledInstance(40,40, Image.SCALE_SMOOTH)));

		} catch (Exception ex) {
			System.out.println(ex);
		}
		menu.setBorderPainted(false);
		menu.setFocusPainted(false);
		menu.setContentAreaFilled(false);

		menu.setPreferredSize(new Dimension(30,30));
		setUpMenu();

		menu.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {
				if(!isClicked2){
					popUpMenu.show(e.getComponent(),-100,40);
					isClicked2=true;
				}else if(isClicked2){
					popUpMenu.setVisible(false);
					isClicked2=false;
				}
				canvas.repaint();
			}
			@Override
			public void mouseReleased(MouseEvent e) {canvas.repaint();}
			@Override
			public void mouseEntered(MouseEvent e) {canvas.repaint();}
			@Override
			public void mouseExited(MouseEvent e) {canvas.repaint();}
		});
		menu.setComponentPopupMenu(popUpMenu);
		WindowPane.add(menu);
		WindowPane.setComponentZOrder(menu,0);


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

	public void setUpNightMode(JComboBox combo, JPopupMenu menu, JMenuItem tools,  JPopupMenu popUpMenu){
		menu.setBackground(new Color(36,47,62));
		combo.getEditor().getEditorComponent().setBackground(new Color(36,47,62));
		JTextComponent component = (JTextComponent) combo.getEditor().getEditorComponent();
		component.setForeground(Color.WHITE);
		component.setCaretColor(Color.WHITE);
		BasicComboPopup popup = (BasicComboPopup)combo.getAccessibleContext().getAccessibleChild(0);
		JList list = popup.getList();
		list.setBackground(new Color(36,47,62));
		list.setForeground(Color.WHITE);
		tools.setForeground(Color.WHITE);
		popUpMenu.setForeground(Color.WHITE);

	}

	public void tearDownNightMode(JComboBox combo, JPopupMenu menu, JMenuItem tools, JPopupMenu popUpMenu){
		menu.setBackground(null);
		combo.getEditor().getEditorComponent().setBackground(Color.WHITE);
		JTextComponent component = (JTextComponent) combo.getEditor().getEditorComponent();
		component.setForeground(Color.BLACK);
		component.setCaretColor(Color.BLACK);
		BasicComboPopup popup = (BasicComboPopup)combo.getAccessibleContext().getAccessibleChild(0);
		JList list = popup.getList();
		list.setBackground(null);
		list.setForeground(null);
		tools.setForeground(Color.BLACK);
		popUpMenu.setForeground(Color.BLACK);

	}
	public void addKeyListeners(KeyStroke stroke, String action, JMenuItem clicker){
		WindowPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke,action);
		WindowPane.getActionMap().put(action, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clicker.doClick();
			}
		});
	}

	public void setUpMenu() {
		popUpMenu = new JPopupMenu("Options");

		JMenuItem save = new JMenuItem("Save", KeyEvent.VK_S);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_S,Event.CTRL_MASK),"action5",save);

		JMenuItem load = new JMenuItem("Load", KeyEvent.VK_L);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_L,Event.CTRL_MASK),"action6",load);

		JMenuItem exit = new JMenuItem("Exit", KeyEvent.VK_Q);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_Q,Event.CTRL_MASK),"action7",exit);

		JCheckBoxMenuItem directions = new JCheckBoxMenuItem("Directions");


		popUpMenu.add(save);
		popUpMenu.add(load);
		popUpMenu.add(directions);
		popUpMenu.addSeparator();

		JMenu tools = new JMenu("Tools");
		JMenuItem zoomIn = new JMenuItem("Zoom In (CTRL-MINUS)", KeyEvent.VK_PLUS);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS,Event.CTRL_MASK),"action1",zoomIn);

		JMenuItem zoomOut = new JMenuItem("Zoom Out (CTRL-PLUS)", KeyEvent.VK_MINUS);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,Event.CTRL_MASK),"action2",zoomOut);

		JMenuItem greyScale = new JMenuItem("GreyScale (CTRL-G)", KeyEvent.VK_G);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_G,Event.CTRL_MASK),"action3",greyScale);

		JMenuItem nightMode = new JMenuItem("NightMode (CTRL-N)", KeyEvent.VK_N);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_N,Event.CTRL_MASK),"action4",nightMode);

		JMenuItem fancyPan = new JMenuItem("FancyPan (CTRL-F)", KeyEvent.VK_F);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_F,Event.CTRL_MASK),"action9", fancyPan);

		JMenuItem aA = new JMenuItem("AntiAliasing (CTRL-T)", KeyEvent.VK_T);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_T,Event.CTRL_MASK),"action11", aA);

		tools.add(nightMode);
		tools.add(greyScale);
		tools.add(fancyPan);
		tools.add(aA);

		tools.add(zoomIn);
		tools.add(zoomOut);
		popUpMenu.add(tools);
		nightMode.addActionListener(e->{
			if(nightMode.getText().equals("NightMode")){
				canvas.setNightMode();
				canvas.setGreyScaleFalse();
				greyScale.setText("GreyScale");
				canvas.repaint();
				nightMode.setText("Color");
				setUpNightMode(combo,popUpMenu,tools,popUpMenu);


			} else {
				canvas.setNightModeFalse();
				tearDownNightMode(combo,popUpMenu,tools,popUpMenu);
				canvas.repaint();
				nightMode.setText("NightMode");
			}
		});

		greyScale.addActionListener(e->{
			if(greyScale.getText().equals("GreyScale")) {
				canvas.setGreyScale();
				canvas.setNightModeFalse();
				if(nightMode.getText().equals("Color")) {
					nightMode.setText("NightMode");
					tearDownNightMode(combo,popUpMenu,tools,popUpMenu);
				}
				canvas.repaint();
				greyScale.setText("Color");} else
			{
				canvas.setGreyScaleFalse();
				canvas.repaint();
				greyScale.setText("GreyScale");
			}
		});

		fancyPan.addActionListener(e->{
			canvas.toggleFancyPan();
		});

		aA.addActionListener(e->{
			canvas.toggleAA();
		});

		//metode til at save
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();

				fileChooser.setDialogTitle("Choose save location");

				int userSelection = fileChooser.showSaveDialog(window);

				if (userSelection == JFileChooser.APPROVE_OPTION) {
					File fileToSave = fileChooser.getSelectedFile();

					model.save(fileToSave.getAbsolutePath() + ".bin");
				}
			}
		});

		//metode til at loade
		load.addActionListener(new ActionListener() {
			boolean first = true;
			File currentPath;

			@Override
			public void actionPerformed(ActionEvent e) {
				if(first) {
					currentPath = null;
				}

				JFileChooser fileChooser = new JFileChooser();
				if(currentPath != null) {
					fileChooser.setCurrentDirectory(currentPath);
				}

				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(File file) {
						return file.getName().endsWith(".osm") || file.getName().endsWith(".bin") || file.getName().endsWith(".zip") || (file.isDirectory() && !file.getName().endsWith(".app"));
					}

					@Override
					public String getDescription() {
						return ".osm files, .bin files or .zip files";
					}
				});
				fileChooser.setDialogTitle("Choose file to load");

				int userSelection = fileChooser.showOpenDialog(window);
				if (userSelection == JFileChooser.APPROVE_OPTION) {

					File fileToLoad = fileChooser.getSelectedFile();
					if(fileChooser.accept(fileToLoad) && fileToLoad.exists()){
						model.load(fileToLoad.getAbsolutePath());
						window.dispose();
						DrawWindow a = new DrawWindow(model);
						first = true;
					}
					else if(!fileChooser.accept(fileToLoad)){
						JOptionPane.showMessageDialog(window, "You must choose a correct filetype to load");
						currentPath = fileChooser.getCurrentDirectory();
						first = false;
						load.doClick();

					}
					else if(!fileToLoad.exists()){
						JOptionPane.showMessageDialog(window, "File does not exist");
						currentPath = fileChooser.getCurrentDirectory();
						first = false;
						load.doClick();
					}

				}


			}
		});
		directions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUpDirectionsMenu = !setUpDirectionsMenu;
				SetAndTearSecondSearch();
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
				canvas.pan(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
				canvas.zoom(1.25);
				canvas.pan(canvas.getWidth()/ 2, canvas.getHeight() / 2);
			}
		});
		zoomOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canvas.pan(-canvas.getWidth() / 2, -canvas.getHeight() / 2);
				canvas.zoom(0.75);
				canvas.pan(canvas.getWidth() / 2, canvas.getHeight() / 2);
			}
		});
		window.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				canvas.setBounds(0,0,window.getWidth(),window.getHeight());
				sidebarMenu.setBounds(canvas.getWidth()-60,10,40,130);
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
		});

	}

	public void SetAndTearSecondSearch(){
		if(setUpDirectionsMenu){
			secondCombo = new AutocompleteJComboBox(searchable);
			WindowPane.add(secondCombo,75);
			WindowPane.setComponentZOrder(secondCombo,2);

			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {

				int i = 1;
				int yStart =10;
				@Override
				public void run() {
					secondCombo.setBounds(10,yStart+=1,300,40);
					canvas.repaint();

					if(yStart==50){cancel();
						try {
							BufferedImage bar = ImageIO.read(getClass().getResource("/Search Bar.png"));
							barImage = new JLabel(new ImageIcon(bar));
							WindowPane.add(barImage,76);
							WindowPane.setComponentZOrder(barImage,0);
							barImage.setBounds(11,31,298,40);

						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}
			},0, 5);
			secondCombo.setEditable(true);


		}else if(!setUpDirectionsMenu){
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				int i = 1;
				int yStart =51;
				@Override
				public void run() {
					secondCombo.setBounds(10,yStart-=1,300,40);
					canvas.repaint();

					if(yStart==10){
						cancel();
						WindowPane.remove(secondCombo);
						WindowPane.remove(barImage);
						canvas.repaint();
					}
				}
			},0, 5);

		}

	}

}