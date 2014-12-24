/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.multicore_association.shim.api.AddressSpace;
import org.multicore_association.shim.api.AddressSpaceSet;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.SlaveComponent;
import org.multicore_association.shim.api.SubSpace;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.gui.jface.SHIMEditJFaceApplicationWindow;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.ShimComponentTree;
import org.multicore_association.shim.edit.model.ShimModelManager;
import org.multicore_association.shim.edit.model.ShimPreferences;
import org.multicore_association.shim.edit.model.preferences.SavePolicyPreferences;
import org.multicore_association.shim.edit.model.preferences.ShimPreferencesStore;

/**
 * A wizard to generate AddressSpaceSet again.
 */
public class RemakeAddressSpaceWizard extends Wizard {

	private static final Logger log = ShimLoggerUtil
			.getLogger(RemakeAddressSpaceWizard.class);

	TreeViewer addressSpaceTreeViewer = null;

	private AddressSpaceParameterWizardPage createAddressSpace = null;
	private AddressSpaceEditorWizardPage editAsTreeWz = null;

	public ComponentSet rootComponent = null;

	private Object previousSelectedPage;

	private int x = CommonConstants.WIZARD_DEFAULT_X;
	private int y = CommonConstants.WIZARD_DEFAULT_Y;

	private Set<String> currentAsIdSet;
	private Set<String> currentSsIdSet;

	/**
	 * Constructs a new instance of RemakeAddressSpaceWizard.
	 */
	public RemakeAddressSpaceWizard() {
		setWindowTitle("Re-Make AddressSpaceSet Wizard");

		// saves current AddressSpaces/SubSpaces id set to delete reference node
		// after clicking on finish button.
		this.currentAsIdSet = new HashSet<String>();
		this.currentSsIdSet = new HashSet<String>();
		SystemConfiguration sys = ShimModelManager.getCurrentScd();
		AddressSpaceSet addressSpaceSet = sys.getAddressSpaceSet();
		if (addressSpaceSet != null) {
			List<AddressSpace> addressSpaces = addressSpaceSet
					.getAddressSpace();
			for (AddressSpace addressSpace : addressSpaces) {
				currentAsIdSet.add(addressSpace.getId());

				List<SubSpace> subSpaces = addressSpace.getSubSpace();
				for (SubSpace subSpace : subSpaces) {
					currentSsIdSet.add(subSpace.getId());
				}
			}
		}

		// current sc is changed.
		ShimPreferences.getNewCopyInstance();

		log.info(getWindowTitle() + " open");
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {

		createAddressSpace = new AddressSpaceParameterWizardPage();
		addPage(createAddressSpace);

		editAsTreeWz = new AddressSpaceEditorWizardPage();
		editAsTreeWz.setRequiredToCreateData(true);
		addPage(editAsTreeWz);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		getShell().setBounds(x, y, CommonConstants.WIZARD_WIDTH,
				CommonConstants.WIZARD_HEIGHT);
	}

	/**
	 * Sets the receiver's location to the rectangular area specified by the
	 * arguments.
	 * 
	 * @param x
	 *            the x coordinate of the rectangle
	 * @param y
	 *            the y coordinate of the rectangle
	 */
	public void setLayout(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {

		final SystemConfiguration sys = ShimModelManager.getCurrentScd();
		final ShimPreferences preferences = ShimPreferences
				.getCurrentInstance();
		log.info(ShimPreferences.createDumpStr(preferences, true));

		try {
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(
					getShell());
			progressDialog.run(false, false, new IRunnableWithProgress() {
				/**
				 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
				 */
				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					remakeShimXml(sys, preferences, monitor);
				}

			});
		} catch (InvocationTargetException | InterruptedException e) {
			log.log(Level.SEVERE, "Re-Make AddressSpace Error.", e);
		}

		// Saves preferences.
		SavePolicyPreferences wizParameter = preferences
				.getSavePolicyPreferences();
		boolean requestToSave = false;
		if (wizParameter.isSaveOnRequest()) {
			MessageBox request = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.OK | SWT.CANCEL);
			request.setMessage("Save preferences you input in the wizard.");
			int result = request.open();
			requestToSave = (result == SWT.OK);
		}
		if (requestToSave || wizParameter.isAlwaysSave()) {
			ShimPreferencesStore store = ShimPreferencesStore.getInstance();
			store.setAccessTypePreferences(preferences
					.getAccessTypePreferences());
			store.setAddressSpacePreferences(preferences
					.getAddressSpacePreferences());
			store.setCacheDataPreferences(preferences.getCacheDataPreferences());
			store.setCommunicationSetPreferences(preferences
					.getCommunicationSetPreferences());

			store.setComponentsPreferences(preferences
					.getComponentsPreferences());
			store.setMasterComponentPreferences(preferences
					.getMasterComponentPreferences());
			store.setSlaveComponentPreferences(preferences
					.getSlaveComponentPreferences());
			store.setComponentSetPreferences(preferences
					.getComponentSetPreferences());

			store.save();
		}

		return true;
	}

