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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.gui.common.LabelConstants;
import org.multicore_association.shim.edit.gui.databinding.FloatToStringConverter;
import org.multicore_association.shim.edit.gui.databinding.IntToStringConverter;
import org.multicore_association.shim.edit.gui.databinding.StringToFloatConverter;
import org.multicore_association.shim.edit.gui.databinding.StringToIntConverter;
import org.multicore_association.shim.edit.gui.jface.ErrorMessagePool;
import org.multicore_association.shim.edit.gui.swt.TextRequiredModifier;
import org.multicore_association.shim.edit.gui.swt.UnsignedFloatModifier;
import org.multicore_association.shim.edit.gui.swt.UnsignedIntegerModifier;
import org.multicore_association.shim.edit.model.ShimPreferences;
import org.multicore_association.shim.edit.model.preferences.ComponentsPreferences;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesKey;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesStore;

/**
 * Preference page that allows configuration of the Components.
 */
public class ComponentsPreferencePage extends PreferencePage {
	private DataBindingContext m_bindingContext;
	private Text txtSystemName;
	private Text txtNMasterComponent;
	private Text txtNSlaveComponent;
	private Text txtNComponentSet;
	private Text txtClock;
	
	private ComponentsPreferences parameter;

	private ShimPreferencesStore store;
	
	private ErrorMessagePool pool;
	
	/**
	 * Constructs a new instance of ComponentsPreferencePage.
	 */
	public ComponentsPreferencePage() {
		setTitle("ComponentSet General");
		store = ShimPreferencesStore.getInstance();
		
		parameter = new ComponentsPreferences();
		parameter.setSystemName(store.getString(ShimPreferencesKey.CP_SYSTEM_NAME));
		parameter.setNumberMaster(store.getInt(ShimPreferencesKey.CP_NUMBER_MASTER));
		parameter.setNumberSlave(store.getInt(ShimPreferencesKey.CP_NUMBER_SLAVE));
		parameter.setNumberComponent(store.getInt(ShimPreferencesKey.CP_NUMBER_COMPONENT));
		parameter.setClock(store.getFloat(ShimPreferencesKey.CP_CLOCK));
		
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
		
		Label lblSystem = new Label(container, SWT.NONE);
		lblSystem.setText(LabelConstants.SYSTEM_NAME);
		
		txtSystemName = new Text(container, SWT.BORDER);
		GridData gd_txtSystemName = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_txtSystemName.widthHint = 200;
		txtSystemName.setLayoutData(gd_txtSystemName);
		txtSystemName.addModifyListener(new TextRequiredModifier(pool,
				SystemConfiguration.class, "name", lblSystem.getText()));
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText(LabelConstants.NUMBER_OF_MASTERCOMPONENT);
		
		txtNMasterComponent = new Text(container, SWT.BORDER);
		GridData gd_txtNMasterComponent = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_txtNMasterComponent.widthHint = 120;
		txtNMasterComponent.setLayoutData(gd_txtNMasterComponent);
		txtNMasterComponent.addModifyListener(new UnsignedIntegerModifier(pool, lblNewLabel.getText(), false));
		
		Label lblNumberOfSlavecomponent = new Label(container, SWT.NONE);
		lblNumberOfSlavecomponent.setText(LabelConstants.NUMBER_OF_SLAVECOMPONENT);
		
		txtNSlaveComponent = new Text(container, SWT.BORDER);
		GridData gd_txtNSlaveComponent = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_txtNSlaveComponent.widthHint = 120;
		txtNSlaveComponent.setLayoutData(gd_txtNSlaveComponent);
		txtNSlaveComponent.addModifyListener(new UnsignedIntegerModifier(pool, lblNumberOfSlavecomponent.getText(), false));
		
		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setText(LabelConstants.NUMBER_OF_COMPONENTSET);
		
		txtNComponentSet = new Text(container, SWT.BORDER);
		GridData gd_txtNComponentSet = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_txtNComponentSet.widthHint = 120;
		txtNComponentSet.setLayoutData(gd_txtNComponentSet);
		txtNComponentSet.addModifyListener(new UnsignedIntegerModifier(pool, lblNewLabel_1.getText(), false));
		
		Label lblNewLabel_2 = new Label(container, SWT.NONE);
		lblNewLabel_2.setText(LabelConstants.CLOCK_FREQUENCY);
		
		Composite container_1 = new Composite(container, SWT.NULL);
		GridData gd_container_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_container_1.widthHint = 176;
		container_1.setLayoutData(gd_container_1);
		GridLayout gl_container_1 = new GridLayout(2, false);
		gl_container_1.verticalSpacing = 0;
		gl_container_1.marginWidth = 0;
		container_1.setLayout(gl_container_1);
		
		txtClock = new Text(container_1, SWT.BORDER);
		txtClock.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtClock.addModifyListener(new UnsignedFloatModifier(pool, lblNewLabel_2.getText(), false));
		
		Label lblNewLabel_3 = new Label(container_1, SWT.NONE);
		lblNewLabel_3.setText(LabelConstants.UNITSTR_HZ);
		
		m_bindingContext = initDataBindings();
		
		return container;
	}
	
	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		parameter.setSystemName(store.getDefaultString(ShimPreferencesKey.CP_SYSTEM_NAME));
		parameter.setNumberMaster(store.getDefaultInt(ShimPreferencesKey.CP_NUMBER_MASTER));
		parameter.setNumberSlave(store.getDefaultInt(ShimPreferencesKey.CP_NUMBER_SLAVE));
		parameter.setNumberComponent(store.getDefaultInt(ShimPreferencesKey.CP_NUMBER_COMPONENT));
		parameter.setClock(store.getDefaultFloat(ShimPreferencesKey.CP_CLOCK));
		
