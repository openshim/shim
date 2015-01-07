/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.multicore_association.shim.api.AbstractCommunication;
import org.multicore_association.shim.api.CommunicationSet;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.Connection;
import org.multicore_association.shim.api.ConnectionSet;
import org.multicore_association.shim.api.EventCommunication;
import org.multicore_association.shim.api.FIFOCommunication;
import org.multicore_association.shim.api.InterruptCommunication;
import org.multicore_association.shim.api.Latency;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.Performance;
import org.multicore_association.shim.api.Pitch;
import org.multicore_association.shim.api.SharedMemoryCommunication;
import org.multicore_association.shim.api.SharedRegisterCommunication;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.gui.jface.SHIMEditJFaceApplicationWindow;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.EnumUtil;
import org.multicore_association.shim.edit.model.ShimModelManager;
import org.multicore_association.shim.edit.model.ShimPreferences;
import org.multicore_association.shim.edit.model.data.DefaultDataStore;
import org.multicore_association.shim.edit.model.preferences.SavePolicyPreferences;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesStore;

/**
 * A wizard to generate CommunicationSet again.
 */
public class RemakeCommunicationsWizard extends Wizard {

	private static final Logger log = ShimLoggerUtil
			.getLogger(RemakeCommunicationsWizard.class);

	TreeViewer communicationTreeViewer = null;

	private CommunicationSetWizardPage communicationSetWz = null;

	public ComponentSet rootComponent = null;

	private Object previousSelectedPage;

	private int x = CommonConstants.WIZARD_DEFAULT_X;
	private int y = CommonConstants.WIZARD_DEFAULT_Y;

	/**
	 * Constructs a new instance of RemakeCommunicationsWizard.
	 */
	public RemakeCommunicationsWizard() {
		setWindowTitle("Re-Make CommunicationSet Wizard");
		ShimPreferences.getNewCopyInstance();

		log.info(getWindowTitle() + " open");
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		communicationSetWz = new CommunicationSetWizardPage(true);
		addPage(communicationSetWz);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		getShell().setBounds(x, y, CommonConstants.WIZARD_WIDTH,
				CommonConstants.WIZARD_HEIGHT);
	}

	/**
	 * Sets the receiver's location to the rectangular area specified by the
	 * arguments.
	 * 
	 * @param x
	 *            the x coordinate of the rectangle
	 * @param y
	 *            the y coordinate of the rectangle
	 */
	public void setLayout(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {

		final SystemConfiguration sys = ShimModelManager.getCurrentScd();
		final ShimPreferences preferences = ShimPreferences
				.getCurrentInstance();
		log.info(ShimPreferences.createDumpStr(preferences, true));

		try {
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(
					getShell());
			progressDialog.run(false, false, new IRunnableWithProgress() {
				/**
				 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
				 */
				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					remakeShimXml(sys, preferences, monitor);
				}

			});
		} catch (InvocationTargetException | InterruptedException e) {
			log.log(Level.SEVERE, "Create New SHIM XML Error.", e);
		}

		// Saves preferences.
		SavePolicyPreferences wizParameter = preferences
				.getSavePolicyPreferences();
		boolean requestToSave = false;
		if (wizParameter.isSaveOnRequest()) {
			MessageBox request = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.OK | SWT.CANCEL);
			request.setMessage("Save preferences you input in the wizard.");
			int result = request.open();
			requestToSave = (result == SWT.OK);
		}
		if (requestToSave || wizParameter.isAlwaysSave()) {
			ShimPreferencesStore store = ShimPreferencesStore.getInstance();
			store.setAccessTypePreferences(preferences
					.getAccessTypePreferences());
			store.setAddressSpacePreferences(preferences
					.getAddressSpacePreferences());
			store.setCacheDataPreferences(preferences.getCacheDataPreferences());
			store.setCommunicationSetPreferences(preferences
					.getCommunicationSetPreferences());

			store.setComponentsPreferences(preferences
					.getComponentsPreferences());
			store.setMasterComponentPreferences(preferences
					.getMasterComponentPreferences());
			store.setSlaveComponentPreferences(preferences
					.getSlaveComponentPreferences());
			store.setComponentSetPreferences(preferences
					.getComponentSetPreferences());

			store.save();
		}

