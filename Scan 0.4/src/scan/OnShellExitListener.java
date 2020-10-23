package scan;

import java.util.ArrayList;


public interface OnShellExitListener {
    
    boolean onShellExit(int exitCode, ArrayList output);
    
}
