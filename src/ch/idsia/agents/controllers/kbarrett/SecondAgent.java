package ch.idsia.agents.controllers.kbarrett;

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
	
	public SecondAgent()
	{	
		thisAgent = SecondAgentManager.getNextAgentNumber();
		probabilityJump = SecondAgentManager.getProbabilityJump(thisAgent);
		probabilityMoveRight = SecondAgentManager.getProbabilityMoveRight(thisAgent);
	}
	public SecondAgent(int agentNumber, float probJump, float probRight)
	{	
		thisAgent = agentNumber;
		probabilityJump = probJump;
		probabilityMoveRight = probRight;
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
			actions[Mario.KEY_JUMP] = true;
		}
		if(Math.random() < probabilityMoveRight)
		{
			actions[Mario.KEY_RIGHT] = true;
		}
		
		return actions;
	}

	@Override
	public void integrateObservation(Environment environment){}

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
	
	public Element toSaveFormat()
	{
		Element element = new Element("SecondAgent");
		element.addContent(new Element("probabilityJump").setText(""+probabilityJump));
		element.addContent(new Element("probabilityMoveRight").setText(""+probabilityMoveRight));
		element.addContent(new Element("probabilityCoin").setText(""));
		element.addContent(new Element("probabilityAvoidEnemy").setText(""));
		return element;
	}
	
	public void fromSaveFormat(Element savedFormat)
	{
		probabilityJump = Float.parseFloat(savedFormat.getChildText("probabilityJump"));
		probabilityMoveRight = Float.parseFloat(savedFormat.getChildText("probabilityMoveRight"));
		thisAgent = SecondAgentManager.getNextAgentNumber();
	}
	
	@Override
	public String toString()
	{
		return "jump: " + probabilityJump + " right: " + probabilityMoveRight;
	}

}
