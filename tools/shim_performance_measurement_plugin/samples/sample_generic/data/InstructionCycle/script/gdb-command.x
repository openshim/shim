target remote localhost:50003
file sample.out
load sample.out
#info threads

b exit

#shell echo  $(date +%s)
c
#shell echo  $(date +%s)
printf "*start dump...\n"
dump memory result.bin (long)result (long)result+sizeof(result)
#shell echo  $(date +%s)
q
y

