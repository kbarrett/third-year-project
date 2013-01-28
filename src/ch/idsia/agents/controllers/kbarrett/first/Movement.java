package ch.idsia.agents.controllers.kbarrett.first;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

/** 
 * This class is responsible for storing the array of actions that move Mario.
 * @author Kim Barrett
 */
public abstract class Movement 
{	
	//Data
		/**
		 * Contains Mario's location in the map.
		 */
		protected int[] marioMapLoc = new int[2];
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
		protected boolean[] actions = new boolean[Environment.numberOfKeys];
		
			//Relating to jumping
				/**
				 * The approximate number of squares (in terms of amount of space each element in the matrix levelScene corresponds to) high that Mario can jump.
				 * @see ch.idsia.agents.controllers.kbarrett.first.Movement#MAX_JUMP_WIDTH
				 */
				public static int MAX_JUMP_HEIGHT = 3;
				/**
				 * The approximate number of squares (in terms of amount of space each element in the matrix levelScene corresponds to) across that Mario can jump.
				 * @see ch.idsia.agents.controllers.kbarrett.first.Movement#MAX_JUMP_HEIGHT
				 */
				public static int MAX_JUMP_WIDTH = 3;

	//Methods relating to checking Mario Movements

		/**
		 * @return true if Mario is currently facing right & false is he is not.
		 */
		public boolean isFacingRight() {
			return facingRight;
		}
		/**
		 * @return true if Mario is currently jumping & false is he is not.
		 */
		public abstract boolean isJumping();

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
		 * @see ch.idsia.agents.controllers.kbarrett.first.Movement#actions
		 */
		public boolean[] reset()
		{			
			/* 
			 * Copy the array containing the actions requested of Mario, reset all elements of the 
			 * original to be false ready for the next set of observations and then return the copy.
			 */
			boolean[] copyOfActions = actions.clone();
			actions = new boolean[Environment.numberOfKeys];
			return copyOfActions;
		}
	
	//Methods relating to Mario movements - these are involved in actually setting the booleans in the actions array
		
		public void isEnemy(boolean wantToShoot)
		{
			if(wantToShoot)
			{
				actions[Mario.KEY_SPEED] = true;
			}
		}
	
		/**
		 * Sets the action array to actions that will move Mario towards the provided location.
		 * @param location byte array of size 2 where location[0] is the row and location[1] is the column in the
		 * observation matrix levelScene that Mario is required to move towards.
		 */
		public abstract void moveTowards(int[] location);
		
		/**
		 * Used to move if no decision has been made about where he needs to move to next.
		 */
		protected abstract void defaultMove();

		/**
		 * Used to set the booleans in actions to the correct configuration to move Mario to the right.
		 * Note: when already decided to move left, this method will have no effect.
		 */
		protected void goRight()
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
		protected void goLeft()
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
		protected abstract void jump();
		/**
		 * Used to update Mario's knowledge about how far through a jump at the point when he lands.
		 */
		public abstract void land();
		
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
}
