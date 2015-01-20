/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * An implementation of listener for resizing the tree viewer columns.
 */
public class TreeViewerColumnResizeListener implements Listener {
	
	private static final int SPAN = 10;

	private Tree tree;

	private int[] minWidths;

	/**
	 * Constructs a new instance of TreeColumnResizeListener.
	 * 
	 * @param tree
	 *            the tree
	 * @param minWidths
	 *            the every column's min width
	 */
	public TreeViewerColumnResizeListener(Tree tree, int[] minWidths) {
		this.tree = tree;
		this.minWidths = minWidths;
	}

	/**
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	@Override
	public void handleEvent(Event event) {
		final TreeColumn[] columns = tree.getColumns();
		if (columns.length == 0) {
			return;
		}

		assert columns.length == minWidths.length;

		Display.getCurrent().asyncExec(new Runnable() {

			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				for (int i = 0; i < columns.length; i++) {
					columns[i].pack();
					int width = columns[i].getWidth() + SPAN;
					if (width < minWidths[i]) {
						width = minWidths[i];
					}
					columns[i].setWidth(width);
				}
			}
		});
	}

	/**
	 * Adds TreeViewerColumnResizeListener to the specified tree.
	 * 
	 * @param tree
	 *            the tree to add listener
	 * @param minWidths
	 *            the every column's min width
	 */
	public static void addTreeViewerColumnResizeListener(Tree tree,
			int[] minWidths) {
		TreeViewerColumnResizeListener listener = new TreeViewerColumnResizeListener(
				tree, minWidths);

		tree.addListener(SWT.FocusIn, listener);
		tree.addListener(SWT.Expand, listener);
		tree.addListener(SWT.Collapse, listener);
	}

}
