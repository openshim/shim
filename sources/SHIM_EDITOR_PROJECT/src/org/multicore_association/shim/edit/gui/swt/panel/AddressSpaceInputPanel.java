/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.AddressSpace;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.edit.gui.common.ErrorMessageWriter;
import org.multicore_association.shim.edit.gui.swt.viewer.ShimObjectTableViewer;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimObject;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for InputPanel_AddressSpace.
 */
public class AddressSpaceInputPanel extends InputPanelBase {

	private AddressSpace addressSpace = new AddressSpace();
	private Text textAddressSpaceName;
	private ErrorMessageWriter errorMassageWriter = null;
	private boolean calledFromWizard = false;
	private ShimObjectTableViewer subSpaceTableViewer;
	private Table table;
	private Composite compositeSub;

	/**
	 * Constructs a new instance of AddressSpaceInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public AddressSpaceInputPanel(Composite parent, int style,
			boolean calledFromWizard) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		setLblTitleText("Address Space");

		Composite composite_2 = new Composite(this, SWT.NONE);
		composite_2.setLayoutData(BorderLayout.CENTER);
		composite_2.setLayout(new GridLayout(1, true));

		Composite composite_InstructionSet = new Composite(composite_2,
				SWT.NONE);
		composite_InstructionSet.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
				true, false, 1, 1));
		composite_InstructionSet.setLayout(new GridLayout(3, false));

		Label lbl_AddressSpaceName = new Label(composite_InstructionSet,
				SWT.NONE);
		lbl_AddressSpaceName.setText("Name");
		new Label(composite_InstructionSet, SWT.NONE);

		textAddressSpaceName = new Text(composite_InstructionSet, SWT.BORDER);
		textAddressSpaceName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, true, 1, 1));

		Composite composite = new Composite(composite_2, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		composite.setLayout(new GridLayout(1, true));

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setText("Sub Space");

		compositeSub = new Composite(composite, SWT.NONE);
		compositeSub.setLayoutData(new GridData(GridData.FILL_BOTH));
		compositeSub.setLayout(new GridLayout(1, true));

		subSpaceTableViewer = new ShimObjectTableViewer(compositeSub,
				SWT.FILL, this);
		table = subSpaceTableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		this.calledFromWizard = calledFromWizard;
		if (calledFromWizard) {
			// To reset the composite which has a Apply Button.
			Composite composite_1 = getComposite_1();
			composite_1.setLayoutData(BorderLayout.EAST);
			textErrorMassage.setVisible(false);
		}

		subSpaceTableViewer.initTabeleViewer(new SubSpace().getClass(), false);

		// Checking Required.
		textAddressSpaceName.addModifyListener(new TextModifyListener("name"));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		addressSpace = (AddressSpace) input;

		this.textAddressSpaceName.setText(addressSpace.getName());

		List<ShimObject> objectList = ShimModelAdapter.getObjectsList(
				SubSpace.class, addressSpace);
		subSpaceTableViewer.setSelectableItem(this.selectableItem);
		subSpaceTableViewer.setInput(objectList);
		
		subSpaceTableViewer.refresh();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		assert (addressSpace != null);
		addressSpace.setName(this.textAddressSpaceName.getText());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return addressSpace.getClass();
	}

	/**
	 * Sets an ErrorMassageWriter instance.
	 * 
	 * @param writer
	 */
	public void setErrorMassageWriterForWizard(ErrorMessageWriter writer) {
		errorMassageWriter = writer;
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setErrorMessage(java.lang.String)
	 */
	@Override
	public void setErrorMessage(String value) {
		if (calledFromWizard && errorMassageWriter != null) {
			errorMassageWriter.writeErrorMessage(value);
		} else {
			super.setErrorMessage(value);
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		addressSpace = new AddressSpace();
		errorMassageWriter = null;
		subSpaceTableViewer.dispose();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) addressSpace;
	}
}
