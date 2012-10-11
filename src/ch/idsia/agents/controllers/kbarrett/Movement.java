package ch.idsia.agents.controllers.kbarrett;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

public class Movement {
	
	//Relating to jumping
	private int jumpSize = 0;
	private int currentJumpPoint = 0;
	
	//Data
	private int[] marioLoc = new int[2];
	private boolean facingRight = true;
	private boolean[] actions = new boolean[Environment.numberOfKeys];
	
	//DEBUG
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

	//Checking Mario Movements
	public boolean isJumping()
	{
		return jumpSize > 0;
	}	
	public boolean isFacingRight() {
		return facingRight;
	}
	public boolean actionsHaveBeenDecided()
	{
		for(int i = 0; i< actions.length; i++)
		{
			if(actions[i]==true) {return true;}
		}
		return false;
	}

	//Updating data
	public void setFacingRight(boolean facingRight) {
		this.facingRight = facingRight;
	}
	public void setMarioLoc(int[] newLoc)
	{
		marioLoc = newLoc;
	}
	public boolean[] reset()
	{
		boolean[] copyOfActions = actions.clone();
		actions = new boolean[Environment.numberOfKeys];
		return copyOfActions;
	}
	
	//Mario Movements
	public void moveTowards(byte[] location)
	{
		System.out.println("");
		
		if(location.length != 2)
		{
			System.err.println("Cannot move to this location.");
			return;
		}
		
		//vertical movement
		if(location[0] <= marioLoc[0])
		{
			jumpSize = Math.max(1, (int)(marioLoc[0] - location[0]));
		}
		
		//horizontal movement
		if(location[1] >= marioLoc[1])
		{
			goRight();
		}
		else
		{
			goLeft();
		}
	}
	public void goRight()
	{
		if(!actions[Mario.KEY_LEFT])
		{
			actions[Mario.KEY_RIGHT] = true;
			setFacingRight(true);
		}
	}
	public void goLeft()
	{
		if(!actions[Mario.KEY_RIGHT])
		{
			actions[Mario.KEY_LEFT] = true;
			setFacingRight(false);
		}
	}
	public void jump()
	{
		if(currentJumpPoint>jumpSize) {return;}
		actions[Mario.KEY_JUMP] = true;
		++currentJumpPoint;
	}
	public void land()
	{
		currentJumpPoint = 0;
		jumpSize = 0;
	}
}
