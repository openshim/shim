/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.RWType;
import org.multicore_association.shim.api.SizeUnitType;
import org.multicore_association.shim.api.SlaveComponent;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for InputPanel_SlaveComponent.
 */
public class SlaveComponentInputPanel extends InputPanelBase {

	private static final Logger log = ShimLoggerUtil
			.getLogger(SlaveComponentInputPanel.class);

	private Text textName;
	private Text textSlcSize;
	private Combo comboSlcSizeUnit;
	private Combo comboRwType;

	private SlaveComponent slaveComponent = new SlaveComponent();

	/**
	 * Constructs a new instance of SlaveComponentInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public SlaveComponentInputPanel(Composite parent, int style) {
		super(parent, style);
		btnApply.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				// NOOP
			}
		});
		setLayout(new BorderLayout(0, 0));

		setLblTitleText("Slave Component");

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(3, false));

		Label lbl_Name = new Label(composite, SWT.NONE);
		lbl_Name.setText("Name");
		new Label(composite, SWT.NONE);

		textName = new Text(composite, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Label lbl_Size = new Label(composite, SWT.NONE);
		lbl_Size.setText("Size");
		new Label(composite, SWT.NONE);

		Composite composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setLayout(new GridLayout(2, false));

		textSlcSize = new Text(composite_3, SWT.BORDER);
		GridData gd_text_slc_Size = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		gd_text_slc_Size.widthHint = 96;
		textSlcSize.setLayoutData(gd_text_slc_Size);

		comboSlcSizeUnit = new ComboFactory(composite_3).createCombo(
				SizeUnitType.class,
				ShimModelAdapter.isRequired(getApiClass(), "sizeUnit"));
		comboSlcSizeUnit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lbl_rwType = new Label(composite, SWT.NONE);
		lbl_rwType.setText("rwType");
		new Label(composite, SWT.NONE);

		Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		composite_2.setLayout(new GridLayout(1, false));

		comboRwType = new ComboFactory(composite_2).createCombo(RWType.class,
				ShimModelAdapter.isRequired(getApiClass(), "rwType"));
		comboRwType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		// Checking Required.
		textName.addModifyListener(new TextModifyListener("name"));
		textSlcSize.addModifyListener(new TextModifyListener("size"));
		textSlcSize.addModifyListener(new NumberModifyListener("size"));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		if (slaveComponent == null) {
			return;
		}
		slaveComponent.setName(textName.getText());
		slaveComponent.setSize(Integer.parseInt(textSlcSize.getText()
				.toString()));
		slaveComponent.setSizeUnit(SizeUnitType.fromValue(this.comboSlcSizeUnit
				.getText()));
		slaveComponent.setRwType(RWType.fromValue(this.comboRwType.getText()));

		log.finest("[applyChange] : SlaveComponent" + "\n  Name="
				+ slaveComponent.getName() + "\n  Size="
				+ slaveComponent.getSize() + "\n  SizeUnit="
				+ slaveComponent.getSizeUnit() + "\n  RwType="
				+ slaveComponent.getRwType());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object slc) {
		slaveComponent = (SlaveComponent) slc;

		textName.setText(slaveComponent.getName());
		textSlcSize.setText(slaveComponent.getSize() + "");
		comboSlcSizeUnit.setText(slaveComponent.getSizeUnit().value());
		comboRwType.setText(slaveComponent.getRwType() + "");
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return slaveComponent.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		slaveComponent = new SlaveComponent();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) slaveComponent;
	}
}
