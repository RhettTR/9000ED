package scan;

import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Tiff implements OnShellExitListener {
    
    
    private final int tiffType;
    private static String outputFile;
    
    
    public Tiff(String resolution, String binfile, String outputFile)
    {
        
        if ("thumb.tiff".equals(outputFile))
            tiffType = 0;
        else
            tiffType = 1;   // preview
        
        Tiff.outputFile = outputFile;
        
        
        ArrayList command = new ArrayList();

        command.add("convert");
        command.add("-depth");
        command.add("16");
        command.add("-size");
        command.add(resolution);
        command.add(binfile);
        command.add(outputFile);
        

        Shell shell = new Shell(this);

        shell.start(command);    
        
    }
    
    
    
    
    
    @Override
    public boolean onShellExit(int exitCode, ArrayList output) {    
        
        for (Object line : output)
            scan.Main.logger.append(((String)line) + "\n");
        
        
        if (exitCode == 0)
        {
            if (tiffType == 0)   // display thumbs
            {
                if (exitCode == 0)
                    new Split().doSplit();
            }
            else // display scaled preview 
            {                
                BufferedImage newimage = new ReadTiff(outputFile).read();
                Main.preview.setImage(newimage);
            }
        }
            
        
        return false;
        
    }
    
    
}
