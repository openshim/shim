/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt;

/**
 * Interface for the SWT resource which needs to refresh the object list.
 */
public interface ObjectListRefresher {

	/**
	 * Refreshes the object list.
	 */
	public void refreshObjectList();
	
}
