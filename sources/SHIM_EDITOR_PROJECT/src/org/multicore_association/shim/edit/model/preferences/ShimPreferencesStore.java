/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.preference.PreferenceStore;
import org.multicore_association.shim.api.CacheCoherencyType;
import org.multicore_association.shim.api.EndianType;
import org.multicore_association.shim.api.LockDownType;
import org.multicore_association.shim.api.MasterType;
import org.multicore_association.shim.api.OperationType;
import org.multicore_association.shim.api.RWType;
import org.multicore_association.shim.api.SizeUnitType;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;

/**
 * DataStore for SHIM Editor's preference.
 * 
 * @author mio
 */
public class ShimPreferencesStore {
	
	private static final Logger log = ShimLoggerUtil.getLogger(ShimPreferencesStore.class);

	private static final String NOT_SETTING_VALUE = "";

	public static enum CacheTypeSelect {
		NONE, UNIFIED, DATA, INSTRUCTION, AND
	}

	private static final ShimPreferencesStore instance = new ShimPreferencesStore();

	private PreferenceStore store = new PreferenceStore();

	private File prefFile = new File(".metadata/shim.prefs");

	/**
	 * default constructor
	 */
	private ShimPreferencesStore() {
		store = new PreferenceStore();
		try {
			if (prefFile.exists()) {
				store.load(new FileInputStream(prefFile));
			}
		} catch (Exception e) {
		}

		// -------------------------------------------------
		// Shim_WizardPage_ComponentParameter
		store.setDefault(ShimPreferencesKey.CP_SYSTEM_NAME, "System");
		store.setDefault(ShimPreferencesKey.CP_NUMBER_MASTER, 4);
		store.setDefault(ShimPreferencesKey.CP_NUMBER_SLAVE, 3);
		store.setDefault(ShimPreferencesKey.CP_NUMBER_COMPONENT, 2);
		store.setDefault(ShimPreferencesKey.CP_CLOCK, 100000000f);

		// Master tab
		store.setDefault(ShimPreferencesKey.CP_MASTER_NAME, "Core");
		store.setDefault(ShimPreferencesKey.CP_MASTER_TYPE,
				MasterType.PU.ordinal());
		store.setDefault(ShimPreferencesKey.CP_MASTER_ENDIAN,
				EndianType.LITTLE.ordinal());
		store.setDefault(ShimPreferencesKey.CP_MASTER_ARCH, "Generic");
		store.setDefault(ShimPreferencesKey.CP_MASTER_ARCH_OPT, "");
		store.setDefault(ShimPreferencesKey.CP_MASTER_NUMBER_CHANNEL, 16);
		store.setDefault(ShimPreferencesKey.CP_MASTER_NUMBER_THREAD, 1);
		store.setDefault(ShimPreferencesKey.CP_MASTER_CHECK_CLOCKFREQUENCY,
				true);
		store.setDefault(ShimPreferencesKey.CP_MASTER_CLOCK, 100000000f);

		// Slave tab
		store.setDefault(ShimPreferencesKey.CP_SLAVE_NAME, "Memory");
		store.setDefault(ShimPreferencesKey.CP_SLAVE_SIZE, 128);
		store.setDefault(ShimPreferencesKey.CP_SLAVE_SIZE_UNIT,
				SizeUnitType.KI_B.ordinal());
		store.setDefault(ShimPreferencesKey.CP_SLAVE_RWTYPE,
				RWType.RW.ordinal());

		// Slave tab
		store.setDefault(ShimPreferencesKey.CP_COMPONENTSET_NAME, "Cluster");

		// -------------------------------------------------
		// Shim_WizardPage_AddressSpaceParameter
		store.setDefault(ShimPreferencesKey.AS_NUMBER_SUBSPACE, 3);
		store.setDefault(ShimPreferencesKey.AS_SIZE_SUBSPACE, 128);
		store.setDefault(ShimPreferencesKey.AS_NAME, "AS");
		store.setDefault(ShimPreferencesKey.AS_SUBSPACE_NAME, "SS");
		store.setDefault(ShimPreferencesKey.AS_CHECK_DONT_CONNECT, false);

		// -------------------------------------------------
		// Shim_WizardPage_AddressSpace
		store.setDefault(ShimPreferencesKey.AS_START, 0);
		store.setDefault(ShimPreferencesKey.AS_END, 0);
		store.setDefault(ShimPreferencesKey.AS_ENDIAN,
				EndianType.LITTLE.ordinal());

		// -------------------------------------------------
		// Shim_WizardPage_CommunicationSet
		store.setDefault(ShimPreferencesKey.CS_CHECK_INTERRUPT, true);
		store.setDefault(ShimPreferencesKey.CS_CHECK_EVENT, true);
		store.setDefault(ShimPreferencesKey.CS_CHECK_FIFO, true);
		store.setDefault(ShimPreferencesKey.CS_FIFO_DATA_SIZE, 64);
		store.setDefault(ShimPreferencesKey.CS_FIFO_DATA_SIZE_UNIT,
				SizeUnitType.KI_B.ordinal());
		store.setDefault(ShimPreferencesKey.CS_FIFO_QUEUE_SIZE, 32);
		store.setDefault(ShimPreferencesKey.CS_CHECK_SMC, true);
		store.setDefault(ShimPreferencesKey.CS_SMC_DATA_SIZE, 128);
		store.setDefault(ShimPreferencesKey.CS_SMC_DATA_SIZE_UNIT,
				SizeUnitType.KI_B.ordinal());
		store.setDefault(ShimPreferencesKey.CS_SMC_OPERATION,
				OperationType.TAS.ordinal());
		store.setDefault(ShimPreferencesKey.CS_CHECK_SRC, true);
		store.setDefault(ShimPreferencesKey.CS_SRC_DATA_SIZE, 32);
		store.setDefault(ShimPreferencesKey.CS_SRC_DATA_SIZE_UNIT,
				SizeUnitType.KI_B.ordinal());
		store.setDefault(ShimPreferencesKey.CS_SRC_NUMBER_REGISTER, 32);
		store.setDefault(ShimPreferencesKey.CS_DONT_CONNECT, false);

		// -------------------------------------------------
		// Shim_WizardPage_Cache
		store.setDefault(ShimPreferencesKey.CD_CACHE_TYPE,
				CacheTypeSelect.UNIFIED.ordinal());
		store.setDefault(ShimPreferencesKey.CD_UNIFIED_CACHE_COHERENCY,
				CacheCoherencyType.SOFT.ordinal());
		store.setDefault(ShimPreferencesKey.CD_UNIFIED_CACHE_SIZE, 64);
		store.setDefault(ShimPreferencesKey.CD_UNIFIED_CACHE_SIZE_UNIT,
				SizeUnitType.KI_B.ordinal());
		store.setDefault(ShimPreferencesKey.CD_UNIFIED_NUMBER_WAY, 16);
		store.setDefault(ShimPreferencesKey.CD_UNIFIED_LINE_SIZE, 128);
		store.setDefault(ShimPreferencesKey.CD_UNIFIED_LOCK_DOWN_TYPE,
				LockDownType.LINE.ordinal());
		store.setDefault(ShimPreferencesKey.CD_DATA_CACHE_COHERENCY,
				CacheCoherencyType.SOFT.ordinal());
		store.setDefault(ShimPreferencesKey.CD_DATA_CACHE_SIZE, 64);
		store.setDefault(ShimPreferencesKey.CD_DATA_CACHE_SIZE_UNIT,
				SizeUnitType.KI_B.ordinal());
		store.setDefault(ShimPreferencesKey.CD_DATA_NUMBER_WAY, 16);
		store.setDefault(ShimPreferencesKey.CD_DATA_LINE_SIZE, 128);
		store.setDefault(ShimPreferencesKey.CD_DATA_LOCK_DOWN_TYPE,
				LockDownType.LINE.ordinal());
		store.setDefault(ShimPreferencesKey.CD_INSTRUCTION_CACHE_COHERENCY,
				CacheCoherencyType.SOFT.ordinal());
		store.setDefault(ShimPreferencesKey.CD_INSTRUCTION_CACHE_SIZE, 64);
		store.setDefault(ShimPreferencesKey.CD_INSTRUCTION_CACHE_SIZE_UNIT,
				SizeUnitType.KI_B.ordinal());
		store.setDefault(ShimPreferencesKey.CD_INSTRUCTION_NUMBER_WAY, 16);
		store.setDefault(ShimPreferencesKey.CD_INSTRUCTION_LINE_SIZE, 128);
		store.setDefault(ShimPreferencesKey.CD_INSTRUCTION_LOCK_DOWN_TYPE,
				LockDownType.LINE.ordinal());

		// -------------------------------------------------
		// Shim_WizardPage_AccessType
		store.setDefault(ShimPreferencesKey.AT_NAME, "AT");
		store.setDefault(ShimPreferencesKey.AT_CHECK_R, true);
		store.setDefault(ShimPreferencesKey.AT_CHECK_W, true);
		store.setDefault(ShimPreferencesKey.AT_CHECK_RW, false);
		store.setDefault(ShimPreferencesKey.AT_CHECK_RWX, false);
		store.setDefault(ShimPreferencesKey.AT_CHECK_RX, false);
		store.setDefault(ShimPreferencesKey.AT_CHECK_X, false);
		store.setDefault(ShimPreferencesKey.AT_ACCESS_BYTE_SIZE, "4,8");
		store.setDefault(ShimPreferencesKey.AT_NUMBER_BURST, 8);

		// -------------------------------------------------
		// Shim_WizardPage_Performance
		store.setDefault(ShimPreferencesKey.P_LATENCY_BEST, 10.0);
		store.setDefault(ShimPreferencesKey.P_LATENCY_TYPICAL, 10.0);
		store.setDefault(ShimPreferencesKey.P_LATENCY_WORST, 10.0);
		store.setDefault(ShimPreferencesKey.P_PITCH_BEST, 10.0);
		store.setDefault(ShimPreferencesKey.P_PITCH_TYPICAL, 10.0);
		store.setDefault(ShimPreferencesKey.P_PITCH_WORST, 10.0);

		// -------------------------------------------------
		// save preferences which you input in the wizard.
		store.setDefault(ShimPreferencesKey.WIZ_CHECK_ALWAYS, false);
		store.setDefault(ShimPreferencesKey.WIZ_CHECK_ON_REQUEST, true);
		store.setDefault(ShimPreferencesKey.WIZ_CHECK_NEVER, false);
	}

