/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model;

import org.multicore_association.shim.api.MasterType;
import org.multicore_association.shim.edit.gui.common.LabelConstants;
import org.multicore_association.shim.edit.model.preferences.AccessTypePreferences;
import org.multicore_association.shim.edit.model.preferences.AddressSpacePreferences;
import org.multicore_association.shim.edit.model.preferences.CacheDataPreferences;
import org.multicore_association.shim.edit.model.preferences.CommunicationSetPreferences;
import org.multicore_association.shim.edit.model.preferences.ComponentSetPreferences;
import org.multicore_association.shim.edit.model.preferences.ComponentsPreferences;
import org.multicore_association.shim.edit.model.preferences.MasterComponentPreferences;
import org.multicore_association.shim.edit.model.preferences.PerformancePreferences;
import org.multicore_association.shim.edit.model.preferences.SavePolicyPreferences;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesKey;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesStore;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesStore.CacheTypeSelect;
import org.multicore_association.shim.edit.model.preferences.SlaveComponentPreferences;

/**
 * Preferences to create SHIM Data obejct's instance.
 */
public class ShimPreferences {

	private static ShimPreferences currInstance = new ShimPreferences();

	private static ShimPreferences oldInstance = new ShimPreferences();

	private ShimPreferencesStore store;

	private ComponentsPreferences componentsPreferences;
	private MasterComponentPreferences masterComponentPreferences;
	private SlaveComponentPreferences slaveComponentPreferences;
	private ComponentSetPreferences componentSetPreferences;

	private AddressSpacePreferences addressSpacePreferences;

	private CommunicationSetPreferences communicationSetPreferences;

	private CacheDataPreferences cacheDataPreferences;

	private AccessTypePreferences accessTypePreferences;

	private PerformancePreferences latencyPreferences;

	private PerformancePreferences pitchPreferences;

	private SavePolicyPreferences savePolicyPreferences;

	/**
	 * Constructs a new instance of ShimPreferences.
	 */
	private ShimPreferences() {
		store = ShimPreferencesStore.getInstance();

		/* Component Parameter */
		componentsPreferences = new ComponentsPreferences();
		loadComponentsPreferences();

		// MasterComponent Parameter
		masterComponentPreferences = new MasterComponentPreferences();
		loadMasterComponentPreferences();

		// SlaveComponent Parameter
		slaveComponentPreferences = new SlaveComponentPreferences();
		loadSlaveComponentPreferences();

		// ComponentSet Parameter
		componentSetPreferences = new ComponentSetPreferences();
		loadComponentSetPreferences();

		/* AddressSpace Parameter */
		addressSpacePreferences = new AddressSpacePreferences();
		loadAddressSpacePreferences();

		/* CommunicationSet Parameter */
		communicationSetPreferences = new CommunicationSetPreferences();
		loadCommunicationSetPreferences();

		/* CacheDate Parameter */
		cacheDataPreferences = new CacheDataPreferences();
		loadCacheDataPreferences();

		/* AccessType Parameter */
		accessTypePreferences = new AccessTypePreferences();
		loadAccessTypePreferences();

		/* Performance */
		latencyPreferences = new PerformancePreferences();
		loadLatencyPreferences();

		pitchPreferences = new PerformancePreferences();
		loadPitchPreferences();

		savePolicyPreferences = new SavePolicyPreferences();
		loadSavePolicyPreferences();
	}

	/**
	 * Returns the ShimPreferences instance which is used now.
	 * 
	 * @return the ShimPreferences instance which is used now
	 */
	public static ShimPreferences getCurrentInstance() {
		return currInstance;
	}

	/**
	 * Creates a new ShimPreferences instance, and returns it.
	 * 
	 * @return new Shim_Settings instance
	 */
	public static ShimPreferences getNewInstance() {
		oldInstance = currInstance;
		currInstance = new ShimPreferences();
		return currInstance;
	}

