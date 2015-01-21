/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.ui;

import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Browse Button Listener class to select variables.
 */
public class VariableSelectionListener implements SelectionListener {

	private Shell shell = null;
	private Text textWidget = null;

	public VariableSelectionListener(Shell shell, Text text) {
		this.shell = shell;
		this.textWidget = text;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		StringVariableSelectionDialog dialog =
				new StringVariableSelectionDialog(shell);
		dialog.open();
		String variable = dialog.getVariableExpression();
		if (variable != null) {
			textWidget.insert(variable);
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

}
