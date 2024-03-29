/**
 *  Canvasable.java
 *  Created on Jan 30, 2013 1:00:08 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.screen;

import net.wombatrpgs.rainfall.core.Updateable;
import net.wombatrpgs.rainfall.graphics.Renderable;

/**
 * Something that's both renderable and updateable. Can also be sorted.
 */
public interface ScreenShowable extends Renderable, 
										Updateable {
	
	/**
	 * If this is true, ignores the screen's transition tint. This means that
	 * it will have to be rendered in a separate phase.
	 * @return					True if tint is ignored, false if it is applied
	 */
	public boolean ignoresTint();

}
