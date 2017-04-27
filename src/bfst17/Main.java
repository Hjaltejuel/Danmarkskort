package bfst17;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			Model model = null;
			if (args.length == 0) {
				model = new Model();
			} else {
				try {
					model = new Model(args[0]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			WindowController controller = new WindowController(model);

		});
	}
}