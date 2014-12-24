/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.CommunicationSet;
import org.multicore_association.shim.api.EventCommunication;
import org.multicore_association.shim.api.FIFOCommunication;
import org.multicore_association.shim.api.InterruptCommunication;
import org.multicore_association.shim.api.SharedMemoryCommunication;
import org.multicore_association.shim.api.SharedRegisterCommunication;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for CommunicationSet.
 */
public class CommunicationSetInputPanel extends InputPanelBase {
	private CommunicationSet communicationSet = new CommunicationSet();
	private Text textEvent;
	private Text textInterrupt;
	private Text textFifo;
	private Text textSharedMemory;
	private Text textSharedRegister;

	/**
	 * Constructs a new instance of CommunicationSetInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public CommunicationSetInputPanel(Composite parent, int style) {
		super(parent, style);

		setLblTitleText("Communication Set");
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblInterruptCommunication = new Label(composite, SWT.NONE);
		GridData gd_lblInterruptCommunication = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblInterruptCommunication.widthHint = 280;
		lblInterruptCommunication.setLayoutData(gd_lblInterruptCommunication);
		lblInterruptCommunication.setText("Number of Interrupt Communication");
		
		textInterrupt = new Text(composite, SWT.BORDER);
		GridData gd_textInterrupt = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_textInterrupt.widthHint = 60;
		textInterrupt.setLayoutData(gd_textInterrupt);
		textInterrupt.setEditable(false);
		
		Label lblEventCommunication = new Label(composite, SWT.NONE);
		lblEventCommunication.setText("Number of Event Communication");
		
		textEvent = new Text(composite, SWT.BORDER);
		GridData gd_textevent = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_textevent.widthHint = 60;
		textEvent.setLayoutData(gd_textevent);
		textEvent.setEditable(false);
		
		Label lblFifoCommunication = new Label(composite, SWT.NONE);
		lblFifoCommunication.setText("Number of FIFO Communication");
		
		textFifo = new Text(composite, SWT.BORDER);
		GridData gd_textFifo = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_textFifo.widthHint = 60;
		textFifo.setLayoutData(gd_textFifo);
		textFifo.setEditable(false);
		
		Label lblSharedMemoryCommunication = new Label(composite, SWT.NONE);
		lblSharedMemoryCommunication.setText("Number of Shared Memory Communication");
		
		textSharedMemory = new Text(composite, SWT.BORDER);
		GridData gd_textSharedMemory = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_textSharedMemory.widthHint = 60;
		textSharedMemory.setLayoutData(gd_textSharedMemory);
		textSharedMemory.setEditable(false);
		
		Label lblNumberOfShared = new Label(composite, SWT.NONE);
		lblNumberOfShared.setText("Number of Shared Register Communication");
		
		textSharedRegister = new Text(composite, SWT.BORDER);
		GridData gd_textSharedRegister = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_textSharedRegister.widthHint = 60;
		textSharedRegister.setLayoutData(gd_textSharedRegister);
		textSharedRegister.setEditable(false);
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		assert (communicationSet != null);
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		if (input != null) {
			communicationSet = (CommunicationSet) input;

			List<InterruptCommunication> interruptCommunication = communicationSet.getInterruptCommunication();
			int numInterrupt = interruptCommunication == null ? 0 : interruptCommunication.size();
			textInterrupt.setText(Integer.toString(numInterrupt));
			
			List<EventCommunication> eventCommunication = communicationSet.getEventCommunication();
			int numEvent = eventCommunication == null ? 0 : eventCommunication.size();
			textEvent.setText(Integer.toString(numEvent));
			
			List<FIFOCommunication> fifoCommunication = communicationSet.getFIFOCommunication();
			int numFifo = fifoCommunication == null ? 0 : fifoCommunication.size();
			textFifo.setText(Integer.toString(numFifo));
			
			List<SharedMemoryCommunication> sharedMemoryCommunication = communicationSet.getSharedMemoryCommunication();
			int numSharedMemory = sharedMemoryCommunication == null ? 0 : sharedMemoryCommunication.size();
			textSharedMemory.setText(Integer.toString(numSharedMemory));
			
			List<SharedRegisterCommunication> sharedRegisterCommunication = communicationSet.getSharedRegisterCommunication();
			int numSharedRegister = sharedRegisterCommunication == null ? 0 : sharedRegisterCommunication.size();
			textSharedRegister.setText(Integer.toString(numSharedRegister));
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return communicationSet;
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return communicationSet.getClass();
	}

}
