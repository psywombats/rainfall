/**
 *  Enemy.java
 *  Created on Jan 23, 2013 9:14:38 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mrogue.characters;

import net.wombatrpgs.mrogue.characters.ai.Intelligence;
import net.wombatrpgs.mrogue.characters.ai.IntelligenceFactory;
import net.wombatrpgs.mrogue.core.MGlobal;
import net.wombatrpgs.mrogue.maps.Level;
import net.wombatrpgs.mrogueschema.characters.EnemyEventMDO;
import net.wombatrpgs.mrogueschema.characters.ai.data.IntelligenceMDO;

/**
 * The one and only class for those pesky badniks that hunt down the valiant
 * hero and hinder his quest to save the earth.
 */
public class EnemyEvent extends CharacterEvent {
	
	protected EnemyEventMDO mdo;
	protected Intelligence ai;
	
	/**
	 * Creates a new enemy on a map from a database entry.
	 * @param 	mdo				The MDO with data to create from
	 * @param	object			The object on the map that made us
	 * @param 	parent			The parent map of the object
	 */
	public EnemyEvent(EnemyEventMDO mdo, Level parent) {
		super(mdo, parent);
		this.mdo = mdo;
		IntelligenceMDO aiMDO = MGlobal.data.getEntryFor(
				mdo.intelligence, IntelligenceMDO.class);
		ai = IntelligenceFactory.create(this, aiMDO);
		assets.add(ai);
	}

	/**
	 * @see net.wombatrpgs.mrogue.characters.CharacterEvent#update(float)
	 */
	@Override
	public void update(float elapsed) {
		ai.act();
		super.update(elapsed);
	}

	/**
	 * @see net.wombatrpgs.mrogue.characters.CharacterEvent#reset()
	 */
	@Override
	public void reset() {
		// TODO: reset
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
		return "a " + getName() + ": " + name + " " + this.getX() + 
				" " +	this.getY() + " rendering " + this.appearance;
	}

}
