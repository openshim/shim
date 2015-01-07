/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.preferences;

/**
 * The preferences to create a Performance instance. 
 */
public class PerformancePreferences extends AbstracrtPreferences {
	
	private Float best;
	
	private float typical;
	
	private Float worst;

	public Float getBest() {
		return best;
	}

	public void setBest(Float best) {
		this.best = best;
	}

	public float getTypical() {
		return typical;
	}

	public void setTypical(float typical) {
		this.typical = typical;
	}

	public Float getWorst() {
		return worst;
	}

	public void setWorst(Float worst) {
		this.worst = worst;
	}

}
