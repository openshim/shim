/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.cycle.generate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import org.ini4j.InvalidFileFormatException;
import org.multicore_association.measure.cycle.generate.config.ArchConfig;
import org.multicore_association.measure.cycle.generate.config.InstSetConfig;
import org.multicore_association.measure.cycle.generate.config.Operation;

public class ConfigLoader {

	private static final String ARCH_CONF_PATTERN = ".*\\.arch";
	private static final String INST_CONF_PATTERN = ".*\\.inst";

	/** Operation name. */
	public static final String CALL_OPERATION = "call";
	/** Operation name. */
	public static final String RETURN_OPERATION = "ret";

	private List<ArchConfig> archConfCacheList;
	private List<InstSetConfig> instSetConfCacheList;

	private String confDirPath;

	private List<File> archConfFileList;
	private List<File> instConfFileList;

	private HashSet<String> shimOpList = null;

	/**
	 *
	 * @param dirPath
	 */
	public ConfigLoader(String dirPath) {
		archConfCacheList = new ArrayList<ArchConfig>();
		instSetConfCacheList = new ArrayList<InstSetConfig>();
		loadConfigFileList(dirPath);
	}

	/**
	 * search architecture and instruction config file.
	 */
	private void loadConfigFileList(String dirPath) {
		confDirPath = dirPath;
		archConfFileList = searchFile(confDirPath, ARCH_CONF_PATTERN);
		instConfFileList = searchFile(confDirPath, INST_CONF_PATTERN);
	}

	/**
	 * configuration file search processing.
	 * This method searches for a setting file in the directory designated
	 * as an argument recursively. Return searches, and returns a list of the files found.
	 * The pattern of the file name is designated as an argument.
	 * @param dirPath
	 * @param pattern
	 * @return
	 */
	private List<File> searchFile(String dirPath, String pattern) {

		List<File> ret = new ArrayList<File>();
		File dir = new File(dirPath);
		if (!dir.isDirectory()) {
			return ret;
		}

		File[] filearray = dir.listFiles();
		for (int i = 0; i < filearray.length; i++) {
			File file = filearray[i];

			if (file.isDirectory()) {
				List<File> lst = searchFile(file.getAbsolutePath(), pattern);
				ret.addAll(lst);
			} else if (file.isFile()) {
				if (file.getName().matches(pattern)) {
					ret.add(file);
				}
			}
		}

		return ret;
	}

