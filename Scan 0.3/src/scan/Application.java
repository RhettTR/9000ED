package scan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JToolBar;
import javax.swing.JSeparator;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicMenuItemUI;
import javax.swing.plaf.basic.BasicSeparatorUI;


public class Application extends JFrame {

  
    public static ScanPanel scanPanel;
    public static ToolBarButton scannerButton;
    public static Application instance;
    public static JLabel progressLabel;
    public static JProgressBar progressBar;
    
    private final ToolBarButton magplussButton;
    private final ToolBarButton magminusButton;
    
    
    public static ScanFrame scanFrame;
    
    
    public Application() {
        
        initComponents();
        
        instance = this;
        
        this.setLocationRelativeTo(null);
        
        jMenuBar1.setBackground(Color.decode("#d6d6d6"));
        jMenuBar1.setBorder(BorderFactory.createEmptyBorder(10, 2, 10, 2));
        ((JMenu)jMenuBar1.getComponent(0)).setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        ((JMenu)jMenuBar1.getComponent(1)).setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        

        
        
        
        Font font = new Font("sans-serif", Font.PLAIN, 12);
        UIManager.put("MenuBar.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("Menu.selectionBackground", Color.LIGHT_GRAY);
        UIManager.put("MenuItem.selectionBackground", Color.LIGHT_GRAY);
        UIManager.put("MenuItem.background", Color.WHITE);
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
        SwingUtilities.updateComponentTreeUI(this);
        
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        
        // make sure c programs have execute permissions
        File file = new File(System.getProperty("java.class.path") + "/scan/c/thumb");
        file.setExecutable(true);
        file = new File(System.getProperty("java.class.path") + "/scan/c/unitready");
        file.setExecutable(true);
        file = new File(System.getProperty("java.class.path") + "/scan/c/preview");
        file.setExecutable(true);
        file = new File(System.getProperty("java.class.path") + "/scan/c/contrast");
        file.setExecutable(true);
        file = new File(System.getProperty("java.class.path") + "/scan/c/scan");
        file.setExecutable(true);
        file = new File(System.getProperty("java.class.path") + "/scan/c/ev");
        file.setExecutable(true);
        file = new File(System.getProperty("java.class.path") + "/scan/c/gamma");
        file.setExecutable(true);
        
        
        
        
        JDesktopPane desktop = new JDesktopPane();
        
        Logger logger = new Logger();
        desktop.add(logger, BorderLayout.CENTER);        
        
        scanFrame = new ScanFrame();
        desktop.add(scanFrame, BorderLayout.CENTER);
        
        add(desktop); 
       
        
        
        
       
        
        
        
        
        JToolBar toolbar = new JToolBar();
        

        toolbar.setSize(0, 68);
        toolbar.setFloatable(true);
        toolbar.setOrientation(JToolBar.HORIZONTAL);

        
        
        toolbar.addSeparator(new Dimension(20,toolbar.getHeight()));
        
        
        magplussButton = new ToolBarButton();
        
        ImageIcon magicon = new ImageIcon(getClass().getResource("/scan/images/magpluss.png"));
        magplussButton.setIcon(magicon);
        magplussButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                scanFrame.magnifier(1); 
            }
        }); 
        
        toolbar.add(magplussButton);
        
        toolbar.addSeparator();
        
        
        magminusButton = new ToolBarButton();

        magicon = new ImageIcon(getClass().getResource("/scan/images/magminus.png"));
        magminusButton.setIcon(magicon);
        magminusButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                scanFrame.magnifier(-1);
            }
        }); 
        
        toolbar.add(magminusButton);
        
        toolbar.addSeparator();
        
        
        
        ToolBarButton button = new ToolBarButton();
        
        ImageIcon logicon = new ImageIcon(getClass().getResource("/scan/images/log.png"));
        button.setIcon(logicon);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                logger.setVisible(!logger.isVisible()); 
            }
        }); 
        
        toolbar.add(button);
        

        toolbar.addSeparator(new Dimension(20,toolbar.getHeight()));
        
        
        JSeparator separator = new JSeparator();
        separator.setUI(new MySeparatorUI(6, toolbar.getHeight() - 6));
        toolbar.add(separator);
        
        toolbar.addSeparator(new Dimension(20,toolbar.getHeight()));

        
        scannerButton = new ToolBarButton(); 
        
        ImageIcon icon = new ImageIcon(getClass().getResource("/scan/images/nikon.png"));
        ImageIcon icondisabled = new ImageIcon(getClass().getResource("/scan/images/nikondisabled.png"));
        
        scannerButton.setIcon(icon);
        scannerButton.setDisabledIcon(icondisabled);
        scannerButton.setEnabled(true);
        
        scannerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (((JButton)me.getSource()).isEnabled())
                {
                    ((JButton)me.getSource()).setEnabled(false);
                    scanPanel = new ScanPanel();
                    desktop.add(scanPanel, BorderLayout.CENTER);
                    scanPanel.setVisible(true);                    
                }                    
            }
        });                                              
        
        toolbar.add(scannerButton);
        
        
        
        toolbar.addSeparator(new Dimension(20, toolbar.getHeight()));
        
        toolbar.setVisible(true);
        
        
        
        
        add(toolbar, BorderLayout.NORTH);
        
        

        
        
        JMenuItem menuitem = new JMenuItem("Save As...");
        menuitem.setMnemonic(KeyEvent.VK_V);
        menuitem.setUI(new MyMenuItemUI());
        menuitem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JFrame parentFrame = new JFrame();                
                FileDialog fd = new FileDialog(parentFrame, "Choose a file", FileDialog.SAVE);
                fd.setDirectory("/home/meg/Documents");
                fd.setFile("*.tiff");
                fd.setLocation(new Point(500,200));
                //fd.setLocationRelativeTo(Application.instance);
                fd.setVisible(true);
                String directory = fd.getDirectory();
                String filename = fd.getFile();
                if (filename != null)
                {
                    BufferedImage image = Application.this.scanFrame.image;
                    InThread inThread = new InThread(new WriteTiff(image, directory + filename));
                    inThread.start();
                }
            }            
        });
        File.add(menuitem);
        
        menuitem = new JMenuItem("Quit");
        menuitem.setMnemonic(KeyEvent.VK_Q);
        menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        menuitem.setUI(new MyMenuItemUI());
        menuitem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        File.add(menuitem);

        File.getPopupMenu().setPopupSize(200, File.getItemCount()*30);
        
             
        
        
        //
 
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(234,234,234));
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));        
        statusPanel.setPreferredSize(new Dimension(this.getWidth(), 26));
        statusPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        
        separator = new JSeparator();
        separator.setPreferredSize(new Dimension(2, 18));
        separator.setUI(new MySeparatorUI(0, 18));
        statusPanel.add(separator);        
        
        progressLabel = new JLabel();
        progressLabel.setPreferredSize(new Dimension(108, 18));
        statusPanel.add(progressLabel);        
 
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(200, 17));
        progressBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(214,214,214)));
        statusPanel.add(progressBar);
        
        JLabel statusLabel = new JLabel();
        statusLabel.setPreferredSize(new Dimension(6, 18));
        statusPanel.add(statusLabel);
        
        
        add(statusPanel, BorderLayout.SOUTH); 
        
        
        
        
        
        DetectDevice detectDevice = new DetectDevice(true);
        
        
        
        
        
    }

    
 
    public class ToolBarButton extends JButton
    {
        
        public ToolBarButton()
        {
            this.setPreferredSize(new Dimension(68,56));
            this.setMaximumSize(new Dimension(68,56));
            this.setMinimumSize(new Dimension(68,56));
            this.setBorder(BorderFactory.createRaisedBevelBorder());
            this.setFocusable(false);
        }
        
    }
    
  
    
    public class MySeparatorUI extends BasicSeparatorUI
    {
        private final int yStart;
        private final int yEnd;
        
        public MySeparatorUI(int yStart, int yEnd)
        {
            this.yStart = yStart;
            this.yEnd = yEnd;
        }
        
        @Override
        public void paint(Graphics g, JComponent c) {            
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(0, yStart, 0, yEnd);
            g.setColor(Color.WHITE);
            g.drawLine(1, yStart, 1, yEnd);
        }
        
    }
    
    
    public class MyMenuItemUI extends BasicMenuItemUI
    {            
        
        
        @Override
        protected void paintMenuItem(Graphics g, JComponent c, Icon checkIcon, 
                                     Icon arrowIcon, Color background, Color foreground, 
                                     int defaultTextIconGap)
        {

            JMenuItem i = (JMenuItem)c;

            Graphics2D g2d = (Graphics2D) g;


            
            i.setBorder(BorderFactory.createEmptyBorder());
            
            if (i.isArmed())
            {
                
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect(2, 2, i.getWidth() - 4, i.getHeight() - 4);
                
                g2d.setColor(new Color(154, 154, 154));
                g2d.drawLine(1, 1, i.getWidth() - 2, 1);
                g2d.drawLine(i.getWidth() - 2, 1, i.getWidth() - 2, i.getHeight() - 2);
                g2d.drawLine(i.getWidth() - 2, i.getHeight() - 2, 1, i.getHeight() - 2);
                g2d.drawLine(1, i.getHeight() - 2, 1, 1);   
            }
            else
            {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, i.getWidth(), i.getHeight());                
            }
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(Color.BLACK); 
            
            FontMetrics fm =  i.getFontMetrics(i.getFont());
            int y = + fm.getAscent() + (i.getHeight() - fm.getHeight()) / 2;
           
            BasicGraphicsUtils.drawStringUnderlineCharAt(i, g2d, i.getText(), 
                    i.getDisplayedMnemonicIndex(), 22 + defaultTextIconGap, y);
            
            
            int gap = i.getWidth() - 22 - defaultTextIconGap - g2d.getFontMetrics().stringWidth(i.getText());
                    
            if (i.getAccelerator() != null)
            {
                String shortCut = i.getAccelerator().toString().replace("ctrl pressed ", "Ctrl+");                
                g2d.drawString(shortCut, gap, y);               
            }
                    
        }
        
    }
   
  
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        File = new javax.swing.JMenu();
        Help = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Scan 0.3");
        setPreferredSize(new java.awt.Dimension(1200, 1000));

        jPanel1.setPreferredSize(new java.awt.Dimension(2, 100));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jMenuBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        File.setText("File");
        File.setPreferredSize(new java.awt.Dimension(30, 21));
        jMenuBar1.add(File);

        Help.setText("Help");
        jMenuBar1.add(Help);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(659, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(106, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Application().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu File;
    private javax.swing.JMenu Help;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
