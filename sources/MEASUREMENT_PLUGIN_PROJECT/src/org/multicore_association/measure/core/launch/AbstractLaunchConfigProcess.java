/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.core.launch;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.multicore_association.measure.core.Activator;
import org.multicore_association.measure.io.ConsoleStream;
import org.multicore_association.measure.io.StreamBufferThread;

abstract class AbstractLaunchConfigProcess {

	protected static final String LABEL_PROCESSING = "Processing";
	protected static final String WORKDIR_PATH = "Measurement";

	protected Process proc = null;
	protected IProgressMonitor monitor = null;
	protected StreamBufferThread stdoutThread = null;
	protected StreamBufferThread stderrThread = null;

	protected class WatchThread extends Thread {
		private boolean alive = true;
		public void setAlive(boolean alive) {
			this.alive = alive;
		}
		@Override
		public void run() {
			try {
				while (alive) {
					Thread.sleep(1000);
					checkCancel(monitor);
				}
			} catch(IllegalThreadStateException e) {
				if (stdoutThread != null) {
					stdoutThread.abort();
				}
				if (stderrThread != null) {
					stderrThread.abort();
				}
			} catch (InterruptedException e) {
				if (stdoutThread != null) {
					stdoutThread.abort();
				}
				if (stderrThread != null) {
					stderrThread.abort();
				}
			}
		}
	};


	protected void checkCancel(IProgressMonitor monitor) throws InterruptedException {
		if (monitor.isCanceled()) {
			if (proc != null) {
				proc.destroy();
				if (stdoutThread != null) {
					stdoutThread.abort();
				}
				if (stderrThread != null) {
					stderrThread.abort();
				}
			}
			throw new InterruptedException("Cancel has been requested.");
		}
	}

	protected String convertVariables(String str) {
		IStringVariableManager svm = VariablesPlugin.getDefault().getStringVariableManager();
		String result = str;
		try {
			result = svm.performStringSubstitution(str);
		} catch (CoreException e) {
		}

		return result;
	}

	void exec(ILaunchConfiguration conf, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		this.monitor = monitor;
	};

	protected void execScript(String command) throws IOException, InterruptedException, CoreException {
		command = command.split("#", 2)[0];
		String[] cmdlist = command.split(";");

		for (int i = 0; i < cmdlist.length; i++) {
			String cmd = cmdlist[i];
			cmd = cmd.trim();

			if (cmd.isEmpty()) {
				continue;
			}

			ConsoleStream.getInstance().println();
			ConsoleStream.getInstance().println("$ " + cmd);

			ProcessBuilder builder = new ProcessBuilder(cmd.split(" "));

			try {
				WatchThread checkThread = new WatchThread();
				checkThread.start();
				checkThread.setPriority(Thread.MAX_PRIORITY);

				proc = builder.redirectErrorStream(false).start();

				InputStream stream = proc.getInputStream();
				InputStream erream = proc.getErrorStream();

				stdoutThread = new StreamBufferThread(stream, ConsoleStream.getInstance());
				stderrThread = new StreamBufferThread(erream, ConsoleStream.getInstance());

				stdoutThread.start();
				stderrThread.start();
				proc.waitFor();
				stdoutThread.join();
				stderrThread.join();

				checkCancel(monitor);
				checkThread.setAlive(false);
				if (proc.exitValue() != 0) {
					IStatus status = new Status(Status.ERROR, Activator.PLUGIN_ID,
							"Command execution error.\n Please check the console log.");
					throw new CoreException(status);
				}
			} finally {
			}
		}
	}


	protected void runProcessAndOutConsole(List<String> cmdList, String errMessage)
			throws InterruptedException, IOException, CoreException {
		PrintStream defout = System.out;

		ProcessBuilder builder = new ProcessBuilder(cmdList);
		try {
			WatchThread checkThread = new WatchThread();
			checkThread.start();
			checkThread.setPriority(Thread.MAX_PRIORITY);

			proc = builder.redirectErrorStream(false).start();
			InputStream stream = proc.getInputStream();
			InputStream erream = proc.getErrorStream();

//			ps = new PrintStream(ConsoleStream.getInstance());

			stdoutThread = new StreamBufferThread(stream, ConsoleStream.getInstance());
			stderrThread = new StreamBufferThread(erream, ConsoleStream.getInstance());

			stdoutThread.start();
			stderrThread.start();
			proc.waitFor();
			stdoutThread.join();
			stderrThread.join();

			checkCancel(monitor);

			checkThread.setAlive(false);
			if (stderrThread.isOutputted() || proc.exitValue() != 0) {
				IStatus status = new Status(Status.ERROR, Activator.PLUGIN_ID, errMessage);
				throw new CoreException(status);
			}
		} finally {
			System.setOut(defout);
			stdoutThread = null;
			stderrThread = null;
		}
	}

	protected void runProcessAndOutFile(List<String> cmdList, String outPath, String errMessage)
			throws InterruptedException, IOException, CoreException {
		PrintStream defout = System.out;
		PrintStream ps = null;
		FileOutputStream fos = null;

		Path gen = new Path(outPath);

		ProcessBuilder builder = new ProcessBuilder(cmdList);
		try {
			WatchThread checkThread = new WatchThread();
			checkThread.start();
			checkThread.setPriority(Thread.MAX_PRIORITY);

			proc = builder.redirectErrorStream(false).start();
			InputStream stream = proc.getInputStream();
			InputStream erream = proc.getErrorStream();

			fos = new FileOutputStream(gen.toFile(), false);
			ps = new PrintStream(fos);

			stdoutThread = new StreamBufferThread(stream, ps);
			stderrThread = new StreamBufferThread(erream, ConsoleStream.getInstance());

			stdoutThread.start();
			stderrThread.start();
			proc.waitFor();
			stdoutThread.join();
			stderrThread.join();

			checkCancel(monitor);

			checkThread.setAlive(false);
			if (stderrThread.isOutputted() || proc.exitValue() != 0) {
				IStatus status = new Status(Status.ERROR, Activator.PLUGIN_ID, errMessage);
				throw new CoreException(status);
			}
		} finally {
			System.setOut(defout);
			try {
				ps.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
			stdoutThread = null;
			stderrThread = null;
		}
	}

}
