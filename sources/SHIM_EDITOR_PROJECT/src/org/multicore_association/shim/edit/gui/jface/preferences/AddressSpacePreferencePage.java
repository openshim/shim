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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.AddressSpace;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.edit.gui.common.LabelConstants;
import org.multicore_association.shim.edit.gui.databinding.IntToStringConverter;
import org.multicore_association.shim.edit.gui.databinding.StringToIntConverter;
import org.multicore_association.shim.edit.gui.jface.ErrorMessagePool;
import org.multicore_association.shim.edit.gui.swt.TextRequiredModifier;
import org.multicore_association.shim.edit.gui.swt.UnsignedIntegerModifier;
import org.multicore_association.shim.edit.model.ShimPreferences;
import org.multicore_association.shim.edit.model.preferences.AddressSpacePreferences;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesKey;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesStore;

/**
 * Preference page that allows configuration of the AddressSpace.
 */
public class AddressSpacePreferencePage extends PreferencePage {
	private DataBindingContext m_bindingContext;

	private ShimPreferencesStore store;
	private AddressSpacePreferences parameter;
	private Text textNSubSpaces;
	private Text textSize;
	private Text textAsBaseName;
	private Text textSsBaseName;
	private Button btnCheckButton;

	private ErrorMessagePool pool;

	/**
	 * Constructs a new instance of AddressSpacePreferencePage.
	 */
	public AddressSpacePreferencePage() {
		setTitle("AddressSpace");

		store = ShimPreferencesStore.getInstance();
		parameter = new AddressSpacePreferences();
		parameter.setNumberSubSpace(store
				.getInt(ShimPreferencesKey.AS_NUMBER_SUBSPACE));
		parameter.setSizeSubSpace(store
				.getInt(ShimPreferencesKey.AS_SIZE_SUBSPACE));
		parameter.setAddressSpaceName(store
				.getString(ShimPreferencesKey.AS_NAME));
		parameter.setSubSpaceName(store
				.getString(ShimPreferencesKey.AS_SUBSPACE_NAME));
		parameter.setDontConnect(store
				.getBoolean(ShimPreferencesKey.AS_CHECK_DONT_CONNECT));

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

		Label lblNumberOfSubspaces = new Label(container, SWT.NONE);
		lblNumberOfSubspaces.setText(LabelConstants.NUMBER_OF_SUBSPACES);

		textNSubSpaces = new Text(container, SWT.BORDER);
		GridData gd_textNSubSpaces = new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1);
		gd_textNSubSpaces.widthHint = 120;
		textNSubSpaces.setLayoutData(gd_textNSubSpaces);
		textNSubSpaces.addModifyListener(new UnsignedIntegerModifier(pool,
				lblNumberOfSubspaces.getText(), false));

		Label lblSize = new Label(container, SWT.NONE);
		lblSize.setText(LabelConstants.SIZE_OF_SUBSPACE);

		Composite composite = new Composite(container, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);

