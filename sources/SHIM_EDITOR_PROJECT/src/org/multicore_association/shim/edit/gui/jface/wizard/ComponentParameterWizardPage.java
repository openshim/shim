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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.EndianType;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.MasterType;
import org.multicore_association.shim.api.RWType;
import org.multicore_association.shim.api.SizeUnitType;
import org.multicore_association.shim.api.SlaveComponent;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.gui.common.LabelConstants;
import org.multicore_association.shim.edit.gui.databinding.FloatToStringConverter;
import org.multicore_association.shim.edit.gui.databinding.IntToStringConverter;
import org.multicore_association.shim.edit.gui.databinding.StringToFloatConverter;
import org.multicore_association.shim.edit.gui.databinding.StringToIntConverter;
import org.multicore_association.shim.edit.gui.jface.ErrorMessagePool;
import org.multicore_association.shim.edit.gui.swt.UnsignedFloatModifier;
import org.multicore_association.shim.edit.gui.swt.UnsignedIntegerModifier;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimPreferences;

import swing2swt.layout.BorderLayout;

/**
 * Wizard page that allows configuration of the Components parameter.
 */
public class ComponentParameterWizardPage extends ShimWizardPageBase {

	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;

	public Label lblSystemName;
	public Label lblNumberOfMC;
	public Label lblNumberOfSLC;
	public Text textNumberOfMC;
	public Text textNumberOfSLC;
	public Text textSystemName;
	public Label lblNumberOfComponentset;
	public Text textNumberOfCSet;
	public Composite composite;
	public TabFolder tabFolder;
	public TabItem tbtmMCDefault;
	public TabItem tbtmSLC;
	public Composite compositeMasterComponent;
	public Label lblMcname;
	public Label lblType;
	public Label lblArch;
	public Text txtProcessor;
	public Text txtCell;
	public Composite compositeSlaveComponent;
	public Label lblSLName;
	public Text txtMemory;
	public Label lblSize;
	public Text textSize;
	public Label lblRwtype;
	public Combo comboMasterType;
	public Combo comboRwType;
	public Label lblNchannel;
	public Label lblNthread;
	public Text textNChannel;
	public Text textNThread;
	public Composite composite_3;
	public Combo comboSizeUnit;
	public TabItem tbtmCSDefault;
	public Composite compositeComponentSet;
	public Label lblName;
	public Text txtBaseCSName;
	public Label lblDescription;

	public Label lblClock;
	public Text textSystemClockValue;

	public Button btnClockfrequencyMC;
	public Composite composite_4;
	public Label label;
	public Text textMasterClockValue;
	public Combo comboMasterFrequencyValueUnit;
	public Label label_1;

	public boolean isFirst = true;
	public Label lblArchOpt;
	public Text textArchOpt;
	public Label lblEndian;
	public Combo comboEndian;
	public Label lblHz;
	public Label label_2;

	private ShimPreferences settings;

	private ErrorMessagePool pool;

	/**
	 * Constructs a new instance of ComponentParameterWizardPage.
	 */
	public ComponentParameterWizardPage() {
		super("wizardPage");
		setTitle("Setting ComponentSet Infomation");
		setDescription("You should input some basic infomation for new SHIM data for Creating initial component set structure.");
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

		if (!isFirst)
			return;
		isFirst = false;

		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new BorderLayout(0, 0));

		composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(BorderLayout.NORTH);
		composite.setLayout(null);

		lblSystemName = new Label(composite, SWT.NONE);
		lblSystemName.setBounds(5, 8, 84, 15);
		lblSystemName.setText(LabelConstants.SYSTEM_NAME);

		textSystemName = new Text(composite, SWT.BORDER);
		textSystemName.setBounds(200, 5, 251, 21);

		// When Using required check.
		RequiredItem itemSys = new RequiredItem(
				(Object) new SystemConfiguration(), lblSystemName.getText(),
				textSystemName);
		textSystemName.addListener(SWT.CHANGED, new RequiredListener(itemSys));

		lblNumberOfMC = new Label(composite, SWT.NONE);
		lblNumberOfMC.setBounds(5, 34, 173, 15);
		lblNumberOfMC.setText(LabelConstants.NUMBER_OF_MASTERCOMPONENT);

		textNumberOfMC = new Text(composite, SWT.BORDER);
		textNumberOfMC.setBounds(200, 31, 63, 21);
		textNumberOfMC.addModifyListener(new UnsignedIntegerModifier(pool,
				lblNumberOfMC.getText(), false));

