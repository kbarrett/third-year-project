package ch.idsia.agents.controllers.kbarrett;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;

import ch.idsia.agents.controllers.kbarrett.MapSquare.Direction;

public class Search {
	
	private static final int DIVISOR = 3;
	private static final int DEFAULT_CUT_OFF_POINT = 10;
	
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
		
		MapSquareWrapper initialSquare = new MapSquareWrapper(start, null, 0, 0);
		initialSquare.setG(0);
		initialSquare.calculateH(destination);
		exploredSquares.add(initialSquare);
		
		while(!exploredSquares.isEmpty())
		{
			MapSquareWrapper currentSquare = exploredSquares.pollFirst();
			if(currentSquare.equals(destination))
			{
				if(FirstAgent.debug)
				{
					LevelSceneInvestigator.debugPrint("We fucking found " + destination + " with G : " + currentSquare.getG() + " with route: ");
					Stack<MapSquare> result = currentSquare.backtrackRouteFromHere();
					for(int i = result.size() - 1; i>=0; --i)
					{
						LevelSceneInvestigator.debugPrint(""+result.get(i));
					}
				}
				return currentSquare.backtrackRouteFromHere();
			}
			MapSquare.Direction enteredFrom = Direction.None;
			if(currentSquare.getParent() !=null)
			{
				if(currentSquare.equals(currentSquare.getParent().getMapSquare().getSquareBelow()))
				{
					enteredFrom = Direction.Below;
				}
				else if(currentSquare.equals(currentSquare.getParent().getMapSquare().getSquareAbove()))
				{
					enteredFrom = Direction.Above;
				}
				else if(currentSquare.equals(currentSquare.getParent().getMapSquare().getSquareLeft()))
				{
					enteredFrom = Direction.Left;
				}
				else if(currentSquare.equals(currentSquare.getParent().getMapSquare().getSquareRight()))
				{
					enteredFrom = Direction.Right;
				}
			}
			
			for(MapSquare s : currentSquare.getMapSquare().getReachableSquares(
					currentSquare.getLevelInJump(), currentSquare.getWidthOfJump(),
					enteredFrom))
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
				MapSquareWrapper msw = new MapSquareWrapper(s, currentSquare, levelInJump, widthOfJump);
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
			System.err.println("Oh shit. We didn't find " + destination + " ..." + destination.getEncoding());
		return null;
	}
}
