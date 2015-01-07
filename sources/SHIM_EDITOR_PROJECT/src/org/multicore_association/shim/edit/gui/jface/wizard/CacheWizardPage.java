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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
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
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesStore.CacheTypeSelect;

import swing2swt.layout.BorderLayout;

/**
 * Wizard page that allows configuration of the Cache.
 */
public class CacheWizardPage extends WizardPage implements SelectionListener {

	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;

	public Composite composite;
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

	private ShimPreferences settings;

	private ErrorMessagePool pool;

	/**
	 * Constructs a new instance of CacheWizardPage.
	 */
	public CacheWizardPage() {
		super("wizardPage");
		setTitle("Cache Information");
		setDescription("This is Basic Information for Cache Data.");

		settings = ShimPreferences.getCurrentInstance();

		pool = new ErrorMessagePool(this);
	}

	/**
	 * Create contents of the wizard.
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
		composite.setLayout(new GridLayout(2, false));

		grpCacheType = new Group(composite, SWT.NONE);
		GridData gd_grpCacheType = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_grpCacheType.widthHint = 261;
		grpCacheType.setLayoutData(gd_grpCacheType);
		grpCacheType.setText("Cache Type");

		int cacheType = settings.getCacheDataPreferences().getCacheType();

		btnRadioButtonNon = new Button(grpCacheType, SWT.RADIO);
		btnRadioButtonNon.addSelectionListener(this);
		btnRadioButtonNon.setBounds(10, 26, 49, 16);
		btnRadioButtonNon.setText(LabelConstants.BUTTON_NON);
		btnRadioButtonNon.setSelection(cacheType == CacheTypeSelect.NONE
				.ordinal());

		btnRadioButtonDandI = new Button(grpCacheType, SWT.RADIO);
		btnRadioButtonDandI.setToolTipText("");
		btnRadioButtonDandI.setBounds(74, 66, 152, 16);
		btnRadioButtonDandI.addSelectionListener(this);
		btnRadioButtonDandI.setText(LabelConstants.BUTTON_DANDI);
		btnRadioButtonDandI.setSelection(cacheType == CacheTypeSelect.AND
				.ordinal());

		btnRadioButtonUnified = new Button(grpCacheType, SWT.RADIO);
		btnRadioButtonUnified.addSelectionListener(this);
		btnRadioButtonUnified.setBounds(74, 26, 96, 16);
		btnRadioButtonUnified.setText(LabelConstants.BUTTON_UNIFIED);
		btnRadioButtonUnified.setSelection(cacheType == CacheTypeSelect.UNIFIED
				.ordinal());

		btnRadioButtonData = new Button(grpCacheType, SWT.RADIO);
		btnRadioButtonData.addSelectionListener(this);
		btnRadioButtonData.setToolTipText("");
		btnRadioButtonData.setText(LabelConstants.BUTTON_DATA);
		btnRadioButtonData.setBounds(74, 48, 58, 16);
		btnRadioButtonData.setSelection(cacheType == CacheTypeSelect.DATA
				.ordinal());

		btnRadioButtonInstruction = new Button(grpCacheType, SWT.RADIO);
		btnRadioButtonInstruction.addSelectionListener(this);
		btnRadioButtonInstruction.setToolTipText("");
		btnRadioButtonInstruction.setText(LabelConstants.BUTTON_INSTRUCTION);
		btnRadioButtonInstruction.setBounds(138, 48, 88, 16);
		btnRadioButtonInstruction
				.setSelection(cacheType == CacheTypeSelect.INSTRUCTION
						.ordinal());

		new Label(composite, SWT.NONE);

		grpCache1 = new Group(composite, SWT.NONE);
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

		lblSize1 = new Label(grpCache1, SWT.NONE);
		lblSize1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblSize1.setText(LabelConstants.CACHE_SIZE);

		composite_1 = new Composite(grpCache1, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));

		textSize1 = new Text(composite_1, SWT.BORDER);
		GridData gdTextSize1 = new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1);
		gdTextSize1.widthHint = 66;
		textSize1.setLayoutData(gdTextSize1);
		textSize1.addModifyListener(new TextRequiredModifier(pool, Cache.class,
				"size", lblSize1.getText()));
		textSize1.addModifyListener(new UnsignedIntegerModifier(pool, lblSize1
				.getText(), false));

		comboSizeUnit1 = new ComboFactory(composite_1).createCombo(
				SizeUnitType.class,
				ShimModelAdapter.isRequired(Cache.class, "sizeUnit"));
		comboSizeUnit1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		lblNway1 = new Label(grpCache1, SWT.NONE);
		lblNway1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNway1.setText(LabelConstants.NUMBER_OF_WAY);

		textNWay1 = new Text(grpCache1, SWT.BORDER);
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

		grpCache2 = new Group(composite, SWT.NONE);
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

		lblSize2 = new Label(grpCache2, SWT.NONE);
		lblSize2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblSize2.setText(LabelConstants.CACHE_SIZE);

		composite_3 = new Composite(grpCache2, SWT.NONE);
		composite_3.setLayout(new GridLayout(2, false));

		textSize2 = new Text(composite_3, SWT.BORDER);
		GridData gdTextSize2 = new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1);
		gdTextSize2.widthHint = 49;
		textSize2.setLayoutData(gdTextSize2);
		textSize2.addModifyListener(new TextRequiredModifier(pool, Cache.class,
				"size", lblSize2.getText()));
		textSize2.addModifyListener(new UnsignedIntegerModifier(pool, lblSize2
				.getText(), false));

		comboSizeUnit2 = new ComboFactory(composite_3).createCombo(
				SizeUnitType.class,
				ShimModelAdapter.isRequired(Cache.class, "sizeUnit"));
		comboSizeUnit2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		lblNway2 = new Label(grpCache2, SWT.NONE);
		lblNway2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNway2.setText(LabelConstants.NUMBER_OF_WAY);

		textNWay2 = new Text(grpCache2, SWT.BORDER);
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

		if (btnRadioButtonNon.getSelection()) {
			performButtonNon();
		} else if (btnRadioButtonUnified.getSelection()) {
			performButtonUnified();
		} else if (btnRadioButtonData.getSelection()) {
			performButtonData();
		} else if (btnRadioButtonInstruction.getSelection()) {
			performButtonInstruction();
		} else if (btnRadioButtonDandI.getSelection()) {
			performButtonDataAndInstruction();
		}
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
		if (event.getSource() == btnRadioButtonInstruction) {
			settings.getCacheDataPreferences().setCacheType(
					CacheTypeSelect.INSTRUCTION.ordinal());
			performButtonInstruction();
		}
		if (event.getSource() == btnRadioButtonData) {
			settings.getCacheDataPreferences().setCacheType(
					CacheTypeSelect.DATA.ordinal());
			performButtonData();
		}
		if (event.getSource() == btnRadioButtonUnified) {
			settings.getCacheDataPreferences().setCacheType(
					CacheTypeSelect.UNIFIED.ordinal());
			performButtonUnified();
		}
		if (event.getSource() == btnRadioButtonNon) {
			settings.getCacheDataPreferences().setCacheType(
					CacheTypeSelect.NONE.ordinal());
			performButtonNon();
		}
		if (event.getSource() == btnRadioButtonDandI) {
			settings.getCacheDataPreferences().setCacheType(
					CacheTypeSelect.AND.ordinal());
			performButtonDataAndInstruction();
		}
	}

	/**
	 * Reflects the change of the controls by selecting 'None' from CacheTypes.
	 */
	private void performButtonNon() {
		grpCache1.setVisible(false);
		grpCache2.setVisible(false);
	}

