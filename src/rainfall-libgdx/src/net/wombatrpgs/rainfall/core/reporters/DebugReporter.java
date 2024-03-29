/**
 *  DebugReporter.java
 *  Created on Nov 4, 2012 6:09:37 AM for project MGNEngine
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.core.reporters;

import net.wombatrpgs.rainfall.core.Reporter;


/**
 * A little reporter meant for psy to test shit.
 */
public class DebugReporter implements Reporter {
	
	public static boolean VERBOSE = true;

	/**
	 * @see net.wombatrpgs.rainfall.core.Reporter#inform(java.lang.String)
	 */
	@Override
	public void inform(String info) {
		if (VERBOSE) {
			System.out.println("INFO: " + info);
			System.out.flush();
		}
	}

	/**
	 * @see net.wombatrpgs.rainfall.core.Reporter#warn(java.lang.String)
	 */
	@Override
	public void warn(String warning) {
		System.out.println("WARN: " + warning);
		System.out.flush();
	}

	/**
	 * @see net.wombatrpgs.rainfall.core.Reporter#err(java.lang.String)
	 */
	@Override
	public void err(String error) {
		System.err.println("ERROR: " + error);
		System.err.flush();
	}

	/**
	 * @see net.wombatrpgs.rainfall.core.Reporter#err(java.lang.String, java.lang.Exception)
	 */
	@Override
	public void err(String error, Exception e) {
		err(error);
		err(e);
	}

	/**
	 * @see net.wombatrpgs.rainfall.core.Reporter#err(java.lang.Exception)
	 */
	@Override
	public void err(Exception e) {
		e.printStackTrace();
	}

	/**
	 * @see net.wombatrpgs.rainfall.core.Reporter#warn(java.lang.String, java.lang.Exception)
	 */
	@Override
	public void warn(String error, Exception e) {
		warn(error);
		e.printStackTrace();
	}

}
