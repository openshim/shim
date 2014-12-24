/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.actions;

import org.eclipse.jface.action.Action;
import org.multicore_association.shim.edit.gui.jface.ShimSelectableItem;
import org.multicore_association.shim.edit.model.ShimModelManager;

/**
 * Renames components with auto-numbering.
 */
public class ReNumberComponnetSetAction extends Action {

	private ShimSelectableItem wz;

	/**
	 * Constructs a new instance of ReNumberComponnetSetAction.
	 * 
	 * @param wz
	 *            the Shim_SelectableItem with this menu
	 */
	public ReNumberComponnetSetAction(ShimSelectableItem wz) {
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

		ShimModelManager.reNummberComponentName();

		wz.refresh();
		super.run();
	}

}