	/**
	 * Creates a new ShimPreferences instance, and returns it.
	 * 
	 * @return the new ShimPreferences instance
	 */
	public static ShimPreferences getNewCopyInstance() {
		oldInstance = currInstance;
		currInstance = new ShimPreferences();
		currInstance.componentsPreferences = new ComponentsPreferences();

		currInstance.componentsPreferences = (ComponentsPreferences) oldInstance.componentsPreferences
				.clone();
		currInstance.masterComponentPreferences = (MasterComponentPreferences) oldInstance.masterComponentPreferences
				.clone();
		currInstance.slaveComponentPreferences = (SlaveComponentPreferences) oldInstance.slaveComponentPreferences
				.clone();
		currInstance.componentSetPreferences = (ComponentSetPreferences) oldInstance.componentSetPreferences
				.clone();
		currInstance.addressSpacePreferences = (AddressSpacePreferences) oldInstance.addressSpacePreferences
				.clone();
		currInstance.communicationSetPreferences = (CommunicationSetPreferences) oldInstance.communicationSetPreferences
				.clone();
		currInstance.cacheDataPreferences = (CacheDataPreferences) oldInstance.cacheDataPreferences
				.clone();
		currInstance.accessTypePreferences = (AccessTypePreferences) oldInstance.accessTypePreferences
				.clone();

		return currInstance;
	}

	/**
	 * 'currInstance' change into oldInstance from currInstance.
	 */
	public static void restoreOldInstance() {
		currInstance = oldInstance;
	}

	/**
	 * Sets ComponentsPreferences which is stored last time to this
	 * ComponentsPreferences.
	 */
	public void loadComponentsPreferences() {
		componentsPreferences.setSystemName(store
				.getString(ShimPreferencesKey.CP_SYSTEM_NAME));
		componentsPreferences.setNumberMaster(store
				.getInt(ShimPreferencesKey.CP_NUMBER_MASTER));
		componentsPreferences.setNumberSlave(store
				.getInt(ShimPreferencesKey.CP_NUMBER_SLAVE));
		componentsPreferences.setNumberComponent(store
				.getInt(ShimPreferencesKey.CP_NUMBER_COMPONENT));
		componentsPreferences.setClock(store
				.getFloat(ShimPreferencesKey.CP_CLOCK));
	}

	/**
	 * Sets MasterComponentPreferences which is stored last time to this
	 * MasterComponentPreferences.
	 */
	public void loadMasterComponentPreferences() {
		masterComponentPreferences.setBaseName(store
				.getString(ShimPreferencesKey.CP_MASTER_NAME));
		masterComponentPreferences.setType(store
				.getInt(ShimPreferencesKey.CP_MASTER_TYPE));
		masterComponentPreferences.setEndian(store
				.getInt(ShimPreferencesKey.CP_MASTER_ENDIAN));
		masterComponentPreferences.setArch(store
				.getString(ShimPreferencesKey.CP_MASTER_ARCH));
		masterComponentPreferences.setArchOpt(store
				.getString(ShimPreferencesKey.CP_MASTER_ARCH_OPT));
		masterComponentPreferences.setnChannel(store
				.getInteger(ShimPreferencesKey.CP_MASTER_NUMBER_CHANNEL));
		masterComponentPreferences.setnThread(store
				.getInteger(ShimPreferencesKey.CP_MASTER_NUMBER_THREAD));
		masterComponentPreferences.setCheckClockForMaster(store
				.getBoolean(ShimPreferencesKey.CP_MASTER_CHECK_CLOCKFREQUENCY));
		masterComponentPreferences.setClock(store
				.getFloat(ShimPreferencesKey.CP_MASTER_CLOCK));
	}

	/**
	 * Sets SlaveComponentPreferences which is stored last time to this
	 * SlaveComponentPreferences.
	 */
	public void loadSlaveComponentPreferences() {
		slaveComponentPreferences.setBaseName(store
				.getString(ShimPreferencesKey.CP_SLAVE_NAME));
		slaveComponentPreferences.setSize(store
				.getInt(ShimPreferencesKey.CP_SLAVE_SIZE));
		slaveComponentPreferences.setSizeUnit(store
				.getInt(ShimPreferencesKey.CP_SLAVE_SIZE_UNIT));
		slaveComponentPreferences.setRwType(store
				.getInt(ShimPreferencesKey.CP_SLAVE_RWTYPE));
	}

