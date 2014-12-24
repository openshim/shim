/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.preferences;

/**
 * The preferences to create a MasterComponent instance. 
 */
public class MasterComponentPreferences extends AbstracrtPreferences {
	
	private String baseName;
	
	private int type;
	
	private int endian;
	
	private String arch;
	
	private String archOpt;
	
	private Integer nChannel;
	
	private Integer nThread;
	
	private boolean checkClockForMaster;
	
	private float clock;

	public String getBaseName() {
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getEndian() {
		return endian;
	}

	public void setEndian(int endian) {
		this.endian = endian;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public String getArchOpt() {
		return archOpt;
	}

	public void setArchOpt(String archOpt) {
		this.archOpt = archOpt;
	}

	public Integer getnChannel() {
		return nChannel;
	}

	public void setnChannel(Integer nChannel) {
		this.nChannel = nChannel;
	}

	public Integer getnThread() {
		return nThread;
	}

	public void setnThread(Integer nThread) {
		this.nThread = nThread;
	}
	
	public boolean isCheckClockForMaster() {
		return checkClockForMaster;
	}
	
	public void setCheckClockForMaster(boolean checkClockForMaster) {
		this.checkClockForMaster = checkClockForMaster;
	}
	
	public float getClock() {
		return clock;
	}
	
	public void setClock(float clock) {
		this.clock = clock;
	}
	
}
