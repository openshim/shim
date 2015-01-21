/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

#include <stdlib.h>

void main_System__Board_B0__Cluster_C0__PE_P01__CPU_P01() __attribute__((weak, alias ("_main_CPU_B0C0P1")));
void main_System__Board_B0__Cluster_C0__PE_P02__CPU_P02() __attribute__((weak, alias ("_main_CPU_B0C0P2")));
void main_System__Board_B0__Cluster_C0__PE_P03__CPU_P03() __attribute__((weak, alias ("_main_CPU_B0C0P3")));
void main_System__Board_B0__Cluster_C0__PE_P04__CPU_P04() __attribute__((weak, alias ("_main_CPU_B0C0P4")));
void main_System__Board_B0__Cluster_C1__PE_P05__CPU_P05() __attribute__((weak, alias ("_main_CPU_B0C1P5")));
void main_System__Board_B0__Cluster_C1__PE_P06__CPU_P06() __attribute__((weak, alias ("_main_CPU_B0C1P6")));
void main_System__Board_B0__Cluster_C1__PE_P07__CPU_P07() __attribute__((weak, alias ("_main_CPU_B0C1P7")));
void main_System__Board_B0__Cluster_C1__PE_P08__CPU_P08() __attribute__((weak, alias ("_main_CPU_B0C1P8")));
void main_System__Board_B1__Cluster_C2__PE_P09__CPU_P09() __attribute__((weak, alias ("_main_CPU_B1C2P1")));
void main_System__Board_B1__Cluster_C2__PE_P10__CPU_P10() __attribute__((weak, alias ("_main_CPU_B1C2P2")));
void main_System__Board_B1__Cluster_C2__PE_P11__CPU_P11() __attribute__((weak, alias ("_main_CPU_B1C2P3")));
void main_System__Board_B1__Cluster_C2__PE_P12__CPU_P12() __attribute__((weak, alias ("_main_CPU_B1C2P4")));
void main_System__Board_B1__Cluster_C3__PE_P13__CPU_P13() __attribute__((weak, alias ("_main_CPU_B1C3P5")));
void main_System__Board_B1__Cluster_C3__PE_P14__CPU_P14() __attribute__((weak, alias ("_main_CPU_B1C3P6")));
void main_System__Board_B1__Cluster_C3__PE_P15__CPU_P15() __attribute__((weak, alias ("_main_CPU_B1C3P7")));
void main_System__Board_B1__Cluster_C3__PE_P16__CPU_P16() __attribute__((weak, alias ("_main_CPU_B1C3P8")));

int _main_CPU_B0C0P1(void)
{
	return 0;
}

int _main_CPU_B0C0P2(void)
{
	while(1){
	    __asm__ __volatile__("" ::: "memory");
	}
}
int _main_CPU_B0C0P3(void)
{
	while(1){
	    __asm__ __volatile__("" ::: "memory");
	}
}
int _main_CPU_B0C0P4(void)
{
	while(1){
	    __asm__ __volatile__("" ::: "memory");
	}
}
int _main_CPU_B0C1P5(void)
{
	while(1){
	    __asm__ __volatile__("" ::: "memory");
	}
}
int _main_CPU_B0C1P6(void)
{
	while(1){
	    __asm__ __volatile__("" ::: "memory");
	}
}
int _main_CPU_B0C1P7(void)
{
	while(1){
	    __asm__ __volatile__("" ::: "memory");
	}
}
int _main_CPU_B0C1P8(void)
{
	while(1){
	    __asm__ __volatile__("" ::: "memory");
	}
}
int _main_CPU_B1C2P1(void)
{
	while(1){
	    __asm__ __volatile__("" ::: "memory");
	}
}
int _main_CPU_B1C2P2(void)
{
	while(1){
	    __asm__ __volatile__("" ::: "memory");
	}
}
int _main_CPU_B1C2P3(void)
{
	while(1){
	    __asm__ __volatile__("" ::: "memory");
	}
}
int _main_CPU_B1C2P4(void)
{
	while(1){
	    __asm__ __volatile__("" ::: "memory");
	}
}
int _main_CPU_B1C3P5(void)
{
	while(1){
	    __asm__ __volatile__("" ::: "memory");
	}
}
int _main_CPU_B1C3P6(void)
{
	while(1){
	    __asm__ __volatile__("" ::: "memory");
	}
}
int _main_CPU_B1C3P7(void)
{
	while(1){
	    __asm__ __volatile__("" ::: "memory");
	}
}
int _main_CPU_B1C3P8(void)
{
	while(1){
	    __asm__ __volatile__("" ::: "memory");
	}
}

void exit(int exit_code) {
    while(1){ Halt (); }
}

int main(void)
{
	main_System__Board_B0__Cluster_C0__PE_P01__CPU_P01();
	exit(0);
}

int main_cl0pe2(void)
{
	main_System__Board_B0__Cluster_C0__PE_P02__CPU_P02();
}
int main_cl0pe3(void)
{
	main_System__Board_B0__Cluster_C0__PE_P03__CPU_P03();
}
int main_cl0pe4(void)
{
	main_System__Board_B0__Cluster_C0__PE_P04__CPU_P04();
}
int main_cl1pe1(void)
{
	main_System__Board_B0__Cluster_C1__PE_P05__CPU_P05();
}
int main_cl1pe2(void)
{
	main_System__Board_B0__Cluster_C1__PE_P06__CPU_P06();
}
int main_cl1pe3(void)
{
	main_System__Board_B0__Cluster_C1__PE_P07__CPU_P07();
}
int main_cl1pe4(void)
{
	main_System__Board_B0__Cluster_C1__PE_P08__CPU_P08();
}
int main_cl2pe1(void)
{
	main_System__Board_B1__Cluster_C2__PE_P09__CPU_P09();
}
int main_cl2pe2(void)
{
	main_System__Board_B1__Cluster_C2__PE_P10__CPU_P10();
}
int main_cl2pe3(void)
{
	main_System__Board_B1__Cluster_C2__PE_P11__CPU_P11();
}
int main_cl2pe4(void)
{
	main_System__Board_B1__Cluster_C2__PE_P12__CPU_P12();
}
int main_cl3pe1(void)
{
	main_System__Board_B1__Cluster_C3__PE_P13__CPU_P13();
}
int main_cl3pe2(void)
{
	main_System__Board_B1__Cluster_C3__PE_P14__CPU_P14();
}
int main_cl3pe3(void)
{
	main_System__Board_B1__Cluster_C3__PE_P15__CPU_P15();
}
int main_cl3pe4(void)
{
	main_System__Board_B1__Cluster_C3__PE_P16__CPU_P16();
}