		lblNumberOfSLC = new Label(composite, SWT.NONE);
		lblNumberOfSLC.setBounds(5, 66, 165, 15);
		lblNumberOfSLC.setText(LabelConstants.NUMBER_OF_SLAVECOMPONENT);

		textNumberOfSLC = new Text(composite, SWT.BORDER);
		textNumberOfSLC.setBounds(200, 63, 73, 21);
		textNumberOfSLC.addModifyListener(new UnsignedIntegerModifier(pool,
				lblNumberOfSLC.getText(), false));

		lblNumberOfComponentset = new Label(composite, SWT.NONE);
		lblNumberOfComponentset.setBounds(5, 98, 189, 15);
		lblNumberOfComponentset.setText(LabelConstants.NUMBER_OF_COMPONENTSET);

		textNumberOfCSet = new Text(composite, SWT.BORDER);
		textNumberOfCSet.setBounds(200, 95, 81, 21);
		textNumberOfCSet.addModifyListener(new UnsignedIntegerModifier(pool,
				lblNumberOfComponentset.getText(), false));

		lblClock = new Label(composite, SWT.NONE);
		lblClock.setLocation(290, 66);
		lblClock.setSize(31, 15);
		lblClock.setText(LabelConstants.CLOCK_FREQUENCY);

		textSystemClockValue = new Text(composite, SWT.BORDER);
		textSystemClockValue.setLocation(327, 63);
		textSystemClockValue.setSize(153, 21);
		textSystemClockValue.addModifyListener(new UnsignedFloatModifier(pool,
				lblClock.getText(), false));

		lblHz = new Label(composite, SWT.NONE);
		lblHz.setText(LabelConstants.UNITSTR_HZ);
		lblHz.setBounds(486, 66, 31, 15);

		tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.setLayoutData(BorderLayout.CENTER);

		tbtmMCDefault = new TabItem(tabFolder, SWT.NONE);
		tbtmMCDefault.setText("MasterComponent");

		compositeMasterComponent = new Composite(tabFolder, SWT.NONE);
		tbtmMCDefault.setControl(compositeMasterComponent);
		compositeMasterComponent.setLayout(new GridLayout(5, false));

		lblMcname = new Label(compositeMasterComponent, SWT.NONE);
		lblMcname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblMcname.setText(LabelConstants.BASE_NAME);
		new Label(compositeMasterComponent, SWT.NONE);

		txtProcessor = new Text(compositeMasterComponent, SWT.BORDER);
		txtProcessor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		// When Using required check.
		RequiredItem itemMC = new RequiredItem((Object) new MasterComponent(),
				lblMcname.getText() + " of " + tbtmMCDefault.getText(),
				txtProcessor);
		txtProcessor.addListener(SWT.CHANGED, new RequiredListener(itemMC));
		new Label(compositeMasterComponent, SWT.NONE);
		new Label(compositeMasterComponent, SWT.NONE);

