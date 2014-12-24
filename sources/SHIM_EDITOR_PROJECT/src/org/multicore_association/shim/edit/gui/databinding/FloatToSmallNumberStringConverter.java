/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.databinding;

import org.eclipse.core.databinding.conversion.IConverter;
import org.multicore_association.shim.edit.gui.common.CommonConstants;

/**
 * A delegate for converting float value to string value.
 */
public class FloatToSmallNumberStringConverter implements IConverter {
	
	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	@Override
	public Object convert(Object fromObject) {
		if (fromObject == null) {
			return "";
		}
		return String.format(CommonConstants.FORMAT_FLOAT, fromObject);
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
}
