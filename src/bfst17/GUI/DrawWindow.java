package bfst17.GUI;

import bfst17.AddressHandling.TST;
import bfst17.Directions.Directions;
import bfst17.Directions.DirectionsObject;
import bfst17.Enums.GUIMode;
import bfst17.Enums.PointsOfInterest;
import bfst17.Enums.RoadDirektion;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class DrawWindow {
	JFrame window;
	private AutocompleteJComboBox combo;
	private AutocompleteJComboBox secondCombo;
	private JLayeredPane windowPane;
	private JPopupMenu popUpMenu;
	private JPopupMenu poiMenu;
	private JLabel barImage;
    private JPanel directionsWindow;
    private JPanel scrollPanel;
    private JPanel car;
	private JPanel bike;
	private JPanel walk;
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
	private ImageButton searchButton;
	private ImageButton menuButton;
	private ImageButton zoomInButton;
	private ImageButton zoomOutButton;
	private ImageButton pointsOfInterestButton;
    private ImageButton directionsButton;
	private ImageButton carIcon;
	private ImageButton bikeIcon;
	private ImageButton walkIcon;
	private JCheckBoxMenuItem[] POICheckBoxArray;
	private JScrollPane directionsScroll;
	boolean menu1IsShown = false;
	boolean menu2IsShown = false;

	private JPanel sidebarMenu = new JPanel(new GridLayout(0, 1));

    /**
	 * opsætter vinduet med alle de forskellige komponenter der ligger i de forskellige undermetoder.
	 * Standard størrelsen på vinduet er 750x750, men kan selvfølgelig ændres på runtime.
	 */

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

	/**
	 * Her opsættes Points of interest menuen. De forskellige kategorier dækker over flere types hver
	 * for at gøre det mere overskueligt. Alle items er checkbox items så de kan slås til og fra.
	 * hver enkelt knap får deres funktion fra GUI controlleren.
	 */
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

	/**
	 * denne metode opsætter de forskellige knapper med deres billeder. samtidig bliver hovedmenuens
	 * konstruktør kaldt og opsat. overlayet til søgebaren bliver sat på windowpane.
	 */
	private void setUpButtons() {
        searchButton = new ImageButton("/SearchButtonImage.png");
        menuButton = new ImageButton("/MenuButtonImage.png");
        zoomInButton = new ImageButton("/ZoomInButtonImage.png");
        zoomOutButton = new ImageButton("/ZoomOutButtonImage.png");
        pointsOfInterestButton = new ImageButton("/PointsOfInterestButtonImage.png");
        directionsButton = new ImageButton("/Directions1.png");

        setUpDirectionsWindow();

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

	public void setUpDirectionsWindow() {
        car = new JPanel();
        bike = new JPanel();
        walk = new JPanel();
        carIcon = new ImageButton("/car.png");
        bikeIcon = new ImageButton("/biking.png");
        walkIcon = new ImageButton("/walking.png");
        car.add(carIcon);
        bike.add(bikeIcon);
        walk.add(walkIcon);
        car.setBackground(new Color(1, 111, 222));
        bike.setBackground(new Color(1, 111, 222));
        walk.setBackground(new Color(1, 111, 222));

        directionsWindow = new JPanel(new GridBagLayout());
		directionsWindow.setVisible(false);

		windowPane.add(directionsWindow);

		directionsWindow.setBackground(new Color(1, 111, 222));
	}

	/**
	 * tilføjer WindowControlleren som listener til komponenterne i vinduet
	 * @param controller
	 */
	public void setComponentListener(ComponentListener controller) {
		window.addComponentListener(controller);
	}

	/**
	 * tilføjer controlleren til de to comboboxe for at anvende keyboard
	 * @param controller
	 */
	public void setKeyListener(KeyListener controller) {
		combo.getEditor().getEditorComponent().addKeyListener((controller));
		secondCombo.getEditor().getEditorComponent().addKeyListener((controller));
	}



	/**
	 * tilføjer genveje til alle de knapper som har en i controlleren ud fra deres givne string
	 * @param controller
	 */

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
		directionsButton.addActionListener(controller);
		directionsButton.setActionCommand("Directions");
		carIcon.addActionListener(controller);
		carIcon.setActionCommand("Car");
		bikeIcon.addActionListener(controller);
		bikeIcon.setActionCommand("Bike");
		walkIcon.addActionListener(controller);
		walkIcon.setActionCommand("Walk");
		searchButton.addActionListener(controller);
		searchButton.setActionCommand("Search");
		zoomInButton.addActionListener(controller);
		zoomInButton.setActionCommand("ZoomInButton");
		zoomOutButton.addActionListener(controller);
		zoomOutButton.setActionCommand("ZoomOutButton");
		pointsOfInterestButton.addActionListener(controller);
		pointsOfInterestButton.setActionCommand("POI");
		menuButton.addActionListener(controller);
		menuButton.setActionCommand("Menu");

	}

	/**
	 * sætter ordnen på alle componenter i den JLayeredPane der er vores windowManager
	 * @param canvas
	 */
	public void setComponentzZOrder(DrawCanvas canvas) {
		windowPane.add(canvas, 100);
		windowPane.add(combo, 50);
		windowPane.add(secondCombo, 50);
		windowPane.add(sidebarMenu, 50);
		windowPane.add(searchButton);
		windowPane.add(directionsButton);

		windowPane.setComponentZOrder(canvas, 5);
		windowPane.setComponentZOrder(searchButton, 1);
		windowPane.setComponentZOrder(directionsButton,1);
		windowPane.setComponentZOrder(sidebarMenu, 1);
		windowPane.setComponentZOrder(combo, 1);
		windowPane.setComponentZOrder(secondCombo, 3);

        directionsButton.setLocation(357, 10);
		searchButton.setLocation(313, 10);
		menuButton.setLocation(401,10);

		combo.setBounds(10, 10, 300, 40);
		secondCombo.setBounds(10, 10, 300, 40);
		window.add(windowPane, BorderLayout.CENTER);
	}

	/**
	 * laver autocompleter baren med alle adresserne
	 */
	public void createAutocomplete(TST tree) {
		combo = new AutocompleteJComboBox(tree);
		combo.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);

		secondCombo = new AutocompleteJComboBox(tree);
		secondCombo.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
	}

	/**
	 * tilføjer tst træet til de to autocompletere
	 * @param tree
	 */
	public void setTreeInAutocompleter(TST tree) {
		combo.setTree(tree);
		secondCombo.setTree(tree);
	}

	/**
	 * funktionen ændrer farverne til nightmode på comboboxen og kortets andre komponenter
	 * @param theme
	 */
	public void toggleNightModeComboBox(GUIMode theme) {
		boolean isNightmode = theme == GUIMode.NIGHT;
		combo.getEditor().getEditorComponent().setBackground(isNightmode ? new Color(36, 47, 62) : Color.white);
		JTextComponent component = (JTextComponent) combo.getEditor().getEditorComponent();
		component.setForeground(isNightmode ? Color.WHITE : Color.black);
		component.setCaretColor(isNightmode ? Color.WHITE : Color.black);
	}
