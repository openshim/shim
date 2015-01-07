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
import org.multicore_association.shim.api.FIFOCommunication;
import org.multicore_association.shim.api.SizeUnitType;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.EnumUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for FIFOCommunication.
 */
public class FIFOCommunicationInputPanel extends InputPanelBase {

	private static final Logger log = ShimLoggerUtil
			.getLogger(FIFOCommunicationInputPanel.class);

	private Text textName;

	private FIFOCommunication fifoComm = new FIFOCommunication();
	private Text textDataSize;
	private Text textQueueSize;
	private Combo comboDataSizeUnit;

	/**
	 * Constructs a new instance of FIFOCommunicationInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public FIFOCommunicationInputPanel(Composite parent, int style) {

		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		setLblTitleText("FIFO Communication");

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(4, false));

		Label lbl_Name = new Label(composite, SWT.NONE);
		lbl_Name.setText("Name");
		new Label(composite, SWT.NONE);

		textName = new Text(composite, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2,
				1));

		Label lbl_dataSize = new Label(composite, SWT.NONE);
		lbl_dataSize.setText("DataSize");
		new Label(composite, SWT.NONE);

		textDataSize = new Text(composite, SWT.BORDER);

		comboDataSizeUnit = new ComboFactory(composite).createCombo(
				SizeUnitType.class, ShimModelAdapter.isRequired(
						FIFOCommunication.class, "dataSizeUnit"));

		Label lbl_QueueSize = new Label(composite, SWT.NONE);
		lbl_QueueSize.setText("QueueSize");
		new Label(composite, SWT.NONE);

		textQueueSize = new Text(composite, SWT.BORDER);
		new Label(composite, SWT.NONE);

		// Checking Required.
		textName.addModifyListener(new TextModifyListener("name"));
		textDataSize.addModifyListener(new TextModifyListener("dataSize"));
		textDataSize.addModifyListener(new NumberModifyListener("dataSize"));
		textQueueSize.addModifyListener(new TextModifyListener("queueSize"));
		textQueueSize.addModifyListener(new NumberModifyListener("queueSize"));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {

		fifoComm = (FIFOCommunication) input;

		textName.setText(fifoComm.getName());
		textDataSize.setText(fifoComm.getDataSize() + "");
		textQueueSize.setText(fifoComm.getQueueSize() + "");

		if (fifoComm.getDataSizeUnit() != null) {
			comboDataSizeUnit.select(fifoComm.getDataSizeUnit().ordinal());
		} else {
			comboDataSizeUnit.select(comboDataSizeUnit.getItemCount() - 1);
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {

		assert (fifoComm != null);

		fifoComm.setName(this.textName.getText());
		fifoComm.setDataSize(Integer.parseInt(this.textDataSize.getText()));
		fifoComm.setDataSizeUnit(EnumUtil.getSizeUnitType(comboDataSizeUnit
				.getSelectionIndex()));

		fifoComm.setQueueSize(Integer.parseInt(this.textQueueSize.getText()));

		log.finest("[applyChange] : FIFOCommunication" + "\n  Name="
				+ fifoComm.getName() + "\n  DataSize=" + fifoComm.getDataSize()
				+ "\n  DataSizeUnit=" + fifoComm.getDataSizeUnit()
				+ "\n  QueueSize=" + fifoComm.getQueueSize());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return fifoComm.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		fifoComm = new FIFOCommunication();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) fifoComm;
	}
}
