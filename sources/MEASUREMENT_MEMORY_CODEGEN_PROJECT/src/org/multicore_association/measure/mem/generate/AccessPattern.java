/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.mem.generate;

public class AccessPattern {
	private String addressSpace;
	private String subSpace;
	private String slaveComponent;
	private String accessType;
	private String accessRW;
	private String accessByteSize;
	public String getAddressSpace() {
		return addressSpace;
	}
	public void setAddressSpace(String addressSpace) {
		this.addressSpace = addressSpace;
	}
	public String getSubSpace() {
		return subSpace;
	}
	public void setSubSpace(String subSpace) {
		this.subSpace = subSpace;
	}
	public String getSlaveComponent() {
		return slaveComponent;
	}
	public void setSlaveComponent(String slaveComponent) {
		this.slaveComponent = slaveComponent;
	}
	public String getAccessType() {
		return accessType;
	}
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}
	public String getAccessRW() {
		return accessRW;
	}
	public void setAccessRW(String accessRW) {
		this.accessRW = accessRW;
	}
	public String getAccessByteSize() {
		return accessByteSize;
	}
	public void setAccessByteSize(String accessByteSize) {
		this.accessByteSize = accessByteSize;
	}
}
