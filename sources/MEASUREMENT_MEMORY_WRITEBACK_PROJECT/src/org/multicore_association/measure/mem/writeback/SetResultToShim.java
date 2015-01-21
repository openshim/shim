/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.mem.writeback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.namespace.QName;
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
import org.multicore_association.measure.mem.writeback.data.MeasurementsCSVData;
import org.multicore_association.shim.api.AccessType;
import org.multicore_association.shim.api.Accessor;
import org.multicore_association.shim.api.AddressSpace;
import org.multicore_association.shim.api.AddressSpaceSet;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.Latency;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.MasterSlaveBinding;
import org.multicore_association.shim.api.MasterSlaveBindingSet;
import org.multicore_association.shim.api.Performance;
import org.multicore_association.shim.api.PerformanceSet;
import org.multicore_association.shim.api.Pitch;
import org.multicore_association.shim.api.SlaveComponent;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.api.SystemConfiguration;
import org.xml.sax.SAXException;

/**
 * Reads the CSV file, and sets the value of Latency and Pitch to SHIM file.
 *
 */
public class SetResultToShim {

	private static final String separate = "__";
	private static File csvf = null;
	private static File shimf = null;
	private static ArrayList<MeasurementsCSVData> measureList = null;

	private static Map<String, MasterComponent> masterComponentMap = null;
	private static Map<String, SlaveComponent> slaveComponentMap = null;
	private static Map<String, AccessType> accessTypeMap = null;

	private static String shimSchemaPath = "";

	private static List<String> parseErrList = new ArrayList<String>();


	private static String createPathName(List<String> route) {
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
	 * make slaveComponentList by recursively access to ComponentSet
	 * @return Flag for success judgements
	 */
	private static void makeRefMapFromComponentSet(ComponentSet cs, List<String> route) {

		if (cs.getMasterComponent().size() != 0) {
			List<MasterComponent> mcList = cs.getMasterComponent();
			for (Iterator<MasterComponent> i = mcList.iterator(); i.hasNext();) {

				MasterComponent mc = i.next();
				route.add(mc.getName());
				String mcPath = createPathName(route);
				masterComponentMap.put(mcPath, mc);

				List<AccessType> atList = mc.getAccessTypeSet().getAccessType();
				for (Iterator<AccessType> j = atList.iterator(); j.hasNext();) {
					AccessType at = j.next();
					route.add(at.getName());
					String atPath = createPathName(route);
					accessTypeMap.put(atPath, at);
					route.remove(at.getName());
				}

				route.remove(mc.getName());
			}
		}


		if (cs.getSlaveComponent().size() != 0) {
			List<SlaveComponent> scList = cs.getSlaveComponent();
			for (Iterator<SlaveComponent> i = scList.iterator(); i.hasNext();) {
				SlaveComponent sc = i.next();
				route.add(sc.getName());
				String scPath = createPathName(route);
				slaveComponentMap.put(scPath, sc);
				route.remove(sc.getName());
			}
		}

		if (cs.getComponentSet().size() != 0) {
			List<ComponentSet> csList = cs.getComponentSet();
			for (Iterator<ComponentSet> i = csList.iterator(); i.hasNext();) {
				ComponentSet compSet = i.next();
				route.add(compSet.getName());
				makeRefMapFromComponentSet(compSet, route);
				route.remove(compSet.getName());
			}
		}
	}



	/**
	 * Check the command line parameters.
	 * @param args command line parameters
	 */
	@SuppressWarnings("static-access")
	private static boolean checkParam(String[] args) {
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

			if (cl.getArgs().length == 2) {
				csvf = new File(cl.getArgs()[0]);
				if (!csvf.exists()) {
					System.err.println("Error: measurements file(.csv) does not exist");
					ch = false;
				}

				shimf = new File(cl.getArgs()[1]);
				if (!shimf.exists()) {
					System.err.println("Error: SHIM file(.xml) does not exist");
					ch = false;
				}
			} else if (cl.getArgs().length == 1) {
				csvf = new File(cl.getArgs()[0]);
				if (!csvf.exists()) {
					System.err.println("Error: measurements file(.csv) does not exist");
					ch = false;
				}
			} else {
				System.err.println("Error: too few arguments");
				ch = false;
			}

			if (!shimSchemaPath.equals("")) {
				File temp = new File(shimSchemaPath);
				if (!temp.exists()) {
					System.err.println("Error: SHIM schema file(.xsd) does not exist");
					ch = false;
				}
			}

			if (!ch) {
				HelpFormatter hf=new HelpFormatter();
				hf.printHelp("java -jar MeasurementMemoryWriteback.jar [opts] measurements_file_path shim_file_path", opts);
			}

		} catch (ParseException e) {
			System.err.println(e.getMessage());
			HelpFormatter hf=new HelpFormatter();
			hf.printHelp("java -jar MeasurementMemoryWriteback.jar [opts] measurements_file_path shim_file_path", opts);
			ch = false;
		}

		return ch;
	}


