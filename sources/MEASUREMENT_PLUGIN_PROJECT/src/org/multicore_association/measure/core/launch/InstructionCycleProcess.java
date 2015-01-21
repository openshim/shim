/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.measure.core.launch;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.multicore_association.measure.core.Activator;
import org.multicore_association.measure.io.ConsoleStream;
import org.multicore_association.measure.ui.ExternalFileEditorInput;

class InstructionCycleProcess extends AbstractLaunchConfigProcess {

	private static final String WORKDIR_PATH = "InstructionCycle";

	private String projectPath = null;
	private String projectName = null;
	private String inputShimPath = null;
	private String outputShimPath = null;
	private String configFileDirPath = null;
	private String measureCodeDirPath = null;
	private String measureCommand = null;
	private String csvResultDirPath = null;

	private String targetArchName = null;
	private String targetCmnInstSet = null;

	private boolean overwriteResult = false;
	private boolean viewResultShim = false;
	private boolean useCodeDirPath = false;
	private boolean useCsvDirPath = false;
	private boolean doCodeGen = true;
	private boolean doExecMeasure = true;

	private String workDir = "";
	private String dateStr = "";

	void exec(ILaunchConfiguration conf, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		super.exec(conf, mode, launch, monitor);

		ConsoleStream.getInstance().setPrefix("[InstructionCycle]: ");

		monitor.beginTask(LABEL_PROCESSING, 100);
		try {
			monitor.worked(20);

			initConfiguration(conf);

			/* generating the measurement code */
			if (doCodeGen) {
				checkCancel(monitor);
				monitor.subTask("generating the measurement code");
				ConsoleStream.getInstance().println("> generating the measurement code");

				generateCode();
				/* output: MeasurementCode */
			}
			monitor.worked(30);

			/* execute measurement script */
			if (doExecMeasure) {
				checkCancel(monitor);
				monitor.subTask("execute measurement process");
				ConsoleStream.getInstance().println();
				ConsoleStream.getInstance().println("> execute measurement process");

				/* execute measurement command */
				execScript();
				/* output: ResultCSV File */
			}
			monitor.worked(30);
			if (doExecMeasure) {
				/* output the execution result */
				checkCancel(monitor);
				monitor.subTask("set result to shim file");
				ConsoleStream.getInstance().println();
				ConsoleStream.getInstance().println("> set result to shim file");

				setResult();
				/* output: updated SHIM file */
			}
			monitor.worked(10);

			if (viewResultShim) {
				Display.getDefault().asyncExec(new Thread() {
					@Override
					public void run() {
						Path path = null;
						if (overwriteResult) {
							path = new Path(inputShimPath);
						} else {
							path = new Path(outputShimPath);
						}
						File file = path.toFile();
						IWorkbench workbench = PlatformUI.getWorkbench();
						IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
						IWorkbenchPage page = window.getActivePage();
						ExternalFileEditorInput input = new ExternalFileEditorInput(file);
						try {
							IDE.openEditor(page, input, org.eclipse.ui.editors.text.EditorsUI.DEFAULT_TEXT_EDITOR_ID);
						} catch (PartInitException e) {
						}
					}
				});
			}
			monitor.worked(10);
		} catch (InterruptedException e) {
			ConsoleStream.getInstance().println();
			ConsoleStream.getInstance().println("> Launch has been cancelled.");
			IStatus status = new Status(Status.OK, Activator.PLUGIN_ID,
					"Launch has been stopped.");
			throw new CoreException(status);
		} catch (Error e) {
			IStatus status = new Status(Status.ERROR, Activator.PLUGIN_ID,
					e.getMessage());
			throw new CoreException(status);
		} catch (RuntimeException e) {
			IStatus status = new Status(Status.ERROR, Activator.PLUGIN_ID,
					e.getMessage());
			throw new CoreException(status);
		} catch (IOException e) {
			IStatus status = new Status(Status.ERROR, Activator.PLUGIN_ID,
					e.getMessage());
			throw new CoreException(status);
		} finally {
			monitor.done();
			ConsoleStream.getInstance().println();
			ConsoleStream.getInstance().println("> Done.");
			ConsoleStream.getInstance().setPrefix(null);
		}
	}

