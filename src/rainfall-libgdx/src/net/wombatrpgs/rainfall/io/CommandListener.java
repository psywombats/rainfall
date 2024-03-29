/**
 *  CommandListener.java
 *  Created on Nov 23, 2012 3:56:59 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.io;

import net.wombatrpgs.rainfallschema.io.data.InputCommand;

/**
 * Anything that listens to intentions and commands from the player. These
 * generally have a whole set of command mappings and key mappings set up
 * behind them.
 */
public interface CommandListener {
	
	/**
	 * Called whenever a specific command is indicated by the player.
	 * @param	command		The command indicated
	 */
	public void onCommand(InputCommand command);

}
