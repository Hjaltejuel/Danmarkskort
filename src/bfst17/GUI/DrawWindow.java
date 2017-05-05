package bfst17.GUI;

import bfst17.AddressHandling.TST;

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
	private JMenuItem showCityNames;
	private JMenuItem exit;
	private JMenuItem zoomInMenuItem;
	private JMenuItem zoomOutMenuItem;
	private JMenuItem greyScaleMenuItem;
	private JMenuItem nightModeMenuItem;
	private JMenuItem AntiAliasingToggle;
	private JMenuItem fancyPan;
	JCheckBoxMenuItem directions;
	private ImageButton searchButton;
	private ImageButton menuButton;
	private ImageButton zoomInButton;
	private ImageButton zoomOutButton;
	private ImageButton pointsOfInterestButton;
	private JCheckBoxMenuItem[] POICheckBoxArray;
	boolean menu1IsShown = false;
	boolean menu2IsShown = false;

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
		POICheckBoxArray = new JCheckBoxMenuItem[7];
		window = new JFrame("Danmarkskort gruppe A");
		windowPane = new JLayeredPane();
		window.setPreferredSize(new Dimension(750, 750));
		window.pack();
		setUpButtons();
		setUpPOIItems();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

	private void setUpPOIItems() {
		poiMenu = new JPopupMenu("Points of interest");
		JCheckBoxMenuItem foodAndDrinks = new JCheckBoxMenuItem("Food and drinks");
		JCheckBoxMenuItem attractions = new JCheckBoxMenuItem("Attractions");
		JCheckBoxMenuItem nature = new JCheckBoxMenuItem("Nature");
		JCheckBoxMenuItem healthCare = new JCheckBoxMenuItem("Healthcare");
		JCheckBoxMenuItem utilities = new JCheckBoxMenuItem("Utilities");
		JCheckBoxMenuItem emergency = new JCheckBoxMenuItem("Emergency");
		JCheckBoxMenuItem shops = new JCheckBoxMenuItem("Shops");

		int i = 0;
		POICheckBoxArray[i++] = foodAndDrinks;
		POICheckBoxArray[i++] = attractions;
		POICheckBoxArray[i++] = nature;
		POICheckBoxArray[i++] = healthCare;
		POICheckBoxArray[i++] = utilities;
		POICheckBoxArray[i++] = emergency;
		POICheckBoxArray[i++] = shops;

		for(JCheckBoxMenuItem item : POICheckBoxArray) {
			item.setUI(new StayOpenCheckBoxMenuItemUI());
			poiMenu.add(item);
		}
	}

	private void setUpButtons() {
		searchButton = new ImageButton("/SearchButtonImage.png");
		menuButton = new ImageButton("/MenuButtonImage.png");
		zoomInButton = new ImageButton("/ZoomInButtonImage.png");
		zoomOutButton = new ImageButton("/ZoomOutButtonImage.png");
		pointsOfInterestButton = new ImageButton("/PointsOfInterestButtonImage.png");

		sidebarMenu.setOpaque(false);

		sidebarMenu.add(zoomInButton);
		sidebarMenu.add(zoomOutButton);
		sidebarMenu.add(pointsOfInterestButton);

		try {
			BufferedImage bar = ImageIO.read(getClass().getResource("/Search Bar.png"));
			barImage = new JLabel(new ImageIcon(bar));
			windowPane.add(barImage, 76);
			windowPane.setComponentZOrder(barImage, 0);
			barImage.setBounds(11, 31, 298, 40);
			barImage.setVisible(false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		setUpMenu();
		menuButton.setComponentPopupMenu(popUpMenu);
		windowPane.add(menuButton);
		windowPane.setComponentZOrder(menuButton, 1);
	}


	public void setComponentListener(ComponentListener controller) {
		window.addComponentListener(controller);
	}

	public void setKeyListener(KeyListener controller) {
		combo.getEditor().getEditorComponent().addKeyListener((controller));
		secondCombo.getEditor().getEditorComponent().addKeyListener((controller));
	}

	public void setMouseListener(MouseListener controller) {
		searchButton.addMouseListener(controller);
		zoomInButton.addMouseListener(controller);
		zoomOutButton.addMouseListener(controller);
		pointsOfInterestButton.addMouseListener(controller);
		menuButton.addMouseListener(controller);
	}

	public void addActionListener(ActionListener controller) {
		for (JCheckBoxMenuItem items : POICheckBoxArray) {
			items.addActionListener(controller);
		}
		load.addActionListener(controller);
		load.setActionCommand("Load");
		showCityNames.addActionListener(controller);
		showCityNames.setActionCommand("ShowCityNames");
		save.addActionListener(controller);
		save.setActionCommand("Save");
		exit.addActionListener(controller);
		exit.setActionCommand("Exit");
		directions.addActionListener(controller);
		directions.setActionCommand("Directions");
		zoomInMenuItem.addActionListener(controller);
		zoomInMenuItem.setActionCommand("ZoomIn");
		zoomOutMenuItem.addActionListener(controller);
		zoomOutMenuItem.setActionCommand("ZoomOut");
		greyScaleMenuItem.addActionListener(controller);
		greyScaleMenuItem.setActionCommand("Greyscale");
		nightModeMenuItem.addActionListener(controller);
		nightModeMenuItem.setActionCommand("Nightmode");
		AntiAliasingToggle.addActionListener(controller);
		AntiAliasingToggle.setActionCommand("Aa");
		fancyPan.addActionListener(controller);
		fancyPan.setActionCommand("Fancypan");
	}

	public void setComponentzZOrder(DrawCanvas canvas) {
		windowPane.add(canvas, 100);
		windowPane.add(combo, 50);
		windowPane.add(secondCombo, 50);
		windowPane.add(sidebarMenu, 50);
		windowPane.add(searchButton);

		windowPane.setComponentZOrder(canvas, 2);
		windowPane.setComponentZOrder(searchButton, 1);
		windowPane.setComponentZOrder(sidebarMenu, 1);
		windowPane.setComponentZOrder(combo, 1);
		windowPane.setComponentZOrder(secondCombo, 3);

		menuButton.setLocation(357, 10);
		searchButton.setLocation(313, 10);

		combo.setBounds(10, 10, 300, 40);
		secondCombo.setBounds(10, 10, 300, 40);
		window.add(windowPane, BorderLayout.CENTER);
	}

	/**
	 * Makes the autocomplete bar with all the addresses
	 */
	public void createAutocomplete(TST tree) {
		combo = new AutocompleteJComboBox(tree);
		combo.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);

		secondCombo = new AutocompleteJComboBox(tree);
		secondCombo.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
	}

	public void setTreeInAutocompleter(TST tree){
		combo.setTree(tree);
		secondCombo.setTree(tree);
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
		zoomInMenuItem = new JMenuItem("Zoom In (CTRL-MINUS)", KeyEvent.VK_PLUS);
		zoomOutMenuItem = new JMenuItem("Zoom Out (CTRL-PLUS)", KeyEvent.VK_MINUS);
		greyScaleMenuItem = new JMenuItem("GreyScale (CTRL-G)", KeyEvent.VK_G);
		nightModeMenuItem = new JMenuItem("NightMode (CTRL-N)", KeyEvent.VK_N);
		fancyPan = new JMenuItem("FancyPan (CTRL-F)", KeyEvent.VK_F);
		AntiAliasingToggle = new JMenuItem("AntiAliasing (CTRL-T)", KeyEvent.VK_T);
		directions = new JCheckBoxMenuItem("Directions");
		showCityNames = new JCheckBoxMenuItem("Show city names");


		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK), "action5", save);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK), "action6", load);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK), "action7", exit);

		popUpMenu.add(save);
		popUpMenu.add(load);
		popUpMenu.addSeparator();
		popUpMenu.add(directions);
		popUpMenu.add(showCityNames);
		popUpMenu.addSeparator();

		JMenu tools = new JMenu("Tools");
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Event.CTRL_MASK), "action1", zoomInMenuItem);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Event.CTRL_MASK), "action2", zoomOutMenuItem);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_G, Event.CTRL_MASK), "action3", greyScaleMenuItem);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK), "action4", nightModeMenuItem);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK), "action9", fancyPan);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK), "action11", AntiAliasingToggle);

		tools.add(nightModeMenuItem);
		tools.add(greyScaleMenuItem);
		tools.add(fancyPan);
		tools.add(AntiAliasingToggle);
		tools.add(zoomInMenuItem);
		tools.add(zoomOutMenuItem);

		popUpMenu.add(tools);
	}

	public void showMenuTwo() {
		if (!menu2IsShown) {
			popUpMenu.show(menuButton, 0, 40);
		} else {
			popUpMenu.setVisible(false);
		}
		menu2IsShown = !menu2IsShown;
		window.repaint();
	}

	public void showMenuOne() {
		if (!menu1IsShown) {
			poiMenu.show(sidebarMenu, 0, 130);
		} else {
			poiMenu.setVisible(false);
		}
		menu1IsShown = !menu1IsShown;
		window.repaint();
	}

	public void setBounds(DrawCanvas canvas) {
		canvas.setBounds(0, 0, window.getWidth(), window.getHeight());
		sidebarMenu.setBounds(canvas.getWidth() - 60, 10, 40, 130);
	}

	boolean showDirectionsComboBox = false;
	public void toggleDirectionsBar() {
		showDirectionsComboBox = !showDirectionsComboBox;

		if(showDirectionsComboBox) {
			secondCombo.setVisible(true);
			barImage.setVisible(true);
		}
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			int runCounter=0;
			int direction = showDirectionsComboBox ? 1 : -1; //Skal den foldes ud eller ind?
			@Override
			public void run() {
				int newY = secondCombo.getY()+direction;
				secondCombo.setLocation(10, newY);
				window.repaint();

				runCounter++;
				if(runCounter==40) {
					if(!showDirectionsComboBox){
						secondCombo.setVisible(false);
						barImage.setVisible(false);
					}
					cancel();
				}

			}
		}, 0, 5);
		secondCombo.setEditable(showDirectionsComboBox);
	}

	public AutocompleteJComboBox getCombo() {
		return combo;
	}

	public JCheckBoxMenuItem getDirections() {
		return directions;
	}

	public JCheckBoxMenuItem[] getPOICheckBoxArray() {
		return POICheckBoxArray;
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
}
