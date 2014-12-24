/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model;

import org.multicore_association.shim.edit.gui.common.CommonConstants;

/**
 * An implementation for the element of SHIM Data.
 */
public class ShimObject {

	public static final String CL_PARENT_NAME = "Parent Name";
	public static final String CL_X_PATH = "XPath";

	private Object obj;
	private String parentName;
	private String xPath;

	/**
	 * Constructs a new instance of ShimObject.
	 * 
	 * @param obj
	 *            the element of SHIM Data
	 * @param parentName
	 *            the name attribute of the parent element
	 * @param xPath
	 *            XPath of the element
	 */
	public ShimObject(Object obj, String parentName, String xPath) {
		this.obj = obj;
		this.parentName = parentName;
		this.xPath = xPath;
	}

	/**
	 * Constructs a new instance of ShimObject.
	 * 
	 * @param obj
	 *            the element of SHIM Data
	 * @param xPath
	 *            XPath of the element
	 */
	public ShimObject(Object obj, String xPath) {
		this.obj = obj;
		this.parentName = "";
		this.xPath = xPath;
	}

	/**
	 * Constructs a new instance of ShimObject.
	 * 
	 * @param obj
	 *            the element of SHIM Data
	 */
	public ShimObject(Object obj) {
		this.obj = obj;
		this.parentName = "";
		this.xPath = "";
	}

	/**
	 * Returns the object.
	 * 
	 * @return the object
	 */
	public Object getObj() {
		return this.obj;
	}

	/**
	 * Returns the parent name.
	 * 
	 * @return the parent name
	 */
	String getParentName() {
		return this.parentName;
	}

	/**
	 * Returns the XPath.
	 * 
	 * @return the XPath
	 */
	String getXPath() {
		return this.xPath;
	}

	/**
	 * Sets XPath to the field.
	 * 
	 * @param XPath
	 *            the XPath to set
	 */
	void setXPath(String XPath) {
		this.xPath = XPath;
	}

	/**
	 * Returns the value of the specified field.
	 * 
	 * @param fieldName
	 *            the name of field to return
	 * @return the value of the specified field
	 */
	public Object getValue(String fieldName) {
		Object val = null;
		if (fieldName.equals(CL_PARENT_NAME)) {
			val = getParentName();

		} else if (fieldName.equals(CL_X_PATH)) {
			val = getXPath();

		} else {
			val = ShimModelAdapter.getValObject(getObj(), fieldName);
		}
		return val;
	}

	/**
	 * Sets the specified value to the specified field.
	 * 
	 * @param fieldName
	 *            the name of field to set
	 * @param val
	 *            the value to set to field
	 */
	public void setValue(String fieldName, Object val) {
		if (fieldName.equals(CL_PARENT_NAME)) {
			this.parentName = (String) val;

		} else if (fieldName.equals(CL_X_PATH)) {
			this.xPath = (String) val;
		} else if (val == null) {
			ShimModelAdapter.setNull(getObj(), fieldName);

		} else {
			ShimModelAdapter.setValObject(getObj(), fieldName, val);

			if (CommonConstants.FIELD_NAME.endsWith(fieldName)) {
				String _xPath = this.xPath;

				if (_xPath.length() > 0) {
					String str = "@" + CommonConstants.FIELD_NAME + "='";
					int startIdx = _xPath.lastIndexOf(str) + str.length();
					int endIdx = _xPath.lastIndexOf("'");

					StringBuffer sb = new StringBuffer();
					sb.append(_xPath.substring(0, startIdx));
					sb.append(val);
					sb.append(_xPath.substring(endIdx, _xPath.length()));
					this.xPath = sb.toString();
				}
			}
		}
	}
}