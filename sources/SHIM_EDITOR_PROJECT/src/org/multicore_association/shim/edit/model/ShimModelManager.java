/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;

import org.multicore_association.shim.api.AbstractCommunication;
import org.multicore_association.shim.api.AccessType;
import org.multicore_association.shim.api.AccessTypeSet;
import org.multicore_association.shim.api.Accessor;
import org.multicore_association.shim.api.AddressSpace;
import org.multicore_association.shim.api.AddressSpaceSet;
import org.multicore_association.shim.api.Cache;
import org.multicore_association.shim.api.CacheType;
import org.multicore_association.shim.api.ClockFrequency;
import org.multicore_association.shim.api.CommonInstructionSet;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.Connection;
import org.multicore_association.shim.api.ConnectionSet;
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
import org.multicore_association.shim.api.Performance;
import org.multicore_association.shim.api.PerformanceSet;
import org.multicore_association.shim.api.Pitch;
import org.multicore_association.shim.api.SharedMemoryCommunication;
import org.multicore_association.shim.api.SharedRegisterCommunication;
import org.multicore_association.shim.api.SlaveComponent;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.ShimComponentTree.TreeNode;
import org.multicore_association.shim.edit.model.data.DefaultDataStore;

import com.sun.xml.internal.ws.util.StringUtils;

/**
 * The SHIM Data Model manager which includes the ability to create an instance
 * in generation management.
 */
public class ShimModelManager {

	private static final Logger log = ShimLoggerUtil
			.getLogger(ShimModelManager.class);

	private static ArrayList<SystemConfiguration> scdset = new ArrayList<SystemConfiguration>();
	private static int currentIndex = -1;

	private static ObjectFactory objectFactory = null;

	public static int masterPid_counter = 0;

	/**
	 * Constructs a new instance of Shim_ModelManager.
	 */
	private ShimModelManager() {
		// NOOP
	}

	/**
	 * Returns the index of the current model in the static model list.
	 * 
	 * @return the current index
	 */
	public static int getCurrentIndex() {
		return currentIndex;
	}

	/**
	 * Returns the model list.
	 * 
	 * @return the SHIM Data model list
	 */
	public static ArrayList<SystemConfiguration> getModelList() {
		return scdset;
	}

	/**
	 * Creates a new model in generation management.
	 * 
	 * @return the new model
	 */
	public static SystemConfiguration createSystemConfiguration() {

		if (objectFactory == null) {
			objectFactory = new ObjectFactory();
		}

		// top level and 2nd level Element
		SystemConfiguration system = objectFactory.createSystemConfiguration();

		system.setShimVersion(CommonConstants.SHIM_VERSION);
		addSystemConfiguration(system);
		return system;
	}

	/**
	 * Returns the model at the specified position in the model list.
	 * 
	 * @param index
	 *            the index of the model to return
	 * @return the model at the specified position in the model list
	 */
	public static SystemConfiguration getScd(int index) {
		if (scdset.size() > index || scdset.size() < 0) {
			return null;
		}
		return scdset.get(index);
	}

	/**
	 * Returns the model at the current index.
	 * 
	 * @return the model at the current index
	 */
	public static SystemConfiguration getCurrentScd() {
		if (currentIndex == -1 || currentIndex > scdset.size()
				|| scdset.size() < 0) {
			return null;
		}
		return scdset.get(currentIndex);
	}

	/**
	 * Inserts the model to the model list for generation management.
	 * 
	 * @param sc
	 *            the model to be inserted
	 */
	public static void addSystemConfiguration(SystemConfiguration sc) {
		scdset.add(sc);
		currentIndex++;
	}

	/**
	 * Removes the model to the model list for generation management.
	 * 
	 * @param sc
	 *            the model to remove
	 */
	public static void removeSystemConfiguration(SystemConfiguration sc) {
		if (scdset.indexOf(sc) == -1) {
			return;
		}
		scdset.remove(sc);
	}

	/**
	 * Removes the model to the model list for generation management.
	 * 
	 * @param index
	 *            the index of the model to remove
	 */
	public static void removeSystemConfiguration(int index) {
		if (index < 0 || index > scdset.size()) {
			return;
		}
		scdset.remove(index);
		if (currentIndex >= index) {
			currentIndex--;
		}
	}

	/**
	 * Returns the ObjectFactory instance using.
	 * 
	 * @return the ObjectFactory instance using
	 */
	public static ObjectFactory getObjectFactory() {
		if (objectFactory == null) {
			objectFactory = new ObjectFactory();
		}
		return objectFactory;
	}

	/**
	 * Creates a new ComponentSet instance with the name of the specified
	 * suffix.
	 * 
	 * @param subname
	 *            the suffix
	 * @return a new ComponentSet instance with the name of the specified suffix
	 */
	public static ComponentSet createComponentSet(String subname) {
		// To correspond specifiedation of Ref. 2014/06/17
		ComponentSet cs = (ComponentSet) DefaultDataStore
				.getDefaultInstance(ComponentSet.class);

		final ComponentSet dcs = (ComponentSet) DefaultDataStore
				.getDefaultInstance(ComponentSet.class);

		cs.setName(dcs.getName() + subname);
		return cs;

	}

	/**
	 * Creates a new MasterComponent instance with the name of the specified
	 * suffix.
	 * 
	 * @param subname
	 *            the suffix
	 * @return a new MasterComponent instance with the name of the specified
	 *         suffix
	 */
	public static MasterComponent createMasterComponent(String subname) {

		MasterComponent mc = (MasterComponent) DefaultDataStore
				.getDefaultInstance(MasterComponent.class);

		final MasterComponent dmc = (MasterComponent) DefaultDataStore
				.getDefaultInstance(MasterComponent.class);

		mc.setName(dmc.getName() + subname);

		mc.setPid(masterPid_counter + "");
		masterPid_counter++;

		mc.setArch(dmc.getArch());
		mc.setArchOption(dmc.getArchOption());
		mc.setNThread(dmc.getNThread());
		mc.setMasterType(dmc.getMasterType());
		mc.setEndian(dmc.getEndian());

		if (dmc.getClockFrequency() != null) {

			ClockFrequency dcf = dmc.getClockFrequency();
			ClockFrequency cf = new ClockFrequency();
			cf.setClockValue(dcf.getClockValue());

			mc.setClockFrequency(cf);
		}

		return mc;
	}

	/**
	 * Creates a new SlaveComponent instance with the name of the specified
	 * suffix.
	 * 
	 * @param subname
	 *            the suffix
	 * @return a new SlaveComponent instance with the name of the specified
	 *         suffix
	 */
	public static SlaveComponent createSlaveComponent(String subname) {
		SlaveComponent slc = (SlaveComponent) DefaultDataStore
				.getDefaultInstance(SlaveComponent.class);

		final SlaveComponent dslc = (SlaveComponent) DefaultDataStore
				.getDefaultInstance(SlaveComponent.class);

		slc.setName(dslc.getName() + subname);
		slc.setSize(dslc.getSize());
		slc.setSizeUnit(dslc.getSizeUnit());
		slc.setRwType(dslc.getRwType());

		return slc;
	}

	/**
	 * Counts number of ComponentSet in the specified ComponentSet list
	 * recursively.
	 * 
	 * @param cs_list
	 *            the ComponentSet list to count number of ComponentSet
	 * @return number of ComponentSet
	 */
	public static int countComponentSet(List<ComponentSet> cs_list) {
		log.finest("In CountComponentSet ca_list.size() = " + cs_list.size());
		if (cs_list.size() == 0) {
			return 0;
		} else {
			int count = 0;
			for (ComponentSet cset : cs_list) {
				log.finest(" cs name= " + cset.getName());
				count += countComponentSet(cset.getComponentSet());
			}
			return cs_list.size() + count;
		}
	}

	/**
	 * Counts number of MasterComponent in the specified ComponentSet
	 * recursively.
	 * 
	 * @param cs
	 *            the ComponentSet to count number of MasterComponent
	 * @return number of MasterComponent
	 */
	public static int countMasterComponent(ComponentSet cs) {
		int count = 0;
		List<MasterComponent> masterComponent = cs.getMasterComponent();
		if (masterComponent != null) {
			count += masterComponent.size();
		}
		List<ComponentSet> componentSetList = cs.getComponentSet();
		if (componentSetList != null) {
			for (ComponentSet componentSet : componentSetList) {
				count += countMasterComponent(componentSet);
			}
		}
		return count;
	}

	/**
	 * Counts number of SlaveComponent in the specified ComponentSet
	 * recursively.
	 * 
	 * @param cs
	 *            the ComponentSet to count number of SlaveComponent
	 * @return number of the specified SlaveComponent
	 */
	public static int countSlaveComponent(ComponentSet cs) {
		int count = 0;
		List<SlaveComponent> slaveComponent = cs.getSlaveComponent();
		if (slaveComponent != null) {
			count += slaveComponent.size();
		}
		List<ComponentSet> componentSetList = cs.getComponentSet();
		if (componentSetList != null) {
			for (ComponentSet componentSet : componentSetList) {
				count += countSlaveComponent(componentSet);
			}
		}
		return count;
	}

