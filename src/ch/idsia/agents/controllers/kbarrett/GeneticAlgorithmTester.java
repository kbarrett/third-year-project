package ch.idsia.agents.controllers.kbarrett;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

public class GeneticAlgorithmTester
{
	public static void main(String[] args)
	{
		Evolver<EvolvingObject> evolver = new EvolverTest();
		List<EvolvingObject> initialPopulation = new LinkedList<EvolvingObject>();
		initialPopulation.add(new EvolvingObject(-100, 200));
		initialPopulation.add(new EvolvingObject(1000, -1));
		initialPopulation.add(new EvolvingObject(-50, -3));
		initialPopulation.add(new EvolvingObject(1000, 1000));
		
		GeneticAlgorithm<EvolvingObject> algorithm = new GeneticAlgorithm<EvolvingObject>(initialPopulation, evolver);
		
		for(int  i = 0; i< 1000; ++i)
		{
			initialPopulation = algorithm.getNewGeneration();
		}
		
		System.out.println(initialPopulation);
	}

}

class EvolvingObject
{
	private float i;
	private float j;
	public EvolvingObject(float i, float j)
	{
		this.i = i;
		this.j = j;
	}
	public float getIValue()
	{
		return i;
	}
	public void setIValue(float i1)
	{
		i = i1;
	}
	public float getJValue()
	{
		return j;
	}
	public void setJValue(float i1)
	{
		j = i1;
	}
	@Override
	public String toString()
	{
		return i + "," + j;
	}
}

class EvolverTest implements Evolver<EvolvingObject>
{

	private float probability = 0.3f;
	
	@Override
	public int fitnessFunction(EvolvingObject element)
	{
		return (int) Math.floor(element.getIValue() + element.getJValue());
	}

	@Override
	public List<EvolvingObject> crossover(EvolvingObject element1,EvolvingObject element2)
	{
		float i1 = (element1.getIValue() * probability) + (element2.getIValue() * (1-probability));
		float i2 = (element2.getIValue() * probability) + (element1.getIValue() * (1-probability));
		float j1 = (element1.getJValue() * probability) + (element2.getJValue() * (1-probability));
		float j2 = (element2.getJValue() * probability) + (element1.getJValue() * (1-probability));
		
		EvolvingObject e1 = new EvolvingObject(i1, j1);
		EvolvingObject e2 = new EvolvingObject(i2, j2);
		
		ArrayList<EvolvingObject> returnValue = new ArrayList<EvolvingObject>();
		returnValue.add(e1);
		returnValue.add(e2);
		return returnValue;
	}

	@Override
	public void mutate(EvolvingObject element)
	{
		double rand = Math.random();
		element.setIValue(element.getIValue() + (float)rand * 10);
		element.setJValue(element.getIValue() + (float)rand * 10);
	}

	@Override
	public float getProbabilityOfMutation() {
		return 0.1f;
	}

	@Override
	public int getSizeOfGeneration()
	{
		return 10;
	}

	@Override
	public Element toSaveFormat(EvolvingObject element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EvolvingObject fromSaveFormat(Element element)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}