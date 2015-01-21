.PHONY: all build clean
all: build

CC = gcc
NM = nm
OBJCOPY = objcopy
OBJDUMP = objdump

TARGET_NAME = sample.out
TARGET_HEX_NAME = sample.hex
TARGET_DISASSEMBLE_NAME = sample.dis
MAP_NAME = sample.map

SRCS  = *.c

OPT = -O2

build:
	$(CC) $(OPT) $(SRCS) -o $(TARGET_NAME)
	$(NM) -n $(TARGET_NAME) > $(MAP_NAME)
	$(OBJCOPY) $(TARGET_NAME) $(TARGET_HEX_NAME) -O srec
	$(OBJDUMP) -d $(TARGET_NAME) > $(TARGET_DISASSEMBLE_NAME)

clean:
	@-rm -f $(TARGET_NAME) $(MAP_NAME) $(TARGET_HEX_NAME) $(TARGET_DISASSEMBLE_NAME)
