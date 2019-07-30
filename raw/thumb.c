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



#define BYTE_TO_BINARY_PATTERN "%c%c%c%c%c%c%c%c"
#define BYTE_TO_BINARY(byte)  \
  (byte & 0x80 ? '1' : '0'), \
  (byte & 0x40 ? '1' : '0'), \
  (byte & 0x20 ? '1' : '0'), \
  (byte & 0x10 ? '1' : '0'), \
  (byte & 0x08 ? '1' : '0'), \
  (byte & 0x04 ? '1' : '0'), \
  (byte & 0x02 ? '1' : '0'), \
  (byte & 0x01 ? '1' : '0')
  
 
 
#define MODESELECT_OPCODE	0x15
#define MODESELECT_CMD_LEN	6
#define PAGEFORMAT			0x10	// 00010000
#define PAGECODE			0x03	// 00 0000 11
#define MODE_LENGTH			0x14	// 20d 
 
 
  
#define GET_OPCODE			0x25	// p.46 25h
#define GET_CMD_LEN			10
#define WINDOW_HEADER		8
#define SINGLE				0x01	
#define GET_LENGTH_MSB 		0x00   
#define GET_LENGTH_LSB		0x3A	// 58
  
  

#define SET_OPCODE			0x24
#define SET_CMD_LEN			10
#define SET_LENGTH_MSB 		0x00   
#define SET_LENGTH_LSB		0x3A


#define RED		1
#define GREEN	2
#define BLUE	3


#define SCAN_OPCODE			0x1B	// SCAN	
#define CMD_SCAN_LEN		6
#define SCAN_LENGTH			0x03	// 0 for BW  3 for RGB ?


#define READ_OPCODE			0x28	// p.53 28h
#define READ_CMD_LEN		10
#define DATA_TYPE_CODE		0x00		
#define DATA_TYPE_QUAL_MSB  0x00   
#define DATA_TYPE_QUAL_LSB	0x00
#define READ_LENGTH_MSB 	0x00   
#define READ_LENGTH_LSB		0x00	
#define READ_DATA_HEADER	6	



      
int sg_device;



unsigned char modeCmd[MODESELECT_CMD_LEN] =
	{MODESELECT_OPCODE, PAGEFORMAT, 0, 0, MODE_LENGTH, 0};
	
	
unsigned char getCmd[GET_CMD_LEN] =
	{GET_OPCODE, SINGLE, 0, 0, 0, RED, GET_LENGTH_MSB, GET_LENGTH_MSB, GET_LENGTH_LSB, 0};
	
		
unsigned char setCmd[SET_CMD_LEN] =
	{SET_OPCODE, 0, 0, 0, 0, 0, SET_LENGTH_MSB, SET_LENGTH_MSB, SET_LENGTH_LSB, 0};
	
	
unsigned char scanCommand[CMD_SCAN_LEN] =
	{SCAN_OPCODE, 0, 0, 0, SCAN_LENGTH, 0};	
	
	
unsigned char readCmd[READ_CMD_LEN] =
	{READ_OPCODE, 0, DATA_TYPE_CODE, 0, DATA_TYPE_QUAL_MSB, DATA_TYPE_QUAL_LSB, 
	 READ_LENGTH_MSB, READ_LENGTH_MSB, READ_LENGTH_LSB, 0};
	 			
	
	
				
unsigned char sense_buffer[32];		// p.6 'Status of this unit' 8 quadlets




sg_io_hdr_t io_hdr;




