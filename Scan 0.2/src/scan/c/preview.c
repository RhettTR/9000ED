#include <unistd.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <inttypes.h>
#include <string.h>
#include <errno.h>
#include <sys/ioctl.h>
#include <scsi/sg.h>
#include <math.h> 

  
 
 
#define MODESELECT_OPCODE	0x15
#define MODESELECT_CMD_LEN	6
#define PAGEFORMAT			0x10	// 00010000
#define PAGECODE			0x03	// 00 0000 11
#define MODE_LENGTH			0x14	// 20d 
 

#define WINDOW_HEADER		8

  
#define SET_OPCODE			0x24
#define SET_CMD_LEN			10
#define SET_LENGTH_MSB 		0x00   
#define SET_LENGTH_LSB		0x3A


#define RED		1
#define GREEN	2
#define BLUE	3


#define SCAN_OPCODE			0x1B	// SCAN	
#define CMD_SCAN_LEN		6
#define SCAN_LENGTH			0x03	// 0 for BW  3 for RGB


#define READ_OPCODE			0x28	// p.53 28h
#define READ_CMD_LEN		10
#define READ_TYPE_CODE		0x00		
#define READ_TYPE_QUAL_MSB  0x00   
#define READ_TYPE_QUAL_LSB	0x00
#define READ_LENGTH_MSB 	0x00   
#define READ_LENGTH_LSB		0x00	
#define READ_DATA_HEADER	6	


#define SETPARAM_OPCODE		0xE0
#define SETPARAM_CMD_LEN	10
#define SETPARAM_LENGTH_LSB	0x0D


#define EXECUTE_OPCODE		0xC1
#define EXECUTE_CMD_LEN		6				

#define DOFOCUS				0xA0







		
#define TESTUNITREADY_OPCODE	0x00
#define TESTUNITREADY_CMD_LEN	6






      
int sg_device;



unsigned char modeCmd[MODESELECT_CMD_LEN] =
	{MODESELECT_OPCODE, PAGEFORMAT, 0, 0, MODE_LENGTH, 0};
	
		
unsigned char setCmd[SET_CMD_LEN] =
	{SET_OPCODE, 0, 0, 0, 0, 0, SET_LENGTH_MSB, SET_LENGTH_MSB, SET_LENGTH_LSB, 0};
	
	
unsigned char scanCommand[CMD_SCAN_LEN] =
	{SCAN_OPCODE, 0, 0, 0, SCAN_LENGTH, 0};	
	
	
unsigned char readCmd[READ_CMD_LEN] =
	{READ_OPCODE, 0, READ_TYPE_CODE, 0, READ_TYPE_QUAL_MSB, READ_TYPE_QUAL_LSB, 
	 READ_LENGTH_MSB, READ_LENGTH_MSB, READ_LENGTH_LSB, 0};
	 	 
	 
unsigned char setCmdBlk[SETPARAM_CMD_LEN] =
	{SETPARAM_OPCODE, 0, DOFOCUS, 0, 0, 0, 0, 0, SETPARAM_LENGTH_LSB, 0};
    
                    
unsigned char exeCmdBlk[EXECUTE_CMD_LEN] =
    {EXECUTE_OPCODE, 0, 0, 0, 0, 0};
	 			
	 			
unsigned char testCmdBlk[TESTUNITREADY_CMD_LEN] =
	{TESTUNITREADY_OPCODE, 0, 0, 0, 0, 0};
	
	
				
unsigned char sense_buffer[32];		// p.6 'Status of this unit' 8 quadlets




sg_io_hdr_t io_hdr;



unsigned char *buffer;






int testunitready()
{
	
	sg_io_hdr_t local;
	
	
    memset(&local, 0, sizeof(sg_io_hdr_t));
    local.interface_id = 'S';
    local.cmd_len = sizeof(testCmdBlk);
    local.mx_sb_len = sizeof(sense_buffer);
    local.dxfer_direction = SG_DXFER_NONE;
    local.dxfer_len = 0;
    local.dxferp = NULL;
    local.cmdp = testCmdBlk;
    local.sbp = sense_buffer;
    local.timeout = 20000;
	
	
		
	if (ioctl(sg_device, SG_IO, &local) < 0) 
	{
		perror("TEST UNIT READY ioctl error");
		return 1;
	}

        
	return local.status;
	
}




