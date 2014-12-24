/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.actions;

import java.util.ArrayList;
import java.util.List;

import org.multicore_association.shim.edit.model.ShimModelManager;

/**
 * SHIM Editor Clipboard to copy and paste items.
 */
public class MyClipboard {
	private static boolean multiSelectFlg = false;

	private static Object clip;

	private static List<Object> clips;

	/**
	 * Copies single object.
	 * 
	 * @param object
	 *            the object to copy
	 */
	public static void Copy(Object object) {
		multiSelectFlg = false;
		clip = ShimModelManager.makeClone(object);
	}

	/**
	 * Copies multiple objects.
	 * 
	 * @param selist
	 *            the object list to copy
	 */
	public static void Copy(List<Object> selist) {
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
	public static Object Paste() {
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
	public static boolean canPaste() {
		return clip != null || (clips != null && !clips.isEmpty());
	}
}
