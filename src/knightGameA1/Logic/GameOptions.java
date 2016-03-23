package knightGameA1.Logic;

import knightGameA1.Globals;
import knightGameA1.AI.NodeHeuristic;
import knightGameA1.AI.Search;
import processing.core.PApplet;
import controlP5.Button;
import controlP5.CheckBox;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.Numberbox;
import controlP5.RadioButton;

public class GameOptions implements GameObject {

	private ControlP5 cp5;
	private CheckBox gameBoard;
	private CheckBox continueSolve;
	RadioButton heuristics;
	private boolean enabled;
	
	private final int xOrigin = 0;
	private final int yOrigin = 500;
	private final int margin = 10;
	private boolean setInitialValues = false;

	public int boardDimensions = Globals.BOARD_DIMENSIONS;
	public int pawns = Globals.NUM_PAWNS;
	private Numberbox numPawnBox;
	
	//@SuppressWarnings("deprecation")
	public void createGUI(PApplet g) {	
		cp5 = new ControlP5(g);
		final int textColour = g.color(0);		
		final int uncheckedColour = g.color(0,255,0);
		int currentX = xOrigin + margin;
		int currentY = yOrigin + margin;
		
		gameBoard = cp5.addCheckBox("gameBoard")
					  	.setPosition(currentX, currentY)
					  	.setColorForeground(g.color(255))
					  	.setColorActive(uncheckedColour)
					  	.setColorLabel(textColour)
					  	.setSize(20, 20)
					  	.setItemsPerRow(1)
					  	.setSpacingColumn(30)
					  	.setSpacingRow(20);
		
		Button but;
		but = cp5.addButton("startGame")
				 .setPosition(currentX,currentY)
				 .setSize(50,30)
				 .setColorBackground(g.color(255,152,29))
				 .setColorCaptionLabel(g.color(0))
				 ;
		
		currentX += but.getWidth() + margin * 2;
							
		but = cp5.addButton("DFS")
				 .setPosition(currentX,currentY)
				 .setColorBackground(g.color(255,152,29))
				 .setColorCaptionLabel(g.color(0))
				 .setSize(40,30)
				 ;
		
		currentX += but.getWidth() + margin;
		
		but = cp5.addButton("BFS")
				 .setPosition(currentX,currentY)
				 .setColorBackground(g.color(255,152,29))
				 .setColorCaptionLabel(g.color(0))
				 .setSize(40,30)
				 ;
		
		currentX += but.getWidth() + margin;
		
		but = cp5.addButton("aStarSearch")
				 .setPosition(currentX,currentY)
				 .setColorBackground(g.color(255,152,29))
				 .setColorCaptionLabel(g.color(0))
				 .setSize(80,30)
				 ;
		
		currentX += but.getWidth() + margin * 2;
				
		heuristics = cp5.addRadio("aStarHeuristic")
			  	.setPosition(currentX, currentY)
			  	.setColorForeground(g.color(0))
			  	.setColorActive(g.color(255))
			  	.setColorActive(uncheckedColour)
			  	.setColorLabel(textColour)
			  	.setSize(20, 20)
			  	.setItemsPerRow(1)
			  	.setSpacingRow(5)
			  	;
		
		
		int i = 0;
		for (NodeHeuristic.Type type : NodeHeuristic.Type.values()) {
			heuristics.addItem(type.toString(), i);
			++i;
		}
		
//		currentX += but.getWidth() + margin * 2 + 20;
//		
//		continueSolve = cp5.addCheckBox("continueSolving")
//			  	.setPosition(currentX, currentY)
//			  	.setColorForeground(g.color(255))
//			  	.setColorActive(uncheckedColour)
//			  	.setColorLabel(textColour)
//			  	.setSize(20, 20)
//			  	.setItemsPerRow(1)
//			  	.setSpacingColumn(30)
//			  	.setSpacingRow(20)
//			  	.addItem("Continue Solving", 0)
//			  	;
	}
	
	public void setBoardDimensions(int value) {
		boardDimensions = value;
		if (enabled) {
		numPawnBox.setMax(getMax());
		}
	}
	
	private int getMax() {
		if (boardDimensions < 15) {
			return boardDimensions;
		}
		return (int)(boardDimensions*boardDimensions * 0.2f);
	}
	
	@Override
	public void update() {
		if (setInitialValues == false) {
			gameBoard.toggle(0);
			enabled = true;
			setInitialValues = true;
		}
	}

	@Override
	public void draw(PApplet g) {

	}
	
	public void startGame() {
		if (setInitialValues) {
			Globals.BOARD_DIMENSIONS = boardDimensions;
			if (enabled) {
				Globals.NUM_PAWNS = pawns;
			}
			else {
				Globals.NUM_PAWNS = 1;
			}
		}
	}
		
	public boolean getContinueSolve() {
		return continueSolve.getState(0);
	}
	
	public void handleEvent(ControlEvent theEvent) {
		 if (theEvent.isGroup()) {
			 if (theEvent.getGroup() == heuristics) {
				 if((int)heuristics.getValue() >= 0){
					 Search.HEURISTIC_TYPE = NodeHeuristic.Type.values()[(int)heuristics.getValue()]; 
				 }
			 }
		 }
	}
}
