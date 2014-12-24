/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.AddressSpace;
import org.multicore_association.shim.api.OperationType;
import org.multicore_association.shim.api.SharedMemoryCommunication;
import org.multicore_association.shim.api.SizeUnitType;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.gui.common.RefComboFactory;
import org.multicore_association.shim.edit.model.EnumUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for SharedMemoryCommunication.
 */
public class SharedMemoryCommunicationInputPanel extends InputPanelBase {
	private Text text_Name;

	private SharedMemoryCommunication shmemComm = new SharedMemoryCommunication();
	private Text textDataSize;
	private Combo comboDataSizeUnit;
	private Combo comboSubSpaceRef;
	private Combo comboAddressSpaceRef;
	private RefComboFactory refComboFactoryAs;
	private RefComboFactory refComboFactorySs;
	private Label lblOperationtype;
	private Combo comboOperationType;

	/**
	 * Constructs a new instance of SharedMemoryCommunicationInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public SharedMemoryCommunicationInputPanel(Composite parent, int style) {

		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		setLblTitleText("SharedMemory Communication");

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(4, false));

		Label lbl_Name = new Label(composite, SWT.NONE);
		lbl_Name.setText("Name");
		new Label(composite, SWT.NONE);

		text_Name = new Text(composite, SWT.BORDER);
		text_Name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1));

		Label lbl_dataSize = new Label(composite, SWT.NONE);
		lbl_dataSize.setText("DataSize");
		new Label(composite, SWT.NONE);

		textDataSize = new Text(composite, SWT.BORDER);
		GridData gd_text_DataSize = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_text_DataSize.widthHint = 105;
		textDataSize.setLayoutData(gd_text_DataSize);

		comboDataSizeUnit = new ComboFactory(composite).createCombo(
				SizeUnitType.class,
				ShimModelAdapter.isRequired(getApiClass(), "dataSizeUnit"));
		comboDataSizeUnit.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				true, false, 1, 1));

		Label lbl_addressSpace = new Label(composite, SWT.NONE);
		lbl_addressSpace.setText("AddressSpace");
		new Label(composite, SWT.NONE);

		refComboFactoryAs = new RefComboFactory(composite, getApiClass(),
				"addressSpaceRef", AddressSpace.class);
		comboAddressSpaceRef = refComboFactoryAs.createEmptyCombo();
		comboAddressSpaceRef.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 2, 1));

		Label lbl_subSpace = new Label(composite, SWT.NONE);
		lbl_subSpace.setText("SubSpace");
		new Label(composite, SWT.NONE);

		refComboFactorySs = new RefComboFactory(composite, getApiClass(),
				"subSpaceRef", SubSpace.class);
		comboSubSpaceRef = refComboFactorySs.createEmptyCombo();
		comboSubSpaceRef.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));

		// Checking Required.
		text_Name.addModifyListener(new TextModifyListener("name"));
		textDataSize.addModifyListener(new TextModifyListener("dataSize"));
		textDataSize.addModifyListener(new NumberModifyListener("dataSize"));
		textDataSize.addModifyListener(new ModifyListener() {
			/**
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent event) {
				Text source = (Text) event.getSource();
				String text = source.getText();
				if (text.isEmpty()) {
					comboDataSizeUnit.select(comboDataSizeUnit.getItemCount() - 1);
				}
			}
		});
		comboAddressSpaceRef.addModifyListener(new TextModifyListener(
				"addressSpaceRef"));

		// Added Listener.
		refComboFactoryAs.setSelectionListener(refComboFactorySs);

		lblOperationtype = new Label(composite, SWT.NONE);
		lblOperationtype.setText("OperationType");
		new Label(composite, SWT.NONE);

		comboOperationType = new ComboFactory(composite).createCombo(
				OperationType.class,
				ShimModelAdapter.isRequired(getApiClass(), "operationType"));
		comboOperationType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		new Label(composite, SWT.NONE);
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		shmemComm = (SharedMemoryCommunication) input;

		text_Name.setText(shmemComm.getName());

		Integer dataSize = shmemComm.getDataSize();
		if (dataSize == null) {
			textDataSize.setText("");
		} else {
			textDataSize.setText(dataSize + "");
		}

		SizeUnitType dataSizeUnit = shmemComm.getDataSizeUnit();
		if (dataSizeUnit == null) {
			comboDataSizeUnit.select(comboDataSizeUnit.getItemCount() - 1);
		} else {
			comboDataSizeUnit.select(dataSizeUnit.ordinal());
		}

		AddressSpace as = (AddressSpace) shmemComm.getAddressSpaceRef();
		comboAddressSpaceRef = refComboFactoryAs.createCombo();
		refComboFactoryAs.setText(as);

		SubSpace ss = (SubSpace) shmemComm.getSubSpaceRef();
		comboSubSpaceRef = refComboFactorySs.createCombo(as);
		refComboFactorySs.setText(ss);

		if (shmemComm.getOperationType() == null) {
			comboOperationType.select(comboOperationType.getItemCount() - 1);
		} else {
			comboOperationType.select(shmemComm.getOperationType().ordinal());
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		assert (shmemComm != null);

		shmemComm.setName(this.text_Name.getText());

		String str_DataSize = this.textDataSize.getText();
		if (isNullOrEmpty(str_DataSize)) {
			shmemComm.setDataSize(null);
		} else {
			shmemComm.setDataSize(Integer.parseInt(str_DataSize));
		}

		shmemComm.setDataSizeUnit(EnumUtil.getSizeUnitType(comboDataSizeUnit
				.getSelectionIndex()));

		shmemComm.setAddressSpaceRef(refComboFactoryAs.getRefObject());
		shmemComm.setSubSpaceRef(refComboFactorySs.getRefObject());
		shmemComm.setOperationType(EnumUtil.getOperationType(comboOperationType
				.getSelectionIndex()));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return shmemComm.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		refComboFactoryAs.dispose();
		refComboFactorySs.dispose();
		shmemComm = new SharedMemoryCommunication();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) shmemComm;
	}
}
