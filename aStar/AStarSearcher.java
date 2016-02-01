import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public AStarSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main a-star search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {

		// FILL THIS METHOD

		// CLOSED list is a Boolean array that indicates if a state associated with a given position in the maze has already been expanded. 
		boolean[][] closed = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
		// ...

		// OPEN list (aka Frontier list)
		PriorityQueue<StateFValuePair> open = new PriorityQueue<StateFValuePair>();

		// TODO initialize the root state and add
		// to OPEN list
		// ...
		Square startPos = maze.getPlayerSquare();
		State initState = new State(startPos,null,0,0);
		StateFValuePair initPair = new StateFValuePair(initState,(initState.getGValue() + initState.calculateH(maze)));
		open.add(initPair);


		while (!open.isEmpty()) {
			// TODO return true if a solution has been found
			// TODO maintain the cost, noOfNodesExpanded,
			// TODO update the maze if a solution found
			StateFValuePair curr = open.poll();
			noOfNodesExpanded++;
			closed[curr.getState().getX()][curr.getState().getY()] = true;
			State currState = curr.getState();
			double fval = curr.getFValue();

			
			// use open.poll() to extract the minimum stateFValuePair.
			// use open.add(...) to add stateFValue pairs
				
			if(currState.isGoal(maze))
			{
				LinkedList<State> parentTrace = new LinkedList<State>();
				State parent = currState.getParent();
				//add all the parents excluding root and goal to this trace
				while(parent.getParent()!=null)
				{
					parentTrace.addFirst(parent);
					parent = parent.getParent();
				}
				cost = currState.getGValue();
				Iterator<State> pathStep = parentTrace.iterator();
				while(pathStep.hasNext())
				{
					State currStep = pathStep.next();
					maze.setOneSquare(currStep.getSquare(),'.');
				}
				return true;
			}
			else//not expanded goal
			{
				ArrayList<State> kids = currState.getSuccessors(closed,maze);
				Iterator<State> itr = kids.iterator();
				//for all the successors of the current state
				while(itr.hasNext())
				{
					State currKid = itr.next();
					int kidFval = currKid.getGValue() + currKid.calculateH(maze);
					//if it has not yet been expanded
					if(!closed[currKid.getX()][currKid.getY()])
					{
						//now compare child to pre-existing frontier nodes to ensure no duplicates
						Iterator<StateFValuePair> frontieritr = open.iterator();
						boolean matched = false;
						while(!matched && frontieritr.hasNext())
						{
							StateFValuePair frontiernode = frontieritr.next();
							//if you find a matching state, compare Fvalues
							if(currKid.equals(frontiernode.getState()) && frontiernode.getFValue() > kidFval)
							{
								open.remove(frontiernode);
								open.add(new StateFValuePair(currKid, kidFval));
								matched = true;
							}
						}
						//if it was matched, you already added it in the lines above(where you are comparing kids to frontier)
						//if you didnt match it, then it doesnt exist yet, so add it now
						if(!matched)
						{
							open.add(new StateFValuePair(currKid,kidFval));
						}
					}
				}
			}
		}

		// TODO return false if no solution
		return false;
	}

}
