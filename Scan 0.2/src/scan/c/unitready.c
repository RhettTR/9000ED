#include <unistd.h>
#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <sys/ioctl.h>
#include <scsi/sg.h>
#include <scsi/scsi.h> 





#define OPCODE_TESTUNITREADY	0x0
#define CMD_TESTUNITREADY		6
	

 
 
unsigned char sense_buffer[32];  




int main(int argc, char * argv[])
{
	
    int sg_device;


        
	unsigned char testCmdBlk[CMD_TESTUNITREADY] =
		{OPCODE_TESTUNITREADY, 0, 0, 0, 0, 0};
 
    
    sg_io_hdr_t io_hdr;
    


    if ((sg_device = open(argv[1], O_RDONLY)) < 0) 
	{
		perror("Error opening device");
		return 1;
	}

    
    
    memset(&io_hdr, 0, sizeof(sg_io_hdr_t));
    io_hdr.interface_id = 'S';
    io_hdr.mx_sb_len = sizeof(sense_buffer);
    io_hdr.dxfer_direction = SG_DXFER_NONE;
    io_hdr.dxfer_len = 0;
    io_hdr.dxferp = NULL;
    io_hdr.sbp = sense_buffer;
    io_hdr.timeout = 20000; 
    


	
	io_hdr.cmd_len = sizeof(testCmdBlk);
    io_hdr.cmdp = testCmdBlk;
   
   

	

	
	if (ioctl(sg_device, SG_IO, &io_hdr) < 0) 
	{
		perror("TEST UNIT READY ioctl error");
		return 1;
	}
		
	
	if (io_hdr.status != 0)	// dirty ... if not ready wait to get a new real status value
		sleep(30);
		

	//printf("%02X-%02X-%02X-%02X\n", io_hdr.status, sense_buffer[2], sense_buffer[12], sense_buffer[13]);
	
	printf("%02X\n", io_hdr.status);	

    


   
    close(sg_device);
    
    return 0;
    
}