int modeselect()
{
	
	
	
	unsigned char parameter_buffer[MODE_LENGTH];
	
	
	
	parameter_buffer[0] = 0x13; 
	parameter_buffer[1] = 0x00; 	
	parameter_buffer[2] = 0x00;
	parameter_buffer[3] = 0x08;		
	
	
	parameter_buffer[4] = 0x00; 
	parameter_buffer[5] = 0x00; 	
	parameter_buffer[6] = 0x00;
	parameter_buffer[7] = 0x00;
	parameter_buffer[8] = 0x00;
	parameter_buffer[9] = 0x00;
	parameter_buffer[10] = 0x00;
	parameter_buffer[11] = 0x01;
	
	parameter_buffer[12] = 0x03; 
	parameter_buffer[13] = 0x06; 	
	parameter_buffer[14] = 0x00;
	parameter_buffer[15] = 0x00;
	parameter_buffer[16] = 0x0F;		// max resolution
	parameter_buffer[17] = 0xA0;		// max resolution
	parameter_buffer[18] = 0x00;
	parameter_buffer[19] = 0x00;
	
	
	
	
	memset(&io_hdr, 0, sizeof(sg_io_hdr_t));
    io_hdr.interface_id = 'S';
    io_hdr.mx_sb_len = sizeof(sense_buffer);
    io_hdr.sbp = sense_buffer;
    io_hdr.dxfer_len = sizeof(parameter_buffer);
	io_hdr.dxferp = parameter_buffer;     
	io_hdr.cmd_len = sizeof(modeCmd);
	io_hdr.cmdp = modeCmd;
	io_hdr.dxfer_direction = SG_DXFER_TO_DEV;				//  /usr/include/scsi/sg.h
	io_hdr.timeout = 20000;	
	
	
	
	for (int i = 0; i < 10; i++)
	{
		
		if (ioctl(sg_device, SG_IO, &io_hdr) < 0) 
		{
			perror("MODESELECT ioctl error");
			return 1;
		}
		
		if (io_hdr.status == 0)
			break;
		
		usleep(20000);
		
	}	

	
	return 0; 
	
}





