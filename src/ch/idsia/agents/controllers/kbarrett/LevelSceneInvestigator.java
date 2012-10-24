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
		private static final float SQUARESIZE = 20.5f;
		/**
		 * Stores the total number of coins Mario has collected so far.
		 */
		private int numberOfCollectedCoins = 0;
		
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
			
			this.levelScene = levelScene;
				if(FirstAgent.debug){checkLevelScene();}
				
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
					
				map = MapUpdater.updateMap(map, levelScene, marioMapLoc);
				if(debug && false)
				{
					printMap();
				}
			}
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
		
		private MapSquare[] aStar(MapSquare destination)
		{
			TreeSet<MapSquareWrapper> exploredSquares = new TreeSet<MapSquareWrapper>(new Comparator<MapSquareWrapper>(){

				@Override
				public int compare(MapSquareWrapper msw1, MapSquareWrapper msw2) {
					return Integer.compare(msw1.getH() + msw1.getG(), msw2.getH() + msw2.getG());
				}});
			
			LinkedList<MapSquareWrapper> expandedSquares = new LinkedList<MapSquareWrapper>();
			
			MapSquareWrapper initialSquare = new MapSquareWrapper(map[marioMapLoc[0]][marioMapLoc[1]], null);
			initialSquare.setH(0);
			exploredSquares.add(initialSquare);
			
			while(!exploredSquares.isEmpty())
			{
				MapSquareWrapper currentSquare = exploredSquares.pollFirst();
				if(currentSquare.equals(destination))
				{
					//TODO: backtrack route
					if(debug)
					{
						System.out.println("We fucking found " + destination + " with G : " + currentSquare.getG());
					}
					return null; //TODO: put result in here
				}
				for(MapSquare s : currentSquare.getMapSquare().getReachableSquares())
				{
					MapSquareWrapper msw = new MapSquareWrapper(s, currentSquare);
					if(s == null || expandedSquares.contains(s) || msw.checkParentTreeFor(s))
					{
						continue;
					}
					if(msw.getH() == -1) {msw.calculateH(initialSquare.getMapSquare());}
					msw.setG(currentSquare.getG() + 1);
					exploredSquares.add(msw);
				}
				expandedSquares.add(currentSquare);
			}
			if(debug)
				System.err.println("Oh shit. We didn't find " + destination + " ..." + destination.getEncoding());
			return null;
		}
		
		private byte[] getGapAvoidanceLocation(byte[] desiredPosition, boolean isFacingRight) {

			if(debug && map[marioMapLoc[0]][marioMapLoc[1]] != null) 
			{
				aStar(map[desiredPosition[0] + marioMapLoc[0] - (levelScene.length / 2)][desiredPosition[1] + marioMapLoc[1] - (levelScene.length / 2)]);
				
				MapSquare marioLoc = map[marioMapLoc[0]][marioMapLoc[1]];
				try{
				debugPrint(""+marioLoc.isReachable(map[marioMapLoc[0] - 1][marioMapLoc[1] - 1])
				+""+marioLoc.isReachable(map[marioMapLoc[0] - 1][marioMapLoc[1]])
				+""+marioLoc.isReachable(map[marioMapLoc[0] - 1][marioMapLoc[1] + 1]));
				debugPrint(""+marioLoc.isReachable(map[marioMapLoc[0]][marioMapLoc[1] - 1])
				+""+marioLoc.isReachable(map[marioMapLoc[0]][marioMapLoc[1]])
				+""+marioLoc.isReachable(map[marioMapLoc[0]][marioMapLoc[1] + 1]));
				debugPrint(""+marioLoc.isReachable(map[marioMapLoc[0] + 1][marioMapLoc[1] - 1])
				+""+marioLoc.isReachable(map[marioMapLoc[0] + 1][marioMapLoc[1]])
				+""+marioLoc.isReachable(map[marioMapLoc[0] + 1][marioMapLoc[1] + 1]));}
				catch(Exception e){}
			}
			
			int increment = 1;
			if(marioLoc[1] > desiredPosition[1]) {increment = -1;}
			
			if //if immediately below Mario's next step is empty
			(
					levelScene[desiredPosition[0]][marioLoc[1] + increment] == Encoding.NOTHING
			)
			{
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
				for(byte j = (byte) (marioLoc[0] - Movement.MAX_JUMP_HEIGHT); j < levelScene[i].length; j++)
				{
					if(levelScene[j][i] == Encoding.COIN)
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
