/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.ccf.sampleapp;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

import org.multicore_association.ccf.api.ConfigurationSet;


public class CCFDataLoader {
	
	String path = null;
	public ConfigurationSet cfset = null;
	
	public CCFDataLoader(ConfigurationSet conf, String path) {
		this.path = path;
		this.cfset = cfset;
	}
	
	public int Load() throws UnmarshalException  {
		FileOutputStream fos = null;
		try {
			
		    InputStream xmlInputStream = new FileInputStream(path);  
		    JAXBContext jc = JAXBContext.newInstance(ConfigurationSet.class);  
		    Unmarshaller u = jc.createUnmarshaller();
		      
		    cfset = (ConfigurationSet) u.unmarshal(xmlInputStream);
		    CCF_ModelManager.root = cfset;
		    
		    xmlInputStream.close();  
		    printCCFData(cfset);
		    System.err.println(" ì«Ç›çûÇ›OK");
			
			return 1;
	
		} catch(javax.xml.bind.UnmarshalException e) {
			System.out.println("*UnmarshalException e.class="+e.getClass().getName());
			throw e;
			
//			Dialog dialog = new ErrorDialog(null, "Error", "Bad XML", null,1);
//			dialog.open();
//			return -1;
			
		} catch(JAXBException e) {
			System.out.println("JAXB Exception e.class="+e.getClass().getName());
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(fos != null)
					fos.close();
			} catch(IOException e) {
			}
		}
		return -1;
	}

	private void printCCFData(ConfigurationSet cfset) {
		
	    System.out.println("<Output Start>----------------");
	    System.out.println("  name="+cfset.getName());
	    //System.out.println("  clock="+sys.getClockFrequency().getClockValue());
	    System.out.println("</Output End>----------------");
		
	}

}