	/**
	 * Creates the list of ComponentSet that is contained in the specified
	 * SystemConfiguration.
	 * 
	 * @param sys
	 *            the SystemConfiguration to create the list of contained
	 *            ComponentSets
	 * @return the list of ComponentSet that is contained in the specified
	 *         SystemConfiguration
	 */
	public static List<ComponentSet> makeComponentSetList(
			SystemConfiguration sys) {

		List<ComponentSet> csetList = makeComponentSetList(sys
				.getComponentSet().getComponentSet());

		csetList.add(sys.getComponentSet());

		return csetList;
	}

	/**
	 * Creates the list of ComponentSet that is contained in the specified
	 * ComponentSets.
	 * 
	 * @param cs_list
	 *            the ComponentSets to create the list of contained
	 *            ComponentSets
	 * 
	 * @return the list of ComponentSet that is contained in the specified
	 *         ComponentSets
	 */
	public static List<ComponentSet> makeComponentSetList(
			List<ComponentSet> cs_list) {

		log.finest("In makeComponentSetList ca_list.size() = " + cs_list.size());

		List<ComponentSet> csetList = new ArrayList<ComponentSet>();

		if (cs_list.size() == 0) {
			return csetList;
		} else {
			for (ComponentSet cset : cs_list) {
				log.finest(" cs name= " + cset.getName());
				csetList.add(cset);
				csetList.addAll(makeComponentSetList(cset.getComponentSet()));
			}
			log.finest("Return csetList Size= " + csetList.size());
			return csetList;
		}
	}

	/**
	 * Creates the name list of SlaveComponent that is contained in the
	 * specified SystemConfiguration.
	 * 
	 * @param sys
	 *            the SystemConfiguration to create the name list of contained
	 *            SlaveComponents
	 * @return the name list of SlaveComponent that is contained in the
	 *         specified SystemConfiguration
	 */
	public static List<String> makeSlaveComponentNameList(
			SystemConfiguration sys) {

		List<String> slaveList = makeSlaveComponentNameList(sys
				.getComponentSet());

		return slaveList;
	}

	/**
	 * Creates the name list of SlaveComponent that is contained in the
	 * specified ComponentSet.
	 * 
	 * @param cs
	 *            the ComponentSet to create the name list of contained
	 *            SlaveComponents
	 * @return the name list of SlaveComponent that is contained in the
	 *         specified ComponentSet
	 */
	public static List<String> makeSlaveComponentNameList(ComponentSet cs) {

		List<String> slaveList = new ArrayList<String>();

		if (cs.getSlaveComponent().size() > 0) {
			for (SlaveComponent sl : cs.getSlaveComponent()) {
				log.finest(" Slave name= " + sl.getName());
				slaveList.add(sl.getName());
			}
		}
		if (cs.getComponentSet().size() > 0) {
			for (ComponentSet cset : cs.getComponentSet()) {
				log.finest(" cs name= " + cset.getName());
				slaveList.addAll(makeSlaveComponentNameList(cset));
			}
		}

		return slaveList;
	}

	/**
	 * Creates the name list of MasterComponent that is contained in the
	 * specified SystemConfiguration.
	 * 
	 * @param sys
	 *            the SystemConfiguration to create the name list of contained
	 *            MasterComponents
	 * @return the name list of MasterComponent that is contained in the
	 *         specified SystemConfiguration
	 */
	public static List<String> makeMasterComponentNameList(
			SystemConfiguration sys) {

		List<String> masterList = makeMasterComponentNameList(sys
				.getComponentSet());

		return masterList;
	}

	/**
	 * Creates the name list of MasterComponent that is contained in the
	 * specified ComponentSet.
	 * 
	 * @param cs
	 *            the ComponentSet to create the name list of contained
	 *            MasterComponents
	 * @return the name list of MasterComponent that is contained in the
	 *         specified ComponentSet
	 */
	private static List<String> makeMasterComponentNameList(ComponentSet cs) {

		List<String> masterList = new ArrayList<String>();

		if (cs.getMasterComponent().size() > 0) {
			for (MasterComponent sl : cs.getMasterComponent()) {
				masterList.add(sl.getName());
			}
		}
		if (cs.getComponentSet().size() > 0) {
			for (ComponentSet cset : cs.getComponentSet()) {
				log.finest(" cs name= " + cset.getName());
				masterList.addAll(makeMasterComponentNameList(cset));
			}
		}

		return masterList;
	}

	/**
	 * Creates the list of MasterComponent that is contained in the specified
	 * SystemConfiguration.
	 * 
	 * @param sys
	 *            the SystemConfiguration to create the list of contained
	 *            MasterComponents
	 * @return the list of MasterComponent that is contained in the specified
	 *         SystemConfiguration
	 */
	public static List<MasterComponent> makeMasterComponentList(
			SystemConfiguration sys) {

		List<MasterComponent> masterList = makeMasterComponentList(sys
				.getComponentSet());

		return masterList;
	}

	/**
	 * Creates the list of MasterComponent that is contained in the specified
	 * ComponentSet.
	 * 
	 * @param cs
	 *            the ComponentSet to create the list of contained
	 *            MasterComponents
	 * @return the list of MasterComponent that is contained in the specified
	 *         ComponentSet
	 */
	public static List<MasterComponent> makeMasterComponentList(ComponentSet cs) {

		List<MasterComponent> masterList = new ArrayList<MasterComponent>();

		if (cs.getMasterComponent().size() > 0) {
			for (MasterComponent mc : cs.getMasterComponent()) {
				masterList.add(mc);
			}
		}
		if (cs.getComponentSet().size() > 0) {
			for (ComponentSet cset : cs.getComponentSet()) {
				masterList.addAll(makeMasterComponentList(cset));
			}
		}

		return masterList;
	}

	/**
	 * Creates the list of SlaveComponent that is contained in the specified
	 * SystemConfiguration.
	 * 
	 * @param sys
	 *            the SystemConfiguration to create the list of contained
	 *            SlaveComponents
	 * @return the list of SlaveComponent that is contained in the specified
	 *         SystemConfiguration
	 */
	public static List<SlaveComponent> makeSlaveCmponentList(
			SystemConfiguration sys) {

		List<SlaveComponent> slaveList = makeSlaveComponentList(sys
				.getComponentSet());

		return slaveList;
	}

	/**
	 * Creates the list of SlaveComponent that is contained in the specified
	 * ComponentSet.
	 * 
	 * @param cs
	 *            the ComponentSet to create the list of contained
	 *            SlaveComponents
	 * @return the list of SlaveComponent that is contained in the specified
	 *         ComponentSet
	 */
	public static List<SlaveComponent> makeSlaveComponentList(ComponentSet cs) {

		List<SlaveComponent> slaveList = new ArrayList<SlaveComponent>();

		if (cs.getMasterComponent().size() > 0) {
			for (SlaveComponent slc : cs.getSlaveComponent()) {
				slaveList.add(slc);
			}
		}
		if (cs.getComponentSet().size() > 0) {
			for (ComponentSet cset : cs.getComponentSet()) {
				slaveList.addAll(makeSlaveComponentList(cset));
			}
		}

		return slaveList;
	}

	/**
	 * Creates the list which covered all the connections between
	 * MasterComponents.
	 * 
	 * @param dontConnect
	 *            whether make the connection with ancestors or not
	 * @return the list which covered all the connections between
	 *         MasterComponents
	 */
	public static ConnectionSet createDefaultConnectionList(boolean dontConnect) {

		SystemConfiguration system = getCurrentScd();
		ConnectionSet conset = new ConnectionSet();

		List<Connection> conlist = conset.getConnection();
		List<MasterComponent> mlist = ShimModelManager
				.makeMasterComponentList(system);

		ShimComponentTree sctree = new ShimComponentTree(system);

		for (MasterComponent fromMC : mlist) {
			for (MasterComponent toMC : mlist) {

				if (dontConnect) {
					TreeNode tfmc = sctree.getNodeByMc(fromMC);
					TreeNode ttmc = sctree.getNodeByMc(toMC);
					if (!(tfmc.getParent().isAncestor(ttmc) || ttmc.getParent()
							.isAncestor(tfmc))) {
						continue;
					}
				}

				String from = fromMC.getName();
				String to = toMC.getName();
				if (from.compareTo(to) == 0)
					continue;
				Connection cij = new Connection();
				cij.setFrom(fromMC);
				cij.setTo(toMC);

				List<Performance> performancelist = cij.getPerformance();

				Performance performance = (Performance) DefaultDataStore
						.getDefaultInstance(Performance.class);

				// create latency
				Latency latency = (Latency) DefaultDataStore
						.getDefaultInstance(Latency.class);
				performance.setLatency(latency);

				// create pitch
				Pitch pitch = (Pitch) DefaultDataStore
						.getDefaultInstance(Pitch.class);
				performance.setPitch(pitch);

				// add latency and pitch
				performancelist.add(performance);

				conlist.add(cij);
			}
		}
		return conset;
	}

