/**
 *  TriggerEvent.java
 *  Created on Feb 4, 2013 2:04:30 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.maps.events;

import net.wombatrpgs.rainfall.core.RGlobal;
import net.wombatrpgs.rainfall.maps.Level;
import net.wombatrpgs.rainfall.physics.CollisionResult;
import net.wombatrpgs.rainfall.physics.Hitbox;
import net.wombatrpgs.rainfall.physics.RectHitbox;
import net.wombatrpgs.rainfall.scenes.SceneParser;
import net.wombatrpgs.rainfallschema.cutscene.SceneMDO;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;

/**
 * An event that triggers a cutscene or other scene script.
 */
public class TriggerEvent extends MapEvent {
	
	protected static final String PROPERTY_ID = "id";
	
	protected SceneParser scene;
	protected Hitbox box;
	
	/**
	 * Constructs a new trigger. Called by the superclass factory method.
	 * @param 	parent			The parent level of this event
	 * @param 	object			The tiled object that this object is based on
	 */
	protected TriggerEvent(Level parent, TiledObject object) {
		super(parent, object, false, true);
		this.box = new RectHitbox(this, 0, -object.height, object.width, 0);
		String id = object.properties.get(PROPERTY_ID);
		if (id == null) {
			RGlobal.reporter.warn("Trigger event had no id: " + object.name);
		} else {
			SceneMDO mdo = RGlobal.data.getEntryFor(id, SceneMDO.class);
			scene = new SceneParser(mdo, getLevel());
		}
	}
	
	/**
	 * @see net.wombatrpgs.rainfall.maps.events.MapEvent#getHitbox()
	 */
	@Override
	public Hitbox getHitbox() {
		return box;
	}
	
	/**
	 * @see net.wombatrpgs.rainfall.maps.events.MapEvent#onCollide
	 * (net.wombatrpgs.rainfall.maps.MapObject, 
	 * net.wombatrpgs.rainfall.physics.CollisionResult)
	 */
	@Override
	public boolean onCollide(MapEvent other, CollisionResult result) {
		if (other != RGlobal.hero) return true;
		scene.run(getLevel());
		return true;
	}

	/**
	 * @see net.wombatrpgs.rainfall.maps.events.MapEvent#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		super.queueRequiredAssets(manager);
		scene.queueRequiredAssets(manager);
	}

	/**
	 * @see net.wombatrpgs.rainfall.maps.events.MapEvent#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		super.postProcessing(manager, pass);
		scene.postProcessing(manager, pass);
	}

}
