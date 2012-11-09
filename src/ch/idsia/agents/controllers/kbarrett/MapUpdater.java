package ch.idsia.agents.controllers.kbarrett;

import java.awt.geom.Point2D;

/**
 * Used to update the map from a levelScene.
 * 
 * @author Kim
 *
 */
public class MapUpdater {
	
	private static MapSquare[][] map;
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
	public static MapSquare[][] updateMap(MapSquare[][] map, byte[][] levelScene, int[] marioMapLoc)
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
		map = getNewMapOfCorrectSize();
		for(int i = 0; i < levelScene.length; ++i)
		{
			int levelSceneMidPoint0 = (levelScene.length / 2);
			//FIXME: this should be using marioLoc in the levelScene, not always assuming he's in the middle everytime!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			for(int j = 0; j < levelScene[i].length; ++j)
			{
				int levelSceneMidPoint1 = (levelScene[i].length / 2);
				int y = getMapYCoordinate(i,  marioMapLoc[0], levelSceneMidPoint0);
				int x = getMapXCoordinate(j,  marioMapLoc[1], levelSceneMidPoint1);
				MapSquare square = map[y][x];
				if(square==null)
				{
					map[y][x] 
							= new MapSquare(levelScene[i][j], map, x, y);
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
		int levelSceneMidPoint0 = (levelScene.length / 2);
		for(int i = 0; i < levelScene.length; ++i)
		{
			int levelSceneMidPoint1 = (levelScene[i].length / 2);
			for(int j = 0; j < levelScene[i].length; ++j)
			{
				MapSquare square = map[getMapYCoordinate(i,  marioMapLoc[0], levelSceneMidPoint0)][getMapXCoordinate(j,  marioMapLoc[1], levelSceneMidPoint1)];
				square.workOutReachableSquares();
			}
		}
	}
	/**
	 * @return MapSquare[][] of the required size to fit the new levelScene observations into.
	 */
	private static MapSquare[][] getNewMapOfCorrectSize()
	{
		int levelSceneMidPoint0 = (levelScene.length / 2);
		int levelSceneMidPoint1 = (levelScene[0].length / 2);
		
		MapSquare[][] newMap = null;
		if(marioMapLoc[0] + levelSceneMidPoint0 >= map.length) //if off bottom of map
		{
			if(marioMapLoc[1] + levelSceneMidPoint1 >= map[0].length) //if off right of map
			{
				newMap = transferOldMapIntoNewMap(
						map.length + levelSceneMidPoint0, 
						map[0].length  + levelSceneMidPoint1, 
						new Point2D.Float(0,0));
			}
			else if(marioMapLoc[1] < levelSceneMidPoint1) //if off left of map
			{
				newMap = transferOldMapIntoNewMap(
						map.length + levelSceneMidPoint0, 
						map[0].length  + levelSceneMidPoint1, 
						new Point2D.Float(0,levelSceneMidPoint1));
				marioMapLoc[1]+=levelSceneMidPoint1;
			}
			else
			{
				newMap = transferOldMapIntoNewMap(
						map.length + levelSceneMidPoint0,
						map[0].length, 
						new Point2D.Float(0,0));
			}
		}
		else if(marioMapLoc[0] < levelSceneMidPoint0) //if off top of map
		{
			if(marioMapLoc[1] + levelSceneMidPoint1 >= map[0].length) //if off right of map
			{
				newMap = transferOldMapIntoNewMap(
						map.length + levelSceneMidPoint0, 
						map[0].length  + levelSceneMidPoint1, 
						new Point2D.Float(levelSceneMidPoint0,0));
				marioMapLoc[0]+=levelSceneMidPoint0;
			}
			else if(marioMapLoc[1] < levelSceneMidPoint1) //if off left of map
			{
				newMap = transferOldMapIntoNewMap(
						map.length + levelSceneMidPoint0, 
						map[0].length  + levelSceneMidPoint1, 
						new Point2D.Float(levelSceneMidPoint0,levelSceneMidPoint1));
				marioMapLoc[0]+=levelSceneMidPoint0;
				marioMapLoc[1]+=levelSceneMidPoint1;
			}
			else
			{
				newMap = transferOldMapIntoNewMap(
						map.length + levelSceneMidPoint0, 
						map[0].length, 
						new Point2D.Float(levelSceneMidPoint0, 0));
				marioMapLoc[0]+=levelSceneMidPoint0;
			}
		}
		else
		{
			if(marioMapLoc[1] + levelSceneMidPoint1 >= map[0].length) //if off right of map
			{
				newMap = transferOldMapIntoNewMap(
						map.length, 
						map[0].length + levelSceneMidPoint1, 
						new Point2D.Float(0,0));
			}
			else if(marioMapLoc[1] < levelSceneMidPoint1) //if off left of map
			{
				newMap = transferOldMapIntoNewMap(
						map.length, 
						map[0].length  + levelSceneMidPoint1, 
						new Point2D.Float(0,levelSceneMidPoint1));
				marioMapLoc[1]+=levelSceneMidPoint1;
			}
		}
		if(newMap == null) //if none of those
		{
			return map;
		}
		return newMap;
	}
	/**
	 * Copies the old map array into the new one in the correct position.
	 * @param newHeight - height of the new map
	 * @param newWidth - width of the new map
	 * @param newPosOfOrigin - the position the origin of the old map needs to take in the new map
	 * @return MapSquare[][] with the all the same MapSquares as the old map, but with additional nulls where the map has been enlarged.
	 */
	private static MapSquare[][] transferOldMapIntoNewMap(int newHeight, int newWidth, Point2D.Float newPosOfOrigin)
	{
		MapSquare[][] newMap = new MapSquare[newHeight][newWidth];
		for(int i = (int) newPosOfOrigin.x; i < map.length; ++i)
		{
			for(int j = (int) newPosOfOrigin.y; j< map[i].length; ++j)
			{
				newMap[i][j] = map[i][j];
				if(newMap[i][j]!=null)
				{
					newMap[i][j].setLocInMap(j, i);
				}
			}
		}
		return newMap;
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
