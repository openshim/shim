/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.cycle.generate.config;

import java.util.List;


/**
 * Class that contains the configuration data of the architecture.
 *
 */
public class ArchConfig extends BaseConfig {

	/** Entry name. */
	public static final String ENTRY_INITIALIZE = "initialize";
	/** Entry name. */
	public static final String ENTRY_MEASURE_DIFF_BEGIN = "measure_diff_begin";
	/** Entry name. */
	public static final String ENTRY_MEASURE_DIFF_END = "measure_diff_end";
	/** Entry name. */
	public static final String ENTRY_MEASURE_TARGET_BEGIN = "measure_target_begin";
	/** Entry name. */
	public static final String ENTRY_MEASURE_TARGET_END = "measure_target_end";
	/** Entry name. */
	public static final String ENTRY_TRY_DIFF_BEGIN = "try_diff_begin";
	/** Entry name. */
	public static final String ENTRY_TRY_DIFF_END = "try_diff_end";
	/** Entry name. */
	public static final String ENTRY_TRY_TARGET_BEGIN = "try_target_begin";
	/** Entry name. */
	public static final String ENTRY_TRY_TARGET_END = "try_target_end";
	/** Entry name. */
	public static final String ENTRY_CONTINUITY_BEGIN = "continuity_begin";
	/** Entry name. */
	public static final String ENTRY_CONTINUITY_END = "continuity_end";
	/** Entry name. */
	public static final String ENTRY_MEASURE_CALL_INIT = "measure_call_init";
	/** Entry name. */
	public static final String ENTRY_MEASURE_CALL_BEGIN = "measure_call_begin";
	/** Entry name. */
	public static final String ENTRY_MEASURE_CALL_END = "measure_call_end";
	/** Entry name. */
	public static final String ENTRY_MEASURE_CALL_RESULT = "measure_call_result";
	/** Entry name. */
	public static final String ENTRY_MEASURE_RET_INIT = "measure_ret_init";
	/** Entry name. */
	public static final String ENTRY_MEASURE_RET_BEGIN = "measure_ret_begin";
	/** Entry name. */
	public static final String ENTRY_MEASURE_RET_END = "measure_ret_end";
	/** Entry name. */
	public static final String ENTRY_MEASURE_RET_RESULT = "measure_ret_result";
	/** Entry name. */
	public static final String ENTRY_TRY_CALL_RET_BEGIN = "try_call_ret_begin";
	/** Entry name. */
	public static final String ENTRY_TRY_CALL_RET_END = "try_call_ret_end";

	private List<String> mainInitialize = null;
	private List<String> mainMeasureDiffBegin = null;
	private List<String> mainMeasureDiffEnd = null;
	private List<String> mainMeasureTargetBegin = null;
	private List<String> mainMeasureTargetEnd = null;
	private List<String> mainTryDiffBegin = null;
	private List<String> mainTryDiffEnd = null;
	private List<String> mainTryTargetBegin = null;
	private List<String> mainTryTargetEnd = null;
	private List<String> mainContinuityBegin = null;
	private List<String> mainContinuityEnd = null;
	private List<String> mainMeasureCallInit = null;
	private List<String> mainMeasureCallBegin = null;
	private List<String> mainMeasureCallEnd = null;
	private List<String> mainMeasureCallResult = null;
	private List<String> mainMeasureRetInit = null;
	private List<String> mainMeasureRetBegin = null;
	private List<String> mainMeasureRetEnd = null;
	private List<String> mainMeasureRetResult = null;
	private List<String> mainTryCallRetBegin = null;
	private List<String> mainTryCallRetEnd = null;

	/**
	 * Getting a section name list.
	 * @return Section name list
	 */
	public static List<String> getSectionNameList() {
		List<String> list = BaseConfig.getSectionNameList();
		return list;
	}

	/**
	 * Getting a entry name list of header section.
	 * @return Entry name list of header
	 */
	public static List<String> getHeaderEntryNameList() {
		List<String> list = BaseConfig.getHeaderEntryNameList();
		return list;
	}