		return true;
	}

	private void remakeShimXml(SystemConfiguration sys,
			ShimPreferences preferences, IProgressMonitor monitor) {

		// ----------------------------------------------------------------
		// CommunicationSet
		CommunicationSet communicationSet = remakeCommunicationSet(sys,
				preferences, monitor);
		remakeConnectionPerformances(sys, communicationSet, monitor);

		List<CommunicationSet> comsetlist = new ArrayList<CommunicationSet>();
		comsetlist.add(communicationSet);
		this.communicationTreeViewer.setInput(comsetlist);

		SHIMEditJFaceApplicationWindow.changeInputPanel(communicationSet);
	}

	/**
	 * Re-Makes CommunicationSets by according to inputed parameters.
	 * 
	 * @param sys
	 *            the current SystemConfiguration
	 * @param preferences
	 *            the current ShimPreferences
	 * @param monitor
	 *            the progress monitor
	 * @return the new Communications
	 */
	private CommunicationSet remakeCommunicationSet(SystemConfiguration sys,
			ShimPreferences preferences, IProgressMonitor monitor) {
		int taskNum = 1;
		if (communicationSetWz.btnEvent.getSelection()) {
			taskNum++;
		}
		if (communicationSetWz.btnFIFO.getSelection()) {
			taskNum++;
		}
		if (communicationSetWz.btnInterrupt.getSelection()) {
			taskNum++;
		}
		if (communicationSetWz.btnSharedMemory.getSelection()) {
			taskNum++;
		}
		if (communicationSetWz.btnSharedRegister.getSelection()) {
			taskNum++;
		}
		monitor.beginTask("Re-Makes the base Communications.", taskNum);

		CommunicationSet communicationSet = sys.getCommunicationSet();

		log.finest("#####CommunicationSet(5th) Completed!!");

		boolean dontConnectCon = communicationSetWz.btnDontConnect
				.getSelection();

		List<MasterComponent> mlist = ShimModelManager
				.makeMasterComponentList(sys);
		int masterComponentCount = mlist.size();

		communicationSet = new CommunicationSet();
		sys.setCommunicationSet(communicationSet);

		if (communicationSetWz.btnEvent.getSelection()) {
			log.finest("Event Communication- no attribute ------------------");
			EventCommunication eventCom = new EventCommunication();
			eventCom.setName("Event_00");

			if (masterComponentCount > 1) {
				// ---------Common Procedure --------
				ConnectionSet conset = ShimModelManager
						.createDefaultConnectionList(dontConnectCon);
				eventCom.setConnectionSet(conset);
			}

			communicationSet.getEventCommunication().clear();
			communicationSet.getEventCommunication().add(eventCom);

			monitor.worked(1);
		}
		if (communicationSetWz.btnFIFO.getSelection()) {
			log.finest("FIFO Communication  2 attribute ------------------------");
			FIFOCommunication fifoCom = new FIFOCommunication();
			fifoCom.setName("fifo_00");
			fifoCom.setDataSize(Integer
					.parseInt(communicationSetWz.textFiFoDataSize.getText()));
			int selectionIndex = communicationSetWz.comboFIFODataSizeUnit
					.getSelectionIndex();
			fifoCom.setDataSizeUnit(EnumUtil.getSizeUnitType(selectionIndex));

			fifoCom.setQueueSize(Integer
					.parseInt(communicationSetWz.textQueueSize.getText()));

			if (masterComponentCount > 1) {
				// ---------Common Procedure --------
				ConnectionSet conset = ShimModelManager
						.createDefaultConnectionList(dontConnectCon);
				fifoCom.setConnectionSet(conset);
			}

			communicationSet.getFIFOCommunication().clear();
			communicationSet.getFIFOCommunication().add(fifoCom);

			monitor.worked(1);
		}

		if (communicationSetWz.btnInterrupt.getSelection()) {
			log.finest("Interrupt Communication  1 attribute ----------------------------");

			log.finest(">btn_Interrupt Selected");
			InterruptCommunication intrCom = new InterruptCommunication();

			intrCom.setName("Interrupt_00");

			if (masterComponentCount > 1) {
				// ---------Common Procedure --------
				ConnectionSet conset = ShimModelManager
						.createDefaultConnectionList(dontConnectCon);
				intrCom.setConnectionSet(conset);
			}

			communicationSet.getInterruptCommunication().clear();
			communicationSet.getInterruptCommunication().add(intrCom);

			monitor.worked(1);
		}

		if (communicationSetWz.btnSharedMemory.getSelection()) {
			log.finest("Shared Memory  Communication 3 attribute ---------------------------------");
			SharedMemoryCommunication shmemCom = new SharedMemoryCommunication();
			shmemCom.setName("shmem_00");

			shmemCom.setDataSize(Integer
					.parseInt(communicationSetWz.textSharedMemoryDataSize
							.getText()));
			int selectionIndex = communicationSetWz.comboSharedMemoryDataSizeUnit
					.getSelectionIndex();
			shmemCom.setDataSizeUnit(EnumUtil.getSizeUnitType(selectionIndex));

			shmemCom.setAddressSpaceRef(communicationSetWz.refComboFactoryAs
					.getRefObject());
			shmemCom.setSubSpaceRef(communicationSetWz.refComboFactorySs
					.getRefObject());

			if (masterComponentCount > 1) {
				// ---------Common Procedure --------
				ConnectionSet conset = ShimModelManager
						.createDefaultConnectionList(dontConnectCon);
				shmemCom.setConnectionSet(conset);
			}

			int opTypeSelectionIndex = communicationSetWz.comboOpType
					.getSelectionIndex();
			shmemCom.setOperationType(EnumUtil
					.getOperationType(opTypeSelectionIndex));

			communicationSet.getSharedMemoryCommunication().clear();
			communicationSet.getSharedMemoryCommunication().add(shmemCom);

			monitor.worked(1);
		}

		if (communicationSetWz.btnSharedRegister.getSelection()) {
			log.finest("SharedRegister Communication 3 attribute------------------------------------");

			SharedRegisterCommunication sregCom = new SharedRegisterCommunication();
			sregCom.setName("sreg_00");

			sregCom.setDataSize(Integer
					.parseInt(communicationSetWz.textSRDataSize.getText()));
			int selectionIndex = communicationSetWz.comboSRDataSizeUnit
					.getSelectionIndex();
			sregCom.setDataSizeUnit(EnumUtil.getSizeUnitType(selectionIndex));
			sregCom.setNRegister(Integer
					.parseInt(communicationSetWz.textNRegister.getText()));

			if (masterComponentCount > 1) {
				// ---------Common Procedure --------
				ConnectionSet conset = ShimModelManager
						.createDefaultConnectionList(dontConnectCon);
				sregCom.setConnectionSet(conset);
			}

			communicationSet.getSharedRegisterCommunication().clear();
			communicationSet.getSharedRegisterCommunication().add(sregCom);

			monitor.worked(1);
		}

		return communicationSet;
	}

	/**
	 * Re-Make the Connection's Performance by according to inputed parameters.
	 * 
	 * @param sys
	 *            the current SystemConfiguration
	 * @param communicationSet
	 *            the base CommunicationSet
	 * @param monitor
	 *            the progress monitor
	 */
	private void remakeConnectionPerformances(SystemConfiguration sys,
			CommunicationSet com_set, IProgressMonitor monitor) {
		CommunicationSet comset = sys.getCommunicationSet();
		if (comset != null) {
			List<AbstractCommunication> list = new ArrayList<AbstractCommunication>();

			list.addAll(com_set.getEventCommunication());
			list.addAll(com_set.getSharedMemoryCommunication());
			list.addAll(com_set.getFIFOCommunication());
			list.addAll(com_set.getInterruptCommunication());
			list.addAll(com_set.getSharedRegisterCommunication());

			int taskNum = 0;
			for (AbstractCommunication acom : list) {
				ConnectionSet conset = acom.getConnectionSet();
				if (conset != null) {
					taskNum += conset.getConnection().size();
				}
			}

			if (taskNum > 0) {
				monitor.beginTask("Creates the Connection's Performances.",
						taskNum);

				// add performance
				for (AbstractCommunication acom : list) {
					ConnectionSet conset = acom.getConnectionSet();
					List<Connection> conlist = conset.getConnection();
					for (Connection con : conlist) {
						List<Performance> pflist = con.getPerformance();
						pflist.clear();
						Performance pf = new Performance();
						Latency latency = (Latency) DefaultDataStore
								.getDefaultInstance(Latency.class);
						pf.setLatency(latency);

						Pitch pitch = (Pitch) DefaultDataStore
								.getDefaultInstance(Pitch.class);
						pf.setPitch(pitch);
						pflist.add(pf);

						monitor.worked(1);
					}
				}
			}
		}
	}

	/**
	 * Sets the TreeViewer of CommunicationSet.
	 * 
	 * @param tv
	 *            the TreeViewer of CommunicationSet
	 */
	public void setCommunicationTreeViewer(TreeViewer tv) {
		this.communicationTreeViewer = tv;
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#dispose()
	 */
	@Override
	public void dispose() {
		communicationTreeViewer = null;

		communicationSetWz = null;
		super.dispose();
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performCancel()
	 */
	@Override
	public boolean performCancel() {
		log.info(getWindowTitle() + " is canceled.");
		ShimPreferences.restoreOldInstance();
		return super.performCancel();
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#setContainer(org.eclipse.jface.wizard.IWizardContainer)
	 */
	@Override
	public void setContainer(IWizardContainer wizardContainer) {
		if (wizardContainer instanceof IPageChangeProvider) {
			final IPageChangeProvider pcp = (IPageChangeProvider) wizardContainer;
			pcp.addPageChangedListener(new IPageChangedListener() {
				@Override
				public void pageChanged(final PageChangedEvent event) {
					Object selectedPage = event.getSelectedPage();
					setPreviousSelectedPage(selectedPage);
				}
			});
		}
		super.setContainer(wizardContainer);
	}

	/**
	 * Sets the previous wizard page.
	 * 
	 * @param previousSelectedPage
	 *            the previous wizard page
	 */
	public void setPreviousSelectedPage(Object previousSelectedPage) {
		this.previousSelectedPage = previousSelectedPage;
	}

	/**
	 * Returns the previous wizard page.
	 * 
	 * @param previousSelectedPage
	 *            the previous wizard page
	 */
	public Object getPreviousSelectedPage() {
		return previousSelectedPage;
	}
}
