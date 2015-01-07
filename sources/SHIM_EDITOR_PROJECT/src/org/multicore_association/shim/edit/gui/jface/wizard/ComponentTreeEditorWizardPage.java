/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.SlaveComponent;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.actions.AddChildAction;
import org.multicore_association.shim.edit.actions.CopySelectedItemAction;
import org.multicore_association.shim.edit.actions.CutSelectedItemAction;
import org.multicore_association.shim.edit.actions.DeleteSelectedItemAction;
import org.multicore_association.shim.edit.actions.MyClipboard;
import org.multicore_association.shim.edit.actions.PasteChildAction;
import org.multicore_association.shim.edit.actions.ReNumberComponnetSetAction;
import org.multicore_association.shim.edit.gui.jface.ShimComponentTreeViewer;
import org.multicore_association.shim.edit.gui.jface.ShimSelectableItem;
import org.multicore_association.shim.edit.gui.swt.viewer.ComponentTreeItemProviderWiz;
import org.multicore_association.shim.edit.gui.swt.viewer.ComponentTreeLabelProvider;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.ShimModelManager;
import org.multicore_association.shim.edit.model.ShimPreferences;

import swing2swt.layout.BorderLayout;

/**
 * Wizard page that allows configuration of the Components.
 */
public class ComponentTreeEditorWizardPage extends ShimEditorWizardPageBase
		implements SelectionListener, ShimSelectableItem {

	private static final Logger log = ShimLoggerUtil
			.getLogger(ComponentTreeEditorWizardPage.class);

	public Tree tree;
	public ShimComponentTreeViewer shimComponentTreeViewer;

	public ComponentTreeEditorWizardPage selfWz;

	public ShimComponentTreeViewer mainTreeViewer = null;
	public ComponentSet root = null;
	public TreeItem selectedItem = null;

	public TreeItem getSelectedItem() {
		return selectedItem;
	}

	public Composite composite;
	public Button btnReNumber;

	/**
	 * Constructs a new instance of ComponentTreeEditorWizardPage.
	 */
	public ComponentTreeEditorWizardPage() {
		super("wizardPage");
		setTitle("Edit Component Structure");
		setDescription("View and Edit Component Set Structure.\nIf you click on Back button, the information that you edited is destroyed.");
		selfWz = this;
	}

	/**
	 * Creates contents of this page.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	@Override
	public void createControl(Composite parent) {

		log.finest("In ShimWizardPage_TreeEdit::createControl()----");
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new BorderLayout(0, 0));

		shimComponentTreeViewer = new ShimComponentTreeViewer(container, false);
		shimComponentTreeViewer.setExpandPreCheckFilters(true);
		shimComponentTreeViewer.setAutoExpandLevel(3);
		tree = shimComponentTreeViewer.getTree();
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(BorderLayout.CENTER);

		composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(BorderLayout.SOUTH);
		composite.setLayout(new GridLayout(2, false));
		new Label(composite, SWT.NONE);

		btnReNumber = new Button(composite, SWT.NONE);
		btnReNumber.addSelectionListener(this);
		btnReNumber.setText("Re-number nodes");
		shimComponentTreeViewer
				.setLabelProvider(new ComponentTreeLabelProvider());
		shimComponentTreeViewer
				.setContentProvider(new ComponentTreeItemProviderWiz());

		SelectionListener selectionListener = new SelectionListener() {

			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				// NOOP
			}

			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem item = (TreeItem) event.item;
				selectedItem = item;
			}

		};

		tree.addSelectionListener(selectionListener);

		MenuManager menuMgr = new MenuManager();
		Menu menu = menuMgr.createContextMenu(shimComponentTreeViewer
				.getControl());
		menuMgr.setRemoveAllWhenShown(true);

		shimComponentTreeViewer.getControl().setMenu(menu);
		menuMgr.addMenuListener(new IMenuListener() {

			/**
			 * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
			 */
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				final ShimComponentTreeViewer viewer = shimComponentTreeViewer;
				if (viewer.getSelection() instanceof IStructuredSelection) {
					if (selectedItem == null) {
						return;
					}

					Object node = selectedItem.getData();
					if (node instanceof ComponentSet && viewer.getTree().getSelectionCount() == 1) {
						AddChildAction addChildComponentSetAct = new AddChildAction(viewer,
								node, ComponentSet.class);
						AddChildAction addChildMasterComponentAct = new AddChildAction(viewer,
								node, MasterComponent.class);
						AddChildAction addChildSlaveComponentAct = new AddChildAction(viewer,
								node, SlaveComponent.class);

						manager.add(addChildComponentSetAct);
						manager.add(addChildMasterComponentAct);
						manager.add(addChildSlaveComponentAct);
						
						manager.add(new Separator());
					}

					TreeItem parentItem = selectedItem.getParentItem();
					if (parentItem != null) {
						manager.add(new DeleteSelectedItemAction(viewer));
					}

					manager.add(new CutSelectedItemAction(viewer));
					manager.add(new CopySelectedItemAction(viewer));
					if (MyClipboard.canPaste()) {
						manager.add(new PasteChildAction(viewer));
					}
				}
			}
		});
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		// NOOP
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(SelectionEvent event) {
		if (event.getSource() == btnReNumber) {
			performRenumber(event);
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.jface.ShimSelectableItem#refresh()
	 */
	@Override
	public void refresh() {
		shimComponentTreeViewer.refresh();

	}

	/**
	 * Activates 'Re-number nodes' action.
	 * 
	 * @param e
	 *            the notified SelectionEvent
	 */
	protected void performRenumber(SelectionEvent event) {
		ReNumberComponnetSetAction renumberAction = new ReNumberComponnetSetAction(
				selfWz);
		renumberAction.run();
	}

	/**
	 * Creates the root ComponentSet element from input preferences.
	 */
	public void createRootComponentSet() {
		ShimPreferences settings = ShimPreferences.getCurrentInstance();

		ComponentSet dcs = ShimModelManager.getDefaultComponentSet();

		root = new ComponentSet();
		root.setName(dcs.getName() + "_0");

		int numberOfComonentSet = settings.getComponentsPreferences()
				.getNumberComponent();
		int numberOfMasterComponent = settings.getComponentsPreferences()
				.getNumberMaster();
		int numberOfSlaveComponent = settings.getComponentsPreferences()
				.getNumberSlave();
		
		if (numberOfComonentSet == 0) {
			// In case of numberOfComonentSet == 0, 
			// adds MasterComponents and SlaveComponents to the root.
			for (int j = 0; j < numberOfMasterComponent; j++) {
				MasterComponent mc = ShimModelManager
						.createMasterComponent("_0_" + j);
				root.getMasterComponent().add(mc);
			}

			for (int j = 0; j < numberOfSlaveComponent; j++) {
				SlaveComponent slc = ShimModelManager
						.createSlaveComponent("_0_" + j);
				log.log(Level.FINEST,
						"-------------In Wizard CreateSlaveComponent id="
								+ slc.getId());
				root.getSlaveComponent().add(slc);
			}
			
		} else {
			// Otherwise, adds  MasterComponents and SlaveComponents
			// to the child ComponentSets.
			for (int i = 0; i < numberOfComonentSet; i++) {
				ComponentSet cset = new ComponentSet();
				cset.setName(dcs.getName() + "_0_" + i);
	
				for (int j = 0; j < numberOfMasterComponent; j++) {
					MasterComponent mc = ShimModelManager
							.createMasterComponent("_0_" + i + "_" + j);
					cset.getMasterComponent().add(mc);
				}
	
				for (int j = 0; j < numberOfSlaveComponent; j++) {
					SlaveComponent slc = ShimModelManager
							.createSlaveComponent("_0_" + i + "_" + j);
					log.log(Level.FINEST,
							"-------------In Wizard CreateSlaveComponent id="
									+ slc.getId());
					cset.getSlaveComponent().add(slc);
				}
				root.getComponentSet().add(cset);
			}
		}

		List<ComponentSet> rootArray = new ArrayList<ComponentSet>();
		rootArray.add(root);
		shimComponentTreeViewer.setInput(rootArray);

		SystemConfiguration sys = ShimModelManager.getCurrentScd();
		sys.setComponentSet(root);
	}

	/**
	 * Returns the TreeViewer of ComponentSet.
	 * 
	 * @return the TreeViewer of ComponentSet
	 */
	public ShimComponentTreeViewer getShimComponentTreeViewer() {
		return shimComponentTreeViewer;
	}

	/**
	 * Returns the TreeViewer of ComponentSet.
	 * 
	 * @param shimComponentTreeViewer
	 *            the TreeViewer of ComponentSet
	 */
	public void setShimComponentTreeViewer(
			ShimComponentTreeViewer shimComponentTreeViewer) {
		this.shimComponentTreeViewer = shimComponentTreeViewer;
	}
}
