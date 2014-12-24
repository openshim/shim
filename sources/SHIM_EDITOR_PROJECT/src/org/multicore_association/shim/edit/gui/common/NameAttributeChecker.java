/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimObject;

/**
 * A delegate for handling validation for "name" attribute.
 */
public class NameAttributeChecker {

	private static final Logger log = ShimLoggerUtil
			.getLogger(NameAttributeChecker.class);

	private HashMap<String, HashSet<String>> nameList = new HashMap<String, HashSet<String>>();
	private String parentXPath = null;
	private Class<?> cls = null;
	private Object parent = null;
	private Object[] objects = null;

	private static final String ERROR_MESSAGE_INVALID_OLD_NAME = "The old Name (%s) doesn't exist.";
	private static final String ERROR_MESSAGE_INVALID_NEW_NAME = "The new Name (%s) already exists in same parent.";

	private static final String DUMMY_XPATH = "dummy_parent/dummy_object";

	/**
	 * It supports to {@linkplain shim.gui.swt.InputPanelBase InputPanel}.
	 * 
	 * @param cls
	 * @param parent
	 */
	public NameAttributeChecker(Class<?> cls, Object parent) {
		this.cls = cls;
		this.parent = parent;
		updateNameList();
	}

	/**
	 * It supports to {@linkplain shim.gui.swt.viewer.ShimObjectTableViewer
	 * ShimObjectTableViewer}.
	 * 
	 * @param objectList
	 */
	public NameAttributeChecker(List<ShimObject> objectList) {
		nameList = createNameList(objectList);
	}

	/**
	 * It supports to Object which is member of package shim.api.
	 * 
	 * @param objects
	 *            Specify the objects you want to compare.
	 * @see org.multicore_association.shim.edit.gui.jface.wizard.AddressSpaceEditorWizardPage#getNextPage()
	 */
	public NameAttributeChecker(Object[] objects) {
		this.parentXPath = getParentXpath(DUMMY_XPATH);
		this.objects = objects;

		nameList = createNameList(objects);
	}

	/**
	 * Creates a mapping of the name attribute every objects from the specified
	 * objects.
	 * 
	 * @param objects
	 *            objects which a mapping of the name attribute every objects is
	 *            created from
	 * @return the mapping of the name attribute every objects
	 */
	private HashMap<String, HashSet<String>> createNameList(Object[] objects) {

		List<ShimObject> objectList = new ArrayList<ShimObject>();
		for (Object obj : objects) {
			ShimObject so = new ShimObject(obj, DUMMY_XPATH);
			objectList.add(so);
		}

		return createNameList(objectList);
	}

	/**
	 * Creates a mapping of the name attribute every objects from the specified
	 * Shim_Objects.
	 * 
	 * @param list
	 *            Shim_Objects which a mapping of the name attribute every
	 *            objects is created from
	 * @return the mapping of the name attribute every objects
	 */
	private HashMap<String, HashSet<String>> createNameList(
			List<ShimObject> list) {

		HashMap<String, HashSet<String>> nameList = new HashMap<String, HashSet<String>>();

		for (ShimObject l_so : list) {
			String parentXPath = getParentXpath((String) l_so
					.getValue(ShimObject.CL_X_PATH));
			String name = (String) l_so.getValue(CommonConstants.FIELD_NAME);

			if (nameList.containsKey(parentXPath)) {
				HashSet<String> nameSet = nameList.get(parentXPath);
				nameSet.add(name);
			} else {
				HashSet<String> nameSet = new HashSet<String>();
				nameSet.add(name);
				nameList.put(parentXPath, nameSet);
			}
		}

		// debug log
		log.finest("===== NameList =====");
		Set<String> keySet = nameList.keySet();
		for (String key : keySet) {
			log.finest("parentXPath = " + key);
			HashSet<String> nameSet = nameList.get(key);
			for (String name : nameSet) {
				log.finest("    Name = " + name);
			}
		}
		log.finest("===== ======== =====");

		return nameList;
	}

	/**
	 * Creates the XPath of parent element from the specified XPath.
	 * 
	 * @param xPath
	 *            XPath which the XPath of parent element is created from
	 * @return the XPath of parent element
	 */
	private String getParentXpath(String xPath) {
		String parentXPath = null;
		int endIndex = xPath.lastIndexOf("/");
		if (endIndex >= 0) {
			parentXPath = xPath.substring(0, endIndex);
		}
		return parentXPath;
	}

