/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.cycle.writeback.data;

import org.multicore_association.shim.api.Latency;

public class ExtLatency extends Latency {

	private boolean validValue = false;

	public boolean isValidValue() {
		return validValue;
	}

	public void setValidValue(boolean validValue) {
		this.validValue = validValue;
	}

}
