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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.multicore_association.shim.edit.gui.common.CellEditorComboValidator;
import org.multicore_association.shim.edit.gui.common.CellEditorFloatValidator;
import org.multicore_association.shim.edit.gui.common.CellEditorHexValidator;
import org.multicore_association.shim.edit.gui.common.CellEditorIntValidator;
import org.multicore_association.shim.edit.gui.common.CellEditorValidator;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.gui.common.ErrorMessageWriter;
import org.multicore_association.shim.edit.gui.common.LabelConstants;
import org.multicore_association.shim.edit.gui.common.NameAttributeChecker;
import org.multicore_association.shim.edit.gui.jface.ShimSelectableItem;
import org.multicore_association.shim.edit.gui.jface.wizard.ShimWizardPageBase;
import org.multicore_association.shim.edit.gui.swt.ObjectListRefresher;
import org.multicore_association.shim.edit.gui.swt.ShimObjectColumnFormat;
import org.multicore_association.shim.edit.gui.swt.ShimObjectColumnFormat.ColumnType;
import org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimObject;

import com.sun.xml.internal.ws.util.StringUtils;

/**
 * A TableViewer implementation for ShimObject.
 * 
 * @see org.multicore_association.shim.edit.gui.swt.ShimObjectColumnFormat
 * @see org.multicore_association.shim.edit.gui.swt.viewer.ShimObjectTableContentProvider
 * @see org.multicore_association.shim.edit.gui.swt.viewer.ShimObjectTableLabelProvider
 * @see org.multicore_association.shim.edit.gui.swt.viewer.ShimObjectTableViewerSorter
 */
public class ShimObjectTableViewer extends TableViewer {

	private static Map<String, String> convPropMap = new HashMap<String, String>();
	static {
		convPropMap.put(LabelConstants.COLUMN_START,
				LabelConstants.COLUMN_START_HEX);
		convPropMap.put(LabelConstants.COLUMN_END,
				LabelConstants.COLUMN_END_HEX);
	}

	protected Composite _parent;
	protected ShimObjectTableViewer shimObjectTableViewer;
	private ErrorMessageWriter errorMassageWriter = null;
	protected ObjectListRefresher refresher = null;
	protected List<ShimObjectColumnFormat> columnsList = null;
	protected ShimObjectTableViewerSorter sorter;
	private ShimSelectableItem selectableItem = null;
	private NameAttributeChecker nameAttributeChecker = null;

	private static final String CLASS_NAME_STRING = String.class.getName();

	/**
	 * Constructs a new instance of ShimObjectTableViewer.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 * @param inputPanel
	 *            InputPanel which needs to refresh object list
	 */
	public ShimObjectTableViewer(Composite parent, int style,
			InputPanelBase inputPanel) {
		super(parent, SWT.FULL_SELECTION | SWT.BORDER);

		this._parent = parent;
		this.errorMassageWriter = (ErrorMessageWriter) inputPanel;
		if (inputPanel instanceof ObjectListRefresher) {
			this.refresher = (ObjectListRefresher) inputPanel;
		}

		init();
	}

	/**
	 * Constructs a new instance of ShimObjectTableViewer.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 * @param wizardPage
	 *            Shim_WizardPage
	 */
	public ShimObjectTableViewer(Composite parent, int style,
			ShimWizardPageBase wizardPage) {
		super(parent, SWT.FULL_SELECTION | SWT.BORDER);

		this.errorMassageWriter = (ErrorMessageWriter) wizardPage;
		if (wizardPage instanceof ObjectListRefresher) {
			this.refresher = (ObjectListRefresher) wizardPage;
		}

		init();
	}

	/**
	 * Initialize the control on this page.
	 */
	private void init() {
		shimObjectTableViewer = this;

		// To sort table.
		sorter = new ShimObjectTableViewerSorter();
		shimObjectTableViewer.setSorter(sorter);

		initDetails();
	}

	/**
	 * Initialize the listener of the controls.
	 */
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
					case HEADER:
					case OBJECT:
						return false;
					case TEXT:
					case INT:
					case HEX:
					case FLOAT:
					case COMBO:
					case FOOTER: // But does not accept the value entered.
					default:
						return true;
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
					case INT:
					case HEADER:
					case FOOTER:
						return val.toString();
					case FLOAT:
						return String.format(CommonConstants.FORMAT_FLOAT, val);
					case HEX:
						return String.format(CommonConstants.FORMAT_HEX, val);
					case COMBO:
						return ((Enum<?>) val).ordinal();

