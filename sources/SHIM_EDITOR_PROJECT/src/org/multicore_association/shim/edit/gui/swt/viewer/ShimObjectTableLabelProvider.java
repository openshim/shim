/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.viewer;

import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.gui.swt.ShimObjectColumnFormat;
import org.multicore_association.shim.edit.gui.swt.ShimObjectColumnFormat.ColumnType;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimObject;

/**
 * The label provider for Shim_Object.
 */
public class ShimObjectTableLabelProvider extends LabelProvider implements
		ITableLabelProvider, ITableFontProvider {

	private Class<?> apiClass;
	private List<ShimObjectColumnFormat> formatList;

	/**
	 * Constructs a new instance of ShimObjectTableLabelProvider.
	 * 
	 * @param apiClass
	 *            the class of Shim_Object's data
	 * @param formatList
	 *            the list of all column's ShimObjectColumnFormat
	 */
	public ShimObjectTableLabelProvider(Class<?> apiClass,
			List<ShimObjectColumnFormat> formatList) {
		super();
		this.apiClass = apiClass;
		this.formatList = formatList;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
	 *      int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
	 *      int)
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
					result = ComboFactory.getEnumValue((Enum<?>) val);
					break;
				case TEXT:
				case INT:
				case HEADER:
				case FOOTER:
					result = val.toString();
					break;
				case FLOAT:
					result = String.format(CommonConstants.FORMAT_FLOAT, val);
					break;
				case HEX:
					result = String.format(CommonConstants.FORMAT_HEX, val);
					break;

				case OBJECT:
					result = (String) ShimModelAdapter.getValObject(val,
							CommonConstants.FIELD_NAME);
					break;

				default:
					result = "--";
					break;
				}
			}
		}
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableFontProvider#getFont(java.lang.Object,
	 *      int)
	 */
	@Override
	public Font getFont(Object element, int columnIndex) {
		ShimObject so = (ShimObject) element;
		Object obj = so.getObj();

		Font result = JFaceResources.getFont(JFaceResources.DEFAULT_FONT);

		if (obj == null || apiClass.equals(obj.getClass())) {
			ShimObjectColumnFormat format = formatList.get(columnIndex);
			ColumnType type = format.getType();

			switch (type) {
			case INT:
			case FLOAT:
			case HEX:
				// memo. JFaceResources.TEXT_FONT is monospace font.
				result = JFaceResources.getFont(JFaceResources.TEXT_FONT);
				break;

			default:
				break;
			}
		}
		return result;
	}

}