	/**
	 * Sets the specified Cache instance to the MasterComponents which is
	 * included the specified ComponentSet.
	 * 
	 * @param cs
	 *            the ComponentSet which includes MasterComponents to be set
	 *            Cache
	 * @param hname
	 *            the lot number that is expressed by the '0_0_..._0' format<br>
	 *            (Start is "0")
	 * @param type
	 *            the cache type
	 * @param c1_base
	 *            the Cache instance
	 * @param c2_base
	 *            the instruction Cache instance if cache type is 'Data and
	 *            Instruction'
	 */
	public static void setCacheDataToMasterComponent(ComponentSet cs,
			String hname, String type, Cache c1_base, Cache c2_base) {

		if (cs.getMasterComponent().size() > 0) {

			int no = 0;
			for (MasterComponent mc : cs.getMasterComponent()) {

				if (type == "unified") {
					Cache c1 = (Cache) ShimModelManager.makeClone(c1_base);
					c1.setName("UnifiedCache_" + hname + "_" + no);
					mc.getCache().add(c1);
					log.finest("Cache Data Set mc name=" + mc.getName()
							+ ", cache name=" + c1.getName());
				} else if (type == "data") {
					Cache c1 = (Cache) ShimModelManager.makeClone(c1_base);
					c1.setName("DataCache_" + hname + "_" + no);
					mc.getCache().add(c1);
				} else if (type == "instruction") {
					Cache c1 = (Cache) ShimModelManager.makeClone(c1_base);
					c1.setName("InstructionCache_" + hname + "_" + no);
					mc.getCache().add(c1);
				} else {
					Cache c1 = (Cache) ShimModelManager.makeClone(c1_base);
					Cache c2 = (Cache) ShimModelManager.makeClone(c2_base);
					c1.setName("DataCache_" + hname + "_" + no);
					c2.setName("InstructionCache_" + hname + "_" + no);
					mc.getCache().add(c1);
					mc.getCache().add(c2);

				}
				no++;
			}
		}
		if (cs.getComponentSet().size() > 0) {
			int no = 0;
			for (ComponentSet cset : cs.getComponentSet()) {
				log.finest(" cs name= " + cset.getName());
				setCacheDataToMasterComponent(cset, hname + "_" + no, type,
						c1_base, c2_base);
				no++;
			}
		}

		return;
	}

	/**
	 * Sets the specified AccessType instance to the MasterComponents which is
	 * included the specified ComponentSet.
	 * 
	 * @param cs
	 *            the ComponentSet which includes MasterComponents to be set
	 *            AccessType
	 * @param hname
	 *            the lot number that is expressed by the '0_0_..._0' format<br>
	 *            (Start is "0")
	 * @param batList
	 *            all combinations of RW types and byte sizes.
	 */
	public static void setAccessTypeDataToMasterComponent(ComponentSet cs,
			String hname, List<AccessType> batList) {

		if (cs.getMasterComponent().size() > 0) {

			int mc_no = 0;
			for (MasterComponent mc : cs.getMasterComponent()) {
				AccessTypeSet atset = new AccessTypeSet();
				List<AccessType> atlist = atset.getAccessType();
				for (int bi = 0; bi < batList.size(); bi++) {
					AccessType at = (AccessType) DefaultDataStore
							.getDefaultInstance(AccessType.class);
					AccessType bat = batList.get(bi);

					at.setName("AT_" + hname + "_" + mc_no + "_" + bi);
					at.setAccessByteSize(bat.getAccessByteSize());
					at.setAlignmentByteSize(bat.getAlignmentByteSize());
					at.setNBurst(bat.getNBurst());
					at.setRwType(bat.getRwType());

					atlist.add(at);
					log.finest("AccessType Data Set mc name=" + mc.getName()
							+ ", AccessType name=" + at.getName());
				}
				mc.setAccessTypeSet(atset);
				mc_no++;
			}
		}
		if (cs.getComponentSet().size() > 0) {
			int no = 0;
			for (ComponentSet cset : cs.getComponentSet()) {
				log.finest(" cs name= " + cset.getName());
				setAccessTypeDataToMasterComponent(cset, hname + "_" + no,
						batList);
				no++;
			}
		}

		return;

	}

	/**
	 * Sets MasterSlaveBindings (all combinations of MasterComponent and
	 * SlaveComponent) to the specified SubSpace.
	 * 
	 * @param subspace
	 *            the SubSpace instance to set MasterSlaveBindings
	 * @param dontConnect
	 *            If true, does not connect siblings.
	 */
	public static void setMasterSlaveBindingToSubSpace(
			ShimComponentTree sctree, List<MasterComponent> mclist,
			List<SlaveComponent> slclist, SubSpace subspace, boolean dontConnect) {

		log.finest(">>> setMasterSlaveBindingToSubSpace");

		MasterSlaveBindingSet msbset = new MasterSlaveBindingSet();

		ArrayList<MasterSlaveBinding> msblist = (ArrayList<MasterSlaveBinding>) msbset
				.getMasterSlaveBinding();
		msblist.ensureCapacity(slclist.size());

		for (SlaveComponent slc : slclist) {

			MasterSlaveBinding msb = new MasterSlaveBinding();
			msb.setSlaveComponentRef(slc);

			ArrayList<Accessor> accsessorList = (ArrayList<Accessor>) msb
					.getAccessor();
			accsessorList.ensureCapacity(mclist.size());

			for (MasterComponent mc : mclist) {
				if (dontConnect) {
					TreeNode tn_slc = sctree.getNodeBySlc(slc);
					TreeNode tn_mc = sctree.getNodeByMc(mc);
					TreeNode tn_slc_parent = tn_slc.getParent();

					if (!(tn_slc_parent.isAncestor(tn_mc) || tn_slc_parent
							.isSibling(tn_mc))) {
						log.finest("---- not connect!!! donotConnect="
								+ dontConnect);
						log.finest("       Node["
								+ tn_slc_parent.getComponentSet().getName()
								+ "] is Not Ancestor of Node["
								+ tn_mc.getMasterComponent().getName());
						continue;
					}
				}

				Accessor accessor = (Accessor) DefaultDataStore
						.getDefaultInstance(Accessor.class);
				accessor.setMasterComponentRef(mc);

				AccessTypeSet atset = mc.getAccessTypeSet();

				List<AccessType> atlist = atset.getAccessType();

				List<PerformanceSet> psetlist = accessor.getPerformanceSet();

				PerformanceSet pset = new PerformanceSet();
				psetlist.add(pset);

				ArrayList<Performance> plist = (ArrayList<Performance>) pset
						.getPerformance();
				plist.ensureCapacity(atlist.size());

				// for number of MasterComponent's accessType
				for (AccessType at : atlist) {
					Latency latency = (Latency) DefaultDataStore
							.getDefaultInstance(Latency.class);

					Pitch pitch = (Pitch) DefaultDataStore
							.getDefaultInstance(Pitch.class);

					Performance pm = new Performance();
					pm.setAccessTypeRef(at);
					pm.setLatency(latency);
					pm.setPitch(pitch);

					plist.add(pm);
				}

				accsessorList.add(accessor);

			}
			msblist.add(msb);

		}
		subspace.setMasterSlaveBindingSet(msbset);
	}

	private static final String[] instarray = { "ret", "br", "switch",
			"indirectbr", "invoke", "resume", "unreachable", "add", "fadd",
			"sub", "fsub", "mul", "fmul", "udiv", "sdiv", "fdiv", "urem",
			"srem", "frem", "shl", "lshr", "ashr", "and", "or", "xor",
			"extractelement", "insertelement", "shufflevector",
			"extractivalue", "insertvalue", "alloca", "load", "store", "fence",
			"cmpxchg", "atomicrmw", "getelementptr", "trunc", "zext", "sext",
			"fptrunc", "fpext", "fptoui", "uitofp", "sitofp", "ptrtoint",
			"uitofp", "sitofp", "ptrtoint", "inttoptr", "bitcast", "icmp",
			"fcmp", "phi", "select", "call", "va_arg", "landingpad" };

	/**
	 * Sets CommonInstructionSet to the MasterComponents which is included the
	 * specified ComponentSet.
	 * 
	 * @param cs
	 *            the ComponentSet which includes MasterComponents to be set
	 *            CommonInstructionSet
	 * @param hname
	 *            the base name
	 */
	public static void setCommonInstructionSetToMasterComponent(
			ComponentSet cs, String hname) {
		if (cs.getMasterComponent().size() > 0) {
			for (MasterComponent mc : cs.getMasterComponent()) {
				setCommonInstructionSetToSingleMasterComponent(mc);
			}
		}
		if (cs.getComponentSet().size() > 0) {
			int no = 0;
			for (ComponentSet cset : cs.getComponentSet()) {
				log.finest(" cs name= " + cset.getName());
				setCommonInstructionSetToMasterComponent(cset, hname + "_" + no);
				no++;
			}
		}

		return;
	}

