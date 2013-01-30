/**
 *  MapObject.java
 *  Created on Nov 12, 2012 11:10:52 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.maps;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.wombatrpgs.rainfall.core.RGlobal;
import net.wombatrpgs.rainfall.core.Updateable;
import net.wombatrpgs.rainfall.graphics.Renderable;

/**
 * All objects that appear in Tiled maps that are not tiles extend this class.
 * This includes both characters and events. As of 2012-01-23
 */
public abstract class MapObject implements	Renderable,
											Updateable {
	
	/** Level this object exists on */
	protected Level parent;
	/** All sub-objects that update with us */
	protected List<Updateable> subEvents;
	
	/**
	 * Creates a new map object for a given level.
	 * @param 	parent		The level this map object is on
	 */
	public MapObject(Level parent) {
		this();
		this.parent = parent;
	}
	
	/**
	 * Creates a new map object floating in limbo land.
	 */
	public MapObject() {
		subEvents = new ArrayList<Updateable>();
	}
	
	/** @return The map the hero is currently on */
	public Level getLevel() { return parent; }

	/**
	 * This is actually the update part of the render loop. CHANGED on
	 * 2013-01-30 so that the level actually calls update separately. So don't
	 * worry about that.
	 * @see net.wombatrpgs.rainfall.graphics.Renderable#render
	 * (com.badlogic.gdx.graphics.OrthographicCamera)
	 */
	@Override
	public void render(OrthographicCamera camera) {
		// default is invisible
	}
	
	/**
	 * @see net.wombatrpgs.rainfall.graphics.Queueable#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		// default queues nothing
	}

	/**
	 * @see net.wombatrpgs.rainfall.graphics.Queueable#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void postProcessing(AssetManager manager) {
		// default does nothing
	}

	/**
	 * @see net.wombatrpgs.rainfall.core.Updateable#update(float)
	 */
	@Override
	public void update(float elapsed) {
		for (Updateable subEvent : subEvents) {
			subEvent.update(elapsed);
		}
	}

	/**
	 * Called when this object is tele'd onto a map.
	 * @param 	map				The map this object is being removed from
	 */
	public void onAddedToMap(Level map) {
		this.parent = map;
	}
	
	/**
	 * Called when this object is tele'd or otherwise removed from a map.
	 * @param 	map				The map this object is being removed from
	 */
	public void onRemovedFromMap(Level map) {
		this.parent = null;
	}
	
	/**
	 * A hook system for sub-events. Adds another event that will be updated
	 * alongside this one.
	 * @param 	subEvent		The other object to update at the same time
	 */
	public void addSubEvent(Updateable subEvent) {
		subEvents.add(subEvent);
	}
	
	/**
	 * Remove the hook from a subevent. Stops it from updating.
	 * @param 	subEvent		The preregistered event to remove
	 */
	public void removeSubEvent(Updateable subEvent) {
		if (subEvents.contains(subEvent)) {
			subEvents.remove(subEvent);
		} else {
			RGlobal.reporter.warn("Removed a non-child subevent: " + subEvent);
		}
	}
	
	/**
	 * Renders a texture at this object's location using its own batch and
	 * coords appropriate to the drawing. This is bascally a static method that
	 * could go in any Positionable but oh well.
	 * @param 	sprite			The region to render
	 * @param	camera			The current camera
	 * @param	x				The x-coord for rendering (in pixels)
	 * @param	y				The y-coord for rendering (in pixels)
	 */
	protected void renderLocal(OrthographicCamera camera, TextureRegion sprite, int x, int y) {
		parent.getBatch().draw(
				sprite, 
				x + Gdx.graphics.getWidth()/2 - camera.position.x, 
				y + Gdx.graphics.getHeight()/2 - camera.position.y);
	}

}
