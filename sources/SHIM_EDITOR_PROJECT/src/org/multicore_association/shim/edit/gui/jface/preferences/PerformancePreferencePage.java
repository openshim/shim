/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * Preference page that shows only message.
 */
public class PerformancePreferencePage extends PreferencePage {

	/**
	 * Constructs a new instance of PerformancePreferencePage.
	 */
	public PerformancePreferencePage() {
		setTitle("Performance");
	}

	/**
	 * Creates contents of this page.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	@Override
	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(1, false));
		
		Label lblExpandTree = new Label(container, SWT.NONE);
		lblExpandTree.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblExpandTree.setText("Expand the tree to edit preferences for a specific feature.");
		
		return container;
	}
	
	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		// There is no preference. So the buttons should be not visible.
		getApplyButton().setVisible(false);
		getDefaultsButton().setVisible(false);
	}
}
