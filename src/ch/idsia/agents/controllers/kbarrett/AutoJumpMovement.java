package ch.idsia.agents.controllers.kbarrett;

import ch.idsia.benchmark.mario.engine.sprites.Mario;

public class AutoJumpMovement extends Movement {
	/**
	 * The required size of a jump (in terms of consecutive jump requests). 
	 * If this is set to 0, no jump is requested or currently taking place.
	 */
	private int jumpSize = 0;
	/**
	 * The current position in the jump - used to keep track of how many consecutive jump requests have occurred.
	 */
	private int currentJumpPoint = 0;

	public boolean isJumping()
	{
		/*
		 * jumpSize is changed when a decision has been made that Mario should jump & is set to a
		 * positive integer determining the number of consecutive jump requests needed to jump 
		 * to the required height. It remains at this value until Mario lands again & the jump is
		 * over.
		 */
		return jumpSize > 0;
	}
	
	@Override
	public boolean[] reset()
	{
		/* 
		 * Jumping is an on-going action so may not have been decided to be done because of the 
		 * current set of environmental observations. If Mario is jumping, continue this jump.
		 */
		if(isJumping())
		{
			jump();
		}
		return super.reset();
	}
	
	/**
	 * Sets the action array to actions that will move Mario towards the provided location.
	 * @param location byte array of size 2 where location[0] is the row and location[1] is the column in the
	 * observation matrix levelScene that Mario is required to move towards.
	 */
	public void moveTowards(int[] location)
	{
		
		//If no location has been provided, make the default move
		if(location == null)
		{
			defaultMove();
			return;
		}
		
		//If the location provided is invalid, print out an error
		if(location.length != 2)
		{
			System.err.println("Cannot move to this location.");
			return;
		}
		
		//Deciding whether to make a vertical movement
			//If the location provided is higher on the screen than Mario's current position, he needs to move higher.
			if(location[0] < marioMapLoc[0])
			{
				//If Mario is already jumping, telling him to jump again will have no effect until he has landed so do nothing.
				if(!isJumping())
				{
					//Is he is not currently jumping, set the required jumpSize based on the height he needs to reach.
					switch(marioMapLoc[0] - location[0])
					{
					case 0 : jumpSize = 0; break;
					case 1 :
					case 2 : jumpSize = 2; break;
					case 3 : jumpSize = 4; break;
					default : jumpSize = 8; break;
					}
				}
			}
		
		//Deciding whether to make a horizontal movement
			//If the required location is further right on the screen than Mario, move him right.
			if(location[1] > marioMapLoc[1])
			{
				goRight();
			}
			//If it is further left, move him left.
			else if(location[1] < marioMapLoc[1])
			{
				goLeft();
			}
			//If it is level with where he is, don't move - this could be the case if jumping for a coin.
	}
	
	/**
	 * Used to move if no decision has been made about where he needs to move to next.
	 */
	@Override
	protected void defaultMove()
	{
		/*
		 * The princess (end of the level) will be towards the right, so most of the level should be right of 
		 * Mario, hence the best move when no information has been provided is to move towards the right.
		 */
		if(!isJumping() && true)// && 10 * Math.random() < 1)
		{
			jumpSize = MAX_JUMP_HEIGHT;
		}
		goRight();
	}

	@Override
	protected void jump()
	{
		/*
		 * If the required number of jump requests have been made, do nothing.
		 * This means that we are still in the air but have finished jumping higher.
		 */
		if(currentJumpPoint>jumpSize)
		{
			++currentJumpPoint;
			return;
		}
		/*
		 * Otherwise set the correct boolean to true & update knowledge about where in the jump we are.
		 */
		actions[Mario.KEY_JUMP] = true;
		++currentJumpPoint;
	}
	
	/*
	 * Note: this method is public because it is used by FirstAgent.
	 * Jumping is not based on observations made of the environment, so landing requires 
	 * external knowledge to that stored in this class.
	 */
	@Override
	public void land()
	{
		System.out.println("-----------------------------------------------------------" + currentJumpPoint);
		//Reset the information about the current jump to indicate we are no longer jumping.
		currentJumpPoint = 0;
		jumpSize = 0;
	}

}
