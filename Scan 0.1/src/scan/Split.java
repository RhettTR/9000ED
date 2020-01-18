package scan;



import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;


public class Split implements OnShellExitListener {
    
    
    final private int LOW = 40;     // the maximum dark value
    final private int WIDTH = 5;    // the minimum width of a dark area
    final private int ERROR = 3;    // the maximum shift of dark areas 
                                    // between columns
    public static int FRAME = 186;  // height and width of a frame
    public static int BORDER = 2;   // border of each frame
    
   
    
    public Split()
    {
        
        this.image = readFile();
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.column0 = (int)(width*0.1);
        this.column1 = (int)(width*0.5);
        this.column2 = (int)(width*0.9);
        this.columns = new Column[3];
        this.standardFrames = new Frames();
        standardFrames.add(new Frame(4));
        standardFrames.add(new Frame(204));
        standardFrames.add(new Frame(403));
        
        scan.Main.jLabel1.setIcon(new ImageIcon(image));
        
    }
    
    
    private final int width;        // vertical size of image
    private final int height;       // horizontal size image
    final private int column0;      // x coordinate 
    final private int column1;
    final private int column2;
    
    private final Column[] columns; 
    private final Frames standardFrames;
    private final BufferedImage image;

    
    
    private class Pixel
    {
        int r;
        int g;
        int b;
            
        public Pixel(int r, int g, int b) 
        {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    } 
    
    private class Column extends ArrayList<Pixel> {}
    
    
    private class Area
    {
        int start;
        int end;
    }
    
    private class Areas extends ArrayList<Area> {}
    
    
    public class Frame
    {
        Point topLeft;       
        
        public Frame(int top)
        {
            this.topLeft  = new Point(0,top);            
        }
    }
    
