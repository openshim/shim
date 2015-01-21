/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.cycle.generate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.ParseException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.multicore_association.measure.cycle.generate.config.ArchConfig;
import org.multicore_association.measure.cycle.generate.config.InstSetConfig;
import org.multicore_association.measure.cycle.generate.config.Operation;
import org.xml.sax.SAXException;

/**
 * Reading and analysis of a setting file, and generation of a source of a C language.
 */
public class CodeGen {

	private static final String VLT_SOURCE_TEMPLATE_NAME = "source_template.vm";
	private static final String VLT_HEADER_TEMPLATE_NAME = "header_template.vm";
	private static final String VLT_KEY_FUNC_NAME_LIST = "funcNameList";
	private static final String VLT_KEY_TIMESTAMP = "timestamp";
	private static final String VLT_KEY_FILE_NAME = "fileName";
	private static final String VLT_KEY_FUNC_NAME = "funcName";
	private static final String VLT_KEY_INST_SET_CONFIG = "instSetConfig";
	private static final String VLT_KEY_ARCH_CONFIG = "archConfig";
	private static final String VLT_KEY_OUTPUT_CONTROL = "outputControl";

	private static final String FUNC_PREFIX = "cycle";
	private static final String FUNC_SEPARATOR = "__";
	private static final String SOURCE_PREFIX = "cycle";
	private static final String SOURCE_SEPARATOR = "__";
	private static final String SOURCE_EXTENSION = ".c";
	private static final String HEADER_PREFIX = "cycle";
	private static final String HEADER_SEPARATOR = "__";
	private static final String HEADER_EXTENSION = ".h";

	/** Operation name. */
	public static final String CALL_OPERATION = "call";
	/** Operation name. */
	public static final String RETURN_OPERATION = "ret";

	private ParameterParser param;
	private ShimLoader shiml;
	private ConfigLoader confLoader;
	private String timestamp;

	public CodeGen() {
		timestamp = Calendar.getInstance().getTime().toString();
	}

	/**
	 * Source generating process.
	 * This function searches for the setting information parallel
	 * with a combination of Architecture and InstructionSetName,
	 * and generates source cord.
	 * @param archName Architecture configuration name
	 * @param instName CommonInstructionSet configuration name
	 * @param funcName made measurement function name
	 * @param fileName generated file name
	 * @return
	 */
	private boolean dumpCSource(
			String archName, String instName, String funcName, String fileName) {

		ArchConfig archConf = confLoader.searchArchitectureConfig(archName);
		if (archConf == null) {
			System.err.println("Error: archtecture configuration file is not found" + " ("
					+ archName + ")");
			return false;
		}
		InstSetConfig instConf = confLoader.searchInstructionSetConfig(instName);
		if (instConf == null) {
			System.err.println("Error: instruction set configuration file is not found" + " ("
					+ instName + ")");
			return false;
		}

		/* limited by the contents of the SHIM */
		Set<String> shimInstSet = shiml.getInstSet(archName, instName);
		List<Operation> confOpList = instConf.getOperationList();
		List<Operation> removeOpList = new ArrayList<Operation>();
		for (Iterator<Operation> i = confOpList.iterator(); i.hasNext();) {
			Operation op = i.next();
			if (!shimInstSet.contains(op.getName())) {
				removeOpList.add(op);
			}
		}

		if (removeOpList.size() > 0) {
			confOpList.removeAll(removeOpList);
		}

//		for (Iterator<Operation> i = confOpList.iterator(); i.hasNext();) {
//			System.out.println(i.next().getName());
//		}

		/* CALL/RET of the output decision */
		OutputControl outputControl = new OutputControl();
		outputControl.setMeasureCallOp(true);
		outputControl.setMeasureRetOp(true);

		Set<String> shimOpList = shiml.getInstSet(archName, instName);

		if(!shimOpList.contains(CALL_OPERATION)) {
			outputControl.setMeasureCallOp(false);
		}

		if(!shimOpList.contains(RETURN_OPERATION)) {
			outputControl.setMeasureRetOp(false);
		}

		Properties p = new Properties();
		p.setProperty("resource.loader", "class");
		p.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		p.setProperty("input.encoding", "UTF-8");

		Velocity.init(p);
		Velocity.setProperty("file.resource.loader.path", "/");
		VelocityContext context = new VelocityContext();
		context.put(VLT_KEY_INST_SET_CONFIG, instConf);
		context.put(VLT_KEY_ARCH_CONFIG, archConf);
		context.put(VLT_KEY_OUTPUT_CONTROL, outputControl);
		context.put(VLT_KEY_TIMESTAMP, timestamp);
		context.put(VLT_KEY_FUNC_NAME, funcName);
		context.put(VLT_KEY_FILE_NAME, fileName);

		StringWriter writer = new StringWriter();

		Template template = Velocity.getTemplate(VLT_SOURCE_TEMPLATE_NAME);

		template.merge(context, writer);

		File file;
		if (param.getDestDir() != null && !param.getDestDir().equals("")) {
			file = new File(param.getDestDir(), fileName);
		} else {
			file = new File(fileName);
		}

		OutputStream os = null;
		Writer wr = null;
		BufferedWriter bwr = null;
		try {
			os = new FileOutputStream(file);
			wr  = new OutputStreamWriter(os, "UTF-8");
			bwr = new BufferedWriter(wr);
			bwr.write(writer.toString());
			bwr.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (bwr != null) try { bwr.close(); } catch (IOException e) {}
			if (wr  != null) try { wr .close(); } catch (IOException e) {}
			if (os != null) try { os.close(); } catch (IOException e) {}
		}

		writer.flush();

		return true;
	}

