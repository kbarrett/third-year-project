package ch.idsia.agents.controllers.kbarrett.first;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Used to update the map from a levelScene.
 * 
 * @author Kim
 *
 */
public class MapUpdater {
	
	private static ArrayList<ArrayList<MapSquare>> map;
	private static byte[][] levelScene;
	private static int[] marioMapLoc;
	
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

	private static void updateMap()
	{
		increaseMapSize();
		for(int i = 0; i < levelScene.length; ++i)
		{
			int levelSceneMidPoint0 = (levelScene.length / 2);
			//FIXME: this should be using marioLoc in the levelScene, not always assuming he's in the middle every time
			for(int j = 0; j < levelScene[i].length; ++j)
			{
				int levelSceneMidPoint1 = (levelScene[i].length / 2);
				int y = getMapYCoordinate(i,  marioMapLoc[0], levelSceneMidPoint0);
				int x = getMapXCoordinate(j,  marioMapLoc[1], levelSceneMidPoint1);
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
	private static void workOutReachableSquares()
	{
		int levelSceneMidPoint = (levelScene.length / 2);
		
		int lowerY = Math.max(0, getMapYCoordinate(0,  marioMapLoc[0], levelSceneMidPoint) - 1);
		int upperY = Math.min(map.size(), getMapYCoordinate(levelScene.length,  marioMapLoc[0], levelSceneMidPoint) + 1);
		for(int i = lowerY; i< upperY; ++i)
		{
			int lowerX = Math.max(0, getMapXCoordinate(0,  marioMapLoc[1], levelSceneMidPoint) - 1);
			int upperX = Math.min(map.get(i).size(), getMapXCoordinate(levelScene.length,  marioMapLoc[1], levelSceneMidPoint) + 1);

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
			map.get(i).ensureCapacity(newWidth);
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
	
	public static int[] getLevelSceneCoordinates(int[] mapCoords, int[] marioMapLoc, int[] marioLoc)
	{
		int[] levelSceneCoords = new int[2];
		levelSceneCoords[0] = getLevelSceneYCoordinate(mapCoords[0], marioMapLoc[0], marioLoc[0]);
		levelSceneCoords[1] = getLevelSceneXCoordinate(mapCoords[1], marioMapLoc[1], marioLoc[1]);
		return levelSceneCoords;
	}
	public static int getLevelSceneXCoordinate(int mapXCoord, int marioMapXLoc, int marioXLoc)
	{
		return mapXCoord + marioXLoc - marioMapXLoc;
	}
	public static int getLevelSceneYCoordinate(int mapYCoord, int marioMapYLoc, int marioYLoc)
	{
		return mapYCoord + marioYLoc - marioMapYLoc;
	}
	
	public static int[] getMapCoordinates(int[] levelSceneCoords, int[] marioMapLoc, int[] marioLoc)
	{
		int[] mapCoords = new int[2];
		mapCoords[0] = getMapYCoordinate(mapCoords[0], marioMapLoc[0], marioLoc[0]);
		mapCoords[1] = getMapXCoordinate(mapCoords[1], marioMapLoc[1], marioLoc[1]);
		return mapCoords;
	}
	public static int getMapXCoordinate(int levelSceneXCoord, int marioMapXLoc, int marioXLoc)
	{
		return levelSceneXCoord + marioMapXLoc - marioXLoc;
	}
	public static int getMapYCoordinate(int levelSceneYCoord, int marioMapYLoc, int marioYLoc)
	{
		return levelSceneYCoord + marioMapYLoc - marioYLoc;
	}
}
