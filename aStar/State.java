import java.util.ArrayList;
import java.lang.Math;

/**
 * A state in the search represented by the (x,y) coordinates of the square and
 * the parent. In other words a (square,parent) pair where square is a Square,
 * parent is a State.
 * 
 * You should fill the getSuccessors(...) method of this class.
 * 
 */
public class State {

	private Square square;
	private State parent;

	// Maintain the gValue (the distance from start)
	// You may not need it for the DFS but you will
	// definitely need it for AStar
	private int gValue;

	// States are nodes in the search tree, therefore each has a depth.
	private int depth;

	/**
	 * @param square
	 *            current square
	 * @param parent
	 *            parent state
	 * @param gValue
	 *            total distance from start
	 */
	public State(Square square, State parent, int gValue, int depth) {
		this.square = square;
		this.parent = parent;
		this.gValue = gValue;
		this.depth = depth;
	}

	/**
	 * @param visited
	 *            closed[i][j] is true if (i,j) is already expanded
	 * @param maze
	 *            initial maze to get find the neighbors
	 * @return all the successors of the current state
	 */
	public ArrayList<State> getSuccessors(boolean[][] closed, Maze maze) {
		// FILL THIS METHOD

		// TODO check all four neighbors (up, right, down, left)
		// TODO return all unvisited neighbors
		// TODO remember that each successor's depth and gValue are
		// +1 of this object.
		
		ArrayList<State> openSuccessors = new ArrayList<State>();

		int row = getX();
		int col = getY();


		//left
		if(col>0 && !closed[row][col-1])
		{
			if(maze.getSquareValue(row,col-1) != '%')
				openSuccessors.add(createState(row,col-1));
		}
		//down
		if(row<maze.getNoOfRows()-1 && !closed[row+1][col])
		{
			if(maze.getSquareValue(row+1,col) != '%')
				openSuccessors.add(createState(row+1,col));
		}
		//right
		if(col<maze.getNoOfCols()-1 && !closed[row][col+1])
		{
			if(maze.getSquareValue(row,col+1) != '%')
				openSuccessors.add(createState(row,col+1));
		}
		//up
		if(row>0 && !closed[row-1][col])
		{
			if(maze.getSquareValue(row-1,col) != '%')
				openSuccessors.add(createState(row-1,col));
		}

		//debug
		/*System.out.println("curr: " + this.getX() + " " + this.getY());
		for(int i = 0; i<openSuccessors.size(); i++)
		{
			State k = openSuccessors.get(i);
			System.out.println(k.getX() + " " + k.getY());
		}
		*/
		return openSuccessors;
		
	}

	/**
       	 * create a new state to add to successors for getSuccessors() function
	 * @param i, j
	 * 	int's i and j representing position relative to this state
	 * @return state to add to successors of this
	 */
	private State createState(int i, int j)
	{
		State successor = new State(new Square(i,j),this, gValue+1, depth+1);
		return successor;
	}

	/**
	 * @return x coordinate of the current state
	 */
	public int getX() {
		return square.X;
	}

	/**
	 * @return y coordinate of the current state
	 */
	public int getY() {
		return square.Y;
	}

	/**
	 * @param maze initial maze
	 * @return true is the current state is a goal state
	 */
	public boolean isGoal(Maze maze) {
		if (square.X == maze.getGoalSquare().X
				&& square.Y == maze.getGoalSquare().Y)
			return true;

		return false;
	}
	
	/**
	 * @return value from heuristic function, heuristic is given to be admissible
	 */
	public int calculateH(Maze maze)
	{
		//heuristic function = |x1-x2| + |y1-y2|
		int goalX = maze.getGoalSquare().X;
		int goalY = maze.getGoalSquare().Y;
		int h = Math.abs(this.getX()-goalX) + Math.abs(this.getY() - goalY);
		return h;
	}

	/**
	 * @return the current state's square representation
	 */
	public Square getSquare() {
		return square;
	}

	/**
	 * @return parent of the current state
	 */
	public State getParent() {
		return parent;
	}

	/**
	 * You may not need g() value in the DFS but you will need it in A-star
	 * search.
	 * 
	 * @return g() value of the current state
	 */
	public int getGValue() {
		return gValue;
	}

	/**
	 * @return depth of the state (node)
	 */
	public int getDepth() {
		return depth;
	}
	
	public boolean equals(State b)
	{
		if(this.getX() == b.getX() && this.getY() == b.getY())
			return true;
		else
			return false;

	}
}
