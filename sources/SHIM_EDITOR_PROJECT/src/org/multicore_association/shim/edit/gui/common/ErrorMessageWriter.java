/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.common;

/**
 * This is the interface for the parts of GUI. 
 * The Interface writes error message on the wizard or panel.
 */
public interface ErrorMessageWriter {

	
	/**
	 * Write error message.
	 * 
	 * @param value the message, or null to clear the error message.
	 */
	public void writeErrorMessage(String value);

}
