package ch.idsia.agents.controllers.kbarrett.first;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ch.idsia.agents.controllers.kbarrett.Encoding;

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
		 * Stores the float position of Mario on the screen during the initial frame.
		 */
		private float[] marioInitialScreenPos;
		/**
		 * Stores the float position of Mario on the screen during the last frame.
		 */
		private float[] marioLastScreenPos;
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
		 * Stores whether there is an enemy within {@link #enemybound} of Mario.
		 */
		private boolean enemyFound = false;
		/**
		 * The maximum distance an enemy can be away for Mario to attempt to deal with it.
		 */
		private int enemybound = 2;
		/**
		 * Stores the current plan Mario is executing and is also responsible for making new plans.
		 */
		private PlanStorer planStorer;
		/**
		 * Mario's current mode.
		 * 2 = fire mode; 1 = large mode; 0 = small mode.
		 */
		private int marioMode;
		/**
		 * Direction Mario is currently moving in, where -1 is left, 0 is not moving & 1 is right.
		 */
		private byte directionOfMovement = 0;
		
		public LevelSceneInvestigator()
		{
			planStorer = new PlanStorer();
		}
		
		/**
		 * @return true if Mario isn't currently moving in the x direction and false if he is.
		 */
		private boolean isStationary()
		{
			return directionOfMovement == 0;
		}
		
	//Methods for updating the data
		/**
		 * Create a new (empty) map
		 */
		public void createEmptyMap()
		{
			map = new ArrayList<ArrayList<MapSquare>>();
			map.add(new ArrayList<MapSquare>());
		}
		/**
		 * Used for updating the {@link #map} from a newly acquired levelScene.
		 * @param levelScene a 2D array representing the current environment of Mario encoded as integers.
		 */
		public void updateMapFromLevelScene(byte[][] levelScene)
		{
			//Updates map using this levelScene
			MapUpdater.updateMap(map, levelScene, marioMapLoc);
			//Checks whether any step of the plan has become an Environment piece by this update.
			planStorer.checkPlan();
		}
		/**
		 * Used for updating Mario's screen position.
		 * Note: will only update the stored position if it has varied by more than {@link #SQUARESIZE}.
		 * This method also updates {@link #marioMapLoc}, if Mario has moved into the space represented by a different position in map.
		 * @param marioScreenPos a float array of size 2 containing Mario's actual position on the screen. Note: this is as an (x,y) coordinate.
		 */
		public void setMarioScreenPos(float[] marioScreenPos)
		{	
			/*Swap the terms in marioScreenPos, so they match the y-x orientation of the map.*/
			float temp = marioScreenPos[0];
			marioScreenPos[0] = marioScreenPos[1];
			marioScreenPos[1] = temp;
			
			//On the first run through, store the top-left (physical) location of the square we're in as our current position
			if(this.marioInitialScreenPos == null || this.marioLastScreenPos == null)
			{
				this.marioInitialScreenPos = new float[2];
				this.marioInitialScreenPos[0] = marioScreenPos[0] - SQUARESIZE/2;
				this.marioInitialScreenPos[1] = marioScreenPos[1] - SQUARESIZE/2;
				
				this.marioLastScreenPos = new float[2];
				this.marioLastScreenPos[0] = marioScreenPos[0];
				this.marioLastScreenPos[1] = marioScreenPos[1];
			}
			//On all subsequent runs, use the initial position to calculate our current position in the map
			else
			{
				//Store the previous location in the map
				int[] prevMapLoc = new int[] {marioMapLoc[0], marioMapLoc[1]};
				
				//Calculate new positions
				marioMapLoc[0] = (int)((marioScreenPos[0] - this.marioInitialScreenPos[0]) / SQUARESIZE) + marioLoc[0];
				marioMapLoc[1] = (int)((marioScreenPos[1] - this.marioInitialScreenPos[1]) / SQUARESIZE) + marioLoc[1];
				
				//If previous and new differ, then we have moved
				if(prevMapLoc[0] != marioMapLoc[0] || prevMapLoc[1] != prevMapLoc[1])
				{
					justMoved = true;
				}
				else
				{
					justMoved = false;
				}
				
				//If old & new screen positions are equals then we are stationary
				float diff = marioScreenPos[0] - this.marioLastScreenPos[0];
				if(diff < 0)
				{
					directionOfMovement = -1;
				}
				else if (diff == 0)
				{
					directionOfMovement = 0;
				}
				else
				{
					directionOfMovement = 1;
				}
				//set previous position to be the current position
				marioLastScreenPos[0] = marioScreenPos[0];
				marioLastScreenPos[1] = marioScreenPos[1];
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
		/**
		 * @return the indices of Mario's current location within {@link #map}.
		 */
		public int[] getMarioMapLoc()
		{
			return marioMapLoc;
		}
		/**
		 * @param marioMode = 0, 1 or 2 representing small, large, fire (respectively).
		 */
		public void setMarioMode(int marioMode)
		{
			this.marioMode = marioMode;
		}
		/**
		 * @return 0 if Mario is small, 1 if Mario is large or 2 if Mario is fire.
		 */
		public int getMarioMode()
		{
			return marioMode;
		}
		/**
		 * @return boolean representing whether there's an enemy within {@link #enemybound} of Mario.
		 */
		public boolean isEnemy()
		{
			return enemyFound;
		}
		/**
		 * @param y - y index in map
		 * @param x - x index in map
		 * @return location in {@link #map} corresponding to [y,x]
		 */
		public MapSquare getMapSquare(int y, int x)
		{
			return map.get(y).get(x);
		}
	
	//Methods for analysing the environment
		
		/**
		 * Used to make a decision based on the environment about which location Mario needs to move to.
		 * @param isFacingRight indicating whether Mario is facing right or not.
		 * @return byte array of size 2 denoting the location that has been decided to move towards. 
		 * Returns null if no location has been chosen.
		 */
		public MapSquare getNextLocation(boolean isJumping)
		{
			//Check if we have achieved the goal of our last plan step & remove it from the plan
			boolean lastStepAchieved = planStorer.isPlanStepAchieved(getMarioMapSquare());
			
			//If we don't already have a plan or we have an unimportant plan (i.e. one that just moves us across the board)
			if(!planStorer.havePlan() || !planStorer.isImportant()) 
			{
				makeNewPlan();
			}
			
			//If we have just made a move
			if(justMoved)
			{
				//If we are not where we expected to be
				if(!lastStepAchieved)
				{
					planStorer.replan(getMarioMapSquare(), getMarioMode());
				}
			}
			
			return getLocationToMoveTo(isJumping);
		}
		/**
		 * Used to get the location Mario wants to move towards.
		 * Generally corresponds to the next stage in the plan, though sometimes different if 
		 * there are enemies to deal with or Mario needs to be stationary for the next move.
		 * @param isJumping - whether Mario is currently jumping
		 * @return a MapSquare that Mario should move towards
		 */
		private MapSquare getLocationToMoveTo(boolean isJumping)
		{
			//Find nearby enemies
			List<MapSquare> enemy = checkForEnemies(enemybound, enemybound);
			if(enemy.size() > 0)
			{
				//Adapt plan to avoid them
				planStorer.avoid(enemy, map, marioMode);
			}
			
			//Get next stage in the plan
			MapSquare s =  planStorer.getLocationToMoveTo(getMarioMapSquare(), this);
			//If our next step is to jump upwards we need to be stationary (relative to the x axis) first.
			if(!isStationary() && !isJumping && getMarioMapSquare().getSquareAbove().equals(s))
			{
				return getMarioMapSquare();
			}
			//Otherwise return the next stage in the plan
			return s;
		}
		/**
		 * @return MapSquare that Mario is currently occupying
		 */
		private MapSquare getMarioMapSquare()
		{
			return map.get(marioMapLoc[0]).get(marioMapLoc[1]);
		}
		
		/**
		 * Finds a location that would be good for Mario to move towards and makes a plan to get there.
		 * @return true if we succeeded in making a plan
		 */
		private boolean makeNewPlan()
		{
			ArrayList<MapSquare> allRewards = getAllRewards();
			for(MapSquare square : allRewards)
			{
				//Find a good location to move towards
				planStorer.makePlan(square, getMarioMapSquare(), getMarioMode());
				//If this succeeded, then use this plan
				if(planStorer.havePlan())
				{
					return true;
				}
			}
			if(!planStorer.havePlan())
			{
				//if we still don't have a plan, plan to move to the RHS of the screen
				planStorer.makePlan(getBestRightHandSide(), getMarioMapSquare(), false, getMarioMode());
			}
			return planStorer.havePlan();
		}
		
		/**
		 * @return list of all MapSquares (within jumping reach of Mario in the x-direction) containing "reward" types (i.e. {@link Encoding#COIN} or {@link Encoding#FIRE_FLOWER}.)
		 */
		private ArrayList<MapSquare> getAllRewards()
		{
			ArrayList<MapSquare> rewardsFound = new ArrayList<MapSquare>(2 * Movement.MAX_JUMP_WIDTH);
			for(
					int i = Math.max(0, marioMapLoc[1] - Movement.MAX_JUMP_WIDTH);
					    i < Math.min(map.get(0).size(), marioMapLoc[1] + Movement.MAX_JUMP_WIDTH);
					++i
				)
			{
				innerloop : for(int j =  0; j <	map.size(); ++j)
				{
					if(map.get(j).get(i) == null)
					{
						//It doesn't have an encoding, so can't contain a reward type.
						continue innerloop;
					}
					if(map.get(j).get(i).getEncoding() == Encoding.COIN || map.get(j).get(i).getEncoding() == Encoding.FIRE_FLOWER)
					{
						rewardsFound.add(map.get(j).get(i));
					}
				}
			}
			return rewardsFound;
		}
		/**
		 * Gets an empty square close to the RHS of the screen
		 * @return MapSquare corresponding to this empty square
		 */
		public MapSquare getBestRightHandSide()
		{
			//whether we have found an environment piece in this column or not
			boolean found = false;
			
			//Iterate from the bottom right of the map, up each column towards Mario
			//Note: we have to be above the ground for Mario to be able to move towards it, so we look for the first non-Environment piece above an Environment piece.
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
						//debugPrint("Found a RHS square to move towards " + map.get(y).get(x));
						return map.get(y).get(x);
					}
				}
				/*
				 * If we haven't found a non-Environment square in this column (above an Environment piece)
				 * then move to next column & look again
				 */
				found = false;
			}
			return null;
		}
		/**
		 * Searches the immediate vicinity of Mario for enemies.
		 * @param xBound - how far in each direction along the x-axis we want to look
		 * @param yBound - how far in each direction along the y-axis we want to look
		 * @return list of MapSquares that contains enemies.
		 */
		public LinkedList<MapSquare> checkForEnemies(int xBound, int yBound)
		{
			enemyFound = false;
			LinkedList<MapSquare> enemies = new LinkedList<MapSquare>();
			for(int j = marioMapLoc[0] + yBound; j >= marioMapLoc[0] - yBound; --j)
			{
				for(int k = marioMapLoc[1] + xBound; k >= marioMapLoc[1] - xBound ; --k)
				{
					if(Encoding.isEnemySprite(map.get(j).get(k)))
					{
						enemyFound = true;
						enemies.add(map.get(j).get(k));
					}
				}
			}
			return enemies;
		}
		
		
		//The following are methods that can be used while debugging
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
		/**
		 * Prints out the current map in the agent's memory
		 */
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
