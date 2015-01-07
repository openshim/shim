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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.EndianType;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.MasterType;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.gui.common.LabelConstants;
import org.multicore_association.shim.edit.gui.databinding.FloatToStringConverter;
import org.multicore_association.shim.edit.gui.databinding.IntToStringConverter;
import org.multicore_association.shim.edit.gui.databinding.StringToFloatConverter;
import org.multicore_association.shim.edit.gui.databinding.StringToIntegerConverter;
import org.multicore_association.shim.edit.gui.jface.ErrorMessagePool;
import org.multicore_association.shim.edit.gui.swt.TextRequiredModifier;
import org.multicore_association.shim.edit.gui.swt.UnsignedFloatModifier;
import org.multicore_association.shim.edit.gui.swt.UnsignedIntegerModifier;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimPreferences;
import org.multicore_association.shim.edit.model.preferences.MasterComponentPreferences;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesKey;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesStore;

/**
 * Preference page that allows configuration of the MasterComponent.
 */
public class MasterComponentPreferencePage extends PreferencePage {
	private DataBindingContext m_bindingContext;
	private Text txtBaseName;
	private Combo comboMasterType;
	private Combo comboEndian;
	private Text txtArch;
	private Text txtArchOpt;
	private Text txtNChannel;
	private Text txtNThread;
	private Text txtClock;

	private MasterComponentPreferences parameter;
	private ShimPreferencesStore store;
	private Button btnClockfrequencyForMaster;

	private ErrorMessagePool pool;

	/**
	 * Constructs a new instance of MasterComponentPreferencePage.
	 */
	public MasterComponentPreferencePage() {
		setTitle("MasterComponent");
		store = ShimPreferencesStore.getInstance();

		parameter = new MasterComponentPreferences();
		parameter.setBaseName(store
				.getString(ShimPreferencesKey.CP_MASTER_NAME));
		parameter.setType(store.getInt(ShimPreferencesKey.CP_MASTER_TYPE));
		parameter.setEndian(store.getInt(ShimPreferencesKey.CP_MASTER_ENDIAN));
		parameter.setArch(store.getString(ShimPreferencesKey.CP_MASTER_ARCH));
		parameter.setArchOpt(store
				.getString(ShimPreferencesKey.CP_MASTER_ARCH_OPT));
		parameter.setnChannel(store
				.getInteger(ShimPreferencesKey.CP_MASTER_NUMBER_CHANNEL));
		parameter.setnThread(store
				.getInteger(ShimPreferencesKey.CP_MASTER_NUMBER_THREAD));
		parameter.setCheckClockForMaster(store
				.getBoolean(ShimPreferencesKey.CP_MASTER_CHECK_CLOCKFREQUENCY));
		parameter.setClock(store.getFloat(ShimPreferencesKey.CP_MASTER_CLOCK));

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
		Color systemColor = Display.getCurrent().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND);

		Composite container = new Composite(parent, SWT.NULL);
		container.setBackground(systemColor);
		container.setLayout(new GridLayout(2, false));

		Label lblBaseName = new Label(container, SWT.NONE);
		lblBaseName.setText(LabelConstants.BASE_NAME);

