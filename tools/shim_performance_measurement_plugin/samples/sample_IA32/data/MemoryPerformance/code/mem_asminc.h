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
    __asm__ __volatile__ ("mov $0, %0":"=r"(X));	\
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



#define LATENCY_LOAD_X(SZ,N,X,P) _LATENCY_LOAD_X(SZ,N,X,P)
#define LATENCY_LOAD_8(SZ,N,X,P1,P2) _LATENCY_LOAD_8(SZ,N,X,P1,P2)
#define LATENCY_STORE_X(SZ,N,P) _LATENCY_STORE_X(SZ,N,P)
#define LATENCY_STORE_8(SZ,N,P1,P2) _LATENCY_STORE_8(SZ,N,P1,P2)

#define _LATENCY_LOAD_X(SZ,N,X,P)				\
  do {								\
    __asm__ __volatile__ (".rept " #N "\n\t"			\
			  "mov 0(%1), %0\n\t"		\
			  ".endr"				\
			  : "=&r" (X)				\
			  : "r" (P)			\
			  : "memory");				\
  } while (0)

#define _LATENCY_LOAD_8(SZ,N,X,P1,P2)				\
  do {								\
    __asm__ __volatile__ (".rept " #N "\n\t"			\
			  "mov 0(%1), %0 /* %2 */\n\t"	\
			  ".endr"				\
			  : "=&r" (X)				\
			  : "A" (P1), "A"(P2)		\
			  : "memory");				\
  } while (0)

/*
 * Latency Store
 */
#define _LATENCY_STORE_X(SZ,N,P)					\
  do {								\
    __asm__ __volatile__ (".rept " #N "\n\t"			\
			  "mov %0, 0(%0)\n\t"		\
			  ".endr"				\
			  :: "r" (P)			\
			  : "memory");				\
  } while (0)

#define _LATENCY_STORE_8(SZ,N,P1,P2)				\
  do {								\
    __asm__ __volatile__ (".rept " #N "\n\t"			\
			  "mov %0, 0(%0) /* %1 */\n\t"	\
			  ".endr"				\
			  :: "A" (P1), "A" (P2)	\
			  : "memory");				\
  } while (0)

#endif //MEM_ASMINC_H
