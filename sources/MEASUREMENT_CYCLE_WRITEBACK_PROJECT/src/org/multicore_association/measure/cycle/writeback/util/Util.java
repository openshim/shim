/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.cycle.writeback.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	/* Restriction chars: \, /, :, *, ?, ", <, >, |, - */
	private static final String FILENAME_RESTRICTION_CHARS_REGEX = "\\W"; //all but [a-zA-Z0-9]
	private static final String FILENAME_SUBSTITUTE_SEPARATOR = "_";

	private static final Pattern pattern = Pattern.compile(FILENAME_RESTRICTION_CHARS_REGEX);

	/**
	 * Restriction character function processing.
	 * The character which can't be used for the file name
	 * in the character string and a function name of src is changed to an underscore.
	 * @param src source string
	 * @return replaced string
	 */
	public static String convertRestrictionCharToUS(String src) {

		if (src == null || src.equals("")) {
			return src;
		}

		Matcher mat = pattern.matcher(src);
		String ret = mat.replaceAll(FILENAME_SUBSTITUTE_SEPARATOR);

		return ret;
	}


	/**
	 * Conversion to Float from String.
	 * -1.0f is returned if can not convert.
	 * @param data String data
	 * @return Float value
	 */
	public static float convertFloatValue(String data) {
		float ret = -1.0f;
		if (data != null) {
			try {
				ret = Float.valueOf(data);
			} catch (NumberFormatException e) {
				ret = -1.0f;
			}
		}

		return ret;
	}

	public static boolean checkFloatFormat(String str) {
		boolean ch = false;
		if (str == null) {
			return false;
		}

		try {
			Float.valueOf(str);
			ch = true;
		} catch (NumberFormatException e) {
			ch = false;
		}
		return ch;
	}
}
