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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.RWType;
import org.multicore_association.shim.api.SizeUnitType;
import org.multicore_association.shim.api.SlaveComponent;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.gui.common.LabelConstants;
import org.multicore_association.shim.edit.gui.databinding.IntToStringConverter;
import org.multicore_association.shim.edit.gui.databinding.StringToIntConverter;
import org.multicore_association.shim.edit.gui.jface.ErrorMessagePool;
import org.multicore_association.shim.edit.gui.swt.TextRequiredModifier;
import org.multicore_association.shim.edit.gui.swt.UnsignedIntegerModifier;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimPreferences;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesKey;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesStore;
import org.multicore_association.shim.edit.model.preferences.SlaveComponentPreferences;

/**
 * Preference page that allows configuration of the SlaveComponent.
 */
public class SlaveComponentPreferencePage extends PreferencePage {
	private DataBindingContext m_bindingContext;
	private Text text;

	private Text txtSize;

	private Combo comboSize;

	private Combo comboRWType;

	private ShimPreferencesStore store;

	private SlaveComponentPreferences parameter;

	private ErrorMessagePool pool;

	/**
	 * Constructs a new instance of SlaveComponentPreferencePage.
	 */
	public SlaveComponentPreferencePage() {
		setTitle("SlaveComponent");

		store = ShimPreferencesStore.getInstance();

		parameter = new SlaveComponentPreferences();
		parameter
				.setBaseName(store.getString(ShimPreferencesKey.CP_SLAVE_NAME));
		parameter.setSize(store.getInt(ShimPreferencesKey.CP_SLAVE_SIZE));
		parameter.setSizeUnit(store
				.getInt(ShimPreferencesKey.CP_SLAVE_SIZE_UNIT));
		parameter.setRwType(store.getInt(ShimPreferencesKey.CP_SLAVE_RWTYPE));

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

		text = new Text(container, SWT.BORDER);
		GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 200;
		text.setLayoutData(gd_text);
		text.addModifyListener(new TextRequiredModifier(pool,
				SlaveComponent.class, "name", lblBaseName.getText()));

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText(LabelConstants.SIZE);

		Composite child = new Composite(container, SWT.NULL);
		GridData gd_child = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_child.widthHint = 197;
		child.setLayoutData(gd_child);
		GridLayout gl_child = new GridLayout(2, false);
		gl_child.verticalSpacing = 0;
		gl_child.marginWidth = 0;
		gl_child.marginHeight = 0;
		gl_child.horizontalSpacing = 0;
		child.setLayout(gl_child);

		txtSize = new Text(child, SWT.BORDER);
		GridData gd_txtSize = new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1);
		gd_txtSize.widthHint = 120;
		txtSize.setLayoutData(gd_txtSize);
		txtSize.addModifyListener(new TextRequiredModifier(pool,
				SlaveComponent.class, "size", lblBaseName.getText()));
		txtSize.addModifyListener(new UnsignedIntegerModifier(pool, lblNewLabel
				.getText(), false));

		comboSize = new ComboFactory(child).createCombo(SizeUnitType.class,
				ShimModelAdapter.isRequired(SlaveComponent.class, "sizeUnit"));
		comboSize.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1));

		Label lblRwtype = new Label(container, SWT.NONE);
		lblRwtype.setText(LabelConstants.RW_TYPE);

		comboRWType = new ComboFactory(container).createCombo(RWType.class,
				ShimModelAdapter.isRequired(SlaveComponent.class, "rwType"));
		comboRWType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1));
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
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(
				SWT.Modify).observe(text);
		IObservableValue baseNameParameterObserveValue = PojoProperties.value(
				"baseName").observe(parameter);
		bindingContext.bindValue(observeTextTextObserveWidget,
				baseNameParameterObserveValue, null, null);
		//
		IObservableValue observeTextTxtSizeObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(txtSize);
		IObservableValue sizeParameterObserveValue = PojoProperties.value(
				"size").observe(parameter);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextTxtSizeObserveWidget,
				sizeParameterObserveValue, strategy, strategy_1);
		//
		IObservableValue observeSingleSelectionIndexSizeComboObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboSize);
		IObservableValue sizeUnitParameterObserveValue = PojoProperties.value(
				"sizeUnit").observe(parameter);
		bindingContext.bindValue(
				observeSingleSelectionIndexSizeComboObserveWidget,
				sizeUnitParameterObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionIndexComboRWTypeObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(comboRWType);
		IObservableValue rwTypeParameterObserveValue = PojoProperties.value(
				"rwType").observe(parameter);
		bindingContext.bindValue(
				observeSingleSelectionIndexComboRWTypeObserveWidget,
				rwTypeParameterObserveValue, null, null);
		//
		return bindingContext;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	@Override
	protected void performApply() {
		m_bindingContext.updateModels();

		store.setSlaveComponentPreferences(parameter);
		store.save();

		ShimPreferences.getCurrentInstance().loadSlaveComponentPreferences();

		super.performApply();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		parameter.setBaseName(store
				.getDefaultString(ShimPreferencesKey.CP_SLAVE_NAME));
		parameter
				.setSize(store.getDefaultInt(ShimPreferencesKey.CP_SLAVE_SIZE));
		parameter.setSizeUnit(store
				.getDefaultInt(ShimPreferencesKey.CP_SLAVE_SIZE_UNIT));
		parameter.setRwType(store
				.getDefaultInt(ShimPreferencesKey.CP_SLAVE_RWTYPE));

		store.setToDefault(ShimPreferencesKey.CP_SLAVE_NAME);
		store.setToDefault(ShimPreferencesKey.CP_SLAVE_SIZE);
		store.setToDefault(ShimPreferencesKey.CP_SLAVE_SIZE_UNIT);
		store.setToDefault(ShimPreferencesKey.CP_SLAVE_RWTYPE);
		store.save();

		ShimPreferences.getCurrentInstance().loadSlaveComponentPreferences();

		m_bindingContext.updateTargets();

		super.performDefaults();
	}
}