	/**
	 * Getting a entry name list of main section.
	 * @return Entry name list of main section
	 */
	public static List<String> getMainEntryNameList() {
		List<String> list = BaseConfig.getMainEntryNameList();
		list.add(ENTRY_INITIALIZE);
		list.add(ENTRY_MEASURE_DIFF_BEGIN);
		list.add(ENTRY_MEASURE_DIFF_END);
		list.add(ENTRY_MEASURE_TARGET_BEGIN);
		list.add(ENTRY_MEASURE_TARGET_END);
		list.add(ENTRY_TRY_DIFF_BEGIN);
		list.add(ENTRY_TRY_DIFF_END);
		list.add(ENTRY_TRY_TARGET_BEGIN);
		list.add(ENTRY_TRY_TARGET_END);
		list.add(ENTRY_CONTINUITY_BEGIN);
		list.add(ENTRY_CONTINUITY_END);
		list.add(ENTRY_MEASURE_CALL_INIT);
		list.add(ENTRY_MEASURE_CALL_BEGIN);
		list.add(ENTRY_MEASURE_CALL_END);
		list.add(ENTRY_MEASURE_CALL_RESULT);
		list.add(ENTRY_MEASURE_RET_INIT);
		list.add(ENTRY_MEASURE_RET_BEGIN);
		list.add(ENTRY_MEASURE_RET_END);
		list.add(ENTRY_MEASURE_RET_RESULT);
		list.add(ENTRY_TRY_CALL_RET_BEGIN);
		list.add(ENTRY_TRY_CALL_RET_END);
		return list;
	}

	/**
	 * Getter for mainInitialize.
	 * @return mainInitialize
	 */
	public List<String> getMainInitialize() {
		return mainInitialize;
	}

	/**
	 * Setter for mainInitialize.
	 * @param mainInitialize
	 */
	public void setMainInitialize(String mainInitialize) {
		this.mainInitialize = mutilateData(mainInitialize);
	}

	/**
	 * Getter for mainMeasureDiffBegin.
	 * @return mainMeasureDiffBegin
	 */
	public List<String> getMainMeasureDiffBegin() {
		return mainMeasureDiffBegin;
	}

	/**
	 * Setter for mainMeasureDiffBegin.
	 * @param mainMeasureDiffBegin
	 */
	public void setMainMeasureDiffBegin(String mainMeasureDiffBegin) {
		this.mainMeasureDiffBegin = mutilateData(mainMeasureDiffBegin);
	}

	/**
	 * Getter for mainMeasureDiffEnd.
	 * @return mainMeasureDiffEnd
	 */
	public List<String> getMainMeasureDiffEnd() {
		return mainMeasureDiffEnd;
	}

	/**
	 * Setter for setMainMeasureDiffEnd.
	 * @param mainMeasureDiffEnd
	 */
	public void setMainMeasureDiffEnd(String mainMeasureDiffEnd) {
		this.mainMeasureDiffEnd = mutilateData(mainMeasureDiffEnd);
	}

	/**
	 * Getter for mainMeasureTargetBegin.
	 * @return mainMeasureTargetBegin
	 */
	public List<String> getMainMeasureTargetBegin() {
		return mainMeasureTargetBegin;
	}

	/**
	 * Setter for mainMeasureTargetBegin.
	 * @param mainMeasureTargetBegin
	 */
	public void setMainMeasureTargetBegin(String mainMeasureTargetBegin) {
		this.mainMeasureTargetBegin = mutilateData(mainMeasureTargetBegin);
	}

	/**
	 * Getter for mainMeasureTargetEnd.
	 * @return mainMeasureTargetEnd
	 */
	public List<String> getMainMeasureTargetEnd() {
		return mainMeasureTargetEnd;
	}

