/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.ccf.sampleapp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CCF_HexIntegerValidator {
	
	Integer _min;
	Integer _max;
	
	CCF_HexIntegerValidator(Integer min, Integer max) {
		_min = min;
		_max = max;
	}
	
	String isValid(String value) {

		System.out.println("****** IntValidator.isValid:");
		Pattern pattern = Pattern.compile("^[-+]?[0-9a-fA-F]+$");
		Matcher matcher = pattern.matcher((String) value);

		if (matcher.matches()) {
			
			
			
			if(_min != null) {
				System.out.println("validator min check minvalue = "+_min);
				Integer iValue = Integer.valueOf((String)value, 16);
				if(iValue < _min) {
					return " Please enter a bigger number than" +_min;
				}
			}
			
			if(_max != null) {
				System.out.println("validator max check maxvalue = "+_max);
				Integer iValue = Integer.valueOf((String)value, 16);
				if(iValue > _max) {
					return " Please enter a number smaller than" +_max;
				}
			}

			
			try {
				Long.parseLong((String) value, 16);
			} catch (NumberFormatException e) {
				System.out.println(value + " isn't one of the numeric types.");
				return "Please enter a decimal number between 0-"
						+ Integer.MAX_VALUE + ".";
			}

			System.out.println(value + " is hex number.");
			return null;
			
		} else {
			System.out.println(value + " isn't decimal number.");
			return "Please enter a hex  number.";
		}
	}

}
