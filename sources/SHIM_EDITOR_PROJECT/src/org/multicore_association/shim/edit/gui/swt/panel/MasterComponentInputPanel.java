/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.EndianType;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.MasterType;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.EnumUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for MasterComponent.
 */
public class MasterComponentInputPanel extends InputPanelBase implements
		SelectionListener {

	private static final Logger log = ShimLoggerUtil
			.getLogger(MasterComponentInputPanel.class);

	private MasterComponent masterComponent = new MasterComponent();

	private Text textName;
	private Combo comboMasterType;
	private Text textArch;
	private Text textPid;
	private Text textNThread;
	private Text textArchOption;
	private Combo comboEndian;

	Button btnCheckButton;

	/**
	 * Constructs a new instance of MasterComponentInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public MasterComponentInputPanel(Composite parent, int style) {
		super(parent, style);

		setLayout(new BorderLayout(0, 0));

		setLblTitleText("Master Component");

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(2, false));

		Label lbl_Name = new Label(composite, SWT.NONE);
		lbl_Name.setText("Name");

		textName = new Text(composite, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Label lbl_Type = new Label(composite, SWT.NONE);
		lbl_Type.setText("Type");

		comboMasterType = new ComboFactory(composite).createCombo(
				MasterType.class,
				ShimModelAdapter.isRequired(getApiClass(), "masterType"));
		comboMasterType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lbl_Arch = new Label(composite, SWT.NONE);
		lbl_Arch.setText("Arch");

		textArch = new Text(composite, SWT.BORDER);
		textArch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		btnCheckButton = new Button(composite, SWT.CHECK);
		btnCheckButton.addSelectionListener(this);
		btnCheckButton.setText("ArchOption");

		textArchOption = new Text(composite, SWT.BORDER);
		textArchOption.setEnabled(false);
		GridData gd_textArchOption = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		gd_textArchOption.widthHint = 311;
		textArchOption.setLayoutData(gd_textArchOption);

		Label lbl_Pid = new Label(composite, SWT.NONE);
		lbl_Pid.setText("Pid");

		textPid = new Text(composite, SWT.BORDER);
		textPid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Label lblNthread = new Label(composite, SWT.NONE);
		lblNthread.setText("nThread");

		textNThread = new Text(composite, SWT.BORDER);
		textNThread.setText("");
		textNThread.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lbl_Endian = new Label(composite, SWT.NONE);
		lbl_Endian.setText("Endian");

		comboEndian = new ComboFactory(composite).createCombo(EndianType.class,
				ShimModelAdapter.isRequired(getApiClass(), "endian"));
		comboEndian.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		// Checking Required.
		textName.addModifyListener(new TextModifyListener("name"));
		textArch.addModifyListener(new TextModifyListener("arch"));
		textPid.addModifyListener(new TextModifyListener("pid"));
		textNThread.addModifyListener(new TextModifyListener("nThread"));
		textNThread.addModifyListener(new NumberModifyListener("nThread"));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	public void applyChange() {
		if (masterComponent == null) {
			return;
		}

		masterComponent.setName(textName.getText());
		masterComponent.setMasterType(MasterType.fromValue(this.comboMasterType
				.getText()));
		masterComponent.setArch(textArch.getText());
		masterComponent.setPid(textPid.getText());
		String str_nThread = textNThread.getText();
		if (isNullOrEmpty(str_nThread)) {
			masterComponent.setNThread(null);
		} else {
			masterComponent.setNThread(Integer.parseInt(str_nThread));
		}

		masterComponent.setEndian(EnumUtil.getEndianType(comboEndian
				.getSelectionIndex()));
		
		if (btnCheckButton.getSelection()) {
			masterComponent.setArchOption(textArchOption.getText());
		} else {
			masterComponent.setArchOption(null);
		}

		if (selectableItem != null) {
			selectableItem.refresh();
		}

		log.finest("[applyChange] : MasterComponent" + "\n  Name="
				+ masterComponent.getName() + "\n  Pid="
				+ masterComponent.getPid() + "\n  MasterType="
				+ masterComponent.getMasterType() + "\n  Arch="
				+ masterComponent.getArch() + "\n  ArchOption="
				+ masterComponent.getArchOption() + "\n  NThread="
				+ masterComponent.getNThread() + "\n  Endian="
				+ masterComponent.getEndian());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		masterComponent = (MasterComponent) input;

		textName.setText(masterComponent.getName());
		comboMasterType.setText(masterComponent.getMasterType().value());
		textArch.setText(masterComponent.getArch());
		textPid.setText(masterComponent.getPid());
		Integer nThread = masterComponent.getNThread();
		if (nThread == null) {
			textNThread.setText("");
		} else {
			textNThread.setText(nThread.toString());
		}
		EndianType endian = masterComponent.getEndian();
		if (endian == null) {
			comboEndian.select(comboEndian.getItemCount() - 1);
		} else {
			comboEndian.select(endian.ordinal());
		}

		if (masterComponent.getArchOption() == null) {
			btnCheckButton.setSelection(false);
			textArchOption.setEnabled(false);
			textArchOption.setText("");
		} else if (masterComponent.getArchOption() == "") {
			btnCheckButton.setSelection(true);
			textArchOption.setEnabled(true);
			textArchOption.setText("");
		} else {
			btnCheckButton.setSelection(true);
			textArchOption.setEnabled(true);
			textArchOption.setText(masterComponent.getArchOption());
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return masterComponent.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		masterComponent = new MasterComponent();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) masterComponent;
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		// NOOP
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(SelectionEvent event) {
		super.widgetSelected(event);
		
		if (event.getSource() == btnCheckButton) {
			performArchOptCheck(event);
		}
	}

	/**
	 * Sets enabled according to the selection of the button.
	 * 
	 * @param event
	 *            the notified SelectionEvent
	 */
	protected void performArchOptCheck(SelectionEvent event) {
		Button chkbtn = (Button) event.getSource();
		if (chkbtn.getSelection() == true) {
			this.textArchOption.setEnabled(true);
		} else {
			this.textArchOption.setEnabled(false);
		}
	}
}
