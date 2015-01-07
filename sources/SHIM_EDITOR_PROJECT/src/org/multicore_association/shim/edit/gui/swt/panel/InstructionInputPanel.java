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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.Instruction;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for Instruction.
 */
public class InstructionInputPanel extends InputPanelBase {
	
	private Text textName;
	
	private Instruction instruction = new Instruction();

	/**
	 * Constructs a new instance of InstructionInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public InstructionInputPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));
		
		setLblTitleText("Instruction");
		
		Composite composite_2 = new Composite(this, SWT.NONE);
		composite_2.setLayoutData(BorderLayout.CENTER);
		composite_2.setLayout(new GridLayout(1, false));
		
		Composite composite_Instruction = new Composite(composite_2, SWT.NONE);
		GridData gd_composite_Instruction = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_Instruction.widthHint = 444;
		composite_Instruction.setLayoutData(gd_composite_Instruction);
		composite_Instruction.setLayout(new GridLayout(3, false));
		
		Label lbl_Name = new Label(composite_Instruction, SWT.NONE);
		lbl_Name.setText("Name");
		new Label(composite_Instruction, SWT.NONE);
		
		textName = new Text(composite_Instruction, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		// Checking Required.
		textName.addModifyListener(new TextModifyListener("name"));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		instruction = (Instruction)input;
		this.textName.setText(instruction.getName());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		instruction.setName(this.textName.getText());	
	}
	 
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return instruction.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		instruction = new Instruction();
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) instruction;
	}
}
