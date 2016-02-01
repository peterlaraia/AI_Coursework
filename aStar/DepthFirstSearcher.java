import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Depth-First Search (DFS)
 * 
 * You should fill the search() method of this class.
 */
public class DepthFirstSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public DepthFirstSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main depth first search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		// FILL THIS METHOD

		// CLOSED list is a 2D Boolean array that indicates if a state associated with a given position in the maze has already been expanded.
		boolean[][] closed = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

		// ...

		// Stack implementing the Frontier list
		LinkedList<State> stack = new LinkedList<State>();
		
		//push initial position
		Square startPos = maze.getPlayerSquare();
		State initState = new State(startPos,null,0, 0);
		stack.push(initState);

		while (!stack.isEmpty()) {
			// TODO return true if find a solution
			// TODO maintain the cost, noOfNodesExpanded
			// TODO update the maze if a solution found


			//debug
			//System.out.println(stack.toString());




			State curr = stack.pop();
			noOfNodesExpanded++;
			closed[curr.getX()][curr.getY()] = true;

			if(curr.isGoal(maze))
			{
				//create linked list to track parents backwards
				//use state.getParent() method
				LinkedList<State> parentTrace = new LinkedList<State>();
				State parent = curr.getParent();
				while(parent.getParent()!=null)
				{
					parentTrace.addFirst(parent);
					parent = parent.getParent();
				}
				cost = curr.getGValue();
				Iterator<State> pathStep = parentTrace.iterator();
				while(pathStep.hasNext())
				{
					State currStep = pathStep.next();
					maze.setOneSquare(currStep.getSquare(),'.');
				}
				return true;
			}
			else
			{
				
				ArrayList<State> kids = curr.getSuccessors(closed, maze);
				Iterator<State> itr = kids.iterator();


				while(itr.hasNext())
				{
					State currKid = itr.next();
					State parent = curr.getParent();
					boolean duple = false;
					while(parent != null)
					{
						if(parent.equals(currKid))
						{
							duple = true;
							break;
						}
						parent = parent.getParent();
					}
					


					if(!duple)
						stack.push(currKid);
				}



				//System.out.println("CYCLE END");
			}

			// use stack.pop() to pop the stack.
			// use stack.push(...) to elements to stack
		}

		// TODO return false if no solution
		return false;
	}
}
