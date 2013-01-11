package ch.idsia.agents.controllers.kbarrett;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.jdom.Element;
import org.jdom.JDOMException;

import ch.idsia.scenarios.Play;
import ch.idsia.agents.controllers.kbarrett.GeneticAlgorithm;

public class GeneticAgentGenerator
{
	private static final String savefilename = "src/ch/idsia/agents/controllers/kbarrett/savedfile.saf";
	
	public static int currentAgent = 0;
	
	public static void main(final String[] args)
	{
		SecondAgentEvolver evolver = new SecondAgentEvolver(args);
		SecondAgentManager.initialise(evolver.getSizeOfGeneration());
		List<SecondAgent> population = new LinkedList<SecondAgent>();
		
		GeneticAlgorithm<SecondAgent> algorithm = new GeneticAlgorithm<SecondAgent>(evolver);
		
		try
		{
			algorithm.loadThisGeneration(savefilename);
			population = algorithm.getCurrentGeneration();
		}
		catch(Exception e)
		{
			System.out.println("File empty, creating initial population.");
			
			population.add(new SecondAgent(0, 0.6f, 0.6f, 0.6f, 0.6f));
			population.add(new SecondAgent(1, 1f, 0.6f, 0.1f, 0.4f));
			population.add(new SecondAgent(2, 1f, 0.1f, 0.2f, 0.3f));
			population.add(new SecondAgent(3, 0.1f, 1f, 1f, 1f));
			population.add(new SecondAgent(4, 0.5f, 0.5f, 0.5f, 0.5f));
			
			algorithm.giveInitialPopulation(population);
		}
		
		SecondAgentManager.resetNumbers();
		
		for(int i = 0; i < 8; ++i)
		{
			System.out.println("Iteration " + i);
			SecondAgentManager.setValues(population);
			population = algorithm.getNewGeneration();
			SecondAgentManager.resetNumbers();
		}
		
		algorithm.saveThisGeneration(savefilename);

		System.exit(0);
	}

}

class SecondAgentEvolver implements Evolver<SecondAgent>
{
	private String[] args;
	private static final float probability = 0.3f;
	private int currentAgent = 0;
	
	public SecondAgentEvolver(String[] args)
	{
		this.args = args;
	}
	
	@Override
	public int fitnessFunction(SecondAgent element)
	{
		Play.main(args);
		return (int)SecondAgentManager.getFitness(element.getAgentNumber());
	}
	
	@Override
	public List<SecondAgent> crossover(SecondAgent element1, SecondAgent element2)
	{
		System.out.println("Crossing over " + element1 + " & " + element2);
		
		float probJump1 = (element1.getProbabilityJump() * probability + element2.getProbabilityJump() * (1-probability));
		float probJump2 = (element2.getProbabilityJump() * probability + element1.getProbabilityJump() * (1-probability));
		float probRight1 = (element1.getProbabilityMoveRight() * probability + element2.getProbabilityMoveRight() * (1-probability));
		float probRight2 = (element2.getProbabilityMoveRight() * probability + element1.getProbabilityMoveRight() * (1-probability));
		float probRun1 = (element1.getProbabilityRun() * probability + element2.getProbabilityRun() * (1-probability));
		float probRun2 = (element2.getProbabilityRun() * probability + element1.getProbabilityRun() * (1-probability));
		float probShoot1 = (element1.getProbabilityShoot() * probability + element2.getProbabilityShoot() * (1-probability));
		float probShoot2 = (element2.getProbabilityShoot() * probability + element1.getProbabilityShoot() * (1-probability));
		
		SecondAgent newAgent1 = new SecondAgent(currentAgent++, probJump1, probRight1, probShoot1, probRun1);
		SecondAgent newAgent2 = new SecondAgent(currentAgent++, probJump2, probRight2, probShoot2, probRun2);
		
		if(currentAgent == getSizeOfGeneration())
		{
			currentAgent = 0;
		}
		
		ArrayList<SecondAgent> returnValue = new ArrayList<SecondAgent>();
		returnValue.add(newAgent1);
		returnValue.add(newAgent2);
		return returnValue;
	}

	@Override
	public void mutate(SecondAgent element)
	{
		System.out.println("Mutating " + element);
		element.setProbabilityJump(element.getProbabilityJump() + (float)Math.random());
		element.setProbabilityMoveRight(element.getProbabilityMoveRight() + (float)Math.random());
		element.setProbabilityShoot(element.getProbabilityShoot() + (float)Math.random());
		element.setProbabilityRun(element.getProbabilityRun() + (float)Math.random());
	}

	@Override
	public float getProbabilityOfMutation()
	{
		return 0.001f;
	}

	@Override
	public int getSizeOfGeneration()
	{
		return 20;
	}

	@Override
	public Element toSaveFormat(SecondAgent element)
	{
		return element.toSaveFormat();
	}

	@Override
	public SecondAgent fromSaveFormat(Element element)
	{
		return new SecondAgent(element);
	}
	
}
