/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.viewer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.multicore_association.shim.edit.gui.common.CellEditorComboValidator;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.gui.swt.ShimObjectColumnFormat;
import org.multicore_association.shim.edit.gui.swt.ShimObjectColumnFormat.ColumnType;
import org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimModelManager;
import org.multicore_association.shim.edit.model.ShimObject;
import org.multicore_association.shim.edit.model.ShimObjectUtils;

import com.sun.xml.internal.ws.util.StringUtils;

/**
 * A TableViewer implementation for the reference field of Shim_Object.
 * 
 * @see org.multicore_association.shim.edit.gui.swt.viewer.ShimRefTableLabelProvider
 */
public class ShimRefTableViewer extends ShimObjectTableViewer {
	private Class<?> apiClass = null;
	private String title = null;

	private List<Object> objectList4Combo = new ArrayList<Object>();

	/**
	 * Constructs a new instance of ShimRefTableViewer.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 * @param inputPanel
	 *            InputPanel which needs to refresh object list
	 * @param title
	 *            title
	 */
	public ShimRefTableViewer(Composite parent, int style,
			InputPanelBase inputPanel, String title) {
		super(parent, style, inputPanel);
		this.apiClass = inputPanel.getApiClass();
		this.title = title;
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.viewer.ShimObjectTableViewer#initDetails()
	 */
	@Override
	protected void initDetails() {
		shimObjectTableViewer.setCellModifier(new ICellModifier() {

			/**
			 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
			 *      java.lang.String)
			 */
			@Override
			public boolean canModify(Object element, String property) {
				ShimObjectColumnFormat format = getColumnFormat(property);
				if (format != null) {
					ColumnType type = format.getType();
					switch (type) {
					case COMBO:
						return true;
					case TEXT:
					default:
						return false;
					}
				}
				return true;
			}

			/**
			 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
			 *      java.lang.String)
			 */
			@Override
			public Object getValue(Object element, String property) {

				ShimObject so = (ShimObject) element;

				ShimObjectColumnFormat format = getColumnFormat(property);
				if (format != null) {
					ColumnType type = format.getType();
					Object val = so.getValue(format.getPropertyName());

					if (val == null) {
						return null;
					}

					switch (type) {
					case TEXT:
						return val.toString();
					case COMBO:
						int idx = objectList4Combo.indexOf(so.getObj());
						return idx;
					default:
						return null;
					}
				}
				return null;
			}

			/**
			 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
			 *      java.lang.String, java.lang.Object)
			 */
			@Override
			public void modify(Object element, String property, Object value) {
				if (element instanceof Item) {
					element = ((Item) element).getData();
				}
				ShimObject so = (ShimObject) element;

				ShimObjectColumnFormat format = getColumnFormat(property);
				if (format != null) {

					ColumnType type = format.getType();
					switch (type) {
					case COMBO:
						Object _objectList = shimObjectTableViewer.getInput();

						@SuppressWarnings("unchecked")
						List<ShimObject> objectList = (List<ShimObject>) _objectList;
						ShimObject newSo = new ShimObject(objectList4Combo
								.get((int) value));
						if (!so.getObj().equals(newSo.getObj())) {
							if (!ShimObjectUtils.contains(objectList, newSo)) {
								int idx = objectList.indexOf(so);
								objectList.remove(idx);
								objectList.add(idx, newSo);
							} else {
								writeErrorMessage("It has already been set.");
							}
							shimObjectTableViewer.refresh();
						}
						break;
					default:
						break;
					}
				}
			}
		});
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.viewer.ShimObjectTableViewer#initTabeleViewer(java.lang.Class,
	 *      boolean)
	 */
	@Override
	public void initTabeleViewer(Class<?> shimClass, boolean hasDefinedRegion) {
		final int COLUMN_WIDTH = 150;

		// Create columns.
		Field[] fields = shimClass.getDeclaredFields();
		List<Field> fieldsAll = new ArrayList<Field>();
		fieldsAll.addAll(Arrays.asList(fields));

		this.columnsList = new ArrayList<ShimObjectColumnFormat>();
		List<String> colTexts = new ArrayList<String>();
		for (Field f : fieldsAll) {

			Class<?> type = f.getType();
			if (type.equals(f.getGenericType())) {
				// It isn't Collection.
				String name = f.getName();
				boolean isRequied = ShimModelAdapter.isRequired(apiClass, name);

				if (name.equals(CommonConstants.FIELD_NAME)) {
					ShimObjectColumnFormat format = new ShimObjectColumnFormat(
							name, ColumnType.COMBO, type, isRequied);
					columnsList.add(format);
					colTexts.add(name);
				} else if (!type.isLocalClass()) {
					ShimObjectColumnFormat format = new ShimObjectColumnFormat(
							name, ColumnType.TEXT, type, isRequied);
					columnsList.add(format);
					colTexts.add(name);
				}

				TableViewerColumn tableViewerColumn = new TableViewerColumn(
						this, SWT.NONE);
				TableColumn tblclmn = tableViewerColumn.getColumn();

				tblclmn.setWidth(COLUMN_WIDTH);
				String txt = StringUtils.capitalize(name);
				tblclmn.setText(txt);

				// To sort table.
				tblclmn.addSelectionListener(new SelectionAdapter() {
					/**
					 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
					 */
					public void widgetSelected(SelectionEvent event) {
						TableColumn colunmn = (TableColumn) event.getSource();
						sorter.setFieldName(colunmn.getText());
						shimObjectTableViewer.refresh();
					}
				});

				TableColumnLayout tcl_composite = new TableColumnLayout();
				tcl_composite.setColumnData(tblclmn, new ColumnPixelData(
						COLUMN_WIDTH, true, true));
				_parent.setLayout(tcl_composite);
			}

		}

		String colTextArray[] = colTexts.toArray(new String[0]);
		setColumnProperties(colTextArray);
		setLabelProvider(new ShimRefTableLabelProvider(apiClass, columnsList));
		setContentProvider(new ShimObjectTableContentProvider());
	}

	/**
	 * Sets the CellEditors according to each object.
	 * 
	 * @param currentObject
	 */
	public void setCellEditors(Object... currentObject) {
		Object rootInstance = ShimModelManager.getCurrentScd();

		if (rootInstance == null) {
			return;
		}

		List<ShimObject> _objectList4Combo = ShimModelAdapter.getObjectsList(
				apiClass, rootInstance);

		Object current = null;
		if (currentObject.length > 0) {
			current = currentObject[0];
		}

		List<String> objectNameList = new ArrayList<String>();
		objectList4Combo.clear();
		for (ShimObject so : _objectList4Combo) {
			Object obj = so.getObj();
			String name = (String) ShimModelAdapter.getValObject(obj,
					CommonConstants.FIELD_NAME);
			if (!current.equals(obj)) {
				objectNameList.add(name);
				objectList4Combo.add(obj);
			}
		}

		String[] strvaluesArray = objectNameList.toArray(new String[0]);

		int cnt = columnsList.size();
		CellEditor[] editors = new CellEditor[cnt];

		int i = 0;
		Table table = this.getTable();
		for (ShimObjectColumnFormat format : columnsList) {

			ColumnType type = format.getType();
			String propertyName = format.getPropertyName() + " of " + title;

			switch (type) {
			case COMBO:
				editors[i] = new ComboBoxCellEditor();
				editors[i].create(table);
				((ComboBoxCellEditor) editors[i]).setItems(strvaluesArray);
				editors[i].setValidator(new CellEditorComboValidator(
						propertyName));
				editors[i]
						.addListener(getComboBoxCellEditorListener(editors[i]));
				i++;
				break;
			case TEXT:
			default:
				editors[i] = new TextCellEditor();
				editors[i].create(table);
				i++;
				break;
			}
		}
		shimObjectTableViewer.setCellEditors(editors);
	}

}
