package ch.idsia.agents.controllers.kbarrett;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import ch.idsia.agents.controllers.kbarrett.third.LoadSave;


public class GeneticAlgorithm<T> {
	
	private Evolver<T> evolver;
	private List<T> lastGeneration;
	private LinkedList<T> thisGeneration;
	
	private int leastValueOffSet = 1;
	
	//Multi-threading Stuff
		final static int numberOfThreads = 4;

	
	private GeneticAlgorithm(){};
	
	public GeneticAlgorithm(List<T> initialPopulation, Evolver<T> evolver)
	{
		this.evolver = evolver;
		giveInitialPopulation(initialPopulation);
		thisGeneration = new LinkedList<T>();
	}
	public GeneticAlgorithm(Evolver<T> evolver)
	{
		this.evolver = evolver;
		
		lastGeneration = new LinkedList<T>();
		thisGeneration = new LinkedList<T>();
	}
	
	public void giveInitialPopulation(List<T> initialPopulation)
	{
		lastGeneration = initialPopulation;
	}
	
	public Evolver<T> getEvolver()
	{
		return evolver;
	}
	
	public List<T> getCurrentGeneration()
	{
		return lastGeneration;
	}
	
	public List<T> getNewGeneration(boolean multiThread)
	{
		if(multiThread)
		{
			return getNewGenerationMulti();
		}
		else
		{
			return getNewGenerationSingle();
		}
	}
	