int set(int color, uint32_t exposure, uint32_t offset)
{
	
	
	
	unsigned char parameter_buffer[58];
	
	
	parameter_buffer[0] = 0x00; 
	parameter_buffer[1] = 0x00; 	
	parameter_buffer[2] = 0x00;
	parameter_buffer[3] = 0x00;
	parameter_buffer[4] = 0x00;
	parameter_buffer[5] = 0x00;
	parameter_buffer[6] = 0x00;
	parameter_buffer[7] = 0x32;		// 50   <<<<< this is 50, not 58 >>>>>



	
	parameter_buffer[WINDOW_HEADER+0] = 0x00 | color;
	
	parameter_buffer[WINDOW_HEADER+1] = 0x00;
	
	parameter_buffer[WINDOW_HEADER+2] = 0x00 | 0x02;		// 666=029A
	parameter_buffer[WINDOW_HEADER+3] = 0x00 | 0x9A;
	
	parameter_buffer[WINDOW_HEADER+4] = 0x00 | 0x02;		// 
	parameter_buffer[WINDOW_HEADER+5] = 0x00 | 0x9A;
	
	
	parameter_buffer[WINDOW_HEADER+6] = 0x00;		// Upper Left X Offset
	parameter_buffer[WINDOW_HEADER+7] = 0x00;
	parameter_buffer[WINDOW_HEADER+8] = 0x00 | 0x02;		
	parameter_buffer[WINDOW_HEADER+9] = 0x00 | 0x06;
	
	parameter_buffer[WINDOW_HEADER+10] = 0x00;		// Upper Left Y Offset 
	parameter_buffer[WINDOW_HEADER+11] = 0x00;
	parameter_buffer[WINDOW_HEADER+12] = (offset >> 8) & 0xFF;		
	parameter_buffer[WINDOW_HEADER+13] = offset & 0xFF;
	
	parameter_buffer[WINDOW_HEADER+14] = 0x00;		// Window Width (X)
	parameter_buffer[WINDOW_HEADER+15] = 0x00;
	parameter_buffer[WINDOW_HEADER+16] = 0x00 | 0x23;	// 8964 px	 
	parameter_buffer[WINDOW_HEADER+17] = 0x00 | 0x04;	// 4000 / 666 = 6
														// 8964 /6 = 1494*3*2 = 8964 bytes pr. line
														
	parameter_buffer[WINDOW_HEADER+18] = 0x00;		// Window Length (Y)
	parameter_buffer[WINDOW_HEADER+19] = 0x00;	
	parameter_buffer[WINDOW_HEADER+20] = 0x00 | 0x23;	// 8964 / 6 = 1494 lines																
	parameter_buffer[WINDOW_HEADER+21] = 0x00 | 0x04;	// 1494*8964 = 13 392 216 bytes total
														
														
	parameter_buffer[WINDOW_HEADER+22] = 0x00;
	parameter_buffer[WINDOW_HEADER+23] = 0x00;
	parameter_buffer[WINDOW_HEADER+24] = 0x00;
	
	parameter_buffer[WINDOW_HEADER+25] = 0x00 | 0x05;	// RBG (02 = BW) 
	parameter_buffer[WINDOW_HEADER+26] = 0x00 | 0x10; 	// 16 bits (not 8 bits)
	
	parameter_buffer[WINDOW_HEADER+27] = 0x00;		// 
	parameter_buffer[WINDOW_HEADER+28] = 0x00;
	
	parameter_buffer[WINDOW_HEADER+29] = 0x00; 
	
	parameter_buffer[WINDOW_HEADER+30] = 0x00;
	parameter_buffer[WINDOW_HEADER+31] = 0x00;
	parameter_buffer[WINDOW_HEADER+32] = 0x00;
	parameter_buffer[WINDOW_HEADER+33] = 0x00;
	parameter_buffer[WINDOW_HEADER+34] = 0x00;
	parameter_buffer[WINDOW_HEADER+35] = 0x00;
	parameter_buffer[WINDOW_HEADER+36] = 0x00;
	parameter_buffer[WINDOW_HEADER+37] = 0x00;
	parameter_buffer[WINDOW_HEADER+38] = 0x00;
	parameter_buffer[WINDOW_HEADER+39] = 0x00;
	
	parameter_buffer[WINDOW_HEADER+40] = 0x00;			// default color order
	parameter_buffer[WINDOW_HEADER+41] = 0x00 | 0x01;	// 1=positive
		

	
	
	
	parameter_buffer[WINDOW_HEADER+42] = 0x00 | 0x01;		// 1=Scan 
	parameter_buffer[WINDOW_HEADER+43] = 0x00 | 0x02;		// 4=high speed
	parameter_buffer[WINDOW_HEADER+44] = 0x00 | 0x02;
	parameter_buffer[WINDOW_HEADER+45] = 0x00 | 0x00;		// AE ajustment (set to middle)
	
	parameter_buffer[WINDOW_HEADER+46] = (exposure >> 24) & 0xFF;	
	parameter_buffer[WINDOW_HEADER+47] = (exposure >> 16) & 0xFF;				
	parameter_buffer[WINDOW_HEADER+48] = (exposure >> 8) & 0xFF;				  
	parameter_buffer[WINDOW_HEADER+49] = exposure & 0xFF;
				
	

	
	memset(&io_hdr, 0, sizeof(sg_io_hdr_t));
    io_hdr.interface_id = 'S';
    io_hdr.mx_sb_len = sizeof(sense_buffer);
    io_hdr.sbp = sense_buffer;
    io_hdr.dxfer_len = sizeof(parameter_buffer);
	io_hdr.dxferp = parameter_buffer;     
	io_hdr.cmd_len = sizeof(setCmd);
	io_hdr.cmdp = setCmd;
	io_hdr.dxfer_direction = SG_DXFER_TO_DEV;				//  /usr/include/scsi/sg.h
	io_hdr.timeout = 20000;	

	
	
	for (int i = 0; i < 50; i++)
	{       
		if (ioctl(sg_device, SG_IO, &io_hdr) < 0) 
		{
			perror("SET ioctl error");
			return 1;
		}
		
		if (io_hdr.status == 0)
			break;
		
		usleep(20000);
	}
		
	
	return 0; 
	
}



