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





int set(int color, uint32_t exposure, uint32_t offset, uint32_t positive, uint32_t composition)
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
	
	parameter_buffer[WINDOW_HEADER+2] = 0x00 | 0x0F;		// 4000
	parameter_buffer[WINDOW_HEADER+3] = 0x00 | 0xA0;
	
	parameter_buffer[WINDOW_HEADER+4] = 0x00 | 0x0F;		// 
	parameter_buffer[WINDOW_HEADER+5] = 0x00 | 0xA0;
	
	
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
	
	parameter_buffer[WINDOW_HEADER+25] = 0x00 | composition;	// RBG BW 
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
	parameter_buffer[WINDOW_HEADER+41] = 0x00 | positive;	// 1=positive
		

	
	
	
	parameter_buffer[WINDOW_HEADER+42] = 0x00 | 0x01;		// 1=Scan 
	parameter_buffer[WINDOW_HEADER+43] = 0x00 | 0x02;		// 2=normal
	parameter_buffer[WINDOW_HEADER+44] = 0x00 | 0x02;		// 2=line reading
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








#define LINELENGTH  8964 	// scan
#define LINES		8964	// 




int readData(char *scale)
{
	
	int buffersize;
	
	unsigned char *out;
	
	
	
	readCmd[2] = 0x00;			// Data type code
	readCmd[3] = 0x00;
	readCmd[4] = 0x00;
	readCmd[5] = 0x00;
	readCmd[6] = 0x00;
	
	


	if (strcmp(scale, "rgb") == 0)	// 3-color
	{
		out = malloc(LINELENGTH*6);
		readCmd[7] = 0x00 | 0xD2;	// 2 * 3 * 8964 = 53784 (D218)
		readCmd[8] = 0x00 | 0x18;
		buffersize = 53784;
	}
	else                            // 1-color
	{
		out = malloc(LINELENGTH*2);
		readCmd[7] = 0x00 | 0x46;	// 2 * 8964 = 17928 (4608)
		readCmd[8] = 0x00 | 0x08;
		buffersize = 17928;
	}
		
	
	
	unsigned char parameter_buffer[buffersize];
	

	
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
		if (strcmp(scale, "rgb") == 0)
		{
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
					
		}
		else   // 1-color
		{
			for (int i = 0; i < LINELENGTH; i++)			
				out[(2*i)] = parameter_buffer[(2*i) + 1];
				
			for (int i = 0; i < LINELENGTH; i++)			
				out[(2*i) + 1] = parameter_buffer[(2*i)];
		}
			
			
		for (int i = 0; i < buffersize; i++)
			buffer[bytes + i] = out[i];
			
		bytes += buffersize;
		
		
		
		usleep(30000);		//microseconds
		
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
	
	
	//printf("autof sense: %02X %02X %02X\n", sense_buffer[2], sense_buffer[12], sense_buffer[13]);
	
	
	while (1)
	{
	
		sleep(4);
		
		
		sg_io_hdr_t local;
	
		
		memset(&local, 0, sizeof(sg_io_hdr_t));
		memset(sense_buffer, 0, sizeof(sense_buffer));
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
	
	
		if (local.status == 0)
			break;	
		
	}
   	
	
	return 0;
	
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



long total(long *array)
{
		
	long sum = 0;
	
	for (int i = 0; i < (256*256); i++)
		sum = sum + array[i];				
		
	return sum;
	
}


long below(long *array, float average)
{
	
	int absAverage = (int)(average * 0xFFFF);	
	long sum = 0;

	for (int i = 0; i < absAverage; i++)
		sum = sum + array[i];
			
	return sum;
	
}



double findGamma1()
{
	
	unsigned short gray;


	long values[256*256]; 				// to store 16-bit values
	
	
	BWPixel16 *pixelbuffer = (BWPixel16 *)buffer;
	
    
	for (int i = 0; i < 256*256; i++)	
        values[i] = 0;
	
    
	
	for (int j = 0; j < LINES; j++)		
		for (int i = 0; i < LINELENGTH; i++)
		{		 	    
		    gray = pixelbuffer[(j*LINELENGTH) + i].gray;
		    	    
		    	     
		    values[gray]++;
		     
		}
	
	
	float averageBrightness = average(values);
	
	long b = below(values, averageBrightness);
	long t = total(values);
	
	
	float epison = 1.0 - ((float)b / (float)t);
	
	
	double gamma = log(epison) / log(averageBrightness); 	//	av^gamma = epsilon
	
	
	return gamma;
	
	
}


double findGamma3()
{
	
	
	unsigned short red; 
	unsigned short green;
	unsigned short blue;

	long values[256*256]; 				// to store 16-bit values
	
	
	RGBPixel16 *pixelbuffer = (RGBPixel16 *)buffer;
	

	for (int i = 0; i < 256*256; i++)	
        values[i] = 0;
	
    
	
	for (int j = 0; j < LINES; j++)		
		for (int i = 0; i < LINELENGTH; i++)
		{		 
			red   = pixelbuffer[(j*LINELENGTH) + i].red;
			green = pixelbuffer[(j*LINELENGTH) + i].green;
			blue  = pixelbuffer[(j*LINELENGTH) + i].blue;		    
		    
		    unsigned short av = 0.2126*red + 0.7152*green + 0.0722*blue;
		   
		    
		    values[av]++;
		     
		}
	
	
	float averageBrightness = average(values);
	
	long b = below(values, averageBrightness);
	long t = total(values);
	
	
	float epison = 1.0 - ((float)b / (float)t);
	
	
	double gamma = log(epison) / log(averageBrightness); 	//	av^gamma = epsilon
	
	
	return gamma;
	
	
}





#define	MAX		0xFFFF


void doGamma1(double gamma)
{

	BWPixel16 *pixelbuffer = (BWPixel16 *)buffer;
	

	for (int j = 0; j < LINES; j++)			
		for (int i = 0; i < LINELENGTH; i++)
		{
			
			float gray = pixelbuffer[(j*LINELENGTH) + i].gray;
			
					
			gray = MAX * powf(gray / MAX, gamma);
			
			
			pixelbuffer[(j*LINELENGTH) + i].gray  = gray;			
		
		}
			
	
}



void doGamma3(double gamma)
{

	RGBPixel16 *pixelbuffer = (RGBPixel16 *)buffer;
	

	for (int j = 0; j < LINES; j++)			
		for (int i = 0; i < LINELENGTH; i++)
		{
			 	
			
			float red   = pixelbuffer[(j*LINELENGTH) + i].red;
			float green = pixelbuffer[(j*LINELENGTH) + i].green;
			float blue  = pixelbuffer[(j*LINELENGTH) + i].blue;
			
					
			red   = MAX * powf(red / MAX, gamma);
			green = MAX * powf(green / MAX, gamma);
			blue  = MAX * powf(blue / MAX, gamma);
			

			pixelbuffer[(j*LINELENGTH) + i].red   = red;
			pixelbuffer[(j*LINELENGTH) + i].green = green;
			pixelbuffer[(j*LINELENGTH) + i].blue  = blue;
			
		}
			
	
}



void invert1()
{
	
	BWPixel16 *pixelbuffer = (BWPixel16 *)buffer;
	
	unsigned short val;
	
	
	for (int j = 0; j < LINES; j++)			
		for (int i = 0; i < LINELENGTH; i++)
		{
			float gray = pixelbuffer[(j*LINELENGTH) + i].gray;
			
			val = MAX * (1.0 - (gray / MAX)); 
			
			pixelbuffer[(j*LINELENGTH) + i].gray = val;			
		}
	
}



void invert3()
{
	
	RGBPixel16 *pixelbuffer = (RGBPixel16 *)buffer;
	
	float red;  
    float green;
	float blue; 
	
	for (int j = 0; j < LINES; j++)			
		for (int i = 0; i < LINELENGTH; i++)
		{
			 				
			red   = pixelbuffer[(j*LINELENGTH) + i].red;
			green = pixelbuffer[(j*LINELENGTH) + i].green;
			blue  = pixelbuffer[(j*LINELENGTH) + i].blue;
			
	
			red   = MAX * (1.0 - (red / MAX)); 
			green = MAX * (1.0 - (green / MAX));
			blue  = MAX * (1.0 - (blue / MAX));
			
			

			pixelbuffer[(j*LINELENGTH) + i].red   = red;
			pixelbuffer[(j*LINELENGTH) + i].green = green;
			pixelbuffer[(j*LINELENGTH) + i].blue  = blue;
			
		}
	
}




#define CUTLOW		655			// 0.01 %
#define CUTHIGH		655
#define NOISE		5





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



long rvalues[256*256];					// to store presence of 16-bit value
long gvalues[256*256];
long bvalues[256*256];




void white1()
{
	
	BWPixel16 *pixelbuffer = (BWPixel16 *)buffer;
	
	
	for (int i = 0; i < 256*256; i++)
		rvalues[i] = 0;
    
    
    unsigned short gray;
	
	
	for (int j = 0; j < LINES; j++)		
		for (int i = 0; i < LINELENGTH; i++)
		{		 
			gray = pixelbuffer[(j*LINELENGTH) + i].gray;
				
		    if (gray > 0xF000)
				continue;
		    
		    rvalues[gray]++;
		    
		}    
	
	
	unsigned short min = less(rvalues);
	unsigned short max = most(rvalues);
	
	
	for (int j = 0; j < LINES; j++)		
		for (int i = 0; i < LINELENGTH; i++)
		{		 
			gray = pixelbuffer[(j*LINELENGTH) + i].gray;			
		    

			float g = (float)MAX * ((float)(gray - min)   / (float)(max - min));
			
			
			
			//g = MAX*(((g/(float)MAX) - 0.5)*CONTRAST + 0.5 + BRIGHTNESS);
			
			
			g = (g > 0 ? g : 0);
 			g = (g < MAX ? g : MAX);
 			
	   
			
			pixelbuffer[(j*LINELENGTH) + i].gray = g;
		     
		}  	

	
}


void white3()
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
	
	
	for (int j = 0; j < LINES; j++)		
		for (int i = 0; i < LINELENGTH; i++)
		{		 
			red   = pixelbuffer[(j*LINELENGTH) + i].red;
			green = pixelbuffer[(j*LINELENGTH) + i].green;
			blue  = pixelbuffer[(j*LINELENGTH) + i].blue;
				
		    
		    if ((red > 0xF000) && (green > 0xF000) && (blue > 0xF000))
				continue;
		    
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
	
	

	
	for (int j = 0; j < LINES; j++)		
		for (int i = 0; i < LINELENGTH; i++)
		{		 
			red   = pixelbuffer[(j*LINELENGTH) + i].red;
			green = pixelbuffer[(j*LINELENGTH) + i].green;
			blue  = pixelbuffer[(j*LINELENGTH) + i].blue;
			
		    
	
			float r = (float)MAX * ((float)(red - minR)   / (float)(maxR - minR));
			float g = (float)MAX * ((float)(green - minG) / (float)(maxG - minG));
			float b = (float)MAX * ((float)(blue - minB)  / (float)(maxB - minB));
			
			
			//r = MAX*(((r/(float)MAX) - 0.5)*CONTRAST + 0.5 + BRIGHTNESS);
			//g = MAX*(((g/(float)MAX) - 0.5)*CONTRAST + 0.5 + BRIGHTNESS);
			//b = MAX*(((b/(float)MAX) - 0.5)*CONTRAST + 0.5 + BRIGHTNESS);
			
			
			r = (r > 0 ? r : 0);
 			g = (g > 0 ? g : 0);
 			b = (b > 0 ? b : 0);
 			
 			r = (r < MAX ? r : MAX);
 			g = (g < MAX ? g : MAX);
 			b = (b < MAX ? b : MAX);
 			
 			
	   
			
			pixelbuffer[(j*LINELENGTH) + i].red   = r;
			pixelbuffer[(j*LINELENGTH) + i].green = g;
			pixelbuffer[(j*LINELENGTH) + i].blue  = b;
		     
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
	
	if (offset < BASEOFFSET)
		offset = BASEOFFSET;
		
	if (offset > MAXOFFSET)
		offset = MAXOFFSET;
	
	
	uint32_t wbR = (uint32_t)atoi(argv[3]);
	uint32_t wbG = (uint32_t)atoi(argv[4]);
	uint32_t wbB = (uint32_t)atoi(argv[5]);
	
	 
	//
	
	uint32_t positive = atoi(argv[6]);
    uint32_t composition = atoi(argv[7]);
    char *scale = argv[8];
    
    
    switch (positive)
    {
		case 0: break;			// negative
		case 1: break;			// positive
		default : return 1;
	}
    switch (composition)
    {
		case 2: break;			// bw - returns single channel
		case 5: break;			// color
		default : return 1;
	}
	if ((strcmp(scale, "rgb") != 0) && (strcmp(scale, "gray") != 0))
		return 1;
	
	
	// can not set composition = bw and scale = rgb
	if (strcmp(scale, "rgb") == 0)
		composition = 5; 
	
	
	
	//	 

    
	modeselect();
	
	
	
	//autofocus(0x08BC);
	
	
	
	
	
	
	set(0, wbG, offset, positive, composition);			// default same as GREEN
    set(RED, wbR, offset, positive, composition);    
    set(GREEN, wbG, offset, positive, composition);
    set(BLUE, wbB, offset, positive, composition);
    
    
    
	if (strcmp(scale, "rgb") == 0)
		scanCommand[4] = 0x03;			// RGB 3-color
	else
		scanCommand[4] = 0x01;			// Gray scale 1-color
    
	
	
    
	scan();
		
	sleep(6);
	
	
	buffer = NULL;			
		
	// 8964 * 6 * 8964 = 482119776 bytes < 470821*1024
	
	size_t mem_size = 470821 * 1024;
    buffer = malloc(mem_size);
    if (buffer == NULL) 
    {
		printf("Buffer=NULL\n");
		return 1;
    }
	
	
	
	  
	int bytes = readData(scale);
	
		
	if (bytes == 0)
		return 1;
	
	
	
	
	
	double gamma;
	
	
	if (!positive)
    {
		if (strcmp(scale, "rgb") == 0)
		{
			white3();		
			invert3();
			gamma = findGamma3();	
			doGamma3(gamma);			
		}
		else
		{
			white1();
			invert1();
			gamma = findGamma1();	
			doGamma1(gamma);
		}
	}
	else
	{
		if (strcmp(scale, "rgb") == 0)
		{
			gamma = findGamma3();	
			doGamma3(gamma);
		}
		else
		{
			gamma = findGamma1();	
			doGamma1(gamma);
		}
	}
	
	
	
	
	
	FILE *write_ptr;

	write_ptr = fopen("./scan.bin", "wb");  // w for write, b for binary
	
	fwrite(buffer, 1, bytes, write_ptr); 
	
	fclose(write_ptr);	
    
	free(buffer);
    
    
    close(sg_device);
    return 0;
    
}
