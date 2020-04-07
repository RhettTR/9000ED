package scan;

import java.awt.image.BufferedImage;


public class Scale {
    

    private final BufferedImage image;
    private final int width;
    private final int height;
    
    
    public Scale(BufferedImage image, int width, int height)            
    {
        this.image = image;
        this.width = width;
        this.height = height;
    }
    
    
    public BufferedImage scale()
    {
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        float widthFactor = image.getWidth() / width;
        float heightFactor = image.getHeight() / height;        
            
        
        for (int x = 0; x < width; x++)        
            for (int y = 0; y < height; y++) 
            {
                int color = image.getRGB((int)(x * widthFactor), (int)(y * heightFactor));
                result.setRGB(x, y, color);
            }
        
        return result;
        
    }
    
    
}