	/**
	 * Reflects the change of the controls by selecting 'Unified' from
	 * CacheTypes.
	 */
	private void performButtonUnified() {
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
		IObservableValue cacheCoherenecy1SettingsgetCdParameterObserveValue = PojoProperties
				.value("cacheCoherenecy1").observe(
						settings.getCacheDataPreferences());
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_CacheCoherency1ObserveWidget,
				cacheCoherenecy1SettingsgetCdParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_Size1ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textSize1);
		IObservableValue cacheSize1SettingsgetCdParameterObserveValue = PojoProperties
				.value("cacheSize1")
				.observe(settings.getCacheDataPreferences());
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_Size1ObserveWidget,
				cacheSize1SettingsgetCdParameterObserveValue, strategy,
				strategy_1);
		//
		IObservableValue observeSingleSelectionIndexCombo_SizeUnit1ObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboSizeUnit1);
		IObservableValue cacheSizeUnit1SettingsgetCdParameterObserveValue = PojoProperties
				.value("cacheSizeUnit1").observe(
						settings.getCacheDataPreferences());
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_SizeUnit1ObserveWidget,
				cacheSizeUnit1SettingsgetCdParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_nWay1ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textNWay1);
		IObservableValue numWay1SettingsgetCdParameterObserveValue = PojoProperties
				.value("numWay1").observe(settings.getCacheDataPreferences());
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		strategy_2.setConverter(new StringToIntegerConverter());
		UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
		strategy_3.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_nWay1ObserveWidget,
				numWay1SettingsgetCdParameterObserveValue, strategy_2,
				strategy_3);
		//
		IObservableValue observeTextText_LineSize1ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textLineSize1);
		IObservableValue lineSize1SettingsgetCdParameterObserveValue = PojoProperties
				.value("lineSize1").observe(settings.getCacheDataPreferences());
		UpdateValueStrategy strategy_4 = new UpdateValueStrategy();
		strategy_4.setConverter(new StringToIntegerConverter());
		UpdateValueStrategy strategy_5 = new UpdateValueStrategy();
		strategy_5.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_LineSize1ObserveWidget,
				lineSize1SettingsgetCdParameterObserveValue, strategy_4,
				strategy_5);
		//
		IObservableValue observeSingleSelectionIndexCombo_LockDownType1ObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboLockDownType1);
		IObservableValue lockDownType1SettingsgetCdParameterObserveValue = PojoProperties
				.value("lockDownType1").observe(
						settings.getCacheDataPreferences());
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_LockDownType1ObserveWidget,
				lockDownType1SettingsgetCdParameterObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionIndexCombo_CacheCoherency2ObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboCacheCoherency2);
		IObservableValue cacheCoherenecy2SettingsgetCdParameterObserveValue = PojoProperties
				.value("cacheCoherenecy2").observe(
						settings.getCacheDataPreferences());
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_CacheCoherency2ObserveWidget,
				cacheCoherenecy2SettingsgetCdParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_Size2ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textSize2);
		IObservableValue cacheSize2SettingsgetCdParameterObserveValue = PojoProperties
				.value("cacheSize2")
				.observe(settings.getCacheDataPreferences());
		UpdateValueStrategy strategy_6 = new UpdateValueStrategy();
		strategy_6.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_7 = new UpdateValueStrategy();
		strategy_7.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_Size2ObserveWidget,
				cacheSize2SettingsgetCdParameterObserveValue, strategy_6,
				strategy_7);
		//
		IObservableValue observeSingleSelectionIndexCombo_SizeUnit2ObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboSizeUnit2);
		IObservableValue cacheSizeUnit2SettingsgetCdParameterObserveValue = PojoProperties
				.value("cacheSizeUnit2").observe(
						settings.getCacheDataPreferences());
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_SizeUnit2ObserveWidget,
				cacheSizeUnit2SettingsgetCdParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_nWay2ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textNWay2);
		IObservableValue numWay2SettingsgetCdParameterObserveValue = PojoProperties
				.value("numWay2").observe(settings.getCacheDataPreferences());
		UpdateValueStrategy strategy_8 = new UpdateValueStrategy();
		strategy_8.setConverter(new StringToIntegerConverter());
		UpdateValueStrategy strategy_9 = new UpdateValueStrategy();
		strategy_9.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_nWay2ObserveWidget,
				numWay2SettingsgetCdParameterObserveValue, strategy_8,
				strategy_9);
		//
		IObservableValue observeTextText_LineSize2ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textLineSize2);
		IObservableValue lineSize2SettingsgetCdParameterObserveValue = PojoProperties
				.value("lineSize2").observe(settings.getCacheDataPreferences());
		UpdateValueStrategy strategy_10 = new UpdateValueStrategy();
		strategy_10.setConverter(new StringToIntegerConverter());
		UpdateValueStrategy strategy_11 = new UpdateValueStrategy();
		strategy_11.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_LineSize2ObserveWidget,
				lineSize2SettingsgetCdParameterObserveValue, strategy_10,
				strategy_11);
		//
		IObservableValue observeSingleSelectionIndexCombo_LockDownType2ObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboLockDownType2);
		IObservableValue lockDownType2SettingsgetCdParameterObserveValue = PojoProperties
				.value("lockDownType2").observe(
						settings.getCacheDataPreferences());
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_LockDownType2ObserveWidget,
				lockDownType2SettingsgetCdParameterObserveValue, null, null);
		//
		return bindingContext;
	}
}
