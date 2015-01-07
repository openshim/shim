/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface.wizard;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.AccessType;
import org.multicore_association.shim.edit.gui.common.LabelConstants;
import org.multicore_association.shim.edit.gui.databinding.IntToStringConverter;
import org.multicore_association.shim.edit.gui.databinding.StringToIntegerConverter;
import org.multicore_association.shim.edit.gui.jface.ErrorMessagePool;
import org.multicore_association.shim.edit.gui.swt.ByteSizeListModifier;
import org.multicore_association.shim.edit.gui.swt.TextRequiredModifier;
import org.multicore_association.shim.edit.gui.swt.UnsignedIntegerModifier;
import org.multicore_association.shim.edit.model.ShimPreferences;

import swing2swt.layout.BorderLayout;

/**
 * Wizard page that allows configuration of the AccessType.
 */
public class AccessTypeWizardPage extends WizardPage implements
		SelectionListener {
	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;

	public Composite composite;
	public Label lblBasename;
	public Text txt_baseName;
	public Composite composite_1;
	public Button btnCheckButtonR;
	public Button btnCheckButtonW;
	public Button btnCheckButtonRW;
	public Button btnCheckButtonRWX;
	public Button btnCheckButtonRX;
	public Button btnCheckButtonX;
	public Label lblAccessByteSize;
	public Text textByteSizeList;
	public Label lblNburst;
	public Text textNBurst;
	public Label lblRwtype;

	private ShimPreferences settings;

	private ErrorMessagePool pool;

	/**
	 * Constructs a new instance of AccessTypeWizardPage.
	 */
	public AccessTypeWizardPage() {
		super("wizardPage");
		setTitle("Setting Information of Base AccessType");
		setDescription("Base AccessType Information for Generate the combination.");

		settings = ShimPreferences.getCurrentInstance();

		pool = new ErrorMessagePool(this);
	}

	/**
	 * Creates contents of this page.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new BorderLayout(0, 0));

		composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(2, false));

		lblBasename = new Label(composite, SWT.NONE);
		lblBasename.setText(LabelConstants.BASE_NAME);

		txt_baseName = new Text(composite, SWT.BORDER);
		GridData gd_txt_baseName = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_txt_baseName.widthHint = 86;
		txt_baseName.setLayoutData(gd_txt_baseName);
		txt_baseName.addModifyListener(new TextRequiredModifier(pool,
				AccessType.class, "name", lblBasename.getText()));

		lblRwtype = new Label(composite, SWT.NONE);
		lblRwtype.setText(LabelConstants.RW_TYPE);

		composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(6, false));

		btnCheckButtonR = new Button(composite_1, SWT.CHECK);
		btnCheckButtonR.setText("R");
		btnCheckButtonR.addSelectionListener(this);

		btnCheckButtonW = new Button(composite_1, SWT.CHECK);
		btnCheckButtonW.setText("W");
		btnCheckButtonW.addSelectionListener(this);

		btnCheckButtonRW = new Button(composite_1, SWT.CHECK);
		btnCheckButtonRW.setText("RW");
		btnCheckButtonRW.addSelectionListener(this);

		btnCheckButtonRWX = new Button(composite_1, SWT.CHECK);
		btnCheckButtonRWX.setText("RWX");
		btnCheckButtonRWX.addSelectionListener(this);

		btnCheckButtonRX = new Button(composite_1, SWT.CHECK);
		btnCheckButtonRX.setText("RX");
		btnCheckButtonRX.addSelectionListener(this);

		btnCheckButtonX = new Button(composite_1, SWT.CHECK);
		btnCheckButtonX.setText("X");
		btnCheckButtonX.addSelectionListener(this);

		lblAccessByteSize = new Label(composite, SWT.NONE);
		lblAccessByteSize.setText(LabelConstants.ACCESS_BYTE_SIZE);

		textByteSizeList = new Text(composite, SWT.BORDER);
		GridData gdTextByteSizeList = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gdTextByteSizeList.widthHint = 174;
		textByteSizeList.setLayoutData(gdTextByteSizeList);
		textByteSizeList
				.addModifyListener(new TextRequiredModifier(pool,
						AccessType.class, "accessByteSize", lblAccessByteSize
								.getText()));
		textByteSizeList.addModifyListener(new ByteSizeListModifier(pool,
				lblAccessByteSize.getText()));

		lblNburst = new Label(composite, SWT.NONE);
		lblNburst.setText(LabelConstants.NUMBER_OF_BURST);

		textNBurst = new Text(composite, SWT.BORDER);
		GridData gdTextNBurst = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gdTextNBurst.widthHint = 77;
		textNBurst.setLayoutData(gdTextNBurst);
		textNBurst.addModifyListener(new TextRequiredModifier(pool,
				AccessType.class, "nBurst", lblNburst.getText()));
		textNBurst.addModifyListener(new UnsignedIntegerModifier(pool,
				lblNburst.getText(), true));

		widgetSelected(null);
		m_bindingContext = initDataBindings();
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
		boolean selected = btnCheckButtonR.getSelection()
				|| btnCheckButtonW.getSelection()
				|| btnCheckButtonRW.getSelection()
				|| btnCheckButtonRWX.getSelection()
				|| btnCheckButtonRX.getSelection()
				|| btnCheckButtonX.getSelection();

		String msg = selected ? null : "Choose any one in RWType.";
		setErrorMessage(msg);
		setPageComplete(selected);
	}

	/**
	 * Initializes the DataBindingContext.<br>
	 * This method is generated by WindowsBuilder.
	 * 
	 * @return the initialized DataBindingContext
	 */
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxt_baseNameObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(txt_baseName);
		IObservableValue baseNameSettingsgetAtParameterObserveValue = PojoProperties
				.value("baseName").observe(settings.getAccessTypePreferences());
		bindingContext.bindValue(observeTextTxt_baseNameObserveWidget,
				baseNameSettingsgetAtParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnCheckButton_RObserveWidget = WidgetProperties
				.selection().observe(btnCheckButtonR);
		IObservableValue checkRSettingsgetAtParameterObserveValue = PojoProperties
				.value("checkR").observe(settings.getAccessTypePreferences());
		bindingContext.bindValue(observeSelectionBtnCheckButton_RObserveWidget,
				checkRSettingsgetAtParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnCheckButton_WObserveWidget = WidgetProperties
				.selection().observe(btnCheckButtonW);
		IObservableValue checkWSettingsgetAtParameterObserveValue = PojoProperties
				.value("checkW").observe(settings.getAccessTypePreferences());
		bindingContext.bindValue(observeSelectionBtnCheckButton_WObserveWidget,
				checkWSettingsgetAtParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnCheckButton_RWObserveWidget = WidgetProperties
				.selection().observe(btnCheckButtonRW);
		IObservableValue checkRWSettingsgetAtParameterObserveValue = PojoProperties
				.value("checkRW").observe(settings.getAccessTypePreferences());
		bindingContext.bindValue(
				observeSelectionBtnCheckButton_RWObserveWidget,
				checkRWSettingsgetAtParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnCheckButton_RWXObserveWidget = WidgetProperties
				.selection().observe(btnCheckButtonRWX);
		IObservableValue checkRWXSettingsgetAtParameterObserveValue = PojoProperties
				.value("checkRWX").observe(settings.getAccessTypePreferences());
		bindingContext.bindValue(
				observeSelectionBtnCheckButton_RWXObserveWidget,
				checkRWXSettingsgetAtParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnCheckButton_RXObserveWidget = WidgetProperties
				.selection().observe(btnCheckButtonRX);
		IObservableValue checkRXSettingsgetAtParameterObserveValue = PojoProperties
				.value("checkRX").observe(settings.getAccessTypePreferences());
		bindingContext.bindValue(
				observeSelectionBtnCheckButton_RXObserveWidget,
				checkRXSettingsgetAtParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnCheckButton_XObserveWidget = WidgetProperties
				.selection().observe(btnCheckButtonX);
		IObservableValue checkXSettingsgetAtParameterObserveValue = PojoProperties
				.value("checkX").observe(settings.getAccessTypePreferences());
		bindingContext.bindValue(observeSelectionBtnCheckButton_XObserveWidget,
				checkXSettingsgetAtParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_ByteSizeListObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textByteSizeList);
		IObservableValue accessByteSizeSettingsgetAtParameterObserveValue = PojoProperties
				.value("accessByteSize").observe(settings.getAccessTypePreferences());
		bindingContext.bindValue(observeTextText_ByteSizeListObserveWidget,
				accessByteSizeSettingsgetAtParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_nBurstObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textNBurst);
		IObservableValue nBurstSettingsgetAtParameterObserveValue = PojoProperties
				.value("nBurst").observe(settings.getAccessTypePreferences());
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new StringToIntegerConverter());
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_nBurstObserveWidget,
				nBurstSettingsgetAtParameterObserveValue, strategy, strategy_1);
		//
		return bindingContext;
	}
}
