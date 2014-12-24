/*
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.multicore_association.shim.api.AbstractCommunication;
import org.multicore_association.shim.api.AccessType;
import org.multicore_association.shim.api.AddressSpace;
import org.multicore_association.shim.api.AddressSpaceSet;
import org.multicore_association.shim.api.Cache;
import org.multicore_association.shim.api.CacheType;
import org.multicore_association.shim.api.ClockFrequency;
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
import org.multicore_association.shim.api.RWType;
import org.multicore_association.shim.api.SharedMemoryCommunication;
import org.multicore_association.shim.api.SharedRegisterCommunication;
import org.multicore_association.shim.api.SlaveComponent;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.gui.jface.ShimComponentTreeViewer;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.EnumUtil;
import org.multicore_association.shim.edit.model.ShimComponentTree;
import org.multicore_association.shim.edit.model.ShimModelManager;
import org.multicore_association.shim.edit.model.ShimPreferences;
import org.multicore_association.shim.edit.model.data.DefaultDataStore;
import org.multicore_association.shim.edit.model.preferences.SavePolicyPreferences;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesStore;

/**
 * A wizard to generate a new SHIM Data.
 */
public class CreateNewDataWizard extends Wizard {

	private static final Logger log = ShimLoggerUtil
			.getLogger(CreateNewDataWizard.class);

	private TreeViewer componentTreeViewer = null;
	private TreeViewer addressSpaceTreeViewer = null;
	private TreeViewer communicationTreeViewer = null;

	private ComponentParameterWizardPage createComponentTreeWz = null;
	private ComponentTreeEditorWizardPage editComponentTreeWz = null;
	private AddressSpaceParameterWizardPage createAddressSpace = null;
	private AddressSpaceEditorWizardPage editAsTreeWz = null;
	private CommunicationSetWizardPage communicationSetWz = null;
	private CacheWizardPage cacheWz = null;
	private AccessTypeWizardPage accessTypeWz = null;

	private Object previousSelectedPage;

