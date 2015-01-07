/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.multicore_association.shim.edit.model.ShimModelManager;

/**
 * SHIM Editor Clipboard to copy and paste items.<br>
 * While every wizard runs, the effective range of the clipboard becomes only the page of the wizard.
 */
public class MyClipboard {
	
	
	/**
	 * SHIM Editor Clipboard to copy and paste items.
	 */
	private static class ClipboardImpl {
		private boolean multiSelectFlg = false;

		private Object clip;

		private List<Object> clips;

		/**
		 * Copies single object.
		 * 
		 * @param object
		 *            the object to copy
		 */
		void Copy(Object object) {
			multiSelectFlg = false;
			clip = ShimModelManager.makeClone(object);
		}

		/**
		 * Copies multiple objects.
		 * 
		 * @param selist
		 *            the object list to copy
		 */
		void Copy(List<Object> selist) {
			multiSelectFlg = true;
			clips = new ArrayList<Object>();
			for (Object o : selist) {
				clips.add(ShimModelManager.makeClone(o));
			}
		}

		/**
		 * Returns items of the clipboard.
		 * 
		 * @return items of the clipboard
		 */
		Object Paste() {
			if (multiSelectFlg) {
				List<Object> dclips = new ArrayList<Object>();
				for (Object o : clips) {
					dclips.add(ShimModelManager.makeClone(o));
				}
				return dclips;

			} else {
				return ShimModelManager.makeClone(clip);
			}
		}

		/**
		 * Whether clipboard is empty or not
		 * 
		 * @return Returns true if there is some paste item, and false otherwise.
		 */
		boolean canPaste() {
			return clip != null || (clips != null && !clips.isEmpty());
		}
	}
	
	private static final ClipboardImpl windowClipboard = new ClipboardImpl();

	private static ClipboardImpl dialogClipboard;
	private static IWizardPage callFrom;

	/**
	 * Copies single object.
	 * 
	 * @param object
	 *            the object to copy
	 */
	public static void Copy(Object object) {
		IWizardPage currentCallFrom = ControlUtil.getCurrentWizardPage();
		if (currentCallFrom != null) {
			if (callFrom != null && currentCallFrom.equals(callFrom)) {
				dialogClipboard.Copy(object);
				
			} else {
				dialogClipboard = new ClipboardImpl();
				dialogClipboard.Copy(object);
				callFrom = currentCallFrom;
			}
		} else {
			windowClipboard.Copy(object);
			dialogClipboard = null;
			callFrom = null;
		}
	}

	/**
	 * Copies multiple objects.
	 * 
	 * @param selist
	 *            the object list to copy
	 */
	public static void Copy(List<Object> selist) {
		IWizardPage currentCallFrom = ControlUtil.getCurrentWizardPage();
		if (currentCallFrom != null) {
			if (callFrom != null && currentCallFrom.equals(callFrom)) {
				dialogClipboard.Copy(selist);
				
			} else {
				dialogClipboard = new ClipboardImpl();
				dialogClipboard.Copy(selist);
				callFrom = currentCallFrom;
			}
		} else {
			windowClipboard.Copy(selist);
			dialogClipboard = null;
			callFrom = null;
		}
	}

	/**
	 * Returns items of the clipboard.
	 * 
	 * @return items of the clipboard
	 */
	public static Object Paste() {
		IWizardPage currentCallFrom = ControlUtil.getCurrentWizardPage();
		if (currentCallFrom != null) {
			if (callFrom != null && currentCallFrom.equals(callFrom)) {
				return dialogClipboard.Paste();
			}
			return null;
			
		} else {
			return windowClipboard.Paste();
		}
	}

	/**
	 * Whether clipboard is empty or not
	 * 
	 * @return Returns true if there is some paste item, and false otherwise.
	 */
	public static boolean canPaste() {
		IWizardPage currentCallFrom = ControlUtil.getCurrentWizardPage();
		if (currentCallFrom != null) {
			if (callFrom != null && currentCallFrom.equals(callFrom)) {
				return dialogClipboard.canPaste();
			}
			return false;
			
		} else {
			return windowClipboard.canPaste();
		}
	}
}
