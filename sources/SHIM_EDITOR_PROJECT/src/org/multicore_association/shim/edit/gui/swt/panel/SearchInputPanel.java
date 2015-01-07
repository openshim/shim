/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.swt.panel;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.multicore_association.shim.edit.gui.jface.SHIMEditJFaceApplicationWindow;
import org.multicore_association.shim.edit.gui.jface.SearchableTreeViewer;
import org.multicore_association.shim.edit.gui.jface.ShimAddressSpaceTreeViewer;
import org.multicore_association.shim.edit.gui.jface.ShimCommunicationTreeViewer;
import org.multicore_association.shim.edit.gui.jface.ShimComponentTreeViewer;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.search.SearchOptions;
import org.multicore_association.shim.edit.model.search.ShimSearchResult;

import swing2swt.layout.BorderLayout;

/**
 * An InputPanel implementation to search for SHIM Data.
 */
public class SearchInputPanel extends InputPanelBase {

	private static final Logger log = ShimLoggerUtil
			.getLogger(SearchInputPanel.class);

	private static final Pattern COMMA = Pattern.compile(",");

	private static final String[] COLUMNS = new String[] { "type", "name",
			"match property", "property value", "parent" };

	private static String SEARCH_RESULT_LABEL_BASE = "Search Result:";
	private static String REPLACE_RESULT_LABEL_BASE = "Replace Result:";

	private final int COLUMN_WIDTH = 150;

	private SearchableTreeViewer shimAddressSpaceTreeViewer;
	private SearchableTreeViewer shimComponentTreeViewer;
	private SearchableTreeViewer shimCommunicationTreeViewer;

	private SearchResultTableSorter sorter;

	private Text textSeachText;
	private Text textReplaceWith;
	private Text textElements;
	private Text textProps;

	private Button btnWholeWord;
	private Button btnCaseSensitive;
	private Button btnRegularExpressions;
	private Button btnExcludeReference;

	private Button btnSearch;
	private Button btnReplaceWith;
	private Button btnReplaceAll;

	private Label lblSearchResult;
	private TableViewer tableViewer;
	private Table table;

	private String currentSearchText;

	/**
	 * Constructs a new instance of SearchInputPanel.
	 * 
	 * @param parent
	 *            parent composite
	 * @param style
	 *            SWT style bits
	 */
	public SearchInputPanel(Composite parent, int style) {
		super(parent, style);

		setLblTitleText("Search");

		KeyListener keyListener = new PerformSearchKeyListener();

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(1, true));

		createSearchTextArea(composite, keyListener);

		createFilterTextArea(composite, keyListener);

		createSearchOptionGroup(composite);

		createButtonArea(composite);

		createSearchResultTable(composite);

