/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.multicore_association.shim.api.AddressSpace;
import org.multicore_association.shim.api.AddressSpaceSet;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.EndianType;
import org.multicore_association.shim.api.MemoryConsistencyModel;
import org.multicore_association.shim.api.OrderingType;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.actions.AddChildAction;
import org.multicore_association.shim.edit.actions.CopySelectedItemAction;
import org.multicore_association.shim.edit.actions.CutSelectedItemAction;
import org.multicore_association.shim.edit.actions.DeleteSelectedItemAction;
import org.multicore_association.shim.edit.actions.MyClipboard;
import org.multicore_association.shim.edit.actions.PasteChildAction;
import org.multicore_association.shim.edit.actions.ReNumberAddressSpaceAction;
import org.multicore_association.shim.edit.gui.common.ErrorMessageWriter;
import org.multicore_association.shim.edit.gui.common.NameAttributeChecker;
import org.multicore_association.shim.edit.gui.jface.ShimAddressSpaceTreeViewer;
import org.multicore_association.shim.edit.gui.jface.ShimSelectableItem;
import org.multicore_association.shim.edit.gui.swt.panel.AddressSpaceInputPanel;
import org.multicore_association.shim.edit.gui.swt.viewer.AddressSpaceTreeItemProviderWiz;
import org.multicore_association.shim.edit.gui.swt.viewer.AddressSpaceTreeLabelProvider;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimModelManager;
import org.multicore_association.shim.edit.model.ShimObject;
import org.multicore_association.shim.edit.model.ShimPreferences;
import org.multicore_association.shim.edit.model.data.DefaultDataStore;
import org.multicore_association.shim.edit.model.preferences.AddressSpacePreferences;

import swing2swt.layout.BorderLayout;

/**
 * Wizard page that allows configuration of the AddressSpace.
 */
