/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.ui.menus;

import org.eclipse.ui.services.IServiceLocator;

/**
 * Launch configuration menu list class for InstructionCycle.
 */
public class InstructionCycleDynamicMenu extends AbstractDynamicMenu {

	private static final String CONF_TYPE_ID = "org.multicore_association.measure.ui.dynamicmenu.cycle"; //$NON-NLS-1$
	private static final String CONF_MENU_ID = "menu:org.multicore_association.measure.core.toolbar.pulldown.cycle"; //$NON-NLS-1$

	public InstructionCycleDynamicMenu() {
		this(CONF_TYPE_ID);
	}

	public InstructionCycleDynamicMenu(final String id) {
		super(id);
	}

	@Override
	public void initialize(IServiceLocator serviceLocator) {
		setMenuType(1);
		setMenuId(CONF_MENU_ID);
	}

}
