package markov_img;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

public class MarkovImageGenerator {
    private File file_out;
    private BufferedImage img;
    private LogWriter log;
    private Random rand;
    private static int NUM_SEEDS = 10;
    private static int NUM_PASSES = 1000;
    
    public MarkovImageGenerator(LogWriter logWriter) {
        log = logWriter;
        rand = new Random();
    }
    
    public void GenerateImage(MarkovImageTable table, String file_path_out) {
        MedianGrid table_grid = table.GetMedianGrid();
        MedianGrid grid = new MedianGrid(table_grid);
        MedianGrid nextGrid = null;
        Map<Color, Map<AdjacentColors, Integer>> mtable = table.GetTable();
        
        // Seed the output image with some random colors from the keys of the Markov table
        Set<Color> keys = mtable.keySet();
        Color [] colorArr = keys.toArray(new Color[keys.size()]);
        for (int i = 0; i < NUM_SEEDS; i++)
        {
            // Pick a random color from the center color key set
            Color randColor = colorArr[randInt(0, colorArr.length - 1)];
            grid.SetColor(randInt(0, grid.width() - 1), randInt(0, grid.height() - 1), randColor);
        }
        
        // Seed the buffer grid too
        nextGrid = grid.Copy();
        
        // Iterate over the grid a few times, filling in more of the image
        for (int pass = 0; pass < NUM_PASSES; pass++)
        {
            for (int col = 0; col < grid.width(); col++)
            {
                for (int row = 0; row < grid.height(); row++)
                {
                    Color curr_color = grid.GetColor(col, row);
                    if (curr_color == null)
                    {
                        continue;
                    }
                    if (mtable.keySet().contains(curr_color))
                    {
                        AdjacentColors adjColors = GenerateRandomColors(mtable.get(curr_color));
                        nextGrid.SetColor(col - 1, row - 1, adjColors.GetOneColor(0));
                        nextGrid.SetColor(col    , row - 1, adjColors.GetOneColor(1));
                        nextGrid.SetColor(col + 1, row - 1, adjColors.GetOneColor(2));
                        nextGrid.SetColor(col - 1, row    , adjColors.GetOneColor(3));
                        nextGrid.SetColor(col + 1, row    , adjColors.GetOneColor(4));
                        nextGrid.SetColor(col - 1, row + 1, adjColors.GetOneColor(5));
                        nextGrid.SetColor(col    , row + 1, adjColors.GetOneColor(6));
                        nextGrid.SetColor(col + 1, row + 1, adjColors.GetOneColor(7));
                    } else {
                        // Uh-oh.  This should not happen.
                    }
                }
            }
            grid = nextGrid.Copy();
        }
        
        
        file_out = new File(file_path_out);
        img = grid.GetBufferedImage();

        /*
         * Test image for debugging
        img = new BufferedImage(table.GetWidth(), table.GetHeight(), BufferedImage.TYPE_INT_RGB);
        
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int r = i % 255;
                int g = (i + j) % 255;
                int b = j % 255;
                int col = (r << 16) | (g << 8) | b;
                img.setRGB(i, j, col);
            }
        }
        */
        
        WriteImage();
    }
    
    private AdjacentColors GenerateRandomColors(Map<AdjacentColors, Integer> subTable)
    {
        AdjacentColors adjColors = new AdjacentColors();
        int total = 0;
        // Total is the number of instances we have to choose from
        for (AdjacentColors a : subTable.keySet())
        {
            total += subTable.get(a);
        }
        if (total == 1)
        {
            return (AdjacentColors)((subTable.keySet().toArray())[0]);
        }
        
        int rndIndex = randInt(0, total - 1);
        int currIndex = 0;
        for (AdjacentColors a : subTable.keySet())
        {
            for (int j = 0; j < subTable.get(a); j++)
            {
                if (rndIndex == currIndex)
                {
                    return a;
                }
                currIndex += 1;
            }
        }
         
        return adjColors;
    }
    
    // http://stackoverflow.com/questions/363681/generating-random-integers-in-a-range-with-java
    private int randInt(int min, int max) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    
    public void GenerateGridImage(MedianGrid grid, String file_path_out) {
        file_out = new File(file_path_out);
        img = grid.GetBufferedImage();
        WriteImage();
    }
    
    private void WriteImage()
    {
        try {
            ImageIO.write(img, "jpg", file_out);
        } catch (IOException e) {
            log.write("Error writing output image: " + e.getMessage());
        }
    }

}
