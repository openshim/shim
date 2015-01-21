.PHONY: all build clean
all: build

#TOOLDIR=/usr/local/genericrisc-renesas-elf/bin
#GENERICRISCGCC=$(TOOLDIR)/genericrisc-elf-gcc
#GENERICRISCNM =$(TOOLDIR)/genericrisc-elf-nm
#GENERICRISCOBJCOPY =$(TOOLDIR)/genericrisc-elf-objcopy
#GENERICRISCOBJDUMP =$(TOOLDIR)/genericrisc-elf-objdump

#CC=$(GENERICRISCGCC)
#NM=$(GENERICRISCNM)
#OBJCOPY = $(GENERICRISCOBJCOPY)
#OBJDUMP = $(GENERICRISCOBJDUMP)

CC=gcc
NM=nm
OBJCOPY = objcopy
OBJDUMP = objdump


TARGET_NAME = sample.out
TARGET_HEX_NAME = sample.hex
TARGET_ASM_NAME = sample.S
#TARGET_DISASSEMBLE_NAME = sample.dis

SRCS= *.c
SRCS+= util.S
SRCS+= boot.S

#OPT_LNK= -g $(MACHINE) -Xlinker -Tsample.ld
OPT= -g $(MACHINE) -O2
MAP_NAME = sample.map

build: $(SRCS) sample.ld
ifeq ($(CC),)
	@echo 'Set Generic RISC CPU GCC in Makefile'
else
	$(CC) $(OPT) $(SRCS) -o $(TARGET_NAME)
	$(NM) -n $(TARGET_NAME) > $(MAP_NAME)
	$(OBJCOPY) $(TARGET_NAME) $(TARGET_HEX_NAME) -O srec
endif

clean:
	rm -f \#* *~ *.o *.map *.hex *.out *.dis
