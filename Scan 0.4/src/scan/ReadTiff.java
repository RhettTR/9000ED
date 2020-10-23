package scan;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.SwingWorker;


public class ReadTiff extends SwingWorker<BufferedImage, Integer> implements RunInThread {
    
    
    private final SetImage setImage;
    private final String fileName;
    
    
    public ReadTiff(SetImage setImage, String fileName)
    {
        this.setImage = setImage;
        this.fileName = fileName;
    }
    
    
    @Override
    public void start()
    {
        
        scan.Application.progressLabel.setText("Loading image ...");
        

        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                if ("progress".equalsIgnoreCase(pce.getPropertyName()))
                {
                    scan.Application.progressBar.setValue((int)pce.getNewValue());
                    scan.Application.progressBar.update(scan.Application.progressBar.getGraphics());
                }
            }
        });

        this.execute();
          
    }
        
        
    @Override
    protected BufferedImage doInBackground() throws Exception {
    
        
        File file = new File(System.getProperty("java.class.path") + "/scan/c/" + fileName);
               
        BufferedImage bufferedImage = null;
        
        ImageReader imageReader; 
        
        
        Iterator<ImageReader> ite  = ImageIO.getImageReadersByMIMEType("image/tiff");
        
        ImageInputStream imageInputStream = new FileImageInputStream(file);
        
        
        if (ite.hasNext()) 
        {
            imageReader = ite.next();
            imageReader.setInput(imageInputStream);
            imageReader.addIIOReadProgressListener(new IIOReadProgressListener() {
                @Override
                public void sequenceStarted(ImageReader reader, int i) {}

                @Override
                public void sequenceComplete(ImageReader reader) {}

                @Override
                public void imageStarted(ImageReader reader, int i) {}

                @Override
                public void imageProgress(ImageReader reader, float f) {
                    setProgress(Math.round(f));
                }

                @Override
                public void imageComplete(ImageReader reader) {}

                @Override
                public void thumbnailStarted(ImageReader reader, int i, int i1) {}

                @Override
                public void thumbnailProgress(ImageReader reader, float f) {}

                @Override
                public void thumbnailComplete(ImageReader reader) {}

                @Override
                public void readAborted(ImageReader reader) {
                    scan.Logger.logger.append("ERROR File not read: " + fileName + "\n");
                }

            });
            bufferedImage = imageReader.read(0);
        }
        else 
            throw new IOException("No ImageReader for tiff");
     
        
        return bufferedImage;
    
    }
    
    
    @Override
    protected void done() {
        try {            
            scan.Application.progressLabel.setText("");
            scan.Application.progressBar.setValue(0);
            BufferedImage image = get();
            setImage.to(image);
        } catch (InterruptedException | ExecutionException ex) {
            scan.Logger.logger.append("ERROR File not read: " + ex.getMessage() + "\n");
        }
    }

    
}