					case OBJECT:
						return ShimModelAdapter.getValObject(val,
								CommonConstants.FIELD_NAME);

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
				if (!checkPropertyName(so, property, value)) {
					return;
				}
				
				// Verify the uniqueness of the name attribute.
				if (ShimModelAdapter.hasNameAttribute(so.getObj())) {
					@SuppressWarnings("unchecked")
					List<ShimObject> objectList = (List<ShimObject>) shimObjectTableViewer
							.getInput();
					nameAttributeChecker = new NameAttributeChecker(objectList);
				}

				String xPath = (String) so.getValue(ShimObject.CL_X_PATH);
				
				// modify
				String valStr = null;
				ShimObjectColumnFormat format = getColumnFormat(property);
				if (format.getType() == ColumnType.COMBO) {
					Object[] _enum = format.getClazz().getEnumConstants();
					Integer intValue = (Integer) value;
					if (intValue < _enum.length) {
						valStr = _enum[intValue].toString();
					} else{
						valStr = "";
					}
				} else {
					valStr = (String) value;
				}

				if (CommonConstants.FIELD_NAME.equals(property)) {
					String oldName = (String) so
							.getValue(CommonConstants.FIELD_NAME);
					String result = nameAttributeChecker.updateNameList(xPath,
							oldName, valStr);
					if (result != null) {
						writeErrorMessage(result);
						return;
					}
				}

				if (format != null) {
					String propertyName = format.getPropertyName();
					if (value == null || valStr.isEmpty()) {
						so.setValue(propertyName, null);
						shimObjectTableViewer.refresh();
						return;
					}

					ColumnType type = format.getType();
					switch (type) {
					case TEXT:
						so.setValue(propertyName, (String) value);
						break;
					case INT:
						Integer vi = Integer.parseInt((String) value);
						so.setValue(propertyName, vi);
						break;
					case HEX:
						long vl = Long.parseLong((String) value, 16);
						so.setValue(propertyName, vl);
						break;
					case FLOAT:
						float vf = Float.parseFloat((String) value);
						so.setValue(propertyName, vf);
						break;
					case COMBO:
						Object[] _enum = format.getClazz().getEnumConstants();
						Integer intValue = (Integer) value;
						if (intValue < _enum.length) {
							so.setValue(propertyName, _enum[intValue]);
						} else {
							so.setValue(propertyName, null);
						}
						
						break;
					case HEADER: // Can't Modify.
					case OBJECT:
						break;
					case FOOTER: // Does not accept the value entered.
						so.setValue(propertyName, so.getValue(propertyName));
						break;
					default:
						break;
					}
				}
				shimObjectTableViewer.refresh();

				if (selectableItem != null) {
					selectableItem.refresh();
				}

