/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt;

import java.util.regex.Pattern;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.edit.gui.jface.ErrorMessagePool;

/**
 * A ModifyListener implementation for a byte size list.
 */
public class ByteSizeListModifier implements ModifyListener {

	private static final Pattern COMMA = Pattern.compile(",");

	private ErrorMessagePool pool;

	private String paramName;

	/**
	 * Constructs a new instance of ByteSizeListModifier.
	 * 
	 * @param pool
	 *            the error message pool
	 * @param paramName
	 *            the parameter name to check
	 */
	public ByteSizeListModifier(ErrorMessagePool pool, String paramName) {
		this.pool = pool;
		this.paramName = paramName;
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	@Override
	public void modifyText(ModifyEvent event) {
		Object source = event.getSource();
		String msg = null;
		if (source instanceof Text) {
			String[] byteSizeArray = COMMA.split(((Text) source).getText());
			for (String byteSizeStr : byteSizeArray) {
				if (!isUnsingedInteger(byteSizeStr)) {
					msg = "Input ${number},${number},...,${number} in \'"
							+ paramName + "\' field.";
					break;
				}
			}
		}

		pool.setErrorMessage(this, msg);
	}

	/**
	 * Returns whether the specified string can parse unsigned integer.
	 * 
	 * @param str
	 *            the string
	 * @return Returns true if the specified string can parse unsigned integer,
	 *         and false otherwise.
	 */
	public boolean isUnsingedInteger(String str) {
		try {
			int num = Integer.parseInt(str);
			if (num >= 0) {
				return true;
			}
		} catch (NumberFormatException nf) {
		}

		return false;
	}

}
