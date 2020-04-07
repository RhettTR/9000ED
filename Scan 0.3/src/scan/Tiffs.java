package scan;

import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Tiffs implements OnShellExitListener {
    
    
    private final int tiffType;
    private final String outputFile;
    
    
    public Tiffs(String resolution, String binfile, String outputFile)
    {
        
        if ("thumb.tiff".equals(outputFile))
            tiffType = 0;
        else
            if (resolution.contains("1494"))    
                tiffType = 1;
            else
                tiffType = 2;
        
        this.outputFile = outputFile;
        
        
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
            scan.Logger.logger.append(((String)line) + "\n");
        
        
        if (exitCode == 0)
        {
            if (tiffType == 0)   // display thumbs
            {
                if (exitCode == 0)
                    new Split().doSplit();
            }
            else if (tiffType == 1) // display scaled preview 
            {                
                
                InThread thread = new InThread(new ReadTiff(new SetImage() {
                    @Override
                    public void to(BufferedImage image) {
                        ScanPanel.preview.setImage(image);    
                    }
                }, outputFile));
                thread.start();  
                
            }
            else    // full scan
            {
                InThread thread = new InThread(new ReadTiff(new SetImage() {
                    @Override
                    public void to(BufferedImage image) {
                        scan.Application.scanFrame.setImage(image);
                    }
                }, outputFile));
                thread.start();
            }
        }
            
        
        return false;
        
    }
    
    
}
