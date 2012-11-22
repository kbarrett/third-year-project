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
		 * Stores the current plan of movement that Mario is executing.
		 */
		private Stack<MapSquare> plan;
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
			
			if(debug && false)
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
				
			debugPrint("Current Position: " + marioScreenPos[0] + "," + marioScreenPos[1]);
			
			/*//If this is the first location we know for Mario, create the array & use the received position as his current position.
			if(this.marioScreenPos == null)
			{
				this.marioScreenPos = new float[2];
				this.marioScreenPos[0] = marioScreenPos[0] - SQUARESIZE/2;
				this.marioScreenPos[1] = marioScreenPos[1] - SQUARESIZE/2;
			}
				
			//Otherwise check it is sufficiently far away from the last known position before updating
			else if (Math.abs(this.marioScreenPos[0] - marioScreenPos[0]) > SQUARESIZE || Math.abs(this.marioScreenPos[1] - marioScreenPos[1]) > SQUARESIZE) 
			{
				if (Math.abs(this.marioScreenPos[0] - marioScreenPos[0]) > SQUARESIZE) 
				{
					//Update Mario's vertical position in the map
					marioMapLoc[0] += (int)((marioScreenPos[0] - this.marioScreenPos[0])/SQUARESIZE);
					debugPrint("Moved down :" + (int)((marioScreenPos[0] - this.marioScreenPos[0])/SQUARESIZE) + "squares");
				}
				if(Math.abs(this.marioScreenPos[1] - marioScreenPos[1]) > SQUARESIZE)
				{
					//Update Mario's horizontal position in the map
					marioMapLoc[1] += (int)((marioScreenPos[1] - this.marioScreenPos[1])/SQUARESIZE);
					debugPrint("Moved right :" + (int)((marioScreenPos[1] - this.marioScreenPos[1])/SQUARESIZE) + "squares");
				}
				//Update the new current position to be here.
				this.marioScreenPos[0] = marioScreenPos[0];
				this.marioScreenPos[1] = marioScreenPos[1];
				justMoved = true;
				System.out.println("MOVED TO " + marioMapLoc[0] +"," + marioMapLoc[1]);
			}
			//Otherwise we haven't just updated Mario's position, so he's in the same map square as previously
			else
			{
				justMoved = false;
			}*/
			
			if(this.marioScreenPos == null)
			{
				this.marioScreenPos = new float[2];
				this.marioScreenPos[0] = marioScreenPos[0] - SQUARESIZE/2;
				this.marioScreenPos[1] = marioScreenPos[1] - SQUARESIZE/2;
			}
			
			int[] prevMapLoc = new int[] {marioMapLoc[0], marioMapLoc[1]};
			marioMapLoc[0] = (int)((marioScreenPos[0] - this.marioScreenPos[0]) / SQUARESIZE) + 9;
			marioMapLoc[1] = (int)((marioScreenPos[1] - this.marioScreenPos[1]) / SQUARESIZE) + 9;
			if(prevMapLoc[0]!=marioMapLoc[0] || prevMapLoc[1]!=prevMapLoc[1])
			{
				debugPrint("Just moved from " + prevMapLoc[0] + "," + prevMapLoc[1] + " to " + marioMapLoc[0] + "," + marioMapLoc[1]);
				justMoved = true;
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
		/**
		 * Used to make a decision based on the environment about which location Mario needs to move to.
		 * @param isFacingRight indicating whether Mario is facing right or not.
		 * @return byte array of size 2 denoting the location that has been decided to move towards. 
		 * Returns null if no location has been chosen.
		 */
		public int[] getNextLocation(boolean isFacingRight, boolean isJumping)
		{
			//If we don't already have a plan
			if((plan == null || plan.size() == 0)) 
			{
				if(justify)
				{
					System.out.println("We have no plan.");
				}
				//Find a good location to move towards
				int[] location = findLocation(isFacingRight);
				if(location != null)
				{
					if(justify)
					{
						System.out.println("We found a good location.");
					}
					makePlan(location);
					if(justify && plan!=null)
					{
						System.out.println("plan is now: " + plan.toString());
					}
				}
				if(plan == null || plan.size() == 0)
				{
					if(justify)
					{
						System.out.println("There's no plan there.");
					}
					makePlan(getBestRightHandSide());
					if(justify && plan!=null)
					{
						System.out.println("Aiming at the RHS, so plan is now: " + plan.toString());
					}
				}
				
				return getNextPlanStep();
			}
			//If we have just made a move
			if(justMoved 
					//or we are in the square we wanted to head to
					|| plan.peek().equals(map.get(marioMapLoc[0]).get(marioMapLoc[1])) 
					|| !(
							//or the plan is not accessible in one turn - then something has gone wrong
							plan.peek().equals(map.get(marioMapLoc[0]+1).get(marioMapLoc[1]))
							|| plan.peek().equals(map.get(marioMapLoc[0]+1).get(marioMapLoc[1]+1))
							|| plan.peek().equals(map.get(marioMapLoc[0]+1).get(marioMapLoc[1]-1))
							|| plan.peek().equals(map.get(marioMapLoc[0]).get(marioMapLoc[1]+1))
							|| plan.peek().equals(map.get(marioMapLoc[0]).get(marioMapLoc[1]-1))
							|| plan.peek().equals(map.get(marioMapLoc[0]-1).get(marioMapLoc[1]+1))
							|| plan.peek().equals(map.get(marioMapLoc[0]-1).get(marioMapLoc[1]))
							|| plan.peek().equals(map.get(marioMapLoc[0]-1).get(marioMapLoc[1]-1))
							)
					)
			{
				if(justify)
				{
					System.out.println("We have just moved or we can't get to the right square - all has gone wrong!");
				}
				//If we're where we expected to be
				if(isInExpectedSquare())
				{
					if(justify)
					{
						System.out.println("Oo it's OK - we expected to be here.");
					}
					//Remove the move we have just made from the stack
					plan.pop();
					if(justify)
					{
						System.out.println("Now the plan is: " + plan.toString());
					}
					//Return the next move in the plan
					return getNextPlanStep();
				}
				else if(!isJumping)
				{
					if(justify)
					{
						System.out.println("We're not where we expected to be & are not jumping.");
					}
					//Otherwise make a plan to get back on track
					replan();
					if(justify)
					{
						System.out.println("Plan is: " + plan.toString());
					}
					return getNextPlanStep();
				}
				else
				{
					if(justify)
					{
						System.out.println("We were lost & jumping - aim at the RHS.");
					}
					plan.clear();
					makePlan(getBestRightHandSide());
					if(justify && plan!=null)
					{
						System.out.println("Plan is: " + plan.toString());
					}
					return null;
				}
			}
			else
			{
				if(justify)
				{
					System.out.println("We haven't moved far enough yet.");
				}
				return getNextPlanStep();
			}
			
			//FIXME: check for enemies
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
		 * Gets the next step in the {@link #plan} (if one exists).
		 * @return int array of size 2 representing the location of the next step of the plan.
		 */
		private int[] getNextPlanStep()
		{
			//If we have no plan, we cannot get the next step from it.
			if(plan == null || plan.size() == 0)
			{
				int[] loc = checkForEnemies(4, 4);
				if(loc!=null)
				{
					makePlan(loc);
					if(plan != null && plan.size() > 0)
					{
						return getNextPlanStep();
					}
				}
				return null;
			}
			
			//This is the next step of the plan
			MapSquare nextLocation = plan.peek();
			while(Encoding.isEnvironment(nextLocation.getEncoding()) && nextLocation.getEncoding()!= 0)
			{
				plan.pop();
				if(plan.size() == 0)
				{
					return checkForEnemies(4, 4);
				}
				nextLocation = plan.peek();
			}
			
			//FIXME: jumping workaround as currently Movement judges size of jump based on distance from current square
				int i = 1;
				MapSquare marioMapLocSquare = map.get(marioMapLoc[0]).get(marioMapLoc[1]);
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
				
			int[] enemies = checkForEnemies(nextLocation.getMapLocationX(), nextLocation.getMapLocationY());
			if(enemies!=null)
			{
				plan.push(map.get(enemies[0]).get(enemies[1]));
				return enemies;
			}
			
			//Put this step into an array
			int[] nextStep = new int[2];
			nextStep[0] = nextLocation.getMapLocationY(); 
			nextStep[1] = nextLocation.getMapLocationX();
			
			//If the next step is the same as this square, something has gone wrong.
			if(nextStep.toString().equals(marioMapLoc.toString()))
			{
				debugPrint("ARGH ERROR!!!!!!!!!!!!!");
			}
			
			//Return it
			return nextStep;
		}
		private int[] checkForEnemies(int xBound, int yBound)
		{
			for(int j = marioMapLoc[0]; j >= Math.max(yBound, 0) ; --j)
			{
				for(int k = marioMapLoc[1]; k >= Math.max(xBound, 0) ; --k)
				{
					if(Encoding.isEnemySprite(map.get(j).get(k)))
					{
						enemyFound = true;
						int[] result = new int[2];
						result[0] = j - 1;
						result[1] = k;
						return result;
					}
				}
			}
			enemyFound = false;
			return null;
		}
		/**
		 * Attempts to find a way for Mario to get back on track with {@link #plan}.
		 * If no way is found, {@link #plan} will be removed.
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
				Stack<MapSquare> thisPlan = Search.aStar(plan.get(i), map.get(marioMapLoc[0]).get(marioMapLoc[1]), plan.size() - i + adjustment);
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
				System.err.println("Plan unachievable. Can't reach " + plan.peek() + " from " + map.get(marioMapLoc[0]).get(marioMapLoc[1]));
				//remove plan as it is unachievable from the current position
				plan.clear();
			}
			else //we have found a new route
			{
				debugPrint("Replanning from square " + rejoinSquare);
				//Remove all steps in the plan before the point at which the new plan joins the old plan
				for(int i = rejoinSquare; i < plan.size(); ++i)
				{
					plan.pop();
				}
				//Add the steps of the new plan to the old plan
				for(int i = newPlan.size() - 1; i >= 0; --i)
				{
					plan.push(newPlan.elementAt(i));
				}
				debugPrint("new plan: " + plan.toString());
			}
		}
		/**
		 * Makes a plan from the current position {@link #marioMapLoc} to the desiredPosition and stores it in {@link #plan}.
		 * @param desiredPosition - a int array of size 2 representing the location that the plan should head towards
		 */
		private void makePlan(int[] desiredPosition)
		{
			plan = Search.aStar(map.get(desiredPosition[0]).get(desiredPosition[1]), map.get(marioMapLoc[0]).get(marioMapLoc[1]));
			if(plan!=null)debugPrint("Made new plan of size " + plan.size());
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
						int[] result = new int[2];
						result[0] = j;
						result[1] = i;
						return result;
					}
				}
			}
			return null;
		}
		
		public int[] getBestRightHandSide()
		{
			for(int i = map.size() - 1; i>=0; --i)
			{
				MapSquare rightHandSquare = map.get(i).get(map.get(i).size() - 1);
				if(rightHandSquare!=null && !Encoding.isEnvironment(rightHandSquare))
				{
					if(justify)
					{
						System.out.println("We found RHS at " + rightHandSquare);
						System.out.println("Mario is at " + marioMapLoc[0] + "," + marioMapLoc[1]);
					}
					return rightHandSquare.getMapLocation();
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
		private boolean justify = true;
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
