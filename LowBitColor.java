package markov_img;

import java.awt.Color;

public class LowBitColor {
    
    private static int NUM_BITS = 0;
    public static Color Convert(Color c)
    {
        int red = c.getRed() >>> NUM_BITS;
        red = red << NUM_BITS;
        int green = c.getGreen() >>> NUM_BITS;
        green = green << NUM_BITS;
        int blue = c.getBlue() >>> NUM_BITS;
        blue = blue << NUM_BITS;
        
        return new Color(red, green, blue);
    }

}
