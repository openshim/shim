/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.cycle.generate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.multicore_association.measure.cycle.generate.util.Util;
import org.multicore_association.shim.api.CommonInstructionSet;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.Instruction;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.MasterType;
import org.multicore_association.shim.api.SystemConfiguration;
import org.xml.sax.SAXException;

public class ShimLoader {

	private SystemConfiguration sysConf = null;
	private List<String> parseErrList = null;
	private Map<String, Map<String, Set<String>>> archInstMap;

	public ShimLoader() {
		archInstMap = new HashMap<String, Map<String, Set<String>>>();
	}

	/**
	 *
	 * @return
	 */
	public List<String> getParseErrList() {
		return parseErrList;
	}

	/**
	 *
	 * @param shimFilePath
	 * @return
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public boolean loadShim(String shimFilePath)
			throws JAXBException, SAXException {
		return loadShim(shimFilePath, null);
	}


	/**
	 *
	 * @param shimFilePath
	 * @param shimSchemaPath
	 * @return
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public boolean loadShim(String shimFilePath, String shimSchemaPath)
			throws JAXBException, SAXException {

		File shimf = new File(shimFilePath);
		sysConf = null;
		parseErrList = new ArrayList<String>();

		JAXBContext context = JAXBContext.newInstance(SystemConfiguration.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		/* validation check setup */
		if (shimSchemaPath != null && !shimSchemaPath.equals("")) { //$NON-NLS-1$
			SchemaFactory sf = SchemaFactory
					.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(new File(shimSchemaPath));
			unmarshaller.setSchema(schema);
			unmarshaller.setEventHandler(new ValidationEventHandler() {
				@Override
				public boolean handleEvent(ValidationEvent event) {
					if ((event.getSeverity() == ValidationEvent.FATAL_ERROR)
							|| (event.getSeverity() == ValidationEvent.ERROR)) {
						ValidationEventLocator loc = event.getLocator();
						parseErrList.add("    Line[" + loc.getLineNumber() + "] : " //$NON-NLS-1$ //$NON-NLS-2$
								+ event.getMessage());
					}
					return true;
				}
			});
		}

		sysConf = unmarshaller.unmarshal(
				new StreamSource(shimf), SystemConfiguration.class).getValue();

		return true;
	}

	/**
	 * Getter of SystemConfiguration instance of input SHIM data file.
	 * @return SystemConfiguration instance
	 */
	public SystemConfiguration getSysConf() {
		return sysConf;
	}


	/**
	 * make a ShimOpList from SHIM
	 * @param cs current ComponentSet
	 */
	private void makeShimOpList(ComponentSet cs) {

		if (cs.getMasterComponent().size() != 0) {

			for (Iterator<MasterComponent> i = cs.getMasterComponent().iterator();
					i.hasNext();) {

				MasterComponent mc = i.next();
				CommonInstructionSet cis = mc.getCommonInstructionSet();

				if(mc.getMasterType() != MasterType.PU) {
					continue;
				}

				if (mc.getCommonInstructionSet() == null) {
					continue;
				}

				String archName = Util.convertRestrictionCharToUS(mc.getArch());
				Map<String, Set<String>> instMap = archInstMap.get(archName);
				if (instMap == null) {
					instMap = new HashMap<String, Set<String>>();

//					System.err.println(archName);
					archInstMap.put(archName, instMap);
				}

				Set<String> instSet = instMap.get(cis.getName());
				if (instSet == null) {
					instSet = new HashSet<String>();

					String cisName = Util.convertRestrictionCharToUS(cis.getName());

					instMap.put(cisName, instSet);

//					System.out.println(mc.getArch() + "," + cisName);
				}

				for (Iterator<Instruction> k = cis.getInstruction().iterator();
						k.hasNext();) {
					Instruction target = k.next();
					instSet.add(target.getName());
				}
			}
		}

		if(cs.getComponentSet().size() == 0) {
			return;
		}

		List<ComponentSet> csList = cs.getComponentSet();
		for (Iterator<ComponentSet> i = csList.iterator(); i.hasNext();) {
			ComponentSet compSet = i.next();
			makeShimOpList(compSet);
		}
	}

	/**
	 * Get the instruction list from SHIM file.
	 * @return Flag for success judgements
	 */
	public boolean makeInstructionMap(SystemConfiguration sysConf) {

		ComponentSet compSet = sysConf.getComponentSet();
		if (compSet == null) {
			System.err.println("Error: mandatory element does not exist" + " (ComponentSet)");
			return false;
		}

		makeShimOpList(compSet);

		return true;
	}

	/**
	 * Getter of architecture list
	 * @return architecture list
	 */
	public Set<String> getArchSet() {
		if (archInstMap == null) {
			return null;
		}

		return archInstMap.keySet();
	}

	/**
	 * Getter of CommonInstructionSet name list
	 * @param arch architecture name
	 * @return CommonInstructionSet name list
	 */
	public Set<String> getInstNameSet(String arch) {
		if (arch == null) {
			return null;
		}

		if (archInstMap == null) {
			return null;
		}

		Map<String, Set<String>> instSet = archInstMap.get(arch);
		if (instSet == null) {
			return null;
		}

		return instSet.keySet();
	}

	/**
	 * Getter of instruction list
	 * @param arch architecture name
	 * @param instName CommonInstructionSet name
	 * @return instruction list
	 */
	public Set<String> getInstSet(String arch, String instName) {
		if (arch == null || instName == null) {
			return null;
		}

		if (archInstMap == null) {
			return null;
		}

		Map<String, Set<String>> instSet = archInstMap.get(arch);
		if (instSet == null) {
			return null;
		}

		return instSet.get(instName);
	}
}
