#####################################################################
#  How to set up a development environment for CCF_SAMPLE_PROJECT   #
#####################################################################

1.Run Eclipse, and create a new java project named 'CCF_SAMPLE_PROJECT'.
  (If there is no 'src' folder, create 'src' folder as Source Folder.)

2.Copy the contents of 'src' in this folder to the project.

3.Set the project classpath referring to '.classpath.sample'.
  Point to Notice:
    JARs in Eclipse plugin folder;
      org.eclipse.osgi_${version}.jar
      org.eclipse.core.commands_${version}.jar
      org.eclipse.equinox.common_${version}.jar
      org.eclipse.equinox.registry_${version}.jar
      org.eclipse.core.runtime_${version}.jar
      org.eclipse.text_${version}.jar
      org.eclipse.swt.${os}_${version}.jar
      org.eclipse.jface_${version}.jar
      org.eclipse.jface.text_${version}.jar
      org.eclipse.ui.workbench_${version}.jar
      org.eclipse.ui.forms_${version}.jar
      com.ibm.icu_${version}.jar
    others;
      swing2swt.jar
      swt32.jar (For building a windows 32-bit version)
      swt64.jar (For building a windows 64-bit version)
      swt_linux32.jar (For building a linux 32-bit version)
      swt_linux64.jar (For building a linux 64-bit version)


