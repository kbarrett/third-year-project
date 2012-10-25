package ch.idsia.agents.controllers.kbarrett;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;

public class AStarTestClass {
	
	static boolean debug = FirstAgent.debug;
	static MapSquare[][] map = new MapSquare[5][5];
	static int[] marioMapLoc = {0,0};
	
	public static void main(String[] args)
	{
		byte[][] levelScene = {{0,0,Encoding.WALL,Encoding.WALL, 0},{Encoding.WALL,0,0,Encoding.WALL,Encoding.WALL},{0,0,Encoding.WALL,Encoding.WALL, 0},{Encoding.WALL,0,0,0, 0}, {0,0,0,0,0}};
		
		int[] marioMapLoc = {2,2};
		MapUpdater.updateMap(map, levelScene, marioMapLoc);
		
		aStar(map[3][3]);
	}
	
	public static MapSquare[] aStar(MapSquare destination)
	{
		TreeSet<MapSquareWrapper> exploredSquares = new TreeSet<MapSquareWrapper>(new Comparator<MapSquareWrapper>(){

			@Override
			public int compare(MapSquareWrapper msw1, MapSquareWrapper msw2) {
				return Integer.compare(msw1.getH() + msw1.getG(), msw2.getH() + msw2.getG());
			}});
		
		LinkedList<MapSquareWrapper> expandedSquares = new LinkedList<MapSquareWrapper>();
		
		MapSquareWrapper initialSquare = new MapSquareWrapper(map[marioMapLoc[0]][marioMapLoc[1]], (MapSquareWrapper)null, 0);
		initialSquare.setG(0);
		exploredSquares.add(initialSquare);
		
		while(!exploredSquares.isEmpty())
		{
			MapSquareWrapper currentSquare = exploredSquares.pollFirst();
			System.out.println("Taking square: " +currentSquare);
			if(currentSquare.equals(destination))
			{
				if(debug)
				{
					System.out.println("We fucking found " + destination + " with G : " + currentSquare.getG() + " with route: ");
					MapSquare[] result = currentSquare.backtrackRouteFromHere();
					for(int i = 0; i<result.length; i++)
					{
						System.out.println(""+result[i]);
					}
				}
				return currentSquare.backtrackRouteFromHere();
			}
			for(MapSquare s : currentSquare.getMapSquare().getReachableSquares(currentSquare.getLevelInJump()))
			{
				System.out.println("s is " + s);
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
		if(debug)
			System.err.println("Oh shit. We didn't find " + destination + " ..." + destination.getEncoding());
		return null;
	}

}
