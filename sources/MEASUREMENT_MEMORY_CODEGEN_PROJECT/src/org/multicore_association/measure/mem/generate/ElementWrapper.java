/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.mem.generate;

public class ElementWrapper<E> {

	private String name = "";
	private E element = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public E getElement() {
		return element;
	}

	public void setElement(E element) {
		this.element = element;
	}

}
