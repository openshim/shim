/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.cycle.generate.config;

import java.util.ArrayList;
import java.util.List;


public class InstSetConfig extends BaseConfig {

	/** Section name. */
	public static final String SECTION_OPERATION = "operation";

	/** Entry name. */
	public static final String ENTRY_ITERATION = "iteration";
	/** Entry name. */
	public static final String ENTRY_DEPTH = "depth";
	/** Entry name. */
	public static final String ENTRY_TRY = "try";
	/** Entry name. */
	public static final String ENTRY_CONTINUITY = "continuity";
	/** Entry name. */
	public static final String ENTRY_INITIALIZE = "initialize";

	private int headerIteration;
	private int headerDepth = 0;
	private int headerTry = 0;
	private int headerContinuity = 0;
	private List<String> localInitialize;
	private List<Operation> operationList;
	private String name;

	/**
	 * Getting a entry name list of header section.
	 * @return Entry name list of header
	 */
	public static List<String> getHeaderEntryNameList() {
		List<String> list = BaseConfig.getHeaderEntryNameList();
		list.add(ENTRY_ITERATION);
		list.add(ENTRY_DEPTH);
		list.add(ENTRY_TRY);
		list.add(ENTRY_CONTINUITY);
		return list;
	}
	/**
	 * Getting a entry name list of main section.
	 * @return Entry name list of main section
	 */
	public static List<String> getMainEntryNameList() {
		List<String> list = BaseConfig.getMainEntryNameList();
		list.add(ENTRY_INITIALIZE);
		return list;
	}
	/**
	 * Getter for headerIteration.
	 * @return headerIteration
	 */
	public int getHeaderIteration() {
		return headerIteration;
	}
	/**
	 * Setter for headerIteration.
	 * @param headerIteration
	 */
	public void setHeaderIteration(int headerIteration) {
		this.headerIteration = headerIteration;
	}
	/**
	 * Getter for headerDepth.
	 * @return headerDepth
	 */
	public int getHeaderDepth() {
		return headerDepth;
	}
	/**
	 * Setter for headerDepth.
	 * @param headerDepth
	 */
	public void setHeaderDepth(int headerDepth) {
		this.headerDepth = headerDepth;
	}
	/**
	 * Getter for headerTry.
	 * @return headerTry
	 */
	public int getHeaderTry() {
		return headerTry;
	}
	/**
	 * Setter for headerTry.
	 * @param headerTry
	 */
	public void setHeaderTry(int headerTry) {
		this.headerTry = headerTry;
	}
	/**
	 * Getter for headerContinuity.
	 * @return headerContinuity
	 */
	public int getHeaderContinuity() {
		return headerContinuity;
	}
	/**
	 * Setter for headerContinuity.
	 * @param headerContinuity
	 */
	public void setHeaderContinuity(int headerContinuity) {
		this.headerContinuity = headerContinuity;
	}
	/**
	 * Getter for localInitialize.
	 * @return localInitialize
	 */
	public List<String> getLocalInitialize() {
		return localInitialize;
	}
	/**
	 * Setter for localInitialize.
	 * @param localInitialize
	 */
	public void setLocalInitialize(String localInitialize) {
		this.localInitialize = mutilateData(localInitialize);
	}
	/**
	 * Getter for operationList.
	 * @return operationList
	 */
	public List<Operation> getOperationList() {
		return operationList;
	}
	/**
	 * Setter for operationList.
	 * @param operationList
	 */
	public void setOperationList(List<Operation> operationList) {
		this.operationList = operationList;
	}
	/**
	 * Getter for operationList.
	 * @return operationList
	 */
	public void addOperationList(Operation operation) {
		if (this.operationList == null) {
			this.operationList = new ArrayList<Operation>();
		}
		this.operationList.add(operation);
	}
	/**
	 * Setter for operationList.
	 * @param operationList
	 */
	public String formatDepth(int num) {
		return String.format("%05d", num);
	}
	/**
	 * Getter for name.
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Setter for name.
	 * @return name
	 */
	public void setName(String name) {
		this.name = name;
	}
}