void printSense()
{
	
	printf("0: "BYTE_TO_BINARY_PATTERN" %02X %02X%02X\n", BYTE_TO_BINARY(sense_buffer[0]), sense_buffer[1], sense_buffer[2], sense_buffer[3]);
	printf("1: %02X%02X%02X%02X\n", sense_buffer[4], sense_buffer[5], sense_buffer[6], sense_buffer[7]);
	printf("2: "BYTE_TO_BINARY_PATTERN" "BYTE_TO_BINARY_PATTERN" %02X %02X\n", BYTE_TO_BINARY(sense_buffer[8]), 
				BYTE_TO_BINARY(sense_buffer[9]), sense_buffer[10], sense_buffer[11]);
	printf("3: %02X%02X%02X%02X\n", sense_buffer[12], sense_buffer[13], sense_buffer[14], sense_buffer[15]);
	printf("4: %02X%02X%02X%02X\n", sense_buffer[16], sense_buffer[17], sense_buffer[18], sense_buffer[19]);
	printf("5: %02X%02X%02X%02X\n", sense_buffer[20], sense_buffer[21], sense_buffer[22], sense_buffer[23]);
	printf("6: %02X%02X%02X%02X\n", sense_buffer[24], sense_buffer[25], sense_buffer[26], sense_buffer[27]);
	printf("7: %02X%02X%02X%02X\n\n", sense_buffer[28], sense_buffer[29], sense_buffer[30], sense_buffer[31]);
	
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
		
	
	printf("ModeSelect: %02X\n\n", io_hdr.status);
		

	

	
	return 0; 
	
}




int set(int color, uint32_t exposure)
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
	
	parameter_buffer[WINDOW_HEADER+2] = 0x00 | 0x00;		// 83=0053  666=029A
	parameter_buffer[WINDOW_HEADER+3] = 0x00 | 0x53;
	
	parameter_buffer[WINDOW_HEADER+4] = 0x00 | 0x00;		// 
	parameter_buffer[WINDOW_HEADER+5] = 0x00 | 0x53;
	
	
	parameter_buffer[WINDOW_HEADER+6] = 0x00;		// Upper Left X Offset
	parameter_buffer[WINDOW_HEADER+7] = 0x00;
	parameter_buffer[WINDOW_HEADER+8] = 0x00 | 0x02;		
	parameter_buffer[WINDOW_HEADER+9] = 0x00 | 0x06;
	
	parameter_buffer[WINDOW_HEADER+10] = 0x00;		// Upper Left Y Offset
	parameter_buffer[WINDOW_HEADER+11] = 0x00;
	parameter_buffer[WINDOW_HEADER+12] = 0x00 | 0x08;		
	parameter_buffer[WINDOW_HEADER+13] = 0x00 | 0xBC;
	
	parameter_buffer[WINDOW_HEADER+14] = 0x00;		// Window Width (X)
	parameter_buffer[WINDOW_HEADER+15] = 0x00;
	parameter_buffer[WINDOW_HEADER+16] = 0x00 | 0x23;	// 8964 px	 
	parameter_buffer[WINDOW_HEADER+17] = 0x00 | 0x04;	// 4000 / 83 = 48;
														// 8964 / 48 = 186*3*2 = 1116 bytes pr. line
														
	parameter_buffer[WINDOW_HEADER+18] = 0x00;		// Window Length (Y)
	parameter_buffer[WINDOW_HEADER+19] = 0x00;	
	parameter_buffer[WINDOW_HEADER+20] = 0x00 | 0x75;	// 8754h - (2x8BCh) = 30172																	
	parameter_buffer[WINDOW_HEADER+21] = 0x00 | 0xC0;	// 30172 - 12 = 30160 - 16 = 30144 (75C0) / 48 = 628 lines
														
														
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
	
	parameter_buffer[WINDOW_HEADER+40] = 0x00;			// color 0=gray
	parameter_buffer[WINDOW_HEADER+41] = 0x00 | 0x01;	// 1=positive
		

	
	
	
	parameter_buffer[WINDOW_HEADER+42] = 0x00 | 0x02;		// 2=Thumbnail 
	parameter_buffer[WINDOW_HEADER+43] = 0x00 | 0x02;		// 4=high speed
	parameter_buffer[WINDOW_HEADER+44] = 0x00 | 0x02;
	parameter_buffer[WINDOW_HEADER+45] = 0x00;				// 
	
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
	
	printf("\nCOLOR:%d Status:%02X\n", color, io_hdr.status);

		
	
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
	
	
     
    if (ioctl(sg_device, SG_IO, &io_hdr) < 0) 
    {
        perror("SCAN ioctl error");
        return 1;
    }

	
	
	printf("scan: %02X\n", io_hdr.status);
	printSense(); 
	
	
	return 0;
	
}




