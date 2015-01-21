/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.cycle.writeback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.multicore_association.measure.cycle.writeback.data.CsvResult;

/**
 * Reading of a CSV file(comma end) and stock to data on list.
 *
 */
public class CsvResultFileLoader {

	private static final String CSV_EXTENSION = "\\.csv$";
	private static final String CSV_PATTERN = ".*" + CSV_EXTENSION;
	private static final String CSV_NAME_SEPARATOR = "__";

	private List<CsvResult> resultCacheList;

	private List<File> resultFileList;

	private String csvDirPath;
	private boolean isRecursive = false;

	/**
	 *
	 * @param dirPath
	 */
	public CsvResultFileLoader(String dirPath) {
		this(dirPath, false);
	}

	/**
	 *
	 * @param dirPath
	 * @param isRecursive
	 */
	public CsvResultFileLoader(String dirPath, boolean isRecursive) {
		this.isRecursive = isRecursive;
		resultCacheList = new ArrayList<CsvResult>();
		resultFileList = new ArrayList<File>();
		loadCsvFileList(dirPath);
	}

	public CsvResultFileLoader(List<String> fileList) {
		resultCacheList = new ArrayList<CsvResult>();
		resultFileList = new ArrayList<File>();
		loadCsvFileList(fileList);
	}

	/**
	 * search architecture and instruction config file.
	 */
	private void loadCsvFileList(String dirPath) {
		csvDirPath = dirPath;
		resultFileList = searchFile(csvDirPath, CSV_PATTERN);

		for (Iterator<File> i = resultFileList.iterator(); i.hasNext();) {
			File f = i.next();
			parse(f.getPath());
		}
	}

	private void loadCsvFileList(List<String> fileList) {

		resultFileList = new ArrayList<File>();

		for (Iterator<String> i = fileList.iterator(); i.hasNext();) {
			String path = i.next();
			File f = new File(path);
			parse(f.getPath());
			resultFileList.add(f);
		}
	}

	/**
	 * csv file search processing.
	 * This method searches for a result file in the directory designated
	 * as an argument. Return searches, and returns a list of the files found.
	 * The pattern of the file name is designated as an argument.
	 * @param dirPath
	 * @param pattern
	 * @return
	 */
	private List<File> searchFile(String dirPath, String pattern) {

		List<File> ret = new ArrayList<File>();
		File dir = new File(dirPath);
		if (!dir.isDirectory()) {
			return ret;
		}

		File[] filearray = dir.listFiles();
		for (int i = 0; i < filearray.length; i++) {
			File file = filearray[i];

			if (file.isDirectory()) {
				if (isRecursive) {
					List<File> lst = searchFile(file.getAbsolutePath(), pattern);
					ret.addAll(lst);
				} else {
					continue;
				}
			} else if (file.isFile()) {
				if (file.getName().matches(pattern)) {
					ret.add(file);
				}
			}
		}

		return ret;
	}

	/**
	 * Reading of a CSV file(comma end).
	 * @param path Path to CSV file
	 * @return Flag for success judgements
	 */
	private boolean parse(String path) {
		boolean ret = true;
		File file = null;
		FileReader reader = null;
		BufferedReader bfreader = null;
		CsvResult data;
		List<String[]> resultList = new ArrayList<String[]>();
		String[] names;

		file = new File(path);
		String buff = file.getName().replaceAll(CSV_EXTENSION, "");
		names = buff.split(CSV_NAME_SEPARATOR);
		if (names.length != 2) {
			/* illegal file name, ignore */
			return false;
		}

		for (Iterator<CsvResult> i = resultCacheList.iterator(); i.hasNext();) {
			CsvResult cache = i.next();

			/* Ignore if there is the same set */
			if (cache.getArchName().equals(names[0]) && cache.getInstSetName().equals(names[1])) {
				return false;
			}
		}

		data = new CsvResult();
		data.setArchName(names[0]);
		data.setInstSetName(names[1]);

		try {
			reader = new FileReader(file);
			bfreader = new BufferedReader(reader);

			resultList.clear();

			String l;
			while ((l = bfreader.readLine()) != null) {
				String[] list = l.split(",", -1);
				for (int i = 0; i < list.length; i++) {
					list[i] = list[i].trim();
				}
				resultList.add(list);
			}

			data.setResultList(resultList);
			resultCacheList.add(data);

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

	public List<CsvResult> getResultCacheList() {
		return resultCacheList;
	}
}
