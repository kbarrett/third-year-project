package ch.idsia.agents.controllers.kbarrett.second;

import java.util.List;

import ch.idsia.scenarios.Play;
/**
 * Stores the data used by {@link Play#main} when {@link SecondAgentEvolver} is assessing a {@link SecondAgent}.
 * This data is stored in float arrays, where the unique ID of each {@link SecondAgent} indicates which element 
 * of an array relates to that agent.
 * @author Kim Barrett
 */
public class SecondAgentManager
{
	/**
	 * The assessed fitness of each of the agents.
	 */
	private static float[] fitnesses;
	/**
	 * The unique ID of the agent currently being assessed.
	 * @see SecondAgent#getAgentNumber()
	 */
	private static int currentAgent = 0;
	/**
	 * The probability of jumping for each of the agents.
	 */
	private static float[] probabilityJump;
	/**
	 * The probability of moving right for each of the agents.
	 */
	private static float[] probabilityMoveRight;
	/**
	 * The probability of shooting for each of the agents.
	 */
	private static float[] probabilityShoot;
	/**
	 * The probability of running for each of the agents.
	 */
	private static float[] probabilityRun;
	/**
	 * Ensures each of the arrays has the desired size.
	 * @param size - the number of agents in each generation.
	 */
	public static void initialise(int size)
	{
		currentAgent = 0;
		fitnesses = new float[size];
		probabilityJump = new float[size];
		probabilityMoveRight = new float[size];
		probabilityShoot = new float[size];
		probabilityRun = new float[size];
	}
	/**
	 * Used at the end of every generation to "zero" the data.
	 * Empties the arrays and sets the number of the agent currently being assessed to 0.
	 */
	public static void resetNumbers()
	{
		currentAgent = 0;
		fitnesses = new float[fitnesses.length];
		probabilityJump = new float[probabilityJump.length];
		probabilityMoveRight = new float[probabilityMoveRight.length];
		probabilityShoot = new float[probabilityShoot.length];
		probabilityRun = new float[probabilityRun.length];
	}
	/**
	 * @param agentNumber - the agent's whose data is required
	 * @return the fitness of the agent
	 */
	public static float getFitness(int agentNumber)
	{
		return fitnesses[agentNumber];
	}
	/**
	 * @param agentNumber - the agent's whose data is required
	 * @return the probability of the agent jumping
	 */
	public static float getProbabilityJump(int agentNumber)
	{
		return probabilityJump[agentNumber];
	}
	/**
	 * @param agentNumber - the agent's whose data is required
	 * @return the probability of the agent moving right
	 */
	public static float getProbabilityMoveRight(int agentNumber)
	{
		return probabilityMoveRight[agentNumber];
	}
	/**
	 * @param agentNumber - the agent's whose data is required
	 * @return the probability of the agent running
	 */
	public static float getProbabilityRun(int agentNumber)
	{
		return probabilityRun[agentNumber];
	}
	/**
	 * @param agentNumber - the agent's whose data is required
	 * @return the probability of the agent shooting
	 */
	public static float getProbabilityShoot(int agentNumber)
	{
		return probabilityShoot[agentNumber];
	}
	/**
	 * Adds the given increment to the fitness for the given agent.
	 * @param agentNumber - the number of the agent whose fitness is to be incremented.
	 * @param fitnessIncrement - the increment to be added to the fitness value.
	 */
	public static void incrementFitnesses(int agentNumber, float fitnessIncrement) {
		fitnesses[agentNumber] += fitnessIncrement;
	}
	/**
	 * Gets the number of the next agent. Note: this method also increments the number, 
	 * so multiple calls to this method will return increasing values.
	 * @return the unique ID of the current agent
	 */
	public static int getNextAgentNumber()
	{
		return currentAgent++;
	}
	/**
	 * Sets the values for a given agent.
	 * @param agentNumber - the number of the agent for whom this data relates.
	 * @param jump - the jump probability for this agent
	 * @param right - the move right probability for this agent
	 * @param run - the run probability for this agent
	 * @param shoot - the shoot probability for this agent
	 */
	public static void setValues(int agentNumber, float jump, float right, float run, float shoot)
	{
		probabilityJump[agentNumber] = jump;
		probabilityMoveRight[agentNumber] = right;
		probabilityRun[agentNumber] = run;
		probabilityShoot[agentNumber] = shoot;
	}
	/**
	 * Set the values for a list of SecondAgents.
	 * Uses {@link #setValues(int, float, float, float, float)} for each element in the list.
	 * @param population - the list of SecondAgents to use.
	 */
	public static void setValues(List<SecondAgent> population)
	{
		for(SecondAgent agent : population)
		{
			setValues(agent.getAgentNumber(), agent.getProbabilityJump(), agent.getProbabilityMoveRight(), agent.getProbabilityRun(), agent.getProbabilityShoot());
		}
	}

}
