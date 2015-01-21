/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.cycle.writeback;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	private static final String INSTSET_NAME_DESC = "Target Architecture name";
	private static final String INSTSET_NAME_ARGNAME = "name";

	private static final String SHIM_SCHEMA_OPT = "s";
	private static final String SHIM_SCHEMA_LOPT = "shim-schema";
	private static final String SHIM_SCHEMA_DESC = "SHIM XML schema file path";
	private static final String SHIM_SCHEMA_ARGNAME = "path";

	private static final String RECURSIVE_OPT = "R";
	private static final String RECURSIVE_LOPT = "recursive";
	private static final String RECURSIVE_DESC = "Recursively search the CSV file";

	private static final String PRINTHELP_OPT = "h";
	private static final String PRINTHELP_LOPT = "help";
	private static final String PRINTHELP_DESC = "Show usage";

	private static final String CSV_DIR_OPT = "c";
	private static final String CSV_DIR_LOPT = "input-csv-dir";
	private static final String CSV_DIR_DESC = "Directory path that containts measurement result CSV file.\nIf this option is specified, ignore individual specified CSV file.";
	private static final String CSV_DIR_ARGNAME = "path";

	private static final String INPUT_SHIM_OPT = "in";
	private static final String INPUT_SHIM_LOPT = "input-shim";
	private static final String INPUT_SHIM_DESC = "Input SHIM XML data file";
	private static final String INPUT_SHIM_ARGNAME = "path";

	private static final String OUTPUT_SHIM_OPT = "out";
	private static final String OUTPUT_SHIM_LOPT = "output-shim";
	private static final String OUTPUT_SHIM_DESC = "Output SHIM XML data file";
	private static final String OUTPUT_SHIM_ARGNAME = "path";

	private String csvDir;					//optional
	private List<String> csvFileList;		//mandatory
	private String inShimPath;				//optional
	private String outShimPath;				//mandatory
	private String architectureName;		//optional
	private String instructionSetName;		//optional
	private String shimSchemaPath;			//optional
	private boolean isRecursive;			//optional

	private Options opts;
	private CommandLine cl;

	public ParameterParser() {
		csvDir = "";
		csvFileList = null;
		inShimPath = "";
		outShimPath = "";
		architectureName = "";
		instructionSetName = "";
		shimSchemaPath = "";
		isRecursive = false;

		opts = generateOpts();
		cl = null;
	}

	public String getInShimPath() {
		return inShimPath;
	}
	public List<String> getCsvFileList() {
		return csvFileList;
	}

	public String getCsvDir() {
		return csvDir;
	}

	public String getOutShimPath() {
		return outShimPath;
	}

	public String getMasterComponentArch() {
		return architectureName;
	}

	public String getCommonInstructionName() {
		return instructionSetName;
	}

	public String getShimSchemaPath() {
		return shimSchemaPath;
	}

	public boolean isRecursive() {
		return isRecursive;
	}

	private Options generateOpts() {
		Option optArch;
		Option optCmnInstSet;
		Option optShimSchema;
		Option optRecursive;
		Option optHelp;

		Option optInputShim;
		Option optOutputShim;
		Option optCsvDir;


		optArch = new Option(ARCH_NAME_OPT, ARCH_NAME_LOPT, true, ARCH_NAME_DESC);
		optArch.setArgName(ARCH_NAME_ARGNAME);

		optCmnInstSet = new Option(INSTSET_NAME_OPT, INSTSET_NAME_LOPT, true, INSTSET_NAME_DESC);
		optCmnInstSet.setArgName(INSTSET_NAME_ARGNAME);

		optShimSchema = new Option(SHIM_SCHEMA_OPT, SHIM_SCHEMA_LOPT, true, SHIM_SCHEMA_DESC);
		optShimSchema.setArgName(SHIM_SCHEMA_ARGNAME);

		optRecursive = new Option(RECURSIVE_OPT, RECURSIVE_LOPT, false, RECURSIVE_DESC);

		optHelp= new Option(PRINTHELP_OPT, PRINTHELP_LOPT, false, PRINTHELP_DESC);

		optInputShim = new Option(INPUT_SHIM_OPT, INPUT_SHIM_LOPT, true, INPUT_SHIM_DESC);
		optInputShim.setArgName(INPUT_SHIM_ARGNAME);
//		optInputShim.setRequired(true);

		optOutputShim = new Option(OUTPUT_SHIM_OPT, OUTPUT_SHIM_LOPT, true, OUTPUT_SHIM_DESC);
		optOutputShim.setArgName(OUTPUT_SHIM_ARGNAME);
		optOutputShim.setRequired(true);

		optCsvDir = new Option(CSV_DIR_OPT, CSV_DIR_LOPT, true, CSV_DIR_DESC);
		optCsvDir.setArgName(CSV_DIR_ARGNAME);
		optCsvDir.setRequired(false);

		Options opts = new Options();
		opts.addOption(optArch);
		opts.addOption(optCmnInstSet);
		opts.addOption(optShimSchema);
		opts.addOption(optRecursive);
		opts.addOption(optHelp);
		opts.addOption(optInputShim);
		opts.addOption(optOutputShim);
		opts.addOption(optCsvDir);

		return opts;
	}

	public boolean verifyParam() {
		boolean ch = true;

		if (cl == null) {
			return false;
		}

		if (cl.getArgs() == null || cl.getArgs().length == 0) {
			if (csvDir == null || csvDir.equals("")) {
				System.err.println("Error: too few arguments");
				ch = false;
			}
		}

		if (inShimPath != null && !inShimPath.equals("")) {
			File file = new File(inShimPath);
			if (!file.exists() || !file.isFile()) {
				System.err.println("Error: Input SHIM data file(.xml) path is invalid");
				ch = false;
			}
		}

		if (outShimPath != null && !outShimPath.equals("")) {
			File file = new File(outShimPath);
			if (file.isDirectory()) {
				System.err.println("Error: Output SHIM data file(.xml) path is invalid");
				ch = false;
			}
		}

		if (csvDir != null && !csvDir.equals("")) {
			File file = new File(csvDir);
			if (!file.exists() || !file.isDirectory()) {
				System.err.println("Error: Result CSV file directory path is invalid");
				ch = false;
			}
		} else {

			for (Iterator<String> i = csvFileList.iterator(); i.hasNext();) {
				String path = i.next();
				File file = new File(path);
				if (file.exists() && !file.isDirectory()) {
					continue;
				}

				System.err.println("Error: Result CSV file path is invalid" + "(" + path+ ")");
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

		return ch;
	}

	public void printHelp() {
		HelpFormatter hf = new HelpFormatter();
		String h = "java -jar SetCycleToShim.jar [opts] [CSV files]...";
		hf.printHelp(h, opts);
	}

	/**
	 * Check and Parse the command line parameters.
	 * @param args command line parameters
	 * @return Flag for success judgements
	 * @throws ParseException
	 */
	public boolean parseParam(String[] args) {
		boolean ch = true;

		PosixParser parser = new PosixParser();
		org.apache.commons.cli.Options opts = generateOpts();

		try {
			cl = parser.parse(opts, args);
		} catch (ParseException e) {
//			e.printStackTrace();
			System.err.println(e.getMessage());
//			printHelp();
			return false;
		}

		if (cl.hasOption(PRINTHELP_LOPT)) {
//			printHelp();
			return false;
		}

		System.out.println();

		if(cl.hasOption(INPUT_SHIM_LOPT)) {
			inShimPath = cl.getOptionValue(INPUT_SHIM_LOPT);
			System.out.println("Input SHIM: " + inShimPath + "");
		} else {
			System.out.println("Input SHIM: None specified");
		}

		outShimPath = cl.getOptionValue(OUTPUT_SHIM_LOPT);
		System.out.println("Output SHIM:" + outShimPath + "");


		if (cl.hasOption(CSV_DIR_LOPT)) {
			csvDir = cl.getOptionValue(CSV_DIR_LOPT);
			System.out.println("Result CSV directory: " + csvDir + "");
		} else {
			csvDir = null;
			csvFileList = new ArrayList<String>();
			String[] files = cl.getArgs();
			System.out.println("Result CSV file(s):");
			for (int i = 0; i < files.length; i++) {
				System.out.println("    " + files[i]);
				csvFileList.add(files[i]);
			}
		}

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

		if (cl.hasOption(RECURSIVE_LOPT)) {
			isRecursive = true;
		}
		System.out.println();

		return ch;
	}
}
