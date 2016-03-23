package knightGameA1;

import java.awt.Point;

public enum knightMoves {
	UP_LEFT, UP_RIGHT,
	DOWN_LEFT, DOWN_RIGHT, 
	RIGHT_UP, RIGHT_DOWN, 
	LEFT_UP, LEFT_DOWN;
	
	/**
	 * Returns the opposite direction of the one given
	 * @param other
	 * @return
	 */
	public static knightMoves opposite(knightMoves other) {
		if (other == knightMoves.UP_LEFT) return knightMoves.DOWN_LEFT;
		else if (other == knightMoves.UP_RIGHT) return knightMoves.DOWN_RIGHT;
		else if (other == knightMoves.DOWN_LEFT) return knightMoves.UP_LEFT;
		else if (other == knightMoves.DOWN_RIGHT) return knightMoves.UP_RIGHT;
		else if (other == knightMoves.LEFT_UP) return knightMoves.RIGHT_UP;
		else if (other == knightMoves.LEFT_DOWN) return knightMoves.RIGHT_DOWN;
		else if (other == knightMoves.RIGHT_UP) return knightMoves.LEFT_UP;
		else return knightMoves.LEFT_DOWN;	// RIGHT_DOWN
	}
	
	/**
	 * Moves the given point in the given direction
	 * @param p
	 * @param direction
	 */
	public static void movePoint(Point p, knightMoves direction) {
		if (direction == knightMoves.UP_LEFT) { p.y -= 2; p.x -= 1; }
		else if (direction == knightMoves.UP_RIGHT) {p.y -= 2; p.x += 1; }
		else if (direction == knightMoves.DOWN_LEFT) { p.y += 2; p.x -= 1; }
		else if (direction == knightMoves.DOWN_RIGHT) { p.y += 2; p.x += 1; }
		else if (direction == knightMoves.RIGHT_UP){ p.x += 2; p.y -= 1; }
		else if (direction == knightMoves.RIGHT_DOWN){ p.x += 2; p.y += 1; }
		else if (direction == knightMoves.LEFT_UP) { p.x -= 2; p.y -= 1; }
		else if (direction == knightMoves.LEFT_DOWN) { p.x -= 2; p.y += 1; }
	}
}