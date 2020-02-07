package scan;

import java.util.ArrayList;
import static scan.Split.frames;



public class Previews implements OnShellExitListener {
    
    
    public Previews(String device) 
    {
        
        if (Thumb.selected == null)
        {
            scan.Main.logger.append("> A thumb must be selected\n");
            return;
        }
        
        int frameIndex = Thumb.selectedIndex;
        
        int offset = frames.get(frameIndex).topLeft.y;
        
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
    
        scan.Main.logger.append("> Preview exited with status " + exitCode + "\n");
        
        for (Object line : output)
            scan.Main.logger.append(((String)line) + "\n");
        
        
        Tiff tiff;
       
        if (exitCode == 0)
            tiff = new Tiff("1494x1494+0", "rgb:scan.bin", "preview.tiff");
        
        
        return false;
        
    }
    
}
