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

    public Draw(BufferedImage i, int x, int y) {
        JFrame f = new JFrame("Window");
        f.add(new Render(i, x, y));
        f.setSize(i.getWidth(), i.getHeight() + 30);
        f.setVisible(true);


    }

    public class Render extends JPanel {
        public BufferedImage image;
        private int xpos;
        private int ypos;

        public Render(BufferedImage i, int x, int y) {
            super();
            image = i;

            xpos = 0;
            ypos = 0;
        }

        public void paintComponent(Graphics g)
        {
          g.drawImage(image, this.xpos, this.ypos, null);
          repaint();
        }
    }

}