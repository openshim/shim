/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.cycle.generate;

/**
 * Class that contains the output control data of Velocity.
 *
 */
public class OutputControl {

	private String timestamp;
	private boolean isMeasureCallOp = true;
	private boolean isMeasureRetOp = true;

	/**
	 * Getter for timestamp.
	 * @return name
	 */
	public String getTimestamp() {
		return timestamp;
	}
	/**
	 * Setter for timestamp
	 * @param name
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * Getter for isMeasureCallOp.
	 * @return isMeasureCallOp
	 */
	public boolean isMeasureCallOp() {
		return isMeasureCallOp;
	}
	/**
	 * Setter for isMeasureCallOp.
	 * @param isMeasureCallOp
	 */
	public void setMeasureCallOp(boolean isMeasureCallOp) {
		this.isMeasureCallOp = isMeasureCallOp;
	}
	/**
	 * Getter for isMeasureRetOp.
	 * @return isMeasureRetOp
	 */
	public boolean isMeasureRetOp() {
		return isMeasureRetOp;
	}
	/**
	 * Setter for isMeasureRetOp.
	 * @param isMeasureRetOp
	 */
	public void setMeasureRetOp(boolean isMeasureRetOp) {
		this.isMeasureRetOp = isMeasureRetOp;
	}
}
