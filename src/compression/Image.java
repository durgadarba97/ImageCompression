package compression;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.imageio.*;
import java.io.File;
import java.io.IOException;
import compression.DCT.YCbCr;

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

        //Mostly for testing.
        Draw originalimage = new Draw(image, 0 , 0);

        YCbCr[][] color = dct.getColor();
        BufferedImage image2 = new BufferedImage(color.length, color[0].length, BufferedImage.TYPE_INT_RGB);

        for(int i = 0; i < color.length; i++) {
            for(int j = 0; j < color[i].length; j++)
            {
                image2.setRGB(i, j, color[i][j].getRGB().getRGB());
            }
        }

        Draw yimage = new Draw(image2, 0, 100);

    }
}