	/**
	 * Sets CommonInstructionSet to the specified MasterComponent.
	 * 
	 * @param mc
	 *            the MasterComponent to be set
	 */
	public static void setCommonInstructionSetToSingleMasterComponent(
			MasterComponent mc) {
		CommonInstructionSet ciset = new CommonInstructionSet();
		ciset.setName("LLVM Instructions");

		mc.setCommonInstructionSet(ciset);

		for (int j = 0; j < instarray.length; j++) {
			String instname = instarray[j];

			Instruction inst = new Instruction();
			inst.setName(instname);

			Latency latency = (Latency) DefaultDataStore
					.getDefaultInstance(Latency.class);

			Pitch pitch = (Pitch) DefaultDataStore
					.getDefaultInstance(Pitch.class);

			Performance pf = new Performance();
			pf.setLatency(latency);
			pf.setPitch(pitch);

			inst.setPerformance(pf);
			ciset.getInstruction().add(inst);
		}
	}

	/**
	 * Finds MasterComponent with the name which is matched with the specified
	 * string.
	 * 
	 * @param mc_name
	 *            the name to math
	 * @return Return true if MasterComponent with the name which is matched
	 *         with the specified string exists, and flase otherwise.
	 */
	public static MasterComponent findMasterComonentByName(String mc_name) {

		SystemConfiguration sys = getCurrentScd();
		List<MasterComponent> mclist = makeMasterComponentList(sys
				.getComponentSet());

		for (MasterComponent mc : mclist) {
			if (mc.getName().compareTo(mc_name) == 0) {
				return mc;
			}
		}
		return null;
	}

	/**
	 * Renames components with auto-numbering.
	 */
	public static void reNummberComponentName() {

		SystemConfiguration sys = getCurrentScd();

		log.finest("reNummberComponentName called");

		ComponentSet cb = (ComponentSet) DefaultDataStore
				.getDefaultInstance(ComponentSet.class);
		MasterComponent mb = (MasterComponent) DefaultDataStore
				.getDefaultInstance(MasterComponent.class);
		SlaveComponent sb = (SlaveComponent) DefaultDataStore
				.getDefaultInstance(SlaveComponent.class);

		reNummberComponentName(sys.getComponentSet(), 0, "0", mb.getName(),
				sb.getName(), cb.getName());

		return;
	}

	/**
	 * Renames components with auto-numbering
	 * 
	 * @param cs
	 *            the ComponentSet to be renamed
	 * @param level
	 *            the level of component tree
	 * @param hstr
	 *            the lot number that is expressed by the '0_0_..._0' format<br>
	 *            (Start is "0")
	 * @param mb
	 *            the base name of MasterComponent
	 * @param sb
	 *            the base name of SlaveComponent
	 * @param cb
	 *            the base name of ComponentSet
	 */
	public static void reNummberComponentName(ComponentSet cs, int level,
			String hstr, String mb, String sb, String cb) {

		int nc0 = 0;
		for (Cache c : cs.getCache()) {
			if (c.getCacheType() == CacheType.DATA) {
				c.setName("DataChache_" + hstr + "_" + nc0);
			} else if (c.getCacheType() == CacheType.INSTRUCTION) {
				c.setName("InstructionCache_" + hstr + "_" + nc0);
			} else if (c.getCacheType() == CacheType.UNIFIED) {
				c.setName("UnifiedCache_" + hstr + "_" + nc0);
			} else {
				c.setName("UnknownType_Cache_" + hstr + "_" + nc0);
			}
			log.finest(space(level + 1) + "[CS/Cache]" + c.getName());
			nc0++;
		}

		if (cs.getMasterComponent().size() > 0) {
			for (int i = 0; i < cs.getMasterComponent().size(); i++) {
				MasterComponent mc = cs.getMasterComponent().get(i);
				mc.setName(mb + "_" + hstr + "_" + i);
				log.finest(space(level) + "[MC]" + mc.getName());

				int nc = 0;
				for (Cache c : mc.getCache()) {
					if (c.getCacheType() == CacheType.DATA) {
						c.setName("DataChache_" + hstr + "_" + i + "_" + nc);
					} else if (c.getCacheType() == CacheType.INSTRUCTION) {
						c.setName("InstructionCache_" + hstr + "_" + i + "_"
								+ nc);
					} else if (c.getCacheType() == CacheType.UNIFIED) {
						c.setName("UnifiedCache_" + hstr + "_" + i + "_" + nc);
					} else {
						c.setName("UnknownType_Cache_" + hstr + "_" + i + "_"
								+ nc);
					}
					log.finest(space(level + 1) + "[MC/Cache]" + c.getName());
					nc++;
				}

				if (mc.getAccessTypeSet() != null) {
					int nat = 0;
					for (AccessType at : mc.getAccessTypeSet().getAccessType()) {
						at.setName("AT_" + hstr + "_" + i + "_" + nat);
						nat++;
					}
				}

			}
		}
		if (cs.getSlaveComponent().size() > 0) {
			for (int i = 0; i < cs.getSlaveComponent().size(); i++) {
				SlaveComponent slc = cs.getSlaveComponent().get(i);
				slc.setName(sb + "_" + hstr + "_" + i);
				log.finest(space(level) + "[SL]" + slc.getName());
			}
		}
		if (cs.getComponentSet().size() > 0) {
			for (int i = 0; i < cs.getComponentSet().size(); i++) {
				ComponentSet cset = cs.getComponentSet().get(i);
				cset.setName(cb + "_" + hstr + "_" + i);
				log.finest(space(level) + "[CS]" + cset.getName());
				reNummberComponentName(cset, level + 1, hstr + "_" + i, mb, sb,
						cb);
			}
		}
		return;
	}

	/**
	 * Returns space which length is according to the specified level.
	 * 
	 * @param level
	 *            the level of ComponentTree
	 * @return space which length is according to the specified level
	 */
	static String space(int level) {
		String s = String.format("%03d : ", level);
		for (int i = 0; i < level; i++) {
			s = s + "-";
		}
		return s;
	}

	/**
	 * Renames AddressSpaces and SubSpaces with auto-numbering.
	 */
	public static void reNumberAddressSpaceName() {

		SystemConfiguration sys = getCurrentScd();
		log.finest("reNumberAddressSpaceName called");

		AddressSpace as = (AddressSpace) DefaultDataStore
				.getDefaultInstance(AddressSpace.class);
		SubSpace ss = (SubSpace) DefaultDataStore
				.getDefaultInstance(SubSpace.class);

		reNumberAddressSpaceName(sys.getAddressSpaceSet(), 0, "0",
				as.getName(), ss.getName());

		return;
	}

	/**
	 * Renames AddressSpaces and SubSpaces with auto-numbering
	 * 
	 * @param ass
	 *            the AddressSpaceSet which contains AddressSpaces and SubSpaces
	 *            to auto-number
	 * @param level
	 *            the level of component tree
	 * @param hstr
	 *            the lot number that is expressed by the '0_0_..._0' format<br>
	 *            (Start is "0")
	 * @param asName
	 *            the base name of AddressSpace
	 * @param ssName
	 *            the base name of SubSpace
	 */
	private static void reNumberAddressSpaceName(AddressSpaceSet ass,
			int level, String hstr, String asName, String ssName) {

		int i = 0;
		List<AddressSpace> asList = ass.getAddressSpace();
		String _sp = space(level);
		for (AddressSpace as : asList) {
			as.setName(asName + "_" + hstr + "_" + i);
			log.finest(_sp + "[AS]" + as.getName());

			int j = 0;
			List<SubSpace> ssList = as.getSubSpace();
			for (SubSpace ss : ssList) {
				ss.setName(ssName + "_" + hstr + "_" + j);
				log.finest(_sp + "[SS]" + ss.getName());
				j++;
			}
			i++;
		}
		return;
	}

