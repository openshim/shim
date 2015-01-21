/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

#include <stdio.h>
#include <stdlib.h>
#include <windows.h>

void main_Cluster_0__Cluster_0_0__Core_0_0_0();
void main_Cluster_0__Cluster_0_0__Core_0_0_1();
void main_Cluster_0__Cluster_0_0__Core_0_0_2();
void main_Cluster_0__Cluster_0_0__Core_0_0_3();

static void set_cpu_id(int cpu_id)
{
	SetThreadAffinityMask(GetCurrentThread(), (1 << cpu_id));
}

static DWORD WINAPI thread1_func(LPVOID lpParam) {

	set_cpu_id(0);
	main_Cluster_0__Cluster_0_0__Core_0_0_0();

	return 0;
}


static DWORD WINAPI thread2_func(LPVOID lpParam) {

	set_cpu_id(1);
	main_Cluster_0__Cluster_0_0__Core_0_0_1();

	return 0;
}


static DWORD WINAPI thread3_func(LPVOID lpParam) {

	set_cpu_id(2);
	main_Cluster_0__Cluster_0_0__Core_0_0_2();

	return 0;
}


static DWORD WINAPI thread4_func(LPVOID lpParam) {

	set_cpu_id(3);
	main_Cluster_0__Cluster_0_0__Core_0_0_3();
	while(1);

	return 0;
}

int main(void) {
	HANDLE th1, th2, th3, th4;

	th2 = CreateThread(NULL, 0, thread2_func, NULL, 0, NULL);
	th3 = CreateThread(NULL, 0, thread3_func, NULL, 0, NULL);
	th4 = CreateThread(NULL, 0, thread4_func, NULL, 0, NULL);
	Sleep(1000);
	th1 = CreateThread(NULL, 0, thread1_func, NULL, 0, NULL);

	while (WaitForSingleObject(th1, 1000) != WAIT_OBJECT_0) {
		;
	}

	return 0;
}
