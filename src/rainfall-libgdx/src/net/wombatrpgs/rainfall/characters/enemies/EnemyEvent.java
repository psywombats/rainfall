/**
 *  Enemy.java
 *  Created on Jan 23, 2013 9:14:38 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.characters.enemies;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.maps.MapObject;

import net.wombatrpgs.rainfall.characters.CharacterEvent;
import net.wombatrpgs.rainfall.characters.ai.Intelligence;
import net.wombatrpgs.rainfall.characters.ai.IntelligenceFactory;
import net.wombatrpgs.rainfall.characters.moveset.MoveFactory;
import net.wombatrpgs.rainfall.characters.moveset.MovesetAct;
import net.wombatrpgs.rainfall.core.Constants;
import net.wombatrpgs.rainfall.core.RGlobal;
import net.wombatrpgs.rainfall.graphics.particles.Emitter;
import net.wombatrpgs.rainfall.graphics.particles.GibParticleSet;
import net.wombatrpgs.rainfall.maps.Level;
import net.wombatrpgs.rainfall.maps.events.AnimationPlayer;
import net.wombatrpgs.rainfall.maps.events.MapEvent;
import net.wombatrpgs.rainfall.physics.CollisionResult;
import net.wombatrpgs.rainfallschema.characters.enemies.EnemyEventMDO;
import net.wombatrpgs.rainfallschema.characters.enemies.VulnerabilityMDO;
import net.wombatrpgs.rainfallschema.characters.enemies.ai.data.IntelligenceMDO;
import net.wombatrpgs.rainfallschema.graphics.AnimationMDO;
import net.wombatrpgs.rainfallschema.graphics.EmitterMDO;
import net.wombatrpgs.rainfallschema.graphics.GibsetMDO;

/**
 * The one and only class for those pesky badniks that hunt down the valiant
 * hero and hinder his quest to save the earth.
 */
public class EnemyEvent extends CharacterEvent {
	
	protected static final String PROPERTY_RESPAWNS = "respawns";
	
	protected EnemyEventMDO mdo;
	protected List<MovesetAct> moves;
	protected Intelligence ai;
	protected Vulnerability vuln;
	protected Emitter emitter;
	protected AnimationPlayer deathSplat;
	protected int againstWall;
	
	/**
	 * Creates a new enemy on a map from a database entry.
	 * @param 	mdo				The MDO with data to create from
	 * @param	object			The object on the map that made us
	 * @param 	parent			The parent map of the object
	 */
	public EnemyEvent(EnemyEventMDO mdo, MapObject object, Level parent) {
		super(mdo, object, parent);
		this.mdo = mdo;
		againstWall = 0;
		moves = new ArrayList<MovesetAct>();
		for (String mdoKey : mdo.moveset) {
			MovesetAct act = MoveFactory.generateMove(this, mdoKey);
			moves.add(act);
			assets.add(act);
		}
		IntelligenceMDO aiMDO = RGlobal.data.getEntryFor(
				mdo.intelligence, IntelligenceMDO.class);
		ai = IntelligenceFactory.create(this, aiMDO);
		assets.add(ai);
		VulnerabilityMDO vulnMDO = RGlobal.data.getEntryFor(
				mdo.vulnerability, VulnerabilityMDO.class);
		vuln = new Vulnerability(vulnMDO);
		if (mdoHasProperty(mdo.gibset) && mdoHasProperty(mdo.emitter)) {
			GibsetMDO gibsetMDO = RGlobal.data.getEntryFor(mdo.gibset, GibsetMDO.class);
			EmitterMDO emitterMDO = RGlobal.data.getEntryFor(mdo.emitter, EmitterMDO.class);
			GibParticleSet gibs = new GibParticleSet(gibsetMDO);
			emitter = new Emitter(emitterMDO, gibs);
			assets.add(emitter);
		}
		if (mdo.dieAnim != null && !Constants.NULL_MDO.equals(mdo.dieAnim)) {
			AnimationMDO dieMDO = RGlobal.data.getEntryFor(mdo.dieAnim, AnimationMDO.class);
			deathSplat = new AnimationPlayer(dieMDO);
			assets.add(deathSplat);
		}
	}

	/**
	 * @see net.wombatrpgs.rainfall.characters.CharacterEvent#update(float)
	 */
	@Override
	public void update(float elapsed) {
		if (canAct()) ai.act();
		super.update(elapsed);
		if (againstWall > 0) againstWall -= 1;
	}

	/**
	 * @see net.wombatrpgs.rainfall.characters.CharacterEvent#onCharacterCollide
	 * (net.wombatrpgs.rainfall.characters.CharacterEvent,
	 * net.wombatrpgs.rainfall.physics.CollisionResult)
	 */
	@Override
	public boolean onCharacterCollide(CharacterEvent other, CollisionResult result) {
		if (dead) return true;
		return super.onCharacterCollide(other, result); // ie false
	}

	/**
	 * @see net.wombatrpgs.rainfall.maps.events.MapEvent#resolveWallCollision
	 * (net.wombatrpgs.rainfall.physics.CollisionResult)
	 */
	@Override
	public void resolveWallCollision(CollisionResult result) {
		super.resolveWallCollision(result);
		againstWall = 2;
	}

	/**
	 * @see net.wombatrpgs.rainfall.characters.CharacterEvent#reset()
	 */
	@Override
	public void reset() {
		if (object == null) {
			super.reset();
		} else {
			if (getProperty(PROPERTY_RESPAWNS) != null) {
				super.reset();
			} else {
				if (!dead) super.reset();
				// we're still dead :(
			}
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String name;
		if (mdo.name != null && !mdo.name.equals("")) {
			name = mdo.name;
		} else {
			name = mdo.appearance;
		}
		String deads;
		if (this.dead) {
			deads = "dead ";
		} else {
			deads = "";
		}
		return "a " + deads + getName() + ": " + name + " " + this.getX() + 
				" " +	this.getY() + " rendering " + this.appearance;
	}
	
	/**
	 * @see net.wombatrpgs.rainfall.maps.events.MapEvent#endFall()
	 */
	@Override
	public void endFall() {
		dead = true;
	}
	
	/**
	 * Gets the cached list of moves this enemy can perform.
	 * @return					The moves of this enemy
	 */
	public List<MovesetAct> getMoves() {
		return moves;
	}

	/**
	 * Kills self in a spectacular manner. Another object is supplied so that
	 * gibs can scatter correctly. If this enemy just imploded randomly, then
	 * pass in itself and the distribution will be random.
	 * @param 	cause			The event that caused this enemy's death
	 */
	public void selfDestruct(MapEvent cause) {
		if (emitter != null) {
			float xComp, yComp;
			if (cause.isMoving()) {
				xComp = cause.getVX();
				yComp = cause.getVY();
			} else {
				xComp = cause.getX() - getX();
				yComp = cause.getY() - getY();
			}
			float norm = (float) Math.sqrt(xComp*xComp + yComp*yComp);
			norm *= .8;
			if (Math.abs(norm) > .01) {
				xComp /= norm;
				yComp /= norm;
			} else {
				xComp = 0;
				yComp = 0;
			}
			parent.addEvent(emitter, 0, 0, parent.getZ(this));
			emitter.setX(getX());
			emitter.setY(getY());
			emitter.fire(xComp, yComp);
		}
		if (deathSplat != null) {
			parent.addEventAbsolute(deathSplat, getX(), getY(), parent.getZ(this));
			deathSplat.start();
		}
		kill();
		setX(0);
		setY(0);
	}
	
}
