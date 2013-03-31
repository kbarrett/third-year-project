package ch.idsia.agents.controllers.kbarrett.first;

import java.util.ArrayList;

/**
 * Used to update the map from a levelScene.
 * 
 * @author Kim Barrett
 *
 */
public class MapUpdater
{
	/**
	 * The map to be updated.
	 */
	private static ArrayList<ArrayList<MapSquare>> map;
	/**
	 * The levelScene to be incorporated into it.
	 */
	private static byte[][] levelScene;
	/** 
	 * Mario's current location in the map.
	 */
	private static int[] marioMapLoc;
	
	//To prevent this constructor being used.
	private MapUpdater() {}
	
	/**
	 * Integrates the new levelScene into the current map & return the result.
	 * @param map - the previous map
	 * @param levelScene - the new levelScene
	 * @param marioMapLoc - mario's location in map
	 * @return
	 */
	public static ArrayList<ArrayList<MapSquare>> updateMap(ArrayList<ArrayList<MapSquare>> map, byte[][] levelScene, int[] marioMapLoc)
	{
		MapUpdater.map = map;
		MapUpdater.levelScene = levelScene;
		MapUpdater.marioMapLoc = marioMapLoc;
		updateMap();
		workOutReachableSquares();
		return MapUpdater.map;
	}
	
