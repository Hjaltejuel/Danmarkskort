package bfst17;

import javax.swing.*;

/**
 * Created by trold on 2/1/17.
 */
public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			Model model;
			if (args.length == 0) {
				model = new Model();
			} else {
				model = new Model(args[0]);
			}

			DrawWindow drawWindow = new DrawWindow(model);

		});
	}
}
