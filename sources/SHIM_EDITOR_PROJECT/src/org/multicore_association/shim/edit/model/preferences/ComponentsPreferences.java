/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.preferences;

/**
 * The preferences to create a SystemConfiguration instance and components. 
 */
public class ComponentsPreferences extends AbstracrtPreferences {

	private String systemName;

	private int numberMaster;

	private int numberSlave;

	private int numberComponent;

	private float clock;

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public int getNumberMaster() {
		return numberMaster;
	}

	public void setNumberMaster(int numberMaster) {
		this.numberMaster = numberMaster;
	}

	public int getNumberSlave() {
		return numberSlave;
	}

	public void setNumberSlave(int numberSlave) {
		this.numberSlave = numberSlave;
	}

	public int getNumberComponent() {
		return numberComponent;
	}

	public void setNumberComponent(int numberComponent) {
		this.numberComponent = numberComponent;
	}

	public float getClock() {
		return clock;
	}

	public void setClock(float clock) {
		this.clock = clock;
	}
	
}