	/**
	 * get ShimPreferenceStore singleton instance.
	 * 
	 * @return ShimPreferenceStore instance
	 */
	public static ShimPreferencesStore getInstance() {
		return instance;
	}

	/**
	 * @param name
	 * @return
	 */
	public String getString(String name) {
		return store.getString(name);
	}

	public void setString(String name, String value) {
		store.setValue(name, value);
	}

	public String getDefaultString(String name) {
		return store.getDefaultString(name);
	}

	/**
	 * @param name
	 * @return
	 */
	public boolean getBoolean(String name) {
		return store.getBoolean(name);
	}

	public void setBoolean(String name, boolean value) {
		store.setValue(name, value);
	}

	public boolean getDefaultBoolean(String name) {
		return store.getDefaultBoolean(name);
	}

	/**
	 * @param name
	 * @return
	 */
	public int getInt(String name) {
		return store.getInt(name);
	}

	public void setInt(String name, int value) {
		store.setValue(name, value);
	}

	/**
	 * @param name
	 * @return
	 */
	public Integer getInteger(String name) {
		String strValue = store.getString(name);
		if (NOT_SETTING_VALUE.equals(strValue)) {
			return null;
		} else {
			return Integer.parseInt(strValue);
		}
	}

	public void setInteger(String name, Integer value) {
		if (value == null) {
			store.setValue(name, NOT_SETTING_VALUE);
		} else {
			store.setValue(name, value);
		}
	}

