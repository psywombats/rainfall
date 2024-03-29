/**
 *  ActRun.java
 *  Created on Apr 11, 2013 8:24:45 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.characters.moveset;

import net.wombatrpgs.rainfall.characters.CharacterEvent;
import net.wombatrpgs.rainfall.maps.Level;
import net.wombatrpgs.rainfallschema.characters.hero.moveset.RunMDO;

/**
 * Alters the max movement velocity of the actor.
 */
public class ActRun extends MovesetAct {
	
	protected RunMDO mdo;
	protected float delta;

	/**
	 * Inherited constructor. Generate from data.
	 * @param 	actor			The character doing the acting
	 * @param 	mdo				The data to generate from
	 */
	public ActRun(CharacterEvent actor, RunMDO mdo) {
		super(actor, mdo);
		this.mdo = mdo;
	}

	/**
	 * @see net.wombatrpgs.rainfall.characters.moveset.MovesetAct#coreAct
	 * (net.wombatrpgs.rainfall.maps.Level, net.wombatrpgs.rainfall.characters.CharacterEvent)
	 */
	@Override
	public void coreAct(Level map, CharacterEvent actor) {
		if (sfx != null) sfx.play();
		delta = mdo.newRate - actor.getMaxVelocity();
		actor.addToRunBonus(delta);
		actor.startAction(this);
	}

	/**
	 * @see net.wombatrpgs.rainfall.characters.moveset.MovesetAct#coreRelease
	 * (net.wombatrpgs.rainfall.maps.Level, net.wombatrpgs.rainfall.characters.CharacterEvent)
	 */
	@Override
	public void coreRelease(Level map, CharacterEvent actor) {
		actor.addToRunBonus(-delta);
		delta = 0;
		actor.stopAction(this);
	}

	/**
	 * @see net.wombatrpgs.rainfall.characters.moveset.MovesetAct#cancel()
	 */
	@Override
	public void cancel() {
		super.cancel();
		if (delta != 0) {
			actor.addToRunBonus(-delta);
			delta = 0;
		}
	}

	/**
	 * @see net.wombatrpgs.rainfall.characters.moveset.MovesetAct#update(float)
	 */
	@Override
	public void update(float elapsed) {
		super.update(elapsed);
//		if (actor.isMoveActive(this) && !actor.isMoving()) {
//			cancel();
//			anulled = true;
//		}
	}

}
