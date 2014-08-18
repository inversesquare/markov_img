package markov_img;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MarkovImg {

    public static void main(String[] args) {
        int pixels_per_grid = 15;
        String input_path = "C:\\data\\demo\\markov_img\\input\\";
        String output_path = "C:\\data\\demo\\markov_img\\output\\";
        
        String file_in = input_path + "ander.jpg"; // simple_input.jpg";
        String file_out = output_path + "output.jpg";
        String grid_out = output_path + "grid.jpg";
        String log_file = output_path + "log.txt";
        
        LogWriter log = new LogWriter(log_file);
        
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(file_in));
        } catch (IOException e) {
        }
        
        MarkovImageTable table = new MarkovImageTable(log, pixels_per_grid);
        table.AddImage(img);
        table.LogTable();
        
        MarkovImageGenerator generator = new MarkovImageGenerator(log);
        generator.GenerateGridImage(table.GetMedianGrid(), grid_out);
        generator.GenerateImage(table, file_out);
        
        log.close();
    }

}
