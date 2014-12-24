/*
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.FIFOCommunication;
import org.multicore_association.shim.api.OperationType;
import org.multicore_association.shim.api.SharedMemoryCommunication;
import org.multicore_association.shim.api.SharedRegisterCommunication;
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
import org.multicore_association.shim.edit.model.preferences.CommunicationSetPreferences;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesKey;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesStore;

import swing2swt.layout.BorderLayout;

/**
 * Preference page that allows configuration of the CommunicationSet.
 */
public class CommunicationSetPreferencePage extends PreferencePage {
	private DataBindingContext m_bindingContext;

	private Button btnInterrupt;
	private Button btnFIFO;
	private Button btnEvent;
	private Button btnSharedMemory;
	private Button btnSharedRegister;
	private Button btnCheckALL;
	private Label lblDatasize;
	private Text textFiFoDataSize;
	private Label lblQueuesize;
	private Text textQueueSize;
	private Label lblDatasize_1;
	private Text textSharedMemoryDataSize;
	private Label label_2;
	public Text textSRDataSize;
	private Combo comboSRDataSizeUnit;
	private Label lblNregister;
	public Text textNRegister;
	private Composite composite_2;
	private Composite composite_4;
	private Composite composite_6;
	private Composite composite_7;
	private Composite composite_3;
	private Button btnDontConnect;
	private Combo comboFIFODataSizeUnit;
	private Combo comboSharedMemoryDataSizeUnit;

	private Label lblOperationtype;
	private Combo comboOpType;

	private ShimPreferencesStore store;

	private CommunicationSetPreferences parameter;

	private ErrorMessagePool pool;

	/**
	 * Constructs a new instance of CommunicationSetPreferencePage.
	 */
	public CommunicationSetPreferencePage() {
		setTitle("Communication Set");

		store = ShimPreferencesStore.getInstance();

		parameter = new CommunicationSetPreferences();

		parameter.setCheckInterruptCommunication(store
				.getBoolean(ShimPreferencesKey.CS_CHECK_INTERRUPT));

		parameter.setCheckEventCommunication(store
				.getBoolean(ShimPreferencesKey.CS_CHECK_EVENT));

		parameter.setCheckFIFOCommunication(store
				.getBoolean(ShimPreferencesKey.CS_CHECK_FIFO));
		parameter.setFifoDataSize(store
				.getInt(ShimPreferencesKey.CS_FIFO_DATA_SIZE));
		parameter.setFifoSizeUnit(store
				.getInt(ShimPreferencesKey.CS_FIFO_DATA_SIZE_UNIT));
		parameter.setQueueSize(store
				.getInt(ShimPreferencesKey.CS_FIFO_QUEUE_SIZE));

		parameter.setCheckSharedMemoryCommunication(store
				.getBoolean(ShimPreferencesKey.CS_CHECK_SMC));
		parameter.setSharedMemoryDataSize(store
				.getInteger(ShimPreferencesKey.CS_SMC_DATA_SIZE));
		parameter.setSharedMemorySizeUnit(store
				.getInt(ShimPreferencesKey.CS_SMC_DATA_SIZE_UNIT));
		parameter.setOperationType(store
				.getInt(ShimPreferencesKey.CS_SMC_OPERATION));

		parameter.setCheckSharedRegisterCommunication(store
				.getBoolean(ShimPreferencesKey.CS_CHECK_SRC));
		parameter.setSharedRegisterDataSize(store
				.getInt(ShimPreferencesKey.CS_SRC_DATA_SIZE));
		parameter.setSharedRegisterSizeUnit(store
				.getInt(ShimPreferencesKey.CS_SRC_DATA_SIZE_UNIT));
		parameter.setNumRegister(store
				.getInt(ShimPreferencesKey.CS_SRC_NUMBER_REGISTER));

		parameter.setDontConnect(store
				.getBoolean(ShimPreferencesKey.CS_DONT_CONNECT));

		pool = new ErrorMessagePool(this);
	}