		textSize = new Text(composite, SWT.BORDER);
		GridData gd_textSize = new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1);
		gd_textSize.widthHint = 120;
		textSize.setLayoutData(gd_textSize);
		textSize.addModifyListener(new UnsignedIntegerModifier(pool, lblSize
				.getText(), false));

		Label lblbyte = new Label(composite, SWT.NONE);
		lblbyte.setText(LabelConstants.UNITSTR_BYTE);

		Label lblAddressspaceBaseName = new Label(container, SWT.NONE);
		lblAddressspaceBaseName.setText(LabelConstants.ADDRESSSPACE_BASE_NAME);

		textAsBaseName = new Text(container, SWT.BORDER);
		GridData gd_textAsBaseName = new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1);
		gd_textAsBaseName.widthHint = 200;
		textAsBaseName.setLayoutData(gd_textAsBaseName);
		textAsBaseName.addModifyListener(new TextRequiredModifier(pool,
				AddressSpace.class, "name", lblAddressspaceBaseName.getText()));

		Label lblSubspaceBaseName = new Label(container, SWT.NONE);
		lblSubspaceBaseName.setText(LabelConstants.SUBSPACE_BASE_NAME);

		textSsBaseName = new Text(container, SWT.BORDER);
		GridData gd_textSsBaseName = new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1);
		gd_textSsBaseName.widthHint = 200;
		textSsBaseName.setLayoutData(gd_textSsBaseName);
		textSsBaseName.addModifyListener(new TextRequiredModifier(pool,
				SubSpace.class, "name", lblSubspaceBaseName.getText()));

		Label lblCombinationnumberRestruction = new Label(container, SWT.NONE);
		lblCombinationnumberRestruction
				.setText("CombinationNumber Restruction");

		btnCheckButton = new Button(container, SWT.CHECK);
		btnCheckButton.setText("don't connect the relation of ancestor");
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
		IObservableValue observeTextTextNSubSpacesObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textNSubSpaces);
		IObservableValue numberSubSpaceParameterObserveValue = PojoProperties
				.value("numberSubSpace").observe(parameter);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextTextNSubSpacesObserveWidget,
				numberSubSpaceParameterObserveValue, strategy, strategy_1);
		//
		IObservableValue observeTextTextSizeObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textSize);
		IObservableValue sizeSubSpaceParameterObserveValue = PojoProperties
				.value("sizeSubSpace").observe(parameter);
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		strategy_2.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
		strategy_3.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextTextSizeObserveWidget,
				sizeSubSpaceParameterObserveValue, strategy_2, strategy_3);
		//
		IObservableValue observeTextTextAsBaseNameObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textAsBaseName);
		IObservableValue addressSpaceNameParameterObserveValue = PojoProperties
				.value("addressSpaceName").observe(parameter);
		bindingContext.bindValue(observeTextTextAsBaseNameObserveWidget,
				addressSpaceNameParameterObserveValue, null, null);
		//
		IObservableValue observeTextTextSsBaseNameObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(textSsBaseName);
		IObservableValue subSpaceNameParameterObserveValue = PojoProperties
				.value("subSpaceName").observe(parameter);
		bindingContext.bindValue(observeTextTextSsBaseNameObserveWidget,
				subSpaceNameParameterObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnCheckButtonObserveWidget = WidgetProperties
				.selection().observe(btnCheckButton);
		IObservableValue dontConnectParameterObserveValue = PojoProperties
				.value("dontConnect").observe(parameter);
		bindingContext.bindValue(observeSelectionBtnCheckButtonObserveWidget,
				dontConnectParameterObserveValue, null, null);
		//
		return bindingContext;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		parameter.setNumberSubSpace(store
				.getDefaultInt(ShimPreferencesKey.AS_NUMBER_SUBSPACE));
		parameter.setSizeSubSpace(store
				.getDefaultInt(ShimPreferencesKey.AS_SIZE_SUBSPACE));
		parameter.setAddressSpaceName(store
				.getDefaultString(ShimPreferencesKey.AS_NAME));
		parameter.setSubSpaceName(store
				.getDefaultString(ShimPreferencesKey.AS_SUBSPACE_NAME));
		parameter.setDontConnect(store
				.getDefaultBoolean(ShimPreferencesKey.AS_CHECK_DONT_CONNECT));

		store.setToDefault(ShimPreferencesKey.AS_NUMBER_SUBSPACE);
		store.setToDefault(ShimPreferencesKey.AS_SIZE_SUBSPACE);
		store.setToDefault(ShimPreferencesKey.AS_NAME);
		store.setToDefault(ShimPreferencesKey.AS_SUBSPACE_NAME);
		store.setToDefault(ShimPreferencesKey.AS_CHECK_DONT_CONNECT);
		store.save();

		ShimPreferences.getCurrentInstance()
				.loadAddressSpacePreferences();

		m_bindingContext.updateTargets();

		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	@Override
	protected void performApply() {
		m_bindingContext.updateModels();

		store.setAddressSpacePreferences(parameter);
		store.save();

		ShimPreferences.getCurrentInstance()
				.loadAddressSpacePreferences();
		super.performApply();
	}
}
