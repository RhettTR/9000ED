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
    
        scan.Main.logger.append("> Scan of thumbs exited with status " + exitCode + "\n");
        
        for (Object line : output)
            scan.Main.logger.append(((String)line) + "\n");
        
        
        Tiff tiff;
       
        if (exitCode == 0)
            tiff = new Tiff("186x660+0", "rgb:scan.bin", "thumb.tiff");
        
        return false;
    
    }
    
    
}
