/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimModelManager;
import org.multicore_association.shim.edit.model.ShimObject;

/**
 * The main contract here is the creation of Combo instances.<br>
 * This class is used instead of ComboFactory when to create Combo from IDREF.
 */
public class RefComboFactory {

	private Composite compose;
	private Combo combo;
	private Class<?> targetCls;
	private Class<?> cls;
	private boolean isRequired;
	private List<ShimObject> objectList = new ArrayList<ShimObject>();

	/**
	 * Constructs a new instance of ComboFactory.
	 * 
	 * @param compose
	 *            the Composite which a created combo is included in
	 * @param targetCls
	 *            the class which has IDREF field.
	 * @param valueName
	 *            the property name of IDREF field
	 * @param cls
	 *            the class which is referred to
	 */
	public RefComboFactory(Composite compose, Class<?> targetCls,
			String valueName, Class<?> cls) {
		this.compose = compose;
		this.targetCls = targetCls;
		this.cls = cls;

		isRequired = ShimModelAdapter.isRequired(this.targetCls, valueName);
		combo = new Combo(this.compose, SWT.READ_ONLY);
	}

	/**
	 * Creates a ComboBox widget with only empty value.
	 * 
	 * @return a ComboBox widget with only empty value
	 */
	public Combo createEmptyCombo() {
		this.combo.setText(ComboFactory.NONE);
		this.combo.setItems(new String[] { ComboFactory.NONE });
		this.combo.select(0);
		return combo;
	}

	/**
	 * Creates a ComboBox widget according to constructor's arguments.
	 * 
	 * @return a ComboBox widget
	 */
	public Combo createCombo() {
		SystemConfiguration sys = ShimModelManager.getCurrentScd();
		this.objectList = ShimModelAdapter.getObjectsList(cls, sys);
		_createCombo();
		return combo;
	}

	/**
	 * Creates a ComboBox widget from the specified instance.
	 * 
	 * @param rootInstance
	 *            the instance witch a Combo is created from
	 * @return a ComboBox widget from the specified instance
	 */
	public Combo createCombo(Object rootInstance) {
		if (rootInstance != null) {
			this.objectList = ShimModelAdapter
					.getObjectsList(cls, rootInstance);
			_createCombo();
		}
		return combo;
	}

	/**
	 * Creates a ComboBox widget.
	 */
	private void _createCombo() {
		ArrayList<String> comboDisplayList = new ArrayList<String>();
		for (int i = 0; i < objectList.size(); i++) {
			String str = getDisplayName(i);
			comboDisplayList.add(str);
		}
		if (!isRequired) {
			comboDisplayList.add(ComboFactory.NONE);
		}
		combo.setItems(comboDisplayList.toArray(new String[comboDisplayList
				.size()]));
	}

	/**
	 * Sets the contents of the receiver's text field to the given object.
	 * 
	 * @param obj
	 */
	public void setText(Object obj) {
		boolean isFound = false;
		if (obj != null) {
			String id = (String) ShimModelAdapter.getValObject(obj,
					CommonConstants.FIELD_ID);

			int i = 0;
			for (ShimObject so : objectList) {
				Object _obj = so.getObj();

				String _id = (String) ShimModelAdapter.getValObject(_obj,
						CommonConstants.FIELD_ID);
				if (id.equals(_id)) {
					String str = getDisplayName(i);
					combo.setText(str);
					combo.select(i);
					isFound = true;
					break;
				}
				i++;
			}
		} 
		
		if (!isRequired && !isFound) {
			combo.setText(ComboFactory.NONE);
			combo.select(objectList.size());
		}
		combo.redraw();
	}

	/**
	 * Returns the displayed name of item at the specified position in ComboBox.
	 * 
	 * @param index
	 *            index of the item in ComboBox
	 * @return the displayed name of item at the specified position
	 */
	private String getDisplayName(int index) {
		ShimObject so = objectList.get(index);
		Object obj = so.getObj();
		String _name = (String) ShimModelAdapter.getValObject(obj,
				CommonConstants.FIELD_NAME);
		String parentName = (String) so.getValue(ShimObject.CL_PARENT_NAME);

		String displayName = null;
		if (parentName.equals(ShimModelAdapter.DOES_NOT_HAVE_A_NAME_FIELD)) {
			displayName = _name;
		} else {
			displayName = _name + " (" + so.getValue(ShimObject.CL_PARENT_NAME)
					+ ")";
		}
		return displayName;
	}

	/**
	 * Returns the referred object to be selected.
	 * 
	 * @return the referred object to be selected
	 */
	public Object getRefObject() {
		int i = combo.getSelectionIndex();

		if (i < 0 || i >= objectList.size()) {
			if (isRequired) {
				i = 0;
			} else {
				return null;
			}
		}
		return getRefObject(i);
	}

	/**
	 * Returns the first object in referred object list.
	 * 
	 * @return the first object in referred object list
	 */
	public Object getFirstRefObject() {
		return getRefObject(0);
	}

	/**
	 * Returns the object at the specified position in referred object list.
	 * 
	 * @param index
	 *            index of referred object list
	 * @return the object at the specified position
	 */
	private Object getRefObject(int index) {
		ShimObject so = objectList.get(index);
		Object object = so.getObj();
		return object;
	}

	/**
	 * Disposes of the operating system resources associated with the receiver
	 * and all its descendants.
	 */
	public void dispose() {
		objectList.clear();
	}

	/**
	 * Sets the listener who will be notified when a selection event.
	 * 
	 * @param linkedRefComboFactory
	 *            this instance
	 */
	public void setSelectionListener(final RefComboFactory linkedRefComboFactory) {
		SelectionListener SListener = new SelectionListener() {

			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				// NOOP
			}

			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				Combo cb = (Combo) event.getSource();

				int idx = cb.getSelectionIndex();
				if (idx >= 0 && idx < objectList.size()) {
					ShimObject so = objectList.get(idx);

					Object obj = so.getObj();
					if (obj != null) {
						linkedRefComboFactory.createCombo(obj);
						if (linkedRefComboFactory.combo.getItemCount() > 0) {
							linkedRefComboFactory.select(0);
						}
					}
				}
			}
		};
		combo.addSelectionListener(SListener);

		ModifyListener vListener = new ModifyListener() {

			/**
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent event) {
				String str = ((Combo) event.widget).getText();
				if (!isRequired && str.equals(ComboFactory.NONE)) {
					linkedRefComboFactory.createEmptyCombo();

				} else if (linkedRefComboFactory.combo.getText().equals(
						ComboFactory.NONE)
						&& linkedRefComboFactory.combo.getItemCount() > 0) {
					linkedRefComboFactory.select(0);
				}
			}
		};
		combo.addModifyListener(vListener);
	}

	/**
	 * Selects the item at the specified position from the Combo.
	 * 
	 * @param index
	 *            the index of the Combo
	 */
	public void select(int index) {
		combo.select(index);
	}

	public int getSelectionIndex() {
		return combo.getSelectionIndex();
	}
}
