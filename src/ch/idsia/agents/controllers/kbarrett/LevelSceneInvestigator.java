package ch.idsia.agents.controllers.kbarrett;

import java.util.ArrayDeque;
import java.util.Deque;
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
		 * Used for translating between map co-ordinates & levelScene co-ordinates.
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
		private MapSquare[][] map;
		/**
		 * Stores the current plan of movement that Mario is executing.
		 */
		private Stack<MapSquare> plan;
		/**
		 * Stores Mario's current position within the map.
		 */
		private int[] marioMapLoc = {0,0};
		/** 
		 * Stores the physical size of a square in levelScene and map.
		 * @see ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator.levelScene
		 */
		private static final float SQUARESIZE = 16;// 9.5f;
		/**
		 * Stores the total number of coins Mario has collected so far.
		 */
		private int numberOfCollectedCoins = 0;
		
	//Methods for updating the data
		/**
		 * Used for updating the map from a newly acquired levelScene.
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
			
			if(debug && false)
			{
				printMap();
			}
		}
		/**
		 * Used for updating Mario's screen position.
		 * Note: will only update the stored position if it has varied by more than SQUARESIZE.
		 * This method also updates marioMapLoc, if Mario has moved into the space represented by a different position in map.
		 * @param marioScreenPos a float array of size 2 containing Mario's actual position on the screen. Note: this is as an (x,y) coordinate.
		 */
		public void setMarioScreenPos(float[] marioScreenPos)
		{
			/*Swap the terms in marioScreenPos, so they match the y-x orientation of the map.*/
			float temp = marioScreenPos[0];
			marioScreenPos[0] = marioScreenPos[1];
			marioScreenPos[1] = temp;
				
			//If this is the first location we know for Mario, create the array & use the received position as his current position.
			if(this.marioScreenPos == null)
			{
				this.marioScreenPos = new float[2];
				this.marioScreenPos[0] = marioScreenPos[0];
				this.marioScreenPos[1] = marioScreenPos[1];
			}
			//Otherwise check it is sufficiently far away from the last known position before updating
			else if (
					Math.abs(this.marioScreenPos[0] - marioScreenPos[0]) > SQUARESIZE ||
					Math.abs(this.marioScreenPos[1] - marioScreenPos[1]) > SQUARESIZE
				)
			{
				//Update Mario's vertical position in the map
					if(this.marioScreenPos[0] - marioScreenPos[0] > SQUARESIZE) //Mario has moved downwards
					{
						++marioMapLoc[0];
					}
					else if(marioScreenPos[0] - this.marioScreenPos[0] > SQUARESIZE) //Mario has moved upwards
					{
						--marioMapLoc[0];
					}
				//Update Mario's horizontal position in the map
					if(marioScreenPos[1] - this.marioScreenPos[1] > SQUARESIZE) //Mario has moved right
					{
						++marioMapLoc[1];
					}
					else if(this.marioScreenPos[1] - marioScreenPos[1] > SQUARESIZE) //Mario has moved left
					{
						--marioMapLoc[1];
					}
					
					//Update the new current position to be here.
					this.marioScreenPos[0] = marioScreenPos[0];
					this.marioScreenPos[1] = marioScreenPos[1];
					justMoved = true;
					
			}
			//Otherwise we haven't just updated Mario's position, so he's in the same map square as previously
			else
			{
				justMoved = false;
			}
			
		}
		/**
		 * Used for updating marioLoc.
		 * @param marioLoc int array of size 2 representing the position of Mario in levelScene.
		 * @see ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator.marioLoc
		 */
		public void setMarioLoc(int[] marioLoc, Movement movement)
		{
			this.marioLoc = marioLoc;
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
		 * If no map has previously been created, creates a map of the given size.
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
		public int[] getNextLocation(boolean isFacingRight)
		{
			//If we don't already have a plan
			if((plan == null || plan.size() == 0)) 
			{
				//Find a good location to move towards
				int[] location = findLocation(isFacingRight);
				//If there isn't one
				if(location == null)
				{
					//return null, which will cause a default behaviour
					return null;
				}
				//Otherwise, make a plan to get to this location
				makePlan(location);
				//Return the first step of this plan
				return getNextPlanStep();
			}
			//If we have just made a move
			if(justMoved)
			{
				//If we're where we expected to be
				if(isInExpectedSquare())
				{
					//Remove the move we have just made from the stack
					plan.pop();
					//Return the next move in the plan
					return getNextPlanStep();
				}
				else
				{
					//Otherwise make a plan to get back on track
					replan();
					return getNextPlanStep();
				}
			}
			else
			{
				return getNextPlanStep();
			}
			
			//TODO: check for enemies
		}
		
		/**
		 * After a move has been made, decides whether we're in the square we thought we would be after that move.
		 * @return boolean indicating whether we're in the expected square or not.
		 */
		private boolean isInExpectedSquare()
		{
			//If we're in the square of the next step in the plan, then we're in the expected square
			return (marioMapLoc[0] == plan.peek().getMapLocationY() && marioMapLoc[1] == plan.peek().getMapLocationX());
		}
		/**
		 * Gets the next step in the plan (if one exists).
		 * @return int array of size 2 representing the location of the next step of the plan.
		 */
		private int[] getNextPlanStep()
		{
			//If we have no plan, we cannot get the next step from it.
			if(plan == null || plan.size() == 0) {return null;}
			
			//This is the next step of the plan
			MapSquare nextLocation = plan.peek();
			
			//FIXME: jumping workaround as currently Movement judges size of jump based on distance from current square
				int i = 1;
				MapSquare marioMapLocSquare = map[marioMapLoc[0]][marioMapLoc[1]];
				//While the next plan square is above this one, get the next one
				while(marioMapLocSquare!=null && marioMapLocSquare.getSquareAbove() == nextLocation)
				{
					//If we get to the end of the plan, stop looking
					if(i>=plan.size() - 1) {break;}
					//Replace the current square with the next plan square
					marioMapLocSquare = nextLocation;
					//get the next square in the plan
					nextLocation = plan.elementAt(i++);
				}
			
			//Put this step into an array
			int[] nextStep = new int[2];
			nextStep[0] = nextLocation.getMapLocationY(); 
			nextStep[1] = nextLocation.getMapLocationX();
			
			//If the next step is the same as this square, something has gone wrong.
			if(nextStep.equals(marioMapLoc))
			{
				debugPrint("ARGH ERROR");
			}
			
			//Return it
			return nextStep;
		}
		/**
		 * Attempts to find a way for Mario to get back on track with the old plan.
		 * If no way is found, the old plan will be removed.
		 */
		private void replan()
		{
			//How much longer the new plans are allowed to be than the previous plan
			int adjustment = 0;
			
			//Stores the best plan to rejoin the previous plan
			Stack<MapSquare> newPlan = new Stack<MapSquare>();
			//Stores the square at which the best plan joins the previous plan
			int rejoinSquare = -1;
			
			FirstAgent.debug = false;
			//For each step in the plan
			for(int i = 0; i<plan.size(); ++i)
			{
				//Find a new plan to this square from the current square, using the same number of steps (+ the adjustment value)
				Stack<MapSquare> thisPlan = Search.aStar(plan.get(i), map[marioMapLoc[0]][marioMapLoc[1]], plan.size() - i + adjustment);
				if(
						//we have successfully found a route to the required square
						thisPlan != null &&
						(
								//we haven't already made a new plan
								rejoinSquare < 0 || 
								//this plan is shorter than the previous plan
								thisPlan.size() + i < newPlan.size() + rejoinSquare
						)
					)
				{
					//Update the best plan
					rejoinSquare = i;
					newPlan = thisPlan;
				}
			}
			FirstAgent.debug = true;
			
			if(rejoinSquare < 0) //we failed to find a new route
			{
				System.err.println("Plan unachievable. Can't reach " + plan.peek() + " from " + map[marioMapLoc[0]][marioMapLoc[1]]);
				//remove plan as it is unachievable from the current position
				plan.clear();
			}
			else //we have found a new route
			{
				//Remove all steps in the plan before the point at which the new plan joins the old plan
				for(int i = 0; i < rejoinSquare; ++i)
				{
					plan.pop();
				}
				//Add the steps of the new plan to the old plan
				for(int i = newPlan.size() - 1; i >= 0; --i)
				{
					plan.push(newPlan.elementAt(i));
				}
			}
		}
		/**
		 * Makes a plan from the current position to the desiredPosition
		 * @param desiredPosition - a int array of size 2 representing the location that the plan should head towards
		 */
		private void makePlan(int[] desiredPosition)
		{
			plan = Search.aStar(map[desiredPosition[0]][desiredPosition[1]], map[marioMapLoc[0]][marioMapLoc[1]]);
		}
		/**
		 * Finds a location in the map that Mario should move towards.
		 * @param isFacingRight - whether or not Mario is currently facing right
		 * @return int array of size 2 representing a desirable location on the map for Mario to move towards
		 */
		private int[] findLocation(boolean isFacingRight)
		{
			int[] locationOfReward = getRewardLocation();
			if(locationOfReward != null)
			{
				return locationOfReward;
			}	
			
			int[] requiredlocationForBlockage = getBlockageLocation(isFacingRight);
			if(requiredlocationForBlockage != null)
			{
				return requiredlocationForBlockage;
			}
			
			return null;
		}
		public int[] getRewardLocation()
		{
			for(
					int i = MapUpdater.getMapXCoordinate(marioLoc[1] - Movement.MAX_JUMP_WIDTH / 2, marioMapLoc[1], marioLoc[1]);
					    i <  MapUpdater.getMapXCoordinate(marioLoc[1] + Movement.MAX_JUMP_WIDTH / 2, marioMapLoc[1], marioLoc[1]);
					++i
				)
			{
				for(
						int j =  MapUpdater.getMapYCoordinate(marioLoc[0] - Movement.MAX_JUMP_HEIGHT / 2, marioMapLoc[0], marioLoc[0]);
						j <	MapUpdater.getMapYCoordinate(marioLoc[1] + Movement.MAX_JUMP_HEIGHT / 2, marioMapLoc[0], marioLoc[0]);
						++j
					)
				{
					if(map[j][i] == null) {continue;}
					if(map[j][i].getEncoding() == Encoding.COIN)
					{
						int[] result = new int[2];
						result[0] = j;
						result[1] = i;
						return result;
					}
				}
			}
			return null;
		}
		
		public int[] getBlockageLocation(boolean facingRight)
		{
			int direction = 1;
			if(!facingRight)
			{
				direction = -1;
			}
			
			for(int i = 1; i < Movement.MAX_JUMP_WIDTH; ++i)
			{
				byte square = map[marioMapLoc[0]][marioMapLoc[1] + (i * direction)]!= null ? map[marioMapLoc[0]][marioMapLoc[1] + (i * direction)].getEncoding() : 0;
				if(Encoding.isEnvironment(square))
				{
					int[] result = new int[2];
	
					result[1] = marioMapLoc[1] + (i * direction);
					result[0] = marioMapLoc[0];
					while(Encoding.isEnvironment(map[result[0]][result[1]]))
					{
						result[0] -= 1;
					}
					return result;
				}
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
					String s = "";
					if(i == marioMapLoc[0] && j == marioMapLoc[1]) {s += "*";}
					if(map[i][j] == null) {s += ""+map[i][j];}
					else {s += ""+map[i][j].getEncoding();}
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