int scan()
{
	
	
	unsigned char parameter_buffer[3];
	
	parameter_buffer[0] = 0x01;
	parameter_buffer[1] = 0x02;	 	
	parameter_buffer[2] = 0x03;
	
	
	
	memset(&io_hdr, 0, sizeof(sg_io_hdr_t));
    io_hdr.interface_id = 'S';
    io_hdr.mx_sb_len = sizeof(sense_buffer);
    io_hdr.sbp = sense_buffer;
    io_hdr.dxfer_len = sizeof(parameter_buffer);
	io_hdr.dxferp = parameter_buffer;     
	io_hdr.cmd_len = sizeof(scanCommand);
	io_hdr.cmdp = scanCommand;
	io_hdr.dxfer_direction = SG_DXFER_TO_DEV;				//  /usr/include/scsi/sg.h
	io_hdr.timeout = 20000;	
	
	


	for (int i = 0; i < 4; i++)
	{       
		if (ioctl(sg_device, SG_IO, &io_hdr) < 0) 
		{
			perror("SCAN ioctl error");
			return 1;
		}
		
		if (io_hdr.status == 0)
			break;
		
		usleep(20000);
	}
	
		
	printf("sense: %02X %02X %02X\n", sense_buffer[2], sense_buffer[12], sense_buffer[13]);
	
	
	return 0;
	
}



uint32_t wbValue(int color)
{
	

	readCmd[2] = 0x00 | 0x8C;			// Data type code
	readCmd[4] = 0x00 | color;			// DATA_TYPE_QUAL_MSB   
	readCmd[5] = 0x00 | 0x03;			// DATA_TYPE_QUAL_LSB   
	readCmd[8] = 0x00 | 0x0A;			// READ_DATA_HEADER + 4
		
	
	
	unsigned char parameter_buffer[10];
	
	
	
	
	memset(&io_hdr, 0, sizeof(sg_io_hdr_t));
    io_hdr.interface_id = 'S';
    io_hdr.mx_sb_len = sizeof(sense_buffer);
    io_hdr.sbp = sense_buffer;
    io_hdr.dxfer_len = sizeof(parameter_buffer);
	io_hdr.dxferp = parameter_buffer;     
	io_hdr.cmd_len = sizeof(readCmd);
	io_hdr.cmdp = readCmd;
	io_hdr.dxfer_direction = SG_DXFER_FROM_DEV;				//  /usr/include/scsi/sg.h
	io_hdr.timeout = 20000;
    
	
	
	if (ioctl(sg_device, SG_IO, &io_hdr) < 0) 
    {
        perror("READ ioctl error");
        return 1;
    }
         
	
	uint32_t result = ((parameter_buffer[6] << 24) | 
					   (parameter_buffer[7] << 16) |
					   (parameter_buffer[8] << 8) |
					    parameter_buffer[9]);
	
	return result; 
	
}





#define READBUFFER_MSB	0x23		// bytes per line
#define READBUFFER_LSB	0x04
#define READBUFFER 		8964		// 



#define LINELENGTH  1494 	// preview
#define LINES		1494




