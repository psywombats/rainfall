/**
 *  TeleportEvent.java
 *  Created on Dec 24, 2012 2:00:17 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.maps.events;

import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;

import net.wombatrpgs.rainfall.core.RGlobal;
import net.wombatrpgs.rainfall.maps.Level;
import net.wombatrpgs.rainfall.physics.CollisionResult;
import net.wombatrpgs.rainfall.physics.Hitbox;
import net.wombatrpgs.rainfall.physics.RectHitbox;
import net.wombatrpgs.rainfall.scenes.FinishListener;

/**
 * Constructs a teleportation device! (or event, just depends on your
 * perspective...)
 */
public class TeleportEvent extends MapEvent {
	
	protected Hitbox box;
	protected String mapID;
	protected int targetX;
	protected int targetY;
	
	protected static final String PROPERTY_X = "x";
	protected static final String PROPERTY_Y = "y";
	protected static final String PROPERTY_ID = "id";

	/**
	 * Creates a new teleport for the supplied parent level using coordinates
	 * inferred from the tiled object. Called from the superclass's factory
	 * method.
	 * @param 	parent		The parent levelt to make teleport for
	 * @param 	object		The object to infer coords from
	 */
	public TeleportEvent(Level parent, TiledObject object) {
		super(parent, object, false, true);
		this.box = new RectHitbox(this, 0, -object.height, object.width, 0);
		
		// TODO: center the hero
		this.targetX = Integer.valueOf(object.properties.get(PROPERTY_X));
		this.targetY = Integer.valueOf(object.properties.get(PROPERTY_Y));
		this.mapID = object.properties.get(PROPERTY_ID);
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
	 * (net.wombatrpgs.rainfall.maps.MapEvent, 
	 * net.wombatrpgs.rainfall.physics.CollisionResult)
	 */
	@Override
	public boolean onCollide(MapEvent other, CollisionResult result) {
		if (other != RGlobal.hero) return true;
		if (getLevel().contains(RGlobal.teleport.getPre())) return true;
		RGlobal.teleport.getPre().addListener(new FinishListener() {
			@Override
			public void onFinish(Level map) {
				Level newMap = RGlobal.levelManager.getLevel(mapID);
				RGlobal.teleport.teleport(
						newMap, 
						targetX, 
						newMap.getHeight() - targetY - 1);
				RGlobal.teleport.getPost().run(newMap);
			}
		});
		RGlobal.teleport.getPre().run(RGlobal.hero.getLevel());
		return true;
	}
	
}
