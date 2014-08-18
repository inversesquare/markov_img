package markov_img;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class FastImage {
    private byte[] raw_bytes;
    private int width;
    private int height;
    private LogWriter log;
    public static int INVALID_INDEX = -1;
    
    public FastImage(BufferedImage im, LogWriter log_writer) {
        log = log_writer;
        width = im.getWidth();
        height = im.getHeight();

        raw_bytes = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();
    }
    
    public int width() {
        return width;
    }
    
    public int height() { 
        return height;
    }
    
    private boolean indexIsInvalid(int col, int row)
    {
        if (row > height - 1 || col > width - 1) {
            return true;
        }
        if (row < 0 || col < 0)
        {
            return true;
        }
        return false;
    }

    // raw bytes go: (R, G, B,) (R, G, B,) etc
    public int getRed(int col, int row) {
        if (indexIsInvalid(col, row))
        {
            return INVALID_INDEX;
        }
        return (((int)raw_bytes[((col + (row * width)) * 3) + 2]) + 256) % 256;
    }
    
    public int getGreen(int col, int row) {
        if (indexIsInvalid(col, row))
        {
            return INVALID_INDEX;
        }
        return (((int)raw_bytes[((col + (row * width)) * 3) + 1]) + 256) % 256;
    }
    
    public int getBlue(int col, int row) {
        if (indexIsInvalid(col, row))
        {
            return INVALID_INDEX;
        }
        return (((int)raw_bytes[((col + (row * width)) * 3)]) + 256) % 256;
    }

}
