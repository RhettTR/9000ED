package scan;

import java.util.ArrayList;
import static scan.Split.frames;


public class Scans implements OnShellExitListener {
    
    
    public Scans(String device, int evR, int evG, int evB) 
    {
            
        int frameIndex = Thumb.selectedIndex;
        
        int offset = frames.get(frameIndex).topLeft.y + scan.Split.BORDER;
        
        String input = String.valueOf(offset);
        String r = String.valueOf(evR);
        String g = String.valueOf(evG);
        String b = String.valueOf(evB);
        
        
        ArrayList command = new ArrayList();

        command.add("./scan");
        command.add(device);
        command.add(input);
        command.add(r);
        command.add(g);
        command.add(b);
        command.add("./scan.bin");


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
            Tiffs tiff;

            tiff = new Tiffs("8964x8964+0", "rgb:scan.bin", "scan.tiff");
        }
        
        
        return false;
        
    }
    
    
    
}
