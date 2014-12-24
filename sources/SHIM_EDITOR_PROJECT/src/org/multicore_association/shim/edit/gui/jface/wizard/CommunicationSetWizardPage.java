/*
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.AddressSpace;
import org.multicore_association.shim.api.FIFOCommunication;
import org.multicore_association.shim.api.OperationType;
import org.multicore_association.shim.api.SharedMemoryCommunication;
import org.multicore_association.shim.api.SharedRegisterCommunication;
import org.multicore_association.shim.api.SizeUnitType;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.gui.common.LabelConstants;
import org.multicore_association.shim.edit.gui.common.RefComboFactory;
import org.multicore_association.shim.edit.gui.databinding.IntToStringConverter;
import org.multicore_association.shim.edit.gui.databinding.StringToIntConverter;
import org.multicore_association.shim.edit.gui.databinding.StringToIntegerConverter;
import org.multicore_association.shim.edit.gui.jface.ErrorMessagePool;
import org.multicore_association.shim.edit.gui.swt.TextRequiredModifier;
import org.multicore_association.shim.edit.gui.swt.UnsignedIntegerModifier;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimPreferences;

import swing2swt.layout.BorderLayout;

/**
 * Wizard page that allows configuration of the CommunicationSet.
 */
public class CommunicationSetWizardPage extends WizardPage {
	private DataBindingContext m_bindingContext;
	public Composite composite;
	public Button btnInterrupt;
	public Button btnFIFO;
	public Button btnEvent;
	public Button btnSharedMemory;
	public Button btnSharedRegister;
	public Label label;
	public Label label_1;
	public Button btnCheckALL;
	public Label lblDatasize;
	public Text textFiFoDataSize;
	public Label lblQueuesize;
	public Text textQueueSize;
	public Label lblDatasize_1;
	public Text textSharedMemoryDataSize;
	public Label lblAddressspace;
	public Label lblSubspace;
	public Label label_2;
	public Text textSRDataSize;
	public Combo comboSRDataSizeUnit;
	public Label lblNregister;
	public Text textNRegister;
	public Composite composite_2;
	public Composite composite_4;
	public Composite composite_6;
	public Composite composite_7;
	public Composite composite_3;
	public Combo comboAddressSpaceRef;
	public Combo comboSubSpaceRef;
	protected RefComboFactory refComboFactoryAs;
	protected RefComboFactory refComboFactorySs;
	public Button btnDontConnect;
	public Combo comboFIFODataSizeUnit;
	public Combo comboSharedMemoryDataSizeUnit;

	public Label lblOperationtype;
	public Combo comboOpType;

	private ShimPreferences settings;

	private ErrorMessagePool pool;

	private boolean asCreated = false;

	/**
	 * Constructs a new instance of CommunicationSetWizardPage.
	 */
	public CommunicationSetWizardPage() {
		super("wizardPage");
		setTitle("Setting Communication Set");
		setDescription("Choose Communication Set.");

		settings = ShimPreferences.getCurrentInstance();

		pool = new ErrorMessagePool(this);
	}

	/**
	 * Constructs a new instance of Shim_WizardPage_CommunicationSet.
	 */
	public CommunicationSetWizardPage(boolean asCreated) {
		super("wizardPage");
		setTitle("Setting Communication Set");
		setDescription("Choose Communication Set");

		settings = ShimPreferences.getCurrentInstance();

		pool = new ErrorMessagePool(this);

		this.asCreated = asCreated;
	}

