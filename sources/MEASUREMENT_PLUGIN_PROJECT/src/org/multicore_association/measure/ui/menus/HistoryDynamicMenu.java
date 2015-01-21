/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.ui.menus;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;
import org.multicore_association.measure.core.commands.RelaunchHistoryCommand;
import org.multicore_association.measure.core.prefs.MainPrefsAccessor;

/**
 * Launch configuration menu class that started just before.
 */
public class HistoryDynamicMenu extends CompoundContributionItem implements IWorkbenchContribution{

	private static final String LABEL_HISTORY = "(no launch history)";

	@Override
	public void initialize(IServiceLocator serviceLocator) {
	}

	/**
	 * Get the history from the preferences, and returns the menu item.
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		String history = MainPrefsAccessor.getLaunchHistory();
		ILaunchConfiguration conf = null;
		CommandContributionItemParameter cciParam;
		IContributionItem item;

		if (history != null && !history.isEmpty()) {
			try {
				conf = DebugPlugin.getDefault().getLaunchManager().getLaunchConfiguration(history);
			} catch (CoreException e) {
				MainPrefsAccessor.setLaunchHistory("");
				e.printStackTrace();
				return null;
			}
		}

		Map<String, String> params = new HashMap<String, String>();
		String memento = "";
		if (conf != null) {
			try {
				memento = conf.getMemento();
			} catch (CoreException e1) {
			}
		}

		if (conf != null && conf.exists()) {
			params.put(RelaunchHistoryCommand.CMD_PARAM_ID_MEMENTO, memento);
			cciParam = new CommandContributionItemParameter(PlatformUI.getWorkbench(), null,
					RelaunchHistoryCommand.CMD_ID, CommandContributionItem.STYLE_PUSH);
			cciParam.parameters = params;
			cciParam.label = conf.getName();
			cciParam.visibleEnabled = true;

			item = new CommandContributionItem(cciParam);
			item.setVisible(true);
		} else {
			IAction a = new Action(LABEL_HISTORY){};
			a.setEnabled(false);
			item = new ActionContributionItem(a);
		}

		return new IContributionItem[] { item, };
	}
}
