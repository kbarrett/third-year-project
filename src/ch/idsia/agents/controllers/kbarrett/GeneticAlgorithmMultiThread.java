package ch.idsia.agents.controllers.kbarrett;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import ch.idsia.agents.controllers.kbarrett.Fitness;

public class GeneticAlgorithmMultiThread<T> {
	
	Evolver<T> evolver;
	LinkedList<T> lastGeneration;
	LinkedList<T> thisGeneration;
	
	private static final int addition = 3000;
	
	public GeneticAlgorithmMultiThread(LinkedList<T> initialPopulation, Evolver<T> evolver)
	{
		this.evolver = evolver;
		
		lastGeneration = initialPopulation;
		thisGeneration = new LinkedList<T>();
	}
	
	public LinkedList<T> getNewGeneration()
	{
		int sum = 0;
		LinkedList<Fitness<T>> fitness = new LinkedList<Fitness<T>>();
		LinkedList<Thread> threads = new LinkedList<Thread>();
		for(final T element : lastGeneration)
		{
			Thread thread = new Thread(new Runnable(){

				@Override
				public void run() { 
					evolver.fitnessFunction(element);
				}
			});
			
			threads.add(thread);
			
			thread.start();
		}
		
		for(Thread thread : threads)
		{	
			try
			{
				thread.join();
				int elementFitness = (int)SecondAgentManager.getFitness(threads.indexOf(thread));
				
				fitness.add(new Fitness<T>(lastGeneration.get(threads.indexOf(thread)), elementFitness));
				sum += elementFitness + addition;
				
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		Collections.sort(fitness);
		//collection should now be in order from greatest fitness to smallest fitness
		
		for(int i = 0; i<evolver.getSizeOfGeneration(); ++i)
		{
			int choice = (int) Math.floor(sum * Math.random());
			int choice2 = (int) Math.floor(sum * Math.random());
			
			T element1 = chooseElement(fitness, choice);
			T element2 = chooseElement(fitness, choice2);
			
			thisGeneration.addAll(evolver.crossover(element1, element2));
		}
		
		for(T element : thisGeneration)
		{
			if(Math.random() < evolver.getProbabilityOfMutation())
			{
				evolver.mutate(element);
			}
		}
		
		lastGeneration = (LinkedList<T>) thisGeneration.clone();
		thisGeneration.clear();
		return lastGeneration;
	}
	
	private T chooseElement(LinkedList<Fitness<T>> fitnessList, int randomChoice)
	{
		Iterator<Fitness<T>> iterator = fitnessList.iterator();
		while(iterator.hasNext())
		{
			Fitness<T> current = iterator.next();
			if(randomChoice < (current.fitness + addition))
			{
				return current.getObject();
			}
			randomChoice -= current.fitness + addition;
		}
		
		return null;
	}
	
	public boolean saveThisGeneration(String fileName)
	{
		return false;
	}
	public void loadThisGeneration(String fileName)
	{
		
	}

}
