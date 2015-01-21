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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.multicore_association.measure.core.launch.ConfIDs;
import org.multicore_association.measure.io.ConsoleStream;
import org.multicore_association.measure.ui.FileSelectionListener;
import org.multicore_association.measure.ui.VariableSelectionListener;
import org.multicore_association.measure.ui.WorkspaceFileSelectionListener;

/**
 * Launch configuration setting screen MemoryPerformance tab class.
 */
public class MemoryPerformanceTab extends AbstractLaunchConfigurationTab {

	private static final String TEXT_TAB_NAME = "Memory Performance";
	private static final String TEXT_BUTTON_SELECT = "Browse...";
	private static final String TEXT_WINDOW_FILE_REF_TITLE = "File Selection";
	private static final String TEXT_LABEL_CHECK_GENCODE = "Code generation";
	private static final String TEXT_LABEL_CHECK_EXECUTE = "Measurement";
	private static final String TEXT_LABEL_INPUT_SHIM = "Input SHIM file:";
	private static final String TEXT_LABEL_OUTPUT_SHIM = "Output SHIM file:";
	private static final String TEXT_LABEL_SELECT_CONF = "Config file:";
	private static final String TEXT_LABEL_SELECT_CODE = "Measurement code location:";
	private static final String TEXT_LABEL_SELECT_SCRIPT = "Command:";
	private static final String TEXT_LABEL_SELECT_RESULT = "Result CSV file location:";
	private static final String TEXT_LABEL_VIEW_OUTPUT_SHIM = "View the output SHIM file";
	private static final String TEXT_LABEL_OVERWRITE = "Overwrite the results to the input SHIM file.";

	private static final String TEXT_BUTTON_FILESYSTEM = "Filesystem...";
	private static final String TEXT_BUTTON_WORKSPACE = "Workspace...";
	private static final String TEXT_BUTTON_VARIABLES = "Variables...";

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
	private Button btnOpenTheOutput;

	private Label lblSelectConf;
	private Text txtSelectConf;
	private Button btnSelectConfFS;
	private Button btnSelectConfWS;
	private Button btnSelectConfVA;

	private Group grpCommandConfig;
	private Label lblMeasureScript;
	private Text txtMeasureScript;

	private Group grpOptionConfig;
	private Button chkMeasureCode;
	private Label lblMeasureCode;
	private Text txtMeasureCode;
	private Button btnMeasureCode;

	private Button chkMeasureResult;
	private Label lblMeasureResult;
	private Text txtMeasureResult;
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