	/**
	 * Sets ComponentSetPreferences which is stored last time to this
	 * ComponentSetPreferences.
	 */
	public void loadComponentSetPreferences() {
		componentSetPreferences.setBaseName(store
				.getString(ShimPreferencesKey.CP_COMPONENTSET_NAME));
	}

	/**
	 * Sets AddressSpacePreferences which is stored last time to this
	 * AddressSpacePreferences.
	 */
	public void loadAddressSpacePreferences() {
		addressSpacePreferences.setNumberSubSpace(store
				.getInt(ShimPreferencesKey.AS_NUMBER_SUBSPACE));
		addressSpacePreferences.setSizeSubSpace(store
				.getInt(ShimPreferencesKey.AS_SIZE_SUBSPACE));
		addressSpacePreferences.setAddressSpaceName(store
				.getString(ShimPreferencesKey.AS_NAME));
		addressSpacePreferences.setSubSpaceName(store
				.getString(ShimPreferencesKey.AS_SUBSPACE_NAME));
		addressSpacePreferences.setDontConnect(store
				.getBoolean(ShimPreferencesKey.AS_CHECK_DONT_CONNECT));
	}

	/**
	 * Sets CommunicationSetPreferences which is stored last time to this
	 * CommunicationSetPreferences.
	 */
	public void loadCommunicationSetPreferences() {
		// InterruptCommunication parameter
		communicationSetPreferences.setCheckInterruptCommunication(store
				.getBoolean(ShimPreferencesKey.CS_CHECK_INTERRUPT));

		// EventCommunication parameter
		communicationSetPreferences.setCheckEventCommunication(store
				.getBoolean(ShimPreferencesKey.CS_CHECK_EVENT));

		// FIFOCommunication parameter
		communicationSetPreferences.setCheckFIFOCommunication(store
				.getBoolean(ShimPreferencesKey.CS_CHECK_FIFO));
		communicationSetPreferences.setFifoDataSize(store
				.getInt(ShimPreferencesKey.CS_FIFO_DATA_SIZE));
		communicationSetPreferences.setFifoSizeUnit(store
				.getInt(ShimPreferencesKey.CS_FIFO_DATA_SIZE_UNIT));
		communicationSetPreferences.setQueueSize(store
				.getInt(ShimPreferencesKey.CS_FIFO_QUEUE_SIZE));

		// SharedMemoryCommunication parameter
		communicationSetPreferences.setCheckSharedMemoryCommunication(store
				.getBoolean(ShimPreferencesKey.CS_CHECK_SMC));
		communicationSetPreferences.setSharedMemoryDataSize(store
				.getInteger(ShimPreferencesKey.CS_SMC_DATA_SIZE));
		communicationSetPreferences.setSharedMemorySizeUnit(store
				.getInt(ShimPreferencesKey.CS_SMC_DATA_SIZE_UNIT));
		communicationSetPreferences.setOperationType(store
				.getInt(ShimPreferencesKey.CS_SMC_OPERATION));

		// SharedRegisterCommunication parameter
		communicationSetPreferences.setCheckSharedRegisterCommunication(store
				.getBoolean(ShimPreferencesKey.CS_CHECK_SRC));
		communicationSetPreferences.setSharedRegisterDataSize(store
				.getInt(ShimPreferencesKey.CS_SRC_DATA_SIZE));
		communicationSetPreferences.setSharedRegisterSizeUnit(store
				.getInt(ShimPreferencesKey.CS_SRC_DATA_SIZE_UNIT));
		communicationSetPreferences.setNumRegister(store
				.getInt(ShimPreferencesKey.CS_SRC_NUMBER_REGISTER));

		communicationSetPreferences.setDontConnect(store
				.getBoolean(ShimPreferencesKey.CS_DONT_CONNECT));
	}

