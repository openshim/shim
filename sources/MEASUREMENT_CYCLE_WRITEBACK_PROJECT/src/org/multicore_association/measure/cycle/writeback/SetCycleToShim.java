/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.cycle.writeback;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
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

import org.multicore_association.measure.cycle.writeback.data.CsvResult;
import org.multicore_association.measure.cycle.writeback.data.ExtLatency;
import org.multicore_association.measure.cycle.writeback.util.Util;
import org.multicore_association.shim.api.AccessType;
import org.multicore_association.shim.api.AccessTypeSet;
import org.multicore_association.shim.api.ClockFrequency;
import org.multicore_association.shim.api.CommonInstructionSet;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.Instruction;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.MasterType;
import org.multicore_association.shim.api.Performance;
import org.multicore_association.shim.api.Pitch;
import org.multicore_association.shim.api.SystemConfiguration;
import org.xml.sax.SAXException;


/**
 * Reads the CSV file, and sets the value of Latency and Pitch to SHIM file.
 *
 */
public class SetCycleToShim {

	private static final byte[] hex = {
		'0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', 'a', 'b', 'c', 'd', 'e', 'f', };

	private ParameterParser param;

	private int idCounter = 0;
	private String idSeed = Long.toString(System.currentTimeMillis());

	private List<String> parseErrList = new ArrayList<String>();
	private List<CsvResult> resultList = null;
	private List<MasterComponent> mcSrcList = null;


	/**
	 * To be stored in the SHIM instance by reading the CSV file.
	 * @return Flag for success judgements
	 */
	private boolean readCsvFile() {
		/* file reading */
		CsvResultFileLoader loader = null;
		if (param.getCsvDir() != null) {
			loader = new CsvResultFileLoader(param.getCsvDir(), param.isRecursive());
		} else {
			loader = new CsvResultFileLoader(param.getCsvFileList());
		}
		resultList = loader.getResultCacheList();

		mcSrcList = new ArrayList<MasterComponent>();

		for (Iterator<CsvResult> i = resultList.iterator(); i.hasNext();) {
			CsvResult csv = i.next();

			MasterComponent mc = new MasterComponent();

			CommonInstructionSet cis = new CommonInstructionSet();
			List<Instruction> instructionList = cis.getInstruction();
			cis.setName(csv.getInstSetName());
			mc.setCommonInstructionSet(cis);
			mc.setArch(csv.getArchName());

			mcSrcList.add(mc);

			List<String[]> instructionDataList = csv.getResultList();

			for (Iterator<String[]> j = instructionDataList.iterator(); j.hasNext();) {
				String instructionName;
				String latencyBest, latencyTypical, latencyWorst;
				String[] array = j.next();
				boolean isValid = true;

				if (array.length == 0) {
					continue;
				}

				if (array.length == 1) {
					instructionName = array[0];
					latencyTypical = "";
					latencyBest = "";
					latencyWorst = "";
					isValid = false;
				} else if (array.length >= 3) {
					instructionName = array[0];
					latencyBest = array[1];
					latencyTypical = array[2];
					latencyWorst = array[3];
					isValid &= Util.checkFloatFormat(latencyBest);
					isValid &= Util.checkFloatFormat(latencyTypical);
					isValid &= Util.checkFloatFormat(latencyWorst);
				} else {
					instructionName = array[0];
					latencyTypical = array[1];
					latencyBest = array[1];
					latencyWorst = array[1];
					isValid &= Util.checkFloatFormat(latencyBest);
				}

				Instruction instruction = new Instruction();
				Performance performance = new Performance();
				ExtLatency latency = new ExtLatency();
				Pitch pitch = new Pitch();

				instruction.setName(instructionName);

				latency.setValidValue(isValid);

				latency.setBest(Util.convertFloatValue(latencyBest));
				latency.setTypical(Util.convertFloatValue(latencyTypical));
				latency.setWorst(Util.convertFloatValue(latencyWorst));

				performance.setLatency(latency);
				performance.setPitch(pitch);
				instruction.setPerformance(performance);

				instructionList.add(instruction);
			}
		}

		return true;
	}

	/**
	 * generating unique ID for SHIM ID attribute.
	 * @param prefix
	 * @return generated ID
	 */
	private String generateId(String prefix) {
		String src = idSeed + idCounter++;
		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}

		byte [] digest = md.digest(src.getBytes());
		if (digest.length < 20) {
			return null;
		}

		byte [] str = new byte[40];

		for (int i = 0; i < 20; i++) {
			str[i*2  ] = hex[(digest[i] >> 4) & 0xF];
			str[i*2+1] = hex[(digest[i] >> 0) & 0xF];
		}

