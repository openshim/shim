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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.multicore_association.measure.core.commands.LaunchCommand;
import org.multicore_association.measure.core.launch.ConfIDs;

/**
 * Handler class of command to launch selected process.
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class LaunchCommandHandler extends AbstractHandler {

	private static final String CONF_TYPE_ID = "org.multicore_association.measure.ui.launchConfigurationType";

	private static final String[] BASE_NAME = {
		"_MemoryPerform", "_InstructionCycle",
//		"_CDFGGeneration", "_CDFGAnalysis",
	};

	/**
	 * The constructor.
	 */
	public LaunchCommandHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		String cmdId = event.getCommand().getId();
		String memento = event.getParameter(LaunchCommand.CMD_PARAM_ID_MEMENTO);
		String valType = event.getParameter(LaunchCommand.CMD_PARAM_ID_TYPE);
		String baseName = BASE_NAME[Integer.valueOf(valType)];

		if (cmdId.equals(LaunchCommand.CMD_ID)) {
			if (memento == null || memento.isEmpty()) {
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

				IStringVariableManager svm = VariablesPlugin.getDefault().getStringVariableManager();
				ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
				ILaunchConfigurationType type = lm.getLaunchConfigurationType(CONF_TYPE_ID);
				ILaunchConfigurationWorkingCopy wc = null;

				try {
					String projName = svm.performStringSubstitution("${project_name}"); //$NON-NLS-1$
					String confName = projName + lm.generateLaunchConfigurationName(baseName);
					wc = type.newInstance(null, confName);

					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					IWorkspaceRoot root = workspace.getRoot();
					IProject proj = root.getProject(projName);

					wc.setContainer(proj);

					wc.setAttribute(ConfIDs.KEY_CMN_PROJECT, projName);
					wc.setAttribute(ConfIDs.KEY_CMN_SELECT_MODE, Integer.valueOf(valType));

					/* see setDefautl in each Tabs */
					wc.setAttribute(ConfIDs.KEY_MEM_INPUT_SHIM, "${project_log}/data/shim.xml");
					wc.setAttribute(ConfIDs.KEY_MEM_OUTPUT_SHIM, "${project_loc}/Measurement/shim_output.xml");
					wc.setAttribute(ConfIDs.KEY_MEM_OVERWRITE, false);
					wc.setAttribute(ConfIDs.KEY_MEM_CONF_FILE, "${project_log}/data/config.cfg");
					wc.setAttribute(ConfIDs.KEY_MEM_COMMAND, "echo ${GenerateCodePath}; touch ${ResultFilePath};");
					wc.setAttribute(ConfIDs.KEY_MEM_CODE_FILE, "${project_loc}/Measurement/MemoryPerformance.c");
					wc.setAttribute(ConfIDs.KEY_MEM_RSLT_FILE, "${project_loc}/Measurement/MemoryPerformance.csv");
					wc.setAttribute(ConfIDs.KEY_CYC_INPUT_SHIM, "${project_loc}/data/in_shim.xml");
					wc.setAttribute(ConfIDs.KEY_CYC_OUTPUT_SHIM, "${project_loc}/data/out_shim");
					wc.setAttribute(ConfIDs.KEY_CYC_CONFDIR_PATH, "${project_loc}/data/InstructionCycle/");
					wc.setAttribute(ConfIDs.KEY_CYC_COMMAND, "echo ${GenerateCodePath}; touch ${ResultFilePath};");
					wc.setAttribute(ConfIDs.KEY_CYC_DESTDIR_PATH, "${project_loc}/Measurement/InstructionCycle/Code/");
					wc.setAttribute(ConfIDs.KEY_CYC_RSLTDIR_PATH, "${project_loc}/Measurement/InstructionCycle/Csv/");


					wc.doSave();
				} catch (CoreException ex) {
					return null;
				}

				DebugUITools.openLaunchConfigurationDialog(shell, wc, IDebugUIConstants.ID_RUN_LAUNCH_GROUP, null);
			} else {
				ILaunchConfiguration conf = null;
				try {
					conf = DebugPlugin.getDefault().getLaunchManager().getLaunchConfiguration(memento);
				} catch (CoreException e) {
					e.printStackTrace();
					return null;
				}
				if (conf != null && conf.exists()) {
					DebugUITools.launch(conf, "run");
				}
			}
		}

		return null;
	}
}
