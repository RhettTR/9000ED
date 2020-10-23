# 9000ED
A Linux SCSI Generic (sg) Driver for Nikon Super COOLSCAN 9000 ED



Version 0.1

DESIGN:

The very first simple GUI, written in JAVA. It is a Apache NetBeans IDE 11.2 project.

The C programs are compiled separately as a Geany project with build command:

gcc -Wall -o "%e" "%f" -lm

The environment is Linux Mint 19.1

The fundamental design decision that JAVA never calls C functions, only executes 
C programs asynchronously. The communication between JAVA and the C programs happen
by arguments to the programs and files/exit-codes/outputs from the programs.


PREREQUESTS:

A Nikon Super CoolScan 9000 ED Film Scanner with a FH-869S Brownie Strip Film holder.
A filmstrip of three 6x6 (cm) images.

lsscsi must be installed (sudo apt install lsscsi).
convert must be installed (sudo apt install imagemagick-6.q16).
 


DEVELOPMENT:

The ability to asynchronously execute shell commands. The ability to detect 
the Nikon scanner as a Linux device. The ability to make a thumb scan of the 
filmstrip and split it into 3 separate images based on the unexposed film 
between the individual images. The ability to read and view TIFF files in JAVA.


TODO:

Spin until scanner warm-up time done (UNIT READY).