	public int getDefaultInt(String name) {
		return store.getDefaultInt(name);
	}

	/**
	 * @param name
	 * @return
	 */
	public long getLong(String name) {
		return store.getLong(name);
	}

	public void setLong(String name, long value) {
		store.setValue(name, value);
	}

	public long getDefaultLong(String name) {
		return store.getDefaultLong(name);
	}

	/**
	 * @param name
	 * @return
	 */
	public float getFloat(String name) {
		return store.getFloat(name);
	}

	public void setFloat(String name, float value) {
		store.setValue(name, value);
	}

	/**
	 * @param name
	 * @return
	 */
	public Float getFloatWrapper(String name) {
		String strValue = store.getString(name);
		if (NOT_SETTING_VALUE.equals(strValue)) {
			return null;
		} else {
			return Float.parseFloat(strValue);
		}
	}

	public void setFloatWrapper(String name, Float value) {
		if (value == null) {
			store.setValue(name, NOT_SETTING_VALUE);
		} else {
			store.setValue(name, value);
		}
	}

	public float getDefaultFloat(String name) {
		return store.getDefaultFloat(name);
	}

	/**
	 * @param name
	 * @return
	 */
	public double getDouble(String name) {
		return store.getDouble(name);
	}

	public void setDouble(String name, double value) {
		store.setValue(name, value);
	}

