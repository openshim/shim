/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.mem.generate;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import org.ini4j.InvalidFileFormatException;
import org.multicore_association.shim.api.AccessType;
import org.multicore_association.shim.api.AccessTypeSet;
import org.multicore_association.shim.api.Accessor;
import org.multicore_association.shim.api.AddressSpace;
import org.multicore_association.shim.api.AddressSpaceSet;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.MasterSlaveBinding;
import org.multicore_association.shim.api.MasterSlaveBindingSet;
import org.multicore_association.shim.api.MasterType;
import org.multicore_association.shim.api.Performance;
import org.multicore_association.shim.api.PerformanceSet;
import org.multicore_association.shim.api.RWType;
import org.multicore_association.shim.api.SlaveComponent;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.api.SystemConfiguration;

/**
 * Reading and analysis of a setting file, and generation of a source of a C language.
 */
public class MemCodeGen {

	private static final String separate = "__";

	private static final String PACKAGENAME = "org.multicore_association.shim.api";
	private static final String READ = "RW_READ";
	private static final String WRITE = "RW_WRITE";

	private static Preferences confPrefs = null;
	private static ConfigData confData = null;
	private static File conff = null;
	private static File shimf = null;
	private static ArrayList<CPU> cpuList = null;
	private static HashMap<String, Integer> cpuMap = null;
	private static ArrayList<ElementWrapper<AddressSpace>> addressSpaceList = null;
	private static ArrayList<SubSpaceWrapper> subSpaceList = null;
	private static ArrayList<ElementWrapper<SlaveComponent>> slaveComponentList = null;
	private static ArrayList<ElementWrapper<AccessType>> accessTypeList = null;
	private static HashMap<String, Integer> accessTypeMap = null;
	private static ArrayList<Integer> accessByteSizeList = null;
	private static TreeSet<Integer> accessByteSizeSet = null;
	private static int measureCountMax = 0;

	private static SystemConfiguration sysConf = null; //SHIM Root Element

	private static int cpu_idx = 0;
	private static int at_idx = 0;

	private static String shimSchemaPath = "";
	private static List<String> parseErrList = new ArrayList<String>();

	/**
	 * Load of configuration file.
	 * @param f
	 * @return
	 * @throws InvalidFileFormatException
	 * @throws IOException
	 */
	private Preferences loadConfigFile(File f)
			throws InvalidFileFormatException, IOException {
		Ini config = null;
		Preferences prefs = null;

		config = new Ini(f);
		prefs = new IniPreferences(config);

		return prefs;
	}

	/**
	 * Check of the section presence.
	 * @param prefs read configulation data
	 * @param section section name
	 * @return Flag of presense
	 */
	private boolean checkPrefSection(Preferences prefs, String section) {
		boolean ret = false;
		try {
			ret = prefs.nodeExists(section);
		} catch (Exception e) {
			ret = false;
		}
		return ret;
	}

	/**
	 * Check of the entry presense.
	 * @param prefs read configuration data
	 * @param section section name
	 * @param entry entry name
	 * @return Flag of presense
	 */
	private boolean checkPrefEntry(Preferences prefs, String section,
			String entry) {
		boolean ret = false;
		String buf;
		Preferences s = null;
		try {
			s = prefs.node(section);
			buf = s.get(entry, null);
			if (buf != null) {
				ret = true;
			}
		} catch (Exception e) {
			ret = false;
		}

		return ret;
	}

