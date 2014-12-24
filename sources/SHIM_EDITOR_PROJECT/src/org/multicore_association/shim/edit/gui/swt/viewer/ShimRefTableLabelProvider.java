/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.viewer;

import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.multicore_association.shim.edit.gui.swt.ShimObjectColumnFormat;
import org.multicore_association.shim.edit.gui.swt.ShimObjectColumnFormat.ColumnType;
import org.multicore_association.shim.edit.model.ShimObject;

/**
 * A LabelProvider implementation for the reference field of Shim_Object.
 * 
 * @see org.multicore_association.shim.edit.gui.swt.viewer.ShimRefTableViewer
 */
public class ShimRefTableLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	private Class<?> apiClass;
	private List<ShimObjectColumnFormat> formatList;

	/**
	 * Constructs a new instance of ShimRefTableLabelProvider.
	 * 
	 * @param apiClass
	 *            the class of Shim_Object's data
	 * @param formatList
	 *            the list of all column's ShimObjectColumnFormat
	 */
	public ShimRefTableLabelProvider(Class<?> apiClass,
			List<ShimObjectColumnFormat> formatList) {
		super();
		this.apiClass = apiClass;
		this.formatList = formatList;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		ShimObject so = (ShimObject) element;
		Object obj = so.getObj();

		String result = "";
		if (apiClass.equals(obj.getClass())) {
			ShimObjectColumnFormat format = formatList.get(columnIndex);
			ColumnType type = format.getType();
			Object val = so.getValue(format.getPropertyName());

			if (val != null) {
				switch (type) {
				case COMBO:
				case TEXT:
					result = val.toString();
					break;
					
				default:
					// should not reach here.
					result = "--";
					break;
				}
			}
		}
		return result;
	}

}
