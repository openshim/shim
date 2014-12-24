/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface.wizard;

import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.AddressSpace;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.gui.common.LabelConstants;
import org.multicore_association.shim.edit.gui.databinding.IntToStringConverter;
import org.multicore_association.shim.edit.gui.databinding.StringToIntConverter;
import org.multicore_association.shim.edit.gui.jface.ErrorMessagePool;
import org.multicore_association.shim.edit.gui.swt.TextRequiredModifier;
import org.multicore_association.shim.edit.gui.swt.UnsignedIntegerModifier;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.ShimModelManager;
import org.multicore_association.shim.edit.model.ShimPreferences;

import swing2swt.layout.BorderLayout;

/**
 * Wizard page that allows configuration of the AddressSpace parameter.
 */
public class AddressSpaceParameterWizardPage extends WizardPage {

	private static final Logger log = ShimLoggerUtil
			.getLogger(AddressSpaceParameterWizardPage.class);

	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;

	public List<AddressSpace> addressSpaceList = null;

	public Composite composite;
	public Label lblNumberOfAddressSpace;
	public Label lblNumberOfSubSpaces;
	public Label lblSizeOfSubSpace;
	public Text textNumberOfAddressSpace;
	public Text textNumberOfSubSpace;
	public Text textSizeOfSubspace;
	public Label lblDefaultSlaveComponent;
	public Combo comboDefaultSlaveComponent;
	private ComboViewer comboViewer;
	public Label lblAddressspaceBaseName;
	public Label lblSubspaceBaseName;
	public Text textASBaseName;
	public Text textSSBaseName;
	public Composite composite_1;
	public Label lblbyte;
	public Label lblOnlyEqual;
	public Label lblCombinationnumber;
	public Button btnDontConnect;

	private ShimPreferences settings;

	private ErrorMessagePool pool;

	/**
	 * Constructs a new instance of AddressSpaceParameterWizardPage.
	 */
	public AddressSpaceParameterWizardPage() {
		super("wizardPage");
		setTitle("Setting AddressSpace Information");
		setDescription("Input some number of Element for the creation AddressSpace and SubSpace.");

		settings = ShimPreferences.getCurrentInstance();

		pool = new ErrorMessagePool(this);
	}

