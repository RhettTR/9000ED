#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <stdint.h>


#define LINELENGTH	8964			// preview
#define LINES		1494
#define FROMFILE	"./scan.bin"	
#define TOFILE		"./contrast.bin"


#define LOWCUT		0x00
#define HIGHCUT		0x00





float average(long *array)
{
		
	long sum = 0;
	
	long n = 0;
	

	for (int i = 0 + LOWCUT; i < (256*256) - HIGHCUT; i++)
		if (array[i] > 0)
		{
			sum = sum + (array[i] * i);
			n = n + array[i];
		}
			
	
	return (sum / n) / (float)0xFFFF;
	
}



typedef struct Pixels {
	unsigned short red;
	unsigned short green;
	unsigned short blue;
} Pixel;


Pixel *buffer;


long rvalues[256*256];		// to store 16-bit values
long gvalues[256*256];
long bvalues[256*256];



int open(char *file)
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
	
	
	
	
	int pixels = (int)(LINELENGTH / 6);  	// 2 bytes per color, 3 colors
	
	Pixel pixel;
	
	
	for (int j = 0; j < LINES; j++)		
		for (int i = 0; i < pixels; i++)
		{		 
			pixel.red   = buffer[(j*pixels) + i].red;
			pixel.green = buffer[(j*pixels) + i].green;
			pixel.blue  = buffer[(j*pixels) + i].blue;
				
			rvalues[pixel.red]++;
		    gvalues[pixel.green]++;
		    bvalues[pixel.blue]++; 
		     
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

	
	
	
	
	if (open(FROMFILE))
		return 1;
	
	float redOld   = average(rvalues);
	float greenOld = average(gvalues);
	float blueOld  = average(bvalues); 


	
	if (open(TOFILE))
		return 1;
	
	
	float redNew   = average(rvalues);
	float greenNew = average(gvalues);
	float blueNew  = average(bvalues); 



	
	 
	redNew   = powf(redNew, 1/ gamma);
	greenNew = powf(greenNew, 1 / gamma);
	blueNew  = powf(blueNew, 1 / gamma);
	


	uint32_t wbr = oldwbR*(redNew/redOld);
	uint32_t wbg = oldwbG*(greenNew/greenOld);
	uint32_t wbb = oldwbB*(blueNew/blueOld);

	
	/*
	printf("%f\n", gamma);
	
	printf("%08X\n", oldwbR);
	printf("%08X\n", oldwbG);
	printf("%08X\n", oldwbB);
	
	printf("%f\n", redOld);
	printf("%f\n", greenOld);
	printf("%f\n", blueOld);
	
	
	printf("%f\n", redNew);
	printf("%f\n", greenNew);
	printf("%f\n", blueNew);*/
	
	printf("%u\n", wbr);
	printf("%u\n", wbg);
	printf("%u\n", wbb);
	
	
	
	
    return 0;
    
}
