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
        downsample();
        transform();
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

    // public void transform() {
    //     int u;
    //     int v;
    //     double alphau;
    //     double alphav;

    //     double[][] test = {
    //         {52, 55, 61, 66, 70, 61, 64, 73},   
    //         {63, 59, 55, 90, 109, 85, 69, 72},
    //         {62, 59, 68, 113, 144, 104, 66, 73},
    //         {63, 58, 71, 122, 154, 106, 70, 69}, 
    //         {67, 61, 68, 104, 126, 88, 68, 70},  
    //         {79, 65, 60, 70, 77, 68, 58, 75},
    //         {85, 71, 64, 59, 55, 61, 65, 83},
    //         {87, 79, 69, 68, 65, 76, 78, 94}
    //     };

    //     double summation = 0;
    //     double[][] coefficients = new double[8][8];

    //     for(int x = 0; x < test.length; x++) 
    //     {

    //         for(int y = 0; y < test[x].length; y++)
    //         {
    //             //Inner discrete transform.
    //             u = x % 8;
    //             v = y % 8;

    //             double cosu = Math.cos(((((2 * x) + 1) * u * Math.PI) / 16));
    //             double cosv = Math.cos(((((2 * y) + 1) * v * Math.PI) / 16));

    //             summation = ((test[x][y]) * cosu * cosv) + summation;
    //             System.out.print(test[x][y] - 128 + ", ");

    //             alphau = 1.0;
    //             alphav = 1.0;
    
    //             if(u == 0) {
    //                 alphav = 1 / Math.sqrt(2);
    //             }
    //             if(v == 0) {
    //                 alphau = 1 / Math.sqrt(2);
    //             }

    //             coefficients[u][v] = .25 * alphau * alphav * summation;
    //         }
    //         System.out.println("");
    //     }
        
    //     System.out.println("");

    //     for(int i = 0; i < 8; i++)
    //     {
    //         for(int j = 0; j < 8; j++) 
    //         {
    //             //Outer discrete transform.
    //             // alphau = 1.0;
    //             // alphav = 1.0;
    
    //             // if(i == 0) {
    //             //     alphav = 1 / Math.sqrt(2);
    //             // }
    //             // if(j == 0) {
    //             //     alphau = 1 / Math.sqrt(2);
    //             // }

    //             // coefficients[i][j] = .25 * alphau * alphav * summation;

    //             System.out.print((int)coefficients[i][j] + ", ");
    //         }
    //         System.out.println("");
    //     }

    // }


    public void transform() {
        int i, j, k, l;


        double[][] matrix = {
            {52, 55, 61, 66, 70, 61, 64, 73},   
            {63, 59, 55, 90, 109, 85, 69, 72},
            {62, 59, 68, 113, 144, 104, 66, 73},
            {63, 58, 71, 122, 154, 106, 70, 69}, 
            {67, 61, 68, 104, 126, 88, 68, 70},  
            {79, 65, 60, 70, 77, 68, 58, 75},
            {85, 71, 64, 59, 55, 61, 65, 83},
            {87, 79, 69, 68, 65, 76, 78, 94}
        };

        int m = 8;
        int n = 8;

        // dct will store the discrete cosine transform
        double[][] dct = new double[m][n];

        double ci, cj, dct1, sum;

        for (i = 0; i < m; i++)
        {
            for (j = 0; j < n; j++)
            {
                // ci and cj depends on frequency as well as
                // number of row and columns of specified matrix
                if (i == 0)
                    ci = 1 / Math.sqrt(m);
                else
                    ci = Math.sqrt(2) / Math.sqrt(m);

                if (j == 0)
                    cj = 1 / Math.sqrt(n);
                else
                    cj = Math.sqrt(2) / Math.sqrt(n);

                // sum will temporarily store the sum of
                // cosine signals
                sum = 0;
                for (k = 0; k < m; k++)
                {
                    for (l = 0; l < n; l++)
                    {
                        dct1 = (matrix[k][l] - 128) *
                                Math.cos((2 * k + 1) * i * Math.PI / (2 * m)) *
                                Math.cos((2 * l + 1) * j * Math.PI / (2 * n));
                        sum = sum + dct1;
                    }
                }
                dct[i][j] = ci * cj * sum;
            }
        }

        for (i = 0; i < m; i++)
        {
            for (j = 0; j < n; j++)
                System.out.printf("%f\t", dct[i][j]);
            System.out.println();
        }
    }
    public YCbCr[][] getColor() {
        return this.color;
    }

    public int[][] getQTable() {
        return this.quantization;
    }
    public BufferedImage getImage() {
        return this.dctimage;
    }

    public class YCbCr {
        public double Cb;
        public double Cr;
        public double Y;

        public YCbCr(float b, float r, float y) {
            this.Y = y;
            this.Cr = r;
            this.Cb = b;
        }

        public YCbCr(double r, double b, double g, String s) {
            if(s == "rgb") {

                r = r / 255;
                g = g / 255;
                b = b / 255;

                this.Y = (0.299* r) + (0.587 * g) + (0.114 * b);
                this.Cb = (-0.168736 * r ) - (0.331264 * g) + (0.5 * b);
                this.Cr =  (0.5 * r) - (0.418688 * g) - (0.081312 * b);

                // this.Y = (this.Y * 219) + 16;
                // this.Cb = (this.Cb * 224) + 128;
                // this.Cr = (this.Cr * 224) + 128;
            }
        }
        public Color getRGB() {
            double r = this.Y + (1.402 * this.Cr);
            double g = this.Y - (0.344136 * this.Cb) - (0.714136 * this.Cr);
            double b = this.Y + (1.772 * this.Cb);

            // System.out.println("R : " + r);
            // System.out.println("G : " + g);
            // System.out.println("B : " + b);

            r = r * 255;
            g = g * 255;
            b = b * 255;

            r = Math.max(0, Math.min(255, r));
            g = Math.max(0, Math.min(255, g));
            b = Math.max(0, Math.min(255, b));

            // System.out.println("NEW RGB = " + r);

            return new Color((int)r, (int)g, (int)b);
        }
    }
}