/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.actions;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.multicore_association.shim.edit.gui.jface.SHIMEditJFaceApplicationWindow;

/**
 * This class contains various methods for using swt and jface's control.
 */
public class ControlUtil {

	/**
	 * SHIM Editor's original event type; refresh
	 */
	public static final int EVENT_REFRESH = 100000;

	/**
	 * Returns the current wizard page.
	 * 
	 * @return Returns the current wizard page while runs wizard dialog. While
	 *         does not run wizard dialog, returns null.
	 */
	public static IWizardPage getCurrentWizardPage() {
		Shell activeShell = Display.getCurrent().getActiveShell();
		if (activeShell != null
				&& activeShell.getData() instanceof WizardDialog) {
			WizardDialog wd = (WizardDialog) activeShell.getData();
			return wd.getCurrentPage();
		}
		return null;
	}

	/**
	 * Returns whether the command is called from the wizard.
	 * 
	 * @return Returns true if the command is called from the wizard, and false
	 *         otherwise.
	 */
	public static boolean callFromWizard() {
		Shell activeShell = Display.getCurrent().getActiveShell();
		if (activeShell != null
				&& activeShell.getData() instanceof WizardDialog) {
			return true;
		}
		return false;
	}

	/**
	 * Notifies the refresh event to the top control of IWizardPage or the ApplicationWindow.
	 */
	public static void notifyRefreshEvent() {
		Shell activeShell = Display.getCurrent().getActiveShell();
		if (activeShell == null) {
			return;
		}

		Event event = new Event();
		event.type = EVENT_REFRESH;

		Object data = activeShell.getData();
		if (data instanceof SHIMEditJFaceApplicationWindow) {
			SHIMEditJFaceApplicationWindow window = (SHIMEditJFaceApplicationWindow) data;
			window.refresh();
			window.setInputToEachCurrentInputPannel();

		} else if (data instanceof WizardDialog) {
			IWizardPage currentPage = ((WizardDialog) data).getCurrentPage();
			currentPage.getControl().notifyListeners(EVENT_REFRESH, event);
		}
	}
}
