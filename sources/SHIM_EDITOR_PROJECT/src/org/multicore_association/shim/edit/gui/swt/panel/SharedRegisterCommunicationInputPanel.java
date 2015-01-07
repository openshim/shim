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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.SharedRegisterCommunication;
import org.multicore_association.shim.api.SizeUnitType;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for InputPanel_SharedRegisterCommunication.
 */
public class SharedRegisterCommunicationInputPanel extends InputPanelBase {

	private static final Logger log = ShimLoggerUtil
			.getLogger(SharedRegisterCommunicationInputPanel.class);

	private Text textName;

	private SharedRegisterCommunication shregComm = new SharedRegisterCommunication();
	private Text textDataSize;
	private Text textNRegister;

	private Combo comboDataSizeUnit;

	/**
	 * Constructs a new instance of SharedRegisterCommunicationInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public SharedRegisterCommunicationInputPanel(Composite parent, int style) {

		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		setLblTitleText("Shared Register Communication");

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(4, false));

		Label lbl_Name = new Label(composite, SWT.NONE);
		lbl_Name.setText("Name");
		new Label(composite, SWT.NONE);

		textName = new Text(composite, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				2, 1));

		Label lbl_dataSize = new Label(composite, SWT.NONE);
		lbl_dataSize.setText("DataSize");
		new Label(composite, SWT.NONE);

		textDataSize = new Text(composite, SWT.BORDER);
		textDataSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));

		comboDataSizeUnit = new ComboFactory(composite).createCombo(
				SizeUnitType.class,
				ShimModelAdapter.isRequired(getApiClass(), "dataSizeUnit"));
		comboDataSizeUnit.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				true, false, 1, 1));

		Label lbl_nRegister = new Label(composite, SWT.NONE);
		lbl_nRegister.setText("Number of Registers");
		new Label(composite, SWT.NONE);

		textNRegister = new Text(composite, SWT.BORDER);
		textNRegister.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		new Label(composite, SWT.NONE);

		// Checking Required.
		textName.addModifyListener(new TextModifyListener("name"));
		textDataSize.addModifyListener(new TextModifyListener("dataSize"));
		textDataSize.addModifyListener(new NumberModifyListener("dataSize"));
		textNRegister.addModifyListener(new TextModifyListener("nRegister"));
		textNRegister.addModifyListener(new NumberModifyListener("nRegister"));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		shregComm = (SharedRegisterCommunication) input;

		textName.setText(shregComm.getName());
		textDataSize.setText(shregComm.getDataSize() + "");
		comboDataSizeUnit.setText(shregComm.getDataSizeUnit().value());
		textNRegister.setText(shregComm.getNRegister() + "");

	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {

		assert (shregComm != null);

		shregComm.setName(textName.getText());
		shregComm.setDataSize(Integer.parseInt(this.textDataSize.getText()));
		shregComm.setDataSizeUnit(SizeUnitType.fromValue(this.comboDataSizeUnit
				.getText()));
		shregComm.setNRegister(Integer.parseInt(this.textNRegister.getText()));

		log.finest("[applyChange] : SharedRegisterCommunication" + "\n  Name="
				+ shregComm.getName() + "\n  DataSize="
				+ shregComm.getDataSize() + "\n  DataSizeUnit="
				+ shregComm.getDataSizeUnit() + "\n  NRegister="
				+ shregComm.getNRegister());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return shregComm.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		shregComm = new SharedRegisterCommunication();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) shregComm;
	}
}
