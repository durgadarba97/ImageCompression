package compression;
import static java.lang.Math.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics;

/*
    TODO:
    Downsample
    Find DCT
    Relate to quantization table
    Hufmann Encode
    Write out file
    Fix public vs private
    Clean code
*/


public class DCT {
    private int[][] quantization = {{16, 11, 10, 16, 24,  40,  51,  61},   
                                    {12, 12, 14, 19, 26,  58,  60,  55},
                                    {14, 13, 16, 24, 40, 57, 69, 56},
                                    {14, 17, 22, 29, 51, 87, 80, 62}, 
                                    {18, 22, 37, 56, 68, 109, 103, 77},  
                                    {24, 35, 55, 64, 81, 104, 113, 92},
                                    {49, 64, 78, 87, 103, 121, 120, 101},
                                    {72, 92, 95, 98, 112, 100, 103, 99}};
        BufferedImage originalimage;
        BufferedImage dctimage;
        int width;
        int height;
        double[][] y;
        double[][] Cb;
        double[][] Cr;

    public DCT(BufferedImage image) {
        this.originalimage = image;
        this.dctimage = checkPadding(this.originalimage);
        this.width = dctimage.getWidth();
        this.height = dctimage.getHeight();

        System.out.println("WIDTH = " + this.width);
        System.out.println("HEIGHT = " + this.height);

        setColorSpace();

    }

    public BufferedImage checkPadding(BufferedImage i) {

        int w = originalimage.getWidth();
        int h = originalimage.getHeight();
        BufferedImage paddedimage;

        if(w % 8 != 0 && h % 8 != 0) {
            //Make the height next heighest multiple of 8.
            double padh = (8.0 * Math.ceil(h / 8)) + 8;
            double padw = (8.0 * Math.ceil(w / 8)) + 8;

            paddedimage = new BufferedImage((int)padw, (int)padh, i.getType());
            Graphics g = paddedimage.getGraphics();

            g.drawImage(this.originalimage, 0, 0, null);
            g.dispose();
        } 
        else {
            paddedimage = i;
        }

        return paddedimage;
    }

    public void setColorSpace() {
        for(int i = 0; i < this.height; i ++)
        {
            for(int j = 0; j < this.width; j++)
            {
                int rgb  = this.dctimage.getRGB(j, i);
                int blue = rgb & 0xff;
                int green = (rgb & 0xff00) >> 8;
                int red = (rgb & 0xff0000) >> 16;

                this.y[i][j] = (0.299* red) + (0.587 * green) + (0.114 * blue);
                this.Cb[i][j] = (0.299 * red) + (0.587 * green) + (0.114 * blue) + 128;
                this.Cr[i][j] =  (0.5 * red) - (0.4187 * green) - (0.0813 * blue) + 128;
            }
        }

    }

    public double[][] getY() {
        return this.y;
    }

    public double[] getCr() {
        return this.getCr();
    }

    public double[][] getCb() {
        return this.Cb;
    }

    public int[][] getQTable()
    {
        return quantization;
    }
    public BufferedImage getImage() {
        return dctimage;
    }
}