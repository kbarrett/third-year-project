package ch.idsia.agents.controllers.kbarrett.second;

import org.jdom.Element;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.environments.Environment;

public class SecondAgent implements Agent
{
	/**
	 * The name of this Agent.
	 */
	private static String name = "SecondAgent";
	/**
	 * The unique (in each generation) ID number given to this instance.
	 */
	private int thisAgent;
	/**
	 * The probability that Mario will jump in each frame.
	 */
	private float probabilityJump;
	/**
	 * The probability that Mario will move right in each frame.
	 */
	private float probabilityMoveRight;
	/**
	 * The probability that Mario will shoot in each frame.
	 */
	private float probabilityShoot;
	/**
	 * The probability that Mario will run in each frame.
	 */
	private float probabilityRun;
	/**
	 * Whether Mario is currently able to shoot - this is used to decide whether to use 
	 * {@link #probabilityRun} or {@link #probabilityShoot} when choosing an action.
	 */
	private boolean marioAbleToShoot = false;
	/**
	 * Create a SecondAgent based off knowledge stored in {@link SecondAgentManager}.
	 */
	public SecondAgent()
	{	
		//Get the data from SecondAgentManager and store it locally.
		thisAgent = SecondAgentManager.getNextAgentNumber();
		probabilityJump = SecondAgentManager.getProbabilityJump(thisAgent);
		probabilityMoveRight = SecondAgentManager.getProbabilityMoveRight(thisAgent);
		probabilityShoot = SecondAgentManager.getProbabilityShoot(thisAgent);
		probabilityRun = SecondAgentManager.getProbabilityRun(thisAgent);
	}
	/**
	 * Creates a SecondAgent using the given values.
	 * @param agentNumber - sets {@link #thisAgent}
	 * @param probJump - sets {@link #probabilityJump}
	 * @param probRight - sets {@link #probabilityMoveRight}
	 * @param probShoot - sets {@link #probabilityShoot}
	 * @param probRun - sets {@link #probabilityRun}
	 */
	public SecondAgent(int agentNumber, float probJump, float probRight, float probShoot, float probRun)
	{	
		thisAgent = agentNumber;
		probabilityJump = probJump;
		probabilityMoveRight = probRight;
		probabilityShoot = probShoot;
		probabilityRun = probRun;
	}
	/**
	 * Create a SecondAgent object from a given xml representation.
	 * @param string - xml representation of the required SecondAgent
	 */
	public SecondAgent(Element string)
	{
		fromSaveFormat(string);
	}
	/**
	 * @return the unique ID for this agent ({@link #thisAgent}).
	 */
	public int getAgentNumber()
	{
		return thisAgent;
	}
	/**
	 * Used to return the required actions to the game.
	 * @return boolean array of size 6. Each element in the array represents a 
	 * movement: true indicates it is being requested during this frame & false 
	 * means it is not. 
	 * From 0 to 5 the corresponding movements are: left, right, down, jump, speed, up.
	 */
	@Override
	public boolean[] getAction()
	{
		boolean[] actions = new boolean[Environment.numberOfKeys];
		
		//Chooses whether to jump with probability of probabilityJump.
		if(Math.random() < probabilityJump)
		{
			actions[Environment.MARIO_KEY_JUMP] = true;
		}
		//Chooses whether to move right with probability of probabilityMoveRight.
		if(Math.random() < probabilityMoveRight)
		{
			actions[Environment.MARIO_KEY_RIGHT] = true;
		}
		//If Mario can shoot, then he can't run
		if(marioAbleToShoot)
		{
			//Chooses whether to shoot with probability of probabilityShoot.
			if(Math.random() < probabilityShoot)
			{
				actions[Environment.MARIO_KEY_SPEED] = true;
			}
		}
		//If Mario can't shoot, then he can run
		else 
			//Chooses whether to run with probability of probabilityRun.
			if(Math.random() < probabilityRun)
		{
			actions[Environment.MARIO_KEY_SPEED] = true;
		}
		
		return actions;
	}
	/**
	 * Used to update the Agent with information about the environment.
	 * This gets called by the game every frame & passes the updated Environment
	 * to the Agent to allow it to update its knowledge of the world (such as
	 * where remaining enemies, coins and blocks are).
	 * @param environment containing updated knowledge
	 */
	@Override
	public void integrateObservation(Environment environment)
	{
		//Update knowledge on whether Mario can shoot.
		marioAbleToShoot = environment.isMarioAbleToShoot();
	}
	/**
	 * Used to tell Mario when he has done a good move & when he hasn't.
	 * Examples:
	 * Increases if coins, mushrooms or hidden blocks are collected; or if 
	 * enemies are stomped on. Decreases if you collide with an enemy.
	 * @param intermediateReward gives the new value of the reward
	 */
	@Override
	public void giveIntermediateReward(float intermediateReward)
	{
		/*
		 * This data cannot be passed out of the Agent directly, so the information
		 * is given to SecondAgentManager. This is done each frame instead of at the
		 * end of a level because the end of the level cannot be detected from within
		 * the agent (i.e. no method is called with that information).
		 */
		SecondAgentManager.incrementFitnesses(getAgentNumber(), intermediateReward);
	}
	//These methods aren't necessary for this agent.
		@Override
		public void reset(){}
		@Override
		public void setObservationDetails(int rfWidth, int rfHeight, int egoRow, int egoCol) {}
	/**
	 * @return {@value #name}.
	 */
	@Override
	public String getName() {
		return name;
	}
	/**
	 * Updates this Agent's name.
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	//Getter and setter methods for the probabilities. Used by {@link SecondAgentEvolver#mutate(SecondAgent)} and {@link Analyser}.
		public float getProbabilityJump()
		{
			return probabilityJump;
		}
		public void setProbabilityJump(float probJump)
		{
			probabilityJump = probJump;
		}
		public float getProbabilityMoveRight()
		{
			return probabilityMoveRight;
		}
		public void setProbabilityMoveRight(float probRight)
		{
			probabilityMoveRight = probRight;
		}
		public float getProbabilityRun()
		{
			return probabilityRun;
		}
		public void setProbabilityRun(float probRun)
		{
			probabilityRun = probRun;
		}
		public float getProbabilityShoot()
		{
			return probabilityShoot;
		}
		public void setProbabilityShoot(float probShoot)
		{
			probabilityShoot = probShoot;
		}
	/**
	 * Creates an xml representation of this object.
	 * @return Element representing this instance.
	 */
	public Element toSaveFormat()
	{
		Element element = new Element("SecondAgent");
		element.addContent(new Element("probabilityJump").setText(""+probabilityJump));
		element.addContent(new Element("probabilityMoveRight").setText(""+probabilityMoveRight));
		element.addContent(new Element("probabilityShoot").setText(""+probabilityShoot));
		element.addContent(new Element("probabilityRun").setText(""+probabilityRun));
		return element;
	}
	/**
	 * Sets the probabilities and {@link #thisAgent} using the data held in the Element.
	 * @param savedFormat - xml representation of data to be used.
	 */
	public void fromSaveFormat(Element savedFormat)
	{
		probabilityJump = Float.parseFloat(savedFormat.getChildText("probabilityJump"));
		probabilityMoveRight = Float.parseFloat(savedFormat.getChildText("probabilityMoveRight"));
		probabilityRun = Float.parseFloat(savedFormat.getChildText("probabilityRun"));
		probabilityShoot = Float.parseFloat(savedFormat.getChildText("probabilityShoot"));
		thisAgent = SecondAgentManager.getNextAgentNumber();
	}
	/**
	 * Returns String representation of this object.
	 */
	@Override
	public String toString()
	{
		return "jump: " + probabilityJump + " right: " + probabilityMoveRight + " run: " + probabilityRun + " shoot: " + probabilityShoot;
	}

}
