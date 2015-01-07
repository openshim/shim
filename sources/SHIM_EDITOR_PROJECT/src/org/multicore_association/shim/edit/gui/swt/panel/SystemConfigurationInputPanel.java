/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import java.awt.BorderLayout;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.ClockFrequency;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.gui.databinding.FloatToStringConverter;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;

/**
 * An InputPanel implementation for SystemConfiguration.
 */
public class SystemConfigurationInputPanel extends InputPanelBase {

	private static final Logger log = ShimLoggerUtil
			.getLogger(SystemConfigurationInputPanel.class);

	private Text textName;
	private Text textFrequency;

	private SystemConfiguration systemConfiguration = new SystemConfiguration();
	private Text textShimVersion;

	/**
	 * Constructs a new instance of SystemConfigurationInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public SystemConfigurationInputPanel(Composite parent, int style) {
		super(parent, style);

		setLblTitleText("System Configuration");

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(2, false));

		Label lblShimVersion = new Label(composite, SWT.NONE);
		lblShimVersion.setText("Shim Version");

		textShimVersion = new Text(composite, SWT.BORDER);
		textShimVersion.setEditable(false);
		GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 80;
		textShimVersion.setLayoutData(gd_text);

		Label lbl_Name = new Label(composite, SWT.NONE);
		lbl_Name.setText("Name");

		textName = new Text(composite, SWT.BORDER);
		GridData gd_textName = new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1);
		gd_textName.widthHint = 210;
		textName.setLayoutData(gd_textName);

		Label lblClockFrequency = new Label(composite, SWT.NONE);
		lblClockFrequency.setText("Clock Frequency");

		Composite composite_1 = new Composite(composite, SWT.NONE);
		GridData gd_composite_1 = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_composite_1.widthHint = 150;
		composite_1.setLayoutData(gd_composite_1);
		GridLayout gl_composite_1 = new GridLayout(2, false);
		gl_composite_1.marginWidth = 0;
		gl_composite_1.horizontalSpacing = 0;
		gl_composite_1.marginHeight = 0;
		composite_1.setLayout(gl_composite_1);

		textFrequency = new Text(composite_1, SWT.BORDER);
		textFrequency.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lblHz = new Label(composite_1, SWT.NONE);
		GridData gd_lblHz = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_lblHz.widthHint = 24;
		lblHz.setLayoutData(gd_lblHz);
		lblHz.setText("Hz");

		// Checking Required.
		textName.addModifyListener(new TextModifyListener("name"));
		textFrequency
				.addModifyListener(new TextModifyListener("clockFrequency"));
		textFrequency.addModifyListener(new NumberModifyListener(
				"clockFrequency"));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		if (systemConfiguration == null) {
			return;
		}

		systemConfiguration.setName(textName.getText());
		ClockFrequency clockFrequency = systemConfiguration.getClockFrequency();
		if (clockFrequency == null) {
			clockFrequency = new ClockFrequency();
			systemConfiguration.setClockFrequency(clockFrequency);
		}
		clockFrequency.setClockValue(Float.parseFloat(textFrequency.getText()));

		log.finest("[applyChange] : SystemConfiguration" + "\n  Name="
				+ systemConfiguration.getName() + "\n  Clock="
				+ systemConfiguration.getClockFrequency().getClockValue());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		systemConfiguration = (SystemConfiguration) input;

		textShimVersion.setText(systemConfiguration.getShimVersion());
		textName.setText(systemConfiguration.getName());

		ClockFrequency clockFrequency = systemConfiguration.getClockFrequency();
		if (clockFrequency != null) {
			textFrequency.setText(FloatToStringConverter
					.convertStr(clockFrequency.getClockValue()));
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return systemConfiguration;
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return systemConfiguration.getClass();
	}
}