	/**
	 * Sets CacheDataPreferences which is stored last time to this
	 * CacheDataPreferences.
	 */
	public void loadCacheDataPreferences() {
		cacheDataPreferences.setCacheType(store
				.getInt(ShimPreferencesKey.CD_CACHE_TYPE));
		cacheDataPreferences.setCacheCoherenecy1(store
				.getInt(ShimPreferencesKey.CD_DATA_CACHE_COHERENCY));
		cacheDataPreferences.setCacheSize1(store
				.getInt(ShimPreferencesKey.CD_DATA_CACHE_SIZE));
		cacheDataPreferences.setCacheSizeUnit1(store
				.getInt(ShimPreferencesKey.CD_DATA_CACHE_SIZE_UNIT));
		cacheDataPreferences.setNumWay1(store
				.getInteger(ShimPreferencesKey.CD_DATA_NUMBER_WAY));
		cacheDataPreferences.setLineSize1(store
				.getInteger(ShimPreferencesKey.CD_DATA_LINE_SIZE));
		cacheDataPreferences.setLockDownType1(store
				.getInt(ShimPreferencesKey.CD_DATA_LOCK_DOWN_TYPE));

		// parameter for [data and instruction]
		cacheDataPreferences.setCacheCoherenecy2(store
				.getInt(ShimPreferencesKey.CD_INSTRUCTION_CACHE_COHERENCY));
		cacheDataPreferences.setCacheSize2(store
				.getInt(ShimPreferencesKey.CD_INSTRUCTION_CACHE_SIZE));
		cacheDataPreferences.setCacheSizeUnit2(store
				.getInt(ShimPreferencesKey.CD_INSTRUCTION_CACHE_SIZE_UNIT));
		cacheDataPreferences.setNumWay2(store
				.getInteger(ShimPreferencesKey.CD_INSTRUCTION_NUMBER_WAY));
		cacheDataPreferences.setLineSize2(store
				.getInteger(ShimPreferencesKey.CD_INSTRUCTION_LINE_SIZE));
		cacheDataPreferences.setLockDownType2(store
				.getInt(ShimPreferencesKey.CD_INSTRUCTION_LOCK_DOWN_TYPE));
	}

	/**
	 * Sets AccessTypePreferences which is stored last time to this
	 * AccessTypePreferences.
	 */
	public void loadAccessTypePreferences() {
		accessTypePreferences.setBaseName(store
				.getString(ShimPreferencesKey.AT_NAME));
		accessTypePreferences.setCheckR(store
				.getBoolean(ShimPreferencesKey.AT_CHECK_R));
		accessTypePreferences.setCheckW(store
				.getBoolean(ShimPreferencesKey.AT_CHECK_W));
		accessTypePreferences.setCheckRW(store
				.getBoolean(ShimPreferencesKey.AT_CHECK_RW));
		accessTypePreferences.setCheckRWX(store
				.getBoolean(ShimPreferencesKey.AT_CHECK_RWX));
		accessTypePreferences.setCheckRX(store
				.getBoolean(ShimPreferencesKey.AT_CHECK_RX));
		accessTypePreferences.setCheckX(store
				.getBoolean(ShimPreferencesKey.AT_CHECK_X));
		accessTypePreferences.setAccessByteSize(store
				.getString(ShimPreferencesKey.AT_ACCESS_BYTE_SIZE));
		accessTypePreferences.setNBurst(store
				.getInteger(ShimPreferencesKey.AT_NUMBER_BURST));
	}

	/**
	 * Sets LatencyPreferences which is stored last time to this
	 * LatencyPreferences.
	 */
	public void loadLatencyPreferences() {
		latencyPreferences.setBest(store
				.getFloatWrapper(ShimPreferencesKey.P_LATENCY_BEST));
		latencyPreferences.setTypical(store
				.getFloat(ShimPreferencesKey.P_LATENCY_TYPICAL));
		latencyPreferences.setWorst(store
				.getFloatWrapper(ShimPreferencesKey.P_LATENCY_WORST));
	}

