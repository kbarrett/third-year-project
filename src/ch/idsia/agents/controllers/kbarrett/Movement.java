package ch.idsia.agents.controllers.kbarrett;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

/** 
 * This class is responsible for storing the array of actions that move Mario.
 * @author Kim Barrett
 */
public class Movement 
{	
	//Data
		/**
		 * Contains Mario's location in the map.
		 */
		private int[] marioMapLoc = new int[2];
		/**
		 * Contains whether Mario is facing right. Used to determine whether something is in front of or behind Mario.
		 */
		private boolean facingRight = true;
		/**
		 * Contains the actions that Mario is required to perform. Each position relates to an action.
		 * An array position being set to true indicates its corresponding action is being requested during 
		 * this frame & false means it is not. 
		 * From 0 to 5 the corresponding movements are: left, right, down, jump, speed, up.
		 */
		private boolean[] actions = new boolean[Environment.numberOfKeys];
		
			//Relating to jumping
				/**
				 * The required size of a jump (in terms of consecutive jump requests). 
				 * If this is set to 0, no jump is requested or currently taking place.
				 */
				private int jumpSize = 0;
				/**
				 * The current position in the jump - used to keep track of how many consecutive jump requests have occurred.
				 */
				private int currentJumpPoint = 0;
				/**
				 * The approximate number of squares (in terms of amount of space each element in the matrix levelScene corresponds to) high that Mario can jump.
				 * @see ch.idsia.agents.controllers.kbarrett.Movement#MAX_JUMP_WIDTH
				 */
				public static int MAX_JUMP_HEIGHT = 5;
				/**
				 * The approximate number of squares (in terms of amount of space each element in the matrix levelScene corresponds to) across that Mario can jump.
				 * @see ch.idsia.agents.controllers.kbarrett.Movement#MAX_JUMP_HEIGHT
				 */
				public static int MAX_JUMP_WIDTH = 5;
	
	//DEBUG
	boolean debug = FirstAgent.debug;
	private String toStringActions()
	{
		boolean d = FirstAgent.debug;
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

	//Methods relating to checking Mario Movements
		/**
		 * @return true if Mario is currently jumping & false is he is not.
		 */
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
		/**
		 * @return true if Mario is currently facing right & false is he is not.
		 */
		public boolean isFacingRight() {
			return facingRight;
		}

	//Methods relating to updating the data
		/**
		 * Updates the stored value for facingRight.
		 * @param facingRight boolean corresponding to whether Mario is currently facing right
		 */
		public void setFacingRight(boolean facingRight) {
			this.facingRight = facingRight;
		}
		/**
		 * Updates stored value for marioLoc.
		 * @param newLoc int array containing Mario's new location within the matrix of observations (levelScene).
		 */
		public void setMarioMapLoc(int[] newLoc)
		{
			marioMapLoc = newLoc;
		}
		/**
		 * Used to retrieve and reset the array of actions.
		 * @return a boolean array of size 6 containing the instructions for controlling Mario.
		 * @see ch.idsia.agents.controllers.kbarrett.Movement#actions
		 */
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
			
			/* 
			 * Copy the array containing the actions requested of Mario, reset all elements of the 
			 * original to be false ready for the next set of observations and then return the copy.
			 */
			boolean[] copyOfActions = actions.clone();
			actions = new boolean[Environment.numberOfKeys];
			return copyOfActions;
		}
	
	//Methods relating to Mario movements - these are involved in actually setting the booleans in the actions array
	
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
			
			if(location.equals(marioMapLoc)){LevelSceneInvestigator.debugPrint("WHY is location the same as marioMapLoc???");}
			
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
						jumpSize = Math.max(1, (int)(1.6 * (marioMapLoc[0] - location[0])));
						LevelSceneInvestigator.debugPrint("JUMPSIZE: " + jumpSize);
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
		private void defaultMove()
		{
			/*
			 * The princess (end of the level) will be towards the right, so most of the level should be right of 
			 * Mario, hence the best move when no information has been provided is to move towards the right.
			 */
			goRight();
		}
		/**
		 * Used to set the booleans in actions to the correct configuration to move Mario to the right.
		 * Note: when already decided to move left, this method will have no effect.
		 */
		private void goRight()
		{
			/*
			 * Mario cannot move both left & right simultaneously so don't do anything if he has already been 
			 * asked to move left.
			 */
			if(!actions[Mario.KEY_LEFT])
			{
				//Set the correct element in the array to be true & update the knowledge about which way Mario is facing.
				actions[Mario.KEY_RIGHT] = true;
				setFacingRight(true);
			}
		}
		/**
		 * Used to set the booleans in actions to the correct configuration to move Mario to the left.
		 * Note: when already decided to move right, this method will have no effect.
		 */
		private void goLeft()
		{
			/*
			 * Mario cannot move both left & right simultaneously so don't do anything if he has already been 
			 * asked to move right.
			 */
			if(!actions[Mario.KEY_RIGHT])
			{
				//Set the correct element in the array to be true & update the knowledge about which way Mario is facing.
				actions[Mario.KEY_LEFT] = true;
				setFacingRight(false);
			}
		}
		/**
		 * Used to update booleans in actions when Mario is jumping.
		 */
		private void jump()
		{
			/*
			 * If the required number of jump requests have been made, do nothing.
			 * This means that we are still in the air but have finished jumping higher.
			 */
			if(currentJumpPoint>jumpSize)
			{
				return;
			}
			/*
			 * Otherwise set the correct boolean to true & update knowledge about where in the jump we are.
			 */
			actions[Mario.KEY_JUMP] = true;
			++currentJumpPoint;
		}
		/**
		 * Used to update Mario's knowledge about how far through a jump at the point when he lands.
		 */
		/*
		 * Note: this method is public because it is used by FirstAgent.
		 * Jumping is not based on observations made of the environment, so landing requires 
		 * external knowledge to that stored in this class.
		 */
		public void land()
		{
			//Reset the information about the current jump to indicate we are no longer jumping.
			currentJumpPoint = 0;
			jumpSize = 0;
		}
}
