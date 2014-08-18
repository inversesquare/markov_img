package markov_img;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MarkovImageTable {
    private int pixels_per_grid;
    private LogWriter log;
    private FastImage fast_img;
    private MedianGrid grid;
    // Color is the center color in the region we're recording
    // AdjacentColors holds information about surrounding colors
    // the Integer is the number of identical instances of those surrounding colors
    private Map<Color, Map<AdjacentColors, Integer>> mtable;
    
    public MarkovImageTable(LogWriter logWriter, int pixels_per_grid_in) {
        log = logWriter;
        pixels_per_grid = pixels_per_grid_in;
        mtable = new HashMap<Color, Map<AdjacentColors, Integer>>();
    }
    
    // XXX - remove this after debugging
    public MedianGrid GetMedianGrid()
    {
        return grid;
    }
    
    public Map<Color, Map<AdjacentColors, Integer>> GetTable()
    {
        return mtable;
    }
    
    public void AddImage(BufferedImage im) {
        fast_img = new FastImage(im, log);        

        // Log the first line of pixels
        /*
        for (int i = 0; i < fast_img.width(); i++) {
            log.write(
                String.format("%d %d %d",
                        fast_img.getRed(0, i),
                        fast_img.getGreen(0, i),
                        fast_img.getBlue(0, i)
                )
            );            
        }
        */
        
        grid = new MedianGrid(fast_img, pixels_per_grid);
        RecordImage();
    }
    
    public void LogTable()
    {
        for (Map.Entry<Color, Map<AdjacentColors, Integer>> entry : mtable.entrySet()) {
            Color centerColor = entry.getKey();
            Map<AdjacentColors, Integer> subMap = entry.getValue();
            int count = 0;
            // Count will be the total number of adjacent color instances under this center color
            for (Map.Entry<AdjacentColors, Integer> subEntry : subMap.entrySet())
            {
                count += subEntry.getValue();
            }
            for (Map.Entry<AdjacentColors, Integer> subEntry : subMap.entrySet())
            {
                AdjacentColors adjColors = subEntry.getKey();
                StringBuilder s = new StringBuilder();
                s.append("Count: ");
                s.append(String.format("%06d", count));
                s.append(" Center Color: ");
                s.append(AdjacentColors.HumanReadableColor(centerColor) + " Adj.Colors:");
                s.append(adjColors.toString());
                log.write(s.toString() + "\n");
            }
        }
    }
    
    private void RecordImage() {
        log.write("Image median colors:\n");
        for (int col = 0; col < grid.width(); col++) 
        {
            log.write("\n");
            for (int row = 0; row < grid.height(); row++)
            {
                // Construct the list of adjacent median color tiles
                // to the current center tile
                Color centerColor = grid.GetColor(col, row);
                log.write(AdjacentColors.HumanReadableColor(centerColor));
                AdjacentColors adjColors = new AdjacentColors();
                int counter = 0; // ranges from 0 -> 7
                // Go row first:
                // 0    1    2
                // 3    X    4
                // 5    6    7
                for (int r = row - 1; r < row + 2; r++)
                {
                    for (int c = col - 1; c < col + 2; c++)
                    {
                        // Don't record the center color
                        if (c == col && r == row)
                        {
                            continue;
                        }
                        Color x = grid.GetColor(c, r);
                        // Do not include invalid median tiles that are off image
                        if (x != null) {
                            adjColors.AddColor(x, counter);
                        }
                        counter += 1;
                    }
                }
                
                // Add this configuration of center color tile + adjacent tiles to Markov table
                if (mtable.containsKey(centerColor))
                {
                    // This center color has been added before
                    Map<AdjacentColors, Integer> subMap = mtable.get(centerColor);
                    if (subMap.containsKey(adjColors))
                    {
                        // This center color + adjacent colors has been added before - increment counter
                        int currCount = subMap.get(adjColors);
                        subMap.put(adjColors, currCount + 1);
                    } else {
                        // First time adding this center color + adjacent color combo
                        subMap.put(adjColors, 1);
                    }
                } else {
                    // Center color has not yet been added - add it here
                    Map<AdjacentColors, Integer> subMap = new HashMap<AdjacentColors, Integer>();
                    subMap.put(adjColors, 1);
                    mtable.put(centerColor, subMap);
                }
            }
        }
    }
    
}