	private void remakeShimXml(SystemConfiguration sys,
			ShimPreferences preferences, IProgressMonitor monitor) {
		monitor.beginTask("Re-Makes AddressSpaces.", 1);

		if (editAsTreeWz.isRequiredToCreateData()) {
			editAsTreeWz.createAddressSpaceSet();
		}
		monitor.worked(1);

		remakeMasterSlaveBindingSet(sys, preferences, monitor);

		List<AddressSpaceSet> rootArray = new ArrayList<AddressSpaceSet>();
		rootArray.add(sys.getAddressSpaceSet());

		// remove reference nodes
		for (String asId : currentAsIdSet) {
			ShimModelManager
					.removeReferenceNodeFromSharedMemoryCommunication_AS(asId);
		}
		for (String ssId : currentSsIdSet) {
			ShimModelManager
					.removeReferenceNodeFromSharedMemoryCommunication_SS(ssId);
		}

		addressSpaceTreeViewer.setInput(rootArray);

		SHIMEditJFaceApplicationWindow.changeInputPanel(rootArray.get(0));
	}

	/**
	 * Re-Makes MasterSlaveBindingSet by according to inputed parameters.
	 * 
	 * @param sys
	 *            the current SystemConfiguration
	 * @param preferences
	 *            the current ShimPreferences
	 * @param monitor
	 *            the progress monitor
	 * @return the new Communications
	 */
	private void remakeMasterSlaveBindingSet(SystemConfiguration sys,
			ShimPreferences preferences, IProgressMonitor monitor) {
		boolean dontConnect = false;
		if (this.createAddressSpace.btnDontConnect.getSelection()) {
			dontConnect = true;
		}

		// Sets a MasterSlaveBinding to every SubSpace.
		List<AddressSpace> asList = sys.getAddressSpaceSet().getAddressSpace();
		int taskNum = 0;
		for (AddressSpace as : asList) {
			List<SubSpace> sslist = as.getSubSpace();
			taskNum += sslist.size();
		}

		if (taskNum > 0) {
			monitor.beginTask("Creates the MasterSlaveBindingSet.", taskNum);

			ShimComponentTree scTree = new ShimComponentTree(sys);

			// set MasterSlaveBinding to every SubSpace
			List<MasterComponent> mcList = ShimModelManager
					.makeMasterComponentList(sys.getComponentSet());
			List<SlaveComponent> slcList = ShimModelManager
					.makeSlaveComponentList(sys.getComponentSet());
			if (mcList.isEmpty() || slcList.isEmpty()) {
				monitor.worked(taskNum);
				return;
			}

			for (AddressSpace as : asList) {
				List<SubSpace> ssList = as.getSubSpace();
				for (SubSpace ss : ssList) {
					ShimModelManager.setMasterSlaveBindingToSubSpace(scTree,
							mcList, slcList, ss, dontConnect);
					monitor.worked(1);
				}
			}
		}
	}

	/**
	 * Sets the TreeViewer of AddressSpace.
	 * 
	 * @param tv
	 *            the TreeViewer of Components
	 */
	public void setAddressSpaceTreeViewer(TreeViewer addressSpaceTreeViewer) {
		this.addressSpaceTreeViewer = addressSpaceTreeViewer;
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#dispose()
	 */
	@Override
	public void dispose() {
		addressSpaceTreeViewer = null;

		createAddressSpace = null;
		editAsTreeWz = null;
		super.dispose();
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performCancel()
	 */
	@Override
	public boolean performCancel() {
		log.info(getWindowTitle() + " is canceled.");
		ShimPreferences.restoreOldInstance();
		return super.performCancel();
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#setContainer(org.eclipse.jface.wizard.IWizardContainer)
	 */
	@Override
	public void setContainer(IWizardContainer wizardContainer) {
		if (wizardContainer instanceof IPageChangeProvider) {
			final IPageChangeProvider pcp = (IPageChangeProvider) wizardContainer;
			pcp.addPageChangedListener(new IPageChangedListener() {
				@Override
				public void pageChanged(final PageChangedEvent event) {
					Object selectedPage = event.getSelectedPage();

					if (createAddressSpace.equals(selectedPage)) {
						createAddressSpace.update();
						if (editAsTreeWz.equals(getPreviousSelectedPage())) {
							// go from editAddressSpace to createAddressSpace
							editAsTreeWz.setRequiredToCreateData(true);
						}

					} else if (createAddressSpace
							.equals(getPreviousSelectedPage())
							&& editAsTreeWz.equals(selectedPage)) {
						// go from createAddressSpace to editAddressSpace
						editAsTreeWz.createAddressSpaceSet();
						editAsTreeWz.setRequiredToCreateData(false);

					}

					setPreviousSelectedPage(selectedPage);
				}
			});
		}
		super.setContainer(wizardContainer);
	}

	/**
	 * Sets the previous wizard page.
	 * 
	 * @param previousSelectedPage
	 *            the previous wizard page
	 */
	public void setPreviousSelectedPage(Object previousSelectedPage) {
		this.previousSelectedPage = previousSelectedPage;
	}

	/**
	 * Returns the previous wizard page.
	 * 
	 * @param previousSelectedPage
	 *            the previous wizard page
	 */
	public Object getPreviousSelectedPage() {
		return previousSelectedPage;
	}
}
