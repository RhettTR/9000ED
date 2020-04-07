package scan;

import java.util.ArrayList;
import javax.swing.JOptionPane;


public class DetectDevice implements OnShellExitListener {
   
    public static String device = null;
    private int loops;
    private boolean justMessage;
    
    
    public DetectDevice(boolean justMesssage)
    {
        
        this.loops = 0;
        this.justMessage = justMesssage;
        
        ArrayList command = new ArrayList();

        command.add("lsscsi");
        command.add("-g");


        Shell shell = new Shell(this);

        shell.start(command);
        
    }

    
    @Override
    public boolean onShellExit(int exitCode, ArrayList output) {
        
        for (Object line : output)
        {    
            // output of lsscsi -g
            
            if (!((String)line).contains("Nikon") || !((String)line).contains("LS-9000 ED"))
                continue;
            
            int startIndex = ((String)line).indexOf("/dev/sg");
            
            if (startIndex == -1)
                continue;
            
            device = ((String)line).substring(startIndex).trim();
            scan.Logger.logger.append("> Nikon LS-9000 ED scanner detected on " + device + "\n");
            
            // kill warning box (if not clicked away)
            JOptionPane.getRootFrame().dispose();   
            
            // start wait for unit ready
            UnitReady unitReady = new UnitReady(device);
            
            
            return false;
            
        }
        
        loops++;
        
        if (loops == 1)
        {
            scan.Logger.logger.append("> Nikon LS-9000 ED scanner not detected\n");         
                
            if (justMessage)
            {
                JOptionPane.showMessageDialog(scan.Application.instance, 
                    "Nikon LS-9000 ED scanner not detected", 
                    "Warning", 
                    JOptionPane.WARNING_MESSAGE);
                
                return false;
            }
        }
        
        return true;
        
    }
     
    
}
