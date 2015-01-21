/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.measure.core.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.multicore_association.measure.core.commands.OpenConfigCommand;

/**
 * Handler class of command to open the launch configuration screen.
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class OpenConfigCommandHandler extends AbstractHandler {

	private static final String CONF_TYPE_ID = "org.multicore_association.measure.ui.launchConfigurationType"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public OpenConfigCommandHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		String cmdId = event.getCommand().getId();

		if (cmdId.equals(OpenConfigCommand.CMD_ID)) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

			ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = lm.getLaunchConfigurationType(CONF_TYPE_ID);

			DebugUITools.openLaunchConfigurationDialogOnGroup(shell, new StructuredSelection(type),
					IDebugUIConstants.ID_RUN_LAUNCH_GROUP, null);
		}

		return null;
	}
}