		// To reset the Apply Button and Text of error message.
		btnApply.setVisible(false);
		textErrorMassage.setVisible(true);
	}

	/**
	 * Creates the composite contains the text fields to search and replace.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param keyListener
	 *            the KeyListener
	 */
	private void createSearchTextArea(Composite parent, KeyListener keyListener) {
		Composite compositeSeachText = new Composite(parent, SWT.NONE);
		compositeSeachText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false, 1, 1));
		compositeSeachText.setLayout(new GridLayout(2, false));

		// search text
		Label lbl_SeachText = new Label(compositeSeachText, SWT.NONE);
		GridData gd_lbl_SeachText = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_lbl_SeachText.widthHint = 60;
		lbl_SeachText.setLayoutData(gd_lbl_SeachText);
		lbl_SeachText.setText("Find:");

		textSeachText = new Text(compositeSeachText, SWT.BORDER);
		GridData gd_textSeachText = new GridData(SWT.LEFT, SWT.CENTER, true,
				true, 1, 1);
		gd_textSeachText.widthHint = 450;
		textSeachText.setLayoutData(gd_textSeachText);
		textSeachText.addModifyListener(new ModifyListener() {
			/**
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent event) {
				String text = ((Text) event.widget).getText();
				btnSearch.setEnabled(!text.isEmpty());
			}
		});
		textSeachText.addKeyListener(keyListener);

		// replace text
		Label lblReplaceWith = new Label(compositeSeachText, SWT.NONE);
		lblReplaceWith.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblReplaceWith.setText("Replace With:");

		textReplaceWith = new Text(compositeSeachText, SWT.BORDER);
		GridData gd_text_1 = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1,
				1);
		gd_text_1.widthHint = 450;
		textReplaceWith.setLayoutData(gd_text_1);
		textReplaceWith.addModifyListener(new ModifyListener() {
			/**
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent event) {
				confirmBtnReplaceAllEnabled();
			}
		});

		textReplaceWith.addKeyListener(new KeyListener() {
			/**
			 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
			 */
			@Override
			public void keyReleased(KeyEvent event) {
				// NOOP
			}

			/**
			 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
			 */
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.character == SWT.CR && btnReplaceAll.isEnabled()) {
					performReplaceAll();
				}
			}
		});
	}

	/**
	 * Creates the composite contains the text fields to filter from the search
	 * results.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param keyListener
	 *            the KeyListener
	 */
	private void createFilterTextArea(Composite parent, KeyListener keyListener) {
		Composite compositeSearchRange = new Composite(parent, SWT.NONE);
		compositeSearchRange.setLayout(new GridLayout(4, false));
		compositeSearchRange.setLayoutData(new GridData(SWT.LEFT, SWT.FILL,
				false, false, 1, 1));

		Label lblElements = new Label(compositeSearchRange, SWT.NONE);
		lblElements.setText("Element Filter:");

		textElements = new Text(compositeSearchRange, SWT.BORDER);
		GridData gd_text_2 = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_text_2.widthHint = 220;
		textElements.setLayoutData(gd_text_2);
		textElements
				.setToolTipText("Input the element to filter from the search results. If input is empty, filters nothing.");
		textElements.addKeyListener(keyListener);

		Label lblProps = new Label(compositeSearchRange, SWT.RIGHT);
		GridData gd_lblProps = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_lblProps.widthHint = 120;
		lblProps.setLayoutData(gd_lblProps);
		lblProps.setText("Property Filter:");

		textProps = new Text(compositeSearchRange, SWT.BORDER);
		GridData gd_text_3 = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_text_3.widthHint = 220;
		textProps.setLayoutData(gd_text_3);
		textProps
				.setToolTipText("Input the property of element to filter from the search results. If input is empty, filters nothing.");
		textProps.addKeyListener(keyListener);
	}

	/**
	 * Creates the composite of the search option group.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createSearchOptionGroup(Composite parent) {
		Group grpOptions = new Group(parent, SWT.NONE);
		GridData gd_grpOptions = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gd_grpOptions.widthHint = 404;
		grpOptions.setLayoutData(gd_grpOptions);
		grpOptions.setText("Options");

		btnWholeWord = new Button(grpOptions, SWT.CHECK);
		btnWholeWord.setBounds(10, 31, 102, 18);
		btnWholeWord.setText("Whole word");

		btnCaseSensitive = new Button(grpOptions, SWT.CHECK);
		btnCaseSensitive.setBounds(113, 31, 102, 18);
		btnCaseSensitive.setText("Case sensitive");

		btnRegularExpressions = new Button(grpOptions, SWT.CHECK);
		btnRegularExpressions.setBounds(226, 31, 150, 18);
		btnRegularExpressions.setText("Regular expressions");
		btnRegularExpressions.addSelectionListener(new SelectionListener() {

			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (btnRegularExpressions.getSelection()) {
					btnWholeWord.setSelection(false);
				}
				btnWholeWord.setEnabled(!btnRegularExpressions.getSelection());
			}

			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				// NOOP
			}
		});

		btnExcludeReference = new Button(grpOptions, SWT.CHECK);
		btnExcludeReference.setBounds(376, 31, 150, 18);
		btnExcludeReference.setText("Exclude reference");
		btnExcludeReference.setSelection(true);
		btnExcludeReference
				.setToolTipText("do not search ID reference properties");
		btnExcludeReference.addSelectionListener(new SelectionAdapter() {
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				confirmBtnReplaceWithEnabled();
				confirmBtnReplaceAllEnabled();
			}
		});
	}

	/**
	 * Creates the button to search and repalce.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createButtonArea(Composite parent) {
		Composite compositeButtons = new Composite(parent, SWT.NONE);
		compositeButtons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false, 1, 1));
		GridLayout gl_compositeButtons = new GridLayout(3, false);
		compositeButtons.setLayout(gl_compositeButtons);

		// search button
		btnSearch = new Button(compositeButtons, SWT.NONE);
		GridData gd_btnSearch = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_btnSearch.widthHint = 100;
		btnSearch.setLayoutData(gd_btnSearch);
		btnSearch.setText("Search");
		btnSearch.setEnabled(false);
		btnSearch.addSelectionListener(new SelectionListener() {

			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				performSearch();
			}

			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				// NOOP
			}
		});

		// replace with button
		btnReplaceWith = new Button(compositeButtons, SWT.NONE);
		GridData gd_btnReplaceWith = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_btnReplaceWith.widthHint = 100;
		btnReplaceWith.setLayoutData(gd_btnReplaceWith);
		btnReplaceWith.setText("Replace");
		btnReplaceWith.setEnabled(false);
		btnReplaceWith.addSelectionListener(new SelectionListener() {

			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				String replaceText = textReplaceWith.getText();

				TableItem[] selection = table.getSelection();
				for (TableItem item : selection) {
					ShimSearchResult result = (ShimSearchResult) item.getData();
					result.replace(currentSearchText, replaceText);
				}

				tableViewer.refresh();
			}

			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				// NOOP
			}
		});

		// replace all button
		btnReplaceAll = new Button(compositeButtons, SWT.NONE);
		GridData gd_btnReplaceAll = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_btnReplaceAll.widthHint = 100;
		btnReplaceAll.setLayoutData(gd_btnReplaceAll);
		btnReplaceAll.setText("Replace All");
		btnReplaceAll.setEnabled(false);
		btnReplaceAll.addSelectionListener(new SelectionListener() {

			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				performReplaceAll();
			}

			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				// NOOP
			}
		});

	}

	/**
	 * Creates the composite which displays the search result on the list.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createSearchResultTable(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		composite.setLayout(new GridLayout(1, true));

		lblSearchResult = new Label(composite, SWT.NONE);
		GridData gd_lblSearchResult = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gd_lblSearchResult.widthHint = 100;
		lblSearchResult.setLayoutData(gd_lblSearchResult);
		lblSearchResult.setText(SEARCH_RESULT_LABEL_BASE);

		Composite composite_result = new Composite(composite, SWT.NONE);
		composite_result.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		TableColumnLayout tcl_composite = new TableColumnLayout();
		composite_result.setLayout(tcl_composite);

		tableViewer = new TableViewer(composite_result, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		sorter = new SearchResultTableSorter();

		for (String colName : COLUMNS) {
			TableViewerColumn tableViewerColumn = new TableViewerColumn(
					tableViewer, SWT.NONE);
			TableColumn tblclmn = tableViewerColumn.getColumn();
			tblclmn.setText(colName);
			tcl_composite.setColumnData(tblclmn, new ColumnPixelData(
					COLUMN_WIDTH, true, true));
			tblclmn.addSelectionListener(new SelectionAdapter() {

				/**
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent event) {
					TableColumn column = (TableColumn) event.getSource();
					sorter.setFieldName(column.getText());
					tableViewer.refresh();
				}
			});
		}

		tableViewer.setLabelProvider(new SearchResultLabelProvider());
		tableViewer.setContentProvider(new SearchResultContentProvider());

		tableViewer.setInput(new ArrayList<ShimSearchResult>());

		table.addSelectionListener(new SelectionListener() {

			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				confirmBtnReplaceWithEnabled();
			}

			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				TableItem[] selection = table.getSelection();
				if (selection.length > 0) {
					ShimSearchResult data = (ShimSearchResult) selection[0]
							.getData();
					SHIMEditJFaceApplicationWindow.changeInputPanel(data
							.getData());
				}
			}
		});

		tableViewer.setSorter(sorter);
	}

	/**
	 * Confirms whether the 'Replace With' button is enabled or not.
	 */
	private void confirmBtnReplaceWithEnabled() {
		TableItem[] selection = tableViewer.getTable().getSelection();
		btnReplaceWith.setEnabled(selection.length > 0
				&& !textReplaceWith.getText().isEmpty()
				&& btnExcludeReference.getSelection());
	}

	/**
	 * Confirms whether the 'Replace All' button is enabled or not.
	 */
	private void confirmBtnReplaceAllEnabled() {
		btnReplaceAll.setEnabled(!textSeachText.getText().isEmpty()
				&& !textReplaceWith.getText().isEmpty()
				&& btnExcludeReference.getSelection());
	}

	/**
	 * Defines an action of clicking on search button.
	 */
	private void performSearch() {
		final String searchText = textSeachText.getText();

		boolean isCaseSensitive = btnCaseSensitive.getSelection();
		boolean isWholeWord = btnWholeWord.getSelection();
		boolean isRegExp = btnRegularExpressions.getSelection();
		boolean doNotSearchIdRef = btnExcludeReference.getSelection();

		final SearchOptions options = new SearchOptions(isCaseSensitive,
				isWholeWord, isRegExp, doNotSearchIdRef);

		String elements = textElements.getText();
		if (!elements.isEmpty()) {
			String[] elementsArray = COMMA.split(elements);
			for (String element : elementsArray) {
				options.addElementFilter(element);
			}
		}

		String properties = textProps.getText();
		if (!properties.isEmpty()) {
			String[] propertiesArray = COMMA.split(properties);
			for (String property : propertiesArray) {
				options.addPropertyFilter(property);
			}
		}

		final List<ShimSearchResult> searchResultList = new ArrayList<ShimSearchResult>();

		try {
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(
					getComposite_1().getShell());
			progressDialog.run(true, true, new IRunnableWithProgress() {

				/**
				 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
				 */
				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {

					monitor.beginTask("search start ", 3);

					if (shimComponentTreeViewer != null) {
						monitor.subTask("search ComponentSet");
						List<ShimSearchResult> tmpResult = shimComponentTreeViewer
								.search(searchText, options);
						searchResultList.addAll(tmpResult);
					}
					monitor.worked(1);
					if (monitor.isCanceled()) {
						return;
					}

					if (shimAddressSpaceTreeViewer != null) {
						monitor.subTask("search AddressSpaceSet");
						List<ShimSearchResult> tmpResult = shimAddressSpaceTreeViewer
								.search(searchText, options);
						searchResultList.addAll(tmpResult);
					}
					monitor.worked(1);
					if (monitor.isCanceled()) {
						return;
					}

					if (shimCommunicationTreeViewer != null) {
						monitor.subTask("search CommunicationSet");
						List<ShimSearchResult> tmpResult = shimCommunicationTreeViewer
								.search(searchText, options);
						searchResultList.addAll(tmpResult);
					}
					monitor.worked(1);
					if (monitor.isCanceled()) {
						return;
					}
				}

			});

			tableViewer.setInput(searchResultList);

			// creates search result log.
			StringBuilder logBuilder = new StringBuilder();
			if (isWholeWord) {
				logBuilder.append("Whole Word ");
			} else if (isRegExp) {
				logBuilder.append("Regular Expressions ");
			}
			logBuilder.append("Search Result");
			if (isCaseSensitive) {
				logBuilder.append("(Case Sensitive)");
			}
			logBuilder.append(" : text=");
			logBuilder.append(searchText);
			logBuilder.append(", number of hits=");
			logBuilder.append(searchResultList.size());

			log.info(logBuilder.toString());

			if (searchResultList.isEmpty()) {
				lblSearchResult.setText(SEARCH_RESULT_LABEL_BASE);
				textErrorMassage.setText("\'" + searchText + "\' - 0 matches.");

			} else {
				lblSearchResult.setText(SEARCH_RESULT_LABEL_BASE + " \'"
						+ searchText + "\' - " + searchResultList.size()
						+ " matches.");
				clearErrorMessage();
			}

			this.currentSearchText = searchText;
			tableViewer.refresh();

		} catch (Exception e) {
			log.log(Level.SEVERE, "Fail to search.", e);
		}
	}

	/**
	 * Defines an action of clicking on replace all button.
	 */
	private void performReplaceAll() {
		final String searchText = textSeachText.getText();
		final String replaceText = textReplaceWith.getText();

		performSearch();

		@SuppressWarnings("unchecked")
		List<ShimSearchResult> inputs = (List<ShimSearchResult>) tableViewer
				.getInput();
		if (!inputs.isEmpty()) {
			for (ShimSearchResult input : inputs) {
				input.replace(searchText, replaceText);
			}
			tableViewer.refresh();

			lblSearchResult.setText(REPLACE_RESULT_LABEL_BASE + " replaces \'"
					+ searchText + "\' with \'" + replaceText + "\' - "
					+ inputs.size() + " matches replaced.");
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#applyChange()
	 */
	@Override
	protected void applyChange() {
		// NOOP
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		if (input instanceof ShimAddressSpaceTreeViewer) {
			shimAddressSpaceTreeViewer = (ShimAddressSpaceTreeViewer) input;
		} else if (input instanceof ShimComponentTreeViewer) {
			shimComponentTreeViewer = (ShimComponentTreeViewer) input;
		} else if (input instanceof ShimCommunicationTreeViewer) {
			shimCommunicationTreeViewer = (ShimCommunicationTreeViewer) input;
		}
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getInput()
	 */
	@Override
	public Object getInput() {
		return null;
	}

	/**
	 * @see org.multicore_association.shim.edit.gui.swt.panel.InputPanelBase#getApiClass()
	 */
	@Override
	public Class<?> getApiClass() {
		return null;
	}

	/**
	 * Sets an empty string to the search text widget.
	 */
	public void clearText() {
		textSeachText.setText("");
		btnSearch.setEnabled(false);
	}

	/**
	 * Sets an empty list to the search result table.
	 */
	public void clearSearchResult() {
		lblSearchResult.setText(SEARCH_RESULT_LABEL_BASE);
		tableViewer.setInput(new ArrayList<ShimSearchResult>());
	}

	/**
	 * A LabelProvider implementation for SearchResult.
	 */
	class SearchResultLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
		 *      int)
		 */
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
		 *      int)
		 */
		@Override
		public String getColumnText(Object element, int columnIndex) {
			ShimSearchResult result = (ShimSearchResult) element;
			switch (columnIndex) {
			case 0:
				return result.getType();

			case 1:
				return result.getName();

			case 2:
				return result.getMatchProperty();

			case 3:
				return result.getPropertyValueStr();

			case 4:
				String parentName = result.getParentName();
				if (parentName == null) {
					return "-";
				} else {
					return parentName;
				}

			default:
				break;
			}
			return "";
		}
	}

	/**
	 * A IStructuredContentProvider implementation for SearchResult.
	 */
	class SearchResultContentProvider implements IStructuredContentProvider {

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		@Override
		public void dispose() {
			// NOOP
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// NOOP
		}

		/**
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement) {
			@SuppressWarnings("unchecked")
			List<ShimSearchResult> sr = ((List<ShimSearchResult>) inputElement);
			return sr.toArray();
		}
	}

	/**
	 * A KeyListener implementation for search.
	 */
	class PerformSearchKeyListener implements KeyListener {

		/**
		 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
		 */
		@Override
		public void keyReleased(KeyEvent event) {
			// NOOP
		}

		/**
		 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent event) {
			if (event.character == SWT.CR && !textSeachText.getText().isEmpty()) {
				performSearch();
			}
		}
	}

	/**
	 * A KeyListener implementation for sort.
	 */
	class SearchResultTableSorter extends ViewerSorter {

		private HashMap<String, Integer> sortOrderSet = new HashMap<String, Integer>();

		private String fieldName;

		/**
		 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			int compareTo = 0;
			if (fieldName != null) {
				ShimSearchResult r1 = (ShimSearchResult) e1;
				ShimSearchResult r2 = (ShimSearchResult) e2;

				if (COLUMNS[0].equals(fieldName)) {
					compareTo = r1.getType().compareTo(r2.getType());

				} else if (COLUMNS[1].equals(fieldName)) {
					compareTo = r1.getName().compareTo(r2.getName());

				} else if (COLUMNS[2].equals(fieldName)) {
					compareTo = r1.getMatchProperty().compareTo(
							r2.getMatchProperty());

				} else if (COLUMNS[3].equals(fieldName)) {
					compareTo = r1.getPropertyValueStr().compareTo(
							r2.getPropertyValueStr());

				} else if (COLUMNS[4].equals(fieldName)) {
					String parentName1 = r1.getParentName() == null ? "" : r1
							.getParentName();
					String parentName2 = r2.getParentName() == null ? "" : r2
							.getParentName();
					compareTo = parentName1.compareTo(parentName2);

				}

				Integer sortOrder = sortOrderSet.get(fieldName);
				compareTo = compareTo * sortOrder;
			}

			return compareTo;
		}

		/**
		 * Sets the field name of the parent element.
		 * 
		 * @param fieldName
		 *            the field name of the parent element
		 */
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;

			Integer sortOrder = sortOrderSet.get(fieldName);
			if (sortOrder == null) {
				sortOrder = -1;
				sortOrderSet.put(fieldName, sortOrder);
			}

			sortOrderSet.put(fieldName, sortOrder * -1);
		}
	}
}
