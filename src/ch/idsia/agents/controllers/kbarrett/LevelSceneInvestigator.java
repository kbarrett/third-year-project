package ch.idsia.agents.controllers.kbarrett;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * This class is responsible for seeing what is in the vicinity of Mario.
 * @author Kim Barrett
 */
public class LevelSceneInvestigator
{
	//Data
		/** 
		 * Stores the current surroundings of the agent.
		 * Each element is encoded as a byte representing what is in that position relative to Mario.
		 * Note: Mario is at the location stored in marioLoc in the 2D array.
		 * @see ch.idsia.agents.controllers.kbarrett.Encoding
		 * @see ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator.marioLoc
		 */
		private byte[][] levelScene;
		/**
		 * Stores the location of Mario in the 2D array levelScene.
		 * I.e. levelScene[marioLoc[0]][marioLoc[1]] will return the square that Mario is currently occupying.
		 * @see ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator.levelScene
		 */
		private int[] marioLoc;
		/**
		 * Stores the float position of Mario on the screen.
		 */
		private float[] marioScreenPos;
		private MapSquare[][] map;
		private int[] marioMapLoc = {0,0};
		/** 
		 * Stores the physical size of a square in levelScene.
		 * @see ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator.levelScene
		 */
		private static final float SQUARESIZE = 12;
		/**
		 * Stores the total number of coins Mario has collected so far.
		 */
		private int numberOfCollectedCoins = 0;
		
