/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include "mem_access.h"
#include "mem_asminc.h"

#define P_BEST (0)
#define P_WORST (1)
#define P_TYPICAL (2)

#define RW_READ (0)
#define RW_WRITE (1)

#define MEASUREMENT_NUM (10)
#define MEASUREMENT_SIZE (20480)
#define LATENCY_CACHE_HIT_RATIO (0.95)
#define LATENCY_CACHE_NOT_FOUND_PROBABILITY (0.05)
#define PITCH_CACHE_HIT_RATIO (0.95)
#define PITCH_CACHE_NOT_FOUND_PROBABILITY (0.05)


struct loopnum loopnum = {
        {LATENCY_BEST_MEM_ACCESS_PER_LOOP, LATENCY_WORST_MEM_ACCESS_PER_LOOP, LATENCY_TYPICAL_MEM_ACCESS_PER_LOOP},
        {PITCH_BEST_MEM_ACCESS_PER_LOOP, PITCH_WORST_MEM_ACCESS_PER_LOOP, PITCH_TYPICAL_MEM_ACCESS_PER_LOOP}
    };

typedef struct {
	void * s;
	void * e;
	void * ptr;
	unsigned int configured;
} ACCESSPTR;

#define ACCESSPTR_MAX (8)

ACCESSPTR accessptr[ACCESSPTR_MAX];

////////////////////////////////////////////////////////////
//
//  Allocate Memory
//
////////////////////////////////////////////////////////////
void allocate_measure_memory(void * s, void * e)
{
	int i;
	for (i = 0; i < ACCESSPTR_MAX; i++) {
		if (accessptr[i].configured) {
			if (accessptr[i].s == s && accessptr[i].e == e) {
				break;
			} else {
				continue;
			}
		}

		accessptr[i].s = s;
		accessptr[i].e = e;
		accessptr[i].ptr = malloc(MEASUREMENT_SIZE);
		accessptr[i].configured = 1;
		break;
	}
}

void * search_measure_memory(void * s, void * e)
{
	int i;
	for (i = 0; i < ACCESSPTR_MAX; i++) {
		if (accessptr[i].configured && accessptr[i].s == s && accessptr[i].e == e) {
			return accessptr[i].ptr;
		}
	}
	return NULL;
}

void free_all_measure_memory(void)
{
	int i;
	for (i = 0; i < ACCESSPTR_MAX; i++) {
		if (accessptr[i].configured) {
			free(accessptr[i].ptr);
		}
	}
}


////////////////////////////////////////////////////////////
//
//  Aggregate Memory Access Performance
//
////////////////////////////////////////////////////////////
void aggregate_memory_access_performance(unsigned int latency[3], unsigned int pitch[3], double result[6])
{
    result[0] = latency[0] / loopnum.l.best;
    result[1] = latency[1] / loopnum.l.worst;
    result[2] = latency[2] / loopnum.l.typical;
    result[3] = pitch[0] / loopnum.p.best;
    result[4] = pitch[1] / loopnum.p.worst;
    result[5] = pitch[2] / loopnum.p.typical;

    if (result[0] > result[1]) {
    	double tmp = result[1];
    	result[1] = result[0];
    	result[0] = tmp;
    }

    if (result[3] > result[4]) {
    	double tmp = result[4];
    	result[4] = result[3];
    	result[3] = tmp;
    }

    result[2] = (result[0] * LATENCY_CACHE_HIT_RATIO) + (result[1] * LATENCY_CACHE_NOT_FOUND_PROBABILITY);
    result[5] = (result[3] * PITCH_CACHE_HIT_RATIO) + (result[4] * PITCH_CACHE_NOT_FOUND_PROBABILITY);
}

////////////////////////////////////////////////////////////
//
//  AccessByteSize = 1
//
////////////////////////////////////////////////////////////

