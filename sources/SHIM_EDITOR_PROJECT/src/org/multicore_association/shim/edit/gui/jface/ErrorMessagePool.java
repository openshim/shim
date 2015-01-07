/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.wizard.WizardPage;

/**
 * This class manages whether to set error message from a validation result of
 * all control on the DialogPage.
 */
public class ErrorMessagePool {

	private DialogPage page;

	private Map<Object, String> errorMessages;

	private Object currListenr;

	/**
	 * Constructs a new instance of ErrorMessagePool.
	 * 
	 * @param page
	 *            DialogPage which has controls with validation
	 */
	public ErrorMessagePool(DialogPage page) {
		this.page = page;
		errorMessages = new HashMap<Object, String>();
	}

	/**
	 * Sets error message to DialogPage.
	 * 
	 * @param listener
	 *            ModifyListener which handling validation
	 * @param msg
	 *            If a validation error occurs, sets error message. Otherwise
	 *            sets null.
	 */
	public void setErrorMessage(Object listener, String msg) {
		if (msg == null) {
			removeErrorMessage(listener);
		} else {
			errorMessages.put(listener, msg);
			currListenr = listener;
			page.setErrorMessage(msg);

			setValid(false);
		}
	}

	/**
	 * Sets empty error message to DialogPage
	 * 
	 * @param listener
	 *            ModifyListener which handling validation
	 */
	public void removeErrorMessage(Object listener) {
		errorMessages.remove(listener);
		if (errorMessages.isEmpty()) {
			page.setErrorMessage(null);
			currListenr = null;
			setValid(true);
		} else if (listener.equals(currListenr)) {
			Entry<Object, String> next = errorMessages.entrySet().iterator()
					.next();
			page.setErrorMessage(next.getValue());
			currListenr = next.getKey();
		}
	}

	/**
	 * Sets whether the DialogPage is valid (or completed).
	 * 
	 * @param valid
	 *            Sets true if validation does not occur, and false otherwise.
	 */
	private void setValid(boolean valid) {
		if (page instanceof PreferencePage) {
			((PreferencePage) page).setValid(valid);
		} else if (page instanceof WizardPage) {
			((WizardPage) page).setPageComplete(valid);
		}
	}

}
