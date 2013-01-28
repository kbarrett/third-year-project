package ch.idsia.agents.controllers.kbarrett;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
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
	
	public List<T> getNewGeneration()
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
		return Integer.compare(otherObject.getFitness(), fitness);
	}
	@Override 
	public String toString()
	{
		return object.toString() + " : " + fitness;
	}
}
