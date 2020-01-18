package scan;

import java.util.ArrayList;


public class DetectDevice implements OnShellExitListener {
   
    public static String device;
    private int loops;
    
    
    public DetectDevice()
    {
        
        this.device = null;
        this.loops = 0;
        
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
            scan.Main.logger.append("> Nikon LS-9000 ED scanner detected on " + device + "\n");
            
            return false;
            
        }
        
        loops++;
        
        if (loops == 1)
            scan.Main.logger.append("> Nikon LS-9000 ED scanner not detected\n");
        
        return true;
        
    }
     
    
}
