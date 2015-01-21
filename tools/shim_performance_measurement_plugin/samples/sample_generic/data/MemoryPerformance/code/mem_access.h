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

unsigned int latency1_measure(uint8_t * s, uint8_t* e, unsigned char rw, unsigned char pf);
unsigned int pitch1_measure(uint8_t* s, uint8_t* e, unsigned char rw, unsigned char pf);
unsigned int latency2_measure(uint16_t* s, uint16_t* e, unsigned char rw, unsigned char pf);
unsigned int pitch2_measure(uint16_t* s, uint16_t* e, unsigned char rw, unsigned char pf);
unsigned int latency4_measure(uint32_t* s, uint32_t* e, unsigned char rw, unsigned char pf);
unsigned int pitch4_measure(uint32_t* s, uint32_t* e, unsigned char rw, unsigned char pf);
unsigned int latency8_measure(uint64_t* s, uint64_t* e, unsigned char rw, unsigned char pf);
unsigned int pitch8_measure(uint64_t* s, uint64_t* e, unsigned char rw, unsigned char pf);

#endif //MEM_ACCESS_H
