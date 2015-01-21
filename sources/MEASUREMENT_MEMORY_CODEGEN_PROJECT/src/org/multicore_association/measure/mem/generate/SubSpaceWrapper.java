/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.mem.generate;

import org.multicore_association.shim.api.SubSpace;


public class SubSpaceWrapper {
	private SubSpace subSpace = null;
	private String name = null;

	public SubSpaceWrapper(SubSpace subSpace) {
		this.subSpace = subSpace;
	}

	public SubSpace getSubSpace() {
		return subSpace;
	}

	public void setSubSpace(SubSpace subSpace) {
		this.subSpace = subSpace;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartHex() {
		return Long.toHexString(subSpace.getStart());
	}

	public String getEndHex() {
		return Long.toHexString(subSpace.getEnd());
	}
}