	/**
	 * Parsing configuration.
	 * @param prefs read configuration data
	 * @return Flag for success judgements
	 * @throws BackingStoreException
	 */
	private boolean parseConfigData(Preferences prefs)
			throws BackingStoreException {

		/* check of the section presence */
		ArrayList<String> seclist = ConfigData.getSectionNameList();
		boolean result = true;

		for (Iterator<String> i = seclist.iterator(); i.hasNext();) {
			String name = i.next();
			if (!checkPrefSection(prefs, name)) {
				System.err.println("Error: mandatory section does not exist"
						+ " (" + name + ")");  //$NON-NLS-2$
				result = false;
			}
		}

		/* check of the entry presence */
		ArrayList<String> mentrylist = ConfigData.getMainEntryNameList();
		for (Iterator<String> i = mentrylist.iterator(); i.hasNext();) {
			String name = i.next();
			if (!checkPrefEntry(prefs, ConfigData.SECTION_MAIN, name)) {
				System.err.println("Error: mandatory entry does not exist"
						+ " (" + name + ")");  //$NON-NLS-2$
				result = false;
			}
		}

		/* set configuration data */
		confData = new ConfigData();
		Preferences sec_main = prefs.node(ConfigData.SECTION_MAIN);

		confData.setMainInclude(sec_main.get(
				ConfigData.ENTRY_INCLUDE, ""));
		confData.setMainGlobalValiable(sec_main.get(
				ConfigData.ENTRY_GLOBAL_VARIABLE, ""));
		confData.setMainMacro(sec_main.get(
				ConfigData.ENTRY_MACRO, ""));

		return result;
	}

