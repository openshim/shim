/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.viewer;

import org.eclipse.jface.viewers.LabelProvider;
import org.multicore_association.shim.api.Accessor;
import org.multicore_association.shim.api.AddressSpace;
import org.multicore_association.shim.api.AddressSpaceSet;
import org.multicore_association.shim.api.Latency;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.MasterSlaveBinding;
import org.multicore_association.shim.api.MasterSlaveBindingSet;
import org.multicore_association.shim.api.MemoryConsistencyModel;
import org.multicore_association.shim.api.Performance;
import org.multicore_association.shim.api.PerformanceSet;
import org.multicore_association.shim.api.Pitch;
import org.multicore_association.shim.api.SubSpace;

/**
 * A LabelProvider implementation for AddressSpaceSet.
 */
public class AddressSpaceTreeLabelProvider extends LabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@SuppressWarnings("unused")
	@Override
	public String getText(Object element) {
		if(element instanceof AddressSpaceSet) {
			AddressSpaceSet as = (AddressSpaceSet)element;
			String str = String.format("[AddressSpaceSet]");
			return str;
		}
		if(element instanceof AddressSpace) {
			AddressSpace as = (AddressSpace)element;
			String str = String.format("[AS] %s", as.getName());
			return str;
		}
		if(element instanceof SubSpace) {
			SubSpace ss = (SubSpace)element;
			String str = String.format("[SS] %s", ss.getName());
			return str;
		}
		if(element instanceof MemoryConsistencyModel) {
			MemoryConsistencyModel ss = (MemoryConsistencyModel)element;
			String str = String.format("[MemoryConsistencyModel]");
			return str;
		}
		if(element instanceof MasterSlaveBindingSet) {
			MasterSlaveBindingSet ss = (MasterSlaveBindingSet)element;
			String str = String.format("[MasterSlaveBindingSet]");
			return str;
		}
		if(element instanceof MasterSlaveBinding) {
			MasterSlaveBinding ss = (MasterSlaveBinding)element;
			String str = String.format("[MasterSlaveBinding]");
			return str;
		}
		if(element instanceof Accessor) {
			Accessor acc = (Accessor)element;
			MasterComponent mc = (MasterComponent) acc.getMasterComponentRef();
			String str = String.format("[Accessor] %s", mc.getName());
			
			return str;
		}
		if(element instanceof PerformanceSet) {
			PerformanceSet pfset = (PerformanceSet)element;
			String str = String.format("[PerformanceSet] ");
			return str;
		}
		if(element instanceof Performance) {
			String str = String.format("[Performance] ");
			return str;
		}
		if(element instanceof Latency) {
			String str = String.format("[Latency] ");
			return str;
		}
		if(element instanceof Pitch) {
			String str = String.format("[Pitch] ");
			return str;
		}
		if(element instanceof String) {
			return (String)element;
		}
		
		return "-";
	}

}
