/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.multicore_association.shim.api.AccessTypeSet;
import org.multicore_association.shim.api.Cache;
import org.multicore_association.shim.api.CommonInstructionSet;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.Instruction;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.Performance;
import org.multicore_association.shim.edit.model.ShimModelManager;

/**
 * A ComponentTreeItemProvider for use with a ShimComponentTreeViewer, which
 * uses the ComponentSet to obtain the elements of a tree.
 */
public class ComponentTreeItemProvider implements ITreeContentProvider {

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
	@Override
	public Object[] getChildren(Object parentElement) {
		ArrayList<Object> children = new ArrayList<Object>();

		if (parentElement instanceof ComponentSet) {

			ComponentSet cset = (ComponentSet) parentElement;
			children.addAll(cset.getCache());

			children.addAll(cset.getComponentSet());
			children.addAll(cset.getMasterComponent());
			children.addAll(cset.getSlaveComponent());

			return children.toArray();
		}

		if (parentElement instanceof MasterComponent) {
			MasterComponent mc = (MasterComponent) parentElement;
			children.addAll(mc.getCache());
			children.add(mc.getAccessTypeSet());
			CommonInstructionSet commonInstructionSet = mc
					.getCommonInstructionSet();
			if (commonInstructionSet != null) {
				children.add(commonInstructionSet);
			}
			if (mc.getClockFrequency() != null) {
				children.add(mc.getClockFrequency());
			}

			return children.toArray();
		}

		if (parentElement instanceof AccessTypeSet) {
			AccessTypeSet atset = (AccessTypeSet) parentElement;
			children.addAll(atset.getAccessType());
			return children.toArray();
		}

		if (parentElement instanceof CommonInstructionSet) {
			CommonInstructionSet ciset = (CommonInstructionSet) parentElement;
			children.addAll(ciset.getInstruction());
			return children.toArray();
		}

		if (parentElement instanceof Instruction) {
			Instruction instruction = (Instruction) parentElement;

			if (instruction.getPerformance() != null) {
				children.add(instruction.getPerformance());
			}
			return children.toArray();
		}
		if (parentElement instanceof Performance) {
			Performance pf = (Performance) parentElement;

			if (pf.getLatency() != null) {
				children.add(pf.getLatency());
			}
			if (pf.getPitch() != null) {
				children.add(pf.getPitch());
			}
			return children.toArray();
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

		} else if (inputElement instanceof ComponentSet) {
			ComponentSet cs1 = (ComponentSet) inputElement;
			ArrayList<Object> children = new ArrayList<Object>();
			children.addAll(cs1.getComponentSet());
			children.addAll(cs1.getMasterComponent());
			children.addAll(cs1.getSlaveComponent());
			children.addAll(cs1.getCache());
			return children.toArray();

		} else if (inputElement instanceof MasterComponent) {
			MasterComponent mc = (MasterComponent) inputElement;
			ArrayList<Object> children = new ArrayList<Object>();
			children.addAll(mc.getCache());
			return children.toArray();

		} else if (inputElement instanceof Performance) {

			Performance pf = (Performance) inputElement;
			ArrayList<Object> children = new ArrayList<Object>();

			if (pf.getLatency() != null) {
				children.add(pf.getLatency());
			}
			if (pf.getPitch() != null) {
				children.add(pf.getPitch());
			}

			return children.toArray();

		} else if (inputElement instanceof AccessTypeSet) {
			AccessTypeSet atset = (AccessTypeSet) inputElement;
			ArrayList<Object> children = new ArrayList<Object>();
			children.addAll(atset.getAccessType());
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
		if (element instanceof ComponentSet) {
			ComponentSet cset = (ComponentSet) element;
			int ecount = 0;
			if (cset.getMasterComponent().size() > 0) {
				ecount++;
			}
			if (cset.getSlaveComponent().size() > 0) {
				ecount++;
			}
			if (cset.getComponentSet().size() > 0) {
				ecount++;
			}
			if (cset.getCache().size() > 0) {
				ecount++;
			}
			if (ecount > 0) {
				return true;
			}
		}
		if (element instanceof MasterComponent) {
			MasterComponent mc = (MasterComponent) element;
			if (mc.getCache().size() > 0) {
				return true;
			}
			if (mc.getAccessTypeSet() != null) {
				return true;
			}
		}
		if (element instanceof AccessTypeSet) {
			AccessTypeSet atset = (AccessTypeSet) element;
			if (atset.getAccessType().size() > 0) {
				return true;
			}
		}
		if (element instanceof CommonInstructionSet) {
			CommonInstructionSet ciset = (CommonInstructionSet) element;
			if (ciset.getInstruction().size() > 0) {
				return true;
			}
		}
		if (element instanceof Instruction) {
			Instruction instruction = (Instruction) element;

			if (instruction.getPerformance() != null) {
				return true;
			}
		}
		if (element instanceof Performance) {
			Performance pf = (Performance) element;
			if (pf.getLatency() != null) {
				return true;
			}
			if (pf.getPitch() != null) {
				return true;
			}
		}
		if (element instanceof Cache) {
			Cache cache = (Cache) element;
			if (cache.getCacheRef().size() > 0) {
				return true;
			}
		}

		return false;
	}

}