	//Methods for updating the data
		/**
		 * Used for updating the levelScene when a new one is acquired.
		 * @param levelScene a 2D array representing the current environment of Mario encoded as integers.
		 * @param marioScreenPos a float array of size 2 containing Mario's actual position on the screen
		 * @see ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator.levelScene
		 */
		public void setLevelScene(byte[][] levelScene, float[] marioScreenPos)
		{
			this.levelScene = levelScene;
				if(FirstAgent.debug){checkLevelScene();}
				
			if (
					this.marioScreenPos != null &&
					(
							Math.abs(this.marioScreenPos[0] - marioScreenPos[0]) > SQUARESIZE ||
							Math.abs(this.marioScreenPos[1] - marioScreenPos[0]) > SQUARESIZE
					)
				)
			{
				//Update Mario's vertical position
					if(this.marioScreenPos[0] < marioScreenPos[0]) //Mario has moved downwards
					{
						++marioMapLoc[0];
					}
					else if(this.marioScreenPos[0] > marioScreenPos[0]) //Mario has moved upwards
					{
						--marioMapLoc[0];
					}
				//Update Mario's horizontal position
					if(this.marioScreenPos[1] < marioScreenPos[1]) //Mario has moved right
					{
						++marioMapLoc[1];
					}
					else if(this.marioScreenPos[0] > marioScreenPos[0]) //Mario has moved left
					{
						--marioMapLoc[1];
					}
				updateMap();
				if(debug) {printMap();}
			}
			this.marioScreenPos = marioScreenPos;
		}
		/**
		 * Used for updating marioLoc.
		 * @param marioLoc int array of size 2 representing the position of Mario in levelScene.
		 * @see ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator.marioLoc
		 */
		public void setMarioLoc(int[] marioLoc)
		{
			this.marioLoc = marioLoc;
		}
		/**
		 * Used for updating the number of coins Mario has collected.
		 * @param coins the total number of coins Mario has collected so far.
		 * @see ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator.numberOfCollectedCoins
		 */
		public void updateCoins(int coins)
		{
			if(numberOfCollectedCoins != coins) //do something if the # of coins changes
			{
				numberOfCollectedCoins = coins;
			}
			//TODO: To be used to see if a coin has been achieved by an action in order to give up if impossible 
		}
		/**
		 * Creates a map of the given size, if no map has previously been created.
		 * @param width of the map
		 * @param height of the map
		 */
		public void giveMapSize(int width, int height)
		{
			if(map==null)
			{
				map = new MapSquare[width][height];
			}
		}
		/** 
		 * Incorporates the current levelScene into the map.
		 */
		private void updateMap()
		{
			map = getNewMapOfCorrectSize();
			for(int i = 0; i < levelScene.length; ++i)
			{
				for(int j = 0; j < levelScene[i].length; ++j)
				{
					map[i + marioMapLoc[0] - (levelScene.length / 2)][j + marioMapLoc[1] - (levelScene.length / 2)] 
							= new MapSquare(levelScene[i][j]);
				}
			}
			
		}
		/**
		 * @return MapSquare[][] of the required size to fit the new levelScene observations into.
		 */
		private MapSquare[][] getNewMapOfCorrectSize()
		{
			MapSquare[][] newMap = null;
			if(marioMapLoc[0] + (levelScene.length / 2) > map.length) //if off bottom of map
			{
				newMap = transferOldMapIntoNewMap(map.length + (levelScene.length / 2), map[0].length, new Point2D.Float(0,0));
				System.err.println(debug + "shouldn't happen");
			}
			else if(marioMapLoc[0] < (levelScene.length / 2)) //if off top of map
			{
				newMap = transferOldMapIntoNewMap(map.length + (levelScene.length / 2), map[0].length, new Point2D.Float(0, levelScene.length / 2));
				marioMapLoc[0]+=levelScene.length / 2;
				System.err.println(debug + "shouldn't happen " + marioMapLoc[0] + "," + marioMapLoc[1]);
			}
			
			if(marioMapLoc[1] + (levelScene[0].length / 2) > map[0].length) //if off right of map
			{
				newMap = transferOldMapIntoNewMap(map.length, map[0].length  + (levelScene.length / 2), new Point2D.Float(0,0));
			}
			else if(marioMapLoc[1] < (levelScene[0].length / 2)) //if off left of map
			{
				newMap = transferOldMapIntoNewMap(map.length, map[0].length  + (levelScene.length / 2), new Point2D.Float(levelScene.length / 2,0));
				marioMapLoc[1]+=levelScene.length / 2;
				System.err.println(debug + "definitely shouldn't happen !!" + marioMapLoc[0] + "," + marioMapLoc[1]);
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
		private MapSquare[][] transferOldMapIntoNewMap(int newHeight, int newWidth, Point2D.Float newPosOfOrigin)
		{
			MapSquare[][] newMap = new MapSquare[newHeight][newWidth];
			for(int i = (int) newPosOfOrigin.x; i < map.length; ++i)
			{
				for(int j = (int) newPosOfOrigin.y; j< map[i].length; ++j)
				{
					newMap[i][j] = map[i][j];
				}
			}
			return newMap;
		}

	//Methods for analysing the environment
		/**
		 * Used to make a decision based on the environment about which location Mario needs to move to.
		 * @param isFacingRight indicating whether Mario is facing right or not.
		 * @return byte array of size 2 denoting the location that has been decided to move towards. 
		 * Returns null if no location has been chosen.
		 */
		public byte[] decideLocation(boolean isFacingRight)
		{
			
			byte[] locationOfReward = getRewardLocation();
			if(locationOfReward != null)
			{
				if(FirstAgent.debug)
				{
					System.out.println("FOUND REWARD at " + locationOfReward[0] + "," + locationOfReward[1] );
				}
				return getGapAvoidanceLocation(locationOfReward, isFacingRight);
			}	
			
			byte[] locationOfBlockage = getBlockageLocation(isFacingRight);
			if(locationOfBlockage != null)
			{
				if(FirstAgent.debug){System.out.println("FOUND BLOCKAGE");}
				byte[] requiredLocation = new byte[2];
				requiredLocation[0] = (byte) (locationOfBlockage[0] - 1);
				if(isFacingRight)
				{
					requiredLocation[1] = (byte) (locationOfBlockage[1] + 1);
				}
				else
				{
					requiredLocation[1] = (byte) (locationOfBlockage[1] - 1);
				}
				return getGapAvoidanceLocation(requiredLocation, isFacingRight);
			}
			
			return null;
		}
		
		private byte[] getGapAvoidanceLocation(byte[] desiredPosition, boolean isFacingRight) {

			int increment = 1;
			if(marioLoc[1] > desiredPosition[1]) {increment = -1;}
			
			if //if immediately below Mario's next step is empty
			(
					levelScene[desiredPosition[0]][marioLoc[1] + increment] == Encoding.NOTHING
			)
			{
				if(debug){System.out.println("true");}
				byte[] result = new byte[2];
				
				//check beneath Mario for next step to see if there's floor there
				for(int i = 0; i < Movement.MAX_JUMP_WIDTH; i++)
				{
					if(levelScene[marioLoc[0]][marioLoc[1] + (increment * i)] == Encoding.FLOOR)
					{
						return desiredPosition;
					}
				}
				//if none found, check for floor within eyeshot
				for(int i = 0; i < Movement.MAX_JUMP_HEIGHT; i++)
				{
					if(levelScene[marioLoc[0] + i][marioLoc[1] + increment] == Encoding.FLOOR)
					{
						return desiredPosition;
					}
				}
				
				result[0] = (byte) marioLoc[0];
				result[1] = (byte) (marioLoc[1] - increment);
				return result;
			}
			return desiredPosition;
		}
		
		public byte[] getRewardLocation()
		{
			for(byte i = (byte) (marioLoc[1] - (Movement.MAX_JUMP_WIDTH/2)); i < (marioLoc[1] + (Movement.MAX_JUMP_WIDTH/2)); ++i)
			{
				rows : for(byte j = (byte) (marioLoc[0] - Movement.MAX_JUMP_HEIGHT); j < levelScene[i].length; j++)
				{
					if(levelScene[j][i] == Encoding.COIN)
					{
						byte[] result = new byte[2];
						result[0] = j;
						//If coin directly above Mario
						if(j < marioLoc[0] && marioLoc[1] == i)
						{
							//check not going to headbutt something
							for(int k = marioLoc[0]; k > j; --k)
							{
								debugPrint("COIN DIRECTLY ABOVE: " + k + " " + levelScene[k][i]);
								if(Encoding.isEnvironment(levelScene[k][i]))
								{
									if(debug) {System.out.println("upwards FLOOR IN WAY OF COIN");}
									continue rows;
								}
							}
						}
						//if coin directly below Mario
						else if (j > marioLoc[0] && marioLoc[1] == i )
						{
							debugPrint("COIN DIRECTLY BELOW");
							//if coin immediately below Mario & floor in way, ignore it
							for(int k = marioLoc[0]; k < j; k++)
							{
							if(Encoding.isEnvironment(levelScene[k][i]))
							{
								if(debug) {System.out.println("downwards FLOOR IN WAY OF COIN");}
								continue;
							}
							}
						}

						result[1] = i;
						return result;
					}
				}
			}
			return null;
		}
		
		public byte[] getBlockageLocation(boolean facingRight)
		{
			int direction = 1;
			if(!facingRight)
			{
				direction = -1;
			}
			
			byte oneAway = levelScene[marioLoc[0]][marioLoc[1] + direction];
			if(
					oneAway == Encoding.WALL  || 
					oneAway == Encoding.FLOWERPOT  || 
					oneAway == Encoding.CORNERTOPLEFT || 
					oneAway == Encoding.BRICK  || 
					oneAway == Encoding.BREAKABLE_BRICK
			)
			{
				byte[] result = new byte[2];
				result[0] = (byte) (marioLoc[0]);
				result[1] = (byte) (marioLoc[1] + 1);
				return result;
			}
			byte twoAway = levelScene[marioLoc[0]][marioLoc[1] + (2 * direction)];
			if
			(
					twoAway == Encoding.WALL  ||
					twoAway == Encoding.FLOWERPOT  ||
					twoAway == Encoding.CORNERTOPLEFT ||
					twoAway == Encoding.BRICK  ||
					twoAway == Encoding.BREAKABLE_BRICK
			)
			{	
				byte[] result = new byte[2];
				result[0] = (byte) (marioLoc[0]);
				result[1] = (byte) (marioLoc[1] + 2);
				return result;
			}
			
			return null;
		}
		
		//DEBUG
		boolean debug = FirstAgent.debug;
		private void printLevelSceneLoc(byte i, byte j)
		{
			System.out.print(levelScene[i][j]);
			if(j == levelScene[i].length - 1)
			{
				System.out.println("");
			}
			else
			{
				System.out.print(",");
				if(levelScene[i][j] >= 0){System.out.print(" ");
				if(levelScene[i][j] <  9){System.out.print(" ");}}
				else if(levelScene[i][j] >  -9){System.out.print(" ");}
			}
		}
		public void printLevelScene()
		{
			for(byte i = 0; i < levelScene.length ; ++i)
			{
				for(byte j = 0; j < levelScene[i].length; j++)
				{
					if(i == marioLoc[0] && j == marioLoc[1]) System.out.print("[[");
					else if(i == marioLoc[0] || j == marioLoc[1]) System.out.print("[");
					System.out.print(levelScene[i][j] + " ");
					if(i == marioLoc[0] && j == marioLoc[1]) System.out.print("]]");
					else if(i == marioLoc[0] || j == marioLoc[1]) System.out.print("]");
				}
				System.out.println(" ");
			}
			System.out.println(" ");
			
		}
		/**
		 * Makes sure all encodings in the levelScene have been encountered before.
		 */
		private void checkLevelScene()
		{
			for(int i = 0; i< levelScene.length; i++)
			{
				for(int j = 0; j < levelScene[i].length; ++j)
				{
					for(int thing : Encoding.knownThings)
					{
						if(levelScene[i][j] == thing)
						{
							return;
						}
					}
					System.err.println("ARRRRRRGGGGHHHHHH: " + levelScene[i][j]);
				}
			}
		}
		private void debugPrint(String s)
		{
			if(debug)
			{
				System.out.println(s);
			}
		}
		private void printMap()
		{
			for(int i = 0; i<map.length; ++i)
			{
				for(int j = 0; j<map[i].length; ++j)
				{
					String s = ""+map[i][j];
					while(s.length()<4)
					{
						s+=" ";
					}
					System.out.print(s);
				}
				System.out.println();
			}
		}
		
}
