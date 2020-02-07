/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scan;

import java.util.ArrayList;

/**
 *
 * @author meg
 */
public class UnitReady implements OnShellExitListener {

    
    private final String UNIT_READY = "00";
    
    
    
    public UnitReady(String device)
    {

        ArrayList command = new ArrayList();

        command.add("./unitready");
        command.add(device);
        

        Shell shell = new Shell(this);

        shell.start(command);
        
    }
    
    
    @Override
    public boolean onShellExit(int exitCode, ArrayList output) {
        
            
       if (exitCode == 1)
       {
           
            for (Object line : output)
                scan.Main.logger.append(((String)line) + "\n");
           
            return false;
           
       }    
        
        
       for (Object line : output)
       {
            // output is status
            
            if (!((String)line).equals(UNIT_READY))
                return true;
   
        }
        
        
        // unit ready, enable button to make thumbs
        scan.Main.ThumbButton.setEnabled(true);
        
            
        return false;
        
        
    }
    
      
}
