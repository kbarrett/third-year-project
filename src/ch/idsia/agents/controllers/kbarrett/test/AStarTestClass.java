package ch.idsia.agents.controllers.kbarrett.test;

import ch.idsia.agents.controllers.kbarrett.Encoding;
import ch.idsia.agents.controllers.kbarrett.FirstAgent;
import ch.idsia.agents.controllers.kbarrett.MapSquare;
import ch.idsia.agents.controllers.kbarrett.MapUpdater;
import ch.idsia.agents.controllers.kbarrett.Search;

public class AStarTestClass {
	
	static boolean debug = FirstAgent.debug;
	static MapSquare[][] map = new MapSquare[7][7];
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
		
		Search.aStar(map[2][4], map[AStarTestClass.marioMapLoc[0]][AStarTestClass.marioMapLoc[1]]);
		
	}

}
