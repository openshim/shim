/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.multicore_association.shim.edit.actions.ShimObjectMenuManager;

/**
 * An implementation of SearchableTreeViewer for ComponentSet.
 */
public class ShimComponentTreeViewer extends SearchableTreeViewer implements
		ShimSelectableItem {

	private ShimComponentTreeViewer shimComponentTreeViewer;

	/**
	 * Constructs a new instance of ShimComponentTreeViewer.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	public ShimComponentTreeViewer(Composite parent) {
		this(parent, true);
	}

	/**
	 * Constructs a new instance of ShimComponentTreeViewer.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param createMenu
	 *            whether creates menu or not
	 */
	public ShimComponentTreeViewer(Composite parent, boolean createMenu) {
		super(parent);
		shimComponentTreeViewer = this;

		if (createMenu) {
			new ShimObjectMenuManager(shimComponentTreeViewer);
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.jface.ShimSelectableItem#getSelectedItem()
	 */
	@Override
	public TreeItem getSelectedItem() {
		return this.getSelectedItem();
	}
}
