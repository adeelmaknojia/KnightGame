# KnightGame
Knight Game is a chess-based variation of the well known snake game using Artificial Intelligence.

Game Description 

In the original snake game, a snake moves around a board attempting to eat food without running into itself or the wall. 
In this variation of the game, the snake is replaced by a chess knight which must capture moving pawns instead of food.
The game is played on a grid board of varying size. The Artifical Intelligence controls a chess knight which moves in the 
standard way (two spaces in one direction and one space perpendicular,forming an L shape. A number of pawns pieces are also
present on the board, which are randomly pre-determined (on creation of the game) 

Algorithm used for Searching Pawns:

-	BFS (Breadth First Search)
    Find optimal path, results in low number of moves as compare to DFS, 
    but expands many nodes compare to A* algorithms, takes longer time. Consume more memory
  
-	DFS (Depth First Search) 
   takes longer path to find goal, results in high number of moves and expands many nodes, 
   but its quicker than BFS. Consume less memory
   
Heuristics used:
- Euclidean Distance: 
    Takes the knight position and returns the minimum Euclidean distance to one of the pawn
    locations (√(x^2 )+y^2). This heuristic find path diagonally to ensured shortest path to the goal

- Manhattan Distance: 
    Returns the minimum Manhattan distance to one of the pawns locations. This is involves going in a 
    straight line horizontally and vertically (instead of diagonally). It was chosen because it is 
    the simplest path the knight can take, and is always possible.
    
  
Developed by : Adeel Maknojia

