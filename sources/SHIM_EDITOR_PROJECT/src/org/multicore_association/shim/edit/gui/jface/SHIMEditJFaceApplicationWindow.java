/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.multicore_association.shim.api.AccessType;
import org.multicore_association.shim.api.AccessTypeSet;
import org.multicore_association.shim.api.Accessor;
import org.multicore_association.shim.api.AddressSpace;
import org.multicore_association.shim.api.AddressSpaceSet;
import org.multicore_association.shim.api.Cache;
import org.multicore_association.shim.api.ClockFrequency;
import org.multicore_association.shim.api.CommonInstructionSet;
import org.multicore_association.shim.api.CommunicationSet;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.Connection;
import org.multicore_association.shim.api.ConnectionSet;
import org.multicore_association.shim.api.EventCommunication;
import org.multicore_association.shim.api.FIFOCommunication;
import org.multicore_association.shim.api.Instruction;
import org.multicore_association.shim.api.InterruptCommunication;
import org.multicore_association.shim.api.Latency;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.MasterSlaveBinding;
import org.multicore_association.shim.api.MasterSlaveBindingSet;
import org.multicore_association.shim.api.MemoryConsistencyModel;
import org.multicore_association.shim.api.ObjectFactory;
import org.multicore_association.shim.api.Performance;
import org.multicore_association.shim.api.PerformanceSet;
import org.multicore_association.shim.api.Pitch;
import org.multicore_association.shim.api.SharedMemoryCommunication;
import org.multicore_association.shim.api.SharedRegisterCommunication;
import org.multicore_association.shim.api.SlaveComponent;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.actions.PreferencesAction;
import org.multicore_association.shim.edit.actions.ReNumberComponnetSetAction;
import org.multicore_association.shim.edit.binding.ShimDataLoader;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.gui.common.NameAttributeChecker;
import org.multicore_association.shim.edit.gui.jface.wizard.CreateNewDataWizard;
import org.multicore_association.shim.edit.gui.jface.wizard.RemakeAddressSpaceWizard;
import org.multicore_association.shim.edit.gui.jface.wizard.RemakeCommunicationsWizard;
import org.multicore_association.shim.edit.gui.swt.panel.AccessTypeInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.AccessTypeSetInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.AccessorInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.AddressSpaceInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.AddressSpaceSetInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.CacheInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.ClockFrequencyInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.CommonInstructionSetInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.CommunicationSetInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.ComponentSetInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.ConnectionInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.ConnectionSetInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.EventCommunicationInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.FIFOCommunicationInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase;
import org.multicore_association.shim.edit.gui.swt.panel.InstructionInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.InterruptCommunicationInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.LatencyInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.MasterComponentInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.MasterSlaveBindingInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.MasterSlaveBindingSetInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.MemoryConsistencyModelInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.PerformanceInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.PerformanceSetInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.PitchInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.SearchInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.SharedMemoryCommunicationInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.SharedRegisterCommunicationInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.ShimObjectTableInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.SlaveComponentInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.SubSpaceInputPanel;
import org.multicore_association.shim.edit.gui.swt.panel.SystemConfigurationInputPanel;
import org.multicore_association.shim.edit.gui.swt.viewer.AddressSpaceTreeItemProvider;
import org.multicore_association.shim.edit.gui.swt.viewer.AddressSpaceTreeLabelProvider;
import org.multicore_association.shim.edit.gui.swt.viewer.CommunicationSetTreeItemProvider;
import org.multicore_association.shim.edit.gui.swt.viewer.CommunicationSetTreeLabelProvider;
import org.multicore_association.shim.edit.gui.swt.viewer.ComponentTreeItemProvider;
import org.multicore_association.shim.edit.gui.swt.viewer.ComponentTreeLabelProvider;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimModelManager;
import org.multicore_association.shim.edit.model.ShimObject;
import org.multicore_association.shim.edit.model.ShimPreferences;

import swing2swt.layout.BorderLayout;

/**
 * An editor of the SHIM Data.
 */
