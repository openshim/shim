/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.edit.gui.swt.ObjectListRefresher;
import org.multicore_association.shim.edit.gui.swt.viewer.ShimObjectTableFilter;
import org.multicore_association.shim.edit.gui.swt.viewer.ShimObjectTableViewer;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimModelManager;
import org.multicore_association.shim.edit.model.ShimObject;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for ShimObject table.
 */
public class ShimObjectTableInputPanel extends InputPanelBase implements
		ObjectListRefresher {

	private static final String LABEL_PREFIX = "list of ";
	private static final String ANY_FIELD = "[ANY]";

	private List<ShimObject> objectList = null;
	private Composite composite;
	private Composite filterComposite;
	private Combo filterCombo;
	private Text filterText;

	private ShimObjectTableViewer tableViewer;
	private ShimObjectTableFilter tableFilter;

	private Class<?> apiClass;

	/**
	 * Constructs a new instance of ShimObjectTableInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public ShimObjectTableInputPanel(Composite parent, int style) {
		super(parent, style);

		setLblTitleText("[ANY NAME]");

		apiClass = null;

		Composite composite_2 = new Composite(this, SWT.NONE);
		composite_2.setLayoutData(BorderLayout.CENTER);
		composite_2.setLayout(new BorderLayout(0, 0));

		composite = new Composite(composite_2, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(1, false));

		createFilterInputArea();

		createTableComposite();
	}

	/**
	 * Creates the composite to filter the list.
	 */
	private void createFilterInputArea() {
		filterComposite = new Composite(composite, SWT.NONE);
		filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		filterComposite.setLayout(new GridLayout(3, false));

		Label lblFilter = new Label(filterComposite, SWT.NONE);
		GridData gd_lblFilter = new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1);
		gd_lblFilter.widthHint = 50;
		lblFilter.setLayoutData(gd_lblFilter);
		lblFilter.setText("Filter:");

		filterCombo = new Combo(filterComposite, SWT.READ_ONLY);
		GridData gd_filterCombo = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_filterCombo.widthHint = 120;
		filterCombo.setLayoutData(gd_filterCombo);

		filterText = new Text(filterComposite, SWT.BORDER);
		filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		
		// define modify listener
		ModifyListener modifyListener = new ModifyListener() {
			/**
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent event) {
				String text = filterText.getText();
				tableFilter.setSearchText(text);
				
				String searchFiled = filterCombo.getText();
				if (ANY_FIELD.equals(searchFiled)) {
					tableFilter.setSearchFiled(null);
				} else {
					tableFilter.setSearchFiled(searchFiled);
				}
				
				tableViewer.refresh();
			}
		};
		filterCombo.addModifyListener(modifyListener);
		filterText.addModifyListener(modifyListener);
		
		filterComposite.setEnabled(false);
	}

	/**
	 * Creates the composite which displays the list of SHIM Data.
	 */
	private void createTableComposite() {
		Composite tableComposite = new Composite(composite, SWT.NONE);
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));

		tableViewer = new ShimObjectTableViewer(tableComposite, SWT.BORDER,
				this);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumnLayout tcl_composite = new TableColumnLayout();
		tableComposite.setLayout(tcl_composite);
		
		tableFilter = new ShimObjectTableFilter();
		tableViewer.addFilter(tableFilter);

		// To reset the Apply Button and Text of error message.
		btnApply.setVisible(false);
		textErrorMassage.setVisible(true);
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		if (input != null) {
			apiClass = (Class<?>) input;

			Object rootInstance = ShimModelManager.getCurrentScd();
			objectList = ShimModelAdapter
					.getObjectsList(apiClass, rootInstance);
			setLblTitleText(LABEL_PREFIX + apiClass.getSimpleName());

			tableViewer.initTabeleViewer(apiClass, true);
			tableViewer.setInput(objectList);

			// create combo
			List<String> comboItems = new ArrayList<String>();
			comboItems.add(ANY_FIELD);
			
			TableColumn[] columns = tableViewer.getTable().getColumns();
			for (TableColumn column : columns) {
				comboItems.add(column.getText());
			}
			filterCombo.setItems(comboItems.toArray(new String[comboItems
					.size()]));
			filterCombo.select(0);
			
			filterComposite.setEnabled(true);
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		// NOOP
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return apiClass.getClass();
	}

	/**
	 * @see shim.gui.swt.ObjectListRefresher#refreshObjectList()
	 */
	@Override
	public void refreshObjectList() {
		Object rootInstance = ShimModelManager.getCurrentScd();
		objectList = ShimModelAdapter.getObjectsList(apiClass, rootInstance);
		tableViewer.dispose();
		tableViewer.setInput(objectList);
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		objectList = null;
		tableViewer.dispose();
		tableViewer.getTable().clearAll();
		tableViewer.refresh();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) apiClass;
	}

	/**
	 * Selects the element which is selected in the current InputPanel's tree
	 * viewer.<br>
	 * Synchronizes the selection between InputPanel and this table.
	 * 
	 * @param currentInputPanelObject
	 *            the element which is selected in the current InputPanel's tree
	 *            viewer
	 */
	public void selectCurrentInputPanelObject(Object currentInputPanelObject) {
		if (objectList != null && currentInputPanelObject != null) {
			int size = objectList.size();
			for (int i = 0; i < size; i++) {
				ShimObject object = objectList.get(i);
				if (object.getObj().equals(currentInputPanelObject)) {
					tableViewer.getTable().select(i);
					tableViewer.getTable().showSelection();
					break;
				}
			}
		}
	}

	/**
	 * Returns the item which is selected in the table viewer.
	 * 
	 * @return the item which is selected in the table viewer
	 */
	public Object getSelectedItem() {
		TableItem[] selection = tableViewer.getTable().getSelection();
		if (selection != null && selection.length > 0) {
			ShimObject object = (ShimObject) selection[0].getData();
			return object.getObj();
		}
		return null;
	}
}
