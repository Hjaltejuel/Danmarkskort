package bfst17;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by trold on 2/8/17.
 */
public class WindowKeyController extends KeyAdapter {
	Model model;
	DrawWindow window;

	public WindowKeyController(DrawWindow window, Model model) {
		window.addKeyListener(this);
		this.window = window;
		this.model = model;
	}

	/**
	 * Invoked when a key has been pressed.
	 *
	 * @param e
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyChar()) {
			case 'x':
				window.toggleAA();
				break;
			case 's':
				model.save("savegame.bin");
				break;
			case 'l':
				model.load("savegame.bin");
				break;
		}
	}
}
