/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Browse Button Listener class to select file in workspace.
 */
public class WorkspaceFileSelectionListener implements SelectionListener {

	private static final String TEXT_WINDOW_FILE_REF_TITLE = "Workspace File Selection";

	private Shell shell = null;
	private Text textWidget = null;
	private ElementTreeSelectionDialog dialog = null;

	public WorkspaceFileSelectionListener(Shell shell, Text text) {
		this.shell = shell;
		this.textWidget = text;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		dialog = new ElementTreeSelectionDialog(
				shell, new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider()) {
			@Override
			protected void okPressed() {
				super.okPressed();
			}
		};

		dialog.setTitle(TEXT_WINDOW_FILE_REF_TITLE);
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setDoubleClickSelects(false);
		dialog.setAllowMultiple(false);

		if (dialog.open() == IDialogConstants.OK_ID) {
			IResource resource = (IResource)dialog.getFirstResult();
			if (resource != null) {
				String arg = resource.getFullPath().toString();
				String fileLoc = VariablesPlugin.getDefault().getStringVariableManager()
						.generateVariableExpression("workspace_loc", arg);

				textWidget.setText(fileLoc);
			}
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

}
