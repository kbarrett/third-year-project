package ch.idsia.agents.controllers.kbarrett.test;

import java.util.ArrayList;

import ch.idsia.agents.controllers.kbarrett.Encoding;
import ch.idsia.agents.controllers.kbarrett.FirstAgent;
import ch.idsia.agents.controllers.kbarrett.MapSquare;
import ch.idsia.agents.controllers.kbarrett.MapUpdater;
import ch.idsia.agents.controllers.kbarrett.Search;

public class AStarTestClass {
	
	static boolean debug = FirstAgent.debug;
	static ArrayList<ArrayList<MapSquare>> map = new ArrayList<ArrayList<MapSquare>>(7);
	static int[] marioMapLoc = {5,3};
	
	public static void main(String[] args)
	{
		/*byte[][] levelScene = {
				{Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING, 	Encoding.NOTHING, Encoding.NOTHING, Encoding.NOTHING,	Encoding.NOTHING, Encoding.NOTHING},
				{Encoding.WALL,		Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,Encoding.NOTHING,Encoding.NOTHING,	Encoding.WALL,	 Encoding.NOTHING},
				{Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,Encoding.NOTHING,Encoding.NOTHING,	Encoding.WALL, 	Encoding.NOTHING},
				{Encoding.NOTHING,	Encoding.WALL,		Encoding.WALL,		Encoding.WALL,	 	Encoding.NOTHING, Encoding.NOTHING,Encoding.NOTHING,	Encoding.WALL, 	Encoding.NOTHING}, 
				{Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,	Encoding.WALL,		Encoding.NOTHING, Encoding.NOTHING,Encoding.NOTHING,	Encoding.WALL, 	Encoding.NOTHING},
				{Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,	Encoding.WALL,		Encoding.NOTHING, 	Encoding.NOTHING,Encoding.NOTHING,Encoding.WALL, 	Encoding.NOTHING},
				{Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,	Encoding.WALL,		Encoding.NOTHING, Encoding.NOTHING,Encoding.NOTHING,	Encoding.WALL, 	Encoding.NOTHING}};
		*/
		
		byte[][] levelScene = {
					{0, 	0,	 0,	  0,   0, 	0,	 0},
					{0, 	0,	 0,	  0,   0, 	0,	 0},
					{0, 	0,	 0,	  0,   2, 	0,	 0},
					{0, 	0,	 0,/**/0, -60, 	0, 	 0},
					{0, 	0,	 0,	  0, -60,	0, 	 0},
					{0, 	0,	 0,	  0, -60, 	0, 	 0},
					{-60, -60, -60, -60, -60, -60, -60}};
		
		//only used to put levelScene into map, actual map pos is global
		int[] marioMapLoc = {3,3};
		map = MapUpdater.updateMap(map, levelScene, marioMapLoc);
		
		Search.aStar(map.get(2).get(4), map.get(AStarTestClass.marioMapLoc[0]).get(AStarTestClass.marioMapLoc[1]));
		
	}

}