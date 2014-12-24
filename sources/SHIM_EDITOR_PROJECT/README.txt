#####################################################################
#  How to set up a development environment for SHIM_EDITOR_PROJECT  #
#####################################################################

1.Run Eclipse, and create a new java project named 'SHIM_EDITOR_PROJECT'.
  (If there is no 'src' folder, create 'src' folder as Source Folder.)

2.Create 'resources' folder as Source Folder.

3.Copy the contents of 'src' and 'resources' in this folder to the project.

4.Set the project classpath referring to '.classpath.sample'.
  Point to Notice:
    JARs in Eclipse plugin folder;
      org.eclipse.osgi_${version}.jar
      org.eclipse.core.commands_${version}.jar
      org.eclipse.equinox.common_${version}.jar
      org.eclipse.equinox.registry_${version}.jar
      org.eclipse.core.runtime_${version}.jar
      org.eclipse.text_${version}.jar
      org.eclipse.jface_${version}.jar
      org.eclipse.jface.text_${version}.jar
      org.eclipse.ui.workbench_${version}.jar
      org.eclipse.ui.forms_${version}.jar
      org.eclipse.core.databinding_${version}.jar
      org.eclipse.core.databinding.beans_${version}.jar
      org.eclipse.core.databinding.observable_${version}.jar
      org.eclipse.core.databinding.property_${version}.jar
      org.eclipse.jface.databinding_${version}.jar
      com.ibm.icu_${version}.jar
    others;
      swing2swt.jar
      swt32.jar (For building a windows 32-bit version)
      swt64.jar (For building a windows 64-bit version)
      swt_linux32.jar (For building a linux 32-bit version)
      swt_linux64.jar (For building a linux 64-bit version)

5.If you want to build, create a build file reffering to 'ShimEdit_BinaryBuild.xml.sample'.
  Edit it to the setting of the project accordingly.

