/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A delegate for handling validation for hexadecimal value.
 */
public class CellEditorHexValidator extends CellEditorValidator {

	/**
	 * Constructs a new instance of CellEditorHexValidator.
	 * 
	 * @param propertyName
	 *            the property name to be validated
	 * @param isRequired
	 *            whether the property is required to be non-null value or not
	 */
	public CellEditorHexValidator(String propertyName, boolean isRequired) {
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
		
		Pattern pattern = Pattern.compile("[0-9a-fA-F]*");
		Matcher matcher = pattern.matcher((String) value);

		if (matcher.matches()) {
			try {
				Long.parseLong((String) value, 16);
			} catch (NumberFormatException e) {
				return "Please input a hexadecimal number between 0-"
						+ String.format(CommonConstants.FORMAT_HEX, Long.MAX_VALUE) + ".";
			}

			return null;
			
		} else {
			return "Please input a hexadecimal number.";
		}
	}
}