		return (prefix + (new String(str)));
	}


	/**
	 * Set the value to generate a new SHIM file.
	 * @return Flag for success judgements
	 */
	private boolean createNewShim() {

		File outputShimFile = new File(param.getOutShimPath());

		/*create a new llvm-shim*/
		SystemConfiguration sysConf = new SystemConfiguration();
		sysConf.setName("sample"); 							/* required */
		sysConf.setShimVersion("1.0"); 						/* required */
//		sysConf.setConfigrationName("sample");

		ClockFrequency clkFreq = new ClockFrequency();
		sysConf.setClockFrequency(clkFreq);
		clkFreq.setClockValue(0.0f);	/* required */

		ComponentSet compSet = new ComponentSet();
		sysConf.setComponentSet(compSet);
		compSet.setName("sample"); 	/* required */

		for (Iterator<MasterComponent> i = mcSrcList.iterator(); i.hasNext();) {
			MasterComponent mc = i.next();

			mc.setName("sample");
			mc.setMasterType(MasterType.PU);
			mc.setId(generateId("MCOMP"));

			AccessTypeSet accessTypeSet = new AccessTypeSet();
			AccessType accessType = new AccessType();
			accessType.setName("sample"); 							/* required */
			accessType.setId(generateId("ATYPE"));					/* required */
			accessTypeSet.getAccessType().add(accessType);
			mc.setAccessTypeSet(accessTypeSet);
			compSet.getMasterComponent().add(mc);

			System.out.println("Create Instruction entry:");
			System.out.println("    MasterComponent Name[" + mc.getName()
					+ "], Arch[" + mc.getArch() + "]");
			System.out.println("    CommonInstructionSet Name["
					+ mc.getCommonInstructionSet().getName() + "]");
			System.out.println();

		}

		try {
			JAXBContext context = JAXBContext.newInstance(
					SystemConfiguration.class.getPackage().getName());

			Marshaller marshaller = context.createMarshaller();
			QName qname = new QName("", "SystemConfiguration");

			JAXBElement<SystemConfiguration> elem =
					new JAXBElement<SystemConfiguration>(
							qname, SystemConfiguration.class, sysConf);

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			/* validation check setup */
			if (!param.getShimSchemaPath().equals("")) {
				SchemaFactory sf = SchemaFactory.newInstance(
						javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = sf.newSchema(new File(param.getShimSchemaPath()));
				marshaller.setSchema(schema);
			}

//			marshaller.marshal(elem, new PrintStream(shimf));
//			marshaller.marshal(elem, System.out);
			marshaller.marshal(elem, outputShimFile);

		} catch (JAXBException e) {
			e.printStackTrace();
			System.err.println("Error: exception occurs in SHIM file generation");
			return false;
		} catch (SAXException e) {
			System.err.println("Error: output SHIM file validation error");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * set cycle to CommonInstructionSet in SHIM
	 * @param cs current ComponentSet
	 * @param mcArch the name of the architecture of MasterComponent to set cycle
	 * @param cisName the name of the CommonInstructionSet to set cycle
	 */
	private void setCycleToCommonInstructionSet(ComponentSet cs, String mcArch, String cisName) {

		if (cs.getMasterComponent().size() != 0) {

			List<MasterComponent> mcDestList = cs.getMasterComponent();

			for (Iterator<MasterComponent> i = mcSrcList.iterator(); i.hasNext();) {

				MasterComponent mcSrc = i.next();
				if((mcArch.length() != 0) && (!mcArch.equals(mcSrc.getArch()))) {
					continue;
				}

				CommonInstructionSet cis = mcSrc.getCommonInstructionSet();
				if((cisName.length() != 0) && (!cisName.equals(cis.getName()))) {
					continue;
				}

				for (Iterator<MasterComponent> j = mcDestList.iterator(); j.hasNext();) {
					MasterComponent mcDest = j.next();

					if (mcDest.getMasterType() != MasterType.PU) {
						continue;
					}

					if (mcDest.getCommonInstructionSet() == null) {
						continue;
					}

					String arch = Util.convertRestrictionCharToUS(mcDest.getArch());
					if (!mcSrc.getArch().equals(arch)) {
						continue;
					}

					String name = Util.convertRestrictionCharToUS(
							mcDest.getCommonInstructionSet().getName());
					if (!cis.getName().equals(name)) {
						continue;
					}

					/* update */
					List<Instruction> destInstList = mcDest.getCommonInstructionSet().getInstruction();
					List<Instruction> srcInstList = cis.getInstruction();

					for (Iterator<Instruction> k = srcInstList.iterator(); k.hasNext();) {
						Instruction source = k.next();
						Performance sourcePerfm = source.getPerformance();
						boolean updated = false;

						for (Iterator<Instruction> l = destInstList.iterator(); l.hasNext();) {
							Instruction target = l.next();

							if (source.getName().equals(target.getName())) {
								target.setPerformance(sourcePerfm);
								updated = true;
								break;
							}
						}

						if (!updated) {
							destInstList.add(source);
						}
					}

					System.out.println("Update Instruction entry:");
					System.out.println("    MasterComponent Name[" + mcDest.getName()
							+ "], Arch[" + mcDest.getArch() + "]");
					System.out.println("    CommonInstructionSet Name["
							+ mcDest.getCommonInstructionSet().getName() + "]");
					System.out.println();
				}
			}
		}

		if(cs.getComponentSet().size() != 0) {
			List<ComponentSet> csList = cs.getComponentSet();
			for (Iterator<ComponentSet> i = csList.iterator(); i.hasNext();) {
				ComponentSet compSet = i.next();

				setCycleToCommonInstructionSet(compSet, mcArch, cisName);
			}
		}
	}

	/**
	 * Set the value to read the existing SHIM file.
	 * @return Flag for success judgements
	 */
	private boolean appendToExistingShim() {
		/*append to existing llvm-shim*/
		SystemConfiguration sysConf = null;

		File inputShimFile = new File(param.getInShimPath());
		File outputShimFile = new File(param.getOutShimPath());

		try {
			JAXBContext context = JAXBContext.newInstance(SystemConfiguration.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			/* validation check setup */
			if (!param.getShimSchemaPath().equals("")) {
				SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = sf.newSchema(new File(param.getShimSchemaPath()));
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

			sysConf = unmarshaller.unmarshal(
					new StreamSource(inputShimFile), SystemConfiguration.class).getValue();

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

		ComponentSet compSet = sysConf.getComponentSet();
		if (compSet == null) {
			System.err.println("Error: failed to parse the SHIM file, please check the contents of the file");
			return false;
		}

		setCycleToCommonInstructionSet(compSet, param.getMasterComponentArch(),
				param.getCommonInstructionName());

		try {
			JAXBContext context =
					JAXBContext.newInstance(SystemConfiguration.class.getPackage().getName());

			Marshaller marshaller = context.createMarshaller();

			/* validation check setup */
			if (!param.getShimSchemaPath().equals("")) {
				SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = sf.newSchema(new File(param.getShimSchemaPath()));
				marshaller.setSchema(schema);
			}
			QName qname = new QName("", "SystemConfiguration");

			JAXBElement<SystemConfiguration> elem =
					new JAXBElement<SystemConfiguration>(
							qname, SystemConfiguration.class, sysConf);

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//			marshaller.marshal(elem, new PrintStream(shimf));
//			marshaller.marshal(elem, System.out);
			marshaller.marshal(elem,  outputShimFile);
		} catch (JAXBException e) {
			e.printStackTrace();
			System.err.println("Error: exception occurs in SHIM file generation");
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			System.err.println("Error: output SHIM file validation error");
			return false;
		}

		return true;
	}


	public boolean setCycle(String args[]) {
		boolean ch = true;

		/* parse param */
		param = new ParameterParser();

//		try{
//			ch = param.parseParam(args);
//		} catch(ParseException e) {
//			System.err.println(e.getMessage());
//			ch = false;
//		}
		ch = param.parseParam(args);
		if (!ch) {
			param.printHelp();
			//System.err.println("Error: parameter error");
			System.out.println("Abort.");
			return false;
		}

		ch = param.verifyParam();
		if (!ch) {
			param.printHelp();
			System.out.println("Abort.");
			return false;
		}


		ch = readCsvFile();
		if (!ch) {
			param.printHelp();
			//System.err.println("Error: CSV file read error");
			System.out.println("Abort.");
			return false;
		}

		if (param.getInShimPath() == null || param.getInShimPath().equals("")) {
			ch = createNewShim();
		} else {
			ch = appendToExistingShim();
		}

		if (!ch) {
			//System.err.println("Error: SHIM file read/write error");
			System.out.println("Abort.");
			return false;
		}

		System.out.println("Done.");
		return true;
	}

	/**
	 * main
	 * @param args
	 */
	public static void main(String args[]) {
		SetCycleToShim scts = new SetCycleToShim();
		if (!scts.setCycle(args)) {
			System.exit(1);
		}
		return;
	}
}
