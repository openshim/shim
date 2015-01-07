/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
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
import org.multicore_association.shim.api.Connection;
import org.multicore_association.shim.api.ConnectionSet;
import org.multicore_association.shim.edit.gui.swt.viewer.ShimObjectTableViewer;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimObject;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for ConnectionSet.
 */
public class ConnectionSetInputPanel extends InputPanelBase {
	
	private ConnectionSet connectionSet = new ConnectionSet();

	private ShimObjectTableViewer tableViewer;

	/**
	 * Constructs a new instance of ConnectionSetInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public ConnectionSetInputPanel(Composite parent, int style) {
		super(parent, SWT.BORDER);
		setLayout(new BorderLayout(0, 0));

		setLblTitleText("Connection Set");

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
		textErrorMassage.setVisible(true);

		tableViewer.initTabeleViewer(new Connection().getClass(), false);
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		assert(connectionSet != null);
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		if (input != null) {
			connectionSet = (ConnectionSet) input;

			List<ShimObject> objectList = ShimModelAdapter.getObjectsList(Connection.class,
					connectionSet);
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
			
			tableViewer.refresh();
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return connectionSet;
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return connectionSet.getClass();
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		tableViewer.dispose();
	}

}
