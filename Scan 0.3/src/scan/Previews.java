package scan;

import java.util.ArrayList;
import static scan.Split.frames;



public class Previews implements OnShellExitListener {
    
    
    public Previews(String device) 
    {
            
        int frameIndex = Thumb.selectedIndex;
        
        int offset = frames.get(frameIndex).topLeft.y + scan.Split.BORDER;
        
        String input = String.valueOf(offset);
        
        
        ArrayList command = new ArrayList();

        command.add("./preview");
        command.add(device);
        command.add(input);
        command.add("./scan.bin");


        Shell shell = new Shell(this);

        shell.start(command);
        
    }

    
    @Override
    public boolean onShellExit(int exitCode, ArrayList output) {   
    
        Logger.logger.append("> Preview exited with status " + exitCode + "\n");
        
        for (Object line : output)
            scan.Logger.logger.append(((String)line) + "\n");
        
        
        if (exitCode == 0)
        {
            
            
            try
            {
                scan.ScanPanel.instance.gamma = Double.parseDouble(output.get(0).toString().trim());
                System.out.println("Gamma: " + String.valueOf(scan.ScanPanel.instance.gamma));
            }
            catch (NumberFormatException e)
            {
                Logger.logger.append("Gamma value error -> " + e.getMessage() + "\n");
                return false;
            }
            
            
            try
            {
                scan.ScanPanel.instance.evRed = Integer.parseInt(output.get(1).toString().trim());
                scan.ScanPanel.instance.evGreen = Integer.parseInt(output.get(2).toString().trim());
                scan.ScanPanel.instance.evBlue = Integer.parseInt(output.get(3).toString().trim());
            }
            catch (NumberFormatException e)
            {
                Logger.logger.append("Exposure value error -> " + e.getMessage() + "\n");
                return false;
            }

        



            Tiffs tiff;

            tiff = new Tiffs("1494x1494+0", "rgb:scan.bin", "preview.tiff"); 
            
        }
            
        
        
        return false;
        
    }
    
}
