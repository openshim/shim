/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model;

import org.multicore_association.shim.api.CacheCoherencyType;
import org.multicore_association.shim.api.CacheType;
import org.multicore_association.shim.api.EndianType;
import org.multicore_association.shim.api.LockDownType;
import org.multicore_association.shim.api.MasterType;
import org.multicore_association.shim.api.OperationType;
import org.multicore_association.shim.api.OrderingType;
import org.multicore_association.shim.api.RWType;
import org.multicore_association.shim.api.SizeUnitType;

/**
 * This class contains various methods for using enum class of the SHIM API.
 */
public class EnumUtil {

	/**
	 * Returns the CacheCoherencyType.
	 * 
	 * @param selectionIndex
	 *            the index of selection
	 * @return the CacheCoherencyType
	 */
	public static CacheCoherencyType getCacheCoherencyType(int selectionIndex) {
		CacheCoherencyType result = null;
		CacheCoherencyType[] values = CacheCoherencyType.values();
		if (selectionIndex >= 0 && selectionIndex < values.length) {
			result = values[selectionIndex];
		}
		return result;
	}

	/**
	 * Returns the CacheType.
	 * 
	 * @param selectionIndex
	 *            the index of selection
	 * @return the CacheType
	 */
	public static CacheType getCacheType(int selectionIndex) {
		CacheType result = null;
		CacheType[] values = CacheType.values();
		if (selectionIndex >= 0 && selectionIndex < values.length) {
			result = values[selectionIndex];
		}
		return result;
	}

	/**
	 * Returns the EndianType.
	 * 
	 * @param selectionIndex
	 *            the index of selection
	 * @return the EndianType
	 */
	public static EndianType getEndianType(int selectionIndex) {
		EndianType result = null;
		EndianType[] values = EndianType.values();
		if (selectionIndex >= 0 && selectionIndex < values.length) {
			result = values[selectionIndex];
		}
		return result;
	}

	/**
	 * Returns the LockDownType.
	 * 
	 * @param selectionIndex
	 *            the index of selection
	 * @return the LockDownType
	 */
	public static LockDownType getLockDownType(int selectionIndex) {
		LockDownType result = null;
		LockDownType[] values = LockDownType.values();
		if (selectionIndex >= 0 && selectionIndex < values.length) {
			result = values[selectionIndex];
		}
		return result;
	}

	/**
	 * Returns the MasterType.
	 * 
	 * @param selectionIndex
	 *            the index of selection
	 * @return the MasterType
	 */
	public static MasterType getMasterType(int selectionIndex) {
		MasterType result = null;
		MasterType[] values = MasterType.values();
		if (selectionIndex >= 0 && selectionIndex < values.length) {
			result = values[selectionIndex];
		}
		return result;
	}

	/**
	 * Returns the OperationType.
	 * 
	 * @param selectionIndex
	 *            the index of selection
	 * @return the OperationType
	 */
	public static OperationType getOperationType(int selectionIndex) {
		OperationType result = null;
		OperationType[] values = OperationType.values();
		if (selectionIndex >= 0 && selectionIndex < values.length) {
			result = values[selectionIndex];
		}
		return result;
	}

	/**
	 * Returns the OrderingType.
	 * 
	 * @param selectionIndex
	 *            the index of selection
	 * @return the OrderingType
	 */
	public static OrderingType getOrderingType(int selectionIndex) {
		OrderingType result = null;
		OrderingType[] values = OrderingType.values();
		if (selectionIndex >= 0 && selectionIndex < values.length) {
			result = values[selectionIndex];
		}
		return result;
	}

	/**
	 * Returns the RWType.
	 * 
	 * @param selectionIndex
	 *            the index of selection
	 * @return the RWType
	 */
	public static RWType getRWType(int selectionIndex) {
		RWType result = null;
		RWType[] values = RWType.values();
		if (selectionIndex >= 0 && selectionIndex < values.length) {
			result = values[selectionIndex];
		}
		return result;
	}

	/**
	 * Returns the SizeUnitType.
	 * 
	 * @param selectionIndex
	 *            the index of selection
	 * @return the SizeUnitType
	 */
	public static SizeUnitType getSizeUnitType(int selectionIndex) {
		SizeUnitType result = null;
		SizeUnitType[] values = SizeUnitType.values();
		if (selectionIndex >= 0 && selectionIndex < values.length) {
			result = values[selectionIndex];
		}
		return result;
	}

}
