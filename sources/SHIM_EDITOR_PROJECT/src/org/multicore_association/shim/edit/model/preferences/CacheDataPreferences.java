/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.preferences;

/**
 * The preferences to create a Cache instance. 
 */
public class CacheDataPreferences extends AbstracrtPreferences {

	private int cacheType;

	private int cacheCoherenecy1;

	private int cacheSize1;

	private int cacheSizeUnit1;

	private Integer numWay1;

	private Integer lineSize1;

	private int lockDownType1;

	private int cacheCoherenecy2;

	private int cacheSize2;

	private int cacheSizeUnit2;

	private Integer numWay2;

	private Integer lineSize2;

	private int lockDownType2;

	public int getCacheType() {
		return cacheType;
	}

	public void setCacheType(int cacheType) {
		this.cacheType = cacheType;
	}

	public int getCacheCoherenecy1() {
		return cacheCoherenecy1;
	}

	public void setCacheCoherenecy1(int cacheCoherenecy1) {
		this.cacheCoherenecy1 = cacheCoherenecy1;
	}

	public int getCacheSize1() {
		return cacheSize1;
	}

	public void setCacheSize1(int cacheSize1) {
		this.cacheSize1 = cacheSize1;
	}

	public int getCacheSizeUnit1() {
		return cacheSizeUnit1;
	}

	public void setCacheSizeUnit1(int cacheSizeUnit1) {
		this.cacheSizeUnit1 = cacheSizeUnit1;
	}

	public Integer getNumWay1() {
		return numWay1;
	}

	public void setNumWay1(Integer numWay1) {
		this.numWay1 = numWay1;
	}

	public Integer getLineSize1() {
		return lineSize1;
	}

	public void setLineSize1(Integer lineSize1) {
		this.lineSize1 = lineSize1;
	}

	public int getLockDownType1() {
		return lockDownType1;
	}

	public void setLockDownType1(int lockDownType1) {
		this.lockDownType1 = lockDownType1;
	}

	public int getCacheCoherenecy2() {
		return cacheCoherenecy2;
	}

	public void setCacheCoherenecy2(int cacheCoherenecy2) {
		this.cacheCoherenecy2 = cacheCoherenecy2;
	}

	public int getCacheSize2() {
		return cacheSize2;
	}

	public void setCacheSize2(int cacheSize2) {
		this.cacheSize2 = cacheSize2;
	}

	public int getCacheSizeUnit2() {
		return cacheSizeUnit2;
	}

	public void setCacheSizeUnit2(int cacheSizeUnit2) {
		this.cacheSizeUnit2 = cacheSizeUnit2;
	}

	public Integer getNumWay2() {
		return numWay2;
	}

	public void setNumWay2(Integer numWay2) {
		this.numWay2 = numWay2;
	}

	public Integer getLineSize2() {
		return lineSize2;
	}

	public void setLineSize2(Integer lineSize2) {
		this.lineSize2 = lineSize2;
	}

	public int getLockDownType2() {
		return lockDownType2;
	}

	public void setLockDownType2(int lockDownType2) {
		this.lockDownType2 = lockDownType2;
	}
	
}
