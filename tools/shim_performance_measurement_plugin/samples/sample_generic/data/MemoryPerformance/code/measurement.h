/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

#ifndef MEASUREMENT_H_
#define MEASUREMENT_H_

#define WORD_SIZE      (sizeof (unsigned int))
#define WORD_BIT_SIZE  (WORD_SIZE * 8)
#define CONTAINER_NUM  ((MC_NUM + (WORD_BIT_SIZE - 1)) / WORD_BIT_SIZE)

//Define Type for each AccessByteSize
#define ACCESS_BYTE_1 uint8_t
#define ACCESS_BYTE_2 uint16_t
#define ACCESS_BYTE_4 uint32_t
#define ACCESS_BYTE_8 uint64_t
#define ACCESS_BYTE_16 unsigned long long

//Define to measure Latency/Pitch(AccessByteSize=1)
#define LATENCY1_BEST_MEASURE(S,E,RW,T,R)	\
  do {						\
    R = latency1_measure(S,E,RW,T);		\
  } while (0)

#define LATENCY1_WORST_MEASURE(S,E,RW,T,R)	LATENCY1_BEST_MEASURE(S,E,RW,T,R)

#define LATENCY1_TYPICAL_MEASURE(S,E,RW,T,R)	LATENCY1_BEST_MEASURE(S,E,RW,T,R)

#define PITCH1_BEST_MEASURE(S,E,RW,T,R)	\
  do {					\
    R = pitch1_measure(S,E,RW,T);	\
  } while (0)

#define PITCH1_WORST_MEASURE(S,E,RW,T,R)	PITCH1_BEST_MEASURE(S,E,RW,T,R)

#define PITCH1_TYPICAL_MEASURE(S,E,RW,T,R)	PITCH1_BEST_MEASURE(S,E,RW,T,R)

//Define to measure Latency/Pitch(AccessByteSize=2)
#define LATENCY2_BEST_MEASURE(S,E,RW,T,R)	\
  do {						\
    R = latency2_measure(S,E,RW,T);		\
  } while (0)

#define LATENCY2_WORST_MEASURE(S,E,RW,T,R)	LATENCY2_BEST_MEASURE(S,E,RW,T,R)

#define LATENCY2_TYPICAL_MEASURE(S,E,RW,T,R)	LATENCY2_BEST_MEASURE(S,E,RW,T,R)

#define PITCH2_BEST_MEASURE(S,E,RW,T,R)	\
  do {					\
    R = pitch2_measure(S,E,RW,T);	\
  } while (0)

#define PITCH2_WORST_MEASURE(S,E,RW,T,R)	PITCH2_BEST_MEASURE(S,E,RW,T,R)

#define PITCH2_TYPICAL_MEASURE(S,E,RW,T,R)	PITCH2_BEST_MEASURE(S,E,RW,T,R)

//Define to measure Latency/Pitch(AccessByteSize=4)
#define LATENCY4_BEST_MEASURE(S,E,RW,T,R)	\
  do {						\
    R = latency4_measure(S,E,RW,T);		\
  } while (0)

#define LATENCY4_WORST_MEASURE(S,E,RW,T,R)	LATENCY4_BEST_MEASURE(S,E,RW,T,R)

#define LATENCY4_TYPICAL_MEASURE(S,E,RW,T,R)	LATENCY4_BEST_MEASURE(S,E,RW,T,R)

#define PITCH4_BEST_MEASURE(S,E,RW,T,R)	\
  do {					\
    R = pitch4_measure(S,E,RW,T);	\
  } while (0)

#define PITCH4_WORST_MEASURE(S,E,RW,T,R)	PITCH4_BEST_MEASURE(S,E,RW,T,R)

#define PITCH4_TYPICAL_MEASURE(S,E,RW,T,R)	PITCH4_BEST_MEASURE(S,E,RW,T,R)

//Define to measure Latency/Pitch(AccessByteSize=8)
#define LATENCY8_BEST_MEASURE(S,E,RW,T,R)	\
  do {						\
    R = latency8_measure(S,E,RW,T);		\
  } while (0)

