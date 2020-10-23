package scan;

import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Tiffs implements OnShellExitListener {
    
    
    private int type = 0;
    private String outputFile = "";
    
    
    public Tiffs(String resolution, String binfile, String outputFile, String bitDepth)
    {
        
        
        if (resolution.contains("186"))    
            type = 1;
        
        if (resolution.contains("1494"))    
            type = 2;
        
        if (resolution.contains("8964"))    
            type = 3;
        
               
        this.outputFile = outputFile;
        
        
        ArrayList command = new ArrayList();

        command.add("convert");
        command.add("-depth");
        command.add(bitDepth);
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
            
            if (type == 1)   // display thumbs
                new Split().doSplit();            
            else 
            if (type == 2) // display scaled preview 
            {                  
                InThread thread = new InThread(new ReadTiff(new SetImage() {
                    @Override
                    public void to(BufferedImage image) {
                        ScanPanel.preview.setImage(image);    
                    }
                }, outputFile));
                
                thread.start();  
                
            }
            else
            if (type == 3)   // full scan
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
