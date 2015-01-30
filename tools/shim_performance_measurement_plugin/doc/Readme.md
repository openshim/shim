SHIM Performance Measurement Plugin
================

This tool is an Eclipse Plugin-in, which is capable of measuring and specifying memory access performance and instruction cycles of SHIM.

  1. [Description](#description)
  * [Requirement](#requirement)
  * [Setup](#setup)
    - 3.1. [Install](#install)
    - 3.2. [Uninstall](#uninstall)
  * [Tutorial](#tutorial)
    - 4.1. [Procedures](#procedures)
    - 4.2. [Import the sample project](#import-the-sample-project)
    - 4.3. [Run the measurement process](#run-the-measurement-process)
  * [Configurations Dialog](#configurations-dialog)
    - 5.1. [Create the launch configuration](#create-the-launch-configuration)
      - 5.1.1. [Creation flow](#creation-flow)
      - 5.1.2. [Common Settings](#common-settings)
      - 5.1.3. [Main Tab](#main-tab)
      - 5.1.4. [MemoryPerformance Tab](#memoryperformance-tab)
      - 5.1.5. [InstructionCycle Tab](#instructioncycle-tab)
    - 5.2. [Launching the created configuration file](#launching-the-created-configuration-file)
  * [Sample Project Details](#sample-project-details)
    - 6.1. [About the sample project](#about-the-sample-project)
    - 6.2. [Directory structure(IA-32)](#directory-structure-ia32)
    - 6.3. [Directory structure(Generic)](#directory-structure-generic)
  * [Copyright](#copyright)
  * [License](#license)

1. <a name="description">Description</a>
----------------

This tool performs the following operations on the input SHIM data files.

  1. Generating C language source code for measuring the memory access perfromance (in processor cycles) of each MasterComponent(PU)-memroy combination from a specified SHIM XML file
  2. Writing back the cycles measured with the C language code of 1. to the SHIM XML file
  3. Generating C language source code for measuring instruction cycles of each instruction of LLVM intermediate representation from a specified SHIM XML file
  4. Writing the instruction cycles measured with the C language code of 3. to the SHIM XML file


this tool generates the C language source code to measure the performance of InstructnionCycle or MemoryAccess.  
In order to generate such C language source code, it is necessary to provide the files in which the specific configuration of architecture or execution environment is stored.  
Such configuration files need to be created in advance before executing this tool.

Since this tool just generates the C language source code ,
it is necessary to prepare the script for the measurement in advance so as to execute the measurement and output the measured result.

The measured result is written from the CSV file to SHIM data file.  


2. <a name="requirement">Requirement</a>
----------------
To take advantage of this tool, the following settings should be conducted correctly.

  - Java SE Development Kit (JDK)
    * JDK 7 or above
    * http://www.oracle.com/technetwork/java/javase/downloads/index.html

After installation, please add 'JAVA_HOME' to Environment Variables, and specify the path to the installed JDK.

In addition, please add the path to the bin folder in the JDK installed folder to 'PATH' in Environment Variables.

  - Eclipse
    * 3.6 (Helios) or above
    * https://eclipse.org/downloads/

To use the sample in Windows, it is necessary to install either of the following development environment,
and ensure that GNU Toolchain such as GCC is available.
After installation, please add the path to the bin folder in the installed folder to 'PATH' in Windows Environment Variables.

  - MinGW
    * http://www.mingw.org/
  - Cygwin
    * https://www.cygwin.com/

3. <a name="setup">Setup</a>
----------------

### 3.1. <a name="install">Install</a>
Please install as the steps below.

  1. Download the latest .jar file of the Plug-in
  *  Store the .jar file in any directory under the plugin directory (dropins) of Eclipse
  *  Restart Eclipse

Upon restarting Eclipse, the following icon will be displayed in the toolbar.  

<img src="images/1.png" width="600">  
**figure1. IDE after installation**

Please confirm the information of the installed plug-in have been added to "Help->About Eclipse Platform->Installation Details->Plug-ins"..  

<img src="images/2.png" width="600">  
**figure2. Plug-ins Installation Details(1)**

<img src="images/3.png" width="600">  
**figure3. Plug-ins Installation Details(2)**

<img src="images/4.png" width="600">  
**figure2. Plug-ins Installation Details(3)**

### 3.2. <a name="uninstall">Uninstall</a>
To uninstall this tool, simply delete the .jar file stored during the installation.


4. <a name="tutorial">Tutorial</a>
----------------
#### 4.1. <a name="procedures">Procedures</a>
This section describes the usage of SHIM Performance Measurement Plug-in.  
Please try the performance measurement in Windows environment using a sample project.  
In this case, we use the sample project for IA32 (sample_IA32) from the two sample projects we have,.
Following is the usage of the sample project.

  1. Import the sample project
  *  Rewrite the related files on condition that modification of measurement method or aggregation method is necessary
  *  Select the sample project, and conduct the measurement of InstructionCycle or MemoryPerformance in the drop-down menu on the toolbar
  *  Confirm the output result
    - The SHIM file containing the measurement result will be placed under the folder "data/" by default.
    - The codes and CSV files generated during the measurement will be output to the folder "Measurement/".
  * Repeat Step 2. ~ 4., if necessary.

The sample project will be available by importing SHIM Performance Measurement Plugin to the installed Eclipse environment.  
Please refer to 6. [Sample Project Details](#sample-project-details) for details of the sample projects.

#### 4.2. <a name="import-the-sample-project">Import the sample project</a>
Following is how to import the sample project.

1. Click 'Import' in 'File' menu of Eclipse
*  Select 'General/Existing Projects into Workspace' from the list of 'import source'
*  Locate the directory (or archive file) of sample_IA32 from 'Browse' of 'Select root directory' (or 'Select archive file')
*  Check 'sample_IA32/' is marked as checked in 'Project' and click 'Finish' button

<img src="images/5.png" width="600">  
**figure5. Sample Project import procedure(1)**

<img src="images/6.png" width="600">  
**figure6. Sample Project import procedure(2)**

<img src="images/7.png" width="600">  
**figure7. Sample Project import procedure(3)**

#### 4.3. <a name="run-the-measurement-process">Run the measurement process</a>
Measurement will be executable right after importing the sample project.  
Followings are the two possible measurement processes.  

  - Instruction Cycle
  - Memory Access Performance

Besides these two processes cannot be performed at a time.  

Following is how to execute the measurement.

  1. Select the target sample project
  *  Open the dorp-down menu of the icon in toolbar
  *  Select one of the run configuration for the measurement
    - To measure memory access performance:
      [Memory Performance Measurement...]>[sample_IA32_MemoryPerform]
    - To measure instruction cycles:  
      [Instruction Cycle Extraction...]>[sample_IA32_InstructionCycle]

Tips: The most recently executed run configuration can be started by clicking the icon in toolbar, or selecting the top of the drop-down menu.

<img src="images/8.png" width="600">  
**figure8. Execution method of Sample Project**

Once the measurement process is executed, the information form the attached sample SHIM file (jp.co.topscom.Intel_i5_3550.win7_sp1_64.LLVM3_4_CycCoarseMeasure.xml) will be extracted, and a C language source code for the measurement will be generated.
Meanwhile, the generated source code will be stored in a temporary directory generated in the measurement directory.

Thereafter, the program for measurement will be built in the temporary directory by the shell script attached in the sample and the measurement will be performed.

The measurement result will be output to a CSV file in the temporary directory, then written back into the SHIM data file using the CSV file.

<img src="images/9.png" width="600">  
**figure9. The contents of the temporary directory**

In the sample, the SHIM data file with the contents of the CSV file will be output to the data directory of the project with the following names assigned.

  - SHIM file for memory access performance measurement
    memory_perform_out_shim.xml
  - SHIM file for instruction cycle measurement
    instruction_cycle_out_shim.xml

<img src="images/10.png" width="600">  
**figure10. The SHIM file which reflected measuring result**

5. <a name="configurations-dialog">Configurations Dialog</a>
----------------

### 5.1. <a name="create-the-launch-configuration">Create the launch configuration</a>
#### 5.1.1 <a name="creation-flow">Creation flow</a>

In order to execute this tool, it is necessary to create the run configuration files (.launch) in advance.
In the run configuration file, information such as a type of feature to be executed, and the path of the SHIM data files will be specified.

Run configuration files can be created by the following steps.

  1. Select the target sample project for the creating run configuration (figure11).
  * Open the drop-down menu of the icon in toolbar, and select "Open Configurations..." menu.
  * Select "SHIM Performance Measurement" in the run configuration edit screen, and click 'New' button (figure12).
  * Ensure whether the necessary settings for executing the tool have been edited, and no error occurs.
  * Click "Apply" button to confirm the edit.

if there is no run configuration file in the project, the new run configuration file can be created by selecting either menu of 'Memory Performance Measurement...' or 'Instruction Cycle Extraction...' in the drop-down menu of toolbar icon.  (figure13)

<img src="images/11.png" width="600">  
**figure11. Open Configurations Menu**

<img src="images/12.png" width="600">  
**figure12. Run Configuration Dialog**

<img src="images/13.png" width="600">  
**figure13. New Configurations Menu**

#### 5.1.2. <a name="common-settings">Common Settings</a>

Followings are the common settings in each tab of the configuration edit screen.

##### Name
Specify the name of a run configuration file. Generally,
it follows the format, {project name}_{type of the selected feature}.

<img src="images/14.png" width="600">  
**figure14. Selection of individual configuration settings**

#### 5.1.3. <a name="main-tab">Main Tab</a>

Followings are the items that can be set in Main Tab.

##### Project
Specify the name of a project to save the configuration. ".launch" file and a temporary directory will be generated in the specified project.

##### Process selection
Select either of the features to be specified (Memory Performance Measurement or Instruction Cycle Measurement).

<img src="images/15.png" width="600">  
**figure15. Main Tab**

#### 5.1.4. <a name="memoryperformance-tab">MemoryPerformance Tab</a>

Followings are the items which can be set in MemoryPerformance Tab.

##### Process selection
Select either of the features to be executed. There are two selectable features of Code generation and Measurement.

##### Input SHIM file
Specify the input SHIM file.

##### Output SHIM file
Specify the output SHIM file.

##### Overwrite the input SHIM file with results
Specify to overwrite the input SHIM file with measurement results, if the checkbox is checked.

##### View the output SHIM file
Specify to display the SHIM file containing measurement results in IDE after the measurement, if the checkbox is checked.

##### Config. file
Specify the input configuration file.

##### Command
Specify the command and script to execute measurement.
The paths of generated code and CSV file to store the result
are specified using "${GenerateCodePath}" and "${ResultFilePath}", respectively, if necessary.

##### Measurement code location
Specify the output location of the measurement code.
If not specified, the code file will be located in a temporary directory in the project.

##### Result CSV file location
Specify the location of the measurement result CSV file.
If not specified, it will be located in a temporary directory in the project.

In the measurement result CSV file, each parameter is described in one line in the following order.

| No.|                 Element | Description |
|------|----------------------|------|
|    1 | AddressSpace name    | name attribute of AddressSpace of the measurement target |
|    2 | SubSpace name        | name attribute of SubSpace of the measurement target |
|    3 | SlaveComponent name  | name attribute of SlaveComponent, traced from slaveComponentRef element of MasterSlaveBinding of the measurement target |
|    4 | MasterComponent name | name attribute of masterComponent, traced from masterComponentRef attribute of Accessor of the measurement target |
|    5 | Access Type name     | name attribute of AddressSpace, traced from accessTypeRef attribute of Performance of the measurement target |
|    6 | Latency (best)       | best value of measured Latency |
|    7 | Latency (worst)      | worst value of measured Latency |
|    8 | Latency (typical)    | typical value of measured Latency |
|    9 | Pitch (best)         | best value of measured Pitch |
|   10 | Pitch (worst)        | worst value of measured Pitch |
|   11 | Pitch (typical)      | typical value of measured Pitch |


In oreder to identify the elements correctly, it is necessary to describe the name attributes of the items 1 to 5 traced from SystemConfiguration in the form delimited  by '\_\_' (two underscores).

Ex.: set value of SubSpace name is  'AS_exclusive_support_global_address_area__SS_CRAM_Bank0_C0'

The above example is separated at the '\_\_' as below.

  - AS_exclusive_support_global_address_area
  - SS_CRAM_Bank0_C0

To search the SubSpace of this exmaple, first, search for
AddressSpace with the name attribute of 'AS_exclusive_support_global_address_area' by tracing from SystemConfiguration.
Then, search for the SubSpace with the name attribute of 'SS_CRAM_Bank0_C0' by tracing from that AddressSpace.

<img src="images/16.png" width="600">  
**figure16. MemoryPerformance Tab**

#### 5.1.5. <a name="instructioncycle-tab">InstructionCycle Tab</a>

Followings are the items that can be set in InstructionCycle Tab.

##### Process selection  
Specify an executing function.
There are two functions of Code generation and Measurement.

##### Input SHIM file
Specify the input SHIM file.

##### Output SHIM file
Specify the output SHIM file.

##### Overwrite the input SHIM file with measurement results
Specify to overwrite the input SHIM file with measurement results, if the checkbox is checked.

##### View the output SHIM file
Specify to display the SHIM file containing measurement results in IDE after the measurement, if the checkbox is checked.

##### Configuration directory
Specify the directory in which configuration files are stored.

In this directory, architecture configuration file (.arch) and common instruction set configuration file (.inst) are stored.

The file name of an architecture configuration file is required to be the same as the arch attribute of MasterComponent in the SHIM file, and the file name of a common instruction set configuration file is required to be the same as the name attribute of CommonInstructionSet in the SHIM file, except all the special symbols such as space and hyphen that is required to be replaced by underscores.

|Symbols required to be replaced|
|-------------------|
|\, /, :, &#8727;, ?, ", <, >, &#124;, -|


##### Command
Specify the command and script to execute measurement.
The the paths of generated code and CSV file to store the result
are specified using "${GenerateCodePath}" and "${ResultFilePath}", respectively, if necessary.

##### Generated code destination directory
Specify the destination directory of the measurement code.
If not specified, the code file will be located in a temporary directory in the project.

##### Result CSV file location
Specify the location of the measurement result CSV file.
If not specified, it will be located in a temporary directory in the project.

<img src="images/17.png" width="600">  
**figure17. InstructionCycle Tab**

### 5.2. <a name="launching-the-created-configuration-file">Launching the created configuration file</a>

To execute the created run configuration,
open the edit screen again and click "Run" button while the target project is selected, or select the run configuration from the drop-down menu on the toolbar as shown in the following graph while the target project is selected.

<img src="images/18.png" width="600">  
**figure18. Selection of launch configuration**

6. <a name="sample-project-details">Sample Project Details</a>
----------------

### 6.1. <a name="about-the-sample-project">About the sample project</a>
Followings are the two sample projects utilized by this tool.

  - sample project for IA32 (sample_IA32/, for Windows environment)
  - generic sample project (sample_generic/)

The sample project for IA32 is executable in the environment in which MinGW or Cygwin is installed, PATHs are set, and GNU Toolchain is available.

The generic sample project cannot be executed as is.
Simulator and evaluation board need to be prepared and the source code needs to be modified in accordance with them for the execution.

Here are the steps to use the sample project for IA32 in WindowsOS environment.  
Compile the sample project with GNU Toolchain.  
It is necessary to prepare an environment like MinGW in which the GNU Toolchain is available in Windows.  


### 6.2. <a name="directory-structure-ia32">Directory structure(IA-32)</a>
Following is the directory structure of the sample project (sample_IA32/).

- **data/**  --------  stores various data files used in measurement.
  - **InstructionCycle/**  --------  stores data for instruction cycle measurement.
    - **code/**  --------  stores source codes forbuild.
      + *sample_IA32_instruction_cycle.c*  --------  source code for instruction cycle measurement.
    - **conf/**  --------  stores input configuration files for code generation.
      + *IA_32.arch*  --------  architecture configuration file (for IA_32)
      + *LLVM_Instructions.inst*  --------  common instruction set configuration file (for LLVM Instructions)
    - **script/**  --------  stores the scripts for code generation and measurement.
      + *Makefile*  --------  Makefile to build measurement program.
      + *measurement.sh*  --------  script to execute measurement and to format the result.
  - **MemoryPerformance/**  --------  stores data for memory access performance measurement.
    - **code/**  --------  stores source codes for build.
      + *measurement.h* -------- sample for memory access performance measurement.
      + *mem_access.c* -------- sample for memory access performance measurement.
      + *mem_access.h* -------- sample for memory access performance measurement.
      + *mem_asminc.h* -------- sample for memory access performance measurement.
      + *sample_IA32_memory_perform.c*  --------  source code for memory access performance measurement.
    - **conf/**  --------  stores input configuration files for code generation.
      + *generic.cfg*  --------  generic configuration file for memory access performance measurement.
    - **script/**  --------  stores the scripts for build and measurement.
      + *Makefile*  --------  Makefile to build measurement program.
      + *measurement.sh*  --------  script to execute measurement and to format the results.
  + *jp.co.topscom.Intel_i5_3550.win7_sp1_64.LLVM3_4_CycCoarseMeasure.xml*  
    --------  SHIM file for executing a sample in IA32 environment.
- **Measurement/**  --------  work directory.(empty in the initial)
+ *sample_IA32_InstructionCycle.launch*  --------  run configuration file of instruction cycle measurement.
+ *sample_IA32_MemoryPerform.launch*  --------  run configuration file of memory access performance measurement.


### 6.3. <a name="directory-structure-generic">Directory structure(Generic)</a>
Following is the directory structure of sample project (sample_generic/).

- **data/**  --------  stores various data files used in measurement.
  - **InstructionCycle/**  --------  stores data for instruction cycle measurement.
    - **code/**  --------  stores source codes for build.
      + *sample_generic_instruction_cycle.c*  --------  source code for instruction cycle measurement.
      + *generic.h* -------- empty file.
      + *simulator.h* -------- empty file.
      + *sreg.h* -------- empty file.
      + *boot.S* -------- empty file.
      + *util.S* -------- empty file.
    - **conf/**  --------  stores input configuration files for code generation.
      + *GenericRISC_CPU.arch*  --------  architecture configuration file (for GenericRISC CPU)
      + *LLVM_Instructions.inst*  --------  common instruction set configuration file (for LLVM Instructions)
    - **script/**  --------  stores the scripts for build and measurement.
      + *dummy.csv* -------- file of result output to CSV.
      + *gdb.sh* -------- sample of shell script to start GDB.
      + *gdb-command.x* -------- sample of command file of GDB.
      + *Makefile*  --------  Makefile to build measurement program.
      + *measurement.sh*  --------  script to execute measurement and to format the result.
      + *OpCode.txt* -------- list of result set target instructions.
      + *run-simurator.sh* -------- sample of script to satrt simulator.
      + *sample.ld* -------- empty linker script file.
      + *SimulatorCycleToCsv.py* -------- sample of script to generate CSV file based on measurement result extracted from GDB or Etc.
  - **MemoryPerformance/**  --------  stores data for memory access performance measurement.
    - **code/**  --------  stores source codes for build.
      + *measurement.h* -------- sample for memory access performance measurement.
      + *mem_access.c* -------- sample for memory access performance measurement.
      + *mem_access.h* -------- sample for memory access performance measurement.
      + *mem_asminc.h* -------- sample for memory access performance measurement.
      + *sample_GenericRISC_CPU_memory_perform.c*  --------  source code for memory access performance measurement.
      + *simulator.h* -------- empty file.
      + *sreg.h* -------- empty file.
      + *boot.S* -------- empty file.
      + *util.S* -------- empty file.
    - **conf/**  --------  stores input configuration files for code generation.
      + *generic.cfg*  --------  generic configuration file for memory access performance measurement.
    - **script/**  --------  stores the scripts for build and measurement.
      + *dummy.csv* -------- file of result output to CSV.
      + *gdb.sh* -------- sample shell script to start GDB.
      + *gdb-command.x* -------- sample command file of GDB.
      + *Makefile* -------- Makefile to build measurement program.
      + *measurement.sh* -------- script to execute measurement and to format the result.
      + *run-simurator.sh* -------- sample script to start simulator.
      + *sample.ld* -------- empty linker script file.
      + *SimulatorResultToCsv.py* -------- sample script to generate CSV file based on measurement result extracted from GDB or Etc.
    + *sample.genericRISC_16core.0_0_0.unknown_Compliler.0_0_0.xml*  
--------  sample SHIM file for generic RISC CPU 16 core environment.
- **Measurement/**  --------  work directory.(empty in the initial)
+ *sample_generic_InstructionCycle.launch*  --------  run configuration file of instruction cycle measurement.
+ *sample_generic_MemoryPerform.launch*  --------  run configuration file of memory access performance measurement.

In the samples, there are some empty, incomplete, or modified files.
It is necessary for tool users to edit them to fit their runtime environments.

7. <a name="copyright">Copyright</a>
----------------
Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University

This product includes software developed by
The Apache Software Foundation (http://www.apache.org/).

Apache Commons CLI:  
Copyright 2001-2009 The Apache Software Foundation

Apache Commons Collections:  
Copyright 2001-2008 The Apache Software Foundation

Apache Commons Lang:  
Copyright 2001-2011 The Apache Software Foundation

Apache Velocity:  
Copyright (C) 2000-2007 The Apache Software Foundation

This product includes software developed by Ivan SZKIBA
(http://ini4j.sourceforge.net/).

ini4j:  
Copyright 2005,2009 Ivan SZKIBA

8. <a name="license">License</a>
----------------
```
The MIT License (MIT)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```
