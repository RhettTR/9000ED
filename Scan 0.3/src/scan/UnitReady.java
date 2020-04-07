package scan;

import java.util.ArrayList;


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
                scan.Logger.logger.append(((String)line) + "\n");
           
            return false;
           
       }    
        
        
       for (Object line : output)
       {
            // output is status
            
            if (!((String)line).equals(UNIT_READY))
            {
                return true;
            }
   
        }
        
        
        // unit ready         
        
            
        return false;
        
        
    }
    
      
}
