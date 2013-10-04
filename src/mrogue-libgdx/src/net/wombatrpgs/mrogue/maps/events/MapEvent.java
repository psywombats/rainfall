/**
 *  MapEvent.java
 *  Created on Dec 24, 2012 2:41:32 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mrogue.maps.events;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.wombatrpgs.mrogue.core.MGlobal;
import net.wombatrpgs.mrogue.graphics.PreRenderable;
import net.wombatrpgs.mrogue.maps.Level;
import net.wombatrpgs.mrogue.maps.MapThing;
import net.wombatrpgs.mrogue.maps.PositionSetable;
import net.wombatrpgs.mrogue.maps.layers.EventLayer;
import net.wombatrpgs.mrogueschema.maps.data.Direction;

/**
 * A map event is any map object defined in Tiled, including characters and
 * teleports and other fun stuff. Revised as of 2012-01-30 to be anything that
 * exists on a Tiled layer, even if it wasn't created in Tiled itself.
 * 
 * MR: MapEvent is anything that exists in the world of tiles, as opposed to a
 * thing that just lives on a map. It isn't necessarily a character
 */
public abstract class MapEvent extends MapThing implements	PositionSetable,
															Comparable<MapEvent>,
															PreRenderable {
	
	/** A thingy to fool the prerenderable, a sort of no-appear flag */
	protected static final TextureRegion NO_APPEARANCE = null;
	
	/** Is this object hidden from view/interaction due to cutscene? */
	protected boolean commandHidden;
	protected boolean switchHidden;
	/** Another toggle on our visibility - if it exists, link it to hidden */
	protected String showSwitch;
	protected String hideSwitch;
	
	/** Coords in pixels relative to map origin */
	protected float x, y;
	/** Coords in tiles, (0,0) is upper left */
	protected int tileX, tileY;
	
	/** Velocity the object is trying to reach in pixels/second */
	protected float targetVX, targetVY;
	/** Velocity the object is currently moving at in pixels/second */
	protected float vx, vy;
	
	/** How fast this object accelerates when below its top speed, in px/s^2 */
	protected float acceleration;
	/** How fast this object deccelerates when above its top speed, in px/s^2 */
	protected float decceleration;
	/** The top speed this object can voluntarily reach, in px/s */
	protected float maxVelocity;
	
	/** Are we currently moving towards some preset destination? */
	protected boolean tracking;
	/** The place we're possibly moving for */
	protected float targetX, targetY;
	/** Gotta keep track of these for some reason (tracking reasons!) */
	protected float lastX, lastY;

	/**
	 * Creates a new map event for the level at the origin.
	 * @param 	parent		The parent level of the event
	 */
	protected MapEvent(Level parent) {
		super(parent);
		zeroCoords();
		// TODO: MapEvent
	}
	
	/**
	 * Creates a blank map event associated with no map. Assumes the subclass
	 * will do something interesting in its constructor.
	 */
	protected MapEvent() {
		zeroCoords();
	}
	
	/** @see net.wombatrpgs.mrogue.maps.Positionable#getX() */
	@Override
	public float getX() { return x; }

	/** @see net.wombatrpgs.mrogue.maps.Positionable#getY() */
	@Override
	public float getY() { return y; }

	/** @see net.wombatrpgs.mrogue.maps.PositionSetable#setX(int) */
	@Override
	public void setX(float x) { this.x = x; }

	/** @see net.wombatrpgs.mrogue.maps.PositionSetable#setY(int) */
	@Override
	public void setY(float y) { this.y = y; }
	
	/** @return x-coord of the center of this object, in px */
	public float getCenterX() { return x; }
	
	/** @return y-coord of the center of this object, in px */
	public float getCenterY() { return y; }
	
	/** @return x-coord of this object, in tiles */
	public int getTileX() { return tileX; }
	
	/** @return y-coord of this object, in tiles */
	public int getTileY() { return tileY; }
	
	/** @return The x-velocity of this object, in px/s */
	public float getVX() { return this.vx; }
	
	/** @return The y-velocity of this object, in px/s */
	public float getVY() { return this.vy; }
	
	/** @param f The offset to add to x */
	public void moveX(float f) { this.x += f; }
	
	/** @param y The offset to add to x */
	public void moveY(float g) { this.y += g; }
	
	/** @return True if this object is moving towards a location */
	public boolean isTracking() { return tracking; }
	
	/** @return The max velocity of this event, including run bonus */ 
	public float getMaxVelocity() { return this.calcAccelerationMaxVelocity(); }
	
	/** @param The new max targetable speed by this event */
	public void setMaxVelocity(float maxVelocity) { this.maxVelocity = maxVelocity; }
	
	/**
	 * Determines if this object is "stuck" or not. This means it's tracking
	 * but hasn't moved much at all.
	 * @return					True if the event is stuck, false otherwise
	 */
	public boolean isStuck() {
		return 	isTracking() &&
				Math.abs(lastX - x) < Math.abs(vx) / 2.f &&
				Math.abs(lastY - y) < Math.abs(vy) / 2.f;
	}
	
	/**
	 * Sorts map objects based on z-depth.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MapEvent other) {
		return Math.round(other.getSortY() - getSortY());
	}
	
	/** @see net.wombatrpgs.mrogue.graphics.PreRenderable#getRenderX() */
	@Override
	public int getRenderX() { return (int) getX(); }

	/** @see net.wombatrpgs.mrogue.graphics.PreRenderable#getRenderY() */
	@Override
	public int getRenderY() { return (int) getY(); }

	/**
	 * Default is inivisible.
	 * @see net.wombatrpgs.mrogue.graphics.PreRenderable#getRegion()
	 */
	@Override
	public TextureRegion getRegion() {
		return NO_APPEARANCE;
	}
	
	/**
	 * Stops this event from a period of pathing towards its logical next
	 * turn position by permanently setting it to is next turn positon.
	 */
	public void stopMoving() {
		x = tileX * parent.getTileWidth();
		y = tileY * parent.getTileHeight();
		halt();
	}
	
	/**
	 * Update yoself! This is called from the rendering loop but it's with some
	 * filters set on it for target framerate. As of 2012-01-30 it's not called
	 * from the idiotic update loop.
	 * @param 	elapsed			Time elapsed since last update, in seconds
	 */
	public void update(float elapsed) {
		super.update(elapsed);
		
		if (parent.isMoving() && !isTracking()) {
			onMoveStart();
			
		}
		
		float deltaVX, deltaVY;
		if (vx != targetVX) {
			if (Math.abs(vx) < calcDecelerationMaxVelocity()) {
				deltaVX = acceleration * elapsed;
			} else {
				deltaVX = decceleration * elapsed;
			}
			if (vx < targetVX) {
				vx = Math.min(vx + deltaVX, targetVX);
			} else {
				vx = Math.max(vx - deltaVX, targetVX);
			}
		}
		if (vy != targetVY) {
			if (Math.abs(vy) < calcDecelerationMaxVelocity()) {
				deltaVY = acceleration * elapsed;
			} else {
				deltaVY = decceleration * elapsed;
			}
			if (vy < targetVY) {
				vy = Math.min(vy + deltaVY, targetVY);
			} else {
				vy = Math.max(vy - deltaVY, targetVY);
			}
		}
		if (Float.isNaN(vx) || Float.isNaN(vy)) {
			MGlobal.reporter.warn("NaN values in physics!! " + this);
		}
		integrate(elapsed);
		if (tracking) {
			if ((x < targetX && lastX > targetX) || (x > targetX && lastX < targetX)) {
				x = targetX;
				vx = 0;
				targetVX = 0;
			}
			if ((y < targetY && lastY > targetY) || (y > targetY && lastY < targetY)) {
				y = targetY;
				vy = 0;
				targetVY = 0;
			}
			if (x == targetX && y == targetY) {
				tracking = false;
			}
		}
		storeXY();
	}

	/**
	 * This version kills itself unless it was present on the map in the first
	 * place.
	 * @see net.wombatrpgs.mrogue.maps.MapThing#reset()
	 */
	@Override
	public void reset() {
		// TODO: reset
//		if (object == null) {
//			if (getLevel() != null) {
//				getLevel().removeEvent(this);
//			} else {
//				MGlobal.reporter.warn("Strange ordering of remove events... " + this);
//			}
//		} else {
//			setX(extractX(parent, object));
//			setY(extractY(parent, object));
//		}
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.maps.MapThing#render
	 * (com.badlogic.gdx.graphics.OrthographicCamera)
	 */
	@Override
	public void render(OrthographicCamera camera) {
		if (hidden()) return;
		super.render(camera);
	}
	
	/**
	 * Sets the hide status of this map event via event command. Hidden events
	 * do not update or interact with other events. It's a way of having objects
	 * on the map but not using them until they're needed.
	 * @param 	hidden			True to hide the event, false to reveal it
	 */
	public void setCommandHidden(boolean hidden) {
		this.commandHidden = hidden;
	}
	
	/**
	 * Gets the name of this event as specified in Tiled. Null if the event is
	 * unnamed in tiled or was not created from tiled.
	 * @return
	 */
	public String getName() {
		// TODO: getName
		return "TODO: getName";
	}
	
	/**
	 * Checks if this event's in a specific group. Events can belong to multiple
	 * groups if their group name contains the separator character.
	 * @param 	groupName		The name of the group we may be in
	 * @return					True if in that group, false if not
	 */
	public boolean inGroup(String groupName) {
		// TODO: inGroup
		return false;
	}

	/**
	 * @see net.wombatrpgs.mrogue.maps.MapThing#renderLocal
	 * (com.badlogic.gdx.graphics.OrthographicCamera,
	 * com.badlogic.gdx.graphics.g2d.TextureRegion, int, int, int)
	 */
	public void renderLocal(OrthographicCamera camera, TextureRegion sprite, 
			int offX, int offY, int angle) {
		super.renderLocal(camera, sprite, getRenderX() + offX, getRenderY() + offY, 
				angle, 0);
	}
	
	/**
	 * Uses this event's x/y to render locally.
	 * @see net.wombatrpgs.mrogue.maps.MapThing#renderLocal
	 * (com.badlogic.gdx.graphics.OrthographicCamera, 
	 * com.badlogic.gdx.graphics.g2d.TextureRegion, int, int)
	 */
	public void renderLocal(OrthographicCamera camera, TextureRegion sprite) {
		super.renderLocal(camera, sprite, (int) getX(), (int) getY(), 0);
	}

	/**
	 * Determines if this object is currently in motion.
	 * @return					True if the object is moving, false otherwise
	 */
	public boolean isMoving() {
		return Math.abs(vx) > .1 || Math.abs(vy) > .1;
	}
	
	/**
	 * Stops all movement in a key-friendly way.
	 */
	public void halt() {
		targetVX = 0;
		targetVY = 0;
		vx = 0;
		vy = 0;
	}
	
	/**
	 * Gets the y were we sort at. This is for relative positioning with the z-
	 * layer. Used for above/below hero in subclasses. By default is y-coord.
	 * @return
	 */
	public float getSortY() {
		return getY();
	}
	
	/**
	 * Gives this map object a new target to track towards.
	 * @param 	targetX		The target location x-coord (in px)
	 * @param 	targetY		The target location y-coord (in px)
	 */
	public void targetLocation(float targetX, float targetY) {
		this.targetX = targetX;
		this.targetY = targetY;
		this.tracking = true;
	}

	/**
	 * Updates the target velocity of this map object.
	 * @param 	targetVX	The target x-velocity of the object, in px/s
	 * @param 	targetVY	The target y-velocity of the object, in px/s
	 */
	public final void targetVelocity(float targetVX, float targetVY) {
		internalTargetVelocity(targetVX, targetVY);
		this.tracking = false;
	}
	
	/**
	 * Updates the effective velocity of this map object.
	 * @param 	vx			The new x-velocity of the object, in px/s
	 * @param 	vy			The new y-velocity of the object, in px/s
	 */
	public void setVelocity(float vx, float vy) {
		this.vx = vx;
		this.vy = vy;
	}
	
	/**
	 * Calculates distance. This is like 7th grade math class here.
	 * @param 	other			The other object in the calculation
	 * @return					The distance between this and other, in pixels
	 */
	public float distanceTo(MapEvent other) {
		float dx = other.x - x;
		float dy = other.y - y;
		return (float) Math.sqrt(dx*dx + dy*dy);
	}
	
	/**
	 * Calculates the direction towards some other map event.
	 * @param 	event			The event to get direction towards
	 * @return					The direction towards that event
	 */
	public Direction directionTo(MapEvent event) {
		float dx = event.getX() - this.getX();
		float dy = event.getY() - this.getY();
		if (Math.abs(dx) > Math.abs(dy)) {
			if (dx > 0) {
				return Direction.RIGHT;
			} else {
				return Direction.LEFT;
			}
		} else {
			if (dy > 0) {
				return Direction.UP;
			} else {
				return Direction.DOWN;
			}
		}
	}
	
	/**
	 * Called when this object is added to an object layer. Nothing by default.
	 * @param 	layer			The layer this object is being added to
	 */
	public void onAdd(EventLayer layer) {
		this.lastX = getX();
		this.lastY = getY();
		// nothing by default
	}
	
	/**
	 * Called when this object begins updating its position in the move phase.
	 */
	public void onMoveStart() {
		int tWidth = parent.getTileWidth();
		int tHeight = parent.getTileHeight();
		targetLocation(tileX * tWidth, tileY * tHeight);
		float vx = (tileX*tWidth - x) / parent.getMoveTimeLeft();
		float vy = (tileY*tHeight - y) / parent.getMoveTimeLeft();
		setVelocity(vx, vy);
		targetVX = vx;
		targetVY = vy;
	}
	
	/**
	 * Calculates max velocity for the purposes of accelerating.
	 * @return					Target max velocity in px/s
	 */
	protected float calcAccelerationMaxVelocity() {
		return maxVelocity;
	}
	
	/**
	 * Calculates max velocity for the purposes of slowing down
	 * @return					Target max velocity in px/s
	 */
	protected float calcDecelerationMaxVelocity() {
		return maxVelocity;
	}
	
	/**
	 * Determines if an event is "hidden" either by switch or command.
	 * @return					True if the event is hidden, false otherwise
	 */
	protected boolean hidden() {
		return commandHidden || switchHidden;
	}
	
	/**
	 * Internal method of targeting velocities. Feel free to override this one.
	 * @param 	targetVX			The new target x-velocity (in px/s)
	 * @param 	targetVY			The new target y-velocity (in px/s)
	 */
	protected void internalTargetVelocity(float targetVX, float targetVY) {
		this.targetVX = targetVX;
		this.targetVY = targetVY;
	}
	
	/**
	 * Does some constructor-like stuff to reset physical variables.
	 */
	protected void zeroCoords() {
		x = 0;
		y = 0;
		targetVX = 0;
		targetVY = 0;
		vx = 0;
		vy = 0;
		
		// TODO: dummy movement
		acceleration = 1000;
		decceleration = 1000;
		maxVelocity = 100000;
	}
	
	/**
	 * Applies the physics integration for a timestep.
	 * @param 	elapsed			The time elapsed in that timestep
	 */
	protected void integrate(float elapsed) {
		x += vx * elapsed;
		y += vy * elapsed;
	}
	
	/**
	 * Updates last x/y.
	 */
	protected void storeXY() {
		lastX = x;
		lastY = y;
	}

}
