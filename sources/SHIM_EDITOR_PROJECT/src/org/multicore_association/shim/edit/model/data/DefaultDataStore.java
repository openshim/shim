/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.multicore_association.shim.api.AccessType;
import org.multicore_association.shim.api.AccessTypeSet;
import org.multicore_association.shim.api.Accessor;
import org.multicore_association.shim.api.AddressSpace;
import org.multicore_association.shim.api.AddressSpaceSet;
import org.multicore_association.shim.api.Cache;
import org.multicore_association.shim.api.CacheType;
import org.multicore_association.shim.api.ClockFrequency;
import org.multicore_association.shim.api.CommonInstructionSet;
import org.multicore_association.shim.api.CommunicationSet;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.Connection;
import org.multicore_association.shim.api.ConnectionSet;
import org.multicore_association.shim.api.EndianType;
import org.multicore_association.shim.api.EventCommunication;
import org.multicore_association.shim.api.FIFOCommunication;
import org.multicore_association.shim.api.Instruction;
import org.multicore_association.shim.api.InterruptCommunication;
import org.multicore_association.shim.api.Latency;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.MasterSlaveBinding;
import org.multicore_association.shim.api.MasterSlaveBindingSet;
import org.multicore_association.shim.api.MemoryConsistencyModel;
import org.multicore_association.shim.api.ObjectFactory;
import org.multicore_association.shim.api.OrderingType;
import org.multicore_association.shim.api.Performance;
import org.multicore_association.shim.api.PerformanceSet;
import org.multicore_association.shim.api.Pitch;
import org.multicore_association.shim.api.RWType;
import org.multicore_association.shim.api.SharedMemoryCommunication;
import org.multicore_association.shim.api.SharedRegisterCommunication;
import org.multicore_association.shim.api.SizeUnitType;
import org.multicore_association.shim.api.SlaveComponent;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.EnumUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimModelManager;
import org.multicore_association.shim.edit.model.ShimObject;
import org.multicore_association.shim.edit.model.ShimPreferences;
import org.multicore_association.shim.edit.model.preferences.CacheDataPreferences;
import org.multicore_association.shim.edit.model.preferences.PerformancePreferences;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesStore.CacheTypeSelect;

import com.sun.xml.internal.ws.util.StringUtils;

/**
 * This Store contains the default values of the data to be used by this editor.
 */
public class DefaultDataStore {

	private static final Logger log = ShimLoggerUtil
			.getLogger(DefaultDataStore.class);

	public static final String ID_PREFIX = "SHIMEDITOR";

	private static final String REF_DOES_NOT_EXIST = "no data";

	public static final String CHILD_SUB_NAME = "_New";

	private static ObjectFactory factory = new ObjectFactory();

	/**
	 * Creates and returns a new instance of the specified class with the
	 * current preferences.<br>
	 * Creates also its member's instances.
	 * 
	 * @param cls
	 *            the class to create an instance
	 * @return a new instance of the specified class
	 */
	public static Object getDefaultSet(Class<?> cls) {

		Object defaultSet = ShimModelManager.createObject(cls, CHILD_SUB_NAME);
		if (defaultSet == null) {
			Object newObject = ShimModelAdapter.getNewInstance(cls);
			defaultSet = createInstance(newObject, true, false);
		}

		ArrayList<Class<?>> childrenCls = ShimModelAdapter
				.getChildren(defaultSet);

		for (Class<?> childCls : childrenCls) {
			String valueName = StringUtils.decapitalize(childCls
					.getSimpleName());

			if (ShimModelAdapter.isRequired(defaultSet, valueName)) {
				Object childDefaultSet = getDefaultSet(childCls);
				if (childDefaultSet != null) {
					setDefaultSet(defaultSet, childCls, childDefaultSet);
				}
			}
		}
		return defaultSet;
	}

