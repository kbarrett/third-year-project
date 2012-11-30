package ch.idsia.agents.controllers.kbarrett;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;

import ch.idsia.agents.controllers.kbarrett.MapSquare.Direction;

public class Search {
	
	private static final int DIVISOR = 3;
	private static final int DEFAULT_CUT_OFF_POINT = 15;
	
	public static Stack<MapSquare> aStar(MapSquare destination, MapSquare start)
	{
		return aStar(destination, start, DEFAULT_CUT_OFF_POINT);
	}

	public static Stack<MapSquare> aStar(MapSquare destination, MapSquare start, int cutOffPoint)
	{
		TreeSet<MapSquareWrapper> exploredSquares = new TreeSet<MapSquareWrapper>(new Comparator<MapSquareWrapper>(){

			@Override
			public int compare(MapSquareWrapper msw1, MapSquareWrapper msw2) {
				int compare = Integer.compare(msw1.getH() + msw1.getG(), msw2.getH() + msw2.getG());
				if(compare == 0 && !msw1.equals(msw2))
				{
					return 1;
				}
				else
				{
					return compare;
				}
			}});
		
		LinkedList<MapSquareWrapper> expandedSquares = new LinkedList<MapSquareWrapper>();
		
		MapSquareWrapper initialSquare = new MapSquareWrapper(start, null, 0, 0, Direction.Above);
		initialSquare.setG(0);
		initialSquare.calculateH(destination);
		exploredSquares.add(initialSquare);
		
		while(!exploredSquares.isEmpty())
		{
			MapSquareWrapper currentSquare = exploredSquares.pollFirst();
			if(currentSquare.equals(destination))
			{
				return currentSquare.backtrackRouteFromHere();
			}
			
			ArrayList<MapSquare> reachableSquares = currentSquare.getMapSquare().getReachableSquares(
					currentSquare.getLevelInJump(), currentSquare.getWidthOfJump(),
					currentSquare.getDirection());
			
			for(MapSquare s : reachableSquares)
			{
				int levelInJump = 0;
				int widthOfJump = 0;
				if(currentSquare.getMapSquare().getSquareAbove() == s)
				{
					levelInJump = currentSquare.getLevelInJump() + 1;
				}
				if(currentSquare.getLevelInJump() > 0 && (currentSquare.getMapSquare().getSquareLeft() == s || currentSquare.getMapSquare().getSquareRight() == s))
				{
					widthOfJump = currentSquare.getWidthOfJump() + 1;
				}
				
				Direction enteredFrom = Direction.None;
				if(s.equals(currentSquare.getMapSquare().getSquareBelow()))
				{
					enteredFrom = Direction.Below;
				}
				else if(s.equals(currentSquare.getMapSquare().getSquareAbove()))
				{
					enteredFrom = Direction.Above;
				}
				else if(s.equals(currentSquare.getMapSquare().getSquareLeft()))
				{
					enteredFrom = Direction.Left;
				}
				else if(s.equals(currentSquare.getMapSquare().getSquareRight()))
				{
					enteredFrom = Direction.Right;
				}
				MapSquareWrapper msw = new MapSquareWrapper(s, currentSquare, levelInJump, widthOfJump, enteredFrom);
				
				if(s == null || expandedSquares.contains(s) || msw.checkParentTreeFor(s))
				{
					continue;
				}
				if(msw.getH() == -1)
				{
					msw.calculateH(destination);
				}
				msw.setG(currentSquare.getG() + 1);
				if(msw.getG() < cutOffPoint || msw.getH() < (int)(cutOffPoint / DIVISOR))
				{
					exploredSquares.add(msw);
				}
			}
			expandedSquares.add(currentSquare);
		}
		if(FirstAgent.debug)
			System.err.println("Oh shit. We didn't find " + destination + " ..." + " from " + start);
		return null;
	}
}
