/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.viewer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.TableColumn;
import org.multicore_association.shim.edit.model.ShimObject;

/**
 * An implementation of ViewerFilter for ShimObjectTableViewer.
 */
public class ShimObjectTableFilter extends ViewerFilter {

	private String searchString;

	private String searchFiled;

	/**
	 * Sets the search text.
	 * 
	 * @param searchString
	 *            the search text
	 */
	public void setSearchText(String searchString) {
		this.searchString = searchString;
	}

	/**
	 * Sets the field to search.
	 * 
	 * @param searchFiled
	 *            the field to search
	 */
	public void setSearchFiled(String searchFiled) {
		this.searchFiled = searchFiled;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.isEmpty()) {
			return true;
		}

		List<Integer> searchIndexes = new ArrayList<Integer>();

		TableViewer tableViewer = (TableViewer) viewer;
		TableColumn[] columns = tableViewer.getTable().getColumns();
		for (int i = 0; i < columns.length; i++) {
			if (searchFiled != null
					&& !searchFiled.equals(columns[i].getText())) {
				continue;
			}
			searchIndexes.add(i);
		}

		ITableLabelProvider labelProvider = (ITableLabelProvider) tableViewer
				.getLabelProvider();

		ShimObject obj = (ShimObject) element;
		for (Integer searchIndex : searchIndexes) {
			String columnText = labelProvider.getColumnText(obj, searchIndex);
			if (isMatch(columnText)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns whether the specified column text matches the search text.
	 * 
	 * @param columnText
	 *            the column text to match
	 * @return Returns true if the specified column text matches the search
	 *         text, and false otherwise.
	 */
	private boolean isMatch(String columnText) {
		if (columnText.contains(searchString)) {
			return true;
		}

		try {
			return columnText.matches(searchString);
		} catch (PatternSyntaxException e) {
		}

		return false;
	}
}
