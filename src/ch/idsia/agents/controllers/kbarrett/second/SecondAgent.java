package ch.idsia.agents.controllers.kbarrett.second;

import org.jdom.Element;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

public class SecondAgent implements Agent
{
	private static String name = "SecondAgent";
	private int thisAgent;
	
	private float probabilityJump;
	private float probabilityMoveRight;
	private float probabilityShoot;
	private float probabilityRun;
	
	private boolean marioAbleToShoot = false;
	
	public SecondAgent()
	{	
		thisAgent = SecondAgentManager.getNextAgentNumber();
		probabilityJump = SecondAgentManager.getProbabilityJump(thisAgent);
		probabilityMoveRight = SecondAgentManager.getProbabilityMoveRight(thisAgent);
		probabilityShoot = SecondAgentManager.getProbabilityShoot(thisAgent);
		probabilityRun = SecondAgentManager.getProbabilityRun(thisAgent);
	}
	public SecondAgent(int agentNumber, float probJump, float probRight, float probShoot, float probRun)
	{	
		thisAgent = agentNumber;
		probabilityJump = probJump;
		probabilityMoveRight = probRight;
		probabilityShoot = probShoot;
		probabilityRun = probRun;
	}
	
	public SecondAgent(Element string)
	{
		fromSaveFormat(string);
	}
	
	public int getAgentNumber()
	{
		return thisAgent;
	}
	
	@Override
	public boolean[] getAction()
	{
		boolean[] actions = new boolean[Environment.numberOfKeys];
		
		if(Math.random() < probabilityJump)
		{
			actions[Environment.MARIO_KEY_JUMP] = true;
		}
		if(Math.random() < probabilityMoveRight)
		{
			actions[Environment.MARIO_KEY_RIGHT] = true;
		}
		if(marioAbleToShoot)
		{
			if(Math.random() < probabilityShoot)
			{
				actions[Environment.MARIO_KEY_SPEED] = true;
			}
		}
		else if(Math.random() < probabilityRun)
		{
			actions[Environment.MARIO_KEY_SPEED] = true;
		}
		
		return actions;
	}

	@Override
	public void integrateObservation(Environment environment)
	{
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
		SecondAgentManager.getFitnesses()[getAgentNumber()] += intermediateReward;
	}

	@Override
	public void reset(){}

	@Override
	public void setObservationDetails(int rfWidth, int rfHeight, int egoRow, int egoCol) {}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
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
	
	public Element toSaveFormat()
	{
		Element element = new Element("SecondAgent");
		element.addContent(new Element("probabilityJump").setText(""+probabilityJump));
		element.addContent(new Element("probabilityMoveRight").setText(""+probabilityMoveRight));
		element.addContent(new Element("probabilityShoot").setText(""+probabilityShoot));
		element.addContent(new Element("probabilityRun").setText(""+probabilityRun));
		return element;
	}
	
	public void fromSaveFormat(Element savedFormat)
	{
		probabilityJump = Float.parseFloat(savedFormat.getChildText("probabilityJump"));
		probabilityMoveRight = Float.parseFloat(savedFormat.getChildText("probabilityMoveRight"));
		probabilityRun = Float.parseFloat(savedFormat.getChildText("probabilityRun"));
		probabilityShoot = Float.parseFloat(savedFormat.getChildText("probabilityShoot"));
		thisAgent = SecondAgentManager.getNextAgentNumber();
	}
	
	@Override
	public String toString()
	{
		return "jump: " + probabilityJump + " right: " + probabilityMoveRight + " run: " + probabilityRun + " shoot: " + probabilityShoot;
	}

}
