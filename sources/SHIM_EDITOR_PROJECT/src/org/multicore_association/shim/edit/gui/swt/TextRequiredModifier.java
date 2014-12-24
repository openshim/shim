/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.edit.gui.jface.ErrorMessagePool;
import org.multicore_association.shim.edit.model.ShimModelAdapter;

/**
 * A ModifyListener implementation for checking whether the parameter is
 * required.
 */
public class TextRequiredModifier implements ModifyListener {

	private ErrorMessagePool pool;

	private Class<?> apiClass;

	private String propertyName;

	private String paramName;

	/**
	 * Constructs a new instance of TextModifyListener.
	 * 
	 * @param pool
	 *            the error message pool
	 * @param apiClass
	 *            the class to check
	 * @param propertyName
	 *            the property name to check
	 */
	public TextRequiredModifier(ErrorMessagePool pool, Class<?> apiClass,
			String propertyName) {
		this.pool = pool;
		this.apiClass = apiClass;
		this.propertyName = propertyName;
		this.paramName = propertyName;
	}

	/**
	 * Constructs a new instance of TextModifyListener, which is used when the
	 * property name is not coincident with displayed parameter name.
	 * 
	 * @param pool
	 *            the error message pool
	 * @param apiClass
	 *            the class to check
	 * @param propertyName
	 *            the property name to check
	 * @param paramName
	 *            the parameter name to show
	 */
	public TextRequiredModifier(ErrorMessagePool pool, Class<?> apiClass,
			String propertyName, String paramName) {
		this.pool = pool;
		this.apiClass = apiClass;
		this.propertyName = propertyName;
		this.paramName = paramName;
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	@Override
	public void modifyText(ModifyEvent event) {
		Object source = event.getSource();
		assert (source instanceof Text);

		String text = ((Text) source).getText();
		if (text == null || text.isEmpty()) {
			boolean required = ShimModelAdapter.isRequired(apiClass,
					propertyName);
			if (required) {
				pool.setErrorMessage(this, paramName + " is required.");
				return;
			}
		}

		pool.removeErrorMessage(this);
	}

}
