/**
 *  GameScreen.java
 *  Created on Feb 22, 2013 4:13:09 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.screen.instances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import net.wombatrpgs.rainfall.core.Constants;
import net.wombatrpgs.rainfall.core.RGlobal;
import net.wombatrpgs.rainfall.io.TestCommandMap;
import net.wombatrpgs.rainfall.maps.Level;
import net.wombatrpgs.rainfall.scenes.SceneParser;
import net.wombatrpgs.rainfall.screen.Screen;
import net.wombatrpgs.rainfallschema.cutscene.SceneMDO;
import net.wombatrpgs.rainfallschema.io.data.InputCommand;
import net.wombatrpgs.rainfallschema.settings.IntroSettingsMDO;
import net.wombatrpgs.rainfallschema.test.FramerateTestMDO;
import net.wombatrpgs.rainfallschema.test.ShaderTestMDO;
import net.wombatrpgs.rainfallschema.test.data.TestState;

/**
 * This is the default screen that appears when the game is first loaded. Once
 * on this screen, the opening scene is played.
 */
// TODO: there's some sloppy fullscreen shit here that should be generalized
public class DefaultScreen extends Screen {
	
	protected SceneParser introParser;
	protected Level map;
	protected BitmapFont defaultFont;
	
	// tests
	protected FramerateTestMDO fpsMDO;
	protected ShaderTestMDO shaderMDO;
	
	protected ShaderProgram testShader;
	
	/**
	 * Constructs the introduction scene. This consists of simply setting up the
	 * parser and map.
	 */
	public DefaultScreen() {
		IntroSettingsMDO introMDO=RGlobal.data.getEntryFor("default_intro", IntroSettingsMDO.class);
		SceneMDO sceneMDO = RGlobal.data.getEntryFor(introMDO.scene, SceneMDO.class);
		RGlobal.levelManager.setScreen(this);
		map = RGlobal.levelManager.getLevel(introMDO.map);
		introParser = new SceneParser(sceneMDO);
		this.canvas = map;
		this.commandContext = new TestCommandMap();
		defaultFont = new BitmapFont();
		batch = new SpriteBatch();
		
		fpsMDO = RGlobal.data.getEntryFor("test_fps", FramerateTestMDO.class);
		
		shaderMDO = RGlobal.data.getEntryFor("test_shader", ShaderTestMDO.class);
		if (shaderMDO.enabled == TestState.ENABLED) {
			testShader = new ShaderProgram(
					RGlobal.loader.getText(Constants.SHADERS_DIR + shaderMDO.vertexFile),
					RGlobal.loader.getText(Constants.SHADERS_DIR + shaderMDO.fragmentFile));
			batch.setShader(testShader);
			mapShader = (testShader);
		}
		
		init();
	}
	
	/**
	 * @see net.wombatrpgs.rainfall.io.CommandListener#onCommand
	 * (net.wombatrpgs.rainfallschema.io.data.InputCommand)
	 */
	@Override
	public void onCommand(InputCommand command) {
		//RGlobal.reporter.inform("Command received: " + command);
		switch (command) {
		case INTENT_EXIT:
			Gdx.app.exit();
			break;
		case INTENT_FULLSCREEN:
			Gdx.graphics.setDisplayMode(
					RGlobal.window.getResolutionWidth(), 
					RGlobal.window.getResolutionHeight(), 
					!Gdx.graphics.isFullscreen());
			break;
		default:
			RGlobal.hero.act(command, RGlobal.hero.getLevel());
		}
		
	}

	/**
	 * @see net.wombatrpgs.rainfall.screen.Screen#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		super.queueRequiredAssets(manager);
		introParser.queueRequiredAssets(manager);
		// the map should be done already
	}

	/**
	 * @see net.wombatrpgs.rainfall.screen.Screen#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		super.postProcessing(manager, pass);
		introParser.postProcessing(manager, pass);
		// the map should be done already
	}
	
	/**
	 * @see net.wombatrpgs.rainfall.screen.Screen#render()
	 */
	@Override
	public void render() {
		super.render();
		batch.begin();
		if (fpsMDO.enabled == TestState.ENABLED) {
			float wr = RGlobal.window.getZoom();
			float wh = RGlobal.window.getZoom();
			defaultFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
					cam.position.x - RGlobal.window.getWidth()/2*wh + 8,
					cam.position.y + RGlobal.window.getHeight()/2*wr - 8);
		}
		batch.end();
	}

	/**
	 * @see net.wombatrpgs.rainfall.screen.Screen#update(float)
	 */
	@Override
	public void update(float elapsed) {
		super.update(elapsed);
		if (!introParser.isRunning() && !introParser.hasExecuted()) {
			introParser.run(map);
		}
	}

}