				// When the name has been changed, XPath needs to be changed.
				boolean needsRefresh = ShimModelAdapter.isSelfReferencing(so
						.getObj());
				if (needsRefresh) {
					refresher.refreshObjectList();
				}
			}
		});
	}

	/**
	 * Initialize the TabeleViewer.
	 * 
	 * @param apiClass
	 *            the class of Shim_Object's data
	 * @param hasDefinedRegion
	 *            whether displays XPath or not
	 */
	public void initTabeleViewer(Class<?> apiClass, boolean hasDefinedRegion) {
		final int COLUMN_WIDTH = 150;
		final int COLUMN_WIDTH_FOR_XPATH = 300;

		deleteOldColums();

		this.columnsList = createColumsList(apiClass, hasDefinedRegion);

		List<String> colTexts = new ArrayList<String>();
		int columnWidth = COLUMN_WIDTH;
		for (ShimObjectColumnFormat format : columnsList) {
			TableViewerColumn tableViewerColumn = new TableViewerColumn(this,
					SWT.NONE);
			TableColumn tblclmn = tableViewerColumn.getColumn();

			String propertyName = format.getPropertyName();
			if (propertyName.equals(ShimObject.CL_X_PATH)) {
				columnWidth = COLUMN_WIDTH_FOR_XPATH;
			}
			colTexts.add(propertyName);
			tblclmn.setWidth(columnWidth);
			String txt = StringUtils.capitalize(propertyName);
			if (convPropMap.containsKey(txt)) {
				tblclmn.setText(convPropMap.get(txt));
			} else {
				tblclmn.setText(txt);
			}

			// To sort table.
			tblclmn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					TableColumn colunmn = (TableColumn) event.getSource();
					sorter.setFieldName(colunmn.getText());
					shimObjectTableViewer.refresh();
				}
			});

			TableColumnLayout tcl_composite = new TableColumnLayout();
			tcl_composite.setColumnData(tblclmn, new ColumnPixelData(
					columnWidth, true, true));
			_parent.setLayout(tcl_composite);
		}

		String colTextArray[] = colTexts.toArray(new String[0]);
		setColumnProperties(colTextArray);
		setLabelProvider(new ShimObjectTableLabelProvider(apiClass, columnsList));
		setCellEditors();
		setContentProvider(new ShimObjectTableContentProvider());
	}

	/**
	 * Disposes all set columns.
	 */
	private void deleteOldColums() {
		int oldColumnCount = getTable().getColumnCount();
		for (int i = 0; i < oldColumnCount; i++) {
			TableColumn col = getTable().getColumn(0);
			col.dispose();
		}
	}

	/**
	 * Creates the list of all columns' format.
	 * 
	 * @param apiClass
	 *            the class of Shim_Object's data
	 * @param hasDefinedRegion
	 *            whether displays XPath or not
	 * @return the list of all columns' format
	 */
	private List<ShimObjectColumnFormat> createColumsList(Class<?> apiClass,
			boolean hasDefinedRegion) {

		List<ShimObjectColumnFormat> columnsList = new ArrayList<ShimObjectColumnFormat>();

		// Create default header.
		if (hasDefinedRegion) {
			ShimObjectColumnFormat hFormat = new ShimObjectColumnFormat(
					ShimObject.CL_PARENT_NAME, ColumnType.HEADER);
			columnsList.add(0, hFormat);
		}

		List<Field> fieldsAll = new ArrayList<Field>();
		Package cPackage = apiClass.getPackage();

		// Display the direct superclass of the apiClass.
		Class<?> genericSuperClass = (Class<?>) apiClass.getGenericSuperclass();
		if (genericSuperClass.getPackage().equals(cPackage)) {
			Field[] fieldsSuper = genericSuperClass.getDeclaredFields();
			if (fieldsSuper != null) {
				fieldsAll.addAll(Arrays.asList(fieldsSuper));
			}
		}

		// Create columns.
		Field[] fields = apiClass.getDeclaredFields();
		fieldsAll.addAll(Arrays.asList(fields));

		for (Field f : fieldsAll) {
			Class<?> type = f.getType();

			if (type.equals(f.getGenericType())) {
				// It isn't Collection.
				String name = f.getName();
				if (CommonConstants.notDisplayTableProps.contains(name)) {
					continue;
				}

				boolean isRequied = ShimModelAdapter.isRequired(apiClass, name);

				Package fPackage = type.getPackage();
				if (fPackage != null && fPackage.equals(cPackage)) {
					if (type.isEnum()) {
						// It is 'Type' Class of SHIM API.
						ShimObjectColumnFormat format = new ShimObjectColumnFormat(
								name, ColumnType.COMBO, type, isRequied);
						columnsList.add(format);
					}
				} else {
					ShimObjectColumnFormat format;
					String insName = type.getName();
					if (insName.equals(CLASS_NAME_STRING)) {
						format = new ShimObjectColumnFormat(name,
								ColumnType.TEXT, type, isRequied);
					} else if (insName
							.equals(ShimModelAdapter.CLASS_NAME_INTEGER_WRAPPER)
							|| insName
									.equals(ShimModelAdapter.CLASS_NAME_INTEGER_PRIMITIVE)) {
						format = new ShimObjectColumnFormat(name,
								ColumnType.INT, type, isRequied);
					} else if (insName
							.equals(ShimModelAdapter.CLASS_NAME_LONG_WRAPPER)
							|| insName
									.equals(ShimModelAdapter.CLASS_NAME_LONG_PRIMITIVE)) {
						format = new ShimObjectColumnFormat(name,
								ColumnType.HEX, type, isRequied);
					} else if (insName
							.equals(ShimModelAdapter.CLASS_NAME_FLOAT_WRAPPER)
							|| insName
									.equals(ShimModelAdapter.CLASS_NAME_FLOAT_PRIMITIVE)) {
						format = new ShimObjectColumnFormat(name,
								ColumnType.FLOAT, type, isRequied);

					} else if (insName
							.equals(ShimModelAdapter.CLASS_NAME_OBJECT)) {
						format = new ShimObjectColumnFormat(name,
								ColumnType.OBJECT, type, isRequied);

					} else {
						format = new ShimObjectColumnFormat(name,
								ColumnType.TEXT, type, isRequied);
					}
					columnsList.add(format);
				}
			}
		}

		// Create default footer.
		if (hasDefinedRegion) {
			ShimObjectColumnFormat fFormat = new ShimObjectColumnFormat(
					ShimObject.CL_X_PATH, ColumnType.FOOTER);
			int idx = columnsList.size();
			columnsList.add(idx, fFormat);
		}
		return columnsList;
	}

	/**
	 * In the case of the property name which is start or end, it should check
	 * the range of values.
	 * 
	 * @param so
	 *            Shim_Object
	 * @param property
	 *            the property name
	 * @param value
	 *            the value of the specified property
	 * @return true if 'start' is smaller than 'end'; false otherwise.
	 */
	private boolean checkPropertyName(ShimObject so, String property,
			Object value) {
		final String PROPERTY_START = "start";
		final String PROPERTY_END = "end";

		boolean result = false;
		if (property.equals(PROPERTY_START)) {
			long val = Long.parseLong((String) value, 16);
			long endVal = (long) so.getValue(PROPERTY_END);

			if (val <= endVal) {
				result = true;
			} else {
				writeErrorMessage("Start value must be smaller than end value.");
			}
		} else if (property.equals(PROPERTY_END)) {
			long val = Long.parseLong((String) value, 16);
			long startVal = (long) so.getValue(PROPERTY_START);
			if (val >= startVal) {
				result = true;
			} else {
				writeErrorMessage("End value must be larger than start value.");
			}
		} else {
			result = true;
		}
		return result;
	}

	/**
	 * Returns the format of the specified property.
	 * 
	 * @param property
	 *            the property name to return the format
	 * @return the format of the specified property
	 */
	protected ShimObjectColumnFormat getColumnFormat(String property) {
		for (ShimObjectColumnFormat format : columnsList) {
			if (format.getPropertyName().equals(property)) {
				return format;
			}
		}
		return null;
	}

	/**
	 * Sets CellEditors according to each type.
	 */
	private void setCellEditors() {
		int cnt = columnsList.size();
		CellEditor[] editors = new CellEditor[cnt];

		int i = 0;
		Table table = this.getTable();
		for (ShimObjectColumnFormat format : columnsList) {

			ColumnType type = format.getType();
			String propertyName = format.getPropertyName();
			boolean isRequired = format.isRequired();

			switch (type) {
			case COMBO:
				editors[i] = new ShimComboBoxCellEditor();
				editors[i].create(table);

				Enum<?>[] consts = (Enum[]) format.getClazz()
						.getEnumConstants();
				String[] strvaluesArray = ComboFactory.getStrings(consts, isRequired);
				((ComboBoxCellEditor) editors[i]).setItems(strvaluesArray);

				editors[i].setValidator(new CellEditorComboValidator(
						propertyName));
				editors[i]
						.addListener(getComboBoxCellEditorListener(editors[i]));
				i++;
				break;
			case INT:
				editors[i] = new ShimObjectCellEditor(true);
				editors[i].create(table);
				editors[i].setValidator(new CellEditorIntValidator(
						propertyName, isRequired));
				editors[i].addListener(getCellEditorListener(editors[i]));
				i++;
				break;
			case HEX:
				editors[i] = new ShimObjectCellEditor(true);
				editors[i].create(table);
				editors[i].setValidator(new CellEditorHexValidator(
						propertyName, isRequired));
				editors[i].addListener(getCellEditorListener(editors[i]));
				i++;
				break;
			case FLOAT:
				editors[i] = new ShimObjectCellEditor(true);
				editors[i].create(table);
				editors[i].setValidator(new CellEditorFloatValidator(
						propertyName, isRequired));
				editors[i].addListener(getCellEditorListener(editors[i]));
				i++;
				break;
			case TEXT:
				editors[i] = new ShimObjectCellEditor(false);
				editors[i].create(table);
				if (isRequired) {
					editors[i].setValidator(new CellEditorValidator(
							propertyName, isRequired));
					editors[i].addListener(getCellEditorListener(editors[i]));
				}
				i++;
				break;
			case FOOTER:
			default:
				editors[i] = new ShimObjectCellEditor(false);
				editors[i].create(table);
				i++;
				break;
			}
		}
		shimObjectTableViewer.setCellEditors(editors);
	}

	/**
	 * Returns the CellEditorListener depending on the specified editor.
	 * 
	 * @param editor
	 *            the specified editor to return the CellEditorListener
	 * @return the CellEditorListener depending on the specified editor
	 */
	private ICellEditorListener getCellEditorListener(final CellEditor editor) {
		ICellEditorListener listener = new ICellEditorListener() {

			/**
			 * @see org.eclipse.jface.viewers.ICellEditorListener#applyEditorValue()
			 */
			@Override
			public void applyEditorValue() {
				writeErrorMessage(null);
			}

			/**
			 * @see org.eclipse.jface.viewers.ICellEditorListener#cancelEditor()
			 */
			@Override
			public void cancelEditor() {
				writeErrorMessage(null);
			}

			/**
			 * @see org.eclipse.jface.viewers.ICellEditorListener#editorValueChanged(boolean,
			 *      boolean)
			 */
			@Override
			public void editorValueChanged(boolean oldValidState,
					boolean newValidState) {
				writeErrorMessage(editor.getErrorMessage());
			}
		};
		return listener;
	}

	/**
	 * Returns the CellEditorListener of ComboBox column depending on the
	 * specified editor.
	 * 
	 * @param editor
	 *            the specified editor to return the CellEditorListener
	 * @return the CellEditorListener of ComboBox column depending on the
	 *         specified editor
	 */
	protected ICellEditorListener getComboBoxCellEditorListener(
			final CellEditor editor) {
		ICellEditorListener listener = new ICellEditorListener() {

			/**
			 * @see org.eclipse.jface.viewers.ICellEditorListener#applyEditorValue()
			 */
			@Override
			public void applyEditorValue() {
				writeErrorMessage(editor.getErrorMessage());
			}

			/**
			 * @see org.eclipse.jface.viewers.ICellEditorListener#cancelEditor()
			 */
			@Override
			public void cancelEditor() {
				writeErrorMessage(editor.getErrorMessage());
			}

			/**
			 * @see org.eclipse.jface.viewers.ICellEditorListener#editorValueChanged(boolean,
			 *      boolean)
			 */
			@Override
			public void editorValueChanged(boolean oldValidState,
					boolean newValidState) {
				writeErrorMessage(editor.getErrorMessage());
			}
		};
		return listener;
	}

	/**
	 * Writes the error message by the ErrorMessageWriter.
	 * 
	 * @param msg
	 *            the error message to write
	 */
	protected void writeErrorMessage(String msg) {
		if (errorMassageWriter != null) {
			errorMassageWriter.writeErrorMessage(msg);
		}
	}

	/**
	 * Sets the Shim_SelectableItem instance.
	 * 
	 * @param selectableItem
	 *            the Shim_SelectableItem instance
	 */
	public void setSelectableItem(ShimSelectableItem selectableItem) {
		this.selectableItem = selectableItem;
	}

	// Verify the uniqueness of the name attribute.
	/**
	 * Disposes of this table viewer.
	 */
	public void dispose() {
		if (nameAttributeChecker != null) {
			this.nameAttributeChecker = null;
		}
	}

	/**
	 * A TextCellEditor implementation for ShimObject.
	 */
	class ShimObjectCellEditor extends TextCellEditor {

		boolean useMonospaceFont;

		/**
		 * Constructs a new instance of ShimObjectCellEditor.
		 * 
		 * @param useMonospaceFont
		 *            Sets true if the column's format is numeric, and false
		 *            otherwise.
		 */
		ShimObjectCellEditor(boolean useMonospaceFont) {
			this.useMonospaceFont = useMonospaceFont;
		}

		/**
		 * @see org.eclipse.jface.viewers.CellEditor#getControl()
		 */
		@Override
		public Control getControl() {
			Control control = super.getControl();
			if (useMonospaceFont) {
				control.setFont(JFaceResources
						.getFont(JFaceResources.TEXT_FONT));
			} else {
				control.setFont(JFaceResources
						.getFont(JFaceResources.DEFAULT_FONT));
			}
			return control;
		}
		
		@Override
		protected void doSetValue(Object value) {
			if (value == null) {
				value = "";
			}
			super.doSetValue(value);
		}
	}
	
	/**
	 * A ComboBoxCellEditor implementation for ShimObject.
	 */
	class ShimComboBoxCellEditor extends ComboBoxCellEditor {
		
		/**
		 * @see org.eclipse.jface.viewers.ComboBoxCellEditor#doSetValue(java.lang.Object)
		 */
		@Override
		protected void doSetValue(Object value) {
			if (value == null) {
				int index = -1;
				String[] items = getItems();
				for (int i = 0; i < items.length; i++) {
					if (ComboFactory.NONE.equals(items[i])) {
						index = i;
						break;
					}
				}
				
				value = index;
			}
			
			super.doSetValue(value);
		}
	}
}
