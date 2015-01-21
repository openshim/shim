/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.cycle.generate.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class that contains the configuration.
 *
 */
public abstract class BaseConfig {

	/** Section name. */
	public static final String SECTION_HEADER = "header";
	/** Section name. */
	public static final String SECTION_MAIN = "main";

	/** Entry name. */
	public static final String ENTRY_INCLUDE = "include";
	/** Entry name. */
	public static final String ENTRY_DEFINE = "define";
	/** Entry name. */
	public static final String ENTRY_GLOBAL_VAR = "global_variable";
	/** Entry name. */
	public static final String ENTRY_FUNCTION = "function";
	/** Entry name. */
	public static final String ENTRY_VARIABLE = "variable";

	private String name;
	private List<String> headerInclude;
	private List<String> headerDefine;
	private List<String> headerGlobalValiable;
	private List<String> headerFunction;
	private List<String> localValiable;


	/**
	 * Getting a section name list.
	 * @return Section name list
	 */
	public static List<String> getSectionNameList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(SECTION_HEADER);
		list.add(SECTION_MAIN);
		return list;
	}

	/**
	 * Getting a entry name list of header section.
	 * @return Entry name list of header
	 */
	public static List<String> getHeaderEntryNameList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(ENTRY_INCLUDE);
		list.add(ENTRY_DEFINE);
		list.add(ENTRY_GLOBAL_VAR);
		list.add(ENTRY_FUNCTION);
		return list;
	}

	/**
	 * Getting a entry name list of main section.
	 * @return Entry name list of main section
	 */
	public static List<String> getMainEntryNameList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(ENTRY_VARIABLE);
		return list;
	}


	protected List<String> mutilateData(String data) {
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


	/**
	 * Getter for name.
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Setter for name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Getter for headerInclude.
	 * @return headerInclude
	 */
	public List<String> getHeaderInclude() {
		return headerInclude;
	}
	/**
	 * Setter for headerInclude
	 * @param headerInclude
	 */
	public void setHeaderInclude(String headerInclude) {
		this.headerInclude = mutilateData(headerInclude);
	}
	/**
	 * Getter for headerDefine.
	 * @return headerDefine
	 */
	public List<String> getHeaderDefine() {
		return headerDefine;
	}
	/**
	 * Setter for headerDefine.
	 * @param headerDefine
	 */
	public void setHeaderDefine(String headerDefine) {
		this.headerDefine = mutilateData(headerDefine);
	}
	/**
	 * Getter for headerGlobalValiable.
	 * @return headerGlobalValiable
	 */
	public List<String> getHeaderGlobalValiable() {
		return headerGlobalValiable;
	}
	/**
	 * Setter for headerGlobalValiable.
	 * @param headerGlobalValiable
	 */
	public void setHeaderGlobalValiable(String headerGlobalValiable) {
		this.headerGlobalValiable = mutilateData(headerGlobalValiable);
	}
	/**
	 * Getter for headerFunction.
	 * @return headerFunction
	 */
	public List<String> getHeaderFunction() {
		return headerFunction;
	}
	/**
	 * Setter for headerFunction.
	 * @param headerFunction
	 */
	public void setHeaderFunction(String headerFunction) {
		this.headerFunction = mutilateData(headerFunction);
	}
	/**
	 * Getter for localValiable.
	 * @return localValiable
	 */
	public List<String> getLocalValiable() {
		return localValiable;
	}
	/**
	 * Setter for localValiable.
	 * @param localValiable
	 */
	public void setLocalValiable(String mainValiable) {
		this.localValiable = mutilateData(mainValiable);
	}
}
