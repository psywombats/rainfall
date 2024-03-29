/**
 *  Action.java
 *  Created on Jan 24, 2013 12:12:31 AM for project RainfallSchema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfallschema.characters.enemies.ai.data;

import net.wombatrpgs.mgns.core.Annotations.ExcludeFromTree;

/**
 * An action is part of an intent. If an intent's condition is met, the 
 * intelligence will carry out the action. This is the active part of the
 * action, there's usually some target associated with it.
 */
@ExcludeFromTree
public enum ActionType {
	
	SIT_STILL,
	PACE_MENACINGLY,
	PACE_HITWALLS,
	WANDER_ORGANICALLY,
	WANDER_RANDOMLY,
	PATHFIND_TO_HERO,
	CHARGE_HERO,
	CHARGE_FORWARD,
	FACE_HERO,
	DO_NOTHING,
	
}
