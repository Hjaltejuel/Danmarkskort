package bfst17.GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

/**
 * Created by Jens on 30-04-2017.
 */
public class ImageButton extends JButton {
    public ImageButton(String ImagePath) {
        super();
        setSize(40, 40);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);

        try {
            Image img = ImageIO.read(getClass().getResource(ImagePath));
            setIcon(new ImageIcon(img.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