		btnProcess1 = createCheckButton(grpProcessSelection,
				TEXT_LABEL_CHECK_GENCODE);
		btnProcess1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controlEnabled(true);
				scheduleUpdateJob();
			}
		});

		btnProcess2 = createCheckButton(grpProcessSelection,
				TEXT_LABEL_CHECK_EXECUTE);
		btnProcess2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controlEnabled(true);
				scheduleUpdateJob();
			}
		});
	}

	private void configureViewOutputShim(Composite parent, int horizontalSpan, int verticalSpan) {
		GridData gridSpan = new GridData(GridData.FILL_HORIZONTAL);
		gridSpan.horizontalSpan = horizontalSpan;
		gridSpan.verticalSpan = verticalSpan;

		/* Select viewing SHIM File (Label) */
		btnOpenTheOutput = createCheckButton(parent, TEXT_LABEL_VIEW_OUTPUT_SHIM);
		btnOpenTheOutput.setLayoutData(gridSpan);
		btnOpenTheOutput.addSelectionListener(new SelectionAdapter() {
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
		grpCodeGenConfig.setLayout(new GridLayout(3, false));
		grpCodeGenConfig.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		configureInputShimFile(grpCodeGenConfig);
		configureOutputShimFile(grpCodeGenConfig);
		configureOverwriteCheck(grpCodeGenConfig, 3, 1);
		configureViewOutputShim(grpCodeGenConfig, 3, 1);
		configureSelectConfig(grpCodeGenConfig);
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

		configureSelectMeasurementCode(grpOptionConfig);
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
		GridLayout ld = (GridLayout)bcomp.getLayout();
		ld.marginWidth = 1;
		ld.marginHeight = 1;
		bcomp.setLayoutData(new GridData(SWT.END, SWT.NORMAL, true, false, 3, 1));

		/* Input SHIM File (Button) */
		btnInputShimFS = createPushButton(bcomp, TEXT_BUTTON_FILESYSTEM, null);
		btnInputShimFS.addSelectionListener(new FileSelectionListener(getShell(), txtInputShim));

		btnInputShimWS = createPushButton(bcomp, TEXT_BUTTON_WORKSPACE, null);
		btnInputShimWS.addSelectionListener(new WorkspaceFileSelectionListener(getShell(), txtInputShim));

		btnInputShimVA = createPushButton(bcomp, TEXT_BUTTON_VARIABLES, null);
		btnInputShimVA.addSelectionListener(new VariableSelectionListener(getShell(), txtInputShim));
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
		GridLayout ld = (GridLayout)bcomp.getLayout();
		ld.marginWidth = 1;
		ld.marginHeight = 1;
		bcomp.setLayoutData(new GridData(SWT.END, SWT.NORMAL, true, false, 3, 1));

		/* Output SHIM File (Button) */
		btnOutputShimFS = createPushButton(bcomp, TEXT_BUTTON_FILESYSTEM, null);
		btnOutputShimFS.addSelectionListener(new FileSelectionListener(getShell(), txtOutputShim));

		btnOutputShimWS = createPushButton(bcomp, TEXT_BUTTON_WORKSPACE, null);
		btnOutputShimWS.addSelectionListener(new WorkspaceFileSelectionListener(getShell(), txtOutputShim));

		btnOutputShimVA = createPushButton(bcomp, TEXT_BUTTON_VARIABLES, null);
		btnOutputShimVA.addSelectionListener(new VariableSelectionListener(getShell(), txtOutputShim));
	}


	/**
	 *
	 * @param parent
	 */
	private void configureSelectConfig(Composite parent) {
		/* Select Config (Label) */
		lblSelectConf = new Label(parent, SWT.NONE);
		lblSelectConf.setText(TEXT_LABEL_SELECT_CONF);

		/* Select Config (Text) */
		txtSelectConf = new Text(parent, SWT.BORDER);
		txtSelectConf.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtSelectConf.addModifyListener(new ModifyListener() {
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
		btnSelectConfFS = createPushButton(bcomp, TEXT_BUTTON_FILESYSTEM, null);
		btnSelectConfFS.addSelectionListener(new FileSelectionListener(getShell(), txtSelectConf));

		btnSelectConfWS = createPushButton(bcomp, TEXT_BUTTON_WORKSPACE, null);
		btnSelectConfWS.addSelectionListener(new WorkspaceFileSelectionListener(getShell(), txtSelectConf));

		btnSelectConfVA = createPushButton(bcomp, TEXT_BUTTON_VARIABLES, null);
		btnSelectConfVA.addSelectionListener(new VariableSelectionListener(getShell(), txtSelectConf));
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
	private void configureSelectMeasurementCode(Composite parent) {
		GridData gridSpan3 = new GridData(GridData.FILL_HORIZONTAL);
		gridSpan3.horizontalSpan = 3;

		chkMeasureCode = createCheckButton(parent, TEXT_LABEL_SELECT_CODE);
		chkMeasureCode.setLayoutData(gridSpan3);
		chkMeasureCode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controlEnabled(true);
				scheduleUpdateJob();
			}
		});

		/* Select Measurement Code Dir (Label) */
		lblMeasureCode = new Label(parent, SWT.NONE);
		lblMeasureCode.setText("File:");

		/* Select Measurement Code Dir (Text) */
		txtMeasureCode = new Text(parent, SWT.BORDER);
		txtMeasureCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtMeasureCode.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		/* Select Measurement Code Dir (Button) */
		btnMeasureCode = createPushButton(parent, TEXT_BUTTON_SELECT, null);
		btnMeasureCode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
				dialog.setText(TEXT_WINDOW_FILE_REF_TITLE);
				File inFile = new Path(txtMeasureCode.getText()).toFile();

				String inPathStr;
				if (inFile.isFile() || !inFile.exists()) {
					inPathStr = inFile.getParent();
				} else {
					inPathStr = inFile.toString();
				}

				dialog.setFilterPath(inPathStr);

				String path = dialog.open();
				if (txtMeasureCode != null && path != null) {
					txtMeasureCode.setText(path);
				}
			}
		});
	}

	/**
	 *
	 * @param parent
	 */
	private void configureSelectMeasurementResult(Composite parent) {
		GridData gridSpan3 = new GridData(GridData.FILL_HORIZONTAL);
		gridSpan3.horizontalSpan = 3;

		chkMeasureResult = createCheckButton(parent,
				TEXT_LABEL_SELECT_RESULT);
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
		lblMeasureResult.setText("File:");

		/* Select Measurement Result Dir (Text) */
		txtMeasureResult = new Text(parent, SWT.BORDER);
		txtMeasureResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtMeasureResult.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		/* Select Measurement Result Dir (Button) */
		btnMeasureResult = createPushButton(parent, TEXT_BUTTON_SELECT, null);
		btnMeasureResult.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
				dialog.setText(TEXT_WINDOW_FILE_REF_TITLE);
				File inFile = new Path(txtMeasureResult.getText()).toFile();

				String inPathStr;
				if (inFile.isFile() || !inFile.exists()) {
					inPathStr = inFile.getParent();
				} else {
					inPathStr = inFile.toString();
				}

				dialog.setFilterPath(inPathStr);

				String path = dialog.open();
				if (txtMeasureResult != null && path != null) {
					txtMeasureResult.setText(path);
				}
			}
		});
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

		/* Process selection group */
		if (!btnProcess1.getSelection() && !btnProcess2.getSelection()) {
			setErrorMessage("Select one or more processing.");
			return false;
		}

		if (txtInputShim.getText().isEmpty()) {
			setErrorMessage("Input SHIM file path is empty.");
			return false;
		}

		/* Input SHIM File */
		String inputShimText = "";
		try {
			inputShimText = svm.performStringSubstitution(txtInputShim.getText());
		} catch (CoreException e) {
//			e.printStackTrace();
			setErrorMessage("Input SHIM file path is invalid.");
			return false;
		}

		File inputShimFile = new File(inputShimText);
		if (inputShimFile == null || !inputShimFile.exists() || !inputShimFile.isFile()) {
			setErrorMessage("Input SHIM file does not exist.");
			return false;
		}

		if (!chkOverwrite.getSelection() && txtOutputShim.getText().isEmpty()) {
			setErrorMessage("Output SHIM file path is empty.");
			return false;
		}

		/* Output SHIM File */
		if (chkOverwrite.getSelection()) {
			String outputShimText = null;
			try {
				outputShimText = svm.performStringSubstitution(txtOutputShim.getText());
			} catch (CoreException e) {
//				e.printStackTrace();
				setErrorMessage("Output SHIM file path is invalid.");
				return false;
			}

			File outputShimFile = new File(outputShimText);
			if (outputShimFile == null || outputShimFile.isDirectory()) {
				setErrorMessage("Output SHIM file path is invalid.");
				return false;
			}
		}


		/* Conf File */
		if (btnProcess1.getSelection()) {
			if (txtSelectConf.getText().isEmpty()) {
				setErrorMessage("Config file path is empty.");
				return false;
			}

			String confText = "";
			try {
				confText = svm.performStringSubstitution(txtSelectConf.getText());
			} catch (CoreException e) {
//				e.printStackTrace();
				setErrorMessage("Config file path is invalid.");
				return false;
			}

			File confFile = new File(confText);
			if (confFile == null || !confFile.exists() || !confFile.isFile()) {
				setErrorMessage("Config file does not exist.");
				return false;
			}
		}

		/* Measurement Script */
		if (btnProcess2.getSelection()) {
			if (txtMeasureScript.getText().isEmpty()) {
				setErrorMessage("Measurement script file path is empty.");
				return false;
			}
		}

		/* Measurement Code */
		if (btnProcess1.getSelection() ^ btnProcess2.getSelection()) {
			if (!chkMeasureCode.getSelection()) {
				setErrorMessage("You need to select the measurement code.");
				return false;
			}
		}

		if (chkMeasureCode.getSelection()) {
			//check Dir
			Path path = new Path(txtMeasureCode.getText());
			File file = path.toFile();

			if (btnProcess1.getSelection()) {
				if (file == null || file.isDirectory()) {
					setErrorMessage("Measurement code filename is invalid.");
					return false;
				}
			} else {
				if (file == null || !file.exists() || !file.isFile()) {
					setErrorMessage("Measurement code file does not exist.");
					return false;
				}
			}
		}

		/* Measurement Result */
		if (btnProcess2.getSelection() && chkMeasureResult.getSelection()) {
			//check file
			Path path = new Path(txtMeasureResult.getText());
			File file = path.toFile();

			if (txtMeasureResult.getText().isEmpty()) {
				setErrorMessage("Measurement result file path is empty.");
				return false;
			}

			if (file == null || file.isDirectory()) {
				setErrorMessage("Measurement result file path is invalid.");
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
			setMessage("### Warning: The name has been changed. Also change the name of the launch configuration file. ###");
		}

		try {
			int mode = launchConfig.getAttribute(ConfIDs.KEY_CMN_SELECT_MODE, 4);
			if (mode != 0) {
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
		conf.setAttribute(ConfIDs.KEY_MEM_INPUT_SHIM, "${project_loc}/data/shim.xml");
		conf.setAttribute(ConfIDs.KEY_MEM_OUTPUT_SHIM, "${project_loc}/Measurement/shim_output.xml");
		conf.setAttribute(ConfIDs.KEY_MEM_CONF_FILE, "${project_loc}/data/config.cfg");
		conf.setAttribute(ConfIDs.KEY_MEM_COMMAND, "echo ${GenerateCodePath}; touch ${ResultFilePath};");
		conf.setAttribute(ConfIDs.KEY_MEM_CODE_FILE, "${project_loc}/Measurement/sample.c");
		conf.setAttribute(ConfIDs.KEY_MEM_RSLT_FILE, "${project_loc}/Measurement/sample.csv");
	}

	private void controlEnabled(boolean en) {
		btnProcess1.setEnabled(en);
		btnProcess2.setEnabled(en);

		lblInputShim.setEnabled(en);
		txtInputShim.setEnabled(en);
		btnInputShimFS.setEnabled(en);
		btnInputShimWS.setEnabled(en);
		btnInputShimVA.setEnabled(en);

		boolean enOverwrite = en & !chkOverwrite.getSelection();
		chkOverwrite.setEnabled(en);
		lblOutputShim.setEnabled(enOverwrite);
		txtOutputShim.setEnabled(enOverwrite);
		btnOutputShimFS.setEnabled(enOverwrite);
		btnOutputShimWS.setEnabled(enOverwrite);
		btnOutputShimVA.setEnabled(enOverwrite);

		boolean enProcess1 = en & btnProcess1.getSelection();
		lblSelectConf.setEnabled(enProcess1);
		txtSelectConf.setEnabled(enProcess1);
		btnSelectConfFS.setEnabled(enProcess1);
		btnSelectConfWS.setEnabled(enProcess1);
		btnSelectConfVA.setEnabled(enProcess1);

		boolean enProcess2 = en & btnProcess2.getSelection();
		lblMeasureScript.setEnabled(enProcess2);
		txtMeasureScript.setEnabled(enProcess2);
		btnOpenTheOutput.setEnabled(enProcess2);

		boolean enMeasureCode = en & chkMeasureCode.getSelection();
		chkMeasureCode.setEnabled(en);
		lblMeasureCode.setEnabled(enMeasureCode);
		txtMeasureCode.setEnabled(enMeasureCode);
		btnMeasureCode.setEnabled(enMeasureCode);

		boolean enMeasureResult = en & chkMeasureResult.getSelection();
		chkMeasureResult.setEnabled(en);
		lblMeasureResult.setEnabled(enMeasureResult);
		txtMeasureResult.setEnabled(enMeasureResult);
		btnMeasureResult.setEnabled(enMeasureResult);
	}

	/**
	 * Called when the screen is displayed.
	 */
	@Override
	public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
		boolean en = false;
		try {
			int mode = workingCopy.getAttribute(ConfIDs.KEY_CMN_SELECT_MODE, -1);
			en = (mode == 0);
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
			btnProcess1.setSelection(conf.getAttribute(ConfIDs.KEY_MEM_DO_CODE_GEN, true));
			btnProcess2.setSelection(conf.getAttribute(ConfIDs.KEY_MEM_DO_EXEC_MEASURE, true));
			txtInputShim.setText(conf.getAttribute(ConfIDs.KEY_MEM_INPUT_SHIM, ""));
			chkOverwrite.setSelection(conf.getAttribute(ConfIDs.KEY_MEM_OVERWRITE, false));
			txtOutputShim.setText(conf.getAttribute(ConfIDs.KEY_MEM_OUTPUT_SHIM, ""));
			btnOpenTheOutput.setSelection(conf.getAttribute(ConfIDs.KEY_MEM_VIEW_RSLT, false));
			txtSelectConf.setText(conf.getAttribute(ConfIDs.KEY_MEM_CONF_FILE, ""));
			txtMeasureScript.setText(conf.getAttribute(ConfIDs.KEY_MEM_COMMAND, ""));
			chkMeasureCode.setSelection(conf.getAttribute(ConfIDs.KEY_MEM_USE_CODEPATH, false));
			txtMeasureCode.setText(conf.getAttribute(ConfIDs.KEY_MEM_CODE_FILE, ""));
			chkMeasureResult.setSelection(conf.getAttribute(ConfIDs.KEY_MEM_USE_RSLTPATH, false));
			txtMeasureResult.setText(conf.getAttribute(ConfIDs.KEY_MEM_RSLT_FILE, ""));
			isAvailable = conf.getAttribute(ConfIDs.KEY_MEM_IS_AVAILABLE, false);

			boolean en = false;
			int mode = conf.getAttribute(ConfIDs.KEY_CMN_SELECT_MODE, -1);
			en = (mode == 0);

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

		conf.setAttribute(ConfIDs.KEY_MEM_DO_CODE_GEN, btnProcess1.getSelection());
		conf.setAttribute(ConfIDs.KEY_MEM_DO_EXEC_MEASURE, btnProcess2.getSelection());
		conf.setAttribute(ConfIDs.KEY_MEM_INPUT_SHIM, txtInputShim.getText());
		conf.setAttribute(ConfIDs.KEY_MEM_OUTPUT_SHIM, txtOutputShim.getText());
		conf.setAttribute(ConfIDs.KEY_MEM_OVERWRITE, chkOverwrite.getSelection());
		conf.setAttribute(ConfIDs.KEY_MEM_VIEW_RSLT, btnOpenTheOutput.getSelection());
		conf.setAttribute(ConfIDs.KEY_MEM_CONF_FILE, txtSelectConf.getText());
		conf.setAttribute(ConfIDs.KEY_MEM_COMMAND, txtMeasureScript.getText());

		conf.setAttribute(ConfIDs.KEY_MEM_USE_CODEPATH, chkMeasureCode.getSelection());
		conf.setAttribute(ConfIDs.KEY_MEM_CODE_FILE, txtMeasureCode.getText());
		conf.setAttribute(ConfIDs.KEY_MEM_USE_RSLTPATH, chkMeasureResult.getSelection());
		conf.setAttribute(ConfIDs.KEY_MEM_RSLT_FILE, txtMeasureResult.getText());
		conf.setAttribute(ConfIDs.KEY_MEM_IS_AVAILABLE, isAvailable);
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
