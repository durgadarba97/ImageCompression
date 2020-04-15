package compression;
import static java.lang.Math.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;

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
        YCbCr[][] color;
        YCbCr[][] subimage;

    public DCT(BufferedImage image) {
        this.originalimage = image;
        this.dctimage = checkPadding(this.originalimage);
        this.width = dctimage.getWidth();
        this.height = dctimage.getHeight();

        System.out.println("WIDTH = " + this.width);
        System.out.println("HEIGHT = " + this.height);

        this.color = new YCbCr[this.height][this.width];
        this.subimage = new YCbCr[this.height / 8][this.width / 8];
        setColorSpace();
        downsample();
        setSubArray();
    }

    public BufferedImage checkPadding(BufferedImage i) {

        int w = originalimage.getWidth();
        int h = originalimage.getHeight();
        BufferedImage paddedimage;

        if(w % 8 != 0 && h % 8 != 0) {
            //Make the height next heighest multiple of 8.
            double padh = (64.0 * Math.ceil(h / 64)) + 64;
            double padw = (64.0 * Math.ceil(w / 64)) + 64;

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
                Color c = new Color(rgb);
                int blue = c.getBlue();
                int green =  c.getGreen();
                int red = c.getRed();

                color[i][j] = new YCbCr(red, blue, green, "rgb");
            }
        }

    }

    public void downsample() {
        for(int i = 0; i < this.color.length/2; i+=2) 
        {
            for(int j = 0; j < this.color[i].length/2; j+=2)
            {
                double avgcb = (this.color[i][j].Cb + this.color[i][j+1].Cb + this.color[i+1][j].Cb + this.color[i+1][j+1].Cb) / 4;
                double avgcr = (this.color[i][j].Cr + this.color[i][j+1].Cr + this.color[i+1][j].Cr + this.color[i+1][j+1].Cr) / 4;

                this.color[i][j].Cb = avgcb;
                this.color[i][j].Cr = avgcr;
                this.color[i][j+1].Cb = avgcb;
                this.color[i][j+1].Cr = avgcr;
                this.color[i+1][j].Cb = avgcb;
                this.color[i+1][j].Cr = avgcr;
                this.color[i+1][j+1].Cb = avgcb;
                this.color[i+1][j+1].Cr = avgcr;

            }
        }

        // for(int i = 10; i < 12; i++)
        // {
        //     for(int j = 10; j < 12; j++)
        //     {
        //         System.out.println("Y, Cb, Cr: " + this.color[i][j].Y + ", " + this.color[i][j].Cb + ", " + this.color[i][j].Cr);
        //     }
        // }
    }

    public void setSubArray() {
        double[][] ycumulative = new double[this.height / 8][this.width / 8];
        double[][] cbcumulative = new double[this.width][this.height];
        double[][] crcumulative = new double[this.width][this.height];
        int subi = 0;
        int subj = 0;

        for(int i = 0; i < this.color.length; i++) 
        {
            for(int j = 0; j < this.color[i].length; j++)
            {
                int cj = j / 8;
                int ci = i / 8;
                ycumulative[ci][cj] = ycumulative[ci][cj] + this.color[i][j].Y;



                if(ci == 0 && cj == 0)
                {
                    System.out.println(this.color[i][j].Y);
                }
            }
        }
        

        System.out.println("++++++++++++++++++++++++++++++++=");
        System.out.println("length: " + ycumulative[0].length);
        for(int i = 0; i < ycumulative.length; i++)
        {
            for(int j = 0; j < ycumulative[i].length; j++)
            {
                System.out.println(ycumulative[i][j]);
            }
        }
    }

    public YCbCr[][] getColor() {
        return this.color;
    }

    public int[][] getQTable()
    {
        return this.quantization;
    }
    public BufferedImage getImage() {
        return this.dctimage;
    }

    private class YCbCr {
        public double Cb;
        public double Cr;
        public double Y;

        public YCbCr(double b, double r, double y) {
            this.Y = y;
            this.Cr = r;
            this.Cb = b;
        }

        public YCbCr(double r, double b, double g, String s) {
            if(s == "rgb") {
                this.Y = (0.299* r) + (0.587 * g) + (0.114 * b);
                this.Cb = (0.299 * r) + (0.587 * g) + (0.114 * b) + 128;
                this.Cr =  (0.5 * r) - (0.4187 * g) - (0.0813 * b) + 128;
            }
        }
    }
}