int readData()
{
	
	readCmd[2] = 0x00;			// Data type code
	readCmd[3] = 0x00;
	readCmd[4] = 0x00;
	readCmd[5] = 0x00;
	readCmd[6] = 0x00;
	readCmd[7] = 0x00 | READBUFFER_MSB;
	readCmd[8] = 0x00 | READBUFFER_LSB;
	

		
	
	
	unsigned char parameter_buffer[READBUFFER];
	

	
	memset(&io_hdr, 0, sizeof(sg_io_hdr_t));
    io_hdr.interface_id = 'S';
    io_hdr.mx_sb_len = sizeof(sense_buffer);
    io_hdr.sbp = sense_buffer;
    io_hdr.dxfer_len = sizeof(parameter_buffer);
	io_hdr.dxferp = parameter_buffer;     
	io_hdr.cmd_len = sizeof(readCmd);
	io_hdr.cmdp = readCmd;
	io_hdr.dxfer_direction = SG_DXFER_FROM_DEV;				//  /usr/include/scsi/sg.h
	io_hdr.timeout = 20000;
	
	
	
	int bytes = 0;

    unsigned char *out = malloc(LINELENGTH*6);
	
		
	
	while (1)
	{
		
		if (ioctl(sg_device, SG_IO, &io_hdr) < 0) 
		{
			perror("READ ioctl error");
			return 0;
		}    
		
		if (io_hdr.status != 0)
			break;
			
			
			
		// convert image data format
		for (int i = 0; i < LINELENGTH; i++)			
			out[(6*i)] = parameter_buffer[(2*i) + 1];
			
		for (int i = 0; i < LINELENGTH; i++)			
			out[(6*i) + 1] = parameter_buffer[(2*i)];	
										
		for (int i = 0; i < LINELENGTH; i++) 	
			out[(6*i)+ 2] = parameter_buffer[(LINELENGTH*2) + (2*i) + 1];
			
		for (int i = 0; i < LINELENGTH; i++) 	
			out[(6*i)+ 3] = parameter_buffer[(LINELENGTH*2) + (2*i)];
					
		for (int i = 0; i < LINELENGTH; i++)
			out[(6*i) + 4] = parameter_buffer[(LINELENGTH*4) + (2*i) + 1];
		
		for (int i = 0; i < LINELENGTH; i++)
			out[(6*i) + 5] = parameter_buffer[(LINELENGTH*4) + (2*i)];
			
				
			
			
		for (int i = 0; i < READBUFFER; i++)
			buffer[bytes + i] = out[i];
			
		bytes += READBUFFER;
		
		
		
		usleep(60000);		//microseconds
		
	}
		
	
	return bytes;	
	
}



int autofocus(uint32_t offset)
{
	
	
	unsigned char parameter_buffer[SETPARAM_LENGTH_LSB];
	
	
    parameter_buffer[1] = 0x00;
    parameter_buffer[2] = 0x00;
    parameter_buffer[3] = 0x00 | 0x02;
    parameter_buffer[4] = 0x00 | 0x06;
    parameter_buffer[5] = 0x00;
    parameter_buffer[6] = 0x00;
    parameter_buffer[7] = (offset >> 8) & 0xFF;
    parameter_buffer[8] = offset & 0xFF;
    
    
    memset(&io_hdr, 0, sizeof(sg_io_hdr_t));
    io_hdr.interface_id = 'S';
    io_hdr.cmd_len = sizeof(setCmdBlk);
    io_hdr.mx_sb_len = sizeof(sense_buffer);
    io_hdr.dxfer_direction = SG_DXFER_TO_DEV;
    io_hdr.dxfer_len = sizeof(parameter_buffer);
    io_hdr.dxferp = parameter_buffer;
    io_hdr.cmdp = setCmdBlk;
    io_hdr.sbp = sense_buffer;
    io_hdr.timeout = 20000; 
    


		   

        
	if (ioctl(sg_device, SG_IO, &io_hdr) < 0) 
	{
		perror("SET PARAM ioctl error");
		return 1;
	}
  

	testunitready();
	

	memset(&io_hdr, 0, sizeof(sg_io_hdr_t));
    io_hdr.interface_id = 'S';
    io_hdr.cmd_len = sizeof(exeCmdBlk);
    io_hdr.mx_sb_len = sizeof(sense_buffer);
    io_hdr.dxfer_direction = SG_DXFER_NONE;
    io_hdr.dxfer_len = 0;
    io_hdr.dxferp = NULL;
    io_hdr.cmdp = exeCmdBlk;
    io_hdr.sbp = sense_buffer;
    io_hdr.timeout = 20000; 
    
	
	
   
	if (ioctl(sg_device, SG_IO, &io_hdr) < 0) 
	{
		perror("EXECUTE ioctl error");
		return 1;
	}
   		
	
	return 0;
	
}





typedef struct Pixels {
	unsigned short red;
	unsigned short green;
	unsigned short blue;
} Pixel;
	


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
	
	
	
