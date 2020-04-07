package scan;

import java.awt.EventQueue;


public class InThread {   
   
    Object object;
    
    
    public InThread(Object object)
    {
        
        this.object = object;
        
    }
    
    
    private void run() 
    { 
  
        EventQueue.invokeLater( new Runnable() {
            
            @Override
            public void run() {
                ((RunInThread)object).start();
            }
            
        }); 
        
    } 
  
    
    public void start() 
    { 
        this.run();        
    } 
    
}
