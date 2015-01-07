/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlType;

import org.multicore_association.shim.edit.model.ReflectionUtils;

/**
 * Logging utility.
 */
public class ShimLoggerUtil {

	static {
		File file = new File("conf/shimeditor-logging.properties");
		if (file.exists()) {
			try {
				readConfig(new FileInputStream(file));
			} catch (Exception e) {
			}
			// If the custom property file exists, loads it.
			getLogger(ShimLoggerUtil.class)
					.info("Load logging properties. : Path="
							+ file.getAbsoluteFile());
		} else {
			// If the custom property file does not exist, loads default
			// property file.
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			InputStream stream = loader
					.getResourceAsStream("conf/shimeditor-default-logging.properties");
			try {
				readConfig(stream);
			} catch (Exception e) {
			}

			getLogger(ShimLoggerUtil.class).info(
					"Load default logging properties.");
		}
	}

	/**
	 * Reads logging property file.<br>
	 * If you will use FileHandler and configure FileHandler.pattern, makes
	 * parent directory of log file.
	 * 
	 * @param stream
	 * @throws IOException
	 */
	protected static void readConfig(InputStream stream) throws IOException {
		LogManager manager = LogManager.getLogManager();
		manager.readConfiguration(stream);

		// And creates parent directories.
		String pattern = manager.getProperty(FileHandler.class.getName()
				+ ".pattern");
		if (pattern != null && !pattern.isEmpty()) {
			int lastIndexOf = pattern.lastIndexOf("/");
			if (lastIndexOf > 0) {
				String substring = pattern.substring(0, lastIndexOf);
				File dir = new File(substring);
				if (!dir.exists()) {
					dir.mkdirs();
				}
			}
		}
	}

	/**
	 * Returns a logger for the specified class.
	 * 
	 * @param claz
	 *            the class that needs a logger
	 * @return the logger for the specified class
	 */
	public static Logger getLogger(Class<?> claz) {
		return Logger.getLogger(claz.getName());
	}

	/**
	 * Returns the element name of the specified class.
	 * 
	 * @param claz
	 *            the class which has XmlType annotation
	 * @return the element name of the specified class
	 */
	public static String getElementName(Class<?> claz) {
		try {
			XmlType annotation = claz.getAnnotation(XmlType.class);
			if (annotation.name() != null) {
				return annotation.name();
			}
		} catch (Exception e) {
		}
		return claz.getSimpleName();
	}

	/**
	 * Returns the name attribute of the element.
	 * 
	 * @param element
	 *            the element which may have name attribute
	 * @return the name attribute of the element
	 */
	public static String getNameProperty(Object element) {
		String name = ReflectionUtils.getNameProperty(element);
		if (name == null) {
			return getElementName(element.getClass());
		} else {
			return name;
		}
	}

}
