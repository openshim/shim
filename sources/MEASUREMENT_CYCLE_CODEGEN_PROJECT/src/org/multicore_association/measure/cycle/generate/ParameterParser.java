/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.cycle.generate;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class ParameterParser {

	private static final String ARCH_NAME_OPT = "a";
	private static final String ARCH_NAME_LOPT = "architecture-name";
	private static final String ARCH_NAME_DESC = "Target Architecture name";
	private static final String ARCH_NAME_ARGNAME = "name";

	private static final String INSTSET_NAME_OPT = "i";
	private static final String INSTSET_NAME_LOPT = "instruction-set-name";
	private static final String INSTSET_NAME_DESC = "Target CommonInstructionSet name";
	private static final String INSTSET_NAME_ARGNAME = "name";

	private static final String SHIM_SCHEMA_OPT = "s";
	private static final String SHIM_SCHEMA_LOPT = "shim-schema";
	private static final String SHIM_SCHEMA_DESC = "SHIM XML schema file path";
	private static final String SHIM_SCHEMA_ARGNAME = "path";

	private static final String INPUT_SHIM_OPT = "in";
	private static final String INPUT_SHIM_LOPT = "input-shim";
	private static final String INPUT_SHIM_DESC = "Input SHIM XML data file";
	private static final String INPUT_SHIM_ARGNAME = "path";

	private static final String CONF_DIR_OPT = "c";
	private static final String CONF_DIR_LOPT = "conf-dir";
	private static final String CONF_DIR_DESC = "Configuration file directory path";
	private static final String CONF_DIR_ARGNAME = "path";

	private static final String DEST_DIR_OPT = "d";
	private static final String DEST_DIR_LOPT = "dest-dir";
	private static final String DEST_DIR_DESC = "Destination directory path";
	private static final String DEST_DIR_ARGNAME = "path";

	private static final String FILE_PREFIX_OPT = "p";
	private static final String FILE_PREFIX_LOPT = "prefix";
	private static final String FILE_PREFIX_DESC = "Generated Code File name prefix";
	private static final String FILE_PREFIX_ARGNAME = "prefix";

	private static final String PRINTHELP_OPT = "h";
	private static final String PRINTHELP_LOPT = "help";
	private static final String PRINTHELP_DESC = "Show usage";

	private String architectureName;	//optional
	private String instructionSetName;	//optional
	private String shimSchemaPath;		//optional
	private String shimDataPath;		//mandatory
	private String destDir;				//optional
	private String prefix;				//optional
	private String configDir;			//mandatory

	private Options opts;
	private CommandLine cl;

	public ParameterParser() {
		configDir = "";
		shimDataPath = "";
		architectureName = "";
		instructionSetName = "";
		shimSchemaPath = "";
		destDir = "";
		prefix = "";

		opts = generateOpts();
		cl = null;
	}

	public String getConfigDir() {
		return configDir;
	}
	public String getShimDataPath() {
		return shimDataPath;
	}
	public String getArchitectureName() {
		return architectureName;
	}
	public String getInstructionSetName() {
		return instructionSetName;
	}
	public String getShimSchemaPath() {
		return shimSchemaPath;
	}
	public String getDestDir() {
		return destDir;
	}
	public String getPrefix() {
		return prefix;
	}

	private Options generateOpts() {
		Option optArch;
		Option optCmnInstSet;
		Option optShimSchema;
		Option optInputShim;
		Option optConfDir;
		Option optDestDir;
		Option optPrefix;
		Option optHelp;

		optArch = new Option(ARCH_NAME_OPT, ARCH_NAME_LOPT, true, ARCH_NAME_DESC);
		optArch.setArgName(ARCH_NAME_ARGNAME);

		optCmnInstSet = new Option(INSTSET_NAME_OPT, INSTSET_NAME_LOPT, true, INSTSET_NAME_DESC);
		optCmnInstSet.setArgName(INSTSET_NAME_ARGNAME);

		optShimSchema = new Option(SHIM_SCHEMA_OPT, SHIM_SCHEMA_LOPT, true, SHIM_SCHEMA_DESC);
		optShimSchema.setArgName(SHIM_SCHEMA_ARGNAME);

		optInputShim = new Option(INPUT_SHIM_OPT, INPUT_SHIM_LOPT, true, INPUT_SHIM_DESC);
		optInputShim.setArgName(INPUT_SHIM_ARGNAME);
		optInputShim.setRequired(true);

		optConfDir= new Option(CONF_DIR_OPT, CONF_DIR_LOPT, true, CONF_DIR_DESC);
		optConfDir.setArgName(CONF_DIR_ARGNAME);
		optConfDir.setRequired(true);

		optDestDir= new Option(DEST_DIR_OPT, DEST_DIR_LOPT, true, DEST_DIR_DESC);
		optDestDir.setArgName(DEST_DIR_ARGNAME);

		optPrefix= new Option(FILE_PREFIX_OPT, FILE_PREFIX_LOPT, true, FILE_PREFIX_DESC);
		optPrefix.setArgName(FILE_PREFIX_ARGNAME);

		optHelp= new Option(PRINTHELP_OPT, PRINTHELP_LOPT, false, PRINTHELP_DESC);

		Options opts = new Options();
		opts.addOption(optArch)
			.addOption(optCmnInstSet)
			.addOption(optShimSchema)
			.addOption(optInputShim)
			.addOption(optConfDir)
			.addOption(optDestDir)
			.addOption(optPrefix)
			.addOption(optHelp);

		return opts;
	}

	public boolean verifyParam() {
		boolean ch = true;

		if (cl == null) {
			return false;
		}

		if (cl.getArgs().length > 0) {
			System.err.println("Error: too few arguments");
			ch = false;
		}

		if (configDir != null && !configDir.equals("")) {
			File file = new File(configDir);
			if (!file.exists() || !file.isDirectory()) {
				System.err.println("Error: Configuration file directory path is invalid");
				ch = false;
			}
		}

		if (shimDataPath != null && !shimDataPath.equals("")) {
			File file = new File(shimDataPath);
			if (!file.exists() || !file.isFile()) {
				System.err.println("Error: SHIM file(.xml) path is invalid");
				ch = false;
			}
		}

		if (shimSchemaPath != null && !shimSchemaPath.equals("")) {
			File file = new File(shimSchemaPath);
			if (!file.exists() || !file.isFile()) {
				System.err.println("Error: SHIM schema file(.xsd) path is invalid");
				ch = false;
			}
		}

		if (destDir != null && !destDir.equals("")) {
			File file = new File(destDir);
			if (!file.exists()) {
				file.mkdirs();
			}

			if (!file.isDirectory()) {
				System.err.println("Error: Destination directory path is invalid");
				ch = false;
			}
		}

		return ch;
	}

	public void printHelp() {
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("java -jar MeasurementCycleCodegen.jar [opts]", opts);
	}


	/**
	 * Check and Parse the command line parameters.
	 * @param args command line parameters
	 * @return Flag for success judgements
	 * @throws ParseException
	 */
	public boolean parseParam(String[] args) throws ParseException {
		boolean ch = true;

		PosixParser parser = new PosixParser();
		org.apache.commons.cli.Options opts = generateOpts();

		cl = parser.parse(opts, args);

		if (cl.hasOption(PRINTHELP_LOPT)) {
//			printHelp();
			return false;
		}

		System.out.println();
		if(cl.hasOption(ARCH_NAME_LOPT)) {
			architectureName = cl.getOptionValue(ARCH_NAME_LOPT);
			System.out.println("Architecture: " + architectureName + "");
		} else {
			System.out.println("Architecture: None specified");
		}

		if(cl.hasOption(INSTSET_NAME_LOPT)) {
			instructionSetName = cl.getOptionValue(INSTSET_NAME_LOPT);
			System.out.println("CommonInstructionSet name: " + instructionSetName + "");
		} else {
			System.out.println("CommonInstructionSet name: None specified");
		}

		if (cl.hasOption(SHIM_SCHEMA_LOPT)) {
			shimSchemaPath = cl.getOptionValue(SHIM_SCHEMA_LOPT);
			System.out.println("SHIM Schema: " + shimSchemaPath + "");
		} else {
			System.out.println("SHIM Schema: None specified");
		}

		if (cl.hasOption(INPUT_SHIM_LOPT)) {
			shimDataPath = cl.getOptionValue(INPUT_SHIM_LOPT);
			System.out.println("Input SHIM: " + shimDataPath + "");
		} else {
			System.out.println("Input SHIM: None specified");
		}

		if (cl.hasOption(SHIM_SCHEMA_LOPT)) {
			shimSchemaPath = cl.getOptionValue(SHIM_SCHEMA_LOPT);
			System.out.println("SHIM Schema: " + shimSchemaPath + "");
		} else {
			System.out.println("SHIM Schema: None specified");
		}

		if (cl.hasOption(CONF_DIR_LOPT)) {
			configDir = cl.getOptionValue(CONF_DIR_LOPT);
			System.out.println("Configuration file directory: " + configDir + "");
		} else {
			System.out.println("Configuration file directory: None specified");
		}

		if (cl.hasOption(DEST_DIR_LOPT)) {
			destDir = cl.getOptionValue(DEST_DIR_LOPT);
			System.out.println("Destination directory: " + destDir + "");
		} else {
			System.out.println("Destination directory: .");
		}

		if (cl.hasOption(FILE_PREFIX_LOPT)) {
			prefix = cl.getOptionValue(FILE_PREFIX_LOPT);
			System.out.println("Prefix: " + prefix);
		} else {
			System.out.println("Prefix: None specified");
		}
		System.out.println();

		return ch;
	}
}
