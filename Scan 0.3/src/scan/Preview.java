package scan;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;


public class Preview extends JPanel {
    
    
    public final int width;
    public final int height;
    public final int border;
    
    private BufferedImage image;
    
    
    public Preview(int previewSize, int previewBorder)
    {
        this.width = previewSize;
        this.height = previewSize;
        this.border = previewBorder;
        
        this.image = null;
    }
       
    
    public void setImage(BufferedImage image)
    {
        this.image = image;                 
        this.repaint(); 
    }
    
    
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
                             RenderingHints.VALUE_RENDER_QUALITY);
        
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawRect(0, 0, width + (2*border) - 2, height + (2*border) - 2);
        
        
        if (image != null)
            g2d.drawImage(image, border-1, border-1, width, height, null);
            

        g2d.dispose();
    }
    
    
}
