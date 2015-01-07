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
import org.multicore_association.shim.api.AbstractCommunication;
import org.multicore_association.shim.api.CommunicationSet;
import org.multicore_association.shim.api.Connection;
import org.multicore_association.shim.api.ConnectionSet;
import org.multicore_association.shim.api.EventCommunication;
import org.multicore_association.shim.api.FIFOCommunication;
import org.multicore_association.shim.api.InterruptCommunication;
import org.multicore_association.shim.api.Latency;
import org.multicore_association.shim.api.Performance;
import org.multicore_association.shim.api.PerformanceSet;
import org.multicore_association.shim.api.Pitch;
import org.multicore_association.shim.api.SharedMemoryCommunication;
import org.multicore_association.shim.api.SharedRegisterCommunication;
import org.multicore_association.shim.edit.model.ShimModelManager;

/**
 * A CommunicationSetTreeItemProvider for use with a ShimCommunicationTreeViewer
 * on a wizard page, which uses the CommunicationSet to obtain the elements of a
 * tree.
 */
public class CommunicationSetTreeItemProvider implements ITreeContentProvider {

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

		if (parentElement instanceof CommunicationSet) {
			CommunicationSet cset = (CommunicationSet) parentElement;

			List<AbstractCommunication> comList = this.getCommucationList(cset);

			return comList.toArray(new AbstractCommunication[comList.size()]);
		}

		if (parentElement instanceof AbstractCommunication) {
			AbstractCommunication com = (AbstractCommunication) parentElement;
			ConnectionSet conSet = com.getConnectionSet();
			if (conSet == null) {
				return new ConnectionSet[0];
			} else {
				return new ConnectionSet[] { conSet };
			}
		}

		if (parentElement instanceof ConnectionSet) {
			ConnectionSet conset = (ConnectionSet) parentElement;
			List<Connection> conlist = conset.getConnection();
			return conlist.toArray(new Connection[conlist.size()]);
		}
		
		if (parentElement instanceof PerformanceSet) {
			PerformanceSet pfset = (PerformanceSet) parentElement;
			List<Performance> pflist = pfset.getPerformance();
			return pflist.toArray(new Performance[pflist.size()]);
		}

		if (parentElement instanceof Performance) {
			Performance pf = (Performance) parentElement;
			return new Object[] { pf.getLatency(), pf.getPitch() };
		}

		if (parentElement instanceof Connection) {
			Connection con = (Connection) parentElement;
			List<Performance> pflist = con.getPerformance();
			return pflist.toArray(new Performance[pflist.size()]);

		}
		
		if (parentElement instanceof Latency) {
			Latency lat = (Latency) parentElement;
			return new Latency[] { lat };
		}
		
		if (parentElement instanceof Pitch) {
			Pitch pch = (Pitch) parentElement;
			return new Pitch[] { pch };
		}

		return new Object[0];
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	@Override
	public Object[] getElements(Object inputElement) {

		if (ShimModelManager.getModelList().size() == 0) {
			Object[] root = new String[] { "NoData2" };
			return root;
		}

		if (inputElement instanceof List) {
			return ((List) inputElement).toArray();

		} else if (inputElement instanceof CommunicationSet) {
			CommunicationSet cs1 = (CommunicationSet) inputElement;
			List<AbstractCommunication> children = this.getCommucationList(cs1);
			return children.toArray();

		} else if (inputElement instanceof ConnectionSet) {
			ConnectionSet conset = (ConnectionSet) inputElement;
			List<Connection> conlist = conset.getConnection();
			return conlist.toArray();

		} else if (inputElement instanceof EventCommunication) {
			EventCommunication comset = (EventCommunication) inputElement;

		} else if (inputElement instanceof Connection) {
			Connection con = (Connection) inputElement;
			List<Performance> pset = con.getPerformance();

			return pset.toArray();

		} else if (inputElement instanceof Performance) {
			Performance pf = (Performance) inputElement;
			ArrayList<Object> alist = new ArrayList<Object>();
			alist.add(pf.getLatency());
			alist.add(pf.getPitch());
			alist.add(pf.getAccessTypeRef());

			return alist.toArray();
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
	@SuppressWarnings("unused")
	@Override
	public boolean hasChildren(Object element) {

		if (element instanceof CommunicationSet) {
			CommunicationSet cset = (CommunicationSet) element;
			List<AbstractCommunication> comlist = getCommucationList(cset);
			if (comlist.size() > 0) {
				return true;
			}
		}
		if (element instanceof ConnectionSet) {
			ConnectionSet conset = (ConnectionSet) element;
			List<Connection> conlist = conset.getConnection();
			if (conlist.size() > 0) {
				return true;
			}
		}
		if (element instanceof AbstractCommunication) {
			return getChildren(element).length > 0;
		}
		if (element instanceof Connection) {
			return getChildren(element).length > 0;
		}
		if (element instanceof PerformanceSet) {
			PerformanceSet pfset = (PerformanceSet) element;
			return getChildren(element).length > 0;
		}
		if (element instanceof Performance) {
			Performance pf = (Performance) element;
			return getChildren(element).length > 0;
		}

		return false;
	}

	/**
	 * Returns the list of AbstractCommunication which is contained in the
	 * specified CommunicationSet.
	 * 
	 * @param comset
	 *            the CommunicationSet which contains AbstractCommunication
	 * @return the list of AbstractCommunication which is contained in the
	 *         specified CommunicationSet
	 */
	public List<AbstractCommunication> getCommucationList(
			CommunicationSet comset) {

		List<EventCommunication> evntList = comset.getEventCommunication();
		List<FIFOCommunication> fifoList = comset.getFIFOCommunication();

		List<InterruptCommunication> intrList = comset
				.getInterruptCommunication();
		List<SharedMemoryCommunication> llscList = comset
				.getSharedMemoryCommunication();
		List<SharedRegisterCommunication> sregList = comset
				.getSharedRegisterCommunication();

		List<AbstractCommunication> comList = new ArrayList<AbstractCommunication>();
		comList.addAll(evntList);
		comList.addAll(fifoList);

		comList.addAll(intrList);
		comList.addAll(llscList);
		comList.addAll(sregList);

		return comList;
	}

}