	/**
	 * Creates and returns a copy of the specified object.
	 * 
	 * @param data
	 *            the data to create and return copy
	 * @return the copy of the specified object
	 */
	public static Object makeClone(Object data) {
		log.finest(" makeClone called :");

		Object new_data = null;
		if (data instanceof MasterComponent) {
			log.finest(" --- makeClone MasterComponent");
			MasterComponent omc = (MasterComponent) data;

			// Changed to correspond specifiedation of Ref.
			MasterComponent mc = (MasterComponent) DefaultDataStore
					.getDefaultInstance(MasterComponent.class);

			mc.setName(omc.getName());
			mc.setArch(omc.getArch());
			mc.setArchOption(omc.getArchOption());
			mc.setMasterType(omc.getMasterType());
			mc.setNThread(omc.getNThread());
			mc.setPid(omc.getPid());
			mc.setEndian(omc.getEndian());

			mc.setId(DefaultDataStore.createId(String.valueOf(mc.hashCode()))); // Set
																				// Id
																				// from
																				// object
																				// hash
			log.finest("----makeClone(MC)-------------------Set Id from object hash -------");

			if (omc.getAccessTypeSet() != null) {
				AccessTypeSet atset = (AccessTypeSet) makeClone(omc
						.getAccessTypeSet());
				mc.setAccessTypeSet(atset);
			}
			if (omc.getClockFrequency() != null) {
				ClockFrequency cfrq = (ClockFrequency) makeClone(omc
						.getClockFrequency());
				mc.setClockFrequency(cfrq);
			}

			mc.setCommonInstructionSet(omc.getCommonInstructionSet());

			for (Cache cache : omc.getCache()) {
				Cache copyCache = (Cache) makeClone(cache);
				mc.getCache().add(copyCache);
			}
			new_data = mc;

		} else if (data instanceof SlaveComponent) {
			log.finest(" --- makeClone SlaveComponent");
			SlaveComponent oslc = (SlaveComponent) data;

			// Changed to correspond specifiedation of Ref. 2014/06/17
			SlaveComponent slc = (SlaveComponent) DefaultDataStore
					.getDefaultInstance(SlaveComponent.class);

			slc.setName(oslc.getName());
			slc.setRwType(oslc.getRwType());
			slc.setSize(oslc.getSize());
			slc.setSizeUnit(oslc.getSizeUnit());
			new_data = slc;

		} else if (data instanceof ComponentSet) {
			log.finest(" --- makeClone ComponentSet");
			ComponentSet ocset = (ComponentSet) data;
			ComponentSet cset = new ComponentSet();
			cset.setName(ocset.getName());
			for (MasterComponent mc : ocset.getMasterComponent()) {
				cset.getMasterComponent().add((MasterComponent) makeClone(mc));
			}
			for (SlaveComponent slc : ocset.getSlaveComponent()) {
				cset.getSlaveComponent().add((SlaveComponent) makeClone(slc));
			}
			for (ComponentSet c : ocset.getComponentSet()) {
				cset.getComponentSet().add((ComponentSet) makeClone(c));
			}

			new_data = cset;

		} else if (data instanceof ClockFrequency) {
			log.finest(" --- makeClone ClockFrequency");
			ClockFrequency ocf = (ClockFrequency) data;
			ClockFrequency cf = new ClockFrequency();
			cf.setClockValue(ocf.getClockValue());

			new_data = cf;

		} else if (data instanceof AccessTypeSet) {
			log.finest(" --- makeClone AccessTypeSet");

			AccessTypeSet oatset = (AccessTypeSet) data;
			AccessTypeSet atset = new AccessTypeSet();
			for (AccessType at : oatset.getAccessType()) {
				atset.getAccessType().add((AccessType) makeClone(at));
			}

			new_data = atset;

		} else if (data instanceof AccessType) {
			log.finest(" --- makeClone AccessType");

			AccessType oat = (AccessType) data;

			// Changed to correspond specifiedation of Ref. 2014/06/17
			AccessType at = (AccessType) DefaultDataStore
					.getDefaultInstance(AccessType.class);

			at.setAccessByteSize(oat.getAccessByteSize());
			at.setAlignmentByteSize(oat.getAccessByteSize());
			at.setName(oat.getName());
			at.setNBurst(oat.getNBurst());
			at.setRwType(oat.getRwType());

			new_data = at;

		} else if (data instanceof CommonInstructionSet) {
			log.finest(" --- makeClone CommonInstructionSet");

			CommonInstructionSet ociset = (CommonInstructionSet) data;
			CommonInstructionSet ciset = new CommonInstructionSet();
			ciset.setName(ociset.getName());
			for (Instruction inst : ociset.getInstruction()) {
				ciset.getInstruction().add((Instruction) makeClone(inst));
			}
			new_data = ciset;

		} else if (data instanceof Instruction) {
			log.finest(" --- makeClone Instruction");

			Instruction oinst = (Instruction) data;
			Instruction inst = new Instruction();
			inst.setName(oinst.getName());
			inst.setPerformance((Performance) makeClone(oinst.getPerformance()));

			new_data = inst;

		} else if (data instanceof Performance) {
			log.finest(" --- makeClone Performance");

			Performance opf = (Performance) data;
			Performance pf = new Performance();
			pf.setAccessTypeRef(opf.getAccessTypeRef());
			pf.setLatency((Latency) makeClone(opf.getLatency()));
			pf.setPitch((Pitch) makeClone(opf.getPitch()));

			new_data = pf;

		} else if (data instanceof Latency) {
			log.finest(" --- makeClone Latency");

			Latency olatency = (Latency) data;
			Latency latency = new Latency();
			latency.setBest(olatency.getBest());
			latency.setTypical(olatency.getTypical());
			latency.setWorst(olatency.getWorst());

			new_data = latency;

		} else if (data instanceof Pitch) {

			log.finest(" --- makeClone Pitch");

			Pitch opitch = (Pitch) data;
			Pitch pitch = new Pitch();
			pitch.setBest(opitch.getBest());
			pitch.setTypical(opitch.getTypical());
			pitch.setWorst(opitch.getWorst());

			new_data = pitch;

		} else if (data instanceof AbstractCommunication) {

			log.finest(" --- makeClone EventCommunication");

			AbstractCommunication ocom = (AbstractCommunication) data;
			AbstractCommunication com;
			try {
				com = (AbstractCommunication) data.getClass().newInstance();
				com.setName(ocom.getName());
				ConnectionSet connectionSet = ocom.getConnectionSet();
				if (connectionSet != null) {
					com.setConnectionSet((ConnectionSet) makeClone(connectionSet));
				}

				if (data instanceof FIFOCommunication) {
					FIFOCommunication ofifoCom = (FIFOCommunication) data;
					FIFOCommunication fifoCom = (FIFOCommunication) com;
					fifoCom.setDataSize(ofifoCom.getDataSize());
					fifoCom.setDataSizeUnit(ofifoCom.getDataSizeUnit());
					fifoCom.setQueueSize(ofifoCom.getQueueSize());

				} else if (data instanceof SharedMemoryCommunication) {
					SharedMemoryCommunication osharedMemoryCom = (SharedMemoryCommunication) data;
					SharedMemoryCommunication sharedMemoryCom = (SharedMemoryCommunication) com;
					sharedMemoryCom.setDataSize(osharedMemoryCom.getDataSize());
					sharedMemoryCom.setDataSizeUnit(osharedMemoryCom
							.getDataSizeUnit());
					sharedMemoryCom.setOperationType(osharedMemoryCom
							.getOperationType());

				} else if (data instanceof SharedRegisterCommunication) {
					SharedRegisterCommunication osharedRegisterCom = (SharedRegisterCommunication) data;
					SharedRegisterCommunication sharedRegisterCom = (SharedRegisterCommunication) com;
					sharedRegisterCom.setDataSize(osharedRegisterCom
							.getDataSize());
					sharedRegisterCom.setDataSizeUnit(osharedRegisterCom
							.getDataSizeUnit());
					sharedRegisterCom.setNRegister(osharedRegisterCom
							.getNRegister());

				}

				new_data = com;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		} else if (data instanceof ConnectionSet) {
			log.finest(" --- makeClone ConnectionSet");

			ConnectionSet oconset = (ConnectionSet) data;
			ConnectionSet conset = new ConnectionSet();
			for (Connection ocon : oconset.getConnection()) {
				conset.getConnection().add((Connection) makeClone(ocon));
			}
			new_data = conset;

		} else if (data instanceof Connection) {
			log.finest(" --- makeClone Connection");

			Connection ocon = (Connection) data;
			Connection con = new Connection();

			con.setFrom(ocon.getFrom());
			con.setTo(ocon.getTo());

			for (Performance pf : ocon.getPerformance()) {
				con.getPerformance().add((Performance) makeClone(pf));
			}
			new_data = con;
		} else if (data instanceof PerformanceSet) {
			log.finest(" --- makeClone PerformanceSet");

			PerformanceSet ocon = (PerformanceSet) data;
			PerformanceSet con = new PerformanceSet();

			for (Performance pf : ocon.getPerformance()) {
				con.getPerformance().add((Performance) makeClone(pf));
			}

			new_data = con;

		} else if (data instanceof Performance) {
			log.finest(" --- makeClone Performance");

			Performance ocon = (Performance) data;
			Performance con = new Performance();
			con.setAccessTypeRef(ocon.getAccessTypeRef());
			con.setLatency((Latency) makeClone(ocon.getLatency()));
			con.setPitch((Pitch) makeClone(ocon.getPitch()));

			new_data = con;
		}

		else if (data instanceof AddressSpace) {
			log.finest(" --- makeClone AddressSpace");

			AddressSpace ocon = (AddressSpace) data;

			AddressSpace con = (AddressSpace) DefaultDataStore
					.getDefaultInstance(AddressSpace.class);

			con.setName(ocon.getName());
			for (SubSpace ss : ocon.getSubSpace()) {
				con.getSubSpace().add((SubSpace) makeClone(ss));
			}
			new_data = con;

		} else if (data instanceof SubSpace) {
			log.finest(" --- makeClone SubSpace");

			SubSpace ocon = (SubSpace) data;

			SubSpace con = (SubSpace) DefaultDataStore
					.getDefaultInstance(SubSpace.class);

			con.setName(ocon.getName());
			con.setStart(ocon.getStart());
			con.setEnd(ocon.getEnd());
			con.setEndian(ocon.getEndian());
			if (ocon.getMasterSlaveBindingSet() == null) {
				con.setMasterSlaveBindingSet(new MasterSlaveBindingSet());
			} else {
				con.setMasterSlaveBindingSet((MasterSlaveBindingSet) makeClone(ocon
						.getMasterSlaveBindingSet()));
			}
			new_data = con;

		} else if (data instanceof MasterSlaveBindingSet) {
			log.finest(" --- makeClone MasterSlaveBindingSet");

			MasterSlaveBindingSet ocon = (MasterSlaveBindingSet) data;
			MasterSlaveBindingSet con = new MasterSlaveBindingSet();
			for (MasterSlaveBinding msb : ocon.getMasterSlaveBinding()) {
				con.getMasterSlaveBinding().add(
						(MasterSlaveBinding) makeClone(msb));
			}
			new_data = con;

		} else if (data instanceof MasterSlaveBinding) {
			log.finest(" --- makeClone MasterSlaveBinding");

			MasterSlaveBinding ocon = (MasterSlaveBinding) data;
			MasterSlaveBinding con = new MasterSlaveBinding();
			con.setSlaveComponentRef(ocon.getSlaveComponentRef());
			for (Accessor acc : ocon.getAccessor()) {
				con.getAccessor().add((Accessor) makeClone(acc));
			}
			new_data = con;

		} else if (data instanceof Accessor) {
			log.finest(" --- makeClone Accessor");

			Accessor ocon = (Accessor) data;
			Accessor con = new Accessor();
			con.setMasterComponentRef(ocon.getMasterComponentRef());
			for (PerformanceSet pset : ocon.getPerformanceSet()) {
				con.getPerformanceSet().add((PerformanceSet) makeClone(pset));
			}
			new_data = con;

		} else if (data instanceof Cache) {
			log.finest(" --- makeClone Cache");

			Cache ocache = (Cache) data;

			Cache cache = (Cache) DefaultDataStore
					.getDefaultInstance(Cache.class);

			cache.setName(ocache.getName());
			cache.setLineSize(ocache.getLineSize());
			cache.setCacheCoherency(ocache.getCacheCoherency());
			cache.setLockDownType(ocache.getLockDownType());
			cache.setNWay(ocache.getNWay());
			cache.setSize(ocache.getSize());
			cache.setSizeUnit(ocache.getSizeUnit());
			cache.setCacheType(ocache.getCacheType());
			for (JAXBElement<Object> cacheRef : ocache.getCacheRef()) {
				cache.getCacheRef().add(cacheRef);
			}
			new_data = cache;

		} else if (data instanceof MemoryConsistencyModel) {
			log.finest(" --- makeClone Cache");

			MemoryConsistencyModel omcModel = (MemoryConsistencyModel) data;

			MemoryConsistencyModel mcModel = (MemoryConsistencyModel) DefaultDataStore
					.getDefaultInstance(MemoryConsistencyModel.class);
			mcModel.setRarOrdering(omcModel.getRarOrdering());
			mcModel.setRawOrdering(omcModel.getRawOrdering());
			mcModel.setWarOrdering(omcModel.getWarOrdering());
			mcModel.setWawOrdering(omcModel.getWawOrdering());

			new_data = mcModel;

		} else {
			log.warning("Cannot make Clone this object yet "
					+ data.getClass().getName());
		}
		return new_data;
	}

	/**
	 * Returns whether the specified SlaveComponent and the specified
	 * MasterComponent belong to same ComponentSet.
	 * 
	 * @param slc
	 *            a SlaveComponent
	 * @param mc
	 *            a MasterComponent
	 * @return Returns true if the specified SlaveComponent and the specified
	 *         MasterComponent belong to same ComponentSet, and false otherwise.
	 */
	static boolean isAncestor(SlaveComponent slc, MasterComponent mc) {
		SystemConfiguration sys = getCurrentScd();

		ComponentSet root = sys.getComponentSet();

		ComponentSet cs1 = findComponentSet(slc, root);
		ComponentSet cs2 = findComponentSet(mc, root);

		if (cs1 == cs2) {
			return true;
		}

		for (ComponentSet cs : cs1.getComponentSet()) {
			if (cs2 == cs) {
				return true;
			}
		}

		for (ComponentSet cs : cs2.getComponentSet()) {
			if (cs1 == cs) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the ComponentSet which contains the specified SlaveComponent.
	 * 
	 * @param slc
	 *            a SlaveComponent
	 * @param root
	 *            the root ComponentSet of ComponentTree
	 * @return the ComponentSet which contains the specified SlaveComponent
	 */
	private static ComponentSet findComponentSet(SlaveComponent slc,
			ComponentSet root) {
		for (SlaveComponent sc : root.getSlaveComponent()) {
			if (sc == slc) {
				return root;
			}
		}
		for (ComponentSet cs : root.getComponentSet()) {
			ComponentSet root1 = findComponentSet(slc, cs);
			if (root1 != null) {
				return root1;
			}
		}
		return null;
	}

	/**
	 * Returns the ComponentSet which contains the specified MasterComponent.
	 * 
	 * @param mc
	 *            a MasterComponent
	 * @param root
	 *            the root ComponentSet of ComponentTree
	 * @return the ComponentSet which contains the specified MasterComponent
	 */
	private static ComponentSet findComponentSet(MasterComponent mc,
			ComponentSet root) {
		for (MasterComponent mc1 : root.getMasterComponent()) {
			if (mc == mc1) {
				return root;
			}
		}
		for (ComponentSet cs : root.getComponentSet()) {
			ComponentSet root1 = findComponentSet(mc, cs);
			if (root1 != null) {
				return root1;
			}
		}
		return null;
	}

	public static MasterComponent findParentRefMasterComponent(
			Performance performance) {
		MasterComponent result = null;

		SystemConfiguration currentScd = getCurrentScd();
		List<AddressSpace> addressSpaceList = currentScd.getAddressSpaceSet()
				.getAddressSpace();
		outer_loop: for (AddressSpace addressSpace : addressSpaceList) {
			List<SubSpace> subSpaceList = addressSpace.getSubSpace();

			for (SubSpace subSpace : subSpaceList) {
				MasterSlaveBindingSet masterSlaveBindingSet = subSpace
						.getMasterSlaveBindingSet();
				if (masterSlaveBindingSet == null) {
					continue;
				}

				List<MasterSlaveBinding> masterSlaveBindingList = masterSlaveBindingSet
						.getMasterSlaveBinding();
				for (MasterSlaveBinding masterSlaveBinding : masterSlaveBindingList) {
					List<Accessor> accessorList = masterSlaveBinding
							.getAccessor();
					for (Accessor accessor : accessorList) {
						MasterComponent masterComponentRef = (MasterComponent) accessor
								.getMasterComponentRef();

						boolean contain = false;
						List<PerformanceSet> performanceSetList = accessor
								.getPerformanceSet();
						for (PerformanceSet performanceSet : performanceSetList) {
							List<Performance> performanceList = performanceSet
									.getPerformance();
							if (performanceList.contains(performance)) {
								contain = true;
								break;
							}
						}

						if (contain) {
							result = masterComponentRef;
							break outer_loop;
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * Creates an instance of the specified class.
	 * 
	 * @param cls
	 *            the class to create an instance
	 * @param suffix
	 *            suffix of the name
	 * @return an instance of the specified class
	 */
	public static Object createObject(Class<?> cls, String suffix) {
		Object createdObj = null;

		try {
			Object clsObj = cls.newInstance();

			if (ComponentSet.class.equals(clsObj)) {
				createdObj = createComponentSet(suffix);
			} else if (MasterComponent.class.equals(clsObj)) {
				createdObj = createMasterComponent(suffix);
			} else if (SlaveComponent.class.equals(clsObj)) {
				createdObj = createSlaveComponent(suffix);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return createdObj;
	}

	/**
	 * Removes the specified object from the parent Object.
	 * 
	 * @param parentObject
	 *            parent of the object to remove
	 * @param targetObject
	 *            the object to remove
	 * @param topObject
	 *            the top object in ComponentTree
	 */
	public static void removeObject(Object parentObject, Object targetObject,
			Object topObject) {
		removeObject(parentObject, targetObject, topObject, false);
	}

	/**
	 * Removes the specified object from the parent Object.
	 * 
	 * @param parentObject
	 *            parent of the object to remove
	 * @param targetObject
	 *            the object to remove
	 * @param topObject
	 *            the top object in ComponentTree
	 * @param force
	 *            If true : Even if the object is required to be non-null value,
	 *            removes it.
	 */
	public static void removeObject(Object parentObject, Object targetObject,
			Object topObject, boolean force) {

		log.finest("------removeObject called targetObject="
				+ targetObject.getClass());

		if (parentObject instanceof List) {
			try {
				Method getid = targetObject.getClass().getMethod("getId");
				String id = (String) getid.invoke(targetObject);
				log.finest("------targetObject=" + targetObject.getClass());

				removeReferenceNodes(targetObject, id);

			} catch (NoSuchMethodException e) {

				log.finest("***TragetObject have not getId method localmessage="
						+ e.getLocalizedMessage());
				if (targetObject instanceof ComponentSet) {

					log.finest("------NEST DELETE OBJECT ------");

					ComponentSet cs = (ComponentSet) targetObject;

					for (MasterComponent mc : cs.getMasterComponent()) {
						removeObject(cs.getMasterComponent(), mc, topObject);
					}
					cs.getMasterComponent().clear();

					for (SlaveComponent sc : cs.getSlaveComponent()) {
						removeObject(cs.getSlaveComponent(), sc, topObject);
					}
					cs.getSlaveComponent().clear();

					for (Cache c : cs.getCache()) {
						removeObject(cs.getCache(), c, topObject);
					}
					cs.getCache().clear();

					for (ComponentSet cs1 : cs.getComponentSet()) {
						removeObject(cs.getComponentSet(), cs1, topObject);
					}
					cs.getComponentSet().clear();
				}

			} catch (Exception e) {
				log.log(Level.SEVERE, "Fail to remove Object.", e);
			}

		} else {
			ArrayList<?> treeList = ((ArrayList<?>) topObject);
			if (treeList.contains(targetObject)) {
				treeList.remove(targetObject);
			}

			if (targetObject instanceof AddressSpaceSet) {
				AddressSpaceSet asSet = (AddressSpaceSet) targetObject;
				removeReferenceNodes(asSet, null);
			}

			String valueName = StringUtils.decapitalize(targetObject.getClass()
					.getSimpleName());
			ShimModelAdapter.setNull(parentObject, valueName);

			try {
				Field id = targetObject.getClass().getField("id");
				log.finest("Get id =" + id);

			} catch (NoSuchFieldException e) {
				if (targetObject instanceof CommonInstructionSet) {
					((MasterComponent) parentObject)
							.setCommonInstructionSet(null);
				} else if (targetObject instanceof ClockFrequency
						&& parentObject instanceof MasterComponent) {
					((MasterComponent) parentObject).setClockFrequency(null);
				} else if (force) {
					if (targetObject instanceof AccessTypeSet) {
						List<AccessType> accessTypeList = ((AccessTypeSet) targetObject)
								.getAccessType();
						for (AccessType accessType : accessTypeList) {
							removeReferenceNodes(accessType, accessType.getId());
						}
					}
				} else {
					log.finest("TragetObject have not id field");
				}
			} catch (SecurityException e) {
				log.log(Level.SEVERE, "Fail to remove object.", e);
			}
		}
	}

	/**
	 * Object with id MasterComponent, <br>
	 * REF: Accessor, from, to, SlaveComponent, <br>
	 * REF: MasterSlaveBinding AccessType, <br>
	 * REF: Performance AddressSpace, <br>
	 * REF: SharedMemoryCommunication SubSpace <br>
	 * REF: SharedMemoryCommunication Cache <br>
	 * REF: Cache, MasterComponent
	 */
	public static void removeReferenceNodes(Object targetObject, String id) {
		// Delete ComponentSet include Cache
		if (targetObject instanceof ComponentSet) {

			ComponentSet cs = (ComponentSet) targetObject;

			for (Cache c : cs.getCache()) {
				removeReferenceNodes(c, c.getId());
			}
			for (MasterComponent mc : cs.getMasterComponent()) {
				removeReferenceNodes(mc, mc.getId());
			}
			for (SlaveComponent sc : cs.getSlaveComponent()) {
				removeReferenceNodes(sc, sc.getId());
			}
			for (ComponentSet cs1 : cs.getComponentSet()) {
				removeReferenceNodes(cs1, "");
			}

		} else
		// Delete MasterComponent include AccessType
		if (targetObject instanceof MasterComponent) {
			MasterComponent mc = (MasterComponent) targetObject;

			removeReferenceNodeFromAccessor(mc.getId());
			removeReferenceNodeFromConnection(mc.getId());

			for (AccessType at : mc.getAccessTypeSet().getAccessType()) {
				removeReferenceNodeFromPerformance(at.getId());
			}
			for (Cache c : mc.getCache()) {
				removeReferenceNodeFromCache(c.getId());
			}

		} else
		// Delete AddressSpaceSet directly then delete Performance Object
		if (targetObject instanceof AddressSpaceSet) {
			AddressSpaceSet asSet = (AddressSpaceSet) targetObject;

			List<AddressSpace> addressSpaces = asSet.getAddressSpace();
			for (AddressSpace addressSpace : addressSpaces) {
				removeReferenceNodes(addressSpace, addressSpace.getId());

				List<SubSpace> subSpaces = addressSpace.getSubSpace();
				for (SubSpace subSpace : subSpaces) {
					removeReferenceNodes(subSpace, subSpace.getId());
				}
			}
		} else
		// Delete AccessType directly then delete Performance Object
		if (targetObject instanceof AccessType) {
			AccessType at = (AccessType) targetObject;
			removeReferenceNodeFromPerformance(at.getId());
		} else if (targetObject instanceof SlaveComponent) {
			SlaveComponent slc = (SlaveComponent) targetObject;
			removeReferenceNodeFromMasterSlaveBinding(slc.getId());
		} else if (targetObject instanceof AddressSpace) {
			AddressSpace as = (AddressSpace) targetObject;
			removeReferenceNodeFromSharedMemoryCommunication_AS(as.getId());

		} else if (targetObject instanceof SubSpace) {
			SubSpace ss = (SubSpace) targetObject;
			removeReferenceNodeFromSharedMemoryCommunication_SS(ss.getId());
		} else if (targetObject instanceof Cache) {
			Cache c = (Cache) targetObject;
			removeReferenceNodeFromCache(c.getId());
		}
	}

	/**
	 * Removes the reference with specified id from Caches.
	 * 
	 * @param id
	 *            the id to remove
	 */
	public static void removeReferenceNodeFromCache(String id) {
		SystemConfiguration sys = ShimModelManager.getCurrentScd();

		removeReferenceNodeFromCacheInCS(sys.getComponentSet(), id);
	}

	/**
	 * Removes the reference with specified id from Caches.
	 * 
	 * @param cs
	 *            the ComponentSet which contains caches
	 * @param id
	 *            the id to remove
	 */
	public static void removeReferenceNodeFromCacheInCS(ComponentSet cs,
			String id) {
		for (Cache c : cs.getCache()) {
			List<Object> removeCacheRefs = new ArrayList<Object>();
			for (JAXBElement<Object> cref : c.getCacheRef()) {
				Cache csh = (Cache) cref.getValue();
				if (csh.getId().equals(id)) {
					removeCacheRefs.add(cref);
				}
			}
			for (Object cacheRef : removeCacheRefs) {
				c.getCacheRef().remove(cacheRef);
			}
		}

		for (MasterComponent mc : cs.getMasterComponent()) {
			removeReferenceNodeFromCacheInMC(mc, id);
		}
		for (ComponentSet cs1 : cs.getComponentSet()) {
			removeReferenceNodeFromCacheInCS(cs1, id);
		}
	}

	/**
	 * Removes the reference with specified id from Caches.
	 * 
	 * @param mc
	 *            the MasterComponent which contains caches
	 * @param id
	 *            the id to remove
	 */
	public static void removeReferenceNodeFromCacheInMC(MasterComponent mc,
			String id) {
		for (Cache c : mc.getCache()) {
			List<Object> removeCacheRefs = new ArrayList<Object>();
			for (JAXBElement<Object> cref : c.getCacheRef()) {
				Cache csh = (Cache) cref.getValue();
				if (csh.getId().equals(id)) {
					removeCacheRefs.add(cref);
				}
			}
			for (Object cacheRef : removeCacheRefs) {
				c.getCacheRef().remove(cacheRef);
			}
		}
	}

	/**
	 * Removes the reference with specified id from Performance.
	 * 
	 * @param id
	 *            the id to remove
	 */
	public static void removeReferenceNodeFromPerformance(String id) {

		SystemConfiguration sys = ShimModelManager.getCurrentScd();

		for (AddressSpace as : sys.getAddressSpaceSet().getAddressSpace()) {
			for (SubSpace ss : as.getSubSpace()) {
				MasterSlaveBindingSet masterSlaveBindingSet = ss
						.getMasterSlaveBindingSet();
				if (masterSlaveBindingSet == null) {
					continue;
				}

				for (MasterSlaveBinding msb : masterSlaveBindingSet
						.getMasterSlaveBinding()) {

					for (Accessor acc : msb.getAccessor()) {

						for (PerformanceSet pfset : acc.getPerformanceSet()) {
							List<Performance> removePerformances = new ArrayList<Performance>();
							for (Performance pf : pfset.getPerformance()) {
								AccessType at = (AccessType) pf
										.getAccessTypeRef();
								if (at.getId().equals(id)) {
									removePerformances.add(pf);
								}
							}
							for (Performance rempf : removePerformances) {
								pfset.getPerformance().remove(rempf);
							}
						}

					}
				}
			}
		}

	}

	/**
	 * Removes the reference with specified id from Accessor.
	 * 
	 * @param id
	 *            the id to remove
	 */
	public static void removeReferenceNodeFromAccessor(String id) {

		SystemConfiguration sys = ShimModelManager.getCurrentScd();

		for (AddressSpace as : sys.getAddressSpaceSet().getAddressSpace()) {
			for (SubSpace ss : as.getSubSpace()) {
				MasterSlaveBindingSet masterSlaveBindingSet = ss
						.getMasterSlaveBindingSet();
				if (masterSlaveBindingSet == null) {
					continue;
				}

				for (MasterSlaveBinding msb : masterSlaveBindingSet
						.getMasterSlaveBinding()) {
					List<Accessor> removeAccessors = new ArrayList<Accessor>();

					for (Accessor acc : msb.getAccessor()) {
						MasterComponent mc = (MasterComponent) acc
								.getMasterComponentRef();
						if (mc.getId().equals(id)) {
							removeAccessors.add(acc);
						}
					}
					for (Accessor racc : removeAccessors) {
						msb.getAccessor().remove(racc);
					}
				}
			}
		}
	}

	/**
	 * Removes the reference with specified id from Connection.
	 * 
	 * @param id
	 *            the id to remove
	 */
	public static void removeReferenceNodeFromConnection(String id) {
		SystemConfiguration sys = ShimModelManager.getCurrentScd();

		for (EventCommunication comm : sys.getCommunicationSet()
				.getEventCommunication()) {
			removeReferenceNodeFromConnection(id, comm);
		}
		for (FIFOCommunication comm : sys.getCommunicationSet()
				.getFIFOCommunication()) {
			removeReferenceNodeFromConnection(id, comm);
		}
		for (InterruptCommunication comm : sys.getCommunicationSet()
				.getInterruptCommunication()) {
			removeReferenceNodeFromConnection(id, comm);
		}
		for (SharedMemoryCommunication comm : sys.getCommunicationSet()
				.getSharedMemoryCommunication()) {
			removeReferenceNodeFromConnection(id, comm);
		}
		for (SharedRegisterCommunication comm : sys.getCommunicationSet()
				.getSharedRegisterCommunication()) {
			removeReferenceNodeFromConnection(id, comm);
		}
	}

	/**
	 * Removes the reference with specified id from Connection.
	 * 
	 * @param id
	 *            the id to remove
	 * @param comm
	 *            the Connection which may contain the references
	 */
	public static void removeReferenceNodeFromConnection(String id,
			AbstractCommunication comm) {
		List<Connection> removeConnections = new ArrayList<Connection>();
		ConnectionSet connectionSet = comm.getConnectionSet();
		if (connectionSet == null) {
			return;
		}

		for (Connection con : connectionSet.getConnection()) {
			MasterComponent from = (MasterComponent) con.getFrom();
			MasterComponent to = (MasterComponent) con.getTo();

			if (from.getId().equals(id) || to.getId().equals(id)) {
				removeConnections.add(con);
			}
		}
		for (Connection remcon : removeConnections) {
			connectionSet.getConnection().remove(remcon);
		}

		if (connectionSet.getConnection().isEmpty()) {
			comm.setConnectionSet(null);
			log.finest("removeReferenceNode(): remove ConnectionSet because of empty");
		}
	}

	/**
	 * Removes the reference with specified AddressSpace id from
	 * SharedMemoryCommunication.
	 * 
	 * @param id
	 *            the id to remove
	 */
	public static void removeReferenceNodeFromSharedMemoryCommunication_AS(
			String id) {

		SystemConfiguration sys = ShimModelManager.getCurrentScd();

		for (SharedMemoryCommunication smc : sys.getCommunicationSet()
				.getSharedMemoryCommunication()) {
			AddressSpace as = (AddressSpace) smc.getAddressSpaceRef();
			if (as != null && as.getId().equals(id)) {
				smc.setAddressSpaceRef(null);
				smc.setSubSpaceRef(null);
			}
		}
	}

	/**
	 * Removes the reference with specified SubSpace id from
	 * SharedMemoryCommunication.
	 * 
	 * @param id
	 *            the id to remove
	 */
	public static void removeReferenceNodeFromSharedMemoryCommunication_SS(
			String id) {

		SystemConfiguration sys = ShimModelManager.getCurrentScd();

		for (SharedMemoryCommunication smc : sys.getCommunicationSet()
				.getSharedMemoryCommunication()) {
			SubSpace ss = (SubSpace) smc.getSubSpaceRef();
			if (ss != null && ss.getId().equals(id)) {
				smc.setSubSpaceRef(null);
			}
		}
	}

	/**
	 * Removes the reference with specified SlaveComponent id from
	 * MasterSlaveBinding.
	 * 
	 * @param slcId
	 *            the id to remove
	 */
	public static void removeReferenceNodeFromMasterSlaveBinding(String slcId) {

		SystemConfiguration sys = ShimModelManager.getCurrentScd();
		for (AddressSpace as : sys.getAddressSpaceSet().getAddressSpace()) {
			for (SubSpace ss : as.getSubSpace()) {
				List<MasterSlaveBinding> removeMSB = new ArrayList<MasterSlaveBinding>();
				MasterSlaveBindingSet masterSlaveBindingSet = ss
						.getMasterSlaveBindingSet();
				if (masterSlaveBindingSet == null) {
					continue;
				}

				for (MasterSlaveBinding msb : masterSlaveBindingSet
						.getMasterSlaveBinding()) {
					SlaveComponent slc = (SlaveComponent) msb
							.getSlaveComponentRef();
					if (slc.getId().equals(slcId)) {
						removeMSB.add(msb);
					}
				}
				for (MasterSlaveBinding rmsb : removeMSB) {
					masterSlaveBindingSet.getMasterSlaveBinding().remove(rmsb);
				}

				if (masterSlaveBindingSet.getMasterSlaveBinding().isEmpty()) {
					ss.setMasterSlaveBindingSet(null);
					log.finest("removeReferenceNode(): remove MasterSlaveBindingSet because of empty");
				}
			}
		}

	}

	/**
	 * Creates and returns a new AddressSpace instance with the current
	 * preferences.
	 * 
	 * @return a new AddressSpace instance
	 */
	public static AddressSpace getDefaultAddressSpace() {
		return (AddressSpace) DefaultDataStore
				.getDefaultInstance(AddressSpace.class);
	}

	/**
	 * Creates and returns a new SubSpace instance with the current preferences.
	 * 
	 * @return a new SubSpace instance
	 */
	public static SubSpace getDefaultSubSpace() {
		return (SubSpace) DefaultDataStore.getDefaultInstance(SubSpace.class);
	}

	/**
	 * Creates and returns a new ComponentSet instance with the current
	 * preferences.
	 * 
	 * @return a new ComponentSet instance
	 */
	public static ComponentSet getDefaultComponentSet() {
		return (ComponentSet) DefaultDataStore
				.getDefaultInstance(ComponentSet.class);
	}

	/**
	 * Creates and returns a new MasterComponent instance with the current
	 * preferences.
	 * 
	 * @return a new MasterComponent instance
	 */
	public static MasterComponent getDefaultMasterComponent() {
		return (MasterComponent) DefaultDataStore
				.getDefaultInstance(MasterComponent.class);
	}

	/**
	 * Creates and returns a new SlaveComponent instance with the current
	 * preferences.
	 * 
	 * @return a new SlaveComponent instance
	 */
	public static SlaveComponent getDefaultSlaveComponent() {
		return (SlaveComponent) DefaultDataStore
				.getDefaultInstance(SlaveComponent.class);
	}

}
