#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <stdint.h>


#define LINELENGTH	8964			// preview
#define LINES		1494
#define FROMFILE	"./outScan.bin"	
#define TOFILE		"./outContrast.bin"


#define LOWCUT		0x00
#define HIGHCUT		0x00

#define GAMMA		0.382124f		// from average.c


#define	WBR			0x0004E24F
#define WBG			0x0003969F
#define	WBB			0x0002F573




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



void open(char *file)
{
	
	FILE *ptr;
 
	ptr = fopen(file, "rb");  				// r for read, b for binary
	
	
	fseek(ptr, 0L, SEEK_END);				// get file size in bytes
	
	size_t filesize = ftell(ptr);
	
	rewind(ptr);
	
	
	buffer = malloc(filesize);

	size_t bytes = fread(buffer, 1, filesize, ptr);
	
	fclose(ptr);
	
	printf("Bytes: %s %ld\n", file, bytes);
	
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
		
	
}



int main(int argc, char * argv[])
{
	

	open(FROMFILE);
	
	float redOld   = average(rvalues);
	float greenOld = average(gvalues);
	float blueOld  = average(bvalues); 


	
	open(TOFILE);

	float redNew   = average(rvalues);
	float greenNew = average(gvalues);
	float blueNew  = average(bvalues); 
	
	
	

	
	 
	redNew   = powf(redNew, 1/ GAMMA);
	greenNew = powf(greenNew, 1 / GAMMA);
	blueNew  = powf(blueNew, 1 / GAMMA);
	


	uint32_t wbr = WBR*(redNew/redOld);
	uint32_t wbg = WBG*(greenNew/greenOld);
	uint32_t wbb = WBB*(blueNew/blueOld);

	
	
	printf("wbR: %08X\n", wbr);
	printf("wbG: %08X\n", wbg);
	printf("wbB: %08X\n", wbb);
	
	
	
	
    return 0;
    
}