public class AddressSpaceEditorWizardPage extends ShimEditorWizardPageBase
		implements SelectionListener, ShimSelectableItem {

	private static final Logger log = ShimLoggerUtil
			.getLogger(AddressSpaceEditorWizardPage.class);

	public List<ComponentSet> csetList = new ArrayList<ComponentSet>();

	public TreeItem selectedItem = null;
	public Composite composite;
	public Tree tree;
	public ShimAddressSpaceTreeViewer addressSpaceTreeViewer;
	public Composite compositeAddressSpace;
	public Table table;
	public TreeColumn trclmnAddressspacename;
	private TreeViewerColumn treeViewerColumn;
	private AddressSpaceInputPanel inputPanelAddressSpace;
	private Button btnReNumber;

	private AddressSpaceEditorWizardPage selfWz;

	/**
	 * Constructs a new instance of AddressSpaceEditorWizardPage.
	 */
	public AddressSpaceEditorWizardPage() {
		super("wizardPage");
		setTitle("Setting  AddressSpace");
		setDescription("View and Edit AddressSpaces and SubSpaces.\nIf you click on Back button, the information that you edited is destroyed.");
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

		log.finest("In ShimWizardPage_AddressSpace::createControl()----");
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new BorderLayout(0, 0));

		composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(BorderLayout.SOUTH);
		composite.setLayout(new GridLayout(1, false));

		btnReNumber = new Button(composite, SWT.NONE);
		btnReNumber.addSelectionListener(this);
		btnReNumber.setText("Re-number nodes");

		addressSpaceTreeViewer = new ShimAddressSpaceTreeViewer(container,
				false);
		addressSpaceTreeViewer.setAutoExpandLevel(3);
		addressSpaceTreeViewer.setExpandPreCheckFilters(true);

		tree = addressSpaceTreeViewer.getTree();
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(BorderLayout.WEST);

		treeViewerColumn = new TreeViewerColumn(addressSpaceTreeViewer,
				SWT.NONE);
		trclmnAddressspacename = treeViewerColumn.getColumn();
		trclmnAddressspacename.setWidth(150);
		trclmnAddressspacename.setText("AddressSpaceName");

		compositeAddressSpace = new Composite(container, SWT.NONE);
		compositeAddressSpace.setLayoutData(BorderLayout.CENTER);
		compositeAddressSpace.setLayout(new BorderLayout(0, 0));

		{
			inputPanelAddressSpace = new AddressSpaceInputPanel(
					compositeAddressSpace, SWT.NONE, true);
			inputPanelAddressSpace.setLayoutData(BorderLayout.CENTER);
		}

		// ShimSubSpaceTableViewer send error message into wizard page.
		addressSpaceTreeViewer
				.setLabelProvider(new AddressSpaceTreeLabelProvider());
		addressSpaceTreeViewer
				.setContentProvider(new AddressSpaceTreeItemProviderWiz());

		ErrorMessageWriter errorMassageWriter = (ErrorMessageWriter) this;
		inputPanelAddressSpace
				.setErrorMassageWriterForWizard(errorMassageWriter);
		inputPanelAddressSpace.setSelectableItem((ShimSelectableItem) this);

		SelectionListener listener = new SelectionListener() {

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

				if (selectedItem == item) {
					selectedItem = null;

				} else {
					selectedItem = item;
					tableRefresh();
				}
				addressSpaceTreeViewer.refresh();
			}

		};

		tree.addSelectionListener(listener);

		MenuManager menuMgr = new MenuManager();
		Menu menu = menuMgr.createContextMenu(addressSpaceTreeViewer
				.getControl());
		menuMgr.setRemoveAllWhenShown(true);
		addressSpaceTreeViewer.getControl().setMenu(menu);
		menuMgr.addMenuListener(new IMenuListener() {

			/**
			 * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
			 */
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				final ShimAddressSpaceTreeViewer viewer = addressSpaceTreeViewer;
				if (viewer.getSelection() instanceof IStructuredSelection) {
					if (selectedItem == null || viewer.getSelection().isEmpty()) {
						return;
					}

					Object node = selectedItem.getData();
					if (node instanceof AddressSpace
							&& viewer.getTree().getSelectionCount() == 1) {
						AddChildAction act_add_sb = new AddChildAction(viewer,
								node, SubSpace.class);
						manager.add(act_add_sb);

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
	 * Activates 'Re-number nodes' action.
	 * 
	 * @param event
	 *            the notified SelectionEvent
	 */
	protected void performRenumber(SelectionEvent event) {
		ReNumberAddressSpaceAction renumberAction = new ReNumberAddressSpaceAction(
				selfWz);
		renumberAction.run();
		tableRefresh();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.jface.ShimSelectableItem#getSelectedItem()
	 */
	@Override
	public TreeItem getSelectedItem() {
		return selectedItem;
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.jface.ShimSelectableItem#refresh()
	 */
	@Override
	public void refresh() {
		addressSpaceTreeViewer.refresh();
	}

	/**
	 * Refreshes the table viewers on this page.
	 */
	private void tableRefresh() {
		Object data = null;
		if (selectedItem == null || selectedItem.isDisposed()) {
			StructuredSelection selection = (StructuredSelection) addressSpaceTreeViewer
					.getSelection();
			Object[] selectionArray = selection.toArray();
			for (Object o : selectionArray) {
				data = o;
			}
		} else {
			data = selectedItem.getData();
		}

		// When using AddressSpaceTree From Wizard.
		if (data instanceof AddressSpace) {
			AddressSpace as = (AddressSpace) data;

			inputPanelAddressSpace.setInput(as);
			inputPanelAddressSpace.updateNameAttributeChecker();
		}
	}

	/**
	 * Creates an AddressSpaceSet from the input preferences.
	 */
	public void createAddressSpaceSet() {
		SystemConfiguration sys = ShimModelManager.getCurrentScd();
		AddressSpaceSet addressSpaceSet = sys.getAddressSpaceSet();

		List<AddressSpace> asList = addressSpaceSet.getAddressSpace();
		asList.clear();

		csetList = ShimModelManager.makeComponentSetList(sys);

		Map<ComponentSet, AddressSpace> ca_map = new HashMap<ComponentSet, AddressSpace>();

		AddressSpacePreferences asParameter = ShimPreferences
				.getCurrentInstance().getAddressSpacePreferences();
		String as_prefix = asParameter.getAddressSpaceName();
		String ss_prefix = ShimPreferences.getCurrentInstance()
				.getAddressSpacePreferences().getSubSpaceName();

		for (ComponentSet cset : csetList) {
			AddressSpace as = (AddressSpace) DefaultDataStore
					.getDefaultInstance(AddressSpace.class);

			as.setName(as_prefix
					+ cset.getName().substring(cset.getName().indexOf("_")));
			asList.add(as);
			ca_map.put(cset, as);
		}

		for (int i = 0; i < asList.size(); i++) {
			AddressSpace as = asList.get(i);
			List<SubSpace> subspacelist = as.getSubSpace();
			int nsub = asParameter.getNumberSubSpace();

			int startAddress = 0;

			int subSpaceSize = asParameter.getSizeSubSpace();

			log.finest("******SubSpace Size = " + subSpaceSize);

			int subSpaceAddress = startAddress;

			for (int j = 0; j < nsub; j++) {
				SubSpace ss = (SubSpace) DefaultDataStore
						.getDefaultInstance(SubSpace.class);

				ss.setName(ss_prefix
						+ as.getName().substring(as.getName().indexOf("_"))
						+ "_" + j);

				Integer startSubSpaceAddress = subSpaceAddress;
				Integer endSubSpaceAddress = subSpaceAddress + subSpaceSize - 1;

				ss.setStart(startSubSpaceAddress);
				ss.setEnd(endSubSpaceAddress);

				ss.setEndian(EndianType.LITTLE);

				MemoryConsistencyModel mcm = new MemoryConsistencyModel();
				mcm.setRarOrdering(OrderingType.ORDERD);
				mcm.setRawOrdering(OrderingType.ORDERD);
				mcm.setWarOrdering(OrderingType.ORDERD);
				mcm.setWawOrdering(OrderingType.ORDERD);

				ss.getMemoryConsistencyModel().add(mcm);

				subspacelist.add(ss);

				subSpaceAddress += subSpaceSize;
			}
		}

		log.finest("****csetList.size() = " + csetList.size());
		log.finest("****addressSpaceList.size() = " + asList.size());

		addressSpaceTreeViewer.setInput(asList);

		if (!asList.isEmpty()) {
			TreeItem item = addressSpaceTreeViewer.getTree().getItem(0);
			addressSpaceTreeViewer.getTree().select(item);
			addressSpaceTreeViewer.getTree().setFocus();

			IWizardPage nextpage = super.getNextPage();

			Object firstRefObject = null;

			if (nextpage == null) {
				// in case re-make AddressSpace
				List<ShimObject> objectList = ShimModelAdapter.getObjectsList(
						AddressSpace.class, sys);
				if (!objectList.isEmpty()) {
					firstRefObject = objectList.get(0).getObj();
				}

			} else {
				// in case create new SHIM XML
				CommunicationSetWizardPage np = (CommunicationSetWizardPage) nextpage;
				np.updateCombo();

				addressSpaceTreeViewer.refresh();

				firstRefObject = np.refComboFactoryAs.getFirstRefObject();
			}

			// Verify the uniqueness of the name attribute.
			Object[] objects = addressSpaceTreeViewer.getExpandedElements();

			NameAttributeChecker checker = new NameAttributeChecker(objects);
			inputPanelAddressSpace.setNameAttributeChecker(checker);

			inputPanelAddressSpace.setInput(firstRefObject);
		}
	}

	/**
	 * Returns the TreeViewer of AddressSpace.
	 * 
	 * @return the TreeViewer of AddressSpace
	 */
	public TreeViewer getAddressSpaceTreeViewer() {
		return addressSpaceTreeViewer;
	}

	/**
	 * Sets the TreeViewer of AddressSpace.
	 * 
	 * @param addressSpaceTreeViewer
	 *            the TreeViewer of AddressSpace
	 */
	public void setAddressSpaceTreeViewer(
			ShimAddressSpaceTreeViewer addressSpaceTreeViewer) {
		this.addressSpaceTreeViewer = addressSpaceTreeViewer;
	}
}