	/**
	 * Setter for mainMeasureTargetEnd.
	 * @param mainMeasureTargetEnd
	 */
	public void setMainMeasureTargetEnd(String mainMeasureTargetEnd) {
		this.mainMeasureTargetEnd = mutilateData(mainMeasureTargetEnd);
	}

	/**
	 * Getter for mainTryDiffBegin.
	 * @return mainTryDiffBegin
	 */
	public List<String> getMainTryDiffBegin() {
		return mainTryDiffBegin;
	}

	/**
	 * Setter for mainTryDiffBegin.
	 * @param mainTryDiffBegin
	 */
	public void setMainTryDiffBegin(String mainTryDiffBegin) {
		this.mainTryDiffBegin = mutilateData(mainTryDiffBegin);
	}

	/**
	 * Getter for mainTryDiffEnd.
	 * @return mainTryDiffEnd
	 */
	public List<String> getMainTryDiffEnd() {
		return mainTryDiffEnd;
	}

	/**
	 * Setter for mainTryDiffEnd.
	 * @param mainTryDiffEnd
	 */
	public void setMainTryDiffEnd(String mainTryDiffEnd) {
		this.mainTryDiffEnd = mutilateData(mainTryDiffEnd);
	}

	/**
	 * Getter for mainTryTargetBegin.
	 * @return mainTryTargetBegin
	 */
	public List<String> getMainTryTargetBegin() {
		return mainTryTargetBegin;
	}

	/**
	 * Setter for mainTryTargetBegin.
	 * @param mainTryTargetBegin
	 */
	public void setMainTryTargetBegin(String mainTryTargetBegin) {
		this.mainTryTargetBegin = mutilateData(mainTryTargetBegin);
	}

	/**
	 * Getter for mainTryTargetEnd.
	 * @return mainTryTargetEnd
	 */
	public List<String> getMainTryTargetEnd() {
		return mainTryTargetEnd;
	}

	/**
	 * Setter for mainTryTargetEnd.
	 * @param mainTryTargetEnd
	 */
	public void setMainTryTargetEnd(String mainTryTargetEnd) {
		this.mainTryTargetEnd = mutilateData(mainTryTargetEnd);
	}

	/**
	 * Getter for mainContinuityBegin.
	 * @return mainContinuityBegin
	 */
	public List<String> getMainContinuityBegin() {
		return mainContinuityBegin;
	}

	/**
	 * Setter for mainContinuityBegin.
	 * @param mainContinuityBegin
	 */
	public void setMainContinuityBegin(String mainContinuityBegin) {
		this.mainContinuityBegin = mutilateData(mainContinuityBegin);
	}

	/**
	 * Getter for mainContinuityEnd.
	 * @return mainContinuityEnd
	 */
	public List<String> getMainContinuityEnd() {
		return mainContinuityEnd;
	}

