package knightGameA1;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;

import knightGameA1.AI.Node;
import knightGameA1.AI.Search;
import knightGameA1.Logic.*;
import knightGameA1.listener.CollisionListener;
import controlP5.ControlEvent;
import processing.core.PApplet;

@SuppressWarnings("serial")
public class Drawer extends PApplet implements CollisionListener {
	private ArrayList<GameObject> gameObjects;
	private Board gameBoard;
	private LinkedList<Node> solution;
	private GameOptions options;
	private boolean continueSolving = false;
	private boolean noSolution;
	private boolean solveAgain;		// To let the draw loop finish when solution done and next time start again

	private Search.SearchType previousSearchType;
	
	private boolean gameOver = false;
	private int currentFrame = 0;
	private static final int autoFrameRate = 24;
	private static final int normalFrameRate = 60;
	private int currentFrameRate = normalFrameRate;
	
	@Override
	public void setup() {
		//size(Globals.BOARD_SIZE + Globals.OPTIONS_PANEL_WIDTH, Globals.BOARD_SIZE);
		size(500,600);
		options = new GameOptions();
		options.createGUI(this);
		startGame();
	}
	
	private void startGame() {
		options.startGame();
		noSolution = false;
		solveAgain = false;
		solution = new LinkedList<Node>();
		//currentFrameRate = Math.max(4, currentFrameRate - 80);
		frameRate(currentFrameRate);
		
		gameBoard = new Board();
		gameBoard.setCollisionListener(this);

		gameObjects = new ArrayList<GameObject>();
		gameObjects.add(gameBoard);
		gameObjects.add(options);
	}

	@Override
	public void draw() {
		
		// Find solution before
		if (solveAgain && continueSolving) {
			solveAgain = false;
			continueSolving();
		}
		
		// If a solution exists
		if (solution.size() > 0) {
			gameBoard.changeknightDirection(solution.pop().action);
			if (solution.size() == 0) {
				solveAgain = true;
			}
		}
		
		if (gameOver == false) {
			// Draw components
			for (GameObject g: gameObjects) {
				g.draw(this);
			}
			
			// Update components
			for (GameObject g : gameObjects) {
				g.update();
			}
		}
	}

	@Override
	public void onCollision() {
		gameOver = true;
		noLoop();
	}
	
	@Override
	public void mouseClicked() {
		if (gameOver || noSolution) {
			gameOver = false;
			noSolution = false;
			startGame();
			loop();
		}
	}
	
	
	private void continueSolving() {
		switch (previousSearchType) {
		case BreadthFirst:
			BFS();
			break;
		case DepthFirst:
			DFS();
			break;
		case AStar:
			aStarSearch();
			break;
		}
	}
	
	private void finishedSearch() {
		// Display statistics and stuff
		System.out.println("---------------------------------------------");
		if (solution != null) {
			noSolution = false;
		}
		else {
			// Display no solution
			solution = new LinkedList<Node>();
			noSolution = true;
		}
	}
		
	public void continueSolving(float[] a) {
		continueSolving = options.getContinueSolve();
	}
	
	public void boardDimensions(float value) {
		options.setBoardDimensions((int)value);
	}
	
	public void numPawns(float value) {
		options.pawns = (int) value;
	}
	
	public void BFS() {
		previousSearchType = Search.SearchType.BreadthFirst;		
		if (solution.size() == 0) {
			solution = Search.bfsSearch(gameBoard.getCurrentState());
			finishedSearch();
		}
	}
	
	public void DFS() {
		previousSearchType = Search.SearchType.DepthFirst;
		if (solution.size() == 0) {
			solution = Search.dfsSearch(gameBoard.getCurrentState());
			finishedSearch();
		}
	}

	public void aStarSearch() {
		previousSearchType = Search.SearchType.AStar;
		if (solution.size() == 0) {
			solution = Search.aStarSearch(gameBoard.getCurrentState());
			finishedSearch();
		}
	}
	
	public void controlEvent(ControlEvent theEvent) {
		options.handleEvent(theEvent);
	}

}