		/**
	 * To be stored in the SHIM instance by reading the CSV file.
	 * @return Flag for success judgements
	 * @throws IOException
	 */
	private static boolean readCsvFile(String path) {
		/* file reading */
		CSVParser parser = new CSVParser();
		if (!parser.parse(csvf.getPath())) {
			System.err.println("Error: failed to load measurements file(.csv)");
			return false;
		}

		parser.parse(path);

		measureList = parser.getResult();

		return true;
	}

	/**
	 * Set the value to read the existing SHIM file.
	 * @return Flag for success judgements
	 * @throws ShimFileFormatException
	 * @throws ShimFileGenerateException
	 */
	private static boolean appendToExistingShim() {
		/*
		 * append to existing llvm-shim
		 */
		SystemConfiguration sysConf = null;

		try {
			JAXBContext context =
					JAXBContext.newInstance(SystemConfiguration.class);
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
		} catch (Exception e) {
			System.err.println("Error: failed to parse the SHIM file, please check the contents of the file");
			e.printStackTrace();
			return false;
		}

		AddressSpaceSet addrSpaceSet = sysConf.getAddressSpaceSet();
		if (addrSpaceSet == null) {
			System.err.println("Error: failed to parse the SHIM file, please check the contents of the file");
			return false;
		}

		List<AddressSpace> addrSpaceList = addrSpaceSet.getAddressSpace();
		if (addrSpaceList == null) {
			System.err.println("Error: failed to parse the SHIM file, please check the contents of the file");
			return false;
		}

		masterComponentMap = new HashMap<String, MasterComponent>();
		slaveComponentMap = new HashMap<String, SlaveComponent>();
		accessTypeMap = new HashMap<String, AccessType>();

		List<String> route = new ArrayList<String>();
		ComponentSet cs = sysConf.getComponentSet();
		route.add(cs.getName());

		makeRefMapFromComponentSet(cs, route);



		for (int i = 0; i < measureList.size(); i++) {
			MeasurementsCSVData data = measureList.get(i);

			route.clear();

			/*
			 * search AddressSpaceName
			 */
			AddressSpace addrSp = null;
			for (Iterator<AddressSpace> j = addrSpaceList.iterator(); j.hasNext();) {
				AddressSpace as = j.next();

				if (as.getName() != null &&
						as.getName().equals(data.getAddressSpaceName())) {
					addrSp = as;
					break;
				}
			}
			if (addrSp == null) {
				System.err.println("Error: Unknown 'Address Space' name (\"" 
						+ data.getAddressSpaceName() + "\").");
				return false;
			}

			route.add(addrSp.getName());

			/*
			 * search SubSpaceName
			 */
			SubSpace subSp = null;
			List<SubSpace> ssList = addrSp.getSubSpace();
			for (Iterator<SubSpace> j = ssList.iterator(); j.hasNext();) {
				SubSpace ss = j.next();

				route.add(ss.getName());
				String path = createPathName(route);
				route.remove(ss.getName());
				if (path != null &&
						path.equals(data.getSubSpaceName())) {
					subSp = ss;
					break;
				}

			}
			if (subSp == null) {
				System.err.println("Error: Unknown 'Sub Space' name (\"" 
						+ data.getSubSpaceName() + "\").");
				return false;
			}

			/*
			 * search SlaveComponentRef in MasterSlaveBinding
			 */
			MasterSlaveBindingSet msBindSet = null;
			msBindSet = subSp.getMasterSlaveBindingSet();
			if (msBindSet == null) {
				continue;
			}

			MasterSlaveBinding msBind = null;
			List<MasterSlaveBinding> msBindList = msBindSet.getMasterSlaveBinding();
			SlaveComponent scComp = slaveComponentMap.get(data.getSlaveComponentName());
			if (scComp == null) {
				System.err.println("Error: Unknown 'Slave Comonent' name (\"" 
					+ data.getSlaveComponentName() + "\").");
				return false;
			}
			for (Iterator<MasterSlaveBinding> j = msBindList.iterator(); j.hasNext();) {
				MasterSlaveBinding msb = j.next();
				SlaveComponent sca = (SlaveComponent)msb.getSlaveComponentRef();

				if (sca != null && sca.getId().equals(scComp.getId())) {
					msBind = msb;
					break;
				}
			}
			if (msBind == null) {
				continue;
			}

			/*
			 * search MasterComponentRef in Accessor
			 */
			Accessor accessor = null;
			List<Accessor> acList = msBind.getAccessor();
			MasterComponent mcComp = masterComponentMap.get(data.getMasterComponentName());
			if (mcComp == null) {
				System.err.println("Error: Unknown 'Master Comonent' name (\"" 
					+ data.getMasterComponentName() + "\").");
				return false;
			}
			for (Iterator<Accessor> j = acList.iterator(); j.hasNext();) {
				Accessor ac = j.next();
				MasterComponent mc = (MasterComponent)ac.getMasterComponentRef();

				if (mc != null && mc.getId().equals(mcComp.getId())) {
					accessor = ac;
					break;
				}
			}
			if (accessor == null) {
				continue;
			}

			/*
			 * search PerformanceSet
			 */
			PerformanceSet perfrmSet = null;

			if (accessor.getPerformanceSet().size() != 0) {
				perfrmSet = accessor.getPerformanceSet().get(0);
			}
			if (perfrmSet == null) {
				continue;
			}

			/*
			 * search Performance
			 */
			List<Performance> pfrmList = perfrmSet.getPerformance();
			AccessType atComp = accessTypeMap.get(data.getAccessTypeName());
			if (atComp == null) {
				System.err.println("Error: Unknown 'Access Type' name (\"" 
					+ data.getAccessTypeName() + "\").");
				return false;
			}
			for (Iterator<Performance> j = pfrmList.iterator(); j.hasNext();) {
				Performance pfm = j.next();
				AccessType at = (AccessType)pfm.getAccessTypeRef();

				if (at != null && at.getId().equals(atComp.getId())) {
					Latency latency = new Latency();
					Pitch pitch = new Pitch();

					latency.setBest(data.getBestLatency());
					latency.setWorst(data.getWorstLatency());
					latency.setTypical(data.getTypicalLatency());
					pitch.setBest(data.getBestPitch());
					pitch.setWorst(data.getWorstPitch());
					pitch.setTypical(data.getTypicalPitch());

					pfm.setLatency(latency);
					pfm.setPitch(pitch);
					break;
				}
			}
		}

		try {
			JAXBContext context = JAXBContext.newInstance(
					SystemConfiguration.class.getPackage().getName());

			Marshaller marshaller = context.createMarshaller();
			/* validation check setup */
			if (!shimSchemaPath.equals("")) {
				SchemaFactory sf = SchemaFactory.newInstance(
						javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = sf.newSchema(new File(shimSchemaPath));
				marshaller.setSchema(schema);
			}
			QName qname = new QName("", "SystemConfiguration");

			JAXBElement<SystemConfiguration> elem =
					new JAXBElement<SystemConfiguration>(
							qname, SystemConfiguration.class, sysConf);

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//			marshaller.marshal(elem, new PrintStream(shimf));
			marshaller.marshal(elem, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
			System.err.println("Error: exception occurs in SHIM file generation"); //$NON-NLS-1$
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			System.err.println("Error: output SHIM file validation error"); //$NON-NLS-1$
			return false;
		}

		return true;
	}

	/**
	 * main
	 * @param args
	 */
	public static void main(String args[]) {
		boolean ch = false;

		ch = checkParam(args);
		if (!ch) {
			System.exit(1);
		}

		ch = readCsvFile(csvf.getPath());
		if (!ch) {
			System.exit(1);
		}

		ch = appendToExistingShim();
		if (!ch) {
			System.exit(1);
		}
	}


}
