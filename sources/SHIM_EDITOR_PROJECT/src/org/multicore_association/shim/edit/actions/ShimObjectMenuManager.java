/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimModelManager;

import com.sun.xml.internal.ws.util.StringUtils;

/**
 * This is the action bar contributor for the SHIM editor.
 */
public class ShimObjectMenuManager extends MenuManager {

	private TreeViewer viewer = null;
	private TreeItem componentSelectedItem = null;

	/**
	 * Constructs a new instance of ShimObjectMenuManager.
	 * 
	 * @param tv
	 *            the tree viewer with this menu
	 */
	public ShimObjectMenuManager(TreeViewer tv) {
		this.viewer = tv;

		Menu menu = this.createContextMenu(viewer.getControl());

		viewer.getControl().setMenu(menu);

		this.setRemoveAllWhenShown(true);

		this.addMenuListener(new IMenuListener() {

			/**
			 * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
			 */
			@Override
			public void menuAboutToShow(IMenuManager manager) {

				componentSelectedItem = null;

				if (viewer.getSelection() instanceof IStructuredSelection) {

					TreeItem[] selections = viewer.getTree().getSelection();
					if (selections.length > 0) {
						componentSelectedItem = selections[0];
					}

					// Getting parent.
					Object parentObject;
					if (componentSelectedItem == null) {
						parentObject = null;
						
					} else {
						TreeItem parentItem = componentSelectedItem
								.getParentItem();
						if (parentItem == null) {
							parentObject = ShimModelManager.getCurrentScd();
						} else {
							parentObject = parentItem.getData();
						}
					}

					// Getting current node.
					Object node;
					if (componentSelectedItem == null) {
						node = ShimModelManager.getCurrentScd();
					} else {
						node = componentSelectedItem.getData();
					}
					Class<?> cls = node.getClass();
					String className = cls.getSimpleName();
					String fieldNameAtParent = StringUtils
							.decapitalize(className);
					Object currentObject;
					if (parentObject == null) {
						currentObject = node;
					} else {
						currentObject = ShimModelAdapter.getValObject(
								parentObject, fieldNameAtParent);
					}

					// Create menu.
					if (selections.length == 1) {
						manager = createAddMenu(manager, node, className);
						manager.add(new Separator());
					}

					manager = createDeleteMenu(manager, parentObject,
							fieldNameAtParent, currentObject);

					if (componentSelectedItem != null) {
						manager.add(new CutSelectedItemAction(viewer));
						manager.add(new CopySelectedItemAction(viewer));
					}

					if (MyClipboard.canPaste()) {
						manager.add(new PasteChildAction(viewer));
					}
				}
			}
		});
	}

	/**
	 * Create Add Child Element menu according to the element.
	 * 
	 * @param manager
	 *            MenuManager
	 * @param node
	 *            the element node that add a child element to.
	 * @param className
	 *            element node's class name
	 * @return MenuManager
	 */
	private IMenuManager createAddMenu(IMenuManager manager, Object node,
			String className) {

		ArrayList<Class<?>> children = ShimModelAdapter.getChildren(node);

		for (Class<?> child : children) {
			Object newChildObject = ShimModelAdapter.getNewInstance(child);
			if (newChildObject != null) {
				AddChildAction act_add = new AddChildAction(viewer, node, child);

				if (ShimModelAdapter.hasSiblings(newChildObject, className)) {
					manager.add(act_add);

				} else {
					String fieldNameAtNode = StringUtils.decapitalize(child
							.getSimpleName());
					Object childObjct = ShimModelAdapter.getValObject(node,
							fieldNameAtNode);

					if (ShimModelAdapter.isRequired(node, fieldNameAtNode)) {
						// Already has set. Don't show menu.
					} else {
						boolean enabled = false;
						if (childObjct == null) {
							enabled = true;
						} else if (childObjct.equals(newChildObject)) {
							enabled = true;
						}
						act_add.setEnabled(enabled);
						manager.add(act_add);
					}
				}
			}
		}
		return manager;
	}

	/**
	 * Create Delete Items menu.
	 * 
	 * @param manager
	 *            MenuManager
	 * @param parentObject
	 *            the parent object of the selected object.
	 * @param fieldNameAtParent
	 *            field name of the selected element on the parent object
	 * @param currentObject
	 *            current selected object
	 * @return MenuManager
	 */
	private IMenuManager createDeleteMenu(IMenuManager manager,
			Object parentObject, String fieldNameAtParent, Object currentObject) {

		boolean isRequired = false;
		if (parentObject == null) {
			isRequired = true;
		} else {
			isRequired = ShimModelAdapter.isRequired(parentObject,
					fieldNameAtParent);
		}

		if (currentObject instanceof List) {
			DeleteSelectedItemAction act_del = new DeleteSelectedItemAction(
					viewer);
			manager.add(act_del);

		} else {
			if (!isRequired) {
				DeleteSelectedItemAction act_del = new DeleteSelectedItemAction(
						viewer);
				manager.add(act_del);
			}
		}
		return manager;
	}
}
