/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

#ifndef MEM_ACCESS_H
#define MEM_ACCESS_H

#define LATENCY_BEST_MEM_ACCESS_PER_LOOP 1
#define LATENCY_WORST_MEM_ACCESS_PER_LOOP 1
#define LATENCY_TYPICAL_MEM_ACCESS_PER_LOOP 1
#define PITCH_BEST_MEM_ACCESS_PER_LOOP 10
#define PITCH_WORST_MEM_ACCESS_PER_LOOP 10
#define PITCH_TYPICAL_MEM_ACCESS_PER_LOOP 1

struct latency {
    unsigned int best;
    unsigned int worst;
    unsigned int typical;
};

struct pitch {
    unsigned int best;
    unsigned int worst;
    unsigned int typical;
};

struct loopnum {
    struct latency l;
    struct pitch p;
};

void allocate_measure_memory(void * s, void * e);
void * search_measure_memory(void * s, void * e);
void free_all_measure_memory(void);
void aggregate_memory_access_performance(unsigned int latency[3], unsigned int pitch[3], double result[6]);

unsigned int latency1_measure(uint8_t * s, uint8_t* e, unsigned char rw, unsigned char pf);
unsigned int pitch1_measure(uint8_t* s, uint8_t* e, unsigned char rw, unsigned char pf);
unsigned int latency2_measure(uint16_t* s, uint16_t* e, unsigned char rw, unsigned char pf);
unsigned int pitch2_measure(uint16_t* s, uint16_t* e, unsigned char rw, unsigned char pf);
unsigned int latency4_measure(uint32_t* s, uint32_t* e, unsigned char rw, unsigned char pf);
unsigned int pitch4_measure(uint32_t* s, uint32_t* e, unsigned char rw, unsigned char pf);
unsigned int latency8_measure(uint64_t* s, uint64_t* e, unsigned char rw, unsigned char pf);
unsigned int pitch8_measure(uint64_t* s, uint64_t* e, unsigned char rw, unsigned char pf);

static void rdtsc_init (void) { }

typedef struct {
	unsigned int eax_s;
	unsigned int edx_s;
	unsigned int eax_e;
	unsigned int edx_e;
} REGINF;

#define rdtsc_start_count(INF)  __asm__ volatile ("rdtsc" : "=a" (INF.eax_s), "=d" (INF.edx_s));
#define rdtsc_stop_count(INF)  __asm__ volatile ("rdtsc" : "=a" (INF.eax_e), "=d" (INF.edx_e));
#define rdtsc_get_count(INF, RET) \
	do { \
		unsigned long clk_start, clk_end; \
		clk_start = INF.eax_s | ((unsigned long long)INF.edx_s << 32); \
		clk_end = INF.eax_e | ((unsigned long long)INF.edx_e << 32); \
		return clk_end - clk_start; \
	} while (0);

#endif //MEM_ACCESS_H