		store.setToDefault(ShimPreferencesKey.CP_SYSTEM_NAME);
		store.setToDefault(ShimPreferencesKey.CP_NUMBER_MASTER);
		store.setToDefault(ShimPreferencesKey.CP_NUMBER_SLAVE);
		store.setToDefault(ShimPreferencesKey.CP_NUMBER_COMPONENT);
		store.setToDefault(ShimPreferencesKey.CP_CLOCK);
		store.save();
		
		ShimPreferences.getCurrentInstance().loadComponentsPreferences();
		
		m_bindingContext.updateTargets();
		
		super.performDefaults();
	}
	
	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	@Override
	protected void performApply() {
		m_bindingContext.updateModels();
		
		store.setComponentsPreferences(parameter);
		store.save();
		
		ShimPreferences.getCurrentInstance().loadComponentsPreferences();
		
		super.performApply();
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
		IObservableValue observeTextTxtNewTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtSystemName);
		IObservableValue cpSystemNameParameterObserveValue = PojoProperties.value("systemName").observe(parameter);
		bindingContext.bindValue(observeTextTxtNewTextObserveWidget, cpSystemNameParameterObserveValue, null, null);
		//
		IObservableValue observeTextTxtNewText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtNMasterComponent);
		IObservableValue numberMasterParameterObserveValue = PojoProperties.value("numberMaster").observe(parameter);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextTxtNewText_1ObserveWidget, numberMasterParameterObserveValue, strategy, strategy_1);
		//
		IObservableValue observeTextTxtNewText_2ObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtNSlaveComponent);
		IObservableValue numberSlaveParameterObserveValue = PojoProperties.value("numberSlave").observe(parameter);
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		strategy_2.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
		strategy_3.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextTxtNewText_2ObserveWidget, numberSlaveParameterObserveValue, strategy_2, strategy_3);
		//
		IObservableValue observeTextTxtNewText_3ObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtNComponentSet);
		IObservableValue numberComponentParameterObserveValue = PojoProperties.value("numberComponent").observe(parameter);
		UpdateValueStrategy strategy_4 = new UpdateValueStrategy();
		strategy_4.setConverter(new StringToIntConverter());
		UpdateValueStrategy strategy_5 = new UpdateValueStrategy();
		strategy_5.setConverter(new IntToStringConverter());
		bindingContext.bindValue(observeTextTxtNewText_3ObserveWidget, numberComponentParameterObserveValue, strategy_4, strategy_5);
		//
		IObservableValue observeTextTxtNewText_3ObserveWidget_1 = WidgetProperties.text(SWT.Modify).observe(txtClock);
		IObservableValue clockParameterObserveValue = PojoProperties.value("clock").observe(parameter);
		UpdateValueStrategy strategy_6 = new UpdateValueStrategy();
		strategy_6.setConverter(new StringToFloatConverter());
		UpdateValueStrategy strategy_7 = new UpdateValueStrategy();
		strategy_7.setConverter(new FloatToStringConverter());
		bindingContext.bindValue(observeTextTxtNewText_3ObserveWidget_1, clockParameterObserveValue, strategy_6, strategy_7);
		//
		return bindingContext;
	}
}
