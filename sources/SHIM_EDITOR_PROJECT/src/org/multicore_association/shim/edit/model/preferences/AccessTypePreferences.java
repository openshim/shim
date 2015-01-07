/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.preferences;

/**
 * The preferences to create an AccessType instance. 
 */
public class AccessTypePreferences extends AbstracrtPreferences {
	
	private String baseName;
	
	private boolean checkR;
	
	private boolean checkW;
	
	private boolean checkRW;
	
	private boolean checkRWX;
	
	private boolean checkRX;
	
	private boolean checkX;
	
	private String accessByteSize;
	
	private Integer nBurst;

	public String getBaseName() {
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public boolean isCheckR() {
		return checkR;
	}

	public void setCheckR(boolean checkR) {
		this.checkR = checkR;
	}

	public boolean isCheckW() {
		return checkW;
	}

	public void setCheckW(boolean checkW) {
		this.checkW = checkW;
	}

	public boolean isCheckRW() {
		return checkRW;
	}

	public void setCheckRW(boolean checkRW) {
		this.checkRW = checkRW;
	}

	public boolean isCheckRWX() {
		return checkRWX;
	}

	public void setCheckRWX(boolean checkRWX) {
		this.checkRWX = checkRWX;
	}

	public boolean isCheckRX() {
		return checkRX;
	}

	public void setCheckRX(boolean checkRX) {
		this.checkRX = checkRX;
	}

	public boolean isCheckX() {
		return checkX;
	}

	public void setCheckX(boolean checkX) {
		this.checkX = checkX;
	}

	public String getAccessByteSize() {
		return accessByteSize;
	}

	public void setAccessByteSize(String accessByteSize) {
		this.accessByteSize = accessByteSize;
	}

	public Integer getNBurst() {
		return nBurst;
	}

	public void setNBurst(Integer nBurst) {
		this.nBurst = nBurst;
	}
	
}
