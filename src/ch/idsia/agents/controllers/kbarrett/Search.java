package ch.idsia.agents.controllers.kbarrett;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;

public class Search {

	public static MapSquare[] aStar(MapSquare destination, MapSquare start)
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
		
		MapSquareWrapper initialSquare = new MapSquareWrapper(start, null, 0);
		initialSquare.setG(0);
		exploredSquares.add(initialSquare);
		
		while(!exploredSquares.isEmpty())
		{
			MapSquareWrapper currentSquare = exploredSquares.pollFirst();
			if(currentSquare.equals(destination))
			{
				if(FirstAgent.debug)
				{
					System.out.println("We fucking found " + destination + " with G : " + currentSquare.getG() + " with route: ");
					MapSquare[] result = currentSquare.backtrackRouteFromHere();
					for(int i = 0; i<result.length; i++)
					{
						LevelSceneInvestigator.debugPrint(""+result[i]);
					}
				}
				return currentSquare.backtrackRouteFromHere();
			}
			for(MapSquare s : currentSquare.getMapSquare().getReachableSquares(
					currentSquare.getLevelInJump(), 
					currentSquare.getParent()==null || currentSquare.equals(currentSquare.getParent().getMapSquare().getSquareBelow())))
			{
				int levelInJump = 0;
				if(currentSquare.getMapSquare().getSquareAbove() == s)
				{
					levelInJump = currentSquare.getLevelInJump() + 1;
				}
				MapSquareWrapper msw = new MapSquareWrapper(s, currentSquare, levelInJump);
				if(s == null || expandedSquares.contains(s) || msw.checkParentTreeFor(s))
				{
					continue;
				}
				if(msw.getH() == -1) {msw.calculateH(destination);}
				msw.setG(currentSquare.getG() + 1);
				exploredSquares.add(msw);
			}
			expandedSquares.add(currentSquare);
		}
		if(FirstAgent.debug)
			System.err.println("Oh shit. We didn't find " + destination + " ..." + destination.getEncoding());
		return null;
	}
}
