###################################################################################
#  How to set up a development environment for MEASUREMENT_CYCLE_CODEGEN_PROJECT  #
###################################################################################

Note: In advance to set up the development environment of 'SHIM_API_PROJECT',
      please sure that you create a packaged JAR file the compilation results
      (named "SHIM_API.jar").
      If the same as creating location 'SHIM_API_PROJECT' of this project,
      at build time to 'SHIM_API.jar' will be created using
      the 'build.xml.sample'. In this case, step 5 below need not be performed.

1.Run Eclipse, and create a new java project named 'MEASUREMENT_CYCLE_CODEGEN_PROJECT'.
  (If there is no 'src' folder, create 'src' folder as Source Folder.)

2.Copy the contents of 'src' in this folder to the project.

3.Create 'lib' folder as Folder.

4.Obtain the following libraries needed to build, and put in 'lib' folder.
  - Apache Commons CLI, v1.2, "commons-cli-1.2.jar"
  - Apache Commons Collections, v3.2.1, "commons-collections-3.2.1.jar" (*B)
  - Apache Commons Lang, v2.6, "commons-lang-2.6.jar" (*C)
  - ini4j, v0.5.2, "ini4j-0.5.2.jar" (*D)
  - Apache Velocity, v1.7, "velocity-1.7.jar" (*E)

  (*A) http://commons.apache.org/proper/commons-cli/
  (*B) http://commons.apache.org/proper/commons-collections/
  (*C) http://commons.apache.org/proper/commons-lang/
  (*D) http://ini4j.sourceforge.net/
  (*E) http://velocity.apache.org/

5.Copy 'SHIM_API.jar' that was created prior to 'lib' folder.

6.Set the project classpath referring to '.classpath.sample'.

7.If you want to build, create a build file reffering to 'build.xml.sample'.
  Edit it to the setting of the project accordingly.
