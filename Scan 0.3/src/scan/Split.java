package scan;



import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Split implements OnShellExitListener {
    
    
    final private int DEV   = 10;   // the deviation of dark values
    final private int WIDTH = 4;    // the minimum width of a dark area
    final private int ERROR = 4;    // the maximum shift of dark areas 
                                    // between columns
    public static int FRAME = 186;  // height and width of a frame
    public static int BORDER = 2;   // border of each frame
    
    
    public static Frames frames = null;
   
   
     
    private final int width;        // vertical size of image
    private final int height;       // horizontal size image
    final private int column0;      // x coordinate 
    final private int column1;
    final private int column2;
    
    private final Column[] columns; 
    private final Frames standardFrames;
    private BufferedImage image;  
    
    private int low = 0;            // the lowest dark value
    
        
    
    public Split()
    {
        
        ReadTiff readTiff = new ReadTiff(null, "thumb.tiff");
        
        try {
            this.image = readTiff.doInBackground();
        } catch (Exception ex) {
            scan.Logger.logger.append("ERROR File not read: " + ex.getMessage() + "\n");
        }                 
        
        
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
        
    }
    
  
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
   
    
    private int computeColumn(Column column)
    {
        int lowest = 0xFF;
        
        for (int i = 0; i < height; i++)
        {
            int r = column.get(i).r;      
            int g = column.get(i).g; 
            int b = column.get(i).b; 
            
            int max = (r > g ? r : g);
            max = (max > b ? max : b);
            
            lowest = (max < lowest ? max : lowest);
        }
        
        return lowest;
        
    }
    
    
    private int computeLow()
    {
        int low0 = computeColumn(columns[0]);
        int low1 = computeColumn(columns[1]);
        int low2 = computeColumn(columns[2]);
        
        int min = (low0 < low1 ? low0 : low1);
        min = (min < low2 ? min : low2);
        
        return min + DEV;
    }
    
    
    private Areas findSplit(Column column)
    {
        ArrayList<Boolean> normalizedColumn = new ArrayList<>();
        
        column.forEach((pixel) -> {
            if (pixel.r <= low && pixel.g <= low && pixel.b <= low)
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
       
        ArrayList list = new ArrayList<Area>();
        
        // find common areas within error
        areas1.forEach((area1) -> {
            areas3.stream().filter((Area area3) -> (Math.abs(area1.start - area3.start) <= ERROR) &&
                    (Math.abs(area1.end - area3.end) <= ERROR)).map((area3) -> {
                        Area area = new Area();
                        area.start = (int)((area1.start + area3.start) / 2);
                        area.end = (int)((area1.end + area3.end) / 2);
                return area;
            }).forEachOrdered((area) -> {
                list.add(area);
            });    
        });
        
        
        Areas common = new Areas();
        
        for (int i = 0; i < list.size(); i++)
            for (Area area3 : areas3)
                if ((Math.abs(((Area)list.get(i)).start - area3.start) <= ERROR) &&
                    (Math.abs(((Area)list.get(i)).end - area3.end) <= ERROR))
                {
                    Area result = new Area();                    
                    result.start = (int)((((Area)list.get(i)).start + area3.start) / 2);
                    result.end = (int)((((Area)list.get(i)).end + area3.end) / 2);
                    common.add(result);
                }
        
        return common;
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
        
        
        // 1 between 0 - 102
        // 2 between 103 - 238
        // 3 between 239 - 557
        // 4 between 558 - 659
        
        
        int free = 660 - (3*FRAME);
            
        for (Area area : areas)
        {
            if (area.end <= free)
                result += String.valueOf(1);
            else if (area.end > free && area.end <= (660 / 2))
                result += String.valueOf(2);
            else if (area.end > (660 / 2) && area.end <= 660 - free)
                result += String.valueOf(3);
            else if (area.end > 660 - free)
                result += String.valueOf(4);
        }
        
        return result;
        
    }
    

    private Frames findFrames(Areas areas)
    {
        Frames out = new Frames();
        String who = findWho(areas);
        
        
        switch (areas.size())
        {
            case 4:
                                
                out.add(new Frame(areas.get(0).end-BORDER));
                out.add(new Frame(areas.get(1).end-BORDER));
                out.add(new Frame(areas.get(2).end-BORDER));
                return out;    
                
            case 3:

                if ("234".equals(who))
                {
                    out.add(new Frame(areas.get(0).end-BORDER));
                    out.add(new Frame(areas.get(1).end-BORDER));
                    out.add(new Frame(areas.get(2).start-FRAME-BORDER));
                }
                if ("134".equals(who))
                {
                    out.add(new Frame(areas.get(0).end-BORDER));
                    out.add(new Frame(areas.get(1).start-FRAME-BORDER));
                    out.add(new Frame(areas.get(2).start-FRAME-BORDER));
                }
                if ("124".equals(who))
                {
                    out.add(new Frame(areas.get(0).end-BORDER));
                    out.add(new Frame(areas.get(1).end-BORDER));
                    out.add(new Frame(areas.get(2).start-FRAME-BORDER));
                }
                if ("123".equals(who))
                {
                    out.add(new Frame(areas.get(0).end-BORDER));
                    out.add(new Frame(areas.get(1).end-BORDER));
                    out.add(new Frame(areas.get(2).end-BORDER));
                }
                return out;
                
            case 2:
   
                if ("34".equals(who))
                {
                    out.add(standardFrames.get(0));
                    out.add(new Frame(areas.get(0).start-FRAME-BORDER));
                    out.add(new Frame(areas.get(1).start-FRAME-BORDER));
                }
                if ("24".equals(who))
                {
                    out.add(new Frame(areas.get(0).start-FRAME-BORDER));
                    out.add(new Frame(areas.get(0).end-BORDER));
                    out.add(new Frame(areas.get(1).start-FRAME-BORDER));
                }
                if ("23".equals(who))
                {
                    out.add(new Frame(areas.get(0).start-FRAME-BORDER));
                    out.add(new Frame(areas.get(0).end-BORDER));
                    out.add(new Frame(areas.get(1).end-BORDER));
                }
                if ("14".equals(who))
                {
                    out.add(new Frame(areas.get(0).end-BORDER));
                    out.add(standardFrames.get(1));
                    out.add(new Frame(areas.get(1).start-FRAME-BORDER));
                } 
                if ("13".equals(who))
                {
                    out.add(new Frame(areas.get(0).end-BORDER));
                    out.add(new Frame(areas.get(1).start-FRAME-BORDER));
                    out.add(new Frame(areas.get(1).end-BORDER));
                } 
                if ("12".equals(who))
                {
                    out.add(new Frame(areas.get(0).end-BORDER));
                    out.add(new Frame(areas.get(1).end-BORDER));
                    out.add(standardFrames.get(2));
                } 
                return out;
                
            case 1:
                
                if ("4".equals(who))
                {
                    out.add(standardFrames.get(0));
                    out.add(standardFrames.get(1));
                    out.add(new Frame(areas.get(0).start-FRAME-BORDER));
                }
                if ("3".equals(who))
                {
                    out.add(standardFrames.get(0));
                    out.add(new Frame(areas.get(0).start-FRAME-BORDER));
                    out.add(new Frame(areas.get(0).end-BORDER));
                }
                if ("2".equals(who))
                {
                    out.add(new Frame(areas.get(0).start-FRAME-BORDER));
                    out.add(new Frame(areas.get(1).end-BORDER));
                    out.add(standardFrames.get(2));
                }
                if ("1".equals(who))
                {
                    out.add(new Frame(areas.get(0).end-BORDER));
                    out.add(standardFrames.get(1));
                    out.add(standardFrames.get(2));
                }
                return out;
                
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
    
    
    
    
    
    public void doSplit()
    {
       
        readColumns();
        low = computeLow();
        Areas areas = findAreas();
        frames = findFrames(areas);
        

        scan.ScanPanel.thumbs[0].setImage(copyImagePiece(image, frames.get(0).topLeft.y));
        scan.ScanPanel.thumbs[1].setImage(copyImagePiece(image, frames.get(1).topLeft.y));
        scan.ScanPanel.thumbs[2].setImage(copyImagePiece(image, frames.get(2).topLeft.y)); 
                
    }
    
    
    
    
    @Override
    public boolean onShellExit(int exitCode, ArrayList output) {
    
        return false;
    
    }

    
}
