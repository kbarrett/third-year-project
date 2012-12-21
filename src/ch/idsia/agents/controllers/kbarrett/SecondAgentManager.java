package ch.idsia.agents.controllers.kbarrett;

import java.util.LinkedList;

public class SecondAgentManager
{
	
	private static float[] fitnesses;
	private static int currentAgent = 0;
	
	private static float[] probabilityJump;
	private static float[] probabilityMoveRight;
	private static float[] probabilityCoin;
	private static float[] probabilityAvoidEnemy;
	
	public static void initialise(int size)
	{
		fitnesses = new float[size];
		probabilityJump = new float[size];
		probabilityMoveRight = new float[size];
		probabilityCoin = new float[size];
		probabilityAvoidEnemy = new float[size];
	}
	
	public static void resetNumbers()
	{
		currentAgent = 0;
		fitnesses = new float[getFitnesses().length];
		probabilityJump = new float[probabilityJump.length];
		probabilityMoveRight = new float[probabilityMoveRight.length];
		probabilityCoin = new float[probabilityCoin.length];
		probabilityAvoidEnemy = new float[probabilityAvoidEnemy.length];
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
	public static float getProbabilityCoin(int agentNumber)
	{
		return (int)probabilityCoin[agentNumber];
	}
	public static float getProbabilityAvoidEnemy(int agentNumber)
	{
		return probabilityAvoidEnemy[agentNumber];
	}

	public static float[] getFitnesses() {
		return fitnesses;
	}
	
	public static int getNextAgentNumber()
	{
		return currentAgent++;
	}
	
	public static void setValues(int agentNumber, float jump, float right, float coin, float enemy)
	{
		probabilityJump[agentNumber] = jump;
		probabilityMoveRight[agentNumber] = right;
		probabilityCoin[agentNumber] = coin;
		probabilityAvoidEnemy[agentNumber] = enemy;
	}
	public static void setValues(LinkedList<SecondAgent> population)
	{
		for(SecondAgent agent : population)
		{
			setValues(agent.getAgentNumber(), agent.getProbabilityJump(), agent.getProbabilityMoveRight(), 0, 0);
		}
	}

}
