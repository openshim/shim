/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.multicore_association.shim.edit.gui.jface.SHIMEditJFaceApplicationWindow;
import org.multicore_association.shim.edit.gui.swt.viewer.ShimTreeViewerUtil;
import org.multicore_association.shim.edit.gui.swt.viewer.ShimTreeViewerUtil.ParentAndTarget;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.ShimModelManager;

/**
 * Deletes items that is selected in TreeViewer.
 */
public class DeleteSelectedItemAction extends Action {

	private static final Logger log = ShimLoggerUtil
			.getLogger(DeleteSelectedItemAction.class);

	private TreeViewer viewer;
	private List<ParentAndTarget> targetList = new ArrayList<ParentAndTarget>();

	/**
	 * Constructs a new instance of DeleteSelectedItemAction.
	 * 
	 * @param viewer
	 *            the tree viewer with this menu
	 */
	public DeleteSelectedItemAction(TreeViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		return "Delete Item@Delete";
	}

	/**
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		targetList.clear();

		boolean isDeletable = ShimTreeViewerUtil.isDeletable(viewer.getTree()
				.getSelection(), targetList);

		return isDeletable;
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		log.finest("[Action] Delete Item start");

		Object topObject = viewer.getTree().getData();

		log.finest("topObject class=" + topObject.getClass().getName());

		for (ParentAndTarget pair : targetList) {
			deleteSingleObject(pair.getParentObject(), pair.getTargetObject(),
					topObject);
		}

		viewer.refresh();

		if (SHIMEditJFaceApplicationWindow.window != null) {
			SHIMEditJFaceApplicationWindow.window
					.setInputToEachCurrentInputPannel();
		}

		super.run();
	}

	/**
	 * Deletes the single SHIM object.
	 * 
	 * @param parentObject
	 *            targetObject's parent
	 * @param targetObject
	 *            object to delete
	 * @param topObject
	 *            top object of tree
	 */
	private void deleteSingleObject(Object parentObject, Object targetObject,
			Object topObject) {
		ShimModelManager.removeObject(parentObject, targetObject, topObject);

		if (parentObject instanceof List) {
			ArrayList<?> list = ((ArrayList<?>) parentObject);
			list.remove(targetObject);
		}
	}
}
