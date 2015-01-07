/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.EndianType;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.EnumUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;

import swing2swt.layout.BorderLayout;

import com.sun.xml.internal.ws.util.StringUtils;

/**
 * An InputPanel implementation for SubSpace.
 */
public class SubSpaceInputPanel extends InputPanelBase {

	private static final Logger log = ShimLoggerUtil
			.getLogger(SubSpaceInputPanel.class);

	private static final Pattern pattern = Pattern.compile("[0-9a-fA-F]+");

	private SubSpace subSpace = new SubSpace();
	private Text textName;
	private Text textStart;
	private Text textEnd;
	private Combo comboEndian;

	/**
	 * Constructs a new instance of SubSpaceInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public SubSpaceInputPanel(Composite parent, int style) {
		super(parent, style);

		setLblTitleText("Sub Space");

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(2, false));

		Label lblName = new Label(composite, SWT.NONE);
		GridData gd_lblName = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_lblName.widthHint = 150;
		lblName.setLayoutData(gd_lblName);
		lblName.setText("Name");

		textName = new Text(composite, SWT.BORDER);
		GridData gd_textName = new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1);
		gd_textName.widthHint = 150;
		textName.setLayoutData(gd_textName);
		textName.addModifyListener(new TextModifyListener("name"));

		Label lblStart = new Label(composite, SWT.NONE);
		lblStart.setText("Start (hexadecimal)");

		textStart = new Text(composite, SWT.BORDER);
		GridData gd_textStart = new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1);
		gd_textStart.widthHint = 160;
		textStart.setLayoutData(gd_textStart);
		textStart.addModifyListener(new HexModifyListener(lblStart.getText()));
		textStart.addModifyListener(new CompareStartToEndModifyListener());

		Label lblEnd = new Label(composite, SWT.NONE);
		lblEnd.setText("End (hexadecimal)");

		textEnd = new Text(composite, SWT.BORDER);
		GridData gd_textEnd = new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1);
		gd_textEnd.widthHint = 160;
		textEnd.setLayoutData(gd_textEnd);
		textEnd.addModifyListener(new HexModifyListener(lblEnd.getText()));
		textEnd.addModifyListener(new CompareStartToEndModifyListener());

		Label lblEndian = new Label(composite, SWT.NONE);
		lblEndian.setText("Endian");

		comboEndian = new ComboFactory(composite).createCombo(EndianType.class,
				ShimModelAdapter.isRequired(getApiClass(), "endian"));
		comboEndian.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		if (subSpace == null) {
			return;
		}

		subSpace.setName(textName.getText());
		subSpace.setStart(Long.parseLong(textStart.getText(), 16));
		subSpace.setEnd(Long.parseLong(textEnd.getText(), 16));
		subSpace.setEndian(EnumUtil.getEndianType(comboEndian
				.getSelectionIndex()));

		log.finest("[applyChange] : SubSpace" + "\n  Name="
				+ subSpace.getName() + "\n  Start=" + subSpace.getStart()
				+ "\n  End=" + subSpace.getEnd() + "\n  Endian="
				+ subSpace.getEndian());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		subSpace = (SubSpace) input;

		textName.setText(subSpace.getName());
		textStart.setText(String.format(CommonConstants.FORMAT_HEX,
				subSpace.getStart()));
		textEnd.setText(String.format(CommonConstants.FORMAT_HEX,
				subSpace.getEnd()));

		EndianType endian = subSpace.getEndian();
		if (endian == null) {
			comboEndian.select(comboEndian.getItemCount() - 1);
		} else {
			comboEndian.select(endian.ordinal());
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return subSpace;
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return subSpace.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		subSpace = new SubSpace();
	}

	/**
	 * An ModifyListener implementation for hexadecimal.
	 */
	class HexModifyListener implements ModifyListener {

		private String paramName;

		/**
		 * Constructs a new instance of HexModifyListener.
		 * 
		 * @param paramName
		 *            the parameter name to be modified
		 */
		public HexModifyListener(String paramName) {
			this.paramName = paramName;
		}

		/**
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 */
		@Override
		public void modifyText(ModifyEvent event) {
			TreeSet<String> errList = getErrList(this.getClass());

			Text text = (Text) event.getSource();
			String value = text.getText();
			Matcher matcher = pattern.matcher(value);

			if (matcher.matches()) {
				try {
					Long.parseLong((String) value, 16);
				} catch (NumberFormatException e) {
					errList.add(paramName);
				}

				errList.remove(paramName);

			} else {
				errList.add(paramName);
			}

			int i = 0;
			StringBuffer sb = new StringBuffer();
			for (String _propertyName : errList) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(StringUtils.capitalize(_propertyName));
				i++;
			}
			if (sb.length() > 0) {
				String msg = "Please enter a hexadecimal number between 0-"
						+ String.format(CommonConstants.FORMAT_HEX,
								Long.MAX_VALUE) + " in " + sb.toString()
						+ " field.";
				setErrorMessage(msg);
			} else {
				setErrorMessage(null);
			}
		}
	}

	/**
	 * An ModifyListener implementation for comparing the 'start' value with the
	 * 'end' value.
	 */
	class CompareStartToEndModifyListener implements ModifyListener {

		/**
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 */
		@Override
		public void modifyText(ModifyEvent event) {
			TreeSet<String> errList = getErrList(this.getClass());

			try {
				boolean isStart = event.getSource().equals(textStart);

				long start = Long.parseLong(textStart.getText(), 16);
				long end = Long.parseLong(textEnd.getText(), 16);

				if (start >= end) {
					errList.add(this.getClass().getSimpleName());
					String msg = isStart ? "Start value must be smaller than end value."
							: "End value must be larger than start value.";
					setErrorMessage(msg);
				} else {
					errList.remove(this.getClass().getSimpleName());
					setErrorMessage(null);
				}

			} catch (NumberFormatException e) {
			}
		}

	}

}
