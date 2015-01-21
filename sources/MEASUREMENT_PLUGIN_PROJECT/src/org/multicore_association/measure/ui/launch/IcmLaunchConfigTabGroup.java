/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.ui.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.multicore_association.measure.core.launch.ConfIDs;

/**
 * Launch configuration screen tab group class.
 */
public class IcmLaunchConfigTabGroup extends AbstractLaunchConfigurationTabGroup {

	private static final String BAD_CONTAINER = "bad_container_name";

	/**
	 * Generate tab to be displayed on the configuration screen.
	 */
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {

		setTabs(new ILaunchConfigurationTab[] {
				new MainTab(),
				new MemoryPerformanceTab(),
				new InstructionCycleTab(),
		});

	}

	/**
	 * Initial setting of the new generation of launch configuration.
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		super.setDefaults(configuration);

		IStringVariableManager svm = VariablesPlugin.getDefault().getStringVariableManager();
		String name = "";
		String confName = null;
		try {
			name = svm.performStringSubstitution("${project_name}");
		} catch (CoreException e) {
		}

		try {
			confName = configuration.getAttribute(ConfIDs.KEY_CMN_PROJECT, "");
		} catch (CoreException e) {
		}
		if (name.isEmpty()) {
			if (!confName.isEmpty()) {
				name = confName;
			} else {
				name = "(ProjectIsNotSet)";
			}
		}

		if (!name.isEmpty()) {
			ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
			configuration.rename(lm.generateLaunchConfigurationName(name + "_NewConfiguration"));

			configuration.setAttribute(ConfIDs.KEY_CMN_PROJECT, name);
			configuration.setAttribute(ConfIDs.KEY_CMN_SELECT_MODE, -1);

			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IProject proj = root.getProject(name);

			if (proj == null || !proj.exists()) {
				configuration.setAttribute(BAD_CONTAINER, name);
				configuration.setContainer(null);
			} else {
				configuration.setContainer(proj);
			}
		}
	}

}
