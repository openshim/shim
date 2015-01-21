/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

#ifndef MEM_ASMINC_H
#define MEM_ASMINC_H

#define SET_ZERO(X)				\
  do {						\
    __asm__ __volatile__ ("mov r0,%0":"=r"(X));	\
  } while (0)

#define CLOBBER1(A)							\
  do {									\
    __asm__ __volatile__ ("/* %0 -- clobber & memory barrier */"	\
			  :"=r"(A)					\
			  :						\
			  :"memory");					\
  } while (0)

#define DATAFLOW1(A)							\
  do {									\
    __asm__ __volatile__ ("/* %0 -- dataflow & memory barrier */" 	\
			  :						\
			  : "r" (A)					\
			  : "memory");					\
  } while (0)

#define DATAFLOW2(A,B)							\
  do {									\
    __asm__ __volatile__ ("/* %0,%1 -- dataflow & memory barrier */" 	\
			  :						\
			  : "r" (A), "r" (B)				\
			  : "memory");					\
  } while (0)

#define DATAFLOW3(A,B,C)						\
  do {									\
    __asm__ __volatile__ ("/* %0,%1,%2 -- dataflow & memory barrier */" \
			  :						\
			  : "r" (A), "r" (B), "r" (C)			\
			  : "memory");					\
  } while (0)

#define DATAFLOW4(A,B,C,D)							\
  do {										\
    __asm__ __volatile__ ("/* %0,%1,%2,%3 -- dataflow & memory barrier */"	\
			  :							\
			  : "r" (A), "r" (B), "r" (C), "r" (D)			\
			  : "memory");						\
  } while (0)

/*
 * Latency Load
 */
#define LATENCY_LOAD_X(SZ,N,X,P)				\
  do {								\
    __asm__ __volatile__ (".rept %2\n\t"			\
			  "ld." #SZ " 0[%1], %0\n\t"		\
			  ".endr"				\
			  : "=&r" (X)				\
			  : "r" (P), "i" (N)			\
			  : "memory");				\
  } while (0)

#define LATENCY_LOAD_8(SZ,N,X,P1,P2)				\
  do {								\
    __asm__ __volatile__ (".rept %3\n\t"			\
			  "ld." #SZ " 0[%1], %0 /* %2 */\n\t"	\
			  ".endr"				\
			  : "=&r" (X)				\
			  : "r" (P1), "r"(P2), "i" (N)		\
			  : "memory");				\
  } while (0)

/*
 * Latency Store
 */
#define LATENCY_STORE_X(SZ,N,P)					\
  do {								\
    __asm__ __volatile__ (".rept %1\n\t"			\
			  "st." #SZ " %0, 0[%0]\n\t"		\
			  ".endr"				\
			  :: "r" (P), "i" (N)			\
			  : "memory");				\
  } while (0)

#define LATENCY_STORE_8(SZ,N,P1,P2)				\
  do {								\
    __asm__ __volatile__ (".rept %2\n\t"			\
			  "st." #SZ " %0, 0[%0] /* %1 */\n\t"	\
			  ".endr"				\
			  :: "r" (P1), "r" (P2), "i" (N)	\
			  : "memory");				\
  } while (0)


#endif //MEM_ASMINC_H