#define LATENCY8_WORST_MEASURE(S,E,RW,T,R)	LATENCY8_BEST_MEASURE(S,E,RW,T,R)

#define LATENCY8_TYPICAL_MEASURE(S,E,RW,T,R)	LATENCY8_BEST_MEASURE(S,E,RW,T,R)

#define PITCH8_BEST_MEASURE(S,E,RW,T,R)	\
  do {					\
    R = pitch8_measure(S,E,RW,T);	\
  } while (0)
#define PITCH8_WORST_MEASURE(S,E,RW,T,R)	PITCH8_BEST_MEASURE(S,E,RW,T,R)

#define PITCH8_TYPICAL_MEASURE(S,E,RW,T,R)	PITCH8_BEST_MEASURE(S,E,RW,T,R)

//Define to Main Process

#define NOTIFY 0
#define STATUS 1

#define N_NOT_RUN 0
#define N_RUN 1

#define S_IDLE 0
#define S_PROCESSING 1

//INIT for Main CPU
#define INIT(N)										\
  do {												\
    if (N == MC__System__Board_B0__Cluster_C0__PE_P01__CPU_P01) {	\
      memset(core_sync, 0 , sizeof (core_sync));	\
    } else {										\
      core_sync[STATUS][N] = S_IDLE;				\
    }												\
  } while (0)

//UNINIT for Main CPU
#define UNINIT()	do { ; } while (0)

#define RESULT(AS,SS,SC,MC,AT,T,P,CY)							\
    do {														\
		measurement_set_result(AS, SS, SC, MC, AT, T, P, CY);	\
    } while (0)


//DELAY
#define DELAY(T)									\
  do {												\
    int i;											\
    for(i=0;i<T;i++) {								\
        __asm__ __volatile__("" ::: "memory");		\
    }												\
  } while (0)


//RUN sub CPU(N)
#define RUN_BASE(CONT)													\
	do {																\
		unsigned int bits, num, pos, i, ch;								\
		unsigned int running[CONTAINER_NUM];							\
		for (pos = 0; pos < CONTAINER_NUM; pos++) {						\
			num = pos * WORD_BIT_SIZE;									\
			for (bits = CONT[pos], i = 0;								\
					num < MC_NUM && i < WORD_BIT_SIZE;					\
					num++, i++, bits >>= 1) {							\
				if (num == 0) {											\
					continue;											\
				}														\
				if ((bits & 0x1) == 1) {								\
					core_sync[NOTIFY][num] = N_RUN;						\
				}														\
			}															\
		}																\
		do {															\
			for (i = 0; i < CONTAINER_NUM; i++) {						\
				running[i] = 0;											\
			}															\
			for (pos = 0; pos < CONTAINER_NUM; pos++) {					\
				num = pos * WORD_BIT_SIZE;								\
				for (bits = CONT[pos], i = 0;							\
						num < MC_NUM && i < WORD_BIT_SIZE;				\
						num++, i++, bits >>= 1) {						\
					if ((bits & 0x1) == 0 || num == 0) {				\
						continue;										\
					}													\
					if (core_sync[STATUS][num] == S_PROCESSING) {		\
						running[pos] |= (0x1 << i);						\
					}													\
				}														\
			}															\
			ch = 0;														\
			for (i = 0; i < CONTAINER_NUM; i++) {						\
				if (CONT[i] != running[i]) {							\
					ch = 1;												\
					break;												\
				}														\
			}															\
		} while (ch == 1);												\
		for (pos = 0; pos < CONTAINER_NUM; pos++) {						\
			num = pos * WORD_BIT_SIZE;									\
			for (bits = CONT[pos], i = 0; 								\
					num < MC_NUM && i < WORD_BIT_SIZE;					\
					num++, i++, bits >>= 1) {							\
				if (num == 0) {											\
					continue;											\
				}														\
				if ((bits & 0x1) == 1) {								\
					core_sync[NOTIFY][num] = N_NOT_RUN;					\
				}														\
			}															\
		}																\
	} while (0)


