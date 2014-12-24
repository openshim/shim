/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface;

import org.eclipse.swt.widgets.Composite;
import org.multicore_association.shim.edit.actions.ShimObjectMenuManager;

/**
 * An implementation of SearchableTreeViewer for CommunicationSet.
 */
public class ShimCommunicationTreeViewer extends SearchableTreeViewer {

	private ShimCommunicationTreeViewer shimCommunicationTreeViewer;

	/**
	 * Constructs a new instance of ShimCommunicationTreeViewer.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	public ShimCommunicationTreeViewer(Composite parent) {
		this(parent, true);
	}

	/**
	 * Constructs a new instance of ShimCommunicationTreeViewer.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param createMenu
	 *            whether creates menu or not
	 */
	public ShimCommunicationTreeViewer(Composite parent, boolean createMenu) {
		super(parent);
		shimCommunicationTreeViewer = this;
		
		if (createMenu) {
			new ShimObjectMenuManager(shimCommunicationTreeViewer);
		}
	}
}
