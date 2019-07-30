#include <stdio.h>
#include <stdlib.h>


//#define LINELENGTH  186 	// thumb
//#define LINES		  628
//#define FILENAME	  "./testThumb.bin"
//#define FILENAMEO	  "./outThumb.bin"
//#define LINELENGTH	1494	// preview
//#define LINES		1494
//#define FILENAME	"./testScan.bin"
//#define FILENAMEO	"./outScan.bin"
#define LINELENGTH    8964	// scan
#define LINES		  8964
#define FILENAME	  "./Scan.bin"
#define FILENAMEO	  "./out.bin"


int main(int argc, char * argv[])
{
	

	
	unsigned char *buffer;
	
	buffer = NULL;
	
	size_t mem_size = 512 * 1024 * 1024;
    buffer = malloc(mem_size);
    if (buffer == NULL) 
    {
		printf("Buffer=NULL\n");
		return 1;
    } 
	

	
	
	FILE *ptr;
 
	ptr = fopen(FILENAME, "rb");  	// r for read, b for binary
	
	
	fseek(ptr, 0L, SEEK_END);				// get file size in bytes
	
	size_t filesize = ftell(ptr);
	
	rewind(ptr);
	

	size_t bytes = fread(buffer, 1, filesize, ptr);
	
	fclose(ptr);
	
	
	
	unsigned char *out = malloc(bytes);
	
	printf("Bytes: %ld\n", bytes);
	

	
	

	for (int j = 0; j < LINES; j++)			// 601 lines in Y, see SET
	{
		
		
		
		for (int i = 0; i < LINELENGTH; i++)			
			out[(j*LINELENGTH*6) + (6*i)] = buffer[(j*LINELENGTH*6) + (2*i) + 1];
			
		for (int i = 0; i < LINELENGTH; i++)			
			out[(j*LINELENGTH*6) + (6*i) + 1] = buffer[(j*LINELENGTH*6) + (2*i)];	
										
		for (int i = 0; i < LINELENGTH; i++) 	
			out[(j*LINELENGTH*6) + (6*i)+ 2] = buffer[(j*LINELENGTH*6) + (LINELENGTH*2) + (2*i) + 1];
			
		for (int i = 0; i < LINELENGTH; i++) 	
			out[(j*LINELENGTH*6) + (6*i)+ 3] = buffer[(j*LINELENGTH*6) + (LINELENGTH*2) + (2*i)];
					
		for (int i = 0; i < LINELENGTH; i++)
			out[(j*LINELENGTH*6) + (6*i) + 4] = buffer[(j*LINELENGTH*6) + (LINELENGTH*4) + (2*i) + 1];
		
		for (int i = 0; i < LINELENGTH; i++)
			out[(j*LINELENGTH*6) + (6*i) + 5] = buffer[(j*LINELENGTH*6) + (LINELENGTH*4) + (2*i)];
		 	
		
	}			




	
	FILE *write_ptr;

	write_ptr = fopen(FILENAMEO, "wb");  // w for write, b for binary

	fwrite(out, 1, bytes, write_ptr); 
	
	fclose(write_ptr);
	
	
	free(buffer);
	
	
    return 0;
    
}
