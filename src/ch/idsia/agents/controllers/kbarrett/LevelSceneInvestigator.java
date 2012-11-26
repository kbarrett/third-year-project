package ch.idsia.agents.controllers.kbarrett;

import java.util.ArrayList;
import java.util.Stack;

/**
 * This class is responsible for seeing what is in the vicinity of Mario.
 * @author Kim Barrett
 */
public class LevelSceneInvestigator
{
	//Data
		/**
		 * Stores Mario's location in the levelScene.
		 * Used for translating between {@link #map} co-ordinates & levelScene co-ordinates.
		 */
		private int[] marioLoc;
		/**
		 * Stores the float position of Mario on the screen.
		 */
		private float[] marioScreenPos;
		/**
		 * Stores whether Mario has moved to a different position within the map (i.e. whether marioMapLoc has changed) on this iteration.
		 */
		private boolean justMoved = false;
		/**
		 * Stores Mario's knowledge about the environment.
		 */
		private ArrayList<ArrayList<MapSquare>> map;
		/**
		 * Stores Mario's current position within the {@link #map}.
		 */
		private int[] marioMapLoc = {0,0};
		/** 
		 * Stores the physical size of a square in levelScene and map.
		 * @see ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator.levelScene
		 */
		private static final float SQUARESIZE = 16f;
		/**
		 * Stores the total number of coins Mario has collected so far.
		 */
		private int numberOfCollectedCoins = 0;
		
		private boolean enemyFound = false;
		
		private PlanStorer planStorer;
		
		public LevelSceneInvestigator()
		{
			planStorer = new PlanStorer();
		}
		
	//Methods for updating the data
		/**
		 * Used for updating the {@link #map} from a newly acquired levelScene.
		 * @param levelScene a 2D array representing the current environment of Mario encoded as integers.
		 */
		public void updateMapFromLevelScene(byte[][] levelScene)
		{
			if(FirstAgent.debug)
			{
				//checks for any previously unencountered encodings
				checkLevelScene(levelScene);
			}
			
			//Updates map using this levelScene
			map = MapUpdater.updateMap(map, levelScene, marioMapLoc);
			
			if(debug && true)
			{
				printMap();
			}
		}
		/**
		 * Used for updating Mario's screen position.
		 * Note: will only update the stored position if it has varied by more than {@link #SQUARESIZE}.
		 * This method also updates {@link #marioMapLoc}, if Mario has moved into the space represented by a different position in map.
		 * @param marioScreenPos a float array of size 2 containing Mario's actual position on the screen. Note: this is as an (x,y) coordinate.
		 */
		public void setMarioScreenPos(float[] marioScreenPos)
		{
			if(debug){findSquareSize(marioScreenPos);}
			
			/*Swap the terms in marioScreenPos, so they match the y-x orientation of the map.*/
			float temp = marioScreenPos[0];
			marioScreenPos[0] = marioScreenPos[1];
			marioScreenPos[1] = temp;
			
			boolean firstRun = false;
			
			if(this.marioScreenPos == null)
			{
				this.marioScreenPos = new float[2];
				this.marioScreenPos[0] = marioScreenPos[0] - SQUARESIZE/2;
				this.marioScreenPos[1] = marioScreenPos[1] - SQUARESIZE/2;
				
				firstRun = true;
			}
			
			int[] prevMapLoc = new int[] {marioMapLoc[0], marioMapLoc[1]};
			marioMapLoc[0] = (int)((marioScreenPos[0] - this.marioScreenPos[0]) / SQUARESIZE) + marioLoc[0];
			marioMapLoc[1] = (int)((marioScreenPos[1] - this.marioScreenPos[1]) / SQUARESIZE) + marioLoc[1];
			if(prevMapLoc[0] != marioMapLoc[0] || prevMapLoc[1] != prevMapLoc[1] && !firstRun)
			{
				debugPrint("Just moved from " + prevMapLoc[0] + "," + prevMapLoc[1] + " to " + marioMapLoc[0] + "," + marioMapLoc[1]);
				justMoved = true;
				
				//FIXME: wnats to not be true on first turn!!!!!
			}
			else
			{
				justMoved = false;
			}
		}
		/**
		 * Used for updating {@link #marioLoc}.
		 * @param marioLoc int array of size 2 representing the position of Mario in levelScene.
		 */
		public void setMarioLoc(int[] marioLoc)
		{
			this.marioLoc = marioLoc;
		}
		
