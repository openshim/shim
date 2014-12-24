/*
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
import org.multicore_association.shim.api.AccessType;
import org.multicore_association.shim.api.RWType;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.EnumUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for AccessType.
 */
public class AccessTypeInputPanel extends InputPanelBase {

	private static final Logger log = ShimLoggerUtil
			.getLogger(AccessTypeInputPanel.class);

	private Text textName;
	private Text textAccessSize;
	private Text textAlignmentSize;
	private Text textNBurst;

	private Combo comboRwType;

	private AccessType accessType = new AccessType();

	/**
	 * Constructs a new instance of AccessTypeInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public AccessTypeInputPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		setLblTitleText("Access Type");

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(3, false));

		Label lbl_Name = new Label(composite, SWT.NONE);
		lbl_Name.setText("Name");
		new Label(composite, SWT.NONE);

		textName = new Text(composite, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Label lbl_rwType = new Label(composite, SWT.NONE);
		lbl_rwType.setText("rwType");
		new Label(composite, SWT.NONE);

		comboRwType = new ComboFactory(composite).createCombo(RWType.class,
				ShimModelAdapter.isRequired(getApiClass(), "rwType"));
		comboRwType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lbl_AccessSize = new Label(composite, SWT.NONE);
		lbl_AccessSize.setText("Access Size");
		new Label(composite, SWT.NONE);

		Composite composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setLayout(new GridLayout(2, false));

		textAccessSize = new Text(composite_3, SWT.BORDER);
		textAccessSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lblByte = new Label(composite_3, SWT.NONE);
		lblByte.setText("byte");

		Label lbl_AlignmentSize = new Label(composite, SWT.NONE);
		lbl_AlignmentSize.setText("Alignment Size");
		new Label(composite, SWT.NONE);

		Composite composite_4 = new Composite(composite, SWT.NONE);
		composite_4.setLayout(new GridLayout(2, false));

		textAlignmentSize = new Text(composite_4, SWT.BORDER);
		textAlignmentSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));

		Label lblByte_1 = new Label(composite_4, SWT.NONE);
		lblByte_1.setText("byte");

		Label lbl_nBurst = new Label(composite, SWT.NONE);
		lbl_nBurst.setText("nBurst");
		new Label(composite, SWT.NONE);

		textNBurst = new Text(composite, SWT.BORDER);
		textNBurst.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		// Checking Required.
		textName.addModifyListener(new TextModifyListener("name"));
		textAccessSize.addModifyListener(new TextModifyListener(
				"accessByteSize"));
		textAccessSize.addModifyListener(new NumberModifyListener(
				"accessByteSize"));
		textAlignmentSize.addModifyListener(new TextModifyListener(
				"alignmentByteSize"));
		textAlignmentSize.addModifyListener(new NumberModifyListener(
				"alignmentByteSize"));
		textNBurst.addModifyListener(new TextModifyListener("nBurst"));
		textNBurst.addModifyListener(new NumberModifyListener("nBurst"));

	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		accessType = (AccessType) input;

		this.textName.setText(accessType.getName());

		Integer accessByteSize = accessType.getAccessByteSize();
		if (accessByteSize == null) {
			this.textAccessSize.setText("");
		} else {
			this.textAccessSize.setText(accessByteSize + "");
		}

		Integer alignmentByteSize = accessType.getAlignmentByteSize();
		if (alignmentByteSize == null) {
			this.textAlignmentSize.setText("");
		} else {
			this.textAlignmentSize.setText(alignmentByteSize + "");
		}

		Integer nBurst = accessType.getNBurst();
		if (nBurst == null) {
			this.textNBurst.setText("");
		} else {
			this.textNBurst.setText(nBurst + "");
		}

		RWType rwType = accessType.getRwType();
		if (rwType == null) {
			comboRwType.select(comboRwType.getItemCount() - 1);
		} else {
			comboRwType.select(rwType.ordinal());
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {

		accessType.setName(textName.getText());
		String str_accessSize = textAccessSize.getText();
		if (isNullOrEmpty(str_accessSize)) {
			accessType.setAccessByteSize(null);
		} else {
			accessType.setAccessByteSize(Integer.parseInt(str_accessSize));
		}
		String str_alignmentSize = textAlignmentSize.getText();
		if (isNullOrEmpty(str_alignmentSize)) {
			accessType.setAlignmentByteSize(null);
		} else {
			accessType
					.setAlignmentByteSize(Integer.parseInt(str_alignmentSize));
		}
		accessType
				.setRwType(EnumUtil.getRWType(comboRwType.getSelectionIndex()));
		String str_nBurst = textNBurst.getText();
		if (isNullOrEmpty(str_nBurst)) {
			accessType.setNBurst(null);
		} else {
			accessType.setNBurst(Integer.parseInt(str_nBurst));
		}

		log.finest("[applyChange] : AccessType\n" + "  Name="
				+ accessType.getName() + "\n  AccessByteSize="
				+ accessType.getAccessByteSize() + "\n  AlignmentByteSize="
				+ accessType.getAlignmentByteSize() + "\n  RWType="
				+ accessType.getRwType() + "\n  NBurst="
				+ accessType.getNBurst());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return accessType.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		accessType = new AccessType();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) accessType;
	}

}
