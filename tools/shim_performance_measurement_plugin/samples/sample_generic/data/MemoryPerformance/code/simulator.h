/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */


#ifndef SIMULATOR_H
#define SIMULATOR_H

// The number of cycles the entire system acquisition register
#define SYS_CYCLE_CNT    (*(volatile unsigned long*)0xF3008018)  /* The number of cycles the entire system acquisition register */

// Number of cycles cluster 0 acquisition register
#define CL0PE0_CYCLE_CNT    (*(volatile unsigned long*)0xFF00FFFF)  /* Number of cycles register PE0(CL0PE0) */
#define CL0PE1_CYCLE_CNT    (*(volatile unsigned long*)0xFF01FFFF)  /* Number of cycles register PE1(CL0PE1) */
#define CL0PE2_CYCLE_CNT    (*(volatile unsigned long*)0xFF02FFFF)  /* Number of cycles register PE2(CL0PE2) */
#define CL0PE3_CYCLE_CNT    (*(volatile unsigned long*)0xFF03FFFF)  /* Number of cycles register PE3(CL0PE3) */

// Number of cycles cluster 1 acquisition register
#define CL1PE0_CYCLE_CNT    (*(volatile unsigned long*)0xFF04FFFF)  /* Number of cycles register PE4(CL1PE0) */
#define CL1PE1_CYCLE_CNT    (*(volatile unsigned long*)0xFF05FFFF)  /* Number of cycles register PE5(CL1PE1) */
#define CL1PE2_CYCLE_CNT    (*(volatile unsigned long*)0xFF06FFFF)  /* Number of cycles register PE6(CL1PE2) */
#define CL1PE3_CYCLE_CNT    (*(volatile unsigned long*)0xFF07FFFF)  /* Number of cycles register PE7(CL1PE3) */

// Number of cycles cluster 2 acquisition register
#define CL2PE0_CYCLE_CNT    (*(volatile unsigned long*)0xFF08FFFF)  /* Number of cycles register PE8(CL2PE0) */
#define CL2PE1_CYCLE_CNT    (*(volatile unsigned long*)0xFF09FFFF)  /* Number of cycles register PE9(CL2PE1) */
#define CL2PE2_CYCLE_CNT    (*(volatile unsigned long*)0xFF0AFFFF)  /* Number of cycles register PE10(CL2PE2) */
#define CL2PE3_CYCLE_CNT    (*(volatile unsigned long*)0xFF0BFFFF)  /* Number of cycles register PE11(CL2PE3) */

// Number of cycles cluster 3 acquisition register
#define CL3PE0_CYCLE_CNT    (*(volatile unsigned long*)0xFF0CFFFF)  /* Number of cycles register PE12(CL3PE0) */
#define CL3PE1_CYCLE_CNT    (*(volatile unsigned long*)0xFF0DFFFF)  /* Number of cycles register PE13(CL3PE1) */
#define CL3PE2_CYCLE_CNT    (*(volatile unsigned long*)0xFF0EFFFF)  /* Number of cycles register PE14(CL3PE2) */
#define CL3PE3_CYCLE_CNT    (*(volatile unsigned long*)0xFF0FFFFF)  /* Number of cycles register PE15(CL3PE3) */

// Get the number of cycles the entire system
#define SYS_GetCycle()      SYS_CYCLE_CNT;      /* Number of cycles PE0(CL0PE0) */

// Get the number of cycles cluster 0
#define CL0PE0_GetCycle()   CL0PE0_CYCLE_CNT;   /* Number of cycles PE0(CL0PE0) */
#define CL0PE1_GetCycle()   CL0PE1_CYCLE_CNT;   /* Number of cycles PE1(CL0PE1) */
#define CL0PE2_GetCycle()   CL0PE2_CYCLE_CNT;   /* Number of cycles PE2(CL0PE2) */
#define CL0PE3_GetCycle()   CL0PE3_CYCLE_CNT;   /* Number of cycles PE3(CL0PE3) */

// Get the number of cycles cluster 1
#define CL1PE0_GetCycle()   CL1PE0_CYCLE_CNT;   /* Number of cycles PE4(CL1PE0) */
#define CL1PE1_GetCycle()   CL1PE1_CYCLE_CNT;   /* Number of cycles PE5(CL1PE1) */
#define CL1PE2_GetCycle()   CL1PE2_CYCLE_CNT;   /* Number of cycles PE6(CL1PE2) */
#define CL1PE3_GetCycle()   CL1PE3_CYCLE_CNT;   /* Number of cycles PE7(CL1PE3) */

// Get the number of cycles cluster 2
#define CL2PE0_GetCycle()   CL2PE0_CYCLE_CNT;   /* Number of cycles PE8(CL2PE0) */
#define CL2PE1_GetCycle()   CL2PE1_CYCLE_CNT;   /* Number of cycles PE9(CL2PE1) */
#define CL2PE2_GetCycle()   CL2PE2_CYCLE_CNT;   /* Number of cycles PE10(CL2PE2) */
#define CL2PE3_GetCycle()   CL2PE3_CYCLE_CNT;   /* Number of cycles PE11(CL2PE3) */

// Get the number of cycles cluster 3
#define CL3PE0_GetCycle()   CL3PE0_CYCLE_CNT;   /* Number of cycles PE12(CL3PE0) */
#define CL3PE1_GetCycle()   CL3PE1_CYCLE_CNT;   /* Number of cycles PE13(CL3PE1) */
#define CL3PE2_GetCycle()   CL3PE2_CYCLE_CNT;   /* Number of cycles PE14(CL3PE2) */
#define CL3PE3_GetCycle()   CL3PE3_CYCLE_CNT;   /* Number of cycles PE15(CL3PE3) */

#endif /* SIMULATOR_H */