	/**
	 * Load of configuration file.
	 * @param f
	 * @return
	 * @throws InvalidFileFormatException
	 * @throws IOException
	 */
	private Preferences loadConfigFile(File f) throws InvalidFileFormatException, IOException {
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
	private boolean checkPrefEntry(Preferences prefs, String section, String entry) {
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
	 * Parsing common instruction set configuration.
	 * @param prefs read configuration data
	 * @return Flag for success judgements
	 * @throws BackingStoreException
	 */
	private InstSetConfig parseInstructionSetConfig(Preferences prefs, String name)
			throws BackingStoreException {

		/* check of the section presence */
		List<String> seclist = InstSetConfig.getSectionNameList();
		boolean result = true;

		for (Iterator<String> i = seclist.iterator(); i.hasNext();) {
			String n = i.next();
			if (!checkPrefSection(prefs, n)) {
				System.err.println("Error: mandatory section does not exist" + " (" + n + ")");
				result = false;
			}
		}

		/* check of the entry presence */
		List<String> hentrylist = InstSetConfig.getHeaderEntryNameList();
		for (Iterator<String> i = hentrylist.iterator(); i.hasNext();) {
			String n = i.next();
			if (!checkPrefEntry(prefs, InstSetConfig.SECTION_HEADER, n)) {
				System.err.println("Error: mandatory entry does not exist" + " (" + n + ")");
				result = false;
			}
		}

		/* check of the entry presence */
		List<String> mentrylist = InstSetConfig.getMainEntryNameList();
		for (Iterator<String> i = mentrylist.iterator(); i.hasNext();) {
			String n = i.next();
			if (!checkPrefEntry(prefs, InstSetConfig.SECTION_MAIN, n)) {
				System.err.println("Error: mandatory entry does not exist" + " (" + n + ")");
				result = false;
			}
		}

		if (!result) {
			return null;
		}

		/* set configuration data */
		InstSetConfig instSetConf = new InstSetConfig();
		Preferences sec_header = prefs.node(InstSetConfig.SECTION_HEADER);
		Preferences sec_main = prefs.node(InstSetConfig.SECTION_MAIN);
		Preferences sec_operation = prefs.node(InstSetConfig.SECTION_OPERATION);

		int header_itr = 0;
		int header_depth = 0;
		int header_try = 0;
		int header_cont = 0;

		try {
			header_itr = Integer.parseInt(sec_header.get(InstSetConfig.ENTRY_ITERATION, "0"));
		} catch (NumberFormatException e) {
			System.err.println(
					"Error: there is a problem with the format of \"iteration\" entry");
			result = false;
		}

		try {
			header_depth = Integer.parseInt(sec_header.get(InstSetConfig.ENTRY_DEPTH, "0"));
		} catch (NumberFormatException e) {
			System.err.println(
					"Error: there is a problem with the format of \"depth\" entry");
			result = false;
		}

		try {
			header_try = Integer.parseInt(sec_header.get(InstSetConfig.ENTRY_TRY, "0"));
		} catch (NumberFormatException e) {
			System.err.println(
					"Error: there is a problem with the format of \"try\" entry");
			result = false;
		}

		try {
			header_cont = Integer.parseInt(sec_header.get(InstSetConfig.ENTRY_CONTINUITY, "0"));
		} catch (NumberFormatException e) {
			System.err.println(
					"Error: there is a problem with the format of \"continuity\" entry");
			result = false;
		}

		if (header_itr > Integer.MAX_VALUE || header_itr < 0) {
			System.err.println(
					"Error: enter in the range of 0 to 2147483647 value of \"iteration\" entry");
			result = false;
		}

		if (header_depth > 99999 || header_depth < 0) {
			System.err.println(
					"Error: enter in the range of 0 to 99999 value of \"depth\" entry");
			result = false;
		}

		if (header_try > Integer.MAX_VALUE || header_try < 1) {
			System.err.println(
					"Error: enter in the range of 1 to 2147483647 value of \"try\" entry");
			result = false;
		}


		if (header_cont > 10 || header_cont < 1) {
			System.err.println(
					"Error: enter in the range of 1 to 10 value of \"continuity\" entry");
			result = false;
		}

		if (!result) {
			return null;
		}

		instSetConf.setHeaderIteration(header_itr);
		instSetConf.setHeaderDepth(header_depth);
		instSetConf.setHeaderTry(header_try);
		instSetConf.setHeaderContinuity(header_cont);
		instSetConf.setHeaderInclude(sec_header.get(InstSetConfig.ENTRY_INCLUDE, ""));
		instSetConf.setHeaderDefine(sec_header.get(InstSetConfig.ENTRY_DEFINE, ""));
		instSetConf.setHeaderGlobalValiable(sec_header.get(InstSetConfig.ENTRY_GLOBAL_VAR, ""));
		instSetConf.setHeaderFunction(sec_header.get(InstSetConfig.ENTRY_FUNCTION, ""));
		instSetConf.setLocalValiable(sec_main.get(InstSetConfig.ENTRY_VARIABLE, ""));
		instSetConf.setLocalInitialize(sec_main.get(InstSetConfig.ENTRY_INITIALIZE, ""));

		instSetConf.setOperationList(new ArrayList<Operation>());

		/* operation list */
		String keys[] = sec_operation.keys();
		for (int i = 0; i < keys.length; i++) {

			if((shimOpList != null) && !(shimOpList.contains(keys[i]))) {
				continue;
			}

			ArrayList<String> buff = new ArrayList<String>();
			String data = sec_operation.get(keys[i], "");

			String[] list = data.split("[\r\n]");
			for (int j = 0; j < list.length; j++) {
				list[j].trim();
				if (!list[j].equals("")) {
					buff.add(list[j]);
				}
			}

			if (buff.size() != 2) {
				System.err.println(
						"Error: the entry format in the Operation section is wrong"
						+ " (entry: " + keys[i] + ")");
				continue;
			}

			Operation ope = new Operation();
			ope.setName(keys[i]);
			ope.setCompareCode(buff.get(0));
			ope.setTargetCode(buff.get(1));
			instSetConf.addOperationList(ope);
		}

		instSetConf.setName(name);

		return instSetConf;
	}

	/**
	 * Parsing architecture configuration.
	 * @param prefs read configuration
	 * @return Flag for success judgements
	 */
	private ArchConfig parseArchConfig(Preferences prefs, String name) {

		List<String> seclist = ArchConfig.getSectionNameList();
		boolean result = true;

		/* check of the section presence */
		for (Iterator<String> i = seclist.iterator(); i.hasNext();) {
			String n = i.next();
			if (!checkPrefSection(prefs, n)) {
				// xxx
				System.err.println("Error: mandatory section does not exist" + " (" + n + ")");
				result = false;
			}
		}

		/* check of the entry presence */
		List<String> hentrylist = ArchConfig.getHeaderEntryNameList();
		for (Iterator<String> i = hentrylist.iterator(); i.hasNext();) {
			String n = i.next();
			if (!checkPrefEntry(prefs, ArchConfig.SECTION_HEADER, n)) {
				System.err.println("Error: mandatory entry does not exist" + " (" + n + ")");
				result = false;
			}
		}

		/* check of the entry presence */
		List<String> mentrylist = ArchConfig.getMainEntryNameList();
		for (Iterator<String> i = mentrylist.iterator(); i.hasNext();) {
			String n = i.next();
			if (!checkPrefEntry(prefs, ArchConfig.SECTION_MAIN, n)) {
				System.err.println("Error: mandatory entry does not exist" + " (" + n + ")");
				result = false;
			}
		}

		if (!result) {
			return null;
		}

		/* set configuration data */
		ArchConfig archConf = new ArchConfig();
		Preferences sec_h = prefs.node(ArchConfig.SECTION_HEADER); //header section
		Preferences sec_m = prefs.node(ArchConfig.SECTION_MAIN);   //main section

		archConf.setHeaderInclude(sec_h.get(ArchConfig.ENTRY_INCLUDE, ""));
		archConf.setHeaderDefine(sec_h.get(ArchConfig.ENTRY_DEFINE, ""));
		archConf.setHeaderGlobalValiable(sec_h.get(ArchConfig.ENTRY_GLOBAL_VAR, ""));
		archConf.setHeaderFunction(sec_h.get(ArchConfig.ENTRY_FUNCTION, ""));
		archConf.setLocalValiable(sec_m.get(ArchConfig.ENTRY_VARIABLE, ""));
		archConf.setMainInitialize(sec_m.get(ArchConfig.ENTRY_INITIALIZE, ""));
		archConf.setMainMeasureDiffBegin(sec_m.get(ArchConfig.ENTRY_MEASURE_DIFF_BEGIN, ""));
		archConf.setMainMeasureDiffEnd(sec_m.get(ArchConfig.ENTRY_MEASURE_DIFF_END, ""));
		archConf.setMainMeasureTargetBegin(sec_m.get(ArchConfig.ENTRY_MEASURE_TARGET_BEGIN, ""));
		archConf.setMainMeasureTargetEnd(sec_m.get(ArchConfig.ENTRY_MEASURE_TARGET_END, ""));
		archConf.setMainTryDiffBegin(sec_m.get(ArchConfig.ENTRY_TRY_DIFF_BEGIN, ""));
		archConf.setMainTryDiffEnd(sec_m.get(ArchConfig.ENTRY_TRY_DIFF_END, ""));
		archConf.setMainTryTargetBegin(sec_m.get(ArchConfig.ENTRY_TRY_TARGET_BEGIN, ""));
		archConf.setMainTryTargetEnd(sec_m.get(ArchConfig.ENTRY_TRY_TARGET_END, ""));
		archConf.setMainContinuityBegin(sec_m.get(ArchConfig.ENTRY_CONTINUITY_BEGIN, ""));
		archConf.setMainContinuityEnd(sec_m.get(ArchConfig.ENTRY_CONTINUITY_END, ""));
		archConf.setMainMeasureCallInit(sec_m.get(ArchConfig.ENTRY_MEASURE_CALL_INIT, ""));
		archConf.setMainMeasureRetInit(sec_m.get(ArchConfig.ENTRY_MEASURE_RET_INIT, ""));
		archConf.setMainMeasureCallBegin(sec_m.get(ArchConfig.ENTRY_MEASURE_CALL_BEGIN, ""));
		archConf.setMainMeasureCallEnd(sec_m.get(ArchConfig.ENTRY_MEASURE_CALL_END, ""));
		archConf.setMainMeasureRetBegin(sec_m.get(ArchConfig.ENTRY_MEASURE_RET_BEGIN, ""));
		archConf.setMainMeasureRetEnd(sec_m.get(ArchConfig.ENTRY_MEASURE_RET_END, ""));
		archConf.setMainMeasureCallResult(sec_m.get(ArchConfig.ENTRY_MEASURE_CALL_RESULT, ""));
		archConf.setMainMeasureRetResult(sec_m.get(ArchConfig.ENTRY_MEASURE_RET_RESULT, ""));
		archConf.setMainTryCallRetBegin(sec_m.get(ArchConfig.ENTRY_TRY_CALL_RET_BEGIN, ""));
		archConf.setMainTryCallRetEnd(sec_m.get(ArchConfig.ENTRY_TRY_CALL_RET_END, ""));
		archConf.setName(name);

		return archConf;
	}

	/**
	 * This method searches for the setting file parallel with the designated name.
	 * When a file was found, it's read and an instance is returned.
	 * @param name name InstructionSet configuration name
	 * @return instance of InstructionSetConfig
	 */
	public InstSetConfig searchInstructionSetConfig(String name) {
		String filename  = name + ".inst";
		File instFile = null;
		Preferences instPrefs = null;
		InstSetConfig ret = null;

		/* search cache of loaded file */
		for (Iterator<InstSetConfig> i = instSetConfCacheList.iterator(); i.hasNext();) {
			InstSetConfig conf = i.next();
			if (conf.getName().equals(name)) {
				return conf;
			}
		}

		/* search file that has not been loaded */
		for (Iterator<File> i = instConfFileList.iterator(); i.hasNext();) {
			File f = i.next();
			if (!f.getName().equals(filename)) {
				continue;
			}
			instFile = f;
			break;
		}


		if (instFile == null) {
			return null;
		}

		try {
			instPrefs = loadConfigFile(instFile);
		} catch (InvalidFileFormatException e) {
			System.err.println("Error: invalid file format" + " (" + instFile + ")");
			return null;
		} catch (IOException e) {
			System.err.println("Error: file io error" + " (" + instFile + ")");
			return null;
		}

		try {
			ret = parseInstructionSetConfig(instPrefs, name);
		} catch (BackingStoreException e) {
			e.printStackTrace();
			System.err.println("Error: internal error");
			return null;
		}

		if (ret != null) {
			instSetConfCacheList.add(ret);
		}

		return ret;
	}

	/**
	 * This method searches for the setting file parallel with the designated name.
	 * When a file was found, it's read and an instance is returned.
	 * @param name Architecture configuration name
	 * @return instance of ArchConfig
	 */
	public ArchConfig searchArchitectureConfig(String name) {
		String filename = name + ".arch";
		File archFile = null;
		Preferences archPrefs = null;
		ArchConfig ret = null;

		/* search cache of loaded file */
		for (Iterator<ArchConfig> i = archConfCacheList.iterator(); i.hasNext();) {
			ArchConfig conf = i.next();
			if (conf.getName().equals(name)) {
				return conf;
			}
		}

		/* search file that has not been loaded */
		for (Iterator<File> i = archConfFileList.iterator(); i.hasNext();) {
			File f = i.next();
			if (!f.getName().equals(filename)) {
				continue;
			}
			archFile = f;
			break;
		}

		if (archFile == null) {
			return null;
		}

		try {
			archPrefs = loadConfigFile(archFile);
		} catch (InvalidFileFormatException e) {
			System.err.println("Error: invalid file format" + " (" + archFile + ")");
			return null;
		} catch (IOException e) {
			System.err.println("Error: file io error" + " (" + archFile + ")");
			return null;
		}

		ret = parseArchConfig(archPrefs, name);
		if (ret != null) {
			archConfCacheList.add(ret);
		}

		return ret;
	}
}
