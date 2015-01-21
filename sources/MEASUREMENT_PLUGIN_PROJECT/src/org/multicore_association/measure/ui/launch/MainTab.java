/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.ui.launch;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.multicore_association.measure.core.launch.ConfIDs;
import org.multicore_association.measure.io.ConsoleStream;

/**
 * Launch configuration setting screen Main tab class
 */
class MainTab extends AbstractLaunchConfigurationTab {

	private static final String TEXT_TAB_NAME = "Main";
	private static final String TEXT_ID = "org.multicore_association.measure.ui.mainTab";

	private static final String TEXT_BUTTON_SELECT = "Browse...";
	private static final String LABEL_RADIO_GROUP = "Process selection";
	private static final String TEXT_RADIO_MEMORY = "Memory Performance Measurement";
	private static final String TEXT_RADIO_CYCLE = "Instruction Cycle Measurement";
	private static final String BAD_CONTAINER = "bad_container_name";
	private static final String CONF_TYPE_ID = "org.multicore_association.measure.ui.launchConfigurationType";

	private Label lblProject = null;
	private Text txtProject = null;
	private Button btnProject = null;

	private Button btnProcess1 = null;
	private Button btnProcess2 = null;

	private boolean isAvailable = false;

	protected void createProjectGroup(Composite parent, int colSpan) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayout(layout);

		GridData gdSpanParent = new GridData(GridData.FILL_HORIZONTAL);
		gdSpanParent.horizontalSpan = colSpan;
		comp.setLayoutData(gdSpanParent);

		GridData gdSpan2 = new GridData(GridData.FILL_HORIZONTAL);
		gdSpan2.horizontalSpan = 2;

		lblProject = new Label(comp, SWT.NONE);
		lblProject.setText("Project:");
		lblProject.setLayoutData(gdSpan2);

