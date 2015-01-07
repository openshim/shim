/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.actions;

import org.eclipse.jface.action.Action;
import org.multicore_association.shim.edit.gui.jface.ShimSelectableItem;
import org.multicore_association.shim.edit.model.ShimModelManager;

/**
 * Renames AddressSpaces with auto-numbering.
 */
public class ReNumberAddressSpaceAction extends Action {

	private ShimSelectableItem wz;

	/**
	 * Constructs a new instance of ReNumberAddressSpaceAction.
	 * 
	 * @param wz
	 *            the Shim_SelectableItem with this menu
	 */
	public ReNumberAddressSpaceAction(ShimSelectableItem wz) {
		this.wz = wz;
	}

	/**
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		return "ReNumber ComponentSet Tree Label";
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {

		ShimModelManager.reNumberAddressSpaceName();

		wz.refresh();
		super.run();
	}

}
