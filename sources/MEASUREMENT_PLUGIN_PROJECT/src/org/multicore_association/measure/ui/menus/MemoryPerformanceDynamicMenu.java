/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.ui.menus;

import org.eclipse.ui.services.IServiceLocator;

/**
 * Launch configuration menu list class for MemoryPerformance.
 */
public class MemoryPerformanceDynamicMenu extends AbstractDynamicMenu {

	private static final String CONF_TYPE_ID = "org.multicore_association.measure.ui.dynamicmenu.mempfrm"; //$NON-NLS-1$
	private static final String CONF_MENU_ID = "menu:org.multicore_association.measure.core.toolbar.pulldown.mem"; //$NON-NLS-1$

	public MemoryPerformanceDynamicMenu() {
		this(CONF_TYPE_ID);
	}

	public MemoryPerformanceDynamicMenu(final String id) {
		super(id);
	}

	@Override
	public void initialize(IServiceLocator serviceLocator) {
		setMenuType(0);
		setMenuId(CONF_MENU_ID);
	}
}
