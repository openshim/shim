/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlType;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.gui.jface.SHIMEditJFaceApplicationWindow;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimModelManager;

import com.sun.xml.internal.ws.util.StringUtils;

/**
 * Pastes items as a child element.
 */
public class PasteChildAction extends Action {

	private static final Logger log = ShimLoggerUtil
			.getLogger(PasteChildAction.class);

	private static final String ERROR_MSG_HEADER = "Schema error: ";

	private TreeViewer viewer;

	private Shell shell;

	/**
	 * Constructs a new instance of PasteChildAction.
	 * 
	 * @param viewer
	 *            the tree viewer with this menu
	 */
	public PasteChildAction(TreeViewer viewer) {
		this.viewer = viewer;
		this.shell = viewer.getControl().getShell();
	}

	/**
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		return "Paste Item@Ctrl+V";
	}

	/**
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return MyClipboard.canPaste()
				&& viewer.getTree().getSelectionCount() == 1;
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		log.finest("[Action] Paste Item start");

		Object obj = MyClipboard.Paste();
		if (obj instanceof List<?>) {
			List<Object> objlist = (List<Object>) obj;
			log.finest("Multi Paste select count=" + objlist.size());
		} else {
			log.finest("Single Paste select obj=" + obj.getClass().getName());
		}

		// Select Paste parent Node
		TreeItem[] selections = viewer.getTree().getSelection();
		if (selections.length == 0) {
			return;
		}
		TreeItem pasteParentNodeItem = selections[0];
		if (pasteParentNodeItem == null) {
			log.finest("* select nothing *");
			return;
		}
		log.log(Level.FINEST,
				"paste Parent Node Item=" + pasteParentNodeItem.getText());

		Object selobj = MyClipboard.Paste();
		int open = SWT.YES;

		if (!(selobj instanceof List<?>)) {

			Object _data = selobj;

			log.finest("Paste Item =" + MyClipboard.Paste());

			Object pasteParentObject = pasteParentNodeItem.getData();
			ArrayList<Class<?>> children = ShimModelAdapter
					.getChildren(pasteParentObject);
			boolean canPaste = false;
			boolean canOverwrite = false;
			for (Class<?> child : children) {
				if (!_data.getClass().equals(child)) {
					continue;
				}

				Object newChildObject = ShimModelAdapter.getNewInstance(child);

				if (newChildObject == null) {
					continue;
				}

				String valueName = StringUtils.decapitalize(child
						.getSimpleName());
				Object childObject = ShimModelAdapter.getValObject(
						pasteParentObject, valueName);

				if (childObject instanceof List) {
					List<Object> list = (List<Object>) childObject;
					list.add(_data);
					canPaste = true;
					break;

				} else if (childObject != null) {

					MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO);
					box.setText(CommonConstants.QUESTION_DIALOG_TITLE);
					box.setMessage(String.format(
							CommonConstants.MESSAGE_OVERWRITE_ELEMENT,
							ShimLoggerUtil.getElementName(child)));
					open = box.open();

					if (open == SWT.YES) {
						Object topObject = viewer.getTree().getData();
						ShimModelManager.removeObject(pasteParentObject,
								childObject, topObject, true);

						// Action: over write
						ShimModelAdapter.setValObject(pasteParentObject,
								valueName, _data);
						canOverwrite = true;

					} else {
						log.info("Paste: Overwrite is canceled. item="
								+ ShimLoggerUtil.getNameProperty(_data));
					}
					break;
				} else {
					ShimModelAdapter.setValObject(pasteParentObject, valueName,
							_data);
					canPaste = true;
					break;
				}
			}

			if (canPaste || canOverwrite) {
				log.info("Paste: item=" + ShimLoggerUtil.getNameProperty(_data));
			}

			if (canPaste) {
				log.finest("Paste:Add.");
			} else if (canOverwrite) {
				log.finest("Paste:Overwrite.");
			} else if (open != SWT.NO) {
				// if can not paste, open ErrorDialog.
				StringBuilder msg = new StringBuilder(ERROR_MSG_HEADER);
				try {
					XmlType xmlType = _data.getClass().getAnnotation(
							XmlType.class);
					XmlType pXmlType = pasteParentObject.getClass()
							.getAnnotation(XmlType.class);
					if (xmlType != null) {
						msg.append("CopyElement=");
						msg.append(xmlType.name());
					}
					if (pXmlType != null) {
						msg.append(", PasteTargetElement=");
						msg.append(pXmlType.name());
					}
				} catch (Exception e) {
				}
				log.warning("Can not Paste this item. : " + msg.toString());

				ErrorDialog.openError(viewer.getControl().getShell(),
						"Warning", "Can not Paste this item.",
						new Status(IStatus.WARNING, this.getClass().getName(),
								msg.toString()));
			}

		} else {
			// ----------------------------------------------
			// Multi-select Paste

			List<Object> _dataList = (List<Object>) selobj;

			Object pasteParentObject = pasteParentNodeItem.getData();

			List<Object> pasteFailedData = new ArrayList<Object>();

			for (Object _data : _dataList) {

				log.finest("Paste Item =" + MyClipboard.Paste());

				ArrayList<Class<?>> children = ShimModelAdapter
						.getChildren(pasteParentObject);
				boolean canPaste = false;
				boolean canOverwrite = false;
				for (Class<?> child : children) {
					if (!_data.getClass().equals(child)) {
						continue;
					}

					Object newChildObject = ShimModelAdapter
							.getNewInstance(child);

					if (newChildObject == null) {
						continue;
					}

					String valueName = StringUtils.decapitalize(child
							.getSimpleName());
					Object childObject = ShimModelAdapter.getValObject(
							pasteParentObject, valueName);

					if (childObject instanceof List) {
						List<Object> list = (List<Object>) childObject;
						list.add(_data);
						canPaste = true;
						break;
					} else if (childObject != null) {

						MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO);
						box.setText(CommonConstants.QUESTION_DIALOG_TITLE);
						box.setMessage(String.format(
								CommonConstants.MESSAGE_OVERWRITE_ELEMENT,
								ShimLoggerUtil.getElementName(child)));
						open = box.open();

						if (open == SWT.YES) {
							Object topObject = viewer.getTree().getData();
							ShimModelManager.removeObject(pasteParentObject,
									childObject, topObject, true);

							// Action: over write
							ShimModelAdapter.setValObject(pasteParentObject,
									valueName, _data);
							canOverwrite = true;

							log.log(Level.FINEST,
									"Paste Object CanOverwite = true");
						} else {
							log.info("Paste: Overwrite is canceled. item="
									+ ShimLoggerUtil.getNameProperty(_data));
						}
						break;
					} else {
						ShimModelAdapter.setValObject(pasteParentObject,
								valueName, _data);
						canPaste = true;
						break;
					}
				}

				if (canPaste || canOverwrite) {
					log.info("Paste: item="
							+ ShimLoggerUtil.getNameProperty(_data));
				}

				if (canPaste) {
					log.finest("Paste:Add.");
				} else if (canOverwrite) {
					log.finest("Paste:Overwrite.");
				} else if (open != SWT.NO) {
					log.finest("Can not Paste this item.");
					pasteFailedData.add(_data);
				}
			}

			// if can not paste, open ErrorDialog.
			if (!pasteFailedData.isEmpty()) {
				StringBuilder msg = new StringBuilder(ERROR_MSG_HEADER);
				for (Object failedData : pasteFailedData) {
					try {
						StringBuilder newLine = new StringBuilder();
						XmlType xmlType = failedData.getClass().getAnnotation(
								XmlType.class);
						XmlType pXmlType = pasteParentObject.getClass()
								.getAnnotation(XmlType.class);
						if (xmlType != null) {
							newLine.append("CopyElement=");
							newLine.append(xmlType.name());
						}
						if (pXmlType != null) {
							newLine.append(", PasteTargetElement=");
							newLine.append(pXmlType.name());
						}
						if (newLine.length() > 0) {
							newLine.append("\n");
							msg.append(newLine);
						}
					} catch (Exception e) {
					}
				}
				ErrorDialog.openError(viewer.getControl().getShell(),
						"Warning", "Can not Paste this item.",
						new Status(IStatus.WARNING, this.getClass().getName(),
								msg.toString()));
				log.warning("Can not Paste this item. : " + msg.toString());
			}
		}

		viewer.refresh();

		pasteParentNodeItem = null;

		if (SHIMEditJFaceApplicationWindow.window != null) {
			SHIMEditJFaceApplicationWindow.window
					.setInputToEachCurrentInputPannel();
		}

		super.run();
	}
}
