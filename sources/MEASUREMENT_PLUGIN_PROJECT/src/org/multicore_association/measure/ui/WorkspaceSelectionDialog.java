/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Dialog component class to select a workspace.
 */
public class WorkspaceSelectionDialog extends ElementTreeSelectionDialog {

	private IProject project = null;

	public WorkspaceSelectionDialog(Shell shell,
			WorkbenchLabelProvider wlp, BaseWorkbenchContentProvider bwcp) {
		super(shell, wlp, bwcp);
	}

	@Override
	public final void setInput(Object input) {
		this.project = (IProject)input;
		super.setInput(input);
	}

	@Override
	public final void setInitialSelection(Object initialPath) {
		if (initialPath == null) {
			return;
		}

		IPath path = project.getLocation().append((String)initialPath);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IPath rel = path.makeRelativeTo(root.getLocation());
		IFile file = root.getFile(rel);

		super.setInitialSelection(file);
	}

}
