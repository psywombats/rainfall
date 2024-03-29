/**
 *  Context.java
 *  Created on Nov 23, 2012 4:33:37 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.screen;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import net.wombatrpgs.rainfall.core.Queueable;
import net.wombatrpgs.rainfall.core.RGlobal;
import net.wombatrpgs.rainfall.core.Updateable;
import net.wombatrpgs.rainfall.graphics.Disposable;
import net.wombatrpgs.rainfall.io.CommandListener;
import net.wombatrpgs.rainfall.io.CommandMap;
import net.wombatrpgs.rainfallschema.io.data.InputCommand;

/**
 * A screen is the environment in which the game is now running. It's
 * essentially part of a massive state machine that defines the game, and saves
 * some notes about its parameters, such as the screen to display and how to
 * handle keys. Screens can be layered one on top of the other. In this case,
 * the screens with lower z values are rendered first, and the screens behind
 * are only rendered if the screen on top of them is counted as transparent.
 * More info in the ScreenStack class.
 */
public abstract class Screen implements CommandListener,
										Comparable<Screen>,
										Updateable,
										Queueable,
										Disposable {
	
	/** Command map to use while this screen is active */
	protected CommandMap commandContext;
	/** The thing to draw if this canvas is visible */
	protected ScreenShowable canvas;
	/** These things will be drawn over top of the canvas */
	protected List<ScreenShowable> screenObjects;
	/** What we'll use to render */
	protected TrackerCam cam;
	/** What we'll use to render UI */
	protected OrthographicCamera uiCam;
	/** Depth, lower values are rendered last */
	protected float z;
	/** If true, layers with higher z won't be rendered */
	protected boolean transparent;
	/** Batch used to render sprites */
	protected SpriteBatch batch;
	/** Batch used to render frame buffers */
	protected SpriteBatch privateBatch;
	/** Batch used to render UI and anything independent of camera */
	protected SpriteBatch uiBatch;
	/** Shader used to render background maps */
	protected ShaderProgram mapShader;
	/** Buffer we'll be using to draw to before scaling to screen */
	protected FrameBuffer buffer;
	/** What we'll be tinting the screen before each render */
	protected Color tint;
	/** Used to draw the background rects */
	protected ShapeRenderer shapes;
	/** Have we been set up yet? */
	protected boolean initialized;

	
	/**
	 * Creates a new game screen. Remember to call intialize when done setting
	 * things up yourself. Fair warning: if you don't give us a camera, we'll
	 * create one for ourselves, potentially reformatting the game window.
	 */
	public Screen() {
		transparent = false;
		initialized = false;
		z = 0;
		mapShader = null;
		batch = new SpriteBatch();
		privateBatch = new SpriteBatch();
		uiBatch = new SpriteBatch();
		buffer = new FrameBuffer(Format.RGB565, 
				RGlobal.window.getWidth(),
				RGlobal.window.getHeight(),
				false);
		screenObjects = new ArrayList<ScreenShowable>();
		tint = new Color(0, 0, 0, 1);
		shapes = new ShapeRenderer();
		cam = new TrackerCam(RGlobal.window.getWidth(), RGlobal.window.getHeight());
		
		uiCam = new OrthographicCamera();
		uiCam.setToOrtho(false, RGlobal.window.getWidth(), RGlobal.window.getHeight());
		uiCam.zoom = RGlobal.window.getZoom();
		uiCam.position.x = RGlobal.window.getViewportWidth() / 2;
		uiCam.position.y = RGlobal.window.getViewportHeight() / 2;
		uiCam.update();
		uiBatch.setProjectionMatrix(uiCam.combined);
	}
	
	/**
	 * Called whenever this screen stops being the top screen on the stack. The
	 * screen will stop receiving player commands. Default does nothing.
	 */
	public void onFocusLost() {
		// nothing
	}
	
	/**
	 * Called whenever this screen starts being the top screen on the stack. The
	 * screen will start receiving player commands. Default does nothing.
	 */
	public void onFocusGained() {
		// nothing
	}
	
	/**
	 * Sets the z value (depth) of the screen. Higher z values are rendered
	 * later.
	 * @param 	z			The new z-value
	 */
	public void setZ(float z) {
		this.z = z;
	}
	
	/**
	 * Returns the z value (depth) of the screen. Higher z values are rendered
	 * later.
	 * @return					The current z-value
	 */
	public float getZ() {
		return z;
	}
	
	/** @return The camera this screen uses to render */
	public TrackerCam getCamera() { return cam; }
	
	/** @return Batch used for rendering contents of the viewport */
	public SpriteBatch getViewBatch() { return batch; }
	
	/** @return Batch used for UI components */
	public SpriteBatch getUIBatch() { return uiBatch; }
	
	/** @return Buffer used for rendering contents */
	public FrameBuffer getBuffer() { return buffer; }
	
	/** @return Game screen whole tint */
	public Color getTint() { return tint; }
	
	/** @return The shader used to render maps */
	public ShaderProgram getMapShader() { return mapShader; }
	
	/**
	 * Gets the command parser used on this screen. Usually only used by engine.
	 * @return					The command parser used on this screen
	 */
	public CommandMap getCommandContext() {
		return commandContext;
	}
	
	/**
	 * Sets the command parser used on the screen. Usually only used by engine.
	 * @param 	map				The command map to use instead
	 */
	public void setCommandContext(CommandMap map) {
		if (commandContext != null) {
			RGlobal.keymap.unregisterListener(commandContext);
		}
		this.commandContext = map;
		RGlobal.keymap.registerListener(commandContext);
	}
	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Screen other) {
		if (z < other.z) {
			return (int) Math.floor(z - other.z) - 1;
		} else if (z > other.z) {
			return 0;
		} else {
			return (int) Math.floor(z - other.z) + 1;
		}
	}

	/**
	 * The root of all evil. We supply the camera.
	 */
	public void render() {
		if (!initialized) {
			RGlobal.reporter.warn("Forgot to intialize screen " + this);
		}
		cam.update(0);
		batch.setProjectionMatrix(cam.combined);
		WindowSettings window = RGlobal.window;
		buffer.begin();
		Gdx.gl.glClearColor(15.f/255.f, 9.f/255.f, 7.f/255.f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		shapes.setColor(15.f/255.f, 9.f/255.f, 7.f/255.f, 1);
		shapes.begin(ShapeType.Filled);
		shapes.rect(0, 0, window.getWidth(), window.getHeight());
		shapes.end();
		canvas.render(cam);
		for (ScreenShowable pic : screenObjects) {
			pic.render(cam);
		}
		buffer.end();
		privateBatch.setColor(tint);
		privateBatch.begin();
		// oh god I'm so sorry
		privateBatch.draw(
				buffer.getColorBufferTexture(),			// texture
				0, 0,									// x/y in screen space
				0, 0,									// origin x/y screen
				window.getResolutionWidth(), window.getResolutionHeight(),	// width/height screen
				1, 1,									// scale x/y
				0,										// rotation in degrees
				0, 0,									// x/y in texel space
				window.getWidth(), window.getHeight(),	// width/height texel
				false, true								// flip horiz/vert
			);
		privateBatch.end();
	}

	/**
	 * @see net.wombatrpgs.rainfall.graphics.Renderable#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		//canvas.queueRequiredAssets(manager);
	}

	/**
	 * @see net.wombatrpgs.rainfall.graphics.Renderable#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		//canvas.postProcessing(manager);
	}
	
	/**
	 * @see net.wombatrpgs.rainfall.core.Updateable#update(float)
	 */
	@Override
	public void update(float elapsed) {
		cam.update(elapsed);
		canvas.update(elapsed);
		for (ScreenShowable pic : screenObjects) {
			pic.update(elapsed);
		}
		RGlobal.keymap.update(elapsed);
	}

	/**
	 * @see net.wombatrpgs.rainfall.io.CommandListener#onCommand
	 * (net.wombatrpgs.rainfallschema.io.data.InputCommand)
	 */
	@Override
	public void onCommand(InputCommand command) {
		// default does nothing I think
	}

	/**
	 * Changes the screen's canvas.
	 * @param 	newCanvas		The new renderable canvas
	 */
	public void setCanvas(ScreenShowable newCanvas) {
		this.canvas = newCanvas;
	}
	
	/**
	 * @see net.wombatrpgs.rainfall.graphics.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		batch.dispose();
		privateBatch.dispose();
		buffer.dispose();
		uiBatch.dispose();
	}

	/**
	 * Adds a screen-showable picture to the screen.
	 * @param 	screenObject	The object to add
	 */
	public void addScreenObject(ScreenShowable screenObject) {
		screenObjects.add(screenObject);
	}
	
	/**
	 * Removes a screen-showable picture from the screen.
	 * @param	screenObject	The object to add
	 */
	public void removePicture(ScreenShowable screenObject) {
		if (screenObjects.contains(screenObject)) {
			screenObjects.remove(screenObject);
		} else {
			RGlobal.reporter.warn("Tried to remove non-existant picture from screen: " + screenObject);
		}
	}

	/**
	 * Run some final safety checks and finish initialization. Call once during
	 * the constructor.
	 */
	protected final void init() {
		if (canvas == null) {
			RGlobal.reporter.warn("No canvas for screen " + this);
		}
		if (commandContext == null) {
			RGlobal.reporter.warn("No command context for screen " + this);
		} else {
			commandContext.registerListener(this);
		}
		// TODO: load with a loading bar
		this.queueRequiredAssets(RGlobal.assetManager);
		RGlobal.assetManager.finishLoading();
		this.postProcessing(RGlobal.assetManager, 0);
		initialized = true;
	}

}
