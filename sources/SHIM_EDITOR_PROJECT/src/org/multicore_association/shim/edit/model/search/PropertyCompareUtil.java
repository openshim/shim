/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.search;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlType;

import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.gui.swt.ShimObjectColumnFormat;
import org.multicore_association.shim.edit.gui.swt.ShimObjectColumnFormat.ColumnType;
import org.multicore_association.shim.edit.model.ReflectionUtils;
import org.multicore_association.shim.edit.model.ShimModelAdapter;

/**
 * The class which provides the function that the search word compares with the
 * property of the class.
 */
public class PropertyCompareUtil {

	private static final String CLASS_NAME_STRING = String.class.getName();

	/**
	 * Returns names of the properties that is matched with the search text.
	 * 
	 * @param text
	 *            search text
	 * @param element
	 *            element
	 * @param options
	 */
	public static List<String> getMatchProperties(String text, Object element,
			SearchOptions options) {
		List<String> matchProperties = new ArrayList<String>();
		if (!options.matchElement(getElementName(element))) {
			return matchProperties;
		}

		Class<?> apiClass = element.getClass();

		String searchText = text;
		if (!options.isCaseSensitive()) {
			searchText = searchText.toLowerCase();
		}

		ValueMatcher valueMatcher = ValueMatcher.getValueMatcher(searchText,
				options.isWholeWord(), options.isRegExp());

		List<Field> declaredFieldList = ReflectionUtils
				.getDeclaredFieldList(apiClass);

		for (Field declaredField : declaredFieldList) {
			String propertyName = declaredField.getName();
			if (CommonConstants.notDisplayTableProps.contains(propertyName)) {
				continue;
			}
			if (!options.matchProperty(propertyName)) {
				continue;
			}

			try {

				declaredField.setAccessible(true);
				Object propValue = declaredField.get(element);
				if (propValue == null || propValue instanceof List) {
					continue;
				}

				ShimObjectColumnFormat format = getColumnFormat(apiClass,
						declaredField);
				String propertyString = null;
				if (format != null && format.getType() == ColumnType.OBJECT) {
					propertyString = options.isDoNotSearchIdRef() ? null
							: ReflectionUtils.getNameProperty(propValue);
				} else {
					propertyString = getPropertyValueStr(propValue, format);
				}

				if (propertyString == null) {
					continue;
				}

				if (!options.isCaseSensitive()) {
					propertyString = propertyString.toLowerCase();
				}

				if (valueMatcher.isMatch(propertyString)) {
					matchProperties.add(propertyName);
				}

			} catch (Exception e) {
			}

		}

		return matchProperties;
	}

	/**
	 * Returns the element name of the spedified object.
	 * 
	 * @param obj
	 *            the object
	 * @return XmlType.name()
	 */
	private static String getElementName(Object obj) {
		XmlType xmlType = obj.getClass().getAnnotation(XmlType.class);
		if (xmlType != null && xmlType.name() != null) {
			return xmlType.name();
		}
		return null;
	}

	/**
	 * Returns the string representation of the specified property value.
	 * 
	 * @param propVal
	 *            the property value
	 * @param format
	 *            the column format
	 * @return the string representation of the property value
	 */
	public static String getPropertyValueStr(Object propVal,
			ShimObjectColumnFormat format) {
		if (format != null) {
			ColumnType type = format.getType();

			if (propVal == null) {
				return null;
			}

			switch (type) {
			case TEXT:
			case INT:
			case HEADER:
			case FOOTER:
			case COMBO:
				return propVal.toString();
			case FLOAT:
				return String.format(CommonConstants.FORMAT_FLOAT, propVal);
			case HEX:
				return String.format(CommonConstants.FORMAT_HEX, propVal);
			default:
				break;
			}
		}
		return null;
	}

