package knightGameA1.AI;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import knightGameA1.Globals;
import knightGameA1.knightMoves;
import knightGameA1.Logic.Board;

public class Search {
	public enum SearchType {
		BreadthFirst, DepthFirst, AStar
	}
	
	public static NodeHeuristic.Type HEURISTIC_TYPE = NodeHeuristic.Type.EUCLIDEANDISTANCE; 
	
	public static LinkedList<Node> bfsSearch(State initialState) {		
		return myBackTrackingSearch(initialState, SearchType.BreadthFirst);
	}
	
	public static LinkedList<Node> dfsSearch(State initialState) {		
		return myBackTrackingSearch(initialState, SearchType.DepthFirst);
	}
	
	public static LinkedList<Node> aStarSearch(State initialState) {
		System.out.println("Search of type : AStar, Heuristic: " + HEURISTIC_TYPE);
		long startTime = System.currentTimeMillis();
		
		long nodesProcessed = 1;
		
		// The object that calculates the heuristics of the nodes using the given heuristic type
		NodeHeuristic heuristic = new NodeHeuristic(HEURISTIC_TYPE);
		
		// At least this many moves possible
		PriorityQueue<Node> open = new PriorityQueue<Node>(Globals.BOARD_DIMENSIONS*Globals.BOARD_DIMENSIONS, heuristic);		
		HashSet<Node> closed = new HashSet<Node>();
		
		HashMap<Node, Integer> totalCostToGoal = new HashMap<Node, Integer>();		// G in the equation f(N) = g(N) + h(N)
		
		// Used to recursively update cost if a node's parent is changed
		HashMap<Node, LinkedList<Node>> nodeToChildren = new HashMap<Node, LinkedList<Node>>();
		
		Node currentNode = new Node(initialState, null);
		open.add(currentNode);
		totalCostToGoal.put(currentNode, currentNode.totalCostToGoal);
		
		Node goalNode = null;
		
		// Repeat until a goal Node is found or there's nothing left to process
		while (goalNode == null && open.size() > 0) {
			// Get the node with the smallest f(N) value (totalCostSoFarToGoal + heuristicToGoal)
			currentNode = open.poll();
			
			if (currentNode.state.isGoalState()) {
				goalNode = currentNode;
				break;
			}
			
			// Get the children and process them
			LinkedList<Node> allChildren = getNodeChildren(currentNode);
			closed.add(currentNode);		// Don't expand it again
			int newCost = currentNode.totalCostToGoal + heuristic.COST_PER_MOVE;	// Total cost so far plus the cost to move to this position
			
			LinkedList<Node> storedChildren = nodeToChildren.get(currentNode);
			if (storedChildren == null) {
				storedChildren = new LinkedList<Node>();
				nodeToChildren.put(currentNode, storedChildren);
			}
			
			// Check if Children already visited
			for (Node child : allChildren) {
				boolean inClosed = closed.contains(child);
				boolean inOpen = open.contains(child);
				
				// Haven't seen this child before
				if (!inClosed && !inOpen) {
					storedChildren.add(child);
					child.totalCostToGoal = newCost;
					totalCostToGoal.put(child, child.totalCostToGoal);	// For retrieval later
					open.add(child);
					++nodesProcessed;
					continue;
				}
				
				// Seen this child before				
				int oldCost = totalCostToGoal.get(child);
				
				// If this new path is better than old one, use it instead
				if (newCost < oldCost) {
					child.parent = currentNode;

					// Updated child's parent, recursively update it and all of its children's cost
					LinkedList<Node> childrenToRecurse = new LinkedList<Node>();
					Node recurseChild = child;
					while (true) {
						// Get this current child's children
						LinkedList<Node> recurseChildren = nodeToChildren.get(recurseChild);
						for (Node rChild : recurseChildren) {
							// Add to the children to recurse
							childrenToRecurse.addLast(rChild);
						}
						
						// Make child parentCost + COST
						int recurseChildNewCost = totalCostToGoal.get(recurseChild.parent) + heuristic.COST_PER_MOVE;
						recurseChild.totalCostToGoal = recurseChildNewCost;
						totalCostToGoal.put(recurseChild, recurseChildNewCost);
						
						// Update object in open/closed list
						if (open.contains(recurseChild)) {
							open.remove(recurseChild);		// Removing old object which doesn't have updated cost
							open.add(recurseChild);			// Adding new object which has updated cost
						}
						else {
							closed.remove(recurseChild);
							closed.add(recurseChild);
						}
						
						// Finished recursing this child
						childrenToRecurse.pop();
						
						// Process next child to recurse
						if (childrenToRecurse.size() > 0) {
							recurseChild = childrenToRecurse.getFirst();
						}
						// Stop when all children recursed
						else {
							break;
						}
					}
				}
			}			
		}
		if (goalNode != null) {
			// This is when there is no solution path, we have to make one starting from
			// solutionNode and going up
			LinkedList<Node> solutionPath = new LinkedList<Node>();
			currentNode = goalNode;
			while (currentNode.parent != null) {
				solutionPath.addFirst(currentNode);
				currentNode = currentNode.parent;
			}
			
			System.out.println("Solution found in " + solutionPath.size() + " moves");
			System.out.println("Solution took " + ((System.currentTimeMillis() - startTime)/1000.0) + " seconds to solve");
			System.out.println("Expanded " + nodesProcessed + " nodes");

			return solutionPath;
		}
		System.out.println("No solution found, took " + ((System.currentTimeMillis() - startTime)/1000.0) + " seconds to solve");
		
		return null;
	}
	
