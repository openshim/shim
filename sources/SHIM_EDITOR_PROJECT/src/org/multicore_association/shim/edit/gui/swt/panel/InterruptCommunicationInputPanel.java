/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.InterruptCommunication;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for InterruptCommunication.
 */
public class InterruptCommunicationInputPanel extends InputPanelBase {
	
	private static final Logger log = ShimLoggerUtil
			.getLogger(InterruptCommunicationInputPanel.class);
	
	private Text textName;
	
	private InterruptCommunication  intrComm = new InterruptCommunication();

	/**
	 * Constructs a new instance of InterruptCommunicationInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public InterruptCommunicationInputPanel(Composite parent, int style) {
		
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		setLblTitleText("Interrupt Communication");
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(3, false));
		
		Label lbl_Name = new Label(composite, SWT.NONE);
		lbl_Name.setText("Name");
		new Label(composite, SWT.NONE);
		
		textName = new Text(composite, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		// Checking Required.
		textName.addModifyListener(new TextModifyListener("name"));
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		intrComm = (InterruptCommunication)input;
		
		textName.setText(intrComm.getName());
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		assert(intrComm  != null);
		
		intrComm.setName(this.textName.getText());
		
		log.finest("[applyChange] : InterruptCommunication"
				+ "\n  Name=" + intrComm.getName());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return intrComm.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		intrComm = new InterruptCommunication();
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) intrComm;
	}
}
