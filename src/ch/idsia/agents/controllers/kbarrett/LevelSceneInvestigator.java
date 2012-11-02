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
		private int[] marioLoc;
		/**
		 * Stores the float position of Mario on the screen.
		 */
		private float[] marioScreenPos;
		private boolean justMoved = false;
		private MapSquare[][] map;
		private Stack<MapSquare> plan;
		private int[] marioMapLoc = {0,0};
		/** 
		 * Stores the physical size of a square in levelScene.
		 * @see ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator.levelScene
		 */
		private static final float SQUARESIZE = 16;//20.5f;
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
					justMoved = true;
					
			}
			else
			{
				justMoved = false;
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
		public int[] decideLocation(boolean isFacingRight)
		{
			int[] locationOfReward = getRewardLocation();
			if(locationOfReward != null)
			{
				return getRoute(locationOfReward, isFacingRight);
			}	
			
			int[] requiredlocationForBlockage = getBlockageLocation(isFacingRight);
			if(requiredlocationForBlockage != null)
			{
				return getRoute(requiredlocationForBlockage, isFacingRight);
			}
			
			return plan == null || plan.size() == 0 ? null : getRoute(marioMapLoc, isFacingRight);
		}
		
		
		
		private int[] getRoute(int[] desiredPosition, boolean isFacingRight)
		{

			MapSquare lastMove = null;
			
			if(plan == null || plan.size() == 0)
			{
				MapSquare s = map[desiredPosition[0]][desiredPosition[1]];
				plan = Search.aStar(s, map[marioMapLoc[0]][marioMapLoc[1]]);
			}
			else if(justMoved || (marioMapLoc[0] == plan.peek().getMapLocationY() && marioMapLoc[1] == plan.peek().getMapLocationX()))
			{
				lastMove = plan.pop();
				
				//if we're not where we think we should be
				if(marioMapLoc[0] != lastMove.getMapLocationY() || marioMapLoc[1] != lastMove.getMapLocationX())
				{
					Stack<MapSquare> newPlan = new Stack<MapSquare>();
					int rejoinSquare = -1;
					for(int i = 0; i<plan.size(); ++i)
					{
						Stack<MapSquare> thisPlan = Search.aStar(plan.get(i), map[marioMapLoc[0]][marioMapLoc[1]]);
						if(thisPlan != null && (rejoinSquare < 0 || thisPlan.size()<newPlan.size()))
						{
							rejoinSquare = i;
							newPlan = thisPlan;
						}
					}
					
					if(rejoinSquare < 0) //we failed to find a new route
					{
						//remove plan as it is unachievable
						plan = null;
					}
					else
					{
						for(int i = newPlan.size() - 1; i >= 0; --i)
						{
							plan.push(newPlan.elementAt(i));
						}
					}
				}
			}
			
			//check for plan
			//if !exists run astar
			//check for enemies
			//return first step of plan
			
			if(plan==null || plan.size() == 0) return desiredPosition;
			
			MapSquare nextLocation = plan.peek();
			
			int i = 1;
			while(lastMove!=null && lastMove.getSquareAbove() == nextLocation)
			{
				if(i>=plan.size() - 1) {break;}
				lastMove = nextLocation;
				nextLocation = plan.elementAt(i++);
			}
			
			desiredPosition[0] = nextLocation.getMapLocationY(); 
			desiredPosition[1] = nextLocation.getMapLocationX();
			return desiredPosition;
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