	/**
	 * Uses iterative recursion (not functional recursion) to find a path from the initialState
	 * to the goalState (pawns). Uses different search types depending on the type parameter
	 * Options are:
	 * 
	 * DepthFirst Search
	 * BreadthFirst Search
	 * A* Search
	 * @param initialState
	 * @param type
	 * @return
	 */
	private static LinkedList<Node> myBackTrackingSearch(State initialState, SearchType type) {
		System.out.println("Commencing search of type : " + type);
		long startTime = System.currentTimeMillis();
		
		long nodesProcessed = 1;
		
		Node currentState = new Node(initialState, null);
		Node goalNode = null;
		
		// Either made recursively by DepthFirst or using Goal node and its parents for BreadthFirst
		LinkedList<Node> solutionPath = new LinkedList<Node>();
		
		// Current nodes left to process
		LinkedList<Node> nodeStack = new LinkedList<Node>();
		
		// Nodes that have already been expanded and shown not to be goal nodes
		HashSet<Node> visitedNodes = new HashSet<Node>();
		
		// Nodes that have been in the node stack but not necessarily expanded
		HashSet<Node> nodeStackSet = new HashSet<Node>();

		// Start with current state
		nodeStack.addFirst(currentState);

		while (nodeStack.size() > 0) {
			if (currentState.state.isGoalState()) {
				switch (type) {
				case BreadthFirst:
					// Store this in order to compute solution Path
					goalNode = currentState;
					break;
				case DepthFirst:
					// Recursive solution path is backwards, reverse it and remove initial state
					Collections.reverse(solutionPath);
					System.out.println("Solution found in " + solutionPath.size() + " moves");
					System.out.println("Solution took " + ((System.currentTimeMillis() - startTime)/1000.0) + " seconds to solve");
					System.out.println("Expanded " + nodesProcessed + " nodes");
					return solutionPath;
				default:
					break;
				}
				break;
			}
			LinkedList<Node> allChildren = getNodeChildren(currentState);
			LinkedList<Node> children = new LinkedList<Node>();
			visitedNodes.add(currentState);		// Don't expand it again
			
			// Check if Children already visited
			for (Node child : allChildren) {
				
				if (visitedNodes.contains(child) == false && nodeStackSet.contains(child) == false/* && nodeStack.contains(child) == false*/) {
					children.add(child);
					++nodesProcessed;
				}
			}
			// No children we haven't already seen
			if (children.isEmpty()) {				
				// Remove the first node and make current state it
				// Repeat until its a node that hasn't been expanded yet
				while (visitedNodes.contains(currentState)) {
					nodeStack.pop();
					currentState = nodeStack.peek();
					
					if (type == SearchType.DepthFirst) {
						// Remove these nodes from the solution path as well
						if (solutionPath.size() > 0 ) {
							solutionPath.pop();
						}
					}
				}
			}
			// There are new children
			else {
				switch (type) {
				case BreadthFirst:
					// Add to end (queue) and remove the first one (done processing it)
					for (Node child : children) {
						nodeStackSet.add(child);
						nodeStack.addLast(child);
					}
					nodeStack.pop();
					break;
				case DepthFirst:
					// Add to front of list (stack)
					for (Node child : children) {
						nodeStackSet.add(child);
						nodeStack.push(child);
					}					
					break;
				default:
					break;				
				}
				// Set to the new first node
				currentState = nodeStack.peek();
			}
			// Keep track of nodes for solution path
			if (type == SearchType.DepthFirst) {
				solutionPath.push(currentState);
			}
		}
		if (goalNode != null) {
			// This is when there is no solution path, we have to make one starting from
			// solutionNode and going up
			currentState = goalNode;
			while (currentState.parent != null) {
				solutionPath.addFirst(currentState);
				currentState = currentState.parent;
			}
			
			System.out.println("Solution found in " + solutionPath.size() + " moves");
			System.out.println("Solution took " + ((System.currentTimeMillis() - startTime)/1000.0) + " seconds to solve");
			System.out.println("Expanded " + nodesProcessed + " nodes");
			return solutionPath;
		}
		System.out.println("No solution found");
		return null;
	}
	
	/**
	 * For the given node, get the next node in all the directions the knight
	 * can move in.
	 * 
	 * Don't bother using the opposite direction of the given node, since 
	 * that is simply going backwards. It doesn't add anything to the search
	 * @param state
	 * @return
	 */
	private static LinkedList<Node> getNodeChildren(Node state) {
		LinkedList<Node> children = new LinkedList<Node>();

		knightMoves[] directions = new knightMoves[] { knightMoves.LEFT_UP,knightMoves.LEFT_DOWN, 
													 knightMoves.UP_LEFT,knightMoves.UP_RIGHT,
													 knightMoves.RIGHT_UP, knightMoves.RIGHT_DOWN,
													 knightMoves.DOWN_LEFT, knightMoves.DOWN_RIGHT};
		
		knightMoves oppositeDirection;
		if (state.action != null) {
			oppositeDirection = knightMoves.opposite(state.action);
		}
		// When state is initialState, action is null
		else {
			// Avoid moving in opposite direction the knight is currently facing
			oppositeDirection = knightMoves.opposite(state.state.knightOrientation.getFirst());
		}		
		 
		for (knightMoves dir : directions) {
			// Skip going backwards as we've presumably already seen that node
			if (dir.equals(oppositeDirection)) {
				continue;
			}
			State child = Board.simulateMove(new State(state.state), dir);
			
			if (child.isHittingWall == false) {
				children.add(new Node(child, dir, state));
			}
		}
		
		return children;
	}
}
