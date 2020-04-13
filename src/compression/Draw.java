package compression;

import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

public class Draw {
    private String imagePath;
    private BufferedImage image;
    private Graphics graphic;

    public Draw(BufferedImage i) {
        JFrame f = new JFrame("Window");
        f.add(new Render(i));
        f.setSize(i.getWidth(), i.getHeight() + 30);
        f.setVisible(true);
    }

    public class Render extends JPanel {
        public BufferedImage image;

        public Render(BufferedImage i) {
            super();
            image = i;
        }

        public void paintComponent(Graphics g)
        {
          g.drawImage(image, 0, 0, null);
          repaint();
        }
    }

}