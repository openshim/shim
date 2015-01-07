/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.ccf.sampleapp;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.TableItem;


public class CCF_ConfigurationSetComposite extends Composite {
	public Composite composite;
	public CCF_ConfigurationComposite f_ConfigurationComposite;
	public Label lbl_ConfigurationSetName;
	public List list_defineSet;
	public Label lblDefineset;
	public Table table_DefineSet;
	private TableViewer tableViewer;
	public TableColumn tblclmn_defName;
	private TableViewerColumn tableViewerColumn;
	public TableColumn tblclmn_defUri;
	private TableViewerColumn tableViewerColumn_1;
	public TableColumn tblclmn_defPath;
	private TableViewerColumn tableViewerColumn_2;
	public ScrolledComposite scrolledComposite;
	public TableItem tableItem;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CCF_ConfigurationSetComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));
		
		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.NORTH);
		composite.setLayout(new GridLayout(2, false));
		
		lbl_ConfigurationSetName = new Label(composite, SWT.NONE);
		lbl_ConfigurationSetName.setText("*CSNAME*");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		scrolledComposite = new ScrolledComposite(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		tableViewer = new TableViewer(scrolledComposite, SWT.BORDER | SWT.FULL_SELECTION);
		table_DefineSet = tableViewer.getTable();
		table_DefineSet.setHeaderVisible(true);
		
		tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmn_defName = tableViewerColumn.getColumn();
		tblclmn_defName.setWidth(100);
		tblclmn_defName.setText("name");
		
		tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmn_defPath = tableViewerColumn_2.getColumn();
		tblclmn_defPath.setWidth(100);
		tblclmn_defPath.setText("xpath");
		
		tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmn_defUri = tableViewerColumn_1.getColumn();
		tblclmn_defUri.setAlignment(SWT.NONE);
		tblclmn_defUri.setWidth(100);
		tblclmn_defUri.setText("file uri");
		
		tableItem = new TableItem(table_DefineSet, SWT.NONE);
		tableItem.setText("New TableItem");
		scrolledComposite.setContent(table_DefineSet);
		scrolledComposite.setMinSize(table_DefineSet.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		tableItem.setText(0, "var1");
		
		lblDefineset = new Label(composite, SWT.NONE);
		lblDefineset.setText("DefineSet");
		
		list_defineSet = new List(composite, SWT.BORDER);
		GridData gd_list_defineSet = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_list_defineSet.widthHint = 419;
		list_defineSet.setLayoutData(gd_list_defineSet);
		list_defineSet.add("hello");
		list_defineSet.add("world");
		
		f_ConfigurationComposite = new CCF_ConfigurationComposite(this, SWT.NONE);
		f_ConfigurationComposite.setLayoutData(BorderLayout.CENTER);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
