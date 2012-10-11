package ch.idsia.agents.controllers.kbarrett;

public class LevelSceneInvestigator
{
	//Data
		private byte[][] levelScene;
		private int[] marioLoc;
		
	//Update
		public void setLevelScene(byte[][] levelScene)
		{
			this.levelScene = levelScene;
		}
		public void setMarioLoc(int[] marioLoc)
		{
			this.marioLoc = marioLoc;
		}

	//Analysis of Environment
		public byte[] getRewardLocation()
		{
			for(byte i = 0; i < levelScene.length; i++)
			{
				for(byte j = 5; j <= marioLoc[0]; j++)
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
			
			if(facingRight)
			{
				if(
						levelScene[marioLoc[0]][marioLoc[1] + 1] == -112  || levelScene[marioLoc[0]][marioLoc[1] + 2] == -112 ||
						levelScene[marioLoc[0]][marioLoc[1] + 1] == -90  || levelScene[marioLoc[0]][marioLoc[1] + 2] == -90 ||
						levelScene[marioLoc[0]][marioLoc[1] + 1] == -128 || levelScene[marioLoc[0]][marioLoc[1] + 2] == -128 ||
						levelScene[marioLoc[0]][marioLoc[1] + 1] == -22  || levelScene[marioLoc[0]][marioLoc[1] + 2] == -22 ||
						levelScene[marioLoc[0]][marioLoc[1] + 1] == -20  || levelScene[marioLoc[0]][marioLoc[1] + 2] == -20
				)
				{
					byte[] result = new byte[2];
					result[0] = (byte) (marioLoc[0]);
					result[1] = (byte) (marioLoc[1] + 1);
					return result;
				}
			}
			else
			{
				if(
						levelScene[marioLoc[0]][marioLoc[1] - 1] == -112  || levelScene[marioLoc[0]][marioLoc[1] - 2] == -112 ||
						levelScene[marioLoc[0]][marioLoc[1] - 1] == -90  || levelScene[marioLoc[0]][marioLoc[1] - 2] == -90 ||
						levelScene[marioLoc[0]][marioLoc[1] - 1] == -128 || levelScene[marioLoc[0]][marioLoc[1] - 2] == -128 ||
						levelScene[marioLoc[0]][marioLoc[1] - 1] == -22  || levelScene[marioLoc[0]][marioLoc[1] - 2] == -22 ||
						levelScene[marioLoc[0]][marioLoc[1] - 1] == -20  || levelScene[marioLoc[0]][marioLoc[1] - 2] == -20
				)
				{
					byte[] result = new byte[2];
					result[0] = (byte) (marioLoc[0] + 2);
					result[1] = (byte) (marioLoc[1] + 1);
					return result;
				}
			}
			return null;
		}
		
		
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
		
		
		//DEBUG
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
}
