/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.preferences;

/**
 * The preferences whether the preferences save or not. 
 */
public class SavePolicyPreferences {
	
	private boolean alwaysSave;
	
	private boolean saveOnRequest;
	
	private boolean neverSave;

	public boolean isAlwaysSave() {
		return alwaysSave;
	}

	public void setAlwaysSave(boolean alwaysSave) {
		this.alwaysSave = alwaysSave;
	}

	public boolean isSaveOnRequest() {
		return saveOnRequest;
	}

	public void setSaveOnRequest(boolean saveOnRequest) {
		this.saveOnRequest = saveOnRequest;
	}

	public boolean isNeverSave() {
		return neverSave;
	}

	public void setNeverSave(boolean neverSave) {
		this.neverSave = neverSave;
	}
}
