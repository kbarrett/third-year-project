package ch.idsia.agents.controllers.kbarrett.first;

import java.util.ArrayList;
import java.util.Stack;

import ch.idsia.agents.controllers.kbarrett.Encoding;
import ch.idsia.agents.controllers.kbarrett.first.MapSquare.Direction;

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
		
		private boolean enemyFound = false;
		
		private PlanStorer planStorer;
		
		private int marioMode;
		
		private boolean stayStationary = false;
		/**
		 * Direction Mario is currently moving in, where -1 is left, 0 is not moving & 1 is right.
		 */
		private byte directionOfMovement = 0;
		
		public LevelSceneInvestigator()
		{
			planStorer = new PlanStorer();
		}
		
		private boolean isStationary()
		{
			System.out.println("dOM: " + directionOfMovement + " so " + (directionOfMovement == 0));
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
			if(FirstAgent.debug)
			{
				//checks for any previously unencountered encodings
				checkLevelScene(levelScene);
			}
			
			//Updates map using this levelScene
			MapUpdater.updateMap(map, levelScene, marioMapLoc);
			//Checks whether any step of the plan has become an Environment piece by this update.
			planStorer.checkPlan();
			
			if(debug && printMap)
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
					//debugPrint("Just moved from " + prevMapLoc[0] + "," + prevMapLoc[1] + " to " + marioMapLoc[0] + "," + marioMapLoc[1]);
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
		
		public int[] getMarioMapLoc()
		{
			return marioMapLoc;
		}
		
		public void setMarioMode(int marioMode)
		{
			this.marioMode = marioMode;
		}
		public int getMarioMode()
		{
			return marioMode;
		}
		
		public boolean isEnemy()
		{
			return enemyFound;
		}
		
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
			boolean lastStepAchieved = planStorer.isPlanStepAchieved(getMarioMapSquare());
			
			//If we don't already have a plan or we have an unimportant plan (i.e. one that just moves us across the board)
			if(!planStorer.havePlan() || !planStorer.isImportant()) 
			{
				makeNewPlan();
			}
			
			//If we have just made a move
			if(justMoved)
			{
				stayStationary = false;
				
				//if we are not where we expected to be
				if(!lastStepAchieved)
				{
					planStorer.replan(getMarioMapSquare(), getMarioMode());
				}
			}
			
			System.out.println("MARIO AT: " + marioMapLoc[0] + "," + marioMapLoc[1]);
			System.out.println("PLAN: " + planStorer.plan);
			
			MapSquare s = getLocationToMoveTo(isJumping);
			System.out.println("Square to move to " + s);
			return s;
		}
		
		private MapSquare getLocationToMoveTo(boolean isJumping)
		{
			if(false && !isStationary() && !isJumping && stayStationary)
			{
				System.out.println("NOT MOVING - NEED TO BE STATIONARY");
				MapSquare newSquare = getMapSquare(getMarioMapSquare().getMapLocationY(), getMarioMapSquare().getMapLocationX() - directionOfMovement);
				
				return newSquare;
				//FIXME: warning - below line is bullshit place-holding
				//return new MovementInstruction(Direction.Above, null);
			}
			else
			{
				MapSquare s =  planStorer.getLocationToMoveTo(getMarioMapSquare(), this);
				if(!isStationary() && !isJumping && getMarioMapSquare().getSquareAbove().equals(s))
				{
					stayStationary = true;
					return getMarioMapSquare();
					//FIXME: warning - below line is bullshit place-holding
					//return new MovementInstruction(Direction.Above, null);
				}
				return s;
			}
		}
		
		/**
		 * @return boolean saying whether the next step of the plan is adjacent to our current position
		 */
		private boolean nextPlanStepAdjacent()
		{
			MapSquare nextLocation = planStorer.getNextPlanLocation();
			if(nextLocation == null)
			{
				return false;
			}
			
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
		/**
		 * @return MapSquare that Mario is currently occupying
		 */
		private MapSquare getMarioMapSquare()
		{
			return map.get(marioMapLoc[0]).get(marioMapLoc[1]);
		}
		
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
		
		private ArrayList<MapSquare> getAllRewards()
		{
			ArrayList<MapSquare> rewardsFound = new ArrayList<MapSquare>(2 * Movement.MAX_JUMP_WIDTH);
			for(
					int i = Math.max(0, marioMapLoc[1] - Movement.MAX_JUMP_WIDTH);
					    i < Math.min(map.get(0).size(), marioMapLoc[1] + Movement.MAX_JUMP_WIDTH);
					++i
				)
			{
				innerloop : for(
						int j =  0;
						j <	map.size();
						++j
					)
				{
					if(map.get(j).get(i) == null)
					{
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
						debugPrint("Found a RHS square to move towards " + map.get(y).get(x));
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
		
		public MapSquare checkForEnemies(int xBound, int yBound)
		{
			return null;
			/*for(int j = marioMapLoc[0]; j >= Math.max(yBound, 0) ; --j)
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
		*/}
		
		//DEBUG
		boolean debug = FirstAgent.debug;
		boolean printMap = false;
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
