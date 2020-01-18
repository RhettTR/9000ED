package scan;

import java.util.ArrayList;


public class Tiff implements OnShellExitListener {
    
    
    public Tiff()
    {
        
        ArrayList command = new ArrayList();

        command.add("convert");
        command.add("-depth");
        command.add("16");
        command.add("-size");
        command.add("186x660+0");
        command.add("rgb:scan.bin");
        command.add("thumb.tiff");
        

        Shell shell = new Shell(this);

        shell.start(command);    
        
    }
    

    @Override
    public boolean onShellExit(int exitCode, ArrayList output) {    
        
        for (Object line : output)
            scan.Main.logger.append(((String)line) + "\n");
        
        
        Split split;
       
        if (exitCode == 0)
        {
            split = new Split();
            split.doSplit();
        }
            
        
        return false;
        
    }
    
    
}
