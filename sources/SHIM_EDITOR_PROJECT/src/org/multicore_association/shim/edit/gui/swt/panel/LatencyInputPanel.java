/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.Latency;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for Latency.
 */
public class LatencyInputPanel extends InputPanelBase {
	
	private static final Logger log = ShimLoggerUtil
			.getLogger(LatencyInputPanel.class);
	
	private Text textBest;
	private Text textTypical;
	private Text textWorst;
	
	private Latency latency = new Latency();

	/**
	 * Constructs a new instance of LatencyInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public LatencyInputPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));
		
		setLblTitleText("Latency");
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		
		Label lbl_Best = new Label(composite, SWT.NONE);
		lbl_Best.setText("Best");
		new Label(composite, SWT.NONE);
		
		textBest = new Text(composite, SWT.BORDER);
		
		Label lbl_Typical = new Label(composite, SWT.NONE);
		lbl_Typical.setText("Typical");
		new Label(composite, SWT.NONE);
		
		textTypical = new Text(composite, SWT.BORDER);
		
		Label lbl_Worst = new Label(composite, SWT.NONE);
		lbl_Worst.setText("Worst");
		new Label(composite, SWT.NONE);
		
		textWorst = new Text(composite, SWT.BORDER);
		
		// Checking Required.
		textBest.addModifyListener(new TextModifyListener("best"));
		textBest.addModifyListener(new SmallNumberModifyListener("best"));
		textTypical.addModifyListener(new TextModifyListener("typical"));
		textTypical.addModifyListener(new SmallNumberModifyListener("typical"));
		textWorst.addModifyListener(new TextModifyListener("worst"));
		textWorst.addModifyListener(new SmallNumberModifyListener("worst"));
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		latency = (Latency)input;
		
		if (latency.getBest() == null) {
			this.textBest.setText("");
		} else {
			this.textBest.setText(latency.getBest()+"");
		}
		
		this.textTypical.setText(latency.getTypical()+"");
		
		if (latency.getWorst() == null) {
			this.textWorst.setText("");
		} else {
			this.textWorst.setText(latency.getWorst()+"");
		}
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		assert(latency  != null);
		
		String strBest = this.textBest.getText();
		if (isNullOrEmpty(strBest)) {
			latency.setBest(null);
		} else {
			latency.setBest(Float.parseFloat(strBest));
		}
		String strWorst = this.textWorst.getText();
		if (isNullOrEmpty(strWorst)) {
			latency.setWorst(null);
		} else {
			latency.setWorst(Float.parseFloat(strWorst));
		}
		latency.setTypical(Float.parseFloat(this.textTypical.getText()));
		
		log.finest("[applyChange] : Latency"
				+ "\n  Best=" + latency.getBest()
				+ "\n  Typical=" + latency.getTypical()
				+ "\n  Worst=" + latency.getWorst());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return latency.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		latency = new Latency();
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) latency;
	}
}