	/**
	 * Create contents of this preference page.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new BorderLayout(0, 0));

		composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(1, false));

		btnCheckALL = new Button(composite, SWT.CHECK);
		btnCheckALL.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		btnCheckALL.setSelection(true);
		btnCheckALL.setText(LabelConstants.SELECT_DESELECT_ALL);

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

				btnInterrupt.setSelection(check);
				btnFIFO.setSelection(check);
				btnEvent.setSelection(check);
				btnSharedMemory.setSelection(check);
				btnSharedRegister.setSelection(check);

				m_bindingContext.updateModels();
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

		label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);

		composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayout(new GridLayout(3, false));

		btnInterrupt = new Button(composite_2, SWT.CHECK);
		GridData gd_btn_Interrupt = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_btn_Interrupt.widthHint = 199;
		btnInterrupt.setLayoutData(gd_btn_Interrupt);
		btnInterrupt.setText("InterruptCommunication");
		btnInterrupt.addSelectionListener(grayedCheckAllListner);

		composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setLayout(new GridLayout(1, false));

		btnEvent = new Button(composite_3, SWT.CHECK);
		btnEvent.setText("EventCommunication");
		btnEvent.addSelectionListener(grayedCheckAllListner);

		composite_4 = new Composite(composite, SWT.BORDER);
		GridData gd_composite_4 = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_composite_4.widthHint = 499;
		composite_4.setLayoutData(gd_composite_4);
		composite_4.setLayout(new GridLayout(6, false));

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

		comboFIFODataSizeUnit = new ComboFactory(composite_4).createCombo(
				SizeUnitType.class, ShimModelAdapter.isRequired(
						FIFOCommunication.class, "dataSizeUnit"));
		comboFIFODataSizeUnit.setSize(87, 23);

		lblQueuesize = new Label(composite_4, SWT.NONE);
		lblQueuesize.setText(LabelConstants.QUEUE_SIZE);

		textQueueSize = new Text(composite_4, SWT.BORDER);
		GridData gd_text_QueueSize = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_text_QueueSize.widthHint = 54;
		textQueueSize.setLayoutData(gd_text_QueueSize);
		textQueueSize.setSize(72, 21);
		textQueueSize.addModifyListener(new TextRequiredModifier(pool,
				FIFOCommunication.class, "queueSize", lblQueuesize.getText()));
		textQueueSize.addModifyListener(new UnsignedIntegerModifier(pool,
				lblQueuesize.getText(), false));

		composite_6 = new Composite(composite, SWT.BORDER);
		GridData gd_composite_6 = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_composite_6.widthHint = 668;
		composite_6.setLayoutData(gd_composite_6);
		composite_6.setLayout(new GridLayout(6, false));

		btnSharedMemory = new Button(composite_6, SWT.CHECK);
		GridData gd_btn_SharedMemory = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd_btn_SharedMemory.widthHint = 206;
		btnSharedMemory.setLayoutData(gd_btn_SharedMemory);
		btnSharedMemory.setText("SharedMemoryCommunication");
		btnSharedMemory.addSelectionListener(grayedCheckAllListner);

		lblDatasize_1 = new Label(composite_6, SWT.NONE);
		lblDatasize_1.setText(LabelConstants.DATA_SIZE);

		textSharedMemoryDataSize = new Text(composite_6, SWT.BORDER);
		GridData gd_text_SharedMemory_DataSize = new GridData(SWT.LEFT,
				SWT.CENTER, false, false, 1, 1);
		gd_text_SharedMemory_DataSize.widthHint = 48;
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
		GridData gd_combo_SharedMemory_DataSize_Unit = new GridData(SWT.FILL,
				SWT.CENTER, true, false, 1, 1);
		gd_combo_SharedMemory_DataSize_Unit.widthHint = 127;
		comboSharedMemoryDataSizeUnit
				.setLayoutData(gd_combo_SharedMemory_DataSize_Unit);

		lblAddressspace = new Label(composite_6, SWT.NONE);
		GridData gd_lblAddressspace = new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1);
		gd_lblAddressspace.widthHint = 105;
		lblAddressspace.setLayoutData(gd_lblAddressspace);
		lblAddressspace.setSize(81, 15);
		lblAddressspace.setText("AddressSpace");

		Class<?> llClas = new SharedMemoryCommunication().getClass();
		refComboFactoryAs = new RefComboFactory(composite_6, llClas,
				"addressSpaceRef", AddressSpace.class);
		comboAddressSpaceRef = refComboFactoryAs.createEmptyCombo();
		GridData gd_combo_AddressSpaceRef = new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1);
		gd_combo_AddressSpaceRef.widthHint = 224;
		comboAddressSpaceRef.setLayoutData(gd_combo_AddressSpaceRef);
		new Label(composite_6, SWT.NONE);

		lblOperationtype = new Label(composite_6, SWT.NONE);
		lblOperationtype.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblOperationtype.setText(LabelConstants.OPERATION_TYPE);

		comboOpType = new ComboFactory(composite_6).createCombo(
				OperationType.class, ShimModelAdapter.isRequired(
						SharedMemoryCommunication.class, "operationType"));
		comboOpType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		new Label(composite_6, SWT.NONE);

		lblSubspace = new Label(composite_6, SWT.NONE);
		GridData gd_lblSubspace = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_lblSubspace.widthHint = 87;
		lblSubspace.setLayoutData(gd_lblSubspace);
		lblSubspace.setSize(57, 15);
		lblSubspace.setText("SubSpace");

		refComboFactorySs = new RefComboFactory(composite_6, llClas,
				"subSpaceRef", SubSpace.class);
		comboSubSpaceRef = refComboFactorySs.createEmptyCombo();
		GridData gd_combo_subSpace = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		gd_combo_subSpace.widthHint = 173;
		comboSubSpaceRef.setLayoutData(gd_combo_subSpace);

		composite_7 = new Composite(composite, SWT.BORDER);
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
		textNRegister.addModifyListener(new TextRequiredModifier(pool,
				SharedRegisterCommunication.class, "nRegister", lblNregister
						.getText()));
		textNRegister.addModifyListener(new UnsignedIntegerModifier(pool,
				lblNregister.getText(), false));
		new Label(composite_7, SWT.NONE);

		// To Add Listener.
		refComboFactoryAs.setSelectionListener(refComboFactorySs);

		label_1 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);

		btnDontConnect = new Button(composite, SWT.CHECK);
		btnDontConnect
				.setText("Generate connections across multiple ComponentSet");

		m_bindingContext = initDataBindings();

		if (asCreated) {
			refComboFactoryAs.createCombo();
			refComboFactorySs.createCombo();
		}

		updateCheckAllButtonStatus();
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
		IObservableValue observeSelectionBtn_InterruptObserveWidget = WidgetProperties
				.selection().observe(btnInterrupt);
		IObservableValue checkInterruptCommunicationSettingsgetCsParameterObserveValue = PojoProperties
				.value("checkInterruptCommunication").observe(
						settings.getCommunicationSetPreferences());
		bindingContext.bindValue(observeSelectionBtn_InterruptObserveWidget,
				checkInterruptCommunicationSettingsgetCsParameterObserveValue,
				null, null);
		//
		IObservableValue observeSelectionBtn_EventObserveWidget = WidgetProperties
				.selection().observe(btnEvent);
		IObservableValue checkEventCommunicationSettingsgetCsParameterObserveValue = PojoProperties
				.value("checkEventCommunication").observe(
						settings.getCommunicationSetPreferences());
		bindingContext.bindValue(observeSelectionBtn_EventObserveWidget,
				checkEventCommunicationSettingsgetCsParameterObserveValue,
				null, null);
		//
		IObservableValue observeSelectionBtn_FIFOObserveWidget = WidgetProperties
				.selection().observe(btnFIFO);
		IObservableValue checkFIFOCommunicationSettingsgetCsParameterObserveValue = PojoProperties
				.value("checkFIFOCommunication").observe(
						settings.getCommunicationSetPreferences());
		bindingContext.bindValue(observeSelectionBtn_FIFOObserveWidget,
				checkFIFOCommunicationSettingsgetCsParameterObserveValue, null,
				null);
		//
		IObservableValue observeTextText_FiFoDataSizeObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textFiFoDataSize);
		IObservableValue fifoDataSizeSettingsgetCsParameterObserveValue = PojoProperties
				.value("fifoDataSize").observe(
						settings.getCommunicationSetPreferences());
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		strategy_2.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
		strategy_3.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_FiFoDataSizeObserveWidget,
				fifoDataSizeSettingsgetCsParameterObserveValue, strategy_2,
				strategy_3);
		//
		IObservableValue observeSingleSelectionIndexCombo_FIFO_DataSize_UnitObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboFIFODataSizeUnit);
		IObservableValue fifoSizeUnitSettingsgetCsParameterObserveValue = PojoProperties
				.value("fifoSizeUnit").observe(
						settings.getCommunicationSetPreferences());
		bindingContext
				.bindValue(
						observeSingleSelectionIndexCombo_FIFO_DataSize_UnitObserveWidget,
						fifoSizeUnitSettingsgetCsParameterObserveValue, null,
						null);
		//
		IObservableValue observeTextText_QueueSizeObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textQueueSize);
		IObservableValue queueSizeSettingsgetCsParameterObserveValue = PojoProperties
				.value("queueSize").observe(
						settings.getCommunicationSetPreferences());
		UpdateValueStrategy strategy_4 = new UpdateValueStrategy();
		strategy_4.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_5 = new UpdateValueStrategy();
		strategy_5.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_QueueSizeObserveWidget,
				queueSizeSettingsgetCsParameterObserveValue, strategy_4,
				strategy_5);
		//
		IObservableValue observeSelectionBtn_SharedMemoryObserveWidget = WidgetProperties
				.selection().observe(btnSharedMemory);
		IObservableValue checkSharedMemoryCommunicationSettingsgetCsParameterObserveValue = PojoProperties
				.value("checkSharedMemoryCommunication").observe(
						settings.getCommunicationSetPreferences());
		bindingContext
				.bindValue(
						observeSelectionBtn_SharedMemoryObserveWidget,
						checkSharedMemoryCommunicationSettingsgetCsParameterObserveValue,
						null, null);
		//
		IObservableValue observeTextText_SharedMemory_DataSizeObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textSharedMemoryDataSize);
		IObservableValue sharedMemoryDataSizeSettingsgetCsParameterObserveValue = PojoProperties
				.value("sharedMemoryDataSize").observe(
						settings.getCommunicationSetPreferences());
		UpdateValueStrategy strategy_6 = new UpdateValueStrategy();
		strategy_6.setConverter(new StringToIntegerConverter());
		UpdateValueStrategy strategy_7 = new UpdateValueStrategy();
		strategy_7.setConverter(new IntToStringConverter());
		bindingContext.bindValue(
				observeTextText_SharedMemory_DataSizeObserveWidget,
				sharedMemoryDataSizeSettingsgetCsParameterObserveValue,
				strategy_6, strategy_7);
		//
		IObservableValue observeSingleSelectionIndexCombo_SharedMemory_DataSize_UnitObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboSharedMemoryDataSizeUnit);
		IObservableValue sharedMemorySizeUnitSettingsgetCsParameterObserveValue = PojoProperties
				.value("sharedMemorySizeUnit").observe(
						settings.getCommunicationSetPreferences());
		bindingContext
				.bindValue(
						observeSingleSelectionIndexCombo_SharedMemory_DataSize_UnitObserveWidget,
						sharedMemorySizeUnitSettingsgetCsParameterObserveValue,
						null, null);
		//
		IObservableValue observeSingleSelectionIndexCombo_OpTypeObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboOpType);
		IObservableValue operationTypeSettingsgetCsParameterObserveValue = PojoProperties
				.value("operationType").observe(
						settings.getCommunicationSetPreferences());
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_OpTypeObserveWidget,
				operationTypeSettingsgetCsParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtn_SharedRegisterObserveWidget = WidgetProperties
				.selection().observe(btnSharedRegister);
		IObservableValue checkSharedRegisterCommunicationSettingsgetCsParameterObserveValue = PojoProperties
				.value("checkSharedRegisterCommunication").observe(
						settings.getCommunicationSetPreferences());
		bindingContext
				.bindValue(
						observeSelectionBtn_SharedRegisterObserveWidget,
						checkSharedRegisterCommunicationSettingsgetCsParameterObserveValue,
						null, null);
		//
		IObservableValue observeTextText_SR_DataSizeObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textSRDataSize);
		IObservableValue sharedRegisterDataSizeSettingsgetCsParameterObserveValue = PojoProperties
				.value("sharedRegisterDataSize").observe(
						settings.getCommunicationSetPreferences());
		UpdateValueStrategy strategy_8 = new UpdateValueStrategy();
		strategy_8.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_9 = new UpdateValueStrategy();
		strategy_9.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_SR_DataSizeObserveWidget,
				sharedRegisterDataSizeSettingsgetCsParameterObserveValue,
				strategy_8, strategy_9);
		//
		IObservableValue observeSingleSelectionIndexCombo_SR_DataSize_UnitObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboSRDataSizeUnit);
		IObservableValue sharedRegisterSizeUnitSettingsgetCsParameterObserveValue = PojoProperties
				.value("sharedRegisterSizeUnit").observe(
						settings.getCommunicationSetPreferences());
		bindingContext.bindValue(
				observeSingleSelectionIndexCombo_SR_DataSize_UnitObserveWidget,
				sharedRegisterSizeUnitSettingsgetCsParameterObserveValue, null,
				null);
		//
		IObservableValue observeTextText_nRegisterObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textNRegister);
		IObservableValue numRegisterSettingsgetCsParameterObserveValue = PojoProperties
				.value("numRegister").observe(
						settings.getCommunicationSetPreferences());
		UpdateValueStrategy strategy_10 = new UpdateValueStrategy();
		strategy_10.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_11 = new UpdateValueStrategy();
		strategy_11.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_nRegisterObserveWidget,
				numRegisterSettingsgetCsParameterObserveValue, strategy_10,
				strategy_11);
		//
		IObservableValue observeSelectionBtnDontConnectObserveWidget = WidgetProperties
				.selection().observe(btnDontConnect);
		IObservableValue dontConnectSettingsgetCsParameterObserveValue = PojoProperties
				.value("dontConnect").observe(
						settings.getCommunicationSetPreferences());
		bindingContext.bindValue(observeSelectionBtnDontConnectObserveWidget,
				dontConnectSettingsgetCsParameterObserveValue, null, null);
		//
		return bindingContext;
	}

	/**
	 * Updates combo boxes.
	 */
	public void updateCombo() {
		refComboFactoryAs.createCombo();
		refComboFactoryAs.select(0);
		AddressSpace as = (AddressSpace) refComboFactoryAs.getRefObject();
		if (as != null) {
			refComboFactorySs.createCombo(as);
			refComboFactorySs.select(0);
		}
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