int maxValue(int color)
{
	
	readCmd[2] = 0x00 | 0x81;	// Data type code		
	readCmd[4] = 0x00 | color;	// DATA_TYPE_QUAL_MSB
	readCmd[5] = 0x00 | 0x01;	// DATA_TYPE_QUAL_LSB   2-byte-data		
	readCmd[8] = 0x00 | 0x08;	// READ_DATA_HEADER + 2
	
	
	
	
	unsigned char parameter_buffer[8];
	
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
		
	
	printf("Max color %d: %02X%02X\n", color, parameter_buffer[6] & 0x3F, parameter_buffer[7]);
		
	
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
	
	printf("WB%d: %08X\n", color, result);
	
	
	return result; 
	
}



float analogGain()
{
	

	readCmd[2] = 0x00 | 0x8A;			// Data type code
	readCmd[4] = 0x00 | 0x00;			// DATA_TYPE_QUAL_MSB   
	readCmd[5] = 0x00 | 0x03;			// DATA_TYPE_QUAL_LSB   
	readCmd[8] = 0x00 | 0x0E;			// READ_DATA_HEADER + 8
		
	
	
	unsigned char parameter_buffer[14];
	
	
	
	
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
    
	
	printf("\nAnalog Gain\n");
	
	if (ioctl(sg_device, SG_IO, &io_hdr) < 0) 
    {
        perror("READ ioctl error");
        return 1;
    }     
	printf("Read: %02X\n", io_hdr.status);
	
	
	printf("0    : %02X\n", parameter_buffer[0]);
	printf("1    : %02X\n", parameter_buffer[1]);
	printf("2-5  : %02X%02X%02X%02X\n", parameter_buffer[2], parameter_buffer[3], parameter_buffer[4], parameter_buffer[5]);
	
	
	printf(" 6: %02X\n", parameter_buffer[6]);
	printf(" 7: %02X\n", parameter_buffer[7]);
	printf(" 8: %02X\n", parameter_buffer[8]);
	printf(" 9: %02X\n", parameter_buffer[9]);
		
	printf("10: %02X\n", parameter_buffer[10]);
	printf("11: %02X\n", parameter_buffer[11]);
	printf("12: %02X\n", parameter_buffer[12]);
	printf("13: %02X\n", parameter_buffer[13]);
	
	
	
	union 
	{
		float result;
		unsigned char bytearray[sizeof(float)];
	} u;
	
	
	
	u.bytearray[3] = parameter_buffer[10];
	u.bytearray[2] = parameter_buffer[11];
	u.bytearray[1] = parameter_buffer[12];
	u.bytearray[0] = parameter_buffer[13];
	
	

	
	
	printf("Analog gain: %.7f\n", u.result);			 
		 
					   
	
		
	
	return u.result;	
	
}



