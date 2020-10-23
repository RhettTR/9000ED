package scan;

import java.util.ArrayList;
import static scan.Split.frames;


public class Scans implements OnShellExitListener {
    
    
    private final String colorComposition;
    
    public Scans(String device, int evR, int evG, int evB) 
    {
            
        int frameIndex = Thumb.selectedIndex;
        
        int number = frames.get(frameIndex).topLeft.y + scan.Split.BORDER + 
                Application.settings.getOffset();
        
        String offset = String.valueOf(number);
        String r = String.valueOf(evR);
        String g = String.valueOf(evG);
        String b = String.valueOf(evB);
        
        
        ArrayList command = new ArrayList();
        
        
        colorComposition = Application.settings.getColorComposition();
        

        command.add("./scan");
        command.add(device);
        command.add(offset);
        command.add(r);
        command.add(g);
        command.add(b);
        command.add(Application.settings.getScanPositive());        // 0=negative 1=positive
        command.add(Application.settings.getScanColor());           // 5=color 2=BW
        command.add(colorComposition);                              // rgb gray 


        Shell shell = new Shell(this);

        shell.start(command);
        
    }

    
    @Override
    public boolean onShellExit(int exitCode, ArrayList output) {   
    
        
        Logger.logger.append("> Scan exited with status " + exitCode + "\n");
        
        for (Object line : output)
            scan.Logger.logger.append(((String)line) + "\n");
        

       
        if (exitCode == 0)
        {   
            
            Contrasts contrast = new Contrasts(3, "./scan.bin");
            
            
        }
        
        
        return false;
        
    }
    
    
    
}
