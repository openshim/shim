/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.widgets.TreeItem;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimModelManager;

import com.sun.xml.internal.ws.util.StringUtils;

public class ShimTreeViewerUtil {

	/**
	 * This class has only parentObject and targetObject.
	 */
	public static class ParentAndTarget {

		private Object parentObject;
		private Object targetObject;

		public ParentAndTarget(Object parentObject, Object targetObject) {
			this.parentObject = parentObject;
			this.targetObject = targetObject;
		}

		public Object getParentObject() {
			return parentObject;
		}

		public Object getTargetObject() {
			return targetObject;
		}
	}

	/**
	 * Returns whether all selected items belong to the same parent.
	 * 
	 * @param selection
	 *            selecetion of the tree viewer
	 * @return Returns true if all selected items belong to the same parent, and
	 *         false otherwise.
	 */
	public static boolean selectSameParentItems(TreeItem[] selection) {
		boolean result = false;
		if (selection.length > 0) {
			result = true;

			String preParentClassName = null;
			for (TreeItem item : selection) {
				TreeItem parentItem = item.getParentItem();

				// All selected items must have the same parent class.
				String parentClassName = getClassName(parentItem);
				if (preParentClassName != null
						&& !preParentClassName.equals(parentClassName)) {
					result = false;
					break;
				}

				preParentClassName = parentClassName;
			}
		}
		return result;
	}

	/**
	 * Returns the class name of item
	 * 
	 * @param parentItem
	 *            item of tree
	 * @return the class name of item
	 */
	public static String getClassName(TreeItem item) {
		if (item == null) {
			return "";
		}

		Object parent = item.getData();
		Class<?> claz = parent.getClass();
		Package shimPackage = claz.getPackage();
		while (shimPackage.equals(claz.getSuperclass().getPackage())) {
			claz = claz.getSuperclass();
		}

		return claz.getName();
	}

	/**
	 * Returns whether the selection items is deleteable.
	 * 
	 * @param selection
	 *            the selection items in tree viewer
	 * @param targetList
	 *            ParentAndTarget list to delete
	 * @return Returns true if the selection items is deleteable, and false
	 *         otherwise.
	 */
	public static boolean isDeletable(TreeItem[] selection,
			List<ParentAndTarget> targetList) {
		boolean isDeletable = true;
		Map<Object, Map<String, List<Object>>> sameParentObjectMap = new HashMap<Object, Map<String, List<Object>>>();

		// 1st loop: put item to delete in every field of parent object.
		for (TreeItem item : selection) {
			Object currObject = item.getData();
			if (currObject == null) {
				continue;
			}

			Object parentObject = null;
			TreeItem parentItem = item.getParentItem();
			if (parentItem != null) {
				parentObject = parentItem.getData();
			} else {
				parentObject = ShimModelManager.getCurrentScd();
			}

			String className = currObject.getClass().getSimpleName();
			String fieldNameAtParent = StringUtils.decapitalize(className);

			Map<String, List<Object>> sameFieldObjectMap = sameParentObjectMap
					.get(parentObject);
			if (sameFieldObjectMap == null) {
				sameFieldObjectMap = new HashMap<String, List<Object>>();
				sameParentObjectMap.put(parentObject, sameFieldObjectMap);
			}

			List<Object> list = sameFieldObjectMap.get(fieldNameAtParent);
			if (list == null) {
				list = new ArrayList<Object>();
				sameFieldObjectMap.put(fieldNameAtParent, list);
			}

			list.add(currObject);
		}

		// 2nd loop: check whether may delete target, and add target to list.
		outer_loop: for (Entry<Object, Map<String, List<Object>>> sameParentEntry : sameParentObjectMap
				.entrySet()) {

			Object parentObject = sameParentEntry.getKey();
			Map<String, List<Object>> sameFieldObjectMap = sameParentEntry
					.getValue();

			for (Entry<String, List<Object>> sameFieldObjectEntry : sameFieldObjectMap
					.entrySet()) {
				String fieldNameAtParent = sameFieldObjectEntry.getKey();
				List<Object> selectedSameFieldObjectList = sameFieldObjectEntry
						.getValue();

				Object fieldObject = ShimModelAdapter.getValObject(
						parentObject, fieldNameAtParent);

				boolean required = ShimModelAdapter.isRequired(parentObject,
						fieldNameAtParent);

				if (fieldObject instanceof List) {
					List<?> currentList = ((ArrayList<?>) fieldObject);
					if (required
							&& selectedSameFieldObjectList.size() == currentList
									.size()) {
						isDeletable = false;
						break outer_loop;
					}

					// If the class of parent's field is List.class,
					// set 'fieldObject' to ParentAndTarget.parent.
					for (Object targetObj : selectedSameFieldObjectList) {
						targetList.add(new ParentAndTarget(currentList,
								targetObj));
					}

				} else {
					if (required) {
						isDeletable = false;
						break outer_loop;
					} else {
						for (Object targetObj : selectedSameFieldObjectList) {
							targetList.add(new ParentAndTarget(parentObject,
									targetObj));
						}
					}
				}

			}
		}
		return isDeletable;
	}

}
