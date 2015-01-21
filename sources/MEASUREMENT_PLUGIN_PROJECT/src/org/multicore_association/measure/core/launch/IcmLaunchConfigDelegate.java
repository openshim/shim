/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.core.launch;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.multicore_association.measure.core.Activator;
import org.multicore_association.measure.core.prefs.MainPrefsAccessor;
import org.multicore_association.measure.io.ConsoleStream;

public class IcmLaunchConfigDelegate extends LaunchConfigurationDelegate {

	@Override
	protected void buildProjects(IProject[] projects, IProgressMonitor monitor) throws CoreException {
	}

	private boolean checkConfigIsAvailable(ILaunchConfiguration conf, int mode) {

		boolean mainTabAvailable = false;
		boolean refTabAvailable = false;

		try {
			mainTabAvailable = conf.getAttribute(ConfIDs.KEY_CMN_IS_AVAILABLE, false);

			switch (mode) {
			case 0:
				refTabAvailable = conf.getAttribute(ConfIDs.KEY_MEM_IS_AVAILABLE, false);
				break;
			case 1:
				refTabAvailable = conf.getAttribute(ConfIDs.KEY_CYC_IS_AVAILABLE, false);
				break;
//			case 2:
//				refTabAvailable = conf.getAttribute(ConfIDs.KEY_GEN_IS_AVAILABLE, false);
//				refTabAvailable &= conf.getAttribute(ConfIDs.KEY_SIM_IS_AVAILABLE, false);
//				break;
//			case 3:
//				refTabAvailable = conf.getAttribute(ConfIDs.KEY_ALZ_IS_AVAILABLE, false);
//				refTabAvailable &= conf.getAttribute(ConfIDs.KEY_SIM_IS_AVAILABLE, false);
//				break;
			default:
				break;
			}
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}

		return mainTabAvailable & refTabAvailable;
	}

	private void checkFilePathAvailable(ILaunchConfiguration conf, int mode) throws CoreException {

		boolean flag = true;
		String path = "";

		switch (mode) {
		case 0:
			flag = checkFilePathExist(conf.getAttribute(ConfIDs.KEY_MEM_INPUT_SHIM, ""));
			if (!flag) {
				path = "SHIM file";
				break;
			}

			if (conf.getAttribute(ConfIDs.KEY_MEM_DO_CODE_GEN, false)) {
				flag = checkFilePathExist(conf.getAttribute(ConfIDs.KEY_MEM_CONF_FILE, ""));
				if (!flag) {
					path = "Config file";
					break;
				}
			}
			if (!conf.getAttribute(ConfIDs.KEY_MEM_DO_CODE_GEN, false)
					&& conf.getAttribute(ConfIDs.KEY_MEM_DO_EXEC_MEASURE, false)) {
				flag = checkFilePathExist(conf.getAttribute(ConfIDs.KEY_MEM_CODE_FILE, ""));
				if (!flag) {
					path = "Generated code";
					break;
				}
			}
			break;
		case 1:
//			flag = checkFilePathExist(conf.getAttribute(ConfIDs.KEY_CYC_INPUT_SHIM, ""));
//			if (!flag) {
//				path = "Input SHIM";
//				break;
//			}
//			flag = checkFilePathExist(conf.getAttribute(ConfIDs.KEY_CYC_OUTPUT_SHIM, ""));
//			if (!flag) {
//				path = "Output SHIM";
//				break;
//			}
//
//			if (conf.getAttribute(ConfIDs.KEY_CYC_DO_CODE_GEN, false)) {
//				flag = checkFilePathExist(conf.getAttribute(ConfIDs.KEY_CYC_CONFDIR_PATH, ""));
//				if (!flag) {
//					path = "Configuration directory";
//					break;
//				}
//			}
////			if (conf.getAttribute(ConfIDs.KEY_CYC_DO_CODE_GEN, false)) {
////				flag = checkFilePathExist(conf.getAttribute(ConfIDs.KEY_CYC_COMMONCONF_FILE, ""));
////				if (!flag) {
////					path = "Common config file";
////					break;
////				}
////			}
//			if (!conf.getAttribute(ConfIDs.KEY_CYC_DO_CODE_GEN, false)
//					&& conf.getAttribute(ConfIDs.KEY_CYC_DO_EXEC_MEASURE, false)) {
//				flag = checkFilePathExist(conf.getAttribute(ConfIDs.KEY_CYC_DESTDIR_PATH, ""));
//				if (!flag) {
//					path = "Generated code";
//					break;
//				}
//			}

			break;
//		case 2:
//			flag = checkFilePathExist(conf.getAttribute(ConfIDs.KEY_GEN_ASM_FILE, ""));
//			if (!flag) {
//				path = "Assembly code";
//				break;
//			}
//
//			flag = checkDirPathExist(conf.getAttribute(ConfIDs.KEY_GEN_OUT_DIR, ""));
//			if (!flag) {
//				path = "Output directory";
//				break;
//			}
//
//			if (conf.getAttribute(ConfIDs.KEY_SIM_ENABLED, false)) {
//				if (conf.getAttribute(ConfIDs.KEY_SIM_USE_CONFFILE, false)) {
//					flag = checkFilePathExist(conf.getAttribute(ConfIDs.KEY_SIM_CONFFILE, ""));
//					if (!flag) {
//						path = "Simulation config file";
//						break;
//					}
//				} else {
//					if (conf.getAttribute(ConfIDs.KEY_SIM_USE_SHIMFILE, false)) {
//						flag = checkFilePathExist(conf.getAttribute(ConfIDs.KEY_SIM_SHIMFILE, ""));
//						if (!flag) {
//							path = "Simulation SHIM file";
//							break;
//						}
//					}
//				}
//			}
//
//			break;
//		case 3:
//			flag = checkFilePathExist(conf.getAttribute(ConfIDs.KEY_ALZ_GRAPH_FILE, ""));
//			if (!flag) {
//				path = "Graph file";
//				break;
//			}
//
//			if (conf.getAttribute(ConfIDs.KEY_SIM_ENABLED, false)) {
//				if (conf.getAttribute(ConfIDs.KEY_SIM_USE_CONFFILE, false)) {
//					flag = checkFilePathExist(conf.getAttribute(ConfIDs.KEY_SIM_CONFFILE, ""));
//					if (!flag) {
//						path = "Simulation config file";
//						break;
//					}
//				} else {
//					if (conf.getAttribute(ConfIDs.KEY_SIM_USE_SHIMFILE, false)) {
//						flag = checkFilePathExist(conf.getAttribute(ConfIDs.KEY_SIM_SHIMFILE, ""));
//						if (!flag) {
//							path = "Simulation SHIM file";
//							break;
//						}
//					}
//				}
//			}
//
//			break;
		default:
			IStatus status = new Status(Status.ERROR, Activator.PLUGIN_ID,
					"The selected processing invalid\n Please check the launch configuration.");
			throw new CoreException(status);
		}

		if (!flag) {
			String msg = "Can not access the specified path:  " + path + "\n Please check the launch configuration.";
			IStatus status = new Status(Status.ERROR, Activator.PLUGIN_ID, msg);
			throw new CoreException(status);
		}

		return;
	}

