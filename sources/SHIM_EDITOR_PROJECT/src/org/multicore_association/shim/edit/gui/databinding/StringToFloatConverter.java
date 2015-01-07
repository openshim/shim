/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.databinding;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * A delegate for converting string value to float value.<b>
 * The value to be converted must be non-null value.
 */
public class StringToFloatConverter implements IConverter {

	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	@Override
	public Object convert(Object fromObject) {
		return Float.parseFloat((String) fromObject);
	}

	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#getFromType()
	 */
	@Override
	public Object getFromType() {
		return String.class;
	}

	/**
	 * @see org.eclipse.core.databinding.conversion.IConverter#getToType()
	 */
	@Override
	public Object getToType() {
		return Float.class;
	}

}
