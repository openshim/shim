/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model;

import java.util.List;

/**
 * This class contains various methods for ShimObject.
 */
public class ShimObjectUtils {

	/**
	 * Determines whether the object is included in the list specified.
	 * 
	 * @param list
	 * @param so
	 * @return Returns true if this list contains the specified object; false otherwise.
	 */
	public static boolean contains(List<ShimObject> list, ShimObject so) {
		boolean result = false;
		Object obj = so.getObj();
		for (ShimObject l_so : list) {
			if (l_so.getObj().equals(obj)) {
				result = true;
				break;
			}
		}
		return result;
	}
}