	/**
	 * Sets PitchPreferences which is stored last time to this PitchPreferences.
	 */
	public void loadPitchPreferences() {
		pitchPreferences.setBest(store
				.getFloatWrapper(ShimPreferencesKey.P_PITCH_BEST));
		pitchPreferences.setTypical(store
				.getFloat(ShimPreferencesKey.P_PITCH_TYPICAL));
		pitchPreferences.setWorst(store
				.getFloatWrapper(ShimPreferencesKey.P_PITCH_WORST));
	}

	/**
	 * Sets SavePolicyPreferences which is stored last time to this
	 * PitchPreferences.
	 */
	public void loadSavePolicyPreferences() {
		savePolicyPreferences.setAlwaysSave(store
				.getBoolean(ShimPreferencesKey.WIZ_CHECK_ALWAYS));
		savePolicyPreferences.setSaveOnRequest(store
				.getBoolean(ShimPreferencesKey.WIZ_CHECK_ON_REQUEST));
		savePolicyPreferences.setNeverSave(store
				.getBoolean(ShimPreferencesKey.WIZ_CHECK_NEVER));
	}

	/**
	 * Returns preferences to create a SystemConfiguration instance and
	 * components.
	 * 
	 * @return preferences to create a SystemConfiguration instance and
	 *         components
	 */
	public ComponentsPreferences getComponentsPreferences() {
		return componentsPreferences;
	}

	/**
	 * Returns preferences to create a MasterComponent instance.
	 * 
	 * @return preferences to create a MasterComponent instance
	 */
	public MasterComponentPreferences getMasterComponentPreferences() {
		return masterComponentPreferences;
	}

	/**
	 * Returns preferences to create a SlaveComponent instance.
	 * 
	 * @return preferences to create a SlaveComponent instance
	 */
	public SlaveComponentPreferences getSlaveComponentPreferences() {
		return slaveComponentPreferences;
	}

	/**
	 * Returns preferences to create a ComponentSet instance.
	 * 
	 * @return preferences to create a ComponentSet instance
	 */
	public ComponentSetPreferences getComponentSetPreferences() {
		return componentSetPreferences;
	}

	/**
	 * Returns preferences to create a ComponentSet instance.
	 * 
	 * @return preferences to create a ComponentSet instance
	 */
	public AddressSpacePreferences getAddressSpacePreferences() {
		return addressSpacePreferences;
	}

	/**
	 * Returns preferences to create a CommunicationSet instance.
	 * 
	 * @return preferences to create a CommunicationSet instance
	 */
	public CommunicationSetPreferences getCommunicationSetPreferences() {
		return communicationSetPreferences;
	}

	/**
	 * Returns preferences to create a Cache instance.
	 * 
	 * @return preferences to create a Cache instance
	 */
	public CacheDataPreferences getCacheDataPreferences() {
		return cacheDataPreferences;
	}

	/**
	 * Returns preferences to create an AccessType instance.
	 * 
	 * @return preferences to create an AccessType instance
	 */
	public AccessTypePreferences getAccessTypePreferences() {
		return accessTypePreferences;
	}

	/**
	 * Returns preferences to create a Latency instance.
	 * 
	 * @return preferences to create a Latency instance
	 */
	public PerformancePreferences getLatencyPreferences() {
		return latencyPreferences;
	}

	/**
	 * Returns preferences to create a Pitch instance.
	 * 
	 * @return preferences to create a Pitch instance
	 */
	public PerformancePreferences getPitchPreferences() {
		return pitchPreferences;
	}

	/**
	 * Returns preferences whether the preferences saves or not.
	 * 
	 * @return preferences whether the preferences saves or not
	 */
	public SavePolicyPreferences getSavePolicyPreferences() {
		return savePolicyPreferences;
	}

