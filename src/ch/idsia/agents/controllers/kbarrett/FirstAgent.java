package ch.idsia.agents.controllers.kbarrett;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

public class FirstAgent implements Agent {
	
	//DEBUG VALUES
	boolean printingLevelScene = true;
	
	String name = "FirstAgent";
	
	//Encodings of Environment
	byte COIN = 2;
	
	//Relating to jumping
	int jumpSize = 0;
	int currentJumpPoint = 0;
	
	//Knowledge & decisions
	boolean[] actions = new boolean[Environment.numberOfKeys];
	byte[][] levelScene;
	boolean facingRight = true;

	@Override
	public boolean[] getAction() {
		
		if(isJumping())
		{
			jump();
		}
		else if (!actionsHaveBeenDecided());
		{
			goRight();
		}
	
		return resetActions();
	}

	@Override
	public void integrateObservation(Environment environment) {
		
		levelScene = environment.getLevelSceneObservationZ(0);
		if(printingLevelScene) {printLevelScene();}
		
		if(isJumping() && environment.isMarioOnGround())
		{
			land();
		}
		
		else
		{
			byte[] locationOfReward = getRewardLocation();
			
			if(locationOfReward != null)
			{
				moveTowards(locationOfReward);
			}
			
			byte[] locationOfBlockage = getBlockageLocation();
			if(locationOfBlockage != null)
			{
				byte[] requiredLocation = new byte[2];
				requiredLocation[0] = (byte) (locationOfBlockage[0] + 1);
				requiredLocation[1] = (byte) (locationOfBlockage[1] + 1);
				moveTowards(requiredLocation);
			}
		}
	}

	@Override
	public void giveIntermediateReward(float intermediateReward) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setObservationDetails(int rfWidth, int rfHeight, int egoRow, int egoCol) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	//Checking Mario Movements
	private boolean isJumping()
	{
		return jumpSize > 0;
	}
	
	private boolean[] resetActions()
	{
		boolean[] copyOfActions = actions.clone();
		actions = new boolean[Environment.numberOfKeys];
		return copyOfActions;
	}
	
	//Mario Movements
	private void moveTowards(byte[] location)
	{
		if(location.length != 2)
		{
			System.err.println("Cannot move to this location.");
			return;
		}
		
		//vertical movement
		if(location[1] >= 9)
		{
			jumpSize = Math.max(1, (int)(location[1] - 9 / 4));
		}
		
		//horizontal movement
		if(location[0] >= 9)
		{
			goRight();
		}
		else
		{
			goLeft();
		}
		
	}
	private void goRight()
	{
		if(!actions[Mario.KEY_LEFT])
		{
			actions[Mario.KEY_RIGHT] = true;
			facingRight = true;
		}
	}
	private void goLeft()
	{
		if(!actions[Mario.KEY_RIGHT])
		{
			actions[Mario.KEY_LEFT] = true;
			facingRight = false;
		}
	}
	private void jump()
	{
		if(currentJumpPoint>jumpSize) {return;}
		actions[Mario.KEY_JUMP] = true;
		++currentJumpPoint;
	}
	private void land()
	{
		currentJumpPoint = 0;
		jumpSize = 0;
	}
	
	//Analysis of Environment
	private byte[] getRewardLocation()
	{
		for(byte i = 0; i < levelScene.length; i++)
		{
			for(byte j = 0; j< levelScene[i].length; j++)
			{
				if(levelScene[i][j] == 2)
				{
					byte[] result = new byte[2];
					result[0] = i;
					result[1] = j;
				}
			}
		}
		return null;
	}
	private byte[] getBlockageLocation()
	{
		//default when facing left
		/*int increment = -1;
		int bound = 0;
		if(facingRight)
		{
			increment = 1;
			bound = levelScene.length;
		}*/
		for(byte i = 9; i < levelScene.length; i++)
		{
			for(byte j = 9; j < levelScene[i].length; j++)
			{
				if(levelScene[i][j] <= 0)
				{
					byte[] result = new byte[2];
					result[0] = i;
					result[1] = j;
					return result;
				}
			}
		}
		return null;
	}
	
	private void printLevelScene()
	{
		//default when facing left
		if(!facingRight){printLeft();}
		else{printRight();}
	}
	private void printLeft()
	{
		for(byte i = 0; i <= 9; i++)
		{
			for(byte j = 0; j <= 18; j++)
			{
				printLevelSceneLoc(i,j);
			}
		}
		System.out.println("");
	}
	private void printRight()
	{
		for(byte i = 9; i <= 18; i++)
		{
			for(byte j = 9; j <= 18; j++)
			{
				printLevelSceneLoc(i,j);
			}
		}
		System.out.println("");
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
	
	private boolean actionsHaveBeenDecided()
	{
		for(int i = 0; i< actions.length; i++)
		{
			if(actions[i]==true) {return true;}
		}
		return false;
	}
	
}
