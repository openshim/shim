target remote localhost:50003
file sample.out
load sample.out
#info threads

b exit

#shell echo  $(date +%s)
c
#shell echo  $(date +%s)
printf "*start dump...\n"
dump memory loopnum.bin (long)&loopnum (long)&loopnum+sizeof(loopnum)
dump memory result.bin (long)result_buf (long)result_buf+sizeof(result_buf)
#shell echo  $(date +%s)
q
y

