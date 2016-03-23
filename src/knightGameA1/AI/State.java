package knightGameA1.AI;

import java.awt.Point;
import java.util.LinkedList;

import knightGameA1.knightMoves;
import knightGameA1.pDirection;

/**
 * Used to represent a single state in the game, used for path searching
 * 
 */
public class State {
	public Point knight;
	public LinkedList<Point> pawnLocations;
	public LinkedList<Point> wallLocations;
	public boolean isHittingWall;
	public LinkedList<knightMoves> knightOrientation;
	
	
	
	public State(Point knight, LinkedList<knightMoves> knightOrientation, LinkedList<Point> pawns, LinkedList<Point> wall) {
		this.knight = knight;
		this.knightOrientation = knightOrientation;
		this.pawnLocations = pawns;
		this.wallLocations = wall;
		isHittingWall = false;
	}
	
	/**
	 * Copy constructor
	 * @param other
	 */
	public State(State other) {
		knight = new Point(other.knight);
		knightOrientation = new LinkedList<knightMoves>(other.knightOrientation);
		pawnLocations = new LinkedList<Point>(other.pawnLocations);
		wallLocations = new LinkedList<Point>(other.wallLocations);
		isHittingWall = other.isHittingWall;
	}
	
	@Override
	public int hashCode() {
		return knight.hashCode() + knightOrientation.hashCode() + pawnLocations.hashCode() + wallLocations.hashCode();
	}
	
	/**
	 * Two states are equal if they have the exact same values
	 * @param other
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof State)) return false;
		State other = (State) obj;
		return (knight.equals(other.knight) && 
				knightOrientation.equals(other.knightOrientation) &&
				pawnLocations.equals(other.pawnLocations) && 
				wallLocations.equals(other.wallLocations));
	}
	
	/**
	 * Returns true if any of the pawns locations
	 * are equal with the  of the knight
	 * @return
	 */
	public boolean isGoalState() {
		for (Point pawns : pawnLocations) {
			if (knight.equals(pawns)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "knight : " + knight + " knight Orientation: " + knightOrientation;}
}
