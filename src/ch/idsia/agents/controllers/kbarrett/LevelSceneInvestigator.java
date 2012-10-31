package ch.idsia.agents.controllers.kbarrett;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;

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
		//private byte[][] levelScene;
		/**
		 * Stores the location of Mario in the 2D array levelScene.
		 * I.e. levelScene[marioLoc[0]][marioLoc[1]] will return the square that Mario is currently occupying.
		 * @see ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator.levelScene
		 */
		//private int[] marioLoc;
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
		private static final float SQUARESIZE = 20.5f;
		/**
		 * Stores the total number of coins Mario has collected so far.
		 */
		private int numberOfCollectedCoins = 0;
		/**
		 * Stores the value of levelScene.length
		 */
		private int levelScenelength;
		/**
		 * Stores the value of levelScene[0].length
		 */
		private int levelScene0length;
		
	//Methods for updating the data
		/**
		 * Used for updating the levelScene when a new one is acquired.
		 * @param levelScene a 2D array representing the current environment of Mario encoded as integers.
		 * @param marioScreenPos a float array of size 2 containing Mario's actual position on the screen. Note: this is as an (x,y) coordinate.
		 * @see ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator.levelScene
		 */
		public void setLevelScene(byte[][] levelScene, float[] marioScreenPos)
		{
			/*Swap the terms in marioScreenPos, so they match the y-x orientation of the map.*/
			float temp = marioScreenPos[0];
			marioScreenPos[0] = marioScreenPos[1];
			marioScreenPos[1] = temp;
			
			if(FirstAgent.debug){checkLevelScene(levelScene);}
			levelScenelength = levelScene.length;
			levelScene0length = levelScene[0].length;
				
			if(this.marioScreenPos == null)
			{
				this.marioScreenPos = new float[2];
				this.marioScreenPos[0] = marioScreenPos[0];
				this.marioScreenPos[1] = marioScreenPos[1];
			}
			else if (
					Math.abs(this.marioScreenPos[0] - marioScreenPos[0]) > SQUARESIZE ||
					Math.abs(this.marioScreenPos[1] - marioScreenPos[1]) > SQUARESIZE
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
					else if(this.marioScreenPos[1] > marioScreenPos[1]) //Mario has moved left
					{
						--marioMapLoc[1];
					}
					
					//Update the new current position to be here.
					this.marioScreenPos[0] = marioScreenPos[0];
					this.marioScreenPos[1] = marioScreenPos[1];
					
			}
			map = MapUpdater.updateMap(map, levelScene, marioMapLoc);
			if(debug && false)
			{
				printMap();
			}
		}
		/**
		 * Used for updating marioLoc.
		 * @param marioLoc int array of size 2 representing the position of Mario in levelScene.
		 * @see ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator.marioLoc
		 */
		public void setMarioLoc(Movement movement)
		{
			movement.setMarioMapLoc(marioMapLoc);
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
				return getGapAvoidanceLocation(locationOfReward, isFacingRight);
			}	
			
			byte[] locationOfBlockage = getBlockageLocation(isFacingRight);
			if(locationOfBlockage != null)
			{
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

			MapSquare s = map[desiredPosition[0]][desiredPosition[1]];
			MapSquare[] plan = Search.aStar(s, map[marioMapLoc[0]][marioMapLoc[1]]);
			
			//check for plan
			//if !exists run astar
			//check for enemies
			//return first step of plan
			
			if(plan==null) return desiredPosition; 
			desiredPosition[0] = (byte) plan[0].getMapLocationY(); 
			desiredPosition[1] = (byte) plan[0].getMapLocationY();
			return desiredPosition;
		}
		
		public byte[] getRewardLocation()
		{
			for(byte i = marioMapLoc[1] - (Movement.MAX_JUMP_WIDTH/2) > 0 ? (byte) (marioMapLoc[1] - (Movement.MAX_JUMP_WIDTH/2)) : 0; i < (marioMapLoc[1] + (Movement.MAX_JUMP_WIDTH/2)); ++i)
			{
				for(byte j = marioMapLoc[0] - Movement.MAX_JUMP_HEIGHT > 0? (byte) (marioMapLoc[0] - Movement.MAX_JUMP_HEIGHT) : 0; j < levelScene0length; j++)
				{
					if(map[j][i] == null) {continue;}
					if(map[j][i].getEncoding() == Encoding.COIN)
					{
						byte[] result = new byte[2];
						result[0] = j;
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
			
			byte oneAway = map[marioMapLoc[0]][marioMapLoc[1] + direction]!= null ? map[marioMapLoc[0]][marioMapLoc[1] + direction].getEncoding() : 0;
			if(Encoding.isEnvironment(oneAway))
			{
				byte[] result = new byte[2];
				result[0] = (byte) (marioMapLoc[0]);
				result[1] = (byte) (marioMapLoc[1] + 1);
				return result;
			}
			byte twoAway = map[marioMapLoc[0]][marioMapLoc[1] + (2 * direction)]!= null ? map[marioMapLoc[0]][marioMapLoc[1] + (2 * direction)].getEncoding() : 0;
			if(Encoding.isEnvironment(twoAway))
			{	
				byte[] result = new byte[2];
				result[0] = (byte) (marioMapLoc[0]);
				result[1] = (byte) (marioMapLoc[1] + 2);
				return result;
			}
			
			return null;
		}
		
		//DEBUG
		boolean debug = FirstAgent.debug;
		private void printLevelSceneLoc(byte[][] levelScene, byte i, byte j)
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
		public void printLevelScene(byte[][] levelScene, byte[] marioLoc)
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
		private void checkLevelScene(byte[][] levelScene)
		{
			for(int i = 0; i< levelScene.length; i++)
			{
				middleloop: for(int j = 0; j < levelScene[i].length; ++j)
				{
					for(int thing : Encoding.knownThings)
					{
						if(levelScene[i][j] == thing)
						{
							continue middleloop;
						}
					}
					System.err.println("ARRRRRRGGGGHHHHHH: " + levelScene[i][j]);
				}
			}
		}
		public static void debugPrint(String s)
		{
			if(FirstAgent.debug)
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
					String s;
					if(map[i][j] == null) {s = ""+map[i][j];}
					else {s = ""+map[i][j].getEncoding();}
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
