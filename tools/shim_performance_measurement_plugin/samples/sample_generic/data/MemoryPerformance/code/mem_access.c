/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

#include <stdint.h>
#include "simulator.h"
#include "mem_access.h"
#include "mem_asminc.h"

#define P_BEST 0
#define P_WORST 1
#define P_TYPICAL 2

#define RW_READ 0
#define RW_WRITE 1

#define MEASUREMENT_NUM 10


struct loopnum loopnum = {
        {LATENCY_BEST_MEM_ACCESS_PER_LOOP, LATENCY_WORST_MEM_ACCESS_PER_LOOP, LATENCY_TYPICAL_MEM_ACCESS_PER_LOOP},
        {PITCH_BEST_MEM_ACCESS_PER_LOOP, PITCH_WORST_MEM_ACCESS_PER_LOOP, PITCH_TYPICAL_MEM_ACCESS_PER_LOOP}
    };



////////////////////////////////////////////////////////////
//
//  AccessByteSize = 1
//
////////////////////////////////////////////////////////////

unsigned int latency1_measure(uint8_t* s, uint8_t* e, unsigned char rw, unsigned char pf)
{
    unsigned int i;
    unsigned long begin, end;
    unsigned int cycle;

    register uint8_t* p;
    register uint32_t x=0;

    //It does not measure Typical
    if(pf == P_TYPICAL)
        return 0;

    if(pf == P_BEST)
        cycle = 0xFFFFFFFF;
    else
        cycle = 0x0;

    //Access Address Set
    p = e+1-20480;

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_LOAD_X(b,LATENCY_BEST_MEM_ACCESS_PER_LOOP,x,p);
                end = SYS_GetCycle();

                DATAFLOW3(p, x, cycle);

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_LOAD_X(b,LATENCY_WORST_MEM_ACCESS_PER_LOOP,x,p);
                end = SYS_GetCycle();

                DATAFLOW3(p, x, cycle);

                if(cycle < end - begin)
                    cycle = end - begin;
            }
        }
    }
    else
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_STORE_X(b,LATENCY_BEST_MEM_ACCESS_PER_LOOP,p);
                end = SYS_GetCycle();

                DATAFLOW2(p, cycle);

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_STORE_X(b,LATENCY_WORST_MEM_ACCESS_PER_LOOP,p);
                end = SYS_GetCycle();

                DATAFLOW2(p, cycle);

                if(cycle < end - begin)
                    cycle = end - begin;
            }
        }
    }

    return cycle;
}

unsigned int pitch1_measure(uint8_t* s, uint8_t* e, unsigned char rw, unsigned char pf)
{
    unsigned int i,j;
    unsigned long begin, end;
    unsigned int cycle;

    register uint8_t* p;
    register uint32_t x=0;

    //It does not measure Typical
    if(pf == P_TYPICAL)
        return 0;

    if(pf == P_BEST)
        cycle = 0xFFFFFFFF;
    else
        cycle = 0x0;

    //Access Address Set
    p = e+1-20480;

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                end = SYS_GetCycle();

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                end = SYS_GetCycle();

                if(cycle < end - begin)
                    cycle = end - begin;
            }
        }
    }
    else
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                end = SYS_GetCycle();

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                end = SYS_GetCycle();

                if(cycle < end - begin)
                    cycle = end - begin;
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
    unsigned long begin, end;
    unsigned int cycle;

    register uint16_t* p;
    register uint32_t x=0;

    //It does not measure Typical
    if(pf == P_TYPICAL)
        return 0;

    if(pf == P_BEST)
        cycle = 0xFFFFFFFF;
    else
        cycle = 0x0;

    //Access Address Set
    p = (uint16_t*)((char*)e+1-20480);

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_LOAD_X(h,LATENCY_BEST_MEM_ACCESS_PER_LOOP,x,p);
                end = SYS_GetCycle();

                DATAFLOW3(p, x, cycle);

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_LOAD_X(h,LATENCY_WORST_MEM_ACCESS_PER_LOOP,x,p);
                end = SYS_GetCycle();

                DATAFLOW3(p, x, cycle);

                if(cycle < end - begin)
                    cycle = end - begin;
            }
        }
    }
    else
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_STORE_X(h,LATENCY_BEST_MEM_ACCESS_PER_LOOP,p);
                end = SYS_GetCycle();

                DATAFLOW2(p, cycle);

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_STORE_X(h,LATENCY_WORST_MEM_ACCESS_PER_LOOP,p);
                end = SYS_GetCycle();

                DATAFLOW2(p, cycle);

                if(cycle < end - begin)
                    cycle = end - begin;
            }
        }
    }

    return cycle;
}

