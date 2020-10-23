package scan;

import java.util.ArrayList;
import static scan.Split.frames;



public class Previews implements OnShellExitListener {
    
    
    private final String colorComposition;
    
    
    public Previews(String device) 
    {
            
        int frameIndex = Thumb.selectedIndex;
        
        int number = frames.get(frameIndex).topLeft.y + scan.Split.BORDER + 
                Application.settings.getOffset();
        
        String offset = String.valueOf(number);
        
        
        ArrayList command = new ArrayList();
        
        
        colorComposition = Application.settings.getColorComposition();
        

        command.add("./preview");
        command.add(device);
        command.add(offset);
        command.add(Application.settings.getScanPositive());        // 0=negative 1=positive
        command.add(Application.settings.getScanColor());           // 5=color 2=BW
        command.add(colorComposition);                              // rgb gray 



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
            
            

            Contrasts contrast = new Contrasts(2, "./preview.bin");
            //Tiffs tiff = new Tiffs("1494x1494+0", "rgb:preview.bin", "preview.tiff", "16");
            
            
        }
            
        
        
        return false;
        
    }
    
}
