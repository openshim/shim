/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.cycle.writeback.data;

import java.util.ArrayList;
import java.util.List;

public class CsvResult {

	private String archName;
	private String instSetName;
	private List<String[]> resultList = null;

	public CsvResult() {
		resultList = new ArrayList<String[]>();
	}

	public String getArchName() {
		return archName;
	}

	public void setArchName(String archName) {
		this.archName = archName;
	}

	public String getInstSetName() {
		return instSetName;
	}

	public void setInstSetName(String instSetName) {
		this.instSetName = instSetName;
	}

	/**
	 * Getting the file reading result.
	 * @return Read CSV file data.
	 */
	public List<String[]> getResultList() {
		return resultList;
	}


	public void setResultList(List<String[]> resultList) {
		this.resultList = resultList;
	}

}
