#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>




#define	MAX			0xFFFF

#define CUTLOW		655			// 0.01 %
#define CUTHIGH		655
#define NOISE		3


#define CONTRAST	1.075f		// 1.075
#define BRIGHTNESS	-0.030f		// -0.030



unsigned char *buffer;



typedef struct RGBPixels16 {
	unsigned short red;
	unsigned short green;
	unsigned short blue;
} RGBPixel16;


typedef struct RGBPixels8 {
	unsigned char red;
	unsigned char green;
	unsigned char blue;
} RGBPixel8;


typedef struct BWPixels16 {
	unsigned short gray;
} BWPixel16;


typedef struct BWPixels8 {
	unsigned char gray;
} BWPixel8;




long rvalues[256*256];					// to store presence of 16-bit value
long gvalues[256*256];
long bvalues[256*256];


int linelength;
int lines;



unsigned short less(long *array)
{
	
	for (int i = CUTLOW; i < (256*256); i++)
		if (array[i] > NOISE)
		{
			
			return i;
		}
			
	return 0x0;
}



unsigned short most(long *array)
{
	for (int i = (256*256) - 1 - CUTHIGH; i >= 0; i--)
		if (array[i] > NOISE)
			return i;

	return MAX;
}






void contrast1()
{
	
	BWPixel16 *pixelbuffer = (BWPixel16 *)buffer;
	
	
	for (int i = 0; i < 256*256; i++)
		rvalues[i] = 0;
	
    
    unsigned short gray; 
	
	
	
	for (int j = 0; j < lines; j++)		
		for (int i = 0; i < linelength; i++)
		{		 
			gray  = pixelbuffer[(j*linelength) + i].gray;	
		    
		    rvalues[gray]++;
		}    
	
	
	unsigned short minMax = less(rvalues);
	unsigned short maxMax = most(rvalues);
	
		
		
	float factor = (float)MAX / (float)(maxMax - minMax);
	


	
	for (int j = 0; j < lines; j++)		
		for (int i = 0; i < linelength; i++)
		{		 
			gray = pixelbuffer[(j*linelength) + i].gray;
			
			
			float g = factor * (gray - minMax);

			
			g = MAX*(((g/(float)MAX) - 0.5)*CONTRAST + 0.5 + BRIGHTNESS);
			
			
			
			g = (g > 0 ? g : 0); 			
 			g = (g < MAX ? g : MAX);
 			
 			
			pixelbuffer[(j*linelength) + i].gray = g;
			   
		}  

	
}



void contrast3()
{
	
	RGBPixel16 *pixelbuffer = (RGBPixel16 *)buffer;
	
	
	for (int i = 0; i < 256*256; i++)
	{
		rvalues[i] = 0;
        gvalues[i] = 0;
        bvalues[i] = 0;        
	}
	
    
    
    unsigned short red; 
	unsigned short green;
	unsigned short blue;
	
	
	for (int j = 0; j < lines; j++)		
		for (int i = 0; i < linelength; i++)
		{		 
			red   = pixelbuffer[(j*linelength) + i].red;
			green = pixelbuffer[(j*linelength) + i].green;
			blue  = pixelbuffer[(j*linelength) + i].blue;
				
		    
		    rvalues[red]++;
		    gvalues[green]++;
		    bvalues[blue]++;     
		}    
	
	
	unsigned short minR = less(rvalues);
	unsigned short minG = less(gvalues);
	unsigned short minB = less(bvalues);
	
	unsigned short maxR = most(rvalues);
	unsigned short maxG = most(gvalues);
	unsigned short maxB = most(bvalues);
		
		
	float factorR = (float)MAX / (float)(maxR - minR);
	float factorG = (float)MAX / (float)(maxG - minG);
	float factorB = (float)MAX / (float)(maxB - minB);
	
	float r;
    float g;
	float b;
	
	
	for (int j = 0; j < lines; j++)		
		for (int i = 0; i < linelength; i++)
		{		 
			red   = pixelbuffer[(j*linelength) + i].red;
			green = pixelbuffer[(j*linelength) + i].green;
			blue  = pixelbuffer[(j*linelength) + i].blue;
			
		    
	
			r = factorR * (red - minR);
			g = factorG * (green - minG); 
			b = factorB * (blue - minB);

			
			r = MAX*(((r/(float)MAX) - 0.5)*CONTRAST + 0.5 + BRIGHTNESS);
			g = MAX*(((g/(float)MAX) - 0.5)*CONTRAST + 0.5 + BRIGHTNESS);
			b = MAX*(((b/(float)MAX) - 0.5)*CONTRAST + 0.5 + BRIGHTNESS);
			
			
			r = (r > 0 ? r : 0);
 			g = (g > 0 ? g : 0);
 			b = (b > 0 ? b : 0);
 			
 			r = (r < MAX ? r : MAX);
 			g = (g < MAX ? g : MAX);
 			b = (b < MAX ? b : MAX);
 			
 			
	   
			
			pixelbuffer[(j*linelength) + i].red   = r;
			pixelbuffer[(j*linelength) + i].green = g;
			pixelbuffer[(j*linelength) + i].blue  = b;
		     
		}  

	
}





