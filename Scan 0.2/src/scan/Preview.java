package scan;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;


public class Preview extends JPanel {
    
    
    public final int width;
    public final int height;
    
    private BufferedImage image;
    
    
    public Preview()
    {
        this.width = 500;
        this.height = 500;
        
        this.image = null;
    }
       
    
    public void setImage(BufferedImage image)
    {
        this.image = image;                 
        this.repaint(); 
    }
    
    
    private Dimension scaleRatio(Dimension image, Dimension boundary) 
    {

        int imageWidth = image.width;
        int imageHeight = image.height;
        int boundaryWidth = boundary.width;
        int boundaryHeight = boundary.height;
        int resultWidth = imageWidth;
        int resultHeight = imageHeight;

        if (resultWidth > boundaryWidth) {
            resultWidth = boundaryWidth;
            resultHeight = resultWidth * (imageHeight / imageWidth);
        }

        if (resultHeight > boundaryHeight) {
            resultHeight = boundaryHeight;
            resultWidth = resultHeight * (imageWidth / imageHeight);
        }

        return new Dimension(resultWidth, resultHeight);

    }

    
    private BufferedImage getScaledImage(BufferedImage image, int width, int height) 
    {
        
        int imageWidth  = image.getWidth();
        int imageHeight = image.getHeight();

        double scaleX = (double)width/imageWidth;
        double scaleY = (double)height/imageHeight;
        
        AffineTransform scaleTransform = 
            AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = 
            new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

        return bilinearScaleOp.filter(
            image, new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
        
    }
    
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g; 
        
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawRect(0, 0, 499, 499);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        if (image != null)
        {
            Dimension scaled = scaleRatio(new Dimension(image.getWidth(), image.getHeight()), 
                                          new Dimension(500-12, 500-12));
            BufferedImage scaledImage = getScaledImage(image, scaled.width, scaled.height);
            g2d.drawImage(scaledImage, 6, 6, scaled.width, scaled.height, null);
        }

        g2d.dispose();
    }
    
    
}
