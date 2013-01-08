/**
 *  Layer.java
 *  Created on Nov 30, 2012 1:38:13 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.maps.layers;

import net.wombatrpgs.rainfall.collisions.FallResult;
import net.wombatrpgs.rainfall.collisions.Hitbox;
import net.wombatrpgs.rainfall.graphics.Renderable;
import net.wombatrpgs.rainfall.maps.events.MapEvent;

/**
 * A layer in a map, either a grid layer or an object layer. It's how Tiled
 * handles it.
 */
public abstract class Layer implements Renderable {
	
	/**
	 * Provides collision response service to an event on this layer.
	 * @param 	event			The event to push out of collision
	 */
	public abstract void applyPhysicalCorrections(MapEvent event);
	
	/**
	 * Gets the z-value of the layer. Layers with the same z-value share
	 * collisions and collision detection. 0 represents the floor, and each
	 * subsequent integer is another floor.
	 * @return					The z-value (depth) of this layer
	 */
	public abstract float getZ();
	
	/**
	 * Calculates what happens to a hitbox were it to be dropped on this layer.
	 * @param 	box				The hitbox being dropped
	 */
	public abstract FallResult dropObject(Hitbox box);

	/**
	 * Determines whether this layer is the floor, a so-called lower chip layer.
	 * This means that if there is no tile on this layer, that space will be
	 * impassable.
	 * @return					True if this layer is upper chip, else false
	 */
	public abstract boolean isLowerChip();
	
	/**
	 * Determines whether this layer is an object layer, a so-called upper
	 * chip layer. This means that unoccupied grid squares on this layer will
	 * be treated as passable.
	 * @return					True if this layer is lower chip, else false
	 */
	public boolean isUpperChip() {
		return !isLowerChip();
	}

}