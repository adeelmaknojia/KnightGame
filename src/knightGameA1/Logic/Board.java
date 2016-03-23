package knightGameA1.Logic;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Stack;

import knightGameA1.Globals;
import knightGameA1.knightMoves;
import knightGameA1.pDirection;
import knightGameA1.AI.State;
import knightGameA1.listener.CollisionListener;
import processing.core.PApplet;
import processing.core.PImage;

public class Board implements GameObject {
	private enum eBoardObject {
		EMPTY, KNIGHT, WALL, PAWN, PATH
	}
	
	private eBoardObject[][] boardData;
	
	private Point knightPoint;
	private Stack<knightMoves> previousDirections;	// Used to move backwards
	private LinkedList<knightMoves> knight;			// A list of directions to represent the knight
	private int currentNumPawns = Globals.NUM_PAWNS;
	private LinkedList<Point> pawnLocations;
	private LinkedList<Point> wallLocations;
	
	private CollisionListener collisionListener;
	
	private boolean simulation;
	private boolean deadState;

	public Board() {
		boardData = new eBoardObject[Globals.BOARD_DIMENSIONS][Globals.BOARD_DIMENSIONS];
		knight = new LinkedList<knightMoves>();
		previousDirections = new Stack<knightMoves>();
		
		pawnLocations = new LinkedList<Point>();
		wallLocations = new LinkedList<Point>();
		
		simulation = false;		// Real board, make updates
		deadState = false;		// True if simulation and knight collided with something

		// Initialize board
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				if (i == 0 || i == Globals.BOARD_DIMENSIONS - 1 || j == 0 || j == Globals.BOARD_DIMENSIONS - 1) {
					boardData[i][j] = eBoardObject.WALL;
				}
				else {
					boardData[i][j] = eBoardObject.EMPTY;
				}
			}
		}
		createknight();
		createPawn();
		
	}
	
	/**
	 * Used for making a temporary board for simulating moves
	 * @param knightPoint
	 * @param pawnLocations
	 * @param wallLocations
	 */
	private Board(LinkedList<knightMoves> knight, Point knightPoint, LinkedList<Point> pLocations, LinkedList<Point> wallLocations) {
		simulation = true;		// Don't make updates
		this.knight = knight;
		this.knightPoint = knightPoint;
		this.pawnLocations = pLocations;
		this.wallLocations = wallLocations;
		previousDirections = new Stack<knightMoves>();
	}
	
	public void setCollisionListener(CollisionListener listener) {
		if (collisionListener != listener) {
			collisionListener = listener;
		}
	}
		
	/**
	 * Initializes the knight in the middle of the board facing UP
	 */
	private void createknight() {
		knightPoint = new Point((Globals.BOARD_DIMENSIONS / 2) - 1, (Globals.BOARD_DIMENSIONS / 2) - 1);
		knight.add(knightMoves.UP_LEFT);
		positionknight();
	}
	
	/**
	 * Places currentNumPawns pieces at random locations on the board
	 * that don't collide with anything else
	 */
	private void createPawn() {
		createPawnOrBlocks(eBoardObject.PAWN, currentNumPawns, 2, pawnLocations);
	}
	
	private void createPawnOrBlocks(eBoardObject objToCreate, int numToCreate, int distanceFromEdge, LinkedList<Point> pointList) {
		int x, y;
		eBoardObject obj;
		
		for (int i = 0; i < numToCreate; ++i) {
			do {
				x = Globals.random.nextInt(Globals.BOARD_DIMENSIONS - 2 - distanceFromEdge) + distanceFromEdge;
				y = Globals.random.nextInt(Globals.BOARD_DIMENSIONS - 2 - distanceFromEdge) + distanceFromEdge;
				obj = boardData[x][y];
			}
			while (obj == eBoardObject.KNIGHT || obj == eBoardObject.PAWN || obj == eBoardObject.WALL);
			
			boardData[x][y] = objToCreate;
			pointList.add(new Point(x, y));
		}
	}
	
	/**
	 * With the given point, sees if moving it in the given
	 * direction will cause a collision
	 * @param p
	 * @param direction
	 */
	private boolean testMove(Point p, knightMoves direction) {
		Point nextMove = new Point(p);
		knightMoves.movePoint(nextMove, direction);
		boolean movePoint = simulation;

		if (simulation == false) {
			eBoardObject obj = boardData[nextMove.x][nextMove.y];
			
			// See if we can move in the direction specified
			if (obj == eBoardObject.WALL) {
				movePoint = false;
			}
			else {
				movePoint = true;
			}
		}
		else {
			// Find out if next move will make this a dead state
			if (testDeadState(nextMove) == true) {
				deadState = true;
			}
		}
		if (movePoint) {
			// Either simulation or didn't hit a wall
			// Move it, even if dead state
			p.x = nextMove.x;
			p.y = nextMove.y;
			return true;
		}
		else {
			// Collision, fire event
			collisionListener.onCollision();
			return false;
		}
	}
	
	private void positionknight() {
		Point currentPoint = new Point(knightPoint);
		int i = 0;
		int length = knight.size();
		for (knightMoves s : knight) {
			knightMoves direction = knightMoves.opposite(s);
			
			// If this isn't the last direction in s, move currentPoint
			if (++i != length) {
				knightMoves.movePoint(currentPoint, direction);
			}
		}
		boardData[knightPoint.x][knightPoint.y] = eBoardObject.KNIGHT;
	}
	
	public void changeknightDirection(knightMoves direction) {
		knightMoves currentDirection = knight.getFirst();
		boolean isOppositeDirection = currentDirection == knightMoves.opposite(direction);

		// Only works if not moving in current direction or opposite of it
		if (currentDirection != direction) {
			if (!isOppositeDirection) {
				knight.set(0, direction);			// Change current direction
			}
			else if (Globals.CAN_KNIGHT_MOVE_BACKWARDS) {
				flipknight();
				moveknight();
				flipknight();
				
				if (isknightStraight()) {
					// No need to restore previous directions, just move back in a straight line
					previousDirections.clear();
				}
				
				// Restore previous direction if there is one
				if (previousDirections.size() > 0) {
					knight.set(0, previousDirections.pop());
				}
			}
		}
		
		// If auto move is true it will move on its own
		// If knight can move backwards allow opposite direction move
		// Otherwise if knight can't move backwards don't allow opposite direction move
		if (!Globals.IS_AUTO_MOVE && !isOppositeDirection) {
			if (Globals.CAN_KNIGHT_MOVE_BACKWARDS) {
				previousDirections.push(currentDirection);	// Save for moving backwards
			}
			moveknight();
		}
	}

	
	/**
	 * Takes the knight and makes all of its values be the opposite
	 */
	private void flipknight() {
		LinkedList<knightMoves> newknight = new LinkedList<knightMoves>();
		int size = knight.size();
		Point oldPoint = knightPoint;
		
		for (int i = 0; i < size; ++i) {
			knightMoves dir = knight.pop();
			newknight.push(knightMoves.opposite(dir));
		}
		knight = newknight;
		
		if (simulation == false) {
			boardData[knightPoint.x][knightPoint.y] = eBoardObject.KNIGHT;
		}
	}
	
	/**
	 * If the knight never changes directions it is straight
	 * @return
	 */
	private boolean isknightStraight() {
		knightMoves current = knight.getFirst();
		
		for (knightMoves dir : knight) {
			if (dir != current) {
				return false;
			}
		}		
		return true;
	}
	
	private void moveknight() {
		// Move knight
		knightMoves nextMove = knight.getFirst();
		Point oldPoint = new Point(knightPoint.x, knightPoint.y);
		
		if (testMove(knightPoint, nextMove) == false) {
			return;		// collided, stop the update
		}

		// Don't update board if simulation
		if (simulation == false) {
			// Test if capture pawns
			if (boardData[knightPoint.x][knightPoint.y] == eBoardObject.PAWN) {
				capturePawn();
			}
			
			// Update board by moved knight
			boardData[oldPoint.x][oldPoint.y] = eBoardObject.PATH;
			boardData[knightPoint.x][knightPoint.y] = eBoardObject.KNIGHT;
		}
		knight.addFirst(knight.getFirst());	// Duplicate  to next spot
		
		// Remove the last position
			knight.removeLast();
	}
	
	
	
	private void capturePawn() {
		pawnLocations.remove(knightPoint);
		if (--currentNumPawns == 0) {
			System.out.println("Game Over: All Pawns Captured");
		}
		int pawnsRemaining = Globals.NUM_PAWNS- (int)currentNumPawns;
		System.out.println("Total Pawns Captured: " + pawnsRemaining);
		System.out.println("----------------------------------------------");
	}

	@Override
	public void update() {
		if (Globals.IS_AUTO_MOVE) {
			moveknight();
		}
	}
	
	@Override
	public void draw(PApplet g) {
		final int gridWidth = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		final int gridHeight = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				eBoardObject obj = boardData[i][j];
				
				if (obj == eBoardObject.EMPTY) {
					g.fill(g.color(255));
				}
				else if (obj == eBoardObject.WALL) {
					g.fill(g.color(	160,82,45));
				}
				else if (obj == eBoardObject.PAWN) {
					g.fill(g.color(255,255,0));
				}
				else if (obj == eBoardObject.KNIGHT) {
					g.fill(g.color(0,100,0));
				}
				else if (obj == eBoardObject.PATH){
					g.fill(255,0,0);
					
				}
				g.rect(i*gridWidth, j*gridHeight, gridWidth, gridHeight);
			}
		}
		
	}
	
	/**************
	 * AI Section *
	 **************/
	public State getCurrentState() {
		State currentState = new State(new Point(knightPoint),new LinkedList<knightMoves>(knight),new LinkedList<Point>(pawnLocations), new LinkedList<Point>(wallLocations));
		currentState.isHittingWall = deadState;
		return currentState;
	}
	
	/**
	 * If simulation, determines if the simulated move
	 * will cause the knight to collide with itself
	 * 
	 * Since the simulation will move the knight regardless
	 * of collision, this is used simply to determine if
	 * the current state is dead or not
	 * 
	 * Also can't use the board since it is null for a simulation
	 * @param newknightPoint
	 * @return
	 */
	private boolean testDeadState(Point newknightPoint) {
		if (newknightPoint.x <= 0 || newknightPoint.y <= 0 ||
			newknightPoint.x >= Globals.BOARD_DIMENSIONS - 1 || newknightPoint.y >= Globals.BOARD_DIMENSIONS - 1) {
			return true;
		}
		
		// Check if the knight is touching a wall
		for (Point wall: wallLocations) {
			if (newknightPoint.equals(wall)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Create a temporary board with the current state and move
	 * the knight in the given direction
	 * Return the new state
	 * @param direction
	 * @return
	 */
	public static State simulateMove(State theState, knightMoves direction) {


		LinkedList<knightMoves> knight = theState.knightOrientation;
		Board newBoard = new Board(knight,theState.knight, theState.pawnLocations, theState.wallLocations);
		
		// Simulate move
		newBoard.changeknightDirection(direction);
		State newState = newBoard.getCurrentState();
		
		return newState;
	}
}
