/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.edit.gui.swt.viewer.AddressSpaceTreeItemProvider;
import org.multicore_association.shim.edit.gui.swt.viewer.AddressSpaceTreeLabelProvider;

import swing2swt.layout.BorderLayout;


/**
 * An InputPanel implementation for a table of SubSpace.
 */
public class SubSpaceTableInputPanel extends InputPanelBase {
	
	private SubSpace subSpace = new SubSpace();
	private Table table;
	
	private TableViewer tableViewer;

	/**
	 * Constructs a new instance of SubSpaceTableInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public SubSpaceTableInputPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		setLblTitleText("SubSpace");
		
		Composite composite_2 = new Composite(this, SWT.NONE);
		composite_2.setLayoutData(BorderLayout.CENTER);
		composite_2.setLayout(new BorderLayout(0, 0));
		
		Composite composite = new Composite(composite_2, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		TableColumnLayout tcl_composite = new TableColumnLayout();
		composite.setLayout(tcl_composite);
		
		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmn_Name = tableViewerColumn.getColumn();
		tcl_composite.setColumnData(tblclmn_Name, new ColumnPixelData(206, true, true));
		tblclmn_Name.setText("Name");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmn_Start = tableViewerColumn_2.getColumn();
		tblclmn_Start.setText("Start");
		tcl_composite.setColumnData(tblclmn_Start, new ColumnPixelData(150, true, true));
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmn_End = tableViewerColumn_3.getColumn();
		tblclmn_End.setText("End");
		tcl_composite.setColumnData(tblclmn_End, new ColumnPixelData(150, true, true));
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmn_Endian = tableViewerColumn_1.getColumn();
		tblclmn_Endian.setText("Endian");
		tcl_composite.setColumnData(tblclmn_Endian, new ColumnPixelData(150, true, true));
		tableViewer.setLabelProvider(new AddressSpaceTreeLabelProvider());
		tableViewer.setContentProvider(new AddressSpaceTreeItemProvider());
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		subSpace = (SubSpace)input;
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		assert(subSpace != null);
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
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) subSpace;
	}
}
