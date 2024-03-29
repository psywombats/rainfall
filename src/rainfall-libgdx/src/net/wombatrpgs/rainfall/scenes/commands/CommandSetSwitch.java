/**
 *  CommandSetSwitch.java
 *  Created on Mar 8, 2013 4:15:14 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.scenes.commands;

import net.wombatrpgs.rainfall.core.RGlobal;
import net.wombatrpgs.rainfall.scenes.SceneCommand;
import net.wombatrpgs.rainfall.scenes.SceneParser;

/**
 * Sets a switch, like in RM.
 */
public class CommandSetSwitch extends SceneCommand {
	
	protected static final String ARG_ON = "on";
	protected static final String ARG_OFF = "off";
	
	protected String switchName;
	protected boolean value;

	/**
	 * Inherited constructor.
	 * @param 	parent			Parent parser
	 * @param 	line			Line of code
	 */
	public CommandSetSwitch(SceneParser parent, String line) {
		super(parent, line);
		line = line.substring(line.indexOf(' ' ) + 1);
		if (line.indexOf(' ' ) != -1) {
			switchName = line.substring(0, line.indexOf(' '));
			line = line.substring(line.indexOf(' ' ) + 1);
			String arg = line.substring(0, line.indexOf(']'));
			if (arg.equals(ARG_ON)) {
				value = true;
			} else if (arg.equals(ARG_OFF)) {
				value = false;
			} else {
				RGlobal.reporter.warn("Unknown switch arg value: " + arg);
			}
		} else {
			switchName = line.substring(0, line.indexOf(']'));
			value = true;
		}
	}

	/**
	 * @see net.wombatrpgs.rainfall.scenes.SceneCommand#run()
	 */
	@Override
	public boolean run() {
		if (finished) return true;
		RGlobal.hero.setSwitch(switchName, value);
		return true;
	}

}
