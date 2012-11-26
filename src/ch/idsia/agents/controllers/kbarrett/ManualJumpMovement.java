package ch.idsia.agents.controllers.kbarrett;

import ch.idsia.benchmark.mario.engine.sprites.Mario;

public class ManualJumpMovement extends Movement {
	
	private boolean jumping = false;
	private boolean canJumpNextGo = true;
	
	@Override
	public boolean isJumping()
	{
		return jumping;
	}

	@Override
	public void moveTowards(int[] location) {
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
					if(canJumpNextGo)
					{
						jump();
						jumping = true;
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
				
				canJumpNextGo = true;
	}

	@Override
	protected void defaultMove()
	{
		goRight();
		jump();
	}

	@Override
	protected void jump()
	{
		actions[Mario.KEY_JUMP] = true;
	}
	@Override
	public void land()
	{
		jumping = canJumpNextGo = false;
	}

}
