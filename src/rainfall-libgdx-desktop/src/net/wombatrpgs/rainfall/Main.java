package net.wombatrpgs.rainfall;

import net.wombatrpgs.rainfall.core.RainfallGame;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * Autogenerated by project setup.
 */
public class Main {
	
	public static final String WARMUP_NAME = "One moment please...";
	public static final int WARMUP_WIDTH = 320;
	public static final int WARMUP_HEIGHT = 240;
	
	/**
	 * Main launcher. Autogenerated at one point.
	 * @param 	args		Unusused
	 */
	public static void main(String[] args) {
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = WARMUP_NAME;
		cfg.useGL20 = true;
		cfg.width = WARMUP_WIDTH;
		cfg.height = WARMUP_HEIGHT;
		cfg.stencil = 8;
		
		new LwjglApplication(new RainfallGame(new DesktopFocusReporter()), cfg);
	}
}
