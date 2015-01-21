/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.ui.launch;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
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
import org.multicore_association.measure.core.launch.ConfIDs;
import org.multicore_association.measure.io.ConsoleStream;
import org.multicore_association.measure.ui.DirectorySelectionListener;
import org.multicore_association.measure.ui.FileSelectionListener;
import org.multicore_association.measure.ui.VariableSelectionListener;
import org.multicore_association.measure.ui.WorkspaceDirSelectionListener;
import org.multicore_association.measure.ui.WorkspaceFileSelectionListener;

/**
 * Launch configuration setting screen InstructionCycle tab class.
 */
class InstructionCycleTab extends AbstractLaunchConfigurationTab {

	private static final String TEXT_TAB_NAME = "Instruction Cycle";
	private static final String TEXT_BUTTON_SELECT = "Browse...";
	private static final String TEXT_LABEL_CHECK_GENCODE = "Code generation";
	private static final String TEXT_LABEL_CHECK_EXECUTE = "Measurement";
	private static final String TEXT_LABEL_INPUT_SHIM = "Input SHIM file:";
	private static final String TEXT_LABEL_OUTPUT_SHIM = "Output SHIM file:";
	private static final String TEXT_LABEL_CONF_DIR = "Configuration directory:";

	private static final String TEXT_LABEL_SELECT_SCRIPT = "Command:";
	private static final String TEXT_LABEL_SELECT_DESTDIR = "Generated code destination directory:";
	private static final String TEXT_LABEL_SELECT_RESULT = "Result CSV file location:";
	private static final String TEXT_LABEL_VIEW_OUTPUT_SHIM = "View the output SHIM file";
	private static final String TEXT_LABEL_OVERWRITE = "Overwrite the results to the input SHIM file.";

	private static final String TEXT_BUTTON_FILESYSTEM = "Filesystem...";
	private static final String TEXT_BUTTON_WORKSPACE = "Workspace...";
	private static final String TEXT_BUTTON_VARIABLES = "Variables...";
	private static final String TEXT_LABEL_DIRECTORY = "Directory:";

	/* UI Components */
	private Group grpProcessSelection;
	private Button btnProcess1;
	private Button btnProcess2;

	private Group grpCodeGenConfig;
	private Label lblInputShim;
	private Text txtInputShim;
	private Button btnInputShimFS;
	private Button btnInputShimWS;
	private Button btnInputShimVA;

	private Label lblOutputShim;
	private Text txtOutputShim;
	private Button btnOutputShimFS;
	private Button btnOutputShimWS;
	private Button btnOutputShimVA;

	private Button chkOverwrite;
	private Button chkOpenTheOutput;

	private Label lblSelectConfDir;
	private Text txtSelectConfDir;
	private Button btnSelectConfDirFS;
	private Button btnSelectConfDirWS;
	private Button btnSelectConfDirVA;

	private Group grpCommandConfig;
	private Label lblMeasureScript;
	private Text txtMeasureScript;

	private Group grpOptionConfig;

	private Button chkDestDir;
	private Label lblDestDir;
	private Text txtDestDir;
	private Button btnDestDir;

	private Button chkMeasureResult;
	private Label lblMeasureResult;
	private Text txtCsvResultDir;
	private Button btnMeasureResult;

	private boolean isAvailable = false;

