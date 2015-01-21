/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.mem.generate;

import java.util.ArrayList;

import org.multicore_association.shim.api.MasterComponent;

public class CPU {
	private String name;
	private ArrayList<AccessPattern> AccessPatternList;
	private MasterComponent masterComponent;

	public CPU() {
		this.name = "";
		this.AccessPatternList = new ArrayList<AccessPattern>();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<AccessPattern> getAccessPatternList() {
		return AccessPatternList;
	}

	public void addAccessPattern(AccessPattern accessPattern) {
		AccessPatternList.add(accessPattern);
	}

	public MasterComponent getMasterComponent() {
		return masterComponent;
	}

	public void setMasterComponent(MasterComponent masterComponent) {
		this.masterComponent = masterComponent;
	}
}
