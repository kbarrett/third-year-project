package ch.idsia.agents.controllers.kbarrett.test;

import ch.idsia.agents.controllers.kbarrett.LevelSceneInvestigator;
import ch.idsia.agents.controllers.kbarrett.Movement;

public class LevelSceneInvestigatorTester {
	
	static LevelSceneInvestigator levelSceneInvestigator;
	static int[] marioLoc = {5,3};
	static float[] marioScreenPos = {0,0};
	static Movement movement;
	static byte[][] levelScene = {
				{0, 	0,	 0,	  0,   0, 	0,	 0},
				{0, 	0,	 0,	  0,   0, 	0,	 0},
				{0, 	0,	 0,	  0,   2, 	0,	 0},
				{0, 	0,	 0,/**/0, -60, 	0, 	 0},
				{0, 	0,	 0,	  0, -60,	0, 	 0},
				{0, 	0,	 0,	  0, -60, 	0, 	 0},
				{-60, -60, -60, -60, -60, -60, -60}};
	
	public static void main(String[] args)
	{
		while(true)
		{
		levelSceneInvestigator = new LevelSceneInvestigator();
		movement = new Movement();
		
		levelSceneInvestigator.giveMapSize(7, 7);
		levelSceneInvestigator.setMarioLoc(marioLoc, movement);
		
		//Give LevelSceneInvestigator the new LevelScene & Mario's new screen position
			levelSceneInvestigator.updateMapFromLevelScene(levelScene);
			levelSceneInvestigator.setMarioScreenPos(marioScreenPos);
			
			if(movement.isJumping() && true)
			{
				movement.land();
			}
			//Decide next movement and pass this to Movement to be acted upon
			else
			{
				movement.moveTowards(
						levelSceneInvestigator.getNextLocation(movement.isFacingRight(), movement.isJumping()));
			}
			
		movement.reset();
		}
	}

}