	/**
	 * Header generating process.
	 * @param funcNameList list of the functions output for header file
	 * @param fileName generated file name
	 * @return
	 */
	private boolean dumpCHeader(List<String> funcNameList, String fileName) {

		Properties p = new Properties();
		p.setProperty("resource.loader", "class");
		p.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		p.setProperty("input.encoding", "UTF-8");

		Velocity.init(p);
		Velocity.setProperty("file.resource.loader.path", "/");
		VelocityContext context = new VelocityContext();
		context.put(VLT_KEY_FUNC_NAME_LIST, funcNameList);
		context.put(VLT_KEY_TIMESTAMP, timestamp);
		StringWriter writer = new StringWriter();

		Template template = Velocity.getTemplate(VLT_HEADER_TEMPLATE_NAME);

		template.merge(context, writer);

		File file;
		if (param.getDestDir() != null && !param.getDestDir().equals("")) {
			file = new File(param.getDestDir(), fileName);
		} else {
			file = new File(fileName);
		}

		OutputStream os = null;
		Writer wr = null;
		BufferedWriter bwr = null;
		try {
			os = new FileOutputStream(file);
			wr  = new OutputStreamWriter(os, "UTF-8");
			bwr = new BufferedWriter(wr);
			bwr.write(writer.toString());
			bwr.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (bwr != null) try { bwr.close(); } catch (IOException e) {}
			if (wr  != null) try { wr .close(); } catch (IOException e) {}
			if (os != null) try { os.close(); } catch (IOException e) {}
		}

		writer.flush();

		return true;
	}


