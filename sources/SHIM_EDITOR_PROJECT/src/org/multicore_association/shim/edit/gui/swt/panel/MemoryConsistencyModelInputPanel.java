/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.multicore_association.shim.api.MemoryConsistencyModel;
import org.multicore_association.shim.api.OrderingType;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.EnumUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for MemoryConsistencyModel.
 */
public class MemoryConsistencyModelInputPanel extends InputPanelBase {
	
	private static final Logger log = ShimLoggerUtil
			.getLogger(MemoryConsistencyModelInputPanel.class);

	private MemoryConsistencyModel memoryConsistencyModel = new MemoryConsistencyModel();
	
	private Combo comboRaw = null;
	private Combo comboWar = null;
	private Combo comboWaw = null;
	private Combo comboRar = null;
	
	/**
	 * Constructs a new instance of MemoryConsistencyModelInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public MemoryConsistencyModelInputPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));
		
		setLblTitleText("Memory Consistency Model");
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		
		Label lbl_rawOrdering = new Label(composite, SWT.NONE);
		lbl_rawOrdering.setText("Read after Write Ordering");
		new Label(composite, SWT.NONE);
		
		comboRaw = new ComboFactory(composite).createCombo(OrderingType.class,
				ShimModelAdapter.isRequired(getApiClass(), "rawOrdering"));
		comboRaw.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lbl_To = new Label(composite, SWT.NONE);
		lbl_To.setText("Write after Read Ordering");
		new Label(composite, SWT.NONE);
		
		comboWar = new ComboFactory(composite).createCombo(OrderingType.class,
				ShimModelAdapter.isRequired(getApiClass(), "warOrdering"));
		comboWar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setText("Write after Write Ordering");
		new Label(composite, SWT.NONE);
		
		comboWaw = new ComboFactory(composite).createCombo(OrderingType.class,
				ShimModelAdapter.isRequired(getApiClass(), "wawOrdering"));
		comboWaw.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setText("Read after Read Ordering");
		new Label(composite, SWT.NONE);
		
		comboRar = new ComboFactory(composite).createCombo(OrderingType.class,
				ShimModelAdapter.isRequired(getApiClass(), "rarOrdering"));
		comboRar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		memoryConsistencyModel = (MemoryConsistencyModel)input;
		
		OrderingType rawOrdering = memoryConsistencyModel.getRawOrdering();
		if (rawOrdering == null) {
			comboRaw.select(comboRaw.getSelectionIndex() - 1);
		} else {
			comboRaw.select(rawOrdering.ordinal());
		}
		
		OrderingType warOrdering = memoryConsistencyModel.getWarOrdering();
		if (warOrdering == null) {
			comboWar.select(comboWar.getSelectionIndex() - 1);
		} else {
			comboWar.select(warOrdering.ordinal());
		}
		
		OrderingType wawOrdering = memoryConsistencyModel.getWawOrdering();
		if (wawOrdering == null) {
			comboWaw.select(comboWaw.getSelectionIndex() - 1);
		} else {
			comboWaw.select(wawOrdering.ordinal());
		}
		
		OrderingType rarOrdering = memoryConsistencyModel.getRarOrdering();
		if (rarOrdering == null) {
			comboRar.select(comboRar.getSelectionIndex() - 1);
		} else {
			comboRar.select(rarOrdering.ordinal());
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {	
		assert(memoryConsistencyModel  != null);
		
		memoryConsistencyModel.setRawOrdering(EnumUtil.getOrderingType(comboRaw.getSelectionIndex()));
		memoryConsistencyModel.setWarOrdering(EnumUtil.getOrderingType(comboWar.getSelectionIndex()));
		memoryConsistencyModel.setWawOrdering(EnumUtil.getOrderingType(comboWaw.getSelectionIndex()));
		memoryConsistencyModel.setRarOrdering(EnumUtil.getOrderingType(comboRar.getSelectionIndex()));
		
		log.finest("[applyChange] : MemoryConsistencyModel"
				+ "\n  RawOrdering=" + memoryConsistencyModel.getRawOrdering()
				+ "\n  WarOrdering=" + memoryConsistencyModel.getWarOrdering()
				+ "\n  WawOrdering=" + memoryConsistencyModel.getWawOrdering()
				+ "\n  RarOrdering=" + memoryConsistencyModel.getRarOrdering());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return MemoryConsistencyModel.class;
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		memoryConsistencyModel = new MemoryConsistencyModel();
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) memoryConsistencyModel;
	}
}
