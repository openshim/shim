/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.multicore_association.shim.api.Accessor;
import org.multicore_association.shim.api.AddressSpace;
import org.multicore_association.shim.api.AddressSpaceSet;
import org.multicore_association.shim.api.MasterSlaveBinding;
import org.multicore_association.shim.api.MasterSlaveBindingSet;
import org.multicore_association.shim.api.MemoryConsistencyModel;
import org.multicore_association.shim.api.Performance;
import org.multicore_association.shim.api.PerformanceSet;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.edit.model.ShimModelManager;

/**
 * An AddressSpaceTreeItemProvider for use with a ShimAddressSpaceTreeViewer,
 * which uses the AddressSpaceSet to obtain the elements of a tree.
 */
public class AddressSpaceTreeItemProvider implements ITreeContentProvider {

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
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof AddressSpaceSet) {
			AddressSpaceSet cset = (AddressSpaceSet) parentElement;
			List<AddressSpace> csetList = cset.getAddressSpace();
			return csetList.toArray();
		}

		if (parentElement instanceof AddressSpace) {
			AddressSpace as = (AddressSpace) parentElement;
			List<SubSpace> sslist = as.getSubSpace();
			return sslist.toArray();
		}
		if (parentElement instanceof SubSpace) {
			SubSpace ss = (SubSpace) parentElement;

			ArrayList<Object> alist = new ArrayList<Object>();

			List<MemoryConsistencyModel> mcm = ss.getMemoryConsistencyModel();
			if (mcm != null) {
				alist.addAll(mcm);
			}

			MasterSlaveBindingSet msset = ss.getMasterSlaveBindingSet();
			if (msset != null) {
				alist.add(msset);
			}

			return alist.toArray();
		}
		if (parentElement instanceof MasterSlaveBindingSet) {
			MasterSlaveBindingSet msbs = (MasterSlaveBindingSet) parentElement;
			List<MasterSlaveBinding> mslist = msbs.getMasterSlaveBinding();
			return mslist.toArray();

		}
		if (parentElement instanceof MasterSlaveBinding) {
			MasterSlaveBinding msb = (MasterSlaveBinding) parentElement;
			return msb.getAccessor().toArray();

		}
		if (parentElement instanceof PerformanceSet) {
			PerformanceSet pfset = (PerformanceSet) parentElement;
			ArrayList<Object> alist = new ArrayList<Object>();
			alist.addAll(pfset.getPerformance());

			return alist.toArray();
		}
		if (parentElement instanceof Performance) {
			Performance pf = (Performance) parentElement;
			ArrayList<Object> alist = new ArrayList<Object>();
			alist.add(pf.getLatency());
			alist.add(pf.getPitch());

			return alist.toArray();
		}
		if (parentElement instanceof Accessor) {
			Accessor accessor = (Accessor) parentElement;
			ArrayList alist = new ArrayList();

			alist.addAll(accessor.getPerformanceSet());

			return alist.toArray();
		}

		if (parentElement instanceof List) {

			ArrayList alist = new ArrayList();
			alist.addAll((List) parentElement);

			return alist.toArray();
		}

		return new Object[0];
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object[] getElements(Object inputElement) {

		if (ShimModelManager.getModelList().size() == 0) {
			Object[] root = new String[] { "NoData2" };
			return root;
		}

		if (inputElement instanceof List) {
			return ((List) inputElement).toArray();

		} else if (inputElement instanceof AddressSpaceSet) {
			AddressSpaceSet cs1 = (AddressSpaceSet) inputElement;
			ArrayList<Object> children = new ArrayList<Object>();

			children.addAll(cs1.getAddressSpace());
			return children.toArray();
		}

		return new Object[0];

	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

}
