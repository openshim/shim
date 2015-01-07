/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.api.Cache;
import org.multicore_association.shim.api.CacheCoherencyType;
import org.multicore_association.shim.api.CacheType;
import org.multicore_association.shim.api.LockDownType;
import org.multicore_association.shim.api.ObjectFactory;
import org.multicore_association.shim.api.SizeUnitType;
import org.multicore_association.shim.edit.gui.common.ComboFactory;
import org.multicore_association.shim.edit.gui.swt.viewer.ShimRefTableViewer;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.EnumUtil;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimModelManager;
import org.multicore_association.shim.edit.model.ShimObject;
import org.multicore_association.shim.edit.model.ShimObjectUtils;
import org.multicore_association.shim.edit.model.data.CacheRefForDisplay;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation for Cache.
 */
public class CacheInputPanel extends InputPanelBase {

	private static final Logger log = ShimLoggerUtil
			.getLogger(CacheInputPanel.class);

	private Text textName;
	private Text textNWay;
	private Text textLineSize;
	private Text textCacheSize;

	private Combo comboCacheType;
	private Combo comboLockDownType;
	private Combo comboCoherencyType;
	private Combo comboCacheSizeUnit;

	private Cache cache = new Cache();
	private ShimRefTableViewer cacheRefTableViewer;
	private Table table;
	private List<ShimObject> objectList = new ArrayList<ShimObject>();
	private List<ShimObject> cachetList = new ArrayList<ShimObject>();

