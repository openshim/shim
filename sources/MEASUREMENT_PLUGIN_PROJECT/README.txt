############################################################################
#  How to set up a development environment for MEASUREMENT_PLUGIN_PROJECT  #
############################################################################

Note: In advance to set up the development environment of following project,
      please sure that you create the executable JAR files.

      - MEASUREMENT_CYCLE_CODEGEN_PROJECT
      - MEASUREMENT_CYCLE_WRITEBACK_PROJECT
      - MEASUREMENT_MEMORY_CODEGEN_PROJECT
      - MEASUREMENT_MEMORY_WRITEBACK_PROJECT

      If the above projects and 'SHIM_API_PROJECT' is located in a folder in
      the same level, automatically JAR files to build and can be placed
      in 'lib' folder by ANT build using the build.xml.
      In this case, step 10 below need not be performed.

1.Run Eclipse IDE(*A), and create a new Plug-in Project
  with the following parameters.

  (Wizard Page1)
    Project name: MEASUREMENT_PLUGIN_PROJECT
  (Wizard Page2)
    ID: org.multicore_association.measure.core
    Name: SHIM Performance Measurement Plug-in
    Vender: eSOL Co.,Ltd. and Nagoya University
    Activator: org.multicore_association.measure.core.Activator
    (check "Generate an activator, a Java class that control the plug-in's live cycle")

(*A): Must have the "Eclipse IDE for Java EE Developers" is either installed
      environment, or "Eclipse Plug-in Development Environment Package"
      has been installed environment.

2.Copy the contents of 'src' in this folder to the project.

3.If there is no 'icon' folder, create 'icon' folder as Folder,
  and copy the contents of 'icon' in this folder to the project.

4.Set 'META-INF/MANIFEST.MF' referring to 'META-INF/MANIFEST.MF.sample'.

5.Set 'build.properties' referring to 'build.properties.sample'.

6.Set or create 'plugin.xml' referring to 'plugin.xml.sample'.

7.Create 'res' folder as Folder.

8.Copy 'shim.xsd' to 'res' folder.

9.Create 'lib' folder as Folder.

10.Copy the following JAR files that was created prior to 'lib' folder.
  - MeasurementCycleCodegen.jar
  - MeasurementCycleWriteback.jar
  - MeasurementMemoryCodegen.jar
  - MeasurementMemoryWriteback.jar

11.Set the project classpath referring to '.classpath.sample'.

12.If you generate a plug-in, can be generated in the following steps.

  (1) From the Navigator or Project Explorer,
      right-click a project and select Export.

  (2) Select 'Deployable plug-ins and fragments' in 'Plug-in Development'.

  (3) Select a destination directory, and pressing 'Finish' button.