		lblType = new Label(compositeMasterComponent, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblType.setText(LabelConstants.TYPE);
		new Label(compositeMasterComponent, SWT.NONE);

		comboMasterType = new ComboFactory(compositeMasterComponent)
				.createCombo(MasterType.class, ShimModelAdapter.isRequired(
						MasterComponent.class, "masterType"));
		comboMasterType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		lblEndian = new Label(compositeMasterComponent, SWT.NONE);
		lblEndian.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblEndian.setText(LabelConstants.ENDIAN);

		comboEndian = new ComboFactory(compositeMasterComponent).createCombo(
				EndianType.class,
				ShimModelAdapter.isRequired(MasterComponent.class, "endian"));
		comboEndian.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		lblArch = new Label(compositeMasterComponent, SWT.NONE);
		lblArch.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblArch.setText(LabelConstants.ARCH);
		new Label(compositeMasterComponent, SWT.NONE);

		txtCell = new Text(compositeMasterComponent, SWT.BORDER);
		txtCell.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		lblNchannel = new Label(compositeMasterComponent, SWT.NONE);
		lblNchannel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNchannel.setText(LabelConstants.NUMBER_OF_CHANNEL);

		textNChannel = new Text(compositeMasterComponent, SWT.BORDER);
		textNChannel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		textNChannel.addModifyListener(new UnsignedIntegerModifier(pool,
				lblNchannel.getText(), true));

		lblArchOpt = new Label(compositeMasterComponent, SWT.NONE);
		lblArchOpt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblArchOpt.setText(LabelConstants.ARCH_OPT);
		new Label(compositeMasterComponent, SWT.NONE);

		textArchOpt = new Text(compositeMasterComponent, SWT.BORDER);
		textArchOpt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		lblNthread = new Label(compositeMasterComponent, SWT.NONE);
		lblNthread.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNthread.setText(LabelConstants.NUMBER_OF_THREAD);

		textNThread = new Text(compositeMasterComponent, SWT.BORDER);
		textNThread.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		textNThread.addModifyListener(new UnsignedIntegerModifier(pool,
				lblNthread.getText(), true));

		tbtmSLC = new TabItem(tabFolder, SWT.NONE);
		tbtmSLC.setText("SlaveComponent");

		compositeSlaveComponent = new Composite(tabFolder, SWT.NONE);
		tbtmSLC.setControl(compositeSlaveComponent);
		compositeSlaveComponent.setLayout(new GridLayout(2, false));

		lblSLName = new Label(compositeSlaveComponent, SWT.NONE);
		lblSLName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblSLName.setText(LabelConstants.BASE_NAME);

		txtMemory = new Text(compositeSlaveComponent, SWT.BORDER);
		txtMemory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		// When Using required check.
		RequiredItem itemSL = new RequiredItem((Object) new SlaveComponent(),
				lblSLName.getText() + " of " + tbtmSLC.getText(), txtMemory);
		txtMemory.addListener(SWT.CHANGED, new RequiredListener(itemSL));

		lblSize = new Label(compositeSlaveComponent, SWT.NONE);
		lblSize.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblSize.setText(LabelConstants.SIZE);

		composite_3 = new Composite(compositeSlaveComponent, SWT.NONE);
		composite_3.setLayout(new GridLayout(2, false));

		textSize = new Text(composite_3, SWT.BORDER);
		GridData gd_text_Size = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_text_Size.widthHint = 80;
		textSize.setLayoutData(gd_text_Size);
		textSize.addModifyListener(new UnsignedIntegerModifier(pool, lblSize
				.getText(), false));

		comboSizeUnit = new ComboFactory(composite_3).createCombo(
				SizeUnitType.class,
				ShimModelAdapter.isRequired(SlaveComponent.class, "sizeUnit"));
		comboSizeUnit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		comboSizeUnit.setText("Choose Unit");

		lblRwtype = new Label(compositeSlaveComponent, SWT.NONE);
		lblRwtype.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblRwtype.setText(LabelConstants.RW_TYPE);

		comboRwType = new ComboFactory(compositeSlaveComponent).createCombo(
				RWType.class,
				ShimModelAdapter.isRequired(SlaveComponent.class, "rwType"));
		comboRwType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		new Label(compositeMasterComponent, SWT.NONE);
		new Label(compositeMasterComponent, SWT.NONE);

		label_1 = new Label(compositeMasterComponent, SWT.SEPARATOR
				| SWT.HORIZONTAL);
		new Label(compositeMasterComponent, SWT.NONE);
		new Label(compositeMasterComponent, SWT.NONE);
		new Label(compositeMasterComponent, SWT.NONE);
		new Label(compositeMasterComponent, SWT.NONE);

		btnClockfrequencyMC = new Button(compositeMasterComponent, SWT.CHECK);
		btnClockfrequencyMC.setText("ClockFrequency for Master");
		new Label(compositeMasterComponent, SWT.NONE);
		new Label(compositeMasterComponent, SWT.NONE);
		new Label(compositeMasterComponent, SWT.NONE);
		new Label(compositeMasterComponent, SWT.NONE);

		composite_4 = new Composite(compositeMasterComponent, SWT.NONE);
		GridData gd_composite_4 = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_composite_4.widthHint = 216;
		composite_4.setLayoutData(gd_composite_4);
		composite_4.setLayout(new GridLayout(3, false));

		label = new Label(composite_4, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label.setText(LabelConstants.CLOCK_FREQUENCY);

		textMasterClockValue = new Text(composite_4, SWT.BORDER);
		GridData gd_text_masterClockValue = new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1);
		gd_text_masterClockValue.widthHint = 90;
		textMasterClockValue.setLayoutData(gd_text_masterClockValue);
		float clock = settings.getMasterComponentPreferences().getClock();
		textMasterClockValue.setText(FloatToStringConverter.convertStr(clock));
		textMasterClockValue.addModifyListener(new UnsignedFloatModifier(pool,
				label.getText(), false));
		textMasterClockValue.addModifyListener(new ModifyListener() {

			/**
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent event) {
				Object source = event.getSource();
				if (source instanceof Text) {
					Text sourceText = (Text) source;
					try {
						float clock = Float.parseFloat(sourceText.getText());
						settings.getMasterComponentPreferences()
								.setClock(clock);
					} catch (NumberFormatException nf) {
					}
				}

			}
		});

		label_2 = new Label(composite_4, SWT.NONE);
		GridData gd_label_2 = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_label_2.widthHint = 30;
		label_2.setLayoutData(gd_label_2);
		label_2.setText(LabelConstants.UNITSTR_HZ);

		new Label(compositeMasterComponent, SWT.NONE);
		new Label(compositeMasterComponent, SWT.NONE);

		tbtmCSDefault = new TabItem(tabFolder, SWT.NONE);
		tbtmCSDefault.setText("ComponentSet");

		compositeComponentSet = new Composite(tabFolder, SWT.NONE);
		tbtmCSDefault.setControl(compositeComponentSet);
		compositeComponentSet.setLayout(new GridLayout(2, false));

		lblName = new Label(compositeComponentSet, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblName.setText(LabelConstants.BASE_NAME);

		txtBaseCSName = new Text(compositeComponentSet, SWT.BORDER);
		txtBaseCSName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		// When Using required check.
		RequiredItem itemCS = new RequiredItem((Object) new ComponentSet(),
				lblName.getText() + " of " + tbtmCSDefault.getText(),
				txtBaseCSName);
		txtBaseCSName.addListener(SWT.CHANGED, new RequiredListener(itemCS));

		new Label(compositeComponentSet, SWT.NONE);

		lblDescription = new Label(compositeComponentSet, SWT.NONE);
		lblDescription
				.setText("The instance of omponentSet name is\r\n     <BaseName>_L1_L2_..._LN   ('LN' is the number of level)");

		m_bindingContext = initDataBindings();
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxt_SystemNameObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textSystemName);
		IObservableValue systemNameSettingsgetCpParameterObserveValue = PojoProperties
				.value("systemName").observe(
						settings.getComponentsPreferences());
		bindingContext.bindValue(observeTextTxt_SystemNameObserveWidget,
				systemNameSettingsgetCpParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_NumberOfMCObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textNumberOfMC);
		IObservableValue numberMasterSettingsgetCpParameterObserveValue = PojoProperties
				.value("numberMaster").observe(
						settings.getComponentsPreferences());
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_NumberOfMCObserveWidget,
				numberMasterSettingsgetCpParameterObserveValue, strategy,
				strategy_1);
		//
		IObservableValue observeTextText_NumberOfSLCObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textNumberOfSLC);
		IObservableValue numberSlaveSettingsgetCpParameterObserveValue = PojoProperties
				.value("numberSlave").observe(
						settings.getComponentsPreferences());
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		strategy_2.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
		strategy_3.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_NumberOfSLCObserveWidget,
				numberSlaveSettingsgetCpParameterObserveValue, strategy_2,
				strategy_3);
		//
		IObservableValue observeTextText_NumberOfCSetObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textNumberOfCSet);
		IObservableValue numberComponentSettingsgetCpParameterObserveValue = PojoProperties
				.value("numberComponent").observe(
						settings.getComponentsPreferences());
		UpdateValueStrategy strategy_4 = new UpdateValueStrategy();
		strategy_4.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_5 = new UpdateValueStrategy();
		strategy_5.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_NumberOfCSetObserveWidget,
				numberComponentSettingsgetCpParameterObserveValue, strategy_4,
				strategy_5);
		//
		IObservableValue observeTextText_systemClockValueObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textSystemClockValue);
		IObservableValue clockSettingsgetCpParameterObserveValue = PojoProperties
				.value("clock").observe(settings.getComponentsPreferences());
		UpdateValueStrategy strategy_6 = new UpdateValueStrategy();
		strategy_6.setConverter(new StringToFloatConverter());
		UpdateValueStrategy strategy_7 = new UpdateValueStrategy();
		strategy_7.setConverter(new FloatToStringConverter());
		bindingContext
				.bindValue(observeTextText_systemClockValueObserveWidget,
						clockSettingsgetCpParameterObserveValue, strategy_6,
						strategy_7);
		//
		IObservableValue observeTextTxtProcessorObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(txtProcessor);
		IObservableValue baseNameSettingsgetCpMasterParameterObserveValue = PojoProperties
				.value("baseName").observe(
						settings.getMasterComponentPreferences());
		bindingContext.bindValue(observeTextTxtProcessorObserveWidget,
				baseNameSettingsgetCpMasterParameterObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionIndexCombo_MasterTypeObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboMasterType);
		IObservableValue typeSettingsgetCpMasterParameterObserveValue = PojoProperties
				.value("type")
				.observe(settings.getMasterComponentPreferences());
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_MasterTypeObserveWidget,
				typeSettingsgetCpMasterParameterObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionIndexCombo_endianObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboEndian);
		IObservableValue endianSettingsgetCpMasterParameterObserveValue = PojoProperties
				.value("endian").observe(
						settings.getMasterComponentPreferences());
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_endianObserveWidget,
				endianSettingsgetCpMasterParameterObserveValue, null, null);
		//
		IObservableValue observeTextTxtCellObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(txtCell);
		IObservableValue archSettingsgetCpMasterParameterObserveValue = PojoProperties
				.value("arch")
				.observe(settings.getMasterComponentPreferences());
		bindingContext.bindValue(observeTextTxtCellObserveWidget,
				archSettingsgetCpMasterParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_nChannelObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textNChannel);
		IObservableValue nChannelSettingsgetCpMasterParameterObserveValue = PojoProperties
				.value("nChannel").observe(
						settings.getMasterComponentPreferences());
		UpdateValueStrategy strategy_8 = new UpdateValueStrategy();
		strategy_8.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_9 = new UpdateValueStrategy();
		strategy_9.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_nChannelObserveWidget,
				nChannelSettingsgetCpMasterParameterObserveValue, strategy_8,
				strategy_9);
		//
		IObservableValue observeTextText_archOptObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textArchOpt);
		IObservableValue archOptSettingsgetCpMasterParameterObserveValue = PojoProperties
				.value("archOpt").observe(
						settings.getMasterComponentPreferences());
		bindingContext.bindValue(observeTextText_archOptObserveWidget,
				archOptSettingsgetCpMasterParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_nThreadObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textNThread);
		IObservableValue nThreadSettingsgetCpMasterParameterObserveValue = PojoProperties
				.value("nThread").observe(
						settings.getMasterComponentPreferences());
		UpdateValueStrategy strategy_10 = new UpdateValueStrategy();
		strategy_10.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_11 = new UpdateValueStrategy();
		strategy_11.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_nThreadObserveWidget,
				nThreadSettingsgetCpMasterParameterObserveValue, strategy_10,
				strategy_11);
		//
		IObservableValue observeSelectionBtn_ClockfrequencyMCObserveWidget = WidgetProperties
				.selection().observe(btnClockfrequencyMC);
		IObservableValue checkClockForMasterSettingsgetCpMasterParameterObserveValue = PojoProperties
				.value("checkClockForMaster").observe(
						settings.getMasterComponentPreferences());
		bindingContext.bindValue(
				observeSelectionBtn_ClockfrequencyMCObserveWidget,
				checkClockForMasterSettingsgetCpMasterParameterObserveValue,
				null, null);
		//
		IObservableValue observeTextTxtMemoryObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(txtMemory);
		IObservableValue baseNameSettingsgetCpSlaveParameterObserveValue = PojoProperties
				.value("baseName").observe(
						settings.getSlaveComponentPreferences());
		bindingContext.bindValue(observeTextTxtMemoryObserveWidget,
				baseNameSettingsgetCpSlaveParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_SizeObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textSize);
		IObservableValue sizeSettingsgetCpSlaveParameterObserveValue = PojoProperties
				.value("size").observe(settings.getSlaveComponentPreferences());
		UpdateValueStrategy strategy_14 = new UpdateValueStrategy();
		strategy_14.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_15 = new UpdateValueStrategy();
		strategy_15.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_SizeObserveWidget,
				sizeSettingsgetCpSlaveParameterObserveValue, strategy_14,
				strategy_15);
		//
		IObservableValue observeSingleSelectionIndexCombo_sizeUnitObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboSizeUnit);
		IObservableValue sizeUnitSettingsgetCpSlaveParameterObserveValue = PojoProperties
				.value("sizeUnit").observe(
						settings.getSlaveComponentPreferences());
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_sizeUnitObserveWidget,
				sizeUnitSettingsgetCpSlaveParameterObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionIndexCombo_rwTypeObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboRwType);
		IObservableValue rwTypeSettingsgetCpSlaveParameterObserveValue = PojoProperties
				.value("rwType").observe(
						settings.getSlaveComponentPreferences());
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_rwTypeObserveWidget,
				rwTypeSettingsgetCpSlaveParameterObserveValue, null, null);
		//
		IObservableValue observeTextTxtBaseCSNameObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(txtBaseCSName);
		IObservableValue baseNameSettingsgetCpComponentSetParameterObserveValue = PojoProperties
				.value("baseName").observe(
						settings.getComponentSetPreferences());
		bindingContext.bindValue(observeTextTxtBaseCSNameObserveWidget,
				baseNameSettingsgetCpComponentSetParameterObserveValue, null,
				null);
		//
		return bindingContext;
	}
}
