/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.ccf.sampleapp;

import javax.xml.bind.UnmarshalException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.multicore_association.ccf.api.ConfigurationSet;
import org.multicore_association.ccf.api.DefineSet;

import swing2swt.layout.BorderLayout;
import swing2swt.layout.BoxLayout;

public class CCF_DemoSample extends ApplicationWindow implements
		SelectionListener {

	public Button btnOpenccfclockselect;

	public Composite root_container;

	public Menu menu;
	public Action actionOpenCCF;
	public Action action;
	public Action action_1;
	public Composite composite;
	public ScrolledComposite composite_scroll;
	public Composite composite_confs;
	public Button btnConfigureAll;

	// CCF_ActionCollection actionAll = new CCF_ActionCollection();

	/**
	 * Create the application window.
	 */
	public CCF_DemoSample() {
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {

		root_container = new Composite(parent, SWT.NONE);
		root_container.setLayout(new BorderLayout(0, 0));

		composite = new Composite(root_container, SWT.NONE);
		composite.setLayoutData(BorderLayout.NORTH);
		composite.setLayout(new GridLayout(3, false));

		btnOpenccfclockselect = new Button(composite, SWT.NONE);
		btnOpenccfclockselect.setAlignment(SWT.RIGHT);
		btnOpenccfclockselect.addSelectionListener(this);
		btnOpenccfclockselect.setText("OpenCCF");
		new Label(composite, SWT.NONE);

		btnConfigureAll = new Button(composite, SWT.NONE);
		btnConfigureAll.addSelectionListener(this);
		btnConfigureAll.setText("Configure All");

		composite_scroll = new ScrolledComposite(root_container, SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		composite_scroll.setLayoutData(BorderLayout.CENTER);
		composite_scroll.setLayout(new FillLayout());
		composite_scroll.setExpandHorizontal(true);
		composite_scroll.setExpandVertical(true);

		composite_confs = new Composite(composite_scroll, SWT.BORDER);
		composite_confs.setLayoutData(BorderLayout.CENTER);
		composite_confs.setLayout(new BoxLayout(BoxLayout.Y_AXIS));

		menu = new Menu(parent);
		parent.setMenu(menu);

		return root_container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			actionOpenCCF = new Action("Open CCF File") {
				public void run() {
					System.out.println("OpenCCF");
					makeGuiCCFs(root_container);
				}

			};
		}
		{
			action = new Action("New Action") {

			};
		}
		{
			action_1 = new Action("File") {

			};
		}
	}

	/**
	 * Create the toolbar manager.
	 * 
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * 
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			CCF_DemoSample window = new CCF_DemoSample();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * 
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("CCF Sample Application");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(945, 358);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() == btnConfigureAll) {
			do_btnConfigureAll_widgetSelected(e);
		}
		if (e.getSource() == btnOpenccfclockselect) {
			do_btnOpenccfclockselect_widgetSelected(e);
		}
	}

	protected void do_btnOpenccfclockselect_widgetSelected(SelectionEvent e) {

		makeGuiCCFs(composite_confs);
		composite_confs.pack();

		Point computeSize = composite_confs.computeSize(SWT.DEFAULT,
				SWT.DEFAULT);

		composite_scroll.setContent(composite_confs);
		composite_scroll.setMinSize(computeSize.x, computeSize.y);
	}

	public void makeGuiCCFs(Composite container) {

		for (Control child : container.getChildren()) {
			if (child instanceof Button)
				continue;
			child.dispose();
		}
		FileDialog openDialog = new FileDialog(container.getShell(), SWT.OPEN);
		String path = openDialog.open();
		System.out.println("OpenFile Path=" + path);
		if (path == null) {
			return;
		}

		ConfigurationSet cfset = new ConfigurationSet();
		// CCFDataLoader loader = new CCFDataLoader(cfset,
		// "datas/ccf-sample-select.xml");
		CCFDataLoader loader = new CCFDataLoader(cfset, path);
		cfset = CCF_ModelManager.root;
		try {

			loader.Load();
			System.out.println("configurationset name="
					+ loader.cfset.getName());

		} catch (UnmarshalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		makeGuiCCF(container, loader.cfset);

	}

	public void makeGuiCCF(Composite container, ConfigurationSet cfset) {

		DefineSet defset = cfset.getDefineSet();

		for (org.multicore_association.ccf.api.Configuration cf : cfset
				.getConfiguration()) {
			// if(cf.getFormType()== FormType.SELECT){
			CCF_ConfigurationComposite ccfComposite1 = new CCF_ConfigurationComposite(
					container, SWT.NONE);

			ccfComposite1.setInput(cf, defset);
			container.layout();
			// } else if(cf.getFormType() == FormType.EXPRESSION) {
			//
			// } else {
			// System.out.println(" Unknown FormType");
			// }

		}

	}

	protected void do_btnConfigureAll_widgetSelected(SelectionEvent e) {

		CCF_ActionCollection actionAll = new CCF_ActionCollection();

		for (Control ctrl : composite_confs.getChildren()) {
			if (ctrl instanceof CCF_ConfigurationComposite) {

				CCF_ConfigurationComposite ccf_composite = (CCF_ConfigurationComposite) ctrl;
				String u = ccf_composite.get_att_uri();
				String x = ccf_composite.get_att_xpt();
				String r = ccf_composite.getCcf_result();

				System.out.println("action count !! valid ccf_name="
						+ ccf_composite.getCCF_Name() + "valid:"
						+ ccf_composite.isValid());

				if (ccf_composite.isValid()) {
					CCF_Action a = new CCF_Action(r, u, x);
					actionAll.addAction(a);
				}

				actionAll.run();
			}
		}

	}
}
