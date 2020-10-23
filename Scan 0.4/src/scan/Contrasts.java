package scan;

import java.util.ArrayList;



public class Contrasts implements OnShellExitListener {

    
    private int type;
    
    private String bitDepth = "16";
    private String colorComposition = "";
    
    // type 1 - thumbs (not used)
    //      2 - preview
    //      3 - scan
    
    public Contrasts(int type, String inputFile)
    {
        
        this.type = type;
        
        int linelength = 186, lines = 660;
        
        switch (type)
        {
            case 2  : linelength = 1494;
                      lines = 1494;
                      break;
            case 3  : linelength = 8964;
                      lines = 8964;
                      break;
            default : Logger.logger.append("> Contrast type " + type + "\n");
                      this.type = 1;  
                      return;
        }
       
        
        ArrayList command = new ArrayList();
        
        
        if (Application.settings.is8BitScan() && type == 3)
            bitDepth = "8";
        else
            bitDepth = "16";
        colorComposition = Application.settings.getColorComposition();

        command.add("./contrast");
        command.add(String.valueOf(linelength));
        command.add(String.valueOf(lines));
        command.add(bitDepth);
        command.add(inputFile);
        command.add(colorComposition);

        
        

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
            
            switch (type)
            {
                case 2 : if ("rgb".equals(colorComposition))   
                            tiff = new Tiffs("1494x1494+0", "rgb:scan.bin", "preview.tiff", bitDepth);
                         else
                            tiff = new Tiffs("1494x1494+0", "gray:scan.bin", "preview.tiff", bitDepth);
                         break;
                case 3 : if ("rgb".equals(colorComposition))   
                            tiff = new Tiffs("8964x8964+0", "rgb:scan.bin", "scan.tiff", bitDepth);
                         else
                            tiff = new Tiffs("8964x8964+0", "gray:scan.bin", "scan.tiff", bitDepth);
                         break;
                default : 
                         break;  
            }
            
        }    
        
        
        return false;
    
    }   
    
    
}
