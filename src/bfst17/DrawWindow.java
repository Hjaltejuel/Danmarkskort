package bfst17;

		import javax.imageio.ImageIO;
		import javax.swing.*;
		import javax.swing.plaf.basic.BasicComboPopup;
		import javax.swing.text.JTextComponent;
		import java.awt.*;
		import java.awt.event.*;
		import java.awt.image.BufferedImage;
		import java.io.IOException;
		import java.util.*;
		import java.util.Timer;

/**
 * Created by trold on 2/1/17.
 */
public class DrawWindow {
	JFrame window;
	private AutocompleteJComboBox combo;
	private AutocompleteJComboBox secondCombo;
	private JLayeredPane windowPane;
	private JPopupMenu popUpMenu;
	private JPopupMenu poiMenu;
	private JLabel barImage;
	private JMenuItem save;
	private JMenuItem load;
	private JMenuItem exit;
	private JMenuItem zoomInMenu;
	private JMenuItem zoomOutMenu;
	private JMenuItem greyScaleMenuItem;
	private JMenuItem nightModeMenuItem;
	private JMenuItem AntiAliasinToggle;
	private JMenuItem fancyPan;
	JCheckBoxMenuItem directions;
	private JButton searchButton;
	private JButton menuButton;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	private JButton pointsOfInterestButton;
	private JCheckBoxMenuItem[] pointsOfInterestMenues;
	boolean isClicked1 = false;
	boolean isClicked2 = false;

	private JPanel sidebarMenu = new JPanel(new GridLayout(0, 1));


	public DrawWindow() {
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
		setUpSideButtons();
		window.pack();
		setUpTopButtons();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}


	public void setComponentListener(ComponentListener controller) {
		window.addComponentListener(controller);
	}

	public void setKeyListener(KeyListener controller) {
		combo.getEditor().getEditorComponent().addKeyListener((controller));
	}

	public void setMouseListener(MouseListener controller) {
		searchButton.addMouseListener(controller);
		zoomInButton.addMouseListener(controller);
		zoomOutButton.addMouseListener(controller);
		pointsOfInterestButton.addMouseListener(controller);
		menuButton.addMouseListener(controller);
	}

