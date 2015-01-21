/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.ui;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Browse Button Listener class to select file in workspace.
 */
public class FileSelectionListener implements SelectionListener {

	private static final String TEXT_WINDOW_FILE_REF_TITLE = "File Selection";

	private Shell shell = null;
	private Text textWidget = null;
	private int mode = SWT.OPEN;

	public FileSelectionListener(Shell shell, Text text) {
		this.shell = shell;
		this.textWidget = text;
	}
	public FileSelectionListener(Shell shell, Text text, int mode) {
		this.shell = shell;
		this.textWidget = text;
		this.mode = mode;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		FileDialog dialog = new FileDialog(shell, mode);
		dialog.setText(TEXT_WINDOW_FILE_REF_TITLE);
		File inFile = new Path(textWidget.getText()).toFile();

		String inPathStr;
		if (inFile.isFile() || !inFile.exists()) {
			inPathStr = inFile.getParent();
		} else {
			inPathStr = inFile.toString();
		}
		dialog.setFilterPath(inPathStr);

		String path = dialog.open();
		if (textWidget != null && path != null) {
			textWidget.setText(path);
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

}
