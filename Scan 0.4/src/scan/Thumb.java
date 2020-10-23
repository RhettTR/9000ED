package scan;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;



public class Thumb extends JPanel {
      
    
    public final int width;
    public final int height;
    public static int selectedIndex = 0;
    
    private final int thumbWidth;
    private final int thumbHeight;
    
    private BufferedImage image;
    private final int index;
    
    
    private final int BORDER = 6;
    
    
    public Thumb(int index, int width, int height) 
    {
        
        this.thumbWidth = width;
        this.thumbHeight = height;
        this.width = width + (2*BORDER);
        this.height = height + (2*scan.Split.BORDER) + (2*BORDER);
        this.image = null;
        this.index = index;
        
        addMouseListener(new MouseAdapter() { 
            @Override
            public void mousePressed(MouseEvent me) {
                int previousSelection = selectedIndex;
                selectedIndex = ((Thumb)me.getSource()).index;
                scan.ScanPanel.thumbs[previousSelection].deselect(); 
                repaint();
            } 
        });
        
    }
    
    
    public void deselect()
    {     
        repaint();   
    }
    
    
    public void setImage(BufferedImage image)
    {
        this.image = image;                 
        this.repaint(); 
    }
    
    
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        
        BasicStroke stroke;
        
        if (this.index == selectedIndex)
        {
            g.setColor(Color.LIGHT_GRAY);
            stroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
        }
        else
        {
            g.setColor(Color.LIGHT_GRAY);          
        
            float dot = width / 12;       
            float[] pattern = { dot - (dot/3), dot/3 };
            
            stroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, pattern, 0);   
        }
        
        

        g2d.setStroke(stroke);
        
        g2d.drawLine(0, 0, this.width-1, 0);
        g2d.drawLine(this.width-1, 0, this.width-1, this.height-1);
        g2d.drawLine(this.width-1, this.height-1, 0, this.height-1);
        g2d.drawLine(0, this.height-1, 0, 0); 
        
        
        
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        
        
        if (this.image != null)
            g2d.drawImage(image, (width - thumbWidth) / 2, (height - thumbHeight) / 2, 
                          thumbWidth, thumbHeight, this);
       
        
        if (this.index == selectedIndex)
        {
            g.setColor(new Color(33, 167, 59));            
            stroke = new BasicStroke(3.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
            g2d.setStroke(stroke);
            
            g2d.drawLine(1, 1, 30, 1);
            g2d.drawLine(1, 1, 1, 30);
            g2d.drawLine(this.width-1-1, 1, this.width-1-1, 30);
            g2d.drawLine(this.width-1-1, 1, this.width-1-30, 1);
            g2d.drawLine(this.width-1-1, this.height-1-1, this.width-1-1, this.height-1-30);
            g2d.drawLine(this.width-1-1, this.height-1-1, this.width-1-30, this.height-1-1);
            g2d.drawLine(1, this.height-1-1, 1, this.height-1-30);
            g2d.drawLine(1, this.height-1-1, 30, this.height-1-1);
            
        }
        
        g2d.dispose();
        
        
    }


    
}
