/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.viewer;

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.multicore_association.shim.api.AbstractCommunication;
import org.multicore_association.shim.api.CommunicationSet;
import org.multicore_association.shim.api.Connection;
import org.multicore_association.shim.api.ConnectionSet;
import org.multicore_association.shim.api.Latency;
import org.multicore_association.shim.api.Performance;
import org.multicore_association.shim.api.PerformanceSet;
import org.multicore_association.shim.api.Pitch;

/**
 * A LabelProvider implementation for CommunicationSet.
 */
public class CommunicationSetTreeLabelProvider extends LabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@SuppressWarnings("unused")
	@Override
	public String getText(Object element) {
		if(element instanceof CommunicationSet) {
			CommunicationSet comset = (CommunicationSet)element;
			String str = String.format("[CommSet]"); 
			return str;
			
		}
		if(element instanceof AbstractCommunication) {
			AbstractCommunication as = (AbstractCommunication)element;
			String str = String.format("[Comm] %s", as.getName()); 
			return str;
		}
		if(element instanceof Connection) {
			Connection con = (Connection)element;
			String str = String.format("[Connection]");
			return str;
		}
		if(element instanceof PerformanceSet) {
			PerformanceSet pset = (PerformanceSet)element;
			List<Performance> p = pset.getPerformance();
			
			return "PerformanceSet";
		}
		if( element instanceof Performance) {
			
			Performance pf = (Performance)element;
			return "Performance";
		}
		if( element instanceof Latency) {
			return "Latency";
		}
		if( element instanceof Pitch) {
			return "Pitch";
		}
		if( element instanceof ConnectionSet) {
			return "ConnectionSet";
		}
		
		if(element instanceof String) {
			return (String)element;
		}
		return "-";
	}

}
