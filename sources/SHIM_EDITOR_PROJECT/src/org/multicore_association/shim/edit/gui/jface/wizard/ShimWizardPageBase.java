/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.gui.common.ErrorMessageWriter;
import org.multicore_association.shim.edit.model.ShimModelAdapter;

/**
 * Abstract base implementation for SHIM Editor wizard page.
 */
public abstract class ShimWizardPageBase extends WizardPage implements
		ErrorMessageWriter {

	/**
	 * Constructs a new instance of ShimWizardPageBase.
	 * 
	 * @param pageName
	 */
	protected ShimWizardPageBase(String pageName) {
		super(pageName);
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.com.ErrorMessageWriter#writeErrorMessage(java.lang.String)
	 */
	@Override
	public void writeErrorMessage(String value) {
		if (value == "") {
			value = null;
		}
		setErrorMessage(value);
	}

	private List<RequiredItem> requiredItemList = new ArrayList<RequiredItem>();

	/**
	 * Confirms whether all required items of this page is input.
	 * 
	 * @param items
	 *            all required items of this page
	 * @return Returns true if all required items of this page is input, and false otherwise.
	 */
	protected boolean checkRequired(RequiredItem... items) {
		for (RequiredItem item : items) {

			Object obj = item.obj;
			boolean isRequired = ShimModelAdapter.isRequired(obj,
					CommonConstants.FIELD_NAME);
			Object result = ShimModelAdapter.getValObject(obj,
					CommonConstants.FIELD_NAME);

			String name;
			if (result != null) {
				name = ((String) result).toString();
			} else {
				// if the data isn't set to Class, it needs to get the value
				// from the text box.
				name = item.text.getText();
			}

			if (isRequired && name.length() <= 0) {
				setPageComplete(false);
				setErrorMessage(item.displayName + " is required.");
				return false;
			}
		}

		if (!isPageComplete()) {
			setPageComplete(true);
		}

		setErrorMessage(null);
		return true;
	}

	/**
	 * An implementation for required item.
	 */
	protected class RequiredItem {
		private Object obj = new Object();
		private String displayName = null;
		private Text text = null;

		/**
		 * @param obj
		 *            Target class is a member of the SHIM API.
		 * @param displayName
		 * @param text
		 *            Text that will accept data.
		 */
		RequiredItem(Object obj, String displayName, Text text) {
			this.obj = obj;
			this.displayName = displayName;
			this.text = text;
		};
	}

	/**
	 * This class manages listening for required resource changes.
	 */
	protected class RequiredListener implements Listener {

		/**
		 * @param item
		 */
		public RequiredListener(RequiredItem item) {
			requiredItemList.add(item);
		}

		/**
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 */
		@Override
		public void handleEvent(Event event) {
			RequiredItem[] requiredItem = requiredItemList
					.toArray(new RequiredItem[0]);
			checkRequired(requiredItem);
		}
	}
}
