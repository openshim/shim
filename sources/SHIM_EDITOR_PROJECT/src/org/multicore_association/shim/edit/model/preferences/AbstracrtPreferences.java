/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.preferences;


/**
 * Abstract base implementation for making clone a preferences instance.
 */
public abstract class AbstracrtPreferences implements Cloneable {
	
	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
