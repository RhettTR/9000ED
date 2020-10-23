package scan;

import java.util.ArrayList;


public class Thumbs implements OnShellExitListener {
    
    
    private final String colorComposition;
    
    public Thumbs(String device)
    {
        
        ArrayList command = new ArrayList();

        
        colorComposition = Application.settings.getColorComposition();
        
        
        command.add("./thumb");
        command.add(device);
        command.add(Application.settings.getScanPositive());        // 0=negative 1=positive
        command.add(Application.settings.getScanColor());           // 5=color 2=BW
        command.add(colorComposition);                              // rgb gray 

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
           
            if ("rgb".equals(colorComposition))
                tiff = new Tiffs("186x660+0", "rgb:scan.bin", "thumb.tiff", "16");
             else
                tiff = new Tiffs("186x660+0", "gray:scan.bin", "thumb.tiff", "16");
        
        }
        
        return false;
    
    }
    
    
}
