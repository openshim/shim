/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.shim.edit.binding;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;

import org.eclipse.jface.viewers.TreeViewer;
import org.multicore_association.shim.api.AddressSpaceSet;
import org.multicore_association.shim.api.CommunicationSet;
import org.multicore_association.shim.api.ComponentSet;
import org.multicore_association.shim.api.MasterComponent;
import org.multicore_association.shim.api.SlaveComponent;
import org.multicore_association.shim.api.SystemConfiguration;
import org.multicore_association.shim.edit.model.ShimModelAdapter;
import org.multicore_association.shim.edit.model.ShimModelManager;

/**
 * A class that implements methods to load SHIM Data.
 */
public class ShimDataLoader {

	String path = null;
	public SystemConfiguration system = null;

	public TreeViewer componentTreeViewer;
	public TreeViewer addressSpaceTreeViewer;
	public TreeViewer communicationTreeViewer;

	/**
	 * Constructs a new instance of ShimDataLoader.
	 * 
	 * @param system
	 *            an instance of SystemConfiguration to load
	 * @param path
	 *            SHIM Data XML file Path
	 */
	public ShimDataLoader(SystemConfiguration system, String path) {
		this.path = path;
		this.system = system;
	}

	/**
	 * Loads SHIM Data XML file.
	 * 
	 * @return returnCode - If success to load, return 1. If fail to load,
	 *         return -1.
	 * @throws Exception
	 */
	public int Load() throws Exception {

		FileOutputStream fos = null;

		ValidationEventCollector vec = new ValidationEventCollector();
		UnmarshalException ue = null;
		try {

			InputStream xmlInputStream = new FileInputStream(path);

			JAXBContext jc = ShimModelAdapter.getJAXBContext();

			Unmarshaller u = jc.createUnmarshaller();

			// When using the validation
			Schema schema = ShimModelAdapter.getShimSchema();
			u.setSchema(schema);
			u.setEventHandler(vec);

			// Support for ID and IDREF.
			JAXBElement<?> element = (JAXBElement<?>) u
					.unmarshal(xmlInputStream);
			system = (SystemConfiguration) element.getValue();

			ShimModelManager.addSystemConfiguration(system);

			xmlInputStream.close();

			printShimData(system);

			ShimModelManager.addSystemConfiguration(system);

			ComponentSet cs = system.getComponentSet();
			ArrayList<Object> cslist = new ArrayList<Object>();
			cslist.add(cs);

			componentTreeViewer.setInput(cslist);

			ArrayList<Object> aslist = new ArrayList<Object>();
			AddressSpaceSet addressSpaceSet = system.getAddressSpaceSet();
			if (addressSpaceSet != null) {
				aslist.add(addressSpaceSet);
				addressSpaceTreeViewer.setInput(aslist);
			}

			ArrayList<Object> comlist = new ArrayList<Object>();
			CommunicationSet communicationSet = system.getCommunicationSet();
			if (communicationSet != null) {
				comlist.add(communicationSet);
				communicationTreeViewer.setInput(comlist);
			}

			return 1;

		} catch (UnmarshalException e) {
			ue = e;

		} catch (Exception e) {
			throw e;

		} finally {

			// When using the validation
			if (vec != null && vec.hasEvents()) {
				for (ValidationEvent ve : vec.getEvents()) {
					ValidationEventLocator vel = ve.getLocator();
					String msg = "[line:" + vel.getLineNumber() + ", column:"
							+ vel.getColumnNumber() + "] " + ve.getMessage();
					throw new Exception(msg);
				}
			}

			if (ue != null) {
				if (ue.getLinkedException() == null) {
					throw ue;
				} else {
					throw (Exception) ue.getLinkedException();
				}
			}

			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
			}
		}
		return -1;
	}

	/**
	 * Prints the loaded SHIM Data to the standard output stream.
	 * 
	 * @param sys
	 *            the loaded SHIM Data
	 */
	private void printShimData(SystemConfiguration sys) {
		System.out.println("<Output Start>----------------");
		System.out.println("  name=" + sys.getName());
		printComponentSet(sys.getComponentSet(), 1);
		System.out.println("</Output End>----------------");

	}

	/**
	 * Prints the ComponentSet to the standard output stream.
	 * 
	 * @param cset
	 *            the ComponentSet to write
	 * @param level
	 *            non-negative level of cset
	 */
	private void printComponentSet(ComponentSet cset, int level) {
		if (cset == null) {
			System.out.println("<<No ComponentSet>>");
		}
		for (MasterComponent mc : cset.getMasterComponent()) {
			printMasterComponent(mc, level + 1);

		}
		for (SlaveComponent sc : cset.getSlaveComponent()) {
			printSlaveComponent(sc, level + 1);

		}
		for (ComponentSet subCset : cset.getComponentSet()) {
			printComponentSet(subCset, level + 1);
		}

	}

	/**
	 * Prints the MasterComponent to the standard output stream.
	 * 
	 * @param mc
	 *            the MasterComponent to print
	 * @param level
	 *            non-negative level of mc
	 */
	private void printMasterComponent(MasterComponent mc, int level) {
		System.out.println(sp(level) + "<MasterComponent>----------------");
		System.out.println(sp(level) + "  Name=" + mc.getName());
		System.out.println(sp(level) + "  Pid =" + mc.getPid());
		System.out.println(sp(level) + "  Arch=" + mc.getArch());
		System.out.println(sp(level) + "</MasterComponent>----------------");
	}

	/**
	 * Spaces according to level.
	 * 
	 * @param level
	 *            non-negative level of element
	 * @return space
	 */
	private String sp(int level) {
		String ss = "";
		for (int i = 0; i < level; i++) {
			ss = ss + "    ";
		}
		return ss;
	}

	/**
	 * Prints the SlaveComponent to the standard output stream.<br>
	 * <b>No operation now.</b>
	 * 
	 * @param sc
	 *            the SlaveComponent to print
	 * @param level
	 *            non-negative level of mc
	 */
	private void printSlaveComponent(SlaveComponent sc, int level) {
		// NOOP
	}

}
