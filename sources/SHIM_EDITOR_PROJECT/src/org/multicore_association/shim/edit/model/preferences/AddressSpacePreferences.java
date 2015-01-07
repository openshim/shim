/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.preferences;

/**
 * The preferences to create an AddressSpace instance. 
 */
public class AddressSpacePreferences extends AbstracrtPreferences {
	
	private int numberSubSpace;
	
	private int sizeSubSpace;
	
	private String addressSpaceName;
	
	private String subSpaceName;
	
	private boolean dontConnect;

	public int getNumberSubSpace() {
		return numberSubSpace;
	}

	public void setNumberSubSpace(int numberSubSpace) {
		this.numberSubSpace = numberSubSpace;
	}

	public int getSizeSubSpace() {
		return sizeSubSpace;
	}

	public void setSizeSubSpace(int sizeSubSpace) {
		this.sizeSubSpace = sizeSubSpace;
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

	public boolean isDontConnect() {
		return dontConnect;
	}

	public void setDontConnect(boolean dontConnect) {
		this.dontConnect = dontConnect;
	}
	
}
