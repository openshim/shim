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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.multicore_association.measure.core.commands.RelaunchHistoryCommand;
import org.multicore_association.measure.core.prefs.MainPrefsAccessor;

/**
 * Handler class of command to launch hisutory configuration.
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class HistoryRelaunchCommandHandler extends AbstractHandler {

	/**
	 * The constructor.
	 */
	public HistoryRelaunchCommandHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		String cmdId = event.getCommand().getId();

		if (cmdId.equals(RelaunchHistoryCommand.CMD_ID)) {

			String history = MainPrefsAccessor.getLaunchHistory();
			if (history == null || history.isEmpty()) {
				return null;
			}

			ILaunchConfiguration conf = null;
			try {
				conf = DebugPlugin.getDefault().getLaunchManager().getLaunchConfiguration(history);
			} catch (CoreException e) {
				e.printStackTrace();
				MainPrefsAccessor.setLaunchHistory("");
				return null;
			}

			if (conf != null && conf.exists()) {
//				ILaunch launch = config.launch("run", new ProgressMonitor(PlatformUI.getWorkbench()), false, register);
				DebugUITools.launch(conf, "run");

			} else {
				MainPrefsAccessor.setLaunchHistory("");
			}
		}

		return null;
	}
}
