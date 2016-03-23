package knightGameA1.AI;

import java.awt.Point;
import java.util.Comparator;
import java.util.LinkedList;

import knightGameA1.knightMoves;

/**
 * Depending on the type, assigns a Node a heuristic value
 * and can compare Nodes based off this value
 * 
 */
public class NodeHeuristic implements Comparator<Node> {
	public enum Type {
		EUCLIDEANDISTANCE, MANHATTAN, MANHATTANDISTANCE
	}
	
	private Type type;
	
	// Assuming 8 possible moves are there for knight
	public final int COST_PER_MOVE = 8; // Heuristic should return less than this per move (takes 8 moves, should return less than 8*COST_PER_MOVE)
	
	public NodeHeuristic(Type type) {
		this.type = type;
	}
	
//	private boolean inSameDirection(Point start, Point end, knightMoves dir) {
//		switch (dir) {
//		case DOWN_LEFT:
//			return start.x == end.x && start.y <= end.y && // D
//				   start.y == end.y && start.x >= end.x;   // L
//				   
//		case DOWN_RIGHT:
//			return start.x == end.x && start.y <= end.y && // D
//				   start.y == end.y && start.x <= end.x;   // R
//			
//		case UP_RIGHT:
//			return start.x == end.x && start.y >= end.y && // U
//				   start.y == end.y && start.x <= end.x;   // R
//			
//		case UP_LEFT:
//			return start.x == end.x && start.y >= end.y && // U
//				   start.y == end.y && start.x >= end.x;   // L
//				   
//		case LEFT_UP:
//			return start.y == end.y && start.x >= end.x && // L
//				   start.x == end.x && start.y >= end.y;   // U
//			
//		case LEFT_DOWN:
//			return start.y == end.y && start.x >= end.x && // L
//				   start.x == end.x && start.y <= end.y;   // D
//			
//		case RIGHT_UP:
//			return start.y == end.y && start.x <= end.x && // R
//				   start.x == end.x && start.y >= end.y;   // U
//		case RIGHT_DOWN:
//			return start.y == end.y && start.x <= end.x && // R
//			       start.x == end.x && start.y <= end.y;   // D
//		}
//		return false;
//	}
//	
//	private boolean inOppositeDirection(Point start, Point end, knightMoves dir) {
//		switch (dir) {
//		
//		case DOWN_LEFT:
//			return start.x == end.x && start.y > end.y && // D
//				   start.y == end.y && start.x < end.x;   // L
//				   
//		case DOWN_RIGHT:
//			return start.x == end.x && start.y > end.y && // D
//				   start.y == end.y && start.x > end.x;   // R
//			
//		case UP_RIGHT:
//			return start.x == end.x && start.y < end.y && // U
//				   start.y == end.y && start.x > end.x;   // R
//			
//		case UP_LEFT:
//			return start.x == end.x && start.y < end.y && // U
//				   start.y == end.y && start.x < end.x;   // L
//				   
//		case LEFT_UP:
//			return start.y == end.y && start.x < end.x && // L
//				   start.x == end.x && start.y < end.y;   // U
//			
//		case LEFT_DOWN:
//			return start.y == end.y && start.x < end.x && // L
//				   start.x == end.x && start.y > end.y;   // D
//			
//		case RIGHT_UP:
//			return start.y == end.y && start.x > end.x && // R
//				   start.x == end.x && start.y < end.y;   // U
//		case RIGHT_DOWN:
//			return start.y == end.y && start.x > end.x && // R
//			       start.x == end.x && start.y > end.y;   // D
//		}
//		return false;
//	}
		
	/**
	 * Returns the exact diagonal distance between the knight 
	 * and the closest piece of pawn
	 * @param n
	 * @return
	 */
	private int distanceToGoal(Node n) {
		int minDistance = Integer.MAX_VALUE;
		LinkedList<Point> goals = n.state.pawnLocations;
		Point knight = n.state.knight;
		
		for (Point goal : goals) {
			int distanceToGoal = (int) knight.distance(goal.x, goal.y);
			if (distanceToGoal < minDistance) {
				minDistance = distanceToGoal;
			}
		}
		return minDistance * COST_PER_MOVE;
	}
	
	/**
	 * Calculates distance by moving horizontally and vertically to goal
	 * Returns distance to closest goal
	 * @param n
	 * @return
	 */
	private int manhattanDistance(Node n) {
		int minDistance = Integer.MAX_VALUE;
		LinkedList<Point> goals = n.state.pawnLocations;
		Point knight = n.state.knight;
		
		for (Point goal : goals) {
			
			int distanceToGoal = Math.abs(goal.x-knight.x) + Math.abs(goal.y-knight.y);
			if (distanceToGoal < minDistance) {
				minDistance = distanceToGoal;
			}
		}
		return minDistance * COST_PER_MOVE;
	}
	
	public int getHeuristic(Node node) {
		switch (type) {
		case EUCLIDEANDISTANCE:
			return distanceToGoal(node);
		case MANHATTAN:
			return manhattanDistance(node);
		case MANHATTANDISTANCE:
			return (int) ((distanceToGoal(node) + manhattanDistance(node)) / 2.0);
		}
		return 0;
	}

	@Override
	public int compare(Node o1, Node o2) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;
		
		if (o1 == o2) {
			return EQUAL;
		}
		
		// f(N) = g(N) + h(N)
		int valueOne = o1.totalCostToGoal + getHeuristic(o1);
		int valueTwo = o2.totalCostToGoal + getHeuristic(o2);

		if (valueOne < valueTwo) {
			return BEFORE;
		}
		else if (valueOne > valueTwo) {
			return AFTER;
		}
		return EQUAL;
	}

}
