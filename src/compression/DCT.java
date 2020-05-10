package compression;
import static java.lang.Math.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;

/*
    TODO:
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
    double[][] dct;

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
        quantization();
        encode();
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

    // This downsamples the image in a 4:1:1 YCbCr.
    // gets to the idea that we see brightness a lot better than we see Color.
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
    }

    //Does the Discrete Cosine Transform.
    //TODO: make it use a full image. Not just 8 by 8.
    public void transform() {

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

        // dct will store the discrete cosine transform
        this.dct = new double[8][8];

        double au, av, sum;

        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                // ci and cj depends on frequency as well as
                // number of row and columns of specified matrix
                if (i == 0)
                    au = 1 / Math.sqrt(2);
                else
                    au = 1;

                if (j == 0)
                    av = 1 / Math.sqrt(2);
                else
                    av = 1;

                // sum will temporarily store the sum of
                // cosine signals
                sum = 0;
                for (int x = 0; x < 8; x++)
                {
                    for (int y = 0; y < 8; y++)
                    {
                        sum = (matrix[x][y] - 128) *
                                Math.cos((2 * x + 1) * i * Math.PI / (16)) *
                                Math.cos((2 * y + 1) * j * Math.PI / (16)) + sum;
                    }
                }
                this.dct[i][j] = (0.25) * au * av * sum;
            }
        }

        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
                System.out.printf("%f\t", this.dct[i][j]);
            System.out.println();
        }
    }

    // This is where the true downscaling of the image happens. 
    // Downscales image by given quantization amount.
    public void quantization() {

        for(int i = 0; i < this.dct.length; i++)
        {
            for(int j = 0; j < this.dct[i].length; j++)
            {
                this.dct[i][j] = Math.round(this.dct[i][j] / quantization[i % 8][j % 8]);
            }
        }

        System.out.println("QUANTIZATION::::");
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
                System.out.printf("%f\t", this.dct[i][j]);
            System.out.println();
        }
    }

    public void encode() {
        // Matrix for testing. 
        // int[][] test = {
        //         {1, 2,  6,  7,  15, 16, 28, 29},
        //         {3, 5,  8, 14,  17, 27, 30, 43},
        //         {4, 9, 13, 18,  26, 31, 42, 44},
        //         {10,12, 19, 25, 32, 41, 45, 54},
        //         {11,20, 24, 33, 40, 46, 53, 55},
        //         {21,23, 34, 39, 47, 52, 56, 61},
        //         {22,35, 38, 48, 51, 57, 60, 62},
        //         {36,37, 49, 50, 58, 59, 63, 64}
        //     };
        
        boolean firsthalf = true;
        int x = 0;
        int y = 0;
        int cx = 0;
        int cy = 0;
        System.out.println(this.dct[x][y]);
        y++;
        cx++;
        
        while(cx * cy <= 64)
        {
            // while loop to go down and to the left
            while(y >= 0 && y < 8 && x >= 0 && x < 8)
            {
                System.out.print(this.dct[x][y] + ", ");
                x = x + 1;
                y = y - 1;
            }
            System.out.println("");
            if(cx == 7)
                firsthalf = false;

            if(firsthalf) {
                cx++;
                x = cx;
                y = 0;
            } else {
                cy++;
                x = 7;
                y = cy;
            }

            //while loop to go up and the right 
            while(x >= 0 && x < 8 && y < 8 && y >= 0) 
            {
                System.out.print(this.dct[x][y] + ", ");
                x = x - 1;
                y = y + 1;
            }
            System.out.println("");
            // cx++;
            // y = cx;
            // x = 0;

            if(firsthalf) {
                cx++;
                y = cx;
                x = 0;
            } else {
                cy++;
                y = 7;
                x = cy;
            }
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