	/**
	 * Creates contents of this page.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	public void createControl(Composite parent) {

		log.finest("In ShimWizardPage_AddressSpaceCreate::createControl()--");
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new BorderLayout(0, 0));

		composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(BorderLayout.NORTH);
		composite.setLayout(new GridLayout(3, false));

		lblNumberOfAddressSpace = new Label(composite, SWT.NONE);
		lblNumberOfAddressSpace
				.setText(LabelConstants.NUMBER_OF_ADDRESSSPACES);
		new Label(composite, SWT.NONE);

		textNumberOfAddressSpace = new Text(composite, SWT.BORDER);
		textNumberOfAddressSpace.setEditable(false);
		textNumberOfAddressSpace.setText("1");
		textNumberOfAddressSpace.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		lblOnlyEqual = new Label(composite, SWT.NONE);
		lblOnlyEqual
				.setText("* only equal number to ComponentSet [root(1) + child(your input)] is Supported");

		lblNumberOfSubSpaces = new Label(composite, SWT.NONE);
		lblNumberOfSubSpaces.setText(LabelConstants.NUMBER_OF_SUBSPACES);
		new Label(composite, SWT.NONE);

		textNumberOfSubSpace = new Text(composite, SWT.BORDER);
		textNumberOfSubSpace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		textNumberOfSubSpace.addModifyListener(new UnsignedIntegerModifier(
				pool, lblNumberOfSubSpaces.getText(), false));

		lblSizeOfSubSpace = new Label(composite, SWT.NONE);
		lblSizeOfSubSpace.setText(LabelConstants.SIZE_OF_SUBSPACE);
		new Label(composite, SWT.NONE);

		composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));

		textSizeOfSubspace = new Text(composite_1, SWT.BORDER);
		GridData gd_text_SizeOfSubspace = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd_text_SizeOfSubspace.widthHint = 53;
		textSizeOfSubspace.setLayoutData(gd_text_SizeOfSubspace);
		textSizeOfSubspace.addModifyListener(new UnsignedIntegerModifier(pool,
				lblSizeOfSubSpace.getText(), false));

		lblbyte = new Label(composite_1, SWT.NONE);
		lblbyte.setText(LabelConstants.UNITSTR_BYTE);

		lblDefaultSlaveComponent = new Label(composite, SWT.NONE);
		lblDefaultSlaveComponent.setText("Target SlaveComponent");
		new Label(composite, SWT.NONE);

		comboViewer = new ComboViewer(composite, SWT.NONE);
		comboDefaultSlaveComponent = comboViewer.getCombo();
		comboDefaultSlaveComponent.setItems(new String[] { "Slave1", "Slave2",
				"Slave3" });
		comboDefaultSlaveComponent.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, true, false, 1, 1));

		lblAddressspaceBaseName = new Label(composite, SWT.NONE);
		lblAddressspaceBaseName.setText(LabelConstants.ADDRESSSPACE_BASE_NAME);
		new Label(composite, SWT.NONE);

		textASBaseName = new Text(composite, SWT.BORDER);
		textASBaseName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		textASBaseName.addModifyListener(new TextRequiredModifier(pool,
				AddressSpace.class, "name", lblAddressspaceBaseName.getText()));

		lblSubspaceBaseName = new Label(composite, SWT.NONE);
		lblSubspaceBaseName.setText(LabelConstants.SUBSPACE_BASE_NAME);
		new Label(composite, SWT.NONE);

		textSSBaseName = new Text(composite, SWT.BORDER);
		textSSBaseName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		textSSBaseName.addModifyListener(new TextRequiredModifier(pool,
				SubSpace.class, "name", lblSubspaceBaseName.getText()));

		lblCombinationnumber = new Label(composite, SWT.NONE);
		lblCombinationnumber.setText("CombinationNumber Restriction");
		new Label(composite, SWT.NONE);

		btnDontConnect = new Button(composite, SWT.CHECK);
		btnDontConnect.setText("Generate accesses to SlaveComponent within other ComponentSet");
		m_bindingContext = initDataBindings();
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
		IObservableValue observeTextText_NumberOfSubSpaceObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textNumberOfSubSpace);
		IObservableValue numberSubSpaceSettingsgetAsParameterObserveValue = PojoProperties
				.value("numberSubSpace").observe(
						settings.getAddressSpacePreferences());
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_NumberOfSubSpaceObserveWidget,
				numberSubSpaceSettingsgetAsParameterObserveValue, strategy,
				strategy_1);
		//
		IObservableValue observeTextText_SizeOfSubspaceObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textSizeOfSubspace);
		IObservableValue sizeSubSpaceSettingsgetAsParameterObserveValue = PojoProperties
				.value("sizeSubSpace").observe(
						settings.getAddressSpacePreferences());
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		strategy_2.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
		strategy_3.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextText_SizeOfSubspaceObserveWidget,
				sizeSubSpaceSettingsgetAsParameterObserveValue, strategy_2,
				strategy_3);
		//
		IObservableValue observeTextText_AS_BaseNameObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textASBaseName);
		IObservableValue addressSpaceNameSettingsgetAsParameterObserveValue = PojoProperties
				.value("addressSpaceName").observe(
						settings.getAddressSpacePreferences());
		bindingContext.bindValue(observeTextText_AS_BaseNameObserveWidget,
				addressSpaceNameSettingsgetAsParameterObserveValue, null, null);
		//
		IObservableValue observeTextText_SS_BaseNameObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textSSBaseName);
		IObservableValue subSpaceNameSettingsgetAsParameterObserveValue = PojoProperties
				.value("subSpaceName").observe(
						settings.getAddressSpacePreferences());
		bindingContext.bindValue(observeTextText_SS_BaseNameObserveWidget,
				subSpaceNameSettingsgetAsParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnDontConnectObserveWidget = WidgetProperties
				.selection().observe(btnDontConnect);
		IObservableValue dontConnectSettingsgetAsParameterObserveValue = PojoProperties
				.value("dontConnect").observe(
						settings.getAddressSpacePreferences());
		bindingContext.bindValue(observeSelectionBtnDontConnectObserveWidget,
				dontConnectSettingsgetAsParameterObserveValue, null, null);
		//
		return bindingContext;
	}

	/**
	 * Updates controls on this page.
	 */
	public void update() {
		log.finest("In ShimWizardPage_TreeEdit::getNextPage()----------------");

		SystemConfiguration sys = ShimModelManager.getCurrentScd();

		int numberOfComponentSet = 1 + ShimModelManager.countComponentSet(sys
				.getComponentSet().getComponentSet());
		textNumberOfAddressSpace.setText(numberOfComponentSet + "");
		log.finest("count = " + numberOfComponentSet);

		List<String> slave_list = ShimModelManager
				.makeSlaveComponentNameList(sys);
		if (slave_list.size() > 0) {
			comboDefaultSlaveComponent.setItems(slave_list
					.toArray(new String[slave_list.size()]));
			comboDefaultSlaveComponent.setText(slave_list.toArray()[0]
					.toString());
		}
	}
}