    public class Frames extends ArrayList<Frame> {}
    
    
     
    
    private BufferedImage readFile()
    {
        
        File file = new File(System.getProperty("java.class.path") + "/scan/c/thumb.tiff");
               
        ImageInputStream imageInputStream;
        
        BufferedImage bufferedImage = null;
        
        try {
            
            imageInputStream = new FileImageInputStream(file);
        

            Iterator<ImageReader> ite  = ImageIO.getImageReadersByMIMEType("image/tiff");
            

            while (ite.hasNext()) {
                ImageReader imageReader = ite.next();
                imageReader.setInput(imageInputStream);
                bufferedImage = imageReader.read(0);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return bufferedImage;
        
    }
    
    
    private Column readColumn(int xposition)
    {
        Column column = new Column();
        
        for (int i = 0; i < height; i++)
        {
            int rgb = image.getRGB(xposition, i);
             
            int r = (rgb >> 16) & 0xFF;      
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            
            column.add(new Pixel(r,g,b));
        }
        
        return column;
    }
    
    
    private void readColumns()
    {
        columns[0] = readColumn(column0);
        columns[1] = readColumn(column1);
        columns[2] = readColumn(column2);
    }
   
    
    private Areas findSplit(Column column)
    {
        ArrayList<Boolean> normalizedColumn = new ArrayList<>();
        
        column.forEach((pixel) -> {
            if (pixel.r <= LOW && pixel.g <= LOW && pixel.b <= LOW)
                normalizedColumn.add(true);
            else
                normalizedColumn.add(false);
        });
        
        Object[] normalized = normalizedColumn.toArray();
        
        Areas areas = new Areas();
        
        int i = 0; 
        
        while (i < normalized.length)
        {
            int count = 0;
            
            while ((Boolean)normalized[i] && (i + 1) < normalized.length)
            {
                i++;
                count++;
            }
            
            if (count >= WIDTH)
            {
                Area area = new Area();
                
                area.start = i - count;                
                area.end = i - 1;
                
                areas.add(area);
            }
            
            if (i + 1 < normalized.length)
                i++;
            else
                break;
        }
        
        return areas;
    }
    
    
    private Areas compare(Areas areas1, Areas areas2, Areas areas3)
    {
        Areas areas = new Areas();
            
       
        ArrayList list = new ArrayList<Areas>();
        
        if (areas1.size() == areas3.size())
        {
            list.add(areas1);
            list.add(areas3);
        }
        
        if (areas1.size() == areas2.size())
        {
            if (!list.contains(areas1) ) list.add(areas1);
            list.add(areas2);
        }
        
        if (areas2.size() == areas3.size())
        {
            if (!list.contains(areas2) ) list.add(areas2);
            if (!list.contains(areas3) ) list.add(areas3);
        }
        
 
        
        if (list.isEmpty())       // return middle
            return areas2;
        
        if (list.size() == 3)       // remove middle
            list.remove(1);
          
        for (Area area1 : (Areas)list.get(0))       // generate average
            for (Area area2 : (Areas)list.get(1))
                if ((Math.abs(area1.start - area2.start) <= ERROR) &&
                    (Math.abs(area1.end - area2.end) <= ERROR))
                {
                    Area area = new Area();                    
                    area.start = (int)((area1.start + area2.start) / 2);
                    area.end = (int)((area1.end + area2.end) / 2);
                    areas.add(area);
                    break;
                }
        
        return areas;
    }
    
    
    private Areas findAreas()
    {
        Areas areas1 = findSplit(columns[0]);
        Areas areas2 = findSplit(columns[1]);
        Areas areas3 = findSplit(columns[2]);
        
        Areas result = compare(areas1, areas2, areas3);
        
        return result;        
    }
    
    
    private String findWho(Areas areas)
    {
        
        String result = "";
        
        
        // 1 between 0 - 127
        // 2 between 128 - 313
        // 3 between 314 - 441
        // 4 between 442 - 660
            
        for (Area area : areas)
        {
            if (area.end <= 127)
                result += String.valueOf(1);
            if (area.end > 127 && area.end <= 313)
                result += String.valueOf(2);
            if (area.end > 314 && area.end <= 441)
                result += String.valueOf(3);
            if (area.end > 441 && area.end < 660)
                result += String.valueOf(4);
        }
        
        return result;
        
    }
    

    private Frames findFrames(Areas areas)
    {
        Frames frames = new Frames();
        String who = findWho(areas);
        
        
        switch (areas.size())
        {
            case 4:
                                
                frames.add(new Frame(areas.get(0).end-BORDER));
                frames.add(new Frame(areas.get(1).end-BORDER));
                frames.add(new Frame(areas.get(2).end-BORDER));
                return frames;    
                
            case 3:

                if ("234".equals(who))
                {
                    frames.add(new Frame(areas.get(0).start-FRAME-BORDER));
                    frames.add(new Frame(areas.get(1).end-BORDER));
                    frames.add(new Frame(areas.get(2).end-BORDER));
                }
                if ("134".equals(who))
                {
                    frames.add(new Frame(areas.get(0).end-BORDER));
                    frames.add(new Frame(areas.get(1).start-FRAME-BORDER));
                    frames.add(new Frame(areas.get(2).end-BORDER));
                }
                if ("124".equals(who))
                {
                    frames.add(new Frame(areas.get(0).end-BORDER));
                    frames.add(new Frame(areas.get(1).end-BORDER));
                    frames.add(new Frame(areas.get(2).start-FRAME-BORDER));
                }
                if ("123".equals(who))
                {
                    frames.add(new Frame(areas.get(0).end-BORDER));
                    frames.add(new Frame(areas.get(1).end-BORDER));
                    frames.add(new Frame(areas.get(2).end-BORDER));
                }
                return frames;
                
            case 2:
   
                if ("34".equals(who))
                {
                    frames.add(standardFrames.get(0));
                    frames.add(new Frame(areas.get(0).start-FRAME-BORDER));
                    frames.add(new Frame(areas.get(1).start-FRAME-BORDER));
                }
                if ("24".equals(who))
                {
                    frames.add(new Frame(areas.get(0).start-FRAME-BORDER));
                    frames.add(new Frame(areas.get(0).end-BORDER));
                    frames.add(new Frame(areas.get(1).start-FRAME-BORDER));
                }
                if ("23".equals(who))
                {
                    frames.add(new Frame(areas.get(0).start-FRAME-BORDER));
                    frames.add(new Frame(areas.get(1).end-BORDER));
                    frames.add(new Frame(areas.get(2).end-BORDER));
                }
                if ("14".equals(who))
                {
                    frames.add(new Frame(areas.get(0).end-BORDER));
                    frames.add(standardFrames.get(1));
                    frames.add(new Frame(areas.get(1).start-FRAME-BORDER));
                } 
                if ("13".equals(who))
                {
                    frames.add(new Frame(areas.get(0).end-BORDER));
                    frames.add(new Frame(areas.get(1).start-FRAME-BORDER));
                    frames.add(new Frame(areas.get(1).end-BORDER));
                } 
                if ("12".equals(who))
                {
                    frames.add(new Frame(areas.get(0).end-BORDER));
                    frames.add(new Frame(areas.get(1).end-BORDER));
                    frames.add(standardFrames.get(2));
                } 
                return frames;
                
            case 1:
                
                if ("4".equals(who))
                {
                    frames.add(standardFrames.get(0));
                    frames.add(standardFrames.get(1));
                    frames.add(new Frame(areas.get(0).start-FRAME-BORDER));
                }
                if ("3".equals(who))
                {
                    frames.add(standardFrames.get(0));
                    frames.add(new Frame(areas.get(0).start-FRAME-BORDER));
                    frames.add(new Frame(areas.get(0).end-BORDER));
                }
                if ("2".equals(who))
                {
                    frames.add(new Frame(areas.get(0).start-FRAME-BORDER));
                    frames.add(new Frame(areas.get(1).end-BORDER));
                    frames.add(standardFrames.get(2));
                }
                if ("1".equals(who))
                {
                    frames.add(new Frame(areas.get(0).end-BORDER));
                    frames.add(standardFrames.get(1));
                    frames.add(standardFrames.get(2));
                }
                return frames;
                
            case 0:                
            default:
                // either no split or too many splits, use standard split
                return standardFrames;     
        }
        
    }
    
    
    BufferedImage copyImagePiece(BufferedImage image, int offset)
    {
        
        BufferedImage newImage; 
        
        int max = FRAME + (2*BORDER);
        
        if (!(offset + max < this.height))
            max = this.height - offset;
            
        
        newImage = image.getSubimage(0, offset, FRAME, max);
   
        return newImage;
        
    }
    
    
    public Frames doSplit()
    {
       
 
        readColumns();
        Areas areas = findAreas();
        Frames frames = findFrames(areas);
        

        scan.Main.jLabel2.setIcon(new ImageIcon(copyImagePiece(image, frames.get(0).topLeft.y)));
        scan.Main.jLabel3.setIcon(new ImageIcon(copyImagePiece(image, frames.get(1).topLeft.y)));
        scan.Main.jLabel4.setIcon(new ImageIcon(copyImagePiece(image, frames.get(2).topLeft.y)));
        
        return frames;
        
    }
    
    
    
    
    @Override
    public boolean onShellExit(int exitCode, ArrayList output) {
    
        return false;
    
    }

    
}
