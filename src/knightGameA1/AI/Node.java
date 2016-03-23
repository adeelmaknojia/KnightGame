package knightGameA1.AI;

import knightGameA1.knightMoves;

public class Node {
	public State state;
	public knightMoves action;
	public Node parent;			// Used to find solution path if starting from Goal node
	public int totalCostToGoal;
	/**
	 * Action can be null if this is the initial node
	 * @param state
	 * @param action
	 */
	public Node(State state, knightMoves action) {
		this.state = state;
		this.action = action;
	}
	
	/**
	 * Action and parent can be null if this is the initial node
	 * @param state
	 * @param action
	 * @param parent
	 */
	public Node(State state, knightMoves action, Node parent) {
		this.state = state;
		this.action = action;
		this.parent = parent;
	}
	
	/**
	 * Function to check if the node and given parameter are equals 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Node)) {
			return false;
		}
		
		Node other = (Node) obj;
		return state.equals(other.state);
	}
	
	@Override
	public int hashCode() {
		return state.hashCode();// + (action != null ? action.hashCode() : 0);
	}
	
	@Override
	public String toString() {
		return "(" + state + ", Action: " + action + ")";
	}
}