	/**
	 * Constructs a new instance of CreateNewDataWizard.
	 */
	public CreateNewDataWizard() {
		setWindowTitle("Create New SHIM Data Wizard");
		ShimPreferences.getNewInstance();

		SystemConfiguration sys = ShimModelManager.createSystemConfiguration();
		sys.setAddressSpaceSet(new AddressSpaceSet());
		sys.setCommunicationSet(new CommunicationSet());

		log.info(getWindowTitle() + " open");
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {

		// Component Set Default Setting
		createComponentTreeWz = new ComponentParameterWizardPage();
		addPage(createComponentTreeWz);

		// Edit Initial structure of ComponentSet Tree
		editComponentTreeWz = new ComponentTreeEditorWizardPage();
		editComponentTreeWz.mainTreeViewer = (ShimComponentTreeViewer) componentTreeViewer;
		editComponentTreeWz.setRequiredToCreateData(true);

		addPage(editComponentTreeWz);

		createAddressSpace = new AddressSpaceParameterWizardPage();
		addPage(createAddressSpace);

		editAsTreeWz = new AddressSpaceEditorWizardPage();
		editAsTreeWz.setRequiredToCreateData(true);
		addPage(editAsTreeWz);

		communicationSetWz = new CommunicationSetWizardPage();
		addPage(communicationSetWz);

		cacheWz = new CacheWizardPage();
		addPage(cacheWz);

		accessTypeWz = new AccessTypeWizardPage();
		addPage(accessTypeWz);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {

		// Creates the new SHIM XML.
		final SystemConfiguration sys = ShimModelManager.getCurrentScd();
		final ShimPreferences preferences = ShimPreferences
				.getCurrentInstance();
		log.info(ShimPreferences.createDumpStr(preferences, false));

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
					createNewShimXml(sys, preferences, monitor);
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

	/**
	 * Creates the new SHIM XML.
	 * 
	 * @param sys
	 *            the current SystemConfiguration.
	 * @param preferences
	 *            the current ShimPreferences
	 * @param monitor
	 *            the progress monitor
	 */
	private void createNewShimXml(SystemConfiguration sys,
			ShimPreferences preferences, IProgressMonitor monitor) {

		monitor.beginTask("Creates Components.", 1);

		sys.setShimVersion(CommonConstants.SHIM_VERSION);
		sys.setName(preferences.getComponentsPreferences().getSystemName());

		ClockFrequency cf = (ClockFrequency) DefaultDataStore
				.getDefaultInstance(ClockFrequency.class);
		sys.setClockFrequency(cf);

		// ----------------------------------------------------------------
		// ComponentSet
		{
			if (editComponentTreeWz.isRequiredToCreateData()) {
				editComponentTreeWz.createRootComponentSet();
			}
			List<ComponentSet> rootArray = new ArrayList<ComponentSet>();
			rootArray.add(editComponentTreeWz.root);

			log.finest("treeViewer = " + componentTreeViewer);
			componentTreeViewer.setInput(rootArray);
		}
		monitor.worked(1);

		// ----------------------------------------------------------------
		// AddressSpaceSet
		{
			monitor.beginTask("Creates AddressSpaces.", 1);

			if (editAsTreeWz.isRequiredToCreateData()) {
				editAsTreeWz.createAddressSpaceSet();
			}

			List<AddressSpaceSet> rootArray = new ArrayList<AddressSpaceSet>();
			rootArray.add(sys.getAddressSpaceSet());

			addressSpaceTreeViewer.setInput(rootArray);

			monitor.worked(1);
		}

		// ----------------------------------------------------------------
		// CommunicationSet
		CommunicationSet communicationSet = createCommunicationSet(sys,
				preferences, monitor);

		// ----------------------------------------------------------------
		// Cache
		createCache(sys, preferences, monitor);

		// ----------------------------------------------------------------
		// AccessType
		createAccessType(sys, preferences, monitor);

		// ----------------------------------------------------------------
		// MasterSlaveBinding
		createMasterSlaveBindingSet(sys, monitor);

		createConnectionPerformances(sys, communicationSet, monitor);

		List<CommunicationSet> comsetlist = new ArrayList<CommunicationSet>();
		comsetlist.add(communicationSet);
		this.communicationTreeViewer.setInput(comsetlist);
	}

	/**
	 * Creates CommunicationSets by according to inputed parameters.
	 * 
	 * @param sys
	 *            the current SystemConfiguration
	 * @param preferences
	 *            the current ShimPreferences
	 * @param monitor
	 *            the progress monitor
	 * @return the new Communications
	 */
	private CommunicationSet createCommunicationSet(SystemConfiguration sys,
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
		monitor.beginTask("Creates the base Communications.", taskNum);

		CommunicationSet communicationSet = sys.getCommunicationSet();

		log.finest(("#####CommunicationSet(5th) Completed!!"));

		boolean dontConnectCon = communicationSetWz.btnDontConnect
				.getSelection();

		List<MasterComponent> mlist = ShimModelManager
				.makeMasterComponentList(sys);
		int masterComponentCount = mlist.size();

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

			communicationSet.getInterruptCommunication().add(intrCom);

			monitor.worked(1);
		}

		if (communicationSetWz.btnSharedMemory.getSelection()) {
			log.finest("Shared Memory Communication 3 attribute ---------------------------------");
			SharedMemoryCommunication shmemCom = new SharedMemoryCommunication();
			shmemCom.setName("shmem_00");

			shmemCom.setDataSize(preferences.getCommunicationSetPreferences()
					.getSharedMemoryDataSize());

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

			communicationSet.getSharedRegisterCommunication().add(sregCom);

			monitor.worked(1);
		}

		monitor.worked(1);
		return communicationSet;
	}

	/**
	 * Creates Caches by according to inputed parameters.
	 * 
	 * @param sys
	 *            the current SystemConfiguration
	 * @param preferences
	 *            the current ShimPreferences
	 * @param monitor
	 *            the progress monitor
	 */
	private void createCache(SystemConfiguration sys,
			ShimPreferences preferences, IProgressMonitor monitor) {
		monitor.beginTask("Creates the Caches.", 1);

		log.finest("Cache Information Wizard Complete ------------------------------------");

		if (cacheWz.btnRadioButtonNon.getSelection()) {

		} else if (cacheWz.btnRadioButtonUnified.getSelection()) {

			Cache cache = new Cache();
			cache.setLineSize(Integer.parseInt(cacheWz.textLineSize1.getText()));
			cache.setCacheCoherency(EnumUtil
					.getCacheCoherencyType(cacheWz.comboCacheCoherency1
							.getSelectionIndex()));
			cache.setCacheType(CacheType.fromValue("UNIFIED"));
			cache.setLockDownType(EnumUtil
					.getLockDownType(cacheWz.comboLockDownType1
							.getSelectionIndex()));
			cache.setNWay(Integer.parseInt(cacheWz.textNWay1.getText()));
			cache.setSize(Integer.parseInt(cacheWz.textSize1.getText()));

			log.finest("Cache SizeUnit------"
					+ cacheWz.comboSizeUnit1.getText());

			cache.setSizeUnit(EnumUtil.getSizeUnitType(cacheWz.comboSizeUnit1
					.getSelectionIndex()));

			ShimModelManager.setCacheDataToMasterComponent(
					sys.getComponentSet(), "0", "unified", cache, (Cache) null);

		} else if (cacheWz.btnRadioButtonData.getSelection()) {

			Cache cache = new Cache();
			cache.setLineSize(Integer.parseInt(cacheWz.textLineSize1.getText()));
			cache.setCacheCoherency(EnumUtil
					.getCacheCoherencyType(cacheWz.comboCacheCoherency1
							.getSelectionIndex()));
			cache.setCacheType(CacheType.fromValue("DATA"));
			cache.setLockDownType(EnumUtil
					.getLockDownType(cacheWz.comboLockDownType1
							.getSelectionIndex()));
			cache.setNWay(Integer.parseInt(cacheWz.textNWay1.getText()));
			cache.setSize(Integer.parseInt(cacheWz.textSize1.getText()));
			cache.setSizeUnit(EnumUtil.getSizeUnitType(cacheWz.comboSizeUnit1
					.getSelectionIndex()));

			ShimModelManager.setCacheDataToMasterComponent(
					sys.getComponentSet(), "0", "data", cache, (Cache) null);

		} else if (cacheWz.btnRadioButtonInstruction.getSelection()) {

			Cache cache = new Cache();
			cache.setLineSize(Integer.parseInt(cacheWz.textLineSize1.getText()));
			cache.setCacheCoherency(EnumUtil
					.getCacheCoherencyType(cacheWz.comboCacheCoherency1
							.getSelectionIndex()));
			cache.setCacheType(CacheType.fromValue("INSTRUCTION"));
			cache.setLockDownType(EnumUtil
					.getLockDownType(cacheWz.comboLockDownType1
							.getSelectionIndex()));
			cache.setNWay(Integer.parseInt(cacheWz.textNWay1.getText()));
			cache.setSize(Integer.parseInt(cacheWz.textSize1.getText()));
			cache.setSizeUnit(EnumUtil.getSizeUnitType(cacheWz.comboSizeUnit1
					.getSelectionIndex()));

			ShimModelManager.setCacheDataToMasterComponent(
					sys.getComponentSet(), "0", "instruction", cache,
					(Cache) null);

		} else if (cacheWz.btnRadioButtonDandI.getSelection()) {

			Cache cacheData = new Cache();
			cacheData.setLineSize(preferences.getCacheDataPreferences()
					.getLineSize1());
			cacheData.setCacheCoherency(EnumUtil
					.getCacheCoherencyType(cacheWz.comboCacheCoherency1
							.getSelectionIndex()));
			cacheData.setCacheType(CacheType.fromValue("DATA"));
			cacheData.setLockDownType(EnumUtil
					.getLockDownType(cacheWz.comboLockDownType1
							.getSelectionIndex()));
			cacheData.setNWay(preferences.getCacheDataPreferences()
					.getNumWay1());
			cacheData.setSize(Integer.parseInt(cacheWz.textSize1.getText()));
			cacheData
					.setSizeUnit(EnumUtil
							.getSizeUnitType(cacheWz.comboSizeUnit1
									.getSelectionIndex()));

			Cache cacheInst = new Cache();
			cacheInst.setLineSize(preferences.getCacheDataPreferences()
					.getLineSize2());
			cacheInst.setCacheCoherency(EnumUtil
					.getCacheCoherencyType(cacheWz.comboCacheCoherency2
							.getSelectionIndex()));
			cacheInst.setCacheType(CacheType.fromValue("INSTRUCTION"));
			cacheInst.setLockDownType(EnumUtil
					.getLockDownType(cacheWz.comboLockDownType2
							.getSelectionIndex()));
			cacheInst.setNWay(preferences.getCacheDataPreferences()
					.getNumWay2());
			cacheInst.setSize(Integer.parseInt(cacheWz.textSize2.getText()));
			cacheInst
					.setSizeUnit(EnumUtil
							.getSizeUnitType(cacheWz.comboSizeUnit2
									.getSelectionIndex()));

			ShimModelManager.setCacheDataToMasterComponent(
					sys.getComponentSet(), "0", "DandI", cacheData, cacheInst);

		}
		ShimModelManager.setCommonInstructionSetToMasterComponent(
				sys.getComponentSet(), "0");

		monitor.worked(1);
	}

	/**
	 * Creates AccessTypes by according to inputed parameters.
	 * 
	 * @param sys
	 *            the current SystemConfiguration
	 * @param preferences
	 *            the current ShimPreferences
	 * @param monitor
	 *            the progress monitor
	 */
	private void createAccessType(SystemConfiguration sys,
			ShimPreferences preferences, IProgressMonitor monitor) {
		log.finest("accessType_Wizard Complete--------------------------------------------");

		String baseName = accessTypeWz.txt_baseName.getText();
		String byteSizeList = accessTypeWz.textByteSizeList.getText();

		boolean flgR = accessTypeWz.btnCheckButtonR.getSelection();
		boolean flgW = accessTypeWz.btnCheckButtonW.getSelection();
		boolean flgRW = accessTypeWz.btnCheckButtonRW.getSelection();

		List<String> typeList = new ArrayList<String>();

		if (flgR)
			typeList.add("R");
		if (flgW)
			typeList.add("W");
		if (flgRW)
			typeList.add("RW");

		String[] rwTypeArray = typeList.toArray(new String[0]);
		String[] byteSizeArray = byteSizeList.split(",");

		log.finest("--------------rwTypeArray.size=" + rwTypeArray.length);
		log.finest("--------------byteSizeArray.size=" + byteSizeArray.length);

		log.finest("create AccessType");

		monitor.beginTask("Creates the AccessTypes.", rwTypeArray.length
				* byteSizeArray.length + 1);

		List<AccessType> batList = new ArrayList<AccessType>();

		for (int i = 0; i < rwTypeArray.length; i++) {
			for (int j = 0; j < byteSizeArray.length; j++) {
				AccessType bat = new AccessType();
				bat.setName(baseName + rwTypeArray[i] + "_" + byteSizeArray[j]);
				bat.setAccessByteSize(Integer.parseInt(byteSizeArray[j]));
				bat.setAlignmentByteSize(Integer.parseInt(byteSizeArray[j]));
				bat.setRwType(RWType.fromValue(rwTypeArray[i]));
				bat.setNBurst(preferences.getAccessTypePreferences()
						.getNBurst());
				batList.add(bat);

				monitor.worked(1);
			}
		}

		log.finest("----------------------------------bat_list.size="
				+ batList.size());
		ShimModelManager.setAccessTypeDataToMasterComponent(
				sys.getComponentSet(), "0", batList);
		monitor.worked(1);
	}

	/**
	 * Creates MasterSlaveBindingSet by according to inputed parameters.
	 * 
	 * @param sys
	 *            the current SystemConfiguration
	 * @param monitor
	 *            the progress monitor
	 */
	private void createMasterSlaveBindingSet(SystemConfiguration sys,
			IProgressMonitor monitor) {
		boolean dontConnect = false;
		if (this.createAddressSpace.btnDontConnect.getSelection()) {
			dontConnect = true;
		}

		// Sets a MasterSlaveBinding to every SubSpace.
		List<AddressSpace> asList = sys.getAddressSpaceSet().getAddressSpace();
		int taskNum = 0;
		for (AddressSpace as : asList) {
			List<SubSpace> sslist = as.getSubSpace();
			taskNum += sslist.size();
		}

		if (taskNum > 0) {
			monitor.beginTask("Creates the MasterSlaveBindingSet.", taskNum);

			ShimComponentTree scTree = new ShimComponentTree(sys);
			List<MasterComponent> mcList = ShimModelManager
					.makeMasterComponentList(sys.getComponentSet());
			List<SlaveComponent> slcList = ShimModelManager
					.makeSlaveComponentList(sys.getComponentSet());
			if (mcList.isEmpty() || slcList.isEmpty()) {
				monitor.worked(taskNum);
				return;
			}

			for (AddressSpace as : asList) {
				List<SubSpace> ssList = as.getSubSpace();
				for (SubSpace ss : ssList) {
					ShimModelManager.setMasterSlaveBindingToSubSpace(scTree,
							mcList, slcList, ss, dontConnect);
					monitor.worked(1);
				}
			}
		}
	}

	/**
	 * Creates the Connection's Performance by according to inputed parameters.
	 * 
	 * @param sys
	 *            the current SystemConfiguration
	 * @param communicationSet
	 *            the base CommunicationSet
	 * @param monitor
	 *            the progress monitor
	 */
	private void createConnectionPerformances(SystemConfiguration sys,
			CommunicationSet communicationSet, IProgressMonitor monitor) {
		CommunicationSet comset = sys.getCommunicationSet();
		if (comset != null) {
			List<AbstractCommunication> list = new ArrayList<AbstractCommunication>();

			list.addAll(communicationSet.getEventCommunication());
			list.addAll(communicationSet.getSharedMemoryCommunication());
			list.addAll(communicationSet.getFIFOCommunication());
			list.addAll(communicationSet.getInterruptCommunication());
			list.addAll(communicationSet.getSharedRegisterCommunication());

			int taskNum = 0;
			for (AbstractCommunication acom : list) {
				ConnectionSet connectionSet = acom.getConnectionSet();
				if (connectionSet != null) {
					taskNum += connectionSet.getConnection().size();
				}
			}

			if (taskNum > 0) {
				monitor.beginTask("Creates the Connection's Performances.",
						taskNum);

				// add performance
				for (AbstractCommunication acom : list) {
					ConnectionSet connectionSet = acom.getConnectionSet();
					List<Connection> connectionList = connectionSet
							.getConnection();
					for (Connection connection : connectionList) {
						List<Performance> pflist = connection.getPerformance();
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
	 * Sets the TreeViewer of AddressSpace.
	 * 
	 * @param tv
	 *            the TreeViewer of AddressSpace
	 */
	public void setAddressSpaceTreeViewer(TreeViewer tv) {
		this.addressSpaceTreeViewer = tv;
	}

	/**
	 * Sets the TreeViewer of Components.
	 * 
	 * @param tv
	 *            the TreeViewer of Components
	 */
	public void setComponentTreeViewer(TreeViewer tv) {
		componentTreeViewer = tv;
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
		componentTreeViewer = null;
		addressSpaceTreeViewer = null;
		communicationTreeViewer = null;

		createComponentTreeWz = null;
		editComponentTreeWz = null;
		createAddressSpace = null;
		editAsTreeWz = null;
		communicationSetWz = null;
		cacheWz = null;
		accessTypeWz = null;
		super.dispose();
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#setContainer(org.eclipse.jface.wizard.IWizardContainer)
	 */
	@Override
	public void setContainer(IWizardContainer wizardContainer) {
		if (wizardContainer instanceof IPageChangeProvider) {
			final IPageChangeProvider pcp = (IPageChangeProvider) wizardContainer;
			pcp.addPageChangedListener(new IPageChangedListener() {

				/**
				 * @see org.eclipse.jface.dialogs.IPageChangedListener#pageChanged(org.eclipse.jface.dialogs.PageChangedEvent)
				 */
				@Override
				public void pageChanged(final PageChangedEvent event) {
					Object selectedPage = event.getSelectedPage();

					if (createComponentTreeWz.equals(getPreviousSelectedPage())
							&& editComponentTreeWz.equals(selectedPage)) {
						// go from createComponentTree to editComponentTree
						editComponentTreeWz.createRootComponentSet();
						editComponentTreeWz.setRequiredToCreateData(false);

					} else if (editComponentTreeWz
							.equals(getPreviousSelectedPage())
							&& createComponentTreeWz.equals(selectedPage)) {
						// go from editComponentTree to createComponentTree
						editComponentTreeWz.setRequiredToCreateData(true);
						System.err.println("setRequiredToCreateComponentTree");

					} else if (createAddressSpace.equals(selectedPage)) {
						createAddressSpace.update();
						if (editAsTreeWz.equals(getPreviousSelectedPage())) {
							// go from editAddressSpace to createAddressSpace
							editAsTreeWz.setRequiredToCreateData(true);
						}

					} else if (createAddressSpace
							.equals(getPreviousSelectedPage())
							&& editAsTreeWz.equals(selectedPage)) {
						// go from createAddressSpace to editAddressSpace
						editAsTreeWz.createAddressSpaceSet();
						editAsTreeWz.setRequiredToCreateData(false);

					} else if (communicationSetWz.equals(selectedPage)) {
						communicationSetWz.updateCombo();
					}

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

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performCancel()
	 */
	@Override
	public boolean performCancel() {
		log.info(getWindowTitle() + " is canceled.");

		ShimPreferences.restoreOldInstance();

		int currentIndex = ShimModelManager.getCurrentIndex();
		ShimModelManager.removeSystemConfiguration(currentIndex);

		return super.performCancel();
	}
}
