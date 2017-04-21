package bfst17;

import javax.swing.*;
import java.util.Arrays;

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
			Controller controller = new Controller(drawWindow,model);

		});
	}
}