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

    public DCT(BufferedImage image) {
        this.originalimage = image;
        this.dctimage = checkPadding(this.originalimage);
        this.width = dctimage.getWidth();
        this.height = dctimage.getHeight();

        System.out.println("WIDTH = " + this.width);
        System.out.println("HEIGHT = " + this.height);

        this.color = new YCbCr[this.width][this.height];
        setColorSpace();
        // downsample();
        // transform();
    }

    public BufferedImage checkPadding(BufferedImage i) {

        int w = originalimage.getWidth();
        int h = originalimage.getHeight();
        BufferedImage paddedimage;

        if(w % 8 != 0 && h % 8 != 0) {
            //Make the height next heighest multiple of 8.
            int padh = (int)(8.0 * Math.ceil(h / 8)) + 8;
            int padw = (int)(8.0 * Math.ceil(w / 8)) + 8;

            paddedimage = new BufferedImage(padw, padh, BufferedImage.TYPE_INT_RGB);
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
        for(int i = 0; i < this.width; i ++)
        {
            for(int j = 0; j < this.height; j++)
            {
                int rgb  = this.dctimage.getRGB(i, j);
                Color c = new Color(rgb);
                int blue = c.getBlue();
                int green =  c.getGreen();
                int red = c.getRed();

                // System.out.println("red " + red);

                this.color[i][j] = new YCbCr(red, blue, green, "rgb");
            }
        }

    }

    public void downsample() {
        // for(int i = 0; i < this.color.length/2; i+=2) 
        // {
        //     for(int j = 0; j < this.color[i].length/2; j+=2)
        //     {
        //         double avgcb = (this.color[i][j].Cb + this.color[i][j+1].Cb + this.color[i+1][j].Cb + this.color[i+1][j+1].Cb) / 4;
        //         double avgcr = (this.color[i][j].Cr + this.color[i][j+1].Cr + this.color[i+1][j].Cr + this.color[i+1][j+1].Cr) / 4;

        //         this.color[i][j].Cb = avgcb;
        //         this.color[i][j].Cr = avgcr;
        //         this.color[i][j+1].Cb = avgcb;
        //         this.color[i][j+1].Cr = avgcr;
        //         this.color[i+1][j].Cb = avgcb;
        //         this.color[i+1][j].Cr = avgcr;
        //         this.color[i+1][j+1].Cb = avgcb;
        //         this.color[i+1][j+1].Cr = avgcr;

        //     }
        //}

        // for(int i = 10; i < 12; i++)
        // {
        //     for(int j = 10; j < 12; j++)
        //     {
        //         System.out.println("Y, Cb, Cr: " + this.color[i][j].Y + ", " + this.color[i][j].Cb + ", " + this.color[i][j].Cr);
        //     }
        // }
    }

    // public void setSubArray() {
    //     double[][] ycumulative = new double[this.height / 8][this.width / 8];
    //     double[][] cbcumulative = new double[this.width][this.height];
    //     double[][] crcumulative = new double[this.width][this.height];
    //     int subi = 0;
    //     int subj = 0;

    //     for(int i = 0; i < this.color.length; i++) 
    //     {
    //         for(int j = 0; j < this.color[i].length; j++)
    //         {
    //             int cj = j / 8;
    //             int ci = i / 8;
    //             ycumulative[ci][cj] = ycumulative[ci][cj] + this.color[i][j].Y;



    //             if(ci == 0 && cj == 0)
    //             {
    //                 System.out.println(this.color[i][j].Y);
    //             }
    //         }
    //     }
        

    //     System.out.println("++++++++++++++++++++++++++++++++=");
    //     System.out.println("length: " + ycumulative[0].length);
    //     for(int i = 0; i < ycumulative.length; i++)
    //     {
    //         for(int j = 0; j < ycumulative[i].length; j++)
    //         {
    //             System.out.println(ycumulative[i][j]);
    //         }
    //     }
    // }

    public void transform() {
        //Goes through rows of outer array
        for(int i = 0; i < this.color.length; i+=8)
        {
            //goes through columns of outer array
            for(int j = 0; j < this.color[i].length; j+=8)
            {
                //goes through individual pixels of subarray
                for(int k = i; k < 8+i; k++) 
                {
                    for(int l = j; l < 8+j; l++)
                    {
                        System.out.println("k " + k + ",l " + l);
                    }
                }
                System.out.println("============");
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

    public class YCbCr {
        public float Cb;
        public float Cr;
        public float Y;

        public YCbCr(float b, float r, float y) {
            this.Y = y;
            this.Cr = r;
            this.Cb = b;
        }

        public YCbCr(float r, float b, float g, String s) {
            if(s == "rgb") {

                r = r / 255;
                g = g / 255;
                b = b / 255;

                this.Y = ((float) 0.299* r) + ((float) 0.587 * g) + ((float) 0.114 * b);
                this.Cb = ((float) -0.168736 * r ) - ((float) 0.331264 * g) + ((float) 0.5 * b);
                this.Cr =  ((float) 0.5 * r) - ((float) 0.418688 * g) - ((float) 0.081312 * b);

                // this.Y = (this.Y * 219) + 16;
                // this.Cb = (this.Cb * 224) + 128;
                // this.Cr = (this.Cr * 224) + 128;
            }
        }
        public Color getRGB() {
            float r = (float)this.Y + ((float) 1.402 * (float)this.Cr);
            float g = (float)this.Y - ((float) 0.344136 * (float) this.Cb) - ((float) 0.714136 * (float)this.Cr);
            float b = (float)this.Y + ((float) 1.772 * (float)this.Cb);

            // System.out.println("R : " + r);
            // System.out.println("G : " + g);
            // System.out.println("B : " + b);


            // r = Math.max((float) 0, Math.min((float) 255, r));
            // g = Math.max((float) 0, Math.min((float) 255, g));
            // b = Math.max((float) 0, Math.min((float) 255, b));


            r = r * 255;
            g = g * 255;
            b = b * 255;

            // System.out.println("NEW RGB = " + r);

            return new Color((int)r, (int)g, (int)b);
        }
    }
}