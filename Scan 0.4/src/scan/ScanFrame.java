package scan;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicInternalFrameUI;


public class ScanFrame extends JInternalFrame {    
    
    private static int xpos = 10;
    private static int ypos = 10;
    private int x = 0, y = 0;
    private int width = 0;
    private int height = 0;
    private final JPanel background;
    private final JPanel horisontalBarPanel;
    private final JScrollBar horisontalBar;
    private final JScrollBar verticalBar;
    private final Point location = new Point();
    private BufferedImage scaledImage = null;
            
    public BufferedImage image = null;
    
    
    
    public ScanFrame()            
    {
        
        this.setClosable(true);
        
        
        this.background = new JPanel() {    
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (scaledImage != null) 
                    g.drawImage(scaledImage, 0, 0, null);
                
            }
            
        };

        horisontalBar = new JScrollBar(JScrollBar.HORIZONTAL);
        verticalBar = new JScrollBar(JScrollBar.VERTICAL);
             
        
        horisontalBar.setBorder(BorderFactory.createEmptyBorder());
        verticalBar.setBorder(BorderFactory.createEmptyBorder());
        
        horisontalBar.setMinimum(0);
        horisontalBar.setMaximum(8964);
        verticalBar.setMinimum(0);
        verticalBar.setMaximum(8964);
        
        horisontalBar.addAdjustmentListener(new MyAdjustmentListener());
        verticalBar.addAdjustmentListener(new MyAdjustmentListener());
        
        horisontalBar.setVisible(false);
        verticalBar.setVisible(false);
        


        
        horisontalBarPanel = new JPanel();
        horisontalBarPanel.setLayout(new BoxLayout(horisontalBarPanel, BoxLayout.X_AXIS));
        horisontalBarPanel.setBorder(BorderFactory.createEmptyBorder());
        horisontalBarPanel.setVisible(false);
        

        JLabel corner = new JLabel();
        int scrollBarWidth = ((Integer)UIManager.get("ScrollBar.width"));
        corner.setBorder(BorderFactory.createEmptyBorder());
        corner.setPreferredSize(new Dimension(scrollBarWidth - 1, scrollBarWidth - 1));
        
       
        horisontalBarPanel.add(horisontalBar);
        horisontalBarPanel.add(corner);


        add(horisontalBarPanel, BorderLayout.SOUTH);        
        add(verticalBar, BorderLayout.EAST);
        add(background, BorderLayout.CENTER);
        
        setOffset();        
        setBounds(location.x, location.y, 100, 45); 
        setVisible(true);
        
        
    }
    
    
    private void setOffset()
    {      
         
        location.x = xpos;
        location.y = ypos;
        
        xpos = (xpos + 20 > 80 ? 10 : xpos + 20);
        ypos = (ypos + 20 > 80 ? 10 : ypos + 20);
           
    }
       
    
    public void setImage(BufferedImage image)
    {
        
        this.image = image;
        
        
        if (this.image != null)
        {
            
            
            width  = this.image.getWidth()  / 12;        // 3
            height = this.image.getHeight() / 12;        // 3 
              
                        
            horisontalBar.setUnitIncrement(width);
            horisontalBar.getModel().setExtent(width);            
            verticalBar.setUnitIncrement(height);
            verticalBar.getModel().setExtent(width);
            

            
            Dimension titlebarSize = 
                ((BasicInternalFrameUI) getUI()).getNorthPane().getPreferredSize();
            
            
            setBounds(location.x, location.y, 
                      width + getInsets().left + getInsets().right, 
                      height + getInsets().bottom + getInsets().top + titlebarSize.height);
                
            
            scaledImage = new Scale(this.image, width, height).scale();
                    
        }
        
    }
    
    
    public void magnifier(int direction)
    {
        
        if (direction > 0)
        {
            scaledImage = image.getSubimage(x, y, width, height);
            horisontalBarPanel.setVisible(true);
            horisontalBar.setVisible(true);
            verticalBar.setVisible(true);
            background.setPreferredSize(new Dimension(8964, 8964));
        }
        else
        { 
            horisontalBarPanel.setVisible(false);
            horisontalBar.setVisible(false);
            verticalBar.setVisible(false);
            scaledImage = new Scale(this.image, width, height).scale();
        }
               
        
        background.revalidate();
        
        
    }
    
    
    private void setScaledImage(int x, int y)
    {
       
        if (image != null)
        {
            if (x > 8217) x = 8217;
            if (y > 8217) y = 8217;            
            this.x = x;
            this.y = y;
            scaledImage = image.getSubimage(x, y, width, height);
            repaint();
        }
        
    }
    
    
    class MyAdjustmentListener implements AdjustmentListener {
        
        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            
            if (e.getValueIsAdjusting()) 
                return;
       
            int type = e.getAdjustmentType();

            switch (type) {
                case AdjustmentEvent.TRACK:
                    setScaledImage(horisontalBar.getValue(), verticalBar.getValue());
                    break;
                default:
                    break;
            }      
            
        }
        
    }
    
    
    
}