	/**
	 * Sets the specified instance to default.
	 * 
	 * @param defaultSet
	 *            instance to be set to default
	 * @param childCls
	 *            the class of the defaultSet's field member
	 * @param childDefaultSet
	 *            the child (means member of defaultSet) instance to be set to
	 *            default
	 */
	public static void setDefaultSet(Object defaultSet, Class<?> childCls,
			Object childDefaultSet) {

		String valueName = StringUtils.decapitalize(childCls.getSimpleName());
		Object childObject = ShimModelAdapter.getValObject(defaultSet,
				valueName);

		if (childObject instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) childObject;
			list.add(childDefaultSet);
		} else {
			ShimModelAdapter.setValObject(defaultSet, valueName,
					childDefaultSet);
		}
	}

	/**
	 * Returns the default instance according to the specified class.
	 * 
	 * @param cls
	 *            the class to return default object
	 * @return default object
	 */
	public static Object getDefaultInstance(Class<?> cls) {
		Object newObject = ShimModelAdapter.getNewInstance(cls);
		Object defaultObject = createInstance(newObject, false, false);
		return defaultObject;
	}

	/**
	 * Creates and returns a new instance of the specified default instance.
	 * 
	 * @param _defaultSet
	 *            default instance
	 * @param needsRef
	 *            If true, sets default values to field of references.
	 * @param needsIdOnly
	 *            If true, only sets value to id.
	 * @return a new instance
	 */
	private static Object createInstance(Object _defaultSet, boolean needsRef,
			boolean needsIdOnly) {
		Object defaultSet = _defaultSet;
		if (defaultSet instanceof Accessor) {
			defaultSet = createAccessor(needsRef);
		} else if (defaultSet instanceof AccessType) {
			defaultSet = createAccessType(needsIdOnly);
		} else if (defaultSet instanceof AccessTypeSet) {
			defaultSet = createAccessTypeSet();
		} else if (defaultSet instanceof AddressSpace) {
			defaultSet = createAddressSpace(needsIdOnly);
		} else if (defaultSet instanceof AddressSpaceSet) {
			defaultSet = createAddressSpaceSet();
		} else if (defaultSet instanceof Cache) {
			defaultSet = createCache(needsIdOnly);
		} else if (defaultSet instanceof ClockFrequency) {
			defaultSet = createClockFrequency();
		} else if (defaultSet instanceof CommonInstructionSet) {
			defaultSet = createCommonInstructionSet();
		} else if (defaultSet instanceof CommunicationSet) {
			defaultSet = createCommunicationSet();
		} else if (defaultSet instanceof ComponentSet) {
			defaultSet = createComponentSet();
		} else if (defaultSet instanceof Connection) {
			defaultSet = createConnection(needsRef);
		} else if (defaultSet instanceof ConnectionSet) {
			defaultSet = createConnectionSet();
		} else if (defaultSet instanceof EventCommunication) {
			defaultSet = createEventCommunication();
		} else if (defaultSet instanceof FIFOCommunication) {
			defaultSet = createFIFOCommunication();
		} else if (defaultSet instanceof Instruction) {
			defaultSet = createInstruction();
		} else if (defaultSet instanceof InterruptCommunication) {
			defaultSet = createInterruptCommunication();
		} else if (defaultSet instanceof Latency) {
			defaultSet = createLatency();
		} else if (defaultSet instanceof SharedMemoryCommunication) {
			defaultSet = createSharedMemoryCommunication(needsRef);
		} else if (defaultSet instanceof MasterComponent) {
			defaultSet = createMasterComponent(needsIdOnly);
		} else if (defaultSet instanceof MasterSlaveBinding) {
			defaultSet = createMasterSlaveBinding(needsRef);
		} else if (defaultSet instanceof MasterSlaveBindingSet) {
			defaultSet = createMasterSlaveBindingSet();
		} else if (defaultSet instanceof MemoryConsistencyModel) {
			defaultSet = createMemoryConsistencyModel();
		} else if (defaultSet instanceof Performance) {
			defaultSet = createPerformance(needsRef);
		} else if (defaultSet instanceof PerformanceSet) {
			defaultSet = createPerformanceSet();
		} else if (defaultSet instanceof Pitch) {
			defaultSet = createPitch();
		} else if (defaultSet instanceof SharedRegisterCommunication) {
			defaultSet = createSharedRegisterCommunication();
		} else if (defaultSet instanceof SlaveComponent) {
			defaultSet = createSlaveComponent(needsIdOnly);
		} else if (defaultSet instanceof SubSpace) {
			defaultSet = createSubSpace(needsIdOnly);
		} else if (defaultSet instanceof SystemConfiguration) {
			defaultSet = createSystemConfiguration();
		} else {
			log.warning("There is no " + _defaultSet
					+ " object the appropriate.");
		}
		return defaultSet;
	}

	/**
	 * Creates and returns a new Accessor instance.
	 * 
	 * @param needsRef
	 *            If true, sets default values to field of references.
	 * @return a new Accessor instance
	 */
	private static Accessor createAccessor(boolean needsRef) {
		Accessor a = factory.createAccessor();

		if (needsRef) {
			Object rootInstance = ShimModelManager.getCurrentScd();
			List<ShimObject> list = ShimModelAdapter.getObjectsList(
					MasterComponent.class, rootInstance);

			if (list.size() > 0) {
				a.setMasterComponentRef(list.get(0).getObj());
			} else {
				a.setMasterComponentRef(REF_DOES_NOT_EXIST);
			}
		}
		return a;
	}

	/**
	 * Creates and returns a new AccessType instance.
	 * 
	 * @param needsIdOnly
	 *            If true, only sets value to id.
	 * @return a new AccessType instance
	 */
	private static AccessType createAccessType(boolean needsIdOnly) {
		ShimPreferences settings = ShimPreferences.getCurrentInstance();

		AccessType at = factory.createAccessType();
		if (!needsIdOnly) {
			at.setAccessByteSize(32);
			at.setAlignmentByteSize(32);
			at.setName(settings.getAccessTypePreferences().getBaseName());
			at.setNBurst(settings.getAccessTypePreferences().getNBurst());
			at.setRwType(RWType.values()[0]);
		}
		at.setId("AT_" + createId(String.valueOf(at.hashCode())));
		return at;
	}

	/**
	 * Creates and returns a new AccessTypeSet instance.
	 * 
	 * @return a new AccessTypeSet instance
	 */
	private static AccessTypeSet createAccessTypeSet() {
		AccessTypeSet ats = factory.createAccessTypeSet();
		return ats;
	}

	/**
	 * Creates and returns a new AddressSpace instance.
	 * 
	 * @param needsIdOnly
	 *            If true, only sets value to id.
	 * @return a new AddressSpace instance
	 */
	private static AddressSpace createAddressSpace(boolean needsIdOnly) {
		AddressSpace as = factory.createAddressSpace();
		if (!needsIdOnly) {
			as.setName(ShimPreferences.getCurrentInstance()
					.getAddressSpacePreferences().getAddressSpaceName());
		}
		as.setId("AS_" + createId(String.valueOf(as.hashCode())));
		return as;
	}

	/**
	 * Creates and returns a new AddressSpace instance.
	 * 
	 * @param needsIdOnly
	 *            If true, only sets value to id.
	 * @return a new AddressSpace instance
	 */
	private static AddressSpaceSet createAddressSpaceSet() {
		AddressSpaceSet ass = factory.createAddressSpaceSet();
		return ass;
	}

	/**
	 * Creates and returns a new Cache instance.
	 * 
	 * @param needsIdOnly
	 *            If true, only sets value to id.
	 * @return a new Cache instance
	 */
	private static Cache createCache(boolean needsIdOnly) {
		ShimPreferences settings = ShimPreferences.getCurrentInstance();
		CacheDataPreferences cdParameter = settings.getCacheDataPreferences();
		CacheTypeSelect cacheTypeSelection = CacheTypeSelect.values()[cdParameter
				.getCacheType()];
		CacheType type = CacheType.DATA;
		if (cacheTypeSelection == CacheTypeSelect.UNIFIED) {
			type = CacheType.UNIFIED;
		} else if (cacheTypeSelection == CacheTypeSelect.INSTRUCTION) {
			type = CacheType.INSTRUCTION;
		}

		Cache c = factory.createCache();
		if (!needsIdOnly) {
			c.setLineSize(128);
			c.setCacheCoherency(EnumUtil.getCacheCoherencyType(cdParameter
					.getCacheCoherenecy1()));
			c.setCacheType(type);
			c.setLockDownType(EnumUtil.getLockDownType(cdParameter
					.getLockDownType1()));
			c.setName("Cache");
			c.setNWay(cdParameter.getNumWay1());
			c.setSize(cdParameter.getCacheSize1());
			c.setSizeUnit(EnumUtil.getSizeUnitType(cdParameter
					.getCacheSizeUnit1()));
		}
		c.setId("C_" + createId(String.valueOf(c.hashCode())));
		return c;
	}

	/**
	 * Creates and returns a new ClockFrequency instance.
	 * 
	 * @return a new ClockFrequency instance
	 */
	private static ClockFrequency createClockFrequency() {
		ClockFrequency cf = factory.createClockFrequency();
		cf.setClockValue(ShimPreferences.getCurrentInstance()
				.getComponentsPreferences().getClock());
		return cf;
	}

	/**
	 * Creates and returns a new CommonInstructionSet instance.
	 * 
	 * @return a new CommonInstructionSet instance
	 */
	private static CommonInstructionSet createCommonInstructionSet() {
		CommonInstructionSet cis = factory.createCommonInstructionSet();
		cis.setName("LLVM Instructions");
		return cis;
	}

	/**
	 * Creates and returns a new CommunicationSet instance.
	 * 
	 * @return a new CommunicationSet instance
	 */
	private static CommunicationSet createCommunicationSet() {
		CommunicationSet cs = factory.createCommunicationSet();
		return cs;
	}

	/**
	 * Creates and returns a new ComponentSet instance.
	 * 
	 * @return a new ComponentSet instance
	 */
	private static ComponentSet createComponentSet() {
		ComponentSet cs = factory.createComponentSet();
		cs.setName(ShimPreferences.getCurrentInstance()
				.getComponentSetPreferences().getBaseName());
		return cs;
	}

	/**
	 * Creates and returns a new Connection instance.
	 * 
	 * @param needsRef
	 *            If true, sets default values to field of references.
	 * @return a new Connection instance
	 */
	private static Connection createConnection(boolean needsRef) {
		Connection c = factory.createConnection();
		if (needsRef) {
			Object rootInstance = ShimModelManager.getCurrentScd();
			List<ShimObject> list = ShimModelAdapter.getObjectsList(
					MasterComponent.class, rootInstance);

			if (list.size() > 1) {
				c.setFrom(list.get(0).getObj());
				c.setTo(list.get(1).getObj());
			} else {
				c.setFrom(REF_DOES_NOT_EXIST);
				c.setTo(REF_DOES_NOT_EXIST);
			}
		}
		return c;
	}

	/**
	 * Creates and returns a new ConnectionSet instance.
	 * 
	 * @return a new ConnectionSet instance
	 */
	private static ConnectionSet createConnectionSet() {
		ConnectionSet cs = factory.createConnectionSet();
		return cs;
	}

	/**
	 * Creates and returns a new EventCommunication instance.
	 * 
	 * @return a new EventCommunication instance
	 */
	private static EventCommunication createEventCommunication() {
		EventCommunication ec = factory.createEventCommunication();
		ec.setName("Event");
		return ec;
	}

	/**
	 * Creates and returns a new FIFOCommunication instance.
	 * 
	 * @return a new FIFOCommunication instance
	 */
	private static FIFOCommunication createFIFOCommunication() {
		ShimPreferences settings = ShimPreferences.getCurrentInstance();

		FIFOCommunication fc = factory.createFIFOCommunication();
		fc.setDataSize(settings.getCommunicationSetPreferences()
				.getFifoDataSize());
		fc.setDataSizeUnit(EnumUtil.getSizeUnitType(settings
				.getCommunicationSetPreferences().getFifoSizeUnit()));
		fc.setName("fifo");
		fc.setQueueSize(settings.getCommunicationSetPreferences()
				.getQueueSize());
		return fc;
	}

	/**
	 * Creates and returns a new Instruction instance.
	 * 
	 * @return a new Instruction instance
	 */
	private static Instruction createInstruction() {
		Instruction i = factory.createInstruction();
		i.setName("Instruction");
		return i;
	}

	/**
	 * Creates and returns a new InterruptCommunication instance.
	 * 
	 * @return a new InterruptCommunication instance
	 */
	private static InterruptCommunication createInterruptCommunication() {
		InterruptCommunication ic = factory.createInterruptCommunication();
		ic.setName("Interrupt");
		return ic;
	}

	/**
	 * Creates and returns a new Latency instance.
	 * 
	 * @return a new Latency instance
	 */
	private static Latency createLatency() {
		ShimPreferences settings = ShimPreferences.getCurrentInstance();
		PerformancePreferences latencyParameter = settings
				.getLatencyPreferences();

		Latency l = factory.createLatency();
		l.setBest(latencyParameter.getBest());
		l.setTypical(latencyParameter.getTypical());
		l.setWorst(latencyParameter.getWorst());
		return l;
	}

	/**
	 * Creates and returns a new SharedMemoryCommunication instance.
	 * 
	 * @param needsRef
	 *            If true, sets default values to field of references.
	 * @return a new SharedMemoryCommunication instance
	 */
	private static SharedMemoryCommunication createSharedMemoryCommunication(
			boolean needsRef) {
		ShimPreferences settings = ShimPreferences.getCurrentInstance();

		SharedMemoryCommunication lc = factory
				.createSharedMemoryCommunication();
		lc.setDataSize(settings.getCommunicationSetPreferences()
				.getSharedMemoryDataSize());
		lc.setDataSizeUnit(SizeUnitType.values()[settings
				.getCommunicationSetPreferences().getSharedMemorySizeUnit()]);
		lc.setName("shmem");
		if (needsRef) {
			Object rootInstance = ShimModelManager.getCurrentScd();
			List<ShimObject> list_as = ShimModelAdapter.getObjectsList(
					AddressSpace.class, rootInstance);

			if (list_as.size() > 0) {
				AddressSpace as = (AddressSpace) list_as.get(0).getObj();
				lc.setAddressSpaceRef(as);
				if (as != null && as.getSubSpace().size() > 0) {
					lc.setSubSpaceRef(as.getSubSpace().get(0));
				} else {
					lc.setSubSpaceRef(REF_DOES_NOT_EXIST);
				}
			} else {
				lc.setAddressSpaceRef(REF_DOES_NOT_EXIST);
				lc.setSubSpaceRef(REF_DOES_NOT_EXIST);
			}
		}
		return lc;
	}

	/**
	 * Creates and returns a new MasterComponent instance.
	 * 
	 * @param needsIdOnly
	 *            If true, only sets value to id.
	 * @return a new MasterComponent instance
	 */
	private static MasterComponent createMasterComponent(boolean needsIdOnly) {
		ShimPreferences settings = ShimPreferences.getCurrentInstance();

		MasterComponent mc = factory.createMasterComponent();
		if (!needsIdOnly) {
			mc.setArch(settings.getMasterComponentPreferences().getArch());
			mc.setMasterType(EnumUtil.getMasterType(settings
					.getMasterComponentPreferences().getType()));

			mc.setName(settings.getMasterComponentPreferences().getBaseName());
			mc.setNThread(settings.getMasterComponentPreferences().getnThread());
			mc.setPid("64");
			mc.setEndian(EnumUtil.getEndianType(settings
					.getMasterComponentPreferences().getEndian()));

			if (settings.getMasterComponentPreferences()
					.isCheckClockForMaster()) {
				ClockFrequency cf = createClockFrequency();
				cf.setClockValue(settings.getMasterComponentPreferences()
						.getClock());
				mc.setClockFrequency(cf);
			}
		}
		mc.setId("MC_" + createId(String.valueOf(mc.hashCode())));
		return mc;
	}

	/**
	 * Creates and returns a new MasterSlaveBinding instance.
	 * 
	 * @param needsRef
	 *            If true, sets default values to field of references.
	 * @return a new MasterSlaveBinding instance
	 */
	private static MasterSlaveBinding createMasterSlaveBinding(boolean needsRef) {
		MasterSlaveBinding msb = factory.createMasterSlaveBinding();

		if (needsRef) {
			Object rootInstance = ShimModelManager.getCurrentScd();
			List<ShimObject> list = ShimModelAdapter.getObjectsList(
					SlaveComponent.class, rootInstance);

			if (list.size() > 0) {
				msb.setSlaveComponentRef(list.get(0).getObj());
			} else {
				msb.setSlaveComponentRef(REF_DOES_NOT_EXIST);
			}
		}
		return msb;
	}

	/**
	 * Creates and returns a new MasterSlaveBindingSet instance.
	 * 
	 * @return a new MasterSlaveBindingSet instance
	 */
	private static MasterSlaveBindingSet createMasterSlaveBindingSet() {
		MasterSlaveBindingSet msbs = factory.createMasterSlaveBindingSet();
		return msbs;
	}

	/**
	 * Creates and returns a new MemoryConsistencyModel instance.
	 * 
	 * @return a new MemoryConsistencyModel instance
	 */
	private static MemoryConsistencyModel createMemoryConsistencyModel() {
		MemoryConsistencyModel mcm = factory.createMemoryConsistencyModel();
		mcm.setRarOrdering(OrderingType.values()[0]);
		mcm.setRawOrdering(OrderingType.values()[0]);
		mcm.setWarOrdering(OrderingType.values()[0]);
		mcm.setWawOrdering(OrderingType.values()[0]);
		return mcm;
	}

	/**
	 * Creates and returns a new Performance instance.
	 * 
	 * @return a new Performance instance
	 */
	private static Performance createPerformance(boolean needsRef) {
		Performance p = factory.createPerformance();
		if (needsRef) {
			Object rootInstance = ShimModelManager.getCurrentScd();
			List<ShimObject> list = ShimModelAdapter.getObjectsList(
					AccessType.class, rootInstance);

			if (list.size() > 0) {
				p.setAccessTypeRef(list.get(0).getObj());
			} else {
				p.setAccessTypeRef(REF_DOES_NOT_EXIST);
			}
		}
		return p;
	}

	/**
	 * Creates and returns a new PerformanceSet instance.
	 * 
	 * @return a new PerformanceSet instance
	 */
	private static PerformanceSet createPerformanceSet() {
		PerformanceSet ps = factory.createPerformanceSet();
		return ps;
	}

	/**
	 * Creates and returns a new Pitch instance.
	 * 
	 * @return a new Pitch instance
	 */
	private static Pitch createPitch() {
		ShimPreferences settings = ShimPreferences.getCurrentInstance();
		PerformancePreferences pitchParameter = settings.getPitchPreferences();

		Pitch p = factory.createPitch();
		p.setBest(pitchParameter.getBest());
		p.setTypical(pitchParameter.getTypical());
		p.setWorst(pitchParameter.getWorst());
		return p;
	}

	/**
	 * Creates and returns a new SharedRegisterCommunication instance.
	 * 
	 * @return a new SharedRegisterCommunication instance
	 */
	private static SharedRegisterCommunication createSharedRegisterCommunication() {
		ShimPreferences settings = ShimPreferences.getCurrentInstance();

		SharedRegisterCommunication src = factory
				.createSharedRegisterCommunication();
		src.setDataSize(settings.getCommunicationSetPreferences()
				.getSharedRegisterDataSize());
		src.setDataSizeUnit(EnumUtil.getSizeUnitType(settings
				.getCommunicationSetPreferences().getSharedRegisterSizeUnit()));
		src.setName("sreg");
		src.setNRegister(settings.getCommunicationSetPreferences()
				.getNumRegister());
		return src;
	}

	/**
	 * Creates and returns a new SlaveComponent instance.
	 * 
	 * @return a new SlaveComponent instance
	 */
	private static SlaveComponent createSlaveComponent(boolean needsIdOnly) {
		ShimPreferences settings = ShimPreferences.getCurrentInstance();

		SlaveComponent sc = factory.createSlaveComponent();
		if (!needsIdOnly) {
			sc.setName(settings.getSlaveComponentPreferences().getBaseName());
			sc.setRwType(RWType.values()[settings
					.getSlaveComponentPreferences().getRwType()]);
			sc.setSize(settings.getSlaveComponentPreferences().getSize());
			sc.setSizeUnit(EnumUtil.getSizeUnitType(settings
					.getSlaveComponentPreferences().getSizeUnit()));
		}
		sc.setId("SC_" + createId(String.valueOf(sc.hashCode())));
		return sc;
	}

	/**
	 * Creates and returns a new SubSpace instance.
	 * 
	 * @return a new SubSpace instance
	 */
	private static SubSpace createSubSpace(boolean needsIdOnly) {
		SubSpace ss = factory.createSubSpace();
		if (!needsIdOnly) {
			ss.setName(ShimPreferences.getCurrentInstance()
					.getAddressSpacePreferences().getSubSpaceName());
			ss.setStart(0);
			ss.setEnd(127);
			ss.setEndian(EndianType.LITTLE);
		}
		ss.setId("SS_" + createId(String.valueOf(ss.hashCode())));
		return ss;
	}

	/**
	 * Creates and returns a new SystemConfiguration instance.
	 * 
	 * @return a new SystemConfiguration instance
	 */
	private static SystemConfiguration createSystemConfiguration() {
		SystemConfiguration sc = factory.createSystemConfiguration();
		sc.setName(ShimPreferences.getCurrentInstance()
				.getComponentsPreferences().getSystemName());
		return sc;
	}

	/**
	 * Creates and returns a new id from hash code.
	 * 
	 * @param code
	 *            hash code
	 * @return a new id
	 */
	public static String createId(String code) {
		Calendar c = Calendar.getInstance();

		Random rnd = new Random();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		StringBuffer id = new StringBuffer(ID_PREFIX);
		id.append(code);
		id.append(String.format("%1$04d", rnd.nextInt(9999)));
		id.append(sdf.format(c.getTime()));

		log.finest("[DEBUG] created id=" + id.toString());
		return id.toString();
	}

}