	/**
	 * Returns the column format of the specified field.
	 * 
	 * @param apiClass
	 *            the class of ShimObject's data
	 * @param f
	 *            the field of element
	 * @return the column format of the specified field.
	 */
	public static ShimObjectColumnFormat getColumnFormat(Class<?> apiClass,
			Field f) {

		Package cPackage = apiClass.getPackage();
		String name = f.getName();
		Class<?> type = f.getType();

		ShimObjectColumnFormat format = null;

		boolean isRequied = ShimModelAdapter.isRequired(apiClass, name);
		Package fPackage = type.getPackage();

		String insName = type.getName();

		if (fPackage != null && fPackage.equals(cPackage)) {
			if (type.isEnum()) {
				// It is 'Type' Class of SHIM API.
				format = new ShimObjectColumnFormat(name, ColumnType.COMBO,
						type, isRequied);
			}
		} else {

			if (insName.equals(CLASS_NAME_STRING)) {
				format = new ShimObjectColumnFormat(name, ColumnType.TEXT,
						type, isRequied);
			} else if (insName
					.equals(ShimModelAdapter.CLASS_NAME_INTEGER_WRAPPER)
					|| insName
							.equals(ShimModelAdapter.CLASS_NAME_INTEGER_PRIMITIVE)) {
				format = new ShimObjectColumnFormat(name, ColumnType.INT, type,
						isRequied);
			} else if (insName.equals(ShimModelAdapter.CLASS_NAME_LONG_WRAPPER)
					|| insName
							.equals(ShimModelAdapter.CLASS_NAME_LONG_PRIMITIVE)) {
				format = new ShimObjectColumnFormat(name, ColumnType.HEX, type,
						isRequied);
			} else if (insName
					.equals(ShimModelAdapter.CLASS_NAME_FLOAT_WRAPPER)
					|| insName
							.equals(ShimModelAdapter.CLASS_NAME_FLOAT_PRIMITIVE)) {
				format = new ShimObjectColumnFormat(name, ColumnType.FLOAT,
						type, isRequied);

			} else if (insName.equals(ShimModelAdapter.CLASS_NAME_OBJECT)) {
				format = new ShimObjectColumnFormat(name, ColumnType.OBJECT,
						type, isRequied);

			} else {
				format = new ShimObjectColumnFormat(name, ColumnType.TEXT,
						type, isRequied);
			}
		}

		return format;
	}

	/**
	 * Abstract base implementation for matching values.
	 */
	private abstract static class ValueMatcher {

		protected String searchText;

		/**
		 * Constructs a new instance of ValueMatcher.
		 * 
		 * @param searchText
		 *            a string to search
		 */
		ValueMatcher(String searchText) {
			this.searchText = searchText;
		}

		/**
		 * Returns whether the searchText is matched with the specified property
		 * value.
		 * 
		 * @param propertyValue
		 *            the property value
		 * @return Returns true if searchText is matched with the specified
		 *         property value, and false otherwise.
		 */
		abstract boolean isMatch(String propertyValue);

		/**
		 * Returns the ValueMatcher according to the search option.
		 * 
		 * @param searchText
		 *            the text to search
		 * @param isWholeWord
		 *            if true, only whole words were searched
		 * @param isRegExp
		 *            if true, regular expression search was performed; if
		 *            false, search with basic wildcards was performed
		 * @return the ValueMatcher instance
		 */
		static ValueMatcher getValueMatcher(String searchText,
				boolean isWholeWord, boolean isRegExp) {
			if (isRegExp) {
				return new RegularExpressionMatcher(searchText);
			} else if (isWholeWord) {
				return new PerfectMatcher(searchText);
			} else {
				return new ParticleMatcher(searchText);
			}
		}
	}

	/**
	 * Perfect-Matching ValueMatcher class
	 */
	static class PerfectMatcher extends ValueMatcher {

		/**
		 * Constructs a new instance of PerfectMatcher.
		 * 
		 * @param searchText
		 *            a string to search
		 */
		PerfectMatcher(String searchText) {
			super(searchText);
		}

		/**
		 * @see org.multicore_association.shim.edit.model.search.PropertyCompareUtil.ValueMatcher#isMatch(java.lang.String)
		 */
		@Override
		boolean isMatch(String propertyValue) {
			return propertyValue.equals(searchText);
		}
	}

	/**
	 * Particle-Matching ValueMatcher class
	 */
	static class ParticleMatcher extends ValueMatcher {

		/**
		 * Constructs a new instance of ParticleMatcher.
		 * 
		 * @param searchText
		 *            a string to search
		 */
		ParticleMatcher(String searchText) {
			super(searchText);
		}

		/**
		 * @see org.multicore_association.shim.edit.model.search.PropertyCompareUtil.ValueMatcher#isMatch(java.lang.String)
		 */
		@Override
		boolean isMatch(String propertyValue) {
			return propertyValue.contains(searchText);
		}
	}

	/**
	 * Pattern-Matching ValueMatcher class
	 */
	static class RegularExpressionMatcher extends ValueMatcher {

		private Pattern pattern;

		/**
		 * Constructs a new instance of RegularExpressionMatcher.
		 * 
		 * @param searchText
		 *            regular expressions to search
		 */
		RegularExpressionMatcher(String searchText) {
			super(searchText);
			this.pattern = Pattern.compile(searchText);
		}

		/**
		 * @see org.multicore_association.shim.edit.model.search.PropertyCompareUtil.ValueMatcher#isMatch(java.lang.String)
		 */
		@Override
		boolean isMatch(String propertyValue) {
			return pattern.matcher(propertyValue).matches();
		}
	}

}
