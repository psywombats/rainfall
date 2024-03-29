/**
 *  CommandSetLocation.java
 *  Created on Mar 7, 2013 8:50:51 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.scenes.commands;

import net.wombatrpgs.rainfall.core.RGlobal;
import net.wombatrpgs.rainfall.maps.Level;
import net.wombatrpgs.rainfall.maps.events.MapEvent;
import net.wombatrpgs.rainfall.scenes.SceneCommand;
import net.wombatrpgs.rainfall.scenes.SceneParser;

/**
 * Like a teleport but internal
 */
public class CommandSetLocation extends SceneCommand {
	
	protected String eventName;
	protected int teleX, teleY;

	/**
	 * Constructs a new location command from script.
	 * @param 	parent			The parent parser
	 * @param 	line			The line of code to interpret
	 */
	public CommandSetLocation(SceneParser parent, String line) {
		super(parent, line);
		line = line.substring(line.indexOf(' ') + 1);
		eventName = line.substring(0, line.indexOf(' '));
		line = line.substring(line.indexOf(' ') + 1);
		teleX = Integer.valueOf(line.substring(0, line.indexOf(' ')));
		line = line.substring(line.indexOf(' ') + 1);
		teleY = Integer.valueOf(line.substring(0, line.indexOf(']')));
	}

	/**
	 * @see net.wombatrpgs.rainfall.scenes.SceneCommand#run()
	 */
	@Override
	public boolean run() {
		if (finished) return true;
		MapEvent event = parent.getLevel().getEventByName(eventName);
		if (event == null) {
			RGlobal.reporter.warn("Tried to set location of null event: " + eventName);
		} else {
			Level map = parent.getLevel();
			event.setX(teleX * map.getTileWidth());
			event.setY(map.getHeightPixels() - (teleY+1) * map.getTileHeight());
		}
		
		finished = true;
		return true;
	}

}
