/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlEnumValue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * The main contract here is the creation of Combo instances.
 */
public class ComboFactory {
	
	public static final String NONE = "[NONE]";

	private Composite compose;

	/**
	 * Constructs a new instance of ComboFactory.
	 * 
	 * @param compose
	 *            the Composite which a created combo is included in
	 */
	public ComboFactory(Composite compose) {
		this.compose = compose;
	}
	
	/**
	 * Creates a new ComboBox widget from enum members.
	 * 
	 * @param cls
	 *            the enum class which a combo is created from
	 * @return the created ComboBox widget
	 */
	public Combo createCombo(Class<?> cls, boolean isRequired) {
		Combo combo = new Combo(compose, SWT.READ_ONLY);

		Enum<?>[] consts = (Enum[]) cls.getEnumConstants();
		String[] strvaluesArray = getStrings(consts, isRequired);
		combo.setItems(strvaluesArray);

		// Set the initial value.
		if (consts.length > 0) {
			combo.select(0);
		}

		return combo;
	}

	/**
	 * Returns the list of enum value string.
	 * 
	 * @param consts
	 *            the elements of the enum class
	 * @return the list of enum value string
	 */
	public static String[] getStrings(Enum<?>[] consts, boolean isRequired) {
		List<String> valueStrings = new ArrayList<String>();

		for (Enum<?> cts : consts) {
			String str = getEnumValue(cts);
			valueStrings.add(str);
		}
		if (!isRequired) {
			valueStrings.add(NONE);
		}
		
		return valueStrings.toArray(new String[valueStrings.size()]);
	}

	/**
	 * Converts enum value to string.
	 * 
	 * @param myEnum
	 *            the element of the enum class
	 * @return enum value string
	 */
	public static String getEnumValue(Enum<?> myEnum) {
		Field f;
		String value;
		try {
			f = myEnum.getClass().getField(myEnum.name());
			f.setAccessible(true);
			XmlEnumValue xev = (XmlEnumValue) f
					.getAnnotation(XmlEnumValue.class);
			if (xev == null) {
				value = f.getName();
			} else {
				value = xev.value();
			}
		} catch (SecurityException | NoSuchFieldException e) {
			value = null;
		}
		return value;
	}
}
