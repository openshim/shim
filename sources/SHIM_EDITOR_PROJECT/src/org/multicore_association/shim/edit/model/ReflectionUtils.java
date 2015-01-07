/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.multicore_association.shim.edit.gui.common.CommonConstants;

/**
 * This class contains various methods for using java.lang.reflect API.
 */
public class ReflectionUtils {

	/**
	 * Returns declared field list which contains the same package super class'
	 * field.
	 * 
	 * @param claz
	 *            the claz
	 * @return the declared field list
	 */
	public static List<Field> getDeclaredFieldList(Class<?> claz) {
		List<Field> declaredFieldList = new ArrayList<Field>();
		collectDeclaredFieldList(claz, declaredFieldList);
		return declaredFieldList;
	}

	/**
	 * Collects declared fields recursively.
	 * 
	 * @param claz
	 *            the class name to collect declared fields
	 * @param declaredFieldList
	 *            the list to add the declared fields to
	 */
	private static void collectDeclaredFieldList(Class<?> claz,
			List<Field> declaredFieldList) {
		Field[] declaredFields = claz.getDeclaredFields();
		for (Field declaredField : declaredFields) {
			declaredFieldList.add(declaredField);
		}
		Package package1 = claz.getPackage();
		Package package2 = claz.getSuperclass().getPackage();
		if (package1.equals(package2)) {
			collectDeclaredFieldList(claz.getSuperclass(), declaredFieldList);
		}
	}

	/**
	 * Returns the declared field of the specified class.
	 * 
	 * @param claz
	 *            the class
	 * @param fieldName
	 *            the filed name to return
	 * @return the declared field
	 */
	public static Field getDeclaredField(Class<?> claz, String fieldName) {
		try {
			Field declaredField = claz.getDeclaredField(fieldName);
			return declaredField;
		} catch (NoSuchFieldException e) {
			Class<?> superclass = claz.getSuperclass();
			if (claz.getPackage().equals(superclass.getPackage())) {
				return getDeclaredField(superclass, fieldName);
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Returns the declared field value of the specified object.
	 * 
	 * @param obj
	 *            the object
	 * @param fieldName
	 *            the field name to return
	 * @return the declared field value
	 */
	public static Object getDeclaredFieldValue(Object obj, String fieldName) {
		Field declaredField = getDeclaredField(obj.getClass(), fieldName);
		if (declaredField != null) {
			try {
				declaredField.setAccessible(true);
				return declaredField.get(obj);
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * Sets the declared field value of the specified object.
	 * 
	 * @param obj
	 *            the object
	 * @param fieldName
	 *            the field name to set
	 * @param value
	 *            the field value to set
	 * @return the declared field value
	 */
	public static void setDeclaredField(Object obj, String fieldName,
			Object value) {
		Field declaredField = getDeclaredField(obj.getClass(), fieldName);
		if (declaredField != null) {
			try {
				declaredField.setAccessible(true);
				declaredField.set(obj, value);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Sets the declared field value of the specified object.
	 * 
	 * @param obj
	 *            the object
	 * @param fieldName
	 *            the field name to set
	 * @param value
	 *            the field value to set
	 * @return the declared field value
	 */
	public static void setDeclaredFieldWithAutoTypeConversion(Object obj,
			String fieldName, String value) {
		Field declaredField = getDeclaredField(obj.getClass(), fieldName);
		if (declaredField != null) {
			try {
				declaredField.setAccessible(true);

				Class<?> type = declaredField.getType();
				if (type.equals(Integer.class) || type.equals(int.class)) {
					declaredField.set(obj, Integer.parseInt(value));
					
				} else if (type.equals(Long.class) || type.equals(long.class)) {
					declaredField.set(obj, Long.parseLong(value));
					
				} else if (type.equals(Float.class) || type.equals(float.class)) {
					declaredField.set(obj, Float.parseFloat(value));
					
				} else if (type.equals(String.class)) {
					declaredField.set(obj, value);
					
				} else if (type.isEnum()) {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					Enum enumVal = Enum.valueOf((Class<Enum>)type, value);
					declaredField.set(obj, enumVal);
				}

			} catch (Exception e) {
			}
		}
	}

	/**
	 * Returns the 'name' field value of the specified object.
	 * 
	 * @param object
	 *            the object
	 * @return the 'name' field value
	 */
	public static String getNameProperty(Object object) {
		return (String) getDeclaredFieldValue(object,
				CommonConstants.FIELD_NAME);
	}

}
