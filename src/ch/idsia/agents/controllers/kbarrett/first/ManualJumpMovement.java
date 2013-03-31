package ch.idsia.agents.controllers.kbarrett.first;

import ch.idsia.benchmark.mario.engine.sprites.Mario;

/**
 * Uses a desired location to calculate an action array.
 * @see Movement
 * @author Kim
 */
public class ManualJumpMovement extends Movement
{
	/**
	 * Whether Mario is currently jumping.
	 */
	private boolean jumping = false;
	/**
	 * Whether Mario is allowed to jump on the next turn.
	 * He isn't allowed if he has just landed. This prevents requests to jump not being fulfilled
	 * by the program, as the "jump" key must be released before another jump can be performed.
	 */
	private boolean canJumpNextGo = true;
	/**
	 * Whether Mario is currently jumping.
	 */
	@Override
	public boolean isJumping()
	{
		return jumping;
	}
	/**
	 * Creates an action array that moves Mario towards the given location in the map.
	 */
	@Override
	public void moveTowards(int[] location) {
		//If no location has been provided, make the default move
			if(location == null)
			{
				defaultMove();
			}
			else
			{
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
						jump();
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
			
			//Mario has now performed at least one frame where he isn't jumping, so is allowed to jump again.
			canJumpNextGo = true;
	}
	/**
	 * The default move if no location has been provided.
	 */
	@Override
	protected void defaultMove()
	{
		goRight();
		jump();
	}
	/**
	 * Makes Mario jump if he is allowed to.
	 * @see ManualJumpMovement#canJumpNextGo
	 */
	@Override
	protected void jump()
	{
		if(canJumpNextGo)
		{
			actions[Mario.KEY_JUMP] = true;
			jumping = true;
		}
	}
	/**
	 * Called when Mario has just landed.
	 */
	@Override
	public void land()
	{
		//He has stopped jumping and isn't allowed to jump during the next frame.
		jumping = canJumpNextGo = false;
	}

}
