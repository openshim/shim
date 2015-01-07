/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface.wizard;


/**
 * Abstract base implementation for SHIM Editor wizard page which has tree
 * viewer.
 */
public abstract class ShimEditorWizardPageBase extends ShimWizardPageBase {

	private boolean requiredToCreateData;

	/**
	 * Constructs a new instance of ShimEditorWizardPageBase.
	 * 
	 * @param pageName
	 */
	protected ShimEditorWizardPageBase(String pageName) {
		super(pageName);
	}

	/**
	 * Returns whether is required to create the SHIM Data or not.
	 * 
	 * @return Return true if needs to create the SHIM Data, and false
	 *         otherwise.
	 */
	public boolean isRequiredToCreateData() {
		return requiredToCreateData;
	}

	/**
	 * Sets whether is required to create the SHIM Data or not.
	 * 
	 * @param requiredToCreateData
	 *            whether is required to create the SHIM Data or not
	 */
	public void setRequiredToCreateData(boolean requiredToCreateData) {
		this.requiredToCreateData = requiredToCreateData;
	}

}
