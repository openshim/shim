/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * Copies and deletes items that is selected in TreeViewe.
 */
public class CutSelectedItemAction extends Action {
	
	private DeleteSelectedItemAction delete;
	private CopySelectedItemAction copy;

	/**
	 * Constructs a new instance of CutSelectedItemAction.
	 * 
	 * @param tv
	 *            the tree viewer with this menu
	 */
	public CutSelectedItemAction(TreeViewer tv) {
		delete = new DeleteSelectedItemAction(tv);
		copy = new CopySelectedItemAction(tv);
	}
	
	/**
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		return "Cut Item(s)@Ctrl+X";
	}
	
	/**
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return delete.isEnabled() && copy.isEnabled();
	}
	
	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		copy.run();
		delete.run();
	}

}
