/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * An implementation of Formatter for SHIM Editor.
 */
public class ShimLogFormatter extends Formatter {
	private final SimpleDateFormat sd = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss,SSS");

	/**
	 * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
	 */
	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		sb.append(sd.format(new Date()));
		sb.append(' ');

		sb.append(record.getLevel().toString().toUpperCase());
		sb.append(' ');
		sb.append(record.getLoggerName());
		sb.append(" - ");
		sb.append(record.getMessage());
		sb.append("\n");

		Throwable thrown = record.getThrown();
		if (thrown != null) {
			// stacktrace to string
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			thrown.printStackTrace(pw);
			pw.flush();
			
			sb.append(sw.toString());
			sb.append("\n");
		}

		return sb.toString();
	}

}
