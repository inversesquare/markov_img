package markov_img;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

public class MedianGrid {
    private int width;
    private int height;
    private FastImage im;
    private int ppg; // Pixels per grid spacing
    private int [][][] grid;
    private ArrayList<Integer> redArr;
    private ArrayList<Integer> greenArr;
    private ArrayList<Integer> blueArr;
    private static int RED = 0;
    private static int GREEN = 1;
    private static int BLUE = 2;
    private static int NUM_COLOR_CHANNELS = 3;
    
    public MedianGrid(MedianGrid grid_in)
    {
        ppg = grid_in.gridPixels();
        width = grid_in.width();
        height = grid_in.height();
        grid = new int[NUM_COLOR_CHANNELS][width][height];
    }
    
    public MedianGrid(FastImage img_in, int pixels_per_grid_spacing) {
        im = img_in;
        ppg = pixels_per_grid_spacing;
        width = (img_in.width() / ppg) + 1;
        height = (img_in.height() / ppg) + 1;
        grid = new int[NUM_COLOR_CHANNELS][width][height];
        redArr = new ArrayList<Integer>(ppg*ppg);
        greenArr = new ArrayList<Integer>(ppg*ppg);
        blueArr = new ArrayList<Integer>(ppg*ppg);
        PopulateGrid();
    }
    
    public MedianGrid Copy()
    {
        MedianGrid ret = new MedianGrid(this);
        for (int col = 0; col < width; col++)
        {
            for (int row = 0; row < height; row++)
            {
                ret.SetColor(col, row, GetColor(col, row));
            }
        }
        
        return ret;
    }
    
    public int width() {
        return width;
    }
    
    public int height() {
        return height;
    }
    
    public int gridPixels()
    {
        return ppg;
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
    
    public Color GetColor(int col, int row)
    {
        if (indexIsInvalid(col, row))
        {
            return null;
        }
        int red = grid[RED][col][row];
        int green = grid[GREEN][col][row];
        int blue = grid[BLUE][col][row];
        Color ret = new Color(red, green, blue);
        return LowBitColor.Convert(ret);
    }
    
    public void SetColor(int col, int row, Color c)
    {
        if (indexIsInvalid(col, row) || c == null)
        {
            return;
        }
        
        grid[RED][col][row] = c.getRed();
        grid[GREEN][col][row] = c.getGreen();
        grid[BLUE][col][row] = c.getBlue();
    }
    
    public BufferedImage GetBufferedImage()
    {
        BufferedImage img = new BufferedImage(width * ppg, height * ppg, BufferedImage.TYPE_INT_RGB);
        
        for (int col = 0; col < img.getWidth(); col++) {
            for (int row = 0; row < img.getHeight(); row++) {
                int r = 0;
                int g = 0;
                int b = 0;
                Color c = GetColor(col / ppg, row / ppg);
                if (c != null)
                {
                    r = c.getRed();
                    g = c.getGreen();
                    b = c.getBlue();
                }
                int colInt = (r << 16) | (g << 8) | b;
                img.setRGB(col, row, colInt);
            }
        }
        return img;
    }
    
    private void PopulateGrid() {
        // Outer loops through the coarse median grid
        for (int gcol = 0; gcol < width; gcol++)
        {
            for (int grow = 0; grow < height; grow++)
            {
                int col_start = gcol * ppg;
                int row_start = grow * ppg;
                // Reset the lists
                redArr.clear();
                greenArr.clear();
                blueArr.clear();
                // Innner loop through the image pixels
                for (int col = col_start; col < col_start + ppg; col++)
                {
                    for (int row = row_start; row < row_start + ppg; row++)
                    {
                        // Skip pixels from beyond the edge of the image
                        if (im.getRed(col, row) == FastImage.INVALID_INDEX)
                        {
                            continue;
                        }
                        redArr.add(im.getRed(col, row));
                        greenArr.add(im.getGreen(col, row));
                        blueArr.add(im.getBlue(col, row));
                    }
                }
                // Sort and take the middle index to find the median
                Collections.sort(redArr);
                Collections.sort(greenArr);
                Collections.sort(blueArr);
                if (redArr.size() > 0)
                {
                    grid[RED][gcol][grow] = redArr.get(redArr.size()/2);
                    grid[GREEN][gcol][grow] = greenArr.get(greenArr.size()/2);
                    grid[BLUE][gcol][grow] = blueArr.get(blueArr.size()/2);
                }
            }
        }
    }

}
