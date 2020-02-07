package scan;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;


public class ReadTiff {
    
    
    private final String fileName;
    
    
    public ReadTiff(String fileName)
    {
        this.fileName = fileName;        
    }
    
    
    public BufferedImage read()
    {
        
        File file = new File(System.getProperty("java.class.path") + "/scan/c/" + fileName);
               
        ImageInputStream imageInputStream;
        
        BufferedImage bufferedImage = null;
        
        try {
            
            imageInputStream = new FileImageInputStream(file);
        

            Iterator<ImageReader> ite  = ImageIO.getImageReadersByMIMEType("image/tiff");
            

            while (ite.hasNext()) {
                ImageReader imageReader = ite.next();
                imageReader.setInput(imageInputStream);
                bufferedImage = imageReader.read(0);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return bufferedImage;
        
    }
    
}
