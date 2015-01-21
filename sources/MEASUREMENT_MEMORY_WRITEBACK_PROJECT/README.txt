######################################################################################
#  How to set up a development environment for MEASUREMENT_MEMORY_WRITEBACK_PROJECT  #
######################################################################################

Note: In advance to set up the development environment of 'SHIM_API_PROJECT',
      please sure that you create a packaged JAR file the compilation results
      (named "SHIM_API.jar").
      If the same as creating location 'SHIM_API_PROJECT' of this project,
      at build time to 'SHIM_API.jar' will be created using
      the 'build.xml.sample'. In this case, step 5 below need not be performed.

1.Run Eclipse, and create a new java project named 'MEASUREMENT_MEMORY_WRITEBACK_PROJECT'.
  (If there is no 'src' folder, create 'src' folder as Source Folder.)

2.Copy the contents of 'src' in this folder to the project.

3.Create 'lib' folder as Folder.

4.Obtain the following libraries needed to build, and put in 'lib' folder.
  - Apache Commons CLI, v1.2, "commons-cli-1.2.jar"

  (*A) http://commons.apache.org/proper/commons-cli/

5.Copy 'SHIM_API.jar' that was created prior to 'lib' folder.

6.Set the project classpath referring to '.classpath.sample'.

7.If you want to build, create a build file reffering to 'build.xml.sample'.
  Edit it to the setting of the project accordingly.