	public void addActionListener(ActionListener controller) {
		for (JCheckBoxMenuItem items : pointsOfInterestMenues) {
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
		zoomInMenu.addActionListener(controller);
		zoomInMenu.setActionCommand("ZoomIn");
		zoomOutMenu.addActionListener(controller);
		zoomOutMenu.setActionCommand("ZoomOut");
		greyScaleMenuItem.addActionListener(controller);
		greyScaleMenuItem.setActionCommand("Greyscale");
		nightModeMenuItem.addActionListener(controller);
		nightModeMenuItem.setActionCommand("Nightmode");
		AntiAliasinToggle.addActionListener(controller);
		AntiAliasinToggle.setActionCommand("Aa");
		fancyPan.addActionListener(controller);
		fancyPan.setActionCommand("Fancypan");
	}

	public void setComponentzZOrder(DrawCanvas canvas) {
		windowPane.add(canvas, 100);
		windowPane.add(combo, 50);
		windowPane.add(sidebarMenu, 50);
		windowPane.add(searchButton);

		windowPane.setComponentZOrder(canvas, 1);
		windowPane.setComponentZOrder(searchButton, 0);
		windowPane.setComponentZOrder(sidebarMenu, 0);
		windowPane.setComponentZOrder(combo, 0);

		searchButton.setBounds(313, 10, 40, 40);
		combo.setBounds(10, 10, 300, 40);
		window.add(windowPane, BorderLayout.CENTER);

	}

	/**
	 * Makes the autocomplete bar with all the addresses
	 */
	public void createAutocomplete(TST tree) {
		combo = new AutocompleteJComboBox(tree);
		combo.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
	}

	public void setUpSideButtons() {
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

		pointsOfInterestMenues[i++] = foodAndDrinks;
		pointsOfInterestMenues[i++] = attractions;
		pointsOfInterestMenues[i++] = nature;
		pointsOfInterestMenues[i++] = healthCare;
		pointsOfInterestMenues[i++] = utilities;
		pointsOfInterestMenues[i++] = emergency;
		pointsOfInterestMenues[i++] = shops;

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

		zoomInButton = new JButton();
		zoomInButton.setBounds(window.getHeight() - 45, window.getWidth() - 67, 40, 40);
		zoomInButton.setBorderPainted(false);
		zoomInButton.setFocusPainted(false);
		zoomInButton.setContentAreaFilled(false);

		zoomOutButton = new JButton();
		zoomOutButton.setBounds(window.getHeight() - 85, window.getWidth() - 67, 40, 40);
		zoomOutButton.setBorderPainted(false);
		zoomOutButton.setFocusPainted(false);
		zoomOutButton.setContentAreaFilled(false);
		zoomOutButton.setPreferredSize(new Dimension(30, 30));

		pointsOfInterestButton = new JButton();
		pointsOfInterestButton.setBounds(window.getHeight() - 85, window.getWidth() - 67, 40, 40);
		pointsOfInterestButton.setBorderPainted(false);
		pointsOfInterestButton.setFocusPainted(false);
		pointsOfInterestButton.setContentAreaFilled(false);
		pointsOfInterestButton.setPreferredSize(new Dimension(30, 30));

		sidebarMenu.add(zoomInButton);
		sidebarMenu.add(zoomOutButton);
		sidebarMenu.add(pointsOfInterestButton);

		giveSideButtonsIcons();


	}

	public void giveSideButtonsIcons() {
		try {
			Image img4 = ImageIO.read(getClass().getResource("/pointsOfInterestButton.png"));
			pointsOfInterestButton.setIcon(new ImageIcon(img4.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

		} catch (Exception ex) {
			System.out.println(ex);
		}
		try {
			Image img4 = ImageIO.read(getClass().getResource("/zoomout.png"));
			zoomOutButton.setIcon(new ImageIcon(img4.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
		} catch (Exception ex) {
			System.out.println(ex);
		}
		try {
			Image img3 = ImageIO.read(getClass().getResource("/zoomin.png"));
			zoomInButton.setIcon(new ImageIcon(img3.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	public void setUpTopButtons() {
		searchButton = new JButton();
		searchButton.setBorderPainted(false);
		searchButton.setFocusPainted(false);
		searchButton.setContentAreaFilled(false);
		searchButton.setBorder(BorderFactory.createEmptyBorder());
		searchButton.setPreferredSize(new Dimension(30, 30));

		menuButton = new JButton();
		menuButton.setBounds(357, 10, 40, 40);
		menuButton.setBorderPainted(false);
		menuButton.setFocusPainted(false);
		menuButton.setContentAreaFilled(false);
		menuButton.setPreferredSize(new Dimension(30, 30));
		setUpMenu();
		menuButton.setComponentPopupMenu(popUpMenu);
		windowPane.add(menuButton);
		windowPane.setComponentZOrder(menuButton, 0);
		setTopMenuIcons();
	}

	public void setTopMenuIcons() {
		try {
			Image img = ImageIO.read(getClass().getResource("/search.png"));
			searchButton.setIcon(new ImageIcon(img.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

		} catch (Exception ex) {
			System.out.println(ex);
		}
		try {
			Image img2 = ImageIO.read(getClass().getResource("/Untitled.png"));
			menuButton.setIcon(new ImageIcon(img2.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

		} catch (Exception ex) {
			System.out.println(ex);
		}
	}


	public void setUpNightMode() {
		menuButton.setBackground(new Color(36, 47, 62));
		combo.getEditor().getEditorComponent().setBackground(new Color(36, 47, 62));
		JTextComponent component = (JTextComponent) combo.getEditor().getEditorComponent();
		component.setForeground(Color.WHITE);
		component.setCaretColor(Color.WHITE);
		BasicComboPopup popup = (BasicComboPopup) combo.getAccessibleContext().getAccessibleChild(0);
		JList list = popup.getList();
		list.setBackground(new Color(36, 47, 62));
		list.setForeground(Color.WHITE);
		popUpMenu.setForeground(Color.WHITE);

	}

	public void tearDownNightMode() {
		menuButton.setBackground(null);
		combo.getEditor().getEditorComponent().setBackground(Color.WHITE);
		JTextComponent component = (JTextComponent) combo.getEditor().getEditorComponent();
		component.setForeground(Color.BLACK);
		component.setCaretColor(Color.BLACK);
		BasicComboPopup popup = (BasicComboPopup) combo.getAccessibleContext().getAccessibleChild(0);
		JList list = popup.getList();
		list.setBackground(null);
		list.setForeground(null);
		popUpMenu.setForeground(Color.BLACK);

	}

	public void addKeyListeners(KeyStroke stroke, String action, JMenuItem clicker) {
		windowPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, action);
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
		load = new JMenuItem("Load", KeyEvent.VK_L);
		exit = new JMenuItem("Exit", KeyEvent.VK_Q);
		zoomInMenu = new JMenuItem("Zoom In (CTRL-MINUS)", KeyEvent.VK_PLUS);
		zoomOutMenu = new JMenuItem("Zoom Out (CTRL-PLUS)", KeyEvent.VK_MINUS);
		greyScaleMenuItem = new JMenuItem("GreyScale (CTRL-G)", KeyEvent.VK_G);
		nightModeMenuItem = new JMenuItem("NightMode (CTRL-N)", KeyEvent.VK_N);
		fancyPan = new JMenuItem("FancyPan (CTRL-F)", KeyEvent.VK_F);
		AntiAliasinToggle = new JMenuItem("AntiAliasing (CTRL-T)", KeyEvent.VK_T);
		directions = new JCheckBoxMenuItem("Directions");

		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK), "action5", save);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK), "action6", load);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK), "action7", exit);

		popUpMenu.add(save);
		popUpMenu.add(load);
		popUpMenu.add(directions);
		popUpMenu.addSeparator();

		JMenu tools = new JMenu("Tools");
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Event.CTRL_MASK), "action1", zoomInMenu);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Event.CTRL_MASK), "action2", zoomOutMenu);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_G, Event.CTRL_MASK), "action3", greyScaleMenuItem);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK), "action4", nightModeMenuItem);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK), "action9", fancyPan);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK), "action11", AntiAliasinToggle);

		tools.add(nightModeMenuItem);
		tools.add(greyScaleMenuItem);
		tools.add(fancyPan);
		tools.add(AntiAliasinToggle);
		tools.add(zoomInMenu);
		tools.add(zoomOutMenu);

		popUpMenu.add(tools);


	}

	public void showMenuTwo() {
		if (!isClicked2) {
			popUpMenu.show(menuButton, 0, 40);
			isClicked2 = true;
		} else if (isClicked2) {
			popUpMenu.setVisible(false);
			isClicked2 = false;
		}
		window.repaint();
	}

	public void showMenuOne() {
		if (!isClicked1) {
			poiMenu.show(sidebarMenu, 0, 130);
			isClicked1 = true;
		} else if (isClicked1) {
			poiMenu.setVisible(false);
			isClicked1 = false;
		}
		window.repaint();
	}

	public void setBounds(DrawCanvas canvas) {
		canvas.setBounds(0, 0, window.getWidth(), window.getHeight());
		sidebarMenu.setBounds(canvas.getWidth() - 60, 10, 40, 130);
	}

	public void SetSecondSearch(TST tree) {
		secondCombo = new AutocompleteJComboBox(tree);
		windowPane.add(secondCombo, 75);
		windowPane.setComponentZOrder(secondCombo, 2);

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			int i = 1;
			int yStart = 10;

			@Override
			public void run() {
				secondCombo.setBounds(10, yStart += 1, 300, 40);
				window.repaint();

				if (yStart == 50) {
					cancel();
					try {
						BufferedImage bar = ImageIO.read(getClass().getResource("/Search Bar.png"));
						barImage = new JLabel(new ImageIcon(bar));
						windowPane.add(barImage, 76);
						windowPane.setComponentZOrder(barImage, 0);
						barImage.setBounds(11, 31, 298, 40);

					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}, 0, 5);
		secondCombo.setEditable(true);
	}


	public void tearSecondSearch() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			int i = 1;
			int yStart = 51;

			@Override
			public void run() {
				secondCombo.setBounds(10, yStart -= 1, 300, 40);
				window.repaint();

				if (yStart == 10) {
					cancel();
					windowPane.remove(secondCombo);
					windowPane.remove(barImage);
					window.repaint();
				}
			}
		}, 0, 5);

	}


	public AutocompleteJComboBox getCombo() {
		return combo;
	}

	public JCheckBoxMenuItem getDirections() {
		return directions;
	}

	public JCheckBoxMenuItem[] getPointsOfInterestMenues() {
		return pointsOfInterestMenues;
	}

	public JButton getSearchButton() {
		return searchButton;
	}

	public JButton getMenuButton() {
		return menuButton;
	}

	public JButton getPointsOfInterestButton() {
		return pointsOfInterestButton;
	}

	public JButton getZoomInButton() {
		return zoomInButton;
	}

	public JButton getZoomOutButton() {
		return zoomOutButton;
	}

	public JFrame getWindow() {
		return window;
	}

	public JMenuItem getNightModeMenuItem() {
		return nightModeMenuItem;
	}

	public JMenuItem getGreyScaleMenuItem() {
		return greyScaleMenuItem;
	}


	public void loadProgress(){
		JLabel progess = new JLabel("goddav");
		window.add(progess);
	}
}
