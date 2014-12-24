/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.viewer;

import org.eclipse.jface.viewers.LabelProvider;
import org.multicore_association.shim.api.AccessType;
import org.multicore_association.shim.api.AccessTypeSet;
import org.multicore_association.shim.api.Cache;
import org.multicore_association.shim.api.ClockFrequency;
import org.multicore_association.shim.api.CommonInstructionSet;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.Instruction;
import org.multicore_association.shim.api.Latency;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.Performance;
import org.multicore_association.shim.api.Pitch;
import org.multicore_association.shim.api.RWType;
import org.multicore_association.shim.api.SlaveComponent;
import org.multicore_association.shim.api.SystemConfiguration;

/**
 * A LabelProvider implementation for Components.
 */
@SuppressWarnings("unused")
public class ComponentTreeLabelProvider extends LabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {

		String name = "[Nodata!!]";

		if (element instanceof SystemConfiguration) {
			SystemConfiguration sys = (SystemConfiguration) element;
			name = sys.getName();
		}
		if (element instanceof ComponentSet) {
			ComponentSet cs = (ComponentSet) element;
			name = "[CS] " + cs.getName();
		} else if (element instanceof MasterComponent) {
			MasterComponent mc = (MasterComponent) element;
			name = "[MC] " + mc.getName();
		} else if (element instanceof SlaveComponent) {
			SlaveComponent sc = (SlaveComponent) element;
			name = "[SC] " + sc.getName();
		} else if (element instanceof Cache) {
			Cache c = (Cache) element;
			name = c.getName();
		} else if (element instanceof AccessTypeSet) {
			AccessTypeSet atset = (AccessTypeSet) element;
			name = "AccessTypeSet";
		} else if (element instanceof AccessType) {
			AccessType at = (AccessType) element;
			Integer accessByteSize = at.getAccessByteSize();
			RWType rwType = at.getRwType();
			
			if (rwType == null && accessByteSize == null) {
				name = at.getName() + "[]";
			} else if (rwType == null && accessByteSize != null) {
				name = at.getName() + "[" + accessByteSize + "]";
			} else if (rwType != null && accessByteSize == null) {
				name = at.getName() + "[" + rwType + "]";
			} else {
				name = at.getName() + "[" + rwType + "_" + accessByteSize + "]";
			}

		} else if (element instanceof CommonInstructionSet) {
			CommonInstructionSet cset = (CommonInstructionSet) element;
			name = "[CommonInstructionSet]" + cset.getName();
		} else if (element instanceof Instruction) {
			Instruction inst = (Instruction) element;
			name = inst.getName();
		} else if (element instanceof Performance) {
			Performance pf = (Performance) element;
			name = "Performance";
		} else if (element instanceof Latency) {
			Latency latency = (Latency) element;
			name = "Latency";
		} else if (element instanceof Pitch) {
			Pitch pitch = (Pitch) element;
			name = "Pitch";
		} else if (element instanceof ClockFrequency) {
			ClockFrequency clockFrequency = (ClockFrequency) element;
			name = "ClockFrequency";
		}

		return name;
	}
}
