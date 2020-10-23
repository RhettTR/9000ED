package scan;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.SwingWorker;


public class WriteTiff extends SwingWorker<Integer, Integer> implements RunInThread {
   
  
    BufferedImage image;
    String outputFile;
    
    
    public WriteTiff(BufferedImage image, String outputFile)
    {         
        this.image = image;
        this.outputFile = outputFile;     
    }
    
 
    @Override
    public void start()
    {

        scan.Application.progressLabel.setText("Saving file ...");        
        
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                if ("progress".equalsIgnoreCase(pce.getPropertyName())) { 
                    scan.Application.progressBar.setValue((int)pce.getNewValue());
                    scan.Application.progressBar.update(scan.Application.progressBar.getGraphics());
                }
            }
        });
        
        this.execute();
        
        //            
     

     
    }

    
    @Override
    protected Integer doInBackground() throws Exception {
    
   
        ImageWriter imageWriter;           
                
        File file = new File(outputFile);

        try (ImageOutputStream imageOutputStream = new FileImageOutputStream(file)) 
        {
            Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType("image/tiff");
            
            if (imageWriters.hasNext())
            {
                imageWriter = imageWriters.next();
                ImageWriteParam iwp = imageWriter.getDefaultWriteParam();
                imageWriter.setOutput(imageOutputStream);
                imageWriter.addIIOWriteProgressListener(new IIOWriteProgressListener() {
                    @Override
                    public void imageStarted(ImageWriter writer, int i) {}

                    @Override
                    public void imageProgress(ImageWriter writer, float f) {
                        setProgress(Math.round(f));
                    }

                    @Override
                    public void imageComplete(ImageWriter writer) {}

                    @Override
                    public void thumbnailStarted(ImageWriter writer, int i, int i1) {}

                    @Override
                    public void thumbnailProgress(ImageWriter writer, float f) {}

                    @Override
                    public void thumbnailComplete(ImageWriter writer) {}

                    @Override
                    public void writeAborted(ImageWriter writer) {
                        scan.Logger.logger.append("ERROR File not saved: " + outputFile + "\n");
                    }
                });
                
                imageWriter.write(null, new IIOImage(image, null, null), iwp);
                scan.Logger.logger.append("File saved" + "\n");
            }
            else
                throw new IOException("No ImageWriter for tiff");
            
            
            imageWriter.dispose();
        }
        
        
        return 0; 
    
    }
    
    
    
    @Override
    protected void done() {
    
        scan.Application.progressLabel.setText("");
        scan.Application.progressBar.setValue(0);
    
    }

    
}
