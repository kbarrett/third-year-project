package ch.idsia.agents.controllers.kbarrett;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jdom.JDOMException;

/**
 * Used to run a genetic algorithm on a given population.
 * @author Kim
 *
 * @param <T> the type of the population.
 */

public class GeneticAlgorithm<T> {
	
	/**
	 * The Evolver used by this instance of the genetic algorithm.
	 */
	private Evolver<T> evolver;
	/**
	 * The previous generation created by this instance of the genetic algorithm.
	 */
	private List<T> lastGeneration;
	/**
	 * The generation in creation during a call to {@link #getNewGeneration(boolean)}.
	 */
	private LinkedList<T> thisGeneration;
	/**
	 * The value given to the smallest {@link Fitness} if they are all negative, after shifting the values to positive.
	 */
	private int leastValueOffSet = 1;
	
	//Multi-threading Values
		/**
		 * The number of threads to be used when multi-threading.
		 */
		final static int numberOfThreads = 4;

	//To prevent access to this constructor.
	private GeneticAlgorithm(){};
	
	/**
	 * Used to get new generations based off this initial population and methods defined in the Evolver.
	 * @param initialPopulation - population to start the genetic algorithm with.
	 * @param evolver - defines the methods used by the genetic algorithm.
	 */
	public GeneticAlgorithm(List<T> initialPopulation, Evolver<T> evolver)
	{
		this.evolver = evolver;
		giveInitialPopulation(initialPopulation);
		thisGeneration = new LinkedList<T>();
	}
	/**
	 * Used to get new generations based off this evolver.
	 * Note: an initial population must be given using this method: {@link #giveInitialPopulation(List)}.
	 * @param evolver - define the methods used by the genetic algorithm.
	 */
	public GeneticAlgorithm(Evolver<T> evolver)
	{
		this.evolver = evolver;
		
		lastGeneration = new LinkedList<T>();
		thisGeneration = new LinkedList<T>();
	}
	/**
	 * Provides an initial population to the algorithm.
	 * Note: Overwrites all stored generations.
	 * @param initialPopulation - the population to base the genetic algorithm from.
	 */
	public void giveInitialPopulation(List<T> initialPopulation)
	{
		lastGeneration = initialPopulation;
	}
	/**
	 * @return Evolver used to run this genetic algorithm.
	 */
	public Evolver<T> getEvolver()
	{
		return evolver;
	}
	/**
	 * Gets the generation that the new call to {@link #getNewGeneration(boolean)} will evolve.
	 * @return List<T> containing the current population.
	 */
	public List<T> getCurrentGeneration()
	{
		return lastGeneration;
	}
	/**
	 * Runs one iteration of the genetic algorithm.
	 * @param multiThread - whether or not to multi-thread the creation of this generation. 
	 * Note: larger populations benefit from multi-threading whereas smaller ones are slowed down by creating additional threads.
	 * @return List<T> containing the newly created generation.
	 */
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
	/**
	 * Gets a new generation using a sequential (i.e. single thread) method.
	 * @return List<T> containing the newly created generation.
	 */
	private List<T> getNewGenerationSingle()
	{
		//Used to contain the sum of all the fitnesses calculated.
		int sum = 0;
		//Used to contain each element with its associated fitness.
		LinkedList<Fitness<T>> fitness = new LinkedList<Fitness<T>>();
		//Find the fitness of each member of the previous generation.
		for(final T element : lastGeneration)
		{
			int elementFitness = evolver.fitnessFunction(element);
			fitness.add(new Fitness<T>(element, elementFitness));
			sum += elementFitness;
		}

		Collections.sort(fitness);
		//Collection should now be in order from greatest fitness to smallest fitness
		
		//The fitness value of the object with the least fitness.
		int leastValue = fitness.getLast().getFitness();
		//If any fitnesses are negative, shift all of them so they have positive fitnesses.
		if(leastValue <= 0)
		{
			for(Fitness<T> element : fitness)
			{
				element.incrementFitness(leastValueOffSet - leastValue);
			}
			//Adjust sum, so it still contains the value equal to the sum of all the element.fitness.
			sum -= (fitness.size() * (leastValue - leastValueOffSet));
		}
		
		//Create a new generation.
		for(int i = 0; i < Math.floor((float)evolver.getSizeOfGeneration() / 2.0); ++i)
		{
			int choice = (int) Math.floor(sum * Math.random());
			int choice2;
			do
			{
				choice2 = (int) Math.floor(sum * Math.random());
			}
			while(choice == choice2 && sum > 1); //Makes sure the elements are different (if possible).
			
			//Choose parent elements
			T element1 = chooseElement(fitness, choice);
			T element2 = chooseElement(fitness, choice2);

			//Add the crossed over children of these parents to the new generation.
			thisGeneration.addAll(evolver.crossover(element1, element2));
		}

		//Mutate
		for(T element : thisGeneration)
		{
			//Choose each element with a probability.
			if(Math.random() < evolver.getProbabilityOfMutation())
			{
				evolver.mutate(element);
			}
		}

		//Move the new generation to the current generation
		lastGeneration = (List<T>) thisGeneration.clone();
		//Empty the list for the next call to this method.
		thisGeneration.clear();
		//Return the created generation.
		return lastGeneration;
	}
	/**
	 * Gets a new generation using a parallel (i.e. multi-threaded) method.
	 * @return List<T> containing the newly created generation.
	 */
	private List<T> getNewGenerationMulti()
	{
		//Contains the sum of all the fitnesses.
		int sum = 0;
		//Contains each object along with its fitness. 
		LinkedList<Fitness<T>> fitness = new LinkedList<Fitness<T>>();
		
		//The number of members of the generation each thread will assess.
		int offset = lastGeneration.size() / numberOfThreads;
		//Stores the threads used for assessing fitness.
		ArrayList<FitnessThread> threads = new ArrayList<FitnessThread>(numberOfThreads);
		//Starts each thread.
		for(int threadId = 0; threadId < numberOfThreads; ++threadId)
		{
			//The offset is given so each thread can work out how many elements of the list to assess.
			FitnessThread t = new FitnessThread(lastGeneration.listIterator(threadId * offset), offset);
			t.start();
			threads.add(t);
		}

		//Waits for each of the threads to finish & gets the data they have calculated.
		for(FitnessThread t : threads)
		{
			try 
			{
				t.join();
				sum += t.getSum();
				fitness.addAll(t.getFitnesses());
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		
		Collections.sort(fitness);
		//Collection should now be in order from greatest fitness to smallest fitness
		
		//The fitness value of the object with the least fitness.
		int leastValue = fitness.getLast().getFitness();
		//If any fitnesses are negative, shift all of them so they have positive fitnesses.
		if(leastValue <= 0)
		{
			for(Fitness<T> element : fitness)
			{
				element.incrementFitness(leastValueOffSet - leastValue);
			}
			//Adjust sum, so it still contains the value equal to the sum of all the element.fitness.
			sum -= (fitness.size() * (leastValue - leastValueOffSet));
		}
		
		//Stores the threads used to crossover the population.
		LinkedList<CrossOverThread> threadList = new LinkedList<GeneticAlgorithm<T>.CrossOverThread>();
		//Lock for the fitness list.
		Object listLock = new Object();
		//Number of pairs each thread crosses over.
		int size = (int) Math.floor((float)evolver.getSizeOfGeneration() / (numberOfThreads * 2.0));
		//Start the threads.
		for(int i = 0; i < numberOfThreads; ++i)
		{
			if(i == numberOfThreads - 1)
			{
				//The last thread needs to cross over all the remaining pairs - used if the number of pairs isn't divisible by the number of threads.
				size = (int) ((evolver.getSizeOfGeneration() / 2.0) - (numberOfThreads - 1) * size);
			}
			CrossOverThread cot = new CrossOverThread(sum, listLock, fitness, size);
			cot.start();
			threadList.add(cot);
		}
		//Wait for each of the threads to finish.
		for(CrossOverThread cot : threadList)
		{
			try
			{
				cot.join();
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		//Mutate
		for(T element : thisGeneration)
		{
			//Mutate each element with a probability.
			if(Math.random() < evolver.getProbabilityOfMutation())
			{
				evolver.mutate(element);
			}
		}
		
		//Copy the new generation into the last generation.
		lastGeneration = (LinkedList<T>) thisGeneration.clone();
		//Empty the list for the next call to this method.
		thisGeneration.clear();
		//Return the newly generated list.
		return lastGeneration;
	}
	/**
	 * Used to assess the fitnesses of a population.
	 * @author Kim
	 */
	class FitnessThread extends Thread
	{
		//Stores the calculated fitnesses.
		LinkedList<Fitness<T>> fitness;
		//Stores the sum of the calculated fitnesses.
		int sum;
		//The number of elements this thread is assessing.
		int count;
		//The point in the list that is currently being assessed, initially at the point where this thread should begin assessing.
		private Iterator<T> startingPoint;
		/**
		 * @param startingPoint - iterator at the correct point to start assessing.
		 * @param count - the number of elements to be assessed.
		 */
		public FitnessThread(Iterator<T> startingPoint, int count)
		{
			this.startingPoint = startingPoint;
			this.count = count;
			this.fitness = new LinkedList<Fitness<T>>();
			this.sum = 0;
		}
		
		/**
		 * Calculates the fitness of the required elements.
		 */
		@Override
		public void run() 
		{
			int  i = 0;
			for(Iterator<T> current = startingPoint; current.hasNext() && i < count; )
			{
				T element = current.next();
				int elementFitness = evolver.fitnessFunction(element);
				fitness.add(new Fitness<T>(element, elementFitness));
				sum += elementFitness;
				++i;
			}
		}
		/**
		 * @return the sum of the fitnesses calculated by this thread.
		 */
		public int getSum()
		{
			return sum;
		}
		/**
		 * @return List<Fitness<T>> containing the objects and associated fitnesses calculated by this thread.
		 */
		public LinkedList<Fitness<T>> getFitnesses()
		{
			return fitness;
		}
	}
	/**
	 * Thread used to crossover a proportion of the population.
	 * @author Kim Barrett
	 */
	class CrossOverThread extends Thread
	{
		/**
		 * The total of the fitness values.
		 */
		int sum;
		/**
		 * The lock used for the fitness list.
		 */
		Object listLock;
		/**
		 * The list of all objects and their fitnesses.
		 */
		LinkedList<Fitness<T>> fitness;
		/**
		 * The size of the population this thread should generate.
		 */
		int size;
		/**
		 * @param sum - {@link #sum}
		 * @param listLock - {@link #listLock}
		 * @param fitness - {@link #fitness}
		 * @param size - {@link #size}
		 */
		public CrossOverThread(int sum, Object listLock, LinkedList<Fitness<T>> fitness, int size)
		{
			this.sum = sum;
			this.listLock = listLock;
			this.fitness = fitness;
			this.size = size;
		}
		
		/**
		 * Finds parents and crosses them over to produce child elements.
		 */
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
	/**
	 * Chooses each element in the fitness list with a probability relating to their fitness.
	 * @param fitnessList - the list from which elements are chosen.
	 * @param randomChoice - the randomly generated choice of element.
	 * @return chosen element from the list.
	 */
	private T chooseElement(LinkedList<Fitness<T>> fitnessList, int randomChoice)
	{
		/*
		 * Iterate through the list until the element at which the sum of fitnesses 
		 * encountered before this element is greater than the random choice.
		 */
		Iterator<Fitness<T>> iterator = fitnessList.iterator();
		while(iterator.hasNext())
		{
			Fitness<T> current = iterator.next();
			if(randomChoice < current.getFitness())
			{
				return current.getObject();
			}
			randomChoice -= current.getFitness();
		}
		return null;
	}
	/**
	 * Saves the current generation to a file
	 * @param fileName - the name of the file into which the population should be saved.
	 * @return whether the save succeeded.
	 */
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
	/**
	 * Load a generation from a file in to use as an initial population.
	 * @param fileName - the name of the file from which the population should be loaded.
	 * @throws JDOMException
	 * @throws IOException
	 */
	public void loadThisGeneration(String fileName) throws JDOMException, IOException
	{
		LoadSave.loadFromFile(fileName, lastGeneration, evolver);
	}

}
/**
 * Stores an object and its associated fitness.
 * @author Kim Barrett
 * @param <T> - the type of the object
 */
class Fitness<T> implements Comparable<Fitness<T>>
{
	/**	The object represented by this instance. */
	private T object;
	/** The fitness related to this {@link #object}. */
	private int fitness;
	/**
	 * Associates the given object with the given fitness.
	 * @param object
	 * @param fitness
	 */
	public Fitness(T object, int fitness)
	{
		this.object = object;
		this.fitness = fitness;
	}
	/**
	 * @return fitness associated with this object.
	 */
	public int getFitness()
	{
		return fitness;
	}
	/**
	 * Adds the given increment to the fitness associated with this object.
	 * @param increment - the increment for addition.
	 */
	public void incrementFitness(int increment)
	{
		fitness += increment;
	}
	/**
	 * @return object represented by this instance.
	 */
	public T getObject()
	{
		return object;
	}
	@Override
	public int compareTo(Fitness<T> otherObject)
	{
		//Compared entirely based on their fitnesses.
		return new Integer(fitness).compareTo(otherObject.getFitness());
	}
	@Override 
	public String toString()
	{
		return object.toString() + " : " + fitness;
	}
}
