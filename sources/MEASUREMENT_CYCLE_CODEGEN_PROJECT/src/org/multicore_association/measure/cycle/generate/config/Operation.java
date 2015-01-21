/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.cycle.generate.config;

/**
 * Class that contains the operation to be measured.
 *
 */
public class Operation {

	private String name;
	private String compareCode;
	private String targetCode;

	/**
	 * Getter for name.
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Setter for name.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Getter for compareCode.
	 * @return compareCode
	 */
	public String getCompareCode() {
		return compareCode;
	}
	/**
	 * Setter for compareCode.
	 * @param compareCode
	 */
	public void setCompareCode(String compareCode) {
		this.compareCode = compareCode;
	}
	/**
	 * Getter for targetCode.
	 * @return targetCode
	 */
	public String getTargetCode() {
		return targetCode;
	}
	/**
	 * Setter for targetCode.
	 * @param targetCode
	 */
	public void setTargetCode(String targetCode) {
		this.targetCode = targetCode;
	}
}
