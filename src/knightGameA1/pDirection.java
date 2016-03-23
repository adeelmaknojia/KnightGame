package knightGameA1;

import java.awt.Point;

public enum pDirection {
	UP, LEFT,RIGHT, DOWN;
	
	/**
	 * Returns the opposite direction of the one given
	 * @param other
	 * @return
	 */
	public static pDirection opposite(pDirection other) {
		if (other == pDirection.UP) return pDirection.DOWN;
		else if (other == pDirection.RIGHT) return pDirection.LEFT;
		else if (other == pDirection.DOWN) return pDirection.UP;
		else return pDirection.RIGHT;	// LEFT
	}
	
	/**
	 * Knight moves the given point in the given direction
	 * @param p
	 * @param direction
	 */
	public static void movePoint(Point p, pDirection direction) {
		if (direction == pDirection.LEFT) { p.x -= 1; }
		else if (direction == pDirection.RIGHT) {p.x += 1; }
		else if (direction == pDirection.DOWN) { p.y += 1;}
		else if (direction == pDirection.UP) { p.y -= 1;}
	}
}