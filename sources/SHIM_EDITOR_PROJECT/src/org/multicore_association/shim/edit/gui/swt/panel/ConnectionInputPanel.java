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
import org.multicore_association.shim.api.Connection;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.edit.gui.common.RefComboFactory;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for Connection.
 */
public class ConnectionInputPanel extends InputPanelBase {
	private Combo comboFrom;
	private Combo comboTo;
	private RefComboFactory refComboFactoryFrom;
	private RefComboFactory refComboFactoryTo;

	private Connection connection = new Connection();

	/**
	 * Constructs a new instance of ConnectionInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public ConnectionInputPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		setLblTitleText("Connection");

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));

		Label lbl_From = new Label(composite, SWT.NONE);
		lbl_From.setText("From");
		new Label(composite, SWT.NONE);

		refComboFactoryFrom = new RefComboFactory(composite, getApiClass(),
				"from", MasterComponent.class);
		comboFrom = refComboFactoryFrom.createEmptyCombo();
		comboFrom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lbl_To = new Label(composite, SWT.NONE);
		lbl_To.setText("To");
		new Label(composite, SWT.NONE);

		refComboFactoryTo = new RefComboFactory(composite, getApiClass(),
				"to", MasterComponent.class);
		comboTo = refComboFactoryTo.createEmptyCombo();
		comboTo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		// Checking Required.
		comboFrom.addModifyListener(new TextModifyListener("from"));
		comboTo.addModifyListener(new TextModifyListener("to"));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		connection = (Connection) input;

		MasterComponent ms_from = (MasterComponent) connection.getFrom();
		comboFrom = refComboFactoryFrom.createCombo();
		refComboFactoryFrom.setText(ms_from);

		MasterComponent ms_to = (MasterComponent) connection.getTo();
		comboTo = refComboFactoryTo.createCombo();
		refComboFactoryTo.setText(ms_to);
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		assert (connection != null);

		connection.setFrom(refComboFactoryFrom.getRefObject());
		connection.setTo(refComboFactoryTo.getRefObject());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return connection.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		refComboFactoryFrom.dispose();
		refComboFactoryTo.dispose();
		connection = new Connection();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) connection;
	}
}
