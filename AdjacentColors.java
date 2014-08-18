package markov_img;

import java.awt.Color;

public class AdjacentColors {
    private Color[] colors;
    public static int SIZE = 8;
    
    public AdjacentColors() {
        colors = new Color[SIZE];
    }
    
    public void AddColor(Color c, int i) {
        if (i < 0 || i > SIZE - 1)
        {
            return;
        }
        colors[i] = c;
    }
    
    public Color GetOneColor(int i)
    {
        return colors[i];
    }
     
    // Implement equality and hashCode for Markov dictionary
    public boolean equals(Object o)
    {
        if (!(o instanceof AdjacentColors))
        {
            return false;
        }
        
        if (o == this)
        {
            return true;
        }
        
        AdjacentColors testColors = (AdjacentColors) o;
              
        for (int i = 0; i < SIZE; i++)
        {
            if (colors[i] == null && testColors.GetOneColor(i) == null) {
                continue;
            } else if (colors[i] == null) {
                return false;
            }
            
            if (!(colors[i].equals(testColors.GetOneColor(i))))
            {
                return false;
            }
        }
        
        return true;
    }
    
    public int hashCode()
    {
        int counter = 0;
        for (int i = 0; i < SIZE; i++)
        {
            Color c = colors[i];
            if (c == null)
            {
                continue;
            }
            counter += c.getRed() + (c.getGreen() << 8) + (c.getBlue() << 16) + (i << 32);
        }
        return counter;
    }
    
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < SIZE; i++)
        {
            if (colors[i] == null) {
                result.append(" XXX|XXX|XXX");
                continue;
            }
            result.append(" " + HumanReadableColor(colors[i]));
        }
        return result.toString();
    }
    
    public static String HumanReadableColor(Color c)
    {
        StringBuilder s = new StringBuilder();
        s.append(String.format("%03d", c.getRed()));
        s.append("|");
        s.append(String.format("%03d", c.getGreen()));
        s.append("|");
        s.append(String.format("%03d", c.getBlue()));
        return s.toString();
    }

}