	/**
	 * Create contents of this preference page.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	@Override
	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		container.setLayoutData(BorderLayout.CENTER);
		container.setLayout(new GridLayout(1, false));

		btnCheckALL = new Button(container, SWT.CHECK);
		btnCheckALL.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		btnCheckALL.setText(LabelConstants.SELECT_DESELECT_ALL);
		btnCheckALL.setSelection(true);

		btnCheckALL.addSelectionListener(new SelectionListener() {

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
				Button selectedButton = ((Button) event.widget);
				selectedButton.setGrayed(false);
				boolean check = selectedButton.getSelection();

				parameter.setCheckInterruptCommunication(check);
				parameter.setCheckEventCommunication(check);
				parameter.setCheckFIFOCommunication(check);
				parameter.setCheckSharedMemoryCommunication(check);
				parameter.setCheckSharedRegisterCommunication(check);

				m_bindingContext.updateTargets();
			}
		});
		SelectionListener grayedCheckAllListner = new SelectionListener() {

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
				updateCheckAllButtonStatus();
			}
		};

		composite_2 = new Composite(container, SWT.NONE);
		composite_2.setLayout(new GridLayout(1, false));

		btnInterrupt = new Button(composite_2, SWT.CHECK);
		GridData gd_btn_Interrupt = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_btn_Interrupt.widthHint = 199;
		btnInterrupt.setLayoutData(gd_btn_Interrupt);
		btnInterrupt.setText("InterruptCommunication");
		btnInterrupt.addSelectionListener(grayedCheckAllListner);

		composite_3 = new Composite(container, SWT.NONE);
		composite_3.setLayout(new GridLayout(1, false));

		btnEvent = new Button(composite_3, SWT.CHECK);
		btnEvent.setText("EventCommunication");
		btnEvent.addSelectionListener(grayedCheckAllListner);

		composite_4 = new Composite(container, SWT.BORDER);
		GridData gd_composite_4 = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_composite_4.widthHint = 499;
		composite_4.setLayoutData(gd_composite_4);
		composite_4.setLayout(new GridLayout(4, false));

		btnFIFO = new Button(composite_4, SWT.CHECK);
		btnFIFO.setText("FIFOCommunication");
		btnFIFO.addSelectionListener(grayedCheckAllListner);

		lblDatasize = new Label(composite_4, SWT.NONE);
		lblDatasize.setText(LabelConstants.DATA_SIZE);

		textFiFoDataSize = new Text(composite_4, SWT.BORDER);
		GridData gd_text_FiFoDataSize = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd_text_FiFoDataSize.widthHint = 50;
		textFiFoDataSize.setLayoutData(gd_text_FiFoDataSize);
		textFiFoDataSize.addModifyListener(new TextRequiredModifier(pool,
				FIFOCommunication.class, "dataSize", lblDatasize.getText()));
		textFiFoDataSize.addModifyListener(new UnsignedIntegerModifier(pool,
				lblDatasize.getText(), false));

		// Add DataSizeUnit.
		comboFIFODataSizeUnit = new ComboFactory(composite_4).createCombo(
				SizeUnitType.class, ShimModelAdapter.isRequired(
						FIFOCommunication.class, "dataSizeUnit"));
		comboFIFODataSizeUnit.setSize(87, 23);
		new Label(composite_4, SWT.NONE);

		lblQueuesize = new Label(composite_4, SWT.NONE);
		lblQueuesize.setText(LabelConstants.QUEUE_SIZE);

		textQueueSize = new Text(composite_4, SWT.BORDER);
		GridData gd_text_QueueSize = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_text_QueueSize.widthHint = 54;
		textQueueSize.setLayoutData(gd_text_QueueSize);
		textQueueSize.setSize(72, 21);
		new Label(composite_4, SWT.NONE);
		textQueueSize.addModifyListener(new TextRequiredModifier(pool,
				FIFOCommunication.class, "queueSize", lblQueuesize.getText()));
		textQueueSize.addModifyListener(new UnsignedIntegerModifier(pool,
				lblQueuesize.getText(), false));

		composite_6 = new Composite(container, SWT.BORDER);
		composite_6.setLayout(new GridLayout(4, false));

		btnSharedMemory = new Button(composite_6, SWT.CHECK);
		btnSharedMemory.setText("SharedMemoryCommunication");
		btnSharedMemory.addSelectionListener(grayedCheckAllListner);

		lblDatasize_1 = new Label(composite_6, SWT.NONE);
		lblDatasize_1.setSize(52, 15);
		GridData gd_text_SM_DataSize = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd_text_SM_DataSize.widthHint = 67;
		lblDatasize_1.setLayoutData(gd_text_SM_DataSize);
		lblDatasize_1.setText(LabelConstants.DATA_SIZE);

		textSharedMemoryDataSize = new Text(composite_6, SWT.BORDER);
		GridData gd_text_SharedMemory_DataSize = new GridData(SWT.FILL,
				SWT.CENTER, false, false, 1, 1);
		gd_text_SharedMemory_DataSize.widthHint = 67;
		textSharedMemoryDataSize.setLayoutData(gd_text_SharedMemory_DataSize);
		textSharedMemoryDataSize.addModifyListener(new TextRequiredModifier(
				pool, SharedMemoryCommunication.class, "dataSize",
				lblDatasize_1.getText()));
		textSharedMemoryDataSize.addModifyListener(new UnsignedIntegerModifier(
				pool, lblDatasize_1.getText(), true));

		// Add DataSizeUnit.
		comboSharedMemoryDataSizeUnit = new ComboFactory(composite_6)
				.createCombo(SizeUnitType.class, ShimModelAdapter.isRequired(
						SharedMemoryCommunication.class, "dataSizeUnit"));
		comboSharedMemoryDataSizeUnit.setSize(67, 23);

		new Label(composite_6, SWT.NONE);

		lblOperationtype = new Label(composite_6, SWT.NONE);
		lblOperationtype.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblOperationtype.setText(LabelConstants.OPERATION_TYPE);

		comboOpType = new ComboFactory(composite_6).createCombo(
				OperationType.class, ShimModelAdapter.isRequired(
						SharedMemoryCommunication.class, "operationType"));
		new Label(composite_6, SWT.NONE);
		comboOpType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		composite_7 = new Composite(container, SWT.BORDER);
		composite_7.setLayout(new GridLayout(4, false));

		btnSharedRegister = new Button(composite_7, SWT.CHECK);
		btnSharedRegister.setText("SharedRegisterCommunication");
		btnSharedRegister.addSelectionListener(grayedCheckAllListner);

		label_2 = new Label(composite_7, SWT.NONE);
		label_2.setSize(52, 15);
		label_2.setText(LabelConstants.DATA_SIZE);

		textSRDataSize = new Text(composite_7, SWT.BORDER);
		GridData gd_text_SR_DataSize = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd_text_SR_DataSize.widthHint = 67;
		textSRDataSize.setLayoutData(gd_text_SR_DataSize);
		textSRDataSize.addModifyListener(new TextRequiredModifier(pool,
				SharedRegisterCommunication.class, "dataSize", label_2
						.getText()));
		textSRDataSize.addModifyListener(new UnsignedIntegerModifier(pool,
				label_2.getText(), false));

		comboSRDataSizeUnit = new ComboFactory(composite_7).createCombo(
				SizeUnitType.class, ShimModelAdapter.isRequired(
						SharedRegisterCommunication.class, "dataSizeUnit"));

		// Change DataSizeUnit.
		comboSRDataSizeUnit.setSize(87, 23);
		new Label(composite_7, SWT.NONE);

		lblNregister = new Label(composite_7, SWT.NONE);
		lblNregister.setSize(55, 15);
		lblNregister.setText(LabelConstants.NUMBER_OF_REGISTER);

		textNRegister = new Text(composite_7, SWT.BORDER);
		GridData gd_text_nRegister = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_text_nRegister.widthHint = 60;
		textNRegister.setLayoutData(gd_text_nRegister);
		textNRegister.setSize(72, 21);
		new Label(composite_7, SWT.NONE);
		textNRegister.addModifyListener(new TextRequiredModifier(pool,
				SharedRegisterCommunication.class, "nRegister", lblNregister
						.getText()));
		textNRegister.addModifyListener(new UnsignedIntegerModifier(pool,
				lblNregister.getText(), false));

		btnDontConnect = new Button(container, SWT.CHECK);
		btnDontConnect.setText("don't connect  the relation of ancestor");

		m_bindingContext = initDataBindings();

		updateCheckAllButtonStatus();

		return container;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		// set default value to bean
		parameter.setCheckInterruptCommunication(store
				.getDefaultBoolean(ShimPreferencesKey.CS_CHECK_INTERRUPT));

		parameter.setCheckEventCommunication(store
				.getDefaultBoolean(ShimPreferencesKey.CS_CHECK_EVENT));

		parameter.setCheckFIFOCommunication(store
				.getDefaultBoolean(ShimPreferencesKey.CS_CHECK_FIFO));
		parameter.setFifoDataSize(store
				.getDefaultInt(ShimPreferencesKey.CS_FIFO_DATA_SIZE));
		parameter.setFifoSizeUnit(store
				.getDefaultInt(ShimPreferencesKey.CS_FIFO_DATA_SIZE_UNIT));
		parameter.setQueueSize(store
				.getDefaultInt(ShimPreferencesKey.CS_FIFO_QUEUE_SIZE));

		parameter.setCheckSharedMemoryCommunication(store
				.getDefaultBoolean(ShimPreferencesKey.CS_CHECK_SMC));
		parameter.setSharedMemoryDataSize(store
				.getDefaultInt(ShimPreferencesKey.CS_SMC_DATA_SIZE));
		parameter.setSharedMemorySizeUnit(store
				.getDefaultInt(ShimPreferencesKey.CS_SMC_DATA_SIZE_UNIT));
		parameter.setOperationType(store
				.getDefaultInt(ShimPreferencesKey.CS_SMC_OPERATION));

		parameter.setCheckSharedRegisterCommunication(store
				.getDefaultBoolean(ShimPreferencesKey.CS_CHECK_SRC));
		parameter.setSharedRegisterDataSize(store
				.getDefaultInt(ShimPreferencesKey.CS_SRC_DATA_SIZE));
		parameter.setSharedRegisterSizeUnit(store
				.getDefaultInt(ShimPreferencesKey.CS_SRC_DATA_SIZE_UNIT));
		parameter.setNumRegister(store
				.getDefaultInt(ShimPreferencesKey.CS_SRC_NUMBER_REGISTER));

		parameter.setDontConnect(store
				.getDefaultBoolean(ShimPreferencesKey.CS_DONT_CONNECT));

		// set to default PreferencedStore
		store.setToDefault(ShimPreferencesKey.CS_CHECK_INTERRUPT);

		store.setToDefault(ShimPreferencesKey.CS_CHECK_EVENT);

		store.setToDefault(ShimPreferencesKey.CS_CHECK_FIFO);
		store.setToDefault(ShimPreferencesKey.CS_FIFO_DATA_SIZE);
		store.setToDefault(ShimPreferencesKey.CS_FIFO_DATA_SIZE_UNIT);
		store.setToDefault(ShimPreferencesKey.CS_FIFO_QUEUE_SIZE);

		store.setToDefault(ShimPreferencesKey.CS_CHECK_SMC);
		store.setToDefault(ShimPreferencesKey.CS_SMC_DATA_SIZE);
		store.setToDefault(ShimPreferencesKey.CS_SMC_DATA_SIZE_UNIT);
		store.setToDefault(ShimPreferencesKey.CS_SMC_OPERATION);

		store.setToDefault(ShimPreferencesKey.CS_CHECK_SRC);
		store.setToDefault(ShimPreferencesKey.CS_SRC_DATA_SIZE);
		store.setToDefault(ShimPreferencesKey.CS_SRC_DATA_SIZE_UNIT);
		store.setToDefault(ShimPreferencesKey.CS_SRC_NUMBER_REGISTER);

		store.setToDefault(ShimPreferencesKey.CS_DONT_CONNECT);

		store.save();

		ShimPreferences.getCurrentInstance().loadCommunicationSetPreferences();

		// reflect bean to form
		m_bindingContext.updateTargets();
		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	@Override
	protected void performApply() {
		m_bindingContext.updateModels();

		store.setCommunicationSetPreferences(parameter);
		store.save();

		ShimPreferences.getCurrentInstance().loadCommunicationSetPreferences();

		super.performApply();
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeSelectionBtn_InterruptObserveWidget = WidgetProperties
				.selection().observe(btnInterrupt);
		IObservableValue checkInterruptCommunicationParameterObserveValue = PojoProperties
				.value("checkInterruptCommunication").observe(parameter);
		bindingContext.bindValue(observeSelectionBtn_InterruptObserveWidget,
				checkInterruptCommunicationParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtn_EventObserveWidget = WidgetProperties
				.selection().observe(btnEvent);
		IObservableValue checkEventCommunicationParameterObserveValue = PojoProperties
				.value("checkEventCommunication").observe(parameter);
		bindingContext.bindValue(observeSelectionBtn_EventObserveWidget,
				checkEventCommunicationParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtn_FIFOObserveWidget = WidgetProperties
				.selection().observe(btnFIFO);
		IObservableValue checkFIFOCommunicationParameterObserveValue = PojoProperties
				.value("checkFIFOCommunication").observe(parameter);
		bindingContext.bindValue(observeSelectionBtn_FIFOObserveWidget,
				checkFIFOCommunicationParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_FiFoDataSizeObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textFiFoDataSize);
		IObservableValue fifoDataSizeParameterObserveValue = PojoProperties
				.value("fifoDataSize").observe(parameter);
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		strategy_2.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
		strategy_3.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_FiFoDataSizeObserveWidget,
				fifoDataSizeParameterObserveValue, strategy_2, strategy_3);
		//
		IObservableValue observeSingleSelectionIndexCombo_FIFO_DataSize_UnitObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboFIFODataSizeUnit);
		IObservableValue fifoSizeUnitParameterObserveValue = PojoProperties
				.value("fifoSizeUnit").observe(parameter);
		bindingContext
				.bindValue(
						observeSingleSelectionIndexCombo_FIFO_DataSize_UnitObserveWidget,
						fifoSizeUnitParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_QueueSizeObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textQueueSize);
		IObservableValue queueSizeParameterObserveValue = PojoProperties.value(
				"queueSize").observe(parameter);
		UpdateValueStrategy strategy_4 = new UpdateValueStrategy();
		strategy_4.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_5 = new UpdateValueStrategy();
		strategy_5.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_QueueSizeObserveWidget,
				queueSizeParameterObserveValue, strategy_4, strategy_5);
		//
		IObservableValue observeSelectionBtn_SharedMemoryObserveWidget = WidgetProperties
				.selection().observe(btnSharedMemory);
		IObservableValue checkSharedMemoryCommunicationParameterObserveValue = PojoProperties
				.value("checkSharedMemoryCommunication").observe(parameter);
		bindingContext
				.bindValue(observeSelectionBtn_SharedMemoryObserveWidget,
						checkSharedMemoryCommunicationParameterObserveValue,
						null, null);
		//
		IObservableValue observeTextText_SharedMemory_DataSizeObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textSharedMemoryDataSize);
		IObservableValue sharedMemoryDataSizeParameterObserveValue = PojoProperties
				.value("sharedMemoryDataSize").observe(parameter);
		UpdateValueStrategy strategy_6 = new UpdateValueStrategy();
		strategy_6.setConverter(new StringToIntegerConverter());
		UpdateValueStrategy strategy_7 = new UpdateValueStrategy();
		strategy_7.setConverter(new IntToStringConverter());
		bindingContext.bindValue(
				observeTextText_SharedMemory_DataSizeObserveWidget,
				sharedMemoryDataSizeParameterObserveValue, strategy_6,
				strategy_7);
		//
		IObservableValue observeSingleSelectionIndexCombo_SharedMemory_DataSize_UnitObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboSharedMemoryDataSizeUnit);
		IObservableValue sharedMemorySizeUnitParameterObserveValue = PojoProperties
				.value("sharedMemorySizeUnit").observe(parameter);
		bindingContext
				.bindValue(
						observeSingleSelectionIndexCombo_SharedMemory_DataSize_UnitObserveWidget,
						sharedMemorySizeUnitParameterObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionIndexCombo_OpTypeObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboOpType);
		IObservableValue operationTypeParameterObserveValue = PojoProperties
				.value("operationType").observe(parameter);
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_OpTypeObserveWidget,
				operationTypeParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtn_SharedRegisterObserveWidget = WidgetProperties
				.selection().observe(btnSharedRegister);
		IObservableValue checkSharedRegisterCommunicationParameterObserveValue = PojoProperties
				.value("checkSharedRegisterCommunication").observe(parameter);
		bindingContext.bindValue(
				observeSelectionBtn_SharedRegisterObserveWidget,
				checkSharedRegisterCommunicationParameterObserveValue, null,
				null);
		//
		IObservableValue observeTextText_SR_DataSizeObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textSRDataSize);
		IObservableValue sharedRegisterDataSizeParameterObserveValue = PojoProperties
				.value("sharedRegisterDataSize").observe(parameter);
		UpdateValueStrategy strategy_8 = new UpdateValueStrategy();
		strategy_8.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_9 = new UpdateValueStrategy();
		strategy_9.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_SR_DataSizeObserveWidget,
				sharedRegisterDataSizeParameterObserveValue, strategy_8,
				strategy_9);
		//
		IObservableValue observeSingleSelectionIndexCombo_SR_DataSize_UnitObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboSRDataSizeUnit);
		IObservableValue sharedRegisterSizeUnitParameterObserveValue = PojoProperties
				.value("sharedRegisterSizeUnit").observe(parameter);
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_SR_DataSize_UnitObserveWidget,
				sharedRegisterSizeUnitParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_nRegisterObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textNRegister);
		IObservableValue numRegisterParameterObserveValue = PojoProperties
				.value("numRegister").observe(parameter);
		UpdateValueStrategy strategy_10 = new UpdateValueStrategy();
		strategy_10.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_11 = new UpdateValueStrategy();
		strategy_11.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_nRegisterObserveWidget,
				numRegisterParameterObserveValue, strategy_10, strategy_11);
		//
		IObservableValue observeSelectionBtnDontConnectObserveWidget = WidgetProperties
				.selection().observe(btnDontConnect);
		IObservableValue dontConnectParameterObserveValue = PojoProperties
				.value("dontConnect").observe(parameter);
		bindingContext.bindValue(observeSelectionBtnDontConnectObserveWidget,
				dontConnectParameterObserveValue, null, null);
		//
		return bindingContext;
	}

	/**
	 * Changes the checkAllButton to be grayed or selected.
	 */
	private void updateCheckAllButtonStatus() {
		boolean sameAll = true;
		sameAll &= btnInterrupt.getSelection() == btnFIFO.getSelection();
		sameAll &= btnInterrupt.getSelection() == btnEvent.getSelection();
		sameAll &= btnInterrupt.getSelection() == btnSharedMemory
				.getSelection();
		sameAll &= btnInterrupt.getSelection() == btnSharedRegister
				.getSelection();

		if (sameAll) {
			btnCheckALL.setGrayed(false);
			btnCheckALL.setSelection(btnInterrupt.getSelection());
		} else {
			btnCheckALL.setSelection(true);
			btnCheckALL.setGrayed(true);
		}
	}
}
