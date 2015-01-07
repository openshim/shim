/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.gui.common.ErrorMessageWriter;
import org.multicore_association.shim.edit.gui.common.NameAttributeChecker;
import org.multicore_association.shim.edit.gui.jface.SHIMEditJFaceApplicationWindow;
import org.multicore_association.shim.edit.gui.jface.ShimSelectableItem;
import org.multicore_association.shim.edit.model.ShimModelAdapter;

import swing2swt.layout.BorderLayout;

import com.sun.xml.internal.ws.util.StringUtils;

/**
 * Abstract base implementation for composite to show and edit SHIM Data.
 */
public abstract class InputPanelBase extends Composite implements
		SelectionListener, ErrorMessageWriter {

	private Label lblTitle;
	private Composite composite_1;

	protected ShimSelectableItem selectableItem = null;
	private NameAttributeChecker nameAttributeChecker = null;

	private String REJECT_APPLY = "Can't apply change.";

	/**
	 * Constructs a new instance of InputPanelBase.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public InputPanelBase(Composite parent, int style) {
		super(parent, style);

		setLayout(new BorderLayout(0, 0));

		lblTitle = new Label(this, SWT.NONE);
		lblTitle.setAlignment(SWT.CENTER);
		lblTitle.setLayoutData(BorderLayout.NORTH);
		lblTitle.setText("[ANY NAME]");

		composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.SOUTH);
		composite_1.setLayout(new GridLayout(4, false));

		new Label(composite_1, SWT.NONE);
		btnApply = new Button(composite_1, SWT.NONE);
		btnApply.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		btnApply.addSelectionListener(this);
		btnApply.setText("Apply");
		new Label(composite_1, SWT.NONE);

		textErrorMassage = new Text(composite_1, SWT.BORDER);
		textErrorMassage.setEditable(false);
		textErrorMassage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		textErrorMassage.setVisible(true);

		controlDecoration = new ControlDecoration(textErrorMassage, SWT.TOP);
		controlDecoration
				.setImage(getImage("/org/eclipse/jface/fieldassist/images/error_ovr.gif"));
		controlDecoration.setDescriptionText("");
		controlDecoration.hide();
	}

	/**
	 * Returns the image of the specified resource.
	 * 
	 * @param resouce
	 *            the resource to return the image
	 * @return the image of the specified resource
	 */
	protected Image getImage(String resouce) {
		InputStream stream = InputPanelBase.class.getResourceAsStream(resouce);
		ImageData data = new ImageData(stream);
		Image image = null;
		if (data.transparentPixel > 0) {
			image = new Image(Display.getCurrent(), data,
					data.getTransparencyMask());
		} else {
			image = new Image(Display.getCurrent(), data);
		}
		return image;
	}

	protected Button btnApply;
	protected Text textErrorMassage;
	private ControlDecoration controlDecoration;

	protected abstract void applyChange();

	public abstract void setInput(Object input);

	public abstract Object getInput();

	public abstract Class<?> getApiClass();

	/**
	 * Allows some resources held by this object to be released.
	 * 
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose() {
		selectableItem = null;
		nameAttributeChecker = null;
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		// NOOP
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(SelectionEvent event) {
		if (event.getSource() == btnApply) {
			int sum = 0;
			Collection<TreeSet<String>> values = errListMap.values();
			for (TreeSet<String> value : values) {
				sum += value.size();
			}
			
			if (sum <= 0 && finishedNameCheck) {
				applyChange();

				if (selectableItem != null) {
					selectableItem.refresh();
				}
				if (nameAttributeChecker != null) {
					nameAttributeChecker.updateNameList();
				}

				if (SHIMEditJFaceApplicationWindow.window != null) {
					SHIMEditJFaceApplicationWindow.window
							.setInputToEachCurrentInputPannel();
				}

			} else {
				String currentErrorMessage = getCurrentErrorMessage();
				if (currentErrorMessage != null) {
					setErrorMessage(REJECT_APPLY + " Cause of : '"
							+ currentErrorMessage + "'");
				} else {
					setErrorMessage(REJECT_APPLY);
				}
			}
		}
	}

	/**
	 * Returns the composite which has the Apply button and the error message
	 * field.
	 * 
	 * @return the composite which has the Apply button and the error message
	 *         field
	 */
	protected Composite getComposite_1() {
		return composite_1;
	}

	/**
	 * Sets the label of the page title.
	 * 
	 * @param title
	 *            the text to set the page title
	 */
	protected void setLblTitleText(String title) {
		this.lblTitle.setText(title);
	}

	/**
	 * Sets empty message to the error message field.
	 */
	public void clearErrorMessage() {
		textErrorMassage.setText("");
		controlDecoration.hide();
	}

	/**
	 * Sets an error message to the error message field.
	 * 
	 * @param msg
	 *            the error message to set
	 */
	public void setErrorMessage(String msg) {
		if (msg == null || msg.length() <= 0) {
			int sum = 0;
			Collection<TreeSet<String>> values = errListMap.values();
			for (TreeSet<String> value : values) {
				sum += value.size();
			}

			if (sum == 0) {
				clearErrorMessage();
				btnApply.setEnabled(true);
			}
		} else {
			textErrorMassage.setText(msg);
			controlDecoration.show();
			btnApply.setEnabled(false);
		}
	}

	/**
	 * Sets the Shim_SelectableItem instance.
	 * 
	 * @param selectableItem
	 *            the Shim_SelectableItem instance
	 */
	public void setSelectableItem(ShimSelectableItem selectableItem) {
		this.selectableItem = selectableItem;
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.common.ErrorMessageWriter#writeErrorMessage(java.lang.String)
	 */
	@Override
	public void writeErrorMessage(String value) {
		setErrorMessage(value);
	}

	protected Map<Class<?>, TreeSet<String>> errListMap = new HashMap<Class<?>, TreeSet<String>>();
	private boolean finishedNameCheck = true;

	/**
	 * A ModifyListener implementation for checking whether the parameter is
	 * required.
	 */
	protected class TextModifyListener implements ModifyListener {

		private String propertyName = null;
		private boolean isRequired = false;

		/**
		 * Constructs a new instance of TextModifyListener.
		 * 
		 * @param propertyName
		 *            the property name to be modified
		 */
		protected TextModifyListener(String propertyName) {
			this.propertyName = propertyName;
			this.isRequired = ShimModelAdapter.isRequired(getApiClass(),
					propertyName);
		}

		/**
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 */
		@Override
		public void modifyText(ModifyEvent event) {

			if (!checkRequired(event)) {
				return;
			}
			if (!isUniqueName(event)) {
				finishedNameCheck = false;
				return;
			} else {
				finishedNameCheck = true;
			}
		}

		/**
		 * Checking whether the parameter is required.
		 * 
		 * @param event
		 *            the notified ModifyEvent
		 * @return Returns true if the parameter is required to be non-null
		 *         value, and false otherwise.
		 */
		private boolean checkRequired(ModifyEvent event) {
			boolean canContinue = true;
			TreeSet<String> requredErrList = errListMap.get(this.getClass());
			if (requredErrList == null) {
				requredErrList = new TreeSet<String>();
				errListMap.put(this.getClass(), requredErrList);
			}

			if (this.isRequired) {
				Widget widget = event.widget;
				String value = null;
				if (widget instanceof Text) {
					value = ((Text) widget).getText();
				} else if (widget instanceof Combo) {
					value = ((Combo) widget).getText();
				}

				if (value.length() <= 0) {
					requredErrList.add(propertyName);
				} else {
					requredErrList.remove(propertyName);
				}
			}

			int i = 0;
			StringBuffer sb = new StringBuffer();
			for (String _propertyName : requredErrList) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(StringUtils.capitalize(_propertyName));
				i++;
			}
			if (sb.length() > 0) {
				String msg = sb.toString() + " is required.";
				setErrorMessage(msg);
				setCurrentErrorMessage(msg);
				canContinue = false;
			} else {
				setErrorMessage(null);
			}
			return canContinue;
		}

		/**
		 * Verify the uniqueness of the name attribute.
		 * 
		 * @param event
		 *            the notified ModifyEvent
		 * @return Returns true if the name is unique, and false otherwise.
		 */
		private boolean isUniqueName(ModifyEvent event) {
			boolean isUniqueName = true;
			if (nameAttributeChecker != null
					&& propertyName.equals(CommonConstants.FIELD_NAME)) {
				Widget widget = event.widget;
				Object input = getInput();
				if (widget instanceof Text && input != null) {
					String newName = ((Text) widget).getText();
					String oldName = (String) ShimModelAdapter.getValObject(
							input, CommonConstants.FIELD_NAME);
					String msg = nameAttributeChecker.checkNameList(oldName,
							newName);
					if (msg != null) {
						setErrorMessage(msg);
						setCurrentErrorMessage(msg);
						isUniqueName = false;
					}
				}
			}
			return isUniqueName;
		}
	}

	/**
	 * A ModifyListener implementation for a numeric value.
	 */
	protected class NumberModifyListener implements ModifyListener {

		private String paramName;

		/**
		 * Constructs a new instance of NumberModifyListener.
		 * 
		 * @param propertyName
		 *            the parameter name(not necessary to be a property name) to
		 *            be modified
		 */
		public NumberModifyListener(String paramName) {
			this.paramName = paramName;
		}

		/**
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 */
		@Override
		public void modifyText(ModifyEvent event) {
			TreeSet<String> requredErrList = getErrList(this.getClass());

			Object source = event.getSource();
			if (source instanceof Text) {
				try {
					String text = ((Text) source).getText();
					if (text != null && !text.isEmpty()) {
						long num = Long.parseLong(text);
						if (num < 0) {
							requredErrList.add(paramName);
						} else {
							requredErrList.remove(paramName);
						}
					}
				} catch (NumberFormatException nf) {
					requredErrList.add(paramName);
				}
			}

			int i = 0;
			StringBuffer sb = new StringBuffer();
			for (String _propertyName : requredErrList) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(StringUtils.capitalize(_propertyName));
				i++;
			}
			if (sb.length() > 0) {
				String msg = "Input an unsigned positive number in "
						+ sb.toString() + " field.";
				setErrorMessage(msg);
				setCurrentErrorMessage(msg);
			} else {
				setErrorMessage(null);
			}

		}
	}

	/**
	 * A ModifyListener implementation for a small number value.
	 */
	protected class SmallNumberModifyListener implements ModifyListener {

		private String paramName;

		/**
		 * Constructs a new instance of NumberModifyListener.
		 * 
		 * @param propertyName
		 *            the parameter name(not necessary to be a property name) to
		 *            be modified
		 */
		public SmallNumberModifyListener(String paramName) {
			this.paramName = paramName;
		}

		/**
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 */
		@Override
		public void modifyText(ModifyEvent event) {
			TreeSet<String> errList = getErrList(this.getClass());

			Object source = event.getSource();
			if (source instanceof Text) {
				try {
					String text = ((Text) source).getText();
					if (text != null && !text.isEmpty()) {
						double num = Double.parseDouble(text);
						if (num < 0) {
							errList.add(paramName);
						} else {
							errList.remove(paramName);
						}
					}
				} catch (NumberFormatException nf) {
					errList.add(paramName);
				}
			}

			int i = 0;
			StringBuffer sb = new StringBuffer();
			for (String _propertyName : errList) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(StringUtils.capitalize(_propertyName));
				i++;
			}
			if (sb.length() > 0) {
				String msg = "Input an unsigned positive number in "
						+ sb.toString() + " field.";
				setErrorMessage(msg);
				setCurrentErrorMessage(msg);
			} else {
				setErrorMessage(null);
			}
		}
	}

	/**
	 * Returns the parameter names that a validation error occurred.
	 * 
	 * @param claz
	 *            the modifier class that a validation error occurred
	 * @return the parameter names that a validation error occurred
	 */
	protected TreeSet<String> getErrList(Class<?> claz) {
		TreeSet<String> errList = errListMap.get(claz);
		if (errList == null) {
			errList = new TreeSet<String>();
			errListMap.put(claz, errList);
		}
		return errList;
	}

	protected String currentErrorMessage = null;

	/**
	 * Sets an error message to the current error message.
	 * 
	 * @param message
	 *            the message to set
	 */
	private void setCurrentErrorMessage(String message) {
		if (message != null && !message.startsWith(REJECT_APPLY)) {
			this.currentErrorMessage = message;
		}
	}

	/**
	 * Returns the current error message.
	 * 
	 * @return the current error message
	 */
	private String getCurrentErrorMessage() {
		return currentErrorMessage;
	}

	/**
	 * Sets a NameAttributeChecker instance to be verify the uniqueness of the
	 * name attribute.
	 * 
	 * @param nameAttributeChecker
	 *            the NameAttributeChecker instance
	 */
	public void setNameAttributeChecker(
			NameAttributeChecker nameAttributeChecker) {
		this.nameAttributeChecker = nameAttributeChecker;
	}

	/**
	 * Updates the NameAttributeChecker.
	 */
	public void updateNameAttributeChecker() {
		this.nameAttributeChecker.updateNameList();
	}

	/**
	 * Returns true if the given string is null or is the empty string.
	 * 
	 * @param str
	 *            the string to check
	 * @return Returns true if the given string is null or is the empty string,
	 *         and false otherwise.
	 */
	protected static boolean isNullOrEmpty(String str) {
		return str == null || str.isEmpty();
	}

	/**
	 * Returns the apply button widget.
	 * 
	 * @return the apply button widget
	 */
	public Button getBtnApply() {
		return btnApply;
	}
}