public class SHIMEditJFaceApplicationWindow extends ApplicationWindow implements
		ShimSelectableItem, SelectionListener {

	private static final Logger log = ShimLoggerUtil
			.getLogger(SHIMEditJFaceApplicationWindow.class);

	// DEBUG
	private boolean searchFlg = false;

	private Action actionSave;
	private Action actionOpen;
	private Action actionSaveAs;
	private Action actionExit;
	private Action actionNew;
	// Editing the elements.
	private Action actionChangeDisplay;
	// Search the elements.
	private Action actionSearch;
	private Action actionAbout;

	Shell rootShell = null;
	WizardDialog shimWizardDialog = null;
	CreateNewDataWizard createNewDataWizard = null;
	RemakeAddressSpaceWizard remakeAddressSpaceWizard = null;
	RemakeCommunicationsWizard remakeCommunicationsWizard = null;

	public static SystemConfiguration system;

	private Composite compositeSystem;
	private SystemConfigurationInputPanel inputPanelSystemConfiguration;

	public ShimAddressSpaceTreeViewer shimAddressSpaceTreeViewer;

	public ShimComponentTreeViewer shimComponentTreeViewer;

	private MasterComponentInputPanel inputPanelMasterComponent;
	private SlaveComponentInputPanel inputPanelSlaveComponent;
	private ComponentSetInputPanel inputPanelComponentSet;
	private CacheInputPanel inputPanelCache;
	private AccessTypeSetInputPanel inputPanelAccessTypeSet;
	private AccessTypeInputPanel inputPanelAccessType;
	private ClockFrequencyInputPanel inputPanelClockFrequency;
	private Composite compositeComponent;

	TabFolder tabFolder;
	TabItem tbtmComponents;

	Composite compositeSubSpaceTable;

	boolean dirtyFlag = false;
	public static String defaultPathName = null;

	private Composite compositeStackComponent;

	public TreeItem addressSpaceSelectedItem = null;
	public TreeItem componentSelectedItem = null;
	public TreeItem communicationSelectedItem = null;

	/**
	 * @see org.multicore_association.shim.edit.gui.jface.ShimSelectableItem#getSelectedItem()
	 */
	public TreeItem getSelectedItem() {
		log.finest("getSelectedItem(): getSelectionIndex="
				+ tabFolder.getSelectionIndex());
		int selectionIndex = tabFolder.getSelectionIndex();
		if (selectionIndex == 1) {
			return componentSelectedItem;
		} else if (selectionIndex == 2) {
			return addressSpaceSelectedItem;
		} else if (selectionIndex == 3) {
			return communicationSelectedItem;
		} else {
			return null;
		}
	}

	Tree treeAddressSpaceSet;

	public Table table;
	public Composite composite_2;
	public Composite compositeCommunicationTree;
	public Composite compositeStackCommunications;
	public Tree treeCommunicationSet;
	private SearchableTreeViewer shimCommunicationTreeViewer;

	private CommunicationSetInputPanel inputPanelCommunicationSet;
	private SharedRegisterCommunicationInputPanel inputPanelSharedRegisterCommunication;
	private SharedMemoryCommunicationInputPanel inputPanelLLSCCommunication;
	private EventCommunicationInputPanel inputPanelEventCommunication;
	private FIFOCommunicationInputPanel inputPanelFIFOCommunication;
	private InterruptCommunicationInputPanel inputPanelInterruptCommunication;
	private ConnectionSetInputPanel inputPanelConnectionSet;
	private ConnectionInputPanel inputPanelConnection;
	private PitchInputPanel inputPanelPitch;
	private LatencyInputPanel inputPanelLatency;
	private PerformanceInputPanel inputPanelPerformance;

	private Composite compositeStackAddressSpace;
	private MemoryConsistencyModelInputPanel inputPanelMemoryConsistencyModel;
	private MasterSlaveBindingSetInputPanel inputPanelMasterSlaveBindingSet;
	private MasterSlaveBindingInputPanel inputPanelMasterSlaveBinding;
	private PerformanceSetInputPanel inputPanelPerformanceSet;
	private PerformanceInputPanel inputPanelPerformance_1;
	private AccessorInputPanel inputPanelAccessor;
	private LatencyInputPanel inputPanelLatency_1;
	private PitchInputPanel inputPanelPitch_1;
	private CommonInstructionSetInputPanel inputPanelCommonInstructionSet;
	private InstructionInputPanel inputPanelInstruction;
	private PerformanceInputPanel inputPanelPerformance_2;
	private LatencyInputPanel inputPanelLatency_2;
	private PitchInputPanel inputPanelPitch_2;
	private Composite composite;
	private Button btnRenumber;
	private AddressSpaceSetInputPanel inputPanelAddressSpaceSet;
	private AddressSpaceInputPanel inputPanelAddressSpace;
	private SubSpaceInputPanel inputPanelSubSpace;

	// Editing the elements.
	private Composite shimEditContainer;
	private Composite compositeElements;
	private Class<?> clazz;
	private ShimObjectTableInputPanel inputPanelShimObjectTable;

	// Search the elements.
	private Composite compositeSearch;
	private SearchInputPanel inputPanelSearch;

	private InputPanelBase currentInputPanel;
	private Object currentInputPanelObject;
	private Object currentInputPanelObjectParent;
	private Action remakeAddressSpaceAction;
	private Action remakeCommunicationsAction;

	private InputPanelBase currentComponentsPanel;
	private InputPanelBase currentAddressSpacePanel;
	private InputPanelBase currentCommunicationsPanel;

	/**
	 * Constructs a new instance of SHIMEditJFaceApplicationWindow.
	 */
	public SHIMEditJFaceApplicationWindow() {

		super(null);

		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Creates contents of the application window.
	 * 
	 * @param parent
	 *            the parent control
	 */
	@Override
	protected Control createContents(Composite parent) {
		shimEditContainer = new Composite(parent, SWT.NONE);
		shimEditContainer.setLayout(new StackLayout());

		tabFolder = new TabFolder(shimEditContainer, SWT.NONE);

		// creates tab items.
		TabItem tbtmSystem = new TabItem(tabFolder, SWT.NONE);
		tbtmSystem.setText("System");
		{
			compositeSystem = new Composite(tabFolder, SWT.NONE);
			tbtmSystem.setControl(compositeSystem);
			compositeSystem.setLayoutData(BorderLayout.CENTER);
			compositeSystem.setLayout(new StackLayout());
			inputPanelSystemConfiguration = new SystemConfigurationInputPanel(
					compositeSystem, SWT.NONE);
		}

		createComponentsTabItem();

		createAddressSpaceTabItem();

		createCommunicationTabItem();

		// creates the tab item of ElementTable.
		compositeElements = new Composite(shimEditContainer, SWT.NONE);
		compositeElements.setLayoutData(BorderLayout.CENTER);
		compositeElements.setLayout(new BorderLayout(0, 0));

		inputPanelShimObjectTable = new ShimObjectTableInputPanel(
				compositeElements, SWT.NONE);

		// creates the tab item of Search.
		compositeSearch = new Composite(shimEditContainer, SWT.NONE);
		compositeSearch.setLayoutData(BorderLayout.CENTER);
		compositeSearch.setLayout(new BorderLayout(0, 0));

		inputPanelSearch = new SearchInputPanel(compositeSearch, SWT.NONE);

		// layout
		StackLayout shimEditContainerLayout = (StackLayout) shimEditContainer
				.getLayout();
		shimEditContainerLayout.topControl = tabFolder;

		rootShell = parent.getShell();

		SystemConfiguration sys = ShimModelManager.createSystemConfiguration();
		ComponentSet root = new ComponentSet();
		root.setName("root");

		sys.setComponentSet(root);
		ArrayList<ComponentSet> clist = new ArrayList<ComponentSet>();
		clist.add(root);
		shimComponentTreeViewer.setInput(clist.toArray());
		log.finest("shimComponentTreeViewer.setInput() clist.size="
				+ clist.size());
		shimComponentTreeViewer.refresh();

		return shimEditContainer;
	}

	/**
	 * Creates the tab item of Components.
	 */
	private void createComponentsTabItem() {

		tbtmComponents = new TabItem(tabFolder, SWT.NONE);
		tbtmComponents.setText("Components");

		compositeComponent = new Composite(tabFolder, SWT.NONE);
		tbtmComponents.setControl(compositeComponent);
		compositeComponent.setLayout(new BorderLayout(0, 0));
		{
			compositeStackComponent = new Composite(compositeComponent,
					SWT.NONE);
			compositeStackComponent.setLayoutData(BorderLayout.CENTER);
			compositeStackComponent.setLayout(new StackLayout());

			inputPanelMasterComponent = new MasterComponentInputPanel(
					compositeStackComponent, SWT.NONE);
			inputPanelSlaveComponent = new SlaveComponentInputPanel(
					compositeStackComponent, SWT.NONE);
			inputPanelComponentSet = new ComponentSetInputPanel(
					compositeStackComponent, SWT.NONE);
			inputPanelCache = new CacheInputPanel(compositeStackComponent,
					SWT.NONE);
			inputPanelAccessTypeSet = new AccessTypeSetInputPanel(
					compositeStackComponent, SWT.NONE);
			inputPanelAccessType = new AccessTypeInputPanel(
					compositeStackComponent, SWT.NONE);
			inputPanelClockFrequency = new ClockFrequencyInputPanel(
					compositeStackComponent, SWT.NONE);

			inputPanelCommonInstructionSet = new CommonInstructionSetInputPanel(
					compositeStackComponent, SWT.NONE);
			inputPanelInstruction = new InstructionInputPanel(
					compositeStackComponent, SWT.NONE);
			inputPanelPerformance_2 = new PerformanceInputPanel(
					compositeStackComponent, SWT.NONE);
			inputPanelLatency_2 = new LatencyInputPanel(
					compositeStackComponent, SWT.NONE);
			inputPanelPitch_2 = new PitchInputPanel(compositeStackComponent,
					SWT.NONE);
		}
		composite_2 = new Composite(compositeComponent, SWT.NONE);
		composite_2.setLayoutData(BorderLayout.WEST);
		composite_2.setLayout(new BorderLayout(0, 0));

		shimComponentTreeViewer = new ShimComponentTreeViewer(composite_2);
		shimComponentTreeViewer.setExpandPreCheckFilters(true);
		shimComponentTreeViewer.setAutoExpandLevel(4);
		Tree treeComponentSet = shimComponentTreeViewer.getTree();
		treeComponentSet.setLinesVisible(true);
		treeComponentSet.setHeaderVisible(true);
		treeComponentSet.setLayoutData(BorderLayout.CENTER);
		{
			TreeViewerColumn treeViewerColumn = new TreeViewerColumn(
					shimComponentTreeViewer, SWT.NONE);
			TreeColumn treeColumn = treeViewerColumn.getColumn();
			treeColumn.setWidth(300);
			treeColumn.setText("ComponentSetTree");
		}
		{
			composite = new Composite(composite_2, SWT.NONE);
			composite.setLayoutData(BorderLayout.SOUTH);
			composite.setLayout(new GridLayout(1, false));
			{
				btnRenumber = new Button(composite, SWT.NONE);
				btnRenumber.addSelectionListener(this);
				btnRenumber.setText("Re-number nodes");
			}
		}
		shimComponentTreeViewer
				.setLabelProvider(new ComponentTreeLabelProvider());
		shimComponentTreeViewer
				.setContentProvider(new ComponentTreeItemProvider());

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
				componentSelectedItem = item;

				log.finest("TreeViewer:Components " + item.getText()
						+ " selected");

				InputPanelBase inputPanel = null;
				Object selectedObject = item.getData();
				if (selectedObject instanceof MasterComponent) {
					inputPanel = inputPanelMasterComponent;
				} else if (selectedObject instanceof SlaveComponent) {
					inputPanel = inputPanelSlaveComponent;
				} else if (selectedObject instanceof ComponentSet) {
					inputPanel = inputPanelComponentSet;
				} else if (selectedObject instanceof Cache) {
					inputPanel = inputPanelCache;
				} else if (selectedObject instanceof AccessTypeSet) {
					inputPanel = inputPanelAccessTypeSet;
				} else if (selectedObject instanceof AccessType) {
					inputPanel = inputPanelAccessType;
				} else if (selectedObject instanceof CommonInstructionSet) {
					inputPanel = inputPanelCommonInstructionSet;
				} else if (selectedObject instanceof Instruction) {
					inputPanel = inputPanelInstruction;
				} else if (selectedObject instanceof Performance) {
					inputPanel = inputPanelPerformance_2;
				} else if (selectedObject instanceof Latency) {
					inputPanel = inputPanelLatency_2;
				} else if (selectedObject instanceof Pitch) {
					inputPanel = inputPanelPitch_2;
				} else if (selectedObject instanceof ClockFrequency) {
					inputPanel = inputPanelClockFrequency;
				}
				currentComponentsPanel = inputPanel;

				changeInputPanel(item, inputPanel, compositeStackComponent,
						shimComponentTreeViewer);
			}
		};
		treeComponentSet.addSelectionListener(selectionListener);

		// If notifies the selection event in tabFolder, refreshes all
		// tree viewers.
		tabFolder.addSelectionListener(new SelectionListener() {

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
				refresh();
			}
		});

		// creates an empty input
		ObjectFactory of = ShimModelManager.getObjectFactory();

		ComponentSet croot = of.createComponentSet();

		croot.setName("rootCompo");
		ComponentSet cs1 = of.createComponentSet();
		cs1.setName("cs1");

		MasterComponent cpu1 = of.createMasterComponent();
		cpu1.setName("cpu1");
		SlaveComponent mem1 = of.createSlaveComponent();
		mem1.setName("mem1");
		ComponentSet cs11 = of.createComponentSet();
		cs11.setName("cs11");

		croot.getComponentSet().add(cs1);

		cs1.getComponentSet().add(cs11);

		cs1.getMasterComponent().add(cpu1);
		cs1.getSlaveComponent().add(mem1);

		MasterComponent cpu11 = of.createMasterComponent();
		cpu11.setName("cpu11");
		MasterComponent cpu12 = of.createMasterComponent();
		cpu12.setName("cpu12");

		cs11.getMasterComponent().add(cpu11);
		cs11.getMasterComponent().add(cpu12);

		shimComponentTreeViewer.setInput(croot.getComponentSet().toArray());
	}

	/**
	 * Creates the tab item of AddressSpace.
	 */
	private void createAddressSpaceTabItem() {
		TabItem tbtmAddressSpace = new TabItem(tabFolder, SWT.NONE);
		tbtmAddressSpace.setText("AddressSpace");

		Composite compositeAddressSpaces = new Composite(tabFolder, SWT.NONE);
		tbtmAddressSpace.setControl(compositeAddressSpaces);
		compositeAddressSpaces.setLayout(new BorderLayout(0, 0));
		{
			shimAddressSpaceTreeViewer = new ShimAddressSpaceTreeViewer(
					compositeAddressSpaces);
			shimAddressSpaceTreeViewer.setAutoExpandLevel(3);
			shimAddressSpaceTreeViewer.setExpandPreCheckFilters(true);
			treeAddressSpaceSet = shimAddressSpaceTreeViewer.getTree();
			treeAddressSpaceSet.setHeaderVisible(true);
			treeAddressSpaceSet.setLinesVisible(true);
			treeAddressSpaceSet.setLayoutData(BorderLayout.WEST);
			{
				TreeViewerColumn treeViewerColumn = new TreeViewerColumn(
						shimAddressSpaceTreeViewer, SWT.NONE);
				TreeColumn trclmnAddressSpaceName = treeViewerColumn
						.getColumn();
				trclmnAddressSpaceName.setWidth(295);
				trclmnAddressSpaceName.setText("AddressSpaceName");
			}
			shimAddressSpaceTreeViewer
					.setLabelProvider(new AddressSpaceTreeLabelProvider());
			shimAddressSpaceTreeViewer
					.setContentProvider(new AddressSpaceTreeItemProvider());

		}
		compositeStackAddressSpace = new Composite(compositeAddressSpaces,
				SWT.NONE);
		compositeStackAddressSpace.setLayoutData(BorderLayout.CENTER);
		compositeStackAddressSpace.setLayout(new StackLayout());
		{
			inputPanelAddressSpaceSet = new AddressSpaceSetInputPanel(
					compositeStackAddressSpace, SWT.NONE);
			inputPanelAddressSpace = new AddressSpaceInputPanel(
					compositeStackAddressSpace, SWT.NONE, false);
			inputPanelSubSpace = new SubSpaceInputPanel(
					compositeStackAddressSpace, SWT.NONE);
			inputPanelMasterSlaveBindingSet = new MasterSlaveBindingSetInputPanel(
					compositeStackAddressSpace, SWT.NONE);
			inputPanelMasterSlaveBinding = new MasterSlaveBindingInputPanel(
					compositeStackAddressSpace, SWT.NONE);
			inputPanelPerformanceSet = new PerformanceSetInputPanel(
					compositeStackAddressSpace, SWT.NONE);
			inputPanelPerformance_1 = new PerformanceInputPanel(
					compositeStackAddressSpace, SWT.NONE);
			inputPanelAccessor = new AccessorInputPanel(
					compositeStackAddressSpace, SWT.NONE);
			inputPanelLatency_1 = new LatencyInputPanel(
					compositeStackAddressSpace, SWT.NONE);
			inputPanelPitch_1 = new PitchInputPanel(compositeStackAddressSpace,
					SWT.NONE);
			inputPanelMemoryConsistencyModel = new MemoryConsistencyModelInputPanel(
					compositeStackAddressSpace, SWT.NONE);

			SelectionAdapter selectionAdapter = new SelectionAdapter() {
				/**
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent event) {
					Accessor accessor = (Accessor) inputPanelAccessor
							.getInput();

					MasterComponent masterComponentRef = (MasterComponent) accessor
							.getMasterComponentRef();
					List<ShimObject> objectsList = ShimModelAdapter
							.getObjectsList(AccessType.class,
									masterComponentRef);

					boolean dialogFlag = false;

					// Confirms whether invalid reference occurs or not.
					List<PerformanceSet> performanceSetList = accessor
							.getPerformanceSet();
					for (PerformanceSet performanceSet : performanceSetList) {
						List<Performance> performanceList = performanceSet
								.getPerformance();
						for (Performance performance : performanceList) {
							AccessType accessTypeRef = (AccessType) performance
									.getAccessTypeRef();
							if (accessTypeRef == null) {
								continue;
							}

							boolean isFound = false;
							AccessType replace = null;
							for (ShimObject objects : objectsList) {
								AccessType type = (AccessType) objects.getObj();
								if (accessTypeRef.getId().equals(type.getId())) {
									isFound = true;
									break;
								} else if (accessTypeRef.getName().equals(
										type.getName())) {
									replace = type;
								}
							}

							if (!isFound) {
								// If there is the AccessType which has the same name,
								// replaces with it.
								performance.setAccessTypeRef(replace);
								if (replace == null) {
									dialogFlag = true;
								}
							}
						}
					}

					// If some invalid reference occurs, notifies users.
					if (dialogFlag) {
						MessageDialog.openWarning(rootShell, "Warning",
								CommonConstants.MESSAGE_INVALID_IDREF);
					}
				}
			};
			inputPanelAccessor.getBtnApply().addSelectionListener(
					selectionAdapter);
		}

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
				log.finest("TreeViewer:AddressSpace " + item.getText()
						+ " selected");

				addressSpaceSelectedItem = item;

				InputPanelBase inputPanel = null;
				Object selectedObject = item.getData();

				if (selectedObject instanceof AddressSpaceSet) {
					inputPanel = inputPanelAddressSpaceSet;
				} else if (selectedObject instanceof AddressSpace) {
					inputPanel = inputPanelAddressSpace;
				} else if (selectedObject instanceof SubSpace) {
					inputPanel = inputPanelSubSpace;
				} else if (selectedObject instanceof MasterSlaveBindingSet) {
					inputPanel = inputPanelMasterSlaveBindingSet;
				} else if (selectedObject instanceof MasterSlaveBinding) {
					inputPanel = inputPanelMasterSlaveBinding;
				} else if (selectedObject instanceof PerformanceSet) {
					inputPanel = inputPanelPerformanceSet;
				} else if (selectedObject instanceof Performance) {
					inputPanel = inputPanelPerformance_1;
				} else if (selectedObject instanceof Latency) {
					inputPanel = inputPanelLatency_1;
				} else if (selectedObject instanceof Pitch) {
					inputPanel = inputPanelPitch_1;
				} else if (selectedObject instanceof Accessor) {
					inputPanel = inputPanelAccessor;
				} else if (selectedObject instanceof MemoryConsistencyModel) {
					inputPanel = inputPanelMemoryConsistencyModel;
				}
				
				currentAddressSpacePanel = inputPanel;
				
				changeInputPanel(item, inputPanel, compositeStackAddressSpace,
						shimAddressSpaceTreeViewer);
			}
		};
		treeAddressSpaceSet.addSelectionListener(listener);
	}

	/**
	 * Creates the tab item of CommunicationSet.
	 */
	private void createCommunicationTabItem() {
		TabItem tbtmCommunications = new TabItem(tabFolder, SWT.NONE);
		tbtmCommunications.setText("Communications");

		Composite composite_Communications = new Composite(tabFolder, SWT.NONE);
		tbtmCommunications.setControl(composite_Communications);
		composite_Communications.setLayout(new BorderLayout(0, 0));

		compositeCommunicationTree = new Composite(composite_Communications,
				SWT.NONE);
		compositeCommunicationTree.setLayoutData(BorderLayout.WEST);
		compositeCommunicationTree.setLayout(new BorderLayout(0, 0));

		shimCommunicationTreeViewer = new ShimCommunicationTreeViewer(
				compositeCommunicationTree);
		shimCommunicationTreeViewer.setAutoExpandLevel(2);
		treeCommunicationSet = shimCommunicationTreeViewer.getTree();
		treeCommunicationSet.setLinesVisible(true);
		treeCommunicationSet.setHeaderVisible(true);
		{
			TreeViewerColumn treeViewerColumn = new TreeViewerColumn(
					shimCommunicationTreeViewer, SWT.NONE);
			TreeColumn trclmnCommunicationset = treeViewerColumn.getColumn();
			trclmnCommunicationset.setWidth(304);
			trclmnCommunicationset.setText("CommunicationSet");
		}
		shimCommunicationTreeViewer
				.setLabelProvider(new CommunicationSetTreeLabelProvider());
		shimCommunicationTreeViewer
				.setContentProvider(new CommunicationSetTreeItemProvider());

		compositeStackCommunications = new Composite(composite_Communications,
				SWT.NONE);

		// creates InputPanels for each element
		compositeStackCommunications.setLayout(new StackLayout());
		{
			inputPanelCommunicationSet = new CommunicationSetInputPanel(
					compositeStackCommunications, SWT.NONE);
			inputPanelSharedRegisterCommunication = new SharedRegisterCommunicationInputPanel(
					compositeStackCommunications, SWT.NONE);
			inputPanelLLSCCommunication = new SharedMemoryCommunicationInputPanel(
					compositeStackCommunications, SWT.NONE);
			inputPanelEventCommunication = new EventCommunicationInputPanel(
					compositeStackCommunications, SWT.NONE);
			inputPanelFIFOCommunication = new FIFOCommunicationInputPanel(
					compositeStackCommunications, SWT.NONE);
			inputPanelInterruptCommunication = new InterruptCommunicationInputPanel(
					compositeStackCommunications, SWT.NONE);
			inputPanelConnectionSet = new ConnectionSetInputPanel(
					compositeStackCommunications, SWT.NONE);
			inputPanelConnection = new ConnectionInputPanel(
					compositeStackCommunications, SWT.NONE);
			inputPanelPitch = new PitchInputPanel(compositeStackCommunications,
					SWT.NONE);
			inputPanelLatency = new LatencyInputPanel(
					compositeStackCommunications, SWT.NONE);
			inputPanelPerformance = new PerformanceInputPanel(
					compositeStackCommunications, SWT.NONE);
		}

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
				communicationSelectedItem = item;

				log.finest("TreeViewer:CommunicationSet " + item.getText()
						+ " selected");

				InputPanelBase inputPanel = null;
				Object selectedObject = item.getData();
				if (selectedObject instanceof CommunicationSet) {
					inputPanel = inputPanelCommunicationSet;
				} else if (selectedObject instanceof EventCommunication) {
					inputPanel = inputPanelEventCommunication;
				} else if (selectedObject instanceof SharedRegisterCommunication) {
					inputPanel = inputPanelSharedRegisterCommunication;
				} else if (selectedObject instanceof SharedMemoryCommunication) {
					inputPanel = inputPanelLLSCCommunication;
				} else if (selectedObject instanceof FIFOCommunication) {
					inputPanel = inputPanelFIFOCommunication;
				} else if (selectedObject instanceof InterruptCommunication) {
					inputPanel = inputPanelInterruptCommunication;
				} else if (selectedObject instanceof ConnectionSet) {
					inputPanel = inputPanelConnectionSet;
				} else if (selectedObject instanceof Connection) {
					inputPanel = inputPanelConnection;
				} else if (selectedObject instanceof Pitch) {
					inputPanel = inputPanelPitch;
				} else if (selectedObject instanceof Latency) {
					inputPanel = inputPanelLatency;
				} else if (selectedObject instanceof Performance) {
					inputPanel = inputPanelPerformance;
				}
				currentCommunicationsPanel = inputPanel;

				changeInputPanel(item, inputPanel,
						compositeStackCommunications,
						shimCommunicationTreeViewer);
			}

		};
		treeCommunicationSet.addSelectionListener(selectionListener);
	}

	/**
	 * Changes the InputPanel which displays into the specified InputPanel.
	 * 
	 * @param item
	 *            the selected item in tree viewer
	 * @param inputPanel
	 *            the InputPanel to display
	 * @param composite
	 *            the parent composite of InputPanel
	 * @param treeViewer
	 *            the tree viewer which displays at present
	 */
	private void changeInputPanel(TreeItem item, InputPanelBase inputPanel,
			Composite composite, TreeViewer treeViewer) {

		// Verify the uniqueness of the name attribute.
		Object selectedObject = item.getData();
		TreeItem parentItem = item.getParentItem();
		Object parentObject = null;
		if (parentItem != null) {
			parentObject = parentItem.getData();
		}

		StackLayout stack = (StackLayout) composite.getLayout();

		InputPanelBase previousInputpanel = (InputPanelBase) stack.topControl;
		if (previousInputpanel != null) {
			previousInputpanel.dispose();
		}

		if (inputPanel != null) {
			currentInputPanel = inputPanel;
			currentInputPanelObject = selectedObject;
			currentInputPanelObjectParent = parentObject;
			clazz = inputPanel.getApiClass();

			// Verify the uniqueness of the name attribute.
			if (parentObject != null
					&& ShimModelAdapter.hasNameAttribute(selectedObject)) {
				NameAttributeChecker checker = new NameAttributeChecker(clazz,
						parentObject);
				inputPanel.setNameAttributeChecker(checker);
			}

			inputPanel.setSelectableItem((ShimSelectableItem) this);
			inputPanel.setInput(selectedObject);
			treeViewer.refresh();
			log.finest("changeInputPanel: " + clazz.getSimpleName()
					+ " selected.");
		}
		stack.topControl = inputPanel;
		composite.layout();
	}

	/**
	 * Creates the actions.
	 */
	private void createActions() {

		createSaveAction();

		createOpenAction();

		createSaveAsAction();

		createNewAction();

		createElementTableAction();

		createSearchAction();

		createRemakeAddressSpacesAction();

		createRemakeCommunicationsAction();

		final SHIMEditJFaceApplicationWindow window = this;

		actionExit = new Action("Exit@Alt+F4") {
			public void run() {
				log.finest("[Action] Exit run");
				window.close();
			}
		};

		actionAbout = new Action(AboutShimEditorDialog.TITLE) {
			@Override
			public void run() {
				AboutShimEditorDialog dialog = new AboutShimEditorDialog(
						Display.getCurrent().getActiveShell());
				dialog.open();
			}
		};
	}

	/**
	 * Creates an action to save SHIM Data to the current file.<br>
	 * If never opens a file, saves to the specified file.
	 */
	private void createSaveAction() {
		actionSave = new Action("Save@Ctrl+S") {
			public void run() {
				log.finest("[Action] Save Action start");
				ValidationEventCollector vec = new ValidationEventCollector();

				FileOutputStream fos = null;

				try {
					SystemConfiguration system = ShimModelManager
							.getCurrentScd();
					log.finest("system name=" + system.getName());
					JAXBContext jc = ShimModelAdapter.getJAXBContext();
					// writes to file
					Marshaller marshaller = jc.createMarshaller();
					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
							Boolean.TRUE);

					if (defaultPathName == null) {
						log.finest("First Save");
						FileDialog openDialog = new FileDialog(rootShell,
								SWT.SAVE);
						if (defaultPathName == null) {
							defaultPathName = "shim_save_file_out.xml";
						}
						openDialog.setFileName(defaultPathName);
						log.finest("Default PathName=" + defaultPathName);

						String path = openDialog.open();
						defaultPathName = openDialog.getFilterPath() + "/"
								+ openDialog.getFileName();
						log.finest("OpenFile Path=" + path
								+ ",defaultPathName=" + defaultPathName);
					}

					fos = new FileOutputStream(defaultPathName);
					// activates validation
					Schema schema = ShimModelAdapter.getShimSchema();
					marshaller.setSchema(schema);
					marshaller.setEventHandler(vec);

					marshaller.marshal(system, fos);

					rootShell.setText(defaultPathName);
					log.info("Save SHIM data. Path=" + defaultPathName);

				} catch (Exception e) {
					log.log(Level.SEVERE, "XML Save Error", e);
				} finally {
					if (vec != null && vec.hasEvents()) {
						for (ValidationEvent ve : vec.getEvents()) {
							log.log(Level.SEVERE, ve.getMessage(),
									ve.getLinkedException());
							Status status = new Status(IStatus.ERROR,
									"XML Save Error", IStatus.OK,
									ve.getMessage(), null);
							ErrorDialog.openError(getShell(), "Save Error",
									"Not Valid XML Format", status);
						}
					}

					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							log.log(Level.SEVERE, e.getMessage(), e);
						}
					}
				}
			}

		};
		actionSave.setEnabled(false);
	}

	/**
	 * Creates an action to open the specified file.
	 */
	private void createOpenAction() {
		actionOpen = new Action("Open@Ctrl+O") {

			public void run() {

				log.finest("[Action] Open start");

				FileDialog openDialog = new FileDialog(rootShell, SWT.OPEN);
				String path = openDialog.open();
				if (path == null) {
					// Cancel
					log.finest("Open Operation Canceled!!");
					return;
				}
				log.info("Open file. Path=" + path);

				SHIMEditJFaceApplicationWindow.defaultPathName = path;

				SystemConfiguration system = SHIMEditJFaceApplicationWindow.system = ShimModelManager
						.createSystemConfiguration();

				ShimDataLoader loader = new ShimDataLoader(system, path);

				loader.componentTreeViewer = shimComponentTreeViewer;
				loader.addressSpaceTreeViewer = shimAddressSpaceTreeViewer;
				loader.communicationTreeViewer = shimCommunicationTreeViewer;

				try {
					int ret = loader.Load();
					if (ret == 1) {
						rootShell.setText(path);
					}
				} catch (Exception e) {
					log.log(Level.SEVERE, "XML Open Error", e);
					Status status = new Status(IStatus.ERROR, "XML Open Error",
							IStatus.OK, e.getMessage(), null);
					ErrorDialog.openError(getShell(), "Open Error",
							"Not Valid XML Format", status);
					return;
				}

				actionSave.setEnabled(true);
				actionSaveAs.setEnabled(true);
				remakeAddressSpaceAction.setEnabled(true);
				remakeCommunicationsAction.setEnabled(true);
				actionChangeDisplay.setEnabled(true);
				actionSearch.setEnabled(true);

				SystemConfiguration currSystem = ShimModelManager
						.getCurrentScd();
				inputPanelSystemConfiguration.setInput(currSystem);
				StackLayout stack = (StackLayout) compositeSystem.getLayout();
				stack.topControl = inputPanelSystemConfiguration;
				compositeSystem.layout();

				ShimPreferences.getNewInstance();

				super.run();
			}
		};
	}

	/**
	 * Creates an action to save SHIM Data to the specified file.
	 */
	private void createSaveAsAction() {
		actionSaveAs = new Action("SaveAs@Shift+Ctrl+S") {
			public void run() {
				log.finest("[Action] SaveAs start");

				SystemConfiguration system = ShimModelManager.getCurrentScd();
				log.finest("system name=" + system.getName());

				FileDialog openDialog = new FileDialog(rootShell, SWT.SAVE);
				if (defaultPathName == null) {
					defaultPathName = "shim_save_file_out.xml";
				}
				openDialog.setFileName(defaultPathName);
				log.finest("Default PathName=" + defaultPathName);

				String path = openDialog.open();
				defaultPathName = openDialog.getFilterPath() + "/"
						+ openDialog.getFileName();
				log.finest("OpenFile Path=" + path + ",defaultPathName="
						+ defaultPathName);

				ValidationEventCollector vec = new ValidationEventCollector();

				FileOutputStream fos = null;

				try {
					JAXBContext jc = ShimModelAdapter.getJAXBContext();

					fos = new FileOutputStream(defaultPathName);
					Marshaller marshaller = jc.createMarshaller();
					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
							Boolean.TRUE);
					// When using the validation
					Schema schema = ShimModelAdapter.getShimSchema();
					marshaller.setSchema(schema);
					marshaller.setEventHandler(vec);

					marshaller.marshal(system, fos);
					log.info("Save SHIM data. Path=" + defaultPathName);

					rootShell.setText(defaultPathName);

				} catch (Exception e) {
					log.log(Level.SEVERE, "XML Save Error", e);
				} finally {
					if (vec != null && vec.hasEvents()) {
						for (ValidationEvent ve : vec.getEvents()) {
							log.log(Level.SEVERE, ve.getMessage(),
									ve.getLinkedException());
							Status status = new Status(IStatus.ERROR,
									"XML Save Error", IStatus.OK,
									ve.getMessage(), null);
							ErrorDialog.openError(getShell(), "Save Error",
									"Not Valid XML Format", status);
						}
					}

					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							log.log(Level.WARNING, "File close erro.", e);
						}
					}
				}
			}

		};
		actionSaveAs.setEnabled(false);
	}

	/**
	 * Creates an action to create a new SHIM Data.
	 */
	private void createNewAction() {
		actionNew = new Action("New@Ctrl+N") {

			/**
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				log.finest("[Action] New start");

				createNewDataWizard = new CreateNewDataWizard();

				createNewDataWizard
						.setComponentTreeViewer(shimComponentTreeViewer);
				createNewDataWizard
						.setAddressSpaceTreeViewer(shimAddressSpaceTreeViewer);
				createNewDataWizard
						.setCommunicationTreeViewer(shimCommunicationTreeViewer);

				Realm.runWithDefault(
						SWTObservables.getRealm(rootShell.getDisplay()),
						new Runnable() {

							/**
							 * @see java.lang.Runnable#run()
							 */
							@Override
							public void run() {
								shimWizardDialog = new WizardDialog(rootShell,
										createNewDataWizard);

								int open = shimWizardDialog.open();

								if (open == 0) {
									actionSave.setEnabled(true);
									actionSaveAs.setEnabled(true);
									remakeAddressSpaceAction.setEnabled(true);
									remakeCommunicationsAction.setEnabled(true);
									actionChangeDisplay.setEnabled(true);
									actionSearch.setEnabled(true);

									SystemConfiguration system = ShimModelManager
											.getCurrentScd();
									inputPanelSystemConfiguration
											.setInput(system);
									StackLayout stack = (StackLayout) compositeSystem
											.getLayout();
									stack.topControl = inputPanelSystemConfiguration;
									compositeSystem.layout();

									defaultPathName = null;
									rootShell.setText(CommonConstants.API_NAME);
								}
							}
						});

			}
		};
	}

	/**
	 * Creates an action to edit the element in TableViewer.
	 */
	private void createElementTableAction() {
		actionChangeDisplay = new Action("ElementTable") {

			/**
			 * @see org.eclipse.jface.action.Action#getStyle()
			 */
			@Override
			public int getStyle() {
				return SWT.TOGGLE;
			}

			/**
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				log.finest("[Action] ElementTable start");
				actionSearch.setChecked(false);

				StackLayout stackLayout = (StackLayout) shimEditContainer
						.getLayout();
				if (!stackLayout.topControl.equals(compositeElements)) {
					inputPanelShimObjectTable.clearErrorMessage();
					inputPanelShimObjectTable.setInput(clazz);
					inputPanelShimObjectTable
							.selectCurrentInputPanelObject(currentInputPanelObject);
					stackLayout.topControl = compositeElements;

				} else {
					refresh();

					Object selectedItem = inputPanelShimObjectTable
							.getSelectedItem();
					if (selectedItem != null) {
						int selectionIndex = tabFolder.getSelectionIndex();
						SearchableTreeViewer viewer = null;
						if (selectionIndex == 1) {
							viewer = shimComponentTreeViewer;
						} else if (selectionIndex == 2) {
							viewer = shimAddressSpaceTreeViewer;
						} else if (selectionIndex == 3) {
							viewer = shimCommunicationTreeViewer;
						}
						if (viewer != null) {
							viewer.findAndSelect(selectedItem);
						}
					}

					setInputToCurrentInputPannel();

					stackLayout.topControl = tabFolder;

					inputPanelShimObjectTable.dispose();
				}
				shimEditContainer.layout();
			}
		};
		actionChangeDisplay.setEnabled(false);
	}

	/**
	 * Creates an action to search the tree viewers.
	 */
	private void createSearchAction() {
		actionSearch = new Action("Search") {

			/**
			 * @see org.eclipse.jface.action.Action#getStyle()
			 */
			@Override
			public int getStyle() {
				return SWT.TOGGLE;
			}

			/**
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				actionChangeDisplay.setChecked(false);

				StackLayout stackLayout = (StackLayout) shimEditContainer
						.getLayout();
				if (!stackLayout.topControl.equals(compositeSearch)) {
					inputPanelSearch.clearErrorMessage();
					inputPanelSearch.setInput(shimAddressSpaceTreeViewer);
					inputPanelSearch.setInput(shimComponentTreeViewer);
					inputPanelSearch.setInput(shimCommunicationTreeViewer);
					stackLayout.topControl = compositeSearch;
				} else {
					refresh();

					setInputToCurrentInputPannel();

					stackLayout.topControl = tabFolder;

					inputPanelSearch.dispose();
				}
				shimEditContainer.layout();
			}
		};
		actionSearch.setEnabled(false);
	}

	/**
	 * Creates an action to re-make AddressSpaceSet.
	 */
	private void createRemakeAddressSpacesAction() {
		remakeAddressSpaceAction = new Action("Re-Make AddressSpaceSet") {

			/**
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				log.finest("[Action] Re-Make AddressSpaceSet");

				remakeAddressSpaceWizard = new RemakeAddressSpaceWizard();
				remakeAddressSpaceWizard
						.setAddressSpaceTreeViewer(shimAddressSpaceTreeViewer);
				Rectangle bounds = rootShell.getBounds();
				remakeAddressSpaceWizard.setLayout(bounds.x, bounds.y);

				Realm.runWithDefault(
						SWTObservables.getRealm(rootShell.getDisplay()),
						new Runnable() {

							/**
							 * @see java.lang.Runnable#run()
							 */
							@Override
							public void run() {
								shimWizardDialog = new WizardDialog(rootShell,
										remakeAddressSpaceWizard);
								shimWizardDialog.open();
							}
						});
			}

		};
		remakeAddressSpaceAction.setEnabled(false);
	}

	/**
	 * Creates an action to re-make CommunicaionSet.
	 */
	private void createRemakeCommunicationsAction() {
		remakeCommunicationsAction = new Action("Re-Make CommunicaionSet") {

			/**
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				log.finest("[Action] Re-Make CommunicaionSet start");

				remakeCommunicationsWizard = new RemakeCommunicationsWizard();
				remakeCommunicationsWizard
						.setCommunicationTreeViewer(shimCommunicationTreeViewer);
				Rectangle bounds = rootShell.getBounds();
				remakeCommunicationsWizard.setLayout(bounds.x, bounds.y);

				Realm.runWithDefault(
						SWTObservables.getRealm(rootShell.getDisplay()),
						new Runnable() {

							/**
							 * @see java.lang.Runnable#run()
							 */
							@Override
							public void run() {
								shimWizardDialog = new WizardDialog(rootShell,
										remakeCommunicationsWizard);
								shimWizardDialog.open();
							}
						});
			}

		};
		remakeCommunicationsAction.setEnabled(false);
	}

	/**
	 * Creates the menu manager.
	 * 
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		menuManager.setActionDefinitionId("A\r\nB\r\nC");
		{
			MenuManager menu_File = new MenuManager("File");
			menuManager.add(menu_File);
			menu_File.add(actionNew);
			menu_File.add(actionOpen);
			menu_File.add(actionSave);
			menu_File.add(actionSaveAs);
			menu_File.add(actionExit);

			MenuManager menu_Preference = new MenuManager("Preferences");
			menuManager.add(menu_Preference);
			menu_Preference.add(new PreferencesAction());

			MenuManager menu_Help = new MenuManager("Help");
			menuManager.add(menu_Help);
			menu_Help.add(actionAbout);
		}
		return menuManager;
	}

	/**
	 * Creates the toolbar manager.
	 * 
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		toolBarManager.add(actionOpen);

		// Editing the elements.
		toolBarManager.add(new Separator());
		toolBarManager.add(actionNew);
		toolBarManager.add(new Separator());
		toolBarManager.add(remakeAddressSpaceAction);
		toolBarManager.add(new Separator());
		toolBarManager.add(remakeCommunicationsAction);
		toolBarManager.add(new Separator());
		toolBarManager.add(actionChangeDisplay);
		if (searchFlg) {
			toolBarManager.add(new Separator());
			toolBarManager.add(actionSearch);
		}

		return toolBarManager;
	}

	/**
	 * Creates the status line manager.
	 * 
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	public static SHIMEditJFaceApplicationWindow window;

	/**
	 * Launches the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			window = new SHIMEditJFaceApplicationWindow();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			String message = e.getMessage();
			ErrorDialog.openError(Display.getCurrent().getActiveShell(),
					"Error", message, new Status(IStatus.ERROR,
							SHIMEditJFaceApplicationWindow.class.getName(),
							message, e));
			log.log(Level.SEVERE, "Exec error.", e);
		}
	}

	/**
	 * Configures the shell.
	 * 
	 * @param shell
	 *            the shell
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(CommonConstants.API_NAME);
	}

	/**
	 * Returns the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(1025, 612);
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.jface.ShimSelectableItem#refresh()
	 */
	@Override
	public void refresh() {
		log.finest("refresh(): TreeViewer Refreshed");
		shimComponentTreeViewer.refresh();
		shimAddressSpaceTreeViewer.refresh();
		shimCommunicationTreeViewer.refresh();
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
		if (event.getSource() == btnRenumber) {
			performRenumberNodes(event);
		}
	}

	/**
	 * Notifies that re-numbering nodes has been requested for this window.
	 * 
	 * @param event
	 *            the modified SelectionEvent
	 */
	protected void performRenumberNodes(SelectionEvent event) {
		MessageBox dialog = new MessageBox(rootShell, SWT.OK | SWT.CANCEL);
		dialog.setText("Caution");
		dialog.setMessage("OK to re-number nodes? \nThis will serialize all the node-numbers added to the name of nodes from the root to the leaves.");

		int result = dialog.open();
		if (result == SWT.OK) {
			ReNumberComponnetSetAction renum_action = new ReNumberComponnetSetAction(
					shimComponentTreeViewer);
			renum_action.run();
			log.finest("Re-NumberNodes is done");
		}

		refresh();

		setInputToCurrentInputPannel();
	}

	/**
	 * Sets the current selected object to the current InputPannel
	 */
	private void setInputToCurrentInputPannel() {
		if (currentInputPanel != null && currentInputPanelObject != null) {
			if (currentInputPanelObjectParent != null
					&& ShimModelAdapter
							.hasNameAttribute(currentInputPanelObject)) {
				NameAttributeChecker checker = new NameAttributeChecker(clazz,
						currentInputPanelObjectParent);
				currentInputPanel.setNameAttributeChecker(checker);
			}
			currentInputPanel.setInput(currentInputPanelObject);
		}
	}

	/**
	 * Sets the current selected object to each current InputPannel
	 */
	public void setInputToEachCurrentInputPannel() {
		if (currentComponentsPanel != null) {
			Object input = currentComponentsPanel.getInput();
			TreeItem item = shimComponentTreeViewer.findTreeItem(input);
			if (item != null) {
				currentComponentsPanel.setInput(item.getData());
			}
		}

		if (currentAddressSpacePanel != null) {
			Object input = currentAddressSpacePanel.getInput();
			TreeItem item = shimAddressSpaceTreeViewer.findTreeItem(input);
			if (item != null) {
				currentAddressSpacePanel.setInput(item.getData());
			}
		}

		if (currentCommunicationsPanel != null) {
			Object input = currentCommunicationsPanel.getInput();
			TreeItem item = shimCommunicationTreeViewer.findTreeItem(input);
			if (item != null) {
				currentCommunicationsPanel.setInput(item.getData());
			}
		}

		Object asTreeInput = shimAddressSpaceTreeViewer.getInput();
		if (currentAddressSpacePanel != null && asTreeInput instanceof List
				&& ((List<?>) asTreeInput).isEmpty()) {
			StackLayout stackLayout = (StackLayout) compositeStackAddressSpace
					.getLayout();
			if (stackLayout.topControl == currentAddressSpacePanel) {
				stackLayout.topControl.dispose();
				stackLayout.topControl = null;
			}
			currentAddressSpacePanel = null;
			compositeStackAddressSpace.layout();
		}

		Object csTreeInput = shimCommunicationTreeViewer.getInput();
		if (currentCommunicationsPanel != null && csTreeInput instanceof List
				&& ((List<?>) csTreeInput).isEmpty()) {
			StackLayout stackLayout = (StackLayout) compositeStackCommunications
					.getLayout();
			if (stackLayout.topControl == currentCommunicationsPanel) {
				stackLayout.topControl.dispose();
				stackLayout.topControl = null;
			}
			currentCommunicationsPanel = null;
			compositeStackCommunications.layout();
		}
	}

	/**
	 * Changes the InputPanel which displays into the specified InputPanel.
	 * 
	 * @param element
	 *            the element to display and be selected
	 */
	public static void changeInputPanel(Object element) {
		int selectionIndex = -1;
		if (window.shimComponentTreeViewer.findAndSelect(element)) {
			selectionIndex = 1;
		} else if (window.shimAddressSpaceTreeViewer.findAndSelect(element)) {
			selectionIndex = 2;
		} else if (window.shimCommunicationTreeViewer.findAndSelect(element)) {
			selectionIndex = 3;
		}

		if (selectionIndex == -1) {
			window.inputPanelSearch.clearSearchResult();
		} else {
			window.tabFolder.setSelection(selectionIndex);
		}

		window.refresh();

		window.setInputToCurrentInputPannel();

		StackLayout stackLayout = (StackLayout) window.shimEditContainer
				.getLayout();
		stackLayout.topControl = window.tabFolder;

		window.inputPanelSearch.dispose();
		window.actionSearch.setChecked(false);

		window.shimEditContainer.layout();
	}
}
