/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.model.search;

import java.util.HashSet;
import java.util.Set;

/**
 * Defines the search options.
 */
public class SearchOptions {

	private boolean caseSensitive;

	private boolean wholeWord;

	private boolean regExp;

	private boolean doNotSearchIdRef;

	private Set<String> elementFilter;

	private Set<String> propertyFilter;

	/**
	 * Constructs a new instance of SearchOptions.
	 * 
	 * @param caseSensitive
	 *            if true, case sensitive search was performed
	 * @param wholeWord
	 *            if true, only whole words were searched
	 * @param regExp
	 *            if true, regular expression search was performed
	 * @param doNotSearchIdRef
	 *            if true, do not search the reference
	 */
	public SearchOptions(boolean caseSensitive, boolean wholeWord,
			boolean regExp, boolean doNotSearchIdRef) {
		this.caseSensitive = caseSensitive;
		this.wholeWord = wholeWord;
		this.regExp = regExp;
		this.doNotSearchIdRef = doNotSearchIdRef;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean isWholeWord() {
		return wholeWord;
	}

	public void setWholeWord(boolean wholeWord) {
		this.wholeWord = wholeWord;
	}

	public boolean isRegExp() {
		return regExp;
	}

	public void setRegExp(boolean regExp) {
		this.regExp = regExp;
	}

	public boolean isDoNotSearchIdRef() {
		return doNotSearchIdRef;
	}

	public void setDoNotSearchIdRef(boolean doNotSearchIdRef) {
		this.doNotSearchIdRef = doNotSearchIdRef;
	}

	/**
	 * Adds element name to filtering list.
	 * 
	 * @param elementName
	 *            the element name to filter from the search result
	 */
	public void addElementFilter(String elementName) {
		if (elementFilter == null) {
			elementFilter = new HashSet<String>();
		}

		elementFilter.add(elementName.toLowerCase());
	}

	/**
	 * Returns whether the specified element matches the filter elements or not.
	 * 
	 * @param elementName
	 *            the name of element
	 * @return Returns true if the specified element matches the filter elements
	 *         or there is not any filter element.
	 */
	public boolean matchElement(String elementName) {
		if (elementFilter == null) {
			return true;
		}
		
		if (elementName == null) {
			return false;
		}
		
		return elementFilter.contains(elementName.toLowerCase());
	}

	/**
	 * Adds property name to filtering list.
	 * 
	 * @param propertyName
	 *            the property name to filter from the search result
	 */
	public void addPropertyFilter(String propertyName) {
		if (propertyFilter == null) {
			propertyFilter = new HashSet<String>();
		}

		propertyFilter.add(propertyName.toLowerCase());
	}

	/**
	 * Returns whether the specified property matches the filter elements or
	 * not.
	 * 
	 * @param propertyName
	 *            the name of property
	 * @return Returns true if the specified property matches the filter
	 *         properties or there is not any filter property.
	 */
	public boolean matchProperty(String propertyName) {
		if (propertyFilter == null) {
			return true;
		}
		
		if (propertyName == null) {
			return false;
		}
		
		return propertyFilter.contains(propertyName.toLowerCase());
	}

}