	private void deleteDirectory(File file) {
		if (!file.exists()) {
			return;
		}

		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			File[] list = file.listFiles();
			for (int i = 0; i < list.length; i++) {
				deleteDirectory(list[i]);
			}
			file.delete();
		}
	}

	private void initConfiguration(ILaunchConfiguration conf) throws CoreException {
		/* getting parameter */
//		genCodeToolPath = MainPrefsAccessor.getCodeGen();
//		setCycleToolPath = MainPrefsAccessor.getSetCycl();

		projectName = conf.getAttribute(ConfIDs.KEY_CMN_PROJECT, "");

		inputShimPath = convertVariables(conf.getAttribute(ConfIDs.KEY_CYC_INPUT_SHIM, ""));
		outputShimPath = convertVariables(conf.getAttribute(ConfIDs.KEY_CYC_OUTPUT_SHIM, ""));
		configFileDirPath = convertVariables(conf.getAttribute(ConfIDs.KEY_CYC_CONFDIR_PATH, ""));
		measureCodeDirPath = convertVariables(conf.getAttribute(ConfIDs.KEY_CYC_DESTDIR_PATH, ""));
		csvResultDirPath = convertVariables(conf.getAttribute(ConfIDs.KEY_CYC_RSLTDIR_PATH, ""));
		measureCommand = conf.getAttribute(ConfIDs.KEY_CYC_COMMAND, "");
		overwriteResult = conf.getAttribute(ConfIDs.KEY_CYC_OVERWRITE_RSLT, false);
		viewResultShim = conf.getAttribute(ConfIDs.KEY_CYC_VIEW_RSLT, false);
		useCodeDirPath = conf.getAttribute(ConfIDs.KEY_CYC_USE_DESTDIR_PATH, false);
		useCsvDirPath = conf.getAttribute(ConfIDs.KEY_CYC_USE_RSLTDIR_PATH, false);
		doCodeGen = conf.getAttribute(ConfIDs.KEY_CYC_DO_CODE_GEN, true);
		doExecMeasure = conf.getAttribute(ConfIDs.KEY_CYC_DO_EXEC_MEASURE, true);

		targetArchName = conf.getAttribute(ConfIDs.KEY_CYC_TARGET_ARCH, "");
		targetCmnInstSet = conf.getAttribute(ConfIDs.KEY_CYC_TARGET_CMNINST, "");

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		projectPath = root.getProject(projectName).getLocation().toString();

		IPath gen = new Path(projectPath);
		gen = gen.append(AbstractLaunchConfigProcess.WORKDIR_PATH);
		gen = gen.append(WORKDIR_PATH);
		workDir = gen.toFile().getAbsolutePath();

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		dateStr = sdf.format(calendar.getTime());

		if (useCodeDirPath) {
			IPath p = new Path(measureCodeDirPath);
			measureCodeDirPath = p.toFile().getAbsolutePath();
		} else {
			IPath p = new Path(workDir);
			p = p.append("code_" + dateStr);
			measureCodeDirPath = p.toFile().getAbsolutePath();
		}

		if (useCsvDirPath) {
			IPath p = new Path(csvResultDirPath);
			csvResultDirPath = p.toFile().getAbsolutePath();
		} else {
			IPath p = new Path(workDir);
			p = p.append("csv_" + dateStr);
			csvResultDirPath = p.toFile().getAbsolutePath();
		}

		/* init workfiles */
		if (doCodeGen) {
			File f = new File(measureCodeDirPath);
			deleteDirectory(f);
			f.mkdirs();
		}
//		if (doCodeGen) {
//			File f = new File(csvResultDirPath);
//			deleteDirectory(f);
//			f.mkdirs();
//		}

		if (overwriteResult) {
			outputShimPath = inputShimPath;
		}

		IStringVariableManager svm = VariablesPlugin.getDefault().getStringVariableManager();
		IValueVariable[] vl = new IValueVariable[2];
		vl[0] = svm.newValueVariable("GenerateCodeDirPath", "GenerateCodeDirPath");
		vl[1] = svm.newValueVariable("ResultCsvDirPath", "ResultCsvDirPath");

		vl[0].setValue(measureCodeDirPath);
		vl[1].setValue(csvResultDirPath);

		svm.addVariables(vl);
		measureCommand = convertVariables(measureCommand);
		svm.removeVariables(vl);

	}

	private void setResult() throws CoreException, IOException, InterruptedException {
		List<String> cmdList = new ArrayList<String>();

		URL toolUrl = Activator.getDefault().getBundle().getEntry("lib/MeasurementCycleWriteback.jar");
		File toolFile = new File(FileLocator.toFileURL(toolUrl).getPath());
		URL scheUrl = Activator.getDefault().getBundle().getEntry("res/shim.xsd");
		File scheFile = new File(FileLocator.toFileURL(scheUrl).getPath());

		cmdList.add("java");
		cmdList.add("-jar");
		cmdList.add(toolFile.getPath());
		cmdList.add("--shim-schema");
		cmdList.add(scheFile.getPath());
		if (!targetArchName.isEmpty()) {
			cmdList.add("--architecture-name=" + targetArchName);
		}
		if (!targetCmnInstSet.isEmpty()) {
			cmdList.add("--instruction-set-name=" + targetCmnInstSet);
		}
		cmdList.add("--input-csv-dir");
		cmdList.add(csvResultDirPath);
		cmdList.add("--input-shim");
		cmdList.add(inputShimPath);
		cmdList.add("--output-shim");
		cmdList.add(outputShimPath);

		String[] arg = new String[1];
		arg = cmdList.toArray(arg);

		StringBuilder sb = new StringBuilder();
		for (String str : cmdList) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(str);
		}
