/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.preferences;

/**
 * The preferences to create a CommunicationSet instance. 
 */
public class CommunicationSetPreferences extends AbstracrtPreferences {

	private boolean checkInterruptCommunication;

	private boolean checkEventCommunication;

	private boolean checkFIFOCommunication;

	private int fifoDataSize;

	private int fifoSizeUnit;

	private int queueSize;

	private boolean checkSharedMemoryCommunication;

	private Integer sharedMemoryDataSize;

	private int sharedMemorySizeUnit;

	private int operationType;

	private boolean checkSharedRegisterCommunication;

	private int sharedRegisterDataSize;

	private int sharedRegisterSizeUnit;
	
	private int numRegister;
	
	private boolean dontConnect;

	public boolean isCheckInterruptCommunication() {
		return checkInterruptCommunication;
	}

	public void setCheckInterruptCommunication(boolean checkInterruptCommunication) {
		this.checkInterruptCommunication = checkInterruptCommunication;
	}

	public boolean isCheckEventCommunication() {
		return checkEventCommunication;
	}

	public void setCheckEventCommunication(boolean checkEventCommunication) {
		this.checkEventCommunication = checkEventCommunication;
	}

	public boolean isCheckFIFOCommunication() {
		return checkFIFOCommunication;
	}

	public void setCheckFIFOCommunication(boolean checkFIFOCommunication) {
		this.checkFIFOCommunication = checkFIFOCommunication;
	}

	public int getFifoDataSize() {
		return fifoDataSize;
	}

	public void setFifoDataSize(int fifoDataSize) {
		this.fifoDataSize = fifoDataSize;
	}

	public int getFifoSizeUnit() {
		return fifoSizeUnit;
	}

	public void setFifoSizeUnit(int fifoSizeUnit) {
		this.fifoSizeUnit = fifoSizeUnit;
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public boolean isCheckSharedMemoryCommunication() {
		return checkSharedMemoryCommunication;
	}

	public void setCheckSharedMemoryCommunication(
			boolean checkSharedMemoryCommunication) {
		this.checkSharedMemoryCommunication = checkSharedMemoryCommunication;
	}

	public Integer getSharedMemoryDataSize() {
		return sharedMemoryDataSize;
	}

	public void setSharedMemoryDataSize(Integer sharedMemoryDataSize) {
		this.sharedMemoryDataSize = sharedMemoryDataSize;
	}

	public int getSharedMemorySizeUnit() {
		return sharedMemorySizeUnit;
	}

	public void setSharedMemorySizeUnit(int sharedMemorySizeUnit) {
		this.sharedMemorySizeUnit = sharedMemorySizeUnit;
	}

	public int getOperationType() {
		return operationType;
	}

	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}

	public boolean isCheckSharedRegisterCommunication() {
		return checkSharedRegisterCommunication;
	}

	public void setCheckSharedRegisterCommunication(
			boolean checkSharedRegisterCommunication) {
		this.checkSharedRegisterCommunication = checkSharedRegisterCommunication;
	}

	public int getSharedRegisterDataSize() {
		return sharedRegisterDataSize;
	}

	public void setSharedRegisterDataSize(int sharedRegisterDataSize) {
		this.sharedRegisterDataSize = sharedRegisterDataSize;
	}

	public int getSharedRegisterSizeUnit() {
		return sharedRegisterSizeUnit;
	}

	public void setSharedRegisterSizeUnit(int sharedRegisterSizeUnit) {
		this.sharedRegisterSizeUnit = sharedRegisterSizeUnit;
	}

	public int getNumRegister() {
		return numRegister;
	}

	public void setNumRegister(int numRegister) {
		this.numRegister = numRegister;
	}

	public boolean isDontConnect() {
		return dontConnect;
	}

	public void setDontConnect(boolean dontConnect) {
		this.dontConnect = dontConnect;
	}
	
}
