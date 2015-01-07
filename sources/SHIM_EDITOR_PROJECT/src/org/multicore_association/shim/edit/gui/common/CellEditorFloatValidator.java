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
 * A delegate for handling validation for float value.
 */
public class CellEditorFloatValidator extends CellEditorValidator {

	/**
	 * Constructs a new instance of CellEditorFloatValidator.
	 * 
	 * @param propertyName
	 *            the property name to be validated
	 * @param isRequired
	 *            whether the property is required to be non-null value or not.
	 */
	public CellEditorFloatValidator(String propertyName, boolean isRequired) {
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

		Pattern pattern = Pattern
				.compile("(-?(0|[1-9]\\d*)\\.\\d+)|(-?([1-9]\\d*))");
		Matcher matcher = pattern.matcher(valStr);

		if (matcher.matches()) {
			try {
				Float.parseFloat(valStr);
			} catch (NumberFormatException e) {
				return "Please input a number between " + Float.MIN_VALUE + "-"
						+ Float.MAX_VALUE + ".";
			}

			return null;

		} else {
			return "Please input a number.";
		}
	}
}