	private boolean checkFilePathExist(String filepath) {
		IStringVariableManager svm = VariablesPlugin.getDefault().getStringVariableManager();
		try {
			filepath = svm.performStringSubstitution(filepath);
		} catch (CoreException e) {
		}
		Path path = new Path(filepath);
		File file = path.toFile();
		return (file.exists() && file.isFile());
	}

//	private boolean checkDirPathExist(String filepath) {
//		IStringVariableManager svm = VariablesPlugin.getDefault().getStringVariableManager();
//		try {
//			filepath = svm.performStringSubstitution(filepath);
//		} catch (CoreException e) {
//		}
//		Path path = new Path(filepath);
//		File file = path.toFile();
//		return (file.exists() && file.isDirectory());
//	}

	private boolean checkMainPrefsAvailable() {
		boolean ret = true;
//		ret &= !MainPrefsAccessor.getOptPath().isEmpty();
//		ret &= checkFilePathExist(MainPrefsAccessor.getMemCGen());
//		ret &= checkFilePathExist(MainPrefsAccessor.getSetRslt());
//		ret &= checkFilePathExist(MainPrefsAccessor.getCodeGen());
//		ret &= checkFilePathExist(MainPrefsAccessor.getSetCycl());
//		ret &= checkFilePathExist(MainPrefsAccessor.getCDEFGen());
//		ret &= checkFilePathExist(MainPrefsAccessor.getCDFFAlz());
		return ret;
	}

	@Override
	public void launch(ILaunchConfiguration conf, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		/* check selected Project and conf Project */
		IStringVariableManager svm = VariablesPlugin.getDefault().getStringVariableManager();
		String currentProj = ".";
		try {
			currentProj = svm.performStringSubstitution("${project_name}");
		} catch (CoreException e) {
		}
		String configProj = conf.getAttribute(ConfIDs.KEY_CMN_PROJECT, "");

		if (!currentProj.equals(configProj)) {
			IStatus status = new Status(Status.ERROR, Activator.PLUGIN_ID,
					"Project is not selected.\n Try to activate the editor or select a project.");
			throw new CoreException(status);
		}

		/* check Preferences */
		if (!checkMainPrefsAvailable()) {
			IStatus status = new Status(Status.ERROR, Activator.PLUGIN_ID,
					"Tool path setting is not configured or disabled.\n Please check the Preferences.");
			throw new CoreException(status);
		}

		/* check configuration */
		int process = conf.getAttribute(ConfIDs.KEY_CMN_SELECT_MODE, -1);
		if (!checkConfigIsAvailable(conf, process)) {
			IStatus status = new Status(Status.ERROR, Activator.PLUGIN_ID,
					"Specified launch configuration file is not available.\n Please check the launch configuration.");
			throw new CoreException(status);
		}

		checkFilePathAvailable(conf, process);/*FIXME*/

		/* set launch history */
		String history = conf.getMemento();
		if (history != null && !history.isEmpty()) {
			MainPrefsAccessor.setLaunchHistory(conf.getMemento());
		}

		ConsoleStream cs = ConsoleStream.getInstance();
		cs.initConsole();

		switch (process) {
		case 0:
			new MemoryPerformProcess().exec(conf, mode, launch, monitor);
			break;
		case 1:
			new InstructionCycleProcess().exec(conf, mode, launch, monitor);
			break;
//		case 2:
//			new CDFGGenerationProcess().exec(conf, mode, launch, monitor);
//			break;
//		case 3:
//			new CDFGAnalysisProcess().exec(conf, mode, launch, monitor);
//			break;
		default:
			break;
		}
	}

}
