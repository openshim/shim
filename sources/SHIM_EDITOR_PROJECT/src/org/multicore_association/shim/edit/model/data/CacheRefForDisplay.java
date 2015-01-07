/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.data;

import org.multicore_association.shim.api.Cache;
import org.multicore_association.shim.api.CacheType;

/**
 * Display CacheRef.
 * <p>
 * You can redefine the fields in super class which you want to display.
 * </p>
 */
public class CacheRefForDisplay extends Cache {
	protected String name;
	protected CacheType cacheType;
	protected String id;
}