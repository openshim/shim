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
//import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.multicore_association.shim.api.AccessType;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.Performance;
import org.multicore_association.shim.edit.gui.common.RefComboFactory;
import org.multicore_association.shim.edit.model.ShimModelManager;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for Performance.
 */
public class PerformanceInputPanel extends InputPanelBase {

	private Combo comboAccessTypeRef;
	private RefComboFactory refComboFactory; 

	private Performance performance = new Performance();
	
	/**
	 * Constructs a new instance of PerformanceInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public PerformanceInputPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));
		
		setLblTitleText("Performance");
		
		Composite composite_2 = new Composite(this, SWT.NONE);
		composite_2.setLayoutData(BorderLayout.CENTER);
		composite_2.setLayout(new GridLayout(1, false));
		
		Composite composite_InstructionSet = new Composite(composite_2, SWT.NONE);
		composite_InstructionSet.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_InstructionSet.setLayout(new GridLayout(3, false));
		
		Label lbl_MasterComponentRef = new Label(composite_InstructionSet, SWT.NONE);
		lbl_MasterComponentRef.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lbl_MasterComponentRef.setText("AccessType");
		new Label(composite_InstructionSet, SWT.NONE);
		
		refComboFactory = new RefComboFactory(composite_InstructionSet,
				getApiClass(), "accessTypeRef", AccessType.class);
		comboAccessTypeRef = refComboFactory.createEmptyCombo();
		comboAccessTypeRef.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		// Checking Required.
		comboAccessTypeRef.addModifyListener(new TextModifyListener("accessTypeRef"));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		performance = (Performance)input;
		
		AccessType at =(AccessType) performance.getAccessTypeRef();
		MasterComponent master = ShimModelManager.findParentRefMasterComponent(performance);
		if (master == null) {
			comboAccessTypeRef = refComboFactory.createCombo();
		} else {
			comboAccessTypeRef = refComboFactory.createCombo(master);
		}
		
		refComboFactory.setText(at);
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		performance.setAccessTypeRef(refComboFactory.getRefObject());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return performance.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		refComboFactory.dispose();
		performance = new Performance();
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) performance;
	}
}
