/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.actions;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.Connection;
import org.multicore_association.shim.api.ConnectionSet;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.MasterSlaveBinding;
import org.multicore_association.shim.api.MasterSlaveBindingSet;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.gui.common.NameAttributeChecker;
import org.multicore_association.shim.edit.gui.jface.SHIMEditJFaceApplicationWindow;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimModelManager;
import org.multicore_association.shim.edit.model.data.DefaultDataStore;

/**
 * Inserts a child element.
 */
public class AddChildAction extends Action {

	private static final Logger log = ShimLoggerUtil
			.getLogger(AddChildAction.class);

	private TreeViewer viewer;
	private Object current;
	private Class<?> child;
	private String text;

	/**
	 * Constructs a new instance of AddChildAction.
	 * 
	 * @param viewer
	 *            the tree viewer with this menu
	 * @param current
	 *            the selected object in the specified tree viewer.
	 * @param child
	 *            the class of the selected object's field
	 */
	public AddChildAction(TreeViewer viewer, Object current, Class<?> child) {
		this.viewer = viewer;
		this.current = current;
		this.child = child;
		this.setText(child.getSimpleName());
	}

	/**
	 * @see org.eclipse.jface.action.Action#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		return "Add Child " + text;
	}

	/**
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		ComponentSet componentSet = ShimModelManager.getCurrentScd()
				.getComponentSet();

		if (ConnectionSet.class.equals(child) || Connection.class.equals(child)) {
			int countMasterComponent = ShimModelManager
					.countMasterComponent(componentSet);
			if (countMasterComponent < 2) {
				return false;
			}

		} else if (MasterSlaveBindingSet.class.equals(child)
				|| MasterSlaveBinding.class.equals(child)) {
			int countMasterComponent = ShimModelManager
					.countMasterComponent(componentSet);
			int countSlaveComponent = ShimModelManager
					.countSlaveComponent(componentSet);
			if (countMasterComponent == 0 || countSlaveComponent == 0) {
				return false;
			}
		}

		return viewer.getTree().getSelectionCount() == 1 && super.isEnabled();
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		log.finest("[Action] Add_Child Object starts.");

		Object newObject = DefaultDataStore.getDefaultSet(child);

		// The name attribute should be unique.
		String newName = "";
		if (ShimModelAdapter.hasNameAttribute(newObject)) {
			NameAttributeChecker checker = new NameAttributeChecker(child,
					current);
			String name = (String) ShimModelAdapter.getValObject(newObject,
					CommonConstants.FIELD_NAME);
			if (!name.endsWith(DefaultDataStore.CHILD_SUB_NAME)
					&& !name.endsWith(DefaultDataStore.CHILD_SUB_NAME
							.substring(0, 1))) {
				name = name + DefaultDataStore.CHILD_SUB_NAME;
			}
			for (int i = 1; i < 1000; i++) {
				String suffix = String.format("%d", i);
				String oldChar = DefaultDataStore.CHILD_SUB_NAME.substring(1);
				newName = name.replace(oldChar, suffix);
				if (!checker.isExist(newName)) {
					break;
				}
			}
			ShimModelAdapter.setValObject(newObject,
					CommonConstants.FIELD_NAME, newName);
			checker.updateNameList();
		}

		ArrayList<Class<?>> children = ShimModelAdapter.getChildren(newObject);

		log.finest("children.size=" + children.size());
		for (Object child : children) {
			log.finest(" + child class name="
					+ child.getClass().getCanonicalName());
		}

		// If newObject is MasterComponent, set CommonInstructionSet in this
		// method.
		if (newObject instanceof MasterComponent) {
			ShimModelManager
					.setCommonInstructionSetToSingleMasterComponent((MasterComponent) newObject);
		}

		if (newObject != null) {
			DefaultDataStore.setDefaultSet(current, child, newObject);

			log.info("Add Child: element="
					+ ShimLoggerUtil.getElementName(newObject.getClass()));

			// When the target node is the top of tree.
			Object topObject = viewer.getTree().getData();
			ArrayList<?> treeList = ((ArrayList<?>) topObject);

			// This 'if block' may be dead code.
			if (treeList == null) {
				treeList = new ArrayList<Object>();
			}

			if (treeList.size() == 0) {
				ArrayList<Object> _list = new ArrayList<Object>();
				_list.add(newObject);
				treeList = _list;
				viewer.setInput(treeList);
			}
			viewer.refresh();
		}

		if (SHIMEditJFaceApplicationWindow.window != null) {
			SHIMEditJFaceApplicationWindow.window
					.setInputToEachCurrentInputPannel();
		}

		super.run();
	}
}