size_t do8bit1(size_t bytes)
{
	
	unsigned short gray;	
	unsigned char g;
		
	bytes = bytes >> 1;
	
	BWPixel16 *pixelbuffer = (BWPixel16 *)buffer;
	BWPixel8 *newbuffer = malloc(bytes);
	
	for (int j = 0; j < lines; j++)		
		for (int i = 0; i < linelength; i++)
		{		 
			gray   = pixelbuffer[(j*linelength) + i].gray;
			
			g = gray >> 8;
			
			newbuffer[(j*linelength) + i].gray = g;
		}
			
	memcpy(buffer, newbuffer, bytes);
	
	free(newbuffer);
	
	return bytes;
		
}



size_t do8bit3(size_t bytes)
{
	
	unsigned short red; 
	unsigned short green;
	unsigned short blue;
	
	unsigned char r; 
	unsigned char g;
	unsigned char b;
		
	bytes = bytes >> 1;
	
	RGBPixel16 *pixelbuffer = (RGBPixel16 *)buffer;
	RGBPixel8 *newbuffer = malloc(bytes);
	
	for (int j = 0; j < lines; j++)		
		for (int i = 0; i < linelength; i++)
		{		 
			red   = pixelbuffer[(j*linelength) + i].red;
			green = pixelbuffer[(j*linelength) + i].green;
			blue  = pixelbuffer[(j*linelength) + i].blue;
			
			
			r = red >> 8;
			g = green >> 8;
			b = blue >> 8;
			
			newbuffer[(j*linelength) + i].red = r;
			newbuffer[(j*linelength) + i].green = g;
			newbuffer[(j*linelength) + i].blue = b;
		}
			
	memcpy(buffer, newbuffer, bytes);
	
	free(newbuffer);
	
	return bytes;
		
}






int main(int argc, char * argv[])
{
	
	
	linelength = atoi(argv[1]);
	lines = atoi(argv[2]);	
	int bitDepth = atoi(argv[3]);
	char *input = argv[4];
	char *scale = argv[5];
	
	

	
	
	FILE *ptr;
 
	ptr = fopen(input, "rb");  	// r for read, b for binary
	
	
	fseek(ptr, 0L, SEEK_END);			// get file size in bytes
	
	size_t filesize = ftell(ptr);
	
	rewind(ptr);
	
	
	buffer = malloc(filesize);

	size_t bytes = fread(buffer, 1, filesize, ptr);
	
	fclose(ptr);
	
	
	
	
	if (strcmp(scale, "rgb") == 0)
	{
		contrast3();
		if (bitDepth == 8)
			bytes = do8bit3(bytes);
	}
	else
	{
		contrast1();
		if (bitDepth == 8)
			bytes = do8bit1(bytes);
	}
	


	
	FILE *write_ptr;

	write_ptr = fopen("./scan.bin", "wb");  // w for write, b for binary

	fwrite(buffer, 1, bytes, write_ptr); 
	
	fclose(write_ptr);
	
	
	free(buffer);
	
	
    return 0;
    
}
