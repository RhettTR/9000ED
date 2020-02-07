/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scan;


import java.awt.Color;
import java.awt.Font;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;



/**
 *
 * @author Rhett
 */
public final class Main extends javax.swing.JFrame {

    
    public static Thumb thumb1;
    public static Thumb thumb2;
    public static Thumb thumb3;
    
    public static Preview preview;
    
    
    //public BufferedImage image;
    public static Main instance;
    
    
    
    
    
    public Main() 
    {
         
        initComponents();
          
        
        instance = this;
        
        logger.setEditable(false);
        logger.setLineWrap(true);
        logger.setWrapStyleWord(true);
        
        
        Font font = new Font("sans-serif", Font.PLAIN, 12);
        UIManager.put("Menu.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("Menu.selectionBackground", Color.LIGHT_GRAY);
        UIManager.put("MenuItem.selectionBackground", Color.LIGHT_GRAY);
        UIManager.put("MenuItem.background", Color.WHITE);
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(Color.decode("#d6d6d6"), 1));
      
        
        SwingUtilities.updateComponentTreeUI(jMenuBar1);
        jMenuBar1.setBackground(Color.decode("#d6d6d6"));
        
        
        // make sure c programs have execute permissions
        File file = new File(System.getProperty("java.class.path") + "/scan/c/thumb");
        file.setExecutable(true);
        file = new File(System.getProperty("java.class.path") + "/scan/c/unitready");
        file.setExecutable(true);
        file = new File(System.getProperty("java.class.path") + "/scan/c/preview");
        file.setExecutable(true);
        file = new File(System.getProperty("java.class.path") + "/scan/c/contrast");
        file.setExecutable(true);
        
        
        
        
        thumb1 = new Thumb(0);        
        thumb1.setBounds(40, 20, thumb1.width, thumb1.height);
        
        thumb2 = new Thumb(1);        
        thumb2.setBounds(40, 20 + thumb1.height + 20, thumb2.width, thumb2.height);
        
        thumb3 = new Thumb(2);        
        thumb3.setBounds(40, 20 + thumb1.height + 20 + thumb2.height + 20, thumb3.width, thumb3.height);

        
        
        add(thumb1);
        add(thumb2);
        add(thumb3);
        
                 
            
        preview = new Preview();        
        preview.setBounds(260, 40, 500, 500);         

        add(preview);


        
        DetectDevice detectDevice = new DetectDevice();
           
            
    }
    
    
            
            
            
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scroll = new javax.swing.JScrollPane();
        logger = new javax.swing.JTextArea();
        ThumbButton = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButtonPreview = new javax.swing.JButton();
        jButtonContrast = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Scan 0.2");

        scroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        logger.setColumns(20);
        logger.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        logger.setRows(5);
        scroll.setViewportView(logger);

        ThumbButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        ThumbButton.setText("Thumbs");
        ThumbButton.setEnabled(false);
        ThumbButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ThumbsActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jButton2.setText("Split");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButtonPreview.setText("Preview");
        jButtonPreview.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonPreviewClicked(evt);
            }
        });

        jButtonContrast.setText("Contrast");
        jButtonContrast.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonContrastMouseClicked(evt);
            }
        });

        jMenuBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenuBar1.setFont(jMenuBar1.getFont().deriveFont((jMenuBar1.getFont().getStyle() & ~java.awt.Font.ITALIC) & ~java.awt.Font.BOLD));

        jMenu1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenu1.setText("File");

        jMenuItem1.setText("jMenuItem1");
        jMenuItem1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("jMenuItem2");
        jMenuItem2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("jMenuItem3");
        jMenuItem3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenu3.setText("Help");
        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 777, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(ThumbButton, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonContrast, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(674, Short.MAX_VALUE)
                .addComponent(jButtonContrast)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPreview)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ThumbButton)
                .addGap(18, 18, 18)
                .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ThumbsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ThumbsActionPerformed
        
        Thumbs thumbs;
        
        if (scan.DetectDevice.device != null)
            if (scan.DetectDevice.device instanceof String)
                if (scan.DetectDevice.device.contains("/dev/sg"))
                    thumbs = new Thumbs(scan.DetectDevice.device);
        
    }//GEN-LAST:event_ThumbsActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        
        Split split;
            
        split = new Split();
        split.doSplit();   
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButtonPreviewClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonPreviewClicked
        
        Previews view;
        
        if (scan.DetectDevice.device != null)
            if (scan.DetectDevice.device instanceof String)
                if (scan.DetectDevice.device.contains("/dev/sg"))
                    view = new Previews(scan.DetectDevice.device);
        
    }//GEN-LAST:event_jButtonPreviewClicked

    private void jButtonContrastMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonContrastMouseClicked
        
        Contrast contrast;
        
        contrast = new Contrast();
        
    }//GEN-LAST:event_jButtonContrastMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JButton ThumbButton;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonContrast;
    private javax.swing.JButton jButtonPreview;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    public static javax.swing.JTextArea logger;
    private javax.swing.JScrollPane scroll;
    // End of variables declaration//GEN-END:variables


}
