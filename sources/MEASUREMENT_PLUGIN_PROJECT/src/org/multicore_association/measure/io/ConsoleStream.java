/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.io;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.multicore_association.measure.core.Activator;

/**
 * Class to add own console to the console view.
 */
public class ConsoleStream extends PrintStream {

	private static final String CONSOLE_TITLE = "Instruction Cycle Measurement Tool";

	private static ConsoleStream ins = null;
	private MessageConsoleStream stream = null;

	private String prefix = null;

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public static ConsoleStream getInstance() {
		if (ins == null) {
			ins = new ConsoleStream(System.out);
		}
		return ins;
	}

	public ConsoleStream(PrintStream out) {
		super(out);
	}

	public void initConsole() {

		MessageConsole console = new MessageConsole(
				CONSOLE_TITLE, Activator.PLUGIN_ID, null, true);
		stream = console.newMessageStream();
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();

		ArrayList<IConsole> list = new ArrayList<IConsole>();
		IConsole[] cnsl = manager.getConsoles();
		for (int i = 0; i < cnsl.length; i++) {
			String type = cnsl[i].getType();
			if (type == null || type.isEmpty()) {
				continue;
			}
			if (cnsl[i].getType().equals(Activator.PLUGIN_ID)) {
				list.add(cnsl[i]);
			}
		}
		if (!list.isEmpty()) {
			manager.removeConsoles(list.toArray(new IConsole[list.size()]));
		}

		manager.addConsoles(new IConsole[] { console });
		manager.showConsoleView(console);
	}

	@Override
	public void write(byte[] b) throws IOException {
		if (stream == null)
			return;
		stream.write(b);
	}

	@Override
	public void print(String s) {
		if (prefix != null) {
			stream.print(prefix + s + "\n");
		} else {
			stream.print(s);
		}
	}

	@Override
	public void println(String x) {
		if (prefix != null) {
			stream.println(prefix + x);
		} else {
			stream.println(x);
		}
	}

	@Override
	public void println() {
		if (prefix != null) {
			stream.println(prefix);
		} else {
			stream.println();
		}
	}
}
