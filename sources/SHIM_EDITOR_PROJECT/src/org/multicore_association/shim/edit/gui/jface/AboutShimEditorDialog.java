/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.gui.jface;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.multicore_association.shim.edit.gui.common.CommonConstants;
import org.multicore_association.shim.edit.log.ShimLoggerUtil;

/**
 * Shows the information about SHIM Editor.
 */
public class AboutShimEditorDialog extends Dialog implements MouseListener,
		MouseMoveListener {

	private static final Logger log = ShimLoggerUtil
			.getLogger(AboutShimEditorDialog.class);

	public static final String TITLE = "About " + CommonConstants.API_NAME;

	private static final String NEW_LINE = "\n";
	private static final String VISIT_URL = "http://www.multicore-association.org/workgroup/shim.php";

	private StyledText textArea;
	private StyleRange urlRange;

	private Cursor handCursor;
	private Cursor arrowCursor;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 *            the parent shell, or null to create a top-level shell
	 */
	public AboutShimEditorDialog(Shell parentShell) {
		super(parentShell);

		handCursor = new Cursor(parentShell.getDisplay(), SWT.CURSOR_HAND);
		arrowCursor = new Cursor(parentShell.getDisplay(), SWT.CURSOR_ARROW);
	}

	/**
	 * Sets the title to shell.
	 * 
	 * @param newShell
	 *            the shell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(TITLE);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 *            parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Color systemColor = Display.getCurrent()
				.getSystemColor(SWT.COLOR_WHITE);

		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		Composite composite = new Composite(container, SWT.BORDER);
		composite.setBackground(systemColor);
		composite.setLayout(new FormLayout());
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1);
		gd_composite.heightHint = 200;
		gd_composite.grabExcessHorizontalSpace = true;
		composite.setLayoutData(gd_composite);

		// Creates text area.
		String dialogText = getDialogText();
		textArea = new StyledText(composite, SWT.NONE);
		textArea.setText(getDialogText());
		FormData fd_txtA = new FormData();
		fd_txtA.top = new FormAttachment(0, 20);
		fd_txtA.left = new FormAttachment(0, 20);
		textArea.setLayoutData(fd_txtA);
		textArea.setEditable(false);

		// Sets blue colot to url text
		int index = dialogText.indexOf(VISIT_URL);
		urlRange = new StyleRange();
		urlRange.start = index;
		urlRange.length = VISIT_URL.length();
		urlRange.foreground = Display.getCurrent().getSystemColor(
				SWT.COLOR_BLUE);
		textArea.setStyleRange(urlRange);
		textArea.addMouseMoveListener(this);
		textArea.addMouseListener(this);

		composite.setFocus();

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(600, 300);
	}

	/**
	 * Returns the dialog text.
	 * 
	 * @return the dialog text
	 */
	private String getDialogText() {
		StringBuilder builder = new StringBuilder();
		builder.append(CommonConstants.API_NAME);
		builder.append(NEW_LINE);

		builder.append(NEW_LINE);

		builder.append("Version: ");
		builder.append(CommonConstants.EDITOR_VERSION);
		builder.append(NEW_LINE);

		builder.append(NEW_LINE);

		// builder.append(COPY_RIGHT);
		// builder.append(NEW_LINE);
		builder.append("Visit ");
		builder.append(VISIT_URL);

		return builder.toString();
	}

	/**
	 * whether the point is in link text range or not.
	 * 
	 * @param x
	 * @param y
	 * @return Returns true if the point is in link text, and false otherwise.
	 */
	private boolean isInLinkRange(int x, int y) {
		Point p = new Point(x, y);
		try {
			int offset = textArea.getOffsetAtLocation(p);
			if (urlRange.start <= offset
					&& offset < (urlRange.start + urlRange.length)) {
				return true;
			}
		} catch (Exception ex) {
		}
		return false;
	}

	/**
	 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseMove(MouseEvent event) {
		if (isInLinkRange(event.x, event.y)) {
			textArea.setCursor(handCursor);
		} else {
			textArea.setCursor(arrowCursor);
		}
	}

	/**
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDoubleClick(MouseEvent event) {
		// NOOP
	}

	/**
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDown(MouseEvent event) {
		if (isInLinkRange(event.x, event.y)) {
			try {
				java.awt.Desktop.getDesktop().browse(
						new java.net.URI(VISIT_URL));
				textArea.dragDetect(event);
			} catch (Exception e) {
				log.log(Level.WARNING, "Link error", e);
			}
		}
	}

	/**
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseUp(MouseEvent event) {
		// NOOP
	}

}
