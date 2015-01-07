/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.search;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlType;

import org.multicore_association.shim.edit.gui.swt.ShimObjectColumnFormat;
import org.multicore_association.shim.edit.gui.swt.ShimObjectColumnFormat.ColumnType;
import org.multicore_association.shim.edit.model.ReflectionUtils;

/**
 * A search result.
 */
public class ShimSearchResult {

	private String type;

	private String name;

	private Object data;

	private String matchProperty;

	private Object propertyValue;

	private ShimObjectColumnFormat format;

	private String valueStr;

	private String parentName;

	/**
	 * Constructs a new instance of ShimSearchResult.
	 * 
	 * @param data
	 *            the hit data
	 * @param matchProperty
	 *            the propety of the data which matches search text
	 * @param parentName
	 *            the parent name
	 */
	public ShimSearchResult(Object data, String matchProperty, String parentName) {
		this.data = data;
		this.matchProperty = matchProperty;
		this.parentName = parentName;

		Class<? extends Object> apiClaz = data.getClass();
		try {
			Field declaredField = ReflectionUtils.getDeclaredField(apiClaz,
					matchProperty);
			declaredField.setAccessible(true);

			this.propertyValue = declaredField.get(data);
			this.format = PropertyCompareUtil.getColumnFormat(apiClaz,
					declaredField);
		} catch (Exception e) {
		}
	}

	/**
	 * Returns the type of SHIM API.
	 * 
	 * @return the type of SHIM API
	 */
	public String getType() {
		if (type == null) {
			try {
				XmlType type = data.getClass().getAnnotation(XmlType.class);
				if (type.name() != null) {
					this.type = type.name();
				}
			} catch (Exception e) {
				type = data.getClass().getSimpleName();
			}
		}
		return type;
	}

	/**
	 * Returns the 'name' field value of data.
	 * 
	 * @return the 'name' field value of data
	 */
	public String getName() {
		if (name == null) {
			String _name = ReflectionUtils.getNameProperty(data);
			if (_name == null) {
				name = "-";
			} else {
				name = _name;
			}
		}
		return name;
	}

	/**
	 * Returns the propety value which is converted to String type.
	 * 
	 * @return the propety value
	 */
	public String getPropertyValueStr() {
		if (valueStr == null) {
			if (format != null && format.getType() == ColumnType.OBJECT) {
				this.valueStr = ReflectionUtils.getNameProperty(propertyValue);
			} else {
				this.valueStr = PropertyCompareUtil.getPropertyValueStr(
						propertyValue, format);
			}
		}
		return valueStr;
	}

	/**
	 * Replaces the value with the specified value.
	 * 
	 * @param searchText
	 * @param replaceText
	 */
	public void replace(String searchText, String replaceText) {
		// replace
		String orgValue = getPropertyValueStr();
		String replaceValue = orgValue.replace(searchText, replaceText);
		ReflectionUtils.setDeclaredFieldWithAutoTypeConversion(data,
				matchProperty, replaceValue);
		
		// update field value
		propertyValue = ReflectionUtils.getDeclaredFieldValue(data, matchProperty);
		name = null;
		valueStr = null;
	}

	/**
	 * Returns the hit data.
	 * 
	 * @return the hit data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Returns  the propety of the data which matches search text.
	 * 
	 * @return  the propety of the data which matches search text
	 */
	public String getMatchProperty() {
		return matchProperty;
	}

	/**
	 * Returns the parent name.
	 * 
	 * @return the parent name
	 */
	public String getParentName() {
		return parentName;
	}
}