unsigned int latency1_measure(uint8_t* s, uint8_t* e, unsigned char rw, unsigned char pf)
{
    unsigned int i;
    unsigned long diff;
    unsigned int cycle;
    REGINF inf;

    register uint8_t* p;
    register uint8_t x=0;

    //It does not measure Typical
    if(pf == P_TYPICAL)
        return 0;

    if(pf == P_BEST)
        cycle = 0xFFFFFFFF;
    else
        cycle = 0x0;

    //Access Address Set
    p = (uint8_t *)search_measure_memory(s, e);

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_LOAD_X(b,LATENCY_BEST_MEM_ACCESS_PER_LOOP,x,p);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);
                DATAFLOW3(p, x, cycle);
                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_LOAD_X(b,LATENCY_WORST_MEM_ACCESS_PER_LOOP,x,p);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                DATAFLOW3(p, x, cycle);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }
    else
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_STORE_X(b,LATENCY_BEST_MEM_ACCESS_PER_LOOP,p);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                DATAFLOW2(p, cycle);

                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_STORE_X(b,LATENCY_WORST_MEM_ACCESS_PER_LOOP,p);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                DATAFLOW2(p, cycle);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }
    return cycle;
}

unsigned int pitch1_measure(uint8_t* s, uint8_t* e, unsigned char rw, unsigned char pf)
{
    unsigned int i,j;
    unsigned long diff;
    unsigned int cycle;
    REGINF inf;

    register uint8_t* p;
    register uint8_t x=0;

    //It does not measure Typical
    if(pf == P_TYPICAL)
        return 0;

    if(pf == P_BEST)
        cycle = 0xFFFFFFFF;
    else
        cycle = 0x0;

    //Access Address Set
    p = (uint8_t *)search_measure_memory(s, e);

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }
    else
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }

    return cycle;
}

////////////////////////////////////////////////////////////
//
//  AccessByteSize = 2
//
////////////////////////////////////////////////////////////

unsigned int latency2_measure(uint16_t* s, uint16_t* e, unsigned char rw, unsigned char pf)
{
    unsigned int i;
    unsigned long diff;
    unsigned int cycle;
    REGINF inf;

    register uint16_t* p;
    register uint16_t x=0;

    //It does not measure Typical
    if(pf == P_TYPICAL)
        return 0;

    if(pf == P_BEST)
        cycle = 0xFFFFFFFF;
    else
        cycle = 0x0;

    //Access Address Set
    p = (uint16_t *)search_measure_memory(s, e);

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_LOAD_X(w,LATENCY_BEST_MEM_ACCESS_PER_LOOP,x,p);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                DATAFLOW3(p, x, cycle);

                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_LOAD_X(w,LATENCY_WORST_MEM_ACCESS_PER_LOOP,x,p);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                DATAFLOW3(p, x, cycle);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }
    else
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_STORE_X(w,LATENCY_BEST_MEM_ACCESS_PER_LOOP,p);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                DATAFLOW2(p, cycle);

                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_STORE_X(w,LATENCY_WORST_MEM_ACCESS_PER_LOOP,p);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                DATAFLOW2(p, cycle);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }

    return cycle;
}

unsigned int pitch2_measure(uint16_t* s, uint16_t* e, unsigned char rw, unsigned char pf)
{
    unsigned int i,j;
    unsigned long diff;
    unsigned int cycle;
    REGINF inf;

    register uint16_t* p;
    register uint16_t x=0;

    //It does not measure Typical
    if(pf == P_TYPICAL)
        return 0;

    if(pf == P_BEST)
        cycle = 0xFFFFFFFF;
    else
        cycle = 0x0;

    //Access Address Set
    p = (uint16_t *)search_measure_memory(s, e);

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }
    else
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }

    return cycle;
}


////////////////////////////////////////////////////////////
//
//  AccessByteSize = 4
//
////////////////////////////////////////////////////////////

