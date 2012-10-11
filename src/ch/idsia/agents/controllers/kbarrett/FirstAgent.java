package ch.idsia.agents.controllers.kbarrett;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

public class FirstAgent implements Agent {
	
	//DEBUG VALUES
	boolean printingLevelScene = true;
	boolean debug = true;
	
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
	int[] marioLoc;

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
		
		marioLoc = environment.getMarioEgoPos();
		levelScene = environment.getMergedObservationZZ(0, 0);
		if(printingLevelScene) {printLevelScene();}
		
		if(isJumping() && environment.isMarioOnGround())
		{
			if(debug){System.out.println("LANDED");}
			land();
		}
		
		else
		{
			byte[] locationOfReward = null;// = getRewardLocation();
			
			if(locationOfReward != null)
			{
				if(debug){System.out.println("FOUND REWARD");}
				moveTowards(locationOfReward);
			}
			
			else
			{	
				byte[] locationOfBlockage = getBlockageLocation();
				if(locationOfBlockage != null)
				{
					if(debug){System.out.println("FOUND BLOCKAGE");}
					byte[] requiredLocation = new byte[2];
					requiredLocation[0] = (byte) (locationOfBlockage[0] + 1);
					requiredLocation[1] = (byte) (locationOfBlockage[1] + 1);
					moveTowards(requiredLocation);
				}
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
		System.out.println("");
		
		if(location.length != 2)
		{
			System.err.println("Cannot move to this location.");
			return;
		}
		
		//vertical movement
		if(location[1] >= marioLoc[1])
		{
			jumpSize = Math.max(1, (int)((location[1] - marioLoc[1]) / 4));
		}
		
		//horizontal movement
		if(location[0] >= marioLoc[0])
		{
			goRight();
		}
		else
		{
			goLeft();
		}
		System.out.println(toStringActions());
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
					return result;
				}
			}
		}
		return null;
	}
	private byte[] getBlockageLocation()
	{
		printLevelSceneLoc((byte)(marioLoc[0] ), (byte)(marioLoc[1] + 1));
		printLevelSceneLoc((byte)(marioLoc[0] ), (byte)(marioLoc[1] + 2));
		
		if(facingRight)
		{
			if(
					levelScene[marioLoc[0]][marioLoc[1] + 1] == -112  || levelScene[marioLoc[0]][marioLoc[1]+2] == -112 ||
					levelScene[marioLoc[0]][marioLoc[1] + 1] == -90  || levelScene[marioLoc[0]][marioLoc[1]+2] == -90 ||
					levelScene[marioLoc[0]][marioLoc[1] + 1] == -128 || levelScene[marioLoc[0]][marioLoc[1]+2] == -128 ||
					levelScene[marioLoc[0]][marioLoc[1] + 1] == -22  || levelScene[marioLoc[0]][marioLoc[1]+2] == -22 ||
					levelScene[marioLoc[0]][marioLoc[1] + 1] == -20  || levelScene[marioLoc[0]][marioLoc[1]+2] == -20
			)
			{
				byte[] result = new byte[2];
				result[0] = (byte) (marioLoc[0] + 2);
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
	
	private void printLevelScene()
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
	
	private String toStringActions()
	{
		String s = "{";
		if(actions[Mario.KEY_DOWN])
		{
			s += "DOWN, ";
		}
		if(actions[Mario.KEY_UP])
		{
			s += "UP, ";
		}
		if(actions[Mario.KEY_JUMP])
		{
			s += "JUMP, ";
		}
		if(actions[Mario.KEY_LEFT])
		{
			s += "LEFT, ";
		}
		if(actions[Mario.KEY_RIGHT])
		{
			s += "RIGHT, ";
		}
		if(actions[Mario.KEY_SPEED])
		{
			s += "SPEED, ";
		}
		s+= "}";
		return s;
	}
	
}