	/**
	 * generate code main process
	 * @param args
	 */
	public boolean generate(String args[]) {
		boolean ch = true;

		/* parse arguments */
		param = new ParameterParser();

		try {
			ch = param.parseParam(args);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			ch = false;
		}

		/* verify arguments */
		if (ch) {
			ch = param.verifyParam();
		}

		/* if occur the error, to print the usage */
		if (!ch) {
			param.printHelp();
			System.err.println("Abort.");
			return false;
		}

		/* load SHIM data file */
		shiml = new ShimLoader();
		try {
			shiml.loadShim(param.getShimDataPath(), param.getShimSchemaPath());
		} catch (JAXBException e) {
			System.err.println(
					"Error: failed to parse the SHIM file, please check the contents of the file");
			return false;
		} catch (SAXException e) {
			System.err.println(
					"Error: failed to parse the SHIM file, please check the contents of the file");
			return false;
		}

		List<String> errList = shiml.getParseErrList();
		if (errList.size() > 0) {
			System.err.println("Error: input SHIM file validation error");
			for (Iterator<String> i = errList.iterator(); i.hasNext();) {
				System.err.println(i.next());
			}
			System.err.println("Abort.");
			return false;
		}

		/*
		 * extract the combination of Architecture
		 * and InstructionSet name from the SHIM data file
		 */
		if (!shiml.makeInstructionMap(shiml.getSysConf())) {
			return false;
		}

		/* To maintain a list of configuration file */
		confLoader = new ConfigLoader(param.getConfigDir());

		/* generate a code for each combination */
		List<String> funcNameList = new ArrayList<String>();
		Set<String> archSet = shiml.getArchSet();
		String prefix = param.getPrefix();
		StringBuilder sb;

		for (Iterator<String> i = archSet.iterator(); i.hasNext();) {
			String archName = i.next();
			Set<String> instNameSet = shiml.getInstNameSet(archName);

			/* When Architecture name is designated, output is restricted. */
			if (param.getArchitectureName() != null
					&& !param.getArchitectureName().equals("")) {
				if (!param.getArchitectureName().equals(archName)) {
					continue;
				}
			}

			for (Iterator<String> j = instNameSet.iterator(); j.hasNext();) {
				String instName = j.next();

				/* When InstructionSet name is designated, output is restricted. */
				if (param.getInstructionSetName() != null
						&& !param.getInstructionSetName().equals("")) {
					if (!param.getInstructionSetName().equals(instName)) {
						continue;
					}
				}

				String funcName;
				String fileName;

				sb = new StringBuilder();

				if (prefix != null && !prefix.equals("")) {
					sb.append(prefix);
				} else {
					sb.append(SOURCE_PREFIX);
				}
				sb.append(SOURCE_SEPARATOR);
				sb.append(archName);
				sb.append(SOURCE_SEPARATOR);
				sb.append(instName);
				sb.append(SOURCE_EXTENSION);
				fileName = sb.toString();

				sb = new StringBuilder();
				if (prefix != null && !prefix.equals("")) {
					sb.append(prefix);
				} else {
					sb.append(FUNC_PREFIX);
				}
				sb.append(FUNC_SEPARATOR);
				sb.append(archName);
				sb.append(FUNC_SEPARATOR);
				sb.append(instName);
				funcName = sb.toString();
				funcNameList.add(funcName);

				if (!dumpCSource(archName, instName, funcName, fileName)) {
					System.err.println("Generation of [" + fileName + "] failed.");
					System.err.println("Abort.");
					return false;
				}

				System.out.println("Generation of [" + fileName + "] was successful.");
			}
		}

		if (funcNameList.size() > 0) {
			/* generate header file */
			sb = new StringBuilder();
			if (prefix != null && !prefix.equals("")) {
				sb.append(prefix);
			} else {
				sb.append(HEADER_PREFIX);
			}
			sb.append(HEADER_SEPARATOR);
			sb.append(shiml.getSysConf().getName());
			sb.append(HEADER_EXTENSION);

			String fileName = sb.toString();

			if (!dumpCHeader(funcNameList, fileName)) {
				System.err.println("Generation of [" + fileName + "] failed.");
				System.err.println("Abort.");
				return false;
			}

			System.out.println("Generation of [" + fileName + "] was successful.");
		}

		System.out.println("Done.");
		return true;
	}


	/**
	 * main.
	 * @param args Commandline arguments
	 */
	public static void main(String args[]) {
		CodeGen gen = new CodeGen();
		gen.generate(args);
		return;
	}
}