unsigned int latency4_measure(uint32_t* s, uint32_t* e, unsigned char rw, unsigned char pf)
{
    unsigned int i;
    unsigned long diff;
    unsigned int cycle;
    REGINF inf;

    register uint32_t* p;
    register uint32_t x=0;

    //It does not measure Typical
    if(pf == P_TYPICAL)
        return 0;

    if(pf == P_BEST)
        cycle = 0xFFFFFFFF;
    else
        cycle = 0x0;

    //Access Address Set
    p = (uint32_t *)search_measure_memory(s, e);

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_LOAD_X(l,LATENCY_BEST_MEM_ACCESS_PER_LOOP,x,p);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                DATAFLOW3(p, x, cycle);

                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_LOAD_X(l,LATENCY_WORST_MEM_ACCESS_PER_LOOP,x,p);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                DATAFLOW3(p, x, cycle);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }
    else
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_STORE_X(l,LATENCY_BEST_MEM_ACCESS_PER_LOOP,p);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                DATAFLOW2(p, cycle);

                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_STORE_X(l,LATENCY_WORST_MEM_ACCESS_PER_LOOP,p);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                DATAFLOW2(p, cycle);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }

    return cycle;
}

unsigned int pitch4_measure(uint32_t* s, uint32_t* e, unsigned char rw, unsigned char pf)
{
    unsigned int i,j;
    unsigned long diff;
    unsigned int cycle;
    REGINF inf;

    register uint32_t* p;
    register uint32_t x=0;

    //It does not measure Typical
    if(pf == P_TYPICAL)
        return 0;

    if(pf == P_BEST)
        cycle = 0xFFFFFFFF;
    else
        cycle = 0x0;

    //Access Address Set
    p = (uint32_t *)search_measure_memory(s, e);

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }
    else
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }

    return cycle;
}

#if 1
////////////////////////////////////////////////////////////
//
//  AccessByteSize = 8
//
////////////////////////////////////////////////////////////

unsigned int latency8_measure(uint64_t* s, uint64_t* e, unsigned char rw, unsigned char pf)
{
    unsigned int i;
    unsigned long diff;
    unsigned int cycle;
    REGINF inf;

#if 0
    register uint64_t* p1 asm("r26");
    register uint64_t* p2 asm("r27");
#else
    register uint64_t* p1;
    register uint64_t* p2;
#endif
    register uint64_t x=0;

    //It does not measure Typical
    if(pf == P_TYPICAL)
        return 0;

    if(pf == P_BEST)
        cycle = 0xFFFFFFFF;
    else
        cycle = 0x0;

    //Access Address Set
    p1 = (uint64_t*)search_measure_memory(s, e);

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_LOAD_8(q ,LATENCY_BEST_MEM_ACCESS_PER_LOOP,x,p1,p2);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                DATAFLOW4(p1, p2, x, cycle);

                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_LOAD_8(q ,LATENCY_WORST_MEM_ACCESS_PER_LOOP,x,p1,p2);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                DATAFLOW4(p1, p2, x, cycle);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }
    else
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_STORE_8(q ,LATENCY_BEST_MEM_ACCESS_PER_LOOP,p1,p2);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                DATAFLOW3(p1, p2, cycle);

                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                LATENCY_STORE_8(q ,LATENCY_WORST_MEM_ACCESS_PER_LOOP,p1,p2);
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                DATAFLOW3(p1, p2, cycle);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }

    return cycle;
}


unsigned int pitch8_measure(uint64_t* s, uint64_t* e, unsigned char rw, unsigned char pf)
{
    unsigned int i,j;
    unsigned long diff;
    unsigned int cycle;
    REGINF inf;

    register uint64_t* p;
    register uint64_t x=0;

    //It does not measure Typical
    if(pf == P_TYPICAL)
        return 0;

    if(pf == P_BEST)
        cycle = 0xFFFFFFFF;
    else
        cycle = 0x0;

    //Access Address Set
    p = (uint64_t*)search_measure_memory(s, e);

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }
    else
    {
        if(pf == P_BEST)
        {
            SET_ZERO(x);
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle > diff)
                    cycle = diff;
            }
        }
        else if(pf == P_WORST)
        {
            SET_ZERO(x);
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                rdtsc_start_count(inf);
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                rdtsc_stop_count(inf);
                rdtsc_get_count(inf, diff);

                if(cycle < diff)
                    cycle = diff;
            }
        }
    }

    return cycle;
}
#endif
