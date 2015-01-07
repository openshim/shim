/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.actions;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Display;
import org.multicore_association.shim.edit.gui.jface.preferences.AccessTypePreferencePage;
import org.multicore_association.shim.edit.gui.jface.preferences.AddressSpacePreferencePage;
import org.multicore_association.shim.edit.gui.jface.preferences.CachePreferencePage;
import org.multicore_association.shim.edit.gui.jface.preferences.CommunicationSetPreferencePage;
import org.multicore_association.shim.edit.gui.jface.preferences.ComponentSetPreferencePage;
import org.multicore_association.shim.edit.gui.jface.preferences.ComponentsPreferencePage;
import org.multicore_association.shim.edit.gui.jface.preferences.LatencyPreferencePage;
import org.multicore_association.shim.edit.gui.jface.preferences.MasterComponentPreferencePage;
import org.multicore_association.shim.edit.gui.jface.preferences.PerformancePreferencePage;
import org.multicore_association.shim.edit.gui.jface.preferences.PitchPreferencePage;
import org.multicore_association.shim.edit.gui.jface.preferences.SlaveComponentPreferencePage;
import org.multicore_association.shim.edit.gui.jface.preferences.WizardInputPreferencePage;

/**
 * Starts the preference wizard.
 */
public class PreferencesAction extends Action {

	/**
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		return "Preferences";
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		// Set pages to PreferenceManager.
		final PreferenceManager pm = new PreferenceManager();

		final ComponentsPreferencePage page1 = new ComponentsPreferencePage();
		PreferenceNode pnode1 = new PreferenceNode(page1.getTitle());
		pnode1.setPage(page1);
		pm.addToRoot(pnode1);

		MasterComponentPreferencePage page2 = new MasterComponentPreferencePage();
		PreferenceNode pnode2 = new PreferenceNode(page2.getTitle());
		pnode2.setPage(page2);
		pm.addTo(page1.getTitle(), pnode2);

		SlaveComponentPreferencePage page3 = new SlaveComponentPreferencePage();
		PreferenceNode pnode3 = new PreferenceNode(page3.getTitle());
		pnode3.setPage(page3);
		pm.addTo(page1.getTitle(), pnode3);

		ComponentSetPreferencePage page4 = new ComponentSetPreferencePage();
		PreferenceNode pnode4 = new PreferenceNode(page4.getTitle());
		pnode4.setPage(page4);
		pm.addTo(page1.getTitle(), pnode4);
		
		AddressSpacePreferencePage page5 = new AddressSpacePreferencePage();
		PreferenceNode pnode5 = new PreferenceNode(page5.getTitle());
		pnode5.setPage(page5);
		pm.addToRoot(pnode5);
		
		CommunicationSetPreferencePage page6 = new CommunicationSetPreferencePage();
		PreferenceNode pnode6 = new PreferenceNode(page6.getTitle());
		pnode6.setPage(page6);
		pm.addToRoot(pnode6);
		
		CachePreferencePage page7 = new CachePreferencePage();
		PreferenceNode pnode7 = new PreferenceNode(page7.getTitle());
		pnode7.setPage(page7);
		pm.addToRoot(pnode7);
		
		AccessTypePreferencePage page8 = new AccessTypePreferencePage();
		PreferenceNode pnode8 = new PreferenceNode(page8.getTitle());
		pnode8.setPage(page8);
		pm.addToRoot(pnode8);
		
		PerformancePreferencePage page9 = new PerformancePreferencePage();
		PreferenceNode pnode9 = new PreferenceNode(page9.getTitle());
		pnode9.setPage(page9);
		pm.addToRoot(pnode9);
		
		LatencyPreferencePage page10 = new LatencyPreferencePage();
		PreferenceNode pnode10= new PreferenceNode(page10.getTitle());
		pnode10.setPage(page10);
		pm.addTo(page9.getTitle(), pnode10);

		PitchPreferencePage page11 = new PitchPreferencePage();
		PreferenceNode pnode11 = new PreferenceNode(page11.getTitle());
		pnode11.setPage(page11);
		pm.addTo(page9.getTitle(), pnode11);
		
		WizardInputPreferencePage page00 = new WizardInputPreferencePage();
		PreferenceNode pnode00 = new PreferenceNode(page00.getTitle());
		pnode00.setPage(page00);
		pm.addToRoot(pnode00);

		// starts the wizard.
		final Display display = Display.getDefault();

		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {

			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				final PreferenceDialog dialog = new PreferenceDialog(display
						.getActiveShell(), pm);
				dialog.setSelectedNode(page1.getTitle());
				dialog.setMinimumPageSize(640, 480);
				
				// if you want to expand PreferenceDialog's TreeViewer, 
				// call 'create' method.
				dialog.create();
				dialog.getTreeViewer().expandAll();
				
				dialog.open();
			}
		});
	}
}
