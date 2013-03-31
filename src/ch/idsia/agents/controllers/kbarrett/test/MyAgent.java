package ch.idsia.agents.controllers.kbarrett.test;

import java.util.ArrayList;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

/**
 * Test {@link Agent} used to explore what it is possible to make an agent do.
 * @author Kim Barrett
 *
 */
public class MyAgent implements Agent {
	
	String name = "Kim's Agent";
	
	ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	
	boolean[] action;
	boolean jump = false;
	boolean jumped = false;
	
	byte justjumped = 0;
	
	int directionlr = -1;
	
	byte COIN = 2;
	
	int[] marioPos = {11, 11};
	byte[][] levelScene;
	boolean isMarioOnGround;
	
	boolean focussed = false;

	@Override
	public boolean[] getAction() {
		
		findStuff();
		
		//if no instructions have been gleaned from environment observations
		if(directionlr==-1 && !jump)
		{
			action[Mario.KEY_RIGHT] = true;
			action[Mario.KEY_JUMP] = jumped;
			jumped = !jumped;
		}
		
		
		if(directionlr!=-1)
		{
			//if a left/right direction has been chosen by our environment observations, obey & reset
			action[directionlr] = true;
			directionlr = -1;
		}
		
		if(jump)
		{
			action[Mario.KEY_JUMP] = jump;
		} 

		
		boolean[] nextSetOfActions = action.clone();
		action = new boolean[Environment.numberOfKeys];
		
		
		printInstructions(nextSetOfActions);
		
		return nextSetOfActions;
		
	}
	
	private void printInstructions(boolean[] actions)
	{
		System.out.println("JUMP: " + actions[Mario.KEY_JUMP]);
		System.out.println("DOWN: " + actions[Mario.KEY_DOWN]);
		System.out.println("LEFT: " + actions[Mario.KEY_LEFT]);
		System.out.println("RIGHT: " + actions[Mario.KEY_RIGHT]);
		System.out.println("SPEED: " + actions[Mario.KEY_SPEED]);
		System.out.println("UP: " + actions[Mario.KEY_UP]);
	}

	@Override
	public void integrateObservation(Environment environment) {
		
		//marioPos = environment.getMarioEgoPos();
		
		levelScene = environment.getLevelSceneObservationZ(1);
		
		isMarioOnGround = environment.isMarioOnGround();
		
	}
		
		/*for(byte[] array : levelScene)
		{
			System.out.print("{");
			for(byte b : array)
			{
				System.out.print(b + ",");
			}
			System.out.println("}");
		}*/
	
	private void findStuff()
	{
				
		boolean breaking = false;
		for(int i = 0; i < levelScene.length; ++i)
		{
			for(int j = 0; j < levelScene[i].length; ++j)
			{
				System.out.print(levelScene[i][j]);
				if(levelScene[i][j]==COIN)
				{
					focussed = true;
					
					System.out.println("MarioPos: " + marioPos[0] + " i:" + i);
					System.out.println("MarioPos: " + marioPos[1] + " j:" + j);
					if(i <= marioPos[0])
					{
						directionlr = Mario.KEY_RIGHT;
					}
					else
					{
						directionlr = Mario.KEY_LEFT;
					}
					
					if(j >= marioPos[1])
					{
						justjumped++;
						jump = true;
						System.out.println("justjumped: " + justjumped + "jump: " + jump);
					}
					else
					{ 
						jump = false;
					}
					break;
				}
			}

			System.out.println("--------------------------------");
			if(breaking)
			{
				break;
			}
		}
		
		if(jump && justjumped>2 && isMarioOnGround)
		{
			System.out.println("RESETTING JUMP BECAUSE JUMP OVER");
			jump = false;
			justjumped = 0;
		}
		
		//Note new locations of enemies
		/*enemies.clear();
		float[] enemiesInfo = environment.getEnemiesFloatPos();
		for(int i = 0; i < enemiesinfo.length - 3; i += 3)
		{
			Enemy enemy = new Enemy((byte)enemiesInfo[i], enemiesInfo[i+1], enemiesInfo[i+2]);
			enemies.add(enemy);
		}
		*/
		//environment.
		//TODO: find out how to find out where coins are
		
		//environment.
		
	}

	@Override
	public void giveIntermediateReward(float intermediateReward) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		action = new boolean[Environment.numberOfKeys];
		/*action[Mario.KEY_RIGHT] = true;
		action[Mario.KEY_JUMP] = jump;*/
	}

	@Override
	public void setObservationDetails(int rfWidth, int rfHeight, int egoRow, int egoCol) {
		System.out.println("OBSERVATION DATA: " + rfWidth +", " + rfHeight + ", " + egoRow + ", " + egoCol);
		// TODO write method
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
}

class Enemy
{
	byte spriteType;
	float[] location = new float[2];
	
	public Enemy(byte type, float xloc, float yloc)
	{
		spriteType = type;
		location[0] = xloc;
		location[1] = yloc;
	}
	
	public float[] getLocation()
	{
		return location;
	}
	public byte getType()
	{
		return spriteType;
	}
	public float getLocationX()
	{
		return location[0];
	}
	public float getLocationY()
	{
		return location[1];
	}
	
}
