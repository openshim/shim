/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.viewer;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.multicore_association.shim.edit.model.ShimObject;

/**
 * A ShimObjectTableContentProvider for use with a ShimObjectTableViewer, which
 * uses the Shim_Object to obtain the elements of a tree.
 */
public class ShimObjectTableContentProvider implements
		IStructuredContentProvider {

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// NOOP
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// NOOP
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		@SuppressWarnings("unchecked")
		List<ShimObject> sa = ((List<ShimObject>) inputElement);
		return sa.toArray();
	}

}
