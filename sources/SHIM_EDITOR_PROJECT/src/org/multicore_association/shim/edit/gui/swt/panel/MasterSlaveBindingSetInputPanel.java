/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.multicore_association.shim.api.MasterSlaveBinding;
import org.multicore_association.shim.api.MasterSlaveBindingSet;
import org.multicore_association.shim.edit.gui.swt.viewer.ShimObjectTableViewer;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimObject;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for MasterSlaveBindingSet.
 */
public class MasterSlaveBindingSetInputPanel extends InputPanelBase {

	private MasterSlaveBindingSet masterSlaveBindingSet = new MasterSlaveBindingSet();

	private ShimObjectTableViewer tableViewer;

	/**
	 * Constructs a new instance of MasterSlaveBindingSetInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public MasterSlaveBindingSetInputPanel(Composite parent, int style) {
		super(parent, style);

		setLayout(new BorderLayout(0, 0));

		setLblTitleText("Master Slave Binding Set");

		Composite composite_2 = new Composite(this, SWT.NONE);
		composite_2.setLayoutData(BorderLayout.CENTER);
		composite_2.setLayout(new BorderLayout(0, 0));

		Composite composite = new Composite(composite_2, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);

		tableViewer = new ShimObjectTableViewer(composite, SWT.BORDER, this);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumnLayout tcl_composite = new TableColumnLayout();
		composite.setLayout(tcl_composite);

		// To reset the Apply Button and Text of error message.
		btnApply.setVisible(false);
		textErrorMassage.setVisible(false);

		tableViewer
				.initTabeleViewer(new MasterSlaveBinding().getClass(), false);
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		assert (masterSlaveBindingSet != null);
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		if (input != null) {
			masterSlaveBindingSet = (MasterSlaveBindingSet) input;

			List<ShimObject> objectList = ShimModelAdapter.getObjectsList(
					MasterSlaveBinding.class, masterSlaveBindingSet);
			tableViewer.setSelectableItem(this.selectableItem);
			tableViewer.setInput(objectList);

			// resize
			Table table = tableViewer.getTable();
			table.setVisible(false);
			TableColumn[] columns = table.getColumns();
			for (TableColumn column : columns) {
				column.pack();
				column.setWidth(column.getWidth() + 20);
			}
			table.setVisible(true);
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return masterSlaveBindingSet;
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return masterSlaveBindingSet.getClass();
	}

}
