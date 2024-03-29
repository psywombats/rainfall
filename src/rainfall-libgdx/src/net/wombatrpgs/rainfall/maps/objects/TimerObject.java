/**
 *  TimerObject.java
 *  Created on Jan 30, 2013 2:43:17 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.maps.objects;

import java.util.ArrayList;
import java.util.List;

import net.wombatrpgs.rainfall.core.RGlobal;
import net.wombatrpgs.rainfall.maps.MapThing;
import net.wombatrpgs.rainfall.maps.PauseLevel;

/**
 * A timer counts down to zero, then sends messages off to its listeners. It
 * updates in the context of a level that it's a part of.
 */
public class TimerObject extends MapThing {
	
	protected List<TimerListener> listeners;
	protected MapThing parent;
	protected float timeRemaining; // in seconds
	protected float lastTime; // in seconds
	protected boolean running, completed;
	
	/**
	 * A pre-built timer package! Sets some time on the clock, registers the
	 * listener, then starts counting down. Auto-kills itself when time runs
	 * out the first time.
	 * @param remaining				The time to notify after elapsed
	 * @param parent				The thing to jetison from once elapsed
	 * @param listener				The single listener to notify when done
	 */
	public TimerObject(float remaining, MapThing parent, TimerListener listener) {
		this(remaining);
		addListener(listener);
		set(true);
		this.parent = parent;
		this.parent.getLevel().addObject(this);
		if (parent.getLevel().isPaused()) {
			this.setPauseLevel(PauseLevel.PAUSE_RESISTANT);
		}
		completed = false;
	}

	/**
	 * Creates a stopped timer with supplied time on the clock.
	 * @param 	remaining			The time remaining on the clock (in seconds)
	 */
	public TimerObject(float remaining) {
		this();
		this.timeRemaining = remaining;
	}
	
	/**
	 * Create a timer with 0 seconds that's paused.
	 */
	public TimerObject() {
		running = false;
		timeRemaining = 0f;
		listeners = new ArrayList<TimerListener>();
	}
	
	/**
	 * @see net.wombatrpgs.rainfall.maps.MapThing#update(float)
	 */
	@Override
	public void update(float elapsed) {
		super.update(elapsed);
		timeRemaining -= elapsed;
		if (timeRemaining <= 0) {
			reset();
			set(false);
			for (TimerListener listener : listeners) {
				listener.onTimerZero(this);
			}
			if (parent != null && parent.getLevel() != null) {
				parent.getLevel().removeObject(this);
			}
			completed = true;
		}
	}
	
	/** @return The time currently on the clock */
	public float getTime() {
		return timeRemaining;
	}

	/**
	 * Sets the remaining time on the clock. Does not deal with pause status.
	 * @param remaining
	 */
	public void setTime(float remaining) {
		this.timeRemaining = remaining;
		this.lastTime = remaining;
	}
	
	/**
	 * Resets the time on the clock to whatever it was set to last, or zero if
	 * nothing was ever set. Does not deal with pause status.
	 */
	public void reset() {
		this.timeRemaining = lastTime;
	}
	
	/**
	 * Stops or starts the timer.
	 * @param 	run				True to start the clock, false to pause/stop it
	 */
	public void set(boolean run) {
		this.running = run;
	}
	
	/**
	 * Detetermines whether or not this timer is "complete," where complete
	 * here means it's been run at least once to completion.
	 * @return					True if timer completed at least once
	 */
	public boolean hasCompleted() {
		return completed;
	}
	
	/**
	 * Registers a listener to be notified when this timer fires.
	 * @param 	listener		The listener to register.
	 */
	public void addListener(TimerListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Unregisters a listener so that it no longer recieves events.
	 * @param 	listener		The listener to unregister
	 */
	public void removeListener(TimerListener listener) {
		if (listeners.contains(listener)) { 
			listeners.remove(listener);
		} else {
			RGlobal.reporter.warn("Removed a non-listening timer: " + listener);
		}
	}

}
