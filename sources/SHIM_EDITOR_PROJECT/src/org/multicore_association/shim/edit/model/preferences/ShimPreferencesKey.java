/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.preferences;

/**
 * A Constant Class for names of each preference.
 */
public class ShimPreferencesKey {

	/**
	 * Shim_WizardPage_ComponentParameter
	 */
	public static String CP_SYSTEM_NAME = "component.parameter.system.name";

	public static String CP_NUMBER_MASTER = "component.parameter.number.master";

	public static String CP_NUMBER_SLAVE = "component.parameter.number.slave";

	public static String CP_NUMBER_COMPONENT = "component.parameter.number.component";

	public static String CP_CLOCK = "component.parameter.clock";

	// Master tab
	public static String CP_MASTER_NAME = "component.parameter.master.name";

	public static String CP_MASTER_TYPE = "component.parameter.master.type";

	public static String CP_MASTER_ENDIAN = "component.parameter.master.endian";

	public static String CP_MASTER_ARCH = "component.parameter.master.arch";

	public static String CP_MASTER_ARCH_OPT = "component.parameter.master.arch.opt";

	public static String CP_MASTER_NUMBER_CHANNEL = "component.parameter.master.number.channel";

	public static String CP_MASTER_NUMBER_THREAD = "component.parameter.master.number.thread";

	public static String CP_MASTER_CHECK_CLOCKFREQUENCY = "component.parameter.master.check.clockfrequency";

	public static String CP_MASTER_CLOCK = "component.parameter.master.clock";

	// Slave tab
	public static String CP_SLAVE_NAME = "component.parameter.slave.name";

	public static String CP_SLAVE_SIZE = "component.parameter.slave.size";

	public static String CP_SLAVE_SIZE_UNIT = "component.parameter.slave.unit";

	public static String CP_SLAVE_RWTYPE = "component.parameter.slave.rwtype";

	// ComponentSet tab
	public static String CP_COMPONENTSET_NAME = "component.parameter.componentset.name";

	/**
	 * Shim_WizardPage_AddressSpaceParameter
	 */
	public static String AS_NUMBER_SUBSPACE = "address.space.number.subspace";

	public static String AS_SIZE_SUBSPACE = "address.space.size.subspace";

	public static String AS_NAME = "address.space.name";

	public static String AS_SUBSPACE_NAME = "address.space.subspace.name";

	public static String AS_CHECK_DONT_CONNECT = "address.space.dont.connect";

	/**
	 * Shim_WizardPage_AddressSpace
	 */
	public static String AS_START = "address.space.start";

	public static String AS_END = "address.space.end";

	public static String AS_ENDIAN = "address.space.endian";

	/**
	 * Shim_WizardPage_CommunicationSet
	 */
	public static String CS_CHECK_INTERRUPT = "communication.set.check.interrupt";

	public static String CS_CHECK_EVENT = "communication.set.check.event";

	public static String CS_CHECK_FIFO = "communication.set.check.fifo";

	public static String CS_FIFO_DATA_SIZE = "communication.set.fifo.data.size";

	public static String CS_FIFO_DATA_SIZE_UNIT = "communication.set.fifo.data.size.unit";

	public static String CS_FIFO_QUEUE_SIZE = "communication.set.fifo.queue.size";

	public static String CS_CHECK_SMC = "communication.set.check.smc";

	public static String CS_SMC_DATA_SIZE = "communication.set.smc.data.size";

	public static String CS_SMC_DATA_SIZE_UNIT = "communication.set.smc.data.size.unit";

	public static String CS_SMC_OPERATION = "communication.set.smc.operation";

	public static String CS_CHECK_SRC = "communication.set.check.src";

	public static String CS_SRC_DATA_SIZE = "communication.set.src.data.size";

	public static String CS_SRC_DATA_SIZE_UNIT = "communication.set.src.data.size.unit";

	public static String CS_SRC_NUMBER_REGISTER = "communication.set.src.number.register";

	public static String CS_DONT_CONNECT = "communication.set.dont.connect";

	/**
	 * Shim_WizardPage_Cache
	 */
	public static String CD_CACHE_TYPE = "cache.type";

	// unified
	public static String CD_UNIFIED_CACHE_COHERENCY = "cache.unified.coherency";

	public static String CD_UNIFIED_CACHE_SIZE = "cache.unified.size";

	public static String CD_UNIFIED_CACHE_SIZE_UNIT = "cache.unified.size.unit";

	public static String CD_UNIFIED_NUMBER_WAY = "cache.unified.number.way";

	public static String CD_UNIFIED_LINE_SIZE = "cache.unified.line.size";

	public static String CD_UNIFIED_LOCK_DOWN_TYPE = "cache.unified.lock.down.type";

	// data
	public static String CD_DATA_CACHE_COHERENCY = "cache.data.coherency";

	public static String CD_DATA_CACHE_SIZE = "cache.data.size";

	public static String CD_DATA_CACHE_SIZE_UNIT = "cache.data.size.unit";

	public static String CD_DATA_NUMBER_WAY = "cache.data.number.way";

	public static String CD_DATA_LINE_SIZE = "cache.data.line.size";

	public static String CD_DATA_LOCK_DOWN_TYPE = "cache.data.lock.down.type";

	// instruction
	public static String CD_INSTRUCTION_CACHE_COHERENCY = "cache.instruction.coherency";

	public static String CD_INSTRUCTION_CACHE_SIZE = "cache.instruction.size";

	public static String CD_INSTRUCTION_CACHE_SIZE_UNIT = "cache.instruction.size.unit";

	public static String CD_INSTRUCTION_NUMBER_WAY = "cache.instruction.number.way";

	public static String CD_INSTRUCTION_LINE_SIZE = "cache.instruction.line.size";

	public static String CD_INSTRUCTION_LOCK_DOWN_TYPE = "cache.instruction.lock.down.type";

	/**
	 * Shim_WizardPage_AccessType
	 */
	public static String AT_NAME = "access.type.name";

	public static String AT_CHECK_R = "access.type.check.read.only";

	public static String AT_CHECK_W = "access.type.check.write.only";

	public static String AT_CHECK_RW = "access.type.check.read.write";

	public static String AT_CHECK_RWX = "access.type.check.read.write.execute";

	public static String AT_CHECK_RX = "access.type.check.read.execute";

	public static String AT_CHECK_X = "access.type.check.execute.only";

	public static String AT_ACCESS_BYTE_SIZE = "access.type.byte.size";

	public static String AT_NUMBER_BURST = "access.type.number.burst";

	/**
	 * Shim_WizardPage_Performance
	 */
	public static String P_LATENCY_BEST = "performance.latency.best";

	public static String P_LATENCY_TYPICAL = "performance.latency.typical";

	public static String P_LATENCY_WORST = "performance.latency.worst";

	public static String P_PITCH_BEST = "performance.pitch.best";

	public static String P_PITCH_TYPICAL = "performance.pitch.typical";

	public static String P_PITCH_WORST = "performance.pitch.worst";

	/**
	 * save preferences which you input in the wizard.
	 */
	public static String WIZ_CHECK_ALWAYS = "wizard.save.always";

	public static String WIZ_CHECK_ON_REQUEST = "wizard.save.on.request";

	public static String WIZ_CHECK_NEVER = "wizard.save.never";

}