	/**
	 * Check the uniqueness without changing the value.
	 * 
	 * @param oldName
	 * @param newName
	 * @return Error message. If there is no error, this method return null.
	 */
	public String checkNameList(String oldName, String newName) {
		String result = null;
		if (!oldName.equals(newName)) {
			if (!isExist(parentXPath, newName)) {
				if (!isExist(parentXPath, oldName)) {
					result = String.format(ERROR_MESSAGE_INVALID_OLD_NAME,
							oldName);
				}
			} else {
				result = String.format(ERROR_MESSAGE_INVALID_NEW_NAME, newName);
			}
		}
		return result;
	}

	/**
	 * Returns whether an element with the specified name exits or not.
	 * 
	 * @param name
	 * @return Return true if an element with the specified name exits, and
	 *         false otherwise.
	 */
	public boolean isExist(String name) {
		boolean result = false;
		if (parentXPath != null) {
			result = isExist(parentXPath, name);
		} else {
			log.log(Level.FINEST,
					"Please use the appropriate constructor.[isExist] : name="
							+ name);
		}
		return result;
	}

	/**
	 * Updates the mapping of the name attribute every objects.
	 */
	public void updateNameList() {
		if (cls != null && parent != null) {
			if (!cls.equals(parent.getClass())) {
				List<ShimObject> objectList = ShimModelAdapter.getObjectsList(
						cls, parent);
				nameList = createNameList(objectList);
			} else {
				Object obj = ShimModelAdapter.getValObject(parent,
						cls.getSimpleName());
				if (obj instanceof List) {
					ArrayList<?> _objectList = (ArrayList<?>) obj;
					nameList = createNameList(_objectList.toArray());
				}
			}
			Set<String> keys = nameList.keySet();
			if (keys.size() == 1) {
				for (String key : keys) {
					parentXPath = key;
				}
			} else if (keys.size() > 1) {
				String[] keysArray = keys.toArray(new String[0]);
				Arrays.sort(keysArray);
				parentXPath = keysArray[0];
			}
		} else if (parentXPath != null && objects != null) {
			nameList = createNameList(objects);
		} else {
			log.log(Level.FINEST,
					"Please use the appropriate constructor.[updateNameList]");
		}
	}

	/**
	 * Updates the mapping of the name attribute by arguments.
	 * 
	 * @param xPath
	 *            XPath of the element with old name
	 * @param oldName
	 *            name before update
	 * @param newName
	 *            name after update
	 * @return Error message. If there is no error, this method return null.
	 */
	public String updateNameList(String xPath, String oldName, String newName) {
		String parentXPath = null;
		if (xPath != null) {
			parentXPath = getParentXpath(xPath);
		}
		return _updateNameList(parentXPath, oldName, newName);
	}

	/**
	 * Updates the mapping of the name attribute by arguments.
	 * 
	 * @param parentXPath
	 *            XPath of the parent element which is the element with old name
	 * @param oldName
	 *            name before update
	 * @param newName
	 *            name after update
	 * @return Error message. If there is no error, this method return null.
	 */
	private String _updateNameList(String parentXPath, String oldName,
			String newName) {
		// Check argument.
		if (nameList == null || parentXPath == null || oldName == null
				|| newName == null) {
			return null;
		}

		String result = null;
		if (oldName.equals(newName)) {
			result = null;
		} else {
			if (!isExist(parentXPath, newName)) {
				if (isExist(parentXPath, oldName)) {
					HashSet<String> nameSet = nameList.get(parentXPath);
					nameSet.remove(oldName);
					nameSet.add(newName);
				} else {
					result = String.format(ERROR_MESSAGE_INVALID_OLD_NAME,
							oldName);
				}
			} else {
				result = String.format(ERROR_MESSAGE_INVALID_NEW_NAME, newName);
			}
		}
		return result;
	}

	/**
	 * Returns whether an element with the specified name exits or not.
	 * 
	 * @param parentXPath
	 *            XPath of the parent element which is the element with the
	 *            specified name
	 * @param name
	 * @return Return true if an element with the specified name exits, and
	 *         false otherwise.
	 */
	private boolean isExist(String parentXPath, String name) {
		boolean result = false;
		if (nameList != null && parentXPath != null) {
			if (nameList.containsKey(parentXPath)) {
				HashSet<String> nameSet = nameList.get(parentXPath);
				result = nameSet.contains(name);
			}
		}
		return result;
	}
}
