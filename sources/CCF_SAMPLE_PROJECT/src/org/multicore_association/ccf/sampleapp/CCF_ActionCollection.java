/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.ccf.sampleapp;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;


public class CCF_ActionCollection extends Action {

	List<CCF_Action> actionList = new ArrayList<CCF_Action>();
	
	void addAction(CCF_Action action) {
		actionList.add(action);
	}
	
	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return super.getText();
	}
	
	@Override
	public void setText(String text) {
		// TODO Auto-generated method stub
		super.setText(text);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		for(CCF_Action action : actionList) {
			action.run();
		}
		
	}



}
