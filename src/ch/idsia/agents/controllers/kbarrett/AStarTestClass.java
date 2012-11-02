package ch.idsia.agents.controllers.kbarrett;

public class AStarTestClass {
	
	static boolean debug = FirstAgent.debug;
	static MapSquare[][] map = new MapSquare[5][5];
	static int[] marioMapLoc = {0,0};
	
	public static void main(String[] args)
	{
		byte[][] levelScene = {
				{Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING, 	Encoding.NOTHING, Encoding.NOTHING, Encoding.NOTHING,	Encoding.NOTHING, Encoding.NOTHING},
				{Encoding.WALL,		Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,Encoding.NOTHING,Encoding.NOTHING,	Encoding.WALL,	 Encoding.NOTHING},
				{Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,Encoding.NOTHING,Encoding.NOTHING,	Encoding.WALL, 	Encoding.NOTHING},
				{Encoding.NOTHING,	Encoding.WALL,		Encoding.WALL,		Encoding.WALL,	 	Encoding.NOTHING, Encoding.NOTHING,Encoding.NOTHING,	Encoding.WALL, 	Encoding.NOTHING}, 
				{Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,	Encoding.WALL,		Encoding.NOTHING, Encoding.NOTHING,Encoding.NOTHING,	Encoding.WALL, 	Encoding.NOTHING},
				{Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,	Encoding.WALL,		Encoding.NOTHING, 	Encoding.NOTHING,Encoding.NOTHING,Encoding.WALL, 	Encoding.NOTHING},
				{Encoding.NOTHING,	Encoding.NOTHING,	Encoding.NOTHING,	Encoding.WALL,		Encoding.NOTHING, Encoding.NOTHING,Encoding.NOTHING,	Encoding.WALL, 	Encoding.NOTHING}};
		
		//only used to put levelScene into map, actual map pos is global
		int[] marioMapLoc = {3,4};
		map = MapUpdater.updateMap(map, levelScene, marioMapLoc);
		
		Search.aStar(map[6][8], map[AStarTestClass.marioMapLoc[0]][AStarTestClass.marioMapLoc[1]]);
		
	}

}
