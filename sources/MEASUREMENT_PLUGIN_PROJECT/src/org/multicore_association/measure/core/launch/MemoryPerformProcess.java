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

class MemoryPerformProcess extends AbstractLaunchConfigProcess {

	private static final String WORKDIR_PATH = "MemoryPerformance";

	private String projectPath = null;

	private String projectName = null;
	private String inputShimPath = null;
	private String outputShimPath = null;
	private String confFilePath = null;
	private String measureCodePath = null;
	private String measureCommand = null;
	private String measureResultPath = null;
	private boolean overwriteResult = false;
	private boolean viewResultShim = false;
	private boolean useCodePath = false;
	private boolean useResultPath = false;
	private boolean doCodeGen = true;
	private boolean doExecMeasure = true;

	private String workDir = "";
	private String dateStr = "";

	void exec(ILaunchConfiguration conf, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		super.exec(conf, mode, launch, monitor);

		ConsoleStream.getInstance().setPrefix("[MemoryPerformance]: ");

		monitor.beginTask(LABEL_PROCESSING, 100);
		try {
			monitor.worked(20);

			initConfiguration(conf);

			/* generating the measurement code */
			if (doCodeGen) {
				checkCancel(monitor);
				monitor.subTask("generate the measurement code");
				ConsoleStream.getInstance().println("> generate the measurement code");

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

	private void initConfiguration(ILaunchConfiguration conf) throws CoreException {
		/* getting parameter */
//		genCodeToolPath = MainPrefsAccessor.getMemCGen();
//		setRsltToolPath = MainPrefsAccessor.getSetRslt();

		projectName = conf.getAttribute(ConfIDs.KEY_CMN_PROJECT, "");

		inputShimPath = convertVariables(conf.getAttribute(ConfIDs.KEY_MEM_INPUT_SHIM, ""));
		outputShimPath = convertVariables(conf.getAttribute(ConfIDs.KEY_MEM_OUTPUT_SHIM, ""));
		confFilePath = convertVariables(conf.getAttribute(ConfIDs.KEY_MEM_CONF_FILE, ""));
		measureCodePath = convertVariables(conf.getAttribute(ConfIDs.KEY_MEM_CODE_FILE, ""));
		measureResultPath = convertVariables(conf.getAttribute(ConfIDs.KEY_MEM_RSLT_FILE, ""));
		measureCommand = conf.getAttribute(ConfIDs.KEY_MEM_COMMAND, "");
		overwriteResult = conf.getAttribute(ConfIDs.KEY_MEM_OVERWRITE, false);
		viewResultShim = conf.getAttribute(ConfIDs.KEY_MEM_VIEW_RSLT, false);
		useCodePath = conf.getAttribute(ConfIDs.KEY_MEM_USE_CODEPATH, false);
		useResultPath = conf.getAttribute(ConfIDs.KEY_MEM_USE_RSLTPATH, false);
		doCodeGen = conf.getAttribute(ConfIDs.KEY_MEM_DO_CODE_GEN, true);
		doExecMeasure = conf.getAttribute(ConfIDs.KEY_MEM_DO_EXEC_MEASURE, true);

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

		if (useCodePath) {
			IPath p = new Path(measureCodePath);
			measureCodePath = p.toFile().getAbsolutePath();
		} else {
			IPath p = new Path(workDir);
			p = p.append("code_" + dateStr + ".c");
			measureCodePath = p.toFile().getAbsolutePath();
		}

		if (useResultPath) {
			IPath p = new Path(conf.getAttribute(ConfIDs.KEY_MEM_RSLT_FILE, ""));
			measureResultPath = p.toFile().getAbsolutePath();
		} else {
			IPath p = new Path(workDir);
			p = p.append("result_" + dateStr + ".csv");
			measureResultPath = p.toFile().getAbsolutePath();
		}

		/* init workfiles */
		File f;
		if (doCodeGen) {
			f = new File(measureCodePath);
			new File(f.getParent()).mkdirs();
			f.delete();
		}

		f = new File(measureResultPath);
		new File(f.getParent()).mkdirs();
		f.delete();

		IStringVariableManager svm = VariablesPlugin.getDefault().getStringVariableManager();
		IValueVariable[] vl = new IValueVariable[2];
		vl[0] = svm.newValueVariable("GenerateCodePath", "GenerateCodePath");
		vl[1] = svm.newValueVariable("ResultFilePath", "ResultFilePath");

		vl[0].setValue(measureCodePath);
		vl[1].setValue(measureResultPath);

		svm.addVariables(vl);
		measureCommand = convertVariables(measureCommand);
		svm.removeVariables(vl);
	}

	private void setResult() throws CoreException, IOException, InterruptedException {
		List<String> cmdList = new ArrayList<String>();

		URL toolUrl = Activator.getDefault().getBundle().getEntry("lib/MeasurementMemoryWriteback.jar");
		File toolFile = new File(FileLocator.toFileURL(toolUrl).getPath());
		URL scheUrl = Activator.getDefault().getBundle().getEntry("res/shim.xsd");
		File scheFile = new File(FileLocator.toFileURL(scheUrl).getPath());

		IPath gen = new Path(workDir);
		gen = gen.append("mem_" + dateStr + ".tmp");

		cmdList.add("java");
		cmdList.add("-jar");
		cmdList.add(toolFile.getPath());
		cmdList.add("--shim-schema");
		cmdList.add(scheFile.getPath());
		cmdList.add(measureResultPath);
		cmdList.add(inputShimPath);

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

		runProcessAndOutFile(cmdList, gen.toFile().getAbsolutePath(),
				"Measurement result set processing error.\n Please check the console log.");

		File shimf = null;
		if (overwriteResult) {
			shimf = new File(inputShimPath);
		} else {
			shimf = new File(outputShimPath);
		}
		if (shimf.exists()) {
			shimf.delete();
		}
		gen.toFile().renameTo(shimf);
	}

	private void execScript() throws IOException, InterruptedException, CoreException {
		execScript(measureCommand);
	}

	private void generateCode() throws InterruptedException, IOException, CoreException {
		List<String> cmdList = new ArrayList<String>();

		URL toolUrl = Activator.getDefault().getBundle().getEntry("lib/MeasurementMemoryCodegen.jar");
		File toolFile = new File(FileLocator.toFileURL(toolUrl).getPath());
		URL scheUrl = Activator.getDefault().getBundle().getEntry("res/shim.xsd");
		File scheFile = new File(FileLocator.toFileURL(scheUrl).getPath());

		cmdList.add("java");
		cmdList.add("-jar");
		cmdList.add(toolFile.getPath());
		cmdList.add("--shim-schema");
		cmdList.add(scheFile.getPath());
		cmdList.add(confFilePath);
		cmdList.add(inputShimPath);

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

		runProcessAndOutFile(cmdList, measureCodePath,
				"Code generation processing error.\n Please check the console log.");
	}
}