	/**
	 * Dumps preferences.
	 * 
	 * @param preferences
	 *            SHIM Editor preferences
	 * @param isRemake
	 *            whether this method is called by re-make wizard
	 * @return the dump string
	 */
	public static String createDumpStr(ShimPreferences preferences,
			boolean isRemake) {

		StringBuilder dumpBuilder = new StringBuilder();
		dumpBuilder.append("Wizard Finish:\n");
		if (isRemake) {
			dumpBuilder.append("== Re-Make New SHIM Data Parameter ==\n");
		} else {
			dumpBuilder.append("== Create New SHIM Data Parameter ==\n");
		}

		final String equal = "=";
		final String nl = "\n";

		if (!isRemake) {

			// Creates the dump str of components preferences.
			ComponentsPreferences cpParameter = preferences
					.getComponentsPreferences();
			MasterComponentPreferences cpMasterParameter = preferences
					.getMasterComponentPreferences();
			SlaveComponentPreferences cpSlaveParameter = preferences
					.getSlaveComponentPreferences();
			ComponentSetPreferences cpComponentSetParameter = preferences
					.getComponentSetPreferences();
			dumpBuilder.append("1.Components\n");
			dumpBuilder.append(LabelConstants.SYSTEM_NAME + equal
					+ cpParameter.getSystemName() + nl);
			dumpBuilder.append(LabelConstants.NUMBER_OF_MASTERCOMPONENT + equal
					+ cpParameter.getNumberMaster() + nl);
			dumpBuilder.append(LabelConstants.NUMBER_OF_SLAVECOMPONENT + equal
					+ cpParameter.getNumberSlave() + nl);
			dumpBuilder.append(LabelConstants.NUMBER_OF_COMPONENTSET + equal
					+ cpParameter.getNumberComponent() + nl);
			dumpBuilder.append(LabelConstants.CLOCK_FREQUENCY + equal
					+ cpParameter.getClock() + nl);
			dumpBuilder.append("1-1.MasterComponent\n");
			dumpBuilder.append(LabelConstants.BASE_NAME + equal
					+ cpMasterParameter.getBaseName() + nl);
			dumpBuilder.append(LabelConstants.TYPE + equal
					+ MasterType.values()[cpMasterParameter.getType()] + nl);
			dumpBuilder.append(LabelConstants.ENDIAN + equal
					+ EnumUtil.getEndianType(cpMasterParameter.getEndian())
					+ nl);
			dumpBuilder.append(LabelConstants.ARCH + equal
					+ cpMasterParameter.getArch() + nl);
			dumpBuilder.append(LabelConstants.ARCH_OPT + equal
					+ cpMasterParameter.getArchOpt() + nl);
			dumpBuilder.append(LabelConstants.NUMBER_OF_CHANNEL + equal
					+ cpMasterParameter.getnChannel() + nl);
			dumpBuilder.append(LabelConstants.NUMBER_OF_THREAD + equal
					+ cpMasterParameter.getnThread() + nl);
			dumpBuilder.append(LabelConstants.CLOCK_FREQUENCY + equal
					+ cpMasterParameter.getClock() + nl);
			dumpBuilder.append("1-2.SlaveComponent\n");
			dumpBuilder.append(LabelConstants.BASE_NAME + equal
					+ cpSlaveParameter.getBaseName() + nl);
			dumpBuilder.append(LabelConstants.SIZE + equal
					+ cpSlaveParameter.getSize() + " "
					+ EnumUtil.getSizeUnitType(cpSlaveParameter.getSizeUnit())
					+ nl);
			dumpBuilder.append(LabelConstants.RW_TYPE + equal
					+ EnumUtil.getRWType(cpSlaveParameter.getRwType()) + nl);
			dumpBuilder.append("1-3.ComponentSet\n");
			dumpBuilder.append(LabelConstants.BASE_NAME + equal
					+ cpComponentSetParameter.getBaseName() + nl);
		}

		// Creates the dump str of AddressSpaces preferences.
		AddressSpacePreferences asParameter = preferences
				.getAddressSpacePreferences();
		dumpBuilder.append("2.AddressSpace\n");
		dumpBuilder.append(LabelConstants.NUMBER_OF_SUBSPACES + equal
				+ asParameter.getNumberSubSpace() + nl);
		dumpBuilder.append(LabelConstants.SIZE_OF_SUBSPACE + equal
				+ asParameter.getSizeSubSpace() + nl);
		dumpBuilder.append(LabelConstants.ADDRESSSPACE_BASE_NAME + equal
				+ asParameter.getAddressSpaceName() + nl);
		dumpBuilder.append(LabelConstants.SUBSPACE_BASE_NAME + equal
				+ asParameter.getSubSpaceName() + nl);

		// Creates the dump str of communications preferences.
		CommunicationSetPreferences csParameter = preferences
				.getCommunicationSetPreferences();
		dumpBuilder.append("3.CommunicationSet\n");
		dumpBuilder.append("3-1.InterruptCommunication\n");
		if (csParameter.isCheckInterruptCommunication()) {
			dumpBuilder.append("CREATE\n");
		} else {
			dumpBuilder.append("NOT CREATE\n");
		}
		dumpBuilder.append("3-2.EventCommunication\n");
		if (csParameter.isCheckEventCommunication()) {
			dumpBuilder.append("CREATE\n");
		} else {
			dumpBuilder.append("NOT CREATE\n");
		}
		dumpBuilder.append("3-3.FIFOCommunication\n");
		if (csParameter.isCheckFIFOCommunication()) {
			dumpBuilder.append(LabelConstants.DATA_SIZE + equal
					+ csParameter.getFifoDataSize() + " "
					+ EnumUtil.getSizeUnitType(csParameter.getFifoSizeUnit())
					+ nl);
			dumpBuilder.append(LabelConstants.QUEUE_SIZE + equal
					+ csParameter.getQueueSize() + nl);
		} else {
			dumpBuilder.append("NOT CREATE\n");
		}
		dumpBuilder.append("3-4.SharedMemoryCommunication\n");
		if (csParameter.isCheckSharedMemoryCommunication()) {
			dumpBuilder.append(LabelConstants.DATA_SIZE
					+ equal
					+ csParameter.getSharedMemoryDataSize()
					+ " "
					+ EnumUtil.getSizeUnitType(csParameter
							.getSharedMemorySizeUnit()) + nl);
			dumpBuilder.append(LabelConstants.OPERATION_TYPE + equal
					+ EnumUtil.getOperationType(csParameter.getOperationType())
					+ nl);
		} else {
			dumpBuilder.append("NOT CREATE\n");
		}
		dumpBuilder.append("3-5.SharedRegisterCommunication\n");
		if (csParameter.isCheckSharedMemoryCommunication()) {
			dumpBuilder.append(LabelConstants.DATA_SIZE
					+ equal
					+ csParameter.getSharedRegisterDataSize()
					+ " "
					+ EnumUtil.getSizeUnitType(csParameter
							.getSharedRegisterSizeUnit()) + nl);
			dumpBuilder.append(LabelConstants.DATA_SIZE + equal
					+ csParameter.getNumRegister() + nl);
		} else {
			dumpBuilder.append("NOT CREATE\n");
		}

		// Creates the dump str of cache preferences.
		CacheDataPreferences cdParameter = preferences
				.getCacheDataPreferences();
		dumpBuilder.append("4.Cache\n");
		CacheTypeSelect cacheType = CacheTypeSelect.values()[cdParameter
				.getCacheType()];
		dumpBuilder.append("Cache Type" + equal + cacheType + nl);
		switch (cacheType) {
		case NONE:
			break;
		case UNIFIED:
		case DATA:
		case INSTRUCTION:
			dumpBuilder.append("4-1.Cache("
					+ cacheType.toString().toLowerCase() + ")");
			dumpBuilder.append(LabelConstants.CACHE_COHERENCY + equal
					+ EnumUtil.getCacheCoherencyType(cdParameter.getCacheCoherenecy1()) + nl);
			dumpBuilder.append(LabelConstants.CACHE_SIZE + equal
					+ cdParameter.getCacheSize1() + " "
					+ EnumUtil.getSizeUnitType(cdParameter.getCacheSizeUnit1())
					+ nl);
			dumpBuilder.append(LabelConstants.NUMBER_OF_WAY + equal
					+ cdParameter.getNumWay1() + nl);
			dumpBuilder.append(LabelConstants.LINE_SIZE + equal
					+ cdParameter.getLineSize1() + nl);
			dumpBuilder.append(LabelConstants.LOCKDOWN_TYPE + equal
					+ EnumUtil.getLockDownType(cdParameter.getLockDownType1())
					+ nl);
			break;
		case AND:
			dumpBuilder.append("4-1.Cache(Data)");
			dumpBuilder.append(LabelConstants.CACHE_COHERENCY + equal
					+ cdParameter.getCacheCoherenecy1() + nl);
			dumpBuilder.append(LabelConstants.CACHE_SIZE + equal
					+ cdParameter.getCacheSize1() + " "
					+ EnumUtil.getSizeUnitType(cdParameter.getCacheSizeUnit1())
					+ nl);
			dumpBuilder.append(LabelConstants.NUMBER_OF_WAY + equal
					+ cdParameter.getNumWay1() + nl);
			dumpBuilder.append(LabelConstants.LINE_SIZE + equal
					+ cdParameter.getLineSize1() + nl);
			dumpBuilder.append(LabelConstants.LOCKDOWN_TYPE + equal
					+ EnumUtil.getLockDownType(cdParameter.getLockDownType1())
					+ nl);

			dumpBuilder.append("4-2.Cache(Instruction)");
			dumpBuilder.append(LabelConstants.CACHE_COHERENCY + equal
					+ cdParameter.getCacheCoherenecy2() + nl);
			dumpBuilder.append(LabelConstants.CACHE_SIZE + equal
					+ cdParameter.getCacheSize2() + " "
					+ EnumUtil.getSizeUnitType(cdParameter.getCacheSizeUnit2())
					+ nl);
			dumpBuilder.append(LabelConstants.NUMBER_OF_WAY + equal
					+ cdParameter.getNumWay2() + nl);
			dumpBuilder.append(LabelConstants.LINE_SIZE + equal
					+ cdParameter.getLineSize2() + nl);
			dumpBuilder.append(LabelConstants.LOCKDOWN_TYPE + equal
					+ EnumUtil.getLockDownType(cdParameter.getLockDownType2())
					+ nl);
		}

		// Creates the dump str of AccessTypes preferences.
		AccessTypePreferences atParameter = preferences
				.getAccessTypePreferences();
		StringBuilder rwBuilder = new StringBuilder();
		if (atParameter.isCheckR()) {
			rwBuilder.append("R");
		}
		if (atParameter.isCheckRW()) {
			if (rwBuilder.length() > 0) {
				rwBuilder.append(", ");
			}
			rwBuilder.append("RW");
		}
		if (atParameter.isCheckRWX()) {
			if (rwBuilder.length() > 0) {
				rwBuilder.append(", ");
			}
			rwBuilder.append("RWX");
		}
		if (atParameter.isCheckRX()) {
			if (rwBuilder.length() > 0) {
				rwBuilder.append(", ");
			}
			rwBuilder.append("RX");
		}
		if (atParameter.isCheckW()) {
			if (rwBuilder.length() > 0) {
				rwBuilder.append(", ");
			}
			rwBuilder.append("W");
		}
		if (atParameter.isCheckX()) {
			if (rwBuilder.length() > 0) {
				rwBuilder.append(", ");
			}
			rwBuilder.append("X");
		}

		dumpBuilder.append("5.AccessType\n");
		dumpBuilder.append(LabelConstants.RW_TYPE + equal
				+ rwBuilder.toString() + nl);
		dumpBuilder.append(LabelConstants.ACCESS_BYTE_SIZE + equal
				+ atParameter.getAccessByteSize() + nl);
		dumpBuilder.append(LabelConstants.NUMBER_OF_BURST + equal
				+ atParameter.getNBurst() + nl);

		return dumpBuilder.toString();
	}
}