	/**
	 * Incorporates the given levelScene into the map.
	 */
	private static void updateMap()
	{
		increaseMapSize(); //if required
		//Updates the locations in the map with updated information contained in the levelscene.
		for(int i = 0; i < levelScene.length; ++i)
		{
			int levelSceneMidPoint0 = (levelScene.length / 2);
			for(int j = 0; j < levelScene[i].length; ++j)
			{
				int levelSceneMidPoint1 = (levelScene[i].length / 2);
				int y = getMapCoordinate(i,  marioMapLoc[0], levelSceneMidPoint0);
				int x = getMapCoordinate(j,  marioMapLoc[1], levelSceneMidPoint1);
				MapSquare square = map.get(y).get(x);
				if(square==null)
				{
					map.get(y).set(x, new MapSquare(levelScene[i][j], map, x, y));
				}
				else
				{
					square.setEncoding(levelScene[i][j]);
				}
			}
		}
		
	}
	/**
	 * Update which squares are reachable.
	 * Only alters squares that have been updated by the levelscene - i.e. those inside and adjacent where the levelscene was inserted.
	 */
	private static void workOutReachableSquares()
	{
		int levelSceneMidPoint = (levelScene.length / 2);
		
		int lowerY = Math.max(0, getMapCoordinate(0,  marioMapLoc[0], levelSceneMidPoint) - 1);
		int upperY = Math.min(map.size(), getMapCoordinate(levelScene.length,  marioMapLoc[0], levelSceneMidPoint) + 1);
		for(int i = lowerY; i< upperY; ++i)
		{
			int lowerX = Math.max(0, getMapCoordinate(0,  marioMapLoc[1], levelSceneMidPoint) - 1);
			int upperX = Math.min(map.get(i).size(), getMapCoordinate(levelScene.length,  marioMapLoc[1], levelSceneMidPoint) + 1);

			for(int j = lowerX; j < upperX; ++j)
			{
				MapSquare square = map.get(i).get(j);
				if(square != null)
				{
					square.workOutReachableSquares();
				}
			}
		}
	}
	/**
	 * @return MapSquare[][] of the required size to fit the new levelScene observations into.
	 */
	private static void increaseMapSize()
	{
		int levelSceneMidPoint0 = (levelScene.length / 2);
		int levelSceneMidPoint1 = (levelScene[0].length / 2);
		int[] origin = {0,0};
		
		if(marioMapLoc[0] + levelSceneMidPoint0 >= map.size()) //if off bottom of map
		{
			map = increaseMapSize(
					levelSceneMidPoint0 + marioMapLoc[0] + 1,
					map.get(0).size(), 
					origin);
		}
		if(marioMapLoc[0] < levelSceneMidPoint0) //if off top of map
		{
			origin[0] = levelSceneMidPoint0;
			map = increaseMapSize(
					map.size() + levelSceneMidPoint0 - marioMapLoc[0], 
					map.get(0).size(), 
					origin);
			origin[0] = 0;
			marioMapLoc[0] = levelSceneMidPoint0;
		}
		if(marioMapLoc[1] + levelSceneMidPoint1 >= map.get(0).size()) //if off right of map
		{
			map = increaseMapSize(
					map.size(), 
					levelSceneMidPoint1 + marioMapLoc[1] + 1, 
					origin);
		}
		if(marioMapLoc[1] < levelSceneMidPoint1) //if off left of map
		{
			origin[1] = levelSceneMidPoint1;
			map = increaseMapSize(
					map.size(), 
					map.get(0).size()  + levelSceneMidPoint1 - marioMapLoc[1], 
					origin);
			marioMapLoc[1] = levelSceneMidPoint1;
		}
	}
	/**
	 * Copies the old map array into the new one in the correct position.
	 * @param newHeight - height of the new map
	 * @param newWidth - width of the new map
	 * @param newPosOfOrigin - the position the origin of the old map needs to take in the new map
	 * @return MapSquare[][] with the all the same MapSquares as the old map, but with additional nulls where the map has been enlarged.
	 */
	private static ArrayList<ArrayList<MapSquare>> increaseMapSize(int newHeight, int newWidth, int[] newPosOfOrigin)
	{
		while(map.size() < newHeight)
		{
			map.add(new ArrayList<MapSquare>(newWidth));
		}
		for(int i = 0; i < map.size(); ++i)
		{
			while(map.get(i).size() < newWidth)
			{
				map.get(i).add(null);
			}
		}
		if(newPosOfOrigin[0] != 0 || newPosOfOrigin[1] != 0)
		{
			for(int i = map.size() - 1; i >= newPosOfOrigin[0]; --i)
			{
				for(int j = map.get(i).size() - 1; j >= newPosOfOrigin[1]; --j)
				{
					map.get(i).set(j, map.get(i - newPosOfOrigin[0]).get(j - newPosOfOrigin[1]));
					map.get(i - newPosOfOrigin[0]).set(j - newPosOfOrigin[1], null);
					if(map.get(i)!=null && map.get(i).get(j)!=null)
					{
						map.get(i).get(j).setLocInMap(j, i, map);
					}
				}
			}
		}
		return map;
	}
	/**
	 * Translates between map-coordinates and levelscene-coordinates.
	 * @param mapCoords - the required map coordinates.
	 * @param marioMapLoc - where Mario is currently in the map.
	 * @param marioLoc - where Mario is currently in the levelscene.
	 * @return coordinates of where this map location is in the level scene.
	 * Note: will return a location outside of the bounds of the level scene if the given map location was not in the current level scene.
	 */
	public static int[] getLevelSceneCoordinates(int[] mapCoords, int[] marioMapLoc, int[] marioLoc)
	{
		int[] levelSceneCoords = new int[2];
		levelSceneCoords[0] = getLevelSceneCoordinate(mapCoords[0], marioMapLoc[0], marioLoc[0]);
		levelSceneCoords[1] = getLevelSceneCoordinate(mapCoords[1], marioMapLoc[1], marioLoc[1]);
		return levelSceneCoords;
	}
	/**
	 * Translates a specific coordinate (x or y) between map and level scene coordinates.
	 * @param mapCoord - the map x/y coordinate
	 * @param marioMapLoc - the x/y location of Mario in the map.
	 * @param marioLoc - the x/y location of Mario in the levelScene.
	 * @return the x/y level scene coordinate
	 */
	public static int getLevelSceneCoordinate(int mapCoord, int marioMapLoc, int marioLoc)
	{
		return mapCoord + marioLoc - marioMapLoc;
	}
	/**
	 * Translates between map-coordinates and levelscene-coordinates.
	 * @param levelSceneCoords - the required levelscene coordinates.
	 * @param marioMapLoc - where Mario is currently in the map.
	 * @param marioLoc - where Mario is currently in the levelscene.
	 * @return coordinates of where this level scene is within the map.
	 */
	public static int[] getMapCoordinates(int[] levelSceneCoords, int[] marioMapLoc, int[] marioLoc)
	{
		int[] mapCoords = new int[2];
		mapCoords[0] = getMapCoordinate(mapCoords[0], marioMapLoc[0], marioLoc[0]);
		mapCoords[1] = getMapCoordinate(mapCoords[1], marioMapLoc[1], marioLoc[1]);
		return mapCoords;
	}
	/**
	 * Translates a specific coordinate (x or y) between levelscene and map coordinates.
	 * @param levelSceneCoord - the level scene x/y coordinate
	 * @param marioMapLoc - the x/y location of Mario in the map.
	 * @param marioLoc - the x/y location of Mario in the levelScene.
	 * @return the x/y map coordinate
	 */
	public static int getMapCoordinate(int levelSceneCoord, int marioMapLoc, int marioLoc)
	{
		return levelSceneCoord + marioMapLoc - marioLoc;
	}
}
