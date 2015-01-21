/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.mem.writeback;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.multicore_association.measure.mem.writeback.data.MeasurementsCSVData;

/**
 * Reading of a CSV file(comma end) and stock to data on list.
 *
 */
public class CSVParser {

	ArrayList<MeasurementsCSVData> resultList = null;

	/**
	 * Reading of a CSV file(comma end).
	 * @param path Path to CSV file
	 * @throws IOException
	 */
	public boolean parse(String path) {
		boolean ret = true;
		FileReader reader = null;
		BufferedReader bfreader = null;

		try {
			reader = new FileReader(path);
			bfreader = new BufferedReader(reader);

			resultList = new ArrayList<MeasurementsCSVData>();

			String l;
			while ((l = bfreader.readLine()) != null) {
				MeasurementsCSVData elem = new MeasurementsCSVData();
				String[] list = l.split(",", -1);
				for (int i = 0; i < list.length; i++) {
					list[i] = list[i].trim();
				}
				try {
					elem.setElements(list);
					resultList.add(elem);
				} catch (ParseException e) {
//					e.printStackTrace();
					continue;
				}
			}
		} catch (FileNotFoundException e) {
			ret = false;
		} catch (IOException e) {
			ret = false;
		} finally {
			try {
				if (bfreader != null) {
					bfreader.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
			}
		}

		return ret;
	}

	/**
	 * Getting the file reading result.
	 * @return Read CSV file data.
	 */
	public ArrayList<MeasurementsCSVData> getResult() {
		return resultList;
	}
}
