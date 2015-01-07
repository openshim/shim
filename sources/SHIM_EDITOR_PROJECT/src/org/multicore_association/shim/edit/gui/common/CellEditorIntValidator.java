/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A delegate for handling validation for integer value.
 */
public class CellEditorIntValidator extends CellEditorValidator {
	
	/**
	 * Constructs a new instance of CellEditorIntValidator.
	 * 
	 * @param propertyName
	 *            the property name to be validated
	 * @param isRequired
	 *            whether the property is required to be non-null value or not.
	 */
	public CellEditorIntValidator(String propertyName, boolean isRequired) {
		super(propertyName, isRequired);
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.common.CellEditorValidator#isValid(java.lang.Object)
	 */
	@Override
	public String isValid(Object value) {
		// To check Required.
		String msg = super.isValid(value);
		if (msg.length() > 0) {
			return msg;
		}
		String valStr = (String) value;
		if (!isRequired && (value == null || valStr.isEmpty())) {
			return null;
		}

		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher matcher = pattern.matcher(valStr);

		if (matcher.matches()) {
			try {
				Long.parseLong(valStr, 16);
			} catch (NumberFormatException e) {
				return "Please enter a decimal number between 0-"
						+ Integer.MAX_VALUE + ".";
			}

			return null;
			
		} else {
			return "Please enter a decimal number.";
		}
	}
}
