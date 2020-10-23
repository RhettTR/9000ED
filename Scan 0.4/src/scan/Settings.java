package scan;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Settings extends JInternalFrame {

    
    private final String bits8 = "  8";
    
    
    private class Mapping {
        String comboBox, string1, string2;
        public Mapping(String a, String b, String c) {
            comboBox = a;
            string1 = b;
            string2 = c;
        }            
    }
    
    private final ArrayList<Mapping> mappings = new ArrayList<>();
            

    JSlider jBoundryOffsetSlider;
    JTextField offsetValue;
    
  
    
    
    public Settings(int x, int y, int w, int h) {

        initComponents();
        
        setBounds(x, y, w, h);
        
        
        JButton leftOffsetSlider = new JButton();
        leftOffsetSlider.setBounds(101, 69, 10, 16);
        leftOffsetSlider.setIcon(new ImageIcon(getClass().getResource("/scan/images/left.png")));
        leftOffsetSlider.setFocusable(false);
        leftOffsetSlider.setBorder(BorderFactory.createEmptyBorder());
        leftOffsetSlider.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftOffsetSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                int value = Integer.parseInt(offsetValue.getText());
                value--;
                offsetValue.setText(String.valueOf(value));
                jBoundryOffsetSlider.setValue(value); 
            }
        });
        add(leftOffsetSlider);
        
        
        JButton rightOffsetSlider = new JButton();
        rightOffsetSlider.setBounds(199, 69, 10, 16);
        rightOffsetSlider.setIcon(new ImageIcon(getClass().getResource("/scan/images/right.png")));
        rightOffsetSlider.setFocusable(false);
        rightOffsetSlider.setBorder(BorderFactory.createEmptyBorder());
        rightOffsetSlider.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rightOffsetSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                int value = Integer.parseInt(offsetValue.getText());
                value++;
                offsetValue.setText(String.valueOf(value));
                jBoundryOffsetSlider.setValue(value); 
            }
        });
        add(rightOffsetSlider);
        
        
        offsetValue = new JTextField();
        offsetValue.setBounds(212, 67, 37, 21);
        offsetValue.setEditable(false);
        offsetValue.setBackground(Color.WHITE);
        offsetValue.setBorder(BorderFactory.createLoweredBevelBorder());
        offsetValue.setFont(new Font("Noto Sans", Font.PLAIN , 12));
        offsetValue.setHorizontalAlignment(SwingConstants.CENTER);
        offsetValue.setText("0");
        add(offsetValue);
        
        
        jBoundryOffsetSlider = new JSlider();  
        jBoundryOffsetSlider.setBounds(110, 59, 92, 37);
        jBoundryOffsetSlider.setSize(90, 37);
        jBoundryOffsetSlider.setMinimum(-93);
        jBoundryOffsetSlider.setMaximum(93);
        jBoundryOffsetSlider.setMinorTickSpacing(1);
        jBoundryOffsetSlider.setMajorTickSpacing(1);
        jBoundryOffsetSlider.setValue(Integer.parseInt(offsetValue.getText()));
        jBoundryOffsetSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                int value = ((JSlider)ce.getSource()).getValue();
                offsetValue.setText(String.valueOf(value));
            }
        });
        add(jBoundryOffsetSlider);
        
        
        mappings.add(new Mapping("Positive", "1", "5"));
        mappings.add(new Mapping("Negative (color)", "0", "5"));
        mappings.add(new Mapping("Negative (mono)", "0", "2"));
        mappings.add(new Mapping("RGB", "rgb", ""));
        mappings.add(new Mapping("Gray scale", "gray", ""));
        
           
    }
    
    
    
    public boolean is8BitScan()
    {
        return jScanBits.getSelectedItem() == bits8;
    }
    
    
    
    public int getOffset()
    {       
        return Integer.parseInt(offsetValue.getText());
    }
    
    
    
    public String getScanPositive()
    {
        
        String type = (String)jScan.getSelectedItem();
        
        for (int i = 0; i < mappings.size(); i++)
            if (type.equals(mappings.get(i).comboBox))
                return mappings.get(i).string1;
            
        return "0";
        
    }
    
    
    public String getScanColor()
    {
        
        String type = (String)jScan.getSelectedItem();
        
        for (int i = 0; i < mappings.size(); i++)
            if (type.equals(mappings.get(i).comboBox))
                return mappings.get(i).string2;
            
        return "5";
        
    }
    
    
    public String getColorComposition()
    {
        
        String color = (String)jColorComp.getSelectedItem();
            
        for (int i = 0; i < mappings.size(); i++)
            if (color.equals(mappings.get(i).comboBox))
                return mappings.get(i).string1;
            
        return "rgb";
        
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScanBits = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScan = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jColorComp = new javax.swing.JComboBox<>();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setTitle("Settings");
        setComponentPopupMenu(null);
        setDoubleBuffered(true);
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/scan/images/settings16.png"))); // NOI18N

        jLabel1.setText("Pixel Data Size");

        jLabel2.setFont(new java.awt.Font("Noto Sans", 0, 10)); // NOI18N
        jLabel2.setText("Scan Bit Depth:");

        jScanBits.setFont(new java.awt.Font("Noto Sans", 0, 10)); // NOI18N
        jScanBits.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "  8", "  16" }));
        jScanBits.setToolTipText("");
        jScanBits.setFocusable(false);

        jLabel3.setText("Strip Film Offset");

        jLabel4.setFont(new java.awt.Font("Noto Sans", 0, 10)); // NOI18N
        jLabel4.setText("Boundry Offset:");

        jLabel5.setText("Scan");

        jScan.setFont(new java.awt.Font("Noto Sans", 0, 10)); // NOI18N
        jScan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Positive", "Negative (color)", "Negative (mono)" }));
        jScan.setToolTipText("");
        jScan.setFocusable(false);

        jLabel6.setText("Color composition");

        jColorComp.setFont(new java.awt.Font("Noto Sans", 0, 10)); // NOI18N
        jColorComp.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "RGB", "Gray scale" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addGap(370, 370, 370))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jScanBits, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(296, 296, 296)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(jLabel4)))
                                .addGap(357, 357, 357))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jColorComp, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(12, 12, 12)
                                    .addComponent(jScan, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jScanBits, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScan, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jColorComp, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(204, 204, 204))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> jColorComp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JComboBox<String> jScan;
    private javax.swing.JComboBox<String> jScanBits;
    // End of variables declaration//GEN-END:variables
        

}




class StringUtils {

    public static String center(String s, int size) {
        return center(s, size, ' ');
    }

    public static String center(String s, int size, char pad) {
        if (s == null || size <= s.length())
            return s;

        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < (size - s.length()) / 2; i++) {
            sb.append(pad);
        }
        sb.append(s);
        while (sb.length() < size) {
            sb.append(pad);
        }
        return sb.toString();
    }
        
}
