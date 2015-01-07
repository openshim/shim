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
import org.multicore_association.shim.api.Pitch;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for Pitch.
 */
public class PitchInputPanel extends InputPanelBase {
	
	private static final Logger log = ShimLoggerUtil
			.getLogger(PitchInputPanel.class);
	
	private Text textBest;
	private Text textTypical;
	private Text textWorst;
	
	private Pitch pitch = new Pitch();

	/**
	 * Constructs a new instance of PitchInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public PitchInputPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));
		
		setLblTitleText("Pitch");
		
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
		textBest.addModifyListener(new SmallNumberModifyListener("best"));
		textTypical.addModifyListener(new TextModifyListener("typical"));
		textTypical.addModifyListener(new SmallNumberModifyListener("typical"));
		textWorst.addModifyListener(new SmallNumberModifyListener("worst"));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		pitch = (Pitch)input;
		
		if (pitch.getBest() == null) {
			this.textBest.setText("");
		} else {
			this.textBest.setText(pitch.getBest()+"");
		}
		
		this.textTypical.setText(pitch.getTypical()+"");
		
		if (pitch.getWorst() == null) {
			this.textWorst.setText("");
		} else {
			this.textWorst.setText(pitch.getWorst()+"");
		}
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		assert(pitch  != null);
		
		String strBest = this.textBest.getText();
		if (isNullOrEmpty(strBest)) {
			pitch.setBest(null);
		} else {
			pitch.setBest(Float.parseFloat(strBest));
		}
		String strWorst = this.textWorst.getText();
		if (isNullOrEmpty(strWorst)) {
			pitch.setWorst(null);
		} else {
			pitch.setWorst(Float.parseFloat(strWorst));
		}
		pitch.setTypical(Float.parseFloat(this.textTypical.getText()));
		
		log.finest("[applyChange] : Pitch"
				+ "\n  Best=" + pitch.getBest()
				+ "\n  Typical=" + pitch.getTypical()
				+ "\n  Worst=" + pitch.getWorst());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return pitch.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		pitch = new Pitch();
	}
	
	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) pitch;
	}
}
