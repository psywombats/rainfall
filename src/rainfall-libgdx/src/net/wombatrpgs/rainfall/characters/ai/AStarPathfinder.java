/**
 *  AStarPathfinder.java
 *  Created on Mar 14, 2013 7:23:18 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.characters.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import net.wombatrpgs.rainfall.maps.Level;
import net.wombatrpgs.rainfallschema.maps.data.DirVector;
import net.wombatrpgs.rainfallschema.maps.data.Direction;

/**
 * Pathfinds towards a destination. Ooooh, spooky! It uses black magic to work
 * out a path. Actually it just uses heuristic search algorithms but w/e. This
 * is relatively expensive and if you call it every frame you're somewhat of an
 * idiot. (Actually it shouldn't be too bad because this isn't android) If there
 * are ever more ways of finding a path, this thing should implement the
 * Pathfinder interface.
 */
public class AStarPathfinder {
	
	protected Level map;
	protected int fromX, fromY;
	protected int toX, toY;
	protected int z;
	
	/**
	 * Sets up a new pathfinding task on a level. Does not actually evaluate
	 * anything unless you tell it to. You can probably reuse it by changing
	 * around its to/from. That should help for android optimizations if it
	 * ever comes to that.
	 * @param 	map				The map that this task runs on
	 * @param 	fromX			Where search starts from x (in tiles)
	 * @param 	fromY			Where search starts from y (in tiles)
	 * @param 	toX				Where search starts from x (in tiles)
	 * @param 	toY				Where search starts from y (in tiles)
	 * @param	z				The z-depth where everything takes place
	 */
	public AStarPathfinder(Level map, int fromX, int fromY, int toX, int toY, int z) {
		this.map = map;
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
		this.z = z;
	}
	
	/**
	 * Sets where this search is headed.
	 * @param 	toX				Where search is headed x (in tiles)
	 * @param 	toY				Where search is headed y (in tiles)
	 */
	public void setTarget(int toX, int toY) {
		this.toX = toX;
		this.toY = toY;
	}
	
	/**
	 * Sets where this search is from.
	 * @param 	fromX			Where search is from x (in tiles)
	 * @param 	fromY			Where search is from y (in tiles)
	 * @param	z				The z-depth where everything takes place
	 */
	public void setStart(int fromX, int fromY, int z) {
		this.fromX = fromX;
		this.fromY = fromY;
	}
	
	/**
	 * Calculates the path to the location. Woo hoo!
	 * @return					The steps to get to destination, or null if none
	 */
	public List<Direction> getPath() {
		Queue<Path> queue = new PriorityQueue<Path>();
		queue.add(new Path(toX, toY, fromX, fromY));
		// I can't believe I'm making a 2D array like this
		List<List<Boolean>> visited = new ArrayList<List<Boolean>>();
		for (int y = 0; y < map.getHeight(); y++) {
			visited.add(y, new ArrayList<Boolean>());
			for (int x = 0; x < map.getWidth(); x++) {
				visited.get(y).add(x, false);
			}
		}
		int nodes = 0;
		while (queue.size() > 0) {
			Path node = queue.poll();
			nodes++;
			if (visited.get(node.getAtY()).get(node.getAtX())) {
				// we've already been here
				continue;
			} else {
				visited.get(node.getAtY()).set(node.getAtX(), true);
				if (node.getAtX() == toX && node.getAtY() == toY) {
					//RGlobal.reporter.inform("Path found, expanded " + nodes);
					return node.getSteps();
				}
				for (Direction dir : Direction.values()) {
					DirVector vec = dir.getVector();
					int nextX = (int) (vec.x + node.getAtX());
					int nextY = (int) (vec.y + node.getAtY());
					if (nextX >= 0 && nextX < map.getWidth() &&
						nextY >= 0 && nextY < map.getHeight() &&
						!visited.get(nextY).get(nextX) &&
						map.isPassable(nextX, nextY, z)) {
						queue.add(new Path(node, dir));
					}
				}
			}
		}
		return null;
	}

}
