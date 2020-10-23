#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <stdint.h>
#include <string.h>


#define LINELENGTH	1494			// preview
#define LINES		1494
#define FROMFILE	"./preview.bin"
#define TOFILE		"./scan.bin"




typedef struct RGBPixels16 {
	unsigned short red;
	unsigned short green;
	unsigned short blue;
} RGBPixel16;


typedef struct BWPixels16 {
	unsigned short gray;
} BWPixel16;



unsigned char *buffer;




long rvalues[256*256];		// to store 16-bit values
long gvalues[256*256];
long bvalues[256*256];


#define	MAX		0xFFFF





float average(long *array)
{
		
	long sum = 0;
	
	long n = 0;
	

	for (int i = 0; i < (256*256); i++)
		if (array[i] > 0)
		{
			sum = sum + (array[i] * i);
			n = n + array[i];
		}
			
	
	return (sum / n) / (float)MAX;
	
}



int open(char *file, char *scale)
{
	
	FILE *ptr;
 
	ptr = fopen(file, "rb");  				// r for read, b for binary
	
	
	fseek(ptr, 0L, SEEK_END);				// get file size in bytes
	
	size_t filesize = ftell(ptr);
	
	rewind(ptr);
	
	
	buffer = malloc(filesize);
	if (buffer == NULL) 
    {
		printf("Buffer=NULL\n");
		return 1;
    }

	(void)fread(buffer, 1, filesize, ptr);
	
	
	fclose(ptr);
	
	for (int i = 0; i < 256*256; i++)
	{
		rvalues[i] = 0;
        gvalues[i] = 0;
        bvalues[i] = 0;
	}
	
	
	
	
	if (strcmp(scale, "rgb") == 0)
	{
		
		RGBPixel16 pixel;
		
		RGBPixel16 *pixelbuffer = (RGBPixel16 *)buffer;
		
		
		for (int j = 0; j < LINES; j++)		
			for (int i = 0; i < LINELENGTH; i++)
			{		 
				pixel.red   = pixelbuffer[(j*LINELENGTH) + i].red;
				pixel.green = pixelbuffer[(j*LINELENGTH) + i].green;
				pixel.blue  = pixelbuffer[(j*LINELENGTH) + i].blue;
					
				rvalues[pixel.red]++;
				gvalues[pixel.green]++;
				bvalues[pixel.blue]++;				 
			}
	}
	else
	{
		
		BWPixel16 pixel;
		
		BWPixel16 *pixelbuffer = (BWPixel16 *)buffer;
		
		
		for (int j = 0; j < LINES; j++)		
			for (int i = 0; i < LINELENGTH; i++)
			{		 
				pixel.gray = pixelbuffer[(j*LINELENGTH) + i].gray;
					
				rvalues[pixel.gray]++; 				 
			}
	}
	
	
	free(buffer);
	
	return 0;	
	
}






int main(int argc, char * argv[])
{

	
	float gamma = atof(argv[1]);
	uint32_t oldwbR = (uint32_t)atoi(argv[2]);
	uint32_t oldwbG = (uint32_t)atoi(argv[3]);
	uint32_t oldwbB = (uint32_t)atoi(argv[4]);
	
	char *scale = argv[5];

	
	
	float redOld;  
    float greenOld;
    float blueOld;
    
    
	float redNew;  
    float greenNew;
    float blueNew;
    
    
     
	
	if (open(FROMFILE, scale))
		return 1;
		
	
	if (strcmp(scale, "rgb") == 0)
	{	
		redOld   = average(rvalues);
		greenOld = average(gvalues);
		blueOld  = average(bvalues);
	}
	else
	{
		redOld   = average(rvalues);
		greenOld = redOld;
		blueOld  = redOld;
	}
	
	
	
	
	if (open(TOFILE, scale))
		return 1;
		
	
	if (strcmp(scale, "rgb") == 0)
	{	
		redNew   = average(rvalues);
		greenNew = average(gvalues);
		blueNew  = average(bvalues);
	}
	else
	{
		redNew   = average(rvalues);
		greenNew = redNew;
		blueNew  = redNew;
	}	
	
	
	 
	redNew   = powf(redNew, 1/ gamma);
	greenNew = powf(greenNew, 1 / gamma);
	blueNew  = powf(blueNew, 1 / gamma);
	




	uint32_t wbr = oldwbR*(redNew/redOld);
	uint32_t wbg = oldwbG*(greenNew/greenOld);
	uint32_t wbb = oldwbB*(blueNew/blueOld);

	

	
	printf("%u\n", wbr);
	printf("%u\n", wbg);
	printf("%u\n", wbb);
	
	
	
	
    return 0;
    
}
