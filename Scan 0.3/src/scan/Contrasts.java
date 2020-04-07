package scan;

import java.util.ArrayList;



public class Contrasts implements OnShellExitListener {

    
    private int type;
    
    public Contrasts(int type)
    {
        
        this.type = type;
        
        int linelength, lines;
        
        if (type == 2)
        {
            linelength = 1494;
            lines = 1494;
        }
        else
        {
            linelength = 8964;
            lines = 8964;
        }
        
        
        ArrayList command = new ArrayList();

        command.add("./contrast");
        command.add(String.valueOf(linelength));
        command.add(String.valueOf(lines));
        
        

        Shell shell = new Shell(this);

        shell.start(command);
        
    }
    
    
    @Override
    public boolean onShellExit(int exitCode, ArrayList output) {
    
        Logger.logger.append("> Contrast exited with status " + exitCode + "\n");
        
        for (Object line : output)
            Logger.logger.append(((String)line) + "\n");
        
        
        Tiffs tiff;
       
        if (exitCode == 0)
        {   
            if (type == 2)
            {
                scan.ScanPanel.instance.previewContrastDone = true;
                tiff = new Tiffs("1494x1494+0", "rgb:contrast.bin", "contrast.tiff");                
            }
            else
                tiff = new Tiffs("8964x8964+0", "rgb:contrast.bin", "contrast.tiff");
        }    
        
        
        return false;
    
    }   
    
    
}
