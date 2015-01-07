/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.multicore_association.shim.api.Accessor;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.edit.gui.common.RefComboFactory;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for Accessor.
 */
public class AccessorInputPanel extends InputPanelBase {

	private Combo comboMasterComponentRef;
	private RefComboFactory refComboFactory;

	private Accessor accessor = new Accessor();

	/**
	 * Constructs a new instance of AccessorInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public AccessorInputPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		setLblTitleText("Accessor");

		Composite composite_2 = new Composite(this, SWT.NONE);
		composite_2.setLayoutData(BorderLayout.CENTER);
		composite_2.setLayout(new GridLayout(3, false));

		Label lbl_MasterComponentRef = new Label(composite_2, SWT.NONE);
		lbl_MasterComponentRef.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		lbl_MasterComponentRef.setText("MasterComponent");
		new Label(composite_2, SWT.NONE);

		Composite composite_InstructionSet = new Composite(composite_2,
				SWT.NONE);
		composite_InstructionSet.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, true, false, 1, 1));
		composite_InstructionSet.setLayout(new GridLayout(1, false));

		refComboFactory = new RefComboFactory(composite_InstructionSet,
				getApiClass(), "masterComponentRef", MasterComponent.class);
		comboMasterComponentRef = refComboFactory.createEmptyCombo();
		comboMasterComponentRef.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, true, false, 1, 1));

		// Checking Required.
		comboMasterComponentRef.addModifyListener(new TextModifyListener(
				"masterComponentRef"));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		accessor = (Accessor) input;

		MasterComponent refMc = (MasterComponent) accessor
				.getMasterComponentRef();
		comboMasterComponentRef = refComboFactory.createCombo();
		refComboFactory.setText(refMc);
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		assert (accessor != null);
		accessor.setMasterComponentRef(refComboFactory.getRefObject());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return accessor.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		refComboFactory.dispose();
		accessor = new Accessor();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) accessor;
	}
}
