/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.databinding;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * A delegate for converting long value to string value.
 */
public class LongToStringConverter implements IConverter {

	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	@Override
	public Object convert(Object fromObject) {
		if (fromObject == null) {
			return "";
		}
		return fromObject.toString();
	}

	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#getFromType()
	 */
	@Override
	public Object getFromType() {
		return Long.class;
	}

	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#getToType()
	 */
	@Override
	public Object getToType() {
		return String.class;
	}

}
