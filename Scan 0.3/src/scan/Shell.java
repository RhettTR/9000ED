package scan;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class Shell {
    
    
    private OnShellExitListener onShellExitListener;
  
     
    public Shell(OnShellExitListener onShellExitListener)
    {
        registerOnShellExitListener(onShellExitListener); 
    }
    
    private void registerOnShellExitListener(OnShellExitListener listener) 
    { 
        this.onShellExitListener = listener; 
    } 
  
     
    private void doRun(ArrayList command) 
    { 
  
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                
                ProcessBuilder builder = new ProcessBuilder();
                
                builder.command(command);
                
                
                try {
                    
                    builder.directory(new File(System.getProperty("java.class.path") + "/scan/c"));
                    builder.redirectErrorStream(true);
                    
                    boolean loop;
                    
                    do {
                        
                        Process process = builder.start();
                        
                        ArrayList output = new ArrayList();
                        
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(process.getInputStream(), "UTF-8"));
                        
                        String line;
                        
                        while ((line = reader.readLine()) != null)
                            output.add(line);
                        
                        
                        int exitCode = process.waitFor();
                        
                        
                        loop = onShellExitListener.onShellExit(exitCode, output);
                        
                        if (loop)
                            Thread.sleep(2000);
                        
                        
                    } while (loop);
                    
                    
                } catch (InterruptedException | IOException ex) {
                    Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }).start(); 
        
    } 
  
    
    public void start(ArrayList command) 
    { 
  
        this.doRun(command); 
        
    } 
    
}