//RUN sub CPU(N)
#define RUN(N)															\
	do {																\
		unsigned int cont[CONTAINER_NUM];								\
		cont[N / WORD_BIT_SIZE] = (0x1 << (N % WORD_BIT_SIZE));			\
		RUN_BASE(cont);													\
	} while (0)


//RUN sub All CPU(N)
#define RUN_ALL(C)														\
	do {																\
		unsigned int cont[CONTAINER_NUM];								\
		unsigned int pos, num, i;										\
		for (i = 0; i < CONTAINER_NUM; i++) {							\
			cont[i] = 0;												\
		}																\
		for (pos = 0; pos < CONTAINER_NUM; pos++) {						\
			num = pos * WORD_BIT_SIZE;									\
			for (i = 0; num < MC_NUM && i < WORD_BIT_SIZE; num++, i++) {\
				cont[pos] |= (0x1 << i);								\
			}															\
		}																\
		cont[0] &= ~((unsigned int)1);									\
		RUN_BASE(cont);													\
	} while (0)

//WAIT for the end of the sub CPU processing
#define SYNC_BASE(CONT)													\
	do {																\
		unsigned int bits, num, pos, i, ch;								\
		unsigned int result[CONTAINER_NUM];								\
		do {															\
			for (i = 0; i < CONTAINER_NUM; i++) {						\
				result[i] = 0;											\
			}															\
			for (pos = 0; pos < CONTAINER_NUM; pos++) {					\
				num = pos * WORD_BIT_SIZE;								\
																		\
				bits = CONT[pos];										\
				for (i = 0; 											\
						i < WORD_BIT_SIZE && num < MC_NUM; 				\
						i++, num++, bits >>= 1) {						\
																		\
					if ((bits & 0x1) == 0 || num == 0) {				\
						continue;										\
					}													\
																		\
					if (core_sync[STATUS][num] == S_IDLE) {				\
						result[pos] |= (0x1 << i);						\
					}													\
				}														\
			}															\
																		\
			ch = 0;														\
			for (i = 0; i < CONTAINER_NUM; i++) {						\
				if (CONT[i] != result[i]) {								\
					ch = 1;												\
					break;												\
				}														\
			}															\
		} while (ch == 1);												\
	} while (0)


//WAIT for the end of the sub CPU processing
#define SYNC(N)															\
	do {																\
		unsigned int cont[CONTAINER_NUM];								\
		cont[N / WORD_BIT_SIZE] = (0x1 << (N % WORD_BIT_SIZE));			\
		SYNC_BASE(cont);												\
	} while (0)


//WAIT for the end of the sub All CPU processing
#define SYNC_ALL(C)														\
	do {																\
		unsigned int cont[CONTAINER_NUM];								\
		unsigned int pos, num, i;										\
		for (i = 0; i < CONTAINER_NUM; i++) {							\
			cont[i] = 0;												\
		}																\
		for (pos = 0; pos < CONTAINER_NUM; pos++) {						\
			num = pos * WORD_BIT_SIZE;									\
			for (i = 0; num < MC_NUM && i < WORD_BIT_SIZE; num++, i++) {\
				cont[pos] |= (0x1 << i);								\
			}															\
		}																\
		cont[0] &= (unsigned int)(~(unsigned int)1);					\
		SYNC_BASE(cont);												\
	} while (0)


//WAIT for the notification of the start-up from Main CPU
#define WAIT_RUN(N)														\
	do {																\
		do {															\
			__asm__ __volatile__("" ::: "memory");						\
		} while(core_sync[NOTIFY][N] != N_RUN);							\
		core_sync[STATUS][N] = S_PROCESSING;							\
	} while (0)

//Notify to Main CPU the end of Sub CPU process
#define END_RUN(N)														\
	do {																\
		core_sync[STATUS][N] = S_IDLE;									\
	} while (0)

#endif //MEASUREMENT_H_
