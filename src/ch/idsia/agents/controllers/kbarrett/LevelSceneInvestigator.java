package ch.idsia.agents.controllers.kbarrett;

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
		 * Stores the total number of coins Mario has collected so far.
		 */
		private int numberOfCollectedCoins = 0;
		
		private int headButtCount = 0;
		
	//Methods for updating the data
		/**
		 * Used for updating the levelScene when a new one is acquired.
		 * @param levelScene a 2D array representing the current environment of Mario encoded as integers.
		 * @see ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator.levelScene
		 */
		public void setLevelScene(byte[][] levelScene)
		{
			this.levelScene = levelScene;
			if(FirstAgent.debug){checkLevelScene();}
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
				headButtCount = 0;
			}
			//TODO: To be used to see if a coin has been achieved by an action in order to give up if impossible 
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
			int whichCoin = 0;
			for(byte i = (byte) (marioLoc[1] - (Movement.MAX_JUMP_WIDTH/2)); i < (marioLoc[1] + (Movement.MAX_JUMP_WIDTH/2)); ++i)
			{
				rows : for(byte j = (byte) (marioLoc[0] - Movement.MAX_JUMP_HEIGHT); j < levelScene[i].length; j++)
				{
					if(levelScene[j][i] == Encoding.COIN)
					{	
						if(headButtCount > 0 && whichCoin < headButtCount)
						{
							whichCoin++;
							continue;
						}
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
									headButtCount += 5;
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
								headButtCount += 5;
								continue;
							}
							}
						}

						result[1] = i;
						headButtCount--;
						return result;
					}
				}
			}
			headButtCount--;
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
		
}
