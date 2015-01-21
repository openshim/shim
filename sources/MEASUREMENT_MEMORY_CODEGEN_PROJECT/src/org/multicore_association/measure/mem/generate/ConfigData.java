/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.mem.generate;

import java.util.ArrayList;

public class ConfigData {
	/** Section name. */
	public static final String SECTION_MAIN = "main";

	/** Entry name. */
	public static final String ENTRY_INCLUDE = "include";
	/** Entry name. */
	public static final String ENTRY_GLOBAL_VARIABLE = "global_variable";
	/** Entry name. */
	public static final String ENTRY_MACRO = "macro";

	/**
	 * Getting a section name list.
	 * @return Section name list
	 */
	public static ArrayList<String> getSectionNameList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(SECTION_MAIN);
		return list;
	}

	/**
	 * Getting a entry name list of main section.
	 * @return Entry name list of main
	 */
	public static ArrayList<String> getMainEntryNameList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(ENTRY_INCLUDE);
		list.add(ENTRY_GLOBAL_VARIABLE);
		list.add(ENTRY_MACRO);
		return list;
	}

	protected ArrayList<String> mutilateData(String data) {
		if (data == null) {
			return null;
		}

		ArrayList<String> buff = new ArrayList<String>();

		String[] list = data.split("[\r\n]");
		for (int i = 0; i < list.length; i++) {
			list[i].trim();
			if (!list[i].equals("")) {
				buff.add(list[i]);
			}
		}

		return buff;
	}

	private ArrayList<String> mainInclude;
	private ArrayList<String> mainGlobalVariable;
	private ArrayList<String> mainMacro;

	public ArrayList<String> getMainInclude() {
		return mainInclude;
	}

	public void setMainInclude(String mainInclude) {
		this.mainInclude = mutilateData(mainInclude);
	}

	public ArrayList<String> getMainGlobalVariable() {
		return mainGlobalVariable;
	}

	public void setMainGlobalValiable(String mainGlobalVariable) {
		this.mainGlobalVariable = mutilateData(mainGlobalVariable);
	}

	public ArrayList<String> getMainMacro() {
		return mainMacro;
	}

	public void setMainMacro(String mainMacro) {
		this.mainMacro = mutilateData(mainMacro);
	}

}
