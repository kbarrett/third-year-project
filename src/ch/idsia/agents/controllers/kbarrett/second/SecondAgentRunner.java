package ch.idsia.agents.controllers.kbarrett.second;

import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;

import ch.idsia.agents.controllers.kbarrett.GeneticAlgorithm;
import ch.idsia.scenarios.Play;

public class SecondAgentRunner
{
	private static final String savefilename = "src/ch/idsia/agents/controllers/kbarrett/savedfile.saf";
	
	public static void main(String[] args) throws JDOMException, IOException
	{
		SecondAgentEvolver evolver = new SecondAgentEvolver(args);
		GeneticAlgorithm<SecondAgent> algorithm = new GeneticAlgorithm<SecondAgent>(evolver);
		algorithm.loadThisGeneration(savefilename);
		List<SecondAgent> population = algorithm.getCurrentGeneration();
		
		Analyser.main(args);
		
		SecondAgentManager.initialise(evolver.getSizeOfGeneration());
		SecondAgentManager.setValues(population);
		
		int chosen = (int)(evolver.getSizeOfGeneration() * Math.random());
		
		for(int i = 0; i < chosen; ++i)
		{
			SecondAgentManager.getNextAgentNumber();
		}
		
		System.out.println("jump: " + SecondAgentManager.getProbabilityJump(chosen));
		System.out.println("right: " + SecondAgentManager.getProbabilityMoveRight(chosen));
		System.out.println("shoot: " + SecondAgentManager.getProbabilityShoot(chosen));
		System.out.println("run: " + SecondAgentManager.getProbabilityRun(chosen));
		
		Play.main(args);
	}

}
