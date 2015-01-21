/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.core.prefs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.multicore_association.measure.core.Activator;

public class PrefsInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		initializeMainPreferences(store);
	}

	private void initializeMainPreferences(IPreferenceStore store) {
		store.setDefault(MainPrefsAccessor.KEY_LAUNCH_HISTORY, "");
	}
}
