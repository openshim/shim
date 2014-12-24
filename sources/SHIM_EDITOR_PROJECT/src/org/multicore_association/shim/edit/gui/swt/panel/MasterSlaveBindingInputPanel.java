/*
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
import org.multicore_association.shim.api.MasterSlaveBinding;
import org.multicore_association.shim.api.SlaveComponent;
import org.multicore_association.shim.edit.gui.common.RefComboFactory;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for MasterSlaveBinding.
 */
public class MasterSlaveBindingInputPanel extends InputPanelBase {
	
	private MasterSlaveBinding msb = new MasterSlaveBinding();
	
	private Combo comboSlaveComponentRef;
	private RefComboFactory refComboFactory; 

	/**
	 * Constructs a new instance of MasterSlaveBindingInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public MasterSlaveBindingInputPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));
		
		setLblTitleText("Master Slave Binding");
		
		Composite composite_2 = new Composite(this, SWT.NONE);
		composite_2.setLayoutData(BorderLayout.CENTER);
		composite_2.setLayout(new GridLayout(1, false));
		
		Composite composite_InstructionSet = new Composite(composite_2, SWT.NONE);
		composite_InstructionSet.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_InstructionSet.setLayout(new GridLayout(3, false));
		
		Label lbl_Name = new Label(composite_InstructionSet, SWT.NONE);
		lbl_Name.setText("SlaveComponent");
		new Label(composite_InstructionSet, SWT.NONE);
		
		refComboFactory = new RefComboFactory(composite_InstructionSet,
				getApiClass(), "slaveComponentRef", SlaveComponent.class);
		comboSlaveComponentRef = refComboFactory.createEmptyCombo();
		comboSlaveComponentRef.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		// Checking Required.
		comboSlaveComponentRef.addModifyListener(new TextModifyListener("slaveComponentRef"));
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		msb = (MasterSlaveBinding)input;
		
		SlaveComponent sc = (SlaveComponent) msb.getSlaveComponentRef();
		
		comboSlaveComponentRef = refComboFactory.createCombo();
		refComboFactory.setText(sc);
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	protected void applyChange() {
		assert(msb != null);
		msb.setSlaveComponentRef(refComboFactory.getRefObject());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return msb.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		refComboFactory.dispose();
		msb = new MasterSlaveBinding();
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) msb;
	}
}