		public int[] getMarioMapLoc()
		{
			return marioMapLoc;
		}
		/**
		 * Used for updating the number of coins Mario has collected.
		 * @param coins the total number of coins Mario has collected so far.
		 * @see #numberOfCollectedCoins
		 */
		public void updateCoins(int coins)
		{
			if(numberOfCollectedCoins != coins) //do something if the # of coins changes
			{
				numberOfCollectedCoins = coins;
			}
			//FIXME: To be used to see if a coin has been achieved by an action in order to give up if impossible 
		}
		/**
		 * If no {@link #map} has previously been created, creates a map of the given size.
		 * @param width of the map
		 * @param height of the map
		 */
		public void giveMapSize(int width, int height)
		{
			//FIXME: neaten this
			width = 1; height = 1;
			if(map==null)
			{
				map = new ArrayList<ArrayList<MapSquare>>(height);
				for(int i = 0; i < height; ++i)
				{
					map.add(new ArrayList<MapSquare>(width));
					for(int j = 0; j < width; ++j)
					{
						map.get(i).add(null);
					}
				}
			}
		}
		
		public boolean isEnemy()
		{
			return enemyFound;
		}
		
	//Methods for analysing the environment
		
		public MapSquare getMapSquare(int y, int x)
		{
			return map.get(y).get(x);
		}
		/**
		 * Used to make a decision based on the environment about which location Mario needs to move to.
		 * @param isFacingRight indicating whether Mario is facing right or not.
		 * @return byte array of size 2 denoting the location that has been decided to move towards. 
		 * Returns null if no location has been chosen.
		 */
		public MapSquare getNextLocation(boolean isFacingRight, boolean isJumping)
		{
			//If we don't already have a plan
			if(!planStorer.havePlan()) 
			{
				//Find a good location to move towards
				MapSquare location = findLocation(isFacingRight);
				if(location != null)
				{
					planStorer.makePlan(location, getMarioMapSquare());
				}
				if(!planStorer.havePlan())
				{
					planStorer.makePlan(getBestRightHandSide(), getMarioMapSquare());
				}
				
			}
			
			//If we have just made a move
			else if((justMoved && !planStorer.isPlanStepAchieved(getMarioMapSquare())) || !nextPlanStepAdjacent()) 
				{
					/*if(!isJumping)
					{*/
						//Return the next move in the plan
	
						planStorer.replan(getMarioMapSquare());
						return planStorer.getLocationToMoveTo(getMarioMapSquare(), this);
					/*}
					else
					{
						return null;
					}*/
				}
			
			
			return planStorer.getLocationToMoveTo(getMarioMapSquare(), this);
		}
		
		private boolean nextPlanStepAdjacent()
		{
			//checks whether the plan is accessible in one turn
			MapSquare nextLocation = planStorer.getNextPlanLocation();
			
			return
					   nextLocation.equals(map.get(marioMapLoc[0]+1).get(marioMapLoc[1]))
					|| nextLocation.equals(map.get(marioMapLoc[0]+1).get(marioMapLoc[1]+1))
					|| nextLocation.equals(map.get(marioMapLoc[0]+1).get(marioMapLoc[1]-1))
					|| nextLocation.equals(map.get(marioMapLoc[0])  .get(marioMapLoc[1]+1))
					|| nextLocation.equals(map.get(marioMapLoc[0])  .get(marioMapLoc[1]-1))
					|| nextLocation.equals(map.get(marioMapLoc[0]-1).get(marioMapLoc[1]+1))
					|| nextLocation.equals(map.get(marioMapLoc[0]-1).get(marioMapLoc[1]))
					|| nextLocation.equals(map.get(marioMapLoc[0]-1).get(marioMapLoc[1]-1));
		}
		private MapSquare getMarioMapSquare()
		{
			return map.get(marioMapLoc[0]).get(marioMapLoc[1]);
		}
		
		
		/**
		 * Finds a location in the map that Mario should move towards.
		 * @param isFacingRight - whether or not Mario is currently facing right
		 * @return int array of size 2 representing a desirable location on the map for Mario to move towards
		 */
		private MapSquare findLocation(boolean isFacingRight)
		{
			MapSquare locationOfReward = getRewardLocation();
			if(locationOfReward != null)
			{
				return locationOfReward;
			}	
			
			/*int[] requiredlocationForBlockage = getBlockageLocation(isFacingRight);
			if(requiredlocationForBlockage != null)
			{
				return requiredlocationForBlockage;
			}*/
			
			return null;
		}
		public MapSquare getRewardLocation()
		{
			for(
					int i = Math.max(0, marioMapLoc[1] - Movement.MAX_JUMP_WIDTH / 2);
					    i < Math.min(map.size(), marioMapLoc[1] + Movement.MAX_JUMP_WIDTH / 2);
					++i
				)
			{
				for(
						int j =  0;
						j <	map.size();
						++j
					)
				{
					if(map.get(j).get(i) == null) {continue;}
					if(map.get(j).get(i).getEncoding() == Encoding.COIN)
					{
						return map.get(j).get(i);
					}
				}
			}
			return null;
		}
		
