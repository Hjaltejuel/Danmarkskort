package bfst17;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
	private JTextArea userOutput;
	private JLayeredPane WindowPane;
	private JPopupMenu popUpMenu;
    boolean isClicked =false;
        //få filer ind

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

		WindowPane.add(combo, 50);
		combo.setBounds(10,10,300,40);
		WindowPane.setComponentZOrder(combo,0);
		WindowPane.setComponentZOrder(canvas,1);
		window.add(WindowPane, BorderLayout.CENTER);

		window.pack();
		setUpButtons();
		canvas.setBounds(0,0,window.getWidth(),window.getHeight());

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);

		canvas.pan(-model.getMinLon(), -model.getMaxLat());
		canvas.zoom(canvas.getWidth()/(model.getMaxLon()-model.getMinLon()));
		new WindowKeyController(this, model);

	}

	/**
	 * Makes the autocomplete button with all the addresses
	 */
	public void paintAutocomplete() {
		this.listItems = new ArrayList();


		this.listItems.addAll(addressModel.getAddressToCordinate().keySet().stream().map(a -> a.toString().toLowerCase()).collect(Collectors.toList()));

		this.searchable = new StringSearchable(this.listItems);
		this.combo = new AutocompleteJComboBox(this.searchable);
		this.combo.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
		this.combo.setPreferredSize(new Dimension(500, 30));
		this.combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent event) {

				if (event.getKeyChar() == 10) {
				    search();
				}

			}
		});
	}
	public void search(){
        String s = (String) combo.getSelectedItem();
        //points lat, lon
        double lat = -addressModel.getPoint2DToAddress(s.trim()).getY();
        double lon = -addressModel.getPoint2DToAddress(s.trim()).getX();

        //distance from center of screen in lat lon
        double distanceToCenterY = lat - canvas.getCenterCordinateY();
        double distanceToCenterX = lon - canvas.getCenterCordinateX();

        if(150000 / canvas.getXZoomFactor() >= 0.8) {
            canvas.panSlowAndThenZoomIn(distanceToCenterX, distanceToCenterY);
        }

        //er zoomet langt ind og afstanden er lang
        else{
            canvas.zoomOutSlowAndThenPan(distanceToCenterX, distanceToCenterY);
        }

        canvas.setPin((float)lat,(float)lon);
        combo.setSelectedItem((null));


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
		WindowPane.setComponentZOrder(search,0);
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
			public void mouseClicked(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

                if(!isClicked){
					popUpMenu.show(e.getComponent(),0,40);
					isClicked=true;
				}else if(isClicked){
					popUpMenu.setVisible(false);
						isClicked=false;
				}
				canvas.repaint();

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				canvas.repaint();

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				canvas.repaint();

			}

			@Override
			public void mouseExited(MouseEvent e) {
				canvas.repaint();

			}
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

	public void toggleAA() {
		canvas.toggleAA();
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

	public void setUpMenu() {
		popUpMenu = new JPopupMenu("Options");
		JMenu file = new JMenu("File");
		JMenuItem save = new JMenuItem("Save", KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		JMenuItem load = new JMenuItem("Load", KeyEvent.VK_L);
		load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK));
		JMenuItem exit = new JMenuItem("Exit", KeyEvent.VK_Q);


		popUpMenu.add(save);
		popUpMenu.add(load);

		popUpMenu.addSeparator();

		JMenu tools = new JMenu("Tools");
		JMenuItem zoomIn = new JMenuItem("Zoom In", KeyEvent.VK_PLUS);
		zoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Event.CTRL_MASK));
		JMenuItem zoomOut = new JMenuItem("Zoom Out", KeyEvent.VK_MINUS);
		zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Event.CTRL_MASK));
		JMenuItem greyScale = new JMenuItem("GreyScale",KeyEvent.VK_G);
		greyScale.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, Event.CTRL_MASK));
		JMenuItem nightMode = new JMenuItem("NightMode",KeyEvent.VK_N);
		nightMode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));

		tools.add(nightMode);
		tools.add(greyScale);
		tools.add(zoomIn);
		tools.add(zoomOut);

		popUpMenu.add(tools);
		tools.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(tools.getComponentCount());
			}
		});


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



}