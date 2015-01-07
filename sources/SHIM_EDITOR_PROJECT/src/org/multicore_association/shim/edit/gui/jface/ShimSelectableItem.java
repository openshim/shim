/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface;

import org.eclipse.swt.widgets.TreeItem;

/**
 * Interface for the SWT resource which contains TreeViewer.
 */
public interface ShimSelectableItem {
	
	/**
	 * Returns the TreeItem which is selected in TreeViewer.
	 * 
	 * @return
	 */
	public TreeItem getSelectedItem();
	
	/**
	 * Refreshes all controls.
	 */
	public void refresh();

}
