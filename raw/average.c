#include <stdio.h>
#include <stdlib.h>
#include <math.h>


//#define LINELENGTH	1116 			// thumb
//#define LINES		628
//#define FILENAME	"./outThumb.bin"
//#define LINELENGTH	8964			// preview
//#define LINES		1494
//#define FILENAME	"./outScan.bin"
//#define FILENAME	"./gamma.bin"
#define LINELENGTH	(8964*6)			// scan
#define LINES		8964
#define FILENAME	"./out.bin"



#define MORE(a,b)	((a)>(b)?(a):(b)) 
#define MAX(r,g,b)	((MORE(r,g))==(MORE(r,b))?(r):(MORE(g,b)))







float median(long *array)
{
		
	long sum = 0;
	

	for (int i = 0; i < 256*256; i++)
		sum = sum + array[i];
		
			
	long half = sum / 2;
	
	
	
	sum = 0;
	
	for (int i = 0; i < 256*256; i++)
	{
		sum = sum + array[i];
		
		if (sum > half)
			return ((float)i / (float)0xFFFF);
	}
	
	return 0.0;	
	
}






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
			
	
	return (sum / n) / (float)0xFFFF;
	
}



double stddev(long *array, unsigned short mean)
{
	
	double sum = 0;
	
	long n = 0;
	
	
	for (int i = 0; i < 256*256; i++)
		if (array[i] > 0)
		{
			sum = sum + (powf(i - mean, 2) * array[i]);
			n = n + array[i];
		}  
		
	double variance = sum / (n - 1);
	
		
	return sqrtl(variance) / (double)0xFFFF;
	
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
 
	ptr = fopen(FILENAME, "rb");  	// r for read, b for binary
	
	
	fseek(ptr, 0L, SEEK_END);				// get file size in bytes
	
	size_t filesize = ftell(ptr);
	
	rewind(ptr);
	
	
	buffer = malloc(filesize);

	size_t bytes = fread(buffer, 1, filesize, ptr);
	
	fclose(ptr);
	
	
	
	printf("Bytes: %ld\n", bytes);
	

	

	int pixels = (int)(LINELENGTH / 6);  	// 2 bytes per color 16-bit, 3 colors
	
	unsigned short red; 
	unsigned short green;
	unsigned short blue;


    
    
	long rvalues[256*256];					// to store presence of 16-bit value
	long gvalues[256*256];
	long bvalues[256*256];
	long values[256*256]; 
    
	for (int i = 0; i < 256*256; i++)
	{
		rvalues[i] = 0;
        gvalues[i] = 0;
        bvalues[i] = 0;
        values[i] = 0;
	}
    
	
	for (int j = 0; j < LINES; j++)		
		for (int i = 0; i < pixels; i++)
		{		 
			red   = buffer[(j*pixels) + i].red;
			green = buffer[(j*pixels) + i].green;
			blue  = buffer[(j*pixels) + i].blue;
				
			rvalues[red]++;
		    gvalues[green]++;
		    bvalues[blue]++;
		    
		    
		    //unsigned short av = MAX(red,green,blue);
		    //unsigned short av = (0.30*red) + (0.59*green) + (0.11*blue); 
		    unsigned short av = 0.2126*red + 0.7152*green + 0.0722*blue;
		    //unsigned short av = (red + green + blue) / 3;
		    
		    values[av]++;
		     
		}    
		
		

	printf("Red  : %f\n", average(rvalues));
	printf("Green: %f\n", average(gvalues));
	printf("Blue : %f\n", average(bvalues));
	printf("Val  : %f\n\n", average(values));
	
	printf("MedR : %f\n", median(rvalues));
	printf("MedG : %f\n", median(gvalues));
	printf("MedB : %f\n", median(bvalues));
	printf("MedV : %f\n\n", median(values));
		
	
	float averageBrightness = average(values);
	
	
	double deviation = stddev(values, (unsigned short)(averageBrightness*0xFFFF));
	
	

	double gamma = log(0.5) / log(averageBrightness); 	//	av^gamma = 0.5
	
	
	printf("Standard deviation: %lf\n\n", deviation);
	printf("Gamma: %lf\n", gamma); 
	
	
	free(buffer);
	
	
	
	
	
	
    return 0;
    
}
