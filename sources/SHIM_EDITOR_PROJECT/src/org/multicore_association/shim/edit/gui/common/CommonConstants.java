/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.common;

import java.util.HashSet;
import java.util.Set;

/**
 * A Constant Class
 */
public class CommonConstants {

	public static final String API_NAME = "SHIM Editor";

	public static final String EDITOR_VERSION = "1.0";

	public static final String SHIM_VERSION = "1.0";

	// ------------------------------------------------
	// FIELD
	// ------------------------------------------------
	public static final String FIELD_NAME = "name";

	public static final String FIELD_ID = "id";

	// ------------------------------------------------
	// DIALOG, MESSAGE
	// ------------------------------------------------
	public static final String QUESTION_DIALOG_TITLE = "QUESTION";

	public static final String MESSAGE_OVERWRITE_ELEMENT = "Target element(%s) with a maxOccurs=\"1\" attribute already exists.\nWould you like to overwrite it?";

	public static final String MESSAGE_INVALID_IDREF = "The AccessType references of the child Performance elements refer to an invalid AccessType.\nSelects the AccessType references of the child Performance elements again.";

	public static final int WIZARD_DEFAULT_X = 100;

	public static final int WIZARD_DEFAULT_Y = 100;

	public static final int WIZARD_WIDTH = 980;

	public static final int WIZARD_HEIGHT = 590;

	// ------------------------------------------------
	// FORMAT
	// ------------------------------------------------
	public static final String FORMAT_HEX = "%08x";

	public static final String FORMAT_FLOAT = "%#.1f";

	// ------------------------------------------------
	// OTHER
	// ------------------------------------------------
	public static final Set<String> notDisplayTableProps = new HashSet<String>();
	static {
		notDisplayTableProps.add(FIELD_ID);
	}

}
