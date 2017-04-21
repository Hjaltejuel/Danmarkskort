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
public class DrawWindow {
	JFrame window;
	Model model;
	DrawCanvas canvas;
	private StringSearchable searchable;
	private AutocompleteJComboBox combo;
	private AutocompleteJComboBox secondCombo;
	private JLayeredPane windowPane;
	private JPopupMenu popUpMenu;
	private JPopupMenu poiMenu;
	private JLabel barImage;
	private JMenuItem save;
	private JMenuItem load;
	private JMenuItem exit;
	JCheckBoxMenuItem directions;
	private JButton search;
	private JButton menu;
	private JButton zoomIn;
	private JButton zoomOut;
	private JButton pointsOfInterest;
	private JCheckBoxMenuItem[] pointsOfInterestMenues;

	private JPanel sidebarMenu  = new JPanel(new GridLayout(0,1));
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
		pointsOfInterestMenues = new JCheckBoxMenuItem[7];
		window = new JFrame("Danmarkskort gruppe A");
		windowPane = new JLayeredPane();
		window.setPreferredSize(new Dimension(750, 750));
		canvas = new DrawCanvas(model);
		new CanvasMouseController(canvas, model);

		setUpSideButtons();

		window.pack();
		setUpTopButtons();
		canvas.setBounds(0,0,window.getWidth(),window.getHeight());

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);

		canvas.pan(-model.getMinLon(), -model.getMaxLat());
		canvas.zoom(canvas.getWidth()/(model.getMaxLon()-model.getMinLon()));
	}

	public void setKeyListener(KeyListener controller){
		combo.getEditor().getEditorComponent().addKeyListener((controller));
	}
	public void setMouseListener(MouseListener controller){
		search.addMouseListener(controller);
		zoomIn.addMouseListener(controller);
		zoomOut.addMouseListener(controller);
		pointsOfInterest.addMouseListener(controller);
		menu.addMouseListener(controller);
	}

	public void addActionListener(ActionListener controller){
		for(JCheckBoxMenuItem items: pointsOfInterestMenues){
			items.addActionListener(controller);
		}
		load.addActionListener(controller);
		load.setActionCommand("Load");
		save.addActionListener(controller);
		save.setActionCommand("Save");
		exit.addActionListener(controller);
		exit.setActionCommand("Exit");
		directions.addActionListener(controller);
		directions.setActionCommand("Directions");
	}

	public AutocompleteJComboBox getCombo(){
		return combo;
	}
	public JCheckBoxMenuItem[] getPointsOfInterestMenues(){return pointsOfInterestMenues;}

	public JButton getSearch(){
		return search;
	}
	public JButton getMenu(){return menu;}
	public JPopupMenu getPopUpMenu(){return popUpMenu;}
	public JPopupMenu getPoiPopUpMenu(){return poiMenu;}
	public JButton getPointsOfInterest(){return pointsOfInterest;}
	public JButton getZoomIn(){return zoomIn;}
	public DrawCanvas getCanvas(){return canvas;}
	public JButton getZoomOut(){return zoomOut;}
	public JFrame getWindow(){return window;}


	public void setComponentzZOrder(){
		windowPane.add(canvas,100);
		windowPane.add(combo, 50);
		windowPane.add(sidebarMenu,50);
		windowPane.add(search);

		windowPane.setComponentZOrder(canvas,1);
		windowPane.setComponentZOrder(search,0);
		windowPane.setComponentZOrder(sidebarMenu,0);
		windowPane.setComponentZOrder(combo,0);
		
		search.setBounds(313,10,40,40);
		combo.setBounds(10,10,300,40);
		window.add(windowPane, BorderLayout.CENTER);

	}
	/**
	 * Makes the autocomplete bar with all the addresses
	 */
	public void createAutocomplete(ArrayList listItems){
		this.searchable = new StringSearchable(listItems);
		combo = new AutocompleteJComboBox(this.searchable);
		combo.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
	}
	public void setUpSideButtons(){
		sidebarMenu.setOpaque(false);
		int i = 0;

		poiMenu = new JPopupMenu("Points of interest");
		JCheckBoxMenuItem foodAndDrinks = new JCheckBoxMenuItem("Food and drinks");
		JCheckBoxMenuItem attractions = new JCheckBoxMenuItem("Attractions");
		JCheckBoxMenuItem nature = new JCheckBoxMenuItem("Nature");
		JCheckBoxMenuItem healthCare = new JCheckBoxMenuItem("Healthcare");
		JCheckBoxMenuItem utilities = new JCheckBoxMenuItem("Utilities");
		JCheckBoxMenuItem emergency = new JCheckBoxMenuItem("Emergency");
		JCheckBoxMenuItem shops = new JCheckBoxMenuItem("Shops");

		pointsOfInterestMenues[i++]=foodAndDrinks;
		pointsOfInterestMenues[i++]=attractions;
		pointsOfInterestMenues[i++]=nature;
		pointsOfInterestMenues[i++]=healthCare;
		pointsOfInterestMenues[i++]=utilities;
		pointsOfInterestMenues[i++]=emergency;
		pointsOfInterestMenues[i++]=shops;

		foodAndDrinks.setUI(new StayOpenCheckBoxMenuItemUI());
		attractions.setUI(new StayOpenCheckBoxMenuItemUI());
		nature.setUI(new StayOpenCheckBoxMenuItemUI());
		healthCare.setUI(new StayOpenCheckBoxMenuItemUI());
		utilities.setUI(new StayOpenCheckBoxMenuItemUI());
		emergency.setUI(new StayOpenCheckBoxMenuItemUI());
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
		zoomIn.setBorderPainted(false);
		zoomIn.setFocusPainted(false);
		zoomIn.setContentAreaFilled(false);

		zoomOut = new JButton();
		zoomOut.setBounds(window.getHeight()-85,window.getWidth()-67,40,40);
		zoomOut.setBorderPainted(false);
		zoomOut.setFocusPainted(false);
		zoomOut.setContentAreaFilled(false);
		zoomOut.setPreferredSize(new Dimension(30,30));

		pointsOfInterest = new JButton();
		pointsOfInterest.setBounds(window.getHeight()-85,window.getWidth()-67,40,40);
		pointsOfInterest.setBorderPainted(false);
		pointsOfInterest.setFocusPainted(false);
		pointsOfInterest.setContentAreaFilled(false);
		pointsOfInterest.setPreferredSize(new Dimension(30,30));

		sidebarMenu.add(zoomIn);
		sidebarMenu.add(zoomOut);
		sidebarMenu.add(pointsOfInterest);

		giveSideButtonsIcons();



}
	public void giveSideButtonsIcons(){
		try {
			Image img4 = ImageIO.read(getClass().getResource("/pointsOfInterest.png"));
			pointsOfInterest.setIcon(new ImageIcon(img4.getScaledInstance(40,40,Image.SCALE_SMOOTH)));

		}catch (Exception ex){
			System.out.println(ex);
		}
		try {
			Image img4 = ImageIO.read(getClass().getResource("/zoomout.png"));
			zoomOut.setIcon(new ImageIcon(img4.getScaledInstance(40,40,Image.SCALE_SMOOTH)));
		}catch (Exception ex){
			System.out.println(ex);
		}
		try {
			Image img3 = ImageIO.read(getClass().getResource("/zoomin.png"));
			zoomIn.setIcon(new ImageIcon(img3.getScaledInstance(40,40,Image.SCALE_SMOOTH)));
		}catch (Exception ex){
			System.out.println(ex);
		}
	}

	public void setUpTopButtons(){
		search = new JButton();
		search.setBorderPainted(false);
		search.setFocusPainted(false);
		search.setContentAreaFilled(false);
		search.setBorder(BorderFactory.createEmptyBorder());
		search.setPreferredSize(new Dimension(30,30));

		menu = new JButton();
		menu.setBounds(357,10,40,40);
		menu.setBorderPainted(false);
		menu.setFocusPainted(false);
		menu.setContentAreaFilled(false);
		menu.setPreferredSize(new Dimension(30,30));
		setUpMenu();
		menu.setComponentPopupMenu(popUpMenu);
		windowPane.add(menu);
		windowPane.setComponentZOrder(menu,0);
		setTopMenuIcons();
	}

	public void setTopMenuIcons(){
		try {
			Image img = ImageIO.read(getClass().getResource("/search.png"));
			search.setIcon(new ImageIcon(img.getScaledInstance(40,40, Image.SCALE_SMOOTH)));

		} catch (Exception ex) {
			System.out.println(ex);
		}
		try {
			Image img2 = ImageIO.read(getClass().getResource("/Untitled.png"));
			menu.setIcon(new ImageIcon(img2.getScaledInstance(40,40, Image.SCALE_SMOOTH)));

		} catch (Exception ex) {
			System.out.println(ex);
		}
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
		windowPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke,action);
		windowPane.getActionMap().put(action, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clicker.doClick();
			}
		});
	}

	public void setUpMenu() {
		popUpMenu = new JPopupMenu("Options");

		save = new JMenuItem("Save", KeyEvent.VK_S);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_S,Event.CTRL_MASK),"action5",save);

		load = new JMenuItem("Load", KeyEvent.VK_L);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_L,Event.CTRL_MASK),"action6",load);

		exit = new JMenuItem("Exit", KeyEvent.VK_Q);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_Q,Event.CTRL_MASK),"action7",exit);

		directions = new JCheckBoxMenuItem("Directions");


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
			windowPane.add(secondCombo,75);
			windowPane.setComponentZOrder(secondCombo,2);

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
							windowPane.add(barImage,76);
							windowPane.setComponentZOrder(barImage,0);
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
						windowPane.remove(secondCombo);
						windowPane.remove(barImage);
						canvas.repaint();
					}
				}
			},0, 5);

		}

	}

}