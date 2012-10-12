package ch.idsia.agents.controllers.kbarrett;

/**
 * This class is responsible for seeing what is in the vicinity of Mario.
 * @author Kim Barrett
 */
public class LevelSceneInvestigator
{
	//Data
		private byte[][] levelScene;
		private int[] marioLoc;
		private int numberOfCollectedCoins = 0;
		
	//Update
		public void setLevelScene(byte[][] levelScene)
		{
			this.levelScene = levelScene;
			if(FirstAgent.debug){checkLevelScene();}
		}
		public void setMarioLoc(int[] marioLoc)
		{
			this.marioLoc = marioLoc;
		}
		public void updateCoins(int coins)
		{
			if(numberOfCollectedCoins != coins)
			{
				numberOfCollectedCoins = coins;
			}
			//TODO: To be used to see if a coin has been achieved by an action in order to give up if impossible 
		}

	//Analysis of Environment
		public byte[] decideLocation(boolean isFacingRight)
		{
			byte[] locationOfReward = getRewardLocation();
			if(locationOfReward != null)
			{
				if(FirstAgent.debug)
				{
					System.out.println("FOUND REWARD");
				}
				return locationOfReward;
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
				return requiredLocation;
			}
			
			return null;
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
				result[1] = (byte) (marioLoc[1] + 1);
				return result;
			}
			
			return null;
		}
		
		//DEBUG
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
		
}
