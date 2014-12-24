/*
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package org.multicore_association.ccf.sampleapp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CCF_FloatValidator {
	
	Float _min;
	Float _max;
	
	CCF_FloatValidator(Float min, Float max) {
		_min = min;
		_max = max;
	}
	
	String isValid(String value) {
		
		
		System.out.println("****** FloatValidator.isValid:");
		Pattern pattern = Pattern.compile("(-?(0|[1-9]\\d*)\\.\\d+)|(-?([1-9]\\d*))");
		Matcher matcher = pattern.matcher((String) value);

		if (matcher.matches()) {
			
			if(_min != null) {
				System.out.println("validator min check minvalue = "+_min);
				Float fValue = Float.valueOf((String)value);
				if(fValue < _min) {
					return " Please enter a bigger number than" +_min;
				}
			}
			
			if(_max != null) {
				System.out.println("validator max check maxvalue = "+_max);
				Float fValue = Float.valueOf((String)value);
				if(fValue > _max) {
					return " Please enter a number smaller than" +_max;
				}
			}

			
			try {
				Float.parseFloat((String) value);
			} catch (NumberFormatException e) {
				System.out.println(value + " isn't one of the numeric types.");
				return "Please enter a decimal number between 0-"
						+ Float.MAX_VALUE + ".";
			}

			System.out.println(value + " is float number.");
			return null;
			
		} else {
			System.out.println(value + " isn't float number.");
			return "Please enter a float number(xxx.yyy).";
		}
	}

}
