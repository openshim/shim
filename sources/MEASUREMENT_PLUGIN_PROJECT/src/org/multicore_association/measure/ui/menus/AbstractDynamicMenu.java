/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.ui.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;
import org.multicore_association.measure.core.Activator;
import org.multicore_association.measure.core.commands.LaunchCommand;
import org.multicore_association.measure.core.launch.ConfIDs;

/**
 * Launch configuration list abstract class.
 */
public abstract class AbstractDynamicMenu extends CompoundContributionItem implements IWorkbenchContribution {

	private static final String CONF_TYPE_ID = "org.multicore_association.measure.ui.launchConfigurationType";

	private static final String LABEL_NEW = "New...";
	private static final String LABEL_NONEPROJ = "(Project is not selected)";

	private int menuType = 4;
	private String menuId = null;

	public AbstractDynamicMenu() {
	}

	public AbstractDynamicMenu(final String id) {
		super(id);
	}

	public void setMenuType(int menuType) {
		this.menuType = menuType;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	@Override
	public void initialize(IServiceLocator serviceLocator) {
	}

	/**
	 * Behavior control of eclipse version 3.6 or earlier.
	 *
	 * To suppress the phenomenon in which the same launch
	 * configuration list of content is output in the respective lists.
	 */
	@Override
	public void fill(Menu menu, int index) {
		MenuItem mi = menu.getParentItem();
		Object obj = mi.getData();
		if (obj instanceof MenuManager) {
			MenuManager mm = (MenuManager)obj;
			if (mm.getId().equals(menuId)) {
				super.fill(menu, index);
			}
		}
	}

	/**
	 * returns a list of items of process types in the project.
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		ArrayList<IContributionItem> list = new ArrayList<IContributionItem>();

		ArrayList<ILaunchConfiguration> menuList = new ArrayList<ILaunchConfiguration>();
		IStringVariableManager svm = VariablesPlugin.getDefault().getStringVariableManager();
		String name = "";
		try {
			name = svm.performStringSubstitution("${project_name}");
		} catch (CoreException e) {
		}

		if (name.isEmpty()) {
			IAction a = new Action(LABEL_NONEPROJ) {
			};
			a.setEnabled(false);
			IContributionItem item = new ActionContributionItem(a);
			list.add(item);
		} else {
			ILaunchConfiguration[] conf = null;
			try {
				ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager()
						.getLaunchConfigurationType(CONF_TYPE_ID);
				conf = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(type);
			} catch (CoreException e1) {
				e1.printStackTrace();
			}

			for (int i = 0; i < conf.length; i++) {
				String pluginId = "";
				String projName = "";
				int procType = 4;

				try {
					pluginId = conf[i].getType().getPluginIdentifier();
					projName = conf[i].getAttribute(ConfIDs.KEY_CMN_PROJECT, "");
					procType = conf[i].getAttribute(ConfIDs.KEY_CMN_SELECT_MODE, 4);
				} catch (CoreException e) {
					continue;
				}

				if (!Activator.PLUGIN_ID.equals(pluginId)) {
					continue;
				}
				if (!name.isEmpty() && !name.equals(projName)) {
					continue;
				}
				if (menuType != procType) {
					continue;
				}

				menuList.add(conf[i]);
			}

			if (!menuList.isEmpty()) {
				for (int i = 0; i < menuList.size(); i++) {
					ILaunchConfiguration c = menuList.get(i);
					Map<String, String> params = new HashMap<String, String>();

					params.put(LaunchCommand.CMD_PARAM_ID_NAME, c.getName());
					params.put(LaunchCommand.CMD_PARAM_ID_TYPE, String.valueOf(menuType));
					try {
						params.put(LaunchCommand.CMD_PARAM_ID_MEMENTO, c.getMemento());
					} catch (CoreException e) {
					}

					CommandContributionItemParameter cciParam;
					cciParam = new CommandContributionItemParameter(
							PlatformUI.getWorkbench(), null, LaunchCommand.CMD_ID,
							CommandContributionItem.STYLE_PUSH);
					cciParam.parameters = params;
					cciParam.visibleEnabled = false;
					cciParam.label = c.getName();

					CommandContributionItem item;
					item = new CommandContributionItem(cciParam);
					item.setVisible(true);
					list.add(item);
				}
			} else {
				Map<String, String> params = new HashMap<String, String>();

				params.put(LaunchCommand.CMD_PARAM_ID_NAME, LABEL_NEW);
				params.put(LaunchCommand.CMD_PARAM_ID_TYPE, String.valueOf(menuType));
				params.put(LaunchCommand.CMD_PARAM_ID_MEMENTO, "");

				CommandContributionItemParameter cciParam;
				cciParam = new CommandContributionItemParameter(
						PlatformUI.getWorkbench(), null, LaunchCommand.CMD_ID,
						CommandContributionItem.STYLE_PUSH);
				cciParam.parameters = params;
				cciParam.visibleEnabled = false;
				cciParam.label = LABEL_NEW;

				CommandContributionItem item;
				item = new CommandContributionItem(cciParam);
				item.setVisible(true);
				list.add(item);
			}
		}

		return (IContributionItem[])list.toArray(new IContributionItem[list.size()]);
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

}
