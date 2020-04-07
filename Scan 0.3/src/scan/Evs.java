package scan;

import java.util.ArrayList;


public class Evs implements OnShellExitListener {

    
    public Evs (double gamma, int evR, int evG, int evB)
    {
        
        String gammaString = Double.toString(gamma);
        String rString = Integer.toString(evR);
        String gString = Integer.toString(evG);
        String bString = Integer.toString(evB);
        
        ArrayList command = new ArrayList();

        command.add("./ev");
        command.add(gammaString);
        command.add(rString);
        command.add(gString);
        command.add(bString);
        
        
        Shell shell = new Shell(this);

        shell.start(command);
        
    }
    
    
    @Override
    public boolean onShellExit(int exitCode, ArrayList output) {
     
        Logger.logger.append("> Ev exited with status " + exitCode + "\n");
        
        for (Object line : output)
            scan.Logger.logger.append(((String)line) + "\n");
        
        
        
        if (exitCode == 0)
        {
            java.lang.Integer evRed, evGreen, evBlue;
            
            try
            {
                evRed = Integer.parseInt(output.get(0).toString().trim());
                evGreen = Integer.parseInt(output.get(1).toString().trim());
                evBlue = Integer.parseInt(output.get(2).toString().trim());
                
                System.out.println("evR: " + String.valueOf(evRed));
                System.out.println("evG: " + String.valueOf(evGreen));
                System.out.println("evB: " + String.valueOf(evBlue));
            }
            catch (NumberFormatException e)
            {
                Logger.logger.append("Exposure value error -> " + e.getMessage() + "\n");
                return false;
            }


            Scans scans;

            if (scan.DetectDevice.device != null)
                if (scan.DetectDevice.device instanceof String)
                    if (scan.DetectDevice.device.contains("/dev/sg"))
                        scans = new Scans(scan.DetectDevice.device, evRed, evGreen, evBlue);
        }
        
        return false;
        
    }
    
}