//		ConsoleStream.getInstance().println("> " + sb.toString());

		runProcessAndOutConsole(cmdList,
				"Measurement result set processing error.\n Please check the console log.");

//		File shimf = new File(inputShimPath);
//		shimf.delete();
//		gen.toFile().renameTo(shimf);
	}

	private void execScript() throws IOException, InterruptedException, CoreException {
		execScript(measureCommand);
	}

	private void generateCode() throws InterruptedException, IOException, CoreException {
		List<String> cmdList = new ArrayList<String>();

		URL toolUrl = Activator.getDefault().getBundle().getEntry("lib/MeasurementCycleCodegen.jar");
		File toolFile = new File(FileLocator.toFileURL(toolUrl).getPath());
		URL scheUrl = Activator.getDefault().getBundle().getEntry("res/shim.xsd");
		File scheFile = new File(FileLocator.toFileURL(scheUrl).getPath());

		cmdList.add("java");
		cmdList.add("-jar");
		cmdList.add(toolFile.getPath());
		cmdList.add("--shim-schema");
		cmdList.add(scheFile.getPath());
		if (!targetArchName.isEmpty()) {
			cmdList.add("--architecture-name");
			cmdList.add(targetArchName);
		}
		if (!targetCmnInstSet.isEmpty()) {
			cmdList.add("--instruction-set-name");
			cmdList.add(targetCmnInstSet);
		}
		cmdList.add("--conf-dir");
		cmdList.add(configFileDirPath);
		cmdList.add("--input-shim");
		cmdList.add(inputShimPath);
		cmdList.add("--dest-dir");
		cmdList.add(measureCodeDirPath);
//		cmdList.add("--prefix");
//		cmdList.add("FIXME");

		String[] arg = new String[1];
		arg = cmdList.toArray(arg);

		StringBuilder sb = new StringBuilder();
		for (String str : cmdList) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(str);
		}
//		ConsoleStream.getInstance().println("> " + sb.toString());

		runProcessAndOutConsole(cmdList,
				"Code generation processing error.\n Please check the console log.");
	}
}