	/**
	 * Configuration file reading.
	 * @return Flag for success judgements
	 */
	private boolean parseConfig() {
		try {
			confPrefs = loadConfigFile(conff);
		} catch (InvalidFileFormatException e) {
			System.err.println(e.getMessage());
			System.err.println("Error: failed to parse the configuration file");
			return false;
		} catch (IOException e) {
			System.err.println("Error: exception occurs in the file input or output");
			return false;
		}

		try {
			if (!parseConfigData(confPrefs)) {
				System.err.println("Error: failed to parse the configuration file");
				return false;
			}
		} catch (BackingStoreException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Output of a C language source based on read setting data.
	 * @return Flag for success judgements
	 */
	private boolean dumpCSource() {
		try {

			Properties p = new Properties();
			p.setProperty("resource.loader", "class");
			p.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			p.setProperty("input.encoding", "UTF-8");

			Velocity.init(p);
			Velocity.setProperty("file.resource.loader.path", "/");
			VelocityContext context = new VelocityContext();
			context.put("confData", confData);
			context.put("measureCountMax", measureCountMax);
			context.put("addressSpaceList", addressSpaceList);
			context.put("subSpaceList", subSpaceList);
			context.put("slaveComponentList", slaveComponentList);
			context.put("cpuList", cpuList);
			context.put("accessTypeList", accessTypeList);
			context.put("accessByteSizeList", accessByteSizeList);

			StringWriter writer = new StringWriter();

			String tempFile = "template.vm";
			Template template = Velocity.getTemplate(tempFile);

			template.merge(context, writer);
			System.out.println(writer.toString());
			writer.flush();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
			System.err.println("Error: template file is not found");
			return false;
		} catch (MethodInvocationException e) {
			e.printStackTrace();
			System.err.println("Error: syntax error occurs in the template file");
			return false;
		} catch (ParseErrorException e) {
			e.printStackTrace();
			System.err.println("Error: syntax error occurs in the template file");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error: other error");
			return false;
		}

		return true;
	}

	private String createPathName(List<String> route) {
		StringBuilder sb = new StringBuilder("");
		if (route == null) {
			return "";
		}

		for (Iterator<String> i = route.iterator(); i.hasNext();) {
			sb.append(i.next());
			if (i.hasNext()) {
				sb.append(separate);
			}
		}

		return sb.toString();
	}


	/**
	 * make cpuList(name only) and accessTypeList by recursively access to ComponentSet
	 * @return Flag for success judgements
	 */
	private void makeCPUListFromComponentSet(ComponentSet cs, List<String> route) {

		if (cs.getMasterComponent().size() != 0) {
			List<MasterComponent> mcList = cs.getMasterComponent();
			for (Iterator<MasterComponent> i = mcList.iterator(); i.hasNext();) {
				MasterComponent mc = i.next();
				//				if(mc.getMasterType() == MasterType.CPU)
				if (mc.getMasterType() == MasterType.PU) {
					CPU cpu = new CPU();

					route.add(mc.getName());
					String mcPath = createPathName(route);
					cpu.setName(mcPath);
					cpu.setMasterComponent(mc);
					cpuList.add(cpu);
					cpuMap.put(cpu.getName(), cpu_idx);
					cpu_idx++;


					//System.out.println("MasterComponent" + " : " + mc.getName());
					AccessTypeSet accsessTypeSet = mc.getAccessTypeSet();
					if (accsessTypeSet.getAccessType().size() == 0) {
						route.remove(mc.getName());
						continue;
					}

					List<AccessType> atList = accsessTypeSet.getAccessType();
					for (Iterator<AccessType> j = atList.iterator(); j.hasNext();) {
						boolean contains = false;
						AccessType at = j.next();

						if (at.getRwType() == RWType.X) {
							continue;
						}

						route.add(at.getName());
						String atPath = createPathName(route);
						route.remove(at.getName());
						//duplicate check
						for (Iterator<ElementWrapper<AccessType>> k = accessTypeList.iterator(); k.hasNext();) {
							ElementWrapper<AccessType> kpath = k.next();
							if (atPath.equals(kpath.getName())) {
								contains = true;
								break;
							}
						}
						if (contains) {
							continue;
						}

						ElementWrapper<AccessType> ew = new ElementWrapper<AccessType>();
						ew.setName(atPath);
						ew.setElement(at);
						accessTypeList.add(ew);
						//System.out.println("\t" + at_idx  + " : " + at.getName());
						accessTypeMap.put(atPath, at_idx);
						accessByteSizeSet.add(at.getAccessByteSize());
						at_idx++;
					}
					route.remove(mc.getName());
				}
			}
		}

		if (cs.getComponentSet().size() != 0) {
			List<ComponentSet> csList = cs.getComponentSet();
			for (Iterator<ComponentSet> i = csList.iterator(); i.hasNext();) {
				ComponentSet compSet = i.next();
				route.add(compSet.getName());
				makeCPUListFromComponentSet(compSet, route);
				route.remove(compSet.getName());
			}
		}
	}

	/**
	 * make cpuList(name only) and accessTypeList, accessByteSizeList from SHIM file.
	 * @return Flag for success judgements
	 */
	private boolean makeCPUListFromShim() {
		ComponentSet compSet = sysConf.getComponentSet();
		if (compSet == null) {
			System.err.println("Error: mandatory element does not exist"
					+ " (ComponentSet)");
			return false;
		}

		cpuList = new ArrayList<CPU>();
		cpuMap = new HashMap<String, Integer>();
		accessTypeList = new ArrayList<ElementWrapper<AccessType>>();
		accessTypeMap = new HashMap<String, Integer>();
		accessByteSizeSet = new TreeSet<Integer>();
		cpu_idx = 0;
		at_idx = 0;

		ArrayList<String> route = new ArrayList<String>();
//		route.add(sysConf.getName());
		route.add(compSet.getName());

		makeCPUListFromComponentSet(compSet, route);

		if (cpuList.size() == 0) {
			System.err.println("Error: mandatory element does not exist"
					+ " (MasterComponent(masterType:CPU)");
			return false;
		}

		if (accessTypeList.size() == 0) {
			System.err.println("Error: mandatory element does not exist"
					+ " (AccessType)");
			return false;
		}

		accessByteSizeList = new ArrayList<Integer>(accessByteSizeSet);
		return true;
	}

	/**
	 * make slaveComponentList by recursively access to ComponentSet
	 * @return Flag for success judgements
	 */
	private void makeSlaveListFromComponentSet(ComponentSet cs, List<String> route) {

		if (cs.getSlaveComponent().size() != 0) {
			List<SlaveComponent> scList = cs.getSlaveComponent();
			for (Iterator<SlaveComponent> i = scList.iterator(); i.hasNext();) {
				SlaveComponent sc = i.next();

				route.add(sc.getName());
				String scPath = createPathName(route);

				ElementWrapper<SlaveComponent> ew = new ElementWrapper<SlaveComponent>();
				ew.setName(scPath);
				ew.setElement(sc);
				slaveComponentList.add(ew);

				route.remove(sc.getName());
				//System.out.println("SlaveComponent" + " : " + sc.getName());
			}
		}

		if (cs.getComponentSet().size() != 0) {
			List<ComponentSet> csList = cs.getComponentSet();
			for (Iterator<ComponentSet> i = csList.iterator(); i.hasNext();) {
				ComponentSet compSet = i.next();
				route.add(compSet.getName());
				makeSlaveListFromComponentSet(compSet, route);
				route.remove(compSet.getName());
			}
		}
	}

	/**
	 * make slaveComponentList from SHIM file.
	 * @return Flag for success judgements
	 */
	private boolean makeSlaveListFromShim() {
		ComponentSet compSet = sysConf.getComponentSet();
		if (compSet == null) {
			System.err.println("Error: mandatory element does not exist"
					+ " (ComponentSet)");
			return false;
		}

		slaveComponentList = new ArrayList<ElementWrapper<SlaveComponent>>();

		ArrayList<String> route = new ArrayList<String>();
//		route.add(sysConf.getName());
		route.add(compSet.getName());

		makeSlaveListFromComponentSet(compSet, route);

		if (slaveComponentList.size() == 0) {
			System.err.println("Error: mandatory element does not exist"
					+ " (SlaveComponent)");
			return false;
		}

		return true;
	}

	/**
	 * make addressSpaceList and subSpaceList from SHIM file.
	 * @return Flag for success judgements
	 */
	private boolean makeAddressSpaceListFromShim() {
		AddressSpaceSet asSet = sysConf.getAddressSpaceSet();
		if (asSet == null) {
			System.err.println("Error: mandatory element does not exist"
					+ " (AddressSpaceSet)");
			return false;
		}

		if (asSet.getAddressSpace().size() == 0) {
			System.err.println("Error: mandatory element does not exist"
					+ " (AddressSpace)");
			return false;
		}

		addressSpaceList = new ArrayList<ElementWrapper<AddressSpace>>();
		subSpaceList = new ArrayList<SubSpaceWrapper>();

		List<AddressSpace> asList = asSet.getAddressSpace();
		for (Iterator<AddressSpace> i = asList.iterator(); i.hasNext();) {
			AddressSpace as = i.next();
			ElementWrapper<AddressSpace> ew = new ElementWrapper<AddressSpace>();

			ArrayList<String> route = new ArrayList<String>();
			route.add(as.getName());
			ew.setElement(as);
			ew.setName(createPathName(route));

			addressSpaceList.add(ew);

			if (as.getSubSpace().size() == 0) {
				continue;
			}

			List<SubSpace> ssList = as.getSubSpace();
			for (Iterator<SubSpace> j = ssList.iterator(); j.hasNext();) {
				SubSpace ss = j.next();

				route.add(ss.getName());
				SubSpaceWrapper sw = new SubSpaceWrapper(ss);
				sw.setName(createPathName(route));
				subSpaceList.add(sw);
				route.remove(ss.getName());
			}
		}

		if (subSpaceList.size() == 0) {
			System.err.println("Error: mandatory element does not exist"
					+ " (SubSpace)");
			return false;
		}

		return true;
	}

	/**
	 * make accessPatternList from SHIM file.
	 * @return Flag for success judgements
	 */
	private boolean makeAccessPatternListFromShim() {
		AddressSpaceSet asSet = sysConf.getAddressSpaceSet();
		if (asSet == null) {
			System.err.println("Error: mandatory element does not exist"
					+ " (AddressSpaceSet)");
			return false;
		}

		if (asSet.getAddressSpace().size() == 0) {
			System.err.println("Error: mandatory element does not exist"
					+ " (AddressSpace)");
			return false;
		}

		List<AddressSpace> asList = asSet.getAddressSpace();
		for (Iterator<AddressSpace> i = asList.iterator(); i.hasNext();) {
			AddressSpace as = i.next();

			if (as.getSubSpace().size() == 0) {
				continue;
			}

			String asPath = null;
			for (Iterator<ElementWrapper<AddressSpace>> j = addressSpaceList.iterator(); j.hasNext();) {
				ElementWrapper<AddressSpace> ew = j.next();
				if (ew.getElement().equals(as)) {
					asPath = ew.getName();
					break;
				}
			}


			//System.out.println(as.getName());
			List<SubSpace> ssList = as.getSubSpace();
			for (Iterator<SubSpace> j = ssList.iterator(); j.hasNext();) {
				SubSpace ss = j.next();

				String ssPath = null;
				for (Iterator<SubSpaceWrapper> k = subSpaceList.iterator(); k.hasNext();) {
					SubSpaceWrapper sw = k.next();
					if (sw.getSubSpace().equals(ss)) {
						ssPath = sw.getName();
						break;
					}
				}


				MasterSlaveBindingSet msbs = ss.getMasterSlaveBindingSet();
				if (msbs == null) {
					continue;
				}

				List<MasterSlaveBinding> msbList = msbs.getMasterSlaveBinding();
				if (msbList.size() == 0) {
					continue;
				}

				//System.out.println(" " + ss.getName());
				for (Iterator<MasterSlaveBinding> k = msbList.iterator(); k.hasNext();) {
					MasterSlaveBinding msb = k.next();

					//System.out.println("  " + msb.getSlaveComponentRef());
					List<Accessor> acList = msb.getAccessor();
					for (Iterator<Accessor> l = acList.iterator(); l.hasNext();) {
						Accessor ac = l.next();

						MasterComponent mc = (MasterComponent)ac.getMasterComponentRef();

						ArrayList<String> route = new ArrayList<String>();
						route.add(sysConf.getComponentSet().getName());
//						String mcPath = searchMasterComponentPath(mc, sysConf.getComponentSet(), route);

						String mcPath = null;
						for (Iterator<CPU> m = cpuList.iterator(); m.hasNext();) {
							CPU cpu = m.next();
							if (mc.getId().equals(cpu.getMasterComponent().getId())) {
								mcPath = cpu.getName();
								break;
							}
						}


						if (!cpuMap.containsKey(mcPath)) {
							continue;
						}

						if (ac.getPerformanceSet().size() == 0) {
							continue;
						}

						//System.out.println("   " + ac.getMasterComponentRef());
						List<PerformanceSet> pfsList = ac.getPerformanceSet();
						for (Iterator<PerformanceSet> m = pfsList.iterator(); m.hasNext();) {
							PerformanceSet pfs = m.next();

							if (pfs.getPerformance().size() == 0) {
								continue;
							}

							List<Performance> pfList = pfs.getPerformance();
							for (Iterator<Performance> n = pfList.iterator(); n.hasNext();) {
								Performance pf = n.next();

								//System.out.println("     " + pf.getAccessTypeRef());

								AccessType at = (AccessType)pf.getAccessTypeRef();

								if (at == null) {
									continue;
								}

//								String atPath = searchAccessTypePath(at, sysConf.getComponentSet());
								String atPath = null;
								for (Iterator<ElementWrapper<AccessType>> o = accessTypeList.iterator(); o.hasNext();) {
									ElementWrapper<AccessType> ew = o.next();
									if (at.getId().equals(ew.getElement().getId())) {
										atPath = ew.getName();
										break;
									}
								}

								if ((at.getRwType() == RWType.R) || (at.getRwType() == RWType.RW)
										|| (at.getRwType() == RWType.RWX) || (at.getRwType() == RWType.RX)) {

									SlaveComponent scRef = (SlaveComponent)msb.getSlaveComponentRef();
									String scPath = null;
									for (Iterator<ElementWrapper<SlaveComponent>> s = slaveComponentList.iterator();
											s.hasNext();) {
										ElementWrapper<SlaveComponent> ew = s.next();

										if (scRef.getId().equals(ew.getElement().getId())) {
											scPath = ew.getName();
										}
									}

//									System.out.println(scRef.getId());

									AccessPattern accessPattern = new AccessPattern();
									accessPattern.setAddressSpace(asPath);
									accessPattern.setSubSpace(ssPath);
//									accessPattern.setSlaveComponent(msb.getSlaveComponentRef());
//									accessPattern.setMode(mode);
									accessPattern.setSlaveComponent(scPath);
//									accessPattern.setAccessType(tmp.getName());
									accessPattern.setAccessType(atPath);
									accessPattern.setAccessRW(READ);
									accessPattern.setAccessByteSize(at.getAccessByteSize().toString());
									MasterComponent mc2 = (MasterComponent)ac.getMasterComponentRef();

//									String refMcPath = searchMasterComponentPath(mc2, sysConf.getComponentSet());
									String refMcPath = null;
									for (Iterator<CPU> o = cpuList.iterator(); o.hasNext();) {
										CPU cpu = o.next();
										if (mc2.getId().equals(cpu.getMasterComponent().getId())) {
											refMcPath = cpu.getName();
											break;
										}
									}

									cpuList.get(cpuMap.get(refMcPath)).addAccessPattern(accessPattern);
								}

								if ((at.getRwType() == RWType.W) || (at.getRwType() == RWType.RW)
										|| (at.getRwType() == RWType.RWX)) {

									SlaveComponent scRef = (SlaveComponent)msb.getSlaveComponentRef();
									String scPath = null;
									for (Iterator<ElementWrapper<SlaveComponent>> s = slaveComponentList.iterator();
											s.hasNext();) {
										ElementWrapper<SlaveComponent> ew = s.next();

										if (scRef.getId().equals(ew.getElement().getId())) {
											scPath = ew.getName();
										}
									}

									AccessPattern accessPattern = new AccessPattern();
									accessPattern.setAddressSpace(asPath);
									accessPattern.setSubSpace(ssPath);
//									accessPattern.setSlaveComponent(msb.getSlaveComponentRef());
//									accessPattern.setMode(mode);
									accessPattern.setSlaveComponent(scPath);
//									accessPattern.setAccessType(tmp.getName());
									accessPattern.setAccessType(atPath);
									accessPattern.setAccessRW(WRITE);
									accessPattern.setAccessByteSize(at.getAccessByteSize().toString());
									MasterComponent mc2 = (MasterComponent)ac.getMasterComponentRef();

//									String refMcPath = searchMasterComponentPath(mc2, sysConf.getComponentSet());
									String refMcPath = null;
									for (Iterator<CPU> o = cpuList.iterator(); o.hasNext();) {
										CPU cpu = o.next();
										if (mc2.getId().equals(cpu.getMasterComponent().getId())) {
											refMcPath = cpu.getName();
											break;
										}
									}

									cpuList.get(cpuMap.get(refMcPath)).addAccessPattern(accessPattern);
								}
							}
						}
					}
				}
			}
		}

		for (Iterator<CPU> i = cpuList.iterator(); i.hasNext();) {
			CPU cpu = i.next();
			if (measureCountMax < cpu.getAccessPatternList().size() * 3) {
				measureCountMax = cpu.getAccessPatternList().size() * 3;
			}
		}

		return true;
	}

	/**
	 * Get the data which need to make CSource from SHIM file.
	 * @return Flag for success judgements
	 */
	private boolean getDataFromShim() {
		boolean ch = true;

		try {
			JAXBContext context = JAXBContext.newInstance(PACKAGENAME);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			/* validation check setup */
			if (!shimSchemaPath.equals("")) {
				SchemaFactory sf = SchemaFactory.newInstance(
						javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = sf.newSchema(new File(shimSchemaPath));
				unmarshaller.setSchema(schema);
				unmarshaller.setEventHandler(new ValidationEventHandler() {
					@Override
					public boolean handleEvent(ValidationEvent event) {
						if ((event.getSeverity() == ValidationEvent.FATAL_ERROR)
								|| (event.getSeverity() == ValidationEvent.ERROR)) {
							ValidationEventLocator loc = event.getLocator();
							parseErrList.add("    Line[" + loc.getLineNumber() + "] : " + event.getMessage());
						}
						return true;
					}
				});
			}

			sysConf = unmarshaller.unmarshal(new StreamSource(shimf),
					SystemConfiguration.class).getValue();

			if (parseErrList.size() > 0) {
				System.err.println("Error: input SHIM file validation error");

				System.err.println("    Validation error location:");
				for (int i = 0; i < parseErrList.size(); i++) {
					System.err.println(parseErrList.get(i));
				}
				return false;
			}

//			JAXBElement<SystemConfiguration> root = unmarshaller.unmarshal(new StreamSource(shimf), SystemConfiguration.class);
//			sysConf = root.getValue();
		} catch (Exception e) {
			System.err.println("Error: failed to parse the SHIM file, please check the contents of the file");
			return false;
		}

		ch = makeCPUListFromShim();
		if (!ch) {
			return ch;
		}
		ch = makeSlaveListFromShim();
		if (!ch) {
			return ch;
		}

		ch = makeAddressSpaceListFromShim();
		if (!ch) {
			return ch;
		}

		ch = makeAccessPatternListFromShim();
		if (!ch) {
			return ch;
		}

		return ch;
	}

	/**
	 * Check the command line parameters.
	 * @param args command line parameters
	 * @return Flag for success judgements
	 */
	@SuppressWarnings("static-access")
	private boolean checkParam(String[] args) {
		boolean ch = true;

		Option optShimSchema = OptionBuilder.
				withLongOpt("shim-schema").
				withDescription("SHIM XML schema file path").
				hasArg().
				withArgName("path").
				create();

		Options opts = new Options();
		opts.addOption(optShimSchema);

		PosixParser parser = new PosixParser();
		CommandLine cl = null;

		try {
			cl = parser.parse(opts, args);

			if (cl.hasOption("shim-schema")) {
				shimSchemaPath = cl.getOptionValue("shim-schema");
			}

			if (cl.getArgs().length != 2) {
				System.err.println("java -jar MemCodeGen.jar [opts] config_file_path shim_config_file_path");
				ch = false;
			} else {
				conff = new File(cl.getArgs()[0]);
				if (!conff.exists()) {
					System.err.println("Error: Config file(.cfg) does not exist");
					ch = false;
				}

				shimf = new File(cl.getArgs()[1]);
				if (!shimf.exists()) {
					System.err.println("Error: SHIM file(.xml) does not exist");
					ch = false;
				}
			}

			if (!shimSchemaPath.equals("")) {
				File temp = new File(shimSchemaPath);
				if (!temp.exists()) {
					System.err.println("Error: SHIM schema file(.xsd) does not exist");
					ch = false;
				}
			}

			if (!ch) {
				HelpFormatter hf = new HelpFormatter();
				hf.printHelp("java -jar MeasurementMemoryCodegen.jar [opts] config_file_path shim_file_path", opts);
			}
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp("java -jar MeasurementMemoryCodegen.jar [opts] config_file_path shim_file_path", opts);
			ch = false;
		}

		return ch;
	}

	public boolean generate(String args[]) {

		boolean ch = true;

		ch = checkParam(args);
		if (!ch) {
			return false;
		}

		if (shimf != null) {
			ch = getDataFromShim();
			if (!ch) {
				return false;
			}
		}

		ch = parseConfig();
		if (!ch) {
			return false;
		}

		dumpCSource();

		return true;
	}

	/**
	 * main
	 *
	 * @param args Commandline arguments
	 */
	public static void main(String args[]) {
		MemCodeGen gen = new MemCodeGen();
		boolean ret = gen.generate(args);
		if (!ret) {
			System.exit(1);
		}
		System.exit(0);
	}

}
