/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for ComponentSet.
 */
public class ComponentSetInputPanel extends InputPanelBase {
	
	private static final Logger log = ShimLoggerUtil
			.getLogger(ComponentSetInputPanel.class);
	
	private Text textName;
	private Text textNChildComponentSet;
	private Text textNChildMasterComponent;
	private Text textNChildSlaveComponent;
	
	private ComponentSet componentSet = new ComponentSet();

	/**
	 * Constructs a new instance of ComponentSetInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public ComponentSetInputPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));
	
		setLblTitleText("Component Set");
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(2, false));
		
		Label lbl_Name = new Label(composite, SWT.NONE);
		lbl_Name.setText("Name");
		
		textName = new Text(composite, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lbl_Size = new Label(composite, SWT.NONE);
		lbl_Size.setText("Number of Child MasterComponent");
		
		textNChildMasterComponent = new Text(composite, SWT.BORDER);
		textNChildMasterComponent.setEnabled(false);
		textNChildMasterComponent.setEditable(false);
		textNChildMasterComponent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lbl_nChildSlaveComponent = new Label(composite, SWT.NONE);
		lbl_nChildSlaveComponent.setText("Number of Child SlaveComponent");
		
		textNChildSlaveComponent = new Text(composite, SWT.BORDER);
		textNChildSlaveComponent.setEnabled(false);
		textNChildSlaveComponent.setEditable(false);
		textNChildSlaveComponent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lbl_nChildComponentSet = new Label(composite, SWT.NONE);
		lbl_nChildComponentSet.setText("Number of Child ComponentSet");
		
		textNChildComponentSet = new Text(composite, SWT.BORDER);
		textNChildComponentSet.setEnabled(false);
		textNChildComponentSet.setEditable(false);
		textNChildComponentSet.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		// Checking Required.
		textName.addModifyListener(new TextModifyListener("name"));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		componentSet = (ComponentSet)input;
		
		this.textName.setText(componentSet.getName());
		this.textNChildMasterComponent.setText(componentSet.getMasterComponent().size()+"");
		this.textNChildSlaveComponent.setText(componentSet.getSlaveComponent().size()+"");
		this.textNChildComponentSet.setText(componentSet.getComponentSet().size()+"");		
	}


	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		if(componentSet == null) {
			return;
		}
		
		componentSet.setName(this.textName.getText());
		
		log.finest("[applyChange] : ComponentSet"
				+ "\n  Name=" + componentSet.getName());
	}
 
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return componentSet.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		componentSet = new ComponentSet();
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) componentSet;
	}
}
