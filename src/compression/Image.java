package compression;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.imageio.*;
import java.io.File;
import java.io.IOException;

public class Image {
    String imagepath;
    BufferedImage image;
    public Image(String path) {
        imagepath = path;
        image = getImageFromPath(this.imagepath);
    }

    public BufferedImage getImageFromPath(String path) {
        try {
            BufferedImage i = ImageIO.read(new File("src/compression/bliss.png"));
            return i;
        } 
        catch (IOException e) {
            System.out.println("Couldn't read file: " + path);
            e.printStackTrace();
        }
        return null;
    }

    public BufferedImage getImage() {
        return image;
    }
    public static void main(String[] args) {

        BufferedImage image = new Image("src/compression/bliss.png").getImage();

        DCT dct = new DCT(image);
        System.out.println(image.getWidth());
        System.out.println(image.getHeight());
        System.out.println("DCT WIDTH = " + dct.getImage().getWidth());
        System.out.println("DCT HEIGHT =" + dct.getImage().getHeight());

        double y[][] = dct.getY();

        for(int i = 0; i < image.getHeight(); i ++)
        {
            for(int j = 0; j < image.getWidth(); j++)
            {
                int rgb  = image.getRGB(j, i);
                int blue = rgb & 0xff;
                int green = (rgb & 0xff00) >> 8;
                int red = (rgb & 0xff0000) >> 16;

                System.out.print((0.299* red) + (0.587 * green) + (0.114 * blue));

                // this.y[i][j] = (0.299* red) + (0.587 * green) + (0.114 * blue);
                // this.Cb[i][j] = (0.299 * red) + (0.587 * green) + (0.114 * blue) + 128;
                // this.Cr[i][j] =  (0.5 * red) - (0.4187 * green) - (0.0813 * blue) + 128;
            }
        }

        //Mostly for testing.
        Draw originalimage = new Draw(image);

    }
}