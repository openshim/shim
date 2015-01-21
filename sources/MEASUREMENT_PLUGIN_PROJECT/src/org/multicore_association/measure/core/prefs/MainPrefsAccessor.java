/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.measure.core.prefs;

import org.eclipse.jface.preference.IPreferenceStore;
import org.multicore_association.measure.core.Activator;

public class MainPrefsAccessor {
	public static final String KEY_LAUNCH_HISTORY = "LAUNCH_HISTORY";

	public static String getLaunchHistory() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getString(KEY_LAUNCH_HISTORY);
	}

	public static void setLaunchHistory(String history) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(KEY_LAUNCH_HISTORY, history);
	}
}
