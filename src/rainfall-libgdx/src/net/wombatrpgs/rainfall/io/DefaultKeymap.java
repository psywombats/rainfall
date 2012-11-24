/**
 *  DefaultKeymap.java
 *  Created on Nov 23, 2012 3:33:18 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.io;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Input.Keys;

/**
 * The default keyboard bindings for Rainfall.
 */
public class DefaultKeymap extends Keymap {
	
	private Map<Integer, InputButton> map;
	
	/**
	 * Creates and initializes the default keymap.
	 */
	public DefaultKeymap() {
		map = new HashMap<Integer, InputButton>();
		
		// movamant
		map.put(Keys.UP, 		InputButton.UP);
		map.put(Keys.DOWN, 		InputButton.DOWN);
		map.put(Keys.LEFT, 		InputButton.LEFT);
		map.put(Keys.RIGHT, 	InputButton.RIGHT);
		
		// buttans
		map.put(Keys.Z, 		InputButton.BUTTON_1);
		map.put(Keys.X, 		InputButton.BUTTON_2);
		map.put(Keys.C, 		InputButton.BUTTON_3);
		map.put(Keys.V, 		InputButton.BUTTON_4);
		map.put(Keys.S, 		InputButton.BUTTON_5);
		map.put(Keys.D, 		InputButton.BUTTON_6);
	}

	/**
	 * @see net.wombatrpgs.rainfall.io.Keymap#keyDown(int)
	 */
	@Override
	public boolean keyDown(int keycode) {
		if (map.containsKey(keycode)) {
			this.signal(map.get(keycode), true);
		}
		return super.keyDown(keycode);
	}

	/**
	 * @see net.wombatrpgs.rainfall.io.Keymap#keyUp(int)
	 */
	@Override
	public boolean keyUp(int keycode) {
		if (map.containsKey(keycode)) {
			this.signal(map.get(keycode), false);
		}
		return super.keyUp(keycode);
	}
	
	

}