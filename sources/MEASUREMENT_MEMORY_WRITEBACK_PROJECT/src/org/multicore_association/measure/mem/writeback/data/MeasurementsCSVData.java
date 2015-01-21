/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.mem.writeback.data;

import java.text.ParseException;

public class MeasurementsCSVData {
	private String addressSpaceName = null;
	private String subSpaceName = null;
	private String slaveComponentName = null;
	private String masterComponentName = null;
	private String accessTypeName = null;
	private String performanceSetMode = null;
	private float bestLatency = -1.0f;
	private float typicalLatency = -1.0f;
	private float worstLatency = -1.0f;
	private float bestPitch = -1.0f;
	private float typicalPitch = -1.0f;
	private float worstPitch = -1.0f;

	public void setElements(String[] elements) throws ParseException {
		int i = 0;
		boolean modeEnabled = false;

		if (elements.length == 12) {
			modeEnabled = true;
		} else if (elements.length == 11) {
			modeEnabled = false;
		} else {
			/* illegal element size */
			throw new ParseException("Number of Elements Error.", i);
		}

		addressSpaceName = elements[i];
		subSpaceName = elements[++i];
		slaveComponentName = elements[++i];
		masterComponentName = elements[++i];
		if (modeEnabled) {
			performanceSetMode = elements[++i];
		}
		accessTypeName = elements[++i];

		try {
			bestLatency = Float.parseFloat(elements[++i]);
			worstLatency = Float.parseFloat(elements[++i]);
			typicalLatency = Float.parseFloat(elements[++i]);
			bestPitch = Float.parseFloat(elements[++i]);
			worstPitch = Float.parseFloat(elements[++i]);
			typicalPitch = Float.parseFloat(elements[++i]);
		} catch (NumberFormatException e) {
			throw new ParseException(e.getMessage(), i);
		}
	}

	public String getAddressSpaceName() {
		return addressSpaceName;
	}
	public void setAddressSpaceName(String addressSpaceName) {
		this.addressSpaceName = addressSpaceName;
	}
	public String getSubSpaceName() {
		return subSpaceName;
	}
	public void setSubSpaceName(String subSpaceName) {
		this.subSpaceName = subSpaceName;
	}
	public String getSlaveComponentName() {
		return slaveComponentName;
	}
	public void setSlaveComponentName(String slaveComponentName) {
		this.slaveComponentName = slaveComponentName;
	}
	public String getMasterComponentName() {
		return masterComponentName;
	}
	public void setMasterComponentName(String masterComponentName) {
		this.masterComponentName = masterComponentName;
	}
	public String getAccessTypeName() {
		return accessTypeName;
	}
	public void setAccessTypeName(String accessTypeName) {
		this.accessTypeName = accessTypeName;
	}
	public float getBestLatency() {
		return bestLatency;
	}
	public void setBestLatency(float bestLatency) {
		this.bestLatency = bestLatency;
	}
	public float getTypicalLatency() {
		return typicalLatency;
	}
	public void setTypicalLatency(float typicalLatency) {
		this.typicalLatency = typicalLatency;
	}
	public float getWorstLatency() {
		return worstLatency;
	}
	public void setWorstLatency(float worstLatency) {
		this.worstLatency = worstLatency;
	}
	public float getBestPitch() {
		return bestPitch;
	}
	public void setBestPitch(float bestPitch) {
		this.bestPitch = bestPitch;
	}
	public float getTypicalPitch() {
		return typicalPitch;
	}
	public void setTypicalPitch(float typicalPitch) {
		this.typicalPitch = typicalPitch;
	}
	public float getWorstPitch() {
		return worstPitch;
	}
	public void setWorstPitch(float worstPitch) {
		this.worstPitch = worstPitch;
	}

	public String getPerformanceSetMode() {
		return performanceSetMode;
	}

	public void setPerformanceSetMode(String performanceSetMode) {
		this.performanceSetMode = performanceSetMode;
	}
}