double findGamma()
{
	
		
	Pixel *pixelbuffer = (Pixel *)buffer;
	
		
	int pixels = (int)(LINELENGTH);  	// 2 bytes per color 16-bit, 3 colors
	
	unsigned short red; 
	unsigned short green;
	unsigned short blue;


	long values[256*256]; 				// to store presence of 16-bit value
	
	
    
	for (int i = 0; i < 256*256; i++)	
        values[i] = 0;
	
    
	
	for (int j = 0; j < LINES; j++)		
		for (int i = 0; i < pixels; i++)
		{		 
			red   = pixelbuffer[(j*pixels) + i].red;
			green = pixelbuffer[(j*pixels) + i].green;
			blue  = pixelbuffer[(j*pixels) + i].blue;		    
		    
		    unsigned short av = 0.2126*red + 0.7152*green + 0.0722*blue;
		   
		    
		    values[av]++;
		     
		}
	
	
	float averageBrightness = average(values);
	
	
	return log(0.5) / log(averageBrightness); 	//	av^gamma = 0.5
	
	
}



#define	MAX		0xFFFF



void doGamma(double gamma)
{
	
	Pixel *pixelbuffer = (Pixel *)buffer;
	
	
	int pixels = (int)(LINELENGTH);  	// 2 bytes per color 16-bit, 3 colors



	for (int j = 0; j < LINES; j++)			
		for (int i = 0; i < pixels; i++)
		{
			 	
			
			float red   = pixelbuffer[(j*pixels) + i].red;
			float green = pixelbuffer[(j*pixels) + i].green;
			float blue  = pixelbuffer[(j*pixels) + i].blue;
			
					
			red   = MAX * powf(red / MAX, gamma);
			green = MAX * powf(green / MAX, gamma);
			blue  = MAX * powf(blue / MAX, gamma);
			

			pixelbuffer[(j*pixels) + i].red   = red;
			pixelbuffer[(j*pixels) + i].green = green;
			pixelbuffer[(j*pixels) + i].blue  = blue;			
		
		}
			
	
}






#define BASEOFFSET		0x08BC	// the base Y offset for the FH-869S Brownie Strip Film holder
#define HEIGHT			0x2304	// the height (length) of a frame
#define MAXOFFSET		(BASEOFFSET + (660*48) - HEIGHT)



int main(int argc, char * argv[])
{
			
		
    if ((sg_device = open(argv[1], O_RDWR)) < 0) 
    {
        perror("Error opening device");
        return 1;
    }
	        
	    
	// scale up address
	// adr * 48 + 08BC
	// between 08BC and 847C (660*48+08BC) (last start 847C - 2304 = 6178)
	
	uint32_t offset = atoi(argv[2]);
	
	offset = offset * 48;
	
	offset = offset + BASEOFFSET;
	
	if (offset < BASEOFFSET || offset > MAXOFFSET)
		return 1;
	
	
	
	char *outfile = argv[3]; 
	
	 

    
	modeselect();
	
	
	
	autofocus(offset);
	
	sleep(30); 	// for as long as it takes ....
	
	
	
	
	
	uint32_t wbR = wbValue(RED);
	uint32_t wbG = wbValue(GREEN);
	uint32_t wbB = wbValue(BLUE);
	
	
	
	set(0, wbG, offset);			// default same as GREEN
    set(RED, wbR, offset);    
    set(GREEN, wbG, offset);
    set(BLUE, wbB, offset);
    

	
    
	scan();
		
	sleep(5);
	
	
	buffer = NULL;			
		
	// 1494 * 6 * 1494 = 13392216 bytes < 13079*1024
	
	size_t mem_size = 13079 * 1024;
    buffer = malloc(mem_size);
    if (buffer == NULL) 
    {
		printf("Buffer=NULL\n");
		return 1;
    }
	
	
	
	  
	int bytes = readData();
	
		
	if (bytes == 0)
		return 1;
	
	
	double gamma = findGamma();
    
 
    doGamma(gamma);
	
	
	
	FILE *write_ptr;

	write_ptr = fopen(outfile, "wb");  // w for write, b for binary
	
	fwrite(buffer, 1, bytes, write_ptr); 
	
	fclose(write_ptr);	
    
	free(buffer);
    
    
    close(sg_device);
    return 0;
    
}
