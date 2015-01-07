/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.viewer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TreeItem;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.gui.jface.SHIMEditJFaceApplicationWindow;
import org.multicore_association.shim.edit.gui.swt.viewer.ShimTreeViewerUtil.ParentAndTarget;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;

import com.sun.xml.internal.ws.util.StringUtils;

/**
 * An implementation of Drag and Drop.
 */
public class ShimObjectDragAndDropListener implements DragSourceListener,
		DropTargetListener {

	private static final Logger log = ShimLoggerUtil
			.getLogger(ShimObjectDragAndDropListener.class);

	private TreeViewer viewer;

	private List<ParentAndTarget> targetList = new ArrayList<ParentAndTarget>();

	private String parentClassName;

	/**
	 * Constructs a new instance of ShimObjectDragAndDropListener.
	 * 
	 * @param viewer
	 *            the table viewer
	 */
	public ShimObjectDragAndDropListener(TreeViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragFinished(DragSourceEvent event) {
		// NOOP
	}

	/**
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragSetData(DragSourceEvent event) {
		event.data = ShimObjectDragAndDropListener.class.getSimpleName();
	}

	/**
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragStart(DragSourceEvent event) {
		targetList.clear();
		parentClassName = null;

		TreeItem[] selection = viewer.getTree().getSelection();
		boolean doit = false;

		boolean selectSameParentItems = ShimTreeViewerUtil
				.selectSameParentItems(selection);
		if (selectSameParentItems) {
			this.parentClassName = ShimTreeViewerUtil.getClassName(selection[0]
					.getParentItem());
			doit = ShimTreeViewerUtil.isDeletable(viewer.getTree()
					.getSelection(), targetList);
		}

		event.doit = doit;
	}

	/**
	 * @see org.eclipse.swt.dnd.DropTargetListener#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	@Override
	public void dragEnter(DropTargetEvent event) {
		event.operations = DND.DROP_MOVE;
	}

	/**
	 * @see org.eclipse.swt.dnd.DropTargetListener#dragLeave(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	@Override
	public void dragLeave(DropTargetEvent event) {
		// NOOP
	}

	/**
	 * @see org.eclipse.swt.dnd.DropTargetListener#dragOperationChanged(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	@Override
	public void dragOperationChanged(DropTargetEvent event) {
		// NOOP
	}

	/**
	 * @see org.eclipse.swt.dnd.DropTargetListener#dragOver(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	@Override
	public void dragOver(DropTargetEvent event) {
		// NOOP
	}

	/**
	 * @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void drop(DropTargetEvent event) {
		if (event.operations != DND.DROP_MOVE) {
			return;
		}

		log.finest("start moving tree item(s). ");

		Object newParentObj = event.item.getData();

		for (ParentAndTarget pair : targetList) {
			Object oldParentObject = pair.getParentObject();
			Object moveTarget = pair.getTargetObject();
			Class<? extends Object> targetClaz = moveTarget.getClass();
			String valueName = StringUtils.decapitalize(targetClaz
					.getSimpleName());

			Object childObject = ShimModelAdapter.getValObject(newParentObj,
					valueName);

			log.info("Move: item=" + ShimLoggerUtil.getNameProperty(moveTarget)
					+ ", to=" + ShimLoggerUtil.getNameProperty(newParentObj));

			if (childObject instanceof List) {
				List<?> oldList = ((List<?>) oldParentObject);
				oldList.remove(moveTarget);

				List<Object> newList = (List<Object>) childObject;
				newList.add(moveTarget);

			} else {

				int open = SWT.YES;

				// In case of overwrite, confirms whether moves target or not.
				if (childObject != null) {
					MessageBox box = new MessageBox(
							viewer.getTree().getShell(), SWT.YES | SWT.NO);
					box.setText(CommonConstants.QUESTION_DIALOG_TITLE);
					box.setMessage(String.format(
							CommonConstants.MESSAGE_OVERWRITE_ELEMENT,
							ShimLoggerUtil.getElementName(targetClaz)));
					open = box.open();
				}

				if (open == SWT.YES) {
					ShimModelAdapter.setNull(oldParentObject, valueName);
					ShimModelAdapter.setValObject(newParentObj, valueName,
							moveTarget);
				} else {
					log.info("Move: cancel the move action; item="
							+ ShimLoggerUtil.getNameProperty(moveTarget)
							+ ", to="
							+ ShimLoggerUtil.getNameProperty(newParentObj));
				}
			}
		}
		
		viewer.refresh();

		if (SHIMEditJFaceApplicationWindow.window != null) {
			SHIMEditJFaceApplicationWindow.window.refresh();
			SHIMEditJFaceApplicationWindow.window
					.setInputToEachCurrentInputPannel();
		}
	}

	/**
	 * @see org.eclipse.swt.dnd.DropTargetListener#dropAccept(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	@Override
	public void dropAccept(DropTargetEvent event) {
		if (parentClassName != null && event.item instanceof TreeItem) {
			TreeItem item = (TreeItem) event.item;
			if (parentClassName.equals(ShimTreeViewerUtil.getClassName(item))) {
				return;
			}
		}

		event.operations = DND.DROP_NONE;
	}
}