		txtProject = new Text(comp, SWT.SINGLE | SWT.BORDER);
		txtProject.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtProject.setEditable(false);
		txtProject.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent evt) {
				scheduleUpdateJob();
			}
		});

		btnProject = createPushButton(comp, TEXT_BUTTON_SELECT, null);
		btnProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IProject[] projects = workspace.getRoot().getProjects();
				ILabelProvider prov = WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider();

				ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), prov);
				dialog.setTitle("Project Selection");
				dialog.setElements(projects);
				ArrayList<IProject> list = new ArrayList<IProject>();
				list.add(workspace.getRoot().getProject());
				dialog.setInitialElementSelections(list);

				if (dialog.open() == IDialogConstants.OK_ID) {
					IResource resource = (IResource)dialog.getFirstResult();

					if (resource == null) {
						return;
					}

					txtProject.setText(resource.getName());
				}
			}
		});
	}

	private boolean checkPioneerConfig(int checkMode) {
		ILaunchConfiguration[] conf = null;
		try {
			ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager()
					.getLaunchConfigurationType(CONF_TYPE_ID);
			conf = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(type);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		boolean flag = true;
		if (conf != null) {
			for (int i = 0; i < conf.length; i++) {
				int mode = -1;
				try {
					mode = conf[i].getAttribute(ConfIDs.KEY_CMN_SELECT_MODE, -1);
				} catch (CoreException e1) {
				}
				if (mode == checkMode) {
					flag = false;
				}
			}
		}

		return flag;
	}

	protected void createProcessSelectorGroup(Composite parent, int colSpan) {
		/* ProcessSelection Group */
		Group grpProcessSelection = new Group(parent, SWT.NONE);
		grpProcessSelection.setText(LABEL_RADIO_GROUP);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		grpProcessSelection.setLayout(layout);

		GridData gd = new GridData(GridData.CENTER);
		gd.horizontalSpan = colSpan;
		grpProcessSelection.setLayoutData(gd);

		btnProcess1 = createRadioButton(grpProcessSelection, TEXT_RADIO_MEMORY);
		btnProcess1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean flag = checkPioneerConfig(0);
				if (flag) {
					getLaunchConfigurationDialog().setName(
							getLaunchConfigurationDialog().generateName(txtProject.getText() + "_MemoryPerform"));
				}
				scheduleUpdateJob();
			}
		});

		btnProcess2 = createRadioButton(grpProcessSelection, TEXT_RADIO_CYCLE);
		btnProcess2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean flag = checkPioneerConfig(1);
				if (flag) {
					getLaunchConfigurationDialog().setName(
							getLaunchConfigurationDialog().generateName(txtProject.getText() + "_InstructionCycle"));
				}
				scheduleUpdateJob();
			}
		});
	}

	private void updateProjectGroup(ILaunchConfiguration config) {
		try {
			String projPath = config.getAttribute(ConfIDs.KEY_CMN_PROJECT, "");
			if (!projPath.isEmpty()) {
				txtProject.setText(projPath);
			}
		} catch (CoreException e) {
			e.printStackTrace(ConsoleStream.getInstance());
		}
	}

	private void updateModeSelection(ILaunchConfiguration config) {
		try {
			int mode = config.getAttribute(ConfIDs.KEY_CMN_SELECT_MODE, 0);

			btnProcess1.setSelection(mode == 0);
			btnProcess2.setSelection(mode == 1);
		} catch (CoreException e) {
			e.printStackTrace(ConsoleStream.getInstance());
		}
	}

	private boolean checkProjectGroup() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject proj;

		try {
			proj = root.getProject(txtProject.getText());
		} catch (IllegalArgumentException e) {
			setErrorMessage("Project is not found.");
			return false;
		}

		if (proj != null && proj.exists()) {
			return true;
		}

		setErrorMessage("Project is not found.");

		return false;
	}

	private boolean checkModeSelection() {
		int count = 0;
		boolean ret = false;
		if (btnProcess1.getSelection()) {
			count++;
		}
		if (btnProcess2.getSelection()) {
			count++;
		}

		if (count == 1) {
			ret = true;
		} else {
			setErrorMessage("Mode is not selected.");
		}

		return ret;
	}

	/**
	 * Initialize to generate each component of the UI.
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), null);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		createVerticalSpacer(comp, 1);
		createProjectGroup(comp, 1);
		createVerticalSpacer(comp, 1);
		createProcessSelectorGroup(comp, 1);

//		new Label(comp, SWT.NONE);
//
//		Link link = new Link(comp, SWT.NONE);
//		link.setText("Configure Instruction Cycke Measurement tool path from the <a>Workspace Settings</a>.");
//		link.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null,
//						"org.multicore_association.measure.core.prefs.page1", null, null);
//				dialog.open();
//			}
//		});
//		link.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//
//		createVerticalSpacer(comp, 1);
	}

	/**
	 * Consistency check of the set value.
	 *
	 * It runs in time scheduleUpdateJob().
	 */
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setMessage(null);
		setErrorMessage(null);

		if (!launchConfig.exists()) {
			setMessage("### Warning: The name has been changed. Also change the name of the launch configuration file. ###");
		}

		isAvailable = checkProjectGroup() && checkModeSelection();

		return isAvailable;
	}

	/**
	 * The default value setting of a new generation of launch configuration.
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	/**
	 * Loading the saved settings.
	 */
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {

		updateProjectGroup(configuration);
		updateModeSelection(configuration);

		try {
			isAvailable = configuration.getAttribute(ConfIDs.KEY_CMN_IS_AVAILABLE, false);
		} catch (CoreException e) {
			isAvailable = false;
		}
	}

	/**
	 * Process of saving settings when applied.
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		String projectPath = txtProject.getText();
		int selectedMode = -1;

		String projName;
		try {
			projName = configuration.getAttribute(ConfIDs.KEY_CMN_PROJECT, "");
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			if (!projName.isEmpty()) {
				IProject proj = root.getProject(projName);
				if (proj == null) {
					configuration.setAttribute(BAD_CONTAINER, projName);
				} else {
					configuration.setContainer(proj);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		/* execute in foreground */
		configuration.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, false);

		if (btnProcess1.getSelection()) {
			selectedMode = 0;
		} else if (btnProcess2.getSelection()) {
			selectedMode = 1;
		}

		configuration.setAttribute(ConfIDs.KEY_CMN_PROJECT, projectPath);
		configuration.setAttribute(ConfIDs.KEY_CMN_SELECT_MODE, selectedMode);
		configuration.setAttribute("org.eclipse.cdt.launch.ATTR_BUILD_BEFORE_LAUNCH_ATTR", 0);
		configuration.setAttribute(ConfIDs.KEY_CMN_IS_AVAILABLE, isAvailable);
	}

	@Override
	public String getId() {
		return TEXT_ID;
	}

	/**
	 * Get the name to be displayed in the tab.
	 */
	@Override
	public String getName() {
		return TEXT_TAB_NAME;
	}

	/**
	 * Get the icon to be displayed in the tab (Not set).
	 */
	@Override
	public Image getImage() {
		return super.getImage();
	}
}
