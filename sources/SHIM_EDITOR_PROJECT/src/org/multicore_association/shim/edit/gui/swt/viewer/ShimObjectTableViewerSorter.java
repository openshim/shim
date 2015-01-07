/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.viewer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.gui.common.LabelConstants;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimObject;

/**
 * A ViewerSorter implementation for ShimObjectTableViewer.
 * 
 * @see org.multicore_association.shim.edit.gui.swt.viewer.ShimObjectTableViewer
 */
public class ShimObjectTableViewerSorter extends ViewerSorter {

	private Package SHIM_API_PACKAGE = SystemConfiguration.class.getPackage();

	private static final int SORT_ASCENDING_ORDER = 1;
	private static final int SORT_DESCENDING_ORDER = -1;

	private String fieldName = null;
	private HashMap<String, Integer> sortOrderSet = new HashMap<String, Integer>();

	private static Map<String, String> reversePropMap = new HashMap<String, String>();
	static {
		reversePropMap.put(LabelConstants.COLUMN_START_HEX,
				LabelConstants.COLUMN_START);
		reversePropMap.put(LabelConstants.COLUMN_END_HEX,
				LabelConstants.COLUMN_END);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int compare(Viewer viewer, Object o1, Object o2) {
		int result = 0;
		if (fieldName != null) {
			ShimObject so1 = (ShimObject) o1;
			ShimObject so2 = (ShimObject) o2;

			String _fieldName = fieldName;
			if (reversePropMap.containsKey(_fieldName)) {
				_fieldName = reversePropMap.get(_fieldName);
			}

			Object v1 = so1.getValue(_fieldName);
			Object v2 = so2.getValue(_fieldName);
			int sortOrder = sortOrderSet.get(_fieldName);
			
			// If value is null, may occur NullPointerException.
			// So, evaluates null value here.
			if (v1 == null && v2 == null) {
				return 0;
			} else if (v1 == null) {
				return -1 * sortOrder;
			} else if (v2 == null) {
				return 1 * sortOrder;
			}
			
			Comparable c1 = getComparableValue(v1);
			Comparable c2 = getComparableValue(v2);

			result = c1.compareTo(c2) * sortOrder;
		}
		return result;
	}

	/**
	 * Returns the value string.
	 * 
	 * @param value
	 *            the object value
	 * @return the value string (If value is reference object, returns the name
	 *         field.)
	 */
	private Comparable<?> getComparableValue(Object value) {
		if (value instanceof Comparable) {
			return (Comparable<?>) value;
		}
		
		String str = null;
		if (!value.getClass().isEnum() && SHIM_API_PACKAGE.equals(value.getClass().getPackage())) {
			// in case of the reference, returns the name attribute
			str = (String) ShimModelAdapter.getValObject(value,
					CommonConstants.FIELD_NAME);
		}
		if (str == null) {
			str = value.toString();
		}
		return str;
	}

	/**
	 * Sets the field name of the parent element.
	 * 
	 * @param fieldName
	 *            the field name of the parent element
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;

		String _fieldName = fieldName;
		if (reversePropMap.containsKey(_fieldName)) {
			_fieldName = reversePropMap.get(_fieldName);
		}

		Object _sortOrder = sortOrderSet.get(_fieldName);

		int old_sortOrder = SORT_ASCENDING_ORDER;
		if (_sortOrder != null) {
			old_sortOrder = sortOrderSet.get(_fieldName);
			sortOrderSet.remove(_fieldName);
		}
		int new_sortOrder = changeSortOrder(old_sortOrder);
		sortOrderSet.put(_fieldName, new_sortOrder);
	}

	/**
	 * Changes the sort type.
	 * 
	 * @param sortOrder
	 *            SORT_ASCENDING_ORDER or SORT_DESCENDING_ORDER
	 * @return Returns SORT_ASCENDING_ORDER if the current sort type is
	 *         SORT_DESCENDING_ORDER, and SORT_DESCENDING_ORDER otherwise.
	 */
	private int changeSortOrder(int sortOrder) {
		if (sortOrder == SORT_ASCENDING_ORDER) {
			sortOrder = SORT_DESCENDING_ORDER;
		} else {
			sortOrder = SORT_ASCENDING_ORDER;
		}
		return sortOrder;
	}
}