//FIXME
	/**
	 *
	 * @param stroke
	 * @param action
	 * @param clicker
	 */
	public void addKeyListeners(KeyStroke stroke, String action, JMenuItem clicker) {
		windowPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, action);
		windowPane.getActionMap().put(action, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clicker.doClick();
			}
		});
	}

	/**
	 * Laver hovedmenuen og undermenuen og opsætter deres funktioner vi Controlleren.
	 * "action" er de navne de har i controlleren. de får samtidig tastaur genveje
	 */
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
		showCityNames = new JCheckBoxMenuItem("Show city names");
		showCityNames.setSelected(true);


		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK), "action5", save);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK), "action6", load);
		addKeyListeners(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK), "action7", exit);

		popUpMenu.add(save);
		popUpMenu.add(load);
		popUpMenu.addSeparator();
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
//FIXME
	public void showMenuTwo() {
		if (!menu2IsShown) {
			popUpMenu.show(menuButton, 0, 40);
		} else {
			popUpMenu.setVisible(false);
		}
		menu2IsShown = !menu2IsShown;
		window.repaint();
	}
//FIXME
	public void showMenuOne() {
		if (!menu1IsShown) {
			poiMenu.show(sidebarMenu, 0, 130);
		} else {
			poiMenu.setVisible(false);
		}
		menu1IsShown = !menu1IsShown;
		window.repaint();
	}

	/**
	 * sætter altid vejvisningsviduet og sidebarens position til en fast afstand fra kanten af vinduet
	 * @param canvas
	 */
	public void setBounds(DrawCanvas canvas) {
		canvas.setBounds(0, 0, window.getWidth(), window.getHeight());
		sidebarMenu.setBounds(canvas.getWidth() - 60, 10, 40, 130);
		directionsWindow.setBounds(10,canvas.getHeight()-352,300,320);

	}

	/**
	 * funktionen viser directionsMenu søgefeltet (nummer to søgefelt) når den vælges i menuen.
	 * den er som udgangspunkt ikke vist. Timeren her gør så at boxen kommer glidende ned fra den anden box
	 * når den fravælges igen vil den køre op igen på samme måde.
	 * Directions-vinduet kommer samtidig med frem når metoden køres.
	 */
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
		directionsWindow.setVisible(showDirectionsComboBox);
		resetDirectionsWindow();
	}

	public void resetDirectionsWindow() {
		directionsWindow.removeAll();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.3333333333333333;
		directionsWindow.add(car, c);

		c.gridx = 1;
		c.gridy = 0;
		directionsWindow.add(bike, c);

		c.gridx = 2;
		c.gridy = 0;
		directionsWindow.add(walk, c);

		scrollPanel = new JPanel(new GridLayout(0, 1));

		directionsScroll = new JScrollPane(scrollPanel);
		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 1;
		c.ipady = 300;
		c.gridwidth = 3;
		directionsWindow.add(directionsScroll, c);

	}

    public void fillDirections(Directions directions) {
		resetDirectionsWindow();
		boolean isGrey=false;
		GridBagConstraints c = new GridBagConstraints();

		JPanel gridPanel = new JPanel(new GridLayout(1, 0));
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.ipady = 30;

		directionsWindow.add(gridPanel, c);

		JPanel time = new JPanel();
		time.setBackground(Color.lightGray);
		time.setBorder(BorderFactory.createLineBorder(Color.gray, 1));

		JLabel totalTimeLabel = new JLabel("<html>" + directions.getTotalRoadLengthText() + "</html>");
		time.add(totalTimeLabel);

		gridPanel.add(time);

		JPanel distance = new JPanel();
		distance.setBackground(Color.lightGray);
		distance.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
		gridPanel.add(distance);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0.40;
		for (int i = 1; i < directions.size(); i++) {
			//Lav directions streng
			String directionText = directions.getDirectionString(i);
			if(directionText==null){ continue; }

			JPanel boxForEachDirection = new JPanel(new GridBagLayout());

			//Lav panel der holder teksten
			JPanel labelPanel = new JPanel(new GridLayout(0, 1));
			labelPanel.setPreferredSize(new Dimension(155, 60));
			JLabel directionDescription = new JLabel("<html>" + directionText + "</html>");
			labelPanel.add(directionDescription);
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 0.90;
			boxForEachDirection.add(labelPanel, c);

			//Lav pilen
			DirectionsObject dirObj = directions.get(i);
			ImageButton arrowImage= new ImageButton("/" + dirObj.getRoadDirection() + ".png");
			JPanel arrowImagePanel= new JPanel();
			arrowImagePanel.add(arrowImage);
			c.gridx = 1;
			c.gridy = 0;
			c.weightx = 0.10;

			//Farv hver anden boks grå
			if (isGrey) {
				arrowImagePanel.setBackground(Color.LIGHT_GRAY);
				labelPanel.setBackground(Color.LIGHT_GRAY);
			} else {
				arrowImagePanel.setBackground(Color.WHITE);
				labelPanel.setBackground(Color.WHITE);
			}
			isGrey=!isGrey;

			//Add det hele
			boxForEachDirection.add(arrowImagePanel, c);
			scrollPanel.add(boxForEachDirection);
		}
	}

	public AutocompleteJComboBox getCombo() {
		return combo;
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

	public boolean getDirectionsBoolean(){
		return showDirectionsComboBox;
	}

	public AutocompleteJComboBox getSecondCombo(){
	    return secondCombo;
    }

    public JPanel getCar(){return car;}

	public JPanel getBike(){return bike;}

	public JPanel getWalk(){return  walk;}
}
