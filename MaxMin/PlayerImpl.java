/***************************************************************************************
  PlayerImpl.java
  Implement five functions in this file.
  ---------
  Licensing Information:  You are free to use or extend these projects for
  educational purposes provided that (1) you do not distribute or publish
  solutions, (2) you retain this notice, and (3) you provide clear
  attribution to UW-Madison.
 
  Attribution Information: The Take Stone Games was developed at UW-Madison.
  The initial project was developed by Jerry(jerryzhu@cs.wisc.edu) and his TAs.
  Current version with depthLimit and SBE was developed by Fengan Li(fengan@cs.wisc.edu)
  and Chuck Dyer(dyer@cs.wisc.edu)
  
*****************************************************************************************/

import java.util.*;

public class PlayerImpl implements Player {
	// Identifies the player
	private int name = 0;
	int n = 0;
	

	// Constructor
	public PlayerImpl(int name, int n) {
		this.name = 0;
		this.n = n;
	}

	// Function to find possible successors
	@Override
	public ArrayList<Integer> generateSuccessors(int lastMove, int[] takenList) 
	{
		ArrayList<Integer> successors = new ArrayList<Integer>();
                //if this is the first move, then the next possible moves are
                //all odd moves from 1-n/2
                //I use add(index,element) because i want to add in reverse(highest to lowest stones)
                //this allows, imo, for easier pruning in my implementation of max/min_value
		if(lastMove == -1)
		{
			for(int i = 1; i<takenList.length/2; i++)
			{
                            if(i%2==1)
                                successors.add(0,i);
			}
		}
                //else take multiples and factors of the previous move
		else
		{
			for(int i = 1; i<takenList.length; i++)
			{
				if(i<lastMove && lastMove%i==0 && takenList[i] == 0)
				{
					successors.add(0,i);
				}
				if(i>lastMove && i%lastMove == 0 && takenList[i] == 0)
				{
					successors.add(0,i);
				}
			}
		}
		return successors;
	}

	// The max value function
	@Override
	public double max_value(GameState s, int depthLimit) 
	{
                //if there exists a previous move, mark it as taken
                if(s.lastMove != -1)
                {
                    s.takenList[s.lastMove] = s.lastMove;
                }
		ArrayList<Integer> kids = generateSuccessors(s.lastMove,s.takenList);
                //if no successors, this is terminal state. return -1.0 because you have no moves, aka you lose
		if(kids.isEmpty())
		{
			s.leaf = true;
                        s.bestMove = -1;
			return -1.0;
		}
                //if you have reached a depth limit, evaluate this state
                else if(depthLimit == 0)
                {
                    s.leaf = false;
                    s.bestMove = -1;
                    return stateEvaluator(s);
                }
                else
                {
                    //double beta = 1000.0;
                    double alpha = -1000.0;
                   /* for(int i = 0; i<kids.size(); i++)
                    {
                        int curr = kids.get()
                        
                    }   
                    */
                    Iterator<Integer> itr = kids.iterator();
                    //if alpha reaches 1, then you have found the max possible return value
                    //there is no reason to continue searching the other children, because
                    //they will all be either less than or equal to what you have now
                    //since in the generate successors state, we added s.t. the list is 
                    //highest to lowest, and the program specs stated that, when 
                    //values are ==, you should return the highest stone, we have found the 
                    //best move and will not search the rest of the tree
                    while(itr.hasNext()&& alpha < 1)
                    {
                        int curr = itr.next();
                        double kidval = min_value(new GameState(s.takenList,curr),depthLimit-1);
                        //if the value you just found is better than your alpha, update 
                        if(kidval>alpha) 
                        {
                            alpha = kidval;
                            s.bestMove = curr;
                        }
                    }
                    return (double) alpha;
                }
	}

	// The min value function
	@Override
	public double min_value(GameState s, int depthLimit)
	{
                //this function is almost identical to max_value,
                //check above for explanations throughout
                if(s.lastMove != -1)
                {
                    s.takenList[s.lastMove] = s.lastMove;
                }
		ArrayList<Integer> kids = generateSuccessors(s.lastMove,s.takenList);
		if(kids.isEmpty())
		{
			s.leaf = true;
                        s.bestMove = -1;
			return 1.0;
		}
                else if(depthLimit == 0)
                {
                    s.leaf = false;
                    s.bestMove = -1;
                    return stateEvaluator(s);
                }
                else
                {
                    double beta = 1000.0;
                    //double alpha = -1000.0;
                    Iterator<Integer> itr = kids.iterator();
                    while(itr.hasNext() && beta>-1)
                    {
                        int curr = itr.next();
                        double kidval = max_value(new GameState(s.takenList,curr),depthLimit-1);
                        if(kidval<beta) 
                        {
                            beta = kidval;
                            s.bestMove = curr;
                        }
                    }
                    return (double) beta;
                }


	}
	
	// Function to find the next best move
	@Override
	public int move(int lastMove, int[] takenList, int depthLimit) {
                GameState curr = new GameState(takenList,lastMove);
                max_value(curr, depthLimit);
                return curr.bestMove;
                
	}
	
	// The static board evaluator function
	@Override
	public double stateEvaluator(GameState s)
	{
            //if 1 is available, neutral
            if(s.takenList[1]==0)
                return 0;
            //if the lastmove was 1, count number of remaining moves
            else if(s.lastMove == 1)
            {
                if(generateSuccessors(s.lastMove, s.takenList).size()%2==0)
                    return -0.5;
                else
                    return 0.5;
            }
            //if previous move was prime, check how many of its multiples are left
            else if(isPrime(s.lastMove))
            {
                ArrayList<Integer> kids = generateSuccessors(s.lastMove, s.takenList);
                Iterator<Integer> itr = kids.iterator();
                int multiplierCount = 0;
                while(itr.hasNext())
                {
                    int child = itr.next();
                    if(child>s.lastMove && child%s.lastMove==0 )
                        multiplierCount++;
                }
                if(multiplierCount%2 == 0)
                    return -0.7;
                else
                    return 0.7;
                
            }
            //last option, must be composite number, if so find largest prime factor
            //then use that prime number to find how many of its multiples are left
            else
            {
                ArrayList<Integer> kids = generateSuccessors(s.lastMove, s.takenList);
                Iterator<Integer> itr = kids.iterator();
                //biggestPrime
                int bPrime = 1;
                int multiplierCount = 0;
                for(int i = s.lastMove-1; i>0; i--)
                {
                    if(s.lastMove%i == 0 && isPrime(i))
                    {
                        bPrime = i;
                    }

                }
                while(itr.hasNext())
                {
                    int child = itr.next();
                    if(child>=bPrime && child%bPrime==0)
                        multiplierCount++;
                }
                if(multiplierCount%2==0)
                    return -0.6;
                else
                    return 0.6;

            }

	}

        /**
         * used to check if a value is prime
         * @parameter x value to check whether prime or not
         * @return true if prime, false if not prime
         */
        private static boolean isPrime(int x)
        {
            //first check if it is even
            //that way, in the for loop you can
            //do i+=2, b/c if you can divide a number by an even number
            //then you can also divide it by 2
            if(x%2==0) 
                return false;
            //only need to go up to i^2 because once i passes the sqrt(x)
            //you have already checked corresponding possible values
            for(int i = 3; i*i <= x; i+=2)
            {
                if(x%i==0)
                    return false;
            }
            return true;
        }
}
