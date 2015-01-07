/*
 * Copyright (c) 2014 eSOL Co.,Ltd. and Nagoya University
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.ccf.sampleapp;
import org.multicore_association.ccf.api.Configuration;
import org.multicore_association.ccf.api.ConfigurationSet;
import org.multicore_association.ccf.api.FormType;
import org.multicore_association.ccf.api.Item;


public class CCF_ModelManager {
	
	static ConfigurationSet root = null;
	
	public static void setConfigurationSet(ConfigurationSet _root) {
		root = _root;
	}
	public static ConfigurationSet getConfigurationSet() {
		return root;
	}
	
	public static void printCCF() {
		
		printConfigurationSet(root);
	}
	
	public static void printConfigurationSet(ConfigurationSet cfset) {
		System.out.println("[ ConfigurationSet name="+cfset.getName()+" ]");
		for(Configuration conf : cfset.getConfiguration()) {
			System.out.println("               + name="+conf.getName()+"]");
			printConfiguration(conf);
		}
	}
	
	
	public static void printConfiguration(Configuration conf) {
		System.out.println(" [Configuration]");
		System.out.println("  +------- name="+conf.getName());
		System.out.println("  +------- formType="+conf.getFormType());
		if(conf.getMax() != null) {
			System.out.println("  +------- max="+conf.getMax());
		}
		if(conf.getMin() != null) {
			System.out.println("  +------- min="+conf.getMin());
		}
		if(conf.getFormType() == FormType.SELECT) {
			for(Item item : conf.getItem()) {
				System.out.println("     item: key="+item.getKey()+": value="+item.getValue());
			}
		}
		
		if(conf.getFormType() == FormType.INTEGER) {
		    String valstr = conf.getItem().get(0).getValue();
		    Integer val = Integer.parseInt(valstr);
		}
		
		for(Configuration conf1 : root.getConfiguration()) {
			System.out.println("               + name="+conf1.getName()+"]");
		}
	}
	
	public static void printItem(Configuration conf) {
		System.out.println("     [Item]");
		for(Item item : conf.getItem()) {
			System.out.println("       [Key="+item.getKey()+"]");
			System.out.println("       [Value="+item.getValue()+"]");
			for(Configuration cf1 : item.getConfiguration()) {
				printConfiguration();
			}
		}
	}
	
	public static void printConfiguration(){
		
	}

}