	/**
	 * Constructs a new instance of CacheInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public CacheInputPanel(Composite parent, int style) {

		super(parent, SWT.NO_REDRAW_RESIZE);
		setLayout(new BorderLayout(0, 0));

		setLblTitleText("Cache");

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1,
				1));
		composite.setLayout(new GridLayout(3, false));

		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setText("Name");
		new Label(composite, SWT.NONE);

		textName = new Text(composite, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		lblNewLabel_3.setText("cacheType");
		new Label(composite, SWT.NONE);

		comboCacheType = new ComboFactory(composite).createCombo(
				CacheType.class,
				ShimModelAdapter.isRequired(getApiClass(), "cacheType"));
		comboCacheType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lbl_Coherency = new Label(composite, SWT.NONE);
		lbl_Coherency.setText("Coherency");
		new Label(composite, SWT.NONE);

		comboCoherencyType = new ComboFactory(composite).createCombo(
				CacheCoherencyType.class,
				ShimModelAdapter.isRequired(getApiClass(), "cacheCoherency"));
		comboCoherencyType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));

		Label lblNewLabel_5 = new Label(composite, SWT.NONE);
		lblNewLabel_5.setText("Size");
		new Label(composite, SWT.NONE);

		Composite composite_4 = new Composite(composite, SWT.NONE);
		composite_4.setLayout(new GridLayout(2, false));

		textCacheSize = new Text(composite_4, SWT.BORDER);
		textCacheSize.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1));

		comboCacheSizeUnit = new ComboFactory(composite_4).createCombo(
				SizeUnitType.class,
				ShimModelAdapter.isRequired(getApiClass(), "sizeUnit"));

		Label lblNewLabel_6 = new Label(composite, SWT.NONE);
		lblNewLabel_6.setText("nWay");
		new Label(composite, SWT.NONE);

		textNWay = new Text(composite, SWT.BORDER);
		textNWay.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Label lbl_lineSize = new Label(composite, SWT.NONE);
		lbl_lineSize.setText("lineSize (byte)");
		new Label(composite, SWT.NONE);

		textLineSize = new Text(composite, SWT.BORDER);
		textLineSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lbl_lockDownType = new Label(composite, SWT.NONE);
		lbl_lockDownType.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false,
				false, 1, 1));
		lbl_lockDownType.setText("LockDownType");
		new Label(composite, SWT.NONE);

		comboLockDownType = new ComboFactory(composite).createCombo(
				LockDownType.class,
				ShimModelAdapter.isRequired(getApiClass(), "lockDownType"));
		comboLockDownType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));

		Label lblCacheref = new Label(composite, SWT.NONE);
		lblCacheref.setText("CacheRef");
		new Label(composite, SWT.NONE);

		Composite composite_cachButton = new Composite(composite, SWT.NONE);
		composite_cachButton.setLayout(new GridLayout(2, false));

		Button btnNewButton = new Button(composite_cachButton, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				setErrorMessage(null);

				if (cachetList.size() == 0) {
					Object rootInstance = ShimModelManager.getCurrentScd();
					cachetList = ShimModelAdapter.getObjectsList(Cache.class,
							rootInstance);
				}

				int beforeSize = cachetList.size();
				for (int i = 0; i < cachetList.size(); i++) {
					Object obj = cachetList.get(i).getObj();
					if (!obj.equals(cache)) {
						ShimObject so = new ShimObject(obj);
						if (!ShimObjectUtils.contains(objectList, so)) { //
							objectList.add(so);
							cacheRefTableViewer.setInput(objectList);
							cacheRefTableViewer.refresh();
							break;
						}
					} else {
						beforeSize = beforeSize - 1;
					}
				}

				if (beforeSize == 0) {
					setErrorMessage("There is no cache to set.");
				} else if (beforeSize <= objectList.size()) {
					setErrorMessage("All of Caches have been set in CacheRef.");
				}
			}
		});
		btnNewButton.setText("Add");

		Button btnNewButton_1 = new Button(composite_cachButton, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				setErrorMessage(null);

				if (objectList.size() > 0) {
					StructuredSelection selection = (StructuredSelection) cacheRefTableViewer
							.getSelection();
					Object[] selectionArray = selection.toArray();
					List<Object> newObjects = new ArrayList<Object>();
					for (Object o : selectionArray) {
						ShimObject so = (ShimObject) o;
						int idx = objectList.indexOf(so);
						objectList.remove(so);
						cacheRefTableViewer.setInput(objectList);
						if (objectList.size() <= idx) {
							idx = objectList.size() - 1;
						}
						if (idx < 0) {
							idx = 0;
						}
						if (objectList.size() > 0) {
							newObjects.add(objectList.get(idx));
						}
					}
					StructuredSelection newSelection = new StructuredSelection(
							newObjects.toArray(new Object[0]));
					cacheRefTableViewer.setSelection(newSelection);
					cacheRefTableViewer.refresh();
				}
			}
		});
		btnNewButton_1.setText("Delete");

		Composite composite_cacheRef = new Composite(composite, SWT.NONE);
		composite_cacheRef.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, true, 3, 1));
		composite_cacheRef.setLayout(new GridLayout(1, true));

		cacheRefTableViewer = new ShimRefTableViewer(composite_cacheRef,
				SWT.FILL, this, "CacheRef");
		table = cacheRefTableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		cacheRefTableViewer.initTabeleViewer(
				new CacheRefForDisplay().getClass(), false);

		// Checking Required.
		textName.addModifyListener(new TextModifyListener("name"));
		textNWay.addModifyListener(new TextModifyListener("nWay"));
		textNWay.addModifyListener(new NumberModifyListener("nWay"));
		textLineSize.addModifyListener(new TextModifyListener("blockSize"));
		textLineSize.addModifyListener(new NumberModifyListener("blockSize"));
		textCacheSize.addModifyListener(new TextModifyListener("size"));
		textCacheSize.addModifyListener(new NumberModifyListener("size"));
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		cache = (Cache) input;

		textName.setText(cache.getName());

		Integer lineSize = cache.getLineSize();
		if (lineSize == null) {
			textLineSize.setText("");
		} else {
			textLineSize.setText(lineSize + "");
		}

		Integer nWay = cache.getNWay();
		if (nWay == null) {
			textNWay.setText("");
		} else {
			textNWay.setText(nWay + "");
		}

		textCacheSize.setText(cache.getSize() + "");

		comboCacheSizeUnit.select(cache.getSizeUnit().ordinal());
		comboCacheType.select(cache.getCacheType().ordinal());
		comboCoherencyType.select(cache.getCacheCoherency().ordinal());
		LockDownType lockDownType = cache.getLockDownType();
		if (lockDownType == null) {
			comboLockDownType.select(comboLockDownType.getItemCount() - 1);
		} else {
			comboLockDownType.select(lockDownType.ordinal());
		}

		cacheRefTableViewer.setCellEditors(cache);

		objectList.clear();
		List<JAXBElement<Object>> jaxbCacheRefList = cache.getCacheRef();
		for (JAXBElement<Object> element : jaxbCacheRefList) {
			ShimObject so = new ShimObject(element.getValue());
			objectList.add(so);
		}
		cacheRefTableViewer.setInput(objectList);
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		assert (cache != null);

		String str_LineSize = this.textLineSize.getText();
		if (isNullOrEmpty(str_LineSize)) {
			cache.setLineSize(null);
		} else {
			cache.setLineSize(Integer.parseInt(str_LineSize));
		}
		cache.setCacheCoherency(CacheCoherencyType
				.valueOf(this.comboCoherencyType.getText()));
		cache.setCacheType(CacheType.fromValue(this.comboCacheType.getText()));
		cache.setLockDownType(EnumUtil.getLockDownType(comboLockDownType
				.getSelectionIndex()));
		cache.setName(this.textName.getText());
		String str_nWay = this.textNWay.getText();
		if (isNullOrEmpty(str_nWay)) {
			cache.setNWay(null);
		} else {
			cache.setNWay(Integer.parseInt(str_nWay));
		}
		cache.setSize(Integer.parseInt(this.textCacheSize.getText()));
		cache.setSizeUnit(SizeUnitType.fromValue(this.comboCacheSizeUnit
				.getText()));

		// Display CacheRef.
		List<ShimObject> temp_objectList = new ArrayList<ShimObject>();
		for (ShimObject so : objectList) {
			if (!ShimObjectUtils.contains(temp_objectList, so)) {
				temp_objectList.add(so);
			}
		}
		List<JAXBElement<Object>> jaxbCacheRefList = cache.getCacheRef();
		ObjectFactory of = new ObjectFactory();
		jaxbCacheRefList.clear();
		for (ShimObject so : temp_objectList) {
			Object obj = so.getObj();
			JAXBElement<Object> element = of.createCacheCacheRef(obj);
			jaxbCacheRefList.add(element);
		}
		// Needs to call SetInput method.
		setInput(cache);

		log.finest("[applyChange] : Cache\n" + "  Name=" + cache.getName()
				+ "\n  LineSize=" + cache.getLineSize() + "\n  CacheCoherency="
				+ cache.getCacheCoherency() + "\n  CacheType="
				+ cache.getCacheType() + "\n  LockDownType="
				+ cache.getLockDownType() + "\n  NWay=" + cache.getNWay()
				+ "\n  Size=" + cache.getSize() + "\n  SizeUnit="
				+ cache.getSizeUnit());
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return cache.getClass();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		cache = new Cache();
		objectList.clear();
		cachetList.clear();
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return (Object) cache;
	}
}
