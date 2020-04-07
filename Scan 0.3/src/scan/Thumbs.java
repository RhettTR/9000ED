package scan;

import java.util.ArrayList;


public class Thumbs implements OnShellExitListener {
    
    
    public Thumbs(String device)
    {
        
        ArrayList command = new ArrayList();

        command.add("./thumb");
        command.add(device);


        Shell shell = new Shell(this);

        shell.start(command);
        
    }
    
    
    @Override
    public boolean onShellExit(int exitCode, ArrayList output) {
    
        scan.Logger.logger.append("> Scan of thumbs exited with status " + exitCode + "\n");
        
        for (Object line : output)
            scan.Logger.logger.append(((String)line) + "\n");
        
        
        if (exitCode == 0)
        {
           
            Tiffs tiff;

            if (exitCode == 0)
                tiff = new Tiffs("186x660+0", "rgb:scan.bin", "thumb.tiff");
        }
        
        return false;
    
    }
    
    
}
