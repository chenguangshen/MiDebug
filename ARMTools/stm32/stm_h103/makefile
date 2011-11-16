NAME   = demoh103_blink_rom

CC      = arm-none-eabi-gcc
LD      = arm-none-eabi-ld -v
AR      = arm-none-eabi-ar
AS      = arm-none-eabi-as
CP      = arm-none-eabi-objcopy
OD		= arm-none-eabi-objdump
  
CFLAGS  =  -I./ -c -fno-common -O0 -g -mcpu=cortex-m3 -mthumb 
AFLAGS  = -ahls -mapcs-32 -o crt.o
LFLAGS  = -Tstm_h103_blink_rom.cmd -nostartfiles
CPFLAGS = -Obinary
ODFLAGS	= -S

all: test

clean:
	-rm crt.lst a.lst main.lst crt.o main.o main.out main.hex main.map stm32f10x_rcc.o stm32f10x_gpio.o

test: main.out
	@ echo "...copying"
	$(CP) $(CPFLAGS) main.out main.bin
	$(OD) $(ODFLAGS) main.out > main.list

main.out: main.o stm32f10x_rcc.o stm32f10x_gpio.o stm_h103_blink_rom.cmd 
	@ echo "..linking"
	$(LD) $(LFLAGS) -o main.out  main.o stm32f10x_rcc.o stm32f10x_gpio.o 

crt.o: crt.s
	@ echo ".assembling"
	$(AS) $(AFLAGS) crt.s > crt.lst

stm32f10x_rcc.o: stm32f10x_rcc.c 
	@ echo ".compil"
	 $(CC) $(CFLAGS) stm32f10x_rcc.c 
	 
stm32f10x_gpio.o: stm32f10x_gpio.c
	@ echo ".compili"
	 $(CC) $(CFLAGS) stm32f10x_gpio.c 
	 
main.o: main.c
	@ echo ".compiling"
	 $(CC) $(CFLAGS) main.c