/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Browse Button Listener class to select directory.
 */
public class DirectorySelectionListener implements SelectionListener {

	private static final String TEXT_WINDOW_DIR_REF_TITLE = "Directory Selection";

	private Text text = null;
	private Shell shell = null;

	public DirectorySelectionListener(Shell shell, Text text) {
		super();
		this.text = text;
		this.shell = shell;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
		dialog.setText(TEXT_WINDOW_DIR_REF_TITLE);
		String path = dialog.open();
		if (text != null && path != null) {
			text.setText(path);
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		return;
	}
}
