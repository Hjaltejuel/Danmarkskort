package bfst17;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

	public DrawWindow(Model model, AddressModel addressModel) {
		this.model = model;
		this.addressModel = addressModel;
		model.addObserver(this);
		addressModel.addObserver(this);
		window = new JFrame("Awesome OSM Visualizer Thingy!!!! 2.0");
		window.setLayout(new BorderLayout());
		canvas = new DrawCanvas(model);
		canvas.setPreferredSize(new Dimension(500, 500));
		new CanvasMouseController(canvas, model);
		window.add(canvas, BorderLayout.CENTER);
		//paintAutocomplete();
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		canvas.pan(-model.getMinLon(), -model.getMaxLat());
		canvas.zoom(window.getWidth() / (model.getMaxLon() - model.getMinLon()));
		new WindowKeyController(this, model);
	}

	public void paintAutocomplete()
	{
		this.listItems = new ArrayList();
		Iterator var2 = addressModel.iterator();

		while(var2.hasNext()) {
			Address address = (Address)var2.next();
			this.listItems.add(address.toString());
		}

		this.searchable = new StringSearchable(this.listItems);
		this.combo = new AutocompleteJComboBox(this.searchable);
		this.combo.setPreferredSize(new Dimension(500, 30));
		this.combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent event) {
				if(event.getKeyChar() == 10) {
					addressModel.add(Address.parse(combo.getSelectedItem().toString()));
					combo.setSelectedItem((Object)null);
				}

			}
		});
		this.window.add(this.combo, "North");
		this.userOutput = new JTextArea();
		this.userOutput.setEditable(false);
		this.userOutput.setBackground(Color.LIGHT_GRAY);
		this.update((Observable)null, (Object)null);
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
		if(o instanceof AddressModel) {
			StringBuilder sb = new StringBuilder();
			Iterator var4 = this.addressModel.getAddressesUdskrift().iterator();

			while (var4.hasNext()) {
				Address address = (Address) var4.next();
				sb.append(address).append("\n\n");
			}

			this.userOutput.setText(sb.toString());
			this.window.repaint();

		}
	}

	public void addKeyListener(KeyListener keyListener) {
		window.addKeyListener(keyListener);
	}

	public void toggleAA() {
		canvas.toggleAA();
	}
}
