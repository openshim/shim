/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package org.multicore_association.measure.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamBufferThread extends Thread {

	private InputStreamReader isr = null;
	private BufferedReader br = null;
	private PrintStream out = null;
	private boolean outputted = false;
	private boolean erroccured = false;
	private Pattern errptn = Pattern.compile("^Error:.*");

	public boolean isOutputted() {
		return outputted;
	}

	public boolean isErrOccured() {
		return erroccured;
	}

	public StreamBufferThread(InputStream stream, PrintStream out) {
		this.out = out;
		isr = new InputStreamReader(stream);
		br = new BufferedReader(isr);
	}

	public void abort() {
		try {
			br.close();
		} catch (IOException e) {
		}
		try {
			isr.close();
		} catch (IOException e) {
		}
	}

	@Override
	public void run() {
		String buff;
		try {
			while ((buff = br.readLine()) != null) {
				Matcher mat = errptn.matcher(buff);
				out.println(buff);
				outputted = true;
				if (mat.find()) {
					erroccured = true;
				}
			}
		} catch (IOException e) {
		} finally {
			try {
				br.close();
			} catch (IOException e) {
			}
			try {
				isr.close();
			} catch (IOException e) {
			}
		}
	}

}
