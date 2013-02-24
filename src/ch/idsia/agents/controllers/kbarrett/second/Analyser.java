package ch.idsia.agents.controllers.kbarrett.second;

import java.util.LinkedList;

import ch.idsia.agents.controllers.kbarrett.third.LoadSave;

public class Analyser {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		LinkedList<SecondAgent> lastGeneration = new LinkedList<SecondAgent>();
		try
		{
			LoadSave.loadFromFile(GeneticAgentGenerator.getSavedLocation(), lastGeneration, new SecondAgentEvolver(args));
			
			float jump = 0;
			float right = 0;
			float run = 0;
			float shoot = 0;
			
			for(SecondAgent sa : lastGeneration)
			{
				jump += sa.getProbabilityJump();
				right += sa.getProbabilityMoveRight();
				run += sa.getProbabilityRun();
				shoot += sa.getProbabilityShoot();

			}
			
			System.out.println("This generation has the following properties: \n " +
					"Average probability of jumping: " + jump/lastGeneration.size() + 
					"\n Average probability of moving right: " + right/lastGeneration.size() + 
					"\n Average probability of running: " + run/lastGeneration.size() + 
					"\n Average probability of shooting: " + shoot/lastGeneration.size());
			
		} 
		catch (Exception e) {
			System.out.println("ERROR");
		}
	}

}
