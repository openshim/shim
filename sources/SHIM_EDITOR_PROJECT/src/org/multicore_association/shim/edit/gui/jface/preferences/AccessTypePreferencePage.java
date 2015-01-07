/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface.preferences;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
import org.multicore_association.shim.edit.model.preferences.AccessTypePreferences;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesKey;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesStore;

/**
 * Preference page that allows configuration of the AccessType.
 */
public class AccessTypePreferencePage extends PreferencePage implements
		SelectionListener {

	private DataBindingContext m_bindingContext;
	private Text textBaseName;
	public Composite btnComposite;
	public Button btnCheckButtonR;
	public Button btnCheckButtonW;
	public Button btnCheckButtonRW;
	public Button btnCheckButtonRWX;
	public Button btnCheckButtonRX;
	public Button btnCheckButtonX;
	private Label lblAccessByteSize;
	private Text textAccessByteSize;
	private Label lblNumberOfBurst;
	private Text textNBurst;

	private ShimPreferencesStore store;
	private AccessTypePreferences parameter;
	private ErrorMessagePool pool;

	/**
	 * Constructs a new instance of AccessTypePreferencePage.
	 */
	public AccessTypePreferencePage() {
		setTitle("Access Type");

		store = ShimPreferencesStore.getInstance();
		parameter = new AccessTypePreferences();
		parameter.setBaseName(store.getString(ShimPreferencesKey.AT_NAME));
		parameter.setCheckR(store.getBoolean(ShimPreferencesKey.AT_CHECK_R));
		parameter.setCheckW(store.getBoolean(ShimPreferencesKey.AT_CHECK_W));
		parameter.setCheckRW(store.getBoolean(ShimPreferencesKey.AT_CHECK_RW));
		parameter.setCheckRWX(store.getBoolean(ShimPreferencesKey.AT_CHECK_RWX));
		parameter.setCheckRX(store.getBoolean(ShimPreferencesKey.AT_CHECK_RX));
		parameter.setCheckX(store.getBoolean(ShimPreferencesKey.AT_CHECK_X));
		parameter.setAccessByteSize(store
				.getString(ShimPreferencesKey.AT_ACCESS_BYTE_SIZE));
		parameter
				.setNBurst(store.getInteger(ShimPreferencesKey.AT_NUMBER_BURST));

		pool = new ErrorMessagePool(this);
	}

	/**
	 * Creates contents of this page.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	@Override
	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(2, false));

		Label lblBaseName = new Label(container, SWT.NONE);
		lblBaseName.setText(LabelConstants.BASE_NAME);

		textBaseName = new Text(container, SWT.BORDER);
		GridData gd_textBaseName = new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1);
		gd_textBaseName.widthHint = 200;
		textBaseName.setLayoutData(gd_textBaseName);
		textBaseName.addModifyListener(new TextRequiredModifier(pool,
				AccessType.class, "name"));

		Label lblRwType = new Label(container, SWT.NONE);
		lblRwType.setText(LabelConstants.RW_TYPE);

		btnComposite = new Composite(container, SWT.NONE);
		btnComposite.setLayout(new GridLayout(6, false));

		btnCheckButtonR = new Button(btnComposite, SWT.CHECK);
		btnCheckButtonR.setText("R");
		btnCheckButtonR.addSelectionListener(this);

		btnCheckButtonW = new Button(btnComposite, SWT.CHECK);
		btnCheckButtonW.setText("W");
		btnCheckButtonW.addSelectionListener(this);

		btnCheckButtonRW = new Button(btnComposite, SWT.CHECK);
		btnCheckButtonRW.setText("RW");
		btnCheckButtonRW.addSelectionListener(this);

		btnCheckButtonRWX = new Button(btnComposite, SWT.CHECK);
		btnCheckButtonRWX.setText("RWX");
		btnCheckButtonRWX.addSelectionListener(this);

		btnCheckButtonRX = new Button(btnComposite, SWT.CHECK);
		btnCheckButtonRX.setText("RX");
		btnCheckButtonRX.addSelectionListener(this);

		btnCheckButtonX = new Button(btnComposite, SWT.CHECK);
		btnCheckButtonX.setText("X");
		btnCheckButtonX.addSelectionListener(this);

		lblAccessByteSize = new Label(container, SWT.NONE);
		lblAccessByteSize.setText(LabelConstants.ACCESS_BYTE_SIZE);

		textAccessByteSize = new Text(container, SWT.BORDER);
		GridData gd_textAccessByteSize = new GridData(SWT.LEFT, SWT.CENTER,
				true, false, 1, 1);
		gd_textAccessByteSize.widthHint = 150;
		textAccessByteSize.setLayoutData(gd_textAccessByteSize);
		textAccessByteSize.addModifyListener(new ByteSizeListModifier(pool,
				lblAccessByteSize.getText()));
		textAccessByteSize.addModifyListener(new TextRequiredModifier(pool,
				AccessType.class, "accessByteSize"));

		lblNumberOfBurst = new Label(container, SWT.NONE);
		lblNumberOfBurst.setText(LabelConstants.NUMBER_OF_BURST);

		textNBurst = new Text(container, SWT.BORDER);
		GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 120;
		textNBurst.setLayoutData(gd_text);
		textNBurst.addModifyListener(new TextRequiredModifier(pool,
				AccessType.class, "nBurst"));
		textNBurst.addModifyListener(new UnsignedIntegerModifier(pool,
				lblNumberOfBurst.getText(), true));

		m_bindingContext = initDataBindings();

		return container;
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
		IObservableValue observeTextTextBaseNameObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textBaseName);
		IObservableValue baseNameParameterObserveValue = PojoProperties.value(
				"baseName").observe(parameter);
		bindingContext.bindValue(observeTextTextBaseNameObserveWidget,
				baseNameParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnCheckButton_RObserveWidget = WidgetProperties
				.selection().observe(btnCheckButtonR);
		IObservableValue checkRParameterObserveValue = PojoProperties.value(
				"checkR").observe(parameter);
		bindingContext.bindValue(observeSelectionBtnCheckButton_RObserveWidget,
				checkRParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnCheckButton_WObserveWidget = WidgetProperties
				.selection().observe(btnCheckButtonW);
		IObservableValue checkWParameterObserveValue = PojoProperties.value(
				"checkW").observe(parameter);
		bindingContext.bindValue(observeSelectionBtnCheckButton_WObserveWidget,
				checkWParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnCheckButton_RWObserveWidget = WidgetProperties
				.selection().observe(btnCheckButtonRW);
		IObservableValue checkRWParameterObserveValue = PojoProperties.value(
				"checkRW").observe(parameter);
		bindingContext.bindValue(
				observeSelectionBtnCheckButton_RWObserveWidget,
				checkRWParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnCheckButton_RWXObserveWidget = WidgetProperties
				.selection().observe(btnCheckButtonRWX);
		IObservableValue checkRWXParameterObserveValue = PojoProperties.value(
				"checkRWX").observe(parameter);
		bindingContext.bindValue(
				observeSelectionBtnCheckButton_RWXObserveWidget,
				checkRWXParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnCheckButton_RXObserveWidget = WidgetProperties
				.selection().observe(btnCheckButtonRX);
		IObservableValue checkRXParameterObserveValue = PojoProperties.value(
				"checkRX").observe(parameter);
		bindingContext.bindValue(
				observeSelectionBtnCheckButton_RXObserveWidget,
				checkRXParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnCheckButton_XObserveWidget = WidgetProperties
				.selection().observe(btnCheckButtonX);
		IObservableValue checkXParameterObserveValue = PojoProperties.value(
				"checkX").observe(parameter);
		bindingContext.bindValue(observeSelectionBtnCheckButton_XObserveWidget,
				checkXParameterObserveValue, null, null);
		//
		IObservableValue observeTextTextAccessByteSizeObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textAccessByteSize);
		IObservableValue accessByteSizeParameterObserveValue = PojoProperties
				.value("accessByteSize").observe(parameter);
		bindingContext.bindValue(observeTextTextAccessByteSizeObserveWidget,
				accessByteSizeParameterObserveValue, null, null);
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(
				SWT.Modify).observe(textNBurst);
		IObservableValue nBurstParameterObserveValue = PojoProperties.value(
				"nBurst").observe(parameter);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new StringToIntegerConverter());
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextTextObserveWidget,
				nBurstParameterObserveValue, strategy, strategy_1);
		//
		return bindingContext;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		parameter
				.setBaseName(store.getDefaultString(ShimPreferencesKey.AT_NAME));
		parameter.setCheckR(store
				.getDefaultBoolean(ShimPreferencesKey.AT_CHECK_R));
		parameter.setCheckW(store
				.getDefaultBoolean(ShimPreferencesKey.AT_CHECK_W));
		parameter.setCheckRW(store
				.getDefaultBoolean(ShimPreferencesKey.AT_CHECK_RW));
		parameter.setCheckRWX(store
				.getDefaultBoolean(ShimPreferencesKey.AT_CHECK_RWX));
		parameter.setCheckRX(store
				.getDefaultBoolean(ShimPreferencesKey.AT_CHECK_RX));
		parameter.setCheckX(store
				.getDefaultBoolean(ShimPreferencesKey.AT_CHECK_X));
		parameter.setAccessByteSize(store
				.getDefaultString(ShimPreferencesKey.AT_ACCESS_BYTE_SIZE));
		parameter.setNBurst(store
				.getDefaultInt(ShimPreferencesKey.AT_NUMBER_BURST));

		store.setToDefault(ShimPreferencesKey.AT_NAME);
		store.setToDefault(ShimPreferencesKey.AT_CHECK_R);
		store.setToDefault(ShimPreferencesKey.AT_CHECK_W);
		store.setToDefault(ShimPreferencesKey.AT_CHECK_RW);
		store.setToDefault(ShimPreferencesKey.AT_CHECK_RWX);
		store.setToDefault(ShimPreferencesKey.AT_CHECK_RX);
		store.setToDefault(ShimPreferencesKey.AT_CHECK_X);
		store.setToDefault(ShimPreferencesKey.AT_ACCESS_BYTE_SIZE);
		store.setToDefault(ShimPreferencesKey.AT_NUMBER_BURST);
		store.save();

		ShimPreferences.getCurrentInstance().loadAccessTypePreferences();

		m_bindingContext.updateTargets();

		widgetSelected(null);
		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	@Override
	protected void performApply() {
		m_bindingContext.updateModels();

		store.setAccessTypePreferences(parameter);
		store.save();

		ShimPreferences.getCurrentInstance().loadAccessTypePreferences();

		super.performApply();
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
		pool.setErrorMessage(this, msg);
	}
}
