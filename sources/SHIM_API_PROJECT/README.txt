##################################################################
#  How to set up a development environment for SHIM_API_PROJECT  #
##################################################################

1.Generate jaxb classes from 'shim.xsd'.

(1) Run Eclipse, and create a new java project named 'SHIM_API_PROJECT'.

(2) From the Navigator or Project Explorer, right-click a schema ( .xsd file) and select Generate > JAXB Classes. The Generate Classes from Schema dialog appears.
 Configure the following contents in the JAXB Class Generation dialog.
 
 Source folder: SHIM_API_PROJECT/src
 Package:       org.multicore_association.shim.api

(3) Click finish.



2. Add @XmlRootElement to 'org.multicore_association.shim.api.SystemConfiguration'.

example:

@XmlRootElement(name="SystemConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SystemConfiguration", propOrder = {
    "componentSet",
    "communicationSet",
    "addressSpaceSet",
    "clockFrequency"
})
public class SystemConfiguration {

.
.
.

Point to notice:
If the following error happend, download eclipse link MOXy from
'https://eclipse.org/eclipselink/moxy.php' and add JARs to the classpath.

  (1) Error
  java.lang.NoClassDefFoundError: com/sun/tools/xjc/XJCFacade
  
  (2) JARs
  eclipselink/jlib/moxy/com.sun.tools.xjc_2.2.0.jar
  eclipselink/jlib/moxy/com.sun.xml.bind_2.2.0.v201004141950.jar

