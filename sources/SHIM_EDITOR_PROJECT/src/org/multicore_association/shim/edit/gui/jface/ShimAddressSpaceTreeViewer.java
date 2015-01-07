/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface;

import org.eclipse.swt.widgets.Composite;
import org.multicore_association.shim.edit.actions.ShimObjectMenuManager;

/**
 * An implementation of SearchableTreeViewer for AddressSpaceSet.
 */
public class ShimAddressSpaceTreeViewer extends SearchableTreeViewer {

	private ShimAddressSpaceTreeViewer shimAddressSpaceTreeViewer;

	/**
	 * Constructs a new instance of ShimAddressSpaceTreeViewer.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	public ShimAddressSpaceTreeViewer(Composite parent) {
		this(parent, true);
	}

	/**
	 * Constructs a new instance of ShimAddressSpaceTreeViewer.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param createMenu
	 *            whether creates menu or not
	 */
	public ShimAddressSpaceTreeViewer(Composite parent, boolean createMenu) {
		super(parent);
		shimAddressSpaceTreeViewer = this;

		if (createMenu) {
			new ShimObjectMenuManager(shimAddressSpaceTreeViewer);
		}
	}
}
