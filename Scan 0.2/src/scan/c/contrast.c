#include <stdio.h>
#include <stdlib.h>
#include <math.h>



#define LINELENGTH  1494 	// preview
#define LINES		1494


#define	MAX			0xFFFF



#define CUTLOW		655			// 0.01 %
#define CUTHIGH		655


#define CONTRAST	1.075f		// 1.070
#define BRIGHTNESS	-0.030f		// -0.030



unsigned short less(long *array)
{
	
	for (int i = CUTLOW; i < (256*256); i++)
		if (array[i] > 0)
		{
			
			return i;
		}
			
	return 0x0;
}


unsigned short most(long *array)
{
	for (int i = (256*256) - 1 - CUTHIGH; i >= 0; i--)
		if (array[i] > 0)
			return i;

	return MAX;
}








int main(int argc, char * argv[])
{
	
	
	typedef struct Pixels {
		unsigned short red;
		unsigned short green;
		unsigned short blue;
	} Pixel;


	
	Pixel *buffer;
	

	
	
	FILE *ptr;
 
	ptr = fopen("./scan.bin", "rb");  	// r for read, b for binary
	
	
	fseek(ptr, 0L, SEEK_END);			// get file size in bytes
	
	size_t filesize = ftell(ptr);
	
	rewind(ptr);
	
	
	buffer = malloc(filesize);

	size_t bytes = fread(buffer, 1, filesize, ptr);
	
	fclose(ptr);
	
	
	

	
	

	
	
	
	long rvalues[256*256];					// to store presence of 16-bit value
	long gvalues[256*256];
	long bvalues[256*256];
	
    
	for (int i = 0; i < 256*256; i++)
	{
		rvalues[i] = 0;
        gvalues[i] = 0;
        bvalues[i] = 0;        
	}
	
    
    
    unsigned short red; 
	unsigned short green;
	unsigned short blue;
	
	
	for (int j = 0; j < LINES; j++)		
		for (int i = 0; i < LINELENGTH; i++)
		{		 
			red   = buffer[(j*LINELENGTH) + i].red;
			green = buffer[(j*LINELENGTH) + i].green;
			blue  = buffer[(j*LINELENGTH) + i].blue;
				
		    
		    rvalues[red]++;
		    gvalues[green]++;
		    bvalues[blue]++;
		    

		     
		}    
	

	
	

	
	unsigned short minMaxR = less(rvalues);
	unsigned short minMaxG = less(gvalues);
	unsigned short minMaxB = less(bvalues);
	
	unsigned short maxMaxR = most(rvalues);
	unsigned short maxMaxG = most(gvalues);
	unsigned short maxMaxB = most(bvalues);
	

	
	
	
		
	float factorR = (float)MAX / (float)(maxMaxR - minMaxR);
	float factorG = (float)MAX / (float)(maxMaxG - minMaxG);
	float factorB = (float)MAX / (float)(maxMaxB - minMaxB);
	



	for (int j = 0; j < LINES; j++)		
		for (int i = 0; i < LINELENGTH; i++)
		{		 
			red   = buffer[(j*LINELENGTH) + i].red;
			green = buffer[(j*LINELENGTH) + i].green;
			blue  = buffer[(j*LINELENGTH) + i].blue;
			
		    
	
			float r = factorR * (red - minMaxR);
			float g = factorG * (green - minMaxG); 
			float b = factorB * (blue - minMaxB);

			
			r = MAX*(((r/(float)MAX) - 0.5)*CONTRAST + 0.5 + BRIGHTNESS);
			g = MAX*(((g/(float)MAX) - 0.5)*CONTRAST + 0.5 + BRIGHTNESS);
			b = MAX*(((b/(float)MAX) - 0.5)*CONTRAST + 0.5 + BRIGHTNESS);
			
			
			r = (r > 0 ? r : 0);
 			g = (g > 0 ? g : 0);
 			b = (b > 0 ? b : 0);
 			
 			r = (r < MAX ? r : MAX);
 			g = (g < MAX ? g : MAX);
 			b = (b < MAX ? b : MAX);
 			
 			
 			
		   
			
			buffer[(j*LINELENGTH) + i].red   = r;
			buffer[(j*LINELENGTH) + i].green = g;
			buffer[(j*LINELENGTH) + i].blue  = b;
		     
		}  
	
	
	

	
	FILE *write_ptr;

	write_ptr = fopen("./contrast.bin", "wb");  // w for write, b for binary

	fwrite(buffer, 1, bytes, write_ptr); 
	
	fclose(write_ptr);
	
	
	free(buffer);
	
	
    return 0;
    
}
