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
import org.multicore_association.shim.api.ClockFrequency;
import org.multicore_association.shim.edit.gui.common.LabelConstants;
import org.multicore_association.shim.edit.gui.databinding.FloatToStringConverter;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for ClockFrequency.
 */
public class ClockFrequencyInputPanel extends InputPanelBase {

	private Text textFrequency;	
	private ClockFrequency clockFrequency = new ClockFrequency();
	public Label lblClockfrequency;
	public Label lblHz;

	/**
	 * Constructs a new instance of ClockFrequencyInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public ClockFrequencyInputPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));
		
		setLblTitleText("Clock Frequency");
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(3, false));
		
		lblClockfrequency = new Label(composite, SWT.NONE);
		lblClockfrequency.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblClockfrequency.setText("ClockFrequency");
		
		textFrequency = new Text(composite, SWT.BORDER);
		GridData gd_textFrequency = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_textFrequency.widthHint = 137;
		textFrequency.setLayoutData(gd_textFrequency);
		
		lblHz = new Label(composite, SWT.NONE);
		lblHz.setText(LabelConstants.UNITSTR_HZ);
		
		// Checking Required.
		textFrequency.addModifyListener(new TextModifyListener("clockValue"));
		textFrequency.addModifyListener(new NumberModifyListener("clockValue"));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		clockFrequency = (ClockFrequency) input;
		
		this.textFrequency.setText(FloatToStringConverter.convertStr(clockFrequency.getClockValue()));
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		clockFrequency.setClockValue(Float.parseFloat(textFrequency.getText()));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return clockFrequency.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		clockFrequency = new ClockFrequency();
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) clockFrequency;
	}
}
