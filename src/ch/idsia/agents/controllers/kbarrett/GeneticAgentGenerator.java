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
	private static final String savefilename = "src/ch/idsia/agents/controllers/kbarrett/savedfile.txt";
	
	public static int currentAgent = 0;
	
	public static void main(final String[] args)
	{
		SecondAgentEvolver evolver = new SecondAgentEvolver(args);
		SecondAgentManager.initialise(evolver.getSizeOfGeneration());
		LinkedList<SecondAgent> population = new LinkedList<SecondAgent>();
		
		GeneticAlgorithm<SecondAgent> algorithm = new GeneticAlgorithm<SecondAgent>(evolver);
		
		try
		{
			algorithm.loadThisGeneration(savefilename);
			population = algorithm.getCurrentGeneration();
		}
		catch(JDOMException e)
		{
			System.out.println("File empty, creating initial population.");
			
			population.add(new SecondAgent(0,0.6f,0.6f));
			population.add(new SecondAgent(1,1f,0.6f));
			population.add(new SecondAgent(2,1f,0.1f));
			population.add(new SecondAgent(3,0.1f,1f));
			population.add(new SecondAgent(4,0.5f,0.5f));
			
			algorithm.giveInitialPopulation(population);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("Error creating generation");
			return;
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
		
		SecondAgent newAgent1 = new SecondAgent(currentAgent++, probJump1, probRight1);
		SecondAgent newAgent2 = new SecondAgent(currentAgent++, probJump2, probRight2);
		
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
		double rand = Math.random();
		element.setProbabilityJump(element.getProbabilityJump() + (float)rand * 10);
		element.setProbabilityMoveRight(element.getProbabilityMoveRight() + (float)rand * 10);
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
