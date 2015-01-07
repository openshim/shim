/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.multicore_association.shim.edit.gui.swt.viewer.ShimTreeViewerUtil;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;

/**
 * Copies items that is selected in TreeViewe.
 */
public class CopySelectedItemAction extends Action {

	private static final Logger log = ShimLoggerUtil
			.getLogger(CopySelectedItemAction.class);

	private TreeViewer tv;

	/**
	 * Constructs a new instance of CopySelectedItemAction.
	 * 
	 * @param tv
	 *            the tree viewer with this menu
	 */
	public CopySelectedItemAction(TreeViewer tv) {
		this.tv = tv;
	}

	/**
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		return "Copy Item(s)@Ctrl+C";
	}

	/**
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		TreeItem[] selection = tv.getTree().getSelection();
		return ShimTreeViewerUtil.selectSameParentItems(selection);
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		log.finest("[Action] Copy Item start");
		TreeItem[] selections = tv.getTree().getSelection();

		if (selections.length == 1) {
			// single copy
			TreeItem selectedItem = selections[0];
			log.info("Copy: selectItem=" + selectedItem.getText());
			Object _data = selectedItem.getData();
			MyClipboard.Copy(_data);

		} else if (selections.length > 1) {
			// multi copy
			List<Object> selist = new ArrayList<Object>();
			for (TreeItem item : selections) {
				log.info("Copy: selectItem=" + item.getText());
				selist.add(item.getData());
			}
			MyClipboard.Copy(selist);
		}

		tv.refresh();

		super.run();
	}
}
