package scan;

import java.util.ArrayList;



public class Contrast implements OnShellExitListener {

    
    public Contrast()
    {
           
        ArrayList command = new ArrayList();

        command.add("./contrast");
        


        Shell shell = new Shell(this);

        shell.start(command);
        
    }
    
    
    @Override
    public boolean onShellExit(int exitCode, ArrayList output) {
    
        scan.Main.logger.append("> Contrast exited with status " + exitCode + "\n");
        
        for (Object line : output)
            scan.Main.logger.append(((String)line) + "\n");
        
        
        Tiff tiff;
       
        if (exitCode == 0)
            tiff = new Tiff("1494x1494+0", "rgb:contrast.bin", "contrast.tiff");
        
        
        return false;
    
    }   
    
    
}
