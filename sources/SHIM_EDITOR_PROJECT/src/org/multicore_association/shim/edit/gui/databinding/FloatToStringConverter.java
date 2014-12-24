/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.databinding;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * A delegate for converting float value to string value.
 */
public class FloatToStringConverter implements IConverter {

	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.
	 *      Object)
	 */
	@Override
	public Object convert(Object fromObject) {
		if (fromObject == null) {
			return "";
		}
		return convertStr((float) fromObject);
	}

	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#getFromType()
	 */
	@Override
	public Object getFromType() {
		return Float.class;
	}

	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#getToType()
	 */
	@Override
	public Object getToType() {
		return String.class;
	}

	/**
	 * Converts float value to string value.
	 * 
	 * @param f
	 *            float value
	 * @return string value
	 */
	public static String convertStr(float f) {
		return String.format("%.0f", f);
	}

}