		txtBaseName = new Text(container, SWT.BORDER);
		GridData gd_txtBaseName = new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1);
		gd_txtBaseName.widthHint = 200;
		txtBaseName.setLayoutData(gd_txtBaseName);
		txtBaseName.addModifyListener(new TextRequiredModifier(pool,
				MasterComponent.class, "name", lblBaseName.getText()));

		Label lblType = new Label(container, SWT.NONE);
		lblType.setText(LabelConstants.TYPE);

		comboMasterType = new ComboFactory(container).createCombo(
				MasterType.class, ShimModelAdapter.isRequired(
						MasterComponent.class, "masterType"));
		comboMasterType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1));

		Label lblEndian = new Label(container, SWT.NONE);
		lblEndian.setText(LabelConstants.ENDIAN);

		comboEndian = new ComboFactory(container).createCombo(EndianType.class,
				ShimModelAdapter.isRequired(MasterComponent.class, "endian"));
		comboEndian.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1));

		Label lblArch = new Label(container, SWT.NONE);
		lblArch.setText(LabelConstants.ARCH);

		txtArch = new Text(container, SWT.BORDER);
		GridData gd_txtArch = new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1);
		gd_txtArch.widthHint = 150;
		txtArch.setLayoutData(gd_txtArch);
		txtArch.addModifyListener(new TextRequiredModifier(pool,
				MasterComponent.class, "arch", lblArch.getText()));

		Label lblArchopt = new Label(container, SWT.NONE);
		lblArchopt.setText(LabelConstants.ARCH_OPT);

		txtArchOpt = new Text(container, SWT.BORDER);
		GridData gd_txtArchOpt = new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1);
		gd_txtArchOpt.widthHint = 200;
		txtArchOpt.setLayoutData(gd_txtArchOpt);
		txtArchOpt.addModifyListener(new TextRequiredModifier(pool,
				MasterComponent.class, "archOption", lblArchopt.getText()));

		Label lblNumberOfChannel = new Label(container, SWT.NONE);
		lblNumberOfChannel.setText(LabelConstants.NUMBER_OF_CHANNEL);

		txtNChannel = new Text(container, SWT.BORDER);
		GridData gd_txtNChannel = new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1);
		gd_txtNChannel.widthHint = 120;
		txtNChannel.setLayoutData(gd_txtNChannel);
		txtNChannel.addModifyListener(new UnsignedIntegerModifier(pool,
				lblNumberOfChannel.getText(), true));

		Label lblNumberOfThread = new Label(container, SWT.NONE);
		lblNumberOfThread.setText(LabelConstants.NUMBER_OF_THREAD);

		txtNThread = new Text(container, SWT.BORDER);
		GridData gd_txtNThread = new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1);
		gd_txtNThread.widthHint = 120;
		txtNThread.setLayoutData(gd_txtNThread);
		txtNThread.addModifyListener(new TextRequiredModifier(pool,
				MasterComponent.class, "nThread", lblNumberOfThread.getText()));
		txtNThread.addModifyListener(new UnsignedIntegerModifier(pool,
				lblNumberOfThread.getText(), true));

		btnClockfrequencyForMaster = new Button(container, SWT.CHECK);
		btnClockfrequencyForMaster.setSelection(true);
		btnClockfrequencyForMaster.setText("ClockFrequency for master");
		new Label(container, SWT.NONE);

		Label lblClock = new Label(container, SWT.NONE);
		lblClock.setText(LabelConstants.CLOCK_FREQUENCY);

		Composite container_1 = new Composite(container, SWT.NULL);
		GridData gd_container_1 = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_container_1.widthHint = 176;
		container_1.setLayoutData(gd_container_1);
		GridLayout gl_container_1 = new GridLayout(2, false);
		gl_container_1.verticalSpacing = 0;
		gl_container_1.marginWidth = 0;
		container_1.setLayout(gl_container_1);

		txtClock = new Text(container_1, SWT.BORDER);
		txtClock.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		txtClock.addModifyListener(new TextRequiredModifier(pool,
				MasterComponent.class, "clockFrequency", lblClock.getText()));
		txtClock.addModifyListener(new UnsignedFloatModifier(pool, lblClock
				.getText(), false));

		Label lblNewLabel_3 = new Label(container_1, SWT.NONE);
		lblNewLabel_3.setText(LabelConstants.UNITSTR_HZ);

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
		IObservableValue observeTextTxtBaseNameObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(txtBaseName);
		IObservableValue baseNameParameterObserveValue = PojoProperties.value(
				"baseName").observe(parameter);
		bindingContext.bindValue(observeTextTxtBaseNameObserveWidget,
				baseNameParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionComboMasterTypeObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboMasterType);
		IObservableValue typeParameterObserveValue = PojoProperties.value(
				"type").observe(parameter);
		bindingContext.bindValue(observeSelectionComboMasterTypeObserveWidget,
				typeParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionComboEndianObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboEndian);
		IObservableValue endianParameterObserveValue = PojoProperties.value(
				"endian").observe(parameter);
		bindingContext.bindValue(observeSelectionComboEndianObserveWidget,
				endianParameterObserveValue, null, null);
		//
		IObservableValue observeTextTxtArchObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(txtArch);
		IObservableValue archParameterObserveValue = PojoProperties.value(
				"arch").observe(parameter);
		bindingContext.bindValue(observeTextTxtArchObserveWidget,
				archParameterObserveValue, null, null);
		//
		IObservableValue observeTextTxtArchOptObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(txtArchOpt);
		IObservableValue archOptParameterObserveValue = PojoProperties.value(
				"archOpt").observe(parameter);
		bindingContext.bindValue(observeTextTxtArchOptObserveWidget,
				archOptParameterObserveValue, null, null);
		//
		IObservableValue observeTextTxtNChannelObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(txtNChannel);
		IObservableValue nChannelParameterObserveValue = PojoProperties.value(
				"nChannel").observe(parameter);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new StringToIntegerConverter());
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextTxtNChannelObserveWidget,
				nChannelParameterObserveValue, strategy, strategy_1);
		//
		IObservableValue observeTextTxtNThreadObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(txtNThread);
		IObservableValue nThreadParameterObserveValue = PojoProperties.value(
				"nThread").observe(parameter);
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		strategy_2.setConverter(new StringToIntegerConverter());
		UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
		strategy_3.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextTxtNThreadObserveWidget,
				nThreadParameterObserveValue, strategy_2, strategy_3);
		//
		IObservableValue observeSelectionBtnClockfrequencyForMasterObserveWidget = WidgetProperties
				.selection().observe(btnClockfrequencyForMaster);
		IObservableValue checkClockForMasterParameterObserveValue = PojoProperties
				.value("checkClockForMaster").observe(parameter);
		bindingContext.bindValue(
				observeSelectionBtnClockfrequencyForMasterObserveWidget,
				checkClockForMasterParameterObserveValue, null, null);
		//
		IObservableValue observeTextTxtClockObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(txtClock);
		IObservableValue clockParameterObserveValue = PojoProperties.value(
				"clock").observe(parameter);
		UpdateValueStrategy strategy_4 = new UpdateValueStrategy();
		strategy_4.setConverter(new StringToFloatConverter());
		UpdateValueStrategy strategy_5 = new UpdateValueStrategy();
		strategy_5.setConverter(new FloatToStringConverter());
		bindingContext.bindValue(observeTextTxtClockObserveWidget,
				clockParameterObserveValue, strategy_4, strategy_5);
		//
		return bindingContext;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	@Override
	protected void performApply() {
		m_bindingContext.updateModels();

		store.setMasterComponentPreferences(parameter);
		store.save();

		ShimPreferences.getCurrentInstance().loadMasterComponentPreferences();

		super.performApply();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		parameter.setBaseName(store
				.getDefaultString(ShimPreferencesKey.CP_MASTER_NAME));
		parameter.setType(store
				.getDefaultInt(ShimPreferencesKey.CP_MASTER_TYPE));
		parameter.setEndian(store
				.getDefaultInt(ShimPreferencesKey.CP_MASTER_ENDIAN));
		parameter.setArch(store
				.getDefaultString(ShimPreferencesKey.CP_MASTER_ARCH));
		parameter.setArchOpt(store
				.getDefaultString(ShimPreferencesKey.CP_MASTER_ARCH_OPT));
		parameter.setnChannel(store
				.getDefaultInt(ShimPreferencesKey.CP_MASTER_NUMBER_CHANNEL));
		parameter.setnThread(store
				.getDefaultInt(ShimPreferencesKey.CP_MASTER_NUMBER_THREAD));
		parameter
				.setCheckClockForMaster(store
						.getDefaultBoolean(ShimPreferencesKey.CP_MASTER_CHECK_CLOCKFREQUENCY));
		parameter.setClock(store
				.getDefaultFloat(ShimPreferencesKey.CP_MASTER_CLOCK));

		store.setToDefault(ShimPreferencesKey.CP_MASTER_NAME);
		store.setToDefault(ShimPreferencesKey.CP_MASTER_TYPE);
		store.setToDefault(ShimPreferencesKey.CP_MASTER_ENDIAN);
		store.setToDefault(ShimPreferencesKey.CP_MASTER_ARCH);
		store.setToDefault(ShimPreferencesKey.CP_MASTER_ARCH_OPT);
		store.setToDefault(ShimPreferencesKey.CP_MASTER_NUMBER_CHANNEL);
		store.setToDefault(ShimPreferencesKey.CP_MASTER_NUMBER_THREAD);
		store.setToDefault(ShimPreferencesKey.CP_MASTER_CHECK_CLOCKFREQUENCY);
		store.setToDefault(ShimPreferencesKey.CP_MASTER_CLOCK);
		store.save();

		ShimPreferences.getCurrentInstance().loadMasterComponentPreferences();

		m_bindingContext.updateTargets();

		super.performDefaults();
	}
}