	/**
	 *
	 * @param parent
	 */
	private void configureProcessSelectionGroup(Composite parent) {

		/* ProcessSelection Group */
		grpProcessSelection = new Group(parent, SWT.NONE);
		grpProcessSelection.setText("Process selection");
		grpProcessSelection.setLayout(new GridLayout(2, false));

		btnProcess1 = createCheckButton(grpProcessSelection, TEXT_LABEL_CHECK_GENCODE);
		btnProcess1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				controlEnabled(true);
				scheduleUpdateJob();
			}
		});

		btnProcess2 = createCheckButton(grpProcessSelection, TEXT_LABEL_CHECK_EXECUTE);
		btnProcess2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				controlEnabled(true);
				scheduleUpdateJob();
			}
		});
	}

	private void configureViedOutputShim(Composite parent, int horizontalSpan, int verticalSpan) {
		GridData girdSpan = new GridData(GridData.FILL_HORIZONTAL);
		girdSpan.horizontalSpan = horizontalSpan;
		girdSpan.verticalSpan = verticalSpan;

		/* Select viewing SHIM File (Label) */
		chkOpenTheOutput = createCheckButton(parent, TEXT_LABEL_VIEW_OUTPUT_SHIM);
		chkOpenTheOutput.setLayoutData(girdSpan);
		chkOpenTheOutput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scheduleUpdateJob();
			}
		});
	}

	private void configureOverwriteCheck(Composite parent, int horizontalSpan, int verticalSpan) {
		GridData gridSpan = new GridData(GridData.FILL_HORIZONTAL);
		gridSpan.horizontalSpan = horizontalSpan;
		gridSpan.verticalSpan = verticalSpan;

		chkOverwrite = createCheckButton(parent, TEXT_LABEL_OVERWRITE);
		chkOverwrite.setLayoutData(gridSpan);
		chkOverwrite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controlEnabled(true);
				scheduleUpdateJob();
			}
		});
	}

	private void configureCodeGenGroup(Composite parent) {
		/* Code Generation Group */
		grpCodeGenConfig = new Group(parent, SWT.NONE);
		grpCodeGenConfig.setText("Code Generation Settings");
		grpCodeGenConfig.setLayout(new GridLayout(3, true));
		grpCodeGenConfig.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		configureInputShimFile(grpCodeGenConfig);
		configureOutputShimFile(grpCodeGenConfig);
		configureOverwriteCheck(grpCodeGenConfig, 3, 1);
		configureViedOutputShim(grpCodeGenConfig, 3, 1);
		configureSelectConfigrationDirectory(grpCodeGenConfig);
	}

	private void configureCommandExecGroup(Composite parent) {

		/* Code Generation Group */
		grpCommandConfig = new Group(parent, SWT.NONE);
		grpCommandConfig.setText("Measurement Settings");
		grpCommandConfig.setLayout(new GridLayout(1, false));
		grpCommandConfig.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		configureSelectMeasurementScript(grpCommandConfig);
	}

	private void configureOptionGroup(Composite parent) {

		/* Opton Config Gropu */
		grpOptionConfig = new Group(parent, SWT.NONE);
		grpOptionConfig.setText("Option Settings");
		grpOptionConfig.setLayout(new GridLayout(3, false));
		grpOptionConfig.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		/* paddig */
		new Label(parent, SWT.NONE);

		configureSelectDestinationDirectory(grpOptionConfig);
		configureSelectMeasurementResult(grpOptionConfig);
	}

	/**
	 *
	 * @param parent
	 */
	private void configureInputShimFile(Composite parent) {
		/* Input SHIM File (Label) */
		lblInputShim = new Label(parent, SWT.NONE);
		lblInputShim.setText(TEXT_LABEL_INPUT_SHIM);

		txtInputShim = new Text(parent, SWT.BORDER);
		txtInputShim.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtInputShim.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		Composite bcomp = new Composite(parent, GridData.HORIZONTAL_ALIGN_END);
		bcomp.setLayout(new GridLayout(3, false));
		GridLayout ld;
		ld = (GridLayout)bcomp.getLayout();
		ld.marginWidth = 1;
		ld.marginHeight = 1;
		bcomp.setLayoutData(new GridData(SWT.END, SWT.NORMAL, true, false, 3, 1));

		/* Select SHIM File (Button) */
		btnInputShimFS = createPushButton(bcomp, TEXT_BUTTON_FILESYSTEM, null);
		btnInputShimFS.addSelectionListener(new FileSelectionListener(getShell(), txtInputShim));

		btnInputShimWS = createPushButton(bcomp, TEXT_BUTTON_WORKSPACE, null);
		btnInputShimWS.addSelectionListener(new WorkspaceFileSelectionListener(getShell(), txtInputShim));

		btnInputShimVA = createPushButton(bcomp, TEXT_BUTTON_VARIABLES, null);
		btnInputShimVA.addSelectionListener(new VariableSelectionListener(getShell(), txtInputShim));

		Composite ccomp = new Composite(parent, SWT.NONE);
		ccomp.setLayout(new GridLayout(2, true));
		ccomp.setLayoutData(new GridData(SWT.FILL, SWT.NORMAL, true, false, 3, 1));
		ld = (GridLayout)ccomp.getLayout();
		ld.marginWidth = 1;
		ld.marginHeight = 1;
	}

	/**
	 *
	 * @param parent
	 */
	private void configureOutputShimFile(Composite parent) {
		/* Output SHIM File (Label) */
		lblOutputShim = new Label(parent, SWT.NONE);
		lblOutputShim.setText(TEXT_LABEL_OUTPUT_SHIM);

		txtOutputShim = new Text(parent, SWT.BORDER);
		txtOutputShim.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtOutputShim.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		Composite bcomp = new Composite(parent, GridData.HORIZONTAL_ALIGN_END);
		bcomp.setLayout(new GridLayout(3, false));
		GridLayout ld;
		ld = (GridLayout)bcomp.getLayout();
		ld.marginWidth = 1;
		ld.marginHeight = 1;
		bcomp.setLayoutData(new GridData(SWT.END, SWT.NORMAL, true, false, 3, 1));

		/* Select SHIM File (Button) */
		btnOutputShimFS = createPushButton(bcomp, TEXT_BUTTON_FILESYSTEM, null);
		btnOutputShimFS.addSelectionListener(new FileSelectionListener(getShell(), txtOutputShim));

		btnOutputShimWS = createPushButton(bcomp, TEXT_BUTTON_WORKSPACE, null);
		btnOutputShimWS.addSelectionListener(new WorkspaceFileSelectionListener(getShell(), txtOutputShim));

		btnOutputShimVA = createPushButton(bcomp, TEXT_BUTTON_VARIABLES, null);
		btnOutputShimVA.addSelectionListener(new VariableSelectionListener(getShell(), txtOutputShim));

		Composite ccomp = new Composite(parent, SWT.NONE);
		ccomp.setLayout(new GridLayout(2, true));
		ccomp.setLayoutData(new GridData(SWT.FILL, SWT.NORMAL, true, false, 3, 1));
		ld = (GridLayout)ccomp.getLayout();
		ld.marginWidth = 1;
		ld.marginHeight = 1;
	}

	/**
	 *
	 * @param parent
	 */
	private void configureSelectConfigrationDirectory(Composite parent) {
		/* Select Config (Label) */
		lblSelectConfDir = new Label(parent, SWT.NONE);
		lblSelectConfDir.setText(TEXT_LABEL_CONF_DIR);

		/* Select Config (Text) */
		txtSelectConfDir = new Text(parent, SWT.BORDER);
		txtSelectConfDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtSelectConfDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		Composite bcomp = new Composite(parent, GridData.HORIZONTAL_ALIGN_END);
		bcomp.setLayout(new GridLayout(3, false));
		GridLayout ld = (GridLayout)bcomp.getLayout();
		ld.marginWidth = 1;
		ld.marginHeight = 1;
		bcomp.setLayoutData(new GridData(SWT.END, SWT.NORMAL, true, false, 3, 1));

		/* Select Config (Button) */
		btnSelectConfDirFS = createPushButton(bcomp, TEXT_BUTTON_FILESYSTEM, null);
		btnSelectConfDirFS.addSelectionListener(new DirectorySelectionListener(getShell(), txtSelectConfDir));

		btnSelectConfDirWS = createPushButton(bcomp, TEXT_BUTTON_WORKSPACE, null);
		btnSelectConfDirWS.addSelectionListener(new WorkspaceDirSelectionListener(getShell(), txtSelectConfDir));

		btnSelectConfDirVA = createPushButton(bcomp, TEXT_BUTTON_VARIABLES, null);
		btnSelectConfDirVA.addSelectionListener(new VariableSelectionListener(getShell(), txtSelectConfDir));
	}

	/**
	 *
	 * @param parent
	 */
	private void configureSelectMeasurementScript(Composite parent) {
		/* Select Measurement Script (Label) */
		lblMeasureScript = new Label(parent, SWT.NONE);
		lblMeasureScript.setText(TEXT_LABEL_SELECT_SCRIPT);

		/* Select Measurement Script (Text) */
		txtMeasureScript = new Text(parent, SWT.BORDER);
		txtMeasureScript.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtMeasureScript.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});
	}

	/**
	 *
	 * @param parent
	 */
	private void configureSelectDestinationDirectory(Composite parent) {
		GridData gridSpan3 = new GridData(GridData.FILL_HORIZONTAL);
		gridSpan3.horizontalSpan = 3;

		chkDestDir = createCheckButton(parent, TEXT_LABEL_SELECT_DESTDIR);
		chkDestDir.setLayoutData(gridSpan3);
		chkDestDir.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				controlEnabled(true);
				scheduleUpdateJob();
			}
		});

		/* Select Measurement Code Dir (Label) */
		lblDestDir = new Label(parent, SWT.NONE);
		lblDestDir.setText(TEXT_LABEL_DIRECTORY);

		/* Select Measurement Code Dir (Text) */
		txtDestDir = new Text(parent, SWT.BORDER);
		txtDestDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtDestDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		/* Select Measurement Code Dir (Button) */
		btnDestDir = createPushButton(parent, TEXT_BUTTON_SELECT, null);
		btnDestDir.addSelectionListener(new DirectorySelectionListener(getShell(), txtDestDir));
	}

	/**
	 *
	 * @param parent
	 */
	private void configureSelectMeasurementResult(Composite parent) {
		GridData gridSpan3 = new GridData(GridData.FILL_HORIZONTAL);
		gridSpan3.horizontalSpan = 3;

		chkMeasureResult = createCheckButton(parent, TEXT_LABEL_SELECT_RESULT);
		chkMeasureResult.setLayoutData(gridSpan3);
		chkMeasureResult.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controlEnabled(true);
				scheduleUpdateJob();
			}
		});

		/* Select Measurement Result Dir (Label) */
		lblMeasureResult = new Label(parent, SWT.NONE);
		lblMeasureResult.setText(TEXT_LABEL_DIRECTORY);

		/* Select Measurement Result Dir (Text) */
		txtCsvResultDir = new Text(parent, SWT.BORDER);
		txtCsvResultDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		txtCsvResultDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		/* Select Measurement Result Dir (Button) */
		btnMeasureResult = createPushButton(parent, TEXT_BUTTON_SELECT, null);
		btnMeasureResult.addSelectionListener(
				new DirectorySelectionListener(getShell(), txtCsvResultDir));
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
		configureProcessSelectionGroup(comp);
		configureCodeGenGroup(comp);
		configureCommandExecGroup(comp);
		configureOptionGroup(comp);
		createVerticalSpacer(comp, 1);

		checkPage();
	}

	/**
	 * Input value check.
	 */
	private boolean checkPage() {
		IStringVariableManager svm = VariablesPlugin.getDefault().getStringVariableManager();

		boolean chProc1 = btnProcess1.getSelection();
		boolean chProc2 = btnProcess2.getSelection();
		boolean chOutShim = (!btnProcess1.getSelection() || (chProc2 && (!chkOverwrite.getSelection())));

		/* Process selection group */
		if (!btnProcess1.getSelection() && !btnProcess2.getSelection()) {
			setErrorMessage("Select one or more processing.");
			return false;
		}

		if (chProc1) {
			/* Input SHIM File */
			if (txtInputShim.getText().isEmpty()) {
				setErrorMessage("Input SHIM path is empty.");
				return false;
			}

			String inShimText = "";
			try {
				inShimText = svm.performStringSubstitution(txtInputShim.getText());
			} catch (CoreException e) {
//				e.printStackTrace();
				setErrorMessage("Input SHIM path is invalid.");
				return false;
			}

			File inShimFile = new File(inShimText);
			if (inShimFile == null || !inShimFile.exists() || !inShimFile.isFile()) {
				setErrorMessage("Input SHIM file does not exist.");
				return false;
			}
		}

		if (chOutShim) {
			/* Output SHIM File */
			if (txtOutputShim.getText().isEmpty()) {
				setErrorMessage("Output SHIM file path is empty.");
				return false;
			}

			String outShimText = "";
			try {
				outShimText = svm.performStringSubstitution(txtOutputShim.getText());
			} catch (CoreException e) {
//				e.printStackTrace();
				setErrorMessage("Output SHIM path is invalid.");
				return false;
			}

			File outShimFile = new File(outShimText);
			if (outShimFile == null || (outShimFile.exists() && !outShimFile.isFile())) {
				setErrorMessage("Output SHIM path is invalid.");
				return false;
			}
		}


		/* Conf Dir */
		if (chProc1) {
			if (btnProcess1.getSelection()) {
				if (txtSelectConfDir.getText().isEmpty()) {
					setErrorMessage("Config directory path is empty.");
					return false;
				}

				String confText = "";
				try {
					confText = svm.performStringSubstitution(txtSelectConfDir.getText());
				} catch (CoreException e) {
//					e.printStackTrace();
				}

				File confFile = new File(confText);
				if (confFile == null || !confFile.exists() || confFile.isFile()) {
					setErrorMessage("Config directory path is invalid.");
					return false;
				}
			}
		}

		/* Command */
		if (chProc2) {
			if (txtMeasureScript.getText().isEmpty()) {
				setErrorMessage("Measurement script file path is empty.");
				return false;
			}
		}

		/* Dest Dir */
		if (chProc1 ^ chProc2) {
			if (!chkDestDir.getSelection()) {
				setErrorMessage("You need to select the measurement code.");
				return false;
			}
		}

		if (chkDestDir.getSelection()) {
			//check Dir
			Path path = new Path(txtDestDir.getText());
			File file = path.toFile();

			if (txtDestDir.getText().isEmpty()) {
				setErrorMessage("Code destination directory path is empty.");
				return false;
			}

			if (file == null || !file.isDirectory()) {
				setErrorMessage("Code destination directory path is invalid.");
				return false;
			}
		}

		/* Result CSV Dir */
		if (chProc2 && chkMeasureResult.getSelection()) {
			//check file
			Path path = new Path(txtCsvResultDir.getText());
			File file = path.toFile();

			if (txtCsvResultDir.getText().isEmpty()) {
				setErrorMessage("Result csv directory path is empty.");
				return false;
			}

			if (file == null || !file.isDirectory()) {
				setErrorMessage("Result csv directory path is invalid.");
				return false;
			}
		}

		setErrorMessage(null);
		return true;
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
			setMessage("### Warning: The name has been changed. "
					+ "Also change the name of the launch configuration file. ###");
		}

		try {
			int mode = launchConfig
					.getAttribute(ConfIDs.KEY_CMN_SELECT_MODE, 4);
			if (mode != 1) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace(ConsoleStream.getInstance());
		}

		isAvailable = checkPage();

		return isAvailable;
	}

	/**
	 * The default value setting of a new generation of launch configuration.
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy conf) {
		conf.setAttribute(ConfIDs.KEY_CYC_INPUT_SHIM, "${project_loc}/data/in_shim.xml");
		conf.setAttribute(ConfIDs.KEY_CYC_OUTPUT_SHIM, "${project_loc}/data/out_shim");
		conf.setAttribute(ConfIDs.KEY_CYC_CONFDIR_PATH, "${project_loc}/data/InstructionCycle/");
		conf.setAttribute(ConfIDs.KEY_CYC_COMMAND, "echo ${GenerateCodeDirPath}; touch ${ResultCsvDirPath};");
		conf.setAttribute(ConfIDs.KEY_CYC_DESTDIR_PATH, "${project_loc}/Measurement/InstructionCycle/Code/");
		conf.setAttribute(ConfIDs.KEY_CYC_RSLTDIR_PATH, "${project_loc}/Measurement/InstructionCycle/Csv/");
	}

	private void controlEnabled(boolean en) {
		btnProcess1.setEnabled(en);
		btnProcess2.setEnabled(en);

		if (!btnProcess1.getSelection() && !btnProcess2.getSelection()) {
			en = false;
		}
		boolean chProc1 = en && btnProcess1.getSelection();
		boolean chProc2 = en && btnProcess2.getSelection();
		boolean chProcAll = en && btnProcess1.getSelection() && btnProcess2.getSelection();

		/* Input SHIM */
		lblInputShim.setEnabled(chProc1);
		txtInputShim.setEnabled(chProc1);
		btnInputShimFS.setEnabled(chProc1);
		btnInputShimWS.setEnabled(chProc1);
		btnInputShimVA.setEnabled(chProc1);

		/* Output SHIM */
		boolean chOutShim = en && (!btnProcess1.getSelection() || (chProc2 && (!chkOverwrite.getSelection())));
		lblOutputShim.setEnabled(chOutShim);
		txtOutputShim.setEnabled(chOutShim);
		btnOutputShimFS.setEnabled(chOutShim);
		btnOutputShimWS.setEnabled(chOutShim);
		btnOutputShimVA.setEnabled(chOutShim);

		chkOverwrite.setEnabled(chProcAll);
		chkOpenTheOutput.setEnabled(chProc2);

		/* Conf Dir */
		lblSelectConfDir.setEnabled(chProc1);
		txtSelectConfDir.setEnabled(chProc1);
		btnSelectConfDirFS.setEnabled(chProc1);
		btnSelectConfDirWS.setEnabled(chProc1);
		btnSelectConfDirVA.setEnabled(chProc1);

		/* Command */
		lblMeasureScript.setEnabled(chProc2);
		txtMeasureScript.setEnabled(chProc2);

		boolean destDir = en ? chkDestDir.getSelection() : false;
		chkDestDir.setEnabled(en);
		lblDestDir.setEnabled(destDir);
		txtDestDir.setEnabled(destDir);
		btnDestDir.setEnabled(destDir);

		boolean measureResult = chProc2 ? chkMeasureResult.getSelection() : false;
		chkMeasureResult.setEnabled(chProc2);
		lblMeasureResult.setEnabled(measureResult);
		txtCsvResultDir.setEnabled(measureResult);
		btnMeasureResult.setEnabled(measureResult);
	}

	/**
	 * Called when the screen is displayed.
	 */
	@Override
	public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
		boolean en = false;
		try {
			int mode = workingCopy.getAttribute(ConfIDs.KEY_CMN_SELECT_MODE, -1);
			en = (mode == 1);
		} catch (CoreException e) {
			e.printStackTrace(ConsoleStream.getInstance());
		}

		controlEnabled(en);
	}

	/**
	 * Loading the saved settings.
	 */
	@Override
	public void initializeFrom(ILaunchConfiguration conf) {
		try {
			btnProcess1.setSelection(conf.getAttribute(ConfIDs.KEY_CYC_DO_CODE_GEN, true));
			btnProcess2.setSelection(conf.getAttribute(ConfIDs.KEY_CYC_DO_EXEC_MEASURE, true));
			txtInputShim.setText(conf.getAttribute(ConfIDs.KEY_CYC_INPUT_SHIM, ""));
			txtOutputShim.setText(conf.getAttribute(ConfIDs.KEY_CYC_OUTPUT_SHIM, ""));
			chkOverwrite.setSelection(conf.getAttribute(ConfIDs.KEY_CYC_OVERWRITE_RSLT, false));
			chkOpenTheOutput.setSelection(conf.getAttribute(ConfIDs.KEY_CYC_VIEW_RSLT, false));
			txtSelectConfDir.setText(conf.getAttribute(ConfIDs.KEY_CYC_CONFDIR_PATH, ""));
			txtMeasureScript.setText(conf.getAttribute(ConfIDs.KEY_CYC_COMMAND, ""));
			chkDestDir.setSelection(conf.getAttribute(ConfIDs.KEY_CYC_USE_DESTDIR_PATH, false));
			txtDestDir.setText(conf.getAttribute(ConfIDs.KEY_CYC_DESTDIR_PATH, ""));
			chkMeasureResult.setSelection(conf.getAttribute(ConfIDs.KEY_CYC_USE_RSLTDIR_PATH, false));
			txtCsvResultDir.setText(conf.getAttribute(ConfIDs.KEY_CYC_RSLTDIR_PATH, ""));

			isAvailable = conf.getAttribute(ConfIDs.KEY_CYC_IS_AVAILABLE, false);

			int mode = conf.getAttribute(ConfIDs.KEY_CMN_SELECT_MODE, -1);
			boolean en = (mode == 1);

			controlEnabled(en);
		} catch (CoreException e) {
			e.printStackTrace(ConsoleStream.getInstance());
		}
	}

	/**
	 * Process of saving settings when applied.
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy conf) {
		conf.setAttribute(ConfIDs.KEY_CYC_DO_CODE_GEN, btnProcess1.getSelection());
		conf.setAttribute(ConfIDs.KEY_CYC_DO_EXEC_MEASURE, btnProcess2.getSelection());
		conf.setAttribute(ConfIDs.KEY_CYC_INPUT_SHIM, txtInputShim.getText());
		conf.setAttribute(ConfIDs.KEY_CYC_OUTPUT_SHIM, txtOutputShim.getText());
		conf.setAttribute(ConfIDs.KEY_CYC_OVERWRITE_RSLT, chkOverwrite.getSelection());
		conf.setAttribute(ConfIDs.KEY_CYC_VIEW_RSLT, chkOpenTheOutput.getSelection());
		conf.setAttribute(ConfIDs.KEY_CYC_CONFDIR_PATH, txtSelectConfDir.getText());
		conf.setAttribute(ConfIDs.KEY_CYC_COMMAND, txtMeasureScript.getText());
		conf.setAttribute(ConfIDs.KEY_CYC_USE_DESTDIR_PATH, chkDestDir.getSelection());
		conf.setAttribute(ConfIDs.KEY_CYC_DESTDIR_PATH, txtDestDir.getText());
		conf.setAttribute(ConfIDs.KEY_CYC_USE_RSLTDIR_PATH, chkMeasureResult.getSelection());
		conf.setAttribute(ConfIDs.KEY_CYC_RSLTDIR_PATH, txtCsvResultDir.getText());
		conf.setAttribute(ConfIDs.KEY_CYC_IS_AVAILABLE, isAvailable);
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