	private List<T> getNewGenerationSingle()
	{
		int sum = 0;
		LinkedList<Fitness<T>> fitness = new LinkedList<Fitness<T>>();
		for(final T element : lastGeneration)
		{
			int elementFitness = (int) Math.max(leastValueOffSet, evolver.fitnessFunction(element));
			fitness.add(new Fitness<T>(element, elementFitness));
			sum += elementFitness;
		}

		Collections.sort(fitness);
		//collection should now be in order from greatest fitness to smallest fitness

		int leastValue = fitness.getLast().fitness;
		if(leastValue <= 0)
		{
			for(Fitness<T> element : fitness)
			{
				element.fitness += leastValueOffSet - leastValue;
			}

			sum -= (fitness.size() * (leastValue - leastValueOffSet));
		}

		for(int i = 0; i < Math.floor((float)evolver.getSizeOfGeneration() / 2.0); ++i)
		{
			int choice = (int) Math.floor(sum * Math.random());
			int choice2;
			do
			{
				choice2 = (int) Math.floor(sum * Math.random());
			}
			while(choice == choice2 && sum > 1);

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
	
	private List<T> getNewGenerationMulti()
	{
		Object sumlock = new Object();
		int[] sumarray = {0};
		
		LinkedList<Fitness<T>> fitness = new LinkedList<Fitness<T>>();
		
		int offset = lastGeneration.size() / numberOfThreads;

		ArrayList<FitnessThread> threads = new ArrayList<FitnessThread>(numberOfThreads);
		
		for(int threadId = 0; threadId < numberOfThreads; ++threadId)
		{
			FitnessThread t = new FitnessThread(lastGeneration.listIterator(threadId * offset), offset, 
						fitness, sumlock, sumarray);
			t.start();
			threads.add(t);
		}

		for(int cur = 0; cur < threads.size(); ++cur)
		{
			FitnessThread t = threads.get(cur);
			try 
			{
				t.join();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		
		long llTime = System.currentTimeMillis();
		/*for(final T element : lastGeneration)
		{
			
		}*/
		long llNewtime = System.currentTimeMillis();
		//System.out.println("Inital loop: " + (llNewtime - llTime));
		llTime = llNewtime;
		int sum = sumarray[0];
		
		Collections.sort(fitness);
		
		llNewtime = System.currentTimeMillis();
		//System.out.println("Sort: " + (llNewtime - llTime));
		llTime = llNewtime;
		//collection should now be in order from greatest fitness to smallest fitness
		
		int leastValue = fitness.getLast().fitness;
		if(leastValue <= 0)
		{
			for(Fitness<T> element : fitness)
			{
				element.fitness += leastValueOffSet - leastValue;
			}
			
			sum -= (fitness.size() * (leastValue - leastValueOffSet));
		}
		
		llNewtime = System.currentTimeMillis();
		//System.out.println("Secondary loop: " + (llNewtime - llTime));
		llTime = llNewtime;
		
		LinkedList<CrossOverThread> threadList = new LinkedList<GeneticAlgorithm<T>.CrossOverThread>();
		Object listLock = new Object();
		int size = (int) Math.floor((float)evolver.getSizeOfGeneration() / (numberOfThreads * 2.0));
		for(int i = 0; i < numberOfThreads; ++i)
		{
			if(i == numberOfThreads - 1)
			{
				size = (int) ((evolver.getSizeOfGeneration() / 2.0) - (numberOfThreads - 1) * size);
			}
			CrossOverThread cot = new CrossOverThread(sum, listLock, fitness, size);
			cot.start();
			threadList.add(cot);
		}
		for(CrossOverThread cot : threadList)
		{
			try {
				cot.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		llNewtime = System.currentTimeMillis();
		//System.out.println("Tertiay loop: " + (llNewtime - llTime));
		llTime = llNewtime;
		
		for(T element : thisGeneration)
		{
			if(Math.random() < evolver.getProbabilityOfMutation())
			{
				evolver.mutate(element);
			}
		}
		
		llNewtime = System.currentTimeMillis();
		//System.out.println("Fianl loop: " + (llNewtime - llTime));
		llTime = llNewtime;
		
		lastGeneration = (LinkedList<T>) thisGeneration.clone();
		thisGeneration.clear();
		
		llNewtime = System.currentTimeMillis();
		//System.out.println("Clone: " + (llNewtime - llTime));
		llTime = llNewtime;
		
		return lastGeneration;
	}
	class FitnessThread extends Thread
	{
		LinkedList<Fitness<T>> fitness;
		Object sumLock;
		int[] sum;
		//private Iterator<T> endPoint;
		int count;
		private Iterator<T> startingPoint;
		
		public FitnessThread(Iterator<T> startingPoint, int count, LinkedList<Fitness<T>> fitness, Object sumLock, int[] sum)
		{
			this.startingPoint = startingPoint;
			this.count = count;
			this.fitness = fitness;
			this.sumLock = sumLock;
			this.sum = sum;
			
			//System.out.println("Going from: " + startingPoint.)
		}
		public void UpdateData(Iterator<T> startingPoint, int count)
		{
			this.startingPoint = startingPoint;
			this.count = count;
		}
		
		@Override
		public void run() 
		{
			int  i = 0;
			for(Iterator<T> current = startingPoint; current.hasNext() && i < count; )
			{
				T element = current.next();
				int elementFitness = (int) Math.max(leastValueOffSet, evolver.fitnessFunction(element));
				synchronized(sumLock)
				{
					fitness.add(new Fitness<T>(element, elementFitness));
					sum[0] += elementFitness;
				}
				++i;
			}
		}
		
		
	}
	
	class CrossOverThread extends Thread
	{
		int sum;
		Object listLock;
		LinkedList<Fitness<T>> fitness;
		int size;
		
		public CrossOverThread(int sum, Object listLock, LinkedList<Fitness<T>> fitness, int size)
		{
			this.sum = sum;
			this.listLock = listLock;
			this.fitness = fitness;
			this.size = size;
		}
		
		@Override
		public void run()
		{
			ArrayList<T> tempList = new ArrayList<T>(size * 2);
			for(int i = 0; i< size; ++i)
			{	
				int choice = (int) Math.floor(sum * Math.random());
				int choice2;
				do
				{
					choice2 = (int) Math.floor(sum * Math.random());
				}
				while(choice == choice2 && sum > 1);
				
				T element1 = chooseElement(fitness, choice);
				T element2 = chooseElement(fitness, choice2);
				
				tempList.addAll(evolver.crossover(element1, element2));
			}
			synchronized(listLock)
			{
				thisGeneration.addAll(tempList);
			}
		}
	}
	private T chooseElement(LinkedList<Fitness<T>> fitnessList, int randomChoice)
	{
		Iterator<Fitness<T>> iterator = fitnessList.iterator();
		while(iterator.hasNext())
		{
			Fitness<T> current = iterator.next();
			if(randomChoice < current.fitness)
			{
				return current.getObject();
			}
			randomChoice -= current.fitness;
		}
		System.err.println("FUCK");
		return null;
	}
	
	public boolean saveThisGeneration(String fileName)
	{
		try
		{
			LoadSave.saveToFile(fileName, lastGeneration, evolver);
		} catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void loadThisGeneration(String fileName) throws JDOMException, IOException
	{
		LoadSave.loadFromFile(fileName, lastGeneration, evolver);
	}

}

class Fitness<T> implements Comparable<Fitness<T>>
{
	T object;
	int fitness;
	public Fitness(T object, int fitness)
	{
		this.object = object;
		this.fitness = fitness;
	}
	public int getFitness()
	{
		return fitness;
	}
	public T getObject()
	{
		return object;
	}
	@Override
	public int compareTo(Fitness<T> otherObject)
	{
		return new Integer(fitness).compareTo(otherObject.getFitness());
		//return Integer.compare(otherObject.getFitness(), fitness);
	}
	@Override 
	public String toString()
	{
		return object.toString() + " : " + fitness;
	}
}
