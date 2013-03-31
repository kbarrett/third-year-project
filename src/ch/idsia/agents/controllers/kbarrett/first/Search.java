package ch.idsia.agents.controllers.kbarrett.first;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;

import ch.idsia.agents.controllers.kbarrett.first.MapSquare.Direction;
/**
 * Used to perform a search.
 * @author Kim Barrett
 */
public class Search
{
	/**
	 * The multiplicative difference between the maximum g and h values.
	 */
	private static final int DIVISOR = 3;
	/**
	 * The default maximum g value allowed by the search.
	 */
	private static final int DEFAULT_CUT_OFF_POINT = 15;
	/**
	 * Perform an A* Search to find a path between the start and destination MapSquare.
	 * Uses a default cut-off length of {@link #DEFAULT_CUT_OFF_POINT}.
	 * @param destination - the final location
	 * @param start - the start location
	 * @param marioMode - Mario's current mode.
	 * @return the path required to move from start to destination.
	 */
	public static Stack<MapSquare> aStar(MapSquare destination, MapSquare start, int marioMode)
	{
		return aStar(destination, start, DEFAULT_CUT_OFF_POINT, marioMode);
	}
	/**
	 * Perform an A* Search to find a path between the start and destination MapSquare.
	 * @param destination - the final location
	 * @param start - the start location
	 * @param cutOffPoint - the maximum length of path.
	 * @param marioMode - Mario's current mode.
	 * @return the path required to move from start to destination.
	 */
	public static Stack<MapSquare> aStar(MapSquare destination, MapSquare start, int cutOffPoint, int marioMode)
	{
		//Squares that have been encountered but not expanded.
		TreeSet<MapSquareWrapper> exploredSquares = new TreeSet<MapSquareWrapper>(new Comparator<MapSquareWrapper>(){

			@Override
			public int compare(MapSquareWrapper msw1, MapSquareWrapper msw2)
			{
				//Sort using heuristic values.
				int compare = Float.compare(msw1.getH() + msw1.getG(), msw2.getH() + msw2.getG());
				if(compare == 0 && !msw1.equals(msw2))
				{
					return 1; //Give arbitrary order to elements with same heuristic value.
				}
				else
				{
					return compare;
				}
			}});
		//Squares that have been expanded.
		LinkedList<MapSquareWrapper> expandedSquares = new LinkedList<MapSquareWrapper>();
		//Wrap the start square to give it initial heuristic values.
		MapSquareWrapper initialSquare = new MapSquareWrapper(start, null, 0, 0, Direction.Above);
		initialSquare.setG(0);
		initialSquare.calculateH(destination);
		exploredSquares.add(initialSquare);
		//While there are still squares to be expanded
		while(!exploredSquares.isEmpty())
		{
			//Get the first (this will be the one with least heuristic value.
			MapSquareWrapper currentSquare = exploredSquares.pollFirst();
			//If we've found the goal, work out the path to reach it.
			if(currentSquare.equals(destination))
			{
				return currentSquare.backtrackRouteFromHere();
			}
			//Calculate the squares that can be reached from the current one.
			ArrayList<MapSquare> reachableSquares = currentSquare.getMapSquare().getReachableSquares(
					currentSquare.getLevelInJump(), currentSquare.getWidthOfJump(),
					currentSquare.getDirection(),
					marioMode);
			
			for(MapSquare s : reachableSquares)
			{
				int levelInJump = 0;
				int widthOfJump = 0;
				if(isAbove(currentSquare, s))
				{
					//Mario has to jump to reach
					levelInJump = currentSquare.getLevelInJump() + 1;
				}
				if(currentSquare.getLevelInJump() > 0 && (s.equals(currentSquare.getMapSquare().getSquareLeft()) || s.equals(currentSquare.getMapSquare().getSquareRight())))
				{
					//Mario is jumping & moving sideways
					widthOfJump = currentSquare.getWidthOfJump() + 1;
				}
				
				Direction enteredFrom = getDirection(s, currentSquare);
				MapSquareWrapper msw = new MapSquareWrapper(s, currentSquare, levelInJump, widthOfJump, enteredFrom);
				
				//If we've encountered this square before and expanded it there is a shorter path to reach it
				//If it's in the ancestry tree of the current square, then we have entered a loop.
				if(s == null || expandedSquares.contains(s) || msw.checkParentTreeFor(s))
				{
					continue;
				}
				//Calculate the heuristic values.
				if(msw.getH() == -1)
				{
					msw.calculateH(destination);
				}
				//Prevents Mario from jumping when it's unnecessary.
				if(isAbove(currentSquare, s))
				{
					msw.setG(currentSquare.getG() + 1.1f);
				}
				else
				{
					msw.setG(currentSquare.getG() + 1);
				}
				//If we haven't past the cut-off distance travelled, add this square to the list of encountered squares.
				if(msw.getG() < cutOffPoint || msw.getH() < (int)(cutOffPoint / DIVISOR))
				{
					exploredSquares.add(msw);
				}
			}
			//We have now expanded the currentSquare.
			expandedSquares.add(currentSquare);
		}
		return null;
	}
	/**
	 * @param currentSquare
	 * @param s
	 * @return true if s is above currentSquare and false otherwise
	 */
	private static boolean isAbove(MapSquareWrapper currentSquare, MapSquare s)
	{
		return s.equals(currentSquare.getMapSquare().getSquareAbove());
	}
	/**
	 * @param s
	 * @param currentSquare
	 * @return which direction s if from current square.
	 */
	private static Direction getDirection(MapSquare s, MapSquareWrapper currentSquare)
	{
		if(s.equals(currentSquare.getMapSquare().getSquareBelow()))
		{
			return Direction.Above;
		}
		else if(s.equals(currentSquare.getMapSquare().getSquareAbove()))
		{
			return Direction.Below;
		}
		else if(s.equals(currentSquare.getMapSquare().getSquareLeft()))
		{
			return Direction.Right;
		}
		else if(s.equals(currentSquare.getMapSquare().getSquareRight()))
		{
			return Direction.Left;
		}
		else
		{
			return Direction.None;
		}
	}
}
