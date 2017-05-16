package bfst17.GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Jens on 16-05-2017.
 */
public class ImagePanel extends JPanel {
    BufferedImage image;
    public ImagePanel(String imagePath) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
        } catch (IOException ex) {
            // handle exception...
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this); // see javadoc for more info on the parameters
    }
}