	public double getDefaultDouble(String name) {
		return store.getDefaultDouble(name);
	}

	/**
	 * @param name
	 */
	public void setToDefault(String name) {
		store.setToDefault(name);
	}

	/**
	 * save SHIM Editor's preference.
	 * 
	 * @throws IOException
	 */
	public void save() {
		try {
			if (store.needsSaving()) {
				if (!prefFile.exists()) {
					prefFile.getParentFile().mkdirs();
				}
				store.save(new FileOutputStream(prefFile), null);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Save Preferences Error.", e);
		}
	}

	/**
	 * Sets preferences to create an AccessType instance.
	 * 
	 * @param preferences
	 *            preferences to create an AccessType
	 */
	public void setAccessTypePreferences(AccessTypePreferences preferences) {
		setString(ShimPreferencesKey.AT_NAME, preferences.getBaseName());
		setBoolean(ShimPreferencesKey.AT_CHECK_R, preferences.isCheckR());
		setBoolean(ShimPreferencesKey.AT_CHECK_W, preferences.isCheckW());
		setBoolean(ShimPreferencesKey.AT_CHECK_RW, preferences.isCheckRW());
		setBoolean(ShimPreferencesKey.AT_CHECK_RWX, preferences.isCheckRWX());
		setBoolean(ShimPreferencesKey.AT_CHECK_RX, preferences.isCheckRX());
		setBoolean(ShimPreferencesKey.AT_CHECK_X, preferences.isCheckX());
		setString(ShimPreferencesKey.AT_ACCESS_BYTE_SIZE,
				preferences.getAccessByteSize());
		setInteger(ShimPreferencesKey.AT_NUMBER_BURST, preferences.getNBurst());
	}

	/**
	 * Sets preferences to create an AddressSpace instance.
	 * 
	 * @param preferences
	 *            preferences to create an AddressSpace
	 */
	public void setAddressSpacePreferences(AddressSpacePreferences preferences) {
		setInt(ShimPreferencesKey.AS_NUMBER_SUBSPACE,
				preferences.getNumberSubSpace());
		setInt(ShimPreferencesKey.AS_SIZE_SUBSPACE,
				preferences.getSizeSubSpace());
		setString(ShimPreferencesKey.AS_NAME, preferences.getAddressSpaceName());
		setString(ShimPreferencesKey.AS_SUBSPACE_NAME,
				preferences.getSubSpaceName());
		setBoolean(ShimPreferencesKey.AS_CHECK_DONT_CONNECT,
				preferences.isDontConnect());
	}

	/**
	 * Sets preferences to create a CacheData instance.
	 * 
	 * @param preferences
	 *            preferences to create a AddressSpace
	 */
	public void setCacheDataPreferences(CacheDataPreferences preferences) {
		setInt(ShimPreferencesKey.CD_CACHE_TYPE, preferences.getCacheType());

		setInt(ShimPreferencesKey.CD_DATA_CACHE_COHERENCY,
				preferences.getCacheCoherenecy1());
		setInt(ShimPreferencesKey.CD_DATA_CACHE_SIZE,
				preferences.getCacheSize1());
		setInt(ShimPreferencesKey.CD_DATA_CACHE_SIZE_UNIT,
				preferences.getCacheSizeUnit1());
		setInteger(ShimPreferencesKey.CD_DATA_NUMBER_WAY,
				preferences.getNumWay1());
		setInteger(ShimPreferencesKey.CD_DATA_LINE_SIZE,
				preferences.getLineSize1());
		setInt(ShimPreferencesKey.CD_DATA_LOCK_DOWN_TYPE,
				preferences.getLockDownType1());

		setInt(ShimPreferencesKey.CD_INSTRUCTION_CACHE_COHERENCY,
				preferences.getCacheCoherenecy2());
		setInt(ShimPreferencesKey.CD_INSTRUCTION_CACHE_SIZE,
				preferences.getCacheSize2());
		setInt(ShimPreferencesKey.CD_INSTRUCTION_CACHE_SIZE_UNIT,
				preferences.getCacheSizeUnit2());
		setInteger(ShimPreferencesKey.CD_INSTRUCTION_NUMBER_WAY,
				preferences.getNumWay2());
		setInteger(ShimPreferencesKey.CD_INSTRUCTION_LINE_SIZE,
				preferences.getLineSize2());
		setInt(ShimPreferencesKey.CD_INSTRUCTION_LOCK_DOWN_TYPE,
				preferences.getLockDownType2());
	}

	/**
	 * Sets preferences to create a CommunicationSet instance.
	 * 
	 * @param preferences
	 *            preferences to create a CommunicationSet
	 */
	public void setCommunicationSetPreferences(
			CommunicationSetPreferences preferences) {
		setBoolean(ShimPreferencesKey.CS_CHECK_INTERRUPT,
				preferences.isCheckInterruptCommunication());

		setBoolean(ShimPreferencesKey.CS_CHECK_EVENT,
				preferences.isCheckEventCommunication());

		setBoolean(ShimPreferencesKey.CS_CHECK_FIFO,
				preferences.isCheckFIFOCommunication());
		setInt(ShimPreferencesKey.CS_FIFO_DATA_SIZE,
				preferences.getFifoDataSize());
		setInt(ShimPreferencesKey.CS_FIFO_DATA_SIZE_UNIT,
				preferences.getFifoSizeUnit());
		setInt(ShimPreferencesKey.CS_FIFO_QUEUE_SIZE,
				preferences.getQueueSize());

		setBoolean(ShimPreferencesKey.CS_CHECK_SMC,
				preferences.isCheckSharedMemoryCommunication());
		setInteger(ShimPreferencesKey.CS_SMC_DATA_SIZE,
				preferences.getSharedMemoryDataSize());
		setInt(ShimPreferencesKey.CS_SMC_DATA_SIZE_UNIT,
				preferences.getSharedMemorySizeUnit());
		setInt(ShimPreferencesKey.CS_SMC_OPERATION,
				preferences.getOperationType());

		setBoolean(ShimPreferencesKey.CS_CHECK_SRC,
				preferences.isCheckSharedRegisterCommunication());
		setInt(ShimPreferencesKey.CS_SRC_DATA_SIZE,
				preferences.getSharedRegisterDataSize());
		setInt(ShimPreferencesKey.CS_SRC_DATA_SIZE_UNIT,
				preferences.getSharedRegisterSizeUnit());
		setInt(ShimPreferencesKey.CS_SRC_NUMBER_REGISTER,
				preferences.getNumRegister());

		setBoolean(ShimPreferencesKey.CS_DONT_CONNECT,
				preferences.isDontConnect());
	}

	/**
	 * Sets preferences to create a SystemConfiguration instance and components.
	 * 
	 * @param preferences
	 *            preferences to create a SystemConfiguration instance and
	 *            components
	 */
	public void setComponentsPreferences(ComponentsPreferences preferences) {
		setString(ShimPreferencesKey.CP_SYSTEM_NAME,
				preferences.getSystemName());
		setInt(ShimPreferencesKey.CP_NUMBER_MASTER,
				preferences.getNumberMaster());
		setInt(ShimPreferencesKey.CP_NUMBER_SLAVE, preferences.getNumberSlave());
		setInt(ShimPreferencesKey.CP_NUMBER_COMPONENT,
				preferences.getNumberComponent());
		setFloat(ShimPreferencesKey.CP_CLOCK, preferences.getClock());
	}

	/**
	 * Sets preferences to create a ComponentSet instance.
	 * 
	 * @param preferences
	 *            preferences to create a ComponentSet instance
	 */
	public void setComponentSetPreferences(ComponentSetPreferences preferences) {
		setString(ShimPreferencesKey.CP_COMPONENTSET_NAME,
				preferences.getBaseName());
	}

	/**
	 * Sets preferences to create a MasterComponent instance.
	 * 
	 * @param preferences
	 *            preferences to create a MasterComponent instance
	 */
	public void setMasterComponentPreferences(
			MasterComponentPreferences preferences) {
		setString(ShimPreferencesKey.CP_MASTER_NAME, preferences.getBaseName());
		setInt(ShimPreferencesKey.CP_MASTER_TYPE, preferences.getType());
		setInt(ShimPreferencesKey.CP_MASTER_ENDIAN, preferences.getEndian());
		setString(ShimPreferencesKey.CP_MASTER_ARCH, preferences.getArch());
		setString(ShimPreferencesKey.CP_MASTER_ARCH_OPT,
				preferences.getArchOpt());
		setInteger(ShimPreferencesKey.CP_MASTER_NUMBER_CHANNEL,
				preferences.getnChannel());
		setInteger(ShimPreferencesKey.CP_MASTER_NUMBER_THREAD,
				preferences.getnThread());
		setBoolean(ShimPreferencesKey.CP_MASTER_CHECK_CLOCKFREQUENCY,
				preferences.isCheckClockForMaster());
		setFloat(ShimPreferencesKey.CP_MASTER_CLOCK, preferences.getClock());
	}

	/**
	 * Sets preferences to create a SlaveComponent instance.
	 * 
	 * @param preferences
	 *            preferences to create a SlaveComponent instance
	 */
	public void setSlaveComponentPreferences(
			SlaveComponentPreferences preferences) {
		setString(ShimPreferencesKey.CP_SLAVE_NAME, preferences.getBaseName());
		setInt(ShimPreferencesKey.CP_SLAVE_SIZE, preferences.getSize());
		setInt(ShimPreferencesKey.CP_SLAVE_SIZE_UNIT, preferences.getSizeUnit());
		setInt(ShimPreferencesKey.CP_SLAVE_RWTYPE, preferences.getRwType());
	}

	/**
	 * Sets preferences to create a Latency instance.
	 * 
	 * @param preferences
	 *            preferences to create a Latency instance
	 */
	public void setLatencyPreferences(PerformancePreferences preferences) {
		setFloatWrapper(ShimPreferencesKey.P_LATENCY_BEST,
				preferences.getBest());
		setFloat(ShimPreferencesKey.P_LATENCY_TYPICAL, preferences.getTypical());
		setFloatWrapper(ShimPreferencesKey.P_LATENCY_WORST,
				preferences.getWorst());
	}

	/**
	 * Sets preferences to create a Pitch instance.
	 * 
	 * @param preferences
	 *            preferences to create a Pitch instance
	 */
	public void setPitchPreferences(PerformancePreferences preferences) {
		setFloatWrapper(ShimPreferencesKey.P_PITCH_BEST, preferences.getBest());
		setFloat(ShimPreferencesKey.P_PITCH_TYPICAL, preferences.getTypical());
		setFloatWrapper(ShimPreferencesKey.P_PITCH_WORST,
				preferences.getWorst());
	}

	/**
	 * Sets preferences whether saves preferences or not.
	 * 
	 * @param preferences
	 *            preferences whether saves preferences or not.
	 */
	public void setSavePreferences(SavePolicyPreferences preferences) {
		setBoolean(ShimPreferencesKey.WIZ_CHECK_ALWAYS,
				preferences.isAlwaysSave());
		setBoolean(ShimPreferencesKey.WIZ_CHECK_ON_REQUEST,
				preferences.isSaveOnRequest());
		setBoolean(ShimPreferencesKey.WIZ_CHECK_NEVER,
				preferences.isNeverSave());
	}

}
