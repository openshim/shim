/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TreeItem;
import org.multicore_association.shim.edit.actions.CopySelectedItemAction;
import org.multicore_association.shim.edit.actions.CutSelectedItemAction;
import org.multicore_association.shim.edit.actions.DeleteSelectedItemAction;
import org.multicore_association.shim.edit.actions.PasteChildAction;
import org.multicore_association.shim.edit.gui.swt.viewer.ShimObjectDragAndDropListener;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;
import org.multicore_association.shim.edit.model.ReflectionUtils;
import org.multicore_association.shim.edit.model.search.PropertyCompareUtil;
import org.multicore_association.shim.edit.model.search.SearchOptions;
import org.multicore_association.shim.edit.model.search.ShimSearchResult;

/**
 * Abstract base implementation of TreeViewer which can search.
 */
public abstract class SearchableTreeViewer extends TreeViewer {

	private static final Logger log = ShimLoggerUtil
			.getLogger(SearchableTreeViewer.class);

	/**
	 * Constructs a new instance of SearchableTreeViewer.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	public SearchableTreeViewer(Composite parent) {
		super(parent);

		final TreeViewer viewer = this;
		getTree().addKeyListener(new KeyListener() {

			/**
			 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
			 */
			@Override
			public void keyReleased(KeyEvent e) {
				// NOOP
			}

			/**
			 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.keyCode == SWT.DEL && e.stateMask != SWT.CTRL)
						|| (e.keyCode == 'd' && e.stateMask == SWT.CTRL)) {
					// press DELET or CTRL+D key.
					DeleteSelectedItemAction delete = new DeleteSelectedItemAction(
							viewer);
					if (delete.isEnabled()) {
						delete.run();
					}
				} else if (e.keyCode == 'x' && e.stateMask == SWT.CTRL) {
					// press CTRL+C key.
					CutSelectedItemAction cut = new CutSelectedItemAction(
							viewer);
					if (cut.isEnabled()) {
						cut.run();
					}
				} else if (e.keyCode == 'c' && e.stateMask == SWT.CTRL) {
					// press CTRL+C key.
					CopySelectedItemAction copy = new CopySelectedItemAction(
							viewer);
					if (copy.isEnabled()) {
						copy.run();
					}
				} else if (e.keyCode == 'v' && e.stateMask == SWT.CTRL) {
					// press CTRL+V key.
					PasteChildAction paste = new PasteChildAction(viewer);
					if (paste.isEnabled()) {
						paste.run();
					}
				}
			}
		});

		int operations = DND.DROP_MOVE | DND.DROP_DEFAULT;
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		ShimObjectDragAndDropListener listener = new ShimObjectDragAndDropListener(
				viewer);

		addDragSupport(operations, types, listener);
		addDropSupport(operations, types, listener);
	}

	/**
	 * Finds and select the specified element.
	 * 
	 * @param element
	 *            the element which find from TreeViewer.
	 * @return Returns true if the element is found, and false otherwise.
	 */
	public boolean findAndSelect(Object element) {
		Object input = getInput();
		if (input instanceof List) {
			input = ((List<?>) input).get(0);
		}

		return findAndSelect(input, element);
	}

	/**
	 * Finds and select the specified element.
	 * 
	 * @param root
	 *            the starting point element to search
	 * @param element
	 *            the element which find from TreeViewer.
	 * @return Returns true if the element is found, and false otherwise.
	 */
	public boolean findAndSelect(Object root, Object element) {
		ITreeContentProvider provider = (ITreeContentProvider) getContentProvider();
		Set<Object> route = new HashSet<Object>();
		if (findElement(route, root, element, provider)) {
			route.remove(element);
			// If route is empty, element is root element.So no need to expand.
			if (!route.isEmpty()) {
				setExpandedElements(route.toArray());
			}

			TreeItem selectedTreeItem = findTreeItem(element);
			assert selectedTreeItem != null;

			setSelection(new StructuredSelection(element), true);
			getTree().showSelection();

			// send selection event to SelectionListner
			Event event = new Event();
			event.item = selectedTreeItem;
			getTree().notifyListeners(SWT.Selection, event);
			return true;
		}

		log.info("Search is failed. : element=" + element);
		return false;
	}

	/**
	 * Returns the TreeItem of the specified element.
	 * 
	 * @param element
	 *            the element to find
	 * @return the TreeItem
	 */
	public TreeItem findTreeItem(Object element) {
		return (TreeItem) findItem(element);
	}

	/**
	 * @param route
	 *            the set of parent object
	 * @param input
	 * 
	 * @param element
	 *            the element which find from TreeViewer.
	 * @param provider
	 *            ITreeContentProvider of TreeViewer
	 * @return Returns true if the element is found, and false otherwise.
	 */
	private static boolean findElement(Set<Object> route, Object input,
			Object element, ITreeContentProvider provider) {
		if (element.equals(input)) {
			route.add(input);
			return true;
		}
		Object[] children = provider.getChildren(input);
		if (children.length > 0) {
			for (Object child : children) {
				if (child.equals(input)) {
					continue;
				}
				if (findElement(route, child, element, provider)) {
					route.add(input);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Searches TreeViewer by the specified text.
	 * 
	 * @param searchText
	 *            the text to search
	 * @param isCaseSensitive
	 *            if true, case sensitive search was performed
	 * @param isWholeWord
	 *            if true, only whole words were searched
	 * @param isRegExp
	 *            if true, regular expression search was performed
	 * @return the list of search result
	 */
	public List<ShimSearchResult> search(String searchText,
			SearchOptions options) {
		List<ShimSearchResult> searchResultList = new ArrayList<ShimSearchResult>();
		ITreeContentProvider provider = (ITreeContentProvider) getContentProvider();
		Object input = getInput();
		if (input instanceof List) {
			input = ((List<?>) input).get(0);
		}

		searchObject(searchResultList, provider, input, searchText, options,
				null);

		return searchResultList;
	}

	/**
	 * Searches TreeViewer by the specified text.
	 * 
	 * @param searchResultList
	 * @param provider
	 * @param input
	 * @param searchText
	 * @param options
	 * @param parentName
	 */
	private void searchObject(List<ShimSearchResult> searchResultList,
			ITreeContentProvider provider, Object input, String searchText,
			SearchOptions options, String parentName) {
		List<String> matchProperties = PropertyCompareUtil.getMatchProperties(
				searchText, input, options);
		if (!matchProperties.isEmpty()) {
			for (String matchProperty : matchProperties) {
				searchResultList.add(new ShimSearchResult(input, matchProperty,
						parentName));
			}
		}

		Object[] children = provider.getChildren(input);
		if (children.length > 0) {
			String nameProperty = ReflectionUtils.getNameProperty(input);
			if (nameProperty == null) {
				nameProperty = parentName;
			}

			for (Object child : children) {
				if (child.equals(input)) {
					continue;
				}

				searchObject(searchResultList, provider, child, searchText,
						options, nameProperty);
			}
		}

	}
}
