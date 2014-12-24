/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.preferences;

/**
 * The preferences to create a SlaveComponent instance.
 */
public class SlaveComponentPreferences extends AbstracrtPreferences {

	private String baseName;

	private int size;

	private int sizeUnit;

	private int rwType;

	public String getBaseName() {
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSizeUnit() {
		return sizeUnit;
	}

	public void setSizeUnit(int sizeUnit) {
		this.sizeUnit = sizeUnit;
	}

	public int getRwType() {
		return rwType;
	}

	public void setRwType(int rwType) {
		this.rwType = rwType;
	}
}