int coopActionParameter()
{
	
	readCmd[2] = 0x00 | 0x87;			// Data type code
	readCmd[3] = 0x00;
	readCmd[4] = 0x00;					// no meaning
	readCmd[5] = 0x00;					// 1 byte
	readCmd[6] = 0x00;
	readCmd[7] = 0x00;
	
	


	
	unsigned char parameter_buffer[24];
	
	

	
	memset(&io_hdr, 0, sizeof(sg_io_hdr_t));
    io_hdr.interface_id = 'S';
    io_hdr.mx_sb_len = sizeof(sense_buffer);
    io_hdr.sbp = sense_buffer;
    
	io_hdr.dxferp = parameter_buffer;     
	io_hdr.cmd_len = sizeof(readCmd);
	io_hdr.cmdp = readCmd;
	io_hdr.dxfer_direction = SG_DXFER_FROM_DEV;				//  /usr/include/scsi/sg.h
	io_hdr.timeout = 20000;
	
	
	
    	
	
	printf("\nInitiator cooperative action parameter\n");
	
	
	readCmd[8] = 0x00 | 0x06;						// READ_DATA_HEADER
	io_hdr.dxfer_len = 0x0 | 0x06;
	
	
	
	if (ioctl(sg_device, SG_IO, &io_hdr) < 0) 
	{
		perror("READ ioctl error");
		return 1;
	}
	 
	printf("Read: %02X\n", io_hdr.status);
	
	printf("0    : %02X\n", parameter_buffer[0]);
	printf("1    : %02X\n", parameter_buffer[1]);
	printf("2-5  : %02X%02X%02X%02X\n", parameter_buffer[2], parameter_buffer[3], parameter_buffer[4], parameter_buffer[5]);
	
	
	
		
	readCmd[8] = 0x00 | 0x18;						// READ_DATA_HEADER + 18	
	io_hdr.dxfer_len = sizeof(parameter_buffer);
	
	
	
	if (ioctl(sg_device, SG_IO, &io_hdr) < 0) 
	{
		perror("READ ioctl error");
		return 1;
	} 
	
	printf("Read: %02X\n", io_hdr.status);
	
	for (int i = READ_DATA_HEADER; i < READ_DATA_HEADER + parameter_buffer[5]; i++)
		printf("%d : %02X\n", i, parameter_buffer[i]);
	
	return 0;	
	
}





#define READBUFFER_MSB	0x04		// 2 * 3 * 186 = 1116 (045C)
#define READBUFFER_LSB	0x5C
#define READBUFFER 		1116		// 



void readData()
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
	
	
	
	unsigned char *buffer;
	
	buffer = NULL;
	
	
	// 1116 * 628 = 700848 bytes < 685*1024
	
	size_t mem_size = 685 * 1024;
    buffer = malloc(mem_size);
    if (buffer == NULL) 
    {
		printf("Buffer=NULL\n");
		return;
    } 
	
	
	
	printf("Read data\n");
	
	int bytes = 0;
	
	
	while (1)
	{
		
		if (ioctl(sg_device, SG_IO, &io_hdr) < 0) 
		{
			perror("READ ioctl error");
			return;
		}    
		
		if (io_hdr.status != 0)
			break;
			
			
		for (int i = 0; i < READBUFFER; i++)
			buffer[bytes + i] = parameter_buffer[i];
			
		bytes += READBUFFER;
		
		
		
		usleep(45000);		//microseconds
		
	}
		
	
	printf("Bytes: %d\n", bytes);
	
	
	
	FILE *write_ptr;

	write_ptr = fopen("../tmp/testThumb.bin", "wb");  // w for write, b for binary

	fwrite(buffer, 1, bytes, write_ptr); 
	
	fclose(write_ptr);
	
	free(buffer);
	
	
}






int main(int argc, char * argv[])
{
		
	
	if (argc != 2) 
    {
        printf("Usage: 'thumb /dev/sg<device number>'\n");
        return 1;
    }
	
	
		
    if ((sg_device = open(argv[1], O_RDWR)) < 0) 
    {
        perror("Error opening device");
        return 1;
    }
	    

    
	modeselect();
	
	
	uint32_t wbR = wbValue(RED);
	uint32_t wbG = wbValue(GREEN);
	uint32_t wbB = wbValue(BLUE);
	
	
	maxValue(1);	 
    maxValue(2);
    maxValue(3);
	
	analogGain();
	

   
	set(0, wbG);			// default same as GREEN
    set(RED, wbR);    
    set(GREEN, wbG);
    set(BLUE, wbB);
    

    
	scan();
	
	
	coopActionParameter();
	
	scan();	
	
	
	sleep(3);  
	readData();
    
    

	
    
    close(sg_device);
    return 0;
    
}