unsigned int pitch2_measure(uint16_t* s, uint16_t* e, unsigned char rw, unsigned char pf)
{
    unsigned int i,j;
    unsigned long begin, end;
    unsigned int cycle;

    register uint16_t* p;
    register uint32_t x=0;

    //It does not measure Typical
    if(pf == P_TYPICAL)
        return 0;

    if(pf == P_BEST)
        cycle = 0xFFFFFFFF;
    else
        cycle = 0x0;

    //Access Address Set
    p = (uint16_t*)((char*)e+1-20480);

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                end = SYS_GetCycle();

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                end = SYS_GetCycle();

                if(cycle < end - begin)
                    cycle = end - begin;
            }
        }
    }
    else
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                end = SYS_GetCycle();

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                end = SYS_GetCycle();

                if(cycle < end - begin)
                    cycle = end - begin;
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
    unsigned long begin, end;
    unsigned int cycle;

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
    p = (uint32_t*)((char*)e+1-20480);

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_LOAD_X(w,LATENCY_BEST_MEM_ACCESS_PER_LOOP,x,p);
                end = SYS_GetCycle();

                DATAFLOW3(p, x, cycle);

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_LOAD_X(w,LATENCY_WORST_MEM_ACCESS_PER_LOOP,x,p);
                end = SYS_GetCycle();

                DATAFLOW3(p, x, cycle);

                if(cycle < end - begin)
                    cycle = end - begin;
            }
        }
    }
    else
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_STORE_X(w,LATENCY_BEST_MEM_ACCESS_PER_LOOP,p);
                end = SYS_GetCycle();

                DATAFLOW2(p, cycle);

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_STORE_X(w,LATENCY_WORST_MEM_ACCESS_PER_LOOP,p);
                end = SYS_GetCycle();

                DATAFLOW2(p, cycle);

                if(cycle < end - begin)
                    cycle = end - begin;
            }
        }
    }

    return cycle;
}

unsigned int pitch4_measure(uint32_t* s, uint32_t* e, unsigned char rw, unsigned char pf)
{
    unsigned int i,j;
    unsigned long begin, end;
    unsigned int cycle;

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
    p = (uint32_t*)((char*)e+1-20480);

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                end = SYS_GetCycle();

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                end = SYS_GetCycle();

                if(cycle < end - begin)
                    cycle = end - begin;
            }
        }
    }
    else
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                end = SYS_GetCycle();

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                end = SYS_GetCycle();

                if(cycle < end - begin)
                    cycle = end - begin;
            }
        }
    }

    return cycle;
}

////////////////////////////////////////////////////////////
//
//  AccessByteSize = 8
//
////////////////////////////////////////////////////////////

unsigned int latency8_measure(uint64_t* s, uint64_t* e, unsigned char rw, unsigned char pf)
{
    unsigned int i;
    unsigned long begin, end;
    unsigned int cycle;
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
    p1 = (uint64_t*)((char*)e+1-20480);

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_LOAD_8(dw,LATENCY_BEST_MEM_ACCESS_PER_LOOP,x,p1,p2);
                end = SYS_GetCycle();

                DATAFLOW4(p1, p2, x, cycle);

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_LOAD_8(dw,LATENCY_WORST_MEM_ACCESS_PER_LOOP,x,p1,p2);
                end = SYS_GetCycle();

                DATAFLOW4(p1, p2, x, cycle);

                if(cycle < end - begin)
                    cycle = end - begin;
            }
        }
    }
    else
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_STORE_8(dw,LATENCY_BEST_MEM_ACCESS_PER_LOOP,p1,p2);
                end = SYS_GetCycle();

                DATAFLOW3(p1, p2, cycle);

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                LATENCY_STORE_8(dw,LATENCY_WORST_MEM_ACCESS_PER_LOOP,p1,p2);
                end = SYS_GetCycle();

                DATAFLOW3(p1, p2, cycle);

                if(cycle < end - begin)
                    cycle = end - begin;
            }
        }
    }

    return cycle;
}


unsigned int pitch8_measure(uint64_t* s, uint64_t* e, unsigned char rw, unsigned char pf)
{
    unsigned int i,j;
    unsigned long begin, end;
    unsigned int cycle;

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
    p = (uint64_t*)((char*)e+1-20480);

    if(rw == RW_READ)
    {
        if(pf == P_BEST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                end = SYS_GetCycle();

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    x = *p;
                    DATAFLOW1(x);
                }
                end = SYS_GetCycle();

                if(cycle < end - begin)
                    cycle = end - begin;
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
                begin = SYS_GetCycle();
                for(j=PITCH_BEST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                end = SYS_GetCycle();

                if(cycle > end - begin)
                    cycle = end - begin;
            }
        }
        else if(pf == P_WORST)
        {
            SET_ZERO(x);
            for(i=MEASUREMENT_NUM;i!=0;i--)
            {
                begin = SYS_GetCycle();
                for(j=PITCH_WORST_MEM_ACCESS_PER_LOOP;j!=0;j--)
                {
                    *p = x;
                    CLOBBER1(x);
                }
                end = SYS_GetCycle();

                if(cycle < end - begin)
                    cycle = end - begin;
            }
        }
    }

    return cycle;
}