		public MapSquare getBestRightHandSide()
		{
			//whether we have found an environment piece in this column or not
			boolean found = false;
			
			//iterate from the bottom right of the map, up each column towards Mario
			for(int x = map.get(0).size() - 1; x >= marioMapLoc[1]; --x)
			{
				innerloop : for(int y = map.size() - 1; y >= 0 ; --y)
				{
					MapSquare rightHandSquare = map.get(y).get(x);
					
					if(rightHandSquare == null)
					{
						continue innerloop;
					}
					//if it's an Environment piece, then we have found an Environment piece in this column
					else if(Encoding.isEnvironment(rightHandSquare))
					{
						found = true;
					}
					//if we have previously found an Environment piece (& this piece is not Environment)
					else if (found)
					{
						//then this is the square we want to move towards
						return map.get(y).get(x);
					}
				}
				/*
				 * if we haven't found a non-Environment square in this column (above an Environment piece)
				 * then move to next column & look again
				 */
				found = false;
			}
			return null;
		}
		
		/*public int[] getBlockageLocation(boolean facingRight)
		{
			int direction = 1;
			if(!facingRight)
			{
				direction = -1;
			}
			
			for(int i = 1; i < Movement.MAX_JUMP_WIDTH; ++i)
			{
				byte square = map.get(marioMapLoc[0]).get(marioMapLoc[1] + (i * direction))!= null ? map.get(marioMapLoc[0]).get(marioMapLoc[1] + (i * direction)).getEncoding() : 0;
				if(Encoding.isEnvironment(square))
				{
					int[] result = new int[2];
	
					result[1] = marioMapLoc[1] + (i * direction);
					result[0] = marioMapLoc[0];
					while(Encoding.isEnvironment(map.get(result[0]).get(result[1])))
					{
						--result[0];
						if(result[0] < 0 || map.get(result[0]).get(result[1]) == null)
						{
							return null;
							//FIXME: deal with inability to jump over
						}
					}
					return result;
				}
			}
			
			return null;
		}*/
		
		public MapSquare checkForEnemies(int xBound, int yBound)
		{
			for(int j = marioMapLoc[0]; j >= Math.max(yBound, 0) ; --j)
			{
				for(int k = marioMapLoc[1]; k >= Math.max(xBound, 0) ; --k)
				{
					if(Encoding.isEnemySprite(map.get(j).get(k)))
					{
						enemyFound = true;
						return map.get(j).get(k);
					}
				}
			}
			enemyFound = false;
			return null;
		}
		
		//DEBUG
		boolean debug = FirstAgent.debug;
		float[] startloc = new float[2];
		int y = -1;
		int x = -1;
		private void findSquareSize(float[] marioScreenPos)
		{
			if(y >= -1)
			for(int i = 0; i < map.size(); ++i)
			{
				for(int j = 0; j < map.get(i).size(); ++j)
				{
					if(map.get(i).get(j) != null && map.get(i).get(j).getEncoding() == -90)
					{
						if(y == -1)
						{
							y = i;
							x = j;
							startloc[0] = marioScreenPos[0];
							startloc[1] = marioScreenPos[1];
							break;
						}
						else if(y != i || x != j)
						{
							debugPrint("SQUARESIZE IS " + Math.max(
									(marioScreenPos[0] - startloc[0]), (marioScreenPos[1] - startloc[1]) ));
							y = -2;
							break;
						}
					}
				}
			}
		}
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
			for(int i = 0; i<map.size(); ++i)
			{
				for(int j = 0; j<map.get(i).size(); ++j)
				{
					String s = "";
					if(i == marioMapLoc[0] && j == marioMapLoc[1]) {s += "*";}
					if(map.get(i).get(j) == null) {s += ""+map.get(i).get(j);}
					else {s += ""+map.get(i).get(j).getEncoding();}
					if(i == marioMapLoc[0] && j == marioMapLoc[1]) {s += "*";}
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
