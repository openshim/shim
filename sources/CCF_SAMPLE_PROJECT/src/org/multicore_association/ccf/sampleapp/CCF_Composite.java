/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.ccf.sampleapp;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.multicore_association.ccf.api.Configuration;
import org.multicore_association.ccf.api.FormType;
import org.multicore_association.ccf.api.Item;


public class CCF_Composite extends Composite {
	public Label lbl_name;
	public Combo ccf_combo;
	
	List<Composite> composite_list = new ArrayList<Composite>();
	public Composite composite;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 * @param cf 
	 */
	public CCF_Composite(Composite parent, int style) {
		super(parent, SWT.BORDER);
		setLayout(new RowLayout(SWT.VERTICAL));
		
		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new RowData(320, 32));
		
		lbl_name = new Label(composite, SWT.NONE);
		lbl_name.setText("*name*");
		
		ccf_combo = new Combo(composite, SWT.NONE);
		GridData gd_ccf_combo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_ccf_combo.widthHint = 179;
		ccf_combo.setLayoutData(gd_ccf_combo);
		
				System.out.println("In Constructor: combo.size="+ccf_combo.getItemCount());


	}
	
	public void setInput(final Configuration cf) {
		System.out.println("SelectComposite:setInput");
		if(cf.getFormType() == FormType.SELECT) {
			lbl_name.setText(cf.getName());
			//ccf_combo.removeAll();
			for(Item item : cf.getItem()) {
				System.out.println("combo add item =  "+item.getValue());
				ccf_combo.add(item.getValue().toString());
			}
			ccf_combo.setText(ccf_combo.getItem(0));
			ccf_combo.select(0);
			ccf_combo.redraw();
			
			ccf_combo.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					Combo combo = (Combo)e.getSource();
					int idx = combo.getSelectionIndex();
					Item itm = cf.getItem().get(idx);
					System.out.println("Select item index="+idx);
					Composite ccf_base = combo.getParent().getParent();
					
					
					for(Control child : ccf_base.getChildren()){
						if(child instanceof CCF_Composite) {
							if(child != combo.getParent()) {
								child.dispose();
							}
						}
					}
					
					for(Configuration cf3 : itm.getConfiguration()) {
						CCF_Composite ccfc3 = new CCF_Composite(ccf_base, SWT.NONE);
						ccfc3.setInput(cf3);
						System.out.println("Nexted Configuration creteated");
					}
					ccf_base.layout();
					ccf_base.getParent().layout();
				}
				
			});
		
			System.out.println("In SetInput: combo.size="+ccf_combo.getItemCount());
		} else {
			System.out.println("*** This FormType is not supported yet");
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
