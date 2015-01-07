/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface.preferences;

import java.awt.BorderLayout;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.Cache;
import org.multicore_association.shim.api.CacheCoherencyType;
import org.multicore_association.shim.api.LockDownType;
import org.multicore_association.shim.api.SizeUnitType;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.gui.common.LabelConstants;
import org.multicore_association.shim.edit.gui.databinding.IntToStringConverter;
import org.multicore_association.shim.edit.gui.databinding.StringToIntConverter;
import org.multicore_association.shim.edit.gui.databinding.StringToIntegerConverter;
import org.multicore_association.shim.edit.gui.jface.ErrorMessagePool;
import org.multicore_association.shim.edit.gui.swt.TextRequiredModifier;
import org.multicore_association.shim.edit.gui.swt.UnsignedIntegerModifier;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimPreferences;
import org.multicore_association.shim.edit.model.preferences.CacheDataPreferences;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesKey;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesStore;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesStore.CacheTypeSelect;

/**
 * Preference page that allows configuration of the Cache.
 */
public class CachePreferencePage extends PreferencePage implements
		SelectionListener {
	private DataBindingContext m_bindingContext;

	public Button btnRadioButtonNon;
	public Button btnRadioButtonDandI;
	public Button btnRadioButtonUnified;
	public Group grpCacheType;
	public Group grpCache1;
	public Label lblCachechoherency;
	public Label lblSize1;
	public Label lblNway1;
	public Label lblLinesSze1;
	public Label lblLockdowntype1;
	public Text textNWay1;
	public Text textLineSize1;
	public Combo comboLockDownType1;
	public Composite composite_1;
	public Text textSize1;
	public Combo comboSizeUnit1;
	public Group grpCache2;
	public Label lblCachecoherency;
	public Label lblSize2;
	public Composite composite_3;
	public Text textSize2;
	public Combo comboSizeUnit2;
	public Label lblNway2;
	public Text textNWay2;
	public Label lblLineSize2;
	public Text textLineSize2;
	public Label lblLockdowntype2;
	public Combo comboLockDownType2;
	public Combo comboCacheCoherency1;
	public Combo comboCacheCoherency2;
	public Button btnRadioButtonData;
	public Button btnRadioButtonInstruction;

	private ShimPreferencesStore store;
	private CacheDataPreferences parameter;
	private ErrorMessagePool pool;

	/**
	 * Constructs a new instance of CachePreferencePage.
	 */
	public CachePreferencePage() {
		setTitle("Cache Data");

		store = ShimPreferencesStore.getInstance();
		parameter = new CacheDataPreferences();

		parameter.setCacheType(store.getInt(ShimPreferencesKey.CD_CACHE_TYPE));
		parameter.setCacheCoherenecy1(store
				.getInt(ShimPreferencesKey.CD_DATA_CACHE_COHERENCY));
		parameter.setCacheSize1(store
				.getInt(ShimPreferencesKey.CD_DATA_CACHE_SIZE));
		parameter.setCacheSizeUnit1(store
				.getInt(ShimPreferencesKey.CD_DATA_CACHE_SIZE_UNIT));
		parameter.setNumWay1(store
				.getInteger(ShimPreferencesKey.CD_DATA_NUMBER_WAY));
		parameter.setLineSize1(store
				.getInteger(ShimPreferencesKey.CD_DATA_LINE_SIZE));
		parameter.setLockDownType1(store
				.getInt(ShimPreferencesKey.CD_DATA_LOCK_DOWN_TYPE));

		parameter.setCacheCoherenecy2(store
				.getInt(ShimPreferencesKey.CD_INSTRUCTION_CACHE_COHERENCY));
		parameter.setCacheSize2(store
				.getInt(ShimPreferencesKey.CD_INSTRUCTION_CACHE_SIZE));
		parameter.setCacheSizeUnit2(store
				.getInt(ShimPreferencesKey.CD_INSTRUCTION_CACHE_SIZE_UNIT));
		parameter.setNumWay2(store
				.getInteger(ShimPreferencesKey.CD_INSTRUCTION_NUMBER_WAY));
		parameter.setLineSize2(store
				.getInteger(ShimPreferencesKey.CD_INSTRUCTION_LINE_SIZE));
		parameter.setLockDownType2(store
				.getInt(ShimPreferencesKey.CD_INSTRUCTION_LOCK_DOWN_TYPE));

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

		container.setLayoutData(BorderLayout.CENTER);
		container.setLayout(new GridLayout(2, false));

		grpCacheType = new Group(container, SWT.NONE);
		GridData gd_grpCacheType = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_grpCacheType.widthHint = 261;
		grpCacheType.setLayoutData(gd_grpCacheType);
		grpCacheType.setText("Cache Type");

		btnRadioButtonNon = new Button(grpCacheType, SWT.RADIO);
		btnRadioButtonNon.addSelectionListener(this);
		btnRadioButtonNon.setBounds(10, 26, 49, 16);
		btnRadioButtonNon.setText(LabelConstants.BUTTON_NON);
		btnRadioButtonNon
				.setSelection(parameter.getCacheType() == CacheTypeSelect.NONE
						.ordinal());

		btnRadioButtonDandI = new Button(grpCacheType, SWT.RADIO);
		btnRadioButtonDandI.setToolTipText("");
		btnRadioButtonDandI.setBounds(74, 66, 152, 16);
		btnRadioButtonDandI.addSelectionListener(this);
		btnRadioButtonDandI.setText(LabelConstants.BUTTON_DANDI);
		btnRadioButtonDandI
				.setSelection(parameter.getCacheType() == CacheTypeSelect.AND
						.ordinal());

		btnRadioButtonUnified = new Button(grpCacheType, SWT.RADIO);
		btnRadioButtonUnified.addSelectionListener(this);
		btnRadioButtonUnified.setBounds(74, 26, 96, 16);
		btnRadioButtonUnified.setText(LabelConstants.BUTTON_UNIFIED);
		btnRadioButtonUnified
				.setSelection(parameter.getCacheType() == CacheTypeSelect.UNIFIED
						.ordinal());

		btnRadioButtonData = new Button(grpCacheType, SWT.RADIO);
		btnRadioButtonData.addSelectionListener(this);
		btnRadioButtonData.setToolTipText("");
		btnRadioButtonData.setText(LabelConstants.BUTTON_DATA);
		btnRadioButtonData.setBounds(74, 48, 58, 16);
		btnRadioButtonData
				.setSelection(parameter.getCacheType() == CacheTypeSelect.DATA
						.ordinal());

		btnRadioButtonInstruction = new Button(grpCacheType, SWT.RADIO);
		btnRadioButtonInstruction.addSelectionListener(this);
		btnRadioButtonInstruction.setToolTipText("");
		btnRadioButtonInstruction.setText(LabelConstants.BUTTON_INSTRUCTION);
		btnRadioButtonInstruction.setBounds(138, 48, 88, 16);
		btnRadioButtonInstruction
				.setSelection(parameter.getCacheType() == CacheTypeSelect.INSTRUCTION
						.ordinal());

		new Label(container, SWT.NONE);

		grpCache1 = new Group(container, SWT.NONE);
		grpCache1.setText("Cache1");
		grpCache1.setLayout(new GridLayout(2, false));

		lblCachechoherency = new Label(grpCache1, SWT.NONE);
		lblCachechoherency.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblCachechoherency.setText(LabelConstants.CACHE_COHERENCY);

		comboCacheCoherency1 = new ComboFactory(grpCache1).createCombo(
				CacheCoherencyType.class,
				ShimModelAdapter.isRequired(Cache.class, "cacheCoherency"));
		comboCacheCoherency1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		comboCacheCoherency1.select(store
				.getInt(ShimPreferencesKey.CD_DATA_CACHE_COHERENCY));

		lblSize1 = new Label(grpCache1, SWT.NONE);
		lblSize1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblSize1.setText(LabelConstants.CACHE_SIZE);

		composite_1 = new Composite(grpCache1, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));

		textSize1 = new Text(composite_1, SWT.BORDER);
		textSize1.setText(store
				.getString(ShimPreferencesKey.CD_DATA_CACHE_SIZE));
		GridData gd_text_Size1 = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		gd_text_Size1.widthHint = 66;
		textSize1.setLayoutData(gd_text_Size1);
		textSize1.addModifyListener(new TextRequiredModifier(pool, Cache.class,
				"size", lblSize1.getText()));
		textSize1.addModifyListener(new UnsignedIntegerModifier(pool, lblSize1
				.getText(), false));

		comboSizeUnit1 = new ComboFactory(composite_1).createCombo(
				SizeUnitType.class,
				ShimModelAdapter.isRequired(Cache.class, "sizeUnit"));
		comboSizeUnit1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		comboSizeUnit1.select(store
				.getInt(ShimPreferencesKey.CD_DATA_CACHE_SIZE_UNIT));

		lblNway1 = new Label(grpCache1, SWT.NONE);
		lblNway1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNway1.setText(LabelConstants.NUMBER_OF_WAY);

		textNWay1 = new Text(grpCache1, SWT.BORDER);
		textNWay1.setText(store
				.getString(ShimPreferencesKey.CD_DATA_NUMBER_WAY));
		textNWay1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		textNWay1.addModifyListener(new TextRequiredModifier(pool, Cache.class,
				"nWay", lblNway1.getText()));
		textNWay1.addModifyListener(new UnsignedIntegerModifier(pool, lblNway1
				.getText(), true));

		lblLinesSze1 = new Label(grpCache1, SWT.NONE);
		lblLinesSze1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblLinesSze1.setText(LabelConstants.LINE_SIZE);

		textLineSize1 = new Text(grpCache1, SWT.BORDER);
		textLineSize1.setText(store
				.getString(ShimPreferencesKey.CD_DATA_LINE_SIZE));
		textLineSize1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		textLineSize1.addModifyListener(new TextRequiredModifier(pool,
				Cache.class, "lineSize", lblLinesSze1.getText()));
		textLineSize1.addModifyListener(new UnsignedIntegerModifier(pool,
				lblLinesSze1.getText(), true));

		lblLockdowntype1 = new Label(grpCache1, SWT.NONE);
		lblLockdowntype1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblLockdowntype1.setText(LabelConstants.LOCKDOWN_TYPE);

		comboLockDownType1 = new ComboFactory(grpCache1).createCombo(
				LockDownType.class,
				ShimModelAdapter.isRequired(Cache.class, "lockDownType"));
		comboLockDownType1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));

		grpCache2 = new Group(container, SWT.NONE);
		grpCache2.setText("Cache2");
		grpCache2.setLayout(new GridLayout(2, false));

		lblCachecoherency = new Label(grpCache2, SWT.NONE);
		lblCachecoherency.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblCachecoherency.setText(LabelConstants.CACHE_COHERENCY);

		comboCacheCoherency2 = new ComboFactory(grpCache2).createCombo(
				CacheCoherencyType.class,
				ShimModelAdapter.isRequired(Cache.class, "cacheCoherency"));
		comboCacheCoherency2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		comboCacheCoherency2.select(store
				.getInt(ShimPreferencesKey.CD_INSTRUCTION_CACHE_COHERENCY));

		lblSize2 = new Label(grpCache2, SWT.NONE);
		lblSize2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblSize2.setText(LabelConstants.CACHE_SIZE);

		composite_3 = new Composite(grpCache2, SWT.NONE);
		composite_3.setLayout(new GridLayout(2, false));

		textSize2 = new Text(composite_3, SWT.BORDER);
		textSize2.setText(store
				.getString(ShimPreferencesKey.CD_INSTRUCTION_CACHE_SIZE));
		GridData gd_text_Size2 = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		gd_text_Size2.widthHint = 49;
		textSize2.setLayoutData(gd_text_Size2);
		textSize2.addModifyListener(new TextRequiredModifier(pool, Cache.class,
				"size", lblSize2.getText()));
		textSize2.addModifyListener(new UnsignedIntegerModifier(pool, lblSize2
				.getText(), false));

		comboSizeUnit2 = new ComboFactory(composite_3).createCombo(
				SizeUnitType.class,
				ShimModelAdapter.isRequired(Cache.class, "sizeUnit"));
		comboSizeUnit2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		comboSizeUnit2.select(store
				.getInt(ShimPreferencesKey.CD_INSTRUCTION_CACHE_SIZE_UNIT));

		lblNway2 = new Label(grpCache2, SWT.NONE);
		lblNway2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNway2.setText(LabelConstants.NUMBER_OF_WAY);

		textNWay2 = new Text(grpCache2, SWT.BORDER);
		textNWay2.setText(store
				.getString(ShimPreferencesKey.CD_INSTRUCTION_NUMBER_WAY));
		textNWay2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		textNWay2.addModifyListener(new TextRequiredModifier(pool, Cache.class,
				"nWay", lblNway2.getText()));
		textNWay2.addModifyListener(new UnsignedIntegerModifier(pool, lblNway2
				.getText(), true));

		lblLineSize2 = new Label(grpCache2, SWT.NONE);
		lblLineSize2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblLineSize2.setText(LabelConstants.LINE_SIZE);

		textLineSize2 = new Text(grpCache2, SWT.BORDER);
		textLineSize2.setText(store
				.getString(ShimPreferencesKey.CD_INSTRUCTION_LINE_SIZE));
		textLineSize2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		textLineSize2.addModifyListener(new TextRequiredModifier(pool,
				Cache.class, "lineSize", lblLineSize2.getText()));
		textLineSize2.addModifyListener(new UnsignedIntegerModifier(pool,
				lblLineSize2.getText(), true));

		lblLockdowntype2 = new Label(grpCache2, SWT.NONE);
		lblLockdowntype2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblLockdowntype2.setText(LabelConstants.LOCKDOWN_TYPE);

		comboLockDownType2 = new ComboFactory(grpCache2).createCombo(
				LockDownType.class,
				ShimModelAdapter.isRequired(Cache.class, "lockDownType"));
		comboLockDownType2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		comboLockDownType2.select(store
				.getInt(ShimPreferencesKey.CD_INSTRUCTION_LOCK_DOWN_TYPE));

		reflectCacheType();
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
		IObservableValue observeSingleSelectionIndexCombo_CacheCoherency1ObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboCacheCoherency1);
		IObservableValue cacheCoherenecy1ParameterObserveValue = PojoProperties
				.value("cacheCoherenecy1").observe(parameter);
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_CacheCoherency1ObserveWidget,
				cacheCoherenecy1ParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_Size1ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textSize1);
		IObservableValue cacheSize1ParameterObserveValue = PojoProperties
				.value("cacheSize1").observe(parameter);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_Size1ObserveWidget,
				cacheSize1ParameterObserveValue, strategy, strategy_1);
		//
		IObservableValue observeSingleSelectionIndexCombo_SizeUnit1ObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboSizeUnit1);
		IObservableValue cacheSizeUnit1ParameterObserveValue = PojoProperties
				.value("cacheSizeUnit1").observe(parameter);
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_SizeUnit1ObserveWidget,
				cacheSizeUnit1ParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_nWay1ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textNWay1);
		IObservableValue numWay1ParameterObserveValue = PojoProperties.value(
				"numWay1").observe(parameter);
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		strategy_2.setConverter(new StringToIntegerConverter());
		UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
		strategy_3.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_nWay1ObserveWidget,
				numWay1ParameterObserveValue, strategy_2, strategy_3);
		//
		IObservableValue observeTextText_LineSize1ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textLineSize1);
		IObservableValue lineSize1ParameterObserveValue = PojoProperties.value(
				"lineSize1").observe(parameter);
		UpdateValueStrategy strategy_4 = new UpdateValueStrategy();
		strategy_4.setConverter(new StringToIntegerConverter());
		UpdateValueStrategy strategy_5 = new UpdateValueStrategy();
		strategy_5.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_LineSize1ObserveWidget,
				lineSize1ParameterObserveValue, strategy_4, strategy_5);
		//
		IObservableValue observeSingleSelectionIndexCombo_LockDownType1ObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboLockDownType1);
		IObservableValue lockDownType1ParameterObserveValue = PojoProperties
				.value("lockDownType1").observe(parameter);
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_LockDownType1ObserveWidget,
				lockDownType1ParameterObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionIndexCombo_CacheCoherency2ObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboCacheCoherency2);
		IObservableValue cacheCoherenecy2ParameterObserveValue = PojoProperties
				.value("cacheCoherenecy2").observe(parameter);
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_CacheCoherency2ObserveWidget,
				cacheCoherenecy2ParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_Size2ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textSize2);
		IObservableValue cacheSize2ParameterObserveValue = PojoProperties
				.value("cacheSize2").observe(parameter);
		UpdateValueStrategy strategy_6 = new UpdateValueStrategy();
		strategy_6.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_7 = new UpdateValueStrategy();
		strategy_7.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_Size2ObserveWidget,
				cacheSize2ParameterObserveValue, strategy_6, strategy_7);
		//
		IObservableValue observeSingleSelectionIndexCombo_SizeUnit2ObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboSizeUnit2);
		IObservableValue cacheSizeUnit2ParameterObserveValue = PojoProperties
				.value("cacheSizeUnit2").observe(parameter);
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_SizeUnit2ObserveWidget,
				cacheSizeUnit2ParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_nWay2ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textNWay2);
		IObservableValue numWay2ParameterObserveValue = PojoProperties.value(
				"numWay2").observe(parameter);
		UpdateValueStrategy strategy_8 = new UpdateValueStrategy();
		strategy_8.setConverter(new StringToIntegerConverter());
		UpdateValueStrategy strategy_9 = new UpdateValueStrategy();
		strategy_9.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_nWay2ObserveWidget,
				numWay2ParameterObserveValue, strategy_8, strategy_9);
		//
		IObservableValue observeTextText_LineSize2ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textLineSize2);
		IObservableValue lineSize2ParameterObserveValue = PojoProperties.value(
				"lineSize2").observe(parameter);
		UpdateValueStrategy strategy_10 = new UpdateValueStrategy();
		strategy_10.setConverter(new StringToIntegerConverter());
		UpdateValueStrategy strategy_11 = new UpdateValueStrategy();
		strategy_11.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_LineSize2ObserveWidget,
				lineSize2ParameterObserveValue, strategy_10, strategy_11);
		//
		IObservableValue observeSingleSelectionIndexCombo_LockDownType2ObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboLockDownType2);
		IObservableValue lockDownType2ParameterObserveValue = PojoProperties
				.value("lockDownType2").observe(parameter);
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_LockDownType2ObserveWidget,
				lockDownType2ParameterObserveValue, null, null);
		//
		return bindingContext;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		parameter.setCacheType(store
				.getDefaultInt(ShimPreferencesKey.CD_CACHE_TYPE));
		btnRadioButtonNon
				.setSelection(parameter.getCacheType() == CacheTypeSelect.NONE
						.ordinal());
		btnRadioButtonDandI
				.setSelection(parameter.getCacheType() == CacheTypeSelect.AND
						.ordinal());
		btnRadioButtonUnified
				.setSelection(parameter.getCacheType() == CacheTypeSelect.UNIFIED
						.ordinal());
		btnRadioButtonData
				.setSelection(parameter.getCacheType() == CacheTypeSelect.DATA
						.ordinal());
		btnRadioButtonInstruction
				.setSelection(parameter.getCacheType() == CacheTypeSelect.INSTRUCTION
						.ordinal());

		parameter.setCacheCoherenecy1(store
				.getDefaultInt(ShimPreferencesKey.CD_DATA_CACHE_COHERENCY));
		parameter.setCacheSize1(store
				.getDefaultInt(ShimPreferencesKey.CD_DATA_CACHE_SIZE));
		parameter.setCacheSizeUnit1(store
				.getDefaultInt(ShimPreferencesKey.CD_DATA_CACHE_SIZE_UNIT));
		parameter.setNumWay1(store
				.getDefaultInt(ShimPreferencesKey.CD_DATA_NUMBER_WAY));
		parameter.setLineSize1(store
				.getDefaultInt(ShimPreferencesKey.CD_DATA_LINE_SIZE));
		parameter.setLockDownType1(store
				.getDefaultInt(ShimPreferencesKey.CD_DATA_LOCK_DOWN_TYPE));

		parameter
				.setCacheCoherenecy2(store
						.getDefaultInt(ShimPreferencesKey.CD_INSTRUCTION_CACHE_COHERENCY));
		parameter.setCacheSize2(store
				.getDefaultInt(ShimPreferencesKey.CD_INSTRUCTION_CACHE_SIZE));
		parameter
				.setCacheSizeUnit2(store
						.getDefaultInt(ShimPreferencesKey.CD_INSTRUCTION_CACHE_SIZE_UNIT));
		parameter.setNumWay2(store
				.getDefaultInt(ShimPreferencesKey.CD_INSTRUCTION_NUMBER_WAY));
		parameter.setLineSize2(store
				.getDefaultInt(ShimPreferencesKey.CD_INSTRUCTION_LINE_SIZE));
		parameter
				.setLockDownType2(store
						.getDefaultInt(ShimPreferencesKey.CD_INSTRUCTION_LOCK_DOWN_TYPE));

		store.setToDefault(ShimPreferencesKey.CD_CACHE_TYPE);

		store.setToDefault(ShimPreferencesKey.CD_DATA_CACHE_COHERENCY);
		store.setToDefault(ShimPreferencesKey.CD_DATA_CACHE_SIZE);
		store.setToDefault(ShimPreferencesKey.CD_DATA_CACHE_SIZE_UNIT);
		store.setToDefault(ShimPreferencesKey.CD_DATA_NUMBER_WAY);
		store.setToDefault(ShimPreferencesKey.CD_DATA_LINE_SIZE);
		store.setToDefault(ShimPreferencesKey.CD_DATA_LOCK_DOWN_TYPE);

		store.setToDefault(ShimPreferencesKey.CD_INSTRUCTION_CACHE_COHERENCY);
		store.setToDefault(ShimPreferencesKey.CD_INSTRUCTION_CACHE_SIZE);
		store.setToDefault(ShimPreferencesKey.CD_INSTRUCTION_CACHE_SIZE_UNIT);
		store.setToDefault(ShimPreferencesKey.CD_INSTRUCTION_NUMBER_WAY);
		store.setToDefault(ShimPreferencesKey.CD_INSTRUCTION_LINE_SIZE);
		store.setToDefault(ShimPreferencesKey.CD_INSTRUCTION_LOCK_DOWN_TYPE);

		store.save();

		ShimPreferences.getCurrentInstance().loadCacheDataPreferences();

		m_bindingContext.updateTargets();

		reflectCacheType();
		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	@Override
	protected void performApply() {
		m_bindingContext.updateModels();

		store.setCacheDataPreferences(parameter);
		store.save();

		ShimPreferences.getCurrentInstance().loadCacheDataPreferences();

		super.performApply();
	}

	/**
	 * Reflects the change of the controls by the selection of CacheType.
	 */
	private void reflectCacheType() {
		if (btnRadioButtonNon.getSelection()) {
			prformButtonNon();
		} else if (btnRadioButtonUnified.getSelection()) {
			prformButtonUnified();
		} else if (btnRadioButtonData.getSelection()) {
			performButtonData();
		} else if (btnRadioButtonInstruction.getSelection()) {
			performButtonInstruction();
		} else if (btnRadioButtonDandI.getSelection()) {
			performButtonDataAndInstruction();
		}
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse
	 *      .swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		// NOOP
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt
	 *      .events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(SelectionEvent event) {
		if (event.getSource() == btnRadioButtonInstruction) {
			parameter.setCacheType(CacheTypeSelect.INSTRUCTION.ordinal());
			performButtonInstruction();
		}
		if (event.getSource() == btnRadioButtonData) {
			parameter.setCacheType(CacheTypeSelect.DATA.ordinal());
			performButtonData();
		}
		if (event.getSource() == btnRadioButtonUnified) {
			parameter.setCacheType(CacheTypeSelect.UNIFIED.ordinal());
			prformButtonUnified();
		}
		if (event.getSource() == btnRadioButtonNon) {
			parameter.setCacheType(CacheTypeSelect.NONE.ordinal());
			prformButtonNon();
		}
		if (event.getSource() == btnRadioButtonDandI) {
			parameter.setCacheType(CacheTypeSelect.AND.ordinal());
			performButtonDataAndInstruction();
		}
	}

	/**
	 * Reflects the change of the controls by selecting 'None' from CacheTypes.
	 */
	private void prformButtonNon() {
		grpCache1.setVisible(false);
		grpCache2.setVisible(false);
	}

	/**
	 * Reflects the change of the controls by selecting 'Unified' from
	 * CacheTypes.
	 */
	private void prformButtonUnified() {
		grpCache1.setVisible(true);
		grpCache1.setText("Cache(Unified)");
		grpCache2.setVisible(false);
	}

	/**
	 * Reflects the change of the controls by selecting 'Data and Instruction'
	 * from CacheTypes.
	 */
	private void performButtonDataAndInstruction() {
		grpCache1.setVisible(true);
		grpCache1.setText("Data Cache");
		grpCache2.setVisible(true);
		grpCache2.setText("Instruction Cache");
	}

	/**
	 * Reflects the change of the controls by selecting 'Data' from CacheTypes.
	 */
	private void performButtonData() {
		grpCache1.setVisible(true);
		grpCache1.setText("Cache(Data)");
		grpCache2.setVisible(false);
	}

	/**
	 * Reflects the change of the controls by selecting 'Instruction' from
	 * CacheTypes.
	 */
	private void performButtonInstruction() {
		grpCache1.setVisible(true);
		grpCache1.setText("Cache(Instruction)");
		grpCache2.setVisible(false);
	}

}
