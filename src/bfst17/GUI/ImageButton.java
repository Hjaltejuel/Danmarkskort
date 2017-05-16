package bfst17.GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

/**
 * Opsætter de forskellige knapper med billeder på så de alle er ens
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

    @Override
    public void setBackground(Color c){
        super.setBackground(c);
    }
}
