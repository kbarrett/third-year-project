package ch.idsia.agents.controllers.kbarrett;

import java.util.LinkedList;
import java.util.List;

public class SecondAgentManager
{
	
	private static float[] fitnesses;
	private static int currentAgent = 0;
	
	private static float[] probabilityJump;
	private static float[] probabilityMoveRight;
	private static float[] probabilityShoot;
	private static float[] probabilityRun;
	
	public static void initialise(int size)
	{
		fitnesses = new float[size];
		probabilityJump = new float[size];
		probabilityMoveRight = new float[size];
		probabilityShoot = new float[size];
		probabilityRun = new float[size];
	}
	
	public static void resetNumbers()
	{
		currentAgent = 0;
		fitnesses = new float[getFitnesses().length];
		probabilityJump = new float[probabilityJump.length];
		probabilityMoveRight = new float[probabilityMoveRight.length];
		probabilityShoot = new float[probabilityShoot.length];
		probabilityRun = new float[probabilityRun.length];
	}
	
	public static float getFitness(int agentNumber)
	{
		return fitnesses[agentNumber];
	}
	public static float getProbabilityJump(int agentNumber)
	{
		return probabilityJump[agentNumber];
	}
	public static float getProbabilityMoveRight(int agentNumber)
	{
		return probabilityMoveRight[agentNumber];
	}
	public static float getProbabilityRun(int agentNumber)
	{
		return (int)probabilityRun[agentNumber];
	}
	public static float getProbabilityShoot(int agentNumber)
	{
		return probabilityShoot[agentNumber];
	}

	public static float[] getFitnesses() {
		return fitnesses;
	}
	
	public static int getNextAgentNumber()
	{
		return currentAgent++;
	}
	
	public static void setValues(int agentNumber, float jump, float right, float run, float shoot)
	{
		probabilityJump[agentNumber] = jump;
		probabilityMoveRight[agentNumber] = right;
		probabilityRun[agentNumber] = run;
		probabilityShoot[agentNumber] = shoot;
	}
	public static void setValues(List<SecondAgent> population)
	{
		for(SecondAgent agent : population)
		{
			setValues(agent.getAgentNumber(), agent.getProbabilityJump(), agent.getProbabilityMoveRight(), agent.getProbabilityRun(), agent.getProbabilityShoot());
		}
	}

}
