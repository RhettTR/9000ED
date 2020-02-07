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
    public static Thumb selected = null;
    public static int selectedIndex;
    
    private BufferedImage image;
    private final int index;
    
    
    private final int BORDER = 6;
    
    
    public Thumb(int index) 
    {
        
        this.width = scan.Split.FRAME + (2*BORDER);
        this.height = scan.Split.FRAME + (2*scan.Split.BORDER) + (2*BORDER);
        this.image = null;
        this.index = index;
        
        addMouseListener(new MouseAdapter() { 
            @Override
            public void mousePressed(MouseEvent me) {
                if (selected != null)
                    selected.deselect();
                selected = (Thumb)me.getSource();
                selectedIndex = ((Thumb)me.getSource()).index;
                repaint();
            } 
        });
        
    }
    
    
    private void deselect()
    {     
        selected = null;
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
        
        
        if (this.image != null)
            g2d.drawImage(image, BORDER, BORDER, image.getWidth(), image.getHeight(), this);
        
        
        BasicStroke stroke;
        
        if (this == selected)
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
            
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        
        g2d.drawLine(0, 0, this.width-1, 0);
        g2d.drawLine(this.width-1, 0, this.width-1, this.height-1);
        g2d.drawLine(this.width-1, this.height-1, 0, this.height-1);
        g2d.drawLine(0, this.height-1, 0, 0);
        
        
        if (this == selected)
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
