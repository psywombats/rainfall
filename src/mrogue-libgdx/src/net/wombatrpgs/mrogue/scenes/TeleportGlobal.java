/**
 *  TeleportSettings.java
 *  Created on Feb 8, 2013 12:08:39 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mrogue.scenes;

import com.badlogic.gdx.assets.AssetManager;

import net.wombatrpgs.mrogue.core.MGlobal;
import net.wombatrpgs.mrogue.core.Queueable;
import net.wombatrpgs.mrogue.maps.Level;
import net.wombatrpgs.mrogue.maps.events.MapEvent;
import net.wombatrpgs.mrogueschema.cutscene.SceneMDO;
import net.wombatrpgs.mrogueschema.settings.TeleportSettingsMDO;

/**
 * A thing to keep track of which scene scripts play before/after teleports
 */
public class TeleportGlobal implements Queueable {
	
	public static final String DEFAULT_MDO_KEY = "default_teleport";
	
	protected TeleportSettingsMDO mdo;
	protected SceneParser preParser, postParser;
	
	/**
	 * Constructs teleport settings from data.
	 * @param 	mdo				The settings to use to construct the settings
	 */
	public TeleportGlobal(TeleportSettingsMDO mdo) {
		this.mdo = mdo;
		preParser = new SceneParser(MGlobal.data.getEntryFor(mdo.pre, SceneMDO.class));
		postParser = new SceneParser(MGlobal.data.getEntryFor(mdo.post, SceneMDO.class));
	}
	
	/** @return The parser to play before a teleport */
	public SceneParser getPre() { return preParser; }
	
	/** @return The parser to play after a teleport */
	public SceneParser getPost() { return postParser; }

	/**
	 * @see net.wombatrpgs.mrogue.core.Queueable#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		preParser.queueRequiredAssets(manager);
		postParser.queueRequiredAssets(manager);
	}

	/**
	 * @see net.wombatrpgs.mrogue.core.Queueable#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		preParser.postProcessing(manager, pass);
		postParser.postProcessing(manager, pass);
	}
	
	/**
	 * Teleports the hero to the map. This is a core teleport event and doesn't
	 * actually deal with the pre/post stuff... Assumes the teleport affects the
	 * hero and not some other goober.
	 * @param 	map				The level to teleport to
	 * @param 	tileX			The x-coord to teleport to (in tiles);
	 * @param 	tileY			The y-coord to teleport to (in tiles)
	 */
	public void teleport(Level map, int tileX, int tileY) {
		
		MapEvent victim = MGlobal.hero;
		Level old = victim.getLevel();
		
		if (MGlobal.ui.getHud() != null) {
			MGlobal.ui.getHud().setOverlayTintIgnore(false);
		}
		if (old.getBGM() != null && !old.getBGM().matches(map.getBGM())) old.getBGM().stop();	
		old.onFocusLost();
		old.removeEvent(victim);
		old.update(0);
		
		map.addEvent(MGlobal.hero, tileX, tileY);
		if (MGlobal.ui.getHud() != null) {
			MGlobal.ui.getHud().setOverlayTintIgnore(true);
		}
		if (map.getBGM() != null && !map.getBGM().matches(old.getBGM())) map.getBGM().play();
		// TODO: make sure this only applies to maps that want it
		//MGlobal.screens.getCamera().constrainMaps(map);
		
		MGlobal.screens.peek().setCanvas(map);
		
	}

}