	/**
	 * Setter for mainContinuityEnd.
	 * @param mainContinuityEnd
	 */
	public void setMainContinuityEnd(String mainContinuityEnd) {
		this.mainContinuityEnd = mutilateData(mainContinuityEnd);
	}
	/**
	 * Getter for mainMeasureCallInit.
	 * @return mainMeasureCallInit
	 */
	public List<String> getMainMeasureCallInit() {
		return mainMeasureCallInit;
	}
	/**
	 * Setter for mainMeasureCallInit.
	 * @param mainMeasureCallInit
	 */
	public void setMainMeasureCallInit(String mainMeasureCallInit) {
		this.mainMeasureCallInit = mutilateData(mainMeasureCallInit);
	}
	/**
	 * Getter for mainMeasureCallBegin.
	 * @return mainMeasureCallBegin
	 */
	public List<String> getMainMeasureCallBegin() {
		return mainMeasureCallBegin;
	}
	/**
	 * Setter for mainMeasureCallBegin.
	 * @param mainMeasureCallBegin
	 */
	public void setMainMeasureCallBegin(String mainMeasureCallBegin) {
		this.mainMeasureCallBegin = mutilateData(mainMeasureCallBegin);
	}
	/**
	 * Getter for mainMeasureCallEnd.
	 * @return mainMeasureCallEnd
	 */
	public List<String> getMainMeasureCallEnd() {
		return mainMeasureCallEnd;
	}
	/**
	 * Setter for mainMeasureCallEnd.
	 * @param mainMeasureCallEnd
	 */
	public void setMainMeasureCallEnd(String mainMeasureCallEnd) {
		this.mainMeasureCallEnd = mutilateData(mainMeasureCallEnd);
	}
	/**
	 * Getter for mainMeasureCallResult.
	 * @return mainMeasureCallResult
	 */
	public List<String> getMainMeasureCallResult() {
		return mainMeasureCallResult;
	}
	/**
	 * Setter for mainMeasureCallResult.
	 * @param mainMeasureCallResult
	 */
	public void setMainMeasureCallResult(String mainMeasureCallResult) {
		this.mainMeasureCallResult = mutilateData(mainMeasureCallResult);
	}
	/**
	 * Getter for mainMeasureRetInit.
	 * @return mainMeasureRetInit
	 */
	public List<String> getMainMeasureRetInit() {
		return mainMeasureRetInit;
	}
	/**
	 * Setter for mainMeasureRetInit.
	 * @param mainMeasureRetInit
	 */
	public void setMainMeasureRetInit(String mainMeasureRetInit) {
		this.mainMeasureRetInit = mutilateData(mainMeasureRetInit);
	}
	/**
	 * Getter for mainMeasureRetBegin.
	 * @return mainMeasureRetBegin
	 */
	public List<String> getMainMeasureRetBegin() {
		return mainMeasureRetBegin;
	}
	/**
	 * Setter for mainMeasureRetBegin.
	 * @param mainMeasureRetBegin
	 */
	public void setMainMeasureRetBegin(String mainMeasureRetBegin) {
		this.mainMeasureRetBegin = mutilateData(mainMeasureRetBegin);
	}
	/**
	 * Getter for mainMeasureRetEnd.
	 * @return mainMeasureRetEnd
	 */
	public List<String> getMainMeasureRetEnd() {
		return mainMeasureRetEnd;
	}
	/**
	 * Setter for mainMeasureRetEnd.
	 * @param mainMeasureRetEnd
	 */
	public void setMainMeasureRetEnd(String mainMeasureRetEnd) {
		this.mainMeasureRetEnd = mutilateData(mainMeasureRetEnd);
	}
	/**
	 * Getter for mainMeasureRetResult.
	 * @return mainMeasureRetResult
	 */
	public List<String> getMainMeasureRetResult() {
		return mainMeasureRetResult;
	}
	/**
	 * Setter for mainMeasureRetResult.
	 * @param mainMeasureRetResult
	 */
	public void setMainMeasureRetResult(String mainMeasureRetResult) {
		this.mainMeasureRetResult = mutilateData(mainMeasureRetResult);
	}
	/**
	 * Getter for mainTryCallRetBegin.
	 * @return mainTryCallRetBegin
	 */
	public List<String> getMainTryCallRetBegin() {
		return mainTryCallRetBegin;
	}
	/**
	 * Setter for mainTryCallRetBegin.
	 * @param mainTryCallRetBegi
	 */
	public void setMainTryCallRetBegin(String mainTryCallRetBegin) {
		this.mainTryCallRetBegin = mutilateData(mainTryCallRetBegin);
	}
	/**
	 * Getter for mainTryCallRetBegin.
	 * @return mainTryCallRetEnd
	 */
	public List<String> getMainTryCallRetEnd() {
		return mainTryCallRetEnd;
	}
	/**
	 * Setter for mainTryCallRetBegin.
	 * @param mainTryCallRetEnd
	 */
	public void setMainTryCallRetEnd(String mainTryCallRetEnd) {
		this.mainTryCallRetEnd = mutilateData(mainTryCallRetEnd);
	